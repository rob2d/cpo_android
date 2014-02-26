package com.rob2d.android.chinesepoker.gui.gamescreen;

import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.game.Card;
import com.rob2d.android.chinesepoker.game.Card.Rank;
import com.rob2d.android.chinesepoker.game.Card.Suite;
import com.rob2d.android.framework.ImageEntity;
import com.rob2d.android.framework.Screen;

public class CardEntity extends ImageEntity
{
	public final static int LAYER = 2;
	public final static int CARD_ALPHA = 200;
	/** the position of the card entity on the screen */	
	public int cardSlot;
	public boolean selected;

	public int cardValue;
	
	public CardEntity(Rank rank, Suite suite, Screen scr)
	{
		this(Card.calcCardValue(rank, suite), scr);
	}
	
	public CardEntity(int cardValue, Screen scr)
	{
		this(scr);
		setCard(cardValue);
	}
	
	public CardEntity(Screen scr)
	{
		super(LAYER, scr);
		this.semiTrans = true;
		this.alpha = CARD_ALPHA;
	}

	public void setCard(int value)
	{
		if(value != -1)
		{
			cardValue = value;
			imgFrame  = Assets.cardsIF[cardValue];
		}
	}
	
	@Override
	public Rect getBounds()
	{
		if(cardSlot == 0)
			return super.getBounds();
		else
		{
			return new Rect(super.getBounds().left + 72, super.getBounds().top, 
							super.getBounds().right, super.getBounds().bottom);
		}
	}
}
