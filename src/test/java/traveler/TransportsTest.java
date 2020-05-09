package traveler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Parameter;
import util.SqlStatement;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author : Enrico Gamil Toros de Chadarevian
 * Project name : HistoricRouteSearch
 * @version : 1.0
 * @since : 06.05.2020, Mi.
 **/
class TransportsTest {
    SqlStatement sqlStatement;
    Parameter parameter;

    /**
     * Helper method mocking the Parameter class and SqlStatement class before each test
     */
    @BeforeEach
    public void beforeEach(){
        sqlStatement = mock(SqlStatement.class);
        parameter = mock(Parameter.class);
        when(parameter.getSchema()).thenReturn("routing");
    }


    @Test
    void getTransportTypesMapTest() {
        Transports transports = new Transports(parameter.getSchema(), sqlStatement);
        Map<Integer, String> expectedMap = new HashMap<>();
        for (TransportTypes transportTypes: TransportTypes.values()) {
            expectedMap.put(expectedMap.size() + 1, "'" + transportTypes.name() + "', " + transportTypes.getSpeed());
        }

        Assertions.assertEquals(expectedMap, transports.getTransportTypesMap());
    }

    @Test
    void addTransportType() {
        Transports transports = new Transports(parameter.getSchema(), sqlStatement);
        try {
            transports.addTransportType("Boat", 12333);
            Assertions.fail("You shouldn't be able to add a Transport Type that already exists");
        }catch (IllegalArgumentException e){
            //ok
        }
    }
}