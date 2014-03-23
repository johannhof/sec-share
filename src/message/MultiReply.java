package message;

public class MultiReply extends Message{

	private boolean[] results;
	
	public MultiReply(boolean[] results) {
		super(OpCode.REPLY);
		this.results = results;
	}

	public boolean[] getResults() {
		return this.results;
	}

}
