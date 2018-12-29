package OFXConversion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DataModelerAmazon {


    private List<String> transactionTokenList = new ArrayList<>();
    private List<String> transactionList = new ArrayList<>();
    private Double finalBalance = 0.0;


    TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList traslistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);


            traslistFinal.initialBalance = initialBalance;
            String lineOfStatement;
            Boolean isHeader = true;

            //initialise final balance.
            finalBalance = initialBalance;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {

                    String tokens[] = lineOfStatement.split(",");
                    // we know the tokens are
                    // Date	Description	Amount(GBP)
                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();

                        trans.transactionDate = LocalDate.parse(tokens[0], myformatter);
                        trans.transactionDetails = tokens[1];
                        trans.transactionAmount = Double.parseDouble(tokens[2]);

                        traslistFinal.transactionsListFinal.add(trans);
                        finalBalance = finalBalance + trans.transactionAmount;
                    }
                }

                if (isHeader)
                    isHeader = false;
            }
            traslistFinal.finalBalance = finalBalance;

            inputStream.close();
        }
        return traslistFinal;
    }
}
