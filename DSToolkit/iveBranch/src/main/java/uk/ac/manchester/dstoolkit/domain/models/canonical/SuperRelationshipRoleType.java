package uk.ac.manchester.dstoolkit.domain.models.canonical;

/**
 * All kinds of relationships are represented as Enum types
 * 
 * @author chedeler
 *
 * Comments (klitos):
 *  1. Showing the relationship between RDF Subject Nodes and Object Nodes 
 */
public enum SuperRelationshipRoleType {
	REFERENCED, REFERENCING, PARENT, CHILD, SUPER_CLASS, SUB_CLASS, RDF_SUBJECT, RDF_OBJECT;
}
