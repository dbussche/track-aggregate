package nl.bikeprint.trackaggregate.shared;

 
public interface AggregeerInterface {
    void init(DatabaseSchrijverInterface databaseSchrijver, String bbox);
    void add(GPSTrack gpsTrack);
    void schrijfNaarDatabase();
}
