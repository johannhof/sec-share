package file_services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileInfo implements Serializable {

    private static final long serialVersionUID = -3406088714131942956L;

    String filename;
    long lastModified;
    String owner;
    List<String> shares;

    public FileInfo(String filename, long lastModified, String owner, List<String> shares) {
        this.filename = filename;
        this.lastModified = lastModified;
        this.owner = owner;
        this.shares = shares;
    }

    public FileInfo() {
        this.shares = new ArrayList<>();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<String> getShares() {
        return shares;
    }

    public void setShares(List<String> shares) {
        this.shares = shares;
    }

	@Override
	public String toString() {
		
		StringBuilder sharesAux = new StringBuilder();
		for(String st : this.shares)
			sharesAux.append(st + " ");
		
		return "Filename: " + this.filename + "\n\t" +
				"Last Modified: " + this.lastModified  + "\n\t" + 
				"Owner: " + this.owner  + "\n\t" +  
				"Shared with: " +  sharesAux.toString();
	}
}
