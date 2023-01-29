package OFXConversion.data;

public class InvTransactions extends Transactions {
    private String invName;
    private int quantity;
    private double price;
    private double commission;

    private TransactionTypes transactionType;


    public String getInvName() {
        return invName;
    }

    public void setInvName(String invName) {
        this.invName = invName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public TransactionTypes getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionTypes transactionType) {
        this.transactionType = transactionType;
    }

}
