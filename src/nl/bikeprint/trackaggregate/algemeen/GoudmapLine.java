package nl.bikeprint.trackaggregate.algemeen;
  
///import java.text.ParseException;
import java.util.ArrayList;

import nl.bikeprint.trackaggregate.aggregeerMapmatching.Punt;

import com.infomatiq.jsi.Rectangle;
 
public class GoudmapLine {
	int num_vertices;        // aantal vertices van lijn
	public ArrayList<Punt> arr_vertices;       // array met xy-coordinates
	double cache_lengte;        // merkt zich de lengte om niet altijd te moeten herberekenen

	public GoudmapLine() {
		reset();
	}
	
	public GoudmapLine(ArrayList<Punt> arr_vertices) {
		this.arr_vertices = arr_vertices;
		num_vertices = arr_vertices.size();
		cache_lengte=-1;  
	}
	
	// ********************************************
	public GoudmapLine(String wkt) {
	// ********************************************
	// initialiseert het line-object met Wkt-String
	// ******************************************** 
		String[] arr = wkt.split("\\(");
	    
	    float x,y;
	    if (arr[0].indexOf("LINESTRING") == -1) {
	        num_vertices = 0;
	    } else {
	        arr = arr[1].split("\\,");
	        arr_vertices = new ArrayList<Punt>();
	        num_vertices = 0;
	        for (int i=0; i<arr.length; i++) {

	        	String str = arr[i].trim();
	        	String[] strArray = str.split(" ");
	        	
	        	x=0;y=0;
	        	try {
	        		
	        		x = Float.valueOf(strArray[0]);
	        		if (strArray[1].endsWith("")) {
	        			strArray[1] = strArray[1].replace(")", "");
	        		}
	        		y = Float.valueOf(strArray[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}

	            num_vertices++;
	            arr_vertices.add(new Punt(x,y));
	        }  
	    }
        cache_lengte=-1;  
	}

	public void reset() {
		num_vertices = 0;
		arr_vertices = new ArrayList<Punt>();
		cache_lengte = -1;
	}
	
	public void add(Punt punt) {
        num_vertices++;
        arr_vertices.add(punt);		
	}
	
	public Rectangle getBounds() {
		double xmin = Double.MAX_VALUE, xmax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE, ymax = Double.MIN_VALUE;
		 
        for (int i = 0; i < num_vertices; i++) {
            Punt p = (Punt)arr_vertices.get(i);
            if (p.x < xmin) xmin = p.x;
            if (p.x > xmax) xmax = p.x;
            if (p.y < ymin) ymin = p.y;
            if (p.y > ymax) ymax = p.y;     
        }	
		
        Rectangle terug = new Rectangle((float)xmin, (float)ymin, (float)xmax, (float)ymax);
	    return terug;
	}
	
	// ********************************************
	public void merge(GoudmapLine andereLine) {
	// ********************************************
	// voegt andere Lijn toe aan huidige lijn
	// werkt alleen als begin of eindpunt aansluiten
	// ********************************************
	    double minaf = distance_2_Points (beginpunt(), andereLine.beginpunt());
	    int modus = 1;
	    double af = distance_2_Points (beginpunt(), andereLine.eindpunt());
	    if (af < minaf) {
	        minaf = af;
	        modus = 2;
	    }
	    af = distance_2_Points (eindpunt(), andereLine.beginpunt());
	    if (af < minaf) {
	        minaf = af;
	        modus = 3;
	    }
	    af = distance_2_Points(eindpunt(), andereLine.eindpunt());
	    if (af < minaf) {
	        modus = 4;
	    }

	    if (modus == 1) {
	        arr_vertices = array_merge(array_reverse(andereLine.arr_vertices),arr_vertices);    
	    }
	    if (modus == 2) {
	        arr_vertices = array_merge(andereLine.arr_vertices,arr_vertices);    
	    }
	    if (modus == 3) {
	        arr_vertices = array_merge(arr_vertices,andereLine.arr_vertices);    
	    }
	    if (modus == 4) {
	        arr_vertices = array_merge(arr_vertices,array_reverse(andereLine.arr_vertices));    
	    }

	    num_vertices = arr_vertices.size();
	    cache_lengte = -1;
	}

 
	ArrayList<Punt> array_merge(ArrayList<Punt> a, ArrayList<Punt> b) {
		a.addAll(b);
		return a;
	}
	
	ArrayList<Punt> array_reverse(ArrayList<Punt> a) {
		ArrayList<Punt> terug = new ArrayList<Punt>();
		for (int i=a.size()-1; i>=0; i--) {
			terug.add(a.get(i));
		}
		return terug;
	}
	 
	// ********************************************
	public String asWKT() {
	// ********************************************
	// Geeft line terug als WKT string
	// ********************************************
	    String wkt = "LINESTRING(" + ((Punt)arr_vertices.get(0)).x + " " + ((Punt)arr_vertices.get(0)).y;
	    for (int i=1; i<num_vertices; i++) {
	        wkt += "," + ((Punt)arr_vertices.get(i)).x + " " + ((Punt)arr_vertices.get(i)).y;
	    }    
	    wkt += ")";
	    return wkt;
	}

	// ********************************************
	public double length() {
	// ********************************************
	// Geeft lengte van line in meter terug
	// ********************************************
	    if (cache_lengte < 0) { 
	        cache_lengte = 0;
	        for (int i=0; i<num_vertices - 1; i++) {
	            cache_lengte += distance_2_Points(((Punt)arr_vertices.get(i)).x,((Punt)arr_vertices.get(i)).y,((Punt)arr_vertices.get(i+1)).x,((Punt)arr_vertices.get(i+1)).y);
	        }
	    }    
	    return cache_lengte;
	}

	// ********************************************
	Punt beginpunt() { return (Punt) arr_vertices.get(0); }
	Punt eindpunt()  { return (Punt) arr_vertices.get(num_vertices-1); }
	// ********************************************

	public Punt along(double pos) {
		return along(pos,true);
	}
	// ********************************************
	public Punt along(double pos, boolean relatief)
	// ********************************************
	// Geeft punt terug op de lijn op positie $pos
	// terug: x,y,hoek
	// ********************************************
	{
	  double looplengte;
	  double seglengte;
	  double r,xp=-1,yp=-1,hoek=-1;
	  if (relatief) {
          looplengte = length() * pos / 100;
	  } else {
	      looplengte = pos;
	  }
	  
	  double lengte = 0;
	  Punt p1,p2;
	  for (int i=0; i<num_vertices-1;i++) {
		  p1 = arr_vertices.get(i);
		  p2 = arr_vertices.get(i+1);
	      seglengte = distance_2_Points(p1, p2);
	      lengte += seglengte;
	   
	    //
	      if ( (lengte >= looplengte) || (i==num_vertices-2) )  {
              r = (looplengte-lengte+seglengte) / seglengte;
	          xp = p1.x+(p2.x-p1.x)*r;
	          yp = p1.y+(p2.y-p1.y)*r;
	          hoek = Math.atan2((p2.y-p1.y),(p2.x-p1.x));
	          break;
	      }
	  }  
	  return new Punt((float)xp,(float)yp,(float)hoek);
	}
	
 
	
    public int aantal() {
    	return num_vertices;
    }

	// ********************************************
	double[] distance(double x, double y) 
	// ********************************************
	// Geeft afstand, positie en vertex terug 
	// van een punt t.o.v. de lijn
	// terug: afstand, pos
	// ********************************************
	{
	    double afstand = 99999999999999.0;
	    double lengte = 0;
	    double seglengte, pos = 0;
	    Punt punt1, punt2;
	    boolean omdraaien;
	    double a, b, ap, bp, xs, ys, af;
	    double minVertex = 0;
	    double minVertexRelatief = 0;
	  
	    for (int i=0; i<num_vertices-1;i++) { 
	    
	// haal punten van segment op en zorg ervoor dat ALTIJD x2>x2  
	        if (((Punt)arr_vertices.get(i)).x > ((Punt)arr_vertices.get(i+1)).x) {
		        punt2 = (Punt) arr_vertices.get(i);
		        punt1 = (Punt) arr_vertices.get(i+1);
		        omdraaien = true;
	        } else {
		        punt1 = (Punt) arr_vertices.get(i);
		        punt2 = (Punt) arr_vertices.get(i+1);
		        omdraaien = false;
	        }
	        if (punt1.x == punt2.x) {
	      	    punt2.x = punt1.x + 0.001;      
	        }
	        if (punt1.y == punt2.y) {
	      	    punt2.y = punt1.y + 0.001;      
	        }	        
	        seglengte = distance_2_Points(punt1, punt2);
	// functie voor dit lijnsegment
	        a = (punt2.y - punt1.y) / (punt2.x - punt1.x);
	        b = punt1.y - a * punt1.x;

	// functie voor lijn door punt welke haaks op het lijnsegment staat    
	        ap = -1 / a;
	        bp = y - ap * x;
	    
	// bepaal snijpunt tussen deze lijnen    
	        xs = (bp - b) / (a - ap);
	        ys = a * xs + b;
	        
	        if (xs > punt2.x) { // snijpunt naar rechts buiten segment
	            af = distance_2_Points(x, y, punt2.x, punt2.y);
	            if (af < afstand) {
	                afstand = af;
	                pos = lengte;
	                minVertex = i;
	                if (omdraaien) {
	                	minVertex = i;	
	                } else {
	                	minVertex = i + 1;
	                	pos += seglengte;
	                }
	                minVertexRelatief = 0;
	            }  
	        } else if (xs < punt1.x) { // snijpunt naar links buiten segment
	            af = distance_2_Points(x, y, punt1.x, punt1.y);
	            if (af < afstand) { 
	        	    afstand = af;
	                pos = lengte;
	                if (omdraaien) {
	                	minVertex = i + 1;
	                	pos += seglengte;	                	
	                } else {
	                	minVertex = i;
	                }
	                minVertexRelatief = 0;
	            }
	        } else {       // snijpunt binnen segment
	            af = distance_2_Points(x, y, xs, ys);
	            if (af < afstand) {
	            	afstand = af;
	            	minVertex = i;
	                if (!omdraaien) { 
	                    pos = lengte + seglengte * (xs - punt1.x) / (punt2.x - punt1.x);
	                    minVertexRelatief = (xs - punt1.x) / (punt2.x - punt1.x); 
	                } else {
	                    pos = lengte + seglengte * (xs - punt2.x) / (punt1.x - punt2.x);
	                    minVertexRelatief = (xs - punt2.x) / (punt1.x - punt2.x);
	                }            
	            } 
	        }          
	        lengte += seglengte;
	    }  
  	    return new double[]{afstand, pos, minVertex, minVertexRelatief};
	}

	// ********************************************
	public double[] distanceVertexAandeel(Punt xy) 
	/*********************************************
	   shortcut naar distance, 
	   geeft kleinere vertex en aandeel tot volgende vertex terug
	   A---B---------C--D
	          ^
	   geeft dus B, 0.25 terug  
	**********************************************/
	{
	    double[]arr = distance(xy.x, xy.y);
	    return new double[]{arr[2], arr[3]};
	}

	public double getLengteTussen(Punt punt1, Punt punt2) {
	    double[]arr1 = distance(punt1.x, punt1.y);
	    double[]arr2 = distance(punt2.x, punt2.y);
	    //String segWKT = segment(arr1[1], arr2[1]);
	    //GoudmapLine segLine = new GoudmapLine(segWKT);	    
	    //return segLine.length();
	    return Math.abs(arr1[1] - arr2[1]);
	}
	
	// ********************************************
	public int distanceVertex(Punt xy)
	// ********************************************
	// shortcut naar distance, geeft alleen vertex terug
	// ********************************************
	{
	    double[]arr = distance(xy.x, xy.y);
	    return (int)arr[2];
	}
	

	// ********************************************
	public double distancenum(Punt xy)
	// ********************************************
	// shortcut naar distance, geeft alleen afstand terug
	// ********************************************
	{
	    double[]arr = distance(xy.x, xy.y);
	    return arr[0];
	}	

	// ********************************************
	public double getAlong(Punt xy)
	// ********************************************
	// shortcut naar distance, geeft alleen afstand terug
	// ********************************************
	{
	    double[]arr = distance(xy.x, xy.y);
	    return arr[1];
	}
	
	String segment(double vanpos, double naarpos) {
		return segment(vanpos, naarpos, true);
	}
	
	// ********************************************
	String segment(double vanpos, double naarpos, boolean relatief)
	// ********************************************
	// Geeft lijnsegment terug tussen van en naar positie
	// ********************************************
	{
		double looplengteVan, looplengteNaar;
		double seglengte;
	    if (relatief) { 
	    	looplengteVan = length() * vanpos / 100;
	        looplengteNaar = length() * naarpos / 100;
	    } else { 
	    	looplengteVan = vanpos;
	        looplengteNaar = naarpos;
	    } 

	    String tsegment = "LineString(";

	    double lengte = 0;
	    boolean voor = true;
        double r, xp, yp;
        double x1, y1, x2, y2;
	    for (int i=0; i < num_vertices-1; i++) { 
	        x1=((Punt)arr_vertices.get(i)).x;
	        y1=((Punt)arr_vertices.get(i)).y;
	        x2=((Punt)arr_vertices.get(i+1)).x;
	        y2=((Punt)arr_vertices.get(i+1)).y;

	        seglengte = distance_2_Points(x1,y1,x2,y2);
	        lengte += seglengte;
	        xp = -999999999999.0;
	        yp = -999999999999.0;

	        if (voor && (lengte >= looplengteVan)) {
	            r = (looplengteVan - lengte + seglengte) / seglengte;
	            xp = x1+(x2-x1)*r;
	            yp = y1+(y2-y1)*r;
	            tsegment += xp + " " + yp;
	            voor = false;
	        }
	   
	        if (lengte >= looplengteNaar) {
	            r = (looplengteNaar-lengte+seglengte) / seglengte;
	            xp = x1+(x2-x1)*r;
	            yp = y1+(y2-y1)*r;
	            tsegment += ", " + xp + " " + yp;
	            break;
	        }       
	        if (!voor && ((x2 != xp) || (y2 != yp))) tsegment += ", " + x2 + " " + y2;        
	    }  
	    tsegment += ")";
	    return tsegment;
	}
 
			  
	//End of class
	
	static public double distance_2_Points(Punt p1, Punt p2) {
		return distance_2_Points(p1.x, p1.y, p2.x, p2.y);
	}

	static public double distance_2_Points(double x1, double y1, double x2, double y2) {
	      double dx = (x1-x2);
	      double dy = (y1-y2);
	      return (int)Math.sqrt(dx*dx + dy*dy);		
	}
	
	 // ##############################################################################   
	    static public double distance_2_Points(int x1, int y1, int x2, int y2)
	 // ##############################################################################
	 // afstand bepalen tussen twee knopen
	 // ------------------------------------------------------------------------------
	    {
	      double dx = (x1-x2);
	      double dy = (y1-y2);
	      return (int)Math.sqrt(dx*dx + dy*dy);
	    }

		public double beginHoek() {
//			Punt punt = along(0, true);
//			return punt.hoek * 180 / Math.PI;
		
			Punt p1 = arr_vertices.get(0);
			Punt p2 = arr_vertices.get(1);
		
		    double hoek = Math.atan2((p2.y-p1.y),(p2.x-p1.x));
			return hoek * 180 / Math.PI;
		}

		public double eindHoek() {
//	Punt punt = along(100, true);
//	return punt.hoek * 180 / Math.PI;
			Punt p1 = arr_vertices.get(num_vertices - 2);
			Punt p2 = arr_vertices.get(num_vertices - 1);
		
		    double hoek = Math.atan2((p2.y-p1.y),(p2.x-p1.x));
			return hoek * 180 / Math.PI;
			
		}
 

	 
	}

