package com.rob2d.android.chinesepoker.menu;

import android.graphics.Rect;

import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.ImageFrame;
import com.rob2d.android.framework.Screen;

public class MenuButton extends ImageEntity
{
	/** The images associated with the button. <br>
	 *  At index 0, we store the button enabled/unselected graphic.<br>
	 *  At 1 we store the enabled and selected graphic.  <br>
	 *  At 2, we store the disabled graphic.
	 *  */
	public ImageFrame[] buttonImg;
	public static final int CLICK_LENGTH = 4;
	public boolean isASelection = true;
	public boolean isClicked = false;
	public boolean buttonEnabled = true;
	public int clickedTimer = 0;
	public int width, height;
	
	public MenuButton(float x, float y, int l, ScreenWithButtons s, ImageFrame... i)
	{
		super(x, y, i[0], l, s);
		//save image frames
		buttonImg = new ImageFrame[i.length];
		for(int j = 0; j < buttonImg.length; j++)
			buttonImg[j] = i[j];
		
		//retrieve dimensions from first button image index
		width = buttonImg[0].getImg().getWidth(); 
		height = buttonImg[0].getImg().getHeight();
		
		//add this button to the associated menu screen     
		s.buttons.add(this);
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(isClicked == true && clickedTimer != CLICK_LENGTH)
			isClicked = false;
		
		if(clickedTimer > 0)		//button has just been clicked
		{	
			if(buttonImg.length > 1)		
				imgFrame = buttonImg[1];
			clickedTimer -= 1;
		}
		else if(buttonEnabled)			//button is selected(yes)
			imgFrame = buttonImg[0];
		
		if(buttonImg.length > 2 && !buttonEnabled)						//button is disabled
			imgFrame = buttonImg[2];
	}
	
	public void clicked()
	{
		if(clickedTimer == 0 && buttonEnabled)
		{
			isClicked = true;
			clickedTimer = CLICK_LENGTH;
		}
	}
	
	public Rect getBounds()
	{
		return new Rect((int)Math.round(x), (int)Math.round(y), (int)Math.round(x +  width), (int)Math.round(y + height));
	}
}