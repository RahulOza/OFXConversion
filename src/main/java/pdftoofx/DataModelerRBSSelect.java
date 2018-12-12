package pdftoofx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataModelerRBSSelect {

    List<String> transactionTokenList = new ArrayList<String>();
    List<String> transactionList = new ArrayList<String>();
    Double finalBalance = 0.0;

    TransactionList createTransactionList(String sourceFileName,Double initialBalance) throws IOException {

        TransactionList traslistFinal = new TransactionList();
        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);


        traslistFinal.initialBalance = initialBalance;
        String lineOfStatement;
        Boolean isHeader = true;

        //initialise final balance.
        finalBalance = -(initialBalance);

        while((lineOfStatement = inputStream.readLine()) != null) {

            // first line is the header so ignore it
            if(!isHeader){

                String tokens[] = lineOfStatement.split(",");
                // we know the tokens are
                // Date	Description	Amount(GBP)
                if(tokens.length > 1) {
                    Transactions trans = new Transactions();

                    trans.transactionDate = LocalDate.parse(tokens[0], myformatter);
                    trans.transactionDetails = tokens[1];
                    //This is specific to RBS Select, due to the way the statement is displayed and recorded.
                    trans.transactionAmount = -(Double.parseDouble(tokens[2]));

                    traslistFinal.transactionsListFinal.add(trans);
                    finalBalance = finalBalance + trans.transactionAmount;
                }
            }


            if(isHeader == true)
                isHeader = false;
        }
        traslistFinal.finalBalance = -(finalBalance);

        inputStream.close();
        return traslistFinal;
    }
}
