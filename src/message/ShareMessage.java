package message;

import java.util.List;

public class ShareMessage extends Message {

	private List<String> filenames;
	private String targetUser;

	public ShareMessage(List<String> filenames, String targetUser) {
		super(OpCode.SHARE);

		this.filenames = filenames;
		this.targetUser = targetUser;
	}

	public List<String> getFilenames() {
		return this.filenames;
	}

	public String getTargetUser() {
		return this.targetUser;
	}

}
