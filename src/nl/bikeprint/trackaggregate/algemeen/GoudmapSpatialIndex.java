package nl.bikeprint.trackaggregate.algemeen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import gnu.trove.TIntProcedure;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;

import nl.bikeprint.trackaggregate.aggregeerMapmatching.Punt;
import nl.bikeprint.trackaggregate.algemeen.GoudmapLine;

public class GoudmapSpatialIndex {
     
    private SpatialIndex rTree = new RTree();
    private HashMap<Integer,GoudmapLine> goudmapLines = new HashMap<Integer,GoudmapLine>();
    
    public GoudmapSpatialIndex() {
    	rTree.init(null);
    }
    
    public void add(GoudmapLine line, int linknummer) {
    	goudmapLines.put(linknummer, line); 
        Rectangle r = line.getBounds();
        rTree.add(r, linknummer);
    }
    
    public ArrayList<Integer> getNNeighbours(float x, float y, int n) {
    	return getNNeighbours(x, y, n, Float.MAX_VALUE);
    }
    
    public ArrayList<Integer> getNNeighbours(final float x, final float y, final int n, final float maxAfstand) {
    	final TreeMap<Double, Integer> map = new TreeMap<Double, Integer>();
        TIntProcedure proc = new TIntProcedure() {
            @Override
            public boolean execute(int i) {
            	double afstand = goudmapLines.get(i).distancenum(new Punt(x, y));
                if (afstand <= maxAfstand) {
            	    map.put(afstand, i);
                }
            	return true;
            }
        };
        rTree.nearestNUnsorted(new Point(x, y), proc, n * 5, maxAfstand);
        ArrayList<Integer> terug = new ArrayList<Integer>();
        int tel = 0;
        for(Map.Entry<Double,Integer> entry : map.entrySet()) {
        	terug.add(entry.getValue());
        	tel++;
        	if (tel > n) break;        	
        }
        return terug;
    }
    
    public GoudmapLine getLine(int linknummer) {
		return goudmapLines.get(linknummer);
	}
    
}
