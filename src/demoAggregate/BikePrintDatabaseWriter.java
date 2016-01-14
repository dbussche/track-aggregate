package demoAggregate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import nl.bikeprint.trackaggregate.shared.DatabaseWriterInterface;
import nl.bikeprint.trackaggregate.shared.TableWriter;


public class BikePrintDatabaseWriter implements DatabaseWriterInterface {

	private String schema;
	
	public BikePrintDatabaseWriter(String schema) {
		this.schema = schema;
	}

	public boolean createTable(TableWriter table) {
		String query = table.getCreateQuery(schema);
		LoginDatabase.execUpdate(query);
		LoginDatabase.execUpdate("TRUNCATE " + schema + "." + table.getNaam());		 
		return true;
	}

	@Override
	public void writeRecords(TableWriter table, ArrayList<ArrayList<String>> values) {
		Connection con = LoginDatabase.getConnection(); 
		try {
			PreparedStatement preparedStatementInsert = 
				con.prepareStatement(table.getInsertQuery(schema));
			for (ArrayList<String> value: values) {
				for (int i = 0; i < table.getAantalKolommen(); i++) {
					table.setValue(preparedStatementInsert, i, value.get(i));
				}
				preparedStatementInsert.execute();
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
}
