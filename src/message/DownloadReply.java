package message;

public class DownloadReply extends Reply {

    protected long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public DownloadReply(final boolean success, final long filesize, final long timestamp) {
        super(success, filesize);
        this.timestamp = timestamp;
    }
}
