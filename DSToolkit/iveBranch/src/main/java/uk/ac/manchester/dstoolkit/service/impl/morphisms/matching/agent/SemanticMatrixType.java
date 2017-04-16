package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent;

/***
 * This enum type is used for two purposes
 * 	- to set the type of a Semantic Matrix
 *  - for precedence, therefore order is important
 *  
 * @author klitos
 */
public enum SemanticMatrixType {
	//Type of semantic matrix entry for hierarchy matrix
	EQUIVALENCE, SHARE_SUPERCLASS, SUBSUMPTION,
	
	//Type of semantic matrices
	HIERARCHY, DOMAIN, RANGE, NAMESPACE,
	
	//This is for the ground truth
	EXPECTATION_MATRIX,
	
	//This is for the semantic matrix that joins together
	//all the semantic annotations
	CONCATENATE_MATRIX, BAYES;
}