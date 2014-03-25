package server;

import file_services.FileInfo;
import file_services.FileOperations;
import message.*;

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

    private static void respond(final Message message, final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }

    @Override
    public void run() {
        final UserManager userManager = UserManager.getInstance();
        User user = null;
        try {
            final InputStream inputStream = socket.getInputStream();
            while (true) {
                final ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                final Message message = (Message) objectInputStream.readObject();

                switch (message.GetOp()) {
                    case EXIT:
                        System.out.println("EXIT");
                        return;
                    case PUT:
                        System.out.println("PUT");
                        if (user == null) { // if not logged in
                            respond(new Reply(false, "Please log in first"), objectOutputStream);
                            continue;
                        }

                        assert message instanceof PutMessage;

                        final PutMessage putMessage = (PutMessage) message;
                        FileInfo fileInfo = user.findFile(putMessage.getFilename());

                        // if the user has a file with this name in his list
                        if (fileInfo != null) {
                            final File file = new File(this.serverDirectory + "/" + fileInfo.getOwner(), putMessage.getFilename());

                            // if it's a directory or there is a younger file on the server, don't overwrite
                            if (file.isDirectory() || (file.exists() && file.lastModified() > putMessage.getTimestamp())) {
                                respond(new Reply(false, "Could not write file"), objectOutputStream);
                            } else {
                                respond(new Reply(true), objectOutputStream);
                                FileOperations.download(file, inputStream, putMessage.getFilesize());
                            }
                        } else {
                            respond(new Reply(true), objectOutputStream);

                            final File file = new File(this.serverDirectory + "/" + user.getName(), putMessage.getFilename());

                            // make sure that the path exists
                            file.getParentFile().mkdirs();

                            FileOperations.download(
                                    file,
                                    inputStream,
                                    putMessage.getFilesize()
                            );

                            // create a new fileinfo with the user as owner
                            fileInfo = new FileInfo();
                            fileInfo.setFilename(putMessage.getFilename());
                            fileInfo.setOwner(user.getName());
                            user.addFile(fileInfo);

                            // write data to xml
                            userManager.save();
                        }

                        continue;
                    case GET:
                        System.out.println("GET");
                        if (user == null) { // if not logged in
                            respond(new Reply(false, "Please log in first"), objectOutputStream);
                            continue;
                        }

                        final GetMessage getMessage = (GetMessage) message;
                        fileInfo = user.findFile(getMessage.getFilename());

                        if (fileInfo == null) { // user doesn't have that file
                            respond(new Reply(false, "Could not find file"), objectOutputStream);
                        } else {
                            final File file = new File(this.serverDirectory + "/" + fileInfo.getOwner(), fileInfo.getFilename());

                            if (!file.exists() || file.isDirectory()) {
                                respond(new Reply(false, "Could not find file"), objectOutputStream);
                            } else {
                                respond(new Reply(true, file.length()), objectOutputStream);
                                FileOperations.upload(file, socket.getOutputStream());
                            }
                        }

                        continue;
                    case LIST:
                        System.out.println("LIST not implemented yet");
                        if (user == null) { // if not logged in
                            respond(new Reply(false, "Please log in first"), objectOutputStream);
                            continue;
                        }
                        continue;
                    case LOGIN:
                        System.out.println("LOGIN");
                        assert message instanceof LoginMessage;

                        final LoginMessage loginMessage = (LoginMessage) message;
                        user = userManager.findOrCreate(loginMessage.getUsername(), loginMessage.getPassword());

                        if (user == null) {
                            respond(new Reply(false), objectOutputStream);
                        } else {
                            respond(new Reply(true), objectOutputStream);
                        }

                        continue;
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
