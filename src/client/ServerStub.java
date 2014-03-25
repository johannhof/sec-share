package client;

import file_services.FileInfo;
import message.ListMessage;
import message.Reply;
import message.ShareMessage;

import java.io.File;
import java.util.List;

public class ServerStub {
	//adapter class that takes the client requests such as a listing, file upload, file download and converts them in the appropriate 
	//messages and calls the network client procedures
	//typical methods here will be download file, get file
	//the idea is that the file manager never has to deal with messages just the required content	
	//methods might be to be oveloaded to provide option of merely passing filename
	//TODO Datastructure for file management might be something other than pleb old list...

	NetworkClient networkClient;

	public ServerStub(final NetworkClient networkClient) {
		this.networkClient = networkClient;
	}

	public boolean shareFile(final File file, final String targetUser) {
		final ShareMessage request = new ShareMessage(file.getName(), targetUser);
		final Reply reply = (Reply) networkClient.msgSendReceive(request);
		if (reply.isSuccess()) {
			return true;
		} else {
			System.err.println(reply.getMessage());
			return false;
		}
	}

	public List<FileInfo> listFiles() {

		final ListMessage request = new ListMessage(null);

		final ListMessage reply = (ListMessage) networkClient.msgSendReceive(request);

		return reply.getFileInfo();
	}

	public boolean putFile(final File file) {
		return networkClient.sendFile(file);
	}

	public boolean getFile(final File file) {
		return networkClient.receiveFile(file);
	}

	public boolean[] putFiles(final List<File> files) {

		//TODO same issue as file sharing this needs to have an order consistant wiht server processing, not a problem just a concern :P
		final boolean[] replies = new boolean[files.size()];
		int i = 0;

		for (final File file : files)
			replies[i++] = putFile(file);

		return replies;
	}

	public boolean getFiles(final List<File> files) {
		
		for(File f : files)
			getFile(f);

		return true;
	}

}
