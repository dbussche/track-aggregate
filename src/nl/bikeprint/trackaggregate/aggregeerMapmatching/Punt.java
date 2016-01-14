package nl.bikeprint.trackaggregate.aggregeerMapmatching;

public class Punt {
    public double x = 0;
    public double y = 0;
    public double waarde = 0;
	  
    public Punt(double x2, double y2) {
        this.x = x2;
        this.y = y2;
    }

	public Punt(double x, double y, double waarde) {
        this.x = x;
        this.y = y;
        this.waarde = waarde;
	}

	public double distance(Punt p) {
        return distance(p.x, p.y);	
	}
	
	public double distance(double x2, double y2) {
	    double dx = (x - x2);
	    double dy = (y - y2);
	    return (int)Math.sqrt(dx * dx + dy * dy);		
	}
}
