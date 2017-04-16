package uk.ac.manchester.dstoolkit.service.impl.util.statistics;

/**
 * 
 * @author klitos
 */
public enum ErrorMeasuresTypes {
	ABSOLUTE_ERROR("ABSOLUTE_ERROR"),
	SQUARED_ERROR("SQUARED_ERROR");
	
	private final String value;

	ErrorMeasuresTypes(String v) {
        value = v;
    }
	
    public static ErrorMeasuresTypes fromValue(String v) {
        for (ErrorMeasuresTypes c: ErrorMeasuresTypes.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}//end Enum