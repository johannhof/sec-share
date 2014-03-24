package message;

import java.io.File;

public class PutMessage extends Message {

    private String filename;
    private long filesize;
    private long timestamp;

    public PutMessage(File file) {
        super(OpCode.PUT);
        this.filename = file.getName();
        this.filesize = file.length();
        this.timestamp = file.lastModified();
    }

    public String getFilename() {
        return filename;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getFilesize() {
        return filesize;
    }
}
