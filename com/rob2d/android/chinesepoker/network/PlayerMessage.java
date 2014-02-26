package com.rob2d.android.chinesepoker.network;
/*  Filename:   PlayerMessage.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */

import com.rob2d.android.chinesepoker.game.Card;
import com.rob2d.android.chinesepoker.game.GameStyle;

enum MessageType {  
					HOST_LOBBY,			//hosts a new lobby
	   				JOIN_LOBBY,			//attempts to join a lobby currently available
	   				GET_LOBBIES,		//retrieves a list of lobbies for the online portal
	   				LOBBY_STATUS,		//information about a lobby including who is in the lobby
	   				START_GAME,			//starts a game
	   				END_GAME,			//ends the current game in progress
	   				KICK_PLAYER,		//kicks a player from a game as the host
	   				DROP_PLAYER,		//causes a player to drop from a game
	   				GET_GAME_DATA,		//gets available game data
	   				SET_GAME_DATA,		//sets data to be collected by other players on the table
	   				GAME_DATA_OK,		//checks that all game data has been received by other players 
	   				DROP_GAME,			//used when a player drops from a game, or the host decided to end it
	   				DROP_LOBBY,			//
	   				RAGE_QUIT,			//when a player decides to stop playing a game midway through
	   				CONTINUE_GAME,		//when a game has finished and we want new table data
	   				PLAY_AGAIN,			//vote on whether to play again or not
	   				GAME_RESULTS		//sent by host when a game is over to tell what the order of winners was
				}

/** This class is use to encode a message from a user to a server in order to get a response */
public class PlayerMessage 
{ 
	protected MessageType messageType;
	protected Integer	  lobbyId;	//id used to identify a game
	public	  Integer	  anotherGame;
	protected int	 	  userId;	//required after a player starts
	
	//EXTRA DATA
	protected String	   playerName;
	protected GameStyle	   gameStyle;
	
	protected String[]	   playerWinOrder;

	/** used if there is data to pass */
	public GameDataMessage gameData;
	
	public PlayerMessage(OnlineSession online)
	{
		if(online.loggedIn)		//attach the userId to every player message only if logged into the system
			userId = online.loginInfo.userId;
	}
}
