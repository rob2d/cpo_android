package com.rob2d.android.chinesepoker.gui.gamescreen;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Rect;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.game.Card;
import com.rob2d.android.chinesepoker.gui.GameScr;
import com.rob2d.android.framework.ScreenEntity;
import com.rob2d.android.framework.TextEntity;

public class CardPileDisplay extends ScreenEntity
{
	public ArrayList<CardEntity> cards = new ArrayList<CardEntity>();
	public TextEntity lastPlayBy;
	public GameScr gameScreen;
	int alpha;						//alpha of objects overall(from 0-255), used to change entities' alpha as they scroll in

	public CardPileDisplay(GameScr gS)
	{
		super(1, gS);
		gameScreen = gS;
		lastPlayBy = new TextEntity(100, 88, 
							new StringBuffer(""), Color.argb(255, 255, 255, 255), 
							Assets.font1, 32, 5, gameScreen);
	}
	
	@Override
	public void update()
	{
		super.update();
		
		if(gameScreen.playSequence)
		{
			if(y < 108)
				y+= 12;
			alpha = (int)(((y+140)/250) * 250);
		}
		else if(!gameScreen.playSequence)
		{
			if(y > -100)
				y-= 12;
			alpha = 0;
		}
		
		positionObjects();
		setObjectsAlpha();
	}
	
	public void updateCardPile()
	{	
		lastPlayBy.string.replace(0, lastPlayBy.string.length(), "");
		//update the associated gamescreen's card pile display
		if(gameScreen.gamePlayed.cardPile != null && gameScreen.gamePlayed.cardPile.getLastPlay() != null)
		{
			if((gameScreen.gamePlayed.playerToBeat != gameScreen.gamePlayed.playerTurn && gameScreen.onlineSession == null) || 
			   (gameScreen.onlineSession != null && gameScreen.gamePlayed.playerToBeat != gameScreen.onlineSession.playerSlot)	)
			{
				gameScreen.cardsLastPlayed.setCards(
						gameScreen.gamePlayed.cardPile.getLastPlay().cardSet);
				lastPlayBy.string.append(gameScreen.gamePlayed.players[gameScreen.gamePlayed.playerToBeat].name + " has played");
				lastPlayBy.x = 400 - lastPlayBy.getWidth() / 2;
				lastPlayBy.getBounds();
			}
			else
				setCards((Card[])null);
		}
	}
	
	public void setCards(Card... newCards)
	{
		//clear out old cardpile
		if(cards != null) 
		{
			for(CardEntity e : cards)	
				e.destroy();
					cards.clear();
		}
		
		//set the new cards, and then position them
		if(newCards != null)
		for(int i = 0; i < newCards.length; i++)
			cards.add(0, new CardEntity(newCards[newCards.length - i - 1].getCardValue(), gameScreen));
		positionObjects();
	}
	
	public void positionObjects()
	{
		int width = (cards.size() > 0 ? PlayerHand.CARD_A_W + 
					 (cards.size() > 1 ? (PlayerHand.CARD_B_W * (cards.size() - 1)) : 0) 
											 : 0);
		
		x = 400 - width/2;
		
		lastPlayBy.y = y - 24;
		
		for(CardEntity c : cards)
		{
			c.x = x + cards.indexOf(c) * PlayerHand.CARD_B_W;
			c.y = y + 8;
		}
	}
	
	public void setObjectsAlpha()
	{
		for(CardEntity c : cards)
			c.alpha = (int)(alpha * (CardEntity.CARD_ALPHA/255.0));
		lastPlayBy.color = Color.argb(alpha, 255, 255, 255);
	}
	
	public void setVisible(boolean visible)
	{
		for(CardEntity card : cards)
			card.visible = visible;
		lastPlayBy.visible = visible;
	}

	@Override
	public Rect getBounds()
	{
		return null;
	}
}
