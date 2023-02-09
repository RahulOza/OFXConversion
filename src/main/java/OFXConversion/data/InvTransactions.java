package OFXConversion.data;

public class InvTransactions extends Transactions {
    private String invName;
    private Double invQuantity;
    private Double invPrice;
    private Double invCommission;

    private String invSymb;

    private TransactionTypes invTransactionType;

    public InvTransactions(){
        invQuantity = 0.0;
        invPrice =0.0;
        invCommission = 0.0;
    }

    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public Double getInvQuantity() {
        return invQuantity;
    }

    public void setInvQuantity(Double invQuantity) {
        this.invQuantity = invQuantity;
    }

    public Double getInvPrice() {
        return invPrice;
    }

    public void setInvPrice(double invPrice) {
        this.invPrice = invPrice;
    }

    public Double getInvCommission() {
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
