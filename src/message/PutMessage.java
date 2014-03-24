package message;

public class PutMessage extends Message {

    private String filename;
    private int filesize;
    private long timestamp;

    public PutMessage(String filename, long timestamp) {
        super(OpCode.PUT);
        this.filename = filename;
        this.timestamp = timestamp;
    }

    public String getFilename() {
        return filename;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getFilesize() {
        return filesize;
    }
}
