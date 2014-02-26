package com.rob2d.android.framework;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;

/** Copyright 2011 Robert Concepcion III */
public abstract class Screen 
{
	public int fadeInTimer = 0;
	public int gameTimer = 0;
	public int fadeOutTimer = 0;
	Screen nextScreen = null;
	public int layerCount = 6;	//DEFAULT OF 6 LAYERS TO DRAW ON
	public long lastLogicTick;
	public int [] touchTimer = {0, 0};
	public int [] touchLength = {0, 0};
	public Music musicHandler;
	
	public Game game;
	public ArrayList<ScreenEntity>[] entities;	//list of entities within our screen
	public ArrayList<ScreenEntity> destroyEntities = new ArrayList<ScreenEntity>();
	
	public int backPressed = 0;
	
	public boolean fadingIn = true,
			   	   fadingOut= false;
	
	
	public Screen(Game game) 
	{
		this.game = game;
		musicHandler = game.getMusicHandler();
		
		//initialize the layers for entity drawing
		if(layerCount != -1)
		{
			entities = new ArrayList[layerCount];
			for(int i = 0; i < layerCount; i++)
			{
				entities[i] = new ArrayList<ScreenEntity>();
				entities[i].clear();
			}
		}
		 lastLogicTick = (System.currentTimeMillis() - 20) % 20;	//set as if the last logic tick happened right before the app started
		 fadeInTimer = 0;
	}
	
	public void update(float deltaTime)
	{
		if(game.getInput().isKeyPressed(KeyEvent.KEYCODE_BACK) && backPressed < 10)
			backPressed += 1;
		else if(!game.getInput().isKeyPressed(KeyEvent.KEYCODE_BACK))
			backPressed = 0;
		
		//run the "backPressed()" method which is overloaded by subclasses of the Screen object!
		if(backPressed == 1 && (fadeInTimer + gameTimer > 5))
			backPressed();
		
		//if current music track has finished and music is enabled, then play next track
			musicHandler.update();
		
		//PRECURSOR TO TIMEDLOGIC()
		long timePassed = System.currentTimeMillis() - lastLogicTick;
		if(timePassed >= 20 || fadeInTimer == 0)
		{				
			//GESTURE STUFF
			//test touch events being timed for each finger(0, 1)
			for(int i = 0; i < 2; i++)
			{
				touchLength[i] = 0;
				//update the state of the touch Sense during logic Timing Events
				if(game.getInput().isTouchDown(i) && touchTimer[i] < 20)
					touchTimer[i] += 1;
				else if(!game.getInput().isTouchDown(i))
				{
					touchLength[i] = touchTimer[i];
					touchTimer[i] = 0;
				}
			}
			
			synchronized(this)
			{
			//UPDATE ENTITIES/RUN THEIR LOGIC
			for(int i = 0; i < entities.length; i++)
				for(ScreenEntity e : entities[i])
				{
					e.update();
					if(!fadingIn && !fadingOut)
					{
						if(e.touchable == true)
						{
							if(game.getInput().isTouchDown(0))
							{
								if(touchTimer[0] == 1)					//if tapping the screen, set the entity as touched if it happens to be within the entities' bounds
									e.touched(e.getBounds().contains(game.getInput().getTouchX(0), game.getInput().getTouchY(0)));
								else if(e.touched == true)				//otherwise only allow touched to stay true if we're still touching it...
									e.touched(e.getBounds().contains(game.getInput().getTouchX(0), game.getInput().getTouchY(0)));
							}
							else if(!game.getInput().isTouchDown(0) && e.touched)
							{
																					//lastly, if we're not touching the screen and the entity is listed as touched
									e.touched(false);						// then set it not being touched anymore
							}
						}
					}
				}
			
			
			if(!fadingIn && !fadingOut)	//if in game and no fadeIn or fadeOut sequence happenig...
			{
				timedLogic();	//run the current screen's logic event
				gameTimer++;	//increment gaming timer
				
				//DETECT SCREEN TAPS!
				//if the screen is touched with index 0(first finger), jump to the menu!
				if(this.touchLength[0] > 0 && touchLength[0] < 10 && touchTimer[0] == 0)
					screenTapped();	
			}
			else if(fadingIn)
			{
				fadeInLogic();
				fadeInTimer++;
			}
			else if(fadingOut)	//keep track of fade out timer
			{
				fadeOutLogic();
				fadeOutTimer++;
			}
		
			lastLogicTick = System.currentTimeMillis() - (System.currentTimeMillis() % 20);
		}
		}
		
		//clear destroyed entities from the screen so the entities loop will be shorter
		synchronized(this)
		{
			if(destroyEntities != null)
			//destroy all necessary entities
			while(destroyEntities.size() > 0)
			{
				entities[destroyEntities.get(0).layer].remove(destroyEntities.get(0));
				destroyEntities.remove(0).destroy();
			}
		}
	}
	public abstract void timedLogic();
	public void present()
	{
		drawEntities(game.getGraphics());
	}
	
	public void pause()
	{}
	
	public void resume()
	{}
	
	public void dispose()
	{
		for(int i = 0 ; i < entities.length; i++)
		{
			entities[i].clear();
			entities[i] = null;
		}
		entities = null;	
		for(int i = 0; i < destroyEntities.size(); i++)
			destroyEntities.remove(i);
		destroyEntities = null;
		
		musicHandler = null;
		
		game = null;
	}
	/* optional method which is only overriden to detect if a loading dialog which was cancelable was exited */
	public void onLoadingDialogCanceled()
	{}
	/** OPTIONAL. called when the user presses the back key */
	public void backPressed()
	{}
	
	/** OPTIONAL. just makes it easier to detect a simple screen tap from the user */
	public void screenTapped()
	{}
	
	/** method to draw all entities on the screen*/
	public void drawEntities(Graphics g)
	{
		g.drawRect(0, 0, g.getWidth(), g.getHeight(), Color.WHITE);	//clear the screen at the start of every draw loop
		for(int i = 0; i < entities.length; i++)
			for(ScreenEntity e : entities[i])
			{
				//if it is animatable, complete its logic
				if(e instanceof AnimEntity && e.visible)
				{
					g.drawPixmap(((AnimEntity)e).imgFrame.getImg(), (int)e.x - ((AnimEntity)e).imgFrame.actionPoint.x, (int)e.y - ((AnimEntity)e).imgFrame.actionPoint.y);
				}
				//and then display its given image
				else if(e instanceof ImageEntity && e.visible)
				{
					//simplify our calculations by doing a few ahead of time
					ImageEntity sprite = (ImageEntity)e;
					int xDrawn = (int)sprite.x - sprite.imgFrame.actionPoint.x;	//position to draw in x coordinates
					int yDrawn = (int)sprite.y - sprite.imgFrame.actionPoint.y; // position to draw in y coordinates
					
					//draw the sprites
					if(sprite.rotation == 0)
					{
						if(!sprite.semiTrans)
							g.drawPixmap(sprite.imgFrame.getImg(), xDrawn, yDrawn);
						else
							g.drawPixmapAlpha(sprite.imgFrame.getImg(), xDrawn, yDrawn, sprite.alpha);
					}
					else
					{
						if(!sprite.semiTrans)
							g.drawPixmapRotated(sprite.imgFrame.getImg(), xDrawn, yDrawn, sprite.imgFrame.actionPoint, (360 - (int)Math.round(sprite.rotation)) % 360);
						else
							g.drawPixmapRotatedAlpha(sprite.imgFrame.getImg(), xDrawn, yDrawn, sprite.imgFrame.actionPoint, (360 - (int)Math.round(sprite.rotation)) % 360, sprite.alpha);
					}
				}
				if(e instanceof TextEntity && e.visible)
				{
					TextEntity t = (TextEntity)e;
					Paint p = new Paint();
					p.setColor(t.color);
					p.setTextSize(t.size);
					p.setTypeface(t.font);
					g.drawText(((TextEntity)e).string, (int)e.x, (int)e.y, p);
				}
				
				if(e instanceof RectEntity && e.visible)
					g.drawRect(((RectEntity)e).rect.left + (int)e.x, 
							   ((RectEntity)e).rect.top + (int)e.y, 
							   (((RectEntity)e).rect.right - ((RectEntity)e).rect.left) , 
							   (((RectEntity)e).rect.bottom - ((RectEntity)e).rect.top), 
							   ((RectEntity)e).color);
				/*
				 * For drawing collision boxes... debug
				 * 
				 * if(e instanceof Collidable)
				{
					Collidable c = (Collidable)e;
					g.drawRect(c.getCollidable().left, c.getCollidable().top, 32, 32, Color.argb(100, 255, 0, 0));
				} */
			}
	}
	
	/** starts at the beginning of every screen. */
	public void fadeInLogic()
	{fadingIn = false;}
	
	/** initialize the fade out sequence, store next screen to go to when its complete.
	 * If it's not implemented, the default behavior is to go straight to the next screen! */
	public void goToScreen(Screen nScreen)
	{
		if(!fadingOut)
		{
			nextScreen = nScreen;
			fadingOut = true;
		}
	}
	
	public void completeFadeOut()
	{
		game.setScreen(nextScreen);
	}
	
	/** runs at the end of every screen. 
	 * If it's not implemented, default behavior is to go straight to next screen! */
	public void fadeOutLogic()
	{completeFadeOut();}
}