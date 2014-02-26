package com.rob2d.android.chinesepoker.gui.gamescreen;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.rob2d.android.chinesepoker.Assets;
import com.rob2d.android.chinesepoker.game.CardGame;
import com.rob2d.android.chinesepoker.game.Player;
import com.rob2d.android.chinesepoker.gui.GameScr;
import com.rob2d.android.chinesepoker.menu.MenuButton;
import com.rob2d.android.chinesepoker.menu.MenuSlider;
import com.rob2d.android.framework.Input;
import com.rob2d.android.framework.RectEntity;
import com.rob2d.android.framework.ScreenEntity;

public class PlayerHand extends ScreenEntity
{
	public final static int CARD_A_W = 96, CARD_B_W = 56, CARD_H = 128;
	public final int MAX_CARDS_VISIBLE = 13;

	public final int NO_CARD_SELECTED = -1;
	public Input input;
	public Player player;
	public ArrayList<CardEntity> cards;

	public ArrayList<Integer> selectedCards = new ArrayList<Integer>();
	public RectEntity[] selectedCardRects = new RectEntity[5];
	public int playValid;
	public int cardSelected = -1;
	public GameScr gameScreen;
	/** how many cards to the right the hand has been scrolled */
	public int scrollIndex = 0;

	public int hoverSlot = -1;
	public int selectedCardGlow = 0;

	public int cardTapTimer = 0;
	public boolean draggingCard = false;
	public boolean cardPatternValid = false;

	private int initTouchX, initTouchY;
	private RectEntity draggedCardRect;

	public MenuButton scrollLeftButton;
	public MenuButton scrollRightButton;
	public MenuSlider scrollSliderButton;
	public boolean cardsVisible = false;

	public PlayerHand(int l, GameScr scr)
	{
		super(l, scr);
		gameScreen = scr;
		this.touchable = true;
		input = scr.game.getInput();
		cards = new ArrayList<CardEntity>();

		// initialize navigation buttons
		scrollLeftButton = new MenuButton(32, 480, 3, gameScreen,
				Assets.scrollLeftBtnIF);
		scrollRightButton = new MenuButton(712, 480, 3, gameScreen,
				Assets.scrollRightBtnIF);
		scrollSliderButton = new MenuSlider(96, 256, 96, 696, 3, 3, gameScreen,
				Assets.scrollSliderIF);
		scrollSliderButton.setPointValue(0);
		scrollLeftButton.visible = false;
		scrollRightButton.visible= false;
		scrollSliderButton.visible = false;

		x = 16;
		y = 480;

		// initialize the dragged card graphics
		draggedCardRect = new RectEntity(new Rect(0, 0, CARD_A_W, CARD_H),
				Color.argb(80, 40, 255, 100), 3, screen);
		draggedCardRect.visible = false;

		// initialize the selected card graphics
		for (int i = 0; i < 5; i++)
		{
			selectedCardRects[i] = new RectEntity(new Rect(0, 0, CARD_A_W,
					CARD_H), Color.rgb(0, 0, 255), 3, screen);
			selectedCardRects[i].visible = false;
		}
	}

	@Override
	public Rect getBounds()
	{
		// calculate the width based on how many cards are in our card entity
		// array
		int cardsOnScreen = (cards.size() > MAX_CARDS_VISIBLE) ? MAX_CARDS_VISIBLE : cards.size();

		int width = (cardsOnScreen > 0 ? CARD_A_W : 0)
				+ (cardsOnScreen > 1 ? CARD_B_W * (cardsOnScreen - 1) : 0);

		return new Rect(8, (int) y, width + 8, (int) y + CARD_H);
	}

	@Override
	public void update()
	{
		super.update();

		// position the player hand horizontally relative to the scrolling
		x = 8 - (scrollIndex * (CARD_B_W));

		selectedCardGlow = (selectedCardGlow + 1) % 100;
		int selectedCardAlpha = (selectedCardGlow <= 50 ? 30 + selectedCardGlow
				: 30 + (100 - selectedCardGlow));
		for (RectEntity e : selectedCardRects)
			e.color = Color.argb(selectedCardAlpha, 0, 0, 255);
		// enable only during the turn sequence in offline mode, but in online mode always allow player to mess with his cards
		if ((gameScreen.onlineSession == null && gameScreen.playSequence) || (gameScreen.onlineSession != null)) 
		{
			if (y > 336)
				y -= 16;

			// if a user touches the player hand zone, retrieve whether or not
			// he's selected a card and if so, which one
			if (input.isTouchDown(0) && touchTimer == 1)
			{
				initTouchX = input.getTouchX(0);
				initTouchY = input.getTouchY(0);

				if (cardSelected == NO_CARD_SELECTED)
					cardSelected = cardIndexAt(initTouchX + (CARD_A_W / 2));
			} 
			else if (input.isTouchDown(0) && touchTimer == 7)
				draggingCard = true;
			else if (!input.isTouchDown(0))
			{
				if (cardSelected != NO_CARD_SELECTED && touchTimer > 0
						&& touchTimer <= 7)
					toggleSelection(cardSelected);
				else if (cardSelected != NO_CARD_SELECTED
						&& hoverSlot != NO_CARD_SELECTED && draggingCard)
				{
					shiftCard(cardSelected, hoverSlot);
				}

				cardSelected = NO_CARD_SELECTED;
				draggingCard = false;
			}

			if (cardSelected == NO_CARD_SELECTED
					|| hoverSlot == NO_CARD_SELECTED || !draggingCard)
				draggedCardRect.visible = false;

		} else if(gameScreen.gamePlayed != null && gameScreen.gamePlayed.onlineSession == null)//only hide the player hand offline since theres no prompts!
		{
			if (y < 480)
				y += 16;
		}
		positionObjects();

		if (scrollRightButton.isClicked)
		{
			scrollRightButton.isClicked = false;
			setScrollIndex(scrollIndex + 1);
			scrollSliderButton.setPointValue(scrollIndex);
		}
		if (scrollLeftButton.isClicked)
		{
			scrollLeftButton.isClicked = false;
			setScrollIndex(scrollIndex - 1);
			scrollSliderButton.setPointValue(scrollIndex);
		}
	}

	public void setScrollIndex(int index)
	{
			scrollIndex = index;
			enableDisableArrows();
			
			Log.d("CPDEBUG", scrollSliderButton.getPointValue() + "");
	}

	public void clearCards()
	{
		synchronized(this)
		{
			for (CardEntity c : cards)
				c.destroy();
		}
		cards.clear();
	}

	/** declare all of the cards in a player hand */
	public void setCards(int[] newCards)
	{
		int scrollPoints = 0;
		if (newCards.length > MAX_CARDS_VISIBLE)
		{
			scrollLeftButton.visible = true;
			scrollRightButton.visible = true;
			scrollSliderButton.visible = true;
			scrollPoints = (newCards.length - MAX_CARDS_VISIBLE+1);
		}
		else
		{
			scrollLeftButton.visible = false;
			scrollRightButton.visible = false;
			scrollSliderButton.visible = false;
		}
		scrollSliderButton.setValueCount(scrollPoints + 1);

		clearCards();
		for (int i = 0; i < newCards.length; i++)
		{
			CardEntity addedCard = new CardEntity(
					newCards[(newCards.length - 1) - i], screen);
			cards.add(0, addedCard);
		}

		for (CardEntity c : cards)
			c.cardSlot = cards.indexOf(c);

		scrollIndex = 0;
		enableDisableArrows();
		refreshSelectionPointers();
		positionObjects();
	}

	/**
	 * position cards in their slots when they aren't being dragged by your
	 * finger
	 */
	public void positionObjects()
	{
		for (int cardSlot = 0; cardSlot < cards.size(); cardSlot++)
		{
			CardEntity c = cards.get(cardSlot);
			// position the cards, alter their visibility as necessary
			if (cardSelected != cardSlot)
			{
				c.x = x + cardSlot * CARD_B_W;
				c.y = y;
				if (cardsVisible)
				{
					if (cardSlot < scrollIndex || cardSlot > scrollIndex + 12)
						c.visible = false;
					else
						c.visible = true;
				} else if (!cardsVisible)
					c.visible = false;
			} else if (cardSelected == cardSlot)
			{
				if (draggingCard)
					dragCard(c);
			}
		}

		scrollLeftButton.y = y - 88;
		scrollRightButton.y = y - 88;
		scrollSliderButton.y = y - 84;
		drawCardSelectionRects();
	}

	public int getCardX(int cardSlot)
	{
		return (int) x + cardSlot * CARD_B_W;
	}

	/** determine what card is at a horizontal location on a screen */
	public int cardIndexAt(int touchX)
	{
		int returnValue;
		// calculate the width of card array visible based on how many cards are
		// in our card entity array
		int cardsOnScreen = (cards.size() > MAX_CARDS_VISIBLE) ? MAX_CARDS_VISIBLE : cards.size();
		final int INITIAL_X = 8;

		// if there is more than one card, then we select the cards based on
		// whether the player selects the left part of the card unless its the
		// rightmost card
		if (touchX < getBounds().left + CARD_A_W - CARD_B_W) // leftmost case
			returnValue = 0;
		else if (touchX <= getBounds().right && touchX >= getBounds().left)
			returnValue = ((touchX) - (int) (INITIAL_X + CARD_A_W)) / CARD_B_W; // anything
																				// in
																				// between
		else if (touchX > getBounds().right - (CARD_B_W)) // rightmost case
			returnValue = cardsOnScreen - 1;
		else
			returnValue = -1;

		if (returnValue != -1) // if the card was valid, set its selection index
								// relative to the currently scrolled card index
			returnValue += scrollIndex;

		if (!validCardSelectionSlot(returnValue))
			returnValue = -1;

		return returnValue;
	}

	public void dragCard(CardEntity c)
	{
		c.x = getCardX(cardSelected) + input.getTouchX(0) - initTouchX;
		c.y = y - 32 + input.getTouchY(0) - initTouchY;

		// DETECT THE HOVER SLOT AND HIGHLIGHT DRAGGED CARD POSITION
		hoverSlot = cardIndexAt((int) c.x + CARD_A_W);
		if (hoverSlot != NO_CARD_SELECTED && cardSelected != NO_CARD_SELECTED)
		{
			draggedCardRect.visible = true;
			draggedCardRect.x = getCardX(hoverSlot);
			draggedCardRect.y = y;

			if (hoverSlot == scrollIndex) // change shape depending on whether
											// its the first card...
				draggedCardRect.rect.left = 0;
			else
				draggedCardRect.rect.left = CARD_A_W - CARD_B_W;
		}
	}

	/** toggle a user's selection */
	public boolean toggleSelection(int selectedCard)
	{
		boolean cardWasSelected = false;
		for (int i = 0; i < selectedCards.size(); i++)
		{
			if (selectedCard == selectedCards.get(i))
			{
				cards.get(selectedCard).selected = false;
				cardWasSelected = true;
				refreshSelectionPointers();
				return true;
			}
		}
		if (!cardWasSelected)
		{
			if (selectedCards.size() < 5)
			{
				cards.get(selectedCard).selected = true;
				refreshSelectionPointers();
				return true;
			} else
			{
				return false;
			}
		}
		return false; // just to keep the compiler happy... : )
	}

	public void drawCardSelectionRects()
	{
		for (int i = 0; i < 5; i++)
		{
			if (i < selectedCards.size())
			{
				selectedCardRects[i].visible = true;
				selectedCardRects[i].x = getCardX(selectedCards.get(i));
				selectedCardRects[i].y = y;

				if (selectedCards.get(i) == scrollIndex)
					selectedCardRects[i].rect.left = 0;
				else
					selectedCardRects[i].rect.left = CARD_A_W - CARD_B_W;
			} else
			{
				selectedCardRects[i].visible = false;
			}
		}
	}

	public void shiftCard(int fromSlot, int toSlot)
	{
		// shift card algorithm
		if (toSlot > fromSlot)
		{
			int backUpValue = cardAt(fromSlot);

			for (int i = fromSlot + 1; i <= toSlot; i++)
				swapCards(i, i - 1);
			cards.get(toSlot).setCard(backUpValue);
		} else if (toSlot < fromSlot)
		{
			int backUpValue = cardAt(fromSlot);
			for (int i = fromSlot - 1; i >= toSlot; i--)
				swapCards(i, i + 1);
			cards.get(toSlot).setCard(backUpValue);
		}
		// apply any changes to the order of a player's hand(located in the
		// actual CardGame class for computational logic)
		refreshGameHand();
		// and then apply changes to his selection(the values in the actual
		// CardGame class for computational logic)
		refreshSelectionPointers();
	}

	/*
	 * refresh the cards in a player's hand. This is used primarily because we
	 * shift the order in the GUI and need to apply these changes to the
	 * player's actual hand so that the selection indexes still point to the
	 * same card when they are also shifted
	 */
	public void refreshGameHand()
	{
		CardGame gamePlayed = gameScreen.gamePlayed;

		for (int i = 0; i < cards.size(); i++)
		{
			if(gameScreen.gamePlayed.onlineSession == null)
			{
				gameScreen.gamePlayed.players[gameScreen.gamePlayed.playerTurn].hand
					.get(i).setCardValue(cards.get(i).cardValue);
			}
			else
			{
				gameScreen.gamePlayed.players[gameScreen.gamePlayed.onlineSession.playerSlot].hand
					.get(i).setCardValue(cards.get(i).cardValue);
			}
			
		}

		gameScreen.playTurnButton.buttonEnabled = gameScreen.gamePlayed.playerSelectionValid();
	}

	/**
	 * assign selected rectangle graphics to the required when something changes
	 * (right now, called when a card selection is toggled or cards have been
	 * shifted)
	 */
	public void refreshSelectionPointers()
	{
		selectedCards.clear();
		for (CardEntity c : cards)
			if (c.selected)
				selectedCards.add(c.cardSlot);

		// change the player's selection to match the GUI and check whether it
		// was valid(as well, enabling/disabling the play turn button)
		if(gameScreen.gamePlayed.onlineSession == null)
			gameScreen.gamePlayed.players[gameScreen.gamePlayed.playerTurn].selectedCards = selectedCards;
		else
			gameScreen.gamePlayed.players[gameScreen.gamePlayed.onlineSession.playerSlot].selectedCards = selectedCards;
		
		gameScreen.playTurnButton.buttonEnabled = gameScreen.gamePlayed.playerSelectionValid();
	}

	public void swapCards(int slot1, int slot2)
	{
		int backupCardValue = cardAt(slot1);
		boolean backupSelected = cards.get(slot1).selected;

		if (validCardSelectionSlot(slot1))
		{
			cards.get(slot1).setCard(cardAt(slot2));
			cards.get(slot1).selected = cards.get(slot2).selected;
		}
		if (validCardSelectionSlot(slot2))
		{
			cards.get(slot2).setCard(backupCardValue);
			cards.get(slot2).selected = backupSelected;
		}
	}

	public int cardAt(int slot)
	{
		if (slot >= 0 && slot < cards.size())
			return cards.get(slot).cardValue;
		else
			return -1;
	}

	/** checks if a specific card position in a player's hand is selected */
	public int cardSelectedIndex(int cardSlot)
	{
		int selectedCardAt = -1;

		for (int i = 0; i < selectedCards.size(); i++)
			if (cardSlot == selectedCards.get(i))
				selectedCardAt = i;

		return selectedCardAt;
	}

	public boolean validCardSelectionSlot(int cardSlot)
	{
		if (cardSlot >= scrollIndex
				&& cardSlot < (cards.size() <= MAX_CARDS_VISIBLE ? cards.size()
						: scrollIndex + MAX_CARDS_VISIBLE + 1))
			return true;
		else
			return false;
	}

	public void enableDisableArrows()
	{
		if (scrollIndex > 0)
			scrollLeftButton.buttonEnabled = true;
		else
			scrollLeftButton.buttonEnabled = false;

		if (scrollIndex + MAX_CARDS_VISIBLE < cards.size())
			scrollRightButton.buttonEnabled = true;
		else
			scrollRightButton.buttonEnabled = false;
	}

	public void setVisible(boolean visible)
	{
		cardsVisible = visible;
		if(cards.size() > MAX_CARDS_VISIBLE)
		{
			scrollLeftButton.visible = visible;
			scrollRightButton.visible = visible;
			scrollSliderButton.visible = visible;
		}
		else
		{
			scrollLeftButton.visible = false;
			scrollRightButton.visible = false;
			scrollSliderButton.visible = false;
		}
	}
}
