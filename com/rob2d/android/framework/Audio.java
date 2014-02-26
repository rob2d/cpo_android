package com.rob2d.android.framework;

import android.content.res.AssetFileDescriptor;

/** Copyright 2011 Robert Concepcion III */
public interface Audio
{
	public AssetFileDescriptor newMusic(String fileName);
	public Sound newSound(String fileName);
}
