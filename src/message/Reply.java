package message;

public class Reply extends Message {

    private final boolean success;
    private long number;
    private String message;

    public Reply(final boolean success, final String message) {
        super(OpCode.REPLY);
        this.message = message;
        this.success = success;
    }

    public Reply(final boolean success, final long number) {
        super(OpCode.REPLY);
        this.number = number;
        this.success = success;
    }

    public Reply(final boolean success, final long number, final String message) {
        super(OpCode.REPLY);
        this.success = success;
        this.number = number;
        this.message = message;
    }

    public Reply(final boolean success) {
        super(OpCode.REPLY);
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(final long number) {
        this.number = number;
    }


}
