package message;

import file_services.SharedFile;

public class PutMessage extends Message {

    private final String filename;
    private final long filesize;
    private final long timestamp;

    public PutMessage(final SharedFile file) {
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
