package client;

import file_services.FileInfo;

import java.io.File;
import java.util.List;

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

	//TODO CAREFUL with array / list size and order
    public void ShareFiles(final String targetUser) {
        final boolean[] result = myServer.shareFiles(userFiles, targetUser);

		int i=0;
        for (final File file : userFiles) {
            if (result[i])
                System.out.println("+ Sharing " + file.getName() + " complete");
			else
				System.out.println("+ Sharing " + file.getName() + " FAILED");
			i++;
		}
		System.out.println(" +++ File sharing complete +++ ");		
	}
	
	//TODO
    public void SyncFiles(final int syncTimer) {
        //TODO PROBLEM: WHEN getting a file, it has to replace old file if exists or create a new one then add to client list if it is a new share received
        final boolean running = true;

		        while (running) {

		        }

		        //every 30 synctimer seconds list compare upload download
		        //TODO SYNC, must open some user input stream to read commands to stop
			}

}
