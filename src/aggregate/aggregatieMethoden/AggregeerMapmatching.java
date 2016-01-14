package aggregate.aggregatieMethoden;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aggregate.BikePrintTabelSchrijver;
import nl.bikeprint.trackaggregate.aggregeerMapmatching.DLink;
import nl.bikeprint.trackaggregate.aggregeerMapmatching.Dijkstra2;
import nl.bikeprint.trackaggregate.aggregeerMapmatching.KnoopAttributen;
import nl.bikeprint.trackaggregate.aggregeerMapmatching.LinkAttributen;
import nl.bikeprint.trackaggregate.aggregeerMapmatching.Punt;
import nl.bikeprint.trackaggregate.aggregeerMapmatching.RouteAntwoord;
import nl.bikeprint.trackaggregate.algemeen.BasicWFSReader;
import nl.bikeprint.trackaggregate.algemeen.Constanten;
import nl.bikeprint.trackaggregate.algemeen.KolomType;
import nl.bikeprint.trackaggregate.algemeen.Tools;
import nl.bikeprint.trackaggregate.shared.AggregeerInterface;
import nl.bikeprint.trackaggregate.shared.DatabaseSchrijverInterface;
import nl.bikeprint.trackaggregate.shared.GPSTrack;

public class AggregeerMapmatching implements AggregeerInterface {

	private Dijkstra2 dijkstra;
	LinkAttributen[] linkAttributen = null;
	KnoopAttributen[] knoopAttributen = null;
	private DatabaseSchrijverInterface databaseSchrijver;

	@Override
	public void init(DatabaseSchrijverInterface databaseSchrijver, String bbox) {
        this.databaseSchrijver = databaseSchrijver;
		String getCapabilities = 
	            "http://hez04.goudmap.info:8080/geoserver/gc/ows?service=WFS";
        try {
			BasicWFSReader reader = new BasicWFSReader(new URL(getCapabilities));
			reader.getCapabilities();
			Document response;
            
			response = reader.getFeatureBasic("gc:fietsnetwerk", 0, bbox);
			System.out.println(response);
			dijkstra = new Dijkstra2();

	    	NodeList nl = response.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				NodeList nl2 = node.getChildNodes();
				for (int t = 0; t < nl2.getLength(); t++) {
					Node node2 = nl2.item(t);
					NodeList nl3 = node2.getChildNodes();
					for (int t3 = 0; t3 < nl3.getLength(); t3++) {
						Node node3 = nl3.item(t3);					
						NodeList nl4 = node3.getChildNodes();
						
						String wkt = "";
						int nodea = -1;
						int nodeb = -1;
						float x1 = 0;
						float x2 = 0;
						float y1 = 0;
						float y2= 0;
						float lengte = 0;
						int linknummer = -1;
						
						for (int t4 = 0; t4 < nl4.getLength(); t4++) {
							Node node4 = nl4.item(t4);
				 			if (node4.getNodeName().equals("gc:wkt")) {
				 				wkt = node4.getTextContent(); 
				 			}
				 			if (node4.getNodeName().equals("gc:nodea")) {
				 				nodea = Tools.maakInt(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:nodeb")) {
				 				nodeb = Tools.maakInt(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:linknummer")) {
				 				linknummer = Tools.maakInt(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:x1")) {
				 				x1 = Tools.maakFloat(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:x2")) {
				 				x2 = Tools.maakFloat(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:y1")) {
				 				y1 = Tools.maakFloat(node4.getTextContent());
				 			}
				 			if (node4.getNodeName().equals("gc:y2")) {
				 				y2 = Tools.maakFloat(node4.getTextContent());
				 			}				 			

				 			if (node4.getNodeName().equals("gc:lengte")) {
				 				lengte = Tools.maakFloat(node4.getTextContent());
				 			}				 							 				
						}
						if (linknummer > 0) {
		                    dijkstra.toevoegenLink(nodea, nodeb, linknummer, x1, y1, x2, y2, lengte, wkt);
		                    System.out.println(nodea + "," + nodeb + "," + linknummer);
						}
		           	}
				}
			}
			System.err.println("Aantal " + dijkstra.aantKnopen);
			linkAttributen = new LinkAttributen[dijkstra.links.size()];
			knoopAttributen = new KnoopAttributen[dijkstra.links.size() * 2];	
		} catch (MalformedURLException e) {
			System.err.println("URL " + getCapabilities + "is geen geldige URL.");
			e.printStackTrace();
		}
	}

	@Override
	public void add(GPSTrack gpsTrack) {
		int linkIndex;
		int linkNummer; 
        if ((gpsTrack.getAantal() == 0) ||  (gpsTrack.getModality() != 2)) {
        	return;        	
        }
        RouteAntwoord[] links = match(gpsTrack);
		RouteAntwoord gematchteRoute = links[0];
		double trackLengte = gpsTrack.getLengte() / 1000;
		double trackVerhoudingHemelsbreed = trackLengte / (gpsTrack.getHemelsbredeLengte() / 1000);
		double trackTijd = gpsTrack.getTotaleTijd() / 1000 / 3600;
        if (trackTijd == 0) return;
	//	StringBuilder query = new StringBuilder();
		//query.append("INSERT INTO " + LoginDatabase.getTabelNaam("gps_match", dataset) + "  (routeid, linknummer, richting, snelheid, uur) VALUES ");
        	 
		int routeID = gpsTrack.getRouteID();			
		int uur = gpsTrack.getBeginUur();
		//ArrayList<LinkAttributen> linkAttributen = new ArrayList<LinkAttributen>(); 
        for (int i = 0; i < gematchteRoute.getLinkList().length; i++) {
			if (i > 0) {
      //  		query.append(",");
        	}	
        	linkNummer = gematchteRoute.getLinkList()[i];
        	linkIndex = dijkstra.linknummersReverse.get(Math.abs(linkNummer));

        	if (linkAttributen[linkIndex] == null) {
        		linkAttributen[linkIndex] = new LinkAttributen();
        	}
        	addIntensiteit(linkIndex);
        	addVerhoudingHemelsbreed(linkIndex, trackVerhoudingHemelsbreed);
        	
        	DLink dLink = dijkstra.links.get(Math.abs(linkNummer));
        	double snelheid = addSnelheid(linkIndex, linkNummer, dLink, gpsTrack, trackLengte / trackTijd);
        	
        //	query.append(" (" + routeID + "," + 
       // 	    Math.abs(gematchteRoute.getLinkList()[i]) + "," + 
      //  		boolean2String(gematchteRoute.getLinkList()[i] > 0) + "," + 
      //  	    snelheid + "," + 
      //  		uur + ")");
        }
       // LoginDatabase.execUpdate(query.toString());
	 

		RouteAntwoord kortsteRoute = links[1];
        for (int i = 0; i < kortsteRoute.getLinkList().length; i++) {
        	linkNummer = kortsteRoute.getLinkList()[i];
        	linkIndex = dijkstra.linknummersReverse.get(Math.abs(linkNummer));

        	if (linkAttributen[linkIndex] == null) {
        		linkAttributen[linkIndex] = new LinkAttributen();
        	}
        	addIntensiteitKortsteRoute(linkIndex);
        }
	}

	@Override
	public void schrijfNaarDatabase() {
		schrijfFietsnet();
		schrijfLinkattributen();		
	}
	/*
	private RouteAntwoord[] match(GPSTrack gpsTrack, String dataset) {
		RouteAntwoord route;
		int aantal = gpsTrack.getAantal();
	
		ArrayList<Integer> beginNodes = dijkstra.getNodes(gpsTrack.getNode(0).getX(), gpsTrack.getNode(0).getY(), 1000);
		ArrayList<Integer> endNodes   = dijkstra.getNodes(gpsTrack.getNode(aantal-1).getX(), gpsTrack.getNode(aantal-1).getY(), 1000);
		
		double afwijking;
		double minAfwijking = 999999999;
		int minI = 0, minJ = 0;
		RouteAntwoord minRoute = new RouteAntwoord(new int[0], 0, 0);
		RouteAntwoord kortsteRoute;
		
		dijkstra.resetAfstanden();
		for (int i = 0; i < beginNodes.size(); i++) {

// hier een keer route bepalen ipv endNodes.size() keer
			for (int j = 0; j < endNodes.size(); j++) {
		        route = dijkstra.maakRoute(beginNodes.get(i), endNodes.get(j), gpsTrack);
		        afwijking = matchKwaliteit(route, gpsTrack);
		        if (afwijking < minAfwijking) {
		        	minAfwijking = afwijking;
		        	minRoute = route;
		        	minI = i;
		        	minJ = j;
		        }
			}    
		}
		if (beginNodes.isEmpty() || endNodes.isEmpty()) return null;
		kortsteRoute = dijkstra.maakRoute(beginNodes.get(minI), endNodes.get(minJ), null);
		
        if ((minRoute.getLengte() > 5 * kortsteRoute.getLengte()) || (minRoute.getLengte() < 500)) {
        	return null;
        }
	    return new RouteAntwoord[]{minRoute,kortsteRoute};
	}
	*/
	private void schrijfLinkattributen() {
		BikePrintTabelSchrijver tabel = new BikePrintTabelSchrijver("linkattributen");
		tabel.addKolom("linknummer", KolomType.INTEGER);
		tabel.addKolom("snelheid", KolomType.FLOAT);
		tabel.addKolom("snelheid_relatief", KolomType.FLOAT);
		tabel.addKolom("intensiteit", KolomType.INTEGER);
		tabel.addKolom("intensiteit_kortsteroute", KolomType.INTEGER);
		tabel.addKolom("verhouding_hemelsbreed", KolomType.INTEGER);
		databaseSchrijver.maakTabel(tabel);
		
		ArrayList<ArrayList<String>> waardes = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < linkAttributen.length; i++) {
			ArrayList<String> waarde = new ArrayList<String>();
			if (linkAttributen[i] != null) {
				waarde.add(dijkstra.linknummers.get(i) + "");
				waarde.add(Tools.maakNaN0(linkAttributen[i].snelheidSum / linkAttributen[i].getSnelheidN()) + "");
				waarde.add(Tools.maakNaN0(linkAttributen[i].getSnelheidSumRelatief() / linkAttributen[i].getSnelheidN()) + "");
				waarde.add(linkAttributen[i].aantal + "");
				waarde.add(linkAttributen[i].aantalKortsteRoute + "");
				waarde.add(Tools.maakNaN0(linkAttributen[i].verhoudingHemelsbreed / linkAttributen[i].aantal) + "");
				waardes.add(waarde);			
			} else {
				System.out.println("linkattributen["+ i +"] is NULL");
			}
		} 
		databaseSchrijver.schrijfRecords(tabel, waardes);
	}

	private void schrijfFietsnet() {
		BikePrintTabelSchrijver tabel = new BikePrintTabelSchrijver("fietsnet");
		tabel.addKolom("linknummer", KolomType.INTEGER);
		tabel.addKolom("nodea", KolomType.INTEGER);
		tabel.addKolom("nodeb", KolomType.INTEGER);
		tabel.addKolom("geometry", KolomType.GEOMETRY);
		databaseSchrijver.maakTabel(tabel);
		
		ArrayList<ArrayList<String>> waardes = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < dijkstra.aantKnopen; i++) {
			int linknummer = dijkstra.getLinknummer(i);
			DLink link = dijkstra.getLink(linknummer);
			if (link != null) {
				ArrayList<String> waarde = new ArrayList<String>();
				waarde.add(linknummer + "");
				waarde.add(link.nodea + "");
				waarde.add(link.nodeb + "");
				waarde.add(dijkstra.getWkt(linknummer));
				waardes.add(waarde);
			}
		}
		databaseSchrijver.schrijfRecords(tabel, waardes);
	}

	private RouteAntwoord[] match(GPSTrack gpsTrack) {
		RouteAntwoord route;
		int aantal = gpsTrack.getAantal();
		ArrayList<Integer> beginNodes = dijkstra.getNodes(gpsTrack.getNode(0).getX(), gpsTrack.getNode(0).getY(), 1000);
		ArrayList<Integer> endNodes   = dijkstra.getNodes(gpsTrack.getNode(aantal-1).getX(), gpsTrack.getNode(aantal-1).getY(), 1000);
		
		double afwijking;
		double minAfwijking = 999999999;
		int minI = 0, minJ = 0;
		RouteAntwoord minRoute = new RouteAntwoord(new int[0], 0, 0);
		RouteAntwoord kortsteRoute;
		
		dijkstra.resetAfstanden();
		for (int i = 0; i < beginNodes.size(); i++) {
// hier een keer route bepalen ipv endNodes.size() keer
			for (int j = 0; j < endNodes.size(); j++) {

		        route = dijkstra.maakRoute(beginNodes.get(i), endNodes.get(j), gpsTrack);
		        afwijking = matchKwaliteit(route, gpsTrack);
		        if (afwijking < minAfwijking) {
		        	minAfwijking = afwijking;
		        	minRoute = route;
		        	minI = i;
		        	minJ = j;
		        }
			}    
		}
		if (beginNodes.isEmpty() || endNodes.isEmpty()) return null;
		kortsteRoute = dijkstra.maakRoute(beginNodes.get(minI), endNodes.get(minJ), null);
		
        if ((minRoute.getLengte() > 5 * kortsteRoute.getLengte()) || (minRoute.getLengte() < 500)) {
        	return null;
        }
	    return new RouteAntwoord[]{minRoute,kortsteRoute};
	}

	private double matchKwaliteit(RouteAntwoord route, GPSTrack gpsTrack) {
		return route.getKosten();
	}
	private void addIntensiteit(int linkIndex) {
		linkAttributen[linkIndex].incAantal();
	}

	private void addIntensiteitKortsteRoute(int linkIndex) {
		linkAttributen[linkIndex].incAantalKortsteRoute();
	}

	private void addVerhoudingHemelsbreed(int linkIndex, double trackVerhoudingHemelsbreed) {
		linkAttributen[linkIndex].verhoudingHemelsbreed += trackVerhoudingHemelsbreed;
	}	

private double addSnelheid(int linkIndex, int linkNummer, DLink dLink, GPSTrack gpsTrack, double totaleSnelheid) {
		
		boolean gedraaid = false;
		
		Punt punt1 = new Punt(dLink.getX1(), dLink.getY1());
		Punt punt2 = new Punt(dLink.getX2(), dLink.getY2());

		double begin = gpsTrack.getTijdAt(punt1);
		double eind  = gpsTrack.getTijdAt(punt2);
		 
		if (begin > eind) { // swap
			double tussen = eind;
			eind = begin;
			begin = tussen;
			gedraaid = true;
		}
 
		if (eind > begin) {
            double km;
          //  km = (int)dLink.lengte;
            km = gpsTrack.getLengteTussen(punt1, punt2) / Constanten.GOOGLE_FACTOR;
            double uur = (eind - begin) / 1000 / 3600;
       //     if (km/uur>30) {
       //         System.out.println("te hoge snelheid: " + km/uur);
       //     }    
            double percentiel10 = gpsTrack.getPercentilSnelheid(10);
            if (percentiel10 < 5) percentiel10 = 5;
            linkAttributen[linkIndex].snelheidSum += km / uur;
            //linkAttributen[linkIndex].snelheidSumRelatief += km / uur / totaleSnelheid;            
            linkAttributen[linkIndex].setSnelheidSumRelatief(linkAttributen[linkIndex]
					.getSnelheidSumRelatief() + Math.min(1, Math.min(25,km / uur) / Math.min(25, percentiel10)));
            linkAttributen[linkIndex].setSnelheidN(linkAttributen[linkIndex].getSnelheidN() + 1);
 
            
            addKnoopTijd(linkIndex, dLink, gpsTrack, gedraaid, km / uur);
            
            return km / uur;
		} else {
       //     System.out.println("ongeldige snelheid");
            return 0;
		}
		
	}

	private void addKnoopTijd(int linkIndex, DLink dLink, GPSTrack gpsTrack, boolean gedraaid, double linkSnelheid) {
	
		int knoopnummer;
		Punt knoopPunt;
		if (gedraaid) {
		    knoopnummer = dLink.nodeb;
		    knoopPunt = new Punt(dLink.getX2(), dLink.getY2());
		} else {
			knoopnummer = dLink.nodea;
			knoopPunt = new Punt(dLink.getX1(), dLink.getY1());;
		}
	
		double loopLengte = gpsTrack.getLine().getAlong(knoopPunt);
	
	    Punt puntPlus50   = gpsTrack.getLine().along(loopLengte + ( 50D / 1000D * Constanten.GOOGLE_FACTOR), false);
	    Punt puntMinus50  = gpsTrack.getLine().along(loopLengte - ( 50D / 1000D * Constanten.GOOGLE_FACTOR), false);
	    Punt puntPlus100  = gpsTrack.getLine().along(loopLengte + (100D / 1000D * Constanten.GOOGLE_FACTOR), false);
	    Punt puntMinus100 = gpsTrack.getLine().along(loopLengte - (100D / 1000D * Constanten.GOOGLE_FACTOR), false);
	    
		double tijdKnooppunt = (gpsTrack.getTijdAt(puntPlus50 ) - gpsTrack.getTijdAt(puntMinus50 )) / 1000;
		double tijdVoor =      (gpsTrack.getTijdAt(puntMinus50) - gpsTrack.getTijdAt(puntMinus100)) / 1000;
		double tijdNa =        (gpsTrack.getTijdAt(puntPlus100) - gpsTrack.getTijdAt(puntPlus50  )) / 1000;
		//double fiktieveTijd = 0.2 / linkSnelheid / 1000 / 3600;
	    if (knoopAttributen[knoopnummer] == null) {
			knoopAttributen[knoopnummer] = new KnoopAttributen(knoopPunt.x,knoopPunt.y, dijkstra.knopenIndexReverse.get(knoopnummer));
		}
	    knoopAttributen[knoopnummer].tijd += tijdKnooppunt - 2 * Math.min(15, Math.min(tijdVoor, tijdNa));
	    knoopAttributen[knoopnummer].tijdOp += tijdKnooppunt;
	    knoopAttributen[knoopnummer].tijdNa += tijdNa;
	    knoopAttributen[knoopnummer].tijdVoor += tijdVoor;
		knoopAttributen[knoopnummer].aantal++;
	
	}
}
