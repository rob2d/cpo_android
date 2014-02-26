package com.rob2d.android.chinesepoker.game;
/*  Filename:   Deck.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/** Class which contains an ArrayList of cards and can perform different sorting functions on them.
 *  Cards are removed from the top of the deck(index 0++)*/
public class Deck 
{
	Random rand = new Random();
	public ArrayList<Card> cards = new ArrayList<Card>();
	
	public Deck()
	{
		for(int i = 0; i < 52; i++)
			cards.add(new Card(i));
		shuffle();
	}
	
	/** Shuffles cards to random within a deck */
	public void shuffle()
	{
		for(int i = 0; i < cards.size(); i++)
		{
			int shuffledTo = (int)(rand.nextDouble() * 52);
			swap(i, shuffledTo);
		}
	}
	
	/** Sorts cards within the deck from lowest to highest */
	public void sort()
	{
									 //starting from the last card, compare a card with a previous card and subsequently
		for(int i = cards.size(); i > 0; i--) //swap it until it has been checked to be the first(if lowest)
		{
			int swapCard = i;		 //reset value of the card which would hypothetically be swapped first
			while(swapCard > 0 && cards.get(swapCard).getCardValue() > cards.get(swapCard).getCardValue())
			{
				swap(swapCard, swapCard - 1);
				swapCard--;
			}
		}
	}
	
	/** swap two cards within the cards array */
	public void swap(int cardIndex1, int cardIndex2)
	{
		Card backupIndex1 = cards.get(cardIndex1);
		cards.set(cardIndex1, cards.get(cardIndex2));
		cards.set(cardIndex2, backupIndex1);
	}
	
	/** remove a certain number of cards to the deck to give to another object */
	public ArrayList<Card> deal(int numberOfCards)
	{
		if(cards.size() >= numberOfCards)
		{
			//set up an arraylist of cards removed
			ArrayList<Card> cardsRemoved = new ArrayList<Card>();
			for(int i = 0; i < numberOfCards; i++)
				cardsRemoved.add(cards.remove(0));
			//return an array of the cards removed
			return cardsRemoved;
		}
		else
		{
			System.err.println("Could not deal " + numberOfCards + " cards; Not enough in deck!");
			return null;
		}
	}
	
	/** Deal a given nuber of cards to a player from the deck. If there are enough cards, 
	 *  return true and give player these cards. Otherwise, return false and do nothing. 
	 *  @param player - player to give cards to
	 *  @param numberOfCards - number of cards to deal from the deck */
	public boolean dealToPlayer(Player player, int numberOfCards)
	{
		ArrayList<Card> cardsDealt = deal(numberOfCards);	//get cards if possible
		if(cardsDealt != null)	//if so, deal the cards to the player
		{
			player.dealt(cardsDealt);
			return true;
		}
		else return false;		//otherwise return false to let game know nothing happened
	}
	
	public void retrieveCard(Card card)
	{
		cards.add(card);
	}
	
	public ArrayList<Card> getCards()
	{	return cards;	}
	
	/** Give a list of the cards in the deck as they are in order */
	public String toString()
	{
		String str = new String();
		for( int i = 0; i < cards.size(); i++ )
			str += (i+1) + ". " + (cards.get(i).toString() + "\n");
		return str;
	}
}
