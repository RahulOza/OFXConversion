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
}
