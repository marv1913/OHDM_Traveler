package traveler;

import util.SqlStatement;

import java.sql.SQLException;
import java.util.*;

public class Transports {
	private static String transportTypesTable = null;

	private Map<Integer, String> transportTypesMap = new LinkedHashMap<>();

	private static List<String> tm_accessAll = Arrays.asList("motorway", "trunk", "primary", "secondary", "tertiary",
			"unclassified", "residential", "living_street",
			"motorway_link", "trunk_link", "primary_link", "secondary_link",
			"service", "track", "track_grade1", "track_grade2", "track_grade3", "track_grade4");

	private static List<String> tm_accessOnFeet = Arrays.asList("pedestrian", "track_grade5", "bridleway", "cycleway", "footway", "path", "steps", "unknown", "undefined");

	private static List<String> tm_accessOnHorse = Arrays.asList("pedestrian", "track_grade5", "bridleway", "cycleway", "footway", "path");

	private static List<String> tm_accessOnShip = Arrays.asList("river", "stream", "canal", "drain");

	public Transports(String schema, SqlStatement sql) {
		transportTypesTable = schema + ".transport_type";

		//add default transport types to transportTypes Map
		for (TransportTypes transportTypes: TransportTypes.values()) {
			addTransportType(transportTypes.name(), transportTypes.getSpeed());
		}

		this.removeTransportTypesTable(sql);
		this.createTransportTypesTable(sql);
		this.fillTransportTypesTable(sql);
	}

	/**
	 * Returns a map containing for each transport type their access ways
	 * @param transportType Transport type
	 * @return List of Strings
	 */
	public static List<String> getAccessWaysForTransportType(TransportTypes transportType){
		List<String> mapForTm = null;
		switch(transportType){
			case Walking:
			case Bicycle:
				mapForTm = new ArrayList<>(tm_accessAll.size() + tm_accessOnFeet.size());
				mapForTm.addAll(tm_accessAll);
				mapForTm.addAll(tm_accessOnFeet);
				break;
			case Horse:
				mapForTm = new ArrayList<>(tm_accessAll.size() + tm_accessOnHorse.size());
				mapForTm.addAll(tm_accessAll);
				mapForTm.addAll(tm_accessOnHorse);
				break;
			case Carriage:
			case Car:
				mapForTm = new ArrayList<>(tm_accessAll.size());
				mapForTm = tm_accessAll;
				break;
			case Boat:
				mapForTm = new ArrayList<>(tm_accessOnShip.size());
				mapForTm = tm_accessOnShip;
				break;
		}
		return mapForTm;
	}

	/**
	 * Getter for tm_accessAll
	 * @return tm_accessAll
	 */
	public static List<String> getTm_accessAll() {
		return tm_accessAll;
	}

	/**
	 * Getter for transportTypesTable
	 * @return transportTypesTable
	 */
	public static String getTransportTypesTable() {
		return transportTypesTable;
	}

	public Map<Integer, String> getTransportTypesMap() {
		return transportTypesMap;
	}

	/**
	 * Add a new transport type
	 * @param transportTypeName unique name of the transport type
	 * @param speed constant speed of the transport
	 */
	public void addTransportType(String transportTypeName, int speed){
		transportTypesMap.forEach((k, v) -> {
			String temp = v;
			if(temp.contains("'" + transportTypeName + "', ")){
				throw new IllegalArgumentException(transportTypeName + " is already in the list");
			}
		});
		if (transportTypesMap.containsValue("'" + transportTypeName + "', " + speed)){
			throw new IllegalArgumentException(transportTypeName + " is already in the list");
		}
		// Transport type is saved as sql cmd ready to be used
		transportTypesMap.put(transportTypesMap.size() + 1, "'" + transportTypeName + "', " + speed);
	}

	/**
	 * Fills the table in the db with transport types
	 */
	private void fillTransportTypesTable(SqlStatement sql) {
		transportTypesMap.forEach((k, v) -> {
			try {
				sql.append("INSERT INTO " + transportTypesTable + " (id, type, speed)");
				sql.append(" VALUES");
				sql.append(" (" + k + ", " + v + ")");

				sql.forceExecute();
			} catch (SQLException e) {
				System.err.println("failed to fill peopleTable");
				e.printStackTrace();
			}
		});
	}

	/**
	 * Removes the transport types table form the db if already exists
	 */
	private void removeTransportTypesTable(SqlStatement sql) {
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(transportTypesTable);
			sql.append(" CASCADE;");

			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop transport_type table");
			e1.printStackTrace();
		}
	}

	/**
	 * Creates the transport types table in the db
	 */
	private void createTransportTypesTable(SqlStatement sql) {
		try {
			sql.append("CREATE TABLE ");
			sql.append(transportTypesTable);
			sql.append(" (id serial PRIMARY KEY,");
			sql.append(" type VARCHAR (255) UNIQUE NOT NULL,");
			sql.append(" speed INTEGER);");

			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create topology table");
			e.printStackTrace();
		}
	}
}