package nl.bikeprint.trackaggregate.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import gnu.trove.TIntProcedure;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;

import nl.bikeprint.trackaggregate.aggregegationMethods.mapmatching.DPoint;
import nl.bikeprint.trackaggregate.aggregegationMethods.od.Polygon;
import nl.bikeprint.trackaggregate.aggregegationMethods.od.Punt;
import nl.bikeprint.trackaggregate.general.GoudmapLine;

public class SpatialIndexPolygon {
     
    private SpatialIndex rTree = new RTree();
    private HashMap<Integer, Polygon> polygons = new HashMap<Integer, Polygon>();
    
    public SpatialIndexPolygon() {
    	rTree.init(null);
    }
    
    public void add(Polygon polygon, int linknummer) {
    	polygons.put(linknummer, polygon); 
        Rectangle r = polygon.getBounds();
        rTree.add(r, linknummer);
    }
    
    public ArrayList<Integer> getMatchingPolygons(final float x, final float y) {
    	final ArrayList<Integer> foundPolygons = new ArrayList<Integer>();
        TIntProcedure proc = new TIntProcedure() {
            @Override
            public boolean execute(int i) {
            	if (polygons.get(i).contains(x, y)) {
            		foundPolygons.add(i);
            	}
            	return true;
            }
        };
        rTree.nearestNUnsorted(new Point(x, y), proc, Integer.MAX_VALUE, 0);
        return foundPolygons;
    }
    
    public Polygon getPolygon(int id) {
		return polygons.get(id);
	}
    
}
