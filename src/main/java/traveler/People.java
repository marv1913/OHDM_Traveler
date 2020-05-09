package traveler;

import util.SqlStatement;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class People {
	private static String peopleTable;
	private SqlStatement sql = null;
	private Map<Integer, String> peopleMap = new LinkedHashMap<>();

	public People(String schema, SqlStatement sql){
		//Add all default people types to list
		for (PeopleTypes people: PeopleTypes.values()) {
			addPeople(people.toString());
		}
		peopleTable = schema + ".people";
		this.sql = sql;
		this.removePeopleTable();
		this.createPeopleTable();
	}

	/**
	 * Add people able to travel
	 * @param peopleName unique name of the person travelling
	 * @throws IllegalArgumentException if name already in the list
	 */
	public void addPeople(String peopleName) throws IllegalArgumentException {
		if (peopleMap.containsValue(peopleName)) {
			throw new IllegalArgumentException(peopleName + " is already in the list");
		}
		peopleMap.put(peopleMap.size() + 1, peopleName);
	}

	/**
	 * Removes a person from the list
	 * @param peopleId id number of the person
	 * @throws IllegalArgumentException
	 */
	public void removePeople(int peopleId) throws IllegalArgumentException{
		if (!peopleMap.containsKey(peopleId)){
			throw new IllegalArgumentException(peopleId + " is not in the list");
		}
		peopleMap.remove(peopleId);
	}

	/**
	 * Fill tables with people able to travel
	 */
	public void fillPeopleTable() {
		peopleMap.forEach((k, v) -> {
			try {
				sql.append("INSERT INTO " + peopleTable + " (id, title)");
				sql.append(" VALUES");
				sql.append(" (" + k + ", '" + v + "')");
				sql.forceExecute();
			} catch (SQLException e) {
				System.err.println("failed to fill peopleTable");
				e.printStackTrace();
			}
		});
	}

	public Map<Integer, String> getPeopleMap() {
		return peopleMap;
	}

	/**
	 * Removes the people table in the db if already exists
	 */
	private void removePeopleTable(){
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(peopleTable);
			sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop peopleTable");
			e1.printStackTrace();
		}
	}

	/**
	 * Create people Table
	 */
	private void createPeopleTable(){
		try {
			sql.append("CREATE TABLE ");
			sql.append(peopleTable);
			sql.append(" (id serial PRIMARY KEY,");
			sql.append(" title VARCHAR (255) UNIQUE NOT NULL)");

			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create peopleTable");
			e.printStackTrace();
		}
	}
}