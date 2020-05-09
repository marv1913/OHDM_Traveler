package traveler;

import org.junit.jupiter.api.Test;
import util.SearchParameter;
import util.SqlStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

class RoutingMechTest {

    @Test
    public void createTopologyTableTestGoodWithoutWaterways() throws SQLException {
        SqlStatement sqlStatement = mock(SqlStatement.class);
        SearchParameter searchParameter = mock(SearchParameter.class);
        RestrictedArea restrictedArea = mock(RestrictedArea.class);
        when(searchParameter.getWaterwayIncl()).thenReturn("false");
        when(searchParameter.getTransportType()).thenReturn(TransportTypes.Horse);
        when(searchParameter.getStartPoint()).thenReturn("13.441565 52.46982751");
        when(searchParameter.getEndPoint()).thenReturn("13.441454 52.469845454");


        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString("id")).thenReturn("145335");
        when(rs.getString("id")).thenReturn("145368");

        SqlStatement.setResultSet(rs);
        RoutingMech routingMech = new RoutingMech("public", searchParameter, restrictedArea);
        spy(routingMech);
        routingMech.createTopologyTable(sqlStatement);
        verify(sqlStatement, times(36)).forceExecute();
    }

    @Test
    public void createTopologyTableTestGoodWithWaterways() throws SQLException {
        SqlStatement sqlStatement = mock(SqlStatement.class);
        SearchParameter searchParameter = mock(SearchParameter.class);
        RestrictedArea restrictedArea = mock(RestrictedArea.class);
        when(searchParameter.getWaterwayIncl()).thenReturn("true");
        when(searchParameter.getTransportType()).thenReturn(TransportTypes.Horse);
        when(searchParameter.getStartPoint()).thenReturn("13.441565 52.46982751");
        when(searchParameter.getEndPoint()).thenReturn("13.441454 52.469845454");


        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true);
        when(rs.getString("id")).thenReturn("145335");
        when(rs.getString("id")).thenReturn("145368");

        SqlStatement.setResultSet(rs);
        RoutingMech routingMech = new RoutingMech("public", searchParameter, restrictedArea);
        spy(routingMech);
        routingMech.createTopologyTable(sqlStatement);
        verify(sqlStatement, times(37)).forceExecute();
    }

    @Test
    public void deleteNullEntriesFromRoutingTopologyNodedTestGood() throws SQLException {
        SqlStatement sqlStatement = mock(SqlStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true).thenReturn(true).thenReturn(false); // table has two null entries

        SqlStatement.setResultSet(rs);
        RoutingMech routingMech = new RoutingMech("public", mock(SearchParameter.class), mock(RestrictedArea.class));
        routingMech.deleteNullEntriesFromRoutingTopologyNoded(sqlStatement);

        verify(sqlStatement, times(3)).forceExecute();
    }

    @Test
    public void deleteNullEntriesFromRoutingTopologyNodedTestGood2() throws SQLException {
        SqlStatement sqlStatement = mock(SqlStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(false); // table has 0 null entries
        SqlStatement.setResultSet(rs);
        RoutingMech routingMech = new RoutingMech("public", mock(SearchParameter.class), mock(RestrictedArea.class));
        routingMech.deleteNullEntriesFromRoutingTopologyNoded(sqlStatement);

        verify(sqlStatement, times(1)).forceExecute();
    }
}
