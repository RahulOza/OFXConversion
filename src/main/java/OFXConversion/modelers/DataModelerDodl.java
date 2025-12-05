package OFXConversion.modelers;

import OFXConversion.data.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class DataModelerDodl {

    static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        String pdfAsText = new PDFTextStripper().getText(doc);
        doc.close();
        return pdfAsText;

    }

    static Boolean isCredit(String trans) {

        if (trans.contains("Lifetime ISA Government Bonus")) {
            return true;
        }
        else if (trans.contains("Direct debit")) {
            return true;
        }
        else if (trans.contains("Gross interest")) {
            return true;
        }
        else if (trans.contains("CASH CORRECTION")) {
            return true;
        }

        return false;
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

        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH);

        // First let us use dd mmm yyyy to split the statements into chunks of blocks from which we can mine
        // transaction details

        Boolean foundTrans = false;
        while (myscanner.hasNextLine()){
            String line = myscanner.nextLine();

            if(line.startsWith("GBP Brought Forward Balance")){

                //extract and set initial balance

                Pattern regex00 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                Matcher matcher00 = regex00.matcher(line);

                if(matcher00.find()) {
                    transactionList.setInitialBalance(Double.parseDouble(matcher00.group(1)));
                    invTranslistFinal.setInitialBalance(Double.parseDouble(matcher00.group(1)));
                }
                foundTrans = true;
                continue;

            }

            if(line.startsWith("GBP Closing Balance")){

                //extract and set initial balance

                Pattern regex00 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                Matcher matcher00 = regex00.matcher(line);

                if(matcher00.find()) {
                    transactionList.setFinalBalance(Double.parseDouble(matcher00.group(1)));
                    invTranslistFinal.setFinalBalance(Double.parseDouble(matcher00.group(1)));
                }
                break;

            }

            if(!foundTrans){
                continue;
            }

            Matcher m = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{2})").matcher(line);
            if(m.find()) {
                // we have found a transaction
                //Overall looks like this
                /* GBP Brought Forward Balance £28.35
                23/01/25 Dodl Charge – Dec 2024 2.94 25.41
                28/01/25 Lifetime ISA Government Bonus 83.34 108.75
                04/02/25 Direct debit 333.33 442.08
                10/02/25 Bought 0.2854 VANGUARD INVESTMENTS UK LTD @ £175.16 49.99 392.09
                */

                Transactions trans = new Transactions();
                InvTransactions itrans = new InvTransactions();
                //some transactions are cash only while others are investments as well
                // Purchase ==> Inv and cash
                // Sold?? ==> Inv and cash
                // Debit Card Payment ==> cash only
                // Transfer ==> cash only
                // *BALANCE B/F* ==> cash only

                //Date - first 10 chars
                trans.setTransactionDate(LocalDate.parse(line.substring(0, 8), myformatter));
                itrans.setTransactionDate(LocalDate.parse(line.substring(0, 8), myformatter));

                int transDetailsNoDateLength = 0;
                int balAndAmtLength = 0;
                //Balance is the final number DDDD.DD in the remaining string
                String transDetailsNoDate = line.substring(9);
                transDetailsNoDateLength = transDetailsNoDate.length();
                //find all the numbers in the line
                Pattern regex0 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                Matcher matcher0 = regex0.matcher(transDetailsNoDate);

                int counter = 0;

                while (matcher0.find()) {
                    counter++;
                }

                Matcher matcher1 = regex0.matcher(transDetailsNoDate);
                int travCtr = 1;
                while(matcher1.find()){

                    //last - 1 is transaction amount
                    //if only one that that is initial balance
                    if(travCtr == counter){
                        balAndAmtLength= balAndAmtLength + matcher1.group(1).length();
                    }
                    else if (travCtr == (counter - 1)){
                        balAndAmtLength= balAndAmtLength + matcher1.group(1).length();
                        if(isCredit(transDetailsNoDate)){
                            trans.setTransactionAmount(Double.parseDouble(matcher1.group(1)));
                        }
                        else{
                            trans.setTransactionAmount(-Double.parseDouble(matcher1.group(1)));
                        }

                    }
                    travCtr++;
                }

                //Get next substring without date and without balance
                String transDetailsNoDateNoBal = line.substring(8,line.length()-balAndAmtLength-1);


                // Transaction details
                trans.setTransactionDetails(transDetailsNoDateNoBal.trim());

                transDetailsNoDateNoBal = transDetailsNoDateNoBal.trim();

                //Investment transactions are the ones starting with Purchase
                if(transDetailsNoDateNoBal.startsWith("Bought")){
                    // For investment transactions we need to extract Quantity, instrument and price
                    //10/02/25 Bought 0.2854 VANGUARD INVESTMENTS UK LTD @ £175.16 49.99 392.09

                    //we now have
                    //Bought 0.2854 VANGUARD INVESTMENTS UK LTD @ £175.16


                    //set inv details
                    itrans.setTransactionDetails(transDetailsNoDateNoBal.trim());


                    //  extract Quantity and price
                    Pattern regex = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                    Matcher matcher = regex.matcher(transDetailsNoDateNoBal);
                    int quantityLength = 0;
                    int priceLength = 0;
                    Boolean first = true;
                    while(matcher.find()){
                        if(first) {
                            quantityLength = matcher.group(1).length() + 1;
                            itrans.setInvQuantity(Double.parseDouble(matcher.group(1)));
                            first = false;
                        }
                        else{
                            priceLength = matcher.group(1).length() + 1;
                            itrans.setInvPrice(Double.parseDouble(matcher.group(1)));
                        }
                    }
                    // Bought 0.2854 VANGUARD INVESTMENTS UK LTD @ £175.16
                    // Bought = 6 chars + space

                    //Now the rest of the chars will be fund name
                    String fundName = transDetailsNoDateNoBal.substring(((6+1)+quantityLength),transDetailsNoDateNoBal.indexOf("@")-1);
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
                    itrans.setTransactionAmount(itrans.getInvQuantity() * itrans.getInvPrice());

                    invTranslistFinal.getInvTransactionsList().add(itrans);
                } //if investment transactions
                transactionList.getTransactionsList().add(trans);
            }//if

        }//while

    }// end extract()

}
