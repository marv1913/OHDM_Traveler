package util;

import java.io.IOException;
import java.util.HashMap;

public class SearchParameter {
    private HashMap data;


    public SearchParameter(String filename) throws IOException {
        CSVReader csvReader = new CSVReader();
        data = csvReader.readFromCSV(filename);
    }

    public String getClassOfPerson() {
        return (String) data.get("classofperson");
    }

    public String getTransportType() {
        return (String) data.get("transporttype");
    }

    public String getWaterwayIncl() {
        return (String) data.get("waterwayincl");
    }

    public String getStartPoint() {
        return this.convertCoordinates((String) data.get("startpoint_latitude"), (String) data.get("startpoint_longitude"));
    }

    public String getEndPoint() {
        return this.convertCoordinates((String) data.get("endpoint_latitude"), (String) data.get("endpoint_longitude"));
    }

    public String getDay() {
        return (String) data.get("day");
    }

    /**
     * method to convert Coordinates to pgRotuning friendly style
     * @param latitude
     * @param longitude
     * @return converted coordinates
     */
    private String convertCoordinates(String latitude, String longitude) {
        return longitude + " " + latitude;
    }
}
