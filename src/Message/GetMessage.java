package Message;

public class GetMessage extends Message {

	private String filename;
	
	public GetMessage(String filename) {
		super(OpCode.GET);
		
	}

}
