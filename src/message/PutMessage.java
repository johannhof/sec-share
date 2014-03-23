package message;

public class PutMessage extends Message {

	private String filename;
	private long timestamp;
	
	public PutMessage(String filename, long timestamp) {
		super(OpCode.PUT);
		this.filename = filename;
		this.timestamp = timestamp;
	}

}
