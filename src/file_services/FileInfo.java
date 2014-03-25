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

    public FileInfo(final String filename, final long lastModified, final String owner, final List<String> shares) {
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

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    public List<String> getShares() {
        return shares;
    }

    public void setShares(final List<String> shares) {
        this.shares = shares;
    }

    @Override
    public String toString() {

        final StringBuilder sharesAux = new StringBuilder();
        for (final String st : this.shares)
            sharesAux.append(st).append(" ");

        return "Filename: " + this.filename + "\n\t" +
                "Last Modified: " + this.lastModified + "\n\t" +
                "Owner: " + this.owner + "\n\t" +
                "Shared with: " + sharesAux.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileInfo fileInfo = (FileInfo) o;

        if (filename != null ? !filename.equals(fileInfo.filename) : fileInfo.filename != null) return false;
        if (owner != null ? !owner.equals(fileInfo.owner) : fileInfo.owner != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = filename != null ? filename.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
