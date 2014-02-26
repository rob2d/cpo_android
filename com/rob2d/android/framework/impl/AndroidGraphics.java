package com.rob2d.android.framework.impl;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;

import com.rob2d.android.framework.Graphics;
import com.rob2d.android.framework.Pixmap;

/** Copyright 2011 Robert Concepcion III */
public class AndroidGraphics implements Graphics
{
	AssetManager assets;		//loads bitmap instances
	Bitmap frameBuffer;			//artificial buffer
	Canvas canvas;				//used to draw buffer
	Paint paint;				//used for drawing
	Rect srcRect = new Rect();
	Rect dstRect = new Rect();
	
	public AndroidGraphics(AssetManager assets, Bitmap frameBuffer)
	{
		this.assets = assets;
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);		//use the canvas to draw to the frameBuffer
		this.paint = new Paint();
	}
	
	@Override
	public Pixmap newPixmap(String fileName, PixmapFormat format)
	{
		Config config = null;
		if(format == PixmapFormat.RGB565)
			config = Config.RGB_565;
		else if(format == PixmapFormat.ARGB4444)
			config = Config.ARGB_4444;
		else
			config = Config.ARGB_8888;
		
		Options options = new Options();
		options.inPreferredConfig = config;
		
		InputStream in = null;
		Bitmap bitmap = null;
		try
		{
			in = assets.open(fileName);	//load a bitmap asset via the in InputStream
			bitmap = BitmapFactory.decodeStream(in);	//set the Bitmap to the opened stream loading the asset
			if(bitmap == null)
				throw new RuntimeException("Couldn't load bitmap from asset " + fileName + "'");
		}
		catch(IOException e)
		{
			throw new RuntimeException("Couldn't load bitmap from asset " + fileName + "'");
		}
		finally
		{
			if(in != null)
			{
				try{ in.close(); }
				catch(IOException e) {}
			}
		}
		
		if(bitmap.getConfig() == Config.RGB_565)
			format = PixmapFormat.RGB565;
		else if(bitmap.getConfig() == Config.ARGB_4444)
			format = PixmapFormat.ARGB4444;
		else
			format = PixmapFormat.ARGB8888;
		
		//return a new AndroidPixmap image with the bitmap loaded from the assetand the format
		return new AndroidPixmap(bitmap, format);	
	}
	
	@Override
	public void clear(int color)
	{
		canvas.drawRGB(color & 0xff0000, color & 0x00ff00, color & 0x0000ff);
	}
	
	@Override
	public void drawPixel(int x, int y, int color)
	{
		paint.setColor(color);
		canvas.drawPoint((float)x, (float)y, paint);
	}
	@Override
	public void drawLine(int x, int y, int x2, int y2, int color)
	{
		paint.setColor(color);
		canvas.drawLine(x, y, x2, y2, paint);
		paint.setColor(Color.WHITE);
	}
	@Override
	public void drawRect(int x, int y, int width, int height, int color)
	{
		paint.setColor(color);
		paint.setStyle(Style.FILL);
		canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
		paint.setColor(Color.WHITE);
	}
	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) 
	{
		Paint paint = new Paint();
		srcRect.left = srcX;
		srcRect.top = srcY;
		srcRect.right = srcX + srcWidth - 1;
		srcRect.bottom = srcY + srcHeight - 1;
		dstRect.left = x;
		dstRect.top = y;
		dstRect.right = x + srcWidth - 1;
		dstRect.bottom = y + srcHeight - 1;
		canvas.drawBitmap(((AndroidPixmap) pixmap).bitmap, srcRect, dstRect, null);
	}
	
	@Override
	public void drawPixmap(Pixmap pixmap, int x, int y) 
	{
		canvas.drawBitmap(((AndroidPixmap)pixmap).bitmap, x, y, paint);
	}
	
	@Override
	public void drawPixmapRotated(Pixmap pixmap, int x, int y, Point rotatePoint, int angle)
	{
		canvas.save();
		canvas.rotate(angle, x + rotatePoint.x, y + rotatePoint.x);
		drawPixmap(pixmap, (int)x, (int)y);
		canvas.restore();
	}
	
	@Override
	public void drawPixmapAlpha(Pixmap pixmap, int x, int y, int alpha)
	{
		paint.setAlpha(alpha);
		drawPixmap(pixmap, x, y);
		paint.setAlpha(255);
	}

	@Override
	public void drawPixmapRotatedAlpha(Pixmap pixmap, int x, int y,
			Point rotatePoint, int angle, int alpha)
	{
		paint.setAlpha(alpha);
		drawPixmapRotated(pixmap, x, y, rotatePoint, angle);
		paint.setAlpha(255);
	}
	
	@Override
	public void drawPixmapAlpha(Pixmap pixmap, int x, int y, int srcX,
			int srcY, int srcWidth, int srcHeight, int alpha)
	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(alpha);
		drawPixmap(pixmap, x, y, srcX, srcY, srcWidth, srcHeight);
		paint.setAlpha(255);
	}
	
	@Override
	public void drawText(StringBuffer txt, int x, int y, Paint p)
	{
		paint.setColor(p.getColor());
		paint.setTypeface(p.getTypeface());
		paint.setTextSize(p.getTextSize());
		canvas.drawText(txt.toString(), x, y, paint);
		paint.setColor(Color.WHITE);
	}
	
	@Override
	public int getWidth()
	{
		return frameBuffer.getWidth();
	}
	
	@Override
	public int getHeight()
	{
		return frameBuffer.getHeight();
	}
}
