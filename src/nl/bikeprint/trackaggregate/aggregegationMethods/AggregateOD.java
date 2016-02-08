package nl.bikeprint.trackaggregate.aggregegationMethods;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nl.bikeprint.trackaggregate.aggregegationMethods.od.AggregateODGebieden;
import nl.bikeprint.trackaggregate.aggregegationMethods.od.AggregateODMatrix;
import nl.bikeprint.trackaggregate.aggregegationMethods.od.Gebied;
import nl.bikeprint.trackaggregate.general.BasicWFSReader;
import nl.bikeprint.trackaggregate.general.ColumnType;
import nl.bikeprint.trackaggregate.general.Tools;
import nl.bikeprint.trackaggregate.shared.AggregationInterface;
import nl.bikeprint.trackaggregate.shared.DatabaseWriterInterface;
import nl.bikeprint.trackaggregate.shared.GPSPoint;
import nl.bikeprint.trackaggregate.shared.GPSTrack;
import nl.bikeprint.trackaggregate.shared.TableWriter;

public class AggregateOD implements AggregationInterface {
	
	public static final int MIN_TRIPS_PRIVACY = 0;
	
	private DatabaseWriterInterface databaseWriter;
	private AggregateODMatrix odMatrix = null;
	private AggregateODGebieden gebieden = new AggregateODGebieden();
	
	@Override
	public void init(DatabaseWriterInterface databaseWriter, String bbox) {
		getAreasFromWFS(bbox);
		odMatrix = new AggregateODMatrix(gebieden.getNumberOfAreasPerLevel());
	    this.databaseWriter = databaseWriter;
	}

	@Override
	public void add(GPSTrack gpsTrack) {
		GPSPoint startPoint, endPoint;
		
		startPoint = gpsTrack.getNode(0);
		endPoint = gpsTrack.getNode(gpsTrack.getAantal()-1);

		for (int level = 1; level < 5; level ++) {
		    int o = gebieden.getGebiedIndex(startPoint.getX(), startPoint.getY(), level);
		    int d = gebieden.getGebiedIndex(endPoint.getX(), endPoint.getY(), level);
		    if (o >= 0 && d >= 0) {
		    	odMatrix.addRitten(level, o, d, 1);
		    }
		}
	}

	@Override
	public void exit() {
		writeODMatrix();
		writeAreas();
	}
	
	private void writeODMatrix() {
	    TableWriter table = new TableWriter("od");
		table.addColumn("level", ColumnType.INTEGER);
		table.addColumn("o", ColumnType.INTEGER);
		table.addColumn("d", ColumnType.INTEGER);
		table.addColumn("trips", ColumnType.DOUBLE);
		databaseWriter.createTable(table);
		
		ArrayList<ArrayList<String>> waardes = new ArrayList<ArrayList<String>>();
		for (int level = 1; level < 5; level++) {
			int num = gebieden.getNumberOfAreas(level);
			for (int o = 0; o < num; o++) {
				for (int d = 0; d < num; d++) {
				    double trips = odMatrix.getTrips(level, o, d);
					if (trips > MIN_TRIPS_PRIVACY) {
						ArrayList<String> waarde = new ArrayList<String>();
						waarde.add(Tools.makeNaN0(level));
						waarde.add(Tools.makeNaN0(o));
						waarde.add(Tools.makeNaN0(d));
						waarde.add(Tools.makeNaN0(trips));
						waardes.add(waarde);
					}
				}
			}
		}
		databaseWriter.writeRecords(table, waardes);
	}

	private void writeAreas() {
		TableWriter table = new TableWriter("areas");
		table.addColumn("level", ColumnType.INTEGER);
		table.addColumn("id", ColumnType.INTEGER);
		table.addColumn("name", ColumnType.STRING);
		table.addColumn("geometry", ColumnType.GEOMETRY);
		databaseWriter.createTable(table);
		
		ArrayList<ArrayList<String>> waardes = new ArrayList<ArrayList<String>>();
		for (int level = 1; level < 5; level++) {
			int num = gebieden.getNumberOfAreas(level);
			for (int i = 0; i < num; i++) {
				Gebied area = gebieden.getGebied(level, i);
				ArrayList<String> waarde = new ArrayList<String>();
				waarde.add(Tools.makeNaN0(level));
				waarde.add(Tools.makeNaN0(i));
				waarde.add(area.getName());
				waarde.add(area.getWKT());
				waardes.add(waarde);
			}
		}
		databaseWriter.writeRecords(table, waardes);
	}

	private void getAreasFromWFS(String bbox) {
        String getCapabilities = "http://hez04.goudmap.info:8080/geoserver/gc/ows?service=WFS";
        try {
			BasicWFSReader reader = new BasicWFSReader(new URL(getCapabilities));
			reader.getCapabilities();
			Document response = reader.getFeatureBasic("gc:gebieden", 0, bbox);
 
	    	NodeList nl = response.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				NodeList nl2 = node.getChildNodes();
				for (int t = 0; t < nl2.getLength(); t++) {
					Node node2 = nl2.item(t);
					if (!node2.getNodeName().equals("gml:featureMember")) continue;
					NodeList nl3 = node2.getChildNodes();
					for (int t3 = 0; t3 < nl3.getLength(); t3++) {
						Node node3 = nl3.item(t3);
						NodeList nl4 = node3.getChildNodes();
						
						String wkt = "";
						int id = -1;
						int level = -1;
						String l0 = "";
						String l1 = "";
						String l2 = "";
						String l3= "";
						String l4= "";
						
						for (int t4 = 0; t4 < nl4.getLength(); t4++) {
							Node node4 = nl4.item(t4);
				 			
				 			if (node4.getNodeName().equals("gc:geo")) {
				 				NodeList nl5 = node4.getChildNodes();
				 				Node node5 = nl5.item(0);
				 				NodeList nl6 = node5.getChildNodes();
				 				Node node6 = nl6.item(0);
				 				NodeList nl7 = node6.getChildNodes();
				 				Node node7 = nl7.item(0);
				 				NodeList nl8 = node7.getChildNodes();
				 				Node node8 = nl8.item(0);
				 				NodeList nl9 = node8.getChildNodes();
				 				Node node9 = nl9.item(0);
				 				wkt = makeWKT(node9.getTextContent()); 
				 			}
				 			if (node4.getNodeName().equals("gc:id")) {
				 				id = Tools.makeInt(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:level")) {
				 				level = Tools.makeInt(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:l0")) {
				 				l0 = node4.getTextContent();
				 			}
				 			if (node4.getNodeName().equals("gc:l1")) {
				 				l1 = node4.getTextContent();
				 			}
				 			if (node4.getNodeName().equals("gc:l2")) {
				 				l2 = node4.getTextContent();
				 			}
				 			if (node4.getNodeName().equals("gc:l3")) {
				 				l3 = node4.getTextContent();
				 			}
				 			if (node4.getNodeName().equals("gc:l4")) {
				 				l4 = node4.getTextContent();
				 			}
				 		}
						System.out.println("addGebied(" + level + "," + l0+l1+l2+l3+l4+")");
						gebieden.addGebied(id, level, wkt, l0, l1, l2, l3, l4);
		           	}
				}
			}
		} catch (MalformedURLException e) {
			System.err.println("URL " + getCapabilities + "is geen geldige URL.");
			e.printStackTrace();
		}

	}

	private String makeWKT(String text) {
		String t1 = text.replace(",","#");
		t1 = t1.replace(" ",",");
		t1 = t1.replace("#"," ");
		return "POLYGON((" + t1 + "))";
	}

}
