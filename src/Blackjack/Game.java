package Blackjack;
import java.io.File;
import java.util.ArrayList;

/**
 * This class holds the game-level objects (players, deck, etc.)
 * and implements some methods for retrieving the relevant information from these objects
 * @author Telecom group 13
 */
class Game{
	private Deck deck;
	private ArrayList<Player> players;
	
	/**
	 * Simple constructor, initializes players list & deck
	 */
	public Game(){
		players = new ArrayList<Player>();
		this.deck = new Deck();
	}
	
	
	/**
	 * another constructor for loading players from a list of players
	 * @param players
	 */
	public Game(ArrayList<Player> players){
		this.players = players;
	}
	
	/**
	 * another constructor for loading players from a file
	 * @param f
	 */
	public Game(File f){
		//read file & initialize
	}
	
	/**
	 * @return a list of all players
	 */
	public ArrayList<Player> getPlayers(){
		return this.players;
	}
	
	/**
	 * @return a list of all players
	 */
	public ArrayList<Player> getActivePlayers(){
		ArrayList<Player> out = new ArrayList<Player>();
		for(Player p:this.players){
			if(p.isActive())
				out.add(p);
		}
		return out;
	}
	
	/**
	 * @return returns a list of all cards which have been dealt face up
	 */
	public ArrayList<Card> getAllFaceUpCards(){
		ArrayList<Card> faceUpCards = new ArrayList<Card>();
		for(Player p: this.players){
			for(Card c: p.getHand()){
				if(c.getFaceUp())
					faceUpCards.add(c);
			}
		}
		return faceUpCards;
	}
	
	/**
	 * @return returns the dealer player object
	 */
	public Player getDealer(){
		for(Player p: players){
			if(p.isDealer())
				return p;
		}
		return null;
	}
	
	/**
	 * @return next card from the game's deck
	 */
	public synchronized Card getNextCard(){
		return this.deck.getNextCard();
	}

	/**
	 * @param username
	 * @param id
	 * @return retrieves (and sets id) of player with username given if it already exists, else creates one with given username and id 
	 */
	public synchronized Player getPlayer(String username, int id){
		for(Player p: this.players){
			if(p.getUsername().equalsIgnoreCase(username)){ //player already exists!
				p.setActive(true);
				p.setPlayIndex(id);
				if(p.getUsername().equalsIgnoreCase("dealer")){ //just to be sure dealer flag is set
					p.setDealer(true);
				}
				return p;
			}
		}
		Player newPlayer = new Player(username, -1, true); //chipCount = -1, not used yet... set active = true 
		if(username.equalsIgnoreCase("dealer")){
			newPlayer.setDealer(true);
		}
		newPlayer.setPlayIndex(id);
		this.players.add(newPlayer);
		return newPlayer;
	}	
}
