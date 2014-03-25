package file_services;

import java.io.*;

/**
 * A File with the ability to download and upload.
 */
public class FileOperations {


    /**
     * Uploads the file to the specified OutputStream.
     *
     * @param outputStream the stream to upload to
     */
    public static void upload(final File file, final OutputStream outputStream) {
        assert outputStream != null;

        BufferedInputStream bis = null;
        try {
            final byte[] mybytearray = new byte[(int) file.length()];
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.read(mybytearray, 0, mybytearray.length);
            outputStream.write(mybytearray, 0, mybytearray.length);
            outputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the file by loading the specified number of bytes from the specified stream.
     *
     * @param file        the file to download the data to
     * @param inputStream the stream to load data from
     * @param filesize    number of bytes to load
     */
    public static void download(final File file, final InputStream inputStream, final long filesize) {
        assert inputStream != null;

        byte[] buffer = new byte[1024];

        long bytesRead;
        long aux = filesize;

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);

            while (aux > 0) {
                bytesRead = inputStream.read(buffer, 0, 1024);
                aux -= bytesRead;
                fileOutputStream.write(buffer, 0, (int) Math.max(bytesRead, aux));
                buffer = new byte[1024];
            }

            fileOutputStream.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

    }
}
