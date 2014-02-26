package com.rob2d.android.chinesepoker.gui;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.ChinesePokerSettings;
import com.rob2d.android.chinesepoker.menu.MenuButton;
import com.rob2d.android.chinesepoker.menu.ScreenWithButtons;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.impl.AndroidGame;

public class WelcomeMenuScr extends ScreenWithButtons
{	
	public final int FADE_IN_TIMER = 20;
	
	MenuButton localButton, onlineButton, statsButton, credzButton, webButton, tutButton, setsButton;
	
	public WelcomeMenuScr(Game game)
	{
		super(game);
		
		//add the background graphic
		ImageEntity background = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
		
		//create buttons
		localButton 	= new MenuButton(10, 0, 2, this, Assets.localBtnIF);
		onlineButton	= new MenuButton(10, 120, 2, this, Assets.onlineBtnIF);
		statsButton		= new MenuButton(10, 240, 2, this, Assets.statsBtnIF);
		credzButton		= new MenuButton(10, 360, 2, this, Assets.credzBtnIF);
		webButton	    = new MenuButton(660, 60, 2, this, Assets.webBtnIF);	//init settings button
		tutButton	    = new MenuButton(660, 210, 2, this, Assets.tutBtnIF);	//init tutorial button
		setsButton	    = new MenuButton(660, 360, 2, this, Assets.setsBtnIF); //init settings button
	}

	@Override
	public void backPressed()
	{
		goToScreen(new TitleScr(game));
	}

	@Override
	public void dispose()
	{
	}
	
	@Override
	public void fadeInLogic()
	{
		if(fadeInTimer == 0)
		{	
			localButton.x = onlineButton.x = statsButton.x  = credzButton.x  -= (400);
			setsButton.semiTrans = tutButton.semiTrans = webButton.semiTrans = true;
			setsButton.alpha = tutButton.alpha = webButton.alpha = 0;
		}
		if(fadeInTimer < 20)
		{
			localButton.x = onlineButton.x = statsButton.x = credzButton.x += (400/FADE_IN_TIMER);
			setsButton.alpha = webButton.alpha = tutButton.alpha = (255/FADE_IN_TIMER) * fadeInTimer;
		}
		else
		{
			fadingIn = false;
		}
	}
	
	@Override
	public void timedLogic()
	{
		super.timedLogic();
		if(localButton.isClicked)
			goToScreen(new GameScr(null, game));
		else if(onlineButton.isClicked)
			goToScreen(new OnlineMenuScr(game));
		else if(statsButton.isClicked)
			game.launchWebsite("http://www.vovkin.com/choyos/poker/highscores.php");
		else if(credzButton.isClicked)
			goToScreen(new CreditsScr(game));
		else if(webButton.isClicked)
			game.launchWebsite("http://whateversoft.webuda.com");
		else if(tutButton.isClicked)
			game.launchWebsite("http://lmgtfy.com/?q=How+to+play+Big+Two");
		else if(setsButton.isClicked)
		{
			promptForSettings();
			setsButton.isClicked = false;
		}
	}

	@Override
	public void fadeOutLogic()
	{
		//initialize the fade out sequence
		if(fadeOutTimer == 0)
			for (MenuButton b : buttons)
				b.semiTrans = true;
		
		if(fadeOutTimer < FADE_IN_TIMER)
		{
				localButton.x = onlineButton.x = statsButton.x = credzButton.x -= (400/FADE_IN_TIMER);
				setsButton.alpha = tutButton.alpha = webButton.alpha = 255 - ((255/FADE_IN_TIMER) * fadeOutTimer);
		}
		else
			completeFadeOut();
	}
	
	public void promptForSettings()
	{
		((AndroidGame)game).getPrompter().showMsg("We will now be adjusting the game settings", "Chinese Poker Online");
		
		game.getSettings().setOption(ChinesePokerSettings.DEAL_13_CARDS,
		((AndroidGame)game).getPrompter().showDualOption("How will cards in the game be dealt among players?", "Chinese Poker Online", "13 CARDS EACH", "SPLIT THE DECK"));
		
		game.getSettings().setOption(ChinesePokerSettings.WINNER_STARTS,
		((AndroidGame)game).getPrompter().showDualOption("After the first game, who gets to play first?", "Chinese Poker Online", "WINNER", "LOWEST CARD IN HAND"));
		
		game.getSettings().setOption(ChinesePokerSettings.TRIPLES_VALID,
		((AndroidGame)game).getPrompter().showDualOption("Will triples(3 cards of one rank) be a valid play?", "Chinese Poker Online", "YES", "NO"));
		
		game.getSettings().setOption(ChinesePokerSettings.START_FLUSH_A2,
		((AndroidGame)game).getPrompter().showDualOption("Can flushes start from Ace or Deuce(e.g. A2345, 23456)?", "Chinese Poker Online", "YES", "NO"));
		
		game.getSettings().saveSettings();
		
		((AndroidGame)game).getPrompter().showMsg("Alright! Your new game settings have been saved!", "Chinese Poker Online");
	}
}
