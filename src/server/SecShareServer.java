package server;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class SecShareServer {

    public static final String SERVER_REPO = "./serverdir";

    public static void main(final String[] args) {

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

            final ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
            final ServerSocket serverSocket = ssf.createServerSocket();

            System.out.println("\nNow listening on port " + port + "\n");
            //start listening for incoming requests
            while (true)
                new RequestHandler(serverSocket.accept(), serverDirectory).run();

        } catch (final IOException ioe1) {
            System.out.println("Error while trying to listen on port: " + port);
            ioe1.printStackTrace();
            System.exit(-1);
        }

    }

}
