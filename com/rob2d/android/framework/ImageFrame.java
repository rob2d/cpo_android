package com.rob2d.android.framework;

import java.io.Serializable;

import android.graphics.Point;
import android.graphics.Rect;

/** Copyright 2011 Robert Concepcion III */
public class ImageFrame implements Serializable
{
	protected Pixmap img;	//image for particular frame
	public Point actionPoint;	//action point
	
	public ImageFrame(Pixmap i, Object parent)
	{
		actionPoint = new Point(0, 0);
		img = i;
	}
	
	//constructor for image frame, image loaded, the collision box, and optional action points(x, y),
	//the parent ref is needed to extract the resource location!(typically in a sprite)
	public ImageFrame(Pixmap i, int apX, int apY, Object parent)
	{
		this(i, parent);
		actionPoint.x = apX;
		actionPoint.y = apY;
	}
	
	//method to return an image from the frame
	public Pixmap getImg()
	{
		if(img != null) return img;	//prevent nPE exception
		else	return null;
	}
	
	public Rect getImgBounds()
	{
		if(img == null)
			return null;
		else
			return new Rect(-actionPoint.x, -actionPoint.y, 
							img.getWidth() - actionPoint.x, img.getHeight() - actionPoint.y);
	}
}
