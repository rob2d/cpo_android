package com.rob2d.android.chinesepoker;

import com.rob2d.android.chinesepoker.game.GameStyle;
import com.rob2d.android.framework.GameSettings;
import com.rob2d.android.framework.impl.AndroidGame;

public class ChinesePokerSettings implements GameSettings
{
	public static final int 					DEAL_13_CARDS  		  = 0,
								  				WINNER_STARTS 		  = 1,
								  				TRIPLES_VALID     	  = 2,
								  				START_FLUSH_A2 		  = 3,
								  				NUMBER_OF_PLAYERS  	  = 4;
								  
	public AndroidGame game;
	public Object[] settings = new Object[5];
	
	public ChinesePokerSettings(ChinesePoker g)
	{
		game = g;
	}
	
	@Override
	public Object[] getOptions()
	{
		return settings;
	}

	@Override
	public void setOption(int optionIndex, Object setting)
	{
		settings[optionIndex] = setting;
	}

	@Override
	public void saveSettings()
	{
		for(int optionIndex = 0; optionIndex < 5; optionIndex++)
		{
					game.prefEditor.putBoolean("DEAL_13_CARDS", (Boolean)settings[DEAL_13_CARDS]);
					game.prefEditor.putBoolean("WINNER_STARTS", (Boolean)settings[WINNER_STARTS]);
					game.prefEditor.putBoolean("TRIPLES_VALID", (Boolean)settings[TRIPLES_VALID]);
					game.prefEditor.putBoolean("START_FLUSH_A2", (Boolean)settings[START_FLUSH_A2]);
					game.prefEditor.putInt("NUMBER_OF_PLAYERS", (Integer)settings[NUMBER_OF_PLAYERS] );
					game.prefEditor.commit();
		}
		
	}

	@Override
	public void loadSettings()
	{		
			settings[DEAL_13_CARDS] = game.gamePrefs.getBoolean("DEAL_13_CARDS", true);
			settings[WINNER_STARTS] = game.gamePrefs.getBoolean("WINNER_STARTS", true);
			settings[TRIPLES_VALID] = game.gamePrefs.getBoolean("TRIPLES_VALID", false);
			settings[START_FLUSH_A2] = game.gamePrefs.getBoolean("START_FLUSH_A2", false);
			settings[NUMBER_OF_PLAYERS] = game.gamePrefs.getInt("NUMBER_OF_PLAYERS", 2);
	}
	
	public GameStyle getGameStyle()
	{
		GameStyle gameStyle = new GameStyle();
		gameStyle.dealThirteenCards = (Boolean)settings[DEAL_13_CARDS];
		gameStyle.winnerStarts = (Boolean)settings[WINNER_STARTS];
		gameStyle.triplesValid  = (Boolean)settings[TRIPLES_VALID];
		gameStyle.startFlushesAtAce = (Boolean)settings[START_FLUSH_A2];
		gameStyle.playersInGame = (Integer)settings[NUMBER_OF_PLAYERS];
		return gameStyle;
	}

}
