package message;

public class Reply extends Message {

    private final boolean success;
    private int number;
    private String message;

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

    public void setMessage(String message) {
        this.message = message;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


}
