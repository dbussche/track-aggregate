package aggregate.aggregatieMethoden;

import java.util.ArrayList;

import nl.bikeprint.trackaggregate.shared.AggregeerInterface;

public class AlleKlassen {
    public static ArrayList<AggregeerInterface> getAggregeerKlassen() {
    	ArrayList<AggregeerInterface> aggregeerKlassen = new ArrayList<AggregeerInterface>();
		aggregeerKlassen.add(new AggregeerMapmatching());
		aggregeerKlassen.add(new AggregeerHB());
		aggregeerKlassen.add(new AggregeerGPSPunten());
    	return aggregeerKlassen;    	
    }
}
