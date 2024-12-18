package OFXConversion.modelers;


import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
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
            boolean firstAmount = true;

            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

            while (myscanner.hasNextLine()) {
                String line = myscanner.nextLine();

                //To aid debugging
                if(line.startsWith("Date Description")){
                    continue;
                }
                Matcher m = Pattern.compile("^(\\d{2})/(\\d{2})/(\\d{4})").matcher(line);
                if (m.find()) {
                    //Once we get a date, we can start processing the transaction details
                    //too short for a transaction
                    if(line.length() < 15){
                        continue;
                    }
                    String transDetails = line.substring(11);

                    //If there is another date then this isn't a transaction
                    Matcher m1 = Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})").matcher(transDetails);
                    if (m1.find()) {
                            continue;
                    }

                    /*
                    Date        Description     Amount  Balance
                    23/11/2024  Withdraw        -£500   £3,004.73
                     */
                    //First 11 characters are the date, so extract these
                    Transactions trans = new Transactions();
                    trans.setTransactionDate(LocalDate.parse(line.substring(0, 10), myformatter));

                    //Balance is the final number DDDD.DD in the remaining string
                    String transDetailsNoDateWithCommaPadded = line.substring(11);
                    String transDetailsNoDateWithComma = transDetailsNoDateWithCommaPadded.trim();
                    String transDetailsNoDate = transDetailsNoDateWithComma.replace(",","");
                    int commaCount = transDetailsNoDateWithComma.length() - transDetailsNoDate.length() + 1; //+1 for space between date and details

                    String transDetailsNoDateNoCurr = transDetailsNoDate.replace("£","");
                    commaCount = commaCount + transDetailsNoDate.length() - transDetailsNoDateNoCurr.length();

                    firstAmount = true;
                    //Pattern regex0 = Pattern.compile("(\\d+(?:\\.\\d+)?)");
                    Matcher m2 = Pattern.compile("(\\d+(?:\\.\\d+)?)").matcher(transDetailsNoDateNoCurr);
                    int transAmtLength = commaCount;
                    while(m2.find()){
                        transAmtLength = transAmtLength + m2.group(1).length();
                        //System.out.println("Found amount: " + m2.group(1) + " with Length: " + m2.group(1).length());
                        if(firstTrans) {
                            transactionList.setFinalBalance(Double.parseDouble(m2.group(1)));
                            //additional space as there are 2 amounts in this line
                           // commaCount = commaCount + 1;
                        }
                        if(firstAmount) {
                            trans.setTransactionAmount(Double.parseDouble(m2.group(1)));
                            firstAmount = false;
                        }
                    }

                    //Debugging aid
                    //System.out.println(" for String:[" + transDetailsNoDateWithComma + "]");
                    firstTrans = false;

                    String onlyTransactionDetails = transDetailsNoDateWithComma.substring(0,transDetailsNoDateWithComma.length()-transAmtLength);

                    if(onlyTransactionDetails.startsWith("Withdraw") || onlyTransactionDetails.startsWith("Transfer")){
                        //Amount is negative
                        trans.setTransactionAmount(-trans.getTransactionAmount());
                        // also account for - sign character
                        transAmtLength = transAmtLength + 1;
                    }
                    //System.out.println(" Total Length: " + transDetailsNoDateWithComma.length() + " commaCount:" + commaCount + " transAmtLength:" + transAmtLength);
                    trans.setTransactionDetails(transDetailsNoDateWithComma.substring(0,transDetailsNoDateWithComma.length()-transAmtLength));
                    transactionList.getTransactionsList().add(trans);
                }
                transactionList.setInitialBalance(initialBalance);
            }
        }
    }

