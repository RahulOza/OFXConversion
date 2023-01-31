package OFXConversion.data;

import java.util.ArrayList;
import java.util.List;

public class InvTransactionList {
    private List<InvTransactions> invTransactionsList = new ArrayList<>();
    private Double initialBalance = 0.0;
    private Double finalBalance = 0.0;

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
            System.out.println(" Inv Quant : " + i.getInvQuantity());
            System.out.println(" Inv Price : " + i.getInvPrice());
            System.out.println(" Inv Comm : " + i.getInvCommission());
            System.out.println(" Inv Type : " + i.getInvTransactionType());
            System.out.println(" Details : " + i.transactionDetails);
            System.out.println(" Amount : £" + i.transactionAmount.toString());
        }
        System.out.println(" *********************************************** ");
    }
}

