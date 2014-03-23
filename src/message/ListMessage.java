package message;

import java.util.List;

import file_services.FileInfo;

public class ListMessage extends Message{

	private List<FileInfo> fileInfo;
		
	public ListMessage(List<FileInfo> fileInfo) {
		super(OpCode.LIST);
		this.fileInfo = fileInfo;
	}

	public List<FileInfo> getFileInfo() {
		return this.fileInfo;
	}

}
