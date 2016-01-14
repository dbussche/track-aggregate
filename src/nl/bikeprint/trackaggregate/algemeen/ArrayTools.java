package nl.bikeprint.trackaggregate.algemeen;

public class ArrayTools {
    public static int[] add(int[] inArr, int toevoegen) {
    	int[] terug = new int[inArr.length + 1];
    	System.arraycopy(inArr, 0, terug, 0, inArr.length);
    	terug[inArr.length] = toevoegen;
		return terug;    	
    }
    
    public static int[] add(int[] inArr, int toe1, int toe2, int toe3) {
    	int oudLengte = inArr.length;
    	int[] terug = new int[oudLengte + 3];
    	System.arraycopy(inArr, 0, terug, 0, oudLengte);
    	terug[oudLengte    ] = toe1;
    	terug[oudLengte + 1] = toe2;
    	terug[oudLengte + 2] = toe3;    	
		return terug;    	
    }    

    public static byte[] add(byte[] inArr, byte toevoegen) {
    	byte[] terug = new byte[inArr.length + 1];
    	System.arraycopy(inArr, 0, terug, 0, inArr.length);
    	terug[inArr.length] = toevoegen;
		return terug;    	
    }
    
    public static byte[] add(byte[] inArr, byte toe1, byte toe2, byte toe3) {
    	int oudLengte = inArr.length;
    	byte[] terug = new byte[oudLengte + 3];
    	System.arraycopy(inArr, 0, terug, 0, oudLengte);
    	terug[oudLengte    ] = toe1;
    	terug[oudLengte + 1] = toe2;
    	terug[oudLengte + 2] = toe3;
    	return terug;    	
    }    
    
    public static byte[] add(byte[] inArr, byte toe1, byte toe2) {
    	int oudLengte = inArr.length;
    	byte[] terug = new byte[oudLengte + 2];
    	System.arraycopy(inArr, 0, terug, 0, oudLengte);
    	terug[oudLengte    ] = toe1;
    	terug[oudLengte + 1] = toe2;
    	return terug;    	
    }    
    
	public static String print(int[] inArr) {
		String terug = "";
		for (int t = 0; t < inArr.length; t++) {
			terug += inArr[t] + ",";
		}	
		return terug;
	}

	public static String toSQLArray(int[] inArr) {
		String terug = "{";
		for (int t = 0; t < inArr.length; t++) {
			terug += inArr[t] + "";
			if (t < inArr.length - 1) {
				terug += ",";	
			}
		}	
		terug += "}";
		return terug;
	}

	public static boolean isInLijst(int[] arr, int waarde) {
		for (int t = 0; t < arr.length; t++) {
			if (arr[t] == waarde) return true;
		}
		return false;
	}
	public static int[] addAlsNietInLijst(int[] inArr, int toevoegen) {
		if (!isInLijst(inArr, toevoegen)) {
			return add(inArr, toevoegen);
		} else {
		    return inArr;
		}    
	}

	public static int[] verwijder(int[] arr, int verwijderen) {
		if (isInLijst(arr, verwijderen)) {
			int[] terug = new int[arr.length - 1];
			int i = 0;
			for (int t = 0; t < arr.length; t++) {
				if (arr[t] != verwijderen) {
					terug[i++] = arr[t];
				}
			}
			return terug;
		} else {
		    return arr;
		}   
	}

 
}
