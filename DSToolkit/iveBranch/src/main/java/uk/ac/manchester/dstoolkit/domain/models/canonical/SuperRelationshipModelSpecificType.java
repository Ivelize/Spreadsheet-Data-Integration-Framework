package uk.ac.manchester.dstoolkit.domain.models.canonical;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. RDF_PREDICATE
 */
public enum SuperRelationshipModelSpecificType {
	REL_FOREIGN_KEY, XSD_IDREF, XSD_FOREIGN_KEY,
	XSD_NEST, O_REFERENCE, O_GENERALISATION, OR_FOREIGN_KEY,
	OR_REFERENCE_COLUMN, OR_GENERALISATION, OR_NEST,
	RDF_PREDICATE, SPECIALISATION_HIERARCHY;
}
