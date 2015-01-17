import java.io.*;
import java.net.*;

public class BrokerExchange {
	
	private enum Commands {ADD, REMOVE, UPDATE};
	
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		Socket brokerSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			/* variables for hostname/port */
			String hostname = "localhost";
			int port = 4444;
			
			if(args.length == 2 ) {
				hostname = args[0];
				port = Integer.parseInt(args[1]);
			} else {
				System.err.println("ERROR: Invalid arguments!");
				System.exit(-1);
			}
			brokerSocket = new Socket(hostname, port);

			out = new ObjectOutputStream(brokerSocket.getOutputStream());
			in = new ObjectInputStream(brokerSocket.getInputStream());

		} catch (UnknownHostException e) {
			System.err.println("ERROR: Don't know where to connect!!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("ERROR: Couldn't get I/O for the connection.");
			System.exit(1);
		}

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		String userInput;
		Commands command = null;

		System.out.print("Enter queries or x for exit:\n> ");
		while ((userInput = stdIn.readLine()) != null
				&& userInput.toLowerCase().indexOf("bye") == -1) {
			/* make a new request packet */
			System.out.println(userInput);
			command = Commands.valueOf(userInput.split(" ")[0].toUpperCase());
			
			BrokerPacket packetToServer = new BrokerPacket();

			switch (command) {
			
			case ADD:
				packetToServer.type = BrokerPacket.EXCHANGE_ADD;
				packetToServer.symbol = userInput.split(" ")[1];
				System.out.println("adding!!! \n");
				
				break;
				
			case UPDATE:
				packetToServer.type = BrokerPacket.EXCHANGE_UPDATE;
				packetToServer.symbol = userInput.split(" ")[1];
				System.out.println("updating!!! \n");
				break;
	
			case REMOVE:
				packetToServer.type = BrokerPacket.EXCHANGE_REMOVE;
				packetToServer.symbol = userInput.split(" ")[1];
				System.out.println("removing!!! \n");
				break;

			}

			out.writeObject(packetToServer);

			/* print server reply */
			BrokerPacket packetFromServer;
			packetFromServer = (BrokerPacket) in.readObject();

			if (packetFromServer.type == BrokerPacket.BROKER_QUOTE)
				System.out.println("Quote from broker: " + packetFromServer.quote);

			/* re-print console prompt */
			System.out.print("> ");
		}

		/* tell server that i'm quitting */
		BrokerPacket packetToServer = new BrokerPacket();
		packetToServer.type = BrokerPacket.BROKER_BYE;
		out.writeObject(packetToServer);

		out.close();
		in.close();
		stdIn.close();
		brokerSocket.close();
	}
}
