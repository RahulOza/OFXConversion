package OFXConversion;

import OFXConversion.data.OfxgenGetPropertyValues;
import OFXConversion.data.TransactionList;
import OFXConversion.modelers.DataModelerAmazon;
import OFXConversion.modelers.DataModelerMarcus;
import OFXConversion.modelers.DataModelerRBSSelect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import static OFXConversion.OFXConversion.convertFileMarcus;
import static OFXConversion.OFXConversion.convertFileRBSSelect;
import static OFXConversion.OFXConversion.convertFileAmazon;
import static OFXConversion.OFXConversion.convertFileTSB;

import static OFXConversion.data.OfxgenGetPropertyValues.*;
import static org.junit.Assert.*;

public class OFXConversionTest {

    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());
    DecimalFormat df = new DecimalFormat("#.##");

    @Before
    public void setUp() throws Exception {

        df.setRoundingMode(RoundingMode.CEILING);
        try {
            OfxgenGetPropertyValues.getPropValues("ofxgen.properties");

        } catch (IOException exception) {
            logger.severe(exception.toString());
        }

    }

    @Test
    public void testMarcus() throws IOException {
        DataModelerMarcus DM = new DataModelerMarcus();

        // check if balance is correct ?
        logger.finer("testMarcus Started");
        TransactionList transactionList = DM.createTransactionList(testFilePathMarcus, testinitialBalanceMarcus);

        assertEquals(transactionList.getInitialBalance(), testinitialBalanceMarcus);
        assertEquals(transactionList.getFinalBalance(), testFinalBalanceMarcus);

        convertFileMarcus(OfxgenGetPropertyValues.testFilePathMarcus, OfxgenGetPropertyValues.intialBalanceMarcus);
        logger.finer("testMarcus Competed Successfully");
    }

    @Test
    public void testSelect() throws IOException {

        DataModelerRBSSelect DM = new DataModelerRBSSelect();

        // check if balance is correct ?
        logger.finer("testSelect Started");

        TransactionList transactionList = DM.createTransactionList(OfxgenGetPropertyValues.testFilePathSelect, OfxgenGetPropertyValues.testinitialBalanceSelect);

        assertEquals(transactionList.getInitialBalance(), OfxgenGetPropertyValues.testinitialBalanceSelect);
        assertEquals(transactionList.getFinalBalance(), OfxgenGetPropertyValues.testFinalBalanceSelect);

        convertFileRBSSelect(OfxgenGetPropertyValues.testFilePathSelect, OfxgenGetPropertyValues.testinitialBalanceSelect);
        logger.finer("testSelect Competed Successfully");
    }

    @Test
    public void testAmazon() throws IOException {
        DataModelerAmazon DM = new DataModelerAmazon();

        // check if balance is correct ?
        logger.finer("testAmazon Started");

        TransactionList transactionList = DM.createTransactionList(testFilePathAmazon, testinitialBalanceAmazon);

        assertEquals(transactionList.getInitialBalance(), testinitialBalanceAmazon);

        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));

        assertEquals(finalBalanceRounded, testFinalBalanceAmazon);

        convertFileAmazon(OfxgenGetPropertyValues.testFilePathAmazon, OfxgenGetPropertyValues.testinitialBalanceAmazon);
        logger.finer("testAmazon Competed Successfully");
    }

    @Test
    public void testTSB() throws IOException {
        logger.finer("testTSB Started");

        convertFileTSB(OfxgenGetPropertyValues.testFilePathTSB);
        logger.finer("testTSB Competed Successfully");
    }


    @After
    public void tearDown() throws Exception {
    }

}