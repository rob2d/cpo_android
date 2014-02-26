package com.rob2d.android.framework.impl;

import java.util.List;

import android.view.View.OnTouchListener;

import com.rob2d.android.framework.Input.TouchEvent;

/** Copyright 2011 Robert Concepcion III */
public interface TouchHandler extends OnTouchListener
{
	public boolean isTouchDown(int pointerIndex);
	
	public int getTouchX(int pointer);
	
	public int getTouchY(int pointer);
	
	public List<TouchEvent> getTouchEvents();
}
