package client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import message.*;

public class NetworkClient {

	Socket clientSocket;
	String userID;
	ObjectOutputStream outStream;
	ObjectInputStream inStream;
	
	public NetworkClient(String userID, String host, int port) {
		
		this.userID = userID;
		
		try {
			this.clientSocket = new Socket(host, port);
			System.out.println("Connected to " + host + " in port " + port + "\n");

			this.outStream = new ObjectOutputStream(clientSocket.getOutputStream());
			this.outStream.flush();

			this.inStream = new ObjectInputStream(clientSocket.getInputStream());



		} catch (IOException ioExp) {
			System.out.println("Error connecting to " + host + " in port " +port);

			if(this.clientSocket.isBound())
				try {
					this.clientSocket.close();
				} catch (IOException ioExp2) {
					ioExp2.printStackTrace();
				}
			ioExp.printStackTrace();
		}
		
	}
	
//	public boolean login(){
//		//TODO
//	}

	public void disconnect() {
		
		System.out.println("Disconnecting...");

		try {
			outStream.writeObject(new ExitMessage());
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			this.clientSocket.close();
		} catch (IOException ioe3) {
			System.out.println("Error while disconnecting socket");
			ioe3.printStackTrace();
		}
		
	}
	
	
	public Message msgSendReceive(Message msg) {
		
		Message reply = null;
		
		try {
			outStream.writeObject(msg);
			outStream.flush();
			reply = (Message) inStream.readObject();

		} catch (ClassNotFoundException cfn1) {
			cfn1.printStackTrace();

		} catch (IOException ioe1) {
			System.out.println("message exchange failure");
			ioe1.printStackTrace();

		}
		return reply;
	}
	
	public boolean sendFile(File file) {
	//TODO must receive reply and display error message if it fails	
	}
	
	public boolean receiveFile(File clientHome, String filename) {
		//TODO
	}
	
	//might be better to add methods to upload / download several files
	
}
