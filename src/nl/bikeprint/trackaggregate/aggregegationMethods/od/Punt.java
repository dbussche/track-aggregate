package nl.bikeprint.trackaggregate.aggregegationMethods.od;

 
 
public class Punt {
    public double x = 0;
    public double y = 0;
    public double hoek = 0;

    /**
     * Empty constryctor for serialization
     */
    public Punt() {
    }

    public Punt(double x2, double y2) {
        this.x = x2;
        this.y = y2;
    }

    public Punt(double x, double y, double hoek) {
        this.x = x;
        this.y = y;
        this.hoek = hoek;
    }
}

 
