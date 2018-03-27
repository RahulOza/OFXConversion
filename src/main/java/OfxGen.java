import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OfxGen {

    PrintWriter ofxWriter = null;

    String ofxHeader = "OFXHEADER:100\r\n" +
            "DATA:OFXSGML\r\n" +
            "VERSION:102\r\n" +
            "SECURITY:NONE\r\n" +
            "ENCODING:USASCII\r\n" +
            "CHARSET:1252\r\n"+
            "COMPRESSION:NONE\r\n"+
            "OLDFILEUID:NONE\r\n"+
            "NEWFILEUID:NONE\r\n"+
            "\r\n\r\n\r\n"+
            "<OFX>\n\r";

    String ofxSignOn ="<OFX>\r\n" +
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
            "</SIGNONMSGSRSV1>\r\n";


    String ofxFooter = "</OFX>";


   void ofxFileWrite(){
       String ofxFileName="AmazonCreditCard";
       String ofxExtn=".ofx";
       String fileSuffix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
       String filePath = "C:/Users/ozara/IdeaProjects/OFXs/";

       ofxFileName = filePath + ofxFileName + fileSuffix + ofxExtn;

       // Create file name
        try {
            ofxWriter = new PrintWriter(ofxFileName, "UTF-8");
        }
        catch(Exception e){
            e.printStackTrace();

        }

       ofxWriter.print(ofxHeader);
        ofxWriter.print(ofxSignOn);
        ofxWriter.print(ofxFooter);

       ofxWriter.close();

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
