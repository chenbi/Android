/**
 * This is the template file for Assignment 1 Problem 4 for ECSE 414 Fall 2012.
 * 
 * This class implements a multi-threaded HTTP 1.0-compliant web server. The
 * root directory from which files are served is the same directory from which
 * this application is executed. When the server encounters an error, it sends a
 * response message with the appropriate HTML code so that the error information
 * is displayed.  
 * 
 * @author michaelrabbat
 *
 */
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is the main class which runs the loop that listens for incoming requests
 * and spawns new threads to handle each request.
 * 
 * @author michaelrabbat
 * 
 */
public final class WebServer {
	public static void main(String argx[]) throws Exception {
		// Step 1: Set the port number (may not work with 80)
		int port = 6789;
		ServerSocket welcomeSocket = new ServerSocket(port);
		// Create the socket to listen for incoming connections
		
		// Enter an infinite loop and process incoming connections
		// Use Ctrl-C to quit the application
		while (true) {
			System.out.println("waiting for client");
			
			// Listen to client request
			Socket connectionSocket = welcomeSocket.accept();
			
			System.out.println("connection established, creating new thread");
			
			// Create a thread to handle the request
			HttpRequest handler = new HttpRequest(connectionSocket);
			new Thread(handler).start();
		}
	}
}

/**
 * This is the helper class that
 * 
 * @author michaelrabbat
 * 
 */
final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;
	
	/**
	 * Constructor takes the socket for this request
	 */
	public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
	}
	
	/**
	 * Implement the run() method of the Runnable interface. 
	 */
	@Override
	public void run()
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * This is where the action occurs
	 * @throws Exception
	 */
	private void processRequest() throws Exception
	{		
		String question; // Received from client
		String reply; // Reply to client
		// STEP 2a: Parse the HTTP Request message
		// Get a reference to the socket's input and output streams
		
		// Set up input stream filters
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// Open a stream to write to client
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());

		// Process client request 
		// Read client's question
		question = inFromClient.readLine();
		
		System.out.println("from client: " + question);
		
		// Convert the sentence to upper case
		reply = question.toUpperCase();
			
		// Send the sentence back to client

		// Get the request line of the HTTP request message
		String requestLine = question;

		//Display the request line
		System.out.println();

		System.out.println(requestLine);

		// Get and display the header lines


		// (The last part of STEP 2 is at the end of this method)
		// (Close the socket)
		
		// STEP 3a: Prepare and Send the HTTP Response message
		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which we'll assume is "GET"
		String fileName = tokens.nextToken();
				
		// Prepend a "." to the file name so that the file request is in the
		// current directory
		//fileName = "." + fileName;
		System.out.println(fileName.substring(1));
		// Open the requested file
		FileInputStream fis = null;

		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName.substring(1));
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		
		// Construct the response message header
		String statusLine = null;
		String contentTypeLine = null;
		String errorMessage = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>404 Not Found</BODY></HTML>";

		// Fill in the values of statusLine and contentTypeLine based on whether
		// or not the requested file was found

		if(!fileExists)
			outToClient.writeBytes(errorMessage);
		else{
			
		// Send a HTTP response header containing the status line and
		// content-type line. Don't forget to include a blank line after the
		// content-type to signal the end of the header.
			
			outToClient.writeBytes("HTTP/1.0 200 OK Content-Type:  "+contentType(fileName)+"\n\r\n");

		// Send the body of the message (the web object)
		// You may use the sendBytes helper method provided
			sendBytes(fis, outToClient);
	
		}
		// STEP 2b: Close the input/output streams and socket before returning
		outToClient.close();
		fis.close();
		socket.close();
	}
	
	/**
	 * Private method that returns the appropriate MIME-type string based on the
	 * suffix of the appended file
	 * @param fileName
	 * @return
	 */
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".css")) {
			return "text/css";
		}
		if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if (fileName.endsWith(".jpg")) {
			return "image/jpeg";
		}
		else
			return "text/html";
	}

	/**
	 * Private helper method to read the file and send it to the socket
	 * @param fis
	 * @param os
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os) 
		throws Exception 
	{
		// Allocate a 1k buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;
		
		// Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}
}