package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CSVReader {

    /**
     * method to read data from a csv file
     * @param filePath path to csv file
     * @return Hashmap with headlines as key; null if there are more then two lines in csv
     * @throws IOException is thrown if file is not readable
     */
    public HashMap readFromCSV(String filePath) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(filePath));
        String row;
        int count = 0;
        String[] headlines=null;
        String[] data=null;
        HashMap<String, String> readData = new HashMap<>();
        while ((row = csvReader.readLine()) != null) {
            if(count>1){
                return null;
            }
            if (count == 0) {
                headlines = row.split(",");
            } else {
                data = row.split(",");
            }
            count++;
        }
        csvReader.close();
        for(int i=0; i<headlines.length; i++){
            readData.put(headlines[i], data[i]);
        }
        return readData;
    }
}