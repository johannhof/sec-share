package client;

import file_services.FileInfo;
import file_services.SharedFile;
import message.ListMessage;
import message.Reply;
import message.ShareMessage;

import java.util.List;

/**
 * Adapter class that takes the client requests such as a listing, file upload, file download and
 * converts them into the appropriate messages and calls the network client procedures.
 * <p>
 * The idea is that the file manager never has to deal with messages just the required content
 */
public class ServerStub {

    NetworkClient networkClient;

    public ServerStub(final NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    public boolean shareFile(final SharedFile file, final String targetUser) {
        final ShareMessage request = new ShareMessage(file.getName(), targetUser);
        final Reply reply = (Reply) networkClient.msgSendReceive(request);
        if (reply.isSuccess()) {
            return true;
        } else {
            System.err.println(reply.getMessage());
            return false;
        }
    }

    public List<FileInfo> listFiles() {

        final ListMessage request = new ListMessage(null);

        final ListMessage reply = (ListMessage) networkClient.msgSendReceive(request);

        return reply.getFileInfo();
    }

    public boolean putFile(final SharedFile file) {
        return networkClient.sendFile(file);
    }

    public boolean getFile(final SharedFile file) {
        return networkClient.receiveFile(file);
    }

    public boolean[] putFiles(final List<SharedFile> files) {

        final boolean[] replies = new boolean[files.size()];
        int i = 0;

        for (final SharedFile file : files)
            replies[i++] = putFile(file);

        return replies;
    }

    public boolean getFiles(final List<SharedFile> files) {

        for (final SharedFile f : files) {
            getFile(f);
        }

        return true;
    }

}
