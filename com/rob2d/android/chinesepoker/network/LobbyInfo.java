package com.rob2d.android.chinesepoker.network;
/*  Filename:   LobbyInfo.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */

import com.rob2d.android.chinesepoker.game.GameStyle;

/** represents lobby information for an online portal which can be retrieved from a server */
public class LobbyInfo 
{
	/** game Id number */
	public int gameId;
	
	public String hostName;
	/** rules of the game */
	public GameStyle gameStyle;
	
	/** timestamp */
	public String dateStarted;
	
	/** number of players */
	public int playerCount;
}
