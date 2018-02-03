package Blackjack;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Implements server-side IO and game logic for blackjack game
 * @author telecom group 13
 *
 */
public class Server {
	
	protected static Game game = null; 
	public static long startTime;
	public static final int TIMEDELAY = 30;
	public static final int TIMERPERIOD = 5;
	public static int turnIndex=0;
	public static int maxID=0;
	
	public static void main(String[] args) {
		game = new Game();
		
		//used to display of how long before game commences
		Timer timer = new Timer("Printer");
		MyTask t = new MyTask();
        timer.schedule(t, 0, TIMERPERIOD*1000);
		
		System.out.println("Waiting for players..");
		
		try {
			startTime = System.currentTimeMillis() ;
			ServerSocket serverSocket = new ServerSocket(6789);		
			
			int id=0, maxID=0;
			while (true){	

				Socket connectionSocket = serverSocket.accept();
				
				System.out.println("Connection established, Creating new thread #"+id);
				
				//Client connected on time
				if(elapsedTime()<TIMEDELAY){ 
					Connection handler = new Connection(connectionSocket, id, true);
					new Thread(handler).start();
					id++;
					maxID++;
				}
				else{ //too late
					Connection handler = new Connection(connectionSocket, id, false);
					new Thread(handler).start();		
				}
			}
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}	
		
	}	
	
	/**
	 * @return elapsed time, in seconds, since server was started
	 */
	public static int elapsedTime(){
		return (int)(System.currentTimeMillis()-startTime) / 1000;
	}
	
	/**
	 * class instantiated for each client. Extends Server to have access to the some important object (including game)
	 * @author telecom group 13
	 */
	static class Connection extends Server implements Runnable{
		
		private Socket connectionSocket;
		private DataOutputStream out = null;
		private BufferedReader in = null;
		private int id;
		private Player player = null;
		private Boolean onTime;
	
		/**
		 * Constructor
		 * @param connectionSocket
		 * @param id
		 * @param onTime
		 * @throws Exception
		 */
		public Connection (Socket connectionSocket, int id, boolean onTime) throws Exception{			
			this.connectionSocket = connectionSocket;
			this.id = id;
			
			out = new DataOutputStream(connectionSocket.getOutputStream());//send to client
			in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			this.onTime = onTime; 
		}
		
		/**
		 * Server-side thread servicing each client
		 */
		@Override
		public void run(){
			try{			
				if(!this.onTime){ // not on time
					out.write(0);out.flush(); //tells client he/she is too late
				}
				else{
					out.write(1);out.flush();
					//ask for username 
					
					String un = in.readLine();
					
					//fetch or make new player, set it to active
					this.player = game.getPlayer(un, id);
					this.player.setActive(true);
					
					//add hand to player. Player dealt a hand as soon as he/she enters game
					Card card1 = game.getNextCard();
					if(un.equalsIgnoreCase("dealer")){
						card1.setFaceUp(false);
					}
					else{
						card1.setFaceUp(true);
					}
					this.player.addToHand(card1);
					Card card2 = game.getNextCard();
					card2.setFaceUp(true); //second hand is face up
					player.addToHand(card2);
					
					//tell player what his hand is
					out.writeBytes("Your hand is: "+"\n");out.flush();
					out.writeBytes("\t"+card1 +"\n");out.flush();
					out.writeBytes("\t"+card2+"\n");out.flush();
					out.writeBytes(this.player.getPossibleHandValuesString());out.flush();
					System.out.println("\t"+un+"'s hand: "+card1+" "+card2);
					System.out.println(this.player.getPossibleHandValuesString());
					
					//wait for TIMEDELAY to have elapsed. At TIMEDELAY, we are sure that all hands have been dealt
					out.writeBytes("Waiting for all hands to be dealt..\n");out.flush();
					while(elapsedTime()<TIMEDELAY);
					
					out.writeBytes("Waiting for other people to play..\n");
					out.flush();
					
					//the players have to play in order. turnIndex defines the index of the player who can play
					//to be compared to the playIndex of each player, which is set to id when they join the game (see Game.getPlayer()) 
					//synchronized(this){
						while(turnIndex!=this.player.getPlayIndex()){ //breaks when current player can start playing 
							Thread.sleep(50);
						}
					//}
					
					if(un.equalsIgnoreCase("dealer")){
						while(this.player.getBestHandTotal()<17 && this.player.getBestHandTotal()!=0){
							Card c = game.getNextCard();
							c.setFaceUp(false);
							this.player.addToHand(c);
							out.writeBytes("\tYou have been assigned card: "+c+"\n");
							out.writeBytes(this.player.getPossibleHandValuesString());
							System.out.println("dealer's hand: "+c+" ");
						}
						out.writeBytes("\n");
					}
					else{
						//before the guy plays, show him all faceup cards, which will impact his decision
						out.writeBytes("----SHOWING ALL FACEUP CARDS ---- \n");
						for(Player p: game.getPlayers()){
							out.writeBytes(p.getPlayersFaceUpCardsString());out.flush();
						}
						
						out.writeBytes("h&s\n"); //signal to request for a hit or a stay command
						out.flush();
						
						String response = in.readLine();
						
						while(!response.equalsIgnoreCase("hit")&&!response.equalsIgnoreCase("stay")){
							response = in.readLine(); //keep listening until we get hit or stay. client side keeps prompting. 
						}
						
						while(response.equalsIgnoreCase("hit")){						
							Card newCard = game.getNextCard();
							newCard.setFaceUp(true); //all subsequent cards are face up
							this.player.addToHand(newCard);
							out.writeBytes(newCard+"\n");out.flush(); //tell player of his card
							out.writeBytes(this.player.getPossibleHandValuesString());out.flush(); //tell player of possible totals
							System.out.println("\t"+un+"decided to hit! What an adventurous fellow!");
							System.out.println("\t"+un+"'s new card "+newCard+"\n");
							System.out.println(this.player.getPossibleHandValuesString());
							if(this.player.getBestHandTotal()==0){
								out.writeBytes("BUST! You idiot..\n");
								System.out.println("BUST! You idiot..");
								break;
							}
							
							out.writeBytes("h&s\n");out.flush(); //request another command
							response = in.readLine();
						}//breaks if user chooses to stay
					}
					
					System.out.println("Player "+un+"is done playing..");
					
					this.player.setActive(false); //player is done playing. 
					turnIndex++; //next player's turn to play
					
					synchronized(this){ //when player is done, must nevertheless wait for other players to be done
						while(true){
							if(game.getActivePlayers().isEmpty()) //no more active players, everyone has played
								break;
							Thread.sleep(50);
						}
					}
										
					Player dealer = game.getDealer(); //get dealer to compare dealer score with player scores
					if(!this.player.isDealer()){
						if(dealer==null){
							out.writeBytes("dealer inexistant..\n");out.flush();
							System.out.println("dealer inexistant...");
						}
						else{
							//print, both on client & server, the hands & values of dealer & current player (whose thread we are in)
							out.writeBytes(this.player.getPlayerHandString());out.flush();
							System.out.println(this.player.getPlayerHandString());
							out.writeBytes(dealer.getPlayerHandString());out.flush();
							System.out.println(dealer.getPlayerHandString());
							out.writeBytes("Your best hand total is: "+this.player.getBestHandTotal()+" and dealer's is: "+dealer.getBestHandTotal());out.flush();
							System.out.println("Player "+this.player.getUsername()+"'s best hand total is: "+this.player.getBestHandTotal()+" and dealer's is: "+dealer.getBestHandTotal());
							
							if(dealer.getBestHandTotal()<this.player.getBestHandTotal()){
								out.writeBytes("\n\n!!!!!!!!!!!!!!!You have won!!!!!!!!!!!\n");out.flush();
								System.out.println("\n***Player "+un+" has won. All drinks on him/her***");
							}
							else if(dealer.getBestHandTotal()==this.player.getBestHandTotal()){
								out.writeBytes("\nyou tied..\n");out.flush();
								System.out.println("***game was a tie. How unexciting.***");
							}
							else{
								out.writeBytes("\n***you lost miserably..***\n");out.flush();
								System.out.println("***Dealer won.***\n"+un+" will need to get a line of credit to fuel his/her addiction***");
							}
						}
					}
				}
			}
			catch(Exception e){
			}
		}			
	} //closes Connection
} //closes Server
	
/**
 * simple timer thread to print time left before server stops accepting new players
 * @author Telecom group 13
 *
 */
class MyTask extends TimerTask {
    private int times = 0;
    public void run() {
        if (times <= Server.TIMEDELAY/Server.TIMERPERIOD) {
            System.out.println("ALERT: Less than "+(Server.TIMEDELAY-times*Server.TIMERPERIOD)+" seconds left to join game..");
        } else {
            System.out.println("ALERT: Too late to join game.. ");
 
            //Stop Timer.
            this.cancel();
        }
        times++;
    }
}