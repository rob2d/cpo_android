package com.rob2d.android.framework.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/** Copyright 2011 Robert Concepcion III */
public class AndroidFastRenderView extends SurfaceView implements Runnable
{
	AndroidGame game;
	Bitmap frameBuffer;
	Thread renderThread = null;
	SurfaceHolder holder;
	volatile boolean running = false;
	
	/** keeps track of the game's context to load resources later from other classes */
	public static Context context; //
	
	public AndroidFastRenderView(AndroidGame game, Bitmap frameBuffer)
	{
		super(game);
		this.game = game;
		this.frameBuffer = frameBuffer;
		this.holder = getHolder();
	}
	
	public void resume()
	{
		running = true;
		renderThread = new Thread(this);
		renderThread.start();
	}
	
	public void run()
	{
		Looper.prepare();
		Rect dstRect = new Rect();
		long startTime = System.currentTimeMillis();
		while(running)
		{
			if(!holder.getSurface().isValid())
				continue;
			
			long deltaTime = (System.currentTimeMillis() - startTime );	//get the difference in time in miliseconds
			
			if(game.getCurrentScreen() != null)
			{
				synchronized(this)
				{
					game.getCurrentScreen().update(deltaTime);
					game.getCurrentScreen().present();
				}
			}
			
			Canvas canvas = holder.lockCanvas();
			canvas.getClipBounds(dstRect);
			canvas.drawBitmap(frameBuffer, null, dstRect, null);
			holder.unlockCanvasAndPost(canvas);
			startTime = System.currentTimeMillis();
		}
	}
	
	public void pause()
	{
		running = false;
		while(true)
		{
			try
			{
				renderThread.join();
				break;
			}
			catch(InterruptedException e)
			{
				//retry automatically in the while loop
			}
		}
	}
}
