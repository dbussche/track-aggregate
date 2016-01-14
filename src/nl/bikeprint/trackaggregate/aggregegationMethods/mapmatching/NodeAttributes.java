package nl.bikeprint.trackaggregate.aggregegationMethods.mapmatching;

public class NodeAttributes {

	public int aantal = 0;
	public int id = 0;
	public double tijd = 0;
	public double tijdNa = 0;
	public double tijdVoor = 0;
	public double tijdOp = 0;
    public double x = 0;
    public double y = 0;

    public NodeAttributes(double x, double y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
	}

}