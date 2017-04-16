package uk.ac.manchester.dstoolkit.service.impl.util.benchmark;

/***
 * This is the Type to setup the Benchmark.
 * 
 * @author klitos
 */
public enum PlotType {

	PLOT_TOP_K_PRECISION_RECALL("PLOT_TOP_K_PRECISION_RECALL"),
	PLOT_DIFFERENCE_D("PLOT_DIFFERENCE_D"),
	PLOT_INDIVIDUAL_ERR_SYN_ONLY("PLOT_INDIVIDUAL_ERR_SYN_ONLY"),
	PLOT_INDIVIDUAL_ERR_SYN_SEM("PLOT_INDIVIDUAL_ERR_SYN_SEM"),
	OUTPUT_NUMERIC_AGGREGATED_ERROR("OUTPUT_NUMERIC_AGGREGATED_ERROR");
	
	private final String value;

	PlotType (String v) {
		value = v;
    }
	
    public static PlotType fromValue(String v) {
        for (PlotType c: PlotType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }	
}//end BenchmarkType