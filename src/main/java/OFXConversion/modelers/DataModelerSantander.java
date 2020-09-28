package OFXConversion.modelers;

import OFXConversion.data.OfxgenGetPropertyValues;

import java.io.*;

public class DataModelerSantander {
    //TODO - Create unit tests for this class
    public void convert(String sourceFileName) throws IOException {

        String destFileName = OfxgenGetPropertyValues.convertedSantanderFileName;

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

            if(lineOfStatement.startsWith("P")) {

                String newLineOfStatement = lineOfStatement.substring(0,OfxgenGetPropertyValues.maxQifCommentsChars);
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
