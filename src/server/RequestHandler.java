package server;

import file_services.FileOperations;
import message.LoginMessage;
import message.Message;
import message.PutMessage;
import message.Reply;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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

    private static void respond(Message message, OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    @Override
    public void run() {
        UserManager userManager = UserManager.getInstance();
        try {
            boolean listen = true;
            InputStream inputStream = socket.getInputStream();
            while (listen) {
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Message message = (Message) objectInputStream.readObject();

                switch (message.GetOp()) {
                    case EXIT:
                        System.out.println("EXIT");
                        listen = false;
                        break;
                    case PUT:
                        System.out.println("PUT");
                        assert message instanceof PutMessage;

                        PutMessage putMessage = (PutMessage) message;
                        File file = new File(this.serverDirectory, putMessage.getFilename());

                        // if there is a younger file on the server
                        if (file.exists() && file.lastModified() > putMessage.getTimestamp()) {
                            respond(new Reply(false), socket.getOutputStream());
                        } else {
                            respond(new Reply(true), socket.getOutputStream());
                            FileOperations.download(inputStream, putMessage.getFilename(), putMessage.getFilesize());
                        }

                        break;
                    case GET:
                        System.out.println("GET not implemented yet");
                        break;
                    case LIST:
                        System.out.println("LIST not implemented yet");
                        break;
                    case LOGIN:
                        System.out.println("LOGIN");
                        assert message instanceof LoginMessage;

                        LoginMessage loginMessage = (LoginMessage) message;
                        User user = userManager.findOrCreate(loginMessage.getUsername(), loginMessage.getPassword());

                        if (user == null) {
                            respond(new Reply(false), socket.getOutputStream());
                        } else {
                            respond(new Reply(true), socket.getOutputStream());
                        }

                        break;
                    default:
                        System.out.println("wtf");
                }
            }
        } catch (SocketException | EOFException e) {
            System.out.println("Client disconnected");
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

}
