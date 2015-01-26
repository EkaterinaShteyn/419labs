
import java.io.Serializable;
 /**
 * MazewarPkt
 * ============
 * 
 * Packet format of the packets exchanged between the Broker and the Client
 * 
 */



public class MazewarPkt implements Serializable {

	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int FIRE = 5;
	public static final int QUIT = 6;
	public static final int CONNECT = 7;
	public static final int DISCONNECT = 8;

	
	int event;
	String player;
	
}
