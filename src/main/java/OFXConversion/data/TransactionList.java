package OFXConversion.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionList implements Comparator<Transactions> {

    private List<Transactions> transactionsList = new ArrayList<>();
    private Double initialBalance = 0.0;
    private Double finalBalance = 0.0;

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

    public List<Transactions> getTransactionsList() {
        return transactionsList;
    }

    public void printTransactionList(){

        System.out.println(" *********************************************** ");
        System.out.println(" *********************************************** ");
        System.out.println(" Transaction List Printing, Initial Bal = "+initialBalance+" Final Bal = " + finalBalance);
        int ctr = 1;
        for (Transactions t: transactionsList) {
            System.out.println(" Item : " + ctr++);
            System.out.println(" Date : " + t.transactionDate.toString());
            System.out.println(" Details : " + t.transactionDetails);
            System.out.println(" Amount : £" + t.transactionAmount.toString());
        }
        System.out.println(" *********************************************** ");
    }

    public int getLength() { return transactionsList.size(); }

    public int compare(Transactions t1, Transactions t2){
        // Old dates on top, new dates at the bottom
        // 02/06/2019
        // 05/07/2019
        // 23/01/2020

        return t1.getTransactionDate().compareTo(t2.transactionDate);
    }

}
