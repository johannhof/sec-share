package Client;

import java.io.File;
import java.util.List;

//This class implements the core file operations
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

	public void ShareFiles(String targetUser) {
		// TODO Auto-generated method stub

	}

}
