package server;

import external.CertificationAuthority;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.*;
import java.security.cert.CertificateException;

public class SecShareServer {

    public static final String SERVER_REPO = "./serverdir";

    public static void main(final String[] args) throws KeyStoreException {

        CertificationAuthority.getInstance().init("./authority.jks");

        // get user password and file input stream
        final String password = "server";

        int port = 0;

        //Process arguments
        if (args.length == 1) {
            //TODO check if input is a valid port number
            port = Integer.parseInt(args[0]);
        } else {
            //replace with Illegal argument exception?
            System.out.println("Improperly formatted arguments");
            System.out.println("Execution options:");
            System.out.println("SecShareServer <port>");
            System.exit(-1);
        }

        //setup
        final File serverDirectory = new File(SERVER_REPO);

        System.out.println("Welcome to SecShare\n");

        //open server socket
        try {

            final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(SERVER_REPO + "/keystore.jks"), password.toCharArray());

            final SSLContext ctx = SSLContext.getInstance("SSL");

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password.toCharArray());
            ctx.init(kmf.getKeyManagers(), null, null);

            final SSLServerSocketFactory factory = ctx.getServerSocketFactory();

            final ServerSocket serverSocket = factory.createServerSocket(port);

            System.out.println("\nNow listening on port " + port + "\n");
            //start listening for incoming requests
            while (true)
                new RequestHandler(serverSocket.accept(), serverDirectory).run();

        } catch (final IOException ioe1) {
            System.out.println("Error while trying to listen on port: " + port);
            ioe1.printStackTrace();
            System.exit(-1);
        } catch (CertificateException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }

    }

}
