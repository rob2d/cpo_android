package com.rob2d.android.framework;

/** Copyright 2011 Robert Concepcion III */
public interface Music
{
	final short PLAY_PREVIEW 	 = 0,
			  	PLAY_TRACKLIST = 1,
			  	PLAY_RANDOM 	 = 2;
	
	public void play(int trackStart, int playMode, boolean looping);
	public void play();
	public void stop();
	public void pause();
	public void resume();
	public void setVolume(float volume);
	public void setTrack(int index);
	public void setLooping(boolean looping);
	/** set the playmode of the music tracks */
	public void setPlayMode(short playMode);
	/** provide an array of booleans which determine whether each music track is enabled */
	public void setTracksEnabled(boolean [] tracksEnabled);
	public boolean isLooping();
	public int findNextTrack(int nextTrack);
	public boolean trackIsPlaying();
	public boolean trackIsStopped();
	public boolean trackHasPlayed();
	public void update();
	public void dispose();
}
