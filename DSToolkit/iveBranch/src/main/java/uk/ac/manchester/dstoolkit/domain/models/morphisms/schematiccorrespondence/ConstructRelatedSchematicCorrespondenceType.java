package uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence;

/**
 * @author chedeler
 *
 */

public enum ConstructRelatedSchematicCorrespondenceType {
	SUPER_ABSTRACT_TO_SUPER_ABSTRACT("SA2SA"), SUPER_LEXICAL_TO_SUPER_LEXICAL("SL2SL"), SUPER_RELATIONSHIP_TO_SUPER_RELATIONSHIP("SR2SR"), SUPER_ABSTRACT_TO_SUPER_LEXICAL(
			"SA2SL"), SUPER_LEXICAL_TO_SUPER_ABSTRACT("SL2SA"), SUPER_ABSTRACT_TO_SUPER_RELATIONSHIP("SA2SR"), SUPER_RELATIONSHIP_TO_SUPER_ABSTRACT(
			"SR2SA"), SUPER_LEXICAL_TO_SUPER_RELATIONSHIP("SL2SR"), SUPER_RELATIONSHIP_TO_SUPER_LEXICAL("SR2SL");

	private final String shortName;

	ConstructRelatedSchematicCorrespondenceType(String shortName) {
		this.shortName = shortName;
	}

	@SuppressWarnings("unused")
	private String getShortName() {
		return shortName;
	}
}
