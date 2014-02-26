package com.rob2d.android.chinesepoker.impl;

import android.util.Log;

import com.rob2d.android.chinesepoker.game.CardGame;
import com.rob2d.android.chinesepoker.game.GameHandler;
import com.rob2d.android.chinesepoker.game.CardGame.OnlineGameState;
import com.rob2d.android.chinesepoker.gui.GameScr;
import com.rob2d.android.chinesepoker.gui.OnlinePortalScr;
import com.rob2d.android.chinesepoker.gui.WelcomeMenuScr;
import com.rob2d.android.chinesepoker.network.OnlineSession.OnlineScope;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.impl.AndroidGame;
import com.rob2d.android.textui.Prompter;

public class GameHandlerAndroid extends GameHandler
{
	public AndroidGame game;
	public CardGame cardGame;
	public Prompter prompter;
	public GameScr gameScreen;
	public boolean gameFinished = false;
	
	public GameHandlerAndroid(GameScr gS)
	{
		gameScreen = gS;
		game =	(AndroidGame)gS.game;
		prompter = game.getPrompter();
	}
	
	@Override
	public void setCardGame(CardGame cG)
	{
		cardGame = cG;
	}

	@Override
	public void runGame()
	{
		Log.d("CPDEBUG", "runGame() running");
		welcomeMessage();
		
		gameFinished = false;
		
		if(cardGame.onlineSession != null)
			initiateOnlineGame();			//initiate player UI if entering an online game
		//begin the turn sequence!
		startPlayerTurn(cardGame.playerTurn);
	}
	
	public void initiateOnlineGame()
	{
		Log.d("CPDEBUG", "initiateOnlineGame() running");
		//set the player's cards
		gameScreen.mainHand.setCards(cardGame.players[cardGame.onlineSession.playerSlot].getHandValues());
		
		//update the text for other player info
		gameScreen.updatePlayerStatusInfo();
		
		//make UI elements re-appear(in the case of online play, simply make them appear till the end)
		gameScreen.restoreGameScreen();
		
		gameScreen.passTurnButton.buttonEnabled = cardGame.playerCanPass;
	}

	@Override
	public void startPlayerTurn(int player)
	{
		//in offline mode, run through each player's prompt
		if(cardGame.onlineSession == null)
		{
			prompter.showMsg(cardGame.players[player].name + ", it is now your turn!" , 
				"Chinese Poker Online"); 
			//set the player's cards
			gameScreen.mainHand.setCards(cardGame.players[player].getHandValues());
			//update the text for other player info
			gameScreen.updatePlayerStatusInfo();
			//make UI elements re-appear
			gameScreen.restoreGameScreen();
			
			gameScreen.passTurnButton.buttonEnabled = cardGame.playerCanPass;
		}
		else
		{
			if(player == cardGame.onlineSession.playerSlot)
			{
				gameScreen.playSequence = true;					//slide in the play/pass options
				gameScreen.mainHand.refreshGameHand();
				gameScreen.passTurnButton.buttonEnabled = cardGame.playerCanPass;	//set whether or not pass is enabled...
			}
		}
	}
	
	@Override
	public void playerPlaysTurn()
	{
		//play turn logic
		cardGame.playerSelectsPlay();
		
		//update card pile cards!
		gameScreen.cardsLastPlayed.updateCardPile();
		
		if(gameScreen.onlineSession != null)
		{
			gameScreen.mainHand.setCards(cardGame.players[cardGame.onlineSession.playerSlot].getHandValues());
			gameScreen.mainHand.selectedCards.clear();
			gameScreen.mainHand.refreshSelectionPointers();
			gameScreen.updatePlayerStatusInfo();
		}
		
		//start the next player's turn
		if(!gameFinished)
			startPlayerTurn(cardGame.playerTurn);
	}
	
	@Override
	public void playerPassesTurn()
	{
		//player passes turn logic
		cardGame.playerSelectsPass();
		
		//update card pile cards!
		gameScreen.cardsLastPlayed.updateCardPile();
		
		if(gameScreen.onlineSession != null)
		{
			gameScreen.mainHand.setCards(cardGame.players[cardGame.onlineSession.playerSlot].getHandValues());
			gameScreen.mainHand.selectedCards.clear();
			gameScreen.mainHand.refreshSelectionPointers();
			gameScreen.mainHand.refreshGameHand();
			gameScreen.updatePlayerStatusInfo();
		}
		//FILTERED OUT gameScreen.mainHand.refreshGameHand();
		gameScreen.passTurnButton.buttonEnabled = cardGame.playerCanPass;
		//start the next player's turn!
		if(!gameFinished)
		{
			startPlayerTurn(cardGame.playerTurn);
		}
	}

	@Override
	public void playerWon(int player)
	{
		prompter.showMsg(cardGame.players[player].name + ", has won this game!" , 
				"Chinese Poker Online");
	}

	@Override
	public void playerLost(int player)
	{
		if(cardGame.numberOfPlayers > 2)
			prompter.showMsg(cardGame.players[player].name + ", has lost! Too bad." , 
				"Chinese Poker Online");
		
		boolean anotherGame = promptAnotherGame();
		
		synchronized(this)
		{
			if(cardGame.onlineSession != null && anotherGame)
			{
				game.startLoadingDialog("Waiting for other players to decide whether to restart the game", "Chinese Poker Online", false);
				cardGame.onlineSession.waitingForAnotherGame = true;//IF WE'RE ONLINE, INSERT CODE TO WAIT FOR OTHER PLAYERS HERE.
			}
		}															//UPDATE THREAD SHOULD START CHECKING FOR CONTINUE_GAME
	}

	@Override
	public void showMsg(String msg)
	{
		prompter.showMsg(msg, "Chinese Poker Online");
	}

	@Override
	public void welcomeMessage()
	{
		//display who's turn it is
		prompter.showMsg("A new game will now begin!\n\nThe rules are as follows: \n" + cardGame.gameStyle, 
				"Chinese Poker Online");
	}

	@Override
	public boolean promptAnotherGame()
	{
		boolean playAgain = 
				prompter.showDualOption("Would you like to play another game?", "Chinese Poker Online", "YES", "NO");
		
		if(cardGame.onlineSession == null)
		{
			if(playAgain)
			{
				cardGame.setUpNewGame();
				gameFinished = false;
			}
			else
			{
				gameFinished = true;
				gameScreen.goToScreen(new WelcomeMenuScr(game));
			}
		}
		else
		{
			if(playAgain)
			{
				cardGame.onlineGameEvent(OnlineGameState.PLAY_AGAIN);
			}
			else
			{
				cardGame.onlineGameEvent(OnlineGameState.DONT_PLAY_AGAIN);
				cardGame.onlineSession.onlineScope = OnlineScope.BROWSE_PORTAL;
				gameScreen.goToScreen(new OnlinePortalScr(game, cardGame.onlineSession));
			}
		}
		
		return playAgain;
	}
	
	/** Called by the Game Screen when online to call online events when necessary */
	public void onlineTimedLogic()
	{
		if(cardGame != null && cardGame.playerTurn != cardGame.onlineSession.playerSlot)	//if it is not your turn, wait
			cardGame.onlineGameEvent(OnlineGameState.WAITING_FOR_TURN);
	}

	@Override
	public void showOnlineMsg(String msg)
	{
		gameScreen.setNewOnlineMessage(msg);
	}

	@Override
	public Game getGame()
	{
		return game;
	}
}
