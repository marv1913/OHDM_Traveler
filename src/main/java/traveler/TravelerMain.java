package traveler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import util.*;

public class TravelerMain {

    private static RoutingMech routingMech = null;
    private static Parameter ohdmParameter = null;
    private static SearchParameter searchParameter = null;
    private static SqlStatement sqlStatement = null;
    private static String[] requiredArgs = {"-r", "-s"};
    private static String helpText = "-r [path to db_routing.txt] -s [path to search_parameter.txt] optional debug mode: -d [true/false]";

    public static void main(String[] args) throws IOException, SQLException {

        if (args.length < 4) {
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
                TravelerMain.requiredArgs, helpText);

        if (argumentMap != null) {
            String value = argumentMap.get("-r");
            if (value != null) {
                routingDBConfig = value;
            }
            value = argumentMap.get("-s");
            if (value != null) {
                searchParameterConfig = value;
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
        searchParameter = new SearchParameter(searchParameterConfig);

        // check parameter before pass them to routingMech

        sqlStatement = new SqlStatement(ohdmParameter, 1, true);
        SqlStatement.debug_mode = debug_mode;

        RestrictedArea area = new RestrictedArea(ohdmParameter.getSchema(), sqlStatement);
        TransportMittel tm = new TransportMittel(ohdmParameter.getSchema(), sqlStatement);
        People people = new People(ohdmParameter.getSchema(), sqlStatement);

        routingMech = new RoutingMech(ohdmParameter.getSchema(), searchParameter, area);

        routingMech.createTopologyTable(sqlStatement);

        routingMech.deleteNullEntriesFromRoutingTopologyNoded(sqlStatement);

        routingMech.findWay(sqlStatement);
    }

}
