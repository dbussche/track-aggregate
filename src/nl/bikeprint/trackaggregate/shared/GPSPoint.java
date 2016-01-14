package nl.bikeprint.trackaggregate.shared;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.bikeprint.trackaggregate.aggregegationMethods.mapmatching.DPoint;

public class GPSPoint {
	private double x;
    private double y;
    private double speed;
    private Date date;
    
    public GPSPoint(double x, double y, double speed, Date date) {
	    this.x = x;
	    this.y = y;
	    this.speed = speed;
	    this.date = date;
	}

	public double getX() {
    	return x;
    }
    
    public double getY() {
    	return y;
    }

    public DPoint toPoint() {
    	return new DPoint(x, y);
    }
    
    public double getSpeed() {
    	return speed;
    }
    
    public long getTime() {
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

	public double distance(DPoint middenpunt) {
		return middenpunt.distance(x,y);		
	}
	
}
