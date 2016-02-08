package nl.bikeprint.trackaggregate.aggregegationMethods.od;

public class AggregateODMatrix {

	private float[][][] matrix = new float[5][][];
	
	public AggregateODMatrix(int[] numberOfAreasPerLevel) {
		for (int level = 1; level < 5; level++) {
		    matrix[level] = new float[numberOfAreasPerLevel[level]][numberOfAreasPerLevel[level]];
		}
	}

	public void addRitten(int level, int o, int d, float aantal) {
	    matrix[level][o][d] += aantal;	 
	}

	public float getTrips(int level, int o, int d) {
		assert level >= 1 && level < 5 : "illegal level"; 
		
		return matrix[level][o][d];
	}
 
}
