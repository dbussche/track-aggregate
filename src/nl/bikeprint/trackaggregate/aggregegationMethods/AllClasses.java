package nl.bikeprint.trackaggregate.aggregegationMethods;

import java.util.ArrayList;

import nl.bikeprint.trackaggregate.shared.AggregationInterface;

public class AllClasses {
    public static ArrayList<AggregationInterface> getAggregationClasses() {
    	ArrayList<AggregationInterface> aggregationClasses = new ArrayList<AggregationInterface>();
		aggregationClasses.add(new AggregateMapmatching());
		aggregationClasses.add(new AggregateOD());
		aggregationClasses.add(new AggregateGPSPoints());
    	return aggregationClasses;    	
    }
}
