package com.rob2d.android.framework;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/** Copyright 2011 Robert Concepcion III */
public class TextEntity extends ScreenEntity
{
	public StringBuffer string;
	public int color;
	public int size;
	public Typeface font;
	public int width;
	
	public TextEntity(float x, float y, StringBuffer s, int c, Typeface f, int si, int layer, Screen scr)
	{
		super(layer, scr);
		this.x = x;
		this.y = y;
		string = s;
		color = c;
		font = f;
		size = si;
	}

	public int getWidth()
	{
		Paint textPaint = new Paint();
		textPaint.setTypeface(font);
		textPaint.setTextSize(size);
		if(string != null)
			return (int)textPaint.measureText(string.toString());
		else
			return 0;
	}

	@Override
	public Rect getBounds()
	{
		return null;
	}
}
