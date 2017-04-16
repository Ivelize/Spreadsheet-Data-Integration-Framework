package uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation;

/**
 * 
 * @author klitos
 */
public enum PerformanceErrorTypes {
	MEAN_SQUARED_ERROR("MEAN_SQUARED_ERROR"),
	ROOT_MEAN_SQUARED_ERROR("ROOT_MEAN_SQUARED_ERROR"),
	MEAN_ABS_ERROR("MEAN_ABS_ERROR"),
	RELATIVE_SQUARED_ERROR("RELATIVE_SQUARED_ERROR"),
	ROOT_RELATIVE_SQUARED_ERROR("ROOT_RELATIVE_SQUARED_ERROR"),
	RELATIVE_ABS_ERROR("RELATIVE_ABS_ERROR"),
	CORRELATION_COEFFICIENT("CORRELATION_COEFFICIENT");	
	
	private final String value;

	PerformanceErrorTypes(String v) {
        value = v;
    }
	
    public static PerformanceErrorTypes fromValue(String v) {
        for (PerformanceErrorTypes c: PerformanceErrorTypes.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}//end Enum
