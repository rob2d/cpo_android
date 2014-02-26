package com.rob2d.android.chinesepoker.menu;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.RectEntity;

public class MenuSlider extends MenuButton
{
	public boolean sliderMoving = false;
	public float value = 0;	//value is stored between 0 and 1, to be used by the screen to update whatever value accordingly!
	public int boundsL, boundsR;
	public int scrollWidth;
	public int initialX;	//X coordinate of the screen touched initially while locked onto the slider
	public int valueCount;		//number of values that can lie on the slider
	public float incrementPercent;	//how much the value shifts for each point(i.e. if there were 5, increment would be 0.25)
	RectEntity rangeRect;
	
	
	/**constructor
	 * 
	 * @param x - initial X position of the slider
	 * @param y - initial Y position of the Slider
	 * @param sliderW - width of the slider
	 * @param sliderH - height of the slider
	 * @param bL - left X bounds of the slideable area on the screen
	 * @param bR - right X bounds of the slideable area on the screen
	 * @param l - layer
	 * @param s - screen the slider belongs to
	 * @param i - images to represent the slider button(refer to button for the indexes of each image in the array)
	 */
	public MenuSlider(int x, int y,  			
			int bL, int bR, int pointCount, int l, ScreenWithButtons s,
			ImageFrame... i)
	{
		super(x, y, l, s, i);
		isASelection = false;
		
		setValueCount(pointCount);
		boundsL = bL;
		boundsR = bR;
		scrollWidth = boundsR - boundsL - width;
		
		rangeRect = 
				new RectEntity(new Rect(boundsL, (int)(y + 8), boundsR, (int)(y + height - 8)), Color.argb(100, 0, 0, 0), l-1, screen);
	}
	
	/** method is called when the slider is moving, and this is checked and run within the encompassing screen */
	@Override
	public void update()
	{
		super.update();
		
		rangeRect.visible = visible;
		rangeRect.y		  = y + 8;
		
		if(isClicked)
		{
			sliderMoving = true;
			initialX = screen.game.getInput().getTouchX(0);
			isClicked = false;
		}
		
		if(sliderMoving)
		{
			//while holding down finger on the screen, adjust the position to the slider and keep its coordinates within bounds
			if(screen.touchTimer[0] > 0)
			{
				initialX -= initialX - screen.game.getInput().getTouchX(0);

				if(initialX < boundsL)
					initialX = boundsL;
				else if(initialX + width > boundsR)
					initialX = boundsR - width;
				
				//update the new x position
				x = initialX;
				
				//FIGURE OUT PERCENTAGE OF X SLIDE ON SLIDER
				float percentShift = (float) ((((float)initialX - (float)boundsL)/(float)scrollWidth) * 100.0); //return the percentage shifted on the slider
				
				float oldValue = value;
				//USE THIS TO SET VALUE
				setValue(percentShift/100);
				//LET THE SCREEN KNOW THAT THE VALUE HAS BEEN CHANGED BY USER INTERACTION!
				if(value != oldValue)
					((ScreenWithButtons)screen).sliderValueChanged(this);
			}
			else 
			{
				sliderMoving = false;
				//FIGURE OUT PERCENTAGE OF X SLIDE ON SLIDER
				float percentShift = (float) ((((float)initialX - (float)boundsL)/(float)scrollWidth) * 100.0); //return the percentage shifted on the slider
				//USE THIS TO SET VALUE
				setValue(percentShift/100);
				//LET THE SCREEN KNOW THAT A VALUE HAS BEEN SLID/CHANGED BY USER!
				((ScreenWithButtons)screen).sliderValueChanged(this);
			}
		}
	}
	
	/** set the value of the slider between 0 and 1(representing the percent that the slider has been shifted).
	 * also repositions the slider button relative to this value */
	public void setValue(float v)
	{
		if(v >= 0.0 && v <= 1.0)
		{
			value =  (float) ((Math.round((v * 100) / incrementPercent) * incrementPercent))/100; //use this to calculate the increment and return a value between 0 and 1
			//keep value in bounds in case rounding goes slightly off
			if(value > 1)
				value = 1;
			else if(value < 0)
				value = 0;
			//shift the x position of the slider on the screen
			x = boundsL + (value * scrollWidth);
		}
	}
	
	/** set the number of points on the slider */
	public void setValueCount(int pointCount)
	{
		valueCount = pointCount;
		incrementPercent = (1/((float)valueCount - 1)) * 100;
		//reset the value to itself in order to reposition the slider button
		setPointValue(0);
	}
	
	/** set the value of the slider in terms of the point number */
	public void setPointValue(int point)
	{
		setValue((float)(incrementPercent/100) * point);
	}
	
	/** returns the point number of the slider */
	public int getPointValue()
	{
		return (int)(value/(float)(incrementPercent / 100));
	}
}