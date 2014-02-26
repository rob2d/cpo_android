package com.rob2d.android.chinesepoker.menu;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.RectEntity;
import com.rob2d.android.framework.Screen;
import com.rob2d.android.framework.TextEntity;

/** represents a screen which contains buttons and sliders */
public abstract class ScreenWithButtons extends Screen
{
	/** buttons that are available in the current menu screen */
	public ArrayList<MenuButton> buttons = new ArrayList<MenuButton>();
	/** sliders that are available in the current menu screen */
	public ArrayList<MenuSlider> sliders = new ArrayList<MenuSlider>();
	public boolean promptVisible = true;
	boolean screenTouched = false;
	
	public final int SCREEN_BORDER_RIGHT = 480;
	
	public ScreenWithButtons(Game game)
	{
		super(game);
	}

	/** if there are sliders in the menu, this value can be overridden to detect them from within a specific menu! */
	public void sliderValueChanged(MenuSlider slider)
	{}
	
	@Override
	public void timedLogic()
	{
		//DETECT SCREEN TOUCHES
		screenTouched = false; //reset state at start of loop
		if(touchTimer[0] == 1)
		{
			screenTouched = true;
		}
		
		if(screenTouched)
		{
			int touchedX = game.getInput().getTouchX(0);
			int touchedY = game.getInput().getTouchY(0);

			for(MenuButton b: buttons)
				if(touchedX <= ((b.x + b.width + 8) - b.buttonImg[0].actionPoint.x) &&
				   touchedX >= ((b.x - 8) - b.buttonImg[0].actionPoint.x) &&
			       touchedY <= (b.y + b.height + 8) - b.buttonImg[0].actionPoint.y && 
				   touchedY >= (b.y - 8) - b.buttonImg[0].actionPoint.y)
				{
					b.clicked();
				}
		}
	}
	
	public void fadeOutLogic()
	{
		super.fadeOutLogic();
	}
}
