package message;

public class DownloadReply extends Reply {

    protected long timestamp;
    protected byte[] key;

    public long getTimestamp() {
        return timestamp;
    }

    public DownloadReply(final boolean success, final long filesize, final long timestamp, byte[] key) {
        super(success, filesize);
        this.timestamp = timestamp;
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }
}
