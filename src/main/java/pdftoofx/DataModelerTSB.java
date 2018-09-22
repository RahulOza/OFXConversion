package pdftoofx;




import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

public class DataModelerTSB {


    void convert(String sourceFileName) throws IOException{
        /* read all text from TSB statement, then convert Dyyyy-MM-DD to Ddd/MM/yyyy
        D2018-07-10 => D09/05/2017
         */
        String destFileSuffix = "ConvertedbyRahulOza";
        String destFileNameParts[] = sourceFileName.split("\\.(?=[^\\.]+$)");
        String destFileName = "";
        DateTimeFormatter myInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        DateTimeFormatter myOutputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

        if(destFileNameParts.length == 2){
            destFileName = destFileNameParts[0]+destFileSuffix+"."+destFileNameParts[1];
        }
        else{
            //Something funky here so let the file by fileSuffix
            destFileName = destFileSuffix + ".qif";
        }


        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        File UIFile = new File(destFileName);
        // if File doesnt exists, then create it
        if (!UIFile.exists()) {
            UIFile.createNewFile();
        }
        FileWriter filewriter = new FileWriter(UIFile.getAbsoluteFile());
        BufferedWriter outputStream= new BufferedWriter(filewriter);
        String lineOfStatement;
        while((lineOfStatement = inputStream.readLine()) != null){

            if(lineOfStatement.startsWith("D")) {
                LocalDate extractedDate = LocalDate.parse(lineOfStatement.substring(1,lineOfStatement.length()),myInputFormatter);
                String convertedDate  = extractedDate.format(myOutputFormatter);
               // String convertedDate = new SimpleDateFormat("dd/MM/yyyy").format(extractedDate.);
                String newLineOfStatement = "D"+convertedDate;
                outputStream.write(newLineOfStatement);

            }
            else {
                outputStream.write(lineOfStatement);
            }
            outputStream.write("\r\n");
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }
}
