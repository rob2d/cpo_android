package com.rob2d.android.chinesepoker;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;

import com.rob2d.android.framework.Audio;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.Graphics;
import com.rob2d.android.framework.Graphics.PixmapFormat;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.Pixmap;
import com.rob2d.android.framework.Sound;

public class Assets
{
	/* images and animations */
	public static Pixmap logoPix;
	public static Pixmap tapScreenPix;
	public static Pixmap companySplashPix;
	public static Pixmap menuBgPix;
	
	public static Pixmap[] localBtnPix;
	public static Pixmap[] onlineBtnPix;
	public static Pixmap[] statsBtnPix;
	public static Pixmap[] credzBtnPix;
	
	public static Pixmap[] setsBtnPix;
	public static Pixmap[] webBtnPix;
	public static Pixmap[] tutBtnPix;
	
	public static Pixmap[] userBtnPix;
	public static Pixmap[] loginBtnPix;
	public static Pixmap[] regisBtnPix;
	
	public static ImageFrame[] localBtnIF;
	public static ImageFrame[] onlineBtnIF;
	public static ImageFrame[] statsBtnIF;
	public static ImageFrame[] credzBtnIF;
	
	public static ImageFrame[] setsBtnIF;
	public static ImageFrame[] webBtnIF;
	public static ImageFrame[] tutBtnIF;
	
	public static ImageFrame[] userBtnIF;
	public static ImageFrame[] loginBtnIF;
	public static ImageFrame[] regisBtnIF;
	
	public static Pixmap[]	 	playTurnBtnPix;
	public static ImageFrame[] 	playTurnBtnIF;
	public static Pixmap[]	 	passTurnBtnPix;
	public static ImageFrame[] 	passTurnBtnIF;
	
	
	public static Pixmap[] 		scrollLeftBtnPix;
	public static ImageFrame[]	scrollLeftBtnIF;
	public static Pixmap[]		scrollRightBtnPix;
	public static ImageFrame[]	scrollRightBtnIF;
	public static Pixmap[]		  scrollUpBtnPix;
	public static ImageFrame[]   scrollUpBtnIF;
	public static Pixmap[]	      scrollDownBtnPix;
	public static ImageFrame[] scrollDownBtnIF;
	public static Pixmap[]		scrollSliderBtn;
	public static ImageFrame[]	scrollSliderIF;
	
	public static Pixmap		onlinePortalTxtPix;
	public static ImageFrame	onlinePortalTxtIF;
	
	public static Pixmap		lobbyModeTxtPix;
	public static ImageFrame	lobbyModeTxtIF;
	
	public static Pixmap		creditsModeTxtPix;
	public static ImageFrame	creditsModeTxtIF;
	
	public static Pixmap		creditsRobPix;
	public static ImageFrame	creditsRobIF;
	public static Pixmap		creditsChrisPix;
	public static ImageFrame	creditsChrisIF;
	
	public static Pixmap[]		dropBtnPix;
	public static ImageFrame[]	dropBtnIF;
	public static Pixmap[]		joinBtnPix;
	public static ImageFrame[]	joinBtnIF;
	public static Pixmap[]		hostBtnPix;
	public static ImageFrame[]	hostBtnIF;
	public static Pixmap[]		refreshBtnPix;
	public static ImageFrame[]	refreshBtnIF;
	public static Pixmap[]		startBtnPix;
	public static ImageFrame[]	startBtnIF;
	
	public static Pixmap		gSI13CPix;
	public static ImageFrame  	gSI13CIF;
	public static Pixmap		gSISplitPix;
	public static ImageFrame	gSISplitIF;
	public static Pixmap		gSIWSPix;
	public static ImageFrame  	gSIWSIF;
	public static Pixmap		gSILCSPix;
	public static ImageFrame  	gSILCSIF;
	public static Pixmap		gSITVPix;
	public static ImageFrame	gSITVIF;
	public static Pixmap		gSITNVPix;
	public static ImageFrame	gSITNVIF;
	public static Pixmap		gSISFA2Pix;
	public static ImageFrame	gSISFA2IF;
	public static Pixmap		gSINSFA2Pix;
	public static ImageFrame	gSINSFA2IF;
	
	public static Pixmap[] cardsPix;
	public static ImageFrame[] cardsIF;

	public static Pixmap imgPauseButton;
	public static Pixmap imgLifeIcon;
	public static Pixmap imgBombIcon;
	public static AssetFileDescriptor[] bgmusic;

	//load sounds
	public static Sound sndBadHit;
	public static Sound sndGoodHit;
	public static Sound sndBomb;
	public static Sound sndRotate;
	public static Sound sndMove;
	
	public static Typeface font1;
	
	/** constant used to access the game's preferences */
	public final static String PREFERENCE_FILENAME = "CPGamePrefs";
	
	public static void load(Game game)
	{
		//get the graphics object to get new images
		Graphics g = game.getGraphics();
		Audio a = game.getAudio();
		
		logoPix = g.newPixmap("gfx/title_screen/title_splash.png", PixmapFormat.RGB565);
		tapScreenPix = g.newPixmap("gfx/title_screen/tap_to_continue.png", PixmapFormat.RGB565);
		menuBgPix	   = g.newPixmap("gfx/menu_bg.png", PixmapFormat.RGB565);
		companySplashPix = g.newPixmap("gfx/whateversoft_splash.png", PixmapFormat.RGB565);
		
		//menu buttons
		localBtnPix = new Pixmap[2];
		localBtnPix[0] = g.newPixmap("gfx/main_menu/button_local.png", PixmapFormat.ARGB4444);
		localBtnPix[1] = g.newPixmap("gfx/main_menu/button_local_selected.png", PixmapFormat.ARGB4444);
		localBtnIF 	 = new ImageFrame[2];
		localBtnIF[0] = new ImageFrame(localBtnPix[0], 0, 0, game);
		localBtnIF[1] = new ImageFrame(localBtnPix[1], 0, 0, game);
		
		onlineBtnPix = new Pixmap[2];
		onlineBtnPix[0] = g.newPixmap("gfx/main_menu/button_online.png", PixmapFormat.ARGB4444);
		onlineBtnPix[1] = g.newPixmap("gfx/main_menu/button_online_selected.png", PixmapFormat.ARGB4444);
		onlineBtnIF 	 = new ImageFrame[2];
		onlineBtnIF[0] = new ImageFrame(onlineBtnPix[0], 0, 0, game);
		onlineBtnIF[1] = new ImageFrame(onlineBtnPix[1], 0, 0, game);
		
		statsBtnPix = new Pixmap[2];
		statsBtnPix[0] = g.newPixmap("gfx/main_menu/button_stats.png", PixmapFormat.ARGB4444);
		statsBtnPix[1] = g.newPixmap("gfx/main_menu/button_stats_selected.png", PixmapFormat.ARGB4444);
		statsBtnIF 	 = new ImageFrame[2];
		statsBtnIF[0] = new ImageFrame(statsBtnPix[0], 0, 0, game);
		statsBtnIF[1] = new ImageFrame(statsBtnPix[1], 0, 0, game);
		
		credzBtnPix = new Pixmap[2];
		credzBtnPix[0] = g.newPixmap("gfx/main_menu/button_credz.png", PixmapFormat.ARGB4444);
		credzBtnPix[1] = g.newPixmap("gfx/main_menu/button_credz_selected.png", PixmapFormat.ARGB4444);
		credzBtnIF 	 = new ImageFrame[2];
		credzBtnIF[0] = new ImageFrame(credzBtnPix[0], 0, 0, game);
		credzBtnIF[1] = new ImageFrame(credzBtnPix[1], 0, 0, game);
		
		loginBtnPix = new Pixmap[2];
		loginBtnPix[0] = g.newPixmap("gfx/main_menu/button_login.png", PixmapFormat.ARGB4444);
		loginBtnPix[1] = g.newPixmap("gfx/main_menu/button_login_selected.png", PixmapFormat.ARGB4444);
		loginBtnIF 	 = new ImageFrame[2];
		loginBtnIF[0] = new ImageFrame(loginBtnPix[0], 0, 0, game);
		loginBtnIF[1] = new ImageFrame(loginBtnPix[1], 0, 0, game);
		
		regisBtnPix = new Pixmap[2];
		regisBtnPix[0] = g.newPixmap("gfx/main_menu/button_register.png", PixmapFormat.ARGB4444);
		regisBtnPix[1] = g.newPixmap("gfx/main_menu/button_register_selected.png", PixmapFormat.ARGB4444);
		regisBtnIF 	 = new ImageFrame[2];
		regisBtnIF[0] = new ImageFrame(regisBtnPix[0], 0, 0, game);
		regisBtnIF[1] = new ImageFrame(regisBtnPix[1], 0, 0, game);
		
		setsBtnPix = new Pixmap[2];
		setsBtnPix[0] = g.newPixmap("gfx/main_menu/button_settings.png", PixmapFormat.ARGB4444);
		setsBtnPix[1] = g.newPixmap("gfx/main_menu/button_settings_selected.png", PixmapFormat.ARGB4444);
		setsBtnIF = new ImageFrame[2];
		setsBtnIF[0] = new ImageFrame(setsBtnPix[0], 0, 0, game);
		setsBtnIF[1] = new ImageFrame(setsBtnPix[1], 0, 0, game);
		
		webBtnPix = new Pixmap[2];
		webBtnPix[0] = g.newPixmap("gfx/main_menu/button_web.png", PixmapFormat.ARGB4444);
		webBtnPix[1] = g.newPixmap("gfx/main_menu/button_web_selected.png", PixmapFormat.ARGB4444);
		webBtnIF = new ImageFrame[2];
		webBtnIF[0] = new ImageFrame(webBtnPix[0], 0, 0, game);
		webBtnIF[1] = new ImageFrame(webBtnPix[1], 0, 0, game);
		
		tutBtnPix = new Pixmap[2];
		tutBtnPix[0] = g.newPixmap("gfx/main_menu/button_tutorial.png", PixmapFormat.ARGB4444);
		tutBtnPix[1] = g.newPixmap("gfx/main_menu/button_tutorial_selected.png", PixmapFormat.ARGB4444);
		tutBtnIF = new ImageFrame[2];
		tutBtnIF[0] = new ImageFrame(tutBtnPix[0], 0, 0, game);
		tutBtnIF[1] = new ImageFrame(tutBtnPix[1], 0, 0, game);
		
		playTurnBtnPix 	  = new Pixmap[3];
		playTurnBtnPix[0] = g.newPixmap("gfx/in_game/play_button.png", PixmapFormat.ARGB4444);
		playTurnBtnPix[1] = g.newPixmap("gfx/in_game/play_button_selected.png", PixmapFormat.ARGB4444);
		playTurnBtnPix[2] = g.newPixmap("gfx/in_game/play_button_disabled.png", PixmapFormat.ARGB4444);
		passTurnBtnPix 	  = new Pixmap[3];
		passTurnBtnPix[0] = g.newPixmap("gfx/in_game/pass_button.png", PixmapFormat.ARGB4444);
		passTurnBtnPix[1] = g.newPixmap("gfx/in_game/pass_button_selected.png", PixmapFormat.ARGB4444);
		passTurnBtnPix[2] = g.newPixmap("gfx/in_game/pass_button_disabled.png", PixmapFormat.ARGB4444);
		playTurnBtnIF 	  = new ImageFrame[3];
		playTurnBtnIF[0]  = new ImageFrame(playTurnBtnPix[0], game);
		playTurnBtnIF[1]  = new ImageFrame(playTurnBtnPix[1], game);
		playTurnBtnIF[2]  = new ImageFrame(playTurnBtnPix[2], game);
		passTurnBtnIF 	  = new ImageFrame[3];
		passTurnBtnIF[0]  = new ImageFrame(passTurnBtnPix[0], game);
		passTurnBtnIF[1]  = new ImageFrame(passTurnBtnPix[1], game);
		passTurnBtnIF[2]  = new ImageFrame(passTurnBtnPix[2], game);
		
		scrollLeftBtnPix = new Pixmap[3];
		scrollLeftBtnPix[0] =  g.newPixmap("gfx/in_game/scrollleft_button.png", PixmapFormat.ARGB4444);
		scrollLeftBtnPix[1] =  g.newPixmap("gfx/in_game/scrollleft_button_selected.png", PixmapFormat.ARGB4444);
		scrollLeftBtnPix[2] =  g.newPixmap("gfx/in_game/scrollleft_button_disabled.png", PixmapFormat.ARGB4444);
		scrollRightBtnPix = new Pixmap[3];
		scrollRightBtnPix[0] = g.newPixmap("gfx/in_game/scrollright_button.png", PixmapFormat.ARGB4444);
		scrollRightBtnPix[1] = g.newPixmap("gfx/in_game/scrollright_button_selected.png", PixmapFormat.ARGB4444);
		scrollRightBtnPix[2] = g.newPixmap("gfx/in_game/scrollright_button_disabled.png", PixmapFormat.ARGB4444);
		scrollUpBtnPix = new Pixmap[3];
		scrollUpBtnPix[0] = g.newPixmap("gfx/lobby_menu/scrollup_button.png", PixmapFormat.ARGB4444);
		scrollUpBtnPix[1] = g.newPixmap("gfx/lobby_menu/scrollup_button_selected.png", PixmapFormat.ARGB4444);
		scrollUpBtnPix[2] = g.newPixmap("gfx/lobby_menu/scrollup_button_disabled.png", PixmapFormat.ARGB4444);
		scrollDownBtnPix = new Pixmap[3];
		scrollDownBtnPix[0] = g.newPixmap("gfx/lobby_menu/scrolldown_button.png", PixmapFormat.ARGB4444);
		scrollDownBtnPix[1] = g.newPixmap("gfx/lobby_menu/scrolldown_button_selected.png", PixmapFormat.ARGB4444);
		scrollDownBtnPix[2] = g.newPixmap("gfx/lobby_menu/scrolldown_button_disabled.png", PixmapFormat.ARGB4444);
		scrollSliderBtn		 = new Pixmap[2];
		scrollSliderBtn[0] 	 = g.newPixmap("gfx/in_game/scroll_slider_button.png", PixmapFormat.ARGB4444);
		scrollSliderBtn[1] 	 = g.newPixmap("gfx/in_game/scroll_slider_button_selected.png", PixmapFormat.ARGB4444);
		dropBtnPix			= new Pixmap[3];
		dropBtnPix[0]		= g.newPixmap("gfx/lobby_menu/drop_button.png", PixmapFormat.ARGB4444);
		dropBtnPix[1]		= g.newPixmap("gfx/lobby_menu/drop_button_selected.png", PixmapFormat.ARGB4444);
		dropBtnPix[2]		= g.newPixmap("gfx/lobby_menu/drop_button_disabled.png", PixmapFormat.ARGB4444);
		dropBtnIF			= new ImageFrame[3];
		dropBtnIF[0]		= new ImageFrame(dropBtnPix[0], game);
		dropBtnIF[1]		= new ImageFrame(dropBtnPix[1], game);
		dropBtnIF[2]		= new ImageFrame(dropBtnPix[2], game);
		joinBtnPix			= new Pixmap[3];
		joinBtnPix[0]		= g.newPixmap("gfx/lobby_menu/join_button.png", PixmapFormat.ARGB4444);
		joinBtnPix[1]		= g.newPixmap("gfx/lobby_menu/join_button_selected.png", PixmapFormat.ARGB4444);
		joinBtnPix[2]		= g.newPixmap("gfx/lobby_menu/join_button_disabled.png", PixmapFormat.ARGB4444);
		joinBtnIF			= new ImageFrame[3];
		joinBtnIF[0]		= new ImageFrame(joinBtnPix[0], game);
		joinBtnIF[1]		= new ImageFrame(joinBtnPix[1], game);
		joinBtnIF[2]		= new ImageFrame(joinBtnPix[2], game);
		hostBtnPix			= new Pixmap[3];
		hostBtnPix[0]		= g.newPixmap("gfx/lobby_menu/host_button.png", PixmapFormat.ARGB4444);
		hostBtnPix[1]		= g.newPixmap("gfx/lobby_menu/host_button_selected.png", PixmapFormat.ARGB4444);
		hostBtnPix[2]		= g.newPixmap("gfx/lobby_menu/host_button_disabled.png", PixmapFormat.ARGB4444);
		hostBtnIF			= new ImageFrame[3];
		hostBtnIF[0]		= new ImageFrame(hostBtnPix[0], game);
		hostBtnIF[1]		= new ImageFrame(hostBtnPix[1], game);
		hostBtnIF[2]		= new ImageFrame(hostBtnPix[2], game);
		
		refreshBtnPix			= new Pixmap[3];
		refreshBtnPix[0]		= g.newPixmap("gfx/lobby_menu/refresh_button.png", PixmapFormat.ARGB4444);
		refreshBtnPix[1]		= g.newPixmap("gfx/lobby_menu/refresh_button_selected.png", PixmapFormat.ARGB4444);
		refreshBtnPix[2]		= g.newPixmap("gfx/lobby_menu/refresh_button_disabled.png", PixmapFormat.ARGB4444);
		refreshBtnIF			= new ImageFrame[3];
		refreshBtnIF[0]		= new ImageFrame(refreshBtnPix[0], game);
		refreshBtnIF[1]		= new ImageFrame(refreshBtnPix[1], game);
		refreshBtnIF[2]		= new ImageFrame(refreshBtnPix[2], game);
		
		startBtnPix			= new Pixmap[3];
		startBtnPix[0]		= g.newPixmap("gfx/lobby_menu/start_button.png", PixmapFormat.ARGB4444);
		startBtnPix[1]		= g.newPixmap("gfx/lobby_menu/start_button_selected.png", PixmapFormat.ARGB4444);
		startBtnPix[2]		= g.newPixmap("gfx/lobby_menu/start_button_disabled.png", PixmapFormat.ARGB4444);
		startBtnIF			= new ImageFrame[3];
		startBtnIF[0]		= new ImageFrame(startBtnPix[0], game);
		startBtnIF[1]		= new ImageFrame(startBtnPix[1], game);
		startBtnIF[2]		= new ImageFrame(startBtnPix[2], game);
		
		scrollLeftBtnIF = new ImageFrame[3];
		scrollLeftBtnIF[0] = new ImageFrame(scrollLeftBtnPix[0], game);
		scrollLeftBtnIF[1] = new ImageFrame(scrollLeftBtnPix[1], game);
		scrollLeftBtnIF[2] = new ImageFrame(scrollLeftBtnPix[2], game);
		scrollRightBtnIF = new ImageFrame[3];
		scrollRightBtnIF[0] = new ImageFrame(scrollRightBtnPix[0], game);
		scrollRightBtnIF[1] = new ImageFrame(scrollRightBtnPix[1], game);
		scrollRightBtnIF[2] = new ImageFrame(scrollRightBtnPix[2], game);
		scrollUpBtnIF = new ImageFrame[3];
		scrollUpBtnIF [0] = new ImageFrame(scrollUpBtnPix[0], game);
		scrollUpBtnIF[1] = new ImageFrame(scrollUpBtnPix[1], game);
		scrollUpBtnIF[2] = new ImageFrame(scrollUpBtnPix[2], game);
		scrollDownBtnIF = new ImageFrame[3];
		scrollDownBtnIF[0] = new ImageFrame(scrollDownBtnPix[0], game);
		scrollDownBtnIF[1] = new ImageFrame(scrollDownBtnPix[1], game);
		scrollDownBtnIF[2] = new ImageFrame(scrollDownBtnPix[2], game);
		scrollSliderIF			= new ImageFrame[2];
		scrollSliderIF[0]		= new ImageFrame(scrollSliderBtn[0], game);
		scrollSliderIF[1]		= new ImageFrame(scrollSliderBtn[1], game);
		
		lobbyModeTxtPix			= g.newPixmap("gfx/lobby_menu/lobby_mode_text.png", PixmapFormat.ARGB4444);
		lobbyModeTxtIF			= new ImageFrame(lobbyModeTxtPix, game);
		onlinePortalTxtPix		= g.newPixmap("gfx/lobby_menu/online_portal_text.png", PixmapFormat.ARGB4444);
		onlinePortalTxtIF		= new ImageFrame(onlinePortalTxtPix, game);
		creditsModeTxtPix		= g.newPixmap("gfx/credits_screen/credits_title.png", PixmapFormat.ARGB4444);
		creditsModeTxtIF		= new ImageFrame(creditsModeTxtPix, game);
		
		creditsChrisPix			= g.newPixmap("gfx/credits_screen/credits_chris.jpg", PixmapFormat.RGB565);
		creditsChrisIF			= new ImageFrame(creditsChrisPix, game);
		
		creditsRobPix			= g.newPixmap("gfx/credits_screen/credits_rob.jpg", PixmapFormat.RGB565);
		creditsRobIF			= new ImageFrame(creditsRobPix, game);
		
		gSI13CPix = g.newPixmap("gfx/lobby_menu/icon_13ct.png", PixmapFormat.ARGB4444);
		gSISplitPix= g.newPixmap("gfx/lobby_menu/icon_13cf.png", PixmapFormat.ARGB4444);
		gSIWSPix  = g.newPixmap("gfx/lobby_menu/icon_wst.png", PixmapFormat.ARGB4444);
		gSILCSPix  = g.newPixmap("gfx/lobby_menu/icon_wsf.png", PixmapFormat.ARGB4444);
		gSISFA2Pix = g.newPixmap("gfx/lobby_menu/icon_sfa2t.png", PixmapFormat.ARGB4444);
		gSINSFA2Pix= g.newPixmap("gfx/lobby_menu/icon_sfa2f.png", PixmapFormat.ARGB4444);
		gSITVPix = g.newPixmap("gfx/lobby_menu/icon_tvt.png", PixmapFormat.ARGB4444);
		gSITNVPix = g.newPixmap("gfx/lobby_menu/icon_tvf.png", PixmapFormat.ARGB4444);
		
		gSI13CIF = new ImageFrame(gSI13CPix, game);
		gSISplitIF = new ImageFrame(gSISplitPix, game);
		gSIWSIF	 = new ImageFrame(gSIWSPix, game);
		gSILCSIF  = new ImageFrame(gSILCSPix, game);
	
		gSISFA2IF = new ImageFrame(gSISFA2Pix, game);
		gSINSFA2IF = new ImageFrame(gSINSFA2Pix, game);
		
		cardsPix = new Pixmap[52];
		cardsIF = new ImageFrame[52];
		for(int i = 0; i < 52; i++)
		{
			String rank = null; char suite = 0;
			switch((int)(i / 4))
			{
				case 0: rank = "3"; break;
				case 1: rank = "4"; break;
				case 2: rank = "5"; break;
				case 3: rank = "6"; break;
				case 4: rank = "7"; break;
				case 5: rank = "8"; break;					
				case 6: rank = "9"; break;
				case 7: rank = "10"; break;
				case 8: rank = "J"; break;
				case 9: rank = "Q"; break;
				case 10:rank = "K"; break;
				case 11:rank = "A"; break;
				case 12:rank = "2"; break;
			}
			
			switch(i % 4)
			{
				case 0: suite = 'd'; break;
				case 1: suite = 'c'; break;
				case 2: suite = 'h'; break;
				case 3: suite = 's'; break;
			}
			
			cardsPix[i] = g.newPixmap("gfx/in_game/card" + rank + suite + ".png", PixmapFormat.ARGB4444);
			cardsIF[i] = new ImageFrame(cardsPix[i], 0, 0, game);
		}

		//fonts
		font1 = Typeface.createFromAsset(((Activity)game).getApplicationContext().getAssets(), "fonts/supermercado.ttf");
		
		//load game settings after initializing
		game.getSettings().loadSettings();
	}
}