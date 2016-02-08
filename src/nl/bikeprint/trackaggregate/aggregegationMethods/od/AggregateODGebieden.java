package nl.bikeprint.trackaggregate.aggregegationMethods.od;
 
import java.util.ArrayList;

import nl.bikeprint.trackaggregate.general.SpatialIndexPolygon;

public class AggregateODGebieden {

	private ArrayList<Gebied>[] gebieden;
	private SpatialIndexPolygon[] spatialIndex;
	
	@SuppressWarnings("unchecked")
	public AggregateODGebieden() { 
	    gebieden = new ArrayList[5];
	    spatialIndex = new SpatialIndexPolygon[5];
	    for (int level = 1; level < 5; level++) {
	    	gebieden[level] = new ArrayList<Gebied>();
	    	spatialIndex[level] = new SpatialIndexPolygon();
	    }	    
    }
	 

	public void addGebied(int id, int level, String wkt, String l0, String l1, String l2, String l3, String l4) {
		assert level >= 1 && level < 5 : "illegal level";
		
		String name = "";
		Polygon polygon = new Polygon(wkt);
	//	if (level == 0) name = l0;
		if (level == 1) name = l0 + " " + l1;
		if (level >= 2) {
			name = l2;
			if (!l3.equals("")) name += " " + l3;
			if (!l4.equals("")) name += " " + l4;
		}		
		gebieden[level].add(new Gebied(name, wkt));
		spatialIndex[level].add(polygon, gebieden[level].size() - 1);		
	}
	
	public int getGebiedIndex(double x, double y, int level) {
		assert level >= 1 && level < 5 : "illegal level";
		ArrayList<Integer> list = spatialIndex[level].getMatchingPolygons((float)x, (float)y);
		if (list.isEmpty()) {
			return -1;
		} else {
			return list.get(0);
		}		
	}
	 

	public int[] getNumberOfAreasPerLevel() {
		int[] number = new int[5];
		for (int level = 1; level < 5; level++) {
			number[level] = gebieden[level].size();
		}
		return number;
	}
	
	public int getNumberOfAreas(int level) {
		return gebieden[level].size();
	}



	public Gebied getGebied(int level, int i) {
		assert level >= 1 && level < 5 : "illegal level";
		 
		return gebieden[level].get(i);
	}



}
