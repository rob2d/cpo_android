package com.rob2d.android.chinesepoker.network;
/*  Filename:   OnlineUpdateThread.java
 *  Package:    com.rob2d.android.chinesepoker.network
 * 	Author:     Robert Concepcion III  */


import static com.rob2d.android.chinesepoker.network.OnlineRequests.getGameDataMsg;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.gson;
import static com.rob2d.android.chinesepoker.network.OnlineRequests.sendMsg;
import android.util.Log;

import com.rob2d.android.chinesepoker.game.CardGame.OnlineGameState;
import com.rob2d.android.chinesepoker.network.OnlineRequests.LobbyStatusUpdate;
import com.rob2d.android.chinesepoker.network.OnlineSession.OnlineScope;

/** This class is used to constantly retrieve updates from the server on a separate thread                *
  * while the application is running. It is created along with an OnlineSession and is linked back to     *
  * its initiating object where it is called upon for any updates. It is use as a Helper Class for the    *
  * OnlineSession in order to get data asynchronously from the OnlineSession's work on a normal user      *
  * interface thread.                                                                                     */


public class OnlineUpdateThread extends Thread
{
	/** our online session to get data from */
	public OnlineSession onlineSession;
	
	/** used to constantly update our game data when necessary*/
	public InBoundData inBoundData;
	
	public OnlineUpdateThread(OnlineSession oS)
	{
		onlineSession = oS;		//attach a reference to the parent onlineSession object
	}
	
	@Override
	public void run() 
	{
		while(true)	//this thread will run as long as the associated online session is logged in
		{
			if(onlineSession != null)		//make sure we haven't logged out...
			switch(onlineSession.onlineScope)
			{
				case IN_LOBBY:
					synchronized(onlineSession)	//code is sychronized so that the onlineSession data
					{								//does not become corrupt!
						//add a new lobby update if it is applicable
						LobbyStatusUpdate lastUpdate = onlineSession.updateLobbyStatus();
						if(lastUpdate != null && lastUpdate != LobbyStatusUpdate.NO_UPDATES)
							onlineSession.lobbyStatusUpdates.add(lastUpdate);
					}
					break;
				case IN_GAME:
					//if a game is waiting for a turn, continually try to retrieve game data
					//we also make sure that data has been consumed before running "get_game_data" because
					//we do not want to miss data messages and corrupt the get game data loop!
					if(!onlineSession.waitingForAnotherGame)
					{
						if(onlineSession.cardGame != null && onlineSession.cardGame.onlineState == OnlineGameState.WAITING_FOR_TURN
								&& (onlineSession.getGameData == null || onlineSession.getGameData.dataConsumed))
						{
							Log.d("CPDEBUG", "OnlineUpdateThread is looking for turn data...");
							inBoundData = gson.fromJson(sendMsg(getGameDataMsg(onlineSession)), 
									InBoundData.class);
							//if the retrieved data contains something we need, save it for the session to reference
							if(inBoundData != null && inBoundData.dataAvailable)
								onlineSession.getGameData = inBoundData;
						}
					}	
					else
					{
						int continueGameCode = 0;
						final int STILL_WAITING = 0,
								  ANOTHER_GAME  = 1,
								  NO_MORE_GAMES = 2;
						try
						{
							continueGameCode = Integer.valueOf(sendMsg(OnlineRequests.continueGameMsg(onlineSession)));
						}
						catch(NumberFormatException nFE)
						{
							//if there is still a loading dialog, stop it because of the error!
							onlineSession.game.stopLoadingDialog();
							//ONLINE ERROR EXCEPTION CODE HEReE
						}
						if(continueGameCode == ANOTHER_GAME)
						{
							onlineSession.anotherGameSelected = true;
							onlineSession.game.stopLoadingDialog();	//stop loading dialog for next game.
						}
						else if(continueGameCode == NO_MORE_GAMES)
						{
							onlineSession.noMoreGamesSelected = true;
							onlineSession.game.stopLoadingDialog();
							onlineSession.quitGame();
							onlineSession.onlineScope = OnlineScope.BROWSE_PORTAL;
						}
					}
					break;
			}
				
			//wait to run the contents of this thread again
			try 
			{ Thread.sleep(100); } 
			catch (InterruptedException e) 
			{ e.printStackTrace(); }
		}
	}

}
