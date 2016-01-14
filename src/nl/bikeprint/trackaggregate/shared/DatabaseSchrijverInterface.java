package nl.bikeprint.trackaggregate.shared;

import java.util.ArrayList;

import aggregate.BikePrintTabelSchrijver;

public interface DatabaseSchrijverInterface {
	
	public boolean maakTabel(BikePrintTabelSchrijver tabel);
	public void schrijfRecords(BikePrintTabelSchrijver tabel, ArrayList<ArrayList<String>> waardes);

}
