package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class DataModelerVanguard {


    private List<String> transactionTokenList = new ArrayList<>();
    private List<String> transactionList = new ArrayList<>();
    private Double finalBalance = 0.0;
    private Double intialBalance = 0.0;

    public static boolean isPureAscii(String v) {
        return Charset.forName("US-ASCII").newEncoder().canEncode(v);
        // or "ISO-8859-1" for ISO Latin 1
        // or StandardCharsets.US_ASCII with JDK1.7+
    }
    public TransactionList createTransactionList(String sourceFileName) throws IOException {

        TransactionList translistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);


            String lineOfStatement;
            Boolean isHeader = true;
            Boolean firstRec = true;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {
                    //replace consecutive tabs by single
                    String newLineOfStatement = lineOfStatement.replaceAll("\t(?=\t)","");

                    String tokens[] = newLineOfStatement.split("\\t");
                    // we know the tokens are
                    //Date |	Details	| What's gone in |	What's gone out | 	Balance
                    // 0          1           2                                  3
                    // Date	Description	Amount(GBP)
                    //    17 May 2021\tBought 8 S&P 500 UCITS ETF Distributing (VUSA)\t\t−£448.79\t£1,555.59

                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();

                        trans.setTransactionDate(LocalDate.parse(tokens[0], myformatter));
                        trans.setTransactionDetails(tokens[1]);
                        String transactionDetails = tokens[1];
                        //2 is empty
                        String transAmountWithComma = tokens[2].replace("£","");
                        String transAmount = transAmountWithComma.replace(",","");

                        // It was found non ascii characters creep in so check if that is the case
                        if(!isPureAscii(transAmount)){
                         String transAmountNew = transAmount.replaceAll("[^\\x00-\\x7F]", "");
                            trans.setTransactionAmount(Double.parseDouble(transAmountNew));
                            //the non ascii character is the negative sign hence revert sign.
                        }
                        else{
                            trans.setTransactionAmount(Double.parseDouble(transAmount));
                        }
                        //if something is 'Bought' then it is a debit
                        if(transactionDetails.startsWith("Bought")) {
                            trans.setTransactionAmount(-trans.getTransactionAmount());
                        }

                        transAmountWithComma = tokens[3].replace("£","");
                        transAmount = transAmountWithComma.replace(",","");

                        String transAmountNew ="";
                        if(!isPureAscii(transAmount)) {
                            transAmountNew = transAmount.replaceAll("[^\\x00-\\x7F]", "");
                        }
                        else{
                            transAmountNew = transAmount;
                        }

                        if (firstRec) {
                            //Initial balance is in the very first line
                            // Initial balance is AFTER the first transaction so add the value of transaction to get the actual initial value.
                            finalBalance = Double.parseDouble(transAmountNew);
                            firstRec = false;
                        }
                        intialBalance = Double.parseDouble(transAmountNew) - trans.getTransactionAmount();
                        translistFinal.getTransactionsList().add(trans);

                    }
                }

                if (isHeader)
                    isHeader = false;
            }
            translistFinal.setFinalBalance(finalBalance);
            translistFinal.setInitialBalance(intialBalance);

        }
        return translistFinal;
    }
}






