import java.io.Serializable;
import java.util.ArrayList;

public class ClientInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String message = ""; // client's message
	ArrayList<Integer> clientsActive = new ArrayList<Integer>();
	ArrayList<Integer> recievers = new ArrayList<Integer>();
	Boolean updateActiveClientList = false;
	Boolean privateChat = false;

}
