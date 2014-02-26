package com.rob2d.android.chinesepoker.network;
/*  Filename:   InBoundData.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */


/** class which represents retrieval of game data messages. There will only be one messageInBound at a time */
public class InBoundData 
{
	/** is there data on the server to get? */
	public boolean dataAvailable;
	
	/** if a message is received, it is not "consumed". Once we have consumed it, this variable is used to track this
	 * This variable is also not sent over the network as it is only relevant to the application side on the receiving end */
	public volatile boolean dataConsumed	 = false;
	
	/** the current message inbound if there is data available to retrieve */
	public GameDataMessage gameData = null;
	
	public boolean canSetData;
	
	public int errorStatusCode;
}
