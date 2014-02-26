package com.rob2d.android.chinesepoker.game;

/*  Filename:   Card.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */
/**   Class which represents a card within a deck or hand, more specifically the suite and rank enum order
 *   is based on what they are in Chinese Poker(aka Big Two) */
public class Card 
{
	//**********************************//
	//	      USEFUL ENUMS				//
	//**********************************//
	public enum Suite 	{ DIAMONDS, CLUBS, HEARTS, SPADES };
	public enum Rank 	{ THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE, TWO }
	
	//***********************************//
	//	      MEMBER FIELDS				 //
	//***********************************//
	private Suite suite;
	private Rank  rank;
	/** card value is assigned a value between 0 and 51 and used to directly compare two card values for the highest */
	private int  cardValue;
	
	/** constructor which assigns a card's rank and suite */
	public Card(Rank r, Suite s)
	{
		rank = r;
		suite = s;
		
		//assign a card value between 0 and 55
		cardValue = (r.ordinal() * 4) + s.ordinal();
	}
	
	/** constructor which assigns a card's rank and suite based on its card value */
	public Card(int value)
	{
		setCardValue(value);
	}
	
	/** return the value of a card according to it's rank and suite(for example a THREE of 
	 *  DIAMONDS would return 0 and a FOUR of CLUBS would return a 5 */
	public static int calcCardValue(Rank rank, Suite suite)
	{
		return (rank.ordinal() * 4) + suite.ordinal();
	}
	
	/** for debugging, return a more descriptive version of a card as Text e.g. "TWO OF SPADES" */
	public String toString()
	{
		return rank + " OF " + suite;
	}
	
	//***********************************//
	//	      MUTATOR METHODS			 //
	//***********************************//
	/** assign a card's rank and suite depending on its integer card value(used for easy ordering) */
	public void setCardValue(int value)
	{
		cardValue = value;	//save card value
		
		//find the rank and suite of this card based on its new value
		int rankValue = (int)(cardValue / 4);
		int suiteValue = (int)(cardValue % 4);
		//assign accordingly
		switch(rankValue)
		{
		case 0: rank = Rank.THREE;	 break;
		case 1: rank = Rank.FOUR;	 break;
		case 2: rank = Rank.FIVE;	 break;
		case 3: rank = Rank.SIX;	 break;
		case 4: rank = Rank.SEVEN;	 break;
		case 5: rank = Rank.EIGHT;	 break;
		case 6: rank = Rank.NINE;	 break;
		case 7: rank = Rank.TEN;	 break;
		case 8:	 rank = Rank.JACK;	 break;
		case 9: rank = Rank.QUEEN;	 break;
		case 10:rank = Rank.KING;	 break;
		case 11:rank = Rank.ACE;	 break;
		case 12:rank = Rank.TWO;	 break;
		}
		switch(suiteValue)
		{
		case 0: suite = Suite.DIAMONDS;	break;
		case 1: suite = Suite.CLUBS;	break;
		case 2: suite = Suite.HEARTS;	break;
		case 3: suite = Suite.SPADES;	break;
		}
	}
	
	//***********************************//
	//	      ACCESSOR METHODS			 //
	//***********************************//
	/** return a card's basic integer value for simple direct hierarchical comparisons */
	public int getCardValue() { return cardValue; }
	
	/** return a card's suite */
	public Suite getSuite() { return suite;	}
	/** return the numerical value of a card's suite for easier comparisons */
	public int getSuiteValue(){ return suite.ordinal(); }
	
	/** return a card's Rank */
	public Rank getRank() {	return rank; }
	/** return the numerical value of a card's rank for easier comparisons */
	public int getRankValue(){ return rank.ordinal(); }
}
