package com.rob2d.android.chinesepoker.game;
/*  Filename:   GameHandler.java
 *  Package:    com.rob2d.android.chinesepoker.gameflow
 * 	Author:     Robert Concepcion III  */


import java.util.ArrayList;

import com.rob2d.android.framework.Game;


/** the game handler is used simply as a coordinator for the game. It allows turn sequences to complete */
public abstract class GameHandler 
{	
	public final static int 
		OPTION_ADD 	= 0,
    	OPTION_REMOVE  = 1,
    	OPTION_PLAY	= 2,
    	OPTION_PASS	= 3;
	
	public GameHandler()
	{
	}
	
	public abstract Game getGame();
	public abstract void setCardGame(CardGame cG);
	public abstract void runGame();
	public abstract void startPlayerTurn(int player);
	public abstract void playerWon(int player);
	public abstract void playerLost(int player);
	public abstract void showMsg(String msg);
	public abstract void welcomeMessage();
	public abstract void playerPlaysTurn();
	public abstract void playerPassesTurn();
	public abstract boolean promptAnotherGame();
	public abstract void showOnlineMsg(String string);
}
