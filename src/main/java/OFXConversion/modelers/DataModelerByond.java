package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataModelerByond {


    public TransactionList createTransactionList(String sourceFileName) throws IOException {
        Double finalBalance;

        TransactionList translistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH);

            String lineOfStatement;
            boolean isHeader = true;
            Double adjBalance = 0.0;

            while (null != (lineOfStatement = inputStream.readLine())) {

                // first line is the header so ignore it
                if (!isHeader) {

                    String[] tokens = lineOfStatement.split(",");
                    // we know the tokens are
                    // RetailerName,Date,Spend,Earned,CardBalance,Category
                    //      0         1    2     3        4         5
                    // ignore the record where balance is empty, it is still not final
                    if (tokens.length > 3) {
                        Transactions trans = new Transactions();
                        trans.setTransactionDetails(tokens[0]);
                        trans.setTransactionDate(LocalDate.parse(tokens[1], myformatter));
                        //There is no credit/debit indicator in the statement, all values are positive.
                        // if Category 'Fund in' is positive then it is a credit else a debit.
                        /*if((tokens.length >= 6) && tokens[5].equals("Fund in")){
                            //This is credit
                            trans.setTransactionAmount(-Double.parseDouble(tokens[2]));
                        }
                        else {
                            //This amount is going out hence debit/negative
                            trans.setTransactionAmount(Double.parseDouble(tokens[2]));
                        }*/
                        //transaction values are inverted in the csv file
                        trans.setTransactionAmount(-Double.parseDouble(tokens[2]));

                        translistFinal.getTransactionsList().add(trans);

                       //The final balance is in very first row

                        //Initial balance is towards the end so keep overwriting
                        // Initial balance is AFTER the first transaction so add the value of transaction to get the actual initial value.
                        if((tokens.length >= 5) && !tokens[4].isEmpty()) {
                            translistFinal.setInitialBalance(Double.parseDouble(tokens[4]) + trans.getTransactionAmount());
                        }
                        else{
                            adjBalance = adjBalance + trans.getTransactionAmount();
                        }
                    }
                }

                if (isHeader)
                    isHeader = false;
            }

            translistFinal.setInitialBalance(translistFinal.getInitialBalance()+adjBalance);

            finalBalance = translistFinal.getInitialBalance();
            for(Transactions trans: translistFinal.getTransactionsList()){
                finalBalance = finalBalance - trans.getTransactionAmount();
            }
            translistFinal.setFinalBalance(finalBalance);
        }
        return translistFinal;
    }

}

