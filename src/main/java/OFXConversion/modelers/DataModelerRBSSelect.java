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

public class DataModelerRBSSelect {

    private Double finalBalance = 0.0;

    public TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList traslistFinal = new TransactionList();
        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH);


        traslistFinal.setInitialBalance(initialBalance);
        String lineOfStatement;
        Boolean isHeader = true;

        //initialise final balance.
        finalBalance = initialBalance;

        while((lineOfStatement = inputStream.readLine()) != null) {


            if(!isHeader){
                // Manual steps - remove Fin: and Auth: words
                String cleanLineOfStatement1 = lineOfStatement.replace("Fin: ","");
                String cleanLineOfStatement2 = cleanLineOfStatement1.replace("Auth: ","");
                // also remove £ symbol
                String cleanLineOfStatement3 = cleanLineOfStatement2.replace("£","");

                String tokens[] = cleanLineOfStatement3.split("\\t");

                // we know the tokens are
                // Date	Description	Amount(GBP)
                if(tokens.length > 1) {
                    Transactions trans = new Transactions();

                    trans.setTransactionDate(LocalDate.parse(tokens[0], myformatter));
                    trans.setTransactionDetails(tokens[1]);
                    trans.setTransactionAmount(Double.parseDouble(tokens[2]));
                    traslistFinal.getTransactionsList().add(trans);
                    finalBalance = finalBalance + trans.getTransactionAmount();
                }
            } //header


            if(isHeader == true)
                isHeader = false;
        }
        traslistFinal.setFinalBalance(finalBalance);

        inputStream.close();
        return traslistFinal;
    }
}
