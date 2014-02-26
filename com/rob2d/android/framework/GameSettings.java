package com.rob2d.android.framework;

/** Copyright 2011 Robert Concepcion III */
/** interface for saving/loading options */
public interface GameSettings
{
	public Object[] getOptions();
	public void setOption(int optionIndex, Object setting);
	public void saveSettings();
	public void loadSettings();
}