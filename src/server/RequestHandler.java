package server;

import file_services.FileInfo;
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

    public RequestHandler(final Socket socket, final File serverDirectory) {
        System.out.println("New connection");
        this.socket = socket;
        this.serverDirectory = serverDirectory;
        final Thread thread = new Thread(this);
        thread.run();
    }

    private static void respond(final Message message, final OutputStream outputStream) throws IOException {
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    @Override
    public void run() {
        final UserManager userManager = UserManager.getInstance();
        User user = null;
        try {
            boolean listen = true;
            final InputStream inputStream = socket.getInputStream();
            while (listen) {
                final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                final Message message = (Message) objectInputStream.readObject();

                switch (message.GetOp()) {
                    case EXIT:
                        System.out.println("EXIT");
                        listen = false;
                        break;
                    case PUT:
                        System.out.println("PUT");
                        if (user == null) { // if not logged in
                            respond(new Reply(false), socket.getOutputStream());
                            break;
                        }

                        assert message instanceof PutMessage;

                        final PutMessage putMessage = (PutMessage) message;
                        FileInfo fileInfo = user.findFile(putMessage.getFilename());
                        final String filePath = this.serverDirectory + "/" + fileInfo.getOwner() + "/" + putMessage.getFilename();

                        // if the user has a file with this name in his list
                        if (fileInfo != null) {
                            final File file = new File(filePath);

                            // if there is a younger file on the server, don't overwrite
                            if (file.exists() && file.lastModified() > putMessage.getTimestamp()) {
                                respond(new Reply(false), socket.getOutputStream());
                            } else {
                                respond(new Reply(true), socket.getOutputStream());
                                FileOperations.download(inputStream, filePath, putMessage.getFilesize());
                            }
                        } else {
                            // create a new file with the user as owner
                            fileInfo = new FileInfo();
                            fileInfo.setFilename(putMessage.getFilename());
                            fileInfo.setOwner(user.getName());
                            user.addFile(fileInfo);
                            userManager.save();

                            respond(new Reply(true), socket.getOutputStream());
                            FileOperations.download(inputStream, filePath, putMessage.getFilesize());
                        }

                        break;
                    case GET:
                        System.out.println("GET not implemented yet");
                        if (user == null) { // if not logged in
                            respond(new Reply(false), socket.getOutputStream());
                            break;
                        }
                        break;
                    case LIST:
                        System.out.println("LIST not implemented yet");
                        if (user == null) { // if not logged in
                            respond(new Reply(false), socket.getOutputStream());
                            break;
                        }
                        break;
                    case LOGIN:
                        System.out.println("LOGIN");
                        assert message instanceof LoginMessage;

                        final LoginMessage loginMessage = (LoginMessage) message;
                        user = userManager.findOrCreate(loginMessage.getUsername(), loginMessage.getPassword());

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
