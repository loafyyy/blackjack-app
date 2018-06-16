package com.blackjack.android;

import java.util.LinkedList;
import java.util.List;

public class Player {
	
	protected List<Card> hand;
	
	public Player() {
		hand = new LinkedList<Card>();
	}
	
	public void emptyDeck() {
		hand.clear();
	}
	
	public void addCard(Card card) {
		hand.add(card);
	}
	
	public List<Card> getCards() {
		return hand;
	}

	public int getCardsValue() {
		int value = 0;
		boolean hasAce = false;
		for (Card card : hand) {
			int cardValue = card.getValue();
			// check to see whether there is Ace - can count as 1 or 11
			if (cardValue == 1) {
				hasAce = true;
			}
			value += cardValue;
		}
		
		// can count Ace as 11 - return highest value possible
		if (value <= 11 && hasAce) {
			value += 10;
		}
		
		return value;
	}
	
	public boolean isBust() {
		return getCardsValue() > 21;
	}
	
	public boolean isBlackjack() {
		return hand.size() == 2 && getCardsValue() == 21;
	}
	
	public void printDeck() {
		for (Card card: hand) {
			System.out.println("\t" + card);
		}
		System.out.println("\t>> Total: " + getCardsValue());
	}
}
