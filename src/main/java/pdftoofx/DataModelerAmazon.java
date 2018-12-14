package pdftoofx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DataModelerAmazon {


    List<String> transactionTokenList = new ArrayList<String>();
    List<String> transactionList = new ArrayList<String>();
    Double finalBalance = 0.0;

    TransactionList createTransactionList(String sourceFileName,Double initialBalance) throws IOException {

        TransactionList traslistFinal = new TransactionList();
        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);


        traslistFinal.initialBalance = initialBalance;
        String lineOfStatement;
        Boolean isHeader = true;

        //initialise final balance.
        finalBalance = initialBalance;

        while((lineOfStatement = inputStream.readLine()) != null) {

            // first line is the header so ignore it
            if(!isHeader){

                String tokens[] = lineOfStatement.split(",");
                // we know the tokens are
                // Date	Description	Amount(GBP)
                if(tokens.length > 1) {
                    Transactions trans = new Transactions();

                    trans.transactionDate = LocalDate.parse(tokens[0], myformatter);
                    trans.transactionDetails = tokens[1];
                    trans.transactionAmount = Double.parseDouble(tokens[2]);

                    traslistFinal.transactionsListFinal.add(trans);
                    finalBalance = finalBalance + trans.transactionAmount;
                }
            }

            if(isHeader == true)
                isHeader = false;
        }
        traslistFinal.finalBalance = finalBalance;

        inputStream.close();
        return traslistFinal;
    }

    TransactionList createTransactionList(){


        // move date from transactionList and details from transactionList to TransactionList object
        //transactionTokenList
        // we know transactionList[0] has initial balance
        TransactionList traslistFinal = new TransactionList();

        DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd-MM-yy", Locale.ENGLISH);

        traslistFinal.initialBalance = Double.parseDouble(transactionList.get(0).replaceAll(",",""));
        traslistFinal.finalBalance = finalBalance;

        transactionList.remove(transactionList.get(0));

        int itr=0;
        while(itr < transactionList.size() ){
            Transactions trans = new Transactions();

            // myformatter = new DateTimeFormatter("dd-MM-yy", Locale.ENGLISH);
            trans.transactionDate = LocalDate.parse(transactionTokenList.get(itr), myformatter);


            //Matcher m = Pattern.compile("\\d+(\\.\\d{2})").matcher(transactionList.get(itr).replace(",",""));
            Matcher m = Pattern.compile("\\b\\d[\\d,.]*\\b").matcher(transactionList.get(itr));

            while( m.find() ) {
                // TODO: Log this ?? System.out.println(m.group(0));
                // here we found the amount.
                // bug - what if the amount has a comma ? 1,372.72 ...it bombs out, so need
                // to parse commas out !

                String amountWithoutComma = m.group(0).replace(",","");
                trans.transactionAmount = Double.parseDouble(amountWithoutComma);
                // split the amount from the transaction details
                String [] listOfFinalTransactions = transactionList.get(itr).split(m.group(0));

                trans.transactionDetails = listOfFinalTransactions[0];
                //TODO: Check if list of listofFinalTransaction[1] is cr which means amount is -ve or credit.
                if(listOfFinalTransactions[1].contains("cr")){
                    trans.transactionAmount = -(trans.transactionAmount);
                }
            }

            traslistFinal.transactionsListFinal.add(trans);

            itr++;
        }

        return traslistFinal;


    }



    void extract(String originalStr)
    {   String allTrasactions ="";
        String singleTransaction = "";
        int pageCounter = 0;
        String allTransactionsMulPages = "";

        String workingText = originalStr;

           String delims = "Total Brought Forward From Previous Statement";
           String[] tokens = workingText.split(delims);

           String delimspage = "Total Brought Forward From Previous Page";
           String[] tokensPages = tokens[1].split(delimspage);

            while (pageCounter < tokensPages.length) {


                String delims2 = "Continued";
               // String[] tokens2 = tokens[1].split(delims2);

                String[] tokens2 = tokensPages[pageCounter].split(delims2);

                if (tokens2.length > 1) { // if < 1 => then this is the last page

                    // tokens2[0] = useful stuff - trsactions from page 1
                    // token2[1] = needs work


                    /*
                    TODO - remove this section
                    code below is old

                    String delims3 = "Total Brought Forward From Previous Page";
                    String[] tokens3 = tokens2[1].split(delims3);
                    // token3[0] = useless stuff
                    // token3[1] = useful stuff but it has the amount which needs to be stripped off - transactions from final page
                    // bug - break if more than 2 pages..

                    */
                    if (pageCounter >= 1) { //...for more than 1 page, we need to throw away the amount in first line.

                        Matcher tm1 = Pattern.compile("\\d+(\\.\\d{2})").matcher(tokens2[0]);

                        String[] token4 = {"", ""};
                        if (tm1.find()) {
                            token4 = tokens2[0].split(tm1.group(0));
                        }
                        //token4[0] = junk
                        //token4[1] = useful
                        allTransactionsMulPages = allTransactionsMulPages + token4[1];

                    }
                    else
                        allTransactionsMulPages = allTransactionsMulPages + tokens2[0];


                }
                else{
                    //if this is the last page ..first throw away the amount from previous page
                    Matcher tm1 = Pattern.compile("\\d+(\\.\\d{2})").matcher(tokens2[0]);

                    String[] token4 = {"", ""};
                    if (tm1.find()) {
                        token4 = tokens2[0].split(tm1.group(0));
                    }
                    //token4[0] = junk
                    //token4[1] = useful
                    allTransactionsMulPages = allTransactionsMulPages + token4[1];
                }
                pageCounter++;
             }

           if (allTransactionsMulPages.length() <= 0 && tokens.length > 1) {
                //single page statement
               allTransactionsMulPages = tokens[1];

           }

      // }//while number of pages
        //stuff below is for last page only
        String delims1 = "Total";
        String[] tokens1 = allTransactionsMulPages.split(delims1);

        String finalBalanceString = tokens1[1].replace(",","");

        //final balance may contain commas, filter these out
        Matcher tm = Pattern.compile("\\d+(\\.\\d{2})").matcher(finalBalanceString);

        if( tm.find() ) {
            finalBalance = Double.parseDouble(tm.group(0));
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
                transactionList.add(listOfTransactions[itr++]);
            }
            allTrasactions = listOfTransactions[listOfTransactions.length-1];
        }

        transactionList.add(allTrasactions);

        //System.out.println(" /n/n *********************************************** /n/n ");
        // ^\d\.(\d+)? for 34.5


    }


}
