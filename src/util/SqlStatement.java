package util;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlStatement {
	private List<Connection> connections = new ArrayList<>();
	private static ResultSet resultSet = null;
	private static boolean executeQuery = false;

	protected StringBuilder sqlQueue =  null;
	public static boolean debug_mode = false;

	public SqlStatement(Parameter parameter, int maxThreads, boolean forceJDBC) throws SQLException, FileNotFoundException {
        this.connections.clear();

        if(forceJDBC) {
            // set up connections
            try {
                // try to create first
                this.connections.add(DB.createConnection(parameter));

                // create connections more connection
                for(int i=1; i < maxThreads; i++) {
                    this.connections.add(DB.createConnection(parameter));
                }
            }
            catch(SQLException e) {
                System.err.println("could not create db connection.. probably we work with psql");
                this.connections = null;
            }
        } else {
            this.connections = null;
        }
    }

	public void append(String a) {
        if(this.sqlQueue == null) {
            this.sqlQueue = new StringBuilder(a);
        } else {
            this.sqlQueue.append(a);
        }
    }

    public void append(int a) {
        this.append(Integer.toString(a));
    }

    public void append(long a) {
        this.append(Long.toString(a));
    }

    public void forceExecute() throws SQLException {
        if(this.sqlQueue == null || this.sqlQueue.length() < 1) {
            return;
        }

        if(this.connections == null) {
        	//
        } else { // JDBC
            Connection conn = this.getConnections().get(0);
            try {
                SqlStatement.doExec(conn, this.sqlQueue.toString());
            }
            catch(SQLException e) {
                throw e;
            }
            finally {
                // in any case
            	this.sqlQueue = null;
            }
        }
    }

	static void doExec(Connection connection, String sqlStatement) throws SQLException {
        if(sqlStatement == null) return;

        SQLException e = null;

        PreparedStatement stmt = null;

        resultSet = null;

        try {
            if(connection == null) {
                System.err.println("no connection to database - cannot perform sql statement");
                throw new SQLException("connection is null");
            }
            if(sqlStatement == null) {
                System.err.println("cannot execute empty (null) sqlStatement - continue");
                return;
            }

            stmt = connection.prepareStatement(sqlStatement);
            if(SqlStatement.debug_mode){
                System.out.println(sqlStatement);
            }
            if(isExecuteQuery()){
            	resultSet = stmt.executeQuery();
            }else{
            	stmt.execute();
            }
        } catch (SQLException ex) {
            e = ex;
        }
        catch (Throwable re) {
            System.err.println("runtime exception when performing sql: " + sqlStatement);
            throw re;
        }
        finally {
        	setExecuteQuery(false);
            if(e != null) throw e;
        }
    }

	public List<Connection> getConnections() {
		return connections;
	}
	public static boolean isExecuteQuery() {
		return executeQuery;
	}
	public static void setExecuteQuery(boolean executeQuery) {
		SqlStatement.executeQuery = executeQuery;
	}
	public static ResultSet getResultSet() {
		return resultSet;
	}
}
