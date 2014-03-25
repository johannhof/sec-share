package message;

public class DownloadReply extends Reply {

    protected long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public DownloadReply(boolean success, long filesize, long timestamp) {
        super(success, filesize);
        this.timestamp = timestamp;
    }
}
