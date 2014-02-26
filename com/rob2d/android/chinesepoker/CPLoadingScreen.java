package com.rob2d.android.chinesepoker;

import static com.rob2d.android.chinesepoker.Assets.*;
import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;

import com.rob2d.android.chinesepoker.gui.WhateverSoftScr;
import com.rob2d.android.framework.Anim;
import com.rob2d.android.framework.Audio;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.Graphics;
import com.rob2d.android.framework.Graphics.PixmapFormat;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.Pixmap;
import com.rob2d.android.framework.Screen;
import com.rob2d.android.framework.impl.AndroidMusic;


public class CPLoadingScreen extends Screen
{
	public CPLoadingScreen(Game game)
	{
		super(game);
	}
	
	@Override
	public void update(float deltaTime)
	{
		super.update(deltaTime);
		Assets.load(game); //load all static resources(sounds, graphics, etc) into the CSAssets class
		
		//INITIATE THE FIRST SCREEN
		game.setScreen(new WhateverSoftScr(game));
	}

	@Override
	public void present()
	{
		drawEntities(game.getGraphics());
	}

	@Override
	public void timedLogic()
	{}
}