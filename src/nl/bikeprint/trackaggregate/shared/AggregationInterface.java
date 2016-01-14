package nl.bikeprint.trackaggregate.shared;
 
public interface AggregationInterface {
    void init(DatabaseWriterInterface databaseWriter, String bbox);
    void add(GPSTrack gpsTrack);
    void exit();
}
