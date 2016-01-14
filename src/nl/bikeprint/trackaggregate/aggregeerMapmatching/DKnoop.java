package nl.bikeprint.trackaggregate.aggregeerMapmatching;

public class DKnoop {
	  public int databaseKnoopnummer;
	  public int x = 0;
	  public int y = 0;
	  public short hoogte = 0;
	  public byte aantal = 0;
	  public int[] naar = new int[0]; 
	  
/*
	  static int getIndexVoorDatabasenummer(int databasenummer) {
		  int terug = -1;
		  for (int i = 0; i < Dijkstra.aantKnopen; i++) {
		    	if (Dijkstra.knopen[i].databaseKnoopnummer == databasenummer) {
					terug = i;
					break;
				}
		  }		
		  return terug;
	  }
	  */
}
