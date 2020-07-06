package traveler;

import util.SearchParameter;
import util.SqlStatement;
import util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private String requestID;
    private String schema;
    private String renderingDataSchema; // schema where you can find the rendering tables (e.g. highway_lines tables)

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

    // ToDO add functionality to decide whether a new routing topology should be created or an existing routing topology
    //      should be used (speed up)

    public RoutingMech(String schema, SearchParameter searchParam, RestrictedArea area, String requestID, String renderingSchema) {
        linesTable = schema + ".routing_topology";
        nodedTopologyTable = schema + ".routing_topology_noded";
        noded_vertices_pgrTable = schema + ".routing_topology_noded_vertices_pgr";
        restictedAreaTable = schema + ".restricted_area";
        resultWayTable = schema + ".result_way_table";
        resultTimeTable = schema + ".result_time_table";
        searchParameter = searchParam;
        restrictedArea = area;
        this.requestID = requestID;
        this.schema = schema;
        this.renderingDataSchema = renderingSchema;
        System.out.println("rendering schema: " + renderingSchema);
    }

    public RoutingMech(String schema, SearchParameter searchParam, RestrictedArea area, String renderingSchema) {
        linesTable = schema + ".routing_topology";
        nodedTopologyTable = schema + ".routing_topology_noded";
        noded_vertices_pgrTable = schema + ".routing_topology_noded_vertices_pgr";
        restictedAreaTable = schema + ".restricted_area";
        resultWayTable = schema + ".result_way_table";
        resultTimeTable = schema + ".result_time_table";
        searchParameter = searchParam;
        restrictedArea = area;
        this.renderingDataSchema = renderingSchema;
    }

    // ToDo only call this function, if you want to create a new routing topology
    //      if you already have a routing topology this step should be skipped, because this takes the most of the time
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
            sql.append(" AS TABLE " + this.renderingDataSchema + ".highway_lines WITH NO DATA");
            sql.forceExecute();
        } catch (SQLException e) {
            System.err.println("failed to create topology table");
            e.printStackTrace();
        }

        List<String> roadMapForTM = Transports.getAccessWaysForTransportType(searchParameter.getTransportType());

        for (String type : roadMapForTM) {
            try {
                sql.append("INSERT INTO " + linesTable);
                sql.append(" SELECT * FROM " + this.renderingDataSchema + ".highway_lines");
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
            sql.append(" SELECT * FROM " + this.renderingDataSchema + ".waterway_lines");
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

            sql.append("ALTER TABLE ");
            sql.append(resultWayTable);
            sql.append(" ADD COLUMN street character varying;");
            sql.forceExecute();
        } catch (SQLException e) {
            System.err.println("failed to add column subclass");
            e.printStackTrace();

        }

        //fill column subclass
        try {
            sql.append("UPDATE " + resultWayTable + " rr SET subclass=subclassname, street=name FROM (");
            sql.append(" SELECT rwt.*, temp_table.subclassname, temp_table.name FROM " + resultWayTable + " rwt INNER JOIN (");
            sql.append(" SELECT result.id, start_topology.subclassname, start_topology.name FROM " + resultWayTable + " result, " + linesTable + " start_topology");
            sql.append(" WHERE result.old_id=start_topology.id ) temp_table");
            sql.append(" ON rwt.id=temp_table.id ORDER BY path_seq ASC) join_table");
            sql.append(" WHERE rr.id=join_table.id;");
            sql.forceExecute();
        } catch (SQLException e) {
            System.err.println("failed to fill column subclass");
            e.printStackTrace();
        }

        if (requestID != null) {
            System.out.println("copying data into persistent table result_way_table");
            try {
                System.err.println("persisting data");
                this.createAndFillResultWayTablePersistent(sql);
                this.createAndFillResultTimeTablePersistent(sql);
            } catch (SQLException e) {
                System.err.println("copying data into persistent table failed");
                e.printStackTrace();
            }
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

    private void createAndFillResultWayTablePersistent(SqlStatement sql) throws SQLException {
        // create result_way_table_persistent
        sql.append("CREATE TABLE IF NOT EXISTS " + schema + ".result_way_table_persistent" +
                "(id bigint, old_id integer,path_seq integer, source bigint, edge bigint, cost double precision, " +
                "agg_cost double precision, line geometry, subclass character varying,  " +
                "request_id character varying, street character varying, date character varying, my_key character varying PRIMARY KEY)");
        sql.forceExecute();
        // copy data from temporary result_way_table to persistent
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(new Date());

        sql.append("Select * FROM " + schema + ".result_way_table ;");
        SqlStatement.setExecuteQuery(true);
        sql.forceExecute();

        ResultSet rs = SqlStatement.getResultSet();

        // create temporary table to generate some unique id's

        sql.append("DROP TABLE IF EXISTS " + schema + ".result_way_id");
        sql.forceExecute();

        sql.append("CREATE TABLE " + schema + ".result_way_id (id character varying PRIMARY KEY, old_id bigint)");
        sql.forceExecute();

        while (rs.next()) {
            sql.append("INSERT INTO " + schema + ".result_way_id values ('" + new Util().generateUUID() + "', " + rs.getString(1) + ");");
            sql.forceExecute();
        }


        sql.append("INSERT INTO " + schema + ".result_way_table_persistent(id, old_id, path_seq, source, edge, cost, agg_cost, " +
                "line, subclass, request_id, street, date, my_key) select  rt.id, rt.old_id, rt.path_seq, rt.source, rt.edge, rt.cost, rt.agg_cost, rt.line, " +
                "rt.subclass, '" + requestID + "', rt.street,'" + dateString + "', rw.id" +
                " from " + schema + ".result_way_table as rt, " + schema + ".result_way_id as rw where rw.old_id=rt.id;");

        sql.forceExecute();
    }

    private void createAndFillResultTimeTablePersistent(SqlStatement sql) throws SQLException {
        // create result_way_table_persistent
        sql.append("CREATE TABLE IF NOT EXISTS " + schema + ".result_time_table_persistent" +
                "(distance_without_water double precision, distance_water double precision, full_time interval, " +
                "request_id character varying PRIMARY KEY, date character varying)");
        sql.forceExecute();
        // copy data from temporary result_way_table to persistent
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(new Date());


        sql.append("INSERT INTO " + schema + ".result_time_table_persistent(distance_without_water, " +
                "distance_water, full_time, request_id, " +
                "date) select rt.distance_without_water, rt.distance_water, rt.full_time, '" + requestID + "', '" + dateString + "'" +
                " from " + schema + ".result_time_table as rt where rt.id = 1;");

        sql.forceExecute();
    }

    public String getGeometriesFromResultWayTableAsJSON(SqlStatement sql) throws SQLException {
        sql.append("Select st_asGeoJSON(line) FROM " + schema + ".result_way_table;");
        SqlStatement.setExecuteQuery(true);
        sql.forceExecute();

        ResultSet rs = SqlStatement.getResultSet();
        List<String> tempList = new ArrayList<>();
        while (rs.next()) {
            tempList.add(rs.getString(1));
        }
        return new Util().generateJSONFromList(tempList);
    }



}