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
        Col.put("Action", 0);
        //Time
        Col.put("Timestamp", 1);
        Col.put("ISIN", 2);
        Col.put("Ticker", 3);
        //Name
        Col.put("Title", 4);
        //Notes
        Col.put("Notes", 5);
        Col.put("Order ID", 6);
        //No. of shares
        Col.put("Quantity", 7);
        // Price / Share
        Col.put("Price per Share in Account Currency", 8);
        // Currency (Price / share)
        Col.put("Account Currency", 9);
        //Exchange rate
        Col.put("FX Rate", 10);
        // Currency (Result)
        Col.put("Currency Result",11);
        //Total
        Col.put("Total Amount", 12);
        // Currency (Total)
        Col.put("Currency Total",13);
        //Currency converson fee
        Col.put("Currency Conversion Fee",14);
        //Currency of the currency conversion fee
        Col.put("Currency",15);
        //Merchant Name
        Col.put("Merchant name",16);
        //Merchant Category
        Col.put("Merchant Category",17);
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

                        /*
                        //set this amount as credit from T212Card account
                        trans.setTransactionDetails("R T212Card to T212");
                        trans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));
                        trans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));

                        translistFinal.getTransactionsList().add(trans);*/

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

                        //Name
                        itrans.setInvName(tokens[Col.get("Title")]);
                        //Ticker
                        itrans.setInvSymb(tokens[Col.get("Ticker")]);


                        if (tokens[Col.get("Action")].equals("DIVIDEND")) {
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
                                    throw new Exception("invalid symbol encountered");
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



