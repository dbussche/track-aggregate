package nl.bikeprint.trackaggregate.shared;

import java.util.ArrayList;

public interface DatabaseWriterInterface {
	
	public boolean createTable(TableWriter table);
	public void writeRecords(TableWriter table, ArrayList<ArrayList<String>> values);

}
