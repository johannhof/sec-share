package message;

public class Reply extends Message {

	private boolean result;
	
	public Reply(boolean result) {
		super(OpCode.REPLY);
		this.result = result;
	}

    // TODO is that the right method name?
    // shouldn't it be something like isSuccess or getResult?
    public boolean isResult() {
        return this.result;
	}
	
}
