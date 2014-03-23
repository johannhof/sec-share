package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import message.Message;

public class RequestHandler implements Runnable {

    private final Socket socket;
    private final File serverDirectory;

    public RequestHandler(Socket socket, File serverDirectory) {
        System.out.println("New connection");
        this.socket = socket;
        this.serverDirectory = serverDirectory;
        Thread thread = new Thread(this);
        thread.run();
    }

    @Override
    public void run() {
        try {
            boolean listen = true;
            InputStream inputStream = socket.getInputStream();
            while (listen) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message message = (Message) objectInputStream.readObject();

                switch (message.GetOp()) {
                    case EXIT:
                        listen = false;
                        System.out.println("EXIT");
                    default:
                        System.out.println("wtf");
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

}
