package OFXConversion.modelers;

import OFXConversion.data.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataModelerTrading212Card {

    private final Map<String, Integer> Col;

    public DataModelerTrading212Card() {
        Col = new HashMap<>();
    }

    public void populateCols() {
        /* As they keep messing with the cols, col numbers will be variables.
        */
        int ColIndex = 0;

        //Action - Col 0
        Col.put("Action", ColIndex++);
        //Time - Col 1
        Col.put("Timestamp", ColIndex++);
        // ISIN - Col 2
        Col.put("ISIN", ColIndex++);
        //Ticker - Col 3
        Col.put("Ticker", ColIndex++);
        //Name - Col 4
        Col.put("Title", ColIndex++);
        //Notes - Col 5
        Col.put("Notes", ColIndex++);
        //ID - Col 6
        Col.put("Order ID", ColIndex++);
        //No. of shares - Col 7
        Col.put("Quantity", ColIndex++);
        // Price / Share - Col 8
        Col.put("Price per Share in Account Currency", ColIndex++);
        // Currency (Price / share) - Col 9
        Col.put("Account Currency", ColIndex++);
        //Exchange rate - Col 10
        Col.put("FX Rate", ColIndex++);
        //Result - Col 11
        //Column removed due to some reason, may reinstate later!
        //Col.put("Result",ColIndex+ 1);
        // Currency (Result) - Col 12
        Col.put("Currency Result",ColIndex++);
        //Total - Col 13
        Col.put("Total Amount", ColIndex++);
        //Currency (Total) - Col 14
        Col.put("Currency Total", ColIndex++);
        //Withholding Tax - Col 14.1
        Col.put("Withholding Tax", ColIndex++);
        //Currency Withholding Tax - Col 14.2
        Col.put("Currency Withholding Tax", ColIndex++);
        //Stamp Duty Reserve currency - Col 15
        Col.put("Stamp Duty Reserve currency",ColIndex++);
        //Currency (Stamp Duty) - Col 16
        Col.put("Currency Stamp Duty",ColIndex++);
        //Currency converson fee - Col 17
        //Col.put("Currency Conversion Fee",ColIndex+ 1);
        //Currency of the currency conversion fee - Col 18
        //Col.put("Currency", ColIndex+ 1);
        //Merchant Name - Col 19
        Col.put("Merchant name", ColIndex++);
        //Merchant Category - Col 20
        Col.put("Merchant Category", ColIndex);
    }

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
        TransactionList translistCardFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();

        invTranslistFinal.readSymbolMap();
        populateCols();

        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

            String lineOfStatement;
            boolean isHeader = true;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {

                    String[] tokens = lineOfStatement.split(",",-1);

                    if (tokens.length < 14) {
                        //if there are less than the mandated fields we cannot process
                        throw new Exception("Less than 16 fields in this line ..pls revisit");
                    }
                    Transactions transCard = new Transactions();
                    Transactions trans = new Transactions();
                    InvTransactions itrans = new InvTransactions();

                    //some transactions are cash only while others are investments as well
                    // DIVIDEND ==> Inv and cash
                    // Market Buy ==> Inv and cash
                    // Deposit ==> cash only
                    // INTEREST_FROM_CASH ==> cash only


                        //These are Debit card transactions
                        if(tokens[Col.get("Action")].equals("Card debit"))
                            transCard.setTransactionDetails(tokens[Col.get("Merchant name")]);
                        else {
                            //Action is same as transaction detail?
                            transCard.setTransactionDetails(tokens[Col.get("Action")]);
                        }
                        transCard.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));
                        transCard.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));



                    if (tokens[Col.get("Action")].equals("Market buy")) {
                        //Investment transactions ..

                        //Also set the amount in card as a transfer
                        transCard.setTransactionDetails("R T212Card to T212");
                        transCard.setTransactionAmount(-Double.parseDouble(tokens[Col.get("Total Amount")]));
                        transCard.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));

                        //transaction details
                        trans.setTransactionDetails(tokens[Col.get("Action")]);
                        itrans.setTransactionDetails(tokens[Col.get("Title")] + ":" + tokens[Col.get("Action")]);

                        //Amount
                        trans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));
                        itrans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));

                        trans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));
                        itrans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));

                        String invName = tokens[Col.get("Title")].replace("\"","");
                        //Name
                        itrans.setInvName(invName);
                        //Ticker
                        itrans.setInvSymb(tokens[Col.get("Ticker")]);


                        if (tokens[Col.get("Action")].startsWith("Dividend")) {
                            itrans.setInvTransactionType(TransactionTypes.DIVIDEND);
                        } else {
                            //This is buy or sell order

                            //Quantity
                            itrans.setInvQuantity(Double.parseDouble(tokens[Col.get("Quantity")]));
                            //price
                            itrans.setInvPrice(Double.parseDouble(tokens[Col.get("Price per Share in Account Currency")])/Double.parseDouble(tokens[Col.get("FX Rate")]));
                            //commission = fx fees + stamp duty for shares


                            switch (invTranslistFinal.getReverseSymbolMap().get(itrans.getInvSymb())[1]) {
                                case "MF":
                                    if (tokens[Col.get("Action")].equals("Market buy")) {
                                        itrans.setInvTransactionType(TransactionTypes.MF_BUY);
                                        //set negative amount for cash transactions for BUY
                                        trans.setTransactionAmount(-trans.getTransactionAmount());
                                    } else {
                                        itrans.setInvTransactionType(TransactionTypes.MF_SELL);
                                        itrans.setInvQuantity(-itrans.getInvQuantity());
                                    }
                                    break;
                                case "ST":
                                    if (tokens[Col.get("Action")].equals("Market buy")) {
                                        itrans.setInvTransactionType(TransactionTypes.STOCK_BUY);
                                        //set negative amount for cash transactions for BUY
                                        trans.setTransactionAmount(-trans.getTransactionAmount());
                                    } else {
                                        itrans.setInvTransactionType(TransactionTypes.STOCK_SELL);
                                        itrans.setInvQuantity(-itrans.getInvQuantity());
                                    }
                                    break;
                                default:
                                    throw new Exception("invalid symbol encountered:" + itrans.getInvSymb());
                            }//switch

                        }//buy/sell if/else
                        invTranslistFinal.getInvTransactionsList().add(itrans);
                        translistFinal.getTransactionsList().add(trans);
                    }// order or dividend
                    translistCardFinal.getTransactionsList().add(transCard);
                }//header

                if (isHeader)
                    isHeader = false;
            }//while not null
        } //try
        return (new AllTransactions(invTranslistFinal, translistFinal, translistCardFinal));
    }//function
}//class



