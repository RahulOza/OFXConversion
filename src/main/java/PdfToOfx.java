import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
//import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import static java.lang.System.exit;

public class PdfToOfx {


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


        //System.out.println(text);

        // tokenise the text
        DM.extract(text);
        DM.createTransactionList();

        OfGen.ofxFileWrite();

        //System.out.println(" /n/n *********************************************** /n/n ");
        //System.out.println(SanitisedString);
    }




}
