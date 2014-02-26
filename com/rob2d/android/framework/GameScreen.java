package com.rob2d.android.framework;

/** Copyright 2011 Robert Concepcion III */
public interface GameScreen
{
	/** run when the user clicks to either pause or unpause */
	public void pressedPause();
	/** run when the timerLogic() should happen while the game is paused */
	public void pauseUpdate();
}
