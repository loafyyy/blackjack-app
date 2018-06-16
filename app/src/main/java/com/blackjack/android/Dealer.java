package com.blackjack.android;

public class Dealer extends Player {

    // TODO delete print not used
	public void printFirstCard() {
		if (hand.isEmpty()) {
			return;
		}
		Card firstCard = hand.get(0);
		System.out.println("Dealer first card:");
		System.out.println("\t" + firstCard);
		System.out.println("\t>> Total: " + firstCard.getValue());
	}

	public void printDeck() {
		System.out.println("Dealer hand:");
		super.printDeck();
	}

	public Card getFirstCard() {
	    return hand.get(0);
    }
}
