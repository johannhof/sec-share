package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SecShareClient {

    private static final int SYNC_TIMER = 30;
    private static final String CLIENT_HOME = "./clientdir";


    //SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s <filenames>)
    public static void main(final String[] args) {

        String userID = null;
        String serverAddress = null;
        String mode = null;
        String targetUser = null;
        //might want to use some other data structure
        final List<File> clientFiles = new ArrayList<>();

        //CL argument handling
        //argument handling will assumed a list of files separated by spaces
        //TODO allow any argument order?

        if ((args.length >= 5) && args[0].equals("-u") && args[2].equals("-a")) {

            //if this fails switch to args[x].substring(0);
            userID = args[1];
            serverAddress = args[3];
            mode = args[4];

        } else {
            throw new IllegalArgumentException("Invalid command line arguments \n\t " +
                    "Execution options:\n\t " +
                    "SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s <filenames>)");
        }

        System.out.println("Welcome to SecShare\n");
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
                final File file = new File(CLIENT_HOME, args[i++]);

                if (!file.exists() || !file.isFile())
                    throw new IllegalArgumentException("File not found");
                else
                    clientFiles.add(file);
            }

        }


        System.out.println("Welcome to SecShare\n");
        //connect to the server
        //TODO missing a try catch or a check to see if address is properly formated
        final String[] serverAddressAux = serverAddress.split(":");

        final NetworkClient netClient = new NetworkClient(userID, serverAddressAux[0], Integer.parseInt(serverAddressAux[1]), CLIENT_HOME);

        //get user password
        String password = null;
        final BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Welcome to SecShare\n");
        System.out.println("Please enter your password");
        try {
            password = inputReader.readLine();
        } catch (final IOException e) {

            e.printStackTrace();
        }


        //check user password?
        if (netClient.login(userID, password)) {
            System.out.println("Logged in as" + userID);
        } else {
            ///TODO exception here or allow new attempts
            System.err.println("Invalid Login");
            System.exit(-1);
        }

        try {
            inputReader.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        //TODO possible problem with clientfiles beind null in case of -l
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
