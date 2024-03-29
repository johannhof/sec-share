package client;

import file_services.FileInfo;
import file_services.SharedFile;

import java.io.File;
import java.util.*;

/**
 * This class implements the core file operations
 */
public class SecFileManager {

    private final List<SharedFile> userFiles;
    private final ServerStub myServer;
    private final String clientHome;

    public SecFileManager(final List<SharedFile> clientFiles, final ServerStub server, final String clientHome) {
        this.userFiles = clientFiles;
        this.myServer = server;
        this.clientHome = clientHome;
    }

    public void uploadAll() {
        myServer.putFiles(userFiles);
        System.out.println(" +++ Files copied to server +++ ");
    }

    public void downloadAll() {
        myServer.getFiles(userFiles);
        System.out.println(" +++ Files copied from server +++ ");
    }

    public void listFiles() {
        final List<FileInfo> result = myServer.listFiles();
        System.out.println(" +++ File list: +++ ");
        if (result == null)
            System.out.println(" + No files to display + ");
        else {
            for (final FileInfo fi : result)
                System.out.println(fi.toString() + "\n\n");
        }
    }

    public void ShareFiles(final String targetUser) {
        for (final SharedFile file : userFiles) {
            if (myServer.shareFile(file, targetUser)) {
                System.out.println("+ Sharing " + file.getName() + " complete");
            } else {
                System.out.println("+ Sharing " + file.getName() + " FAILED");
            }
        }
        System.out.println(" +++ File sharing complete +++ ");
    }

    public void SyncFiles(final int syncTimer) {
        System.out.println(" +++ File Synchronization Starting +++ ");
        //main sync cycle
        while (true) {

            System.out.println(" + New sync cycle + ");

            //get server file list
            final Map<String, String> fileOps = getFileSyncRequiredOperations(myServer.listFiles(), userFiles.size() < 1);

            //execute required file operations
            for (final String fileName : fileOps.keySet()) {

                try {
                    //TODO split the uploads / downloads timewise by diving syncTimer / fileOps.size() fixed 10 milisec sleep atm
                    Thread.sleep(10);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }

                final String op = fileOps.get(fileName);
                if (op.equals("put"))
                    myServer.putFile(new SharedFile(clientHome, fileName));
                else if (op.equals("get"))
                    myServer.getFile(new SharedFile(clientHome, fileName));
            }

            System.out.println(" + Sync cycle ended + \n");

            //sleep for syncTimer
            try {
                Thread.sleep(syncTimer * 1000);
            } catch (final InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        //TODO is this returning to main and exiting correctly?
        //every 30 synctimer seconds list compare upload download
        //TODO SYNC, must open some user input stream to read commands to stop
    }

    private void walkDir(final File root, final HashMap<String, Long> localFileData, final Set<String> distinctFilenames) {

        final File[] list = root.listFiles();

        if (list == null) return;

        for (final File f : list) {
            if (f.isDirectory()) {
                walkDir(f, localFileData, distinctFilenames);
            } else {
                final SharedFile fi = new SharedFile(clientHome, f.getPath());
                localFileData.put(fi.getName(), fi.lastModified());
                distinctFilenames.add(fi.getName());
            }
        }
    }

    //what a terrible name for a method
    private Map<String, String> getFileSyncRequiredOperations(final List<FileInfo> serverFiles, final boolean all) {

        //sets to contain file data
        final HashMap<String, Long> localFileData = new HashMap<>();
        final HashMap<String, Long> remoteFileData = new HashMap<>();

        //unique filenames from local and server
        final Set<String> distinctFilenames = new HashSet<>();

        //results to return map of filename plus operation Map<filename, operation>
        final Map<String, String> result = new HashMap<>();

        if (all) {
            // add whole user directory
            walkDir(new File(clientHome), localFileData, distinctFilenames);
        } else {
            //add local data to map and set
            for (final SharedFile f : userFiles) {
                localFileData.put(f.getName(), f.lastModified());
                distinctFilenames.add(f.getName());
            }
        }

        //add remote data to map and set
        for (final FileInfo fi : serverFiles) {
            if (localFileData.containsKey(fi.getFilename())) {
                remoteFileData.put(fi.getFilename(), fi.getLastModified());
                distinctFilenames.add(fi.getFilename());
            }
        }

        //main method cycle
        for (final String name : distinctFilenames) {

            //if the file isn't on the local list
            if (!localFileData.containsKey(name)) {
                result.put(name, "get");
            }
            //if the file isn't on the remote file list
            else if (!remoteFileData.containsKey(name)) {
                result.put(name, "put");
            }
            //general case where the file is on both
            else {
                final long localTime = localFileData.get(name);
                final long remoteTime = remoteFileData.get(name);

                //file is more recent on the client
                if (localTime > remoteTime) {
                    result.put(name, "put");
                }

                //file is more recent on the server
                else if (remoteTime > localTime) {
                    result.put(name, "get");
                }

                //both have the same timestamp
                //do nothing
            }
        }

        return result;
    }

}
