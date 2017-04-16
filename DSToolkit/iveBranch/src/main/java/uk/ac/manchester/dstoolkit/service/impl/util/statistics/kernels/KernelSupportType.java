package uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels;

public enum KernelSupportType {
	KDE_WITHOUT_SUPPORT("KDE_WITHOUT_SUPPORT"),
	KDE_WITH_SUPPORT("KDE_WITH_SUPPORT");
	
	private final String value;

	KernelSupportType(String v) {
        value = v;
    }
	
    public static KernelSupportType fromValue(String v) {
        for (KernelSupportType c: KernelSupportType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}//end enum