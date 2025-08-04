package OFXConversion.modelers;

import OFXConversion.data.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataModelerEquate {

    private final Map<String, Integer> Col;

    public DataModelerEquate() {
        Col = new HashMap<>();
    }
    public void populateCols() {
        // csv formate as below
        Col.put("Allocation date", 0);
        Col.put("Share type", 1);
        Col.put("Tax-free from", 2);
        Col.put("Acquisition price", 3);
        Col.put("Acquisition price (unit)", 4);
        Col.put("Quantity", 5);
        Col.put("Estimated value", 6);
        Col.put("Estimated value (unit)", 7);
    }

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();

        invTranslistFinal.readSymbolMap();
        populateCols();

        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

            String lineOfStatement;
            boolean isHeader = true;

            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {

                    // remove all double quotes
                    lineOfStatement = lineOfStatement.replace("\"","");
                    //Sept is an odd one ! so changing to 3 chars
                    lineOfStatement = lineOfStatement.replace("Sept","Sep");

                    String[] tokens = lineOfStatement.split(",",-1);

                    if (tokens.length < 7) {
                        //if there are less than the mandated fields we cannot process
                        throw new Exception("Less than 7 fields in this line ..pls revisit");
                    }
                    Transactions trans = new Transactions();
                    InvTransactions itrans = new InvTransactions();

                    //Date
                    String allocationDate = tokens[Col.get("Allocation date")];
                    // pad a zero if less than 11 chars so that pattern would match dd mmm yyyy
                    if(allocationDate.length() < 11)
                           allocationDate = "0" + allocationDate;
                    trans.setTransactionDate(LocalDate.parse(allocationDate, myformatter));
                    // set cash transaction date to previous date
                    trans.setTransactionDate(trans.getTransactionDate().minusDays(1));
                    itrans.setTransactionDate(LocalDate.parse(allocationDate, myformatter));
                    //transaction details
                    trans.setTransactionDetails("NWG Shares");
                    itrans.setTransactionDetails(tokens[Col.get("Share type")]);

                    String invSymb = "NWG";
                    //Ticker
                    if(invTranslistFinal.getReverseSymbolMap().containsKey(invSymb)) {
                        itrans.setInvSymb(invSymb);
                        itrans.setInvName(invTranslistFinal.getReverseSymbolMap().get(invSymb)[0]);
                    }
                    else{
                        throw new Exception("Symbol in statement does not exist in mapfile, please add it to mapfile:"+ invSymb);
                    }
                    itrans.setInvTransactionType(TransactionTypes.STOCK_BUY);

                    //Quantity
                    itrans.setInvQuantity(Double.parseDouble(tokens[Col.get("Quantity")]));
                    //price
                    itrans.setInvPrice(Double.parseDouble(tokens[Col.get("Acquisition price")]));

                    //Amount
                    trans.setTransactionAmount(itrans.getInvQuantity() * itrans.getInvPrice());
                    itrans.setTransactionAmount(itrans.getInvQuantity() * itrans.getInvPrice());

                    //trans.setTransactionAmount(-trans.getTransactionAmount());
                    invTranslistFinal.getInvTransactionsList().add(itrans);
                    translistFinal.getTransactionsList().add(trans);
                }//header

                if (isHeader)
                    isHeader = false;
            }//while not null
        } //try
        return (new AllTransactions(invTranslistFinal, translistFinal));
    }//function
}//class
