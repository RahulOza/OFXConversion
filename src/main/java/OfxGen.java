import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ofx4j.io.v1.OFXV1Writer;
import net.sf.ofx4j.io.AggregateMarshaller;

class OfxGen {

    private OFXV1Writer ofxv1Writer = null;

    Map<String, String> ofxHeader = new HashMap<String, String>();

    private PrintWriter ofxWriter = null;


    /*private String ofxHeader = "OFXHEADER:100\r\n" +
            "DATA:OFXSGML\r\n" +
            "VERSION:102\r\n" +
            "SECURITY:NONE\r\n" +
            "ENCODING:USASCII\r\n" +
            "CHARSET:1252\r\n"+
            "COMPRESSION:NONE\r\n"+
            "OLDFILEUID:NONE\r\n"+
            "NEWFILEUID:NONE\r\n"; */


    private static final String amazonAccountId = "44000000";

   /* private String ofxSignOn ="<OFX>\r\n" +
            "<SIGNONMSGSRSV1>\r\n" +
            "<SONRS>\r\n" +
            "<STATUS>\r\n" +
            "<CODE>0\r\n" +
            "<SEVERITY>INFO\r\n" +
            "<MESSAGE>OK\r\n" +
            "</STATUS>\r\n" +
            "<DTSERVER>\r\n" +
            "<LANGUAGE>ENG\r\n" +
            "</SONRS>\r\n" +
            "</SIGNONMSGSRSV1>\r\n"+
            "<CREDITCARDMSGSRSV1>\r\n"+
            "<CCSTMTTRNRS>\r\n"+
            "<TRNUID>0\r\n"+
            "<STATUS>\r\n"+
            "<CODE>0\r\n"+
            "<SEVERITY>INFO\r\n"+
            "<MESSAGE>OK\r\n"+
            "</STATUS>\r\n"+
            "<CCSTMTRS>\r\n"+
            "<CURDEF>GBP\r\n"+
            "<CCACCTFROM>"+amazonAccountId+"\r\n"+
            "</CCACCTFROM>\r\n";



    private String ofxFooter = "</CCSTMTRS>\r\n" +
            "</CCSTMTTRNRS>\r\n" +
            "</CREDITCARDMSGSRSV1>\r\n" +
            "</OFX>";*/


   void ofxFileWriteAmazon (TransactionList transactionList){
       String ofxFileName="AmazonCreditCard";
       String ofxExtn=".ofx";
       String fileSuffix = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
       String filePath = "C:/Users/ozara/IdeaProjects/OFXs/";

       ofxFileName = filePath + ofxFileName + fileSuffix + ofxExtn;

      // Create file name
        try {
            OFXV1Writer ofxv1Writer = new OFXV1Writer(new PrintWriter(ofxFileName));

            ofxv1Writer.writeHeaders(ofxHeader);

            ofxv1Writer.setWriteAttributesOnNewLine(true);

            ofxv1Writer.writeStartAggregate("OFX");
            ofxv1Writer.writeStartAggregate("SIGNONMSGSRSV1");
            ofxv1Writer.writeEndAggregate("SIGNONMSGSRSV1");

            ofxv1Writer.writeStartAggregate("CREDITCARDMSGSRSV1");

            ofxv1Writer.writeStartAggregate("CCACCTFROM");
            ofxv1Writer.writeElement("ACCTID",amazonAccountId);
            ofxv1Writer.writeEndAggregate("CCACCTFROM");

            // Bank transactions
            ofxv1Writer.writeStartAggregate("BANKTRANLIST");

            ofxv1Writer.writeElement("DTSTART",transactionList.transactionsListFinal.get(0).transactionDate.toString());
            ofxv1Writer.writeElement("DTEND",transactionList.transactionsListFinal.get(transactionList.transactionsListFinal.size()-1).transactionDate.toString());

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



       // ofxWriter.print(ofxHeader);
       // ofxWriter.print(ofxSignOn);

        // write all transactions to the file
       // ofxWriter.print("<BANKTRANLIST>\r\n");





       //ofxWriter.print("</BANKTRANLIST>\r\n");
      // ofxWriter.print("<LEDGERBAL>\r\n");

      /* <BALAMT>113.96
       <DTASOF>20170914130000.000[0]*/

       // ofxWriter.print("</LEDGERBAL>\r\n");
        //ofxWriter.print(ofxFooter);
       // ofxWriter.close();


       /* Body:
<OFX>
  <SIGNONMSGSRSV1>
    …
  </SIGNONMSGSRSV1>
  <BANKMSGSRSV1>
    …
          <STMTTRN>
            <TRNTYPE>PAYMENT
                <DTPOSTED>20050824080000
                <TRNAMT>-80.32
                <FITID>219378
                <CHECKNUM>1044
                <NAME>FrogKick Scuba Gear
          </STMTTRN>
    …
  </BANKMSGSRSV1>
</OFX>*/

    }
}
