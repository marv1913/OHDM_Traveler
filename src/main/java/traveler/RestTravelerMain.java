package traveler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import rest.RestApi;
import util.*;

public class RestTravelerMain {

    private static Parameter ohdmParameter = null;
    private static SearchParameter searchParameter = null;
    private static SqlStatement sqlStatement = null;
    private static String[] requiredArgs = {"-r"};
    private static String helpText = "-r [path to db_routing.txt] -s [path to search_parameter.txt] optional debug mode: -d [true/false]";

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            /* at least two parameter are required which are
            defined with at least four arguments
            */
            System.out.println("Not enough parameter");
            System.out.println(helpText);
        }

        String routingDBConfig = null;
        String searchParameterConfig = null;
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

        System.err.println("routingDBConfig: " + routingDBConfig);
        System.err.println("searchParameterConfig: " + searchParameterConfig);

        ohdmParameter = new Parameter(routingDBConfig);
        RoutePlanner routePlanner = new RoutePlanner(ohdmParameter);

        RestApi restApi = new RestApi(routePlanner);
        restApi.listenForPostRequest();


    }



}