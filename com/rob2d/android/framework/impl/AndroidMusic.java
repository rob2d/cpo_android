package com.rob2d.android.framework.impl;

import java.io.IOException;
import java.util.ArrayList;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.rob2d.android.chinesepoker.ChinesePoker;
import com.rob2d.android.framework.Game;
import com.rob2d.android.framework.Music;

/** Copyright 2011 Robert Concepcion III */
public class AndroidMusic implements Music, OnCompletionListener
{
	Game game;
	MediaPlayer mediaPlayer;
	public AssetFileDescriptor [] trackList;
	public boolean [] tracksEnabled;
	public short playMode;
	public int trackNo = 0;
	boolean trackIsPrepared = false;
	boolean trackHasPlayed = false;
	/** whether track can be played by the music handler or not */
	boolean enabled = true;
	boolean musicIsPlaying = false;	//boolean to toggle the music to play or not
	
	/** Constructor. accepts an asset descriptor */
	public AndroidMusic(Game g, AssetFileDescriptor... assetDescriptors)
	{
		game = g;							//track parent
		mediaPlayer = new MediaPlayer();	//instantiate music player
	}
	/** Android specific code to set the music tracks to asset descriptors that have been loaded from the Audio object */
	public void setMusicTracks(AssetFileDescriptor... assets)
	{
		trackList = assets;
	}
	
	/** play a track sequence with parameters for initialization */
	public void play(int initialTrack, int playMode, boolean looping)
	{
		setTrack(initialTrack);	//load the initial track to first available
		mediaPlayer.setLooping(looping);	//set looping on or off
		this.playMode = (short)playMode;	//save playmode
		play();
	}
	
	/** start playing a track */
	public void play()
	{
		if(true)	//-CPWIP- change to boolean test IS MUSIC PLAYING
		{
			musicIsPlaying = true;
			setTrack(trackNo);
			try
			{
				synchronized(this)		//synchronized to make sure that the mediaPlayer is prepared and nothing changes before it is started
				{
					if(!trackIsPrepared)				//prepare mediaPlayer if necessary
						mediaPlayer.prepare();
					mediaPlayer.start();		//playback stream
				}
			}
			catch(IllegalStateException e)	//catch error incase
			{
				e.printStackTrace();
			}
			catch(IOException e)			//catch file error incase
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void stop()
	{
		mediaPlayer.stop();
		synchronized(this)		//make sure prepared variable is unaltered elsewhere in the meantime...
		{
			musicIsPlaying = false;
			trackIsPrepared = false;
		}
	}
	
	public void setTrack(int index)
	{
		//if not in preview mode, make sure the currently set track number is OK by moving it back and then finding the next!
		if(playMode != PLAY_PREVIEW)
			trackNo = findNextTrack(index - 1);
		else
			trackNo = index;
		try
		{
			mediaPlayer.reset();	//set mediaplayer to idle state
			trackIsPrepared = false;
			//set up the media player to know about the stream data we want to play
			mediaPlayer.setDataSource(trackList[index].getFileDescriptor(), 
									  trackList[index].getStartOffset(), 
									  trackList[index].getLength());
			mediaPlayer.prepare();	//prepare the media player
			trackIsPrepared = true;		//register the android music class as prepared
			mediaPlayer.setOnCompletionListener(this);	//set the listener to this class so that we can track when music is finished playing
		}
		catch(Exception e)
		{
			throw new RuntimeException("couldn't load music");
		}
	}
	
	@Override
	public void dispose()
	{
		if(mediaPlayer.isPlaying())
			mediaPlayer.stop();
		mediaPlayer.release();	//free memory
	}
	
	@Override
	public boolean isLooping()
	{
		return mediaPlayer.isLooping();
	}
	
	@Override
	public boolean trackIsPlaying()
	{
		return mediaPlayer.isPlaying();
	}
	
	@Override
	public boolean trackIsStopped()
	{
		return !trackIsPrepared;	//makes sure the media player isn't just paused but *stopped*
	}
	
	@Override
	public void setLooping(boolean looping)
	{
		mediaPlayer.setLooping(looping);
	}
	
	@Override
	public void setVolume(float volume)
	{
		mediaPlayer.setVolume(volume, volume);
	}
	
	@Override
	public void onCompletion(MediaPlayer mP)
	{
		synchronized(this)
		{
			musicIsPlaying = false;
			trackHasPlayed = true;
			trackIsPrepared = false;	//reset prepared flag when file is completed, keeps track of this so other classes don't throw exceptions and try to run it
		}
	}

	@Override
	public void pause()
	{
		mediaPlayer.pause();
	}
	/**return whether a track has finished. warning - this resets the state of the finished flag so be sure to
	 * utilize this data where the method is called */
	public boolean trackHasPlayed()
	{
		if(trackHasPlayed)
		{
			trackHasPlayed = false;
			return true;
		}
		else
		{
			return false;
		}
	}


	@Override
	public void resume()
	{
		if(musicIsPlaying)
		{
			mediaPlayer.start();	//continues the current track after MediaPlayer.pause()
		}
	}


	public int findNextTrack(int nextTrack)
	{
		switch(playMode)
		{
			case PLAY_RANDOM:	  //choose random track, then cycle to next in the PLAY_TRACKLIST case
				nextTrack = (int)(Math.random() * (double)(trackList.length));	
			case PLAY_TRACKLIST: //cycle through tracks until the next one is not disabled
			{
				do
				{
					nextTrack++;
					if(nextTrack >= trackList.length)
						nextTrack = 0;
					Log.d("CSDebug", Integer.toString(nextTrack));
				}
				while(!tracksEnabled[nextTrack]);
				break;
			}
			case PLAY_PREVIEW:
			{
				do
				{	
					
					nextTrack++;	//go to the next track
					if(nextTrack >= trackList.length)	//cycle to the beginning
						nextTrack = 0;
					Log.d("CSDebug", Integer.toString(nextTrack));
				}
				while(!tracksEnabled[nextTrack]);
				break;
			}
		}
		return nextTrack;
	}
	
	/** update function called from the game's currently running screen */
	public void update()
	{
		if(game instanceof ChinesePoker)
		//start playing music when necessary
		if(false)	//-CPWIP- BOOLEAN TO TEST IF MUSIC IS PLAYING!
		{
			if(mediaPlayer != null)
			if(musicIsPlaying && !mediaPlayer.isPlaying())
				switch(playMode)
				{
					case PLAY_TRACKLIST:
						setTrack(findNextTrack(trackNo));
						play();
						break;
					case PLAY_PREVIEW:
						if(!isLooping())
						play();
						break;
				}
		}
	}

	/** set the playmode */
	@Override
	public void setPlayMode(short m)
	{
		playMode = m;
	}

	@Override
	public void setTracksEnabled(boolean[] tE)
	{
		tracksEnabled = tE;
	}
}
