package com.rob2d.android.chinesepoker.network;
/*  Filename:   LobbyStatus.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */

/** class used to retrieve data for the lobby's current status. This is used to check whether
 *  a game should be started, what players are currently in, and whether the lobby could be joined or
 *  not. */
public class LobbyStatus 
{
	/** possible state for the lobby */
	public final static int LOBBY_FILLED  = 0,
					 		LOBBY_AVAILABLE    = 1,
					 		GAME_IN_PROGRESS	 = 2;
	
	/** status of the lobby */
	public int statusCode;
	/** list of users in the lobby */
	public String[] players = null;
}