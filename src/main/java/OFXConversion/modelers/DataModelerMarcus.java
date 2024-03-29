package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataModelerMarcus {

    public TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList translistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH);


            translistFinal.setInitialBalance(initialBalance);
            String lineOfStatement;
            boolean isHeader = true;

            //initialise final balance.
            Double finalBalance = initialBalance;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {
                    // Marcus seem to insert a lot of " double quotes before string hence need to remove them all
                    String cleanLineOfStatement = lineOfStatement.replace("\"","");

                    String[] tokens = cleanLineOfStatement.split(",");
                    // we know the tokens are
                    // Date	Description	Amount(GBP)
                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();

                        trans.setTransactionDate(LocalDate.parse(tokens[0], myformatter));
                        trans.setTransactionDetails(tokens[1]);
                        trans.setTransactionAmount((Double.parseDouble(tokens[2])));

                        translistFinal.getTransactionsList().add(trans);
                        finalBalance = finalBalance + trans.getTransactionAmount();
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
