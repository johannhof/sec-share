package client;

import file_services.SharedFile;
import message.*;

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

    public boolean login(String username, String password) {
        Reply reply = (Reply) msgSendReceive(new LoginMessage(username, password));
        return reply.isResult();
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

    /**
     * Requests a PUT on the server and then sends the specified file.
     *
     * @param file the file to send
     * @return a boolean indicating if the file was uploaded
     */
    public boolean sendFile(SharedFile file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("Cannot find file to send");
        }

        // request a download on the server
        Reply reply = (Reply) msgSendReceive(new PutMessage(file));

        // TODO isResult is probably not meant for this, right?
        // if the server allows the upload
        if (reply.isResult()) {
            file.upload(outStream);
            return true;
        }

        // TODO warn that file is out of date
        return false;

    }

    public boolean receiveFile(File clientHome, String filename) {
        //TODO
        return true;
    }

    //might be better to add methods to upload / download several files

}
