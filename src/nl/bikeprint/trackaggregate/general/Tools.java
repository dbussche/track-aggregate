package nl.bikeprint.trackaggregate.general;

public class Tools {
	
	public static int makeInt(String intString) {
		return makeInt(intString, -1);
	}
 
	public static int makeInt(String intString, int defaultValue) {
		if (intString == null) return defaultValue;
		int terug = defaultValue;
		try {
		    terug = Integer.parseInt(intString);
		} catch (Exception e) {
			return defaultValue;
		}
		return terug;
	}

	public static float makeFloat(String floatString) {
		return makeFloat(floatString, -1);
	}
	
	public static float makeFloat(String floatString, int defaultValue) {
		if (floatString == null) return defaultValue;
		float terug = defaultValue;
		try {
		    terug = Float.parseFloat(floatString);
		} catch (Exception e) {
			return defaultValue;
		}
		return terug;
	}

	public static double makeDouble(String doubleString) {
		return makeDouble(doubleString, -1);
	}
	
	public static double makeDouble(String doubleString, int defaultValue) {
		if (doubleString == null) return defaultValue;
		double terug = defaultValue;
		try {
		    terug = Double.parseDouble(doubleString);
		} catch (Exception e) {
			return defaultValue;
		}
		return terug;
	}
	
	public static String makeNaN0(Double d) {
		if (d == null || d.isNaN() || d.isInfinite()) {
			return "0";
		} else {
			return d.toString();
		}
	}
	
	public static String makeNaN0(Integer i) {
		if (i == null) {
			return "0";
		} else {
			return i.toString();
		}
	}	
	 
}
