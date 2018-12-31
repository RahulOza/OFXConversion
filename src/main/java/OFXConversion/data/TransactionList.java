package OFXConversion.data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionList {

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

    Double initialBalance = 0.0;
    Double finalBalance = 0.0;

    public List<Transactions> getTransactionsListFinal() {
        return transactionsListFinal;
    }

    public void setTransactionsListFinal(List<Transactions> transactionsListFinal) {
        this.transactionsListFinal = transactionsListFinal;
    }

    List<Transactions> transactionsListFinal = new ArrayList<Transactions>();

    public void printTransactionList(){

        System.out.println(" *********************************************** ");
        System.out.println(" *********************************************** ");
        System.out.println(" Transaction List Printing, Initial Bal = "+initialBalance+" Final Bal = " + finalBalance);
        int ctr = 1;
        for (Transactions t: transactionsListFinal) {
            System.out.println(" Item : " + ctr++);
            System.out.println(" Date : " + t.transactionDate.toString());
            System.out.println(" Details : " + t.transactionDetails);
            System.out.println(" Amount : £" + t.transactionAmount.toString());
        }
        System.out.println(" *********************************************** ");
    }

    public Boolean datesOutOfSequence(){

        for (Transactions t: transactionsListFinal) {

            // This is due to a bug in Amazon statements wherein dates are messed up
            // if transaction details contains a date, we have a problem
            // TODO we need to fix this

            Matcher m = Pattern.compile("\\d\\d-\\d\\d-\\d\\d").matcher(t.transactionDetails);

            if(m.find()){
                return true;

            }
        }
        return false;
    }

}
