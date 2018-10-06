package pdftoofx;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionList {

    Double initialBalance = 0.0;
    Double finalBalance = 0.0;

    List<Transactions> transactionsListFinal = new ArrayList<Transactions>();

    void printTransactionList(){

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

    Boolean datesOutOfSequence(){

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
