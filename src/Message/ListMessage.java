package Message;

import java.util.List;

public class ListMessage extends Message{

	private List<FileInfo> fileInfo;
		
	public ListMessage(List<FileInfo> fileInfo) {
		super(OpCode.LIST);
		this.fileInfo = fileInfo;
	}

}
