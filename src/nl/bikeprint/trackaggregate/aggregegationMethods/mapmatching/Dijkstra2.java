package nl.bikeprint.trackaggregate.aggregegationMethods.mapmatching;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.bikeprint.trackaggregate.general.ArrayTools;
import nl.bikeprint.trackaggregate.general.GoudmapLine;
import nl.bikeprint.trackaggregate.general.SpatialIndexLine;
import nl.bikeprint.trackaggregate.shared.GPSTrack;

import com.infomatiq.jsi.Point;

 

public class Dijkstra2 {

    public int aantKnopen = -1;    
    public HashMap<Integer, DNode> knopen = new HashMap<Integer, DNode>();
 //  public HashMap<Integer,GoudmapLine> goudmapLines = new HashMap<Integer,GoudmapLine>();
    public HashMap<Integer,DLink> links = new HashMap<Integer,DLink>();
	public HashMap<Integer, Integer> linknummers = new HashMap<Integer,Integer>();
	public HashMap<Integer, Integer> linknummersReverse = new HashMap<Integer,Integer>();
	   
    int maxWeerstand = 2;
    public HashMap<Integer, Integer> knopenIndex;
    public HashMap<Integer, Integer> knopenIndexReverse;
	double WACHTEN_SECONDEN = 30;
    HashMap<Integer,Double> afstandWeerstand = new HashMap<Integer,Double>();
    SpatialIndexLine spatialIndex = new SpatialIndexLine();
    
    public Dijkstra2() {
    	knopenIndex = new HashMap<Integer, Integer>();
    	knopenIndexReverse = new HashMap<Integer, Integer>();
    	aantKnopen = 0; 
    }
    
	public void toevoegenLink(int nodea, int nodeb, int linknummer, float x1, float y1, float x2, float y2, float lengte, String wkt) {	
        toevoegenKnoop(nodea, (int)x1, (int)y1);
        toevoegenKnoop(nodeb, (int)x2, (int)y2);
		toevoegenLink(nodea, nodeb, lengte,  linknummer, (int)x1, (int)y1, (int)x2, (int)y2);
		toevoegenLink(nodeb, nodea, lengte, -linknummer, 0, 0, 0, 0);
        spatialIndex.add(new GoudmapLine(wkt), linknummer);
	}    
	
	public void initNetwerk(String dataset) {
 /*
		long now = System.currentTimeMillis();
    	while (aantKnopen == 0) { // andere thread bezig met inlezen
	    	if (System.currentTimeMillis() - now > WACHTEN_SECONDEN * 1000) {
				return;
			}
	        try {
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	
    	if (aantKnopen >= 0) { // niets te doen als al ingelezen
	    	return;
	    }
	    aantKnopen = 0; // voorkom dat tijdens inlezen tweede initNetwerk wordt aangeroepen
	   
    	ResultSet rs = null;
	    int t;
	    int  nodea, nodeb, x1, y1, x2, y2;
	    double lengte;
	    int linknummer;
 
	    String query; 
	    
	    query = "SELECT " +
	            "     ST_AsText(geometry) AS wkt, " +
	    		"     source as nodea," +
	    		"     target as nodeb," +
	    		"     ST_X(ST_StartPoint(geometry)) as x1," +
	    		"     ST_Y(ST_StartPoint(geometry)) as y1," +
	    		"     ST_X(ST_EndPoint(geometry)) as x2," +
	    		"     ST_Y(ST_EndPoint(geometry)) as y2," +
	    		"     ST_Length(ST_Transform(ST_SetSRID(geometry,900913),28992)) as lengte," +
	    		"     linknummer " +
	    		" FROM " + LoginDatabase.getTabelNaam("fietsnet", dataset) +
	    		" ORDER BY ST_Length(geometry) DESC;"; 
 
	    try {
	   
	    	knopenIndex = new HashMap<Integer, Integer>();
	    	knopenIndexReverse = new HashMap<Integer, Integer>();
			rs = LoginDatabase.execQuery(query);
			
			// pass 1: aanmaken unieke knoopnummers
			aantKnopen = 0;
	    	while (rs.next()) {
	    		nodea = getNodeA(rs);
	    		nodeb = getNodeB(rs);
	    	}	

	    	// pass 2: maken knoop-objecten met x y
	//	    knopen = new DKnoop[aantKnopen];
		    rs.beforeFirst();
		    
		    while (rs.next()) {
	    		nodea = getNodeA(rs);
	    		nodeb = getNodeB(rs);
	    		x1 = rs.getInt("x1");
	    		y1 = rs.getInt("y1");
	    		x2 = rs.getInt("x2");
	    		y2 = rs.getInt("y2");
                toevoegenKnoop(nodea, x1, y1);
                toevoegenKnoop(nodeb, x2, y2);
		    }    
		    rs.beforeFirst();
		    
		    while (rs.next()) {		    
	    		nodea = getNodeA(rs);
	    		nodeb = getNodeB(rs);
	    		lengte = rs.getDouble("lengte");
	    		linknummer = rs.getInt("linknummer");
	    		toevoegenLink(nodea, nodeb, lengte,  linknummer, rs.getInt("x1"), rs.getInt("y1"), rs.getInt("x2"), rs.getInt("y2") );
	    		toevoegenLink(nodeb, nodea, lengte, -linknummer, 0, 0, 0, 0);
		    	goudmapLines.put(linknummer, new GoudmapLine(rs.getString("wkt")));
	    	}
	        rs.close();
	        
        }	catch (SQLException e) {
				e.printStackTrace();
        }	
         
        String ks = ".naar.> ";
        for (t = 0; t < knopen[5].naar.length; t++) {
         	ks += " " + knopen[5].naar[t];
        }
        System.out.println(ks);     
	*/
	}


	private int getNodeA(ResultSet rs) throws SQLException {
		int nodea = rs.getInt("nodea");
		if (nodea >= 0) return nodea;
		int x1 = rs.getInt("x1");
		int y1 = rs.getInt("y1");
		return x1 + y1 * 100000;		
	}
	
	private int getNodeB(ResultSet rs) throws SQLException {
		int nodeb = rs.getInt("nodeb");
		if (nodeb >= 0) return nodeb;
		int x2 = rs.getInt("x2");
		int y2 = rs.getInt("y2");
		return x2 + y2 * 100000;		
	}

	private void toevoegenLink(int nodea, int nodeb, double lengte, int linknr, int x1, int y1, int x2, int y2) {
    	int idxA = knopenIndex.get(nodea);
    	int idxB = knopenIndex.get(nodeb);

       	knopen.get(idxA).aantal++;        	
       	knopen.get(idxA).naar = ArrayTools.add(
       			knopen.get(idxA).naar, 
       			idxB, (int)(lengte * 1000), linknr
       	);
       	if (linknr > 0) {
       	    DLink alink = new DLink(x1, y1, x2, y2, idxA, idxB, lengte, linknr);
       	    links.put(linknr, alink);
       	    linknummers.put(links.size(),linknr);
       	    linknummersReverse.put(linknr,links.size());
       	}           	        
	}

	public void toevoegenKnoop(int node, int x, int y) {

		if (!knopenIndex.containsKey(node)) {
			knopenIndexReverse.put(aantKnopen, node);
			knopenIndex.put(node, aantKnopen++);	    			
		}
	
		int i = knopenIndex.get(node);
		if (knopen.get(i) == null) {			
	        knopen.put(i, new DNode());
	        knopen.get(i).x = x;
	        knopen.get(i).y = y;
	        knopen.get(i).databaseKnoopnummer = node;	        
	        knopen.get(i).aantal = 0;
	        knopen.get(i).naar = new int[0];
		}	
	}


 // ##############################################################################   
    public RouteAnswer maakRoute(int beg, int eind, GPSTrack gpsTrack)
 // ##############################################################################

 // gemodificeerde implementatie Routezoekalgoritme van Dijkstra
 // breekt af als maxkosten bereikt of knopen in eindlist alle gevuld
 // om route tussen 2 knopen te vinden, beg=beginknoop, eindlist={eindknoop}
 // aansluitend staat route en kosten in attributen van Eindknoop
 // als meerdere routes AB, AC, AD gezocht worden, is het sneller,
 // beginlist = A, Eindlist = {B,C,D} te gebruiken. Route AB staat dan in knoop B enz.
    
    {  
      int t;
      int kleinstKnoop, kleinstKost, kleinst_t, aKosten, dezeKosten, eindlistminkosten;
      double afstandKosten;
      DNode kleinstKnoopObject;
      ArrayList<Integer> vooraan; // knopen die op de grens van het verkende gebied liggen
      HashMap<Integer,Integer> kosten = new HashMap<Integer,Integer>();
      HashMap<Integer,Integer> vandaanKnoop = new HashMap<Integer,Integer>();
      HashMap<Integer,Integer> vandaanLink = new HashMap<Integer,Integer>();
      boolean klaar;
      int BegX, BegY, EindX, EindY;
      int MinStraal = 10000;//binnen straal van ten minste .../1.68km zoeken.
      int MaxStraal;
      int aNaarKnoop, aKnoopKosten;
      int[] linkList;
      int tel=0;
          
    //  initNetwerk(dataset);
      
      // zet coordinates voor check op afstand
      BegX = knopen.get(beg).x;
      BegY = knopen.get(beg).y;
      EindX = knopen.get(eind).x;
      EindY = knopen.get(eind).y;
      int straal = 0;
      MaxStraal = straal != 0 ? straal : distance2Points(BegX, BegY, EindX, EindY) / 3;
      if(MaxStraal < MinStraal) MaxStraal = MinStraal;
      
 // zet status voor startknoop op
      kosten.put(beg, 0);
      vooraan = new ArrayList<Integer>();
      vooraan.add(beg);
          
      while (true) {

 // zoek knoop met kleinste kosten die vooran is
          kleinstKnoop = -1;
          kleinst_t    = -1;
          kleinstKost  = 2147483647;

          klaar = true;
          eindlistminkosten = 715827882;
    
          if (!kosten.containsKey(eind)) {
              klaar = false;
          } else { 
              eindlistminkosten = kosten.get(eind);        
          }

          for (int item: vooraan) {
        	  if (eind == item) {
        		  klaar = false;
        	  }
        	  int itemKosten = getIntegerWaarde(kosten, item, 2147483647);
        	  if (itemKosten < kleinstKost) {
        		  kleinstKnoop = item;
        		  kleinstKost = itemKosten;
        		  kleinst_t = item;
        	  }
          }
          
 // als er geen knoop meer is die kleiner is dan maxkosten, of
 // als er alle eindknopen al afsluitend zijn bekeken, dan klaar       
          if ((kleinstKnoop == -1) || (kleinstKost > eindlistminkosten*2) || klaar) { 
              break;
          }
          tel++;
          
 // Bekijk alle naar-knopen vanuit deze knoop
        
          kleinstKnoopObject = knopen.get(kleinstKnoop);
          
          if ( gpsTrack != null ||        		  
        		  ( distance2Points(BegX, BegY, kleinstKnoopObject.x, kleinstKnoopObject.y) < MaxStraal ||
                  distancePointLine(BegX, BegY, EindX, EindY, kleinstKnoopObject.x, kleinstKnoopObject.y) < MaxStraal ||
                  distance2Points(EindX, EindY, kleinstKnoopObject.x, kleinstKnoopObject.y) < MaxStraal
               )  
             ) {    
        	  
         
              aKnoopKosten = getIntegerWaarde(kosten,kleinstKnoop,2147483647);
           
              for (t = 0; t < kleinstKnoopObject.aantal; t ++) {            	  
                  if (gpsTrack != null) {
            	      afstandKosten = getAfstandLinkGPS(
  						  kleinstKnoopObject.naar[t * 3 + 2],
  						  gpsTrack,
  						  afstandWeerstand
  						  );
                  } else {
                	  afstandKosten = 1;
                  }
                                      
            	  if (afstandKosten > 999999) continue;
          		  dezeKosten = (int) (kleinstKnoopObject.naar[t * 3 + 1] * afstandKosten); 
          				  
        		  aKosten = aKnoopKosten + dezeKosten;

            	  aNaarKnoop = kleinstKnoopObject.naar[t * 3];
            
 // als er een nieuwe (of eerste) beste route gevonden, wegschrijven
            	  int kostenANaarKnoop = getIntegerWaarde(kosten,aNaarKnoop,2147483647);
            	  if (aKosten < kostenANaarKnoop) {
                	  kosten.put(aNaarKnoop, aKosten);
                      vandaanKnoop.put(aNaarKnoop, kleinstKnoop);
                      vandaanLink.put(aNaarKnoop, kleinstKnoopObject.naar[t * 3 + 2]);
                      if (!vooraan.contains(aNaarKnoop)) {
                    	  vooraan.add(aNaarKnoop);
                      }   
  	              }  
              }
          } 
          vooraan.remove((Integer)kleinst_t);
      }

      tel = 0;
      t = eind;
      while (t != beg) {             
          tel++;
          
          t = getIntegerWaarde(vandaanKnoop,t,-1);
          if (t == -1) break;   
      }    
      linkList = new int[tel];

      tel = 0;
      t = eind;

      double lengte = 0;
      while (t != beg) {
    	 
    	  int linkid = getIntegerWaarde(vandaanLink, t, 0);
 
	          linkList[tel] = linkid;
	          DLink aLink = links.get(Math.abs(linkid));
	          if (aLink != null) {
	              lengte += aLink.lengte;
	          } else {
	        	  System.out.println("Dijkstra.java: Link bestaat niet " + linkid);
	          }
 
          tel++;
          t = getIntegerWaarde(vandaanKnoop,t,-1);
          if ( t == -1) break;    
      }    
      
      RouteAnswer routeAntwoord = new RouteAnswer(linkList, getIntegerWaarde(kosten, eind, 2147483647), lengte);
      
      return routeAntwoord;
   }
 
   private int getIntegerWaarde(HashMap<Integer,Integer> hashMap, int index, int defaultWaarde) {
	   Integer waarde = hashMap.get(index);
	   if (waarde == null) {
		   waarde = defaultWaarde;
	   }
	   return waarde;
   }

	private int bepaalHemelsbredeAfstandViaKnoop(int x, int y, int begX, int begY, int eindX, int eindY) {
		return distance2Points(begX,   begY, x, y) +
			   distance2Points(eindX, eindY, x, y);
	}

	// ##############################################################################   
    public int distance2Points(int x1, int y1, int x2, int y2)
 // ##############################################################################
 // afstand bepalen tussen twee knopen
 // ------------------------------------------------------------------------------
    {
      double dx = (x1-x2);
      double dy = (y1-y2);
      return (int)Math.sqrt(dx*dx + dy*dy);
    }
    
    
    private double distancePointLine(int begX, int begY, int eindX, int eindY, int x, int y) {
        double a = (eindX - begX);
        double b = (eindY - begY);
        double normalLength = Math.sqrt(a * a + b * b);
        double d = Math.abs((x - begX) * (b) - (y - begY) * (a)) / normalLength;
        return d;
    }
    
	ResultSet execQuery(Connection con, String query) {
		try
		{
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			return (stmt.executeQuery(query));
		}
		catch (SQLException e)
		{
			System.out.println("Kan de query niet maken: " + query);
			return (null);
		}
	}


	public ArrayList<Integer> getNodesOud(double x, double y, double maxAfstand, double maxAfstandBuiten, String dataset) {
		initNetwerk(dataset);
		double afstand;
		ArrayList<Integer> terug = new ArrayList<Integer>();
		
        for (int t = 0; t < aantKnopen; t++) {
            afstand = distance2Points((int)x, (int)y, knopen.get(t).x, knopen.get(t).y);
            if (afstand < maxAfstand) {
                terug.add(t);            	
            }
        }  

        if (terug.size() > 3) {
        	// minimaal 3 knopen gevonden
		    return terug;
        } else {
        	if (maxAfstand * 1.5 <= maxAfstandBuiten) {
        		// recursief nog eens proberen met groetere zoekafstand
        	    return getNodesOud(x, y, maxAfstand * 1.5, maxAfstandBuiten, dataset);
        	} else {
        		// recursie afbreken
        		return terug;
        	}
        }        
	}
/*
	public ArrayList<Integer> getNodes(double x, double y, double maxAfstand, String dataset) {
		initNetwerk(dataset);

		ArrayList<Integer> terug = new ArrayList<Integer>();
        String query = 
		    " SELECT " +
		    " linknummer  " +
		    " FROM " + dataset + ".fietsnet " +
		    " ORDER BY geometry <-> ST_SetSRID(ST_Point(" + x + "," + y + "),900913)" +		    
		    " LIMIT 3 ";
 
        try {
	 	   
	        ResultSet rs = LoginDatabase.execQuery(query);
			while (rs.next()) {
	    		int linknummer = rs.getInt("linknummer");
	    		DLink alink = links.get(linknummer);
	    		if ((!terug.contains(alink.nodea)) && (!terug.contains(alink.nodeb))) {
	    		    terug.add(alink.nodea);
			    }
			} 
		    
        }	catch (SQLException e) {
				e.printStackTrace();
        }	
        return terug;

	}
*/
	public ArrayList<Integer> getNodes(double x, double y, double maxAfstand) {
		ArrayList<Integer> terug = new ArrayList<Integer>();
		ArrayList<Integer> buurlinks = spatialIndex.getNNeighbours((float)x, (float)y, 5, (float)maxAfstand);
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for (int linknummer: buurlinks) {
			DLink alink = links.get(linknummer);
    		if ((!nodes.contains(alink.nodea)) && (!terug.contains(alink.nodeb))) {
    			terug.add(alink.nodea);
    			nodes.add(alink.nodea);
    			nodes.add(alink.nodeb);
    		}
    	}
		return terug;		
	}
	

    private double getAfstandLinkGPS(int linknummer, GPSTrack gpsTrack, HashMap<Integer,Double> afstandWeerstand) {
    	
        linknummer = Math.abs(linknummer);
        Double terug = afstandWeerstand.get(linknummer);
        if (terug != null) return terug;
       DLink alink = links.get(linknummer);        
        double afstand1 = getAfstandLink(gpsTrack, alink.getX1(), alink.getY1());
        double afstand2 = getAfstandLink(gpsTrack, alink.getX2(), alink.getY2());
        double afstand = Math.max(afstand1, afstand2);
        afstand = afstand / 10;
        if (afstand < 1) afstand = 1;
        if (afstand > 10) {
        	if (afstand > 100) {
        		afstand = 999999999;
        	} else {
        		afstand = 10;
        	}
        }
        afstandWeerstand.put(linknummer, afstand);
        return afstand;
    }
        
    private double getAfstandLink(GPSTrack gpsTrack, int x1, int y1) {
    	GoudmapLine line = gpsTrack.getLine();
    	return line.distancenum(new DPoint(x1, y1));	
    }

	public void resetAfstanden() {
		afstandWeerstand = new HashMap<Integer,Double>();		
	}

	public double distanceLinkPunt(int linknummer, DPoint punt) {
		if (linknummer < 0) linknummer = -linknummer;
		GoudmapLine line = spatialIndex.getLine(linknummer);
		if (line == null) return 999999999;
		return line.distancenum(punt);
	}
 
	
	public int getLinknummer(int i) {
		if (linknummers.containsKey(i)) {
		    return linknummers.get(i);
		} else {
			return 0;
		}
	}

	public String getWkt(int linknummer) {
		return spatialIndex.getLine(linknummer).asWKT();
	}
	public DLink getLink(int linknummer) {
		return links.get(linknummer);
	}


}

