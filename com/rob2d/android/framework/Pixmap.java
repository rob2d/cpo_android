package com.rob2d.android.framework;

import com.rob2d.android.framework.Graphics.PixmapFormat;

/** Copyright 2011 Robert Concepcion III */
public interface Pixmap {
	public int getWidth();
	public int getHeight();
	public PixmapFormat getFormat();
	public void dispose();
	}