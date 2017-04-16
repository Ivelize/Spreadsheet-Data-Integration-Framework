package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

public enum AggregationType {
	SIMMAX("SIMMAX"),
	SIMAVERAGE("SIMAVERAGE"),
	SIMWEIGHTED("SIMWEIGHTED"),
	SIMMIN("SIMMIN"),
	NONE("NONE");	
	private final String value;

	AggregationType(String v) {
        value = v;
    }
	
    public static AggregationType fromValue(String v) {
        for (AggregationType c: AggregationType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}//end enum
