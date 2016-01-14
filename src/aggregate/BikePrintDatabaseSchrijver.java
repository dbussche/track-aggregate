package aggregate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import nl.bikeprint.trackaggregate.shared.DatabaseSchrijverInterface;


public class BikePrintDatabaseSchrijver implements DatabaseSchrijverInterface {

	private String schema;
	
	public BikePrintDatabaseSchrijver(String schema) {
		this.schema = schema;
	}

	public boolean maakTabel(BikePrintTabelSchrijver tabel) {
		String query = tabel.getCreateQuery(schema);
		LoginDatabase.execUpdate(query);
		LoginDatabase.execUpdate("TRUNCATE " + schema + "." + tabel.getNaam());
		 
		return true;
	}

	@Override
	public void schrijfRecords(BikePrintTabelSchrijver tabel, ArrayList<ArrayList<String>> waardes) {
		Connection con = LoginDatabase.getConnection(); 
		try {
			PreparedStatement preparedStatementInsert = 
				con.prepareStatement(tabel.getInsertQuery(schema));
			for (ArrayList<String> value: waardes) {
				for (int i = 0; i < tabel.getAantalKolommen(); i++) {
					tabel.setWaarde(preparedStatementInsert, i, value.get(i));
				}
				preparedStatementInsert.execute();
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		 
		
	}
}
