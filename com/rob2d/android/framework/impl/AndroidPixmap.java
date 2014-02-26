package com.rob2d.android.framework.impl;

import android.graphics.Bitmap;

import com.rob2d.android.framework.Graphics.PixmapFormat;
import com.rob2d.android.framework.Pixmap;

/** Copyright 2011 Robert Concepcion III */
public class AndroidPixmap implements Pixmap
{
	Bitmap bitmap;
	PixmapFormat format;
	
	public AndroidPixmap(Bitmap b, PixmapFormat f)
	{
		bitmap = b;
		format = f;
	}
	
	@Override
	public PixmapFormat getFormat()
	{
		return format;
	}
	@Override
	public int getWidth()
	{
		return bitmap.getWidth();
	}
	
	@Override
	public int getHeight()
	{
		return bitmap.getHeight();
	}
	
	@Override
	public void dispose()
	{
		bitmap.recycle();
	}
	
	
}
