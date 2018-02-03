/**
 * Simple class to implement a card
 * @author telecom group 13
 */
package Blackjack;
public class Card {
        private int value;               // from 1 to 13 (Ace to King)
        private int suit;    // the 4 suits from 0 to 3
        private boolean faceUp;
        private boolean ace;
        
        /**
         *  build new card with given attributes. set boolean if it's an ace
         * @param value
         * @param suit
         * @param faceUp in blackjack, only the first card for each player is dealt face down. All players should be able to see the faceUp cards
         */
        Card(int value, int suit, boolean faceUp) {
                this.value = value;
                if(value == 1)
                	this.ace = true;
                this.suit = suit;
                this.faceUp = faceUp;
        }
       
        public boolean getFaceUp(){
        	return this.faceUp;
        }
        
        public boolean isAce(){
        	return this.ace;
        }
        
        public int getSuit() {
                return suit;
        }
        //works for non-ace cards
        public int getValue() {
        	if(value>10){
        		this.value = 10;
        	}
        	return this.value;
        }
               
        public void setFaceUp(boolean faceUp){
        	this.faceUp = faceUp;
        }
        
        /**
         * change the integer representation to a standard string format for a card
         * @return string repr of the card
         */
        public String toString() {
        	String valueString="";
        	switch(this.value){
        		case 1:valueString="Ace";
        			break;
        		case 11: valueString="Jack";
        			break;
        		case 12: valueString="Queen";
    				break;
        		case 13: valueString = "King";
        			break;
        		default: valueString=""+value;
        			break;
        	}
        	
        	String suitString="";
        	switch(this.suit){
        	case 0: suitString = "Diamonds";
        		break;
        	case 1: suitString = "Hearts";
        		break;
        	case 2: suitString = "Spades";
        		break;
        	case 3: suitString = "Clubs";
        		break;
        	}
        	
            return valueString + " of " + suitString;
        }
}

