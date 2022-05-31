package OFXConversion;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;
import net.sf.ofx4j.io.v1.OFXV1Writer;

import static java.lang.System.exit;


class OfxGen {

    private Map<String, String> ofxHeader = new HashMap< >();

    private static final int nameLimit = 32;


   void ofxFileWriter (TransactionList transactionList, String fileName, String accountId, String accounType){

       String ofxExtn=".ofx";

       DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyyMMdd110000.000", Locale.ENGLISH);
       int fitid = 1;
       String fitIdPart = new SimpleDateFormat("ddMMyyyyhhmmssS").format(new Date());
       String fitIdPref = "R";

      // ofxFileName = filePath + ofxFileName + fileSuffix + ofxExtn;
       String ofxFileName = fileName.substring(0,fileName.length()-4) + ofxExtn;


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

            ofxv1Writer.writeElement("DTSERVER",transactionList.getTransactionsList().get(0).getTransactionDate().format( myformatter) + "[0]");
            ofxv1Writer.writeElement("LANGUAGE","ENG");

            ofxv1Writer.writeEndAggregate("SONRS");
            ofxv1Writer.writeEndAggregate("SIGNONMSGSRSV1");

            //TODO - CREDIT/DEBIT CARDS? we consider everyting as CREDIT cards?? it does not really matters but needs to be fixed ??
            ofxv1Writer.writeStartAggregate("CREDITCARDMSGSRSV1");
            ofxv1Writer.writeStartAggregate("CCSTMTTRNRS");

            ofxv1Writer.writeElement("TRNUID","0");

            ofxv1Writer.writeStartAggregate("STATUS");

            ofxv1Writer.writeElement("CODE","0");
            ofxv1Writer.writeElement("SEVERITY","INFO");
            ofxv1Writer.writeElement("MESSAGE","OK");

            ofxv1Writer.writeEndAggregate("STATUS");
            ofxv1Writer.writeStartAggregate("CCSTMTRS");

            ofxv1Writer.writeElement("CURDEF","GBP");

            ofxv1Writer.writeStartAggregate("CCACCTFROM");


            ofxv1Writer.writeElement("ACCTID", accountId);

            ofxv1Writer.writeEndAggregate("CCACCTFROM");

            // Bank transactions
            ofxv1Writer.writeStartAggregate("BANKTRANLIST");

            ofxv1Writer.writeElement("DTSTART",transactionList.getTransactionsList().get(0).getTransactionDate().format(myformatter) + "[0]");
            ofxv1Writer.writeElement("DTEND",transactionList.getTransactionsList().get(transactionList.getTransactionsList().size()-1).getTransactionDate().format(myformatter) + "[0]");

            for (Transactions t: transactionList.getTransactionsList() ) {
                ofxv1Writer.writeStartAggregate("STMTTRN");
                if(t.getTransactionAmount() > 0) {
                    ofxv1Writer.writeElement("TRNTYPE", "DEBIT");
                }
                else{
                    ofxv1Writer.writeElement("TRNTYPE", "CREDIT");
                }
                ofxv1Writer.writeElement("DTPOSTED",t.getTransactionDate().format(myformatter) + "[0]");

                if(accounType.equalsIgnoreCase("Debit")){
                    t.setTransactionAmount(t.getTransactionAmount());
                }
                else if(accounType.equalsIgnoreCase("Credit")){
                    // reverse values if its a credit account
                    // balance and each transaction amounts become negative
                    t.setTransactionAmount(-t.getTransactionAmount());
                }
                else{
                    throw new Exception("Unknow Account Type - Account needs to be either a Credit or Debit account");
                }
                ofxv1Writer.writeElement("TRNAMT",t.getTransactionAmount().toString());
                ofxv1Writer.writeElement("FITID",fitIdPref + fitIdPart + fitid++);
                ofxv1Writer.writeElement("NAME",t.getTransactionDetails().substring(0,Math.min(t.getTransactionDetails().length(),nameLimit)));
                ofxv1Writer.writeElement("MEMO",t.getTransactionDetails());


                ofxv1Writer.writeEndAggregate("STMTTRN");
            }

            ofxv1Writer.writeEndAggregate("BANKTRANLIST");
            ofxv1Writer.writeStartAggregate("LEDGERBAL");

            ofxv1Writer.writeElement("BALAMT", transactionList.getFinalBalance().toString());
            ofxv1Writer.writeElement("DTASOF",transactionList.getTransactionsList().get(transactionList.getTransactionsList().size()-1).getTransactionDate().format(myformatter) + "[0]");

            ofxv1Writer.writeEndAggregate("LEDGERBAL");
            ofxv1Writer.writeEndAggregate("CCSTMTRS");
            ofxv1Writer.writeEndAggregate("CCSTMTTRNRS");
            ofxv1Writer.writeEndAggregate("CREDITCARDMSGSRSV1");

            ofxv1Writer.writeEndAggregate("OFX");

            ofxv1Writer.close();
        }
        catch(Exception e){
            e.printStackTrace();

        }

    }
}
