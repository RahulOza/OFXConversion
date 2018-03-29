import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

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

        //logger.info(text);
        logger.info(fileName);

        // tokenise the text
        DM.extract(text);
        TransactionList transactionList = DM.createTransactionList();

        transactionList.printTransactionList();

        OfGen.ofxFileWriteAmazon(transactionList,fileName);

    }



    public static void main(String[] args) throws IOException {

        //List<String> listOfPDFs = new ArrayList<String>();

        File[] files = new File("C:/Users/ozara/IdeaProjects/PDFs").listFiles();

        for (File file : files) {
            if (file.isFile()) {
                convertFile(file.getPath());
                //listOfPDFs.add(file.getName());
            }
        }
    }

}
