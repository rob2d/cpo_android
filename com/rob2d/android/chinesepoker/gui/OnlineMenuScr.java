package com.rob2d.android.chinesepoker.gui;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.ChinesePoker;
import com.rob2d.android.chinesepoker.menu.MenuButton;
import com.rob2d.android.chinesepoker.menu.ScreenWithButtons;
import com.rob2d.android.chinesepoker.network.LoginInfo;
import com.rob2d.android.chinesepoker.network.OnlineRequests;
import com.rob2d.android.chinesepoker.network.OnlineSession;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.textui.Prompter;

public class OnlineMenuScr extends ScreenWithButtons
{	
	public final int FADE_IN_TIMER = 25;
	
	ImageEntity background;	
	MenuButton loginButton, regisButton;
	OnlineSession onlineSession;

	public OnlineMenuScr(Game game)
	{
		super(game);
		
		//add the background graphic
		background = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
		
		//create buttons
		regisButton = new MenuButton(10,   0, 2, this, Assets.regisBtnIF);
		loginButton	= new MenuButton(10, 120, 2, this, Assets.loginBtnIF);
	}

	@Override
	public void backPressed()
	{
		goToScreen(new WelcomeMenuScr(game));
	}

	@Override
	public void dispose()
	{}
	
	@Override
	public void fadeInLogic()
	{
		if(fadeInTimer == 0)
		{	
			for (MenuButton b : buttons)
				b.x -= 400;
				//b.semiTrans = true;
		}
		if(fadeInTimer < 20)
		{
			for( MenuButton b : buttons )
				b.x += (400/20);
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
		if(loginButton.isClicked)
		{
			login();
			loginButton.isClicked = false;
		}
		else 
		if(regisButton.isClicked)
		{
			game.launchWebsite("http://vovkin.com/choyos/poker/register.php");
			regisButton.isClicked = false;
		}
	}

	@Override
	public void fadeOutLogic()
	{
		//initialize the fade out sequence
		if(fadeOutTimer == 0)
			for (MenuButton b : buttons)
				b.semiTrans = true;
		if(fadeOutTimer < 20)
		{
			for( MenuButton b : buttons )
				b.x -= (400/20);
		}
		else
			completeFadeOut();
	}

	@Override
	public void present()
	{
		drawEntities(game.getGraphics());
	}
	
	public void login()
	{
		boolean DEBUG_LOGIN = true;
		Prompter prompter = game.getPrompter();
		
		String username = null;
		String password = null;
		if(!DEBUG_LOGIN)
		{
			username = prompter.showInputPrompt("Enter your username", "Chinese Poker Online Login");
			password = prompter.showInputPrompt("Enter your password", "Chinese Poker Online Login");
		}
		
		if(DEBUG_LOGIN)
		{
			String[] debugUsers = {"rob2d", "chris", "freddy"};
			int loginSelection = prompter.showTripleOption("Please select a user to login as(BETA TESTING ONLY)", "Chinese Poker Online", 
					debugUsers[0], debugUsers[1], debugUsers[2]);
			username = debugUsers[loginSelection - 1];
			password = "password";
		}
		
		game.startLoadingDialog("please wait while we verify your credentials...", "Logging In", false);
		//if necessary, create a new online session
		if(onlineSession == null)
			onlineSession = new OnlineSession((ChinesePoker) game);
		
		onlineSession.login(username, password);
		game.stopLoadingDialog();
		
		if(onlineSession.loginInfo != null && onlineSession.loginInfo.verified == LoginInfo.LOGIN_VERIFIED)
		{
			prompter.showMsg("You are now logged in as " + onlineSession.loginInfo.userName + ".", "Login Successful!");
			goToScreen(new OnlinePortalScr(game, onlineSession));
		}
		else
		{
			if(onlineSession.loginInfo != null)
				prompter.showMsg(onlineSession.loginInfo.getErrors(), "Login Unsuccessful!");
			else
				prompter.showMsg("Error logging in! There is a problem connecting with the server.", 
					"Login Unsuccessful");
		}
	}
}