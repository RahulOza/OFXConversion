package OFXConversion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import OFXConversion.storage.StorageProperties;
import OFXConversion.storage.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class OFXConversion {

    //TODO Change project name from pdf to ofx to OFXConverter
    //TODO remove one of the manifext files
    //TODO fix logging
    final static Logger logger = Logger.getLogger(OFXConversion.class.getName());
    public static Double initialBalance = 0.0;


    /* 5 Dec 2018 - Unfortunately they keep changing pdf format, which is a pain !!!
    Rahul has taken a decision to use csv format instead as that will not change.
    That however requires the previous balance to be input.
     */
    static void convertFileRBSSelect(String fileName, Double initialBalance) throws IOException{
        DataModelerRBSSelect DM = new DataModelerRBSSelect();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        transactionList.printTransactionList();
        OfGen.ofxFileWriteAmazon(transactionList,fileName,"select");

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }

    }
    static void convertFileAmazon(String fileName, Double initialBalance) throws IOException{
        DataModelerAmazon DM = new DataModelerAmazon();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        transactionList.printTransactionList();
        OfGen.ofxFileWriteAmazon(transactionList,fileName,"amazon");

        if(!transactionList.datesOutOfSequence()){
            logger.info("Process Competed Successfully");
        }
        else{
            logger.info("Dates are out of Sequence, output may be WRONG!!");

        }

    }
    static void convertFileTSB(String fileName) throws IOException {
        DataModelerTSB DMTsb = new DataModelerTSB();

        DMTsb.convert(fileName);
        logger.info("Process Competed Successfully");
    }

    public static void main(String[] args) throws IOException {
        List<String> listOfPDFs = new ArrayList<String>();
        String programType = null;

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
