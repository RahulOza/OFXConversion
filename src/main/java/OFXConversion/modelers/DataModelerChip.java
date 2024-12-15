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

    public class DataModelerChip {

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

        void extract(String originalStr, TransactionList transactionList) {


            Scanner myscanner = new Scanner(originalStr);
            boolean firstTrans = true;

            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

            while (myscanner.hasNextLine()) {
                String line = myscanner.nextLine();

                Matcher m = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{4})").matcher(line);
                if (m.find()) {
                    //Once we get a date, we can start processing the transaction details
                    String transDetails = line.substring(12);

                    //If there is another date then this isn't a transaction
                    Matcher m1 = Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})").matcher(transDetails);
                    if (m1.find()) {
                            continue;
                    }
                    /*
                    Date        Description     Amount  Balance
                    01 Jan 2021 Withdraw        -£500   £3,004.73
                     */
                    //First 12 characters are the date, so extract these

                    Transactions trans = new Transactions();
                    trans.setTransactionDate(LocalDate.parse(line.substring(0, 11), myformatter));
                    trans.setTransactionDetails(transDetails);

                    //Balance is the final number DDDD.DD in the remaining string
                    String transDetailsNoDateWithComma = line.substring(11);
                    String transDetailsNoDate = transDetailsNoDateWithComma.replace(",","");
                    int commaCount = 0;
                    if(transDetailsNoDate.length() != transDetailsNoDateWithComma.length())
                        commaCount = commaCount + 1;

                    Pattern regex0 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                    Matcher matcher0 = regex0.matcher(transDetailsNoDate);
                    int transDetailsNoDateLength = 0;
                    while(matcher0.find()){
                        transDetailsNoDateLength = matcher0.group(1).length() +  commaCount;
                        if(firstTrans) {
                            transactionList.setFinalBalance(Double.parseDouble(matcher0.group(1)));
                        }
                        firstTrans = false;
                        if (matcher0.group(1).contains("-")) {
                            String noSignTransAmount = matcher0.group(1).replace("-", "");
                            trans.setTransactionAmount(-(parseDouble(noSignTransAmount)));
                        }
                        if (matcher0.group(1).contains("+")) {
                            String noSignTransAmount = matcher0.group(1).replace("+", "");
                            trans.setTransactionAmount((parseDouble(noSignTransAmount)));
                        }
                    }
                    String transDetailsNoDateNoBalWithComma = line.substring(11,line.length()-transDetailsNoDateLength-1);

                    trans.setTransactionDetails(transDetailsNoDateNoBalWithComma);
                    transactionList.getTransactionsList().add(trans);
                }
                transactionList.setInitialBalance(initialBalance);
            }
        }
    }

