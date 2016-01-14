package nl.bikeprint.trackaggregate.shared;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.bikeprint.trackaggregate.aggregeerMapmatching.Punt;

public class GPSPunt {
	private double x;
    private double y;
    private double snelheid;
    private Date date;
    
    public GPSPunt(double x, double y, double snelheid, Date date) {
	    this.x = x;
	    this.y = y;
	    this.snelheid = snelheid;
	    this.date = date;
	}

	public double getX() {
    	return x;
    }
    
    public double getY() {
    	return y;
    }

    public Punt toPunt() {
    	return new Punt(x, y);
    }
    
    public double getSnelheid() {
    	return snelheid;
    }
    
    public long getTijd() {
    	return date.getTime();
    }

    @SuppressWarnings("deprecation")
	public long getTimeOfDay() {
    	return (date.getHours() * 60 * 60 + date.getMinutes() * 60 + date.getSeconds()) * 1000;
    }
      
    public String getDateString() {
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	return simpleDateFormat.format(date); 
    }
    
    public Date getDate() {
    	return date;
    }

	public double distance(Punt middenpunt) {
		return middenpunt.distance(x,y);		
	}
	
}
