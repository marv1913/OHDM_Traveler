package util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.*;
import java.sql.DriverManager;

public class DB {
	
	public static Connection createConnection(Parameter parameter) throws SQLException{
        Properties connProps = new Properties();
        connProps.put("user", parameter.getUsername());
        connProps.put("password", parameter.getPwd());
        
        connProps.put("tcpKeepAlive", "true");
        
        Driver d = null;
            try {
                Class driverClass = Class.forName("org.postgresql.Driver");

                Object newInstance = driverClass.getConstructor().newInstance();
                
                d = (Driver)newInstance;
            }
            catch(Throwable re) {
                System.out.println("debugging: runtime problem: " + re.getMessage());
                re.printStackTrace();
            }
        
        Connection connection;
        try {
            connection = d.connect(
                    "jdbc:postgresql://" + parameter.getServername()
                    + ":" + parameter.getPortnumber() + "/" + parameter.getDbname(), connProps);
                    
            if (parameter.getSchema() != null && !parameter.getSchema().equalsIgnoreCase("")) {
                StringBuilder sql = new StringBuilder("SET search_path = public, ");
                sql.append(parameter.getSchema());
                PreparedStatement stmt = connection.prepareStatement(sql.toString());
                
                try {
                	stmt.execute();
                }
                catch(SQLException e) {
                	System.out.print(false);
                }
                
            }
        
            return connection;
        
        } catch (SQLException ex) {
            System.err.println("cannot connect to database - fatal - exit\n" + ex.getMessage());
            ex.printStackTrace(System.err);
        }
        
        System.exit(1);
        return null;
    }
}
