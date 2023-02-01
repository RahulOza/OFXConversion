package OFXConversion.data;

public class InvTransactions extends Transactions {
    private String invName;
    private int invQuantity;
    private double invPrice;
    private double invCommission;

    private String invSymb;

    private TransactionTypes invTransactionType;

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public int getInvQuantity() {
        return invQuantity;
    }

    public void setInvQuantity(int invQuantity) {
        this.invQuantity = invQuantity;
    }

    public double getInvPrice() {
        return invPrice;
    }

    public void setInvPrice(double invPrice) {
        this.invPrice = invPrice;
    }

    public double getInvCommission() {
        return invCommission;
    }

    public void setInvCommission(double invCommission) {
        this.invCommission = invCommission;
    }

    public TransactionTypes getInvTransactionType() {
        return invTransactionType;
    }

    public void setInvTransactionType(TransactionTypes invTransactionType) {
        this.invTransactionType = invTransactionType;
    }

    public String getInvSymb() {
        return invSymb;
    }

    public void setInvSymb(String invSymb) {
        this.invSymb = invSymb;
    }
}
