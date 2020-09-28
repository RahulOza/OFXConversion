package OFXConversion.modelers;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataModelerTSB {


    public void convert(String sourceFileName) throws IOException{
        /* read all text from TSB statement, then convert Dyyyy-MM-DD to Ddd/MM/yyyy
        D2018-07-10 => D09/05/2017
         */

        //TODO - Dest file suffix should be GV
        String destFileSuffix = "ConvertedTSBQIF";
        String destFileNameParts[] = sourceFileName.split("\\.(?=[^\\.]+$)");
        String destFileName = "";
        DateTimeFormatter myInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        DateTimeFormatter myOutputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);

        if(destFileNameParts.length == 2){
            destFileName = destFileNameParts[0]+destFileSuffix+"."+destFileNameParts[1];
        }
        else{
            //Something funky here so let the file be fileSuffix
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
