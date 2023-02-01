package OFXConversion.data;

public class AllTransactions {
    private InvTransactionList invTrans;
    private TransactionList cashTrans;


    public AllTransactions(InvTransactionList iTrans, TransactionList cTrans){
        this.invTrans = iTrans;
        this.cashTrans = cTrans;
    }

    public InvTransactionList getInvTrans() {
        return invTrans;
    }

    public void setInvTrans(InvTransactionList invTrans) {
        this.invTrans = invTrans;
    }

    public TransactionList getCashTrans() {
        return cashTrans;
    }

    public void setCashTrans(TransactionList cashTrans) {
        this.cashTrans = cashTrans;
    }
}
