package com.rob2d.android.chinesepoker.gui;

import android.graphics.Color;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.ChinesePokerSettings;
import com.rob2d.android.chinesepoker.game.CardGame;
import com.rob2d.android.chinesepoker.game.GameStyle;
import com.rob2d.android.chinesepoker.gui.gamescreen.CardPileDisplay;
import com.rob2d.android.chinesepoker.gui.gamescreen.PlayerHand;
import com.rob2d.android.chinesepoker.impl.GameHandlerAndroid;
import com.rob2d.android.chinesepoker.menu.MenuButton;
import com.rob2d.android.chinesepoker.menu.MenuSlider;
import com.rob2d.android.chinesepoker.menu.ScreenWithButtons;
import com.rob2d.android.chinesepoker.network.OnlineSession;
import com.rob2d.android.chinesepoker.network.OnlineSession.OnlineScope;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.TextEntity;

public class GameScr extends ScreenWithButtons
{	
	public PlayerHand mainHand;
	public CardPileDisplay cardsLastPlayed = new CardPileDisplay(this);
	public OnlineSession onlineSession;		//used to track whether the user came to the screen from selecting an online lobby
	
	public GameHandlerAndroid gameHandler;
	public CardGame gamePlayed;
	ImageEntity background = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
	
	public MenuButton playTurnButton = new MenuButton(804,  8, 3, this, Assets.playTurnBtnIF);
	public MenuButton passTurnButton = new MenuButton(804, 120, 3, this, Assets.passTurnBtnIF);
	public TextEntity[] playerNames;
	public TextEntity onlineMessage = new TextEntity(20, 20, new StringBuffer(), Color.WHITE, Assets.font1, 16, 4, this)
	{
		@Override
		public void update()
		{
			if(alpha > 0)
				alpha -= 1;
			color = Color.argb(alpha, 255, 255, 255);
		}
	};

	public boolean gameStarted = false;
	public boolean playSequence = false;
	
	/** signals to the game to perform a certain action involving prompts after clearing the screen */
	public enum PromptBufferAction { NONE, PLAY_TURN, PASS_TURN };
	
	public PromptBufferAction promptAction = PromptBufferAction.NONE;
	
	public GameScr(OnlineSession oS, Game game)
	{
		super(game);
		gameHandler = new GameHandlerAndroid(this);
		mainHand = new PlayerHand(2, this);
		onlineSession = oS;
	}

	@Override
	public void timedLogic()
	{
		super.timedLogic();
		
		if(onlineSession != null && onlineSession.waitingForAnotherGame)
		{
			if(onlineSession.anotherGameSelected)
			{
				Log.d("CPDEBUG", "IF onlineSession.anotherGameSelected check has run");
				game.stopLoadingDialog();
				onlineSession.anotherGameSelected = false;
				onlineSession.waitingForAnotherGame = false;
				if(!onlineSession.isHost)
					onlineSession.launchGame();
				else
					onlineSession.launchGameAsHost();
				goToScreen(new GameScr(onlineSession, game));
			}
			if(onlineSession.noMoreGamesSelected)
			{
				Log.d("CPDEBUG", "IF onlineSession.noMoreGamesSelected check has run");
				game.stopLoadingDialog();
				onlineSession.noMoreGamesSelected = false;
				onlineSession.waitingForAnotherGame = false;
				onlineSession.onlineScope = OnlineScope.BROWSE_PORTAL;
				goToScreen(new OnlinePortalScr(game, onlineSession));
			}
			if(onlineSession.continueGameError)
			{
				Log.d("CPDEBUG", "IF onlineSession.continueGameError check has run");
				game.stopLoadingDialog();
				onlineSession.continueGameError = false;
				onlineSession.waitingForAnotherGame = false;
				onlineSession.quitGame();
			}
		}

		switch(promptAction)
		{
			case PLAY_TURN:
				gameHandler.playerPlaysTurn();
				break;
			case PASS_TURN:
				gameHandler.playerPassesTurn();
				break;
		}
		promptAction = PromptBufferAction.NONE;		//reset state
		
		//INITIALIZE THE GAME WHEN NECESSARY
		if(onlineSession == null && !gameStarted)	//if offline, we automaticaly know to initialize at start
		{
			gameStarted = true;
			initializeNewGame();
		}
		else if(onlineSession != null && !gameStarted)
		{
			if(!onlineSession.isHost && gamePlayed == null)
					createNewGameInstance();
			
			//if cards havent been received while we're online and a client, keep trying every game loop to retreive them!
			if(!onlineSession.isHost)
			{
				gamePlayed.setUpNewGame();	//request the cards...
				//if successful, lets initialize the GUI and game to run!
				if(gamePlayed.onlineGameCardsReceived)
				{
					gameStarted = true;
					initializeNewGame();
				}
			}
			else if(onlineSession.isHost)
			{
				gameStarted = true;
				initializeNewGame();
			}
		}
				
		
		//play/pass button slide motion, disable the buttons while they are moving into position
		if(playSequence && playTurnButton.x > 680 && (onlineSession == null || 
			(onlineSession != null && gamePlayed != null && gamePlayed.playerTurn == onlineSession.playerSlot)))
		{
			playTurnButton.x -= 8;
			passTurnButton.x -= 8;
		}
		else if(!playSequence && playTurnButton.x < 800)
		{
			playTurnButton.x += 8;
			passTurnButton.x += 8;
		}
		
		if(gamePlayed != null)
		{
			if(onlineSession == null || (onlineSession != null && gamePlayed.playerTurn == onlineSession.playerSlot))
			{
				if(playTurnButton.isClicked)
				{
					playTurnButton.isClicked = false;
					clearScreenBtwnTurns();
					promptAction = PromptBufferAction.PLAY_TURN;
				}
				else if(passTurnButton.isClicked)
				{
					passTurnButton.isClicked = false;
					clearScreenBtwnTurns();
					promptAction = PromptBufferAction.PASS_TURN;
				}
			}
		}
		
		//if online, run necessary logic such as waiting for turns when necessary
		if(onlineSession != null)
			gameHandler.onlineTimedLogic();
	}
	
	/** clear the screen for a prompt */
	public void clearScreenBtwnTurns()
	{
		/* NOTE: we hide the player hand only if we are not online, /
		/* otherwise we can play with our hand at all times!       */
		
		//if offline, hide the player's hand, and hide the cards last played
		if(gamePlayed.onlineSession == null)
		{
			mainHand.y = 480;
			mainHand.positionObjects();
			cardsLastPlayed.y = -140;
			cardsLastPlayed.setVisible(false);
			mainHand.setVisible(false);
			playSequence = false;			//reset the playSequence value
		}	
		playTurnButton.x = 800;			//hide the play and pass turn button
		passTurnButton.x = 800;
	}
	
	public void initializeNewGame()
	{
		if(onlineSession == null)
		{
			((ChinesePokerSettings)game.getSettings()).getOptions()[ChinesePokerSettings.NUMBER_OF_PLAYERS] = 
				game.getPrompter().showTripleOption("We are now about to start a local game!\n\n How many players will be playing?", "Chinese Poker Online", "2", "3", "4") + 1;
		}
		
		if(onlineSession == null || (onlineSession != null && onlineSession.isHost))
				createNewGameInstance();
		
		if(onlineSession != null)
			onlineSession.cardGame = gamePlayed;
		
		playerNames = new TextEntity[gamePlayed.players.length - 1];
		for(int i = 0; i < playerNames.length; i++)
			playerNames[i] = new TextEntity(20, 20 + (30 * i), new StringBuffer(), Color.WHITE, Assets.font1, 16, 4, this);
		updatePlayerStatusInfo();	//update player info
		clearScreenBtwnTurns();		//clear all UI elements
		gameHandler.runGame();
	}
	
	public void createNewGameInstance()
	{
		GameStyle gameStyleUsed = ((ChinesePokerSettings)game.getSettings()).getGameStyle();
		gamePlayed = new CardGame(gameStyleUsed, gameHandler, onlineSession);
	}
	
	/** return the screen to the state it should be in for gameplay */
	public void restoreGameScreen()
	{
		playSequence = true;
		cardsLastPlayed.setVisible(true);
		mainHand.setVisible(true);
	}

	@Override
	public void backPressed()
	{
		super.backPressed();
		if(gamePlayed != null && gamePlayed.onlineSession == null )
		{
			if(game.getPrompter().showDualOption("Are you sure you want to quit the current game and go back to the title screen?", 
					"Chinese Poker Online", "YES", "NO"))
				goToScreen(new TitleScr(game));
		}
		else
		{
			if(game.getPrompter().showDualOption("Are you sure you want to quit the current game and go back to the online portal? " +
					"This will end this online game", 
						"Chinese Poker Online", "YES", "NO"))
			{
				onlineSession.rageQuitGame();
				goToScreen(new OnlinePortalScr(game, onlineSession));
			}
		}
	}
	
	@Override
	public void sliderValueChanged(MenuSlider slider)
	{
		//since there's only one slider on the game screen, we will assume it is the card hand navigation slider being shifted and adjust the scroll based on that!
		if(mainHand != null)
		{
			mainHand.scrollIndex = slider.getPointValue();
			mainHand.enableDisableArrows();
		}
	}
	
	public void updatePlayerStatusInfo()
	{
		int playerNamesCount = 0; //to increment the array index we're working with as we add text to the player text array
		
		if(onlineSession == null)
		for(int gamePlayerSlot = 0; gamePlayerSlot < playerNames.length + 1; gamePlayerSlot++)
		{
			if(gamePlayed.playerTurn != gamePlayerSlot)
				playerNames[playerNamesCount++].string = 
					new StringBuffer(gamePlayed.players[gamePlayerSlot].name + " has " + gamePlayed.players[gamePlayerSlot].hand.size() + " cards");
		}
		else
		{
			for(int gamePlayerSlot = 0; gamePlayerSlot < playerNames.length + 1; gamePlayerSlot++)
			{
				if(gamePlayed.onlineSession.playerSlot != gamePlayerSlot)	//if its not the current player, show info
					playerNames[playerNamesCount++].string = 
							new StringBuffer(gamePlayed.players[gamePlayerSlot].name + " has " + gamePlayed.players[gamePlayerSlot].hand.size() + " cards");
			}
		}
		
		
	}
	
	/** set a message on the gamescreen that involves the status of other players while online */
	public void setNewOnlineMessage(String newMessage)
	{
		onlineMessage.alpha = 255;								//make the text visible again
		onlineMessage.string = new StringBuffer(newMessage);		//update text
		onlineMessage.x = 400 - onlineMessage.getWidth()/2;						//center on screen
	}
}