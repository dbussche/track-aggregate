package demoAggregate;

import java.util.ArrayList;

import nl.bikeprint.trackaggregate.aggregegationMethods.AllClasses;
import nl.bikeprint.trackaggregate.shared.AggregationInterface;
import nl.bikeprint.trackaggregate.shared.GPSTrack;

public class SimpleAggregateTest {

	public static void main(String[] args) {
		System.out.println("Start");

        String bbox = "652000,6775000,653000,6776000"; // Lent

	    ArrayList<AggregationInterface> aggregationClasses = AllClasses.getAggregationClasses();
	    BikePrintDatabaseWriter databaseSchrijver = new BikePrintDatabaseWriter("test");
	    for (AggregationInterface aggregeerMethode: aggregationClasses) {
	    	System.out.println("init1 " +  aggregeerMethode.getClass().getName());
	    	aggregeerMethode.init(databaseSchrijver, bbox);
	    }
	    System.out.println("init Klaar ");

	    GPSTrack gpsTrack = new GPSTrack();
	    gpsTrack.add(652277.224220131, 6775497.17613675, 5.328, "2015-09-14 17:52:01");
	    gpsTrack.add(652291.473114952, 6775565.13594801, 5.364, "2015-09-14 17:52:20");
	    gpsTrack.add(652233.809618723, 6775565.67674455, 4.284, "2015-09-14 17:53:41");
	    gpsTrack.add(652203.419397735, 6775580.63879641, 4.284, "2015-09-14 17:53:55");
	    gpsTrack.add(652235.368091594, 6775571.26497756, 11.052, "2015-09-14 17:54:27");
	    gpsTrack.add(652239.486912753, 6775605.51552206, 13.356, "2015-09-14 17:54:32");
	    gpsTrack.add(652238.818995808, 6775711.33259186, 17.136, "2015-09-14 17:54:45");
	    gpsTrack.add(652229.35683909, 6775815.16806057, 17.46, "2015-09-14 17:54:57");
	    gpsTrack.add(652234.588855159, 6775932.16482587, 21.348, "2015-09-14 17:55:10");
	    gpsTrack.add(652231.249270433, 6776048.08162174, 18.684, "2015-09-14 17:55:24");
	    gpsTrack.add(652228.688922146, 6776194.82781158, 10.944, "2015-09-14 17:55:45");
	    gpsTrack.add(652222.677669642, 6776309.66666998, 20.7, "2015-09-14 17:56:00");
	    gpsTrack.add(652207.983496859, 6776422.34373976, 22.716, "2015-09-14 17:56:11");
	    gpsTrack.add(652197.630784213, 6776487.60701467, 15.336, "2015-09-14 17:56:21");
	    gpsTrack.add(652315.184166491, 6776513.38795585, 23.904, "2015-09-14 17:56:33");
	    gpsTrack.add(652358.264809429, 6776516.45282843, 25.74, "2015-09-14 17:56:35");
	    gpsTrack.add(652466.133396006, 6776522.58257708, 13.932, "2015-09-14 17:56:47");
	    gpsTrack.add(652568.547327536, 6776527.27003505, 18.36, "2015-09-14 17:57:00");
	    gpsTrack.add(652674.1895243, 6776532.85893079, 19.764, "2015-09-14 17:57:12");
	    gpsTrack.add(652733.30017391, 6776546.560756, 17.964, "2015-09-14 17:57:25");
	    

	    for (AggregationInterface aggregeerMethode: aggregationClasses) {
	    	aggregeerMethode.add(gpsTrack);
	    }
	    
	    for (AggregationInterface aggregeerMethode: aggregationClasses) {
	    	aggregeerMethode.exit();
	    }
	    
	}

}
