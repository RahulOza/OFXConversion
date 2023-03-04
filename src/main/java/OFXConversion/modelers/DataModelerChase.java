package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.RoundingMode;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import static java.lang.Double.parseDouble;

public class DataModelerChase {

    private Double finalBalance = 0.0;
    private Double initialBalance = 0.0;

    static String getText(File pdfFile) throws IOException {
        PDDocument doc = PDDocument.load(pdfFile);
        String pdfAsText = new PDFTextStripper().getText(doc);
        doc.close();
        return pdfAsText;
    }

    public TransactionList createTransactionList(String sourceFileName) throws IOException {
        String text;

        TransactionList translist = new TransactionList();
        try {
            text = getText(new File(sourceFileName));
            extract(text, translist);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return translist;
    }


    void extract(String originalStr, TransactionList transactionList)
    {

        List<String> transactionTokenList = new ArrayList<String>();
        Scanner myscanner = new Scanner(originalStr);

        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

        // First let us use dd mmm yyyy to split the statements into chunks of blocks from which we can mine
        // transaction details


        while (myscanner.hasNextLine()){
            String line = myscanner.nextLine();
            Matcher m = Pattern.compile("^\\d{2} [A-z]{3} \\d{4}").matcher(line);
            if(m.find()){
                // we have found a transaction
                //Overall looks like this ..BUT

                //Date        Transaction details            Amount   Balance
                //20 Nov 2022 From OZA RR - R RBS TO R CSE  +£150.00  £150.00

                // Actually is spread like this

                //22 Nov 2022 Landlord Certificates   [date] and [tran details]
                //Purchase [keyword]
                //-£75.00 £23.00  [amount] and [balance]

                // Date - First 11 chars - dd mmm yyyy (inc spaces)
                // Transaction details - between date and amount
                // Amount - reg expression


                String transDetails = line.substring(12);
                //Ever only going to happen once
                if (transDetails.startsWith("You opened your account")) {
                    continue;
                }

                if (transDetails.startsWith("Closing balance")) {
                    //final line, set/check final balance
                    String noCommaTransDetails = transDetails.replace(",","");
                    Matcher tm = Pattern.compile("\\d+(\\.\\d{2})").matcher(noCommaTransDetails);
                    if( tm.find() ) {
                        //round off and compare
                        DecimalFormat df = new DecimalFormat("#.##");

                        df.setRoundingMode(RoundingMode.CEILING);
                        Double stmtFinalBalance = Double.parseDouble((tm.group(0)));
                        Double stmtFinalBalanceRounded = Double.parseDouble(df.format(stmtFinalBalance));

                        if (!stmtFinalBalance.equals(stmtFinalBalanceRounded)){
                            throw new RuntimeException("Error in processing due to balance mismatch");
                        }
                    }
                    break;
                }
                if (transDetails.startsWith("Opening balance")){
                    String noCommaTransDetails = transDetails.replace(",","");
                    //first line, set initial balance
                    Matcher tm = Pattern.compile("\\d+(\\.\\d{2})").matcher(noCommaTransDetails);
                    if( tm.find() ) {
                        initialBalance = Double.parseDouble(tm.group(0));
                        finalBalance =  initialBalance;
                    }
                    continue;
                }
                Transactions trans = new Transactions();
                trans.setTransactionDate(LocalDate.parse(line.substring(0,11), myformatter));
                trans.setTransactionDetails(transDetails);

                // next line is - Purchase | Transfer | Payment - prob does not matter for us so ignore
                String tranOperation = myscanner.nextLine();
                // This is amount and balance
                line = myscanner.nextLine();

                String[] tokens = line.split(" ");
                String transAmount = tokens[0].replace("£","");
                String noSignTransAmount;
                String noCommaTransAmount;

                noCommaTransAmount = transAmount.replace(",","");
                              
                if(tranOperation.equals("Purchase") || tranOperation.equals("Transfer")){
                    noSignTransAmount = noCommaTransAmount.replace("-","");
                    trans.setTransactionAmount(-(parseDouble(noSignTransAmount)));
                } else {
                    noSignTransAmount = noCommaTransAmount.replace("+","");
                    trans.setTransactionAmount((parseDouble(noSignTransAmount)));
                }
                finalBalance = finalBalance +  trans.getTransactionAmount();
                transactionList.getTransactionsList().add(trans);
            }//while
            transactionList.setInitialBalance(initialBalance);
            transactionList.setFinalBalance(finalBalance);
        }

    }// end extract()

}






