package client;

import file_services.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//This class implements the core file operations so they aren't all stacked up on main
public class SecFileManager {

	List<File> userFiles;
	ServerStub myServer;
    File clientHome;

    public SecFileManager(final List<File> clientFiles, final ServerStub server, final String clientHome) {
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
        final List<FileInfo> result = myServer.listFiles();
        System.out.println(" +++ File list: +++ ");
        for (final FileInfo fi : result)
            System.out.println(fi.toString() + "\n\n");
    }
	
	private List<FileInfo> getServerFileList(){
		return myServer.listFiles();
	}

    public void ShareFiles(final String targetUser) {
        for (final File file : userFiles) {
            if (myServer.shareFile(file, targetUser)) {
                System.out.println("+ Sharing " + file.getName() + " complete");
            } else {
                System.out.println("+ Sharing " + file.getName() + " FAILED");
            }
        }
        System.out.println(" +++ File sharing complete +++ ");
    }

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
		String input;
		
		System.out.println(" +++ File Synchronization Starting +++ ");		
		System.out.println("");
		System.out.println("Available commands: ");
		System.out.println("\"exit\" - to stop and exit the program");
		//main sync cycle
		while (running) {

			System.out.println(" + New sync cycle + ");
			//TODO split the uploads / downloads timewise			
			
			//get server file list
			
			Map<String, String> fileSyncStatus = fileListCompare();
		}

		//every 30 synctimer seconds list compare upload download
		//TODO SYNC, must open some user input stream to read commands to stop
	}

	//TODO might need to add checks for lists not being empty
	private Map<String, String> FileListCompare(List<FileInfo> serverFiles) {

		//sets to contain file data
		HashMap<String, Long> localFileData = new HashMap<String, Long>();
		HashMap<String, Long> remoteFileData = new HashMap<String, Long>();

		//unique filenames from local and server
		Set<String> distinctFilenames = new HashSet<String>();

		//results to return map of filename plus operation Map<filename, operation>
		Map<String, String> result = new HashMap<String, String>();


		//add local data to map and set
		for(File f : userFiles){
			localFileData.put(f.getName(), f.lastModified());	
			distinctFilenames.add(f.getName());
		}

		//add remote data to map and set
		for(FileInfo fi : serverFiles){
			localFileData.put(fi.getFilename(), fi.getLastModified());
			distinctFilenames.add(fi.getFilename());
		}

		//main method cycle 
		for(String name : distinctFilenames){

			//if the file isn't on the local list
			if(!localFileData.containsKey(name)){
				result.put(name, "get");
			}
			//if the file isn't on the remote file list
			else if(!remoteFileData.containsKey(name)){
				result.put(name, "put");
			}
			//general case where the file is on both
			else {
				long localTime = localFileData.get(name);
				long remoteTime = localFileData.get(name);

				//file is more recent on the client
				if(localTime > remoteTime)
					result.put(name,"put");

				//file is more recent on the server
				else if(remoteTime > localTime)
					result.put(name,"get");

				//both have the same timestamp
				//do nothing
			}
		}

		return result;
	}

}
