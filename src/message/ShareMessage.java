package message;

public class ShareMessage extends Message {

    private final String filename;
    private final String targetUser;

    public ShareMessage(final String filename, final String targetUser) {
        super(OpCode.SHARE);

        this.filename = filename;
        this.targetUser = targetUser;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getTargetUser() {
        return this.targetUser;
    }

}
