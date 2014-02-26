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

public class TitleScr extends Screen
{	
	//fading logic fields
	RectEntity fadeRect;
	public int FADE_IN_TIMER  = 10,
			   FADE_OUT_TIMER = 10;
	
	//the title logo graphic
	ImageEntity titleLogo;
	ImageEntity titleBg;
	ImageEntity tapScreenImg;
	int tapScreenImgWidth;
	
	public TitleScr(Game game)
	{
		super(game);
		
		//load the menu background into an entity
		titleBg   = new ImageEntity(0, 0, new ImageFrame(Assets.menuBgPix, this), 0, this);
		
		//load the title logo into memory and add it to the frame
		titleLogo = new ImageEntity(0, 0, new ImageFrame(Assets.logoPix, this), 1, this);
		tapScreenImg = new ImageEntity(-500, 416, new ImageFrame(Assets.tapScreenPix, this), 1, this);
		tapScreenImg.dx = 4;
		tapScreenImg.visible = false;
		tapScreenImg.semiTrans = true;
		tapScreenImgWidth = tapScreenImg.imgFrame.getImg().getWidth();
		
		//add entities to the frame
		entities[0].add(titleBg);
		entities[0].add(titleLogo);	//add the logo to the screen
		
		//add the fading black rectangle
		fadeRect = new RectEntity(new Rect(0, 0, 801, 481), Color.argb(255, 0, 0, 0), 1, this);
		entities[fadeRect.layer].add(fadeRect);
	}

	@Override
	public void timedLogic()
	{
		tapScreenImg.alpha = 255 - (int)((800 - tapScreenImg.x - tapScreenImgWidth) * 0.25);
		tapScreenImg.dx = (800 - tapScreenImg.x - tapScreenImgWidth) / 30;
		if(tapScreenImg.dx < 0.1)
			tapScreenImg.dx= 0;
		if(tapScreenImg.x > 850)
			tapScreenImg.x = -tapScreenImgWidth - 50;
	}
	
	@Override
	public void fadeInLogic()
	{
		if(fadeInTimer < FADE_IN_TIMER)
			fadeRect.color = Color.argb(255 - (int)(255/FADE_IN_TIMER * fadeInTimer), 0, 0, 0);
		else
		{
			tapScreenImg.visible = true;
			fadeRect.visible = false;
			fadingIn = false;
		}
	}
	
	@Override
	public void fadeOutLogic()
	{
		titleLogo.semiTrans = true;
		if(fadeOutTimer < FADE_OUT_TIMER)
		{
			titleLogo.alpha = 255 - (int)(255/FADE_OUT_TIMER * fadeOutTimer);
			tapScreenImg.x += (tapScreenImgWidth + 50) / FADE_OUT_TIMER;
			tapScreenImg.alpha = 255 - (int)(255/FADE_OUT_TIMER * fadeOutTimer);
		}
		else
			completeFadeOut();
	}

	@Override
	public void pause()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void resume()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void backPressed()
	{
		System.exit(1);
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void screenTapped()
	{
		goToScreen(new WelcomeMenuScr(game));
	}

	@Override
	public void present()
	{
		drawEntities(game.getGraphics());	
	}
}
