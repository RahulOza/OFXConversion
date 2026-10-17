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

    public void loadAndSanitiseCols(String [] tokens) throws Exception {
        //get index of mandatory cols, ensure minimum number of fields are there to process data
        int ctr = 0;
        for(String token : tokens){

            if(token.equals("Action")){
                Col.put("Action",ctr);
            }
            if(token.equals("Time (UTC)")){
                Col.put("Timestamp",ctr);
            }
            if(token.equals("ISIN")){
                Col.put("ISIN",ctr);
            }
            if(token.equals("Ticker")){
                Col.put("Ticker",ctr);
            }
            if(token.equals("Name")){
                Col.put("Title",ctr);
            }
            if(token.equals("Notes")){
                Col.put("Notes",ctr);
            }
            //if(token.equals("Order ID")){
            //    Col.put("Order ID",ctr);
            //}
            if(token.equals("No. of shares")){
                Col.put("Quantity",ctr);
            }
            if(token.equals("Price / share")){
                Col.put("Price per Share in Account Currency",ctr);
            }
            if(token.equals("Currency (Price / share)")){
                Col.put("Account Currency",ctr);
            }
            if(token.equals("Exchange rate")){
                Col.put("FX Rate",ctr);
            }
            if(token.equals("Total")){
                Col.put("Total Amount",ctr);
            }
            if(token.equals("Currency(Total)")){
                Col.put("Currency Total",ctr);
            }
            /* if(token.equals("Withholding Tax")){
                Col.put("Withholding Tax",ctr);
            }
            if(token.equals("Currency Withholding Tax")){
                Col.put("Currency Withholding Tax",ctr);
            }*/
            if(token.equals("Merchant name")){
                Col.put("Merchant name",ctr);
            }
            if(token.equals("Merchant category")){
                Col.put("Merchant Category",ctr);
            }
            ctr++;
        }

        if(ctr < 14){
            throw new Exception("Less than 14 critical fields in the file ..pls revisit");
        }

        if(!Col.containsKey("Action")){
            throw new Exception("Missing Action field in the file ..pls revisit");
        }
        if(!Col.containsKey("Timestamp")){
            throw new Exception("Missing Time (UTC) field in the file ..pls revisit");
        }
        if(!Col.containsKey("Total Amount")){
            throw new Exception("Missing Total field in the file ..pls revisit");
        }

    }

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
        TransactionList translistCardFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();

        invTranslistFinal.readSymbolMap();

        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

            String lineOfStatement;
            boolean isHeader = true;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                String[] tokens = lineOfStatement.split(",",-1);
                if(isHeader){
                    loadAndSanitiseCols(tokens);
                }
                else {// first line is the header so ignore it
                //!header
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



                    if (tokens[Col.get("Action")].equals("Market buy") || tokens[Col.get("Action")].startsWith("Dividend") || tokens[Col.get("Action")].equals("Market sell")) {
                        //Investment transactions ..

                        //Also set the amount in card as a transfer
                        transCard.setTransactionDetails(OfxgenGetPropertyValues.trading212XferText);
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
                isHeader = false;
            }//while not null
        } //try
        return (new AllTransactions(invTranslistFinal, translistFinal, translistCardFinal));
    }//function
}//class



