package uk.ac.manchester.dstoolkit.domain.models.canonical;

/**
 * @author chedeler
 *
 */
public enum SuperRelationshipMIDSTSuperModelType {
	FOREIGN_KEY, ABSTRACT_ATTRIBUTE, GENERALISATION, NEST;

	//TODO add method to superabstract, superlexical and superrelationship that sets automatically the supermodeltype after setting the model specific type
}
