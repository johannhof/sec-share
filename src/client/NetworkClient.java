package client;

import message.ExitMessage;
import message.Message;
import message.PutMessage;
import message.Reply;

import java.io.*;
import java.net.Socket;

public class NetworkClient {

    Socket clientSocket;
    String userID;
    OutputStream outStream;
    InputStream inStream;

    public NetworkClient(String userID, String host, int port) {

        this.userID = userID;

        try {
            this.clientSocket = new Socket(host, port);
            System.out.println("Connected to " + host + " in port " + port + "\n");

            this.outStream = clientSocket.getOutputStream();
            this.outStream.flush();

            this.inStream = clientSocket.getInputStream();

        } catch (IOException ioExp) {
            System.out.println("Error connecting to " + host + " in port " + port);

            if (this.clientSocket.isBound())
                try {
                    this.clientSocket.close();
                } catch (IOException ioExp2) {
                    ioExp2.printStackTrace();
                }
            ioExp.printStackTrace();
        }

    }

    public boolean login(String password) {
        //TODO
        return true;
    }

    public void disconnect() {

        System.out.println("Disconnecting...");

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(new ExitMessage());
            objectOutputStream.flush();
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
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(msg);
            objectOutputStream.flush();
            ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
            reply = (Message) objectInputStream.readObject();

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return reply;
    }

    public boolean sendFile(File file) {
        //TODO must receive reply and display error message if it fails

        PutMessage putMessage = new PutMessage(file.getName(), file.lastModified());

        Reply reply = (Reply) msgSendReceive(putMessage);

        // TODO isResult is probably not meant for this, right?
        if (reply.isResult()) {
            System.out.println("Wow!");
        }

        return true;
    }

    public boolean receiveFile(File clientHome, String filename) {
        //TODO
        return true;
    }

    //might be better to add methods to upload / download several files

}
