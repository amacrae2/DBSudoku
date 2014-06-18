package assign3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySQLUtil {
	
	private static String START_ADD_QUERY = "INSERT INTO metropolises VALUES (\"";
	private static String SEP_ONE_ADD_QUERY = "\",\"";
	private static String SEP_TWO_ADD_QUERY = "\",";
	private static String END_ADD_QUERY = ");";

	
	
	public static List<Row> getRows(String metropolis, String continent, String population, 
								    boolean largerThan, boolean exactMatch) {
		List<Row> rows = new ArrayList<Row>();
		Connection conn = getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		if(conn!=null){
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(generateQueryString(metropolis,continent,population,largerThan,exactMatch));
				while(rs.next()){
					String metro = rs.getString(1);
					String cont = rs.getString(2);
					int pop = rs.getInt(3);
					Row row = new Row(metro, cont, pop);
					rows.add(row);
				}
				
			} catch (SQLException e) {
			    System.out.println("SQLException: " + e.getMessage());
			}
		}
		return rows;
	}
	
	private static String generateQueryString(String metropolis, String continent, String population, 
											  boolean largerThan, boolean exactMatch) {
		String query = "";
		if (largerThan && exactMatch) {
			query = "SELECT * FROM metropolises WHERE metropolis = \""+metropolis+"\" AND continent = \""+continent+"\" AND population > "+population+";";
		} else if (!largerThan && exactMatch) {
			query = "SELECT * FROM metropolises WHERE metropolis = \""+metropolis+"\" AND continent = \""+continent+"\" AND population <= "+population+";";
		} else if (!largerThan && !exactMatch) {
			query = "SELECT * FROM metropolises WHERE metropolis LIKE \"%"+metropolis+"%\" AND continent LIKE \"%"+continent+"%\" AND population <= "+population+";";
		} else if (largerThan && !exactMatch) {
			query = "SELECT * FROM metropolises WHERE metropolis LIKE \"%"+metropolis+"%\" AND continent LIKE \"%"+continent+"%\" AND population > "+population+";";
		}
		return query;
	}

	public static void writeToDB(Row row){
		Connection conn = getConnection();
		Statement stmt = null;
		if(conn!=null){
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(START_ADD_QUERY+row.getMetropolis()+
								   SEP_ONE_ADD_QUERY+row.getContinent()+
								   SEP_TWO_ADD_QUERY+row.getPopulation()+END_ADD_QUERY);
			} catch (SQLException e) {
			    System.out.println("SQLException: " + e.getMessage());
			}
		}
	}
	
	public static Connection getConnection(){
		Connection conn = null;
		try {
		    conn = DriverManager.getConnection("jdbc:mysql://"+MyDBInfo.MYSQL_DATABASE_SERVER+"/"+MyDBInfo.MYSQL_DATABASE_NAME+"?"+
		                                   "user="+MyDBInfo.MYSQL_USERNAME+"&password="+MyDBInfo.MYSQL_PASSWORD);

		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return conn;
	}

	
}
