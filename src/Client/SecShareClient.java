package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecShareClient {

	private static final int SYNC_TIMER = 30;
	private static final String CLIENT_HOME = " ";


	//SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s <filenames>)
	public static void main(String[] args) {

		String userID = null;
		String serverAddress = null;
		String mode = null;
		String targetUser = null;
		//might want to use some other data structure
		List<File> clientFiles = new ArrayList<File>();


		//CL argument handling
		//TODO check filenames, organize better, be more thorough in checking for wrong CL options
		//-p OPTION NOT YET HANDLED
		if(args.length >= 6){
			if(args[0].equals("-u") && args[2].equals("-a")){

				//if this fails switch to args[x].substring(0);
				userID = args[1];
				serverAddress = args[3];
				mode = args[4];

				//how are filenames separated? assuming comma
				for(String aux : args[5].split(",")){
					//check for exceptions
					clientFiles.add(new File(aux));
				}
			}
			//replace with Illegal argument exception?
			System.out.println("Improperly formatted arguments");
			System.out.println("Execution options:");
			System.out.println("SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s <filenames>)");
			System.exit(-1);

		}
		else{
			//replace with Illegal argument exception?
			System.out.println("Improperly formatted arguments");
			System.out.println("Execution options:");
			System.out.println("SecShareClient -u <userId> -a <serverAddress> ( -c <filenames> | -p <userId> <filenames> | -g <filenames> | -s <filenames>)");
			System.exit(-1);
		}

		//TODO check the files
		
		//get user password
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Welcome to SecShare\n");
		System.out.println("Please enter your password");
		try {
			inputReader.readLine();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
	
		//connect to the server
		//TODO missing a try catch or a check to see if addrss is properly formated
		String[] serverAddressAux = serverAddress.split(":");
		
		NetworkClient netClient = new NetworkClient(userID, serverAddressAux[0], Integer.parseInt(serverAddressAux[1]));
		ServerStub mServerStub = new ServerStub(netClient);
		SecFileManager mFileManager = new SecFileManager(clientFiles, mServerStub);
		
		
		//TODO check user password?
		
		
		try {
			inputReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//main switch, manages operation mode
		switch(mode){

		case "-c" : mFileManager.uploadAll();
			break;
		case "-p" : mFileManager.ShareFiles(targetUser);
			break;
		case "-g" : mFileManager.downloadAll();
			break;
		case "-s" : mFileManager.SyncFiles(SYNC_TIMER);
			break;
		case "-l" : mFileManager.listAll();
			break;
		default : //Crap throw exception
			break;


		}
		
		//disconnect from the server if still connected
		netClient.disconnect();
		System.out.println("SecShare out!");
	}


}
