package server;

import file_services.FileInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private static final long serialVersionUID = 278655679754078741L;

    private String name;
    private String password;
    private List<FileInfo> files;

    public User() {
        this.files = new ArrayList<>();
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void addFile(FileInfo file) {
        files.add(file);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
