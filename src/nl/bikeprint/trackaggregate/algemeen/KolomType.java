package nl.bikeprint.trackaggregate.algemeen;

import java.sql.PreparedStatement;
import java.sql.SQLException;
 

public class KolomType {
	public static final KolomType INTEGER = new KolomType(1);
	public static final KolomType FLOAT = new KolomType(2);
	public static final KolomType DOUBLE = new KolomType(3);
	public static final KolomType String = new KolomType(4);
	public static final KolomType GEOMETRY = new KolomType(5);
	
	// toevoegen datumtijd gewijzigd
	
	private int typenummer;
	
	private KolomType(int typenummer) {
		this.typenummer = typenummer;
	}
	
	public String getKolomTypeString() {
		switch (typenummer) {
		   case 1: return "INTEGER";
		   case 2: return "FLOAT"; 
		   case 3: return "DOUBLE PRECISION"; 
		   case 4: return "TEXT"; 
		   case 5: return "GEOMETRY"; 
		   default: return null;
		}
	}

	public void setWaarde(PreparedStatement preparedStatement, int i, String waarde) {
		try {
			switch (typenummer) {
 		        case 1: preparedStatement.setInt(i + 1, Tools.maakInt(waarde)); break;
		        case 2: preparedStatement.setFloat(i + 1, Tools.maakFloat(waarde)); break;
		        case 3: preparedStatement.setDouble(i + 1, Tools.maakDouble(waarde)); break;
		        case 4: preparedStatement.setString(i + 1, waarde); break;
		        case 5: preparedStatement.setString(i + 1, waarde); break;	  
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPlaatshouderInPreparedStatement() {
		switch (typenummer) {
		   case 1: 
		   case 2:  
		   case 3:  
		   case 4: return "?";
		   case 5: return "ST_GeomFromText(?,900913)"; 
		   default: return null;
		}
	}
}
