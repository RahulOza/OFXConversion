package OFXConversion.modelers;

import OFXConversion.data.*;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.RoundingMode;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class DataModelerDodl {

    static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        String pdfAsText = new PDFTextStripper().getText(doc);
        doc.close();
        return pdfAsText;

    }

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {

        String text;
        TransactionList translistFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();

        invTranslistFinal.readSymbolMap();
        try {
            text = getText(new File(sourceFileName));
            extract(text, translistFinal, invTranslistFinal);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (new AllTransactions(invTranslistFinal, translistFinal));
    }


    void extract(String originalStr, TransactionList transactionList, InvTransactionList invTranslistFinal)
    {
       Scanner myscanner = new Scanner(originalStr);
       boolean firstTrans = Boolean.TRUE;

        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

        // First let us use dd mmm yyyy to split the statements into chunks of blocks from which we can mine
        // transaction details

        while (myscanner.hasNextLine()){
            String line = myscanner.nextLine();
            // Ignore if there is date immediately after text 'Trupta Oza'
            if(line.startsWith("Trupta Oza")){
                line = myscanner.nextLine();
                line = myscanner.nextLine();
            }

            Matcher m = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{4})").matcher(line);
            if(m.find()) {
                // we have found a transaction
                //Overall looks like this

                //Date        Description                                           Reference     Settlement Date   Receipt Payment   Balance
                //19/11/2024  Debit Card Payment                                                                    10.00             10.55
                //19/11/2024  Purchase 587.42 iShares US Equity Index (UK) D Acc    44624C0HJF0   04/11/2024        -3,643.99         0.55

                Transactions trans = new Transactions();
                InvTransactions itrans = new InvTransactions();
                //some transactions are cash only while others are investments as well
                // Purchase ==> Inv and cash
                // Sold?? ==> Inv and cash
                // Debit Card Payment ==> cash only
                // Transfer ==> cash only
                // *BALANCE B/F* ==> cash only

                //Date - first 10 chars
                trans.setTransactionDate(LocalDate.parse(line.substring(0, 10), myformatter));
                itrans.setTransactionDate(LocalDate.parse(line.substring(0, 10), myformatter));

                //if it is just the date then don't continue
                if(line.length() < 11){
                    //This is special case where transaction has been spilled over multiple lines
                    /*
                    CurrLine => 28/10/2024
                    Line 1   => Purchase 5.0925 Vanguard FTSE Dev €pe ex-UK Eq Idx
                    Line 2   => £ Acc
                    Line 3   => 44624C0DH7C 30/10/2024 -2,000.02 8,644.54

                    Idea is to append these into a single line

                    */

                    String fragmentedTransactionDate = line;

                    String fragmentedTransactionDateFund = fragmentedTransactionDate + " " + myscanner.nextLine();
                    String fragmentedTransactionDateFundFull = fragmentedTransactionDateFund + " " + myscanner.nextLine();
                    line = fragmentedTransactionDateFundFull  + " " + myscanner.nextLine();

                }

                //Balance is the final number DDDD.DD in the remaining string
                String transDetailsNoDateWithComma = line.substring(11);
                String transDetailsNoDate = transDetailsNoDateWithComma.replace(",","");
                Integer commaCount = 0;
                if(transDetailsNoDate.length() != transDetailsNoDateWithComma.length())
                    commaCount = commaCount + 1;


                Pattern regex0 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                Matcher matcher0 = regex0.matcher(transDetailsNoDate);
                int transDetailsNoDateLength = 0;
                while(matcher0.find()){
                    transDetailsNoDateLength = matcher0.group(1).length() +  commaCount;
                    if(firstTrans) {
                        transactionList.setFinalBalance(Double.parseDouble(matcher0.group(1)));
                        invTranslistFinal.setFinalBalance(Double.parseDouble(matcher0.group(1)));
                    }
                    transactionList.setInitialBalance(Double.parseDouble(matcher0.group(1)));
                    invTranslistFinal.setInitialBalance(Double.parseDouble(matcher0.group(1)));
                }
                //there is always a balance
                firstTrans = Boolean.FALSE;

                //Get next substring without date and without balance
                String transDetailsNoDateNoBalWithComma = line.substring(11,line.length()-transDetailsNoDateLength-1);
                String transDetailsNoDateNoBal = transDetailsNoDateNoBalWithComma.replace(",","");
                commaCount = 0;
                if(transDetailsNoDateNoBal.length() != transDetailsNoDateNoBalWithComma.length())
                    commaCount = commaCount + 1;

                // * BALANCE B/F is a special case with only the balance
                if(transDetailsNoDate.startsWith("* BALANCE B/F *")) {
                    //We just have the transaction details
                    trans.setTransactionDetails(transDetailsNoDateNoBal);
                    trans.setTransactionAmount(transactionList.getInitialBalance());
                    transactionList.getTransactionsList().add(trans);
                    continue;
                }

                //Amount is the number DDDD.DD in the remaining string
                Pattern regex1 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                Matcher matcher1 = regex1.matcher(transDetailsNoDateNoBal);
                int transDetailsNoDateNoBalLength = 0;
                while(matcher1.find()){
                    transDetailsNoDateNoBalLength = matcher1.group(1).length() + commaCount;
                    trans.setTransactionAmount(-Double.parseDouble(matcher1.group(1)));
                    itrans.setTransactionAmount(Double.parseDouble(matcher1.group(1)));
                }

                //what will remain will be either fund name or transaction details.
                //1 for the spaces.
                String transDetails = line.substring(11,(line.length()-transDetailsNoDateLength-transDetailsNoDateNoBalLength)-1);

                // Transaction details
                trans.setTransactionDetails(transDetails);

                //Investment transactions are the ones starting with Purchase
                if(transDetails.startsWith("Purchase")){
                    // For investment transactions strip the settlement date and reference
                    // - negative sign is 1 char, space between amount and settlement date another chat so 2 chars here
                    // Settlement date is 10 chars - 30/10/2024
                    // Reference is 11 chars 44624C0DHT5
                    // 1 Space between them so total 24 chars
                    // not sure why 2 more chars need to be removed so total 26

                    String invTransDetails = line.substring(11,(line.length()-transDetailsNoDateLength-transDetailsNoDateNoBalLength-26));

                    //set transaction details for cash over write as this is much shorter.
                    trans.setTransactionDetails(invTransDetails);
                    itrans.setTransactionDetails(invTransDetails);

                    //Now strip the word 'Purchase' and a space which is 9 chars
                    String quantityAndFundNameWithComma = line.substring((11+9),(line.length()-transDetailsNoDateLength-transDetailsNoDateNoBalLength-26));


                    String quantityAndFundName = quantityAndFundNameWithComma.replace(",","");
                    commaCount = 0;
                    if(quantityAndFundName.length() != quantityAndFundNameWithComma.length())
                        commaCount = commaCount + 1;

                    // next number DDDD.DD is the Quantity
                    Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                    Matcher matcher = regex.matcher(quantityAndFundName);
                    int quantityLength = 0;
                    while(matcher.find()){
                        quantityLength = matcher.group(1).length()+1;
                        itrans.setInvQuantity(Double.parseDouble(matcher.group(1)));
                        break;
                    }
                    //Now the rest of the chars will be fund name
                    String fundName = line.substring(((11+9+commaCount)+quantityLength),line.indexOf("4462")-1);
                    itrans.setInvName(fundName);

                    //Plan as of now is to always buy mutual funds and this is a purchase transactions.
                    itrans.setInvTransactionType(TransactionTypes.MF_BUY);

                    try {
                        itrans.setInvSymb(invTranslistFinal.getInvSymbolMap().get(fundName)[0]);
                    } catch (Exception e) {
                        System.out.println(" Error in fund:" + fundName);
                        throw new RuntimeException(e);
                    }

                    //calculate investment price
                    itrans.setInvPrice(itrans.getTransactionAmount()/itrans.getInvQuantity());

                    invTranslistFinal.getInvTransactionsList().add(itrans);
                } //if investment transactions
                transactionList.getTransactionsList().add(trans);
            }//if

        }//while

    }// end extract()

}
