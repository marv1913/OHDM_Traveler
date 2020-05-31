package rest;

import traveler.RoutePlanner;
import util.Util;

import java.util.HashMap;

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
                SearchRequestDAO dao = util.getSearchParameterFromJSON(request.body());
                util.checkDAO(dao);
                String uuid = util.generateUUID();
                result = generateAnswerJSON(routePlanner.planRoute(dao, uuid).get("total"), uuid);
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
     * @param time travel_time for calculated route
     * @param requestID ID of the clients request
     * @return generated JSON
     */
    public String generateAnswerJSON(String time, String requestID){
        HashMap<String, String> tempHashMap = new HashMap<>();
        tempHashMap.put("travel_time", time);
        tempHashMap.put("request_id", requestID);
        return util.generateJSON(tempHashMap);
    }


}
