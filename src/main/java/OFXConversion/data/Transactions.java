package OFXConversion.data;

import java.time.LocalDate;


public class Transactions {

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(String transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    LocalDate transactionDate = null;
    String transactionDetails;
    Double transactionAmount;

    public void Transactions() {
        transactionDetails = "";
        transactionAmount = 0.0;
    }

}
