package OFXConversion.modelers;

import OFXConversion.data.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import static java.lang.System.exit;

public class DataModelerVanguard {
     HashMap<String, String> invSymbolMap = new HashMap<String, String>();

    public static boolean isNotPureAscii(String v) {
        return !(StandardCharsets.US_ASCII.newEncoder().canEncode(v));
        // or "ISO-8859-1" for ISO Latin 1
        // or StandardCharsets.US_ASCII with JDK1.7+
    }
    private void readSymbolMap() throws Exception {
        try (BufferedReader inputStream = new BufferedReader(new FileReader(OfxgenGetPropertyValues.vanguardSymbolMapFile))) {
            String lineOfStatement;

            while ((lineOfStatement = inputStream.readLine()) != null) {
                //read all symbols and add to a hashmap
                String[] tokens = lineOfStatement.split(",");
                if(tokens.length == 2){
                 invSymbolMap.put(tokens[0],tokens[1]);
                }
                else {
                    throw new Exception("Invalid HashMap file");
                }
            }//while
        }//open file
        catch(IOException e){
            System.out.println("Exception: " + e);

        }
    }
    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();
        boolean isFirstRec = true;
        boolean cashTransStatements = false;
        boolean invTransStatements = false;

        readSymbolMap();

        try {

            FileInputStream file = new FileInputStream(sourceFileName);

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(OfxgenGetPropertyValues.transSheetNumber);

            //Iterate through each rows from first sheet
            Iterator < Row > rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                //For each row, iterate through each columns
                Iterator < Cell > cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {

                   Cell cell = cellIterator.next();


                           if (cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue().equals("Date") && cashTransStatements) {

                            row = rowIterator.next();
                            //these rows are now cash transactions
                            while (rowIterator.hasNext()) {
                                Iterator<Cell> innerCellIterator = row.cellIterator();

                                Cell innerCell = innerCellIterator.next();
                                Transactions trans = new Transactions();
                                //Date	Details	Amount	Balance
                                //01/12/2021	Regular Deposit	200.00	319.97
                                //01/12/2021	Bought 1 S&P 500 UCITS ETF Distributing (VUSA)	-65.89	254.08
                                //01/12/2021	Bought 1 FTSE Developed World UCITS ETF Distributing (VEVE)	-68.73	185.35
                                if (innerCell.getCellType().equals(CellType.STRING) && innerCell.getStringCellValue().equals("Balance")) {
                                    //we have come to end of cash transactions
                                    break;
                                }
                                //date
                                trans.setTransactionDate(innerCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                innerCell = innerCellIterator.next();
                                //transaction details
                                trans.setTransactionDetails(innerCell.getStringCellValue());
                                innerCell = innerCellIterator.next();
                                //Amount
                                trans.setTransactionAmount((innerCell.getNumericCellValue()));
                                //Balance
                                innerCell = innerCellIterator.next();
                                if(isFirstRec){
                                    //final balance in very first line
                                    translistFinal.setFinalBalance(innerCell.getNumericCellValue());
                                    isFirstRec = false;
                                }
                                //keep overwriting initial balance until the very end
                                translistFinal.setInitialBalance(innerCell.getNumericCellValue());

                                translistFinal.getTransactionsList().add(trans);
                                row = rowIterator.next();
                                cashTransStatements = false;
                            }
                        } //if celltype is string for date

                    if(cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue().equals("Cash Transactions") && !cashTransStatements) {
                        cashTransStatements = true;
                    } //if celltype is string for cash transactions



                        if (cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue().equals("Date") && invTransStatements) {

                            row = rowIterator.next();
                            //these rows are now cash transactions
                            while (rowIterator.hasNext()) {
                                Iterator<Cell> innerCellIterator = row.cellIterator();

                                Cell innerCell = innerCellIterator.next();
                                InvTransactions itrans = new InvTransactions()  ;
                                //Date	InvestmentName	TransactionDetails	Quantity	Price	Cost
                                //05/12/2022	FTSE Developed World UCITS ETF Distributing (VEVE)	Bought 9 FTSE Developed World UCITS ETF Distributing (VEVE)	9.00	65.4156	588.74
                                //05/12/2022	S&P 500 UCITS ETF Distributing (VUSA)	Bought 9 S&P 500 UCITS ETF Distributing (VUSA)	9.00	63.7467	573.72
                                //03/11/2022	FTSE Developed World UCITS ETF Distributing (VEVE)	Bought 9 FTSE Developed World UCITS ETF Distributing (VEVE)	9.00	64.7222	582.50
                                if (innerCell.getCellType().equals(CellType.STRING) && innerCell.getStringCellValue().equals("Balance")) {
                                    //we have come to end of cash transactions
                                    break;
                                }

                                itrans.setTransactionDate(innerCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                                innerCell = innerCellIterator.next();

                                String invNameTmp ="";
                                String invSymTmp = "";
                                if(innerCell.getStringCellValue().indexOf('(') >0) {
                                     invNameTmp = innerCell.getStringCellValue().substring(0, innerCell.getStringCellValue().indexOf('('));
                                     invSymTmp = innerCell.getStringCellValue().substring(innerCell.getStringCellValue().indexOf('(') + 1, innerCell.getStringCellValue().indexOf(')'));
                                }
                                else{
                                    invNameTmp = innerCell.getStringCellValue().trim();
                                    invSymTmp = invSymbolMap.get(invNameTmp);
                                }
                                String invNameTmp1 = invNameTmp.replace("Distributing","");

                                itrans.setInvSymb(invSymTmp);
                                itrans.setInvName(invNameTmp1);
                                innerCell = innerCellIterator.next();
                                itrans.setTransactionDetails(innerCell.getStringCellValue());
                                if(itrans.getTransactionDetails().startsWith("Bought")){
                                    itrans.setInvTransactionType(TransactionTypes.MF_BUY);
                                }
                                else if(itrans.getTransactionDetails().startsWith("Sold")){
                                    itrans.setInvTransactionType(TransactionTypes.MF_SELL);
                                }
                                else {
                                    throw new Exception("Invalid Transacton Type");
                                }

                                //Quantity
                                innerCell = innerCellIterator.next();
                                itrans.setInvQuantity((int)innerCell.getNumericCellValue());
                                //price
                                innerCell = innerCellIterator.next();
                                itrans.setInvPrice(innerCell.getNumericCellValue());
                                //cost == amount
                                innerCell = innerCellIterator.next();
                                itrans.setTransactionAmount(innerCell.getNumericCellValue());

                                invTranslistFinal.getInvTransactionsList().add(itrans);
                                row = rowIterator.next();
                                invTransStatements = false;
                            }
                        } //if celltype is string for date
                    if(cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue().equals("Investment Transactions") && !invTransStatements) {
                        invTransStatements = true;
                    } //if celltype is string for cash transactions
                }
                System.out.println("");
            }
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return(new AllTransactions(invTranslistFinal,translistFinal));

    }

/*
    private void processCashTransactionList(String inputStream){

        TransactionList translistFinal = new TransactionList();
        //try (BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName))) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);


            String lineOfStatement;
            boolean isHeader = true;
            boolean firstRec = true;

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

        //}
        return translistFinal;
    } */
}






