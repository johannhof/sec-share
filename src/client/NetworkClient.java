package client;

import external.CertificationAuthority;
import file_services.SharedFile;
import message.*;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

public class NetworkClient {

    private final String userID;
    private KeyStore keyStore;
    private Certificate certificate;
    private Socket clientSocket;
    private OutputStream outStream;
    private InputStream inStream;

    public NetworkClient(final String userID, final String host, final int port) {
        this.userID = userID;

        try {
            final String home = System.getProperty("user.home");
            final File clientKeyStore = new File(home, ".secshare/" + userID + "/truststore.jks");

            /* Create keystore */
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            // TODO password == userID, this is not good
            keyStore.load(new FileInputStream(clientKeyStore), userID.toCharArray());

            /* Get factory for the given keystore */
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            final SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(null, tmf.getTrustManagers(), null);
            final SSLSocketFactory factory = ctx.getSocketFactory();

            this.certificate = keyStore.getCertificate(userID);

            this.clientSocket = factory.createSocket(host, port);

            System.out.println("Connected to " + host + " in port " + port + "\n");

            this.outStream = clientSocket.getOutputStream();
            this.outStream.flush();

            this.inStream = clientSocket.getInputStream();

        } catch (final IOException ioExp) {
            System.out.println("Error connecting to " + host + " in port " + port);

            if (this.clientSocket != null && this.clientSocket.isBound())
                try {
                    this.clientSocket.close();
                } catch (final IOException ioExp2) {
                    ioExp2.printStackTrace();
                }
            ioExp.printStackTrace();
        } catch (CertificateException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public boolean login(final String username, final String password) {
        try {
            final byte[] signature = CertificationAuthority.getInstance().sign(certificate);
            final Reply reply = (Reply) msgSendReceive(new LoginMessage(username, password, certificate, signature));
            return reply.isSuccess();
        } catch (SignatureException | CertificateEncodingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void disconnect() {

        System.out.println("Disconnecting...");

        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(new ExitMessage());
            objectOutputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        try {
            this.clientSocket.close();
        } catch (final IOException ioe3) {
            System.out.println("Error while disconnecting socket");
            ioe3.printStackTrace();
        }

    }


    public Message msgSendReceive(final Message msg) {
        Message reply = null;

        try {
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(msg);
//            objectOutputStream.flush();
            final ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
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
    public boolean sendFile(final SharedFile file) {
        assert file != null;

        if (!file.exists()) {
            throw new IllegalArgumentException("Cannot find file to send");
        }

        // request a download on the server
        final Reply reply = (Reply) msgSendReceive(new PutMessage(file));

        // if the server allows the upload
        if (reply.isSuccess()) {
            file.upload(outStream);
            return true;
        }

        System.err.println(reply.getMessage());
        return false;
    }

    public boolean receiveFile(final SharedFile file) {
        assert file != null;

        // request a download on the server
        final Reply dreply = (Reply) msgSendReceive(new GetMessage(file.getName()));


        // if the server allows the download
        if (dreply.isSuccess()) {
            final DownloadReply reply = (DownloadReply) dreply;
            try {

                final Key privateKey = keyStore.getKey(userID, userID.toCharArray());
                final Cipher rsa_cipher = Cipher.getInstance("RSA");
                rsa_cipher.init(Cipher.UNWRAP_MODE, privateKey);

                final Key secretKey = rsa_cipher.unwrap(reply.getKey(), "AES", Cipher.SECRET_KEY);

                final Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);

                file.download(inStream, reply.getNumber(), (bytes, last) -> {
//                    if (last) {
//                        try {
//                            return cipher.doFinal(bytes);
//                        } catch (IllegalBlockSizeException | BadPaddingException e) {
//                            e.printStackTrace();
//                        }
//                    }
                    return cipher.update(bytes);
                });

                file.setLastModified(reply.getTimestamp());
                System.out.println(file.getName() + "was downloaded");
                return true;
            } catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnrecoverableKeyException | KeyStoreException e) {
                e.printStackTrace();
            }
        }

        System.err.println(dreply.getMessage());
        return false;
    }
}
