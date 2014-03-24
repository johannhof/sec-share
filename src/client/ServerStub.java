package client;

import file_services.FileInfo;
import file_services.SharedFile;
import message.ListMessage;
import message.MultiReply;
import message.ShareMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerStub {
//adapter class that takes the client requests such as a listing, file upload, file download and converts them in the appropriate 
	//messages and calls the network client procedures
    //typical methods here will be download file, get file
    //the idea is that the file manager never has to deal with messages just the required content
	//should this be a static clasS????
	
	//methods might be to be oveloaded to provide option of merely passing filename
	
	NetworkClient networkClient;
	
	public ServerStub(NetworkClient networkClient) {
		this.networkClient = networkClient;
		
	}
	
	//returns a boolean array which is the result of each share
	//can only share files already on the server
	public boolean[] shareFiles(List<File> files, String targetUser){
		//TODO must return error message if failed
		
		ArrayList<String> filenames = new ArrayList<String>();
		
		for(File f : files)
			filenames.add(f.getName());
		
		ShareMessage request = new ShareMessage(filenames, targetUser);
		MultiReply reply = (MultiReply) networkClient.msgSendReceive(request);
		
		//TODO careful with the order, the server must return the replies in the same order as the requests otherwise must add a filename list to multireply
		return reply.getResults();		
	}
	
	//Datastructure for file management might be something other than pleb old list...
	public List<FileInfo> listFiles() {
		
		ListMessage request = new ListMessage(null);
		
		ListMessage reply = (ListMessage) networkClient.msgSendReceive(request);
		
		return reply.getFileInfo();
	}

    public boolean putFile(SharedFile file) {
        return networkClient.sendFile(file);
    }

    public boolean getFile() {

        // TODO
        return true;
    }

    public boolean putFiles(List<SharedFile> files) {
        files.forEach(this::putFile);
        return true;
    }

    public boolean getFiles() {

        // TODO
        return true;
    }

    public boolean shareFile(File file, String targetUser){

        // TODO
        return true;
    }
}
