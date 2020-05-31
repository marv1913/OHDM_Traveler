package util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rest.RequestDAO;
import rest.SearchRequestDAO;

import java.util.*;

public class Util {

    static String[] allowedPeopleTypes = {"farmer", "noble"};
    static String[] allowedTransportTypes = {"walking", "horse", "carriage", "car", "boat", "bicycle"};

    /**
     * method to convert a JSON to a SearchRequestDAO
     *
     * @param json JSON with search parameter
     * @return SearchRequestDAO filled with information of JSON
     */
    public SearchRequestDAO getSearchParameterFromJSON(String json) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            jsonObject = (JSONObject) parser.parse(json);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("check format of string");
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

    /**
     * method to validate whether SearchRequestDAO is filled with appropriate data
     *
     * @param dao SearchRequestDAO which should be checked
     * @throws IllegalArgumentException when some data has an inappropriate form
     */
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
        if (!this.isStringInArray(dao.getPeopleType(), allowedPeopleTypes)) {
            throw new IllegalArgumentException("classofperson '" + dao.getPeopleType() + "' is not defined");
        }

        if (!this.isStringInArray(dao.getTransportType(), allowedTransportTypes)) {
            throw new IllegalArgumentException("transporttype '" + dao.getTransportType() + "' is not defined");
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

    /**
     * method to generate a JSON string from a hashMap
     * @param hashMap hashmap with key value pairs to generate a JSON string
     * @return generated JSON
     */
    public String generateJSON(HashMap<String, String> hashMap) {
        JSONObject obj = new JSONObject();
        for (HashMap.Entry<String, String> entry : hashMap.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }
        return obj.toJSONString();
    }

    public String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * method to check whether given coordinates has an appropriate format
     *
     * @param coordinate coordinate which should be checked
     */
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

    /**
     * method to check whether a string is a string array
     *
     * @param string      string which should be in array
     * @param stringArray string array where to seach for the string
     * @return true if string is an array, else false
     */
    private boolean isStringInArray(String string, String[] stringArray) {
        for (int i = 0; i < stringArray.length; i++) {
            if (string.equals(stringArray[i])) {
                return true;
            }
        }
        return false;
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


    public static HashMap<String, String> parametersToMap(String args[], String[] requiredArguments, String helpMessage) {

        HashMap<String, String> argumentMap = new HashMap<>();

        int i = 0;
        while (i < args.length) {
            // key is followed by value. Key starts with -
            if (!args[i].startsWith("-")) {
                /* found parameter that does not start with '-'
                maybe shell parameters. Leave it alone. We are done here
                */
                break;
            }

            // value can be empty
            if (args.length > i + 1 && !args[i + 1].startsWith("-")) {
                // it is a value
                argumentMap.put(args[i], args[i + 1]);
                i += 2;
            } else {
                // no value - next parameter
                argumentMap.put(args[i], null);
                i += 1;
            }
        }
        for (int k = 0; k < requiredArguments.length; k++) {
            if (!argumentMap.containsKey(requiredArguments[k])) {
                System.err.println("required argument '" + requiredArguments[k] + "' was not passed");
                System.out.println(helpMessage);
            }
        }
        return argumentMap;
    }
}