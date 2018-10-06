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

import static java.lang.System.exit;

@SpringBootApplication

//@EnableWebMvc
public class PdfToOfx {

    final static Logger logger = Logger.getLogger(PdfToOfx.class.getName());



    static void convertFile(String fileName) throws IOException{

        DataModeler DM = new DataModeler();
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
        OfGen.ofxFileWriteAmazon(transactionList,fileName);

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

        if(args.length == 1)
        {
            programType = args[0];
        }
        else
        {
            logger.severe("Insufficient or Invalid Arguments");
            logger.severe("Valid way to run - java PdfToOfx [amazon/tsb]");
            exit(1);
        }


        if(programType.equals("amazon")) {
            File[] files = new File("C:/Users/ozara/IdeaProjects/PDFs").listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    convertFile(file.getPath());
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



        }

      // SpringApplication.run(PdfToOfx.class, args);



        /*ApplicationContext ctx = SpringApplication.run(PdfToOfx.class, args);
        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/


    }

   /* @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }*/


}
