package OFXConversion;

import OFXConversion.data.AllTransactions;
import OFXConversion.data.OfxgenGetPropertyValues;
import OFXConversion.data.TransactionList;
import OFXConversion.modelers.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import static OFXConversion.OFXConversion.*;
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
            throw new Exception(exception.toString());
        }

    }

    @Test
    public void testMarcus() throws IOException {
        DataModelerMarcus DM = new DataModelerMarcus();

        // check if balance is correct ?
        logger.info("testMarcus Started");
        TransactionList transactionList = DM.createTransactionList(testFilePathMarcus, testinitialBalanceMarcus);

        assertEquals(transactionList.getInitialBalance(), testinitialBalanceMarcus);
        assertEquals(transactionList.getFinalBalance(), testFinalBalanceMarcus);

        convertFileMarcus(OfxgenGetPropertyValues.testFilePathMarcus, OfxgenGetPropertyValues.intialBalanceMarcus);
        logger.info("testMarcus Competed Successfully");
    }

    @Test
    public void testByond() throws IOException {

        DataModelerByond DM = new DataModelerByond();

        logger.info("testByond Started");

        TransactionList transactionList = DM.createTransactionList(OfxgenGetPropertyValues.testFilePathByond);

        assertEquals(transactionList.getInitialBalance(), OfxgenGetPropertyValues.testinitialBalanceByond);

        df.setRoundingMode(RoundingMode.DOWN);
        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));

        assertEquals(finalBalanceRounded, OfxgenGetPropertyValues.testFinalBalanceByond);

        convertFileByond(OfxgenGetPropertyValues.testFilePathByond);
        logger.info("testByond Competed Successfully");
    }

    @Test
    public void testAmazon() throws IOException {
        DataModelerAmazon DM = new DataModelerAmazon();

        // check if balance is correct ?
        logger.info("testAmazon Started");

        TransactionList transactionList = DM.createTransactionList(testFilePathAmazon, testinitialBalanceAmazon);

        assertEquals(transactionList.getInitialBalance(), testinitialBalanceAmazon);

        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));

        assertEquals(finalBalanceRounded, testFinalBalanceAmazon);

        convertFileAmazon(OfxgenGetPropertyValues.testFilePathAmazon, OfxgenGetPropertyValues.testinitialBalanceAmazon);
        logger.info("testAmazon Competed Successfully");
    }

    @Test
    public void testTSB() throws IOException {
        logger.info("testTSB Started");

        convertFileTSB(OfxgenGetPropertyValues.testFilePathTSB);
        logger.info("testTSB Competed Successfully");
    }

    @Test
    public void testSantander() throws IOException{
        logger.info("testSantander Started");
        convertFileSantander(OfxgenGetPropertyValues.testFilePathSantander);

        logger.info("testSantander Competed Successfully");
    }

    @Test
    public void testVanguard() throws Exception {

        DataModelerVanguard DM = new DataModelerVanguard();

        logger.info("testVanguard Started");

        AllTransactions alltransactionLists = DM.createTransactionList(testFilePathVanguard);

        alltransactionLists.getInvTrans().printTransactionList();
        alltransactionLists.getCashTrans().printTransactionList();

       Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceVanguard);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceVanguard);

        convertFileVanguard(OfxgenGetPropertyValues.testFilePathVanguard);

        logger.info("testVanguard Competed Successfully");
    }


    @Test
    public void testChase() throws IOException{

        DataModelerChase DM = new DataModelerChase();

        logger.info("testChase Started");

        TransactionList transactionList = DM.createTransactionList(testFilePathChase);

        Double initialBalanceRounded = Double.parseDouble(df.format(transactionList.getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceChase);

        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceChase);

       convertFileChase(OfxgenGetPropertyValues.testFilePathChase);

        logger.info("testChase Competed Successfully");
    }

    @After
    public void tearDown()  {
    }

}