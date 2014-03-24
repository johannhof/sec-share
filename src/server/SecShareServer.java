package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

public class SecShareServer {

    public static final String SERVER_REPO = "./serverdir";


    public static void main(String[] args) {

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
        File serverDirectory = new File(SERVER_REPO);
        boolean listening = true;

        System.out.println("Welcome to SecShare\n");

        //open server socket
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            //start listening for incoming requests
            while (listening)
                new RequestHandler(serverSocket.accept(), serverDirectory).run();

        } catch (IOException ioe1) {
            System.out.println("Error while trying to listen on port: " + port);
            ioe1.printStackTrace();
            System.exit(-1);
        }

        System.out.println("\nNow listening on port " + port + "\n");


    }

}
