package OFXConversion.modelers;

import OFXConversion.data.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataModelerTrading212 {

    private final Map<String, Integer> Col;

    public DataModelerTrading212() {
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
        //No. of shares
        Col.put("Quantity", 5);
        // Price / Share
        Col.put("Price per Share in Account Currency", 6);
        // Currency (Price / share)
        Col.put("Account Currency", 7);
        //Exchange rate
        Col.put("FX Rate", 8);
        // Currency (Result)
        Col.put("Currency Result",9);
        //Total
        Col.put("Total Amount", 10);
        // Currency (Total)
        Col.put("Currency Total",11);

        //Cols below were removed, noticed on 16 Nov TODO: remove these later
        //Col.put("Debit", 10);
        //Col.put("Stamp Duty", 11);

        Col.put("Notes", 12);
        Col.put("Order ID", 13);
    }

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
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
                        throw new Exception("Less than 14 fields in this line ..pls revisit");
                    }
                    Transactions trans = new Transactions();
                    InvTransactions itrans = new InvTransactions();
                    //some transactions are cash only while others are investments as well
                    // DIVIDEND ==> Inv and cash
                    // Market Buy ==> Inv and cash
                    // Deposit ==> cash only
                    // INTEREST_FROM_CASH ==> cash only

                    //Date
                    trans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));
                    itrans.setTransactionDate(LocalDate.parse(tokens[Col.get("Timestamp")].substring(0,10), myformatter));

                    //transaction details
                    trans.setTransactionDetails(tokens[Col.get("Action")]);
                    itrans.setTransactionDetails(tokens[Col.get("Title")] + ":" + tokens[Col.get("Action")]);

                    //Amount
                    trans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));
                    itrans.setTransactionAmount(Double.parseDouble(tokens[Col.get("Total Amount")]));

                   /* if((!(tokens[Col.get("Action")].equals("Deposit")) || !(tokens[Col.get("Action")].equals("Market Buy")))) {

                        //we only know about these two types for now, if anything else then need to error ??
                        throw new Exception("Unknown Action, Neeed to code for this!");
                        //TODO original loop for withdrowal, so fix this when the right time comes.
                        //if withdrawal then amount is negative
                        //trans.setTransactionAmount(-Double.parseDouble(tokens[Col.get("Total Amount")]));
                    }*/

                    //TODO - we don't know the text for dividend
                    if (tokens[Col.get("Action")].equals("Market buy") || tokens[Col.get("Action")].equals("DIVIDEND")) {
                        //Investment transactions ..

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
                            /*if(!tokens[Col.get("Stamp Duty")].isEmpty()){
                                itrans.setInvCommission(itrans.getInvCommission() + Double.parseDouble(tokens[Col.get("Stamp Duty")]));
                            }*/

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
