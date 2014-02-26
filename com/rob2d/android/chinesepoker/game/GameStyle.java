package com.rob2d.android.chinesepoker.game;
/*  Filename:   GameStyle.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */


/** contains all of the options for how to play a game of Chinese Poker */
public class GameStyle 
{
	/** whether players are dealt in 13 cards
	 * (<b>true</b>) or whether the deck is 
	 * split among players(<b>false</b>) */
	public boolean dealThirteenCards = false;
	
	/** whether triples are allowed as a 
	 *  card pattern */
	public boolean triplesValid = false;
	
	/** whether the winner starts. If this is 
	 * <b>false</b>, then the player with the 
	 * lowest card always begins a game */
	public boolean winnerStarts = true;
	
	/** whether a player can throw down a flush 
	 *  that starts with Ace as the lowest
	    possible Flush(A2345) */
	public boolean startFlushesAtAce = false;
	
	/** number of players dealt in a game
	 *  during offline mode            */
	public transient int playersInGame = 2;
	
	public GameStyle()
	{}
	
	/** set the default game setup */
	public void setDefaultParameters()
	{
		dealThirteenCards = false;
		triplesValid 	  = false;
		winnerStarts	  = true;
		startFlushesAtAce = false;
		playersInGame	  = 2;
	}
	
	/** set each parameter */
	public void setParameters(boolean thirteenCards, boolean triplesOK, boolean wStarts, boolean flushAtAce)
	{
		dealThirteenCards = thirteenCards;
		triplesValid = triplesOK;
		winnerStarts = wStarts;
		startFlushesAtAce = flushAtAce;
	}
	
	public String toString()
	{
		String returnStr = new String();
		
		returnStr += "*" + (dealThirteenCards ? "we will be dealing each player thirteen cards." : 
												  "we will split the deck among the players.") + "*\n";
		returnStr += "*"       + (triplesValid ? "triples are a valid play" : "tripes are not a valid play") + "*\n";
		returnStr += "*playing a straight from A or 2 " + (startFlushesAtAce ? "is a valid play." : "is not a valid play") + "*\n";
		returnStr += "*" + (winnerStarts ? "after the first game, the winner begins every new game.": 
											 "the player with the lowest card dealt always begins.") + "*";
		return returnStr;
	}
}
