package nl.bikeprint.trackaggregate.aggregeerMapmatching;

public class LinkAttributen {
    public int aantal = 0;
    public int aantalKortsteRoute = 0;
    public double snelheidSum = 0;
    private double snelheidSumRelatief = 0;
    private int snelheidN = 0;
	public double verhoudingHemelsbreed = 0;
    
	public void incAantal() {
		aantal++;
	}
	public void incAantalKortsteRoute() {
		aantalKortsteRoute++;
	}
	public double getSnelheidSumRelatief() {
		return snelheidSumRelatief;
	}
	public void setSnelheidSumRelatief(double snelheidSumRelatief) {
		this.snelheidSumRelatief = snelheidSumRelatief;
	}
	public int getSnelheidN() {
		return snelheidN;
	}
	public void setSnelheidN(int snelheidN) {
		this.snelheidN = snelheidN;
	}}
