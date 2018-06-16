package com.blackjack.android;

import java.util.Collections;
import java.util.LinkedList;

public class Deck {

	private LinkedList<Card> deck;
	
	// TODO delete when not testing
	public void initializeTest(int numDecks) {
		initialize(numDecks);
		deck.add(0, new Card(5, "spades"));
		deck.add(0, new Card(1, "clubs"));
		deck.add(0, new Card(5, "diamonds"));
		deck.add(0, new Card(10, "clubs"));
		deck.add(0, new Card(5, "hearts"));
		deck.add(0, new Card(1, "clubs"));
		deck.add(0, new Card(5, "clubs"));
	}

	public void initialize(int numDecks) {

		deck = new LinkedList<Card>();

		for (int d = 0; d < numDecks; d++) {
			for (int i = 1; i <= 13; i++) {
				for (int j = 1; j <= 4; j++) {

					String suite = null;

					switch (j) {
					case 1:
						suite = "diamonds";
						break;
					case 2:
						suite = "clubs";
						break;
					case 3:
						suite = "hearts";
						break;
					case 4:
						suite = "spades";
						break;
					}

					deck.add(new Card(i, suite));
				}
			}
		}
	}

	public void shuffleDeck() {
		Collections.shuffle(deck);
	}

	// TODO handle case where no more cards lefts
	public Card deal() {
		return deck.remove(0);
	}
	
	public int getSize() {
		return deck.size();
	}
	
	public void printDeck() {
		for (Card card: deck) {
			System.out.println(card);
		}
	}
}