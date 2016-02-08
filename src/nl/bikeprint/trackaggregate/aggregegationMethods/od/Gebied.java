package nl.bikeprint.trackaggregate.aggregegationMethods.od;

public class Gebied {

	private String name;
	private String wkt;
	
	public Gebied(String name, String wkt) {
		this.name = name;
		this.wkt = wkt;
	}
 

	public String getName() {
		return name;
	}


	public String getWKT() {
		return wkt;
	}

}
