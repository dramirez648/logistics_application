package processor;

public enum Priority {

	TIME,
	COST;
	
	public static Priority from(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim().toLowerCase();
		switch (str) {
		case "time":
			return TIME;
		case "cost":
			return COST;
	  default :
	  	return null;
		}
	}
}
