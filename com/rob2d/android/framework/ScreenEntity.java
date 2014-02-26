package com.rob2d.android.framework;

import android.graphics.Rect;
import android.util.Log;

/** Copyright 2011 Robert Concepcion III */
public abstract class ScreenEntity
{
	public float   x, y, dx, dy;
	public int 	   layer;
	public Screen  screen;
	public boolean destroyed = false;
	public boolean visible = true;
	public boolean semiTrans = false;
	public int	   alpha = 255;
	
	public boolean touchable = false;
	boolean touched = false;
	public boolean touchJustReleased = false;
	public int touchTimer = 0;
	
	public ScreenEntity(int l, Screen scr)
	{
		//-------------------------------------------------------//
		//	SAVE DEFAULT CONSTRUCTOR VALUES(layer, parent screen)
		//-------------------------------------------------------//
		layer = l;
		screen = scr;
		scr.entities[layer].add(this);
	}
	
	public void touched(boolean t)
	{
		touched = t;
	}
	
	/** called before every logic event happens during the game(every x miliseconds) so that the entities can have their unique behaviors. This includes
	 *  moving the coordinates relative to dx/dy. */	
	public synchronized void update()
	{
		//-----------------------------------------//
		//	SHIFT COORDINATES ACCORDING TO DX/DY
		//-----------------------------------------//
		x += dx;
		y += dy;
		
		touchJustReleased = false;	
		if(touched)
			touchTimer += 1;
		else
		{
			if(touchTimer > 0)
				touchJustReleased = true;
			touchTimer = 0;
		}
	}

	public abstract Rect getBounds();
	/** helper method which allows entities to be destroyed from the screen without causing a concurrent modification error. This is done by using an arraylist
	 *  on the screen which destroys objects outside of any for loops after the entity's destroy flag has been set.*/
	public synchronized void destroy()
	{
		//-----------------------------------------------------------------------------//
		//	SET DESTROYED FLAG, ADD TO DESTROYED ENTITIES ARRAYLIST
		//-----------------------------------------------------------------------------//
		if(!destroyed)
		{
			x = -1000;
			y = -1000;
			screen.destroyEntities.add(this);
			destroyed = true;
		}
	}
}