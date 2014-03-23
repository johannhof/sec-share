package client;

import java.io.File;
import java.util.List;

//This class implements the core file operations so they aren't all stacked up on main
public class SecFileManager {

	List<File> userFiles;
	ServerStub myServer;
	File clientHome;

	public SecFileManager(List<File> clientFiles, ServerStub server, String clientHome) {
		this.userFiles = clientFiles;
		this.myServer = server;
		this.clientHome = new File(clientHome);
	}

	public void uploadAll() {
		myServer.putFiles(userFiles);
		//TODO get and print error messages if this failed
	}

	public void downloadAll() {
		myServer.getFiles();
		//TODO add new files that were shared to my file list or is this done with list?
	}

	public void listFiles() {
		myServer.listFiles();
		//TODO must add new files to the file list AND display the information
	}

	public void SyncFiles(int syncTimer) {

		boolean running = true;
		
		while(running)
			
				//every 30 synctimer seconds list compare upload download
		//TODO SYNC, must open some user input stream to read commands to stop
	}

	public void ShareFiles(String targetUser) {
		
	}

}
