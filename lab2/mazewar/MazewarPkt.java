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
	public String  broker_host;
	public Integer broker_port;
	
	/* constructor */
	public BrokerLocation(String host, Integer port) {
		this.broker_host = host;
		this.broker_port = port;
	}
	
	/* printable output */
	public String toString() {
		return " HOST: " + broker_host + " PORT: " + broker_port; 
	}
	
}

public class MazewarPkt implements Serializable {

	public static final int UP = 1;
	int event;
	int player;
	
}
