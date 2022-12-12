package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    Matcher tm = Pattern.compile("\\d+(\\.\\d{2})").matcher(transDetails);
                    if( tm.find() ) {
                        Double stmtFinalBalance = Double.parseDouble(tm.group(0));
                        if (!stmtFinalBalance.equals(finalBalance)){
                            throw new RuntimeException("Error in processing due to balance mismatch");
                        }
                    }
                    break;
                }
                if (transDetails.startsWith("Opening balance")){
                    //first line, set initial balance
                    Matcher tm = Pattern.compile("\\d+(\\.\\d{2})").matcher(transDetails);
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
                String newTransAmount;
                              
                if(tranOperation.equals("Purchase") || tranOperation.equals("Transfer")){
                    newTransAmount = transAmount.replace("-","");
                    trans.setTransactionAmount(-(parseDouble(newTransAmount)));
                } else {
                    newTransAmount = transAmount.replace("+","");
                    trans.setTransactionAmount((parseDouble(newTransAmount)));
                }
                finalBalance = finalBalance +  trans.getTransactionAmount();
                transactionList.getTransactionsList().add(trans);
            }//while
            transactionList.setInitialBalance(initialBalance);
            transactionList.setFinalBalance(finalBalance);
        }


       /* if(tokens.length > 1) {
            // update for multiple page statements


            if(tokens2.length>1) {
                // tokens2[0] = useful stuff
                // token2[1] = needs work
                String delims3 = "Total Brought Forward From Previous Page";
                String[] tokens3 = tokens2[1].split(delims3);
                // token3[0] = useless stuff
                // token3[1] = useful stuff but it has the amount which needs to be stripped off
                if(tokens3.length >1) {
                    Matcher tm1 = Pattern.compile("\\d+(\\.\\d{2})").matcher(tokens3[1]);

                    String[] token4 = {"", ""};
                    if (tm1.find()) {
                        token4 = tokens3[1].split(tm1.group(0));
                    }
                    //token4[0] = junk
                    //token4[1] = useful
                    if(token4.length>1)
                        allTransactionsMulPages = tokens2[0] + token4[1];
                }
            }
        }


        String delims1 = "Total";
        String[] tokens1 = allTransactionsMulPages.split(delims1);

        Matcher tm = Pattern.compile("\\d+(\\.\\d{2})").matcher(tokens1[1]);

        if( tm.find() ) {
            // System.out.println(tm.group(0));
            // here we found the date split the text based on date
            //if(!transactionTokenList.contains(m.group(0))) {
            finalBalance = Double.parseDouble(tm.group(0));

            //}
        }


        allTrasactions = tokens1[0];

        Matcher m = Pattern.compile("\\d\\d-\\d\\d-\\d\\d").matcher(allTrasactions);

        while( m.find() ) {
            // TODO - log this ?? System.out.println(m.group(0));
            // here we found the date split the text based on date
            //if(!transactionTokenList.contains(m.group(0))) {
            transactionTokenList.add(m.group(0));
            //}
        }

        for (Iterator iterator = transactionTokenList.iterator(); iterator.hasNext();) {

            String [] listOfTransactions = allTrasactions.split(iterator.next().toString());

            int itr = 0;
            while(itr<listOfTransactions.length-1){
                // we know first element is junk
                //if(itr == 0){
                //  itr++;

                //}
                //else{
                transactionList.add(listOfTransactions[itr++]);
                //}
            }
            allTrasactions = listOfTransactions[listOfTransactions.length-1];
        }

        transactionList.add(allTrasactions);*/


    }// end extract()

/*

        try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);


            String lineOfStatement;
            boolean isHeader = true;
            boolean firstRec = true;

            PDDocument document = PDDocument.load(sourceFileName);

            //Instantiate PDFTextStripper class
            PDFTextStripper pdfStripper = new PDFTextStripper();

            //Retrieving text from PDF document
            String text = pdfStripper.getText(document);
            //Closing the document
            document.close();



            while ((lineOfStatement = inputStream.readLine()) != null) {

                // first line is the header so ignore it
                if (!isHeader) {
                    //replace consecutive tabs by single
                    String newLineOfStatement = lineOfStatement.replaceAll("\t(?=\t)","");

                    String[] tokens = newLineOfStatement.split("\\t");
                    // we know the tokens are
                    //Date |	Details	| What's gone in |	What's gone out | 	Balance
                    // 0          1           2                                  3
                    // Date	Description	Amount(GBP)
                    //    17 May 2021\tBought 8 S&P 500 UCITS ETF Distributing (VUSA)\t\t−£448.79\t£1,555.59

                    if (tokens.length > 1) {
                        Transactions trans = new Transactions();

                        trans.setTransactionDate(LocalDate.parse(tokens[0], myformatter));
                        trans.setTransactionDetails(tokens[1]);
                        String transactionDetails = tokens[1];
                        //2 is empty
                        String transAmountWithComma = tokens[2].replace("£","");
                        String transAmount = transAmountWithComma.replace(",","");

                        // It was found non ascii characters creep in so check if that is the case
                        if(isNotPureAscii(transAmount)){
                         String transAmountNew = transAmount.replaceAll("[^\\x00-\\x7F]", "");
                            //the non ascii character is the negative sign hence revert sign.
                            trans.setTransactionAmount(Double.parseDouble(transAmountNew));
                        }
                        else{
                            trans.setTransactionAmount(Double.parseDouble(transAmount));
                        }
                        //if something is 'Bought' then it is a debit
                        if(transactionDetails.startsWith("Bought")) {
                            trans.setTransactionAmount(-trans.getTransactionAmount());
                        }

                        transAmountWithComma = tokens[3].replace("£","");
                        transAmount = transAmountWithComma.replace(",","");

                        String transAmountNew;
                        if(isNotPureAscii(transAmount)) {
                            transAmountNew = transAmount.replaceAll("[^\\x00-\\x7F]", "");
                        }
                        else{
                            transAmountNew = transAmount;
                        }

                        if (firstRec) {
                            //Initial balance is in the very first line
                            // Initial balance is AFTER the first transaction so add the value of transaction to get the actual initial value.
                            finalBalance = Double.parseDouble(transAmountNew);
                            firstRec = false;
                        }
                        intialBalance = Double.parseDouble(transAmountNew) - trans.getTransactionAmount();
                        translistFinal.getTransactionsList().add(trans);

                    }
                }

                if (isHeader)
                    isHeader = false;
            }
            translistFinal.setFinalBalance(finalBalance);
            translistFinal.setInitialBalance(intialBalance);

        }
        return translistFinal;
    }*/
}






