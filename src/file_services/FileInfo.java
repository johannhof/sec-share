package file_services;

import java.io.Serializable;
import java.util.List;

public class FileInfo implements Serializable{
	
	String filename;
	long lastModified;
	String owner;
	List<String> shares;
	
	public FileInfo(String filename, long lastModified, String owner, List<String> shares){
		this.filename = filename;
		this.lastModified = lastModified;
		this.owner = owner;
		this.shares = shares;

    }
}
