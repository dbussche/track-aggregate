package nl.bikeprint.trackaggregate.shared;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import nl.bikeprint.trackaggregate.general.ColumnType;

public class TableWriter {

	private String tabelnaam;
    private ArrayList<Column> columns = new ArrayList<Column>();
    
	public TableWriter(String tabelnaam) {
	    this.tabelnaam = tabelnaam;
	}

	public void addColumn(String naam, ColumnType kolomtype) {
		columns.add(new Column(naam, kolomtype));
	}

	public String getCreateQuery(String schema) {
		String query = "CREATE TABLE IF NOT EXISTS " + schema + "." + tabelnaam + "(";
		boolean eerste = true;
		for (Column column:columns) {	
			if (!eerste) {
				query += ",";
			} else {
				eerste = false;
			}
			query += column.getNaam() + " " + column.getColumnType().getKolomTypeString();
		}
		query += " )";
		return query;
	}
	
	public String getInsertQuery(String schema) {
		String query = "INSERT INTO " + schema + "." + tabelnaam + " VALUES (";
		boolean eerste = true;
		for (Column column:columns) {	
			if (!eerste) {
				query += ",";
			} else {
				eerste = false;
			}
			query += column.getColumnType().getPlaceholderInPreparedStatement();
		}
		query += " )";
		return query;
	}

	public int getAantalKolommen() {		 
		return columns.size();
	}

	public void setValue(PreparedStatement preparedStatement, int i, String waarde) {
		if (columns.get(i) == null) {
			System.err.println("NULL:" + i + ": " + waarde);
		} else {
			columns.get(i).getColumnType().setValue(preparedStatement, i, waarde);    
		}
	}

	public String getNaam() {		 
		return tabelnaam;
	}
 

}
