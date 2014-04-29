package server;

import file_services.FileInfo;
import file_services.SharedFile;
import message.*;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.*;
import java.util.List;

public class RequestHandler implements Runnable {

    private final Socket socket;
    private final File storageDir;
    private final File root;
    private final File adminDir;
    private final File certDir;
    private final File keyDir;

    public RequestHandler(final Socket socket, final File root) {
        System.out.println("New connection");
        this.socket = socket;
        this.root = root;
        this.adminDir = new File(root, "administration");
        this.certDir = new File(adminDir, "certificates");
        this.keyDir = new File(adminDir, "keys");
        this.storageDir = new File(root, "storage");
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
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
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

                        // init key generation and encryption
                        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                        keyGenerator.init(128, new SecureRandom());
                        final SecretKey secretKey = keyGenerator.generateKey();
                        try {
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                        } catch (final InvalidKeyException e) {
                            e.printStackTrace();
                        }

                        // if the user has a file with this name in his list
                        if (fileInfo != null) {
                            final SharedFile file = new SharedFile(this.storageDir + "/" + fileInfo.getOwner(), putMessage.getFilename());

                            // it's a directory
                            if (file.isDirectory()) {
                                respond(new Reply(false, "Can not write directories - " + putMessage.getFilename()), objectOutputStream);
                            }

                            // if there is a younger file on the server, don't overwrite
                            if ((file.exists() && file.lastModified() > putMessage.getTimestamp())) {
                                respond(new Reply(false,
                                        "Could not write file " + putMessage.getFilename() + ", there is a more recent version on the server"
                                ), objectOutputStream);
                            } else {
                                respond(new Reply(true), objectOutputStream);
                                file.download(inputStream, putMessage.getFilesize(), (bytes, last) -> {
                                    if (last) {
                                        try {
                                            return cipher.doFinal(bytes);
                                        } catch (IllegalBlockSizeException | BadPaddingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return cipher.update(bytes);
                                });

                                // loop through user.key files and replace with new version

                                file.listFiles((dir, name) -> name.matches("asdkfjn"));

                                file.setLastModified(putMessage.getTimestamp());
                            }
                        } else { // file doesnt exist
                            final SharedFile file = new SharedFile(this.storageDir + "/" + user.getName(), putMessage.getFilename());

                            respond(new Reply(true), objectOutputStream);

                            file.download(inputStream, putMessage.getFilesize(), (bytes, last) -> {
                                if (last) {
                                    try {
                                        return cipher.doFinal(bytes);
                                    } catch (IllegalBlockSizeException | BadPaddingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return cipher.update(bytes);
                            });

                            try {
                                final PublicKey publicKey = user.getPublicKey();
                                cipher.init(Cipher.WRAP_MODE, publicKey);
                                final byte[] wrap = cipher.wrap(secretKey);

                                final File keyFile = new File(keyDir + "/" + user.getName(),
                                        file.getName() + "." + user.getName() + ".key");
                                keyFile.getParentFile().mkdirs(); // create all parent dirs

                                final FileOutputStream outputStream = new FileOutputStream(keyFile);
                                outputStream.write(wrap);
                                outputStream.flush();
                                outputStream.close();
                            } catch (KeyStoreException | IllegalBlockSizeException | InvalidKeyException e) {
                                e.printStackTrace();
                            }

                            file.setLastModified(putMessage.getTimestamp());

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
                            final SharedFile file = new SharedFile(this.storageDir + "/" + fileInfo.getOwner(), fileInfo.getFilename());

                            if (!file.exists() || file.isDirectory()) {
                                respond(new Reply(false, "Could not find file"), objectOutputStream);
                            } else {
                                respond(new DownloadReply(true, file.length(), file.lastModified()), objectOutputStream);
                                file.upload(socket.getOutputStream());
                            }
                        }

                        continue;

                    case LIST:
                        System.out.println("LIST");
                        if (user == null) { // if not logged in
                            respond(new Reply(false, "Please log in first"), objectOutputStream);
                            continue;
                        }

                        final List<FileInfo> userFiles = user.getFiles();

                        for (final FileInfo fi : userFiles) {
                            final SharedFile file = new SharedFile(this.storageDir + "/" + fi.getOwner(), fi.getFilename());
                            assert file.exists();
                            fi.setLastModified(file.lastModified());
                        }

                        respond(new ListMessage(userFiles), objectOutputStream);

                        continue;

                    case SHARE:
                        System.out.println("SHARE");
                        if (user == null) { // if not logged in
                            respond(new Reply(false, "Please log in first"), objectOutputStream);
                            continue;
                        }

                        final ShareMessage shareMessage = (ShareMessage) message;
                        final User targetUser = userManager.find(shareMessage.getTargetUser());

                        if (targetUser == null) {
                            respond(new Reply(false, "This user does not exist"), objectOutputStream);
                            continue;
                        }

                        if (targetUser.findFile(shareMessage.getFilename()) != null) {
                            respond(new Reply(false, "User already has that file!"), objectOutputStream);
                            continue;
                        }

                        fileInfo = user.findFile(shareMessage.getFilename());
                        if (fileInfo == null) {
                            respond(new Reply(false, "Could not find file, maybe you need to upload it first"), objectOutputStream);
                            continue;
                        }

                        targetUser.getFiles().add(fileInfo);
                        userManager.save();

                        respond(new Reply(true), objectOutputStream);

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
        } catch (ClassNotFoundException | IOException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

}
