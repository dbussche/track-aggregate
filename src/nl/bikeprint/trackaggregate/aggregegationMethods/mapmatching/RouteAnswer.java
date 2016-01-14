package nl.bikeprint.trackaggregate.aggregegationMethods.mapmatching;

public class RouteAnswer {
	int[] linkList;
    int kosten;
    double lengte;
    
    public RouteAnswer(int[] linkList, Integer kosten, double lengte) {
        this.linkList = linkList;
        if (kosten == null) {
        	this.kosten = 2147483647;
        } else {
        	this.kosten = kosten;
        }
        this.lengte = lengte;
	}

    public double getLengte() {
    	return lengte;
    }
    
    public int getKosten() {
    	return kosten;
    }
    
    public int[] getLinkList() {
    	return linkList;
    }
}
