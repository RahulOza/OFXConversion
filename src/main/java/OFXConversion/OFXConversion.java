package OFXConversion;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import OFXConversion.data.AllTransactions;
import OFXConversion.data.InvTransactionList;
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

    //TODO fix logging
    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());

    /* 5 Dec 2018 - Unfortunately they keep changing pdf format, which is a pain !!!
    Rahul has taken a decision to use csv format instead as that will not change.
    That however requires the previous balance to be input.
     */
    public static void convertFileByond(String fileName) throws IOException{
        DataModelerByond DM = new DataModelerByond();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName);

        transactionList.getTransactionsList().sort(new TransactionList());

        transactionList.printTransactionList();

        if(transactionList.getLength() > 0)
            OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.byondAccountId,OfxgenGetPropertyValues.byondAccountType);

    }
    public static void convertFileAmazon(String fileName, Double initialBalance) throws IOException{
        DataModelerAmazon DM = new DataModelerAmazon();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName,initialBalance);

        transactionList.printTransactionList();
        OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.amazonAccountId,OfxgenGetPropertyValues.amazonAccountType);


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

        transactionList.getTransactionsList().sort(new TransactionList());

        transactionList.printTransactionList();

        OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.marcusAccountId,OfxgenGetPropertyValues.marcusAccountType);

    }

    public static void convertFileVanguard(String fileName) throws Exception {
        DataModelerVanguard DM = new DataModelerVanguard();
        OfxGen OfGen = new OfxGen();

        AllTransactions transactionLists = DM.createTransactionListXSS(fileName);

        transactionLists.getCashTrans().getTransactionsList().sort(new TransactionList());

        transactionLists.getCashTrans().printTransactionList();

        transactionLists.getInvTrans().getInvTransactionsList().sort(new InvTransactionList());

        transactionLists.getInvTrans().printTransactionList();

        OfGen.ofxFileWriter(transactionLists.getCashTrans(),fileName, OfxgenGetPropertyValues.vanguardAccountId,OfxgenGetPropertyValues.vanguardAccountType);

        OfGen.ofxInvFileWriter(transactionLists.getInvTrans(),fileName,OfxgenGetPropertyValues.vanguardAccountId,OfxgenGetPropertyValues.vanguardAccountType);

    }

    public static void convertFileChase(String fileName) throws IOException {
        DataModelerChase DM = new DataModelerChase();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName);

        transactionList.getTransactionsList().sort(new TransactionList());

        transactionList.printTransactionList();

        OfGen.ofxFileWriter(transactionList,fileName, OfxgenGetPropertyValues.chaseAccountId,OfxgenGetPropertyValues.chaseAccountType);

    }

    public static void convertFileFreetrade(String fileName) throws Exception {
        DataModelerFreeTrade DM = new DataModelerFreeTrade();
        OfxGen OfGen = new OfxGen();

        AllTransactions transactionLists = DM.createTransactionList(fileName);

        transactionLists.getCashTrans().getTransactionsList().sort(new TransactionList());

        transactionLists.getCashTrans().printTransactionList();

        transactionLists.getInvTrans().getInvTransactionsList().sort(new InvTransactionList());

        transactionLists.getInvTrans().printTransactionList();

        OfGen.ofxFileWriter(transactionLists.getCashTrans(),fileName, OfxgenGetPropertyValues.freetradeAccountId,OfxgenGetPropertyValues.freetradeAccountType);

        OfGen.ofxInvFileWriter(transactionLists.getInvTrans(),fileName,OfxgenGetPropertyValues.freetradeAccountId,OfxgenGetPropertyValues.freetradeAccountType);

    }


    public static void convertFileDodl(String fileName) throws Exception {
        DataModelerDodl DM = new DataModelerDodl();
        OfxGen OfGen = new OfxGen();

        AllTransactions transactionLists = DM.createTransactionList(fileName);

        transactionLists.getCashTrans().getTransactionsList().sort(new TransactionList());

        transactionLists.getCashTrans().printTransactionList();

        transactionLists.getInvTrans().getInvTransactionsList().sort(new InvTransactionList());

        transactionLists.getInvTrans().printTransactionList();

        OfGen.ofxFileWriter(transactionLists.getCashTrans(),fileName, OfxgenGetPropertyValues.dodlAccountId,OfxgenGetPropertyValues.dodlAccountType);

        OfGen.ofxInvFileWriter(transactionLists.getInvTrans(),fileName,OfxgenGetPropertyValues.dodlAccountId,OfxgenGetPropertyValues.dodlAccountType);

    }

    public static void convertFileTrading212(String fileName) throws Exception {
        DataModelerTrading212 DM = new DataModelerTrading212();
        OfxGen OfGen = new OfxGen();

        AllTransactions transactionLists = DM.createTransactionList(fileName);

        transactionLists.getCashTrans().getTransactionsList().sort(new TransactionList());

        transactionLists.getCashTrans().printTransactionList();

        transactionLists.getInvTrans().getInvTransactionsList().sort(new InvTransactionList());

        transactionLists.getInvTrans().printTransactionList();

        OfGen.ofxFileWriter(transactionLists.getCashTrans(),fileName, OfxgenGetPropertyValues.trading212AccountId,OfxgenGetPropertyValues.trading212AccountType);

        OfGen.ofxInvFileWriter(transactionLists.getInvTrans(),fileName,OfxgenGetPropertyValues.trading212AccountId,OfxgenGetPropertyValues.trading212AccountType);

    }

    public static void convertFileTrading212Card(String fileName) throws Exception {
        DataModelerTrading212Card DM = new DataModelerTrading212Card();
        OfxGen OfGen = new OfxGen();

        AllTransactions transactionLists = DM.createTransactionList(fileName);

        transactionLists.getCashTrans().getTransactionsList().sort(new TransactionList());

        transactionLists.getCashTrans().printTransactionList();

        transactionLists.getInvTrans().getInvTransactionsList().sort(new InvTransactionList());

        transactionLists.getInvTrans().printTransactionList();

        OfGen.ofxFileWriter(transactionLists.getCashTrans(),fileName, OfxgenGetPropertyValues.trading212CardAccountId,OfxgenGetPropertyValues.trading212CardAccountType);

        OfGen.ofxInvFileWriter(transactionLists.getInvTrans(),fileName,OfxgenGetPropertyValues.trading212CardAccountId,OfxgenGetPropertyValues.trading212CardAccountType);

    }

    public static void convertFileEquate(String fileName) throws Exception {
        DataModelerEquate DM = new DataModelerEquate();
        OfxGen OfGen = new OfxGen();

        AllTransactions transactionLists = DM.createTransactionList(fileName);

        transactionLists.getCashTrans().getTransactionsList().sort(new TransactionList());

        transactionLists.getCashTrans().printTransactionList();

        transactionLists.getInvTrans().getInvTransactionsList().sort(new InvTransactionList());

        transactionLists.getInvTrans().printTransactionList();

        OfGen.ofxFileWriter(transactionLists.getCashTrans(),fileName, OfxgenGetPropertyValues.equateAccountId,OfxgenGetPropertyValues.equateAccountType);

        OfGen.ofxInvFileWriter(transactionLists.getInvTrans(),fileName,OfxgenGetPropertyValues.equateAccountId,OfxgenGetPropertyValues.equateAccountType);

    }

    public static void convertFileChip(String fileName) throws IOException {
        DataModelerChip DM = new DataModelerChip();
        OfxGen OfGen = new OfxGen();

        TransactionList transactionList = DM.createTransactionList(fileName);

        transactionList.getTransactionsList().sort(new TransactionList());

        transactionList.printTransactionList();

        OfGen.ofxFileWriter(transactionList, fileName, OfxgenGetPropertyValues.chipAccountId, OfxgenGetPropertyValues.chipAccountType);
    }

    public static void main(String[] args) throws IOException {

        Double myVersion = 2.5;
        String myVersionDetails = "equate statements support";


        logger.info(" ######### OfxGen v"+myVersion+" ("+ myVersionDetails +") ##########");
        if(args.length < 1){
            logger.severe("Missing ofxgen.properties file as parameter");
        }

        for (String arg:args){
            // if we have a parameter, it's the properties file for running local background processing.
            try {
                OfxgenGetPropertyValues.getPropValues(arg);
                //check if we have the right version of props file
                if(!OfxgenGetPropertyValues.version.equals(myVersion)){
                    throw new Exception("Incorrect properties version file, program version:"+myVersion+" properties file version:"+OfxgenGetPropertyValues.version);
                }

            } catch (IOException exception) {
                logger.severe(exception.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(OfxgenGetPropertyValues.backgroundProcessingRequired.equalsIgnoreCase("True")) {
                Path dir = Paths.get(OfxgenGetPropertyValues.pollingDirPath);
                BackGroundCoversionProcess bckProcess = new BackGroundCoversionProcess(dir);
                bckProcess.run();
            }
            else {
                //If background processing required = false
                SpringApplication.run(OFXConversion.class, args);
            }
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
