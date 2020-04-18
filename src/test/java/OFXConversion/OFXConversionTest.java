package OFXConversion;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static OFXConversion.OFXConversion.convertFileMarcus;
import static OFXConversion.OFXConversion.convertFileRBSSelect;
import static OFXConversion.OFXConversion.convertFileAmazon;
import static OFXConversion.OFXConversion.convertFileTSB;

import static org.junit.Assert.*;

public class OFXConversionTest {

    // TODO : read paths from a config file
    private String testFilePathMarcus ="C:\\Users\\ozara\\IdeaProjects\\OFXConversion\\tests\\Marcus\\MarcusTest.csv";
    private String testFilePathSelect ="C:\\Users\\ozara\\IdeaProjects\\OFXConversion\\tests\\RBS\\RBSSelect.csv";
    private String testFilePathAmazon ="C:\\Users\\ozara\\IdeaProjects\\OFXConversion\\tests\\Amazon\\AmazonTest.csv";
    private String testFilePathTSB ="C:\\Users\\ozara\\IdeaProjects\\OFXConversion\\tests\\Marcus\\MarcusTest.csv";
    private final static Logger logger = Logger.getLogger(OFXConversion.class.getName());

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testMarcus() throws IOException {
        // check if balance is correct ?
        //logger.info("testMarcus Started");
        convertFileMarcus(testFilePathMarcus,0.0);
        //logger.info("testMarcus Competed Successfully");
    }

    @Test
    public void testSelect() throws IOException {
        // check if balance is correct ?
        //logger.info("testSelect Started");
        convertFileRBSSelect(testFilePathSelect,0.0);
        //logger.info("testSelect Competed Successfully");
    }

    @Test
    public void testAmazon() throws IOException {
        // check if balance is correct ?
        //logger.info("testAmazon Started");
        convertFileAmazon(testFilePathAmazon,0.0);
       // logger.info("testAmazon Competed Successfully");
    }

    @Test
    public void testTSB() throws IOException {
        //logger.info("testTSB Started");
        convertFileTSB(testFilePathTSB);
        //logger.info("testTSB Competed Successfully");
    }


    @After
    public void tearDown() throws Exception {
    }

}