package nl.bikeprint.trackaggregate.shared;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import nl.bikeprint.trackaggregate.general.ColumnType;

public class TableWriter {

	private String tabelnaam;
    private HashMap<String,ColumnType> kolommen = new HashMap<String,ColumnType>();
    private ArrayList<String> kolommenIndex = new ArrayList<String>();
    
	public TableWriter(String tabelnaam) {
	    this.tabelnaam = tabelnaam;
	}

	public void addKolom(String naam, ColumnType kolomtype) {
		kolommen.put(naam,  kolomtype);		
		kolommenIndex.add(naam);
	}
	
	public String getCreateQuery(String schema) {
		String query = "CREATE TABLE IF NOT EXISTS " + schema + "." + tabelnaam + "(";
		boolean eerste = true;
		for (Entry<String,ColumnType> entry: kolommen.entrySet()) {
			if (!eerste) {
				query += ",";
			} else {
				eerste = false;
			}
			query += entry.getKey() + " " + entry.getValue().getKolomTypeString();
		}
		query += " )";
		return query;
	}
	
	public String getInsertQuery(String schema) {
		String query = "INSERT INTO " + schema + "." + tabelnaam + " VALUES (";
		boolean eerste = true;
		for (Entry<String,ColumnType> entry: kolommen.entrySet()) {
			if (!eerste) {
				query += ",";
			} else {
				eerste = false;
			}
			query += entry.getValue().getPlaceholderInPreparedStatement();
		}
		query += " )";
		return query;
	}

	public int getAantalKolommen() {		 
		return kolommen.size();
	}

	public void setValue(PreparedStatement preparedStatement, int i, String waarde) {
		if (kolommen.get(kolommenIndex.get(i)) == null) {
			System.err.println("NULL:" + i + ", " + kolommenIndex.get(i) + ": " + waarde);
		} else {
		    kolommen.get(kolommenIndex.get(i)).setValue(preparedStatement, i, waarde);
		}
	}

	public String getNaam() {		 
		return tabelnaam;
	}
 

}
