package message;

public class GetMessage extends Message {

	private String filename;
	
	public GetMessage(String filename) {
		super(OpCode.GET);
		
	}

	public String getFilename() {
		return this.filename;
	}

}
