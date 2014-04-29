package server;

import file_services.FileInfo;

import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * A Java Bean to serialize the user model, including information about all files the user has saved.
 * <p>
 * Yes, we are aware that saving this information in a single file will probably not scale well.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 278655679754078741L;

    private String name;
    private String password;
    private List<FileInfo> files;

    public User() {
        this.files = new ArrayList<>();
    }

    public void setFiles(final List<FileInfo> files) {
        this.files = files;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void addFile(final FileInfo file) {
        files.add(file);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public FileInfo findFile(final String name) {
        for (final FileInfo fileInfo : this.files) {
            if (fileInfo.getFilename().equals(name)) {
                return fileInfo;
            }
        }
        return null;
    }

    public PublicKey getPublicKey() throws KeyStoreException {
        return KeyStore.getInstance("JKS").getCertificate(name).getPublicKey();
    }
}
