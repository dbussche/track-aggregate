package nl.bikeprint.trackaggregate.algemeen;

public class Tools {
	
	public static int maakInt(String intString) {
		return maakInt(intString, -1);
	}
 
	public static int maakInt(String intString, int defaultWaarde) {
		if (intString == null) return defaultWaarde;
		int terug = defaultWaarde;
		try {
		    terug = Integer.parseInt(intString);
		} catch (Exception e) {
			return defaultWaarde;
		}
		return terug;
	}

	public static float maakFloat(String floatString) {
		return maakFloat(floatString, -1);
	}
	
	public static float maakFloat(String floatString, int defaultWaarde) {
		if (floatString == null) return defaultWaarde;
		float terug = defaultWaarde;
		try {
		    terug = Float.parseFloat(floatString);
		} catch (Exception e) {
			return defaultWaarde;
		}
		return terug;
	}

	public static double maakDouble(String doubleString) {
		return maakDouble(doubleString, -1);
	}
	
	public static double maakDouble(String doubleString, int defaultWaarde) {
		if (doubleString == null) return defaultWaarde;
		double terug = defaultWaarde;
		try {
		    terug = Double.parseDouble(doubleString);
		} catch (Exception e) {
			return defaultWaarde;
		}
		return terug;
	}
	
	public static String maakNaN0(Double d) {
		if (d.isNaN() || d.isInfinite()) {
			return "0";
		} else {
			return d.toString();
		}
	}
	 
}
