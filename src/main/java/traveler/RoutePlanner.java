package traveler;

import rest.SearchRequestDAO;
import util.Parameter;
import util.SearchParameter;
import util.SqlStatement;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;

import static util.SqlStatement.debug_mode;

public class RoutePlanner {

    private Parameter ohdmParameter;

    public RoutePlanner(Parameter parameter) {
        ohdmParameter = parameter;
    }

    public HashMap<String, String> planRoute(SearchRequestDAO searchRequestDAO) throws FileNotFoundException, SQLException {
        SearchParameter searchParameter = new SearchParameter(searchRequestDAO.getDay(), searchRequestDAO.getStartPointLongitude(),
                searchRequestDAO.getStartPointLatitude(), searchRequestDAO.getEndPointLongitude(), searchRequestDAO.getEndPointLatitude(),
                searchRequestDAO.getPeopleType(), searchRequestDAO.getWaterwayIncluded(), searchRequestDAO.getTransportType());
        SqlStatement sqlStatement = new SqlStatement(ohdmParameter, 1, true);
        debug_mode = debug_mode;

        RestrictedArea area = new RestrictedArea(ohdmParameter.getSchema(), sqlStatement);
        Transports tm = new Transports(ohdmParameter.getSchema(), sqlStatement);
        People people = new People(ohdmParameter.getSchema(), sqlStatement);
        people.fillPeopleTable();

        RoutingMech routingMech = new RoutingMech(ohdmParameter.getSchema(), searchParameter, area);

        routingMech.createTopologyTable(sqlStatement);

        routingMech.deleteNullEntriesFromRoutingTopologyNoded(sqlStatement);

        routingMech.findWay(sqlStatement);
        HashMap<String, String> results = new HashMap<>();
        results.put("total", RoutingMech.getFull_time());
        results.put("distance without waterways", RoutingMech.getDistance_without_water().toString());
        results.put("distance with waterways", RoutingMech.getDistance_water().toString());
        return results;
    }
}
