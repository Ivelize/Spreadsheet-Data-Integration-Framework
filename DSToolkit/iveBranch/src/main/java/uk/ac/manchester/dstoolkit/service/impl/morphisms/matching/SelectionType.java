package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

public enum SelectionType {
	MAXN("MAXN"),
	MAXDELTA("MAXDELTA"),
	THRESHOLD("THRESHOLD"),
	MULTIPLE("MULTIPLE"),
	SELECT_ALL("SELECT_ALL");
	
	private final String value;

	SelectionType(String v) {
        value = v;
    }
	
    public static SelectionType fromValue(String v) {
        for (SelectionType c: SelectionType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}//end enum
