import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MazeServer {
    private static final int MaxUsers = 1000;
    private static final int Q_SIZE = 1000;
    private static final int MAXTHREAD = 30;
	static ExecutorService executer = Executors.newFixedThreadPool(MAXTHREAD);

	public static ObjectInputStream fromClient;
    public static ObjectOutputStream toClient;
    public static ArrayList<String> clients = new ArrayList<String>();
    static ServerSocket serverSocket = null;
    static boolean listening = true;
    

    public static ArrayBlockingQueue<MazewarPkt> actionRequests = new ArrayBlockingQueue<MazewarPkt>(Q_SIZE);
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        // create a handler for the action requests
        
      
        try {
        	if(args.length == 1) {
        		serverSocket = new ServerSocket(Integer.parseInt(args[0]));
 
        	} else {
        		System.err.println("ERROR: Invalid arguments!");
        		System.exit(-1);
        	}
        } catch (IOException e) {
            System.err.println("ERROR: Could not listen on port!");
            System.exit(-1);
        }

        executer.execute(new RequestHandler());
        executer.execute(new ActionHandler());
     

    }
    
    
    private static class ActionHandler implements Runnable {

		@Override
		public void run() {
			
		}
    	
    
    }
    
    private static class RequestHandler implements Runnable {

		@Override
		public void run() {
			
	        while (listening) {
				try {
		        	Socket inpacket = null;
					inpacket = serverSocket.accept();
					fromClient = new ObjectInputStream(inpacket.getInputStream());
		            toClient = new ObjectOutputStream(inpacket.getOutputStream());
		            MazewarPkt packet = (MazewarPkt) fromClient.readObject();


		            if (packet.event == MazewarPkt.CONNECT){
		            	if (clients.contains(packet.player)){
		            		// HANDLE player exists
		            	} else {
		            		clients.add(packet.player);
		            	}
		            } else if (packet.event == MazewarPkt.DISCONNECT){
		            	if (clients.contains(packet.player)){
		            		clients.remove(clients.lastIndexOf(packet.player));
		            		if (clients.isEmpty()) {
		            			// ALL clients have disconnected
		            			executer.shutdown();
		            			serverSocket.close();
		            		}
		            	} else {
		            		// HANDLE no player to disconnect
		            	}
		            } else {
		            	// ACTION
		            	actionRequests.add(packet);
		            	
		            }        

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		}
    	
    }
}
