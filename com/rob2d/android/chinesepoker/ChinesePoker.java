package com.rob2d.android.chinesepoker;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.rob2d.android.framework.GameSettings;
import com.rob2d.android.framework.Screen;
import com.rob2d.android.framework.impl.AndroidGame;

public class ChinesePoker extends AndroidGame
{
	public ChinesePoker()
	{
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//load preferences to work with
		gamePrefs = getSharedPreferences(Assets.PREFERENCE_FILENAME, MODE_APPEND);
		prefEditor = gamePrefs.edit();
		//create options
		options = new ChinesePokerSettings(this);
	}
	
	public Screen getStartScreen()
	{
		return new CPLoadingScreen(this);
	}

	@Override
	public GameSettings getSettings()
	{
		return options;
	}
}
