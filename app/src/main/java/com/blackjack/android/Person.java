package com.blackjack.android;

import java.util.LinkedList;
import java.util.List;

public class Person extends Player {
	
	private int bet = 0;
	private int money = 0;
	private int insurance = 0;
	
	public Person(int money) {
		this.money = money;
	}
	
	public void makeBet(int bet) {
		this.bet = bet;
		this.money -= bet;
	}
	
	public boolean isValidBet(int bet) {
		return bet <= this.money && bet > 0;
	}
	
	public void makeInsuranceBet(int insurance) {
		this.insurance = insurance;
		this.money -= insurance;
	}
	
	public boolean isValidInsuranceBet(int insurance) {
		return insurance <= (bet / 2.0);
	}
	
	public void doubleBet() {
		this.money -= bet;
		this.bet = this.bet * 2;
	}
	
	public int getBet() {
		return this.bet;
	}
	
	public int getMoney() {
		return this.money;
	}
	
	public void winRound() {
		this.money += bet * 2;
		bet = 0;
	}
	
	public void winRoundBlackjack() {
		this.money += bet * 2.5;
		bet = 0;
	}
	
	public void winInsurance() {
		this.money += insurance * 3;
		insurance = 0;
	}
	
	public void drawRound() {
		this.money += bet;
		bet = 0;
	}
	
	public void loseRound() {
		bet = 0;
	}
	
	public void loseInsurance() {
		insurance = 0;
	}
	
	public boolean canSplit() {
		return (hand.size() == 2 && hand.get(0).getNumber() == hand.get(1).getNumber());
	}
	
	public boolean canDouble() {
		return (hand.size() == 2 && getCardsValue() <= 11 && getCardsValue() >= 9);
	}
	
	
	// TODO turn List into array
	public List<Card> splitCards() {
		List<Card> splitCards = new LinkedList<Card>();
		splitCards.add(hand.remove(hand.size() - 1));
		splitCards.add(hand.remove(hand.size() - 1));
		return splitCards;
	}
	
	public void printDeck() {
		System.out.println("Player hand:");
		super.printDeck();
	}
}
