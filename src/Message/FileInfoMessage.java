package Message;

import java.util.List;

public class FileInfoMessage extends Message{

	List<FileInfo> fileInfo;
		
	public FileInfoMessage(List<FileInfo> fileInfo) {
		super(OpCode.FILEINFO);
		this.fileInfo = fileInfo;
	}

}
