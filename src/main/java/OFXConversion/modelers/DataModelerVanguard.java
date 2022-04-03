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



public class DataModelerVanguard {


    private List<String> transactionTokenList = new ArrayList<>();
    private List<String> transactionList = new ArrayList<>();
    private Double finalBalance = 0.0;


    public TransactionList createTransactionList(String sourceFileName, Double initialBalance) throws IOException {

        TransactionList translistFinal = new TransactionList();
        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);


            translistFinal.setInitialBalance(initialBalance);
            String lineOfStatement;
            Boolean isHeader = true;

            //initialise final balance.
            finalBalance = initialBalance;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {
                    //replace consecutive tabs by single
                    String newLineOfStatement = lineOfStatement.replaceAll("\t(?=\t)","");

                    String tokens[] = newLineOfStatement.split("\\t");
                    // we know the tokens are
                    // Date	Description	Amount(GBP)
                    //    17 May 2021\tBought 8 S&P 500 UCITS ETF Distributing (VUSA)\t\t−£448.79\t£1,555.59


                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();

                        trans.setTransactionDate(LocalDate.parse(tokens[0], myformatter));
                        trans.setTransactionDetails(tokens[1]);
                        //2 is empty
                        String transAmountWithComma = tokens[2].replace("£","");
                        String transAmount = transAmountWithComma.replace(",","");
                        trans.setTransactionAmount(Double.parseDouble(transAmount));
                        //TODO balance ??
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






