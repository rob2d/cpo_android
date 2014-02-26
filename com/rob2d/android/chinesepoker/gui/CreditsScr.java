package com.rob2d.android.chinesepoker.gui;

import java.util.ArrayList;

import android.graphics.Color;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.Screen;
import com.rob2d.android.framework.TextEntity;

public class CreditsScr extends Screen
{	
	public final int FADE_IN_TIMER = 20;
	ImageEntity 	   background = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
	ImageEntity		 titleTextImg = new ImageEntity(0, 20, Assets.creditsModeTxtIF, 1, this);
	public TextEntity scrollInTitle = new TextEntity(800, 200, new StringBuffer("..."), Color.WHITE, Assets.font1,
														36, 3, this),
					  scrollInAuthors = new TextEntity(800, 300, new StringBuffer("..."), Color.WHITE, Assets.font1,
														36, 3, this);
	
	public ArrayList<String> sectionTitles = new ArrayList<String>(), sectionAuthors = new ArrayList<String>();
	public int currentSection = -1;
	public int sectionTimer = 0;
	public boolean sectionFadedOut = false;
	
	public CreditsScr(Game game)
	{
		super(game);
		titleTextImg.x = 400 - titleTextImg.getBounds().right / 2;	//center title screen logo
		scrollInTitle.semiTrans = true;
		scrollInAuthors.semiTrans = true;
		scrollInTitle.alpha = 0; 
		scrollInAuthors.alpha = 0;
		
		sectionTitles.add("Application Programmer, Planner & Designer");
		sectionAuthors.add("Robert Concepcion III");
		
		sectionTitles.add("Database Developer, Web Programmer & Artist");
		sectionAuthors.add("Christopher Hoyos");
		
		sectionTitles.add("Special Thanks");
		sectionAuthors.add("Professor Akhtar");
		
		sectionTitles.add("Special Thanks");
		sectionAuthors.add("You for playing!!!");
		
		nextSection();	//begin showing slides...
	}
	
	@Override
	public void timedLogic()
	{	
		//scroll in the info...
		if(scrollInTitle.x > 40)
		{
			scrollInTitle.x -= 4;
			//fade in info while its scrolling in on screen!
			if(!sectionFadedOut && scrollInTitle.alpha < 255 && scrollInTitle.x < 800)
				scrollInTitle.alpha += 5;
		}
		if(scrollInAuthors.x + scrollInAuthors.getWidth() > 760)
		{
			scrollInAuthors.x -= 4;
			//fade in info while its scrolling in on screen!
			if(!sectionFadedOut && scrollInAuthors.alpha < 255 && scrollInAuthors.x < 800)
				scrollInAuthors.alpha += 5;
		}
		else		//add to timer to get new info otherwise
		{
			if(sectionTimer < 50)
				sectionTimer += 1;
		}
		
		if(sectionTimer == 50)	//if its been half a minute, increment or if we're in last section then return to menu
		{
			if(!sectionFadedOut)
			{
				if(scrollInTitle.alpha > 0)
				{
					scrollInTitle.alpha -= 5;
					scrollInAuthors.alpha -= 5;
				}
				else
				{
					sectionFadedOut = true;
				}
			}
			if(sectionFadedOut)
			{
				sectionTimer = sectionTimer % 50;
				if(currentSection < sectionTitles.size() - 1)
					nextSection();
				else
					goToScreen(new WelcomeMenuScr(game));
			}
		}
		

		
		//set the alpha of the scrolling in text(this is because TextEntities do not support alpha values since
		//they can display in an alpha color instead!
		scrollInAuthors.color = Color.argb(scrollInAuthors.alpha, 255, 255, 255);
		scrollInTitle.color = Color.argb(scrollInTitle.alpha, 255, 255, 255);
		Log.d("CPDEBUG", "sectionFadedOut = " + sectionFadedOut + ", sectionTimer = " + sectionTimer);
	}
	
	public void nextSection()
	{
		sectionFadedOut = false;
		sectionTimer = 0;
		currentSection++;	//increment section
		//change title and author text
		scrollInTitle.x = 800;
		scrollInAuthors.x = 1600;
		scrollInTitle.string = new StringBuffer(sectionTitles.get(currentSection));
		scrollInAuthors.string = new StringBuffer(sectionAuthors.get(currentSection));
	}

	@Override
	public void backPressed()
	{
		goToScreen(new WelcomeMenuScr(game));
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
		}
		if(fadeInTimer < 20)
		{
		}
		else
		{
			fadingIn = false;
		}
	}

	@Override
	public void fadeOutLogic()
	{
		if(fadeOutTimer < FADE_IN_TIMER)
		{
			
		}
		else
			completeFadeOut();
	}

	@Override
	public void present()
	{
		drawEntities(game.getGraphics());
	}
}
