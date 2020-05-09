package util;

import java.io.IOException;
import java.util.HashMap;

public class Parameter {

    private HashMap data;

    public Parameter(String filename) throws IOException {
        CSVReader csvReader = new CSVReader();
        data = csvReader.readFromCSV(filename);
    }

    public String getServername() {
        return (String) data.get("host");
    }

    public String getPortnumber() {
        return (String) data.get("port");
    }

    public String getUsername() {
        return (String) data.get("username");
    }

    public String getPwd() {
        return (String) data.get("password");
    }

    public String getDbname() {
        return (String) data.get("dbname");
    }

    public String getSchema() {
        return (String) data.get("schema");
    }
}