package traveler;

import util.SqlStatement;

import java.sql.SQLException;

public class RestrictedArea {

	private static int restrictedArea_count;
	private static String noded_vertices_pgrTable = null;
	private static String restictedAreaTable = null;
	private static String nodedTopologyTable = null;

	public RestrictedArea(String schema, SqlStatement sql){
		restrictedArea_count = 0;
		noded_vertices_pgrTable = schema + ".routing_topology_noded_vertices_pgr";
		restictedAreaTable = schema + ".restricted_area";
		nodedTopologyTable = schema + ".routing_topology_noded";
		this.createTableForRestrictArea(sql);
	}

	private void createTableForRestrictArea(SqlStatement sql){
		try {
			sql.append("DROP TABLE if exists ");
			sql.append(restictedAreaTable);
			sql.append(" CASCADE;");
			sql.forceExecute();
		} catch (SQLException e1) {
			System.err.println("failed to drop restictedAreaTable");
			e1.printStackTrace();
		}

		try {
			sql.append("CREATE TABLE ");
			sql.append(restictedAreaTable);
			sql.append(" (id INTEGER PRIMARY KEY, area geometry, closedfor INTEGER, valid_since date, valid_until date)");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to create restictedAreaTable");
			e.printStackTrace();
		}
	}

	public void fillTableForRestrictArea(SqlStatement sql){
		restrictedArea_count++;
		try {
			//Test Polygon
			//closedFor 'Adliger'
			sql.append("INSERT INTO ");
			sql.append(restictedAreaTable + " (id, area, closedfor, valid_since, valid_until) SELECT ");
			sql.append(restrictedArea_count +", ST_MakePolygon(ST_MakeLine(ARRAY[punkTe_baum.line,punkTe_baum_2.line,punkTe_baum_3.line,punkTe_baum_4.line,punkTe_baum.line])), 2, '2018-11-01', '2019-12-10'");
			sql.append(" FROM ");
			sql.append(" (SELECT (o.The_geom) as line");
			sql.append(" FROM ( SELECT id, The_geom");
			sql.append(" FROM " + noded_vertices_pgrTable + " WHERE id=97865) as o) As punkTe_baum");
			sql.append(",");
			sql.append(" (SELECT (l.The_geom) as line");
			sql.append(" FROM ( SELECT id, The_geom");
			sql.append(" FROM " + noded_vertices_pgrTable + " WHERE id=92966) as l) As punkTe_baum_2");
			sql.append(",");
			sql.append(" (SELECT (j.The_geom) as line");
			sql.append(" FROM ( SELECT id, The_geom");
			sql.append(" FROM " + noded_vertices_pgrTable + " WHERE id=99125) as j) As punkTe_baum_3");
			sql.append(",");
			sql.append(" (SELECT (n.The_geom) as line");
			sql.append(" FROM ( SELECT id, The_geom");
			sql.append(" FROM " + noded_vertices_pgrTable + " WHERE id=91308) as n) As punkTe_baum_4");
			sql.forceExecute();
		} catch (SQLException e) {
			System.err.println("failed to fill restictedAreaTable");
			e.printStackTrace();
		}
	}

	public void cutOff_RestrictArea(SqlStatement sql, String classOfPerson, String day){
		for(int currentAreaId = 1; currentAreaId <= restrictedArea_count; currentAreaId++){
			try {
				sql.append("DELETE FROM " + nodedTopologyTable + " tab");
				sql.append(" WHERE ST_Intersects((SELECT area FROM " + restictedAreaTable);
				sql.append(" WHERE " + restictedAreaTable +".id=" + currentAreaId);
				sql.append(" AND "+ restictedAreaTable +".closedfor="+ classOfPerson);
				sql.append(" AND ('"+ day +
						"' BETWEEN "+ restictedAreaTable +".valid_since AND "+ restictedAreaTable +".valid_until)),");
				sql.append(" (SELECT the_geom FROM " + noded_vertices_pgrTable);
				sql.append(" WHERE " + noded_vertices_pgrTable + ".id=tab.source))");
				sql.append(" OR");
				sql.append(" ST_Intersects((SELECT area FROM " + restictedAreaTable);
				sql.append(" WHERE " + restictedAreaTable +".id=" + currentAreaId);
				sql.append(" AND "+ restictedAreaTable +".closedfor="+ classOfPerson);
				sql.append(" AND ('"+ day +
						"' BETWEEN "+ restictedAreaTable +".valid_since AND "+ restictedAreaTable +".valid_until)),");
				sql.append(" (SELECT the_geom FROM " + noded_vertices_pgrTable);
				sql.append(" WHERE " + noded_vertices_pgrTable + ".id=tab.target))");

				sql.forceExecute();
			} catch (SQLException e) {
				System.err.println("failed to cut off resticted Areas");
				e.printStackTrace();
			}
		}

	}
}
