package OFXConversion.modelers;

import OFXConversion.data.TransactionList;
import OFXConversion.data.Transactions;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;



public class DataModelerVanguard {


    //private List<String> transactionTokenList = new ArrayList<>();
    //private List<String> transactionList = new ArrayList<>();
    private Double finalBalance = 0.0;
    private Double initialBalance = 0.0;

    public TransactionList createTransactionList(String sourceFileName) throws IOException {

        TransactionList translistFinal = new TransactionList();
        try (FileInputStream inputStream = new FileInputStream(sourceFileName)) {
            DateTimeFormatter myformatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

            boolean firstRec = true;

            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(1);

            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                    if(row.getCell(0).getStringCellValue().equals("Investment Transactions")){
                        //we only want cash transactions
                        break;
                    }
                    //For each row, iterate through each column
                    if(row.getCell(0).getStringCellValue().equals("Date")){
                        //first row of cash transactions has started so get next row
                        row = rowIterator.next();
                        Iterator <Cell> cellIterator = row.cellIterator();
                        /*
                            Date	Details	Amount	Balance
                            06/07/2021	Bought 1 S&P 500 UCITS ETF Distributing (VUSA)	-59.54	157.20
                         */
                        while(cellIterator.hasNext()) {
                            //cell has date
                            Cell cell = cellIterator.next();

                            /*if(cell.getStringCellValue().equals("Balance")){
                                break;
                            }*/
                            Transactions trans = new Transactions();
                            trans.setTransactionDate(LocalDate.parse(cell.getStringCellValue(),myformatter));
                            //cell now has trans details
                            cell = cellIterator.next();
                            trans.setTransactionDetails(cell.getStringCellValue());
                            //cell now has amount
                            cell = cellIterator.next();
                            trans.setTransactionAmount(Double.parseDouble(cell.getStringCellValue()));
                            //cell has balance
                            cell = cellIterator.next();

                            if (firstRec) {
                                //Initial balance is in the very first line
                                // Initial balance is AFTER the first transaction so add the value of transaction to get the actual initial value.
                                initialBalance = Double.parseDouble(cell.getStringCellValue()) - trans.getTransactionAmount();
                                firstRec = false;
                            }
                            finalBalance = Double.parseDouble(cell.getStringCellValue());
                            translistFinal.getTransactionsList().add(trans);
                            break;
                        }//Cell Iterator
                    }
             }
            translistFinal.setFinalBalance(finalBalance);
            translistFinal.setInitialBalance(initialBalance);
        }
        return translistFinal;
    }
}






