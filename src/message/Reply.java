package message;

public class Reply extends Message {

	private boolean result;
	
	public Reply(boolean result) {
		super(OpCode.REPLY);
		this.result = result;
	}

    public boolean getResult() {
        return this.result;
	}
	
}
