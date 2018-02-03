package Blackjack;
import java.util.ArrayList;
import java.util.Random;

/**
 * Defines a deck as an array of Card objects. implements all deck functionality
 * @author Telecom group 13
 */
class Deck{
	private Card[] cards = new Card[13*4];
	private ArrayList<Card> cardArray = new ArrayList<Card>();
    private Random r = new Random(); //used to return a random card

    /**
     * builds sorted array of Cards, then resets the arrayList used to implement random access / shuffling
     */
    public Deck(){
		int k = 0;
			for(int suit = 0; suit <= 3; suit++) {                                        // for suit
				for(int value = 1; value <= 13; value++) {                                // from Ace to King
						// build new card
						cards[k++] = new Card(value, suit, false); //faceup defaults to false
                        }
                }
		// first time reset
		reset();
	}
	
    /**
     * Reset the Deck of card, put all cards into cardArray
     */
	public void reset() {
			cardArray.clear();
			for(int i = 0; i < cards.length; i++)
					cardArray.add(cards[i]);
	}
   
	/**
	 * @return a random card from cardArray, which holds all cards which are still in the deck / have not been used 
	 */
	public Card getNextCard() {
		if(this.cardArray.size()==0)
			throw new IllegalStateException("No more cards in the deck" );

		//randomely choose an index to grab a card from. 
		return this.cardArray.remove(r.nextInt(cardArray.size())); //nore arraylist remove also shifts
	}
	
	public boolean isEmpty(){
		return this.cardArray.isEmpty();
	}
}
