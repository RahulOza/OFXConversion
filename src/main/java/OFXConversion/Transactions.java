package OFXConversion;

import java.time.LocalDate;


public class Transactions {

    LocalDate transactionDate = null;
    String transactionDetails;
    Double transactionAmount;

    Transactions() {
        transactionDetails = "";
        transactionAmount = 0.0;
    }

}
