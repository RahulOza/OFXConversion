package OFXConversion.data;

import OFXConversion.OFXConversion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

public class OfxgenGetPropertyValues {

    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());

    //properties
    public static Double version=0.0;
    public static String pollingDirPath ="";
    public static String backgroundProcessingRequired = "";
    public static String prefixMarcusFilename = "";
    public static Double intialBalanceMarcus =0.0;
    public static String amazonAccountId = "";
    public static String byondAccountId = "";
    public static String marcusAccountId = "";
    public static String testFilePathMarcus ="";
    public static String testFilePathByond ="";
    public static String testFilePathAmazon ="";
    public static String testFilePathTSB ="";
    public static Double testinitialBalanceMarcus =0.0;
    public static Double testinitialBalanceByond =0.0;
    public static Double testinitialBalanceAmazon =0.0;
    public static Double testinitialBalanceTSB =0.0;
    public static String suffixTSB="";
    public static String prefixByondFileName ="";
    public static String prefixAmazonFileName ="";
    public static String amazonAccountType="";
    public static String byondAccountType="";
    public static String marcusAccountType="";
    public static Double testFinalBalanceByond =0.0;
    public static Double testFinalBalanceMarcus = 0.0;
    public static Double testFinalBalanceAmazon = 0.0;
    public static Double initialBalanceAmazon = 0.0;
    public static String prefixSantanderFileName ="";
    public static String convertedSantanderFileName ="";
    public static int maxQifCommentsChars =0;
    public static String testFilePathSantander ="";
    public static String tsbDestFileName ="";

    public static String symbolMapFile ="";

    //Byond
    public static String prefixByondFileName1 ="";
    public static Double testinitialBalanceByond1 =0.0;
    public static Double testFinalBalanceByond1 =0.0;
    public static String testFilePathByond1 ="";
    public static String prefixByondFileName2 ="";
    public static Double testinitialBalanceByond2 =0.0;
    public static Double testFinalBalanceByond2 =0.0;
    public static String testFilePathByond2 ="";

    //Vanguard
    public static String vanguardAccountType = "";
    public static String vanguardAccountId = "";
    public static int vanguardDateChars=0;
    public static String prefixVanguardFileName ="";
    public static String testFilePathVanguard ="";
    public static Double testintialBalanceVanguard =0.0;
    public static Double testfinalBalanceVanguard =0.0;
    public static int transSheetNumber;
    public static String testFilePathVanguard1 ="";
    public static Double testintialBalanceVanguard1 =0.0;
    public static Double testfinalBalanceVanguard1 =0.0;


    //chase
    public static String chaseAccountType = "";
    public static String chaseAccountId = "";
    public static String testFilePathChase ="";
    public static Double testintialBalanceChase =0.0;
    public static Double testfinalBalanceChase =0.0;
    public static String prefixChaseFileName ="";

    //chase - additional test case
    public static String testFilePathChase1 ="";
    public static Double testintialBalanceChase1 =0.0;
    public static Double testfinalBalanceChase1 =0.0;

    //chase - additional test case
    public static String testFilePathChase2 ="";
    public static Double testintialBalanceChase2 =0.0;
    public static Double testfinalBalanceChase2 =0.0;


    //freetrade
    public static String freetradeAccountType = "";
    public static String freetradeAccountId = "";
    public static String testFilePathfreetrade ="";
    public static Double testintialBalancefreetrade =0.0;
    public static Double testfinalBalancefreetrade =0.0;
    public static String prefixfreetradeFileName ="";

    //Trading 212
    public static String trading212AccountType = "";
    public static String trading212AccountId = "";
    public static String testFilePathTrading212 ="";
    public static Double testintialBalanceTrading212 =0.0;
    public static Double testfinalBalanceTrading212 =0.0;
    public static String prefixTrading212FileName ="";


    //Dodl
    public static String dodlAccountType = "";
    public static String dodlAccountId = "";
    public static String testFilePathDodl ="";
    public static Double testintialBalanceDodl =0.0;
    public static Double testfinalBalanceDodl =0.0;
    public static String prefixDodlFileName ="";

    //Chip
    public static String chipAccountType = "";
    public static String chipAccountId = "";
    public static String testFilePathChip = "";
    public static Double testintialBalanceChip = 0.0;
    public static Double testfinalBalanceChip = 0.0;
    public static String prefixChipFileName = "";

    //Equate
    public static String equateAccountType = "";
    public static String equateAccountId = "";
    public static String testFilePathEquate ="";
    public static Double testintialBalanceEquate =0.0;
    public static Double testfinalBalanceEquate =0.0;
    public static String prefixEquateFileName ="";

    //Trading 212Card
    public static String trading212CardAccountType = "";
    public static String trading212CardAccountId = "";
    public static String testFilePathTrading212Card ="";
    public static Double testintialBalanceTrading212Card =0.0;
    public static Double testfinalBalanceTrading212Card =0.0;
    public static String prefixTrading212CardFileName ="";


    //local variables
    private static String result = "";
    private static InputStream inputStream;

    public static String getPropValues(String propFileName) throws IOException {

        try {
            Properties prop = new Properties();

            inputStream = OfxgenGetPropertyValues.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            Date time = new Date(System.currentTimeMillis());

            version = Double.parseDouble(prop.getProperty("ofxgenprops.version"));

            // get the property value and print it out
            pollingDirPath = prop.getProperty("ofxgenprops.backgroundprocessing.pollingDirPath");
            backgroundProcessingRequired = prop.getProperty("ofxgenprops.backgroundprocessing");
            prefixMarcusFilename = prop.getProperty("ofxgenprops.backgroundprocessing.marcus.prefixMarcusFilename");
            intialBalanceMarcus = Double.parseDouble(prop.getProperty("ofxgenprops.marcus.intialBalanceMarcus"));
            amazonAccountId = prop.getProperty("ofxgenprops.amazon.amazonAccountId");
            byondAccountId = prop.getProperty("ofxgenprops.byond.byondAccountId");
            marcusAccountId = prop.getProperty("ofxgenprops.marcus.marcusAccountId");
            testFilePathMarcus = prop.getProperty("ofxgenprops.testing.marcus.testFilePathMarcus");
            testFilePathByond = prop.getProperty("ofxgenprops.testing.byond.testFilePathByond");
            testFilePathAmazon = prop.getProperty("ofxgenprops.testing.amazon.testFilePathAmazon");
            testFilePathTSB = prop.getProperty("ofxgenprops.testing.tsb.testFilePathTSB");
            testinitialBalanceMarcus = Double.parseDouble(prop.getProperty("ofxgenprops.testing.marcus.intialBalanceMarcus"));
            testinitialBalanceByond = Double.parseDouble(prop.getProperty("ofxgenprops.testing.byond.intialBalanceByond"));
            testinitialBalanceAmazon = Double.parseDouble(prop.getProperty("ofxgenprops.testing.amazon.intialBalanceAmazon"));
            testinitialBalanceTSB = Double.parseDouble(prop.getProperty("ofxgenprops.testing.tsb.intialBalanceTSB"));
            suffixTSB = prop.getProperty("ofxgenprops.tsb.suffixTSB");

            prefixByondFileName = prop.getProperty("ofxgenprops.backgroundprocessing.byond.prefixByondFileName");
            amazonAccountType = prop.getProperty("ofxgenprops.amazon.accountype");
            marcusAccountType = prop.getProperty("ofxgenprops.marcus.accountype");
            byondAccountType = prop.getProperty("ofxgenprops.byond.accountype");
            testFinalBalanceByond = Double.parseDouble(prop.getProperty("ofxgenprops.testing.byond.finalBalanceByond"));
            testFinalBalanceMarcus = Double.parseDouble(prop.getProperty("ofxgenprops.testing.marcus.finalBalanceMarcus"));
            testFinalBalanceAmazon = Double.parseDouble(prop.getProperty("ofxgenprops.testing.amazon.finalBalanceAmazon"));
            prefixAmazonFileName = prop.getProperty("ofxgenprops.backgroundprocessing.amazon.prefixAmazonFileName");
            initialBalanceAmazon = Double.parseDouble(prop.getProperty("ofxgenprops.amazon.intialBalanceAmazon"));
            prefixSantanderFileName = prop.getProperty("ofxgenprops.backgroundprocessing.santander.prefixSantanderFileName");
            convertedSantanderFileName = prop.getProperty("ofxgenprops.backgroundprocessing.santander.convertedSantanderFileName");
            maxQifCommentsChars= Integer.parseInt(prop.getProperty("ofxgenprops.santander.maxQifCommentsChars"));
            testFilePathSantander = prop.getProperty("ofxgenprops.testing.santander.testFilePathSantander");
            tsbDestFileName = prop.getProperty("ofxgenprops.tsb.destFileName");

            //Byond
            testinitialBalanceByond1 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.byond.intialBalanceByond1"));
            testFinalBalanceByond1 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.byond.finalBalanceByond1"));
            testFilePathByond1 = prop.getProperty("ofxgenprops.testing.byond.testFilePathByond1");
            prefixByondFileName1 = prop.getProperty("ofxgenprops.backgroundprocessing.byond.prefixByondFileName1");

            testinitialBalanceByond2 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.byond.intialBalanceByond2"));
            testFinalBalanceByond2 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.byond.finalBalanceByond2"));
            testFilePathByond2 = prop.getProperty("ofxgenprops.testing.byond.testFilePathByond2");
            prefixByondFileName2 = prop.getProperty("ofxgenprops.backgroundprocessing.byond.prefixByondFileName2");

            //Vanguard
            vanguardAccountType = prop.getProperty("ofxgenprops.vanguard.accountype");
            vanguardAccountId = prop.getProperty("ofxgenprops.vanguard.vanguardAccountId");
            vanguardDateChars= Integer.parseInt(prop.getProperty("ofxgenprops.vanguard.dateChars"));
            prefixVanguardFileName = prop.getProperty("ofxgenprops.backgroundprocessing.vanguard.prefixVanguardFileName");
            testFilePathVanguard = prop.getProperty("ofxgenprops.testing.vanguard.testFilePathVanguard");
            testintialBalanceVanguard = Double.parseDouble(prop.getProperty("ofxgenprops.testing.vanguard.intialBalanceVanguard"));
            testfinalBalanceVanguard = Double.parseDouble(prop.getProperty("ofxgenprops.testing.vanguard.finalBalanceVanguard"));
            transSheetNumber = Integer.parseInt(prop.getProperty("ofxgenprops.vanguard.transSheetNumber"));
            symbolMapFile = prop.getProperty("ofxgenprops.symbolMapFile");
            testFilePathVanguard1 = prop.getProperty("ofxgenprops.testing.vanguard.testFilePathVanguard1");
            testintialBalanceVanguard1 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.vanguard.intialBalanceVanguard1"));
            testfinalBalanceVanguard1 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.vanguard.finalBalanceVanguard1"));


            //chase
            chaseAccountType = prop.getProperty("ofxgenprops.chase.accountype");
            chaseAccountId = prop.getProperty("ofxgenprops.chase.chaseAccountId");
            testFilePathChase = prop.getProperty("ofxgenprops.testing.chase.testFilePathChase");
            testintialBalanceChase = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.initialBalanceChase"));
            testfinalBalanceChase = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.finalBalanceChase"));
            prefixChaseFileName = prop.getProperty("ofxgenprops.backgroundprocessing.chase.chaseFileName");

            //chase - additional test case
            testFilePathChase1 = prop.getProperty("ofxgenprops.testing.chase.testFilePathChase1");
            testintialBalanceChase1 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.initialBalanceChase1"));
            testfinalBalanceChase1 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.finalBalanceChase1"));

            //chase - additional test case
            testFilePathChase2 = prop.getProperty("ofxgenprops.testing.chase.testFilePathChase2");
            testintialBalanceChase2 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.initialBalanceChase2"));
            testfinalBalanceChase2 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.finalBalanceChase2"));


            //FreeTrade
            freetradeAccountType = prop.getProperty("ofxgenprops.freetrade.accountype");
            freetradeAccountId = prop.getProperty("ofxgenprops.freetrade.freetradeAccountId");
            testFilePathfreetrade = prop.getProperty("ofxgenprops.testing.freetrade.testFilePathfreetrade");
            testintialBalancefreetrade = Double.parseDouble(prop.getProperty("ofxgenprops.testing.freetrade.initialBalancefreetrade"));
            testfinalBalancefreetrade = Double.parseDouble(prop.getProperty("ofxgenprops.testing.freetrade.finalBalancefreetrade"));
            prefixfreetradeFileName = prop.getProperty("ofxgenprops.backgroundprocessing.freetrade.prefixFreetradedFileName");


            //Trading212
            trading212AccountType = prop.getProperty("ofxgenprops.trading212.accountype");
            trading212AccountId = prop.getProperty("ofxgenprops.trading212.trading212AccountId");
            testFilePathTrading212 = prop.getProperty("ofxgenprops.testing.trading212.testFilePathTrading212");
            testintialBalanceTrading212 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.trading212.initialBalanceTrading212"));
            testfinalBalanceTrading212 = Double.parseDouble(prop.getProperty("ofxgenprops.testing.trading212.finalBalanceTrading212"));
            prefixTrading212FileName = prop.getProperty("ofxgenprops.backgroundprocessing.trading212.trading212FileName");


            //Dodl
            dodlAccountType = prop.getProperty("ofxgenprops.dodl.accountype");
            dodlAccountId = prop.getProperty("ofxgenprops.dodl.dodlAccountId");
            testFilePathDodl = prop.getProperty("ofxgenprops.testing.dodl.testFilePathDodl");
            testintialBalanceDodl = Double.parseDouble(prop.getProperty("ofxgenprops.testing.dodl.initialBalanceDodl"));
            testfinalBalanceDodl = Double.parseDouble(prop.getProperty("ofxgenprops.testing.dodl.finalBalanceDodl"));
            prefixDodlFileName = prop.getProperty("ofxgenprops.backgroundprocessing.dodl.dodlFileName");


            //Chip
            chipAccountType = prop.getProperty("ofxgenprops.chip.accountype");
            chipAccountId = prop.getProperty("ofxgenprops.chip.dodlAccountId");
            testFilePathChip = prop.getProperty("ofxgenprops.testing.chip.testFilePathChip");
            testintialBalanceChip = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chip.initialBalanceChip"));
            testfinalBalanceChip = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chip.finalBalanceChip"));
            prefixChipFileName = prop.getProperty("ofxgenprops.backgroundprocessing.chip.chipFileName");

            //Equate
            equateAccountType = prop.getProperty("ofxgenprops.equate.accountype");
            equateAccountId = prop.getProperty("ofxgenprops.equate.equateAccountId");
            testFilePathEquate = prop.getProperty("ofxgenprops.testing.Equate.testFilePathEquate");
            testintialBalanceEquate = Double.parseDouble(prop.getProperty("ofxgenprops.testing.Equate.initialBalanceEquate"));
            testfinalBalanceEquate = Double.parseDouble(prop.getProperty("ofxgenprops.testing.Equate.finalBalanceEquate"));
            prefixEquateFileName = prop.getProperty("ofxgenprops.backgroundprocessing.equate.equateFileNam");


            //Trading212Card
            trading212CardAccountType = prop.getProperty("ofxgenprops.trading212Card.accountype");
            trading212CardAccountId = prop.getProperty("ofxgenprops.trading212Card.trading212AccountId");
            testFilePathTrading212Card = prop.getProperty("ofxgenprops.testing.trading212Card.testFilePathTrading212");
            testintialBalanceTrading212Card = Double.parseDouble(prop.getProperty("ofxgenprops.testing.trading212Card.initialBalanceTrading212"));
            testfinalBalanceTrading212Card = Double.parseDouble(prop.getProperty("ofxgenprops.testing.trading212Card.finalBalanceTrading212"));
            prefixTrading212CardFileName = prop.getProperty("ofxgenprops.backgroundprocessing.trading212Card.trading212FileName");

            logger.info(result);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }
}


