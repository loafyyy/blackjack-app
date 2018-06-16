package com.blackjack.android;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // main view
    private ConstraintLayout constraintLayout;

    // views
    private Button standButton, hitButton, doubleButton, splitButton;
    private Button betOKButton, dealButton, insuranceYesButton, insuranceNoButton;
    private TextView infoTV, betTV, moneyTV;
    private TextView playerValTV, dealerValTV;
    private EditText betET;

    // cards
    private List<ImageView> playerCards;
    private List<ImageView> dealerCards;
    private ImageView playerCard1;
    private ImageView dealerCard1;

    private Deck deck;
    private Dealer dealer;
    private Person person;

    // how many bets there are going on (in case of splits)
    private int numPlaysLeft;
    // total number of piles/plays (in case of splits)
    private int numPiles;

    private Context mContext;
    private int numDecks = 1;
    private int shuffleAmount = 30; // TODO set this - shuffle when how many cards left in the deck
    // when true, edit text is for insuranceBet bet
    private boolean insuranceBet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insuranceBet = false;
        mContext = this;
        constraintLayout = findViewById(R.id.constraint_layout);
        playerCards = new LinkedList<>();
        dealerCards = new LinkedList<>();

        // find views
        playerValTV = findViewById(R.id.playerValue);
        dealerValTV = findViewById(R.id.dealerValue);
        playerCard1 = findViewById(R.id.playerCard1);
        playerCard1.setVisibility(View.INVISIBLE);
        dealerCard1 = findViewById(R.id.dealerCard1);
        dealerCard1.setVisibility(View.INVISIBLE);

        standButton = findViewById(R.id.standButton);
        standButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stand();
            }
        });
        hitButton = findViewById(R.id.hitButton);
        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hit();
            }
        });
        doubleButton = findViewById(R.id.doubleButton);
        doubleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doubleBet();
            }
        });
        splitButton = findViewById(R.id.splitButton);
        splitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                split();
            }
        });
        infoTV = findViewById(R.id.infoTextView);
        moneyTV = findViewById(R.id.moneyTextView);
        betTV = findViewById(R.id.betTextView);
        betET = findViewById(R.id.betEditText);
        betET.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (!insuranceBet) {
                        betOK();
                    } else {
                        insuranceOK();
                    }
                    return true;
                }
                return false;
            }
        });
        betOKButton = findViewById(R.id.betOKButton);
        betOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!insuranceBet) {
                    betOK();
                }
                else {
                    insuranceOK();
                }
            }
        });
        dealButton = findViewById(R.id.dealButton);
        dealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRound();
            }
        });

        // insurance button
        insuranceYesButton = findViewById(R.id.insuranceYesButton);
        insuranceYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoTV.setText("Enter insurance amount (up to half of original bet)");
                betET.setVisibility(View.VISIBLE);
                insuranceBet = true;
            }
        });
        insuranceNoButton = findViewById(R.id.insuranceNoButton);
        insuranceNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // one round until player wins or loses
                List<String> validMoves = new LinkedList<>();
                validMoves.add("stand");
                validMoves.add("hit");
                if (person.canDouble()) {
                    validMoves.add("double");
                }
                if (person.canSplit()) {
                    validMoves.add("split");
                }
                playRound(validMoves);
            }
        });

        // INITIALIZE GAME
        deck = new Deck();
        // deck.initializeTest(6); // for testing
        deck.initialize(numDecks); // TODO take number of decks from args
        deck.shuffleDeck();
        dealer = new Dealer();
        person = new Person(1000); // TODO initialize how much money person starts with

        disableButtons();
        dealButton.setEnabled(true);
        dealButton.setVisibility(View.VISIBLE);
        moneyTV.setText(Integer.toString(person.getMoney()));
        infoTV.setText("Click the Deal button to start");
    }

    public void showPlayerCard(Card card) {
        int id = mContext.getResources().getIdentifier(card.toString(), "drawable", mContext.getPackageName());
        ImageView imageView = null;
        if (playerCards.size() == 0) {
            imageView = playerCard1;
            imageView.setVisibility(View.VISIBLE);
        } else {
            ImageView prevCard = playerCards.get(playerCards.size() - 1);
            ConstraintSet set = new ConstraintSet();
            imageView = new ImageView(mContext);
            imageView.setId(playerCards.size());
            constraintLayout.addView(imageView, 0);
            set.clone(constraintLayout);
            set.connect(imageView.getId(), ConstraintSet.LEFT, prevCard.getId(), ConstraintSet.LEFT, playerCards.get(0).getWidth() / 2);
            set.connect(imageView.getId(), ConstraintSet.BOTTOM, prevCard.getId(), ConstraintSet.BOTTOM, 0);
            set.constrainWidth(imageView.getId(), playerCards.get(0).getWidth());
            set.constrainHeight(imageView.getId(), playerCards.get(0).getHeight());
            set.setElevation(imageView.getId(), playerCards.size());
            set.applyTo(constraintLayout);
        }
        imageView.setImageResource(id);
        playerCards.add(imageView);
        playerValTV.setText(Integer.toString(person.getCardsValue()));
    }

    public void showDealerCard(Card card) {
        int id = mContext.getResources().getIdentifier(card.toString(), "drawable", mContext.getPackageName());
        ImageView imageView = null;
        if (dealerCards.size() == 0) {
            imageView = dealerCard1;
            imageView.setVisibility(View.VISIBLE);
        } else {
            ImageView prevCard = dealerCards.get(dealerCards.size() - 1);
            ConstraintSet set = new ConstraintSet();
            imageView = new ImageView(mContext);
            imageView.setId(dealerCards.size() + 100);
            constraintLayout.addView(imageView, 0);
            set.clone(constraintLayout);
            set.connect(imageView.getId(), ConstraintSet.LEFT, prevCard.getId(), ConstraintSet.LEFT, dealerCards.get(0).getWidth() / 2);
            set.connect(imageView.getId(), ConstraintSet.BOTTOM, prevCard.getId(), ConstraintSet.BOTTOM, 0);
            set.constrainWidth(imageView.getId(), dealerCards.get(0).getWidth());
            set.constrainHeight(imageView.getId(), dealerCards.get(0).getHeight());
            set.setElevation(imageView.getId(), dealerCards.size());
            set.applyTo(constraintLayout);
        }
        imageView.setImageResource(id);
        dealerCards.add(imageView);
        if (dealerCards.size() != 2) {
            dealerValTV.setText(Integer.toString(dealer.getCardsValue()));
        }
    }

    public void startRound() {
        disableButtons();

        // reset decks
        person.emptyDeck();
        dealer.emptyDeck();
        numPlaysLeft = 1;
        numPiles = 1;

        // reset images
        for (int i = 1; i < playerCards.size(); i++) {
            constraintLayout.removeView(playerCards.get(i));
        }
        playerCard1.setVisibility(View.INVISIBLE);
        playerCards.clear();
        for (int i = 1; i < dealerCards.size(); i++) {
            constraintLayout.removeView(dealerCards.get(i));
        }
        dealerCard1.setVisibility(View.INVISIBLE);
        dealerCards.clear();

        // reset values
        playerValTV.setText("");
        dealerValTV.setText("");

        // when to shuffle
        if (deck.getSize() < shuffleAmount) {
            deck.initialize(numDecks);
            deck.shuffleDeck();
            infoTV.setText("Shuffled deck\nEnter bet amount");
        } else {
            infoTV.setText("Enter bet amount");
        }

        // player makes a bet
        betET.setVisibility(View.VISIBLE);
        betOKButton.setEnabled(true);
        betOKButton.setVisibility(View.VISIBLE);
    }

    public void insuranceOK() {
        betET.setVisibility(View.GONE);
        betOKButton.setVisibility(View.GONE);

        // hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        insuranceBet = false;
        String insuranceStr = betTV.getText().toString();
        try {
            int insurance = Integer.parseInt(insuranceStr);
            if (person.isValidInsuranceBet(insurance)) {
                person.makeInsuranceBet(insurance);
            }
            else {
                infoTV.setText("Invalid insurance bet");
            }
        } catch (NumberFormatException e) {
            infoTV.setText("Invalid insurance bet");
        }
        if (dealer.isBlackjack()) {
            person.winInsurance();
            infoTV.setText("Win insurance!");
        } else {
            person.loseInsurance();
            infoTV.setText("Lose insurance!");
        }

        // one round until player wins or loses
        List<String> validMoves = new LinkedList<>();
        validMoves.add("stand");
        validMoves.add("hit");
        if (person.canDouble()) {
            validMoves.add("double");
        }
        if (person.canSplit()) {
            validMoves.add("split");
        }
        playRound(validMoves);
    }

    public void betOK() {
        // hide keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        String betStr = betET.getText().toString();
        try {
            int bet = Integer.parseInt(betStr);
            if (person.isValidBet(bet)) {
                person.makeBet(bet);
                betTV.setText(Integer.toString(person.getBet()));
                moneyTV.setText(Integer.toString(person.getMoney()));
                infoTV.setText("Make your move");
                dealCards();
            } else {
                infoTV.setText("Invalid bet: bet must be between 1 and your total money");
            }
        } catch (NumberFormatException e) {
            infoTV.setText("Invalid bet: please enter a whole number");
        }
    }

    public void dealCards() {
        // initial deal
        person.addCard(deck.deal());
        dealer.addCard(deck.deal());
        person.addCard(deck.deal());
        dealer.addCard(deck.deal());

        // show cards
        showPlayerCard(person.getCards().get(0));
        showPlayerCard(person.getCards().get(1));
        showDealerCard(dealer.getFirstCard());

        // ask player for insuranceBet if dealer's first card is Ace
        if (dealer.getCards().get(0).getNumber() == 1) {
            insuranceYesButton.setEnabled(true);
            insuranceYesButton.setVisibility(View.VISIBLE);
            insuranceNoButton.setEnabled(true);
            insuranceNoButton.setVisibility(View.VISIBLE);
        } else {
            // one round until player wins or loses
            List<String> validMoves = new LinkedList<>();
            validMoves.add("stand");
            validMoves.add("hit");
            if (person.canDouble()) {
                validMoves.add("double");
            }
            if (person.canSplit()) {
                validMoves.add("split");
            }
            playRound(validMoves);
        }
    }

    public void playRound(List<String> validMoves) {
        disableButtons();

        // check for blackjacks
        if (person.isBlackjack() && dealer.isBlackjack()) {
            numPlaysLeft = 0;
            infoTV.setText("Player and Dealer both have Blackjack!");
            person.drawRound();
            playAgain();
            return;
        } else if (person.isBlackjack()) {
            numPlaysLeft = 0;
            infoTV.setText("Player has Blackjack!");
            person.winRoundBlackjack();
            playAgain();
            return;
        } else if (dealer.isBlackjack()) {
            numPlaysLeft = 0;
            infoTV.setText("Dealer has Blackjack!");
            person.loseRound();
            playAgain();
            return;
        }

        standButton.setVisibility(View.VISIBLE);
        standButton.setEnabled(true);
        hitButton.setVisibility(View.VISIBLE);
        hitButton.setEnabled(true);

        if (validMoves.contains("double")) {
            doubleButton.setEnabled(true);
            doubleButton.setVisibility(View.VISIBLE);
        }

        if (validMoves.contains("split")) {
            splitButton.setEnabled(true);
            splitButton.setVisibility(View.VISIBLE);
        }
    }

    public void automateDealer() {
        while (dealer.getCardsValue() < 17) {
            dealer.addCard(deck.deal());
        }
        // player wins - dealer bust
        if (dealer.isBust()) {
            if (numPlaysLeft == 0) {
                infoTV.setText("Dealer bust!"); // don't show results until end of all decks
            }
            person.winRound();
        }
        // player wins
        else if (person.getCardsValue() > dealer.getCardsValue()) {
            if (numPlaysLeft == 0) {
                infoTV.setText("Player wins!");
            }
            person.winRound();
        }
        // tie
        else if (person.getCardsValue() == dealer.getCardsValue()) {
            if (numPlaysLeft == 0) {
                infoTV.setText("Draw!");
            }
            person.drawRound();
        }
        // player loses
        else {
            if (numPlaysLeft == 0) {
                infoTV.setText("Dealer wins!");
            }
            person.loseRound();
        }
        // show dealer deck at the end of the round - round is done and can deal again
        if (numPlaysLeft == 0) {
            for (int i = 1; i < dealer.getCards().size(); i++) {
                System.out.println("card " + i + " " + dealer.getCards().get(i));
                showDealerCard(dealer.getCards().get(i));
            }
            dealer.printDeck();
            playAgain();
        }
    }

    public void stand() {
        numPlaysLeft -= 1;
        // automatic dealer moves - dealer stops when cards are 17 or greater
        automateDealer();
    }

    public void hit() {
        Card newCard = deck.deal();
        person.addCard(newCard);
        showPlayerCard(newCard);
        if (person.isBust()) {
            numPlaysLeft -= 1;
            infoTV.setText("Bust!");
            person.loseRound();
            if (numPlaysLeft == 0) {
                dealer.printDeck();
                playAgain();
            }
        } else {
            List<String> valMoves = new LinkedList<>();
            valMoves.add("stand");
            valMoves.add("hit");
            if (person.canSplit()) {
                valMoves.add("split");
            }
            playRound(valMoves);
        }
    }

    public void doubleBet() {
        // double bet
        person.doubleBet();
        betTV.setText("" + person.getBet());

        // add one more card and then dealer moves
        Card newCard = deck.deal();
        person.addCard(newCard);
        showPlayerCard(newCard);

        if (person.isBust()) {
            infoTV.setText("Bust!");
            person.loseRound();
        } else {
            numPlaysLeft -= 1;
            automateDealer();
        }
    }

    public void split() {
        List<Card> splitCards = person.splitCards(); // last card, second last card
        List<String> valMoves = new LinkedList<String>();
        valMoves.add("stand");
        valMoves.add("hit");
        int bet = person.getBet();

        numPlaysLeft += 1;
        person.addCard(splitCards.get(0));
        infoTV.setText("Split hand " + numPiles);
        person.printDeck(); // TODO show card
        playRound(valMoves);

        numPiles += 1;
        person.makeBet(bet);
        person.emptyDeck();
        person.addCard(splitCards.get(1));
        infoTV.setText("Split hand " + numPiles);
        person.printDeck();
        playRound(valMoves);
    }

    public void disableButtons() {
        dealButton.setEnabled(false);
        dealButton.setVisibility(View.GONE);

        betOKButton.setEnabled(false);
        betOKButton.setVisibility(View.GONE);
        betET.setVisibility(View.GONE);

        // insuranceBet
        insuranceNoButton.setEnabled(false);
        insuranceNoButton.setVisibility(View.GONE);
        insuranceYesButton.setEnabled(false);
        insuranceYesButton.setVisibility(View.GONE);

        // play buttons
        standButton.setEnabled(false);
        standButton.setVisibility(View.GONE);
        hitButton.setEnabled(false);
        hitButton.setVisibility(View.GONE);
        doubleButton.setEnabled(false);
        doubleButton.setVisibility(View.GONE);
        splitButton.setEnabled(false);
        splitButton.setVisibility(View.GONE);
    }

    public void playAgain() {
        moneyTV.setText(Integer.toString(person.getMoney()));
        betTV.setText("");
        disableButtons();
        dealButton.setEnabled(true);
        dealButton.setVisibility(View.VISIBLE);
    }
}
