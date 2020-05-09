package traveler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Parameter;
import util.SqlStatement;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * @author : Enrico Gamil Toros de Chadarevian
 * Project name : Historic Route Search
 * @version : 1.0
 * @since : 03.05.2020, So.
 **/
public class PeopleTest {
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

    /**
     * Good Test
     * Tests if by calling the constructor of the people class
     * the number of "standards" PeopleTypes are put in the people map correctly
     */
    @Test
    public void fillWithDefaultPeopleTypesTest(){
        People people = new People(parameter.getSchema(), sqlStatement);
        Assertions.assertEquals(PeopleTypes.values().length, people.getPeopleMap().size());
    }

    /**
     * Good Test
     * Tests if by calling the constructor of the people class
     * the "standards" PeopleTypes are put in the people map correctly
     */
    @Test
    public void fillWithDefaultPeopleTypes2Test() {
        People people = new People(parameter.getSchema(), sqlStatement);
        Iterator<Map.Entry<Integer, String>> itr = people.getPeopleMap().entrySet().iterator();

        for (PeopleTypes peopleTypes : PeopleTypes.values()) {
            Map.Entry<Integer, String> entry = itr.next();
            Assertions.assertEquals(peopleTypes.name(), entry.getValue());
            Assertions.assertEquals(peopleTypes.ordinal() + 1, entry.getKey());
        }
    }

    /**
     * Good Test
     * Tests addPeople Method
     */
    @Test
    public void addPeopleTest(){
        People people = new People(parameter.getSchema(), sqlStatement);
        people.addPeople("Enrico");

        Assertions.assertEquals(PeopleTypes.values().length + 1, people.getPeopleMap().size());
    }

    /**
     * Bad Test
     * Try to add in people map a new Person that already exists
     */
    @Test
    public void addPeopleBadTest(){
        People people = new People(parameter.getSchema(), sqlStatement);

        try {
            //
            people.addPeople(PeopleTypes.Noble.toString());
            Assertions.fail("IllegalArgumentException was expected to be thrown" + PeopleTypes.Noble + "is already in the list");
        }catch (IllegalArgumentException e){
            //ok
        }
    }

    /**
     * Checks if for each PeopleType forceExecute is called
     */
    @Test
    public void fillTableTest(){
        People people = new People(parameter.getSchema(), sqlStatement);

        try {
            verify(sqlStatement, times(people.getPeopleMap().size())).forceExecute();
        } catch (SQLException e) {
            Assertions.fail(e.toString());
        }
    }
}
