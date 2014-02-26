package com.rob2d.android.chinesepoker.game;
/*  Filename:   Player.java
 *  Package:    com.rob2d.android.chinesepoker.game
 * 	Author:     Robert Concepcion III  */

import java.util.ArrayList;
import java.util.Arrays;

/** Class which represents a player who has a hand and can make selections and gets turns to throw cards 
 *  into a game */
public class Player 
{
	/** number of players currently playing */
	public static int playerCount = 0;
	/** number of the current player within the game(used for turns and keeping track of players */
	public int playerNumber;
	/** the player's username to display in prompt messages */
	public String name;
	/** whether or not the player is still in the game's play rotation */
	public boolean isInGame;
	/** whether the player has come in first place in the last game */
	public boolean hasWon;
	/** whether the player has come in last place */
	public boolean hasLost;
	
	public CardGame cardGame;
	
	/** the player's hand/cards */
	public ArrayList<Card> hand;
	
	/** the index of the cards that the player has current selected */
	public ArrayList<Integer> selectedCards = new ArrayList<Integer>();
	
	public Player(CardGame cG)
	{ 
		playerNumber = playerCount++;
		isInGame = true;
		hand = new ArrayList<Card>();
		cardGame = cG;
	}
	
	/** Give the player's hand a set of cards 
	 * @param c - cards to add to the player's hand*/
	public void dealt(ArrayList<Card> c)
	{
		hand = c;
	}
	
	/** Give the player's hand an extra card */
	public void givenCards(ArrayList<Card> cards)
	{
		for(Card c: cards)
		hand.add(c);
	}
	
	/** Give the player's hand a set of cards 
	 * @param c - cards to add to the player's hand*/
	public void dealt(Card[] cards)
	{
		hand.clear();	//clear the hand of all cards
		for(Card c : cards)	//add all cards from the array to the hand
			hand.add(c);
	}
	
	/** add a card to the player's CardGroup selection */
	public boolean addToSelection(int index)
	{
		boolean isSelectedAlready = false;	//whether or not the card to be added has already been selected
		
		for(int i : selectedCards)			//scan through the selectedCards array to make sure this index is not already there
			if(index == i)
				isSelectedAlready = true;
		
		if(!isSelectedAlready && index >= 0 && index <= hand.size())
		{
			selectedCards.add(index);
			return true;
		}
		else return false;
	}
	
	/** remove a card from the player's CardGroup selection */
	public void removeFromSelection(int index)
	{
		if(index >= 0 && index < selectedCards.size())	//verify the card index of the card to be removed exists
			selectedCards.remove(index);
	}
	
	/** method to get a list of the indexes of unselected cards within a 
	 *  hand for convenient listing to the player in a text GUI */
	public int[] unselectedCards()
	{
		//first, we collect all of the unselected card indexes in an arraylist		
		int[] unselectedCards = new int[hand.size() - selectedCards.size()];
		int insertIndex = 0;
		
		for(int cardInHand = 0; cardInHand < hand.size(); cardInHand++)		//cycle through all cards
		{
			boolean selected = false;
			for(int selectedCard = 0; selectedCard < selectedCards.size(); selectedCard++)	//scroll through selected card indexes to check if the current card in hand is one
				if(cardInHand == selectedCard)
					selected = true;
		
			if(!selected)	//if current index has been selected
				unselectedCards[insertIndex++] = cardInHand;
		} 
		
		return unselectedCards;
	}
	
	/** clear all of the player's currently selected cards*/
	public void clearSelection()
	{
		while(selectedCards.size() > 0)	//cycle through selection and delete all cards
			selectedCards.remove(0);
	}
	
	/**retrieve the pattern of cards played by the player
	 * @return CardPattern.evaluteCards(player' s Hand). If this is an invalid hand, NULL will be returned.
	 * To actually make the comparison of cards played to others, this CardPattern will be checked externally
	 * against another card pattern through use of the CardGame class */
	public CardPattern trySelection()
	{
		//obtain an array of cards from the selected indexes
		Card[] cardSelection = new Card[selectedCards.size()];
		for(int i = 0; i < selectedCards.size(); i++)
			cardSelection[i] = hand.get(selectedCards.get(i));
				
		//generate a pattern from selected cards and then return it
		CardPattern returnPlay = CardPattern.evaluateCards(cardGame.gameStyle, cardSelection);
		
		return returnPlay;
	}
	
	/** remove all cards from your selection as they are being played */
	public void playSelection()
	{
		for(Card c : trySelection().cardSet)//remove all cards from the player's selection as his cards have been thrown down
			hand.remove(c);
		clearSelection();					//reset the player's selection of cards so no indexes are selected
	}
	
	/**summarize the player's hand for print fns */
	public String toString()
	{
		return "Player #" + (playerNumber+1) + " is " + (isInGame? "" : "not") + 
				"in this game\n" + hand;
	}
	
	/** return a convenient string w a list format of the cards in a player's hand */
	public String getHandSummary()
	{
		String returnString = new String();
		for(int i = 0; i < hand.size(); i++)
			returnString += (i+1) + ". " + hand.get(i) + "\n";
		return returnString;
	}
	
	/** return a convenient string with a list format of the cards in a player's selection */
	public String getSelectionSummary()
	{
		String returnString = new String();
		for(int i = 0; i < selectedCards.size(); i++)
			returnString += (i+1) + ". " + hand.get(selectedCards.get(i)) + "\n";
		return returnString;
	}
	
	/** return a convenient string with a list of the unselected cards within a player's hand */
	public String getUnselectedSummary()
	{
		String returnString = new String();
		int[] unselectedCards = unselectedCards();
		for(int i = 0; i < unselectedCards.length; i++)
			returnString += (i+1) + ". " + hand.get(unselectedCards[i]) + "\n";
		
		System.out.println("DEBUG - Unselected Summary: \n\t" + returnString);
		return returnString;
	}
	
	/** get the values of the current player's hand. useful for the gui */
	public int[] getHandValues()
	{
		int[] returnValues = new int[hand.size()];
		for(int i = 0; i < hand.size(); i++)
			returnValues[i] = hand.get(i).getCardValue();
		return returnValues;
	}
	
	public void setSelection(ArrayList<Integer> newSelection)
	{
		selectedCards = newSelection;
	}
}
