package traveler;

import util.SqlStatement;

import java.sql.SQLException;

public class People {
private String peopleTable = null;

	public People(String schema, SqlStatement sql){
		peopleTable = schema + ".people";
		this.fillPeopleTable(sql);
	}

	private void fillPeopleTable(SqlStatement sql){
		try {
            sql.append("DROP TABLE if exists ");
            sql.append(peopleTable);
            sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop peopleTable");
			e1.printStackTrace();
		}
            
        try {    
        	sql.append("CREATE TABLE ");
        	sql.append(peopleTable);
        	sql.append(" (id serial PRIMARY KEY,");
        	sql.append(" titel VARCHAR (255) UNIQUE NOT NULL)");
        	
            sql.forceExecute();
        } catch (SQLException e) {
           System.err.println("failed to create peopleTable");
           e.printStackTrace();
        }
        //exemplarisch
        try {
        	sql.append("INSERT INTO " + peopleTable + " (id, titel)");
            sql.append(" VALUES");
            sql.append(" (1, 'Bauer'),");
            sql.append(" (2, 'Adlige')");
            
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to fill peopleTable");
			e.printStackTrace();
		}
	}
}
