package message;

public class GetMessage extends Message {

    private final String filename;

    public GetMessage(final String filename) {
        super(OpCode.GET);
        this.filename = filename;
    }

	public String getFilename() {
		return this.filename;
	}

}
