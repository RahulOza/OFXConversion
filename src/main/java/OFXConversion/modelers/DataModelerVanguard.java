package OFXConversion.modelers;

import OFXConversion.data.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;


import java.io.*;
import java.time.ZoneId;
import java.util.Iterator;

public class DataModelerVanguard {

    public AllTransactions createTransactionList(String sourceFileName) throws Exception {
        TransactionList translistFinal = new TransactionList();
        InvTransactionList invTranslistFinal = new InvTransactionList();
        boolean isFirstRec = true;
        boolean cashTransStatements = false;
        boolean invTransStatements = false;

        invTranslistFinal.readSymbolMap();

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

                                InvTransactions itransDiv = new InvTransactions();

                                innerCell = innerCellIterator.next();
                                //Amount
                                trans.setTransactionAmount((innerCell.getNumericCellValue()));

                                if(trans.getTransactionDetails().startsWith("DIV:")){

                                    itransDiv.setTransactionDate(trans.getTransactionDate());
                                    itransDiv.setTransactionDetails(trans.getTransactionDetails());
                                    itransDiv.setInvTransactionType(TransactionTypes.DIVIDEND);
                                    // if dividend create inv transactions
                                    String tranDetails = trans.getTransactionDetails();
                                    //symbol is 4 digits at position 5
                                    // DIV: VETY.XLON.GB @ GBP 0.001857370
                                    String invSymbol = tranDetails.substring(5,9);

                                    if(invTranslistFinal.getReverseSymbolMap().containsKey(invSymbol)) {
                                        itransDiv.setInvSymb(invSymbol);
                                        itransDiv.setInvName(invTranslistFinal.getReverseSymbolMap().get(invSymbol)[0]);
                                    }
                                    else{
                                        throw new Exception("Symbol in statement does not exist in mapfile, please add it to mapfile:"+ invSymbol);
                                    }
                                    itransDiv.setTransactionAmount(trans.getTransactionAmount());
                                    invTranslistFinal.getInvTransactionsList().add(itransDiv);

                                }
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
                                InvTransactions itrans = new InvTransactions();
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

                                String invNameTmp;
                                String invSymTmp;
                                String[] invSymTmpMap;
                                if(innerCell.getStringCellValue().indexOf('(') >0) {
                                     invNameTmp = innerCell.getStringCellValue().substring(0, innerCell.getStringCellValue().indexOf('('));
                                     invSymTmp = innerCell.getStringCellValue().substring(innerCell.getStringCellValue().indexOf('(') + 1, innerCell.getStringCellValue().indexOf(')'));
                                     //check if symbol exists else error out
                                    if(invTranslistFinal.getReverseSymbolMap().containsKey(invSymTmp)) {
                                        itrans.setInvSymb(invSymTmp);
                                    }
                                    else{
                                        throw new Exception("Symbol in statement does not exist in mapfile, please add it to mapfile:"+ invNameTmp);
                                    }
                                }
                                else{
                                    invNameTmp = innerCell.getStringCellValue().trim();
                                    invSymTmpMap = invTranslistFinal.getInvSymbolMap().get(invNameTmp);
                                    itrans.setInvSymb(invSymTmpMap[0]);
                                }
                                String invNameTmp1 = invNameTmp.replace("Distributing","");

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
                                    throw new Exception("Invalid Transacton Type:"+ itrans.getTransactionDetails());
                                }

                                //Quantity
                                innerCell = innerCellIterator.next();
                                itrans.setInvQuantity(innerCell.getNumericCellValue());
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
            }
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return(new AllTransactions(invTranslistFinal,translistFinal));

    }
}






