package OFXConversion;
/*
When the program is run from desktop, I need the process to keep running in the background, watch a directory
when a new file is downloaded, classify the file to one of the convertable formats and convert it automatically.

Run this process in a separate thread.
 */


import OFXConversion.data.OfxgenGetPropertyValues;

import java.io.IOException;
import java.util.logging.Logger;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class BackGroundCoversionProcess implements Runnable{

    private final Path pollDirPath;
    private final WatchService watcher;
    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());

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
        boolean processed = false;
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

                logger.info("New File found :" + filename);

                // We are only interested in .csv files and .ofx files ignore other files
               if(filename.toString().endsWith(".csv")){
                    if(filename.toString().startsWith(OfxgenGetPropertyValues.prefixMarcusFilename)){
                        try {
                            OFXConversion.convertFileMarcus(pollDirPath + "\\" + filename.toString(),OfxgenGetPropertyValues.intialBalanceMarcus);
                            processed = true;
                        } catch (IOException e) {
                            logger.severe(e.toString());
                        }
                    }//Marcus
                   if(filename.toString().startsWith(OfxgenGetPropertyValues.prefixAmazonFileName)){
                       try {
                           OFXConversion.convertFileAmazon(pollDirPath + "\\" + filename.toString(),OfxgenGetPropertyValues.initialBalanceAmazon);
                           processed = true;
                       } catch (IOException e) {
                           logger.severe(e.toString());
                       }
                   }//Amazon
                   if(filename.toString().startsWith(OfxgenGetPropertyValues.prefixByondFileName)) {
                       try {
                           OFXConversion.convertFileByond(pollDirPath + "\\" + filename.toString());
                           processed = true;
                       } catch (IOException e) {
                           logger.severe(e.toString());
                       }
                   }//Byond
               } // .csv
                if(filename.toString().endsWith(".Xls")){
                    //if .Xls file can only be vanguard

                    if(filename.toString().startsWith(OfxgenGetPropertyValues.prefixVanguardFileName)){
                        try {
                            OFXConversion.convertFileVanguard(pollDirPath + "\\" + filename.toString());
                            processed = true;
                        } catch (IOException e) {
                            logger.severe(e.toString());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }//Vanguard

                }
                if(filename.toString().endsWith(".qif")){
                    //.qif can be TSB OR Santander ...
                   if(filename.toString().startsWith(OfxgenGetPropertyValues.prefixSantanderFileName) && !(filename.toString().contains(OfxgenGetPropertyValues.convertedSantanderFileName)) ){
                       try {
                           OFXConversion.convertFileSantander(pollDirPath + "\\" + filename.toString());
                           processed = true;
                       } catch (IOException e) {
                           logger.severe(e.toString());
                       }
                    }//Santander
                    else {
                        if (filename.toString().endsWith(OfxgenGetPropertyValues.suffixTSB)) {
                            try {
                                OFXConversion.convertFileTSB(pollDirPath + "\\" + filename.toString());
                                processed = true;
                            } catch (IOException e) {
                                logger.severe(e.toString());
                            }
                        }
                    }//TSB
                } // .qif
                if(filename.toString().endsWith(".pdf")) {
                    try {
                        OFXConversion.convertFileChase(pollDirPath + "\\" + filename.toString());
                        processed = true;
                    } catch (IOException e) {
                        logger.severe(e.toString());
                    }
                }//chase

                if(!processed){
                    logger.info("I have done nothing with file:"+filename.toString());

                }
                processed = false;

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
