package com.rob2d.android.chinesepoker.gui;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.Graphics;
import com.rob2d.android.framework.Graphics.PixmapFormat;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.Pixmap;
import com.rob2d.android.framework.RectEntity;
import com.rob2d.android.framework.Screen;

public class WhateverSoftScr extends Screen
{
	
	RectEntity fadeRect;
	final int FADE_IN_TIMER = 25;
	Pixmap whateverSoftLogo;
	
	public WhateverSoftScr(Game game)
	{
		super(game);
		ImageEntity companyLogo = 
				new ImageEntity(0, 0, new ImageFrame(Assets.companySplashPix, this), 0, this);	//add the logo to the screen
		
		//add the fading black rectangle
		fadeRect = new RectEntity(new Rect(0, 0, 801, 481), Color.argb(255, 0, 0, 0), 1, this);
	}

	@Override
	public void timedLogic()
	{
		if(gameTimer >= 50)
			goToScreen(new TitleScr(game));
	}

	@Override
	public void fadeInLogic()
	{	
		if(fadeInTimer < FADE_IN_TIMER)
			fadeRect.color = Color.argb(255 - (int)(255/FADE_IN_TIMER * fadeInTimer), 0, 0, 0);
		else
		{
			fadeRect.color = Color.argb(0, 0, 0, 0);
			fadingIn = false;
		}
	}

	@Override
	public void fadeOutLogic()
	{
		if(fadeOutTimer < FADE_IN_TIMER)
			fadeRect.color = Color.argb((int)(255/FADE_IN_TIMER * fadeOutTimer), 0, 0, 0);
		else
		{
			completeFadeOut();
		}
	}
	
	@Override
	public void backPressed()
	{
		System.exit(0);
	}
}
