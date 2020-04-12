package OFXConversion;

import java.io.IOException;
import java.util.logging.Logger;

import OFXConversion.modelers.DataModelerAmazon;
import OFXConversion.modelers.DataModelerMarcus;
import OFXConversion.modelers.DataModelerRBSSelect;
import OFXConversion.modelers.DataModelerTSB;
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
    final static Logger logger = Logger.getLogger(OFXConversion.class.getName());
    public static Double initialBalance = 0.0;


    //TODO why not use this real account number - 7365 0100 0067 4567, this account number however should match money
    private static final String amazonAccountId = "44000000";
    private static final String rbsSelectAccountId = "139481331";
    private static final String marcusAccountId = "44000001";

    /* 5 Dec 2018 - Unfortunately they keep changing pdf format, which is a pain !!!
    Rahul has taken a decision to use csv format instead as that will not change.
    That however requires the previous balance to be input.
     */
    public static void convertFileRBSSelect(String fileName, Double initialBalance) throws IOException{
        DataModelerRBSSelect DM = new DataModelerRBSSelect();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        transactionList.printTransactionList();
        OfGen.ofxFileWriter(transactionList,fileName, rbsSelectAccountId);

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
        OfGen.ofxFileWriter(transactionList,fileName, amazonAccountId);

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

    public static void convertFileMarcus(String fileName, Double initialBalance) throws IOException {
        DataModelerMarcus DM = new DataModelerMarcus();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);


        transactionList.printTransactionList();
        OfGen.ofxFileWriter(transactionList,fileName, marcusAccountId);

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }
    }

    public static void main(String[] args) throws IOException {

        SpringApplication.run(OFXConversion.class, args);

    }

  @Bean
    CommandLineRunner init(StorageService storageService) {
      return (args) -> {
          storageService.deleteAll();
          storageService.init();
      };
  }

}
