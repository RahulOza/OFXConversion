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

    //TODO A number of converted file names are not used ?
    //Vanguard
    public static String vanguardAccountType = "";
    public static String vanguardAccountId = "";
    public static int vanguardDateChars=0;
    public static String prefixVanguardFileName ="";
    public static String testFilePathVanguard ="";
    public static Double testintialBalanceVanguard =0.0;
    public static Double testfinalBalanceVanguard =0.0;
    public static String convertedVanguardCashFileName ="";
    public static String convertedVanguardInvFileName ="";
    public static int transSheetNumber;
    public static String vanguardSymbolMapFile ="";

    //chase
    public static String chaseAccountType = "";
    public static String chaseAccountId = "";
    public static String testFilePathChase ="";
    public static Double testintialBalanceChase =0.0;
    public static Double testfinalBalanceChase =0.0;
    public static String convertedChaseFileName ="";

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

            //Vanguard
            vanguardAccountType = prop.getProperty("ofxgenprops.vanguard.accountype");
            vanguardAccountId = prop.getProperty("ofxgenprops.vanguard.vanguardAccountId");
            vanguardDateChars= Integer.parseInt(prop.getProperty("ofxgenprops.vanguard.dateChars"));
            prefixVanguardFileName = prop.getProperty("ofxgenprops.backgroundprocessing.vanguard.prefixVanguardFileName");
            testFilePathVanguard = prop.getProperty("ofxgenprops.testing.vanguard.testFilePathVanguard");
            testintialBalanceVanguard = Double.parseDouble(prop.getProperty("ofxgenprops.testing.vanguard.intialBalanceVanguard"));
            convertedVanguardCashFileName = prop.getProperty("ofxgenprops.vanguard.convertedVanguardCashFileName");
            convertedVanguardInvFileName = prop.getProperty("ofxgenprops.vanguard.convertedVanguardInvFileName");
            testfinalBalanceVanguard = Double.parseDouble(prop.getProperty("ofxgenprops.testing.vanguard.finalBalanceVanguard"));
            transSheetNumber = Integer.parseInt(prop.getProperty("ofxgenprops.vanguard.transSheetNumber"));
            vanguardSymbolMapFile = prop.getProperty("ofxgenprops.vanguard.symbolMapFile");
            //chase
            chaseAccountType = prop.getProperty("ofxgenprops.chase.accountype");
            chaseAccountId = prop.getProperty("ofxgenprops.chase.chaseAccountId");
            testFilePathChase = prop.getProperty("ofxgenprops.testing.chase.testFilePathChase");
            testintialBalanceChase = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.initialBalanceChase"));
            testfinalBalanceChase = Double.parseDouble(prop.getProperty("ofxgenprops.testing.chase.finalBalanceChase"));
            convertedChaseFileName = prop.getProperty("ofxgenprops.backgroundprocessing.chase.convertedChaseFileName");


            result = "Loaded property = pollingDirPath = " + pollingDirPath;

            logger.info(result);


        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }
}


