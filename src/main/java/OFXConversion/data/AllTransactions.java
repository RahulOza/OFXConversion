package OFXConversion.data;

public class AllTransactions {
    private InvTransactionList invTrans;
    private TransactionList cashTrans;
    private TransactionList cardTrans;


    public AllTransactions(InvTransactionList iTrans, TransactionList cTrans){
        this.invTrans = iTrans;
        this.cashTrans = cTrans;
    }

    public AllTransactions(InvTransactionList iTrans, TransactionList cTrans, TransactionList cTransCard){
        this.invTrans = iTrans;
        this.cashTrans = cTrans;
        this.cardTrans = cTransCard;
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

    public TransactionList getCardTrans() {
        return cardTrans;
    }
    public void setCardTrans(TransactionList cardTrans) {
        this.cardTrans = cardTrans;
    }
}
