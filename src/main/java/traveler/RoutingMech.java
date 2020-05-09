package traveler;

import util.SearchParameter;
import util.SqlStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RoutingMech {
	public static String linesTable = null;
	public static String nodedTopologyTable = null;
	public static String noded_vertices_pgrTable = null;
	public static String restictedAreaTable = null;
	public static String resultWayTable = null;
	public static String resultTimeTable = null;
	private static SearchParameter searchParameter = null;
	private static RestrictedArea restrictedArea = null;
	private static Float distance_without_water = null;

	public static Float getDistance_without_water() {
		return distance_without_water;
	}

	public static Float getDistance_water() {
		return distance_water;
	}

	public static String getFull_time() {
		return full_time;
	}

	private static Float distance_water = null;
	private static String full_time = null;

	public static String startNodeDB = null;
	public static String endNodeDB = null;

	public RoutingMech(String schema, SearchParameter searchParam, RestrictedArea area) {
		linesTable = schema + ".routing_topology";
		nodedTopologyTable = schema + ".routing_topology_noded";
		noded_vertices_pgrTable = schema + ".routing_topology_noded_vertices_pgr";
		restictedAreaTable = schema + ".restricted_area";
		resultWayTable = schema + ".result_way_table";
		resultTimeTable = schema + ".result_time_table";
		searchParameter = searchParam;
		restrictedArea = area;
	}

	public void createTopologyTable(SqlStatement sql) {
		System.out.println("create table " + linesTable);
		this.createBasicRoadMap_basedOnTM(sql);

		//add waterways
		if (searchParameter.getWaterwayIncl().equals("true")) {
			System.out.println("add waterways");
			this.addWaterWays(sql);
		}

		try {
			sql.append("ALTER TABLE ");
			sql.append(linesTable);
			sql.append(" ADD COLUMN source bigint;");
			sql.append("ALTER TABLE ");
			sql.append(linesTable);
			sql.append(" ADD COLUMN target bigint;");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to add columns source/target in topology table");
			e.printStackTrace();
		}

		this.addIdColumn(sql);
		System.out.println("create routing_topology");
		this.createTopology(sql, linesTable);
		System.out.println("create routing_topology_noded");
		this.createNodedTable(sql);

		this.createTopology(sql, nodedTopologyTable);
		this.addLengthColumn(sql);

		restrictedArea.fillTableForRestrictArea(sql);
		restrictedArea.cutOff_RestrictArea(sql, searchParameter.getClassOfPerson(), searchParameter.getDay());

		startNodeDB = this.getNearestPoint(sql, searchParameter.getStartPoint());
		endNodeDB = this.getNearestPoint(sql, searchParameter.getEndPoint());
	}

	private void createBasicRoadMap_basedOnTM(SqlStatement sql) {
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(linesTable);
			sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop topology table");
			e1.printStackTrace();
		}
		try {
			sql.append("CREATE TABLE ");
			sql.append(linesTable);
			sql.append(" AS TABLE rendering.highway_lines WITH NO DATA");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create topology table");
			e.printStackTrace();
		}

		List<String> roadMapForTM = Transports.getAccessWaysForTransportType(searchParameter.getTransportType());

		for (String type : roadMapForTM) {
			try {
				sql.append("INSERT INTO " + linesTable);
				sql.append(" SELECT * FROM rendering.highway_lines");
				sql.append(" WHERE subclassname='" + type + "'");
				sql.append(" AND '" + searchParameter.getDay() + "' BETWEEN valid_since AND valid_until;");
				sql.forceExecute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void addIdColumn(SqlStatement sql) {
		try {
			sql.append("ALTER TABLE ");
			sql.append(linesTable);
			sql.append(" ADD COLUMN id SERIAL PRIMARY KEY;");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to add id column");
			e.printStackTrace();
		}
	}

	private void createNodedTable(SqlStatement sql) {
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(nodedTopologyTable);
			sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop topology table");
			e1.printStackTrace();
		}

		try {
			sql.append("SELECT pgr_nodeNetwork('");
			sql.append(linesTable);
			sql.append("', 0.5,'id','line','noded');");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create noded table");
			e.printStackTrace();
		}
	}

	private void createTopology(SqlStatement sql, String topologyTable) {

		try {
			sql.append("SELECT pgr_createTopology('");
			sql.append(topologyTable);
			sql.append("', 0.5,'line','id');");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create topology");
			e.printStackTrace();
		}
	}

	private void addLengthColumn(SqlStatement sql) {
		try {
			sql.append("ALTER TABLE ");
			sql.append(nodedTopologyTable);
			sql.append(" ADD COLUMN length double precision;");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to add length column");
			e.printStackTrace();
		}

		try {
			sql.append("UPDATE ");
			sql.append(nodedTopologyTable);
			sql.append(" SET length=ST_Length(line);");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to fill length column");
			e.printStackTrace();
		}
	}

	private void addWaterWays(SqlStatement sql) {
		try {
			sql.append("INSERT INTO " + linesTable);
			sql.append(" SELECT * FROM rendering.waterway_lines");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to add length column");
			e.printStackTrace();
		}
	}

	private String getNearestPoint(SqlStatement sql, String point) {
		try {
			sql.append("SELECT vertices.id, vertices.the_geom");
			sql.append(" FROM ");
			sql.append(noded_vertices_pgrTable + " vertices");
			sql.append(" ORDER BY ST_Distance(ST_Transform(ST_SetSRID(ST_PointFromText('POINT(" + point + ")'),4326),3857), vertices.the_geom)");
			sql.append(" LIMIT 1;");

			SqlStatement.setExecuteQuery(true);
			sql.forceExecute();
			ResultSet rs = SqlStatement.getResultSet();

			while (rs.next()) {
				System.out.println(rs.getString("id"));
				return rs.getString("id");
			}
		} catch (SQLException e) {
			System.err.println("failed to find nearest point");
			e.printStackTrace();
		}
		return null;
	}

	public void deleteNullEntriesFromRoutingTopologyNoded(SqlStatement sql) {
		try {

			sql.append("Select * FROM " + nodedTopologyTable + " WHERE source is null or target is null;");
			SqlStatement.setExecuteQuery(true);
			sql.forceExecute();
			ResultSet rs = SqlStatement.getResultSet();
			int affectedRows = 0;
			while (rs.next()) {
				affectedRows++;
			}
			if (affectedRows == 0) {
				return;
			}
			sql.append("Select * FROM " + nodedTopologyTable + " ;");
			SqlStatement.setExecuteQuery(true);
			sql.forceExecute();
			rs = SqlStatement.getResultSet();
			int totalRows = 0;
			while (rs.next()) {
				totalRows++;
			}

			sql.append("DELETE from " + nodedTopologyTable + " WHERE source is null or target is null;");
			SqlStatement.setExecuteQuery(false);
			sql.forceExecute();
			System.err.println("WARNING: " + affectedRows + " of " + totalRows + " rowsdeleted, because of null entries");

		} catch (SQLException e1) {
			System.err.println("failed to delete null entries");
			e1.printStackTrace();
		}
	}

	public void findWay(SqlStatement sql) {
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(resultWayTable);
			sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop resultWayTable table");
			e1.printStackTrace();
		}

		System.out.println("create Table with the Route");
		try {
			sql.append("CREATE TABLE " + resultWayTable + " AS");
			sql.append(" (SELECT id, old_id, path_seq, source, target, edge, cost, agg_cost, line FROM pgr_dijkstra(");
			sql.append("'SELECT id, source, target, length AS cost FROM " + nodedTopologyTable + "',");
			sql.append(startNodeDB + ",");
			sql.append(endNodeDB + ",");
			sql.append(" directed := false) INNER JOIN " + nodedTopologyTable + " n ON edge=n.id");
			sql.append(" ORDER BY path_seq ASC);");

			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to find way");
			e.printStackTrace();
		}

		System.out.println("add column subclass");
		try {
			sql.append("ALTER TABLE ");
			sql.append(resultWayTable);
			sql.append(" ADD COLUMN subclass character varying;");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to add column subclass");
			e.printStackTrace();
		}

		//fill colum subclass
		try {
			sql.append("UPDATE " + resultWayTable + " rr SET subclass=subclassname FROM (");
			sql.append(" SELECT rwt.*, temp_table.subclassname FROM " + resultWayTable + " rwt INNER JOIN (");
			sql.append(" SELECT result.id, start_topology.subclassname FROM " + resultWayTable + " result, " + linesTable + " start_topology");
			sql.append(" WHERE result.old_id=start_topology.id ) temp_table");
			sql.append(" ON rwt.id=temp_table.id ORDER BY path_seq ASC) join_table");
			sql.append(" WHERE rr.id=join_table.id;");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to fill column subclass");
			e.printStackTrace();
		}
		System.out.println("create and fill time table");
		this.createAndFillTimeTable(sql);
	}

	private void createAndFillTimeTable(SqlStatement sql) {
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(resultTimeTable);
			sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop resultTimeTable table");
			e1.printStackTrace();
		}

		try {
			sql.append("CREATE TABLE ");
			sql.append(resultTimeTable);
			sql.append(" (id INTEGER PRIMARY KEY, distance_without_water double precision, distance_water double precision, full_time interval)");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create resultTimeTable");
			e.printStackTrace();
		}

		//fill resultTimeTable
		StringBuilder sqlForDistance_NoWater = new StringBuilder();
		StringBuilder sqlForDistance_WithWater = new StringBuilder();
		List<String> waysSubClasses_TM = Transports.getAccessWaysForTransportType(searchParameter.getTransportType());
		List<String> waysSubClasses_Water = Transports.getAccessWaysForTransportType(TransportTypes.Boat);

		for (int i = 0; i < waysSubClasses_TM.size(); i++) {
			sqlForDistance_NoWater.append(" subclass='" + waysSubClasses_TM.get(i) + "'");
			if (i < waysSubClasses_TM.size() - 1) {
				sqlForDistance_NoWater.append(" OR ");
			}
		}
		for (int i = 0; i < waysSubClasses_Water.size(); i++) {
			sqlForDistance_WithWater.append(" subclass='" + waysSubClasses_Water.get(i) + "'");
			if (i < waysSubClasses_Water.size() - 1) {
				sqlForDistance_WithWater.append(" OR ");
			}
		}

		distance_without_water = this.countDistance(sql, sqlForDistance_NoWater);
		distance_water = this.countDistance(sql, sqlForDistance_WithWater);
		full_time = this.countFullTime(sql, distance_without_water, distance_water);

		try {
			sql.append("INSERT INTO " + resultTimeTable + " (id, distance_without_water, distance_water, full_time)");
			sql.append(" VALUES");
			sql.append(" (1, " + distance_without_water + ", " + distance_water + ", '" + full_time + "' )");

			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to fill " + resultTimeTable);
			e.printStackTrace();
		}
	}

	private Float countDistance(SqlStatement sql, StringBuilder waySubClass) {
		try {
			sql.append("SELECT SUM(tab.cost) AS summe FROM (SELECT * FROM " + resultWayTable + " WHERE ");
			sql.append(waySubClass.toString());
			sql.append(") tab;");

			SqlStatement.setExecuteQuery(true);
			sql.forceExecute();
			ResultSet rs = SqlStatement.getResultSet();

			while (rs.next()) {
				return rs.getFloat("summe");
			}
		} catch (SQLException e) {
			System.err.println("failed to find SUM for " + resultWayTable);
			e.printStackTrace();
		}
		return null;
	}

	private String countFullTime(SqlStatement sql, Float distanceLand, Float distanceWater) {
		try {
			sql.append("SELECT ( "
					+ distanceLand
					+ " / (tab.speed_1 / 3.6) ) * interval '1 sec' + ( "
					+ distanceWater
					+ " / (tab.speed_2 / 3.6) ) * interval '1 sec' as summe FROM");
			sql.append("(SELECT speed_1, speed as speed_2  FROM (SELECT speed as speed_1 FROM "
					+ Transports.getTransportTypesTable()
					+ " WHERE id = "
					+ searchParameter.getTransportType().getId()
					+ ") as tm_1");
			sql.append("LEFT JOIN "
					+ Transports.getTransportTypesTable()
					+ " temp ON temp.type='"
					+ TransportTypes.Boat.toString()
					+ "') as tab");

			SqlStatement.setExecuteQuery(true);
			sql.forceExecute();
			ResultSet rs = SqlStatement.getResultSet();

			while (rs.next()) {
				return rs.getString("summe");
			}
		} catch (SQLException e) {
			System.err.println("failed to find full time for " + resultWayTable);
			e.printStackTrace();
		}
		return null;
	}

}