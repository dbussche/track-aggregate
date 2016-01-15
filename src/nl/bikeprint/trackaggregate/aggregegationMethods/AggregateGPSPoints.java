package nl.bikeprint.trackaggregate.aggregegationMethods;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import nl.bikeprint.trackaggregate.general.ColumnType;
import nl.bikeprint.trackaggregate.shared.AggregationInterface;
import nl.bikeprint.trackaggregate.shared.DatabaseWriterInterface;
import nl.bikeprint.trackaggregate.shared.GPSPoint;
import nl.bikeprint.trackaggregate.shared.GPSTrack;
import nl.bikeprint.trackaggregate.shared.TableWriter;
 
public class AggregateGPSPoints implements AggregationInterface {
	
	private final int MAX_GAP = 100;
	
	private DatabaseWriterInterface databaseWriter;
	TableWriter table;
	
	@Override
	public void init(DatabaseWriterInterface databaseWriter, String bbox) {
	    this.databaseWriter = databaseWriter;
		table = new TableWriter("gps_points");
		table.addColumn("id", ColumnType.INTEGER);
		table.addColumn("time", ColumnType.INTEGER);
		table.addColumn("speed", ColumnType.FLOAT);
		table.addColumn("geometry", ColumnType.GEOMETRY);
		databaseWriter.createTable(table);
	}

	@Override
	public void add(GPSTrack gpsTrack) {
		GPSPoint point;
		Date startDate;
		int id;
		ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>();
		int i = 0;
		double length = 0; 
		Random random = new Random();
	
mainLoop:
		while (true) {
			double gap = random.nextDouble() * MAX_GAP;
			double goTo = length + gap;
			while (length < goTo) {
				i++;
				if (i >= gpsTrack.getAantal()) break mainLoop;
				length += gpsTrack.getDistance2Vertices(i);
			} 
			
			point = gpsTrack.getNode(i);
			startDate = point.getDate();
			id = random.nextInt();
			goTo += MAX_GAP;
			while (length < goTo) {
				i++;
				if (i >= gpsTrack.getAantal()) break mainLoop;
				point = gpsTrack.getNode(i);
				ArrayList<String> value = new ArrayList<String>();
				value.add(id + "");
				value.add((int)(point.getDate().getTime() - startDate.getTime()) + "");
				value.add(point.getSpeed() + "");
				value.add("POINT(" + point.getX() + " " + point.getY() + ")");
				values.add(value);
				length += gpsTrack.getDistance2Vertices(i);				
			}  
		} 

		databaseWriter.writeRecords(table, values);
	}

	@Override
	public void exit() {
		// nothing to do		
	}

 
}
