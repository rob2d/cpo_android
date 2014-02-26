package com.rob2d.android.framework;

import android.graphics.Paint;
import android.graphics.Point;

/** Copyright 2011 Robert Concepcion III */
public interface Graphics {
	public static enum PixmapFormat {
	ARGB8888, ARGB4444, RGB565
	}
	public Pixmap newPixmap(String fileName, PixmapFormat format);
	public void clear(int color);
	public void drawPixel(int x, int y, int color);
	public void drawLine(int x, int y, int x2, int y2, int color);
	public void drawRect(int x, int y, int width, int height, int color);
	public void drawText(StringBuffer text, int x, int y, Paint p);
	
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY,
	int srcWidth, int srcHeight);
	public void drawPixmapAlpha(Pixmap pixmap, int x, int y, int srcX, int srcY,
			int srcWidth, int srcHeight, int alpha);
	
	public void drawPixmap(Pixmap pixmap, int x, int y);
	public void drawPixmapAlpha(Pixmap pixmap, int x, int y, int alpha);
	
	public void drawPixmapRotated(Pixmap pixmap, int x, int y, Point rotatePoint, int angle);
	public void drawPixmapRotatedAlpha(Pixmap pixmap, int x, int y, Point rotatePoint, int angle, int alpha);
	
	public int getWidth();
	public int getHeight();
	}
