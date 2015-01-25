import java.io.Serializable;
 /**
 * MazewarPkt
 * ============
 * 
 * Packet format of the packets exchanged between the Broker and the Client
 * 
 */


/* inline class to describe host/port combo */
class ServerLocation implements Serializable {
	public String  server_host;
	public Integer server_port;
	
	/* constructor */
	public ServerLocation(String host, Integer port) {
		this.server_host = host;
		this.server_port = port;
	}
	
	/* printable output */
	public String toString() {
		return " HOST: " + server_host + " PORT: " + server_port; 
	}
	
}

public class MazewarPkt implements Serializable {

	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int FIRE = 5;
	public static final int QUIT = 6;
	
	int event;
	String player;
	
}
