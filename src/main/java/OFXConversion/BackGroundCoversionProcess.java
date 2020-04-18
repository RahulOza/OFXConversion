package OFXConversion;
/*
When the program is run from desktop, I need the process to keep running in the background, watch a directory
when a new file is downloaded, classify the file to one of the convertable formats and convert it automatically.

Run this process in a separate thread.
 */


import java.io.IOException;
import java.util.logging.Logger;
import java.nio.file.*;

import static OFXConversion.OFXConversion.convertFileMarcus;
import static java.nio.file.StandardWatchEventKinds.*;

public class BackGroundCoversionProcess implements Runnable{

    private final Path pollDirPath;
    private final WatchService watcher;
    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());
    // TODO read these values from application.properties file
    /*
     String path = System.getProperty("user.home");
     */
    private final String prefMarcus = "Transactions 50952493";


    /**
     * Creates a WatchService and registers the given directory
     */
    public BackGroundCoversionProcess(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        dir.register(watcher, ENTRY_CREATE);
        this.pollDirPath = dir;
    }
    /**
     * Process all events for the key queued to the watcher.
     */
        /*
        ENTRY_CREATE – A directory entry is created.
        ENTRY_DELETE – A directory entry is deleted.
        ENTRY_MODIFY – A directory entry is modified.
        OVERFLOW – Indicates that events might have been lost or discarded. You do not have to register for the OVERFLOW event to receive it.
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) {
                    continue;
                }

                //The filename is the context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();
                /*
                //Verify that the new file is a text file.
                try {
                    Path child = pollDirPath.resolve(filename);
                    if (!Files.probeContentType(child).equals("text/plain")) {
                        System.err.format("New file '%s' is not a plain text file.%n", filename);
                        continue;
                    }
                } catch (IOException x) {
                    logger.severe(x.toString());
                    continue;
                }
                */
                logger.info("New File found :" + filename);

                // We are only interested in .csv files and .ofx files ignore other files
               if(filename.toString().endsWith(".csv")){
                    if(filename.toString().startsWith(prefMarcus)){
                        try {
                            // TODO what to do re intial balance
                            convertFileMarcus(pollDirPath + "\\" + filename.toString(),0.0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } // .csv

            }

            //Reset the key -- this step is critical if you want to receive
            //further watch events. If the key is no longer valid, the directory
            //is inaccessible so exit the loop.
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }


    public void run(){
        logger.info("Polling directory process invoked .... ");
        processEvents();

    }


}
