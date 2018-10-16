package pdftoofx;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DataModeler {


    List<String> transactionTokenList = new ArrayList<String>();
    List<String> transactionList = new ArrayList<String>();
    Double finalBalance = 0.0;


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


            Matcher m = Pattern.compile("\\d+(\\.\\d{2})").matcher(transactionList.get(itr));
            while( m.find() ) {
                // TODO: Log this ?? System.out.println(m.group(0));
                // here we found the amount.
                // bug - what if the amount has a comma ? 1,372.72 ...it bombs out, so need
                // to parse commas out !
                m.group(0).replaceAll(",","");
                trans.transactionAmount = Double.parseDouble(m.group(0));
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



    void extract(String originalStr, int numberOfPages1)
    {   String allTrasactions ="";
        String singleTransaction = "";
        int pageCounter = 1;
        String allTransactionsMulPages = "";

        String workingText = originalStr;

           String delims = "Total Brought Forward From Previous Statement";
           String[] tokens = workingText.split(delims);

           //if (tokens.length > 1) {
            while (pageCounter > tokens.length -1 ) {


                String delims2 = "Continued";
               // String[] tokens2 = tokens[1].split(delims2);

                String[] tokens2 = tokens[pageCounter].split(delims2);

                if (tokens2.length > 1) { // if < 1 => then this is the last page

                    // tokens2[0] = useful stuff - trsactions from page 1
                    // token2[1] = needs work


                    /*
                    code below is old

                    String delims3 = "Total Brought Forward From Previous Page";
                    String[] tokens3 = tokens2[1].split(delims3);
                    // token3[0] = useless stuff
                    // token3[1] = useful stuff but it has the amount which needs to be stripped off - transactions from final page
                    // bug - break if more than 2 pages..

                    */
                    if (tokens3.length > 1) { //...working with 2 pages

                        Matcher tm1 = Pattern.compile("\\d+(\\.\\d{2})").matcher(tokens3[1]);

                        String[] token4 = {"", ""};
                        if (tm1.find()) {
                            token4 = tokens3[1].split(tm1.group(0));
                        }
                        //token4[0] = junk
                        //token4[1] = useful
                        if (token4.length > 1)
                            allTransactionsMulPages = tokens2[0] + token4[1];

                    }

                }

             }

           if (allTransactionsMulPages.length() <= 0) {
                //single page statement
               allTransactionsMulPages = tokens[1];

           }
           currentPage--;
      // }//while number of pages
        //stuff below is for last page only
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
                transactionList.add(listOfTransactions[itr++]);
            }
            allTrasactions = listOfTransactions[listOfTransactions.length-1];
        }

        transactionList.add(allTrasactions);

        //System.out.println(" /n/n *********************************************** /n/n ");
        // ^\d\.(\d+)? for 34.5


    }


}
