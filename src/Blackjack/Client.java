package Blackjack;
import java.io.*;
import java.net.*;

/**
 * Implements the client-side IO & game logic 
 * @author telecom group 13
 */
public class Client {
	public static void main(String argv[]) throws Exception{
		// Welcome text
		System.out.println("\nWelcome to the Telecom BlackJack game!\n");

		// Open a reader to input from the command line
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		// Open a TCP socket to the server, running on port 6789 "localhost" (i.e., this machine)  
		Socket clientSocket = new Socket("localhost", 6789);
	
		// Open readers to send/receive from server
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
		//server sends one if Server.TIMEDELAY or more time has elapsed.. 
		if(inFromServer.read()==0){
			System.out.println("You are too late to join the game.. sorry");
			System.exit(0);
		}	
		
		/*GAME STARTS*/
		
		System.out.print("Please enter your username (\"dealer\" reserved for dealer): ");
		String username = inFromUser.readLine().replace(" ","") + "\n"; //all \n s necessary when using readLine
		outToServer.writeBytes(username + "\n");			
			
		System.out.println("You have been added to the game with username: "+username);	
		
		//TODO add money stuff
		//System.out.print("Enter quantity of chips you want to purchase: ");
		
		//keep listening for any data from server
		String fromServer ="";
		while(true){
			fromServer = inFromServer.readLine();
			
			
			if(fromServer.equalsIgnoreCase("h&s")){ //a request to hit or stay
				
				System.out.print("Hit or Stay? Please write the word: ");
				String command = inFromUser.readLine();
				if((!command.equalsIgnoreCase("hit")&&!command.equalsIgnoreCase("stay"))){
					System.out.println("invalid command. Write hit or stay.");
					continue;
				}		
				else{
					outToServer.writeBytes(command+"\n"); 
					outToServer.flush();
				}
			}
			else{ //not a request for information from the server, simply print what the server is saying
				System.out.println(fromServer);
			}
		}		
				
			// Close the socket
			//clientSocket.close();
	}
}