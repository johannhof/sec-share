package file_services;

import java.io.*;
import java.net.URI;

/**
 * A File with the ability to download and upload.
 */
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

    /**
     * Uploads the file to the specified OutputStream.
     *
     * @param outputStream the stream to upload to
     */
    public void upload(OutputStream outputStream) {
        assert outputStream != null;

        BufferedInputStream bis = null;
        try {
            byte[] mybytearray = new byte[(int) this.length()];
            bis = new BufferedInputStream(new FileInputStream(this));
            bis.read(mybytearray, 0, mybytearray.length);
            outputStream.write(mybytearray, 0, mybytearray.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the file by loading the specified number of bytes from the specified stream.
     *
     * @param inputStream the stream to load data from
     * @param filesize    number of bytes to load
     */
    public void download(InputStream inputStream, long filesize) {
        assert inputStream != null;

        byte[] buffer = new byte[1024];

        int bytesRead;
        long aux = filesize;

        FileOutputStream fileOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileOutputStream = new FileOutputStream(this);
            bufferedInputStream = new BufferedInputStream(inputStream);

            while (aux > 0) {
                bytesRead = bufferedInputStream.read(buffer, 0, 1024);
                aux -= bytesRead;
                fileOutputStream.write(buffer, 0, bytesRead);
                buffer = new byte[1024];
            }

            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
