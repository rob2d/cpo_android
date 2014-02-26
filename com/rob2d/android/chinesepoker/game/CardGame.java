package com.rob2d.android.chinesepoker.game;

import static com.rob2d.android.chinesepoker.network.OnlineRequests.getGameDataMsg;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.gson;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.sendMsg;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.startGameMsg;

import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.rob2d.android.chinesepoker.gui.OnlinePortalScr;
import com.rob2d.android.chinesepoker.network.GameDataMessage;
import com.rob2d.android.chinesepoker.network.GameDataMessage.GameDataType;
import com.rob2d.android.chinesepoker.network.OnlineSession.OnlineScope;
import com.rob2d.android.chinesepoker.network.InBoundData;
import com.rob2d.android.chinesepoker.network.OnlineRequests;
import com.rob2d.android.chinesepoker.network.OnlineSession;

/*  Filename:   CardGame.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */
/** Represents a game played of Chinese Poker.
 *  Keeps track of players involved a game, the turn sequence, the deck, 
 *  the card pile and the winners.
 
 * @author      Robert Concepcion III <address @ robert.concepcion.iii@gmail.com>               
 * @LastEdited      09-23-11      																	*/

public class CardGame 
{	
	//**********************************//
	//	    GAME SETTING VARIABLES   	//
	//**********************************//
	/** this integer represents the setting "number of players".
	 * (will be replaced with a more elaborate saving scheme later) */
	public int numberOfPlayers = 2;
	
	//**********************************//
	//	    REFERENCE CLASSES        	//
	//**********************************//	
	/** GUI reference is used to issue signals through interface methods such as "playerHasWon()" */
	GameHandler gameHandler;
	
	//**********************************//
	//	    CLASS MEMBERS           	//
	//**********************************//	
	/** a deck of cards associated with our card game */
	Deck deck = new Deck();	
	/** an array of players */
	public Player[] players;
	/** pile of cards that the players can put cards down onto */
	public CardPile cardPile;
	/** specific rules that set up how the game is played */
	public GameStyle gameStyle;	
	
	/** handles all online-related data, including log-in and messages from players */
	public OnlineSession onlineSession = null;
	/** used when in an online session to track who came in what place */
	public String[] playerWinOrder;
	
	public boolean onlineGameCardsReceived = false;
	
	/** when online, this arraylist tracks the values of cards in their initial index which is sent to other players
	 *  for correct reference when we play a selection and send over the network! */
	public ArrayList<Integer> onlineCardValues;
	
	public enum OnlineGameState { 
									RESET_STATE,
									ASSIGNING_CARDS,
							   	   	RECEIVING_CARDS,
							   	   	LAUNCHING_GAME,
							   	   	PLAYING_CARDS,
							   	   	PASSING_TURN,
							   	   	WAITING_FOR_TURN,
							   	   	PLAY_AGAIN,
							   	   	DONT_PLAY_AGAIN
								};
		
	/** tracks the individual state of the game for use during online play. This is the primary object
	 *  that the online session refers to when coordinating communication. */
	public OnlineGameState onlineState = OnlineGameState.RESET_STATE;
	
	//**********************************//
	//	   MORE GAME INFO               //
	//**********************************//	
	/** this is going to be the "unique" gameId later to keep track of this game on a server */
	int gameId = 000000;
	/** current player's turn */
	public int playerTurn;
	/** player who has the highest card pattern put down */
	public int playerToBeat = -1;
	/** keep track of when a player first deals out, this way the first time
	 * <b>playerToBeat</b> is checked against with the current turn(<b>playerTurn</b>), the player has to beat.
	 * After the boolean is reset, the player does not have to beat it */
	public boolean playerJustOut = false;
	/** keeps track of whether a player has won a round by the default rotation after a player is dealt out*/
	public boolean playerOutRound = false;
	/** if it is currently the first turn being thrown down. Player must throw down the three of diamonds! */
	public boolean firstTurn = true;
	/** whether passing is allowed during the current turn.
	 *  passing is disabled on either the first turn, or if a player out round just finished. */
	public boolean playerCanPass = false;
	
	/** the lowest card from the deck dealt to a player */
	int lowestCardDealt = 0;
	/** number of players have finished playing their hand */
	int playersFinished = 0;
	/** the winner! */
	int winningPlayer = -1;
	/** the loser! */
	int losingPlayer =  -1;
		
	//**********************************//
	//	          METHODS           	//
	//**********************************//
	public CardGame(GameStyle localStyle, GameHandler gH, OnlineSession oS)
	{
		gameHandler = gH;
		gameHandler.setCardGame(this);
		
		//INITALIZE THE GAME STYLE AND ONLINE SESSION
		if(oS == null)				//if we're online we know we will use the local game style given
			gameStyle = localStyle;	
		
		else if(oS != null)		//if a non empty online session was given, we set appropriate 
		{							//online session and gamestyle data...
			onlineSession = oS;	//save the online session
			oS.cardGame = this;	//give online object reference to the game running
			
			if(!oS.isHost)			//if we arent hosting, the online session uses the session's game style
				gameStyle = oS.gameStyle;
			else gameStyle = localStyle;		//otherwise we get our own
		}
		
		//retrieve the number of players, either based on local preference or online lobby status player array
		if(onlineSession == null)
		{
			numberOfPlayers = gameStyle.playersInGame;
		}
		else 
		{
			numberOfPlayers = onlineSession.lobbyStatus.players.length;
			playerWinOrder = new String[numberOfPlayers];
		}
		
		//set up the player objects	
		players = new Player[numberOfPlayers];
		for(int i = 0; i < numberOfPlayers; i++)
		{
			//create the player in memory
			players[i] = new Player(this);
			//assign the player's name
			if(onlineSession != null)
				players[i].name = onlineSession.lobbyStatus.players[i];
			else
				players[i].name = "player" + (i+1);
		}
		
		setUpNewGame();	//set up/re-set up the game
	}

	/** deal cards to players */
	public void deal()
	{
		for(int i = 0; i < numberOfPlayers; i++)
		{
			if(gameStyle.dealThirteenCards)		//deal thirteen cards
				players[i].dealt(deck.deal(13));
			else									//split the deck
			{
				if(players.length != 3)	//if there are either 2 or 4 players, split the deck
					players[i].dealt(deck.deal(52/numberOfPlayers));
				
				else players[i].dealt(deck.deal(17));	//if there are 3 players, give each 17 cards(the player 
														//with the lowest card gets the extra in assignStartingPlayer()
			}
		}
	}
	
	/** set up or reset a game. This clears all players hands, puts all players back into the game and sets up the deck.  */
	public void setUpNewGame()
	{
		Log.d("CPDEBUG", "setUpNewGame() is running");
		deck = new Deck();					//set up a new deck of 52 cards, they will be automatically shuffled
		cardPile = new CardPile(this);		//set up a new empty card pile to throw cards onto
		
		//if we are either in an offline game, or we are the host, deal the cards
		if(onlineSession == null || (onlineSession != null && onlineSession.isHost))
		{
			
			deal();						//deal cards to all players		
			//if we are the host, send away the cards!!!
			if(onlineSession != null && onlineSession.isHost)
				onlineGameEvent(OnlineGameState.ASSIGNING_CARDS);
		}
		
		//if we are in an online session, either receive cards or assign them depending on whether we're hosting
		if(onlineSession != null)
		{
			Log.d("CPDEBUG", "attempting to either assign or retrieve cards");
			if(onlineSession.isHost)
				onlineGameEvent(OnlineGameState.ASSIGNING_CARDS);
			else onlineGameEvent(OnlineGameState.RECEIVING_CARDS);
			
			if(!onlineGameCardsReceived && !onlineSession.isHost)
				return;
			
			//save the original values and the corresponding indexes of all of our cards in case we shift them around later between plays!
			onlineCardValues = new ArrayList<Integer>();
			for(Card c : players[onlineSession.playerSlot].hand)
				onlineCardValues.add(c.getCardValue());
		}
		
		assignStartingPlayer();	//search for and assign the starting player
		playerCanPass = false;	//first player cannot pass
	}
	
	/** method to find a starting player. Scans through all of the player's cards to find the player with the lowest
	 * @return player # with the lowest card (starting player) */
	public void assignStartingPlayer()
	{
		playerTurn = 0;		
		lowestCardDealt = 999;	//arbitrary value to find the lowest
		
		for(int playerIndex = 0; playerIndex < players.length; playerIndex++)
			for(Card c : players[playerIndex].hand)
				if(c.getCardValue() < lowestCardDealt)
				{
					playerTurn = playerIndex;
					lowestCardDealt = c.getCardValue();
				}
		
		//if there are 3 players, after the player with the lowest(aka the starter) player is found,
		//give this player the extra card in the deck
		if(players.length == 3 && !gameStyle.dealThirteenCards)
			players[playerTurn].givenCards(deck.deal(1));
	}


	/** evaluate whether the current player's selection is valid(used primarily for the card selection GUI) */
	public boolean playerSelectionValid()
	{	
		boolean successfulPlay = false;		//the return value!
		
		CardPattern cardSelection = players[playerTurn].trySelection();
		
		if(cardSelection != null)	//if we have a valid card selection, proceed with the following logic!...
		{
			//--------------------------------------------------------------//
			//in the case that it is our first turn!
			//--------------------------------------------------------------//
			if(firstTurn)
			{
				//give an error if the first card played doesn't contain the lowest in your hand!
				if(!CardPattern.containsLowest(cardSelection, lowestCardDealt))
				{
					return false;
				}
				//if all goes well...
				else if(CardPattern.containsLowest(cardSelection, lowestCardDealt))
				{
					return true;					//register a good play!
				}
			}
			
			//--------------------------------------------------------------//
			//if the player can play whatever he likes!
			//--------------------------------------------------------------//
			if(playerToBeat == -1 || (playerToBeat == playerTurn && !playerJustOut))	//if the player is first or turns have went full circle, 
			{					
				successfulPlay = true;		
			}
			else
			//--------------------------------------------------------------//
			//the normal case where you play some cards!
			//--------------------------------------------------------------//
				successfulPlay = cardPile.cardPlayValid(cardSelection);
		}
		//tell the user that we require a selection!!!
		else if(cardSelection == null)	
		{
			successfulPlay =  false;
		}
		return successfulPlay;
	}
	
	/** checks the current player's 
	 *  selection of cards against the currently winning card in the cardpattern. If 
	 *  this works, true is returned. 
	 *  @return <b>true</b> if the current player's play wins against the highest on the 
	 *  card pile. otherwise <b>false</b> 
	 *  if invalid or lower than the set */
	public boolean playerSelectsPlay()
	{
		boolean successfulPlay = false;		//the return value!
		
		CardPattern cardSelection = players[playerTurn].trySelection();
		
		if(cardSelection != null)	//if we have a valid card selection, proceed with the following logic!...
		{
			//--------------------------------------------------------------//
			//in the case that it is our first turn!
			//--------------------------------------------------------------//
			if(firstTurn)
			{
				//give an error if the first card played doesn't contain the lowest in your hand!
				if(!CardPattern.containsLowest(cardSelection, lowestCardDealt))
				{
					successfulPlay = false;
					cardSelection = null;	
					gameHandler.showMsg("The first player must play his lowest card in the first play!");
				}
				//if all goes well...
				else if(CardPattern.containsLowest(cardSelection, lowestCardDealt))
				{
					Log.d("CPDEBUG", "FIRST TURN PLAYED!");
					successfulPlay = true;					//register a good play!
					firstTurn = false;						//reset the first turn flag
					
					cardPile.playAnyCards(cardSelection);	//throw any CardPattern onto the pile as valid	
					playerCanPass = true;					//reset the passing variable
				}
			}
			
			//--------------------------------------------------------------//
			//if the player can play whatever he likes!
			//--------------------------------------------------------------//
			if(playerToBeat == -1 || (playerToBeat == playerTurn && !playerJustOut))	//if the player is first or turns have went full circle, 
			{					
				Log.d("CPDEBUG", "PLAYED A PLAYER TURN!");
				playerOutRound = false; 							//reset the player out round flag
				cardPile.playAnyCards(cardSelection);				//throw any CardPattern onto the pile since the turn is free
				successfulPlay = true;		
			}
			else
			//--------------------------------------------------------------//
			//the normal case where you play some cards!
			//--------------------------------------------------------------//
				successfulPlay = cardPile.playCards(cardSelection);
		}
		//tell the user that we require a selection!!!
		else if(cardSelection == null)	
		{
			successfulPlay =  false;
			gameHandler.showMsg("Sorry, you have played an invalid hand! Please try another or pass.");
		}
		
		if(successfulPlay)
		{
			playerPlaysCards();
			Log.d("CPDEBUG", "CARDS PLAYED!");
		}
		
		return successfulPlay;
	}
	
	/** called when player declines to play cards. if passing is disabled, false is returned */
	public boolean playerSelectsPass()
	{
		if(playerCanPass)
		{
			/* since it was a success, if we are online communicate that a play is being made 
			 * before clearing the selection(make sure that it is us as well since we are sending 
			 * the turn and do not want to re-send turns from a network player)              */
			if(onlineSession != null && playerTurn == onlineSession.playerSlot)
				onlineGameEvent(OnlineGameState.PASSING_TURN);
			
			//if player passes after a player was dealt out, then we can reset this variable!
			if(playerJustOut)
			{
				playerJustOut = false;
				playerOutRound = true;	//the player out round has begun
			}
			//next player!
			playerTurn = nextPlayer(playerTurn);	//set the turn to the next player!		
			//if the upcoming player is the one who played the highest card, he cannot pass on his next turn.
			if(playerTurn == playerToBeat)
				playerCanPass = false;
			return true;
		}
		else
			gameHandler.showMsg("Sorry, you cannot pass in this circumstance! Please play any valid hand of cards.");
			return false;	//nothing was done if player can't pass. return FALSE
	}

	/** plays the current players selection of cards.
	 *  method is called which advances the turn variable afterwards */
	public void playerPlaysCards()
	{
		//if we are online communicate that a play was made(make sure that it is not an online player sending a turn as well!)
		if(onlineSession != null && playerTurn == onlineSession.playerSlot)
			onlineGameEvent(OnlineGameState.PLAYING_CARDS);
		
		players[playerTurn].playSelection();	//get rid of the cards from the players hand since it was successful
		//since we made a play, we can reset the card out cycle
		if(!playerJustOut)
			playerJustOut = false;
		
		//if the player has thrown his last cards down, make sure to set off the necessary events
		if(players[playerTurn].hand.size() == 0)
				playerIsOut();
		playerToBeat = playerTurn; //if the player has played the highest card, then his turn has been completed with a "play"
		playerTurn = nextPlayer(playerTurn);	//set the turn to the next player!	
		playerCanPass = true;					//allow the next player to pass if this one couldn't
	}

	/** if the player has played his cards and has none left, record him out of the game and as the winner if necessary
	 * (or whatever place he comes in) */
	public void playerIsOut()
	{
		//add this player to the list of players who won while in an online session
		if(onlineSession != null)
			playerWinOrder[playersFinished] = players[playerTurn].name;
		
		playerJustOut = true;					//flag is set to start player out cycle
		playerToBeat = nextPlayer(playerTurn);	//the next player becomes the player to beat
		players[playerTurn].isInGame = false;
		
		//if this player is first, he has won! In this case we would call playerHasWon()
		//which then interacts with our GUI and changes necessary variables
		if(playersFinished == 0)
			playerHasWon(playerTurn);
		//since a player has finished, increment the counter
		playersFinished++;
		//if we've singled out one player as the loser, game is over!!!
		if((numberOfPlayers - playersFinished) == 1)
			gameOver();
	}
	
	/** process information declaring that a player has won the game */
	public void playerHasWon(int player)
	{
		players[player].hasWon = true;
		winningPlayer = player;
		//give callback to the game loop sequence that somebody has won!
		gameHandler.playerWon(winningPlayer);
	}	
	public void gameOver()
	{
		//determine the losing player
		for(int player = 0; player < players.length; player++)
			if(players[player].isInGame)
				losingPlayer = player;
		if(playerWinOrder != null)
			playerWinOrder[numberOfPlayers - 1] = players[losingPlayer].name;
		//give callback to the game loop sequence to notify the associated GUI that somebody has lost!
		gameHandler.playerLost(losingPlayer);
	}

	
	/** @return current player's turn */
	public int getCurrentPlayer()
	{	return playerTurn;	 }
	
	/** method to find the player # after a specified player. This is for processing
	 * things such as turns without inlining the logic.
	 * @param player - player number at which to start finding the next turn
	 * @return the next player # from the one given
	 */
	public int nextPlayer(int player)
	{
		//find the next player after the given index
		do{	player = (player + 1) % players.length;	}
		while(!players[player].isInGame);	
		return player;
	}
	
	public boolean onlineGameEvent(OnlineGameState initState)
	{
		Log.d("CPDEBUG", "cardGame.onlineGameEvent( " + initState + ") running.");
		onlineState = initState;
		/* If a cardGame's online state is "RECEIVING_CARDS", we will retrieve the data from the GET_GAME_DATA and make sure
		 * that the online state was valid. */
		switch(initState)
		{
			//handle the case where a game has started and the player is the host
			case ASSIGNING_CARDS:
				//message we will send the server
				GameDataMessage gameDataSent = new GameDataMessage();
				gameDataSent.messageType = GameDataType.DEAL_CARDS;

				switch(numberOfPlayers)
				{
					case 4:
						gameDataSent.player3Cards = (players[3].hand.toArray(new Card[players[3].hand.size()]));
					case 3:
						gameDataSent.player2Cards = (players[2].hand.toArray(new Card[players[2].hand.size()]));
					case 2:
						gameDataSent.player0Cards = (players[0].hand.toArray(new Card[players[0].hand.size()]));
						gameDataSent.player1Cards = (players[1].hand.toArray(new Card[players[1].hand.size()]));
						break;
				}
				//return whether or not the game was started to the card game
					boolean onlineGameStarted = 
						Boolean.valueOf(sendMsg(startGameMsg(onlineSession, gameDataSent)));
					if(onlineGameStarted)
						onlineState = OnlineGameState.LAUNCHING_GAME;
						
					return onlineGameStarted;

			//handle the case where a game has started and the player is the client
			case RECEIVING_CARDS:
				//update data containalther object!
				onlineSession.getGameData = 
				gson.fromJson(sendMsg(getGameDataMsg(onlineSession)), InBoundData.class);
				
				//if we find there is an update needed, then we check whether what we need is given and if so return true and assign
				//cards to players on client side, otherwise return false!
				if(onlineSession.getGameData.dataAvailable && onlineSession.getGameData.gameData.messageType == GameDataType.DEAL_CARDS)
				{
					onlineSession.onlineScope = OnlineScope.IN_GAME;
					onlineGameCardsReceived = true;
					//deal cards from game data
					switch(numberOfPlayers)
					{
						case 4:
							players[3].dealt(onlineSession.getGameData.gameData.player3Cards);
						case 3:
							players[2].dealt(onlineSession.getGameData.gameData.player2Cards);
						case 2:
							players[0].dealt(onlineSession.getGameData.gameData.player0Cards);
							players[1].dealt(onlineSession.getGameData.gameData.player1Cards);
							break;
						}
					//clear game data since the host sends his player number which may mess up turn sequence
					onlineSession.getGameData = null;
					}
				else return false;
			
				//set the state of the card game to waiting for the turn after it's received its cards...
				//<<<<<TO INSERT LATER: DISTINGUISH BETWEEN PLAYING FIRST TURN OR NOT, ASSIGN PLAYER #S
				onlineState = OnlineGameState.WAITING_FOR_TURN;
				return true;
			
			case WAITING_FOR_TURN:
				InBoundData getGameData = onlineSession.getGameData;		//for easy reference
				boolean turnIncoming = false;
				//if data is available, run the following:
				if(onlineSession.getGameData != null && getGameData.dataAvailable && !getGameData.dataConsumed)	
				{
					try
					{
						GameDataMessage gameData = getGameData.gameData;	//for easy reference
						//if it is the player who sent game data's turn, set his selected cards and register the play, or pass!
						if(gameData.playerNumber == playerTurn)
						{
							if(gameData.messageType == GameDataType.PLAY_CARDS)
							{
								gameHandler.showOnlineMsg(players[gameData.playerNumber].name + " has played cards");
								//set player who just went's cards to the cards played
								players[gameData.playerNumber].selectedCards = gameData.cardsPlayed;	
								gameHandler.playerPlaysTurn();
								turnIncoming = true;	
							}
							else if(gameData.messageType == GameDataType.PASS)
							{
								gameHandler.showOnlineMsg(players[gameData.playerNumber].name + " has passed");
								playerSelectsPass();
								turnIncoming = true;
								getGameData.dataConsumed = true;
							}
							else
							{
								Log.d("CPDEBUG", "STUCK STATE");
							//~~~~~~~~~~~~ REVISIT THIS SECTION!!! ~~~~~~~~~~~~~~~~~~~~~~~~~ //
							//ONLINE GAME HAS BEEN CORRUPTED! WE MUST CLOSE THE ENTIRE LOBBY // 
							//THE SERVER CODE FOR THIS FUNCTION HAS NOT BEEN IMPLEMENTED     //
							//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
							}
							//data has been checked/consumed
							getGameData.dataConsumed = true;
							onlineState = OnlineGameState.RESET_STATE;
					}
					else
					{
						//~~~~~~~~~~~~ REVISIT THIS SECTION!!! ~~~~~~~~~~~~~~~~~~~~~~~~~ //
						//ONLINE GAME HAS BEEN CORRUPTED! WE MUST CLOSE THE ENTIRE LOBBY // 
						//THE SERVER CODE FOR THIS FUNCTION HAS NOT BEEN IMPLEMENTED     //
						//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
					}
					}
					catch(NullPointerException nPE)
					{
						gameHandler.showMsg("Error getting data from the server :/ exiting game");
						onlineSession.quitGame();
						gameHandler.getGame().setScreen(new OnlinePortalScr(gameHandler.getGame(), onlineSession));
					}
				}
				
				if(getGameData != null)
				{
					Log.d("CPDEBUG", "gameDataConsumed = " + getGameData.dataConsumed + "\n" + "gameDataAvailable = " + getGameData.dataAvailable);
					getGameData.dataConsumed = true;
				}
				return turnIncoming;
				
			case PLAYING_CARDS:
				//initialize data to send
				GameDataMessage gameDataPlay = new GameDataMessage();
				gameDataPlay.messageType = GameDataType.PLAY_CARDS;
				//set up the cards played to be sent
				gameDataPlay.cardsPlayed = playOnlineCardSelection();
				//send data and get response
				boolean turnPlayed = 
					Boolean.valueOf(sendMsg(OnlineRequests.setGameDataMsg(onlineSession, gameDataPlay)));
				//if successful, reset online state 
				if(turnPlayed)
					onlineState = OnlineGameState.WAITING_FOR_TURN;
				return turnPlayed;
				
			case PASSING_TURN:
				//initialize data to send
				GameDataMessage gameDataPass = new GameDataMessage();
				gameDataPass.messageType = GameDataType.PASS;
				//send the data and get response
				boolean turnPassed =
						Boolean.valueOf(sendMsg(OnlineRequests.setGameDataMsg(onlineSession, gameDataPass)));
				//if successful, reset online state 
				if(turnPassed)
					onlineState = OnlineGameState.RESET_STATE;
				return turnPassed;
			case PLAY_AGAIN:
				boolean playAgain =
						Boolean.valueOf(sendMsg(OnlineRequests.playAgainMsg(onlineSession, true)));
				return playAgain;
			case DONT_PLAY_AGAIN:
				boolean dontPlayAgain = 
						Boolean.valueOf(sendMsg(OnlineRequests.playAgainMsg(onlineSession, false)));
				return true;
		}	//end of switch(cG.onlineState)
	return true;	//end of onlineGameEvent()
	}
	
	/** translate selected cards in a player hand to cards online, also removes cards from the selection
	 *  as it is assumed that they are being played when this function is called since all error checking has
	 *  already went through! */
	public ArrayList<Integer> playOnlineCardSelection()
	{
		//generate a return arraylist which will be filled with new return indices
		ArrayList<Integer> cardSelectionOnline = new ArrayList<Integer>();
		ArrayList<Integer> selectedCards = players[onlineSession.playerSlot].selectedCards;
		//scan through selected cards and find corresponding online index values for cards
		for(int SELECTED_INDEX = 0; SELECTED_INDEX < selectedCards.size(); SELECTED_INDEX++)
		{
			int cardValueSelected = players[onlineSession.playerSlot].hand.get(selectedCards.get(SELECTED_INDEX)).getCardValue();
			for(int i : onlineCardValues)
				if(i == cardValueSelected)
					cardSelectionOnline.add(onlineCardValues.indexOf(i));
			
		}
		
		//DEBUG
		String debugStr = "( ";
		//after getting all of the indexes of cards that we will be playing,
		//remove them from the initial online card selection array 
		//--but-- first we sort them from highest to lowest so that we remove the last indexes first!
		
		//sorting from highest to lowest...
		Collections.sort(cardSelectionOnline);
		Collections.reverse(cardSelectionOnline);
		//removing
		for(int i : cardSelectionOnline)
		{
			debugStr += "" + i;	//DEBUG LINE
			if(cardSelectionOnline.indexOf(i) != cardSelectionOnline.size() - 1)
				debugStr += ")";	//DEBUG LINE
			onlineCardValues.remove(i);
		}
		//DEBUG
		//print debug log at the end
		Log.d("CPDEBUG", debugStr);
		
		//return our original card selection array that other players accross the server can use
		return cardSelectionOnline;
	}
}
