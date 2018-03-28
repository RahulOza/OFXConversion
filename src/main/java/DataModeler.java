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

        traslistFinal.initialBalance = Double.parseDouble(transactionList.get(0));
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

    void extract(String originalStr)
    {   String allTrasactions ="";
        String singleTransaction = "";

       /*/ StringTokenizer st = new StringTokenizer(originalStr, "Total Brought Forward From Previous Statement");
        while(st.hasMoreTokens())
        {   st.nextToken();
            String throwawayStr = st.nextToken();
            if (st.hasMoreTokens())
                newStr += newStr;
        }*/

        String delims = "Total Brought Forward From Previous Statement";
        String[] tokens = originalStr.split(delims);

        String delims1 = "Total";
        String[] tokens1 = tokens[1].split(delims1);

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

        transactionList.add(allTrasactions);



        //System.out.println(" /n/n *********************************************** /n/n ");

        // ^\d\.(\d+)? for 34.5


    }
}
