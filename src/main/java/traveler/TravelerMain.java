package traveler;

import util.Parameter;
import util.SearchParameter;
import util.SqlStatement;
import util.Util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;


public class TravelerMain {

	private static RoutingMech routingMech = null;
	private static Parameter ohdmParameter = null;
	private static SearchParameter searchParameter = null;
	private static SqlStatement sqlStatement = null;

	public static void main(String[] args) throws IOException, SQLException {
		if(args.length < 4) {
            /* at least two parameter are required which are 
            defined with at least four arguments
            */
			System.out.println("Not enough parameter");
		}

		String routingDBConfig = null;
		String searchParameterConfig = null;

		HashMap<String, String> argumentMap = Util.parametersToMap(args,
				false, "[usage description should be added here :/]");

		if(argumentMap != null) {
			String value = argumentMap.get("-r");
			if (value != null) {  routingDBConfig = value; }
			value = argumentMap.get("-s");
			if (value != null) {  searchParameterConfig = value; }
		}

		System.err.println("routingDBConfig: " + routingDBConfig);
		System.err.println("searchParameterConfig: " + searchParameterConfig);

		ohdmParameter = new Parameter(routingDBConfig);
		searchParameter = new SearchParameter(searchParameterConfig);

		sqlStatement = new SqlStatement(ohdmParameter, 1, true);

		RestrictedArea area = new RestrictedArea(ohdmParameter.getSchema(), sqlStatement);
		TransportMittel tm = new TransportMittel(ohdmParameter.getSchema(), sqlStatement);
		People people = new People(ohdmParameter.getSchema(), sqlStatement);

		routingMech = new RoutingMech(ohdmParameter.getSchema(), searchParameter, area);

		routingMech.createTopologyTable(sqlStatement);

		routingMech.findWay(sqlStatement);
	}

}
