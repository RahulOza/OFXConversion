package OFXConversion.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InvTransactionList implements Comparator<InvTransactions> {
    private List<InvTransactions> invTransactionsList = new ArrayList<>();
    private Double initialBalance = 0.0;
    private Double finalBalance = 0.0;

    private HashMap<String, String[]> invSymbolMap = new HashMap<String, String[]>();

    private HashMap<String, String[]> reverseSymbolMap = new HashMap<String, String[]>();

    public List<InvTransactions> getInvTransactionsList() {
        return invTransactionsList;
    }

    public void setInvTransactionsList(List<InvTransactions> invTransactionsList) {
        this.invTransactionsList = invTransactionsList;
    }

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Double getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Double finalBalance) {
        this.finalBalance = finalBalance;
    }

    public void printTransactionList(){

        System.out.println(" *********************************************** ");
        System.out.println(" *********************************************** ");
        System.out.println(" Transaction List Printing, Initial Bal = "+initialBalance+" Final Bal = " + finalBalance);
        int ctr = 1;
        for (InvTransactions i: invTransactionsList) {
            System.out.println(" Item : " + ctr++);
            System.out.println(" Date : " + i.transactionDate.toString());
            System.out.println(" Inv Name : " + i.getInvName().toString());
            System.out.println(" Inv Symbol : " + i.getInvSymb());
            System.out.println(" Inv Quant : " + i.getInvQuantity());
            System.out.println(" Inv Price : " + i.getInvPrice());
            System.out.println(" Inv Comm : " + i.getInvCommission());
            System.out.println(" Inv Type : " + i.getInvTransactionType());
            System.out.println(" Details : " + i.transactionDetails);
            System.out.println(" Amount : £" + i.transactionAmount.toString());
        }
        System.out.println(" *********************************************** ");
    }

    public int getLength() { return invTransactionsList.size(); }

    public int compare(InvTransactions t1, InvTransactions t2){
        // Old dates on top, new dates at the bottom
        // 02/06/2019
        // 05/07/2019
        // 23/01/2020
        return t1.getTransactionDate().compareTo(t2.transactionDate);
    }

    public HashMap<String, String[]> getInvSymbolMap() {
        return invSymbolMap;
    }

    public void setInvSymbolMap(HashMap<String, String[]> invSymbolMap) {
        this.invSymbolMap = invSymbolMap;
    }

    public HashMap<String, String[]> getReverseSymbolMap() {
        return reverseSymbolMap;
    }

    public void setReverseSymbolMap(HashMap<String, String[]> reverseSymbolMap) {
        this.reverseSymbolMap = reverseSymbolMap;
    }

    public void readSymbolMap() throws Exception {
        try (BufferedReader inputStream = new BufferedReader(new FileReader(OfxgenGetPropertyValues.vanguardSymbolMapFile))) {
            String lineOfStatement;

            while ((lineOfStatement = inputStream.readLine()) != null) {
                //read all symbols and add to a hashmap
                String[] tokens = lineOfStatement.split(",");
                if(tokens.length == 3){
                    if(tokens[2].equals("MF") || tokens[2].equals("ST")){
                        invSymbolMap.put(tokens[0], new String[]{tokens[1], tokens[2]});
                    }
                    else {
                        throw new Exception("Invalid Investment Type - only 'MF' or 'ST' is valid");
                    }
                }
                else {
                    throw new Exception("Invalid Map file - format: SecurityName, SecuritySymbol, InvestmentType " +
                            "\n\t e.g. LifeStrategy 100% Equity Fund - Accumulation,GB00B41XG308,MF ");
                }
            }//while
            //populate reverse symbol map as well
            reverseSymbolMap();
        }//try ..open file
        catch(IOException e){
            System.out.println("Exception: " + e);

        }
    }
    public void reverseSymbolMap(){
        for (Map.Entry<String, String[]> entry : invSymbolMap.entrySet()) {
            String key = entry.getKey();
            String values[] = entry.getValue();

            //swap keys and values
            //LifeStrategy 100% Equity Fund - Accumulation,GB00B41XG308,MF => GB00B41XG308,LifeStrategy 100% Equity Fund - Accumulation
            //                Key                            values[0], values[1]
            reverseSymbolMap.put(values[0],new String[]{key,values[1]});
        }
    }
    public void printReverseSymbolMap(){
        int ctr=0;
        for (Map.Entry<String, String[]> entry : reverseSymbolMap.entrySet()) {
            String key = entry.getKey();
            String values[] = entry.getValue();
            System.out.println(" Item : " + ctr++);
            System.out.println(" Key : " + key);
            System.out.println(" Values : " + values.toString());
        }
    }
}

