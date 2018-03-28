import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.sf.ofx4j.io.v1.OFXV1Writer;


class OfxGen {

    private OFXV1Writer ofxv1Writer = null;

    Map<String, String> ofxHeader = new HashMap< >();

    private PrintWriter ofxWriter = null;

    private static final String amazonAccountId = "44000000";


   void ofxFileWriteAmazon (TransactionList transactionList){
       String ofxFileName="AmazonCreditCard";
       String ofxExtn=".ofx";
       String fileSuffix = new SimpleDateFormat("_dd_MM_yyyy").format(new Date());
       String filePath = "C:/Users/ozara/IdeaProjects/OFXs/";
       DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyyMMdd110000.000", Locale.ENGLISH);

       ofxFileName = filePath + ofxFileName + fileSuffix + ofxExtn;

      // Create file name
        try {
            OFXV1Writer ofxv1Writer = new OFXV1Writer(new PrintWriter(ofxFileName));

            ofxv1Writer.writeHeaders(ofxHeader);

            ofxv1Writer.setWriteAttributesOnNewLine(true);

            ofxv1Writer.writeStartAggregate("OFX");
            ofxv1Writer.writeStartAggregate("SIGNONMSGSRSV1");
            ofxv1Writer.writeStartAggregate("SONRS");
            ofxv1Writer.writeStartAggregate("STATUS");

            ofxv1Writer.writeElement("CODE","0");
            ofxv1Writer.writeElement("SEVERITY","INFO");
            ofxv1Writer.writeElement("MESSAGE","OK");

            ofxv1Writer.writeEndAggregate("STATUS");
            //yyyyMMdd110000.000[0] DateTimeFormatter.ofPattern("dd-MM-yy", Locale.ENGLISH);
            ofxv1Writer.writeElement("DTSERVER",transactionList.transactionsListFinal.get(0).transactionDate.format( myformatter) + "[0]");
            ofxv1Writer.writeElement("LANGUAGE","ENG");

            ofxv1Writer.writeEndAggregate("SONRS");
            ofxv1Writer.writeEndAggregate("SIGNONMSGSRSV1");

            ofxv1Writer.writeStartAggregate("CREDITCARDMSGSRSV1");
            ofxv1Writer.writeElement("CCSTMTTRNRS","");

            ofxv1Writer.writeElement("TRNUID","0");

            ofxv1Writer.writeStartAggregate("STATUS");

            ofxv1Writer.writeElement("CODE","0");
            ofxv1Writer.writeElement("SEVERITY","INFO");
            ofxv1Writer.writeElement("MESSAGE","OK");

            ofxv1Writer.writeEndAggregate("STATUS");
            ofxv1Writer.writeElement("CCSTMTRS","");
            ofxv1Writer.writeElement("CURDEF","GBP");

            ofxv1Writer.writeStartAggregate("CCACCTFROM");
            ofxv1Writer.writeElement("ACCTID",amazonAccountId);
            ofxv1Writer.writeEndAggregate("CCACCTFROM");

            // Bank transactions
            ofxv1Writer.writeStartAggregate("BANKTRANLIST");

            ofxv1Writer.writeElement("DTSTART",transactionList.transactionsListFinal.get(0).transactionDate.format(myformatter) + "[0]");
            ofxv1Writer.writeElement("DTEND",transactionList.transactionsListFinal.get(transactionList.transactionsListFinal.size()-1).transactionDate.format(myformatter) + "[0]");

            for (Transactions t: transactionList.transactionsListFinal ) {
                ofxv1Writer.writeStartAggregate("STMTTRN");
                if(t.transactionAmount > 0) {
                    ofxv1Writer.writeElement("TRNTYPE", "DEBIT");
                }
                else{
                    ofxv1Writer.writeElement("TRNTYPE", "CREDIT");
                }
                ofxv1Writer.writeElement("DTPOSTED",t.transactionDate.toString());
                t.transactionAmount = -(t.transactionAmount);
                ofxv1Writer.writeElement("TRNAMT",t.transactionAmount.toString());
                ofxv1Writer.writeElement("NAME",t.transactionDetails);
               // ofxv1Writer.writeElement("FITID",t.transactionDetails);

                ofxv1Writer.writeEndAggregate("STMTTRN");
            }

            ofxv1Writer.writeEndAggregate("BANKTRANLIST");


            ofxv1Writer.writeEndAggregate("CREDITCARDMSGSRSV1");

            ofxv1Writer.writeEndAggregate("OFX");

            ofxv1Writer.close();
        }
        catch(Exception e){
            e.printStackTrace();

        }

    }
}
