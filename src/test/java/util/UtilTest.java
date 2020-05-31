package util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rest.RequestDAO;
import rest.SearchRequestDAO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilTest {

    private String testJSON = " {\"classofperson\": \"farmer\", \"transporttype\": \"bicycle\", \"waterwayincl\": \"true\",\n" +
            "               \"startpoint\": {\"latitude\": \"52.457907\", \"longitude\": \"13.527333\"},\n" +
            "               \"endpoint\": {\"latitude\": \"52.444784\", \"longitude\": \"13.507886\"}, \"day\": \"2019-12-1\",\n" +
            "               \"restricted_area\": {}}";
    private Util util;
    private SearchRequestDAO searchRequestDAO;

    @BeforeEach
    public void init() {
        util = new Util();
        searchRequestDAO = new RequestDAO("13.527333","52.457907", "13.507886", "52.444784", "2019-12-1", "farmer", "bicycle", "true", new ArrayList<>(), new ArrayList<>(), null);
    }

    @Test
    public void getResearchParameterGood() {
        util.getSearchParameterFromJSON(testJSON);
        // no exception expected
    }

    @Test
    public void getResearchParameterGood2() {
        SearchRequestDAO searchRequestDAO = util.getSearchParameterFromJSON(testJSON);
        assertEquals(searchRequestDAO.getTransportType(), "bicycle");
    }

    @Test
    public void getResearchParameterBadMissing() {
        String testJSON = " {\"transporttype\": \"bicycle\", \"waterwayincl\": \"true\",\n" +
                "               \"startpoint\": {\"latitude\": \"52.457907\", \"longitude\": \"13.527333\"},\n" +
                "               \"endpoint\": {\"latitude\": \"52.444784\", \"longitude\": \"13.507886\"}, \"day\": \"2019-12-1\",\n" +
                "               \"restricted_area\": {}}";
        // parameter classofperson is missing
        SearchRequestDAO searchRequestDAO = util.getSearchParameterFromJSON(testJSON);
        assertEquals(null, searchRequestDAO.getPeopleType());
    }

    @Test
    public void getResearchParameterBad2() {
        String testJSON = "some message not in JSON format";
        assertThrows(IllegalArgumentException.class, () -> util.getSearchParameterFromJSON(testJSON));
    }

    @Test
    public void checkDAOGood(){
        util.checkDAO(searchRequestDAO);
        // no exception expected
    }

    @Test
    public void checkDAOBad1(){
        assertThrows(NullPointerException.class, () -> util.checkDAO(null));
    }

    @Test
    public void checkDAOBad2(){
        searchRequestDAO = new RequestDAO("13.527333","52.457907", "13.507886", "52.444784", "2019-12-1", "student", "bicycle", "true", new ArrayList<>(), new ArrayList<>(), null);
        assertThrows(IllegalArgumentException.class, () -> util.checkDAO(searchRequestDAO));
    }

    @Test
    public void generateJSONGood(){
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("hello", "world");
        assertTrue(util.generateJSON(testMap).equals("{\"hello\":\"world\"}"));
    }

    @Test
    public void generateJSONGood2() throws ParseException {
        JSONParser parser = new JSONParser();
        HashMap<String, String> testMap = new HashMap<>();
        testMap.put("hello", "world");
        String json= util.generateJSON(testMap);
        JSONObject jsonObject = (JSONObject) parser.parse(json);
        assertTrue(jsonObject.containsKey("hello"));
        assertTrue(jsonObject.containsValue("world"));
    }

    @Test
    public void generateJSONEdge() throws ParseException {
        JSONParser parser = new JSONParser();
        HashMap<String, String> testMap = new HashMap<>(); // pass empty hashMap

        String json= util.generateJSON(testMap);
        JSONObject jsonObject = (JSONObject) parser.parse(json);
        assertTrue(jsonObject.isEmpty());
    }

    @Test
    public void generateUUIDGood1(){
        String uuid1 = util.generateUUID();
        String uuid2 = util.generateUUID();
        assertFalse(uuid1.equals(uuid2));
    }

    @Test
    public void generateUUIDGood2() {
        assertTrue(util.generateUUID().length() > 0);
    }

}