package Client;

import java.io.File;

public class ServerStub {
//adapter class that takes the client requests such as a listing, file upload, file download and converts them in the appropriate 
	//messages and calls the network client procedures
	//typical methods here will be put file, get file
	//the idea is that the file manager never has to deal with messages just the required content
	//should this be a static clasS????
	
	//TODO constructor, must decide on static first
	
	//methods might be to be oveloaded to provide option of merely passing filename
	public boolean shareFile(File file, String targetUser){
		
	}
	
	//Datastructure for file management might be something other than pleb old list...
	public List<FileInfo> listFiles() {
		
	}
	
	public boolean putFile() {
		
	}
	
	public boolean getFile() {
		
	}
	
	public boolean putAllFiles() {
		
	}
	
	public boolean getAllFiles() {
		
	}
	
	public boolean shareFile(File file, String targetUser){
		
		
	}
}