package com.rob2d.android.framework;

import android.view.SurfaceView;

import com.rob2d.android.textui.Prompter;

/** Copyright 2011 Robert Concepcion III */
public interface Game
{
	public Input getInput();
	public FileIO getFileIO();
	public Graphics getGraphics();
	public Audio getAudio();
	public void setScreen(Screen screen);
	public Screen getCurrentScreen();
	public Screen getStartScreen();
	public SurfaceView getRenderView();
	public Music getMusicHandler();
	public boolean isMainRunning();
	public boolean isPaused();
	public void launchWebsite(String url);
	public void startLoadingDialog(String msg, String title, boolean cancellable);
	public void stopLoadingDialog();
	public Prompter getPrompter();
	public GameSettings getSettings();
}
