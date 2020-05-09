package util;

import rest.RequestDAO;
import rest.SearchRequestDAO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Utility {

    static String[] allowedPeopleTypes = {"bauer", "adeliger"};

    public SearchRequestDAO getSearchParameterFromJSON(String json) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            jsonObject = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String transporttype = (String) jsonObject.get("transporttype");
        String peopleType = (String) jsonObject.get("classofperson");
        String waterwayincl = (String) jsonObject.get("waterwayincl");
        String day = (String) jsonObject.get("day");

        JSONObject jsonObjectStartpoint = (JSONObject) jsonObject.get("startpoint");
        JSONObject jsonObjectEndpoint = (JSONObject) jsonObject.get("endpoint");

        String startpointLatitude = getLatitude(jsonObjectStartpoint);
        String startpointLongitude = getLongitude(jsonObjectStartpoint);

        String endpointLatitude = getLatitude(jsonObjectEndpoint);
        String endpointLongitude = getLongitude(jsonObjectEndpoint);

        JSONObject restrictedAreaSubDict = (JSONObject) jsonObject.get("restricted_area");
        List<String> closedFor = new ArrayList<>();
        List<String[]> restrictedAreaCoordinates = new ArrayList<>();
        try {
            JSONArray closedForArray = (JSONArray) restrictedAreaSubDict.get("closedFor");
            closedFor = getListFromJSONArray(closedForArray);
        } catch (NullPointerException e) {
        }

        JSONArray restrictedAreaPoints = null;
        try {
            restrictedAreaPoints = (JSONArray) restrictedAreaSubDict.get("points");
        } catch (NullPointerException e) {

        }
        if (restrictedAreaPoints != null) {
            for (int i = 0; i < restrictedAreaPoints.size(); i++) {
                JSONArray coordinates = (JSONArray) restrictedAreaPoints.get(i);
                String[] tmpCoordinates = new String[2];
                for (int k = 0; k < coordinates.size(); k++) {
                    tmpCoordinates[k] = (String) coordinates.get(k);
                }
                restrictedAreaCoordinates.add(tmpCoordinates);
            }
        }

        SearchRequestDAO tmp = new RequestDAO(startpointLongitude, startpointLatitude, endpointLongitude, endpointLatitude, day, peopleType, transporttype, waterwayincl, restrictedAreaCoordinates, closedFor, "dummyId");

        return tmp;
    }

    public void checkDAO(SearchRequestDAO dao) {
        this.checkCoordinate(dao.getStartPointLatitude());
        this.checkCoordinate(dao.getStartPointLongitude());
        this.checkCoordinate(dao.getEndPointLatitude());
        this.checkCoordinate(dao.getEndPointLongitude());
        String day = dao.getDay();
        int count = day.length() - day.replace("-", "").length();
        if (count != 2) {
            throw new IllegalArgumentException("day has bad format [yyyy-mm-dd]");
        }
        StringTokenizer stringTokenizer = new StringTokenizer(day, "-");
        while (stringTokenizer.hasMoreElements()) {
            this.checkIntegerValue(String.valueOf(Integer.parseInt(stringTokenizer.nextToken())));
        }
        List<String[]> restrictedArea = dao.getRestrictedArea();
        for (int i = 0; i < restrictedArea.size(); i++) {
            if (restrictedArea.get(i).length != 2) {
                throw new IllegalArgumentException();
            }
        }
        String peopleTypeAsString = dao.getPeopleType();
        int peopleType = Integer.parseInt(peopleTypeAsString);
        if (peopleType < 1 || peopleType > 10) {
            throw new IllegalArgumentException();
        }

        String transportTypeAsString = dao.getTransportType();
        this.checkIntegerValue(transportTypeAsString);
        int transportType = Integer.parseInt(transportTypeAsString);
        if (transportType < 1 || transportType > 10) {
            throw new IllegalArgumentException();
        }
        String waterwayInclStr = dao.getWaterwayIncluded();
        if (waterwayInclStr.equals("true") || waterwayInclStr.equals("True")) {
        } else if (!waterwayInclStr.equals("false") || !waterwayInclStr.equals("False")) {
            throw new IllegalArgumentException();
        }
        List<String> closedFor = dao.getClosedFor();
        for (int i = 0; i < closedFor.size(); i++) {
            this.checkIntegerValue(closedFor.get(i));
        }


    }

    private void checkCoordinate(String coordinate) {
        if (!coordinate.contains(".")) {
            throw new IllegalArgumentException();
        }
        int dotIndex = coordinate.indexOf(".");
        String subString1 = coordinate.substring(0, dotIndex);
        String subString2 = coordinate.substring(dotIndex + 1);
        this.checkIntegerValue(subString1);
        this.checkIntegerValue(subString2);
    }

    private void checkIntegerValue(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        }
    }

    private String getLongitude(JSONObject jsonObjectCoordinates) {
        return (String) jsonObjectCoordinates.get("longitude");
    }

    private String getLatitude(JSONObject jsonObjectCoordinates) {
        return (String) jsonObjectCoordinates.get("latitude");
    }

    private List<String> getListFromJSONArray(JSONArray jsonArray) {
        List<String> tmpList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            tmpList.add((String) jsonArray.get(i));
        }
        return tmpList;
    }


}
