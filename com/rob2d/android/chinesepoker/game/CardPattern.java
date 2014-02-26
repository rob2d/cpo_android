package com.rob2d.android.chinesepoker.game;
/*  Filename:   CardPattern.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */

import com.rob2d.android.chinesepoker.game.Card.Rank;
import com.rob2d.android.chinesepoker.game.Card.Suite;

/** Represents a valid hand of cards in a game of chinese poker(e.g., Singles, Double 3, etc) 
 *  that can be compared against another one */
public abstract class CardPattern implements Comparable<CardPattern>
{
	/** cards used in the combo */
	public Card[] cardSet;
	/** type of pattern the cards are in(e.g. SINGLE, DOUBLE, etc */
	public ComboPattern patternType;
	/** Rank that the pattern is in. null if not applicable to the pattern */
	public Rank highRank;
	/** Suite that the pattern is in. null if not applicable to the pattern */
	public Suite highSuite;
	
	public CardPattern(ComboPattern t, Rank r, Suite s, Card... c)
	{
		patternType  = t;
		highSuite = s;
		highRank  = r;
		cardSet      = c;
	}
	
	/** Checks whether a CardPattern has the lowest aka starter card dealt from the deck */
	public static boolean containsLowest(CardPattern p, int lowestCard)
	{
		boolean containsLowest = false;	//assume it is not there unless proven otherwise
		//cycle through all cards and check whether the lowest card is in the pattern
		for(Card c : p.cardSet)
		{
			if(c.getCardValue() <= lowestCard)
				containsLowest = true;
		}
		//return whether or not our card pattern contains the 3 of diamonds
		return containsLowest;
	}
	
	/** Compares CardGroups with the same number of cards in the set. <br>
	 * <i>NOTE: DO NOT COMPARE CARDGROUPS WITH DIFFERENT CARDSET[] LENGTHS!!! WILL RETURN ZERO OR EQUAL</i>
	 */
	public int compareTo(CardPattern otherGroup)
	{
		if(cardSet.length != otherGroup.cardSet.length)
			return 0;
		else 
		{
			switch(patternType)
			{
			case SINGLE: 
			{
				if(cardSet[0].getCardValue() > otherGroup.cardSet[0].getCardValue())
					return 1;
				else if(cardSet[0].getCardValue() < otherGroup.cardSet[0].getCardValue())
					return -1;
				else return 0;
			}
			case DOUBLE:
			{
				int highCardValue = Card.calcCardValue(highRank, highSuite);
				int otherCardValue = Card.calcCardValue(otherGroup.highRank, otherGroup.highSuite);
			
				if(highCardValue > otherCardValue) return 1;
				else if(highCardValue < otherCardValue) return -1;
				else return 0;
			}	
			case TRIPLE:
			{
				int highCardValue = Card.calcCardValue(highRank, highSuite);
				int otherCardValue = Card.calcCardValue(otherGroup.highRank, otherGroup.highSuite);
			
				if(highCardValue > otherCardValue) return 1;
				else if(highCardValue < otherCardValue) return -1;
				else return 0;
			}
			default:
			{
				if(patternType != otherGroup.patternType)
				{
					if(patternType.ordinal() > otherGroup.patternType.ordinal())
						return 1;
					else if(patternType.ordinal() < otherGroup.patternType.ordinal())
						return -1;
					else return 0;
				}
				else if(patternType == otherGroup.patternType)
				{
					int highCardValue = Card.calcCardValue(highRank, highSuite);
					int otherCardValue = Card.calcCardValue(otherGroup.highRank, otherGroup.highSuite);
					if(highCardValue > otherCardValue) return 1;
					else if(highCardValue < otherCardValue) return -1;
					else return 0;
				}
				break;
			}
			}
			return 0;
		}
	}
	
	/** Factory method to create a cardgroup from a set of cards. 
	 *  This also evaluates the cardGroup that is being returned in the process  */	
	public static CardPattern evaluateCards(GameStyle style, Card... cards)
	{
		//values to store the card group evaluation data
		ComboPattern patternType = null;
		Rank highRank = null;
		Suite highSuite = null;
		// variables for evaluating 5-card sets
		boolean 	isFlush = false,
				 isStraight = false;
		
		//**************************************************//
		//	      EVALUATION OF CONBO PATTERNS  			//
		//**************************************************//
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//
		switch(cards.length)
		{
		case 1: 
				//SIMPLE SINGLE CARD PATTERN
				patternType = ComboPattern.SINGLE;
				highRank = cards[0].getRank();
				highSuite = cards[0].getSuite();
				break;
		case 2:
				//DOUBLE CARD PATTERN
				if(cards[0].getRank() == cards[1].getRank())
				{
					patternType = ComboPattern.DOUBLE;
					highRank = cards[0].getRank();	//record the rank
					//record the highest suite
					highSuite= (cards[0].getSuiteValue() > cards[1].getSuiteValue()) ? 
								   cards[0].getSuite() : cards[1].getSuite();
				}
				break;
		case 3:
				//TRIPLE CARD PATTERN
				if(style.triplesValid)		//make sure triples option is available
					if(cards[0].getRank() == cards[1].getRank() && 
					   cards[1].getRank() == cards[2].getRank())
					{
						patternType = ComboPattern.TRIPLE;
						highRank = cards[0].getRank();
						int highestSuite = 0;
						//find and record the highest suite between the three cards in the triple
						for(int i = 0; i < 3; i++)
							if(cards[i].getSuiteValue() > highestSuite)
								highestSuite = cards[i].getSuiteValue();
						highSuite = Suite.values()[highestSuite];
					}
				break;
		case 4:
				//THERE ARE NO FOUR CARD PATTERNS, WILL RETURN 'NULL'
				break;
		case 5:
				//********************************************//
				//	      1. EVALUATE FOR STRAIGHT FLUSH	  //
				//********************************************//
				//First, we evaluate whether or not the flush condition has been satisfied and then store
				//this in a boolean called isFlush to check later
				if(cards[0].getSuite() == cards[1].getSuite() && cards[1].getSuite() == cards[2].getSuite() &&
				cards[2].getSuite() == cards[3].getSuite() && cards[3].getSuite() == cards[4].getSuite())
				{
					isFlush = true;
				}
				//Now we prepare to check whether there is a straight, which is five consecutive ranks.
				//To make this easy, we will sort the cards in our hand by rank
				//We use a simple for loop and make sure that the card array ascends consecutively by rank ending
				//with cards[3]'s rank being 1 lower than card[4]'s. If there is no consecutive ascension, we
				//flag the "isStraight" to false and the card combo is NOT a Straight
				isStraight = true;	//assume true until otherwise
				CardPattern.sortByRank(cards);
				for(Card c : cards)
					System.out.println(c + ": rankValue() == " + c.getRankValue());
				for(int i = 0; i < 4; i++)
					if(cards[i].getRankValue() != cards[i+1].getRankValue() - 1)
					{
						isStraight = false;
						System.out.println("is not a straight!");
					}
				
				//test for the unique cases of starting flushes at Aces or Two if necessary
				if(style.startFlushesAtAce)
				{
					//straight to two
					if((cards[0].getRank() == Rank.THREE && cards[1].getRank() == Rank.FOUR && 
					    cards[2].getRank() == Rank.FIVE &&  cards[3].getRank() == Rank.ACE &&
					    cards[4].getRank() == Rank.TWO) || 	//or
					 //straight to ace
					   (cards[0].getRank() == Rank.THREE && cards[1].getRank() == Rank.FOUR && 
					    cards[2].getRank() == Rank.FIVE && cards[3].getRank() == Rank.SIX &&
					    cards[4].getRank() == Rank.TWO))
							isStraight = true;
				}
				
				if(isStraight && isFlush)
				{
					patternType = ComboPattern.STRAIGHT_FLUSH;	//pattern is declared as a Straight Flush!
					highSuite = cards[4].getSuite();
					highRank  = cards[4].getRank();
					break;
				}
				
				//********************************************//
				//	   2. EVALUATE FOUR OF A KIND(QUAD)       //
				//	   3.    EVALUATE A FULL HOUSE            //
				//********************************************//
				//This evaluation is rather simple. We just sort cards by rank, and then keep track of the first
				//two ranks and then afterwards count how many times these show up. If more than two ranks
				//end up being in our hand, or if the first two ranks have any distribution other than 4 & 1 after
				//we have counted all 5 cards then we know we do not have a full house. If the distribution of
				//2 ranks ends up being just 2 and 3, we know we have a full house
				
				int rank1Value = cards[0].getRankValue(), 	//rank 1 saved as the first card
					rank1Count = 1, 						//this first card has to be counted
				    rank2Value = -1,						//rank 2, second which appears
				    rank2Count = 0; 						//times it comes up
				boolean fullHouseOrQuad = true;	//assume we have either a full house or four of a kind
													//unless we're proven wrong!
															
				for(int i = 1; i < 5; i++)					//continue evalation from the second card
				{
					int rankValue = cards[i].getRankValue(); //for convenient rank value reference
					
					if(rank2Value == -1)		//find the second rank if it exists
					{
						if(rankValue != rank1Value)
							rank2Value = rankValue;	//set the second rank to this new card's rank
					}
					else //if a third rank appears, we know that we do not have either a full house or a quad hand
						if(rankValue != rank1Value && rankValue != rank2Value)
							fullHouseOrQuad = false;
					
					//count how many times each rank shows up
					if(rankValue == rank1Value)
						rank1Count++;			else
					if(rankValue == rank2Value)
						rank2Count++;
				}
				
				if(fullHouseOrQuad && 
						((rank1Count == 4 && rank2Count == 1) || (rank1Count == 1 && rank2Count == 4)))
				{
						patternType = ComboPattern.FOUR_OF_A_KIND;
						Rank quadRank = (rank1Count == 4 ? Rank.values()[rank1Value] : Rank.values()[rank2Value]);
						highRank = quadRank;
						int highestSuite = 0;
						for(int i = 0; i < 5; i++)
							if(cards[i].getRank() == quadRank && cards[i].getSuiteValue() > highestSuite)
								highestSuite = cards[i].getSuiteValue();
						break;
				}																			else
				if(fullHouseOrQuad && 
						((rank1Count == 3 && rank2Count == 2) || (rank1Count == 2 && rank2Count == 3)))
				{
					patternType = ComboPattern.FULL_HOUSE;
					highRank = (rank1Count == 3 ? 	//figure out the triple rank and store it as highest
							 	   Rank.values()[rank1Value] : Rank.values()[rank2Value]);
					highSuite = Suite.DIAMONDS; //WE PICK AN ARBITRARY VALUE FOR THIS TO MAKE COMPARISONS POSSIBLE SINCE FULL HOUSE DOESNT MATTER!
					break;
				}
				
				//********************************************//
				//	       4. EVALUATE A FLUSH                //
				//		   5. EVALUATE A STRAIGHT			  //
				//********************************************//
				//Since we had already checked for a flush earlier when checking for a Straight Flush(1),
				//we simply need to check the boolean "isFlush"
				if(isFlush)
				{
					patternType = ComboPattern.FLUSH;
					int highestCardValue = 0;
					int highestCardIndex = 0;
					for(int i = 0; i < 5; i++)
						if(cards[i].getCardValue() > highestCardValue)
						{
							highestCardValue = cards[i].getCardValue();
							highestCardIndex = i;
						}
					highRank = cards[highestCardIndex].getRank();
					highSuite= cards[highestCardIndex].getSuite();
					break;
				}
				//The same applies for straights. We already know this, and can just retrieve the pattern
				//from the boolean
				if(isStraight)
				{
					patternType = ComboPattern.STRAIGHT;
					
					//first we sort all of the cards again by their value so we can easily retrieve the highest rank
					//from the 5th card(4th index of our cards array) to assign the highest rank and suite
					CardPattern.sortByValue(cards);
					//if we have straights that can begin at Ace and Two, we have to flip the position in the array
					//so that the ace or two is NOT counted as the highest(for our purposes it will be either FIVE or SIX)
					if(style.startFlushesAtAce)
					{
						if(cards[0].getRank() == Rank.THREE && cards[1].getRank() == Rank.FOUR && 
						   cards[2].getRank() == Rank.FIVE && cards[3].getRank() == Rank.ACE &&
						   cards[4].getRank() == Rank.TWO)
								CardPattern.swap(cards, 2, 4);
						else
						if(cards[0].getRank() == Rank.THREE && cards[1].getRank() == Rank.FOUR && 
						   cards[2].getRank() == Rank.FIVE && cards[3].getRank() == Rank.SIX &&
						   cards[4].getRank() == Rank.TWO)
							CardPattern.swap(cards, 3, 4);
					}
					//assign the highest suite and rank now that the last index is set accordingly
					highSuite = cards[4].getSuite();
					highRank  = cards[4].getRank();
					break;
				}
				//** at the end, nothing is found so we just drop into our default statement and
				//return null as this is an invalid hand!
		default:
				return null;	//if we do not have an appropriate number of cards, we return this as a false deck
		}
		//if applicable, we return a CardGroup with all of the information that we've just evaluatted
		if(patternType != null)
			return new CardPattern(patternType, highRank, highSuite, cards){};
		else return null;
	}
	
	/** static helper method to sort an array of cards by rank */
	public static void sortByRank(Card[] cards)
	{
		if(cards.length > 1)					//if there is more than one card,
		for(int i = 1 ; i < cards.length; i++) //start from the lowest and increase
		{
			int cardCheckedAt = i;
			while(cardCheckedAt > 0 && 
				   cards[cardCheckedAt].getRankValue() < cards[cardCheckedAt - 1].getRankValue() )
				CardPattern.swap(cards, cardCheckedAt - 1, cardCheckedAt--);
		}
	}
	
	/** static helper method to sort an array of cards by suite */	
	public static void sortBySuite(Card[] cards)
	{
		if(cards.length > 1)					//if there is more than one card,
		for(int i = 1 ; i < cards.length; i++) //start from the lowest and increase
		{
			int cardCheckedAt = i;
			while(cardCheckedAt > 0 && 
				   cards[cardCheckedAt].getSuiteValue() < cards[cardCheckedAt - 1].getSuiteValue() )
				CardPattern.swap(cards, cardCheckedAt - 1, cardCheckedAt--);
		}
	}
	
	/** static helper method to sort an array of cards by value */
	public static void sortByValue(Card[] cards)
	{
		if(cards.length > 1)					//if there is more than one card,
		for(int i = 1 ; i < cards.length; i++) //start from the lowest and increase
		{
			int cardCheckedAt = i;
			while(cardCheckedAt > 0 && 
				   cards[cardCheckedAt].getCardValue() < cards[cardCheckedAt - 1].getCardValue() )
				CardPattern.swap(cards, cardCheckedAt - 1, cardCheckedAt--);
		}
	}
	
	/** static helper method which swaps two cards within an array of cards */
	public static void swap(Card[] cards, int cardIndex1, int cardIndex2)
	{
		Card backupIndex1 	= cards[cardIndex1];
		cards[cardIndex1]	= cards[cardIndex2];
		cards[cardIndex2]	= backupIndex1;
	}
	
	/** Returns a String with the CardGroup's pattern and it's highest Suite and Rank. For example
	 * "FULL HOUSE with a Suite of 10 and a Rank of Diamonds"
	 * @return 
	 */
	public String patternString()
	{
		String returnString = new String();
		
		if(highRank != null && highSuite != null)
		{
			returnString += patternType + " with a rank of " + highRank +
					" and a suite of " + highSuite;
		} 
		else
		{
			returnString += patternType + " with a " + 
					(highRank != null ? "rank" : "suite") + " of " +
					(highRank != null ? highRank : highSuite);
		}
		return returnString;
	}
	
	/** Display the pattern in a string format as "%PATTERNTYPE with a rank of %RANK and a suite of %SUITE" */
	public String toString()
	{
		if(this == null)
		{
			return "CardGroup is not valid";
		}
		String returnString = new String();
		for(int i = 0; i < cardSet.length; i++)
			returnString += cardSet[i].toString() + "\n";
		
		if(highRank != null && highSuite != null)
		{
			returnString += "This pattern is a " + patternType + " with a rank of " + highRank +
					" and a suite of " + highSuite + ".\n";
		} 
		else
		{
			returnString += "This pattern is a " + this.patternString();
		}
		return returnString;
	}
	
	/** static method to give a convenient string after comparing two CardPatterns by using the compare method */
	public static String comparePatterns(CardPattern c1, CardPattern c2)
	{
		//find the higher card pattern of the two, the variable higherPattern stores which and becomes 0 if
		//comparisons can't be made
		int higherPattern; 
		if(c1.compareTo(c2) > 0)
			higherPattern = 1;
		else if(c1.compareTo(c2) < 0)
			higherPattern = 2;
		else higherPattern = 0;
		
		//create our string and tell whether a comparison could be made and what it was
		String returnString = new String();
		if(higherPattern != 0)
		{
			returnString += "The pattern " + c1.patternString() + 
		 ( (higherPattern == 1) ? " wins to a " : " loses to a ") + c2.patternString(); 
		}
		else
		{
			returnString += "These two card groups can't be compared to each other!";
		}
		//return it from the method
		return returnString;
	}
}
