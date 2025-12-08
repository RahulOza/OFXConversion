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

        assertNotEquals(transactionList.getInitialBalance(), OfxgenGetPropertyValues.testinitialBalanceByond);

        df.setRoundingMode(RoundingMode.DOWN);
        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));

        assertNotEquals(finalBalanceRounded, OfxgenGetPropertyValues.testFinalBalanceByond);

        convertFileByond(OfxgenGetPropertyValues.testFilePathByond);
        logger.info("testByond Competed Successfully");
    }
    @Test
    public void testByond1() throws IOException {

        DataModelerByond DM = new DataModelerByond();

        logger.info("testByond1 Started");

        TransactionList transactionList = DM.createTransactionList(OfxgenGetPropertyValues.testFilePathByond1);

        df.setRoundingMode(RoundingMode.DOWN);
        Double initialBalanceRounded = Double.parseDouble(df.format(transactionList.getInitialBalance()));
        assertEquals(initialBalanceRounded, OfxgenGetPropertyValues.testinitialBalanceByond1);

        df.setRoundingMode(RoundingMode.UP);
        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));

        assertEquals(finalBalanceRounded, OfxgenGetPropertyValues.testFinalBalanceByond1);

        convertFileByond(OfxgenGetPropertyValues.testFilePathByond1);
        logger.info("testByond1 Competed Successfully");
    }

    @Test
    public void testByond2() throws IOException {

        DataModelerByond DM = new DataModelerByond();

        logger.info("testByond2 Started");

        TransactionList transactionList = DM.createTransactionList(OfxgenGetPropertyValues.testFilePathByond2);

        df.setRoundingMode(RoundingMode.DOWN);
        Double initialBalanceRounded = Double.parseDouble(df.format(transactionList.getInitialBalance()));
        assertEquals(initialBalanceRounded, OfxgenGetPropertyValues.testinitialBalanceByond2);

        df.setRoundingMode(RoundingMode.UP);
        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));

        assertEquals(finalBalanceRounded, OfxgenGetPropertyValues.testFinalBalanceByond2);

        convertFileByond(OfxgenGetPropertyValues.testFilePathByond2);
        logger.info("testByond2 Competed Successfully");
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

        AllTransactions alltransactionLists = DM.createTransactionListHSS(testFilePathVanguard);

        alltransactionLists.getInvTrans().printTransactionList();
        alltransactionLists.getCashTrans().printTransactionList();

       Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceVanguard);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceVanguard);

        logger.info("testVanguard Competed Successfully");
    }

    @Test
    public void testVanguard1() throws Exception {

        DataModelerVanguard DM = new DataModelerVanguard();

        logger.info("testVanguard1 Started");

        AllTransactions alltransactionLists = DM.createTransactionListXSS(testFilePathVanguard1);

        alltransactionLists.getInvTrans().printTransactionList();
        alltransactionLists.getCashTrans().printTransactionList();

        Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceVanguard1);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceVanguard1);

        convertFileVanguard(OfxgenGetPropertyValues.testFilePathVanguard1);

        logger.info("testVanguard1 Competed Successfully");
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
    @Test
    public void testChase1() throws IOException{

        DataModelerChase DM = new DataModelerChase();

        logger.info("testChase1  Started");

        TransactionList transactionList = DM.createTransactionList(testFilePathChase1);

        Double initialBalanceRounded = Double.parseDouble(df.format(transactionList.getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceChase1);

        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceChase1);

        convertFileChase(OfxgenGetPropertyValues.testFilePathChase1);

        logger.info("testChase1 Competed Successfully");
    }
    @Test
    public void testChase2() throws IOException{

        DataModelerChase DM = new DataModelerChase();

        logger.info("testChase2  Started");

        TransactionList transactionList = DM.createTransactionList(testFilePathChase2);

        Double initialBalanceRounded = Double.parseDouble(df.format(transactionList.getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceChase2);

        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceChase2);

        convertFileChase(OfxgenGetPropertyValues.testFilePathChase2);

        logger.info("testChase2 Competed Successfully");
    }

    @Test
    public void testChip() throws IOException{

        DataModelerChip DM = new DataModelerChip();

        logger.info("testChip  Started");

        TransactionList transactionList = DM.createTransactionList(testFilePathChip);

        Double initialBalanceRounded = Double.parseDouble(df.format(transactionList.getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceChip);

        Double finalBalanceRounded = Double.parseDouble(df.format(transactionList.getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceChip);

        convertFileChip(OfxgenGetPropertyValues.testFilePathChip);

        logger.info("testChip Competed Successfully");
    }


    @Test
    public void testFreetrade() throws Exception{


        DataModelerFreeTrade DM = new DataModelerFreeTrade();

        logger.info("testFreetrade Started");

        AllTransactions alltransactionLists = DM.createTransactionList(testFilePathfreetrade);

        Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalancefreetrade);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalancefreetrade);

        convertFileFreetrade(testFilePathfreetrade);

        logger.info("testFreetrade Competed Successfully");

    }
    @Test
    public void testTrading212() throws Exception{

        DataModelerTrading212 DM = new DataModelerTrading212();

        logger.info("testTrading212 Started");

        AllTransactions alltransactionLists = DM.createTransactionList(testFilePathTrading212);

        Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceTrading212);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceTrading212);

        convertFileTrading212(testFilePathTrading212);

        logger.info("testTrading212 Competed Successfully");

    }


    @Test
    public void testDodl() throws Exception{

        DataModelerDodl DM = new DataModelerDodl();

        logger.info("testDodl Started");

        AllTransactions alltransactionLists = DM.createTransactionList(testFilePathDodl);

        Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceDodl);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceDodl);

        convertFileDodl(testFilePathDodl);

        logger.info("testDodl Competed Successfully");

    }

    @Test
    public void testEquate() throws Exception{

        DataModelerEquate DM = new DataModelerEquate();

        logger.info("testEquate Started");

        AllTransactions alltransactionLists = DM.createTransactionList(testFilePathEquate);

        Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceEquate);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceEquate);

        convertFileEquate(testFilePathEquate);

        logger.info("testEquate Competed Successfully");

    }

    @Test
    public void testTrading212Card() throws Exception{

        DataModelerTrading212Card DM = new DataModelerTrading212Card();

        logger.info("testTrading212 Card Started");

        AllTransactions alltransactionLists = DM.createTransactionList(testFilePathTrading212Card);

        Double initialBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getInitialBalance()));
        assertEquals(initialBalanceRounded, testintialBalanceTrading212Inv);

        Double finalBalanceRounded = Double.parseDouble(df.format(alltransactionLists.getCashTrans().getFinalBalance()));
        assertEquals(finalBalanceRounded, testfinalBalanceTrading212Inv);

        Double initialBalanceRoundedCard = Double.parseDouble(df.format(alltransactionLists.getCardTrans().getInitialBalance()));
        assertEquals(initialBalanceRoundedCard, testintialBalanceTrading212Card);

        Double finalBalanceRoundedCard = Double.parseDouble(df.format(alltransactionLists.getCardTrans().getFinalBalance()));
        assertEquals(finalBalanceRoundedCard, testfinalBalanceTrading212Card);

        convertFileTrading212Card(testFilePathTrading212Card);

        logger.info("testTrading212 Card Competed Successfully");

    }



    @After
    public void tearDown()  {
    }
}