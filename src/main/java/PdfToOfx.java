import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PdfToOfx {

    final static Logger logger = Logger.getLogger(PdfToOfx.class.getName());

    public static void main(String[] args) throws IOException {

        DataModeler DM = new DataModeler();
        OfxGen OfGen = new OfxGen();
        //Loading an existing document
        File file = new File("C:/Users/ozara/IdeaProjects/PDFs/statement.pdf");
        PDDocument document = PDDocument.load(file);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String text = pdfStripper.getText(document);

        //Closing the document
        document.close();

       // logger.info(text);

        // tokenise the text
        DM.extract(text);
        TransactionList transactionList = DM.createTransactionList();

        transactionList.printTransactionList();

        OfGen.ofxFileWriteAmazon(transactionList);

        //System.out.println(" /n/n *********************************************** /n/n ");

    }




}
