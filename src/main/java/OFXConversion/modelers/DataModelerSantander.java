package OFXConversion.modelers;

import OFXConversion.data.OfxgenGetPropertyValues;

import java.io.*;

public class DataModelerSantander {

    public void convert(String sourceFileName) throws IOException {

        String destFileName;
        String[] destFileNameParts;
        destFileNameParts = sourceFileName.split("\\.(?=[^.]+$)");

        if(destFileNameParts.length == 2){
            destFileName = destFileNameParts[0] + OfxgenGetPropertyValues.convertedSantanderFileName + "." + destFileNameParts[1];
        }
        else{
            //Something funky here so let qif be fileSuffix by default
            destFileName = OfxgenGetPropertyValues.convertedSantanderFileName + ".qif";
        }


        BufferedReader inputStream = new BufferedReader(new FileReader(sourceFileName));
        File UIFile = new File(destFileName);
        // if File doesnt exists, then create it
        if (!UIFile.exists()) {
            UIFile.createNewFile()
        }
        FileWriter filewriter = new FileWriter(UIFile.getAbsoluteFile());
        BufferedWriter outputStream= new BufferedWriter(filewriter);
        String lineOfStatement;
        while((lineOfStatement = inputStream.readLine()) != null){

            if(lineOfStatement.startsWith("P")) {
                // Instead of 'DIRECT DEBIT PAYMENT TO ' shorten it to 'DD '
                String changedLineOfStatement = lineOfStatement.replace("DIRECT DEBIT PAYMENT TO ","DD ");
                String newLineOfStatement = changedLineOfStatement.substring(0,OfxgenGetPropertyValues.maxQifCommentsChars);
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
