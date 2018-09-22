package pdftoofx;

import java.util.ArrayList;
import java.util.List;

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

}
