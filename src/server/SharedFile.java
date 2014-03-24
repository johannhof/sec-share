package server;

import java.io.*;
import java.net.URI;

public class SharedFile extends File {

    public SharedFile(String pathname) {
        super(pathname);
    }

    public SharedFile(String parent, String child) {
        super(parent, child);
    }

    public SharedFile(File parent, String child) {
        super(parent, child);
    }

    public SharedFile(URI uri) {
        super(uri);
    }

    public void put(InputStream inputStream, int filesize) throws IOException {
        byte[] buffer = new byte[1024];

        int bytesRead, aux = filesize;

        FileOutputStream fileOutputStream = new FileOutputStream(this);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        while (aux > 0) {
            bytesRead = bufferedInputStream.read(buffer, 0, 1024);
            aux -= bytesRead;
            fileOutputStream.write(buffer, 0, bytesRead);
            buffer = new byte[1024];
        }

        fileOutputStream.flush();
        fileOutputStream.close();
    }
}
