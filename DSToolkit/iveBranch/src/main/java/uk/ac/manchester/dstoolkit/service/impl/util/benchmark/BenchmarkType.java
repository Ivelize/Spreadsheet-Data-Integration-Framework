package uk.ac.manchester.dstoolkit.service.impl.util.benchmark;

/***
 * This is the Type to setup the Benchmark.
 * 
 * @author klitos
 */
public enum BenchmarkType {

	COMA_APPROACH("COMA"),
	BAEYSIAN_APPROACH("BAEYSIAN"),
	BAEYSIAN_APPROACH_SYN_ONLY("BAEYSIAN_APPROACH_SYN_ONLY"),
	BAEYSIAN_APPROACH_SYN_SEM("BAEYSIAN_APPROACH_SYN_SEM"),
	NUMERICAL_EXPERIMENT("NUMERICAL_EXPERIMENT"),
	SIMULATE_SEMANTIC_ANNOTATIONS("SIMULATE_SEMANTIC_ANNOTATIONS");
	
	private final String value;

	BenchmarkType (String v) {
		value = v;
    }
	
    public static BenchmarkType fromValue(String v) {
        for (BenchmarkType c: BenchmarkType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }	
}//end BenchmarkType
