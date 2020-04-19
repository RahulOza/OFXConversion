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

    //TODO - properties should be enums ...
    /*
    enum ofxgenprops {
        POLLING_DIR("ofxgenprops.pollingDirPath"),
    }

     */
    //properties
    public static String pollingDirPath ="";
    public static String backgroundProcessingRequired = "";
    public static String prefixMarcusFilename = "";
    public static Double intialBalanceMarcus =0.0;
    public static String amazonAccountId = "";
    public static String rbsSelectAccountId = "";
    public static String marcusAccountId = "";
    public static String testFilePathMarcus ="";
    public static String testFilePathSelect ="";
    public static String testFilePathAmazon ="";
    public static String testFilePathTSB ="";
    public static Double testinitialBalanceMarcus =0.0;
    public static Double testinitialBalanceSelect =0.0;
    public static Double testinitialBalanceAmazon =0.0;
    public static Double testinitialBalanceTSB =0.0;

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
            prefixMarcusFilename = prop.getProperty("ofxgenprops.backgroundprocessing.prefixMarcusFilename");
            intialBalanceMarcus = Double.parseDouble(prop.getProperty("ofxgenprops.backgroundprocessing.intialBalanceMarcus"));
            amazonAccountId = prop.getProperty("ofxgenprops.amazonAccountId");
            rbsSelectAccountId = prop.getProperty("ofxgenprops.rbsSelectAccountId");
            marcusAccountId = prop.getProperty("ofxgenprops.marcusAccountId");
            testFilePathMarcus = prop.getProperty("ofxgenprops.testing.testFilePathMarcus");
            testFilePathSelect = prop.getProperty("ofxgenprops.testing.testFilePathSelect");
            testFilePathAmazon = prop.getProperty("ofxgenprops.testing.testFilePathAmazon");
            testFilePathTSB = prop.getProperty("ofxgenprops.testing.testFilePathTSB");
            testinitialBalanceMarcus = Double.parseDouble(prop.getProperty("ofxgenprops.testing.intialBalanceMarcus"));
            testinitialBalanceSelect = Double.parseDouble(prop.getProperty("ofxgenprops.testing.intialBalanceSelect"));
            testinitialBalanceAmazon = Double.parseDouble(prop.getProperty("ofxgenprops.testing.intialBalanceAmazon"));
            testinitialBalanceTSB = Double.parseDouble(prop.getProperty("ofxgenprops.testing.intialBalanceTSB"));



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


