package com.rob2d.android.framework;

import java.util.ArrayList;

/** Copyright 2011 Robert Concepcion III */
public class AnimEntity extends ImageEntity
{
	/** our animation array */
	public ArrayList<Anim> anims = new ArrayList<Anim>();		
	/** quick reference to one of the animations in our anim array */
	protected int animIndex = 0;						//quick reference to one of the animations in our animations array
	/** current frame of animation */
	protected int animFrame = 0;			//current frame of animation			
	/** last animation tick */
	protected long animLastTick = System.currentTimeMillis();
	/** whether anim is paused */
	protected boolean isAnimPaused = false;		
	/** whether anim has finished(for related logic events) */
	protected boolean isAnimFinished = false;
	
	public AnimEntity(int layer, Screen screen) 
	{
		super(layer, screen);
	}
	
	public AnimEntity(float x, float y, int layer, Screen screen, Anim... animsAdded)
	{
		super(layer, screen);
		this.x = x;
		this.y = y;
		
		for(Anim a : animsAdded)
			anims.add(a);
		update();
	}
	
	public void update()
	{
		super.update();
		animate(System.currentTimeMillis());
	}
	
	public void animate(long time)
	{
		isAnimFinished = false;
		//update anim loop when necessary events occur
		while(((time - animLastTick)) >  (100 - anims.get(animIndex).frameSpeed) * 10)
		{
			animLastTick = (long)(time - 									//update last animTick time
						   (time % ((100 - anims.get(animIndex).frameSpeed)) * 10));	
			animFrame += 1;
			if(animFrame >= anims.get(animIndex).frameCount)
			{
				animFrame = anims.get(animIndex).frameCycleTo;
				isAnimFinished = true;
			}
			
			imgFrame = anims.get(animIndex).frames[animFrame];
		}
		//if the image is not yet shown, we update it!
		if(imgFrame == null)
			imgFrame = anims.get(animIndex).frames[animFrame];
	}
}
