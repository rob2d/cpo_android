package com.rob2d.android.framework;

import android.util.Log;

import com.rob2d.android.chinesepoker.ChinesePoker;

/** Copyright 2011 Robert Concepcion III */
/** Class which handles playing music using the Music class for us */
public class MusicHandler
{
	public int playMode = 0;	
	public final int PLAY_TRACKLIST = 0,
					 PLAY_SINGLETRACK = 1;
	public Music [] trackList;
	public int trackNo = -1;
	public boolean isMusicPlaying = false;
	public boolean randomPlay = false;
	public Game game;

	public MusicHandler(Game g)
	{
		trackNo = -1;	//if uninitialized, the music handler's current track is the sentinel value -1
		game = g;
	}
	
	/** method to add the given game's music tracks*/
	public void setMusicTracks(Music... mus)
	{	
		//set up the array of values that tell whether each track is available or not,
		//by default, they are all available
		trackList = new Music[mus.length];
		//add the songs to playlist, enable all tracks
		for(int i = 0; i < mus.length; i++)
		{
			trackList[i] = mus[i];
		}
		
		//initialize the current track index
		if(trackNo == -1)
			trackNo = 0;
		//after number of tracks is determined, we can apply the game settings
		applyGameSettings();
	}
	
	/** stop the music playlist */
	public void stopMusic()
	{
		/*
		//change flag for music playing or not
		isMusicPlaying = false;
		//if music is available and playing then stop it
		if(trackList[trackNo] != null && trackList[trackNo].isPlaying())
			trackList[trackNo].stop();
			*/
	}
	
	/** start the music tracklist if it hasn't been, with it searching for next available track */
	public void initMusic()
	{
		/*
		if(!trackList[trackNo].isEnabled())
			trackNo = findNextTrack();
				//start the music playing
				if(!isMusicPlaying)
					isMusicPlaying = true;;
					*/
	}
	
	/** set music playlist to start */
	public void playTrackList()
	{
		/*
		//if there is music in the tracks, play it
		if(trackList.length > 0)
		{
			if(!trackList[trackNo].isPlaying())
			{
				//if the current song finished, then cycle through them
				if(trackList[trackNo].hasPlayed())
				{
						trackNo = findNextTrack();
				}
				//set music playing flag and play music
				isMusicPlaying = true;
				trackList[trackNo].play();
			}
		}
		*/
	}
	
	/** method to play a single track */
	public void playSingleTrack(int trackNumber)
	{
		/*
		trackList[trackNo].stop();
		isMusicPlaying = true;
		trackList[trackNumber].play();
		*/
	}
	
	/** free all music tracks from memory */
	public void free()
	{
		for(int i = 0; i < trackList.length; i++)
		{
			if(trackList[i] != null)
			trackList[i].dispose();
		}
		trackList = null;
	}
	
	public void setRandomPlay(boolean random)
	{
		randomPlay = random;
	}
	
	/** update function called from the game's currently running screen */
	public void update()
	{
		/*
		if(((ColorShafted)game).options.musicEnabled)
		{
			if(isMusicPlaying && !trackList[trackNo].isPlaying())
				switch(playMode)
				{
					case PLAY_TRACKLIST:
						playTrackList();				//checks whether track has played and increments accordingly
						break;
					case PLAY_SINGLETRACK:
						trackList[trackNo].hasPlayed(); //reset the hasPlayed value
						isMusicPlaying = false;
						break;
				}
		}
		else
		{
			if(isMusicPlaying)
			{
				trackList[trackNo].pause();
				isMusicPlaying = false;
			}
		}
		*/
	}
	
	public int findNextTrack()
	{
		/*
		int nextTrack = trackNo;
		do
		{
			nextTrack++;
			if(nextTrack >= trackList.length)
				 nextTrack = 0;
			Log.d("CSDebug", Integer.toString(nextTrack));
		}
		while(!trackList[nextTrack].isEnabled());
		return nextTrack;
		*/
		return 0;
	}
	
	public void applyGameSettings()
	{
		/*
		Log.d("CSDebug", "running applyGameSettings() in the music handler");
		for(int i = 0; i < ((ColorShafted)game).options.musicTrackEnabled.length; i++)
		{
			Log.d("CSDebug", i + " = " + ((ColorShafted)game).options.musicTrackEnabled[i]);
			trackList[i].setEnabled(((ColorShafted)game).options.musicTrackEnabled[i]);
		}
		*/
	}
}
