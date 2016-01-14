package aggregate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginDatabase {

	static Connection con = null;
	static public boolean LOKAAL = true;

	public static Connection getConnection() {

		if (con != null) {
			return con;
		} else {
		    try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		    try {
		    	if (LOKAAL) {
		    		con = DriverManager.getConnection("jdbc:postgresql:bikeprint", "postgres", "Bigt26041");
		    	} else {
		    	    con = DriverManager.getConnection("jdbc:postgresql://hez04.goudmap.info/bikeprint", "postgres", "*****");		    	
		    	}                       
				return con;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}	
		}
	}
	
	public static ResultSet execQuery(Connection con, String query) {
		try	{
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return (stmt.executeQuery(query));
		}
		catch (SQLException e)
		{
			System.out.println("Kan de query niet maken: " + query);
			System.out.println(e.getMessage());
			return (null);
		}
	}
	
	public static ResultSet execQuery(String query) {
		Connection con = getConnection();
		if (con != null) {
    	    return execQuery(con, query);
		} else {
			return null;
		}
	}
	
	public static void execUpdate(Connection con, String query) {
		try	{
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.executeUpdate(query);
		}
		catch (SQLException e)
		{
			System.out.println("Kan de query niet maken: " + query);
			System.out.println(e.getMessage());
		}
	}
	
	public static void execUpdate(String query) {
		Connection con = getConnection();
		if (con != null) {
    	    execUpdate(con, query);
		} 
	}

}
