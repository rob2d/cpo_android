package com.rob2d.android.chinesepoker.gui;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.menu.MenuButton;
import com.rob2d.android.chinesepoker.menu.ScreenWithButtons;
import com.rob2d.android.chinesepoker.network.LobbyInfo;
import com.rob2d.android.chinesepoker.network.OnlineSession;
import com.rob2d.android.chinesepoker.network.OnlineUpdateThread;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.RectEntity;
import com.rob2d.android.framework.TextEntity;

public class OnlinePortalScr extends ScreenWithButtons
{
	int		scrollIndex    = 0;
	int		selectionIndex = 0;
	
	Game				   game;
	OnlineUpdateThread 	   updateThread;
	OnlineSession		   onlineSession;
	int					   lobbyCount= 0;
	boolean				   screenInitiated = false;
	RectEntity			   bgLobbiesRect, selectedLobbyRect;
	ImageEntity			   background;
	ImageEntity			   titleTextImg;
	MenuButton			   sUpBtn, sDownBtn, hostButton, joinBtn, refreshButton;
	ArrayList<ImageEntity> gameStyleIcons = new ArrayList<ImageEntity>();
	
	final int			   LOBBIES_ON_SCREEN 	= 4,
						   GAMESTYLE_13CARDS_X	= 520,
						   GAMESTYLE_WSTARTS_X	= 520 + 64 + (4*1),
						   GAMESTYLE_TRIPLES_X	= 520 + (64*2) + (4*2),
	   					   GAMESTYLE_FLUSHA2_X	= 520 + (64*3) + (4*3);
	
	/** text entities for our game */
	public TextEntity[] lobbyInfoTxt = new TextEntity[LOBBIES_ON_SCREEN];
	
	public OnlinePortalScr(Game g, OnlineSession oS)
	{
		super(g);
		
		game = g;
		onlineSession = oS;
		
		//instantiate and initialize the online update thread
		updateThread = onlineSession.updateThread;
		
		titleTextImg = new ImageEntity(0, 20, Assets.onlinePortalTxtIF, 1, this);
		titleTextImg.x = 400 - (titleTextImg.getBounds().right/2);
		//instantiate the strings that display lobby info
		for(int i = 0; i < lobbyInfoTxt.length; i++)
			lobbyInfoTxt[i] = new TextEntity(20, 148 + (272/4) *i, new StringBuffer(""), 
											 Color.WHITE, Assets.font1, 36, 3, this);
		
		//add the background graphic
		background = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
		
		// initialize navigation buttons
		sUpBtn = new MenuButton(700, 48,  3, this, Assets.scrollUpBtnIF)
		{
			@Override
			public void clicked()
			{
				super.clicked();
				if(buttonEnabled)
				{
					if(selectionIndex > 0)
						selectionIndex -= 1;
					if(selectionIndex != -1 && selectionIndex < scrollIndex && scrollIndex > 0)
						scrollIndex -= 1;
					//INSERT CODE >> scrollIndex++ when selection index is the last lobby displayed
					updateLobbyDisplay();
				}
			}
		};
		sDownBtn = new MenuButton(700, 400, 3, this, Assets.scrollDownBtnIF)
		{
			@Override
			public void clicked()
			{
				super.clicked();
				if(buttonEnabled)
				{
					if(selectionIndex < lobbyCount - 1)
						selectionIndex += 1;
					if(selectionIndex == scrollIndex + LOBBIES_ON_SCREEN && scrollIndex < lobbyCount - LOBBIES_ON_SCREEN)
						scrollIndex++; //when selection index is the last lobby displayed
					updateLobbyDisplay();
				}
			}
		};
		
		hostButton =    new MenuButton(48, 392, 3, this, Assets.hostBtnIF)
		{
			public void clicked()
			{
				super.clicked();
				if(buttonEnabled)
				{
					game.startLoadingDialog("Loading... please wait...", "Attempting to start a new lobby on the server", false);
					if(onlineSession.hostLobby())
					{
						game.stopLoadingDialog();
						game.getPrompter().showMsg("Alright! You will be the host of a new game. Press start when you'd like to begin the game!", 
								"Chinese Poker Online");
						goToScreen(new LobbyScr(onlineSession, game));
					}
					else
					{
						game.stopLoadingDialog();
						game.getPrompter().showMsg("Sorry, could not host a lobby at this time. Something seems to be wrong with the server...",
								"Chinese Poker Online");
					}
				}
			}
		};
		joinBtn = 	new MenuButton(400, 392, 3, this, Assets.joinBtnIF)
		{
			@Override
			public void clicked()
			{
				super.clicked();
				if(buttonEnabled)
				{ 
					game.startLoadingDialog("Loading... please wait...", "Attempting to join the selected lobby", false);
					if(onlineSession.joinLobby(selectionIndex))
					{
						game.stopLoadingDialog();
						game.getPrompter().showMsg("Join successful! You will now be taken to the lobby...", 
								"Chinese Poker Online");
						goToScreen(new LobbyScr(onlineSession, game));
					}
					else
					{
						game.stopLoadingDialog();
						game.getPrompter().showMsg("Sorry, could not join the lobby successfully, either it is full " +
								"or the game has started. Please try refreshing or joining another lobby!",
								"Chinese Poker Online");
					}
				}
			}
		};
		refreshButton = new MenuButton(248, 392, 3, this, Assets.refreshBtnIF)
		{
			@Override
			public void clicked()
			{
				super.clicked();
				if(buttonEnabled)
					refreshLobbies();
			}
		};
		
		bgLobbiesRect = new RectEntity(new Rect(0, 100, 800, 380), Color.argb(100, 0, 0, 0), 1, this);
		selectedLobbyRect = new RectEntity(new Rect(0, 100, 800, 168), Color.argb(100, 0, 0, 255), 1, this);
	}
	
	public void timedLogic()
	{
		super.timedLogic();
		if(!screenInitiated)
		{
			screenInitiated = true;
			refreshLobbies();
		}
	}
	
	@Override
	public void backPressed()
	{
		super.backPressed();
		if(game.getPrompter().showDualOption("Do you wish to exit the online portal?", "Chinese Poker Online", "YES", "NO"))
		{
			onlineSession.logout();
			goToScreen(new TitleScr(game));
		}
	}
	
	@Override
	public void fadeOutLogic()
	{
		super.fadeOutLogic();
	}

	@Override
	public void present()
	{
		drawEntities(game.getGraphics());		
	}
	
	public void refreshLobbies()
	{
		game.startLoadingDialog("Checking for available lobbies...", "Chinese Poker Online", false);
		onlineSession.refreshLobbies();					//refresh available game lobbies
		lobbyCount = onlineSession.lobbyInfo.length;	//track the size of the number of lobbies
		scrollIndex = 0;								//make sure GUI doesn't point to null lobbies
		//reset initial selection
		if(lobbyCount > 0)
		{
			selectionIndex = 0;			//prevents glitch of being on a bad lobby
		}
		else
			selectionIndex = -1;
		game.stopLoadingDialog();
		updateLobbyDisplay();
	}
	
	/** when the scroll index has changed or we refresh the lobbies, this method redraws all necessary objects..*/
	public void updateLobbyDisplay()
	{
		//if no lobbies available or onlineSession lobby info is not available, do not allow functionality
		if(selectionIndex == -1 || onlineSession.lobbyInfo == null)
		{
			joinBtn.buttonEnabled = false;
			sDownBtn.buttonEnabled = false;
			sUpBtn.buttonEnabled = false;
			
			//set selection rectangle off the screen
			selectedLobbyRect.rect.top	  = -100;
			selectedLobbyRect.rect.bottom = -100;
		}
		else	//otherwise, enable/disable buttons depending on our selection and position selection rectangle
		{
			//button availability
			joinBtn.buttonEnabled = true;
			sUpBtn.buttonEnabled = (selectionIndex != 0);	//scroll up button is disabled only when we're at first index
			sDownBtn.buttonEnabled = !(selectionIndex+1 >= lobbyCount);
			
			//set the selection rectangle's coordinates
			selectedLobbyRect.rect.top 	  = 100 + (68 * (selectionIndex-scrollIndex));
			selectedLobbyRect.rect.bottom = selectedLobbyRect.rect.top + 68;
		}
			
		//erase all icons from the screen
		for(ImageEntity i : gameStyleIcons)
			i.destroy();
		gameStyleIcons.clear();
		
		//create new display info
		for(int i = 0; i < (lobbyCount < LOBBIES_ON_SCREEN ? lobbyCount : LOBBIES_ON_SCREEN); i++)
		{
			LobbyInfo lobbyInfo = onlineSession.lobbyInfo[scrollIndex + i];				//for referencing
			lobbyInfoTxt[i].string.setLength(0);
			lobbyInfoTxt[i].string.append(lobbyInfo.hostName);
			gameStyleIcons.add(
					new ImageEntity(GAMESTYLE_13CARDS_X, lobbyInfoTxt[i].y - 46, 
									new ImageFrame(lobbyInfo.gameStyle.dealThirteenCards ? 
													Assets.gSI13CPix : Assets.gSISplitPix, this), 3, this));
			gameStyleIcons.add(
					new ImageEntity(GAMESTYLE_WSTARTS_X, lobbyInfoTxt[i].y - 46, 
									new ImageFrame(lobbyInfo.gameStyle.dealThirteenCards ? 
													Assets.gSIWSPix : Assets.gSILCSPix, this), 3, this));
			gameStyleIcons.add(
					new ImageEntity(GAMESTYLE_TRIPLES_X, lobbyInfoTxt[i].y - 46, 
									new ImageFrame(lobbyInfo.gameStyle.dealThirteenCards ? 
													Assets.gSITVPix : Assets.gSITNVPix, this), 3, this));
			gameStyleIcons.add(
					new ImageEntity(GAMESTYLE_FLUSHA2_X, lobbyInfoTxt[i].y - 46, 
									new ImageFrame(lobbyInfo.gameStyle.dealThirteenCards ? 
													Assets.gSISFA2Pix : Assets.gSINSFA2Pix, this), 3, this));
		}
		
		if(lobbyCount < LOBBIES_ON_SCREEN)
			for(int i = lobbyCount; i < LOBBIES_ON_SCREEN; i++)
				lobbyInfoTxt[i].string.setLength(0);
		
		if(lobbyCount == 0)
		{
			lobbyInfoTxt[0].string.setLength(0);
			lobbyInfoTxt[0].string.append("no lobbies available");
		}
	}

	}