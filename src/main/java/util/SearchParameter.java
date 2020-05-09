package util;

import traveler.TransportTypes;

import java.io.IOException;
import java.util.HashMap;

public class SearchParameter {
    private HashMap data;
    private String day;
    private String startPointLongitude;
    private String startPointLatitude;
    private String endPointLongitude;
    private String endPointLatitude;
    private String classOfPerson;
    private String waterwayIncl;
    private String transportType;

    public SearchParameter(String filename) throws IOException {
        CSVReader csvReader = new CSVReader();
        data = csvReader.readFromCSV(filename);
        this.startPointLongitude = (String) data.get("startpoint_longitude");
        this.startPointLatitude = (String) data.get("startpoint_latitude");
        this.endPointLongitude = (String) data.get("endpoint_longitude");
        this.endPointLatitude = (String) data.get("endpoint_latitude");
        this.day = (String) data.get("day");
        this.waterwayIncl = (String) data.get("waterwayincl");
        this.classOfPerson = (String) data.get("classofperson");
        this.transportType = (String) data.get("transporttype");
    }

    public SearchParameter(String day, String startPointLongitude, String startPointLatitude, String endPointLongitude,
                           String endPointLatitude, String classOfPerson, String waterwayIncl, String transportType) {
        this.day = day;
        this.classOfPerson = classOfPerson;
        this.waterwayIncl = waterwayIncl;
        this.endPointLatitude = endPointLatitude;
        this.endPointLongitude = endPointLongitude;
        this.startPointLatitude = startPointLatitude;
        this.startPointLongitude = startPointLongitude;
        this.transportType = transportType;
    }

    public String getClassOfPerson() {
        return this.classOfPerson;
    }

    public TransportTypes getTransportType() {
        try {
            return TransportTypes.values()[Integer.parseInt(this.transportType) - 1];
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Parameter transporttype is wrong, " + this.transportType + " is not a defined transport type!");
        }
        return null;
    }

    public String getWaterwayIncl() {
        return this.waterwayIncl;
    }

    public String getStartPoint() {
        return this.convertCoordinates(this.startPointLatitude, this.startPointLongitude);
    }

    public String getEndPoint() {
        return this.convertCoordinates(this.endPointLatitude, this.endPointLongitude);
    }

    public String getDay() {
        return this.day;
    }

    /**
     * method to convert Coordinates to pgRotuning friendly style
     *
     * @param latitude
     * @param longitude
     * @return converted coordinates
     */
    private String convertCoordinates(String latitude, String longitude) {
        return longitude + " " + latitude;
    }
}