package aggregate;


import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import nl.bikeprint.trackaggregate.algemeen.KolomType;

public class BikePrintTabelSchrijver {

	private String tabelnaam;
    private HashMap<String,KolomType> kolommen = new HashMap<String,KolomType>();
    private ArrayList<String> kolommenIndex = new ArrayList<String>();
    
	public BikePrintTabelSchrijver(String tabelnaam) {
	    this.tabelnaam = tabelnaam;
	}

	public void addKolom(String naam, KolomType kolomtype) {
		kolommen.put(naam,  kolomtype);		
		kolommenIndex.add(naam);
	}
	
	public String getCreateQuery(String schema) {
		String query = "CREATE TABLE IF NOT EXISTS " + schema + "." + tabelnaam + "(";
		boolean eerste = true;
		for (Entry<String,KolomType> entry: kolommen.entrySet()) {
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

	public String getInsertQueryOud(String schema) {
		String query = "INSERT INTO " + schema + "." + tabelnaam + " VALUES (";
		for (int i = 0; i < kolommen.size(); i++) {
			if (i != 0) {
				query += ", ";	
			}
			System.out.println("kolom " + i);
			System.out.println(kolommen.get(i));
			System.out.println(kolommen.get(i).getPlaatshouderInPreparedStatement());
			query += kolommen.get(i).getPlaatshouderInPreparedStatement();
			
		}
		query += " )";
		return query;
	}
	
	public String getInsertQuery(String schema) {
		String query = "INSERT INTO " + schema + "." + tabelnaam + " VALUES (";
		boolean eerste = true;
		for (Entry<String,KolomType> entry: kolommen.entrySet()) {
			if (!eerste) {
				query += ",";
			} else {
				eerste = false;
			}
			query += entry.getValue().getPlaatshouderInPreparedStatement();
		}
		query += " )";
		return query;
	}

	public int getAantalKolommen() {		 
		return kolommen.size();
	}

	public void setWaarde(PreparedStatement preparedStatement, int i, String waarde) {
		if (kolommen.get(kolommenIndex.get(i)) == null) {
			System.err.println("NULL:" + i + ", " + kolommenIndex.get(i) + ": " + waarde);
		} else {
		    kolommen.get(kolommenIndex.get(i)).setWaarde(preparedStatement, i, waarde);
		}
	}

	public String getNaam() {		 
		return tabelnaam;
	}
 

}
