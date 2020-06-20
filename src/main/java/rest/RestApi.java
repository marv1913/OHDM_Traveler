package rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import traveler.RoutePlanner;
import util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.*;

public class RestApi {

    private Util util;
    private int port = 5555;
    private RoutePlanner routePlanner;

    public RestApi(RoutePlanner routePlanner) {
        this.util = new Util();
        this.routePlanner = routePlanner;
    }

    public void listenForPostRequest() {
        port(port);
        post("/ohdm_traveler", (request, response) -> {
            String result;
            try {
                String raw_request_body = request.body();
                System.out.println("got request:\n" + raw_request_body);
                SearchRequestDAO dao = util.getSearchParameterFromJSON(raw_request_body);
                util.checkDAO(dao);
                String uuid = util.generateUUID();
                HashMap<String, String> resultFromRoutePlanner = routePlanner.planRoute(dao, uuid);
                result = generateAnswerJSON(resultFromRoutePlanner.get("total"), uuid, resultFromRoutePlanner.get("geometries"));
            } catch (Exception e) {
                e.printStackTrace();
                response.status(500);
                System.out.println(e.getMessage());
                return "request has bad format\n" + e.getMessage();
            }
            if (result != null) {
                return result;
            }
            return "internal server error";
        });

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    }

    /**
     * generates the answer of the server as JSON
     *
     * @param time      travel_time for calculated route
     * @param requestID ID of the clients request
     * @return generated JSON
     */
    public String generateAnswerJSON(String time, String requestID, String geometryJSON) {
        HashMap<String, String> tempHashMap = new HashMap<>();
        tempHashMap.put("travel_time", time);
        tempHashMap.put("request_id", requestID);

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = (JSONArray) jsonParser.parse(geometryJSON);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = util.generateJSON(tempHashMap);
        jsonObject.put("geometries", jsonArray);

        return jsonObject.toJSONString();
    }

}
