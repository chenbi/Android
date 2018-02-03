package Blackjack;
import java.util.ArrayList;

/**
 * Holds everything needed to define a player & defines methods to access this info
 * @author Telecom group 13
 *
 */
class Player{
	private String username;
	private int chipCount; //not implemented yet..
	private boolean active;
	private ArrayList<Card> hand;
	private int playIndex;
	private boolean isDealer;
	
	/**
	 * Straight-forward constructor. Note: isDealer is false by default
	 * @param username
	 * @param chipCount
	 * @param active
	 */
	public Player(String username, int chipCount, boolean active){
		this.username= username;
		this.chipCount=chipCount;
		this.active = active;
		this.hand = new ArrayList<Card>();
		this.isDealer=false;
	}
	
	/*SETTERS*/
	
	public void setDealer(boolean dealer){
		this.isDealer=dealer;
	}

	public void setActive(boolean active){
		this.active = active;	
	}	
	
	public void setChipCount(int chips){
		this.chipCount = chips;
	}
	
	/**
	 * playIndex is used to make players play in order (of arrival to game)
	 * @param id
	 */
	public void setPlayIndex(int id){
		this.playIndex=id;
	}
	
	
	public boolean isDealer(){
		return this.isDealer;
	}
	
	/**
	 * @param card that is added to player's hand
	 */
	public void addToHand(Card card){
		this.hand.add(card);
	}
	
	/*GETTERS*/
	
	/**
	 * @return true if player has hand
	 */
	public boolean hasHand(){
		return !this.hand.isEmpty();
	}
	
	/**
	 * @return A string showing this player's faceUp cards (belong to his hand)
	 */
	public String getPlayersFaceUpCardsString(){
		String out = "\tPlayer "+this.username +" has faceUp card(s): \n";
		for(Card c: this.hand){
			if(c.getFaceUp())
				out+="\t\t"+c+"\n"; 
		}
		return out;
	}
	
	public int getPlayIndex(){
		return this.playIndex;
	}
	
	/**
	 * @return an arraylist of integers containing all possible total values for this player's hand 
	 * (multiple combinations to consider when he/she has an ace)
	 */
	public ArrayList<Integer> getPossibleHandValues(){
		ArrayList<Integer> totals = new ArrayList<Integer>();
		int noAcesTotal = 0, aceCount = 0;
		//get "base" noAcesTotal & a count of the # of aces
		for(Card c: this.hand){
			if(!c.isAce()){
				noAcesTotal+=c.getValue();
			}
			if(c.isAce()){
				aceCount++;
			}
		}

		//no aces, total is simply sum of all face values. totals holds 1 integer in this case
		if(aceCount==0){ 
			totals.add(noAcesTotal);
			return totals;
		}
		
		//Math.pow(aceCount, 2 is the number of combinations of values
		for(int i=0; i<=(int)Math.pow(aceCount, 2); i++){
			//each loop changes one ace to be 11
			totals.add(noAcesTotal+aceCount+10*i); //aceCount is total when all are considered as one
		}
		
		return totals;
	}
	
	/**
	 * @return string representation of output of getPossibleHandValues()
	 */
	public String getPossibleHandValuesString(){
		ArrayList<Integer> values = this.getPossibleHandValues();
		String out="\tYour possible card total(s) is/are: ";
		for(Integer i:values){
			out+=(i+",\t");
		}
		out+="\n";
		return out;
	}

	/**
	 * @return best (as defined by blackjack rules) total from player's hands
	 */
	public int getBestHandTotal(){
		ArrayList<Integer> totals = this.getPossibleHandValues();
		int best=0;
		for(Integer t:totals){
			if(t>=best && t<=21) //greatest possible, but no greater than 21
				best=t;
		}
		//will return 0 if player is bust..
		return best;
	}
	
	/**
	 * @return string repr of player's hand
	 */
	public String getPlayerHandString(){
		String out="\n**********OUTCOME**********\nPlayer "+this.username+"'s final cards are: \n";
		for(Card c:this.hand){
			out+="\t"+c+"\n";
		}
		return out;
	}

	public ArrayList<Card> getHand(){
		return this.hand;
	}
	
	public int getChipCount(){
		return this.chipCount;
	}
	
	public boolean isActive(){
		return this.active;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String toString(){ //for debugging mostly
		return "Username: "+this.username+" chipCount = "+this.chipCount+" active = "+this.active;
	}
}