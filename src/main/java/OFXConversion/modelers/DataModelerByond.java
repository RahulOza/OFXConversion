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

    private Double finalBalance = 0.0;


    public TransactionList createTransactionList(String sourceFileName) throws IOException {

        TransactionList translistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss", Locale.ENGLISH);



            String lineOfStatement;
            Boolean isHeader = true;
            Boolean firstRec = true;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {

                    String tokens[] = lineOfStatement.split(",");
                    // we know the tokens are
                    // RetailerName,Date,Spend,Earned,CardBalance,Category
                    //      0         1    2     3        4         5
                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();
                        trans.setTransactionDetails(tokens[0]);
                        trans.setTransactionDate(LocalDate.parse(tokens[1], myformatter));
                        trans.setTransactionAmount(Double.parseDouble(tokens[2]));
                        translistFinal.getTransactionsList().add(trans);

                        String credit = tokens[5];

                        //The final balance is in very first row
                        if(!tokens[4].isEmpty()) {

                            if (firstRec) {
                                finalBalance = Double.parseDouble(tokens[4]);
                                firstRec = false;
                            }
                            //Initial balance is towards the end so keep overwriting
                            // Initial balance is AFTER the first transaction so add the value of transaction to get the actual initial value.
                            translistFinal.setInitialBalance(Double.parseDouble(tokens[4]) + trans.getTransactionAmount());
                        }
                    }
                }

                if (isHeader)
                    isHeader = false;
            }
            translistFinal.setFinalBalance(finalBalance);
        }
        return translistFinal;
    }

}

