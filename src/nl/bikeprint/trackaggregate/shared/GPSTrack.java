package nl.bikeprint.trackaggregate.shared;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import nl.bikeprint.trackaggregate.aggregeerMapmatching.Punt;
import nl.bikeprint.trackaggregate.algemeen.Constanten;
import nl.bikeprint.trackaggregate.algemeen.GoudmapLine;

import org.apache.commons.lang3.ArrayUtils;

public class GPSTrack {
	
    public final static double DREMPEL_AFSTAND_BEGIN_EIND = 0.3;    // 0.3 km = 300 meter
    public final static long DREMPEL_TIJD_SPLITSEN = 3 * 60 * 1000; // 3 minuten in milliseconden
    public final static double DREMPEL_AFSTAND_SPLITSEN = 0.2;      // 0.2 km = 200 meter
    private ArrayList<GPSPunt> gpsArray = new ArrayList<GPSPunt>();
    private int routeID;
    private boolean isSet = false;
    private Integer beginUur = null;
    private Integer weekDag = null;
    private int modality = -1;
    GoudmapLine cacheLine = null;
    private java.util.Random random = new java.util.Random();
    
	@SuppressWarnings({ "deprecation" })
	public void add(double x, double y, double snelheid, String datestring) {
		isSet = true;
	//	System.out.println("add " + x + ", " + y);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
		    date = simpleDateFormat.parse(datestring);
		} catch (ParseException e) {
			simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			try {
			    date = simpleDateFormat.parse(datestring);
			} catch (ParseException e2) {
			    e2.printStackTrace();
			}
		}		
		

		if (beginUur == null) {
			beginUur = date.getHours();			
		}
		if (weekDag == null) {
			weekDag = date.getDay();
		}
		
		int l = gpsArray.size();
		double berekendeSnelheid = 0;
		double km = 0;
        double uur = 0;
        boolean onbetrouwbaar = false;
		if (l > 0) {
			GPSPunt laatstePunt = gpsArray.get(l - 1);
			km = GoudmapLine.distance_2_Points(x, y, laatstePunt.getX(), laatstePunt.getY()) / Constanten.GOOGLE_FACTOR;
            uur = (double)(date.getTime() - laatstePunt.getTijd()) / 1000 / 3600;
            berekendeSnelheid = km / uur;
            onbetrouwbaar = (km > 0.1 && berekendeSnelheid > 50) || (km > 1 && berekendeSnelheid > 30) || (km > 2);
            if (onbetrouwbaar) {
            	gpsArray.remove(l - 1);
            }                
		}	
		if (!onbetrouwbaar) {
		    gpsArray.add(new GPSPunt(x, y, snelheid, date));
		}    

		cacheLine = null;
	}

	public boolean isSet() {
		return isSet;
	}

	public int getAantal() {
		return gpsArray.size();
	}
	
	public GPSPunt getNode(int i) {
		if (i < 0 || i >= gpsArray.size()) {
			return null;
		} else {
		    return gpsArray.get(i);
		}    
	}

	public int getRouteID() {
		return routeID;
	}

	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}

	public GoudmapLine getLine() {
		if (cacheLine == null) {
			ArrayList<Punt> arr_vertices = new ArrayList<Punt>();
			GPSPunt gpsPoint;
			for (int i = 0; i < getAantal(); i++) {
				gpsPoint = gpsArray.get(i);
			    arr_vertices.add(new Punt(gpsPoint.getX(),gpsPoint.getY()));
			}
			cacheLine = new GoudmapLine(arr_vertices);
		}
		return cacheLine;
	}

	public double getLengte() {
		return getLine().length() / Constanten.GOOGLE_FACTOR;
	}
	

	public double getHemelsbredeLengte() {
		GPSPunt eindNode = getNode(getAantal() - 1);
		GPSPunt beginNode = getNode(0);
		if (eindNode != null && beginNode != null) {
		    return GoudmapLine.distance_2_Points(eindNode.toPunt(),  beginNode.toPunt()) / Constanten.GOOGLE_FACTOR;
		} else {
			return 0;
		}
	} 
	
	public double getTijdAt(Punt punt) {
		double[] arr = getLine().distanceVertexAandeel(punt);
		long tijd = 0;
		long tijdA = getNode((int)arr[0]).getTijd();
		
		if (arr[1] > 0) {
			long tijdB = getNode((int)arr[0] + 1).getTijd();
			tijd = (long)(tijdA + (tijdB - tijdA) * arr[1]);
		} else {
			tijd = tijdA;
		}
		return tijd;
	}
	
	public Punt getPuntAtTijd(long tijd) {
		GPSPunt gpsPoint,gpsPointA,gpsPointB;
		int i;
		if (getAantal() < 2) return null;
		long atijd = gpsArray.get(0).getTimeOfDay();
		if (atijd > tijd) return null;
		atijd = gpsArray.get(getAantal()-1).getTimeOfDay();
		if (atijd < tijd) return null;
		
		for (i = 0; i < getAantal(); i++) {
			gpsPoint = gpsArray.get(i);
			if (gpsPoint.getTimeOfDay() > tijd) {
				break;
			}
		}
		if (i == getAantal()) return new Punt(gpsArray.get(i - 1).getX(),gpsArray.get(i - 1).getY(),gpsArray.get(i - 1).getSnelheid()) ; 
		gpsPointB = gpsArray.get(i);
		gpsPointA = gpsArray.get(i - 1);
		double verhouding = (tijd - gpsPointA.getTimeOfDay()) / (gpsPointB.getTimeOfDay() - gpsPointA.getTimeOfDay());
	
		return new Punt(
				gpsPointA.getX() + verhouding * (gpsPointB.getX() - gpsPointA.getX()),
				gpsPointA.getY() + verhouding * (gpsPointB.getY() - gpsPointA.getY()),
				gpsPointA.getSnelheid() + verhouding * (gpsPointB.getSnelheid() - gpsPointA.getSnelheid()));
	}
	
	public double getTotaleTijd() {
		GPSPunt eindNode = getNode(getAantal() - 1);
		GPSPunt beginNode = getNode(getAantal() - 1);
		if (eindNode != null && beginNode != null) {
		    return getNode(getAantal() - 1).getTijd() - getNode(0).getTijd();
		} else {
			return 0;
		}
	}

	public double getLengteTussen(Punt punt1, Punt punt2) {
		return getLine().getLengteTussen(punt1, punt2);
	}	

	/*
	public void naarDatabase(String dataset) {
		
		if (getAantal() < 2) return;
		
		StringBuilder query = new StringBuilder();
		GPSPunt gpsPoint;
		query.append("INSERT INTO " + LoginDatabase.getTabelNaam("gpstrack",dataset) + " (routeid, datestring, speed, st_transform) VALUES ");

		StringBuilder wkt = new StringBuilder();
 	    wkt.append("ST_GeomFromText('LINESTRING(");
 	    try {	
    		for (int i = 0; i < getAantal(); i++) {
    			gpsPoint = getNode(i);	    			
    			if (i > 0) {
            		wkt.append(",");
            		query.append(",");
            	}	
            	wkt.append(gpsPoint.getX() + " " + gpsPoint.getY());
		        query.append("(" + getRouteID() + ",'" + gpsPoint.getDateString() + "'," + gpsPoint.getSnelheid() + ",");
		        query.append("ST_SetSRID(ST_Point(" + gpsPoint.getX() + "," +  gpsPoint.getY() + "),900913) )");
    	    }
            wkt.append(")',900913)");

            LoginDatabase.execUpdate(query.toString());
            PreparedStatement ps;
		    ps = LoginDatabase.getConnection().prepareStatement(
		    		"INSERT INTO " + LoginDatabase.getTabelNaam("routes",dataset) + " (route_id, modality, uur, dag, snelheid, lengte, geometry) VALUES (?, ?, ?, ?, ?, ?, " + wkt.toString() + ");"
		    	);
            ps.setInt(1, routeID);
            ps.setInt(2, getModality());
            ps.setInt(3, getBeginUur());
            ps.setInt(4, getWeekdag());
            ps.setDouble(5, getSnelheid());
            ps.setDouble(6, getLengte());
            ps.executeUpdate();            	
            
		} catch (SQLException e) {
			e.printStackTrace();
		}			
	
	}
	 
*/
	
	private int getWeekdag() {
		if (weekDag == null) {
			return -1;
		} else {
			return (int)weekDag;	
		}	
	}

	private double getSnelheid() {
        double uur = (double)(getNode(getAantal() - 1).getTijd() - getNode(0).getTijd()) / 1000 / 3600;
        return getLengte() / uur;
	}

	public int getBeginUur() {
		if (beginUur == null) {
			return -1;
		} else {
			return (int)beginUur;	
		}		
	}

	public void setModality(int modality) {
        this.modality = modality;		
	}
    
	public int getModality() {
        return modality;		
	}

	public void filterBeginEnd() {
		GPSPunt gpsPoint;	
		GPSPunt beginPoint = getNode(0);
		GPSPunt eindPoint = getNode(getAantal() - 1);
		int drempelBegin = 0;
		int drempelEind = getAantal();
		
		
		double drempel_begin = DREMPEL_AFSTAND_BEGIN_EIND * random.nextDouble();
		double drempel_eind = DREMPEL_AFSTAND_BEGIN_EIND * random.nextDouble();
		for (int i = 0; i < getAantal(); i++) {
			gpsPoint = getNode(i);
			double kmBegin = GoudmapLine.distance_2_Points(gpsPoint.getX(), gpsPoint.getY(), beginPoint.getX(), beginPoint.getY()) / Constanten.GOOGLE_FACTOR;
			double kmEind  = GoudmapLine.distance_2_Points(gpsPoint.getX(), gpsPoint.getY(), eindPoint.getX(),  eindPoint.getY())  / Constanten.GOOGLE_FACTOR;
		    if (kmBegin < drempel_begin) {
		    	drempelBegin = i;
		    }
		    if (kmEind < drempel_eind && drempelEind == getAantal()) {
		    	drempelEind = i;		    	
		    }
		}
	    ArrayList<GPSPunt> nieuwGpsArray = new ArrayList<GPSPunt>();
	    for (int i = drempelBegin + 1; i < drempelEind - 1; i++) {
			gpsPoint = getNode(i);			
	        nieuwGpsArray.add(new GPSPunt(gpsPoint.getX(), gpsPoint.getY(), gpsPoint.getSnelheid(), gpsPoint.getDate()));
	    }
	    gpsArray = nieuwGpsArray;
	    cacheLine = null;
	}

	public GPSTrack[] splitsAlsNodig() {
		// recursief splitsAlsNodigBinnen aanroepen tot geen splitsen meer nodig
		GPSTrack[] splits = splitsAlsNodigBinnen();
		if (splits.length == 1) {
			return new GPSTrack[] {this}; 
		} else { 
			return (GPSTrack[]) ArrayUtils.addAll(splits[0].splitsAlsNodig(), splits[1].splitsAlsNodig());
		}
	}
	
	public GPSTrack[] splitsAlsNodigBinnen() {
		GPSPunt gpsPoint, centraalPunt;
		long centraalTijd;
    	int splitsPunt = -1;
		for (int i = 0; i < getAantal(); i++) {
			centraalPunt = getNode(i);
		    centraalTijd = centraalPunt.getTijd();
		    int laatstDichtbij = 0;
		    for (int t = 0; t < getAantal(); t++) {
		    	gpsPoint = getNode(t);
		    	if (Math.abs(centraalTijd - gpsPoint.getTijd()) > DREMPEL_TIJD_SPLITSEN) {
                    double km = GoudmapLine.distance_2_Points(gpsPoint.getX(), gpsPoint.getY(), centraalPunt.getX(), centraalPunt.getY()) / Constanten.GOOGLE_FACTOR;
                    if (km < DREMPEL_AFSTAND_SPLITSEN) {
                    	laatstDichtbij = t;    	
                    }
		    	}
		    }
		    if (laatstDichtbij != 0) { // splitspunt gevonden
		    	// zoek punt op maximale afstand van i an laatstDichtbij; 
		    	// bij "gewoon" oponthoud is dat een toevallige punt van de wolk van tussenstop
		    	// bij twee keer langs dezelfde punt lopen is dat het uiteinde van de "doodlopende" weg
		    	GPSPunt laatstPoint = getNode(laatstDichtbij);
		    	double maxAfstand = 0;
			    for (int j = 0; j < getAantal(); j++) {
			    	gpsPoint = getNode(j);			    	
	                double km = GoudmapLine.distance_2_Points(gpsPoint.getX(), gpsPoint.getY(), centraalPunt.getX(), centraalPunt.getY()) / Constanten.GOOGLE_FACTOR;
	                      km += GoudmapLine.distance_2_Points(gpsPoint.getX(), gpsPoint.getY(), laatstPoint.getX(),  laatstPoint.getY())  / Constanten.GOOGLE_FACTOR;
	                if (km > maxAfstand) {
	                	maxAfstand = km;
	                	splitsPunt = j;    		                 
			    	}
			    }		    			    	
		    }
		    if (splitsPunt > 0) break;
		}			
	    
		if (splitsPunt == -1 || splitsPunt < 10 || splitsPunt > getAantal() - 10) {
			return new GPSTrack[] {this};
		} else {
			GPSTrack deel1 = new GPSTrack();
			GPSTrack deel2 = new GPSTrack();
			for (int i = 0; i < getAantal(); i++) {
				gpsPoint = getNode(i);
				if (i < splitsPunt) {
					deel1.add(gpsPoint.getX(), gpsPoint.getY(), gpsPoint.getSnelheid(), gpsPoint.getDateString());
				} else {
				    deel2.add(gpsPoint.getX(), gpsPoint.getY(), gpsPoint.getSnelheid(), gpsPoint.getDateString());
				}    
			}
			//deel1.setRouteID(this.getRouteID());
			deel1.setRouteID(getNieuwRouteID());
			deel1.setModality(this.getModality());
			deel1.filterBeginEnd();
			deel2.setRouteID(getNieuwRouteID());
			deel2.setModality(this.getModality());
			deel2.filterBeginEnd();
			return new GPSTrack[] {deel1, deel2};
		}		
	}

	static int nieuweRouteID = 0;
	private int getNieuwRouteID() {
		nieuweRouteID--;
		return nieuweRouteID;
	}
/*
	public void olifantenPaadjes(String dataset, RouteAntwoord[] links) {
		if (getAantal() < 2) return;
		
		GoudmapLine line = null;
 	 	
		for (int i = 0; i < getAantal(); i++) {
			GPSPunt gpsPoint = getNode(i);	
			if (gpsPoint.distance(links) > 50 && i < getAantal()-1) {  // laatste punt niet meenemen, zo wordt laatste segment altijd in else weggeschreven
				if (line == null) {
					line = new GoudmapLine();
				}	
				line.add(gpsPoint.toPunt());
			} else {
				if (line != null) {
					if (line.aantal() > 2) {    						
    					LoginDatabase.execUpdate(
                        " INSERT INTO  " + LoginDatabase.getTabelNaam("olifantenpaadjes",dataset) + " (routeid, geo) " +
                        " VALUES (" + routeID + ", " +
    					" ST_GeomFromText('" + line.asWKT() + "',900913));");
					}
					line = null;
				}
			}
	    }   			
	}
	*/

	public double getPercentilSnelheid(int n) {
		/*
		 * n is percentil, bijvoorbeeld 20
		 * n = 50 geeft een goede benadering van de mediaan (behalve dat voor even aantal elementen niet van de middenste average genomen wordt)
		 */
		Double[] sortSnelheden = new Double[gpsArray.size()];
		for (int i = 0; i < gpsArray.size(); i++) {
			sortSnelheden[i] = gpsArray.get(i).getSnelheid();
		}
		return getPercentilArray(sortSnelheden, n);

	}

	private double getPercentilArray(Double[] sortSnelheden, int n) {
		int lengte = sortSnelheden.length;
		if (lengte == 0) return 0;
		Arrays.sort(sortSnelheden);
		int element = (int)(lengte - lengte * n / 100.0);
		if (element < 0) element = 0;
		if (element >= lengte) element = lengte - 1;
	//	System.err.println("arrlength="+lengte+", element="+element+", waardes:"+sortSnelheden[0] +"|"+sortSnelheden[element]+"|"+sortSnelheden[lengte-1]);
		return sortSnelheden[element];
	}

	public double getPercentilSnelheid(Punt middenpunt, double radius, int n) {
        Double[] sortSnelheden = getSnelhedenBinnenAfstand(middenpunt, radius);
		return getPercentilArray(sortSnelheden, n);
	}

	private Double[] getSnelhedenBinnenAfstand(Punt middenpunt, double radius) {
		ArrayList<Double> snelhedenList = new ArrayList<Double>();
		for (int i = 0; i < gpsArray.size(); i++) {
			GPSPunt gpsPunt = gpsArray.get(i);
			if (gpsPunt.distance(middenpunt) <= radius) {
			    snelhedenList.add(gpsArray.get(i).getSnelheid());
			}
		}
	//	Double[] sortSnelheden = new Double[];
		return snelhedenList.toArray(new Double[snelhedenList.size()]);		 
	}

	public double getVariatieSnelheid(Punt middenpunt, double radius, double maximum) {
		Double[] snelheden = getSnelhedenBinnenAfstand(middenpunt, radius);
		double gemiddelde = getGemiddelde(snelheden, maximum);
		double sum = 0;
		for (int i = 0; i < snelheden.length; i++) {
			sum += Math.pow(Math.min(snelheden[i], maximum) - gemiddelde, 2);
		}
		return sum / snelheden.length;		
	}

	private double getGemiddelde(Double[] snelheden, double maximum) {
		double sum = 0;
		for (int i = 0; i < snelheden.length; i++) {
			sum += Math.min(snelheden[i], maximum);
		}
		return sum / snelheden.length;
	}
   
}
