package nl.bikeprint.trackaggregate.general;

 
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
 

public class ColumnType {
	public static final ColumnType INTEGER = new ColumnType(1);
	public static final ColumnType FLOAT = new ColumnType(2);
	public static final ColumnType DOUBLE = new ColumnType(3);
	public static final ColumnType String = new ColumnType(4);
	public static final ColumnType GEOMETRY = new ColumnType(5);
	public static final ColumnType TIMESTAMP = new ColumnType(6);
	
	
	// toevoegen datumtijd gewijzigd
	
	private int typenumber;
	
	private ColumnType(int typenumber) {
		this.typenumber = typenumber;
	}
	
	public String getKolomTypeString() {
		switch (typenumber) {
		   case 1: return "INTEGER";
		   case 2: return "FLOAT"; 
		   case 3: return "DOUBLE PRECISION"; 
		   case 4: return "TEXT"; 
		   case 5: return "GEOMETRY"; 
		   case 6: return "TIMESTAMP";
		   default: return null;
		}
	}

	public void setValue(PreparedStatement preparedStatement, int i, String value) {
		try {
			switch (typenumber) {
 		        case 1: preparedStatement.setInt(i + 1, Tools.makeInt(value)); break;
		        case 2: preparedStatement.setFloat(i + 1, Tools.makeFloat(value)); break;
		        case 3: preparedStatement.setDouble(i + 1, Tools.makeDouble(value)); break;
		        case 4: preparedStatement.setString(i + 1, value); break;
		        case 5: preparedStatement.setString(i + 1, value); break;	  
		        case 6: 
		            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
					Date parsedDate;
					Timestamp timestamp;
					try {
						parsedDate = dateFormat.parse(value);
						timestamp = new java.sql.Timestamp(parsedDate.getTime());
					} catch (ParseException e) {
						timestamp = new Timestamp(0);
					}
		            
		        	preparedStatement.setTimestamp(i + 1, timestamp); 
		        	break;
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPlaceholderInPreparedStatement() {
		switch (typenumber) {
		   case 1: return "?";  
		   case 2: return "?"; 
		   case 3: return "?";  
		   case 4: return "?"; 
		   case 5: return "ST_GeomFromText(?,900913)"; 
		   case 6: return "?";
		   default: return null;
		}
	}
}
