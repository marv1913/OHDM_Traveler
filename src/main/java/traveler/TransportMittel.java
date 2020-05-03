package traveler;

import util.SqlStatement;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransportMittel {

	private static String transportMittelTable = null;

	private static List<String> tm_accessAll = Arrays.asList("motorway", "trunk", "primary", "secondary", "tertiary",
			"unclassified", "residential", "living_street",
			"motorway_link", "trunk_link", "primary_link", "secondary_link",
			"service", "track", "track_grade1", "track_grade2", "track_grade3", "track_grade4");
	
	private static List<String> tm_accessOnFeet = Arrays.asList("pedestrian", "track_grade5",
			"bridleway", "cycleway", "footway", "path", "steps", "unknown", "undefined");
	
	private static List<String> tm_accessOnHorse = Arrays.asList("pedestrian", "track_grade5",
			"bridleway", "cycleway", "footway", "path");
	
	private static List<String> tm_accessOnShip = Arrays.asList("river", "stream",
			"canal", "drain");
	
	public TransportMittel(String schema, SqlStatement sql){
		transportMittelTable = schema + ".transport_mittel";
		this.fillTransportMittelTable(sql);
	}
	
	private void fillTransportMittelTable(SqlStatement sql){
		try {
            sql.append("DROP TABLE if exists ");
            sql.append(transportMittelTable);
            sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop transport_mittel table");
			e1.printStackTrace();
		}
            
        try {    
        	sql.append("CREATE TABLE ");
        	sql.append(transportMittelTable);
        	sql.append(" (id serial PRIMARY KEY,");
        	sql.append(" type VARCHAR (255) UNIQUE NOT NULL,");
        	sql.append(" speed INTEGER);");
        	
            sql.forceExecute();
        } catch (SQLException e) {
           System.err.println("failed to create topology table");
           e.printStackTrace();
        }
        
        try {
        	sql.append("INSERT INTO " + transportMittelTable + " (id, type, speed)");
            sql.append(" VALUES");
            sql.append(" (1, 'Fuss', 6),");
            sql.append(" (2, 'Pferd', 50),");
            sql.append(" (3, 'Kutsche', 9),");
            sql.append(" (4, 'Auto', 60),");
            sql.append(" (5, 'Schiff', 12)");
            
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to fill transportMittelTable");
			e.printStackTrace();
		}
	}
	
	public static List<String> getAccessWaysForTransportType(String transportType){
		List<String> mapForTm = null;
		switch(transportType){
        case "1":// Feet
        	mapForTm = new ArrayList<>(tm_accessAll.size() + tm_accessOnFeet.size());
        	mapForTm.addAll(tm_accessAll);
        	mapForTm.addAll(tm_accessOnFeet);
            break;
        case "2":// Horse
        	mapForTm = new ArrayList<>(tm_accessAll.size() + tm_accessOnHorse.size());
        	mapForTm.addAll(tm_accessAll);
        	mapForTm.addAll(tm_accessOnHorse);
            break;
        case "3":// Kutsche
        	mapForTm = new ArrayList<>(tm_accessAll.size());
        	mapForTm = tm_accessAll;
            break;
        case "4":// Auto
        	mapForTm = new ArrayList<>(tm_accessAll.size());
        	mapForTm = tm_accessAll;
            break;
        case "5":// Schiff
        	mapForTm = new ArrayList<>(tm_accessOnShip.size());
        	mapForTm = tm_accessOnShip;
            break;
		}
		return mapForTm;
	}

	public static List<String> getTm_accessAll() {
		return tm_accessAll;
	}

	public static List<String> getTm_accessOnFeet() {
		return tm_accessOnFeet;
	}

	public static List<String> getTm_accessOnHorse() {
		return tm_accessOnHorse;
	}
	public static String getTransportMittelTable() {
		return transportMittelTable;
	}
}
