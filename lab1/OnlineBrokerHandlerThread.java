import java.net.*;
import java.io.*;
import java.util.Scanner;

public class OnlineBrokerHandlerThread extends Thread {
	private Socket socket = null;
	File nasdaq = new File("nasdaq");
	private ObjectOutputStream toClient;

	public OnlineBrokerHandlerThread(Socket socket) {
		super("OnlineBrokerHandlerThread");
		this.socket = socket;
		System.out.println("Created new Thread to handle client");
	}

	public void run() {

		boolean gotByePacket = false;
		
		try {
			/* stream to read from client */
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			BrokerPacket packetFromClient;
			
			/* stream to write back to client */
			toClient = new ObjectOutputStream(socket.getOutputStream());
			

			while (( packetFromClient = (BrokerPacket) fromClient.readObject()) != null) {
				/* create a packet to send reply back to client */
				BrokerPacket packetToClient = new BrokerPacket();
				packetToClient.type = BrokerPacket.BROKER_QUOTE;
				
				/* process message */
				/* just echo in this example */
				if(packetFromClient.type == BrokerPacket.BROKER_REQUEST) {
					// add file parsing here
					packetToClient.quote = getQuote(packetFromClient.symbol); //packetFromClient.quote;
					
					


					// end file parsing
					//System.out.println("From Client: " + packetFromClient.quote);
				
					/* send reply back to client */
					toClient.writeObject(packetToClient);
					
					/* wait for next packet */
					continue;
				}
				
				switch (packetFromClient.type){
				
				case BrokerPacket.EXCHANGE_ADD:
					
					if (getQuote(packetFromClient.symbol) == null) {
						// symbol not yet in file
						addSymbol(packetFromClient.symbol);
						sendMessageToClient(packetToClient, BrokerPacket.EXCHANGE_REPLY, packetFromClient.symbol, 0L);
						
					} else {
						// symbol in file --  needs error handling
					}
				
					continue;
					
				case BrokerPacket.EXCHANGE_REMOVE:
					
					if (getQuote(packetFromClient.symbol) != null) {
						// symbol not yet in file
						removeSymbol(packetFromClient.symbol);
						sendMessageToClient(packetToClient, BrokerPacket.EXCHANGE_REPLY, packetFromClient.symbol, 0L);
						
					} else {
						// symbol not in file --  needs error handling
					}
				
					continue;
				
				}
				
				
				
				/* Sending an BROKER_NULL || BROKER_BYE means quit */
				if (packetFromClient.type == BrokerPacket.BROKER_NULL || packetFromClient.type == BrokerPacket.BROKER_BYE) {
					gotByePacket = true;
					packetToClient = new BrokerPacket();
					packetToClient.type = BrokerPacket.BROKER_BYE;
					//packetToClient.quote = "Bye!";
					toClient.writeObject(packetToClient);
					break;
				}
				
				/* if code comes here, there is an error in the packet */
				System.err.println("ERROR: Unknown BROKER_* packet!!");
				System.exit(-1);
			}
			
			/* cleanup when client exits */
			fromClient.close();
			toClient.close();
			socket.close();

		} catch (IOException e) {
			if(!gotByePacket)
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
			if(!gotByePacket)
				e.printStackTrace();
		}
	}
	
	private void removeSymbol(String symbol) {
		// TO BE IMPLEMENTED
		
	}

	private void sendMessageToClient(BrokerPacket packetToClient, int exchangeReply, String symbol, long l) throws IOException {
		packetToClient.type = exchangeReply;
		packetToClient.symbol = symbol;
		packetToClient.quote = l;
		toClient.writeObject(packetToClient);
		
	}

	private void addSymbol(String symbol) {
		try
		{
		    FileWriter fw = new FileWriter("nasdaq",true); //the true will append the new data
		    fw.write(symbol.concat(" 0\n"));//appends the string to the file
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}

	private Long getQuote(String symbol) {
		/* helper method to get the quote given a symbol from file*/
		try {

			Scanner sc = new Scanner(nasdaq);
			String tmp;
			Long num, quote = null;

			while (sc.hasNext()) {
    			tmp = sc.next();
				num = sc.nextLong();
				if (tmp.equals(symbol)) {
					quote=num;
				}
			}
			sc.close();
			return quote;
		} 
		catch (FileNotFoundException e) {
				e.printStackTrace();
		}
		return null;
	}
}
