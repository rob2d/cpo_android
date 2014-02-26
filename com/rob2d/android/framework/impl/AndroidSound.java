package com.rob2d.android.framework.impl;

import android.media.SoundPool;

import com.rob2d.android.framework.Sound;

/** Copyright 2011 Robert Concepcion III */
public class AndroidSound implements Sound
{
	int soundId;
	SoundPool soundPool;
	
	public AndroidSound(SoundPool sPool, int sId)
	{
		soundPool = sPool;
		soundId = sId;
	}
	
	@Override
	public void play(float volume)
	{
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	@Override
	public void dispose()
	{
		soundPool.unload(soundId);
	}

}
