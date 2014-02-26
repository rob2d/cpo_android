package com.rob2d.android.chinesepoker.network;
/*  Filename:   GameDataMessage.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */


import java.util.ArrayList;

import com.rob2d.android.chinesepoker.game.Card;

/** This class is used to receive incoming messages when <b>(1)</b> a PlayerMessage GET_GAME_DATA is used,
 * 	<b>(2)</b> SET_GAME_DATA is used, or <b>(3)</b> if a game is detected as started from LobbyInfo(in which a GET_GAME_DATA loop
 * is set) after a host has declared START_GAME.															*/
public class GameDataMessage 
{
	/** types of messages that game data can send */
	public enum GameDataType {	DEAL_CARDS,
								PLAY_CARDS,
								PASS		 }
	
	/** type of message being sent */
	public GameDataType messageType;
	
	/** number of the player based on his turn sequence in the game(0-based index) */
	public int playerNumber;
	
	/** string that populates when an event happens with a player's userName */
	public String playerWon  			= null,
				  playerLost 			= null,
				  playerRageQuitted   = null,
				  playerTimeOut		= null;
	
	/** card arrays. player0-3Cards are used for GAME_START.
	 *  cardsPlayed is used for "PLAY_CARDS".				*/
	public Card []	    player0Cards,
						player1Cards,
						player2Cards,
						player3Cards;
	
	public ArrayList<Integer> cardsPlayed;
}