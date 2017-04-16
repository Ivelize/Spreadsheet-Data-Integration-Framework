package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

public enum MatcherType {
	RDFS_LABEL_MATCHER("RDFS_LABEL_MATCHER"),
	NAMESPACE_URI_MATCHER("NAMESPACE_URI_MATCHER"),
	LEVENSHTEIN("LEVENSHTEIN"),
	NGRAM("NGRAM"),
	JARO_WINKLER("JARO_WINKLER"),
	TOPMATCHER("TOPMATCHER"),
	SCHEMAS("SCHEMAS"),
	INSTANCES("INSTANCES"),
	PARENTS("PARENTS"),
	LEAVES("LEAVES"),
	PATH("PATH"),
	NAMESTAT("NAMESTAT"),
	NAMETYPE("NAMETYPE"),
	NAME("NAME"),
	NAMEPATH("NAMEPATH"),
	STATISTICS("STATISTICS"),
	DATATYPE("DATATYPE"),
	TOP_DOWN("TOP_DOWN"),
	BOTTOM_UP("BOTTOM_UP"),
	INSTCOMPARE("INSTCOMPARE"),
	COMA_AVG("COMA_AVG"),
	BAYESIAN_APPROACH("BAYESIAN_APPROACH");

	private final String value;

	MatcherType(String v) {
        value = v;
    }
	
    public static MatcherType fromValue(String v) {
        for (MatcherType c: MatcherType.values()) {
            if (c.value.equals(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}//end Enum