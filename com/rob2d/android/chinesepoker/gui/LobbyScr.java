package com.rob2d.android.chinesepoker.gui;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.menu.MenuButton;
import com.rob2d.android.chinesepoker.menu.ScreenWithButtons;
import com.rob2d.android.chinesepoker.network.LobbyStatus;
import com.rob2d.android.chinesepoker.network.OnlineRequests.LobbyStatusUpdate;
import com.rob2d.android.chinesepoker.network.OnlineSession;
import com.rob2d.android.chinesepoker.network.OnlineSession.OnlineScope;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.RectEntity;
import com.rob2d.android.framework.TextEntity;

public class LobbyScr extends ScreenWithButtons
{
	public OnlineSession onlineSession;
	RectEntity			 bgLobbiesRect;
	ImageEntity			 titleTextImg;
	ImageEntity			 background;
	ImageEntity[]		 gameStyleIcons;
	public TextEntity[]	 userNames;
	public TextEntity	 roomStatusMsg;
	MenuButton			 startButton;
	LobbyStatus			 lobbyStatus;	//keeps track of the lobbyStatus of the onlineSession
	boolean				 gameStarted = false;
	boolean				 exitingAsClient = false;
	boolean				 breakLoopForUpdate = false;
	boolean				 startedWaiting = false;

	public final int GAMESTYLE_X = 736; 
	
	public LobbyScr(OnlineSession oS, final Game game)
	{
		super(game);
		onlineSession = oS;	//store the online session
		bgLobbiesRect = new RectEntity(new Rect(0, 100, 800, 380), Color.argb(100, 0, 0, 0), 1, this);
		background = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
		titleTextImg = new ImageEntity(0, 20, Assets.lobbyModeTxtIF, 1, this);
		titleTextImg.x = 400 - (titleTextImg.getBounds().right/2);
		startButton= new MenuButton(640, 400,  3, this, Assets.startBtnIF)
		{
			@Override
			public void clicked()
			{
				super.clicked();
				onlineSession.launchGameAsHost();
				goToScreen(new GameScr(onlineSession, game));
			}
		};
		startButton.x = 400 - (startButton.imgFrame.getImgBounds().right/2);
		startButton.buttonEnabled = onlineSession.isHost && (onlineSession.lobbyStatus.players.length > 1);
		
		userNames = new TextEntity[4];
		for(int i = 0; i < userNames.length; i++)
			userNames[i] = new TextEntity(20, 148 + (272/4) *i, new StringBuffer("..." + (i+1)), 
											 Color.WHITE, Assets.font1, 36, 3, this);
		
		roomStatusMsg = new TextEntity(0, 400, new StringBuffer(), 
				Color.argb(100, 255, 255, 255), Assets.font1, 36, 3, this)
		{
			int width = 0;
			
			//define specific behavior for the text entity to scroll left and wrap around screen
			@Override
			public void update()
			{
				super.update();
				if(x < 0 - getWidth())
					x = 800;
			}
		};
		roomStatusMsg.x = 800;
		roomStatusMsg.dx = -1;
		
		gameStyleIcons = new ImageEntity[4];
		for(int i = 0; i < 4; i++)
		{
			switch(i)
			{
				case 0:
					gameStyleIcons[i] = 	new ImageEntity(GAMESTYLE_X, 148 + (272/4) * i - 46, 
							new ImageFrame(onlineSession.gameStyle.dealThirteenCards ? 
									Assets.gSI13CPix : Assets.gSISplitPix, this), 3, this);
					break;
				case 1:
					gameStyleIcons[i] = 	new ImageEntity(GAMESTYLE_X, 148 + (272/4) * i - 46, 
							new ImageFrame(onlineSession.gameStyle.dealThirteenCards ? 
									Assets.gSIWSPix : Assets.gSILCSPix, this), 3, this);
					break;
				case 2:
					gameStyleIcons[i] = 	new ImageEntity(GAMESTYLE_X, 148 + (272/4) * i - 46, 
							new ImageFrame(onlineSession.gameStyle.dealThirteenCards ? 
									Assets.gSITVPix : Assets.gSITNVPix, this), 3, this);
					break;
				case 3:
					gameStyleIcons[i] = 	new ImageEntity(GAMESTYLE_X, 148 + (272/4) * i - 46, 
							new ImageFrame(onlineSession.gameStyle.dealThirteenCards ? 
									Assets.gSISFA2Pix : Assets.gSINSFA2Pix, this), 3, this);
					break;
			}
		}
	}

	@Override
	public void timedLogic()
	{
		super.timedLogic();
		
		if(exitingAsClient)
		{
			goToScreen(new OnlinePortalScr(game, onlineSession));
		}
		if(!onlineSession.isHost)
		{
			if(!startedWaiting)				//if this is the first time a user started waiting for updates,
			{								//update the necessary events
				startedWaiting = true;
				roomStatusMsg.string = new StringBuffer("waiting for host to start the game");
				roomStatusMsg.width = roomStatusMsg.getWidth();
			}
			while(onlineSession.hasLobbyStatusUpdates())
			{
				LobbyStatusUpdate lastUpdate = onlineSession.getNextLobbyStatus();
				switch(lastUpdate)
				{
					case ENTERED_LOBBY:
					case PLAYER_JOINED:
					case PLAYER_LEFT:
						startButton.buttonEnabled = onlineSession.isHost && (onlineSession.lobbyStatus.players.length > 1);
						updateUserInfoList();
						breakLoopForUpdate = true;
						break;
					case GAME_STARTED:
						if(!gameStarted)
						{
							gameStarted = true;
							breakLoopForUpdate = true;
							game.stopLoadingDialog();
							onlineSession.launchGame();
							goToScreen(new GameScr(onlineSession, game));
						}
				}
			}
		}
		
		if(onlineSession.isHost)
		{
			while(onlineSession.hasLobbyStatusUpdates())
			{
				Log.d("CPDEBUG", "onlineSession.hasLobbyStatusUpdates() while loop is iterating");
				LobbyStatusUpdate lastUpdate = onlineSession.getNextLobbyStatus();
				switch(lastUpdate)
				{
					case ENTERED_LOBBY:
					case PLAYER_JOINED:
					case PLAYER_LEFT:
						startButton.buttonEnabled = onlineSession.isHost && (onlineSession.lobbyStatus.players.length > 1);
						updateUserInfoList();
						break;
				}
			}
		}
	}

	@Override
	public void onLoadingDialogCanceled()
	{
		game.stopLoadingDialog();
		//!IMPORTANT! -- << INSERT DROP PLAYER CODE HERE
		onlineSession.onlineScope = OnlineScope.BROWSE_PORTAL;
		exitingAsClient = true;
	}
	
	@Override
	public void present()
	{ drawEntities(game.getGraphics()); }
	
	public void updateUserInfoList()
	{
		int numberOfPlayers = onlineSession.lobbyStatus.players.length;
		for(int i = 0; i < 4; i++)
		{
			if(i < numberOfPlayers)
			{
				userNames[i].string.setLength(0);
				userNames[i].string.append(onlineSession.lobbyStatus.players[i]);
			}
			else
				userNames[i].string.setLength(0);
		}
	}
	
	@Override
	public void backPressed()		
	{
		super.backPressed();
		if(onlineSession.isHost)	//if the host, warn that users will be kicked if you choose to exit
		{
			if(game.getPrompter().showDualOption("Are you sure you wish to exit? This will close the lobby since you are the host and kick all players who have joined!", 
					"Chinese Poker Online", "YES", "NO"))
			{
				game.startLoadingDialog("Loading...", "Exiting the Lobby", false);
				onlineSession.closeLobby();
				game.stopLoadingDialog();
				goToScreen(new OnlinePortalScr(game, onlineSession));
			}
		}
		else						//if not the host, ask user if he wants to quit the lobby...
		{
			if(game.getPrompter().showDualOption("Are you sure you wish to exit from this lobby", "Chinese Poker Online", "YES", "NO"))
			{	
				onlineSession.dropFromLobby();
				goToScreen(new OnlinePortalScr(game, onlineSession));
			}
		}
	}
}
