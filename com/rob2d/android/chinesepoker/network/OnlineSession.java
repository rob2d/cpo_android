package com.rob2d.android.chinesepoker.network;
/*  Filename:   OnlineSession.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */


import static com.rob2d.android.chinesepoker.network.OnlineRequests.dropGameMsg;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.gson;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.kickPlayerMsg;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.lobbyStatusMsg;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.sendMsg;

import java.util.ArrayList;

import com.rob2d.android.chinesepoker.ChinesePoker;
import com.rob2d.android.chinesepoker.ChinesePokerSettings;
import com.rob2d.android.chinesepoker.game.CardGame;
import com.rob2d.android.chinesepoker.game.GameStyle;
import com.rob2d.android.chinesepoker.gui.OnlineMenuScr;
import com.rob2d.android.chinesepoker.network.OnlineRequests.LobbyStatusUpdate;

/** This class tracks what a user's status is online with its associated data, 
 * and also coordinates communication between a network and a card game. 
 *  @author Robert Concepcion III*/
public class OnlineSession 
{	
	/** the different scope states you can be in while online */
	public enum OnlineScope {	OFFLINE, LOGGING_IN, REFRESH_LOBBIES, 
								BROWSE_PORTAL, IN_LOBBY, IN_GAME };
								
	/** used to retrieve updates without interrupting the user UI thread */
	public OnlineUpdateThread updateThread;
	
	//SESSION DATA
		//GENERAL ONLINE DATA
	/** the scope of the server that a user is currently in */
	public OnlineScope onlineScope = OnlineScope.OFFLINE;
	/** login results from session */
	public LoginInfo loginInfo = new LoginInfo();
	/** whether a player is logged into an online session */
	public boolean loggedIn = false;
	/** the user's player ID on a server. Used for efficient communication and table-lookup on the server */
	public int myPlayerId    = -1;
	/** the username displayed on screen and also gotten from/used for the login */
	public String username;
	/** the lobbies we retrieve after logging on */
	public LobbyInfo[] lobbyInfo;
	
	ChinesePoker game;
	
		//IN-LOBBY DATA
	/** gamestyle being given according to the lobby's rules(or the player's rules himself if he is hosting!) */
	public GameStyle gameStyle;
	/** whether a player is the host of the lobby */
	public boolean isHost   = false;
	/** data structure used to store lobby status updates */
	public ArrayList<LobbyStatusUpdate> lobbyStatusUpdates = 
			new ArrayList<LobbyStatusUpdate>();
	/** the current up to date status of the lobby that we have joined if applicable */
	public LobbyStatus lobbyStatus = null;
	/** the id on the server of the lobby that we are in */
	public int lobbyId = -1;
	
		//IN-GAME DATA
	/** the cardgame referenced when a session is in game */
	public CardGame cardGame = null;
	/** when there is data to return from the game session, it is inserted in this object */
	public InBoundData getGameData;
	/** the index of the player which we are */
	public int playerSlot = -1;
	
	/** if we're waiting for another turn while online, set this to true to get the update thread to start
	 *  checking CONTINUE_GAME when in the scope of IN_GAME*/
	public boolean waitingForAnotherGame = false;	
	public boolean anotherGameSelected = false;
	public boolean noMoreGamesSelected = false;
	public boolean continueGameError   = false;
	
	public OnlineSession(ChinesePoker cpGame)
	{
		game = cpGame;
	}
	
	/** Logs into a server. Returns true if login was successful and also stores the data.
	 *  Otherwise, returns false. */
	public boolean login(String u, String p)
	{
		logout();									//clear all data before we log in
		loginInfo = OnlineRequests.login(u, p);
		boolean authenticated;
		try
		{
			authenticated = (loginInfo.verified == LoginInfo.LOGIN_VERIFIED);
		}
		catch(NullPointerException nPE)
		{
			authenticated = false;
			
			game.getPrompter().showMsg("Could not communicate with the network. Please check your internet connection!",
					"Chinese Poker Online");
		}
		if(authenticated)
		{
			username = loginInfo.userName;
			myPlayerId =   loginInfo.userId;
			//if we have successfully logged in, begin the updateThread...
			updateThread = new OnlineUpdateThread(this); //instantiate online update thread
			updateThread.start();						 //start the update thread
		}
		loggedIn = authenticated;
		return authenticated;
	}

	/** Logs out of the server. Clears <b>all data</b> collected while in the online session! */
	public void logout()
	{
		//reset online data..
		onlineScope = OnlineScope.OFFLINE;
		lobbyStatus = null;
		lobbyInfo   = null;
		loginInfo   = null;
		loggedIn    = false;
		lobbyId     = -1;
		
		//stop the update thread from running...
		if(updateThread != null && updateThread.isAlive())
		{
			updateThread.onlineSession = null;
			updateThread.interrupt();
			updateThread = null;
		}
	}

	/** refresh our array of available lobbies.<br>
	 * 	returns <b>false</b> if a valid array of lobbies isn't returned from the server,
	 *  otherwise we have an valid list and <b>true</b> is returned.  */
	public boolean refreshLobbies()
	{
		String serverResponse = OnlineRequests.sendMsg(OnlineRequests.getLobbiesMsg(this));		//request the lobbies
		lobbyInfo = OnlineRequests.gson.fromJson(serverResponse, LobbyInfo[].class);			//parse/populate data into the lobby Info
		lobbyStatus = null;		//we assume we are not looking at an individual lobby status so it is clear when we return our first lobby msg
		
		if(lobbyInfo == null)							//check for failure!
			return false;								
		else return true;								//otherwise, SUCCCESS. We have a lobby list
	}
	
	/** try to join a lobby that is shown in our array of lobbies */
	public boolean joinLobby(int lobbyIndex)
	{
		int selectedLobby = lobbyInfo[lobbyIndex].gameId;
		
		boolean canJoinLobby = 							//try to join it and save the result
			Boolean.valueOf(OnlineRequests.sendMsg(OnlineRequests.joinLobbyMsg(this, selectedLobby)));
		
		if(canJoinLobby)									//have we joined?(change required data if so...)
		{
			gameStyle = lobbyInfo[lobbyIndex].gameStyle;
			
			isHost = false;	//we are not hosting if joining
			lobbyId = selectedLobby;
			onlineScope = OnlineScope.IN_LOBBY;		
			
			//if we've joined, clear the lobbyStatus because we want to detect the first change when
			//we get back to the GUI...
			//restart messages and get the first available
			lobbyStatus = null;
			//update lobby status and add it to the list of lobby messages if applicable
			LobbyStatusUpdate updatedStatus = updateLobbyStatus();
			if(updatedStatus != null)
				lobbyStatusUpdates.add(updatedStatus);
			
			return true;										//things have returned ok
		}
		else return false;										//otherwise failure
	}
	
	/** try to host a lobby.<br>
	 * 	return <b>false</b> if we get back a -1 response from the server(fails), otherwise we record the lobby we are in. */
	public boolean hostLobby()
	{
		gameStyle = ((ChinesePokerSettings)game.getSettings()).getGameStyle();
		
		String serverResponse = OnlineRequests.sendMsg(OnlineRequests.hostLobbyMsg(this));
		int returnId 		  =	OnlineRequests.gson.fromJson(serverResponse, Integer.class);
	
		if(returnId != -1)									//hosting was successful!
		{
			isHost = true;
			lobbyId = returnId;
			onlineScope = OnlineScope.IN_LOBBY;		
			
			//restart messages and get the first available
			lobbyStatus = null;
			//update lobby status and add it to the list of lobby messages if applicable
			LobbyStatusUpdate updatedStatus = updateLobbyStatus();
			if(updatedStatus != null)
				lobbyStatusUpdates.add(updatedStatus);
			
			return true;
		}
		else return false;											//otherwise it is false
	}
	
	/** called when a host starts a game from a lobby*/
	public boolean launchGameAsHost()
	{
		//make sure there are at least 2 players to start a game!
		if(lobbyStatus.players.length < 2)
			return false;
	
		launchGame();	//initiate the actual card game sequence
		return true;
	}
	
	
	/** signals that a game is starting */
	public void launchGame()
	{
		onlineScope = OnlineScope.IN_GAME;
	}
	
	/**called when a host decides to kick a player */
	public boolean kickPlayer(String player)
	{
		if(isHost)	//only allow a player to be kicked if you are the host
		{
			boolean playerKicked =
					Boolean.valueOf(sendMsg(kickPlayerMsg(this, player)));
			return playerKicked;
		}
		else return false;
	}
	
	public void quitGame()
	{
		boolean quitTheGame =
				Boolean.valueOf(sendMsg(OnlineRequests.dropGameMsg(this)));
		onlineScope = OnlineScope.BROWSE_PORTAL;
	}
	
	public void rageQuitGame()
	{
		boolean quitTheGame =
				Boolean.valueOf(sendMsg(OnlineRequests.rageQuitMsg(this)));
		onlineScope = OnlineScope.BROWSE_PORTAL;
	}
	
	public boolean closeLobby()
	{
		if(isHost)	//only allow a lobby to be closed if you are the host
		{
			boolean closeLobby =
					Boolean.valueOf(sendMsg(dropGameMsg(this)));
			//exit the lobby if successful
			if(closeLobby)
				onlineScope = OnlineScope.BROWSE_PORTAL;
		
			return closeLobby;
		}
		else return false;
	}
	
	/* drops a client user from a lobby */
	public boolean dropFromLobby()
	{
		boolean droppedFromLobby = false;
		if(!isHost)		//for integrity checking.. this function is meant for regular users, host calls closeLobby()
			droppedFromLobby = Boolean.valueOf(sendMsg(dropGameMsg(this)));
		else
			droppedFromLobby = closeLobby();		//otherwise, call the host function and close the whole lobby. not cool though! :/
		return droppedFromLobby;
	}
	
	/** updates the lobby status from the network and then returns a code for what has changed 
	 * since the last update so that the GUI can give appropriate messages back. 
	 * Also, assigns the current playerSlot so that this is always completely up to date! */
	public LobbyStatusUpdate updateLobbyStatus()
	{
		/* To return the proper lobby status, we have to make sure that its done in the correct order.
		 * The hierarchy(or priority) of returns is as follows:
		 * 		1) ENTERED_LOBBY -- > when you first enter a lobby(there will be no lobbyData in this case)
		 * 		2) GAME_STARTED  -- > game has started, so the lobby data will be fetched again during the start game sequence for
		 * 		   synchronization
		 * 		3) PLAYER_LEFT   -- > A player has left our lobby.
		 * 	    4) PLAYER_JOINED -- > A new player has joined the lobby.
		 * 		5) NO_UPDATES	 -- > data was the same, nothing to change.
		 */
		
		boolean statusChanged  = false;
		LobbyStatus lobbyStatusUpdate = null;
		
		try
		{
		//--------------------------------------------------//
		//   FETCH A COPY OF THE UPDATED DATA FROM SERVER   //
		//--------------------------------------------------//
		lobbyStatusUpdate = 
			gson.fromJson(sendMsg(lobbyStatusMsg(this, lobbyId)), LobbyStatus.class);
		}
		catch(NullPointerException nPE)
		{
			game.getPrompter().showMsg("Sorry, there was a problem connecting to the network while checking for lobby Status Updates : (", "Chinese Poker Online");
			game.setScreen(new OnlineMenuScr(game));
			logout();
		}
		
		
		//--------------------------------------------------//
		//RETRIEVE YOUR PLAYER SLOT AND LOBBY INFO AND KICK.//
		//--------------------------------------------------//
		boolean imInGame = false;
			for(int i = 0; i < lobbyStatusUpdate.players.length; i++)
				if(lobbyStatusUpdate.players[i].equals(username))
				{
					playerSlot = i;
					imInGame = true;
				}
				
		if(!imInGame)		//if you are not found in the player list, it means you have been kicked
		{
			playerSlot = -1;							//you are not in a valid player slot
			onlineScope = OnlineScope.BROWSE_PORTAL;	//exit the current game
			lobbyStatus = null;							//clear the lobby status
			return LobbyStatusUpdate.KICKED;			//return the message that you were kicked
		}	
		
		
		//--------------------------------------------------//
		//CHECK WHAT HAS CHANGED AND RETURN PROPER CODE...	//
		//--------------------------------------------------//
		if(lobbyStatus == null || lobbyStatus.players.length == 0)
		{
			if(lobbyStatusUpdate.players != null && lobbyStatusUpdate.players[0].equals(username))
				isHost = true;
			
			lobbyStatus = lobbyStatusUpdate;
			return LobbyStatusUpdate.ENTERED_LOBBY;
		}
		
		//check if the status changed
		if(lobbyStatusUpdate.statusCode != lobbyStatus.statusCode)
		{
			statusChanged = true;
			
			//if a game has been started, we return this as the update
			if(statusChanged)
				if(lobbyStatusUpdate.statusCode == LobbyStatus.GAME_IN_PROGRESS)
				{
					lobbyStatus = lobbyStatusUpdate;
					return LobbyStatusUpdate.GAME_STARTED;
				}
		}

		//check if a player has left or joined
		int playersTracked = 0;	//to count how many old players were tracked in update
		for(String p : lobbyStatus.players)
		{
			//1) assume we haven't found the current player in the lobby
			boolean playerFound = false;
			//2) scan this name against every name in the update
			for(String p2 : lobbyStatusUpdate.players)
			{	//3) if we find the person we're looking for, we update it
				if(p.equals(p2))
				{
					playersTracked += 1;	//count how many of the players are rediscovered from our update
					playerFound = true;		//
				}
			}
			
			//if a player hasn't been found after comparing the old list against the new, a player has left.
			if(!playerFound)
			{
				lobbyStatus = lobbyStatusUpdate;
				return LobbyStatusUpdate.PLAYER_LEFT;
			}
		}
		
		//if at the end of our sequence a new player has joined from those tracked,
		//we return that a player has joined
		if(lobbyStatusUpdate.players.length > playersTracked)
		{
			lobbyStatus = lobbyStatusUpdate;
			return LobbyStatusUpdate.PLAYER_JOINED;
		}
		
		//if all is good and we have gotten this far, nothing has changed.
		return  LobbyStatusUpdate.NO_UPDATES;
	}

	/** check whether there are any more lobby status updates to retrieve */
	public boolean hasLobbyStatusUpdates()
	{
		if(lobbyStatusUpdates.size() > 0)
			return true;
		else return false;
	}
	
	/** retrieve the next available lobby status update */
	public LobbyStatusUpdate getNextLobbyStatus()
	{
		return lobbyStatusUpdates.remove(0);
	}
	
	/** clear all lobby updates */
	public void clearLobbyUpdates()
	{
		lobbyStatusUpdates.clear();
	}

	/** processes game events that require online communication within an associated card game when it is called upon.
	 *  The process in an oGE involves:<br><i>
	 *  1) A CardGame completes some game logic<br>
	 *  2) It reaches a point where it requires communication with the network<br>
	 *  3) The onlineGameEvent() method checks the onlineState(OnlineGameState) of the CardGame 
	 * 	   in progress and then uses that to send the appropriate
	 *     data.<br>
	 *  4) The onlineState is then changed when the necessary data has been exchanged and then the 
	 *     onlineGameEvent() method is exited.
	 *  </i>
	 *  @return <b>true</b> if the states required are correct and the process succeeds, and <b>false</b> otherwise.
	 *  */
	
}