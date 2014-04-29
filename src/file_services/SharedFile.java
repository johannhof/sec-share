package file_services;

import java.io.*;
import java.nio.file.Paths;

/**
 * A Standard File with extended sharing functionality
 */
public class SharedFile extends File {

    private final String base;

    /**
     * Creates a new Shared file, the provided base needs to be the root path of the client
     * (the directory where the user keeps his files to share)
     *
     * @param base client base path
     * @param path specific file path, should include all subdirectories from the base path
     */
    public SharedFile(final String base, final String path) {
        super(base, path);
        this.base = new File(base).getAbsolutePath();
    }

    /**
     * Uploads the file to the specified OutputStream.
     *
     * @param outputStream the stream to upload to
     */
    public void upload(final OutputStream outputStream) {
        assert outputStream != null;

        BufferedInputStream bis = null;
        try {
            final byte[] mybytearray = new byte[(int) this.length()];
            bis = new BufferedInputStream(new FileInputStream(this));
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

    public void download(final InputStream inputStream, final long filesize) {
        download(inputStream, filesize, null);
    }

    /**
     * Saves the file by loading the specified number of bytes from the specified stream.
     *
     * @param inputStream the stream to load data from
     * @param filesize    number of bytes to load
     */
    public void download(final InputStream inputStream, final long filesize, final CryptoHelper cryptoHelper) {
        assert inputStream != null;

        // make sure that the path exists
        getParentFile().mkdirs();

        byte[] buffer = new byte[1024];

        long bytesRead;
        long aux = filesize;

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(this);

            while (aux > 0) {
                bytesRead = inputStream.read(buffer, 0, 1024);
                aux -= bytesRead;
                if (cryptoHelper != null) {
                    buffer = cryptoHelper.transform(buffer, aux > 0);
                }
                fileOutputStream.write(buffer, 0, (int) bytesRead);
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

    @Override
    public String getName() {
        return Paths.get(base).relativize(Paths.get(getAbsolutePath())).toString();
    }
}
