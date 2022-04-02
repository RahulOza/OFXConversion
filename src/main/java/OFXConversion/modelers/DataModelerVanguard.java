package OFXConversion.modelers;

import OFXConversion.data.OfxgenGetPropertyValues;
import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataModelerVanguard {

    private Double finalBalance = 0.0;

    public TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList traslistFinal = new TransactionList();
        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        traslistFinal.setInitialBalance(initialBalance);
        String lineOfStatement;

        String transDetails = "";
        String transDate = "";
        String transAmount = "";

        //initialise final balance.
        finalBalance = initialBalance;

        while ((lineOfStatement = inputStream.readLine()) != null) {


            // empty lines need to be skipped and mark begining of new transactions
            while (!lineOfStatement.isEmpty()) {


                // first 11 chars are date
                transDate = lineOfStatement.substring(0, OfxgenGetPropertyValues.vanguardDateChars);

                // find index of £
                // sbtract 1 ..text between 12 to index(above) -1 = transaction details.

                int poundIndex = lineOfStatement.indexOf("£");
                transDetails = lineOfStatement.substring(12, poundIndex - 1);

                //index+ 1 until space is amount
                //we are not considering balance for now.

                String transAmountTemp = lineOfStatement.substring(poundIndex + 1, lineOfStatement.length() - 1);
                transAmount = transAmountTemp.substring(0, transAmount.indexOf(" "));

                //TODO balance ??

                Transactions trans = new Transactions();

                trans.setTransactionDate(LocalDate.parse(transDate, myformatter));
                trans.setTransactionDetails(transDetails);
                trans.setTransactionAmount(Double.parseDouble(transAmount));
                traslistFinal.getTransactionsList().add(trans);
                finalBalance = finalBalance + trans.getTransactionAmount();

            }
        }//while

        traslistFinal.setFinalBalance(finalBalance);

        inputStream.close();
        return traslistFinal;
    }
}





