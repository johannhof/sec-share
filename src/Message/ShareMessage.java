package Message;

import java.util.List;

public class ShareMessage extends Message {

	private List<String> filenames;
	private String targetUser;

	public ShareMessage(List<String> filenames, String targetUser) {
		super(OpCode.SHARE);

		this.filenames = filenames;
		this.targetUser = targetUser;
	}

}
