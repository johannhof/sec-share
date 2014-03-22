package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkClient {

	Socket clientSocket;
	String userID;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	public NetworkClient(String userID, String serverAddress) {
		// TODO Auto-generated constructor stub
	}

	public void disconnect() {
		// TODO Auto-generated method stub
		
	}

}
