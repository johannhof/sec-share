package client;

import file_services.SharedFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SecShareClient {

    private static final int SYNC_TIMER = 10;
    private static final String CLIENT_HOME = "./";
    private static final int MAX_ATTEMPTS = 3;

    //SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s <filenames>)
    public static void main(final String[] args) {

        final String userID;
        final String serverAddress;
        final String mode;
        String targetUser = null;
        //might want to use some other data structure
        final List<SharedFile> clientFiles = new ArrayList<>();

        //CL argument handling
        //argument handling will assumed a list of files separated by spaces
        //TODO allow any argument order?..better CL processing

        if ((args.length >= 5) && args[0].equals("-u") && args[2].equals("-a")) {

            //if this fails switch to args[x].substring(0);
            userID = args[1];
            serverAddress = args[3];
            mode = args[4];

        } else {
            throw new IllegalArgumentException("Invalid command line arguments \n\t " +
                    "Execution options:\n\t " +
                    "SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s [<filenames>] )");
        }

        //check and add the files and share target
        if (!mode.equals("-l")) {
            int i = 5;

            //if it's a share
            if (mode.equals("-p")) {
                targetUser = args[5];
                i = 6;
            }

            //check files and add
            while (i < args.length) {
                final SharedFile file = new SharedFile(CLIENT_HOME, args[i++]);
                clientFiles.add(file);
            }
            
        }

        // parse address
        // TODO this probably fails in case of http://localhost:3000
        final String[] serverAddressAux = serverAddress.split(":");
        if (serverAddressAux.length != 2) {
            System.err.println("Invalid address");
            System.exit(-1);
        }

        //connect to the server
        final NetworkClient netClient = new NetworkClient(userID, serverAddressAux[0], Integer.parseInt(serverAddressAux[1]), CLIENT_HOME);

        System.out.println("Welcome to SecShare\n");

        //get user password
        String password = null;
        final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

        for (int i = 0; i <= MAX_ATTEMPTS; i++) {
            if (i == MAX_ATTEMPTS) {
                System.err.println("Too many password attempts");
                System.exit(-1);
            }

            // read password
            System.out.println("Please enter your password");
            try {
                password = inputReader.readLine();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            // check with server
            if (netClient.login(userID, password)) {
                System.out.println("Logged in as " + userID);
                break;
            } else {
                System.out.println("Sorry. Try again.");
            }
        }

        try {
            inputReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        //TODO possible problem with clientfiles being null in case of -l
        final ServerStub mServerStub = new ServerStub(netClient);
        final SecFileManager mFileManager = new SecFileManager(clientFiles, mServerStub, CLIENT_HOME);

        //main switch, manages operation mode
        switch (mode) {

            case "-c":
                mFileManager.uploadAll();
                break;
            case "-p":
                mFileManager.ShareFiles(targetUser);
                break;
            case "-g":
                mFileManager.downloadAll();
                break;
            case "-s":
                mFileManager.SyncFiles(SYNC_TIMER);
                break;
            case "-l":
                mFileManager.listFiles();
                break;
            default: //Crap throw exception
                break;
        }

        //disconnect from the server if still connected
        netClient.disconnect();
        System.out.println("SecShare out!");
    }


}
