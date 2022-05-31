package OFXConversion.modelers;

import OFXConversion.data.OfxgenGetPropertyValues;
import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataModelerByond {

    private Double finalBalance = 0.0;

    public TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList traslistFinal = new TransactionList();
        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        traslistFinal.setInitialBalance(initialBalance);
        String lineOfStatement;
        int lineNum = 0;
        String transDetails ="";
        String transDate = "";
        String transAmount = "";

        //initialise final balance.
        finalBalance = initialBalance;

        while((lineOfStatement = inputStream.readLine()) != null) {

            if(lineOfStatement.isEmpty()){
                lineNum = 0;
                transDetails ="";
                transDate = "";
                transAmount = "";

            }

            // empty lines need to be skipped and mark begining of new transactions
            while(!lineOfStatement.isEmpty()) {
                lineNum++;

                if (lineNum == 1) {
                    //1st line contains the transaction details
                    transDetails = lineOfStatement;
                    break;
                }
                if (lineNum == 2) {
                    // second line contains date
                    transDate = lineOfStatement;
                    break;
                }
                if (lineNum == 3) {
                    // third line is the amount
                    transAmount = lineOfStatement.replace("£","");
                }
                if (lineNum == 4 || lineNum == 5) {
                    // 4th and 5th lines are cashback and/or balances, ignore for now
                    break;
                }

                if( lineNum >= 3) {
                    // if we have 3 valid lines of details that make up a trasaction
                    Transactions trans = new Transactions();

                    trans.setTransactionDate(LocalDate.parse(transDate, myformatter));
                    trans.setTransactionDetails(transDetails);
                    trans.setTransactionAmount(Double.parseDouble(transAmount));
                    traslistFinal.getTransactionsList().add(trans);
                    finalBalance = finalBalance + trans.getTransactionAmount();
                    break;
                }
            }//while
        }

        traslistFinal.setFinalBalance(finalBalance);

        inputStream.close();
        return traslistFinal;
    }
}

