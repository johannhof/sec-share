package message;

import file_services.FileInfo;

import java.util.List;

public class ListMessage extends Message{

    private final List<FileInfo> fileInfo;

    public ListMessage(final List<FileInfo> fileInfo) {
        super(OpCode.LIST);
		this.fileInfo = fileInfo;
	}

	public List<FileInfo> getFileInfo() {
		return this.fileInfo;
	}

}
