package OFXConversion;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.logging.Logger;

import OFXConversion.data.OfxgenGetPropertyValues;
import OFXConversion.modelers.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import OFXConversion.storage.StorageProperties;
import OFXConversion.storage.StorageService;
import OFXConversion.data.TransactionList;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class OFXConversion {

    //TODO remove one of the manifext files
    //TODO fix logging
    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());
    public static Double initialBalance = 0.0;

    //TODO why not use this real account number - 7365 0100 0067 4567, this account number however should match money

    /* 5 Dec 2018 - Unfortunately they keep changing pdf format, which is a pain !!!
    Rahul has taken a decision to use csv format instead as that will not change.
    That however requires the previous balance to be input.
     */
    public static void convertFileByond(String fileName, Double initialBalance) throws IOException{
        DataModelerByond DM = new DataModelerByond();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        Collections.sort(transactionList.getTransactionsList(), new TransactionList());

        transactionList.printTransactionList();

        if(transactionList.getLength() > 0)
            OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.byondAccountId,OfxgenGetPropertyValues.byondAccountType);

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }

    }
    public static void convertFileAmazon(String fileName, Double initialBalance) throws IOException{
        DataModelerAmazon DM = new DataModelerAmazon();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        transactionList.printTransactionList();
        OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.amazonAccountId,OfxgenGetPropertyValues.amazonAccountType);

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }

    }
    public static void convertFileTSB(String fileName) throws IOException {
        DataModelerTSB DMTsb = new DataModelerTSB();

        DMTsb.convert(fileName);
        logger.info("Process Competed Successfully");
    }

    public static void convertFileSantander(String fileName) throws IOException {
        DataModelerSantander DMTSan = new DataModelerSantander();

        DMTSan.convert(fileName);
        logger.info("Process Competed Successfully");
    }

    public static void convertFileMarcus(String fileName, Double initialBalance) throws IOException {
        DataModelerMarcus DM = new DataModelerMarcus();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        Collections.sort(transactionList.getTransactionsList(), new TransactionList());

        transactionList.printTransactionList();

        OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.marcusAccountId,OfxgenGetPropertyValues.marcusAccountType);

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }
    }

    public static void convertFileVanguard(String fileName, Double initialBalance) throws IOException {
        DataModelerVanguard DM = new DataModelerVanguard();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        Collections.sort(transactionList.getTransactionsList(), new TransactionList());

        transactionList.printTransactionList();

        OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.vanguardAccountId,OfxgenGetPropertyValues.vanguardAccountType);

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }
    }

    public static void main(String[] args) throws IOException {

        if(args.length < 1){
            SpringApplication.run(OFXConversion.class, args);
            //logger.severe("Missing ofxgen.properites file as parameter");
        }
        for (String arg:args){
            // if we have a parameter, its the properties file for running local background processing.
            try {
                OfxgenGetPropertyValues.getPropValues(arg);

            } catch (IOException exception) {
                logger.severe(exception.toString());
            }
            if(OfxgenGetPropertyValues.backgroundProcessingRequired.equalsIgnoreCase("True")) {
                Path dir = Paths.get(OfxgenGetPropertyValues.pollingDirPath);
                BackGroundCoversionProcess bckProcess = new BackGroundCoversionProcess(dir);
                bckProcess.run();
            }
            //SpringApplication.run(OFXConversion.class, args);
        }
    }

  @Bean
    CommandLineRunner init(StorageService storageService) {
      return (args) -> {
          storageService.deleteAll();
          storageService.init();
      };
  }

}
