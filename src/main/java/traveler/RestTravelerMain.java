package traveler;

import java.io.IOException;

import java.util.HashMap;

import rest.RestApi;
import util.*;

public class RestTravelerMain {

    private static Parameter ohdmParameter = null;
    private static SearchParameter searchParameter = null;
    private static SqlStatement sqlStatement = null;
    private static String[] requiredArgs = {"-r"};
    private static String helpText = "-r [path to db_routing.csv] optional debug mode: -d [true/false]";

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            /* at least two parameter are required which are
            defined with at least four arguments
            */
            System.out.println("Not enough parameter");
            System.out.println(helpText);
        }

        String routingDBConfig = null;

        boolean debug_mode = false;

        HashMap<String, String> argumentMap = Util.parametersToMap(args,
                RestTravelerMain.requiredArgs, helpText);

        if (argumentMap != null) {
            String value = argumentMap.get("-r");
            if (value != null) {
                routingDBConfig = value;
            }
            value = argumentMap.get("-d");
            if (value != null) {
                if (value.equals("true"))
                    debug_mode = true;
            }
        }

        ohdmParameter = new Parameter(routingDBConfig);
        RoutePlanner routePlanner = new RoutePlanner(ohdmParameter, debug_mode);

        RestApi restApi = new RestApi(routePlanner);
        System.out.println("REST-Server is running. Waiting for requests...");
        restApi.listenForPostRequest();


    }
}
