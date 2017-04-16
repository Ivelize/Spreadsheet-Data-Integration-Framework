package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;


/**
 * @author Klitos
 *
 * Kernel Types for the Kernel Density Estimator (KDE)
 *  - A Kernel is a type of a weighting function.
 *     
 */
public enum KernelType {
	GAUSSIAN("GAUSSIAN"),
	EPANECHNIKOV("EPANECHNIKOV"),
	BOX("BOX"),
	TRIANGULAR("TRIANGULAR"),
	BIWEIGHT("BIWEIGHT"),
	TRIWEIGHT("TRIWEIGHT");

	private final String value;

	KernelType(String v) {
        value = v;
    }
	
    public static KernelType fromValue(String v) {
        for (KernelType c: KernelType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
}//end enum