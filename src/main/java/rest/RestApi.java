package rest;

import traveler.RoutePlanner;
import util.Utility;

import static spark.Spark.*;

public class RestApi {

    private Utility utility;
    private int port = 5555;
    private RoutePlanner routePlanner;

    public RestApi(RoutePlanner routePlanner) {
        this.utility = new Utility();
        this.routePlanner = routePlanner;
    }

    public void listenForPostRequest() {
        port(port);
        post("/ohdm_traveler", (request, response) -> {
            String result = null;
            try {
                SearchRequestDAO dao = utility.getSearchParameterFromJSON(request.body());
                utility.checkDAO(dao);
                result = routePlanner.planRoute(dao).get("total");
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


}
