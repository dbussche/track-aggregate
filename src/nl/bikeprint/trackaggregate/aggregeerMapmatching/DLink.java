package nl.bikeprint.trackaggregate.aggregeerMapmatching;

public class DLink {

	private int x1;
	private int y1;
	private int x2;
	private int y2;
	int linknummer;
	double lengte;
	public int nodea, nodeb;
	
	public DLink(int x1, int y1, int x2, int y2, int nodea, int nodeb, double lengte, int id) {
		this.setX1(x1);
		this.setY1(y1);
		this.setX2(x2);
		this.setY2(y2);
		this.nodea = nodea;
		this.nodeb = nodeb;
		this.lengte = lengte;
		this.linknummer = id;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}
    
}