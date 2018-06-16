package com.blackjack.android;

public class Card {

    private String suite;
    private int number;

    public Card(int number, String suite) {
        this.number = number;
        this.suite = suite;
    }

    public String toString() {
        return "c" + number + "of" + suite;
    }

    public String getSuite() {
        return suite;
    }

    public int getNumber() {
        return number;
    }

    public int getValue() {
        if (number > 10) {
            return 10;
        } else return number;
    }
}