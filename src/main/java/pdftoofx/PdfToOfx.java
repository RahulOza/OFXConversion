package pdftoofx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import static java.lang.System.exit;

@SpringBootApplication
// do not enable MVC until its required. Thymeleaf
//@EnableWebMvc

public class PdfToOfx {

    final static Logger logger = Logger.getLogger(PdfToOfx.class.getName());
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
    static void convertFile(String fileName) throws IOException{

        DataModelerAmazon DM = new DataModelerAmazon();
        OfxGen OfGen = new OfxGen();

        //Loading an existing document
        File file = new File(fileName);
        PDDocument document = PDDocument.load(file);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);

        //Closing the document
        document.close();

        logger.info(text);
        logger.info(fileName);

        // tokenise the text
        DM.extract(text);
        TransactionList transactionList = DM.createTransactionList();

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

        if(args.length >= 1)
        {
            programType = args[0];
            if((programType.equals("amazon")||programType.equals("select")) && (args.length == 2)){
                PdfToOfx.initialBalance = Double.parseDouble(args[1]);
            }else{
                logger.warning("Something isn't right, balance may be incorrect");
            }

        }
        else
        {
            //if these are amazon statements, we also need initial balance to be provided manually.
            logger.severe("Insufficient or Invalid Arguments");
            logger.severe("Valid way to run - java PdfToOfx [amazon/tsb] [initial balance]");
            exit(1);
        }

        /*
        if(programType.equals("amazon")) {
            File[] files = new File("C:/Users/ozara/IdeaProjects/PDFs").listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    //convertFile converts from PDF to OFX, this no longer works as they keep changing format of
                    // of the PDF files.
                    //convertFile(file.getPath());
                    //convertFileAmazon - converts csv to OFX
                    convertFileAmazon(file.getPath(),PdfToOfx.initialBalance);

                    //listOfPDFs.add(file.getName());
                }
            }
        }
        else{

                if (programType.equals("tsb")) {
                    File[] files = new File("C:/Users/ozara/IdeaProjects/TSB").listFiles();
                    for (File file : files) {
                        if (file.isFile()) {
                            convertFileTSB(file.getPath());
                            //listOfPDFs.add(file.getName());
                        }
                    }
                }
            if (programType.equals("select")) {
                File[] files = new File("C:/Users/ozara/IdeaProjects/RBSSelect").listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        convertFileRBSSelect(file.getPath(),PdfToOfx.initialBalance);
                        //listOfPDFs.add(file.getName());
                    }
                }
            } */




       // }

      SpringApplication.run(PdfToOfx.class, args);



       /* ApplicationContext ctx = SpringApplication.run(PdfToOfx.class, args);
        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/


    }

    /* Do not uncomment unless you have proceeded to use it else you will get storageservice error*/
   /*@Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }*/

    //Thymeleaf
    @Bean
    ServletContextTemplateResolver templateResolver(){
        ServletContextTemplateResolver resolver=new ServletContextTemplateResolver();
        resolver.setSuffix(".html");
        resolver.setPrefix("/resources");
        resolver.setTemplateMode("HTML5");
        return resolver;
    }
}
