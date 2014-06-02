package server;

import external.CertificationAuthority;
import file_services.CryptoHelper;
import file_services.FileInfo;
import file_services.SharedFile;
import message.*;

import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.*;
import java.security.cert.CertificateException;
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
            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(new FileInputStream(root.getAbsolutePath() + "/truststore.jks"), "trusted".toCharArray());
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
                        final Cipher cipher = Cipher.getInstance("AES");
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
                                file.download(inputStream, putMessage.getFilesize(), new CryptoHelper() {
                                    @Override
                                    public byte[] transform(byte[] bytes, boolean last) {
                                        return cipher.update(bytes);
                                    }
                                });

                                // loop through user.key files and replace with new version

                                final File keyFiles = new File(keyDir + "/" + user.getName(),
                                        file.getName() + "." + user.getName() + ".key").getParentFile();

                                for (final File keyFile : keyFiles.listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.startsWith(file.getName());
                                    }
                                })) {
                                    try {
                                        // ugly regex splitting to filter out user name
                                        final String name = keyFile.getName().split(file.getName() + ".")[1].split(".key")[0];

                                        final Cipher rsa_cipher = Cipher.getInstance("RSA");
                                        final PublicKey publicKey = trustStore.getCertificate(name).getPublicKey();
                                        rsa_cipher.init(Cipher.WRAP_MODE, publicKey);
                                        final byte[] wrap = rsa_cipher.wrap(secretKey);

                                        keyFile.getParentFile().mkdirs(); // create all parent dirs

                                        final FileOutputStream outputStream = new FileOutputStream(keyFile);
                                        outputStream.write(wrap);
                                        outputStream.flush();
                                        outputStream.close();
                                    } catch (KeyStoreException | IllegalBlockSizeException | InvalidKeyException e) {
                                        e.printStackTrace();
                                    }
                                }

                                file.setLastModified(putMessage.getTimestamp());
                            }
                        } else { // file doesnt exist
                            final SharedFile file = new SharedFile(this.storageDir + "/" + user.getName(), putMessage.getFilename());

                            respond(new Reply(true), objectOutputStream);

                            file.download(inputStream, putMessage.getFilesize(), new CryptoHelper() {
                                @Override
                                public byte[] transform(byte[] bytes, boolean last) {
                                    if (last) {
                                        try {
                                            return cipher.doFinal(bytes);
                                        } catch (IllegalBlockSizeException | BadPaddingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return cipher.update(bytes);
                                }
                            });

                            try {
                                final PublicKey publicKey = trustStore.getCertificate(user.getName()).getPublicKey();
                                final Cipher rsa_cipher = Cipher.getInstance("RSA");
                                rsa_cipher.init(Cipher.WRAP_MODE, publicKey);
                                final byte[] wrap = rsa_cipher.wrap(secretKey);

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
                            final SharedFile keyFile = new SharedFile(this.keyDir + "/" + fileInfo.getOwner(),
                                    fileInfo.getFilename() + "." + user.getName() + ".key");

                            if (!file.exists() || file.isDirectory()) {
                                respond(new Reply(false, "Could not find file"), objectOutputStream);
                            } else {

                                BufferedInputStream bis = null;
                                try {
                                    final byte[] keybytes = new byte[(int) keyFile.length()];
                                    bis = new BufferedInputStream(new FileInputStream(keyFile));
                                    bis.read(keybytes, 0, keybytes.length);

                                    respond(new DownloadReply(true, file.length(), file.lastModified(), keybytes), objectOutputStream);
                                    file.upload(socket.getOutputStream());
                                } catch (final IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (bis != null) {
                                            bis.close();
                                        }
                                    } catch (final IOException e) {
                                        e.printStackTrace();
                                    }
                                }

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

                        final File keyFile = new File(this.keyDir + "/" + fileInfo.getOwner(),
                                fileInfo.getFilename() + "." + targetUser.getName() + ".key");

                        keyFile.createNewFile();

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

                            try {
                                if (CertificationAuthority.getInstance().verify(loginMessage.getSignature())) {
                                    trustStore.setCertificateEntry(user.getName(), loginMessage.getCertificate());
                                    trustStore.store(new FileOutputStream(root.getAbsolutePath() + "/truststore.jks"), "trusted".toCharArray());
                                } else {
                                    respond(new Reply(false), objectOutputStream);
                                }
                            } catch (SignatureException e) {
                                e.printStackTrace();
                            }
                        }

                        continue;

                    default:
                        System.out.println("wtf");
                }
            }
        } catch (SocketException | EOFException e) {
            System.out.println("Client disconnected");
        } catch (ClassNotFoundException
                | IOException
                | NoSuchAlgorithmException
                | NoSuchPaddingException
                | KeyStoreException
                | CertificateException e) {
            e.printStackTrace();
        }
    }

}
