package OFXConversion;

import OFXConversion.data.OfxgenGetPropertyValues;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static OFXConversion.OFXConversion.convertFileMarcus;
import static OFXConversion.OFXConversion.convertFileRBSSelect;
import static OFXConversion.OFXConversion.convertFileAmazon;
import static OFXConversion.OFXConversion.convertFileTSB;

import static OFXConversion.data.OfxgenGetPropertyValues.*;
import static org.junit.Assert.*;

public class OFXConversionTest {

    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());

    @Before
    public void setUp() throws Exception {

        try {
            OfxgenGetPropertyValues.getPropValues("ofxgen.properties");

        } catch (IOException exception) {
            logger.severe(exception.toString());
        }

    }

    @Test
    public void testMarcus() throws IOException {
        // check if balance is correct ?
        //logger.info("testMarcus Started");

        convertFileMarcus(OfxgenGetPropertyValues.testFilePathMarcus, OfxgenGetPropertyValues.intialBalanceMarcus);
        //logger.info("testMarcus Competed Successfully");
    }

    @Test
    public void testSelect() throws IOException {
        // check if balance is correct ?
        //logger.info("testSelect Started");

        convertFileRBSSelect(OfxgenGetPropertyValues.testFilePathSelect, OfxgenGetPropertyValues.testinitialBalanceSelect);
        //logger.info("testSelect Competed Successfully");
    }

    @Test
    public void testAmazon() throws IOException {
        // check if balance is correct ?
        //logger.info("testAmazon Started");

        convertFileAmazon(OfxgenGetPropertyValues.testFilePathAmazon, OfxgenGetPropertyValues.testinitialBalanceAmazon);
       // logger.info("testAmazon Competed Successfully");
    }

    @Test
    public void testTSB() throws IOException {
        //logger.info("testTSB Started");

        convertFileTSB(OfxgenGetPropertyValues.testFilePathTSB);
        //logger.info("testTSB Competed Successfully");
    }


    @After
    public void tearDown() throws Exception {
    }

}