package com.rob2d.android.chinesepoker.game;
/*  Filename:   CardPile.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */

import java.util.ArrayList;

/** represents the card pile in the middle of the deck. This class is responsible for keeping track
 *  of the last played cards in a card game*/
public class CardPile 
{
	/** collection of all of the hands that have been thrown down into the card pile */
	ArrayList<CardPattern> playsThrown;
	
	/** to keep track of the parent card game */
	CardGame game;
	
	public CardPile(CardGame g)
	{
		game = g;
		playsThrown = new ArrayList<CardPattern>();
	}
	
	/** retrieve the highest play(CardPattern) of cards on the card pile */
	public CardPattern getLastPlay()
	{
		if(playsThrown.size() > 0)								//if at least one set of cards was played, return it
			return playsThrown.get(playsThrown.size() - 1);
		else return null;										//otherwise we can only return null
	}
	
	/** play cards against the card pile. If they win against the last set, add them to the card pile.
	 * return @ true if the cards we have tested are higher than the card pile. Otherwise if incomparable or
	 *  less than the highest play on the pile, we return false.*/
	public boolean playCards(CardPattern cardsPlayed)
	{
		boolean cardsCanBePlayed = cardPlayValid(cardsPlayed);
		
		//add cards to play if the cards can be played
		if(cardsCanBePlayed)
			playsThrown.add(cardsPlayed);
		
		return cardsCanBePlayed;
	}
	
	/** this function simply retrieves whether a certain card pattern CAN beat the most recent on the card pile
	 * (as opposed to the cardPlayed function which actually makes the play
	 * @param cardsPlayed
	 * @return
	 */
	public boolean cardPlayValid(CardPattern cardsPlayed)
	{
		if(playsThrown.size() == 0 || getLastPlay() == null || 
				(playsThrown.size() > 0 && cardsPlayed.compareTo(getLastPlay()) > 0))	//are they bigger?
		{
			return true;
		}
		else return false;					//cards given were not bigger than the highest, return false.		
	}
	
	public void playAnyCards(CardPattern cardsPlayed)
	{
		if(cardsPlayed != null)
			playsThrown.add(cardsPlayed);
	}
}
