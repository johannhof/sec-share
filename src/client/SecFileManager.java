package client;

import file_services.FileInfo;
import file_services.FileOperations;

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
		System.out.println(" +++ Files copied to server +++ ");
	}

	public void downloadAll() {
		myServer.getFiles(userFiles);
		System.out.println(" +++ Files copied from server +++ ");
	}

	public void listFiles() {
		List<FileInfo> result = myServer.listFiles();
		System.out.println(" +++ File list: +++ ");
		for(FileInfo fi : result)
			System.out.println(result.toString() + "\n\n");		
	}

	//TODO CAREFUL with array / list size and order
	public void ShareFiles(String targetUser) {
		boolean[] result = myServer.shareFiles(userFiles, targetUser);

		int i=0;
		for(File file : userFiles){
			if(result[i] == true)
				System.out.println("+ Sharing " + file.getName() + " complete");
			else
				System.out.println("+ Sharing " + file.getName() + " FAILED");
			i++;
		}
		System.out.println(" +++ File sharing complete +++ ");		
	}
	
	//TODO
	public void SyncFiles(int syncTimer) {
		//TODO PROBLEM: WHEN getting a file, it has to replace old file if exists or create a new one then add to client list if it is a new share received
				boolean running = true;

		        while (running) {

		        }

		        //every 30 synctimer seconds list compare upload download
		        //TODO SYNC, must open some user input stream to read commands to stop
			}

}
