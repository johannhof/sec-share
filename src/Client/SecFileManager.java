package Client;

import java.io.File;
import java.util.List;

public class SecFileManager {

	List<File> userFiles;
	ServerStub myServer;
	
	public SecFileManager(List<File> clientFiles, ServerStub server) {
		this.userFiles = clientFiles;
		this.myServer = server;
	}

	public void uploadAll() {
		// TODO Auto-generated method stub
		
	}

	public void downloadAll() {
		// TODO Auto-generated method stub
		
	}

	public void listAll() {
		// TODO Auto-generated method stub
		
	}

	public void SyncFiles(int syncTimer) {
		// TODO Auto-generated method stub
		
	}

}
