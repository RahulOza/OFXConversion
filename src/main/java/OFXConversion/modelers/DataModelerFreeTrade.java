package OFXConversion.modelers;

import OFXConversion.data.*;
import groovy.transform.Immutable;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class DataModelerFreeTrade {

    private Map<String, Integer> Col = new HashMap<>();

    public void populateCols() {

        Col.put("Title", 0);
        Col.put("Type", 1);
        Col.put("Timestamp", 2);
        Col.put("Account Currency", 3);
        Col.put("Total Amount", 4);
        Col.put("Buy / Sell", 5);
        Col.put("Ticker", 6);
        Col.put("ISIN", 7);
        Col.put("Price per Share in Account Currency", 8);
        Col.put("Stamp Duty", 9);
        Col.put("Quantity", 10);
        Col.put("Venue", 11);
        Col.put("Order ID", 12);
        Col.put("Order Type", 13);
        Col.put("Instrument Currency", 14);
        Col.put("Total Shares Amount", 15);
        Col.put("Price per Share", 16);
        Col.put("FX Rate", 17);
        Col.put("Base FX Rate", 18);
        Col.put("FX Fee (BPS)", 19);
        Col.put("FX Fee Amount", 20);
        Col.put("Dividend Ex Date", 21);
        Col.put("Dividend Pay Date", 22);
        Col.put("Dividend Eligible Quantity", 23);
        Col.put("Dividend Amount Per Share", 24);
        Col.put("Dividend Gross Distribution Amount", 25);
        Col.put("Dividend Net Distribution Amount", 26);
        Col.put("Dividend Withheld Tax Percentage", 27);
        Col.put("Dividend Withheld Tax Amount", 28);
    }

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();
        boolean isFirstRec = true;
        boolean cashTransStatements = false;
        boolean invTransStatements = false;

        invTranslistFinal.readSymbolMap();
        populateCols();

        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

            String lineOfStatement;
            boolean isHeader = true;
            Double adjBalance = 0.0;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {

                    String[] tokens = lineOfStatement.split(",",-1);
                    // we know the tokens are
                    // Title,Type,Timestamp,Account Currency,Total Amount,Buy / Sell,Ticker,ISIN,Price per Share in Account Currency,Stamp Duty,
                    //  0    1      2           3                4             5       6      7                   8                    9
                    // Quantity,Venue,Order ID,Order Type,Instrument Currency,Total Shares Amount,Price per Share,FX Rate,Base FX Rate,FX Fee (BPS),
                    //    10      11    12         13              14              15                 16             17        18          19
                    // FX Fee Amount,Dividend Ex Date,Dividend Pay Date,Dividend Eligible Quantity,Dividend Amount Per Share,
                    //    20               21                22                23                        24
                    // Dividend Gross Distribution Amount,Dividend Net Distribution Amount,Dividend Withheld Tax Percentage,Dividend Withheld Tax Amount
                    //                 25                             26                               27                         28
                    if (tokens[Col.get("Type")].equals("MONTHLY_STATEMENT")) {
                        //nothing to process if just monthly statement
                        continue;
                    }
                    if (tokens.length < 28) {
                        //if there are less than the mandated fields we cannot process
                        throw new Exception("Less than 28 fields in this line ..pls revisit");
                    }
                    Transactions trans = new Transactions();
                    InvTransactions itrans = new InvTransactions();
                    //some transactions are cash only while others are investments as well
                    // DIVIDEND ==> Inv and cash
                    // ORDER, ==> Inv and cash
                    // TOP_UP ==> cash only
                    // INTEREST_FROM_CASH ==> cash only

                    //Date
                    trans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));
                    itrans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));

                    //transaction details
                    trans.setTransactionDetails(tokens[Col.get("Title")]);
                    itrans.setTransactionDetails(tokens[Col.get("Title")] + ":" + tokens[Col.get("Type")]);

                    //Amount
                    trans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));
                    itrans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));

                    if (tokens[1].equals("ORDER") || tokens[1].equals("DIVIDEND")) {
                        //Investment transactions ..

                        //Name
                        itrans.setInvName(tokens[Col.get("Title")]);
                        //Ticker
                        itrans.setInvSymb(tokens[Col.get("Ticker")]);


                        if (tokens[1].equals("DIVIDEND")) {
                            itrans.setInvTransactionType(TransactionTypes.DIVIDEND);
                        } else {
                            //This is buy or sell order

                            //Quantity
                            itrans.setInvQuantity(Double.parseDouble(tokens[Col.get("Quantity")]));
                            //price
                            itrans.setInvPrice(Double.parseDouble(tokens[Col.get("Price per Share in Account Currency")]));
                            //commission = fx fees + stamp duty for shares
                            if(!tokens[Col.get("FX Fee Amount")].isEmpty()){
                                itrans.setInvCommission(Double.parseDouble(tokens[Col.get("FX Fee Amount")]));
                            }
                            if(!tokens[Col.get("Stamp Duty")].isEmpty()){
                                itrans.setInvCommission(itrans.getInvCommission() + Double.parseDouble(tokens[Col.get("Stamp Duty")]));
                            }

                            switch (invTranslistFinal.getReverseSymbolMap().get(itrans.getInvSymb())[1]) {
                                case "MF":
                                    if (tokens[Col.get("Buy / Sell")].equals("BUY")) {
                                        itrans.setInvTransactionType(TransactionTypes.MF_BUY);
                                        //set negative amount for cash transactions for BUY
                                        trans.setTransactionAmount(-trans.getTransactionAmount());
                                    } else
                                        itrans.setInvTransactionType(TransactionTypes.MF_SELL);
                                    break;
                                case "ST":
                                    if (tokens[Col.get("Buy / Sell")].equals("BUY")) {
                                        itrans.setInvTransactionType(TransactionTypes.STOCK_BUY);
                                        //set negative amount for cash transactions for BUY
                                        trans.setTransactionAmount(-trans.getTransactionAmount());
                                    } else
                                        itrans.setInvTransactionType(TransactionTypes.STOCK_SELL);
                                    break;
                                default:
                                    throw new Exception("invalid symbol encountered");
                            }//switch
                        }//buy/sell if/else
                        invTranslistFinal.getInvTransactionsList().add(itrans);
                    }// order or dividend

                    translistFinal.getTransactionsList().add(trans);
                }//header

                if (isHeader)
                    isHeader = false;
            }//while not null
        } //try
        return (new AllTransactions(invTranslistFinal, translistFinal));
    }//function
}//class
