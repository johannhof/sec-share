package message;

public class Reply extends Message {

    private boolean success;

    public Reply(boolean success) {
        super(OpCode.REPLY);
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

}
