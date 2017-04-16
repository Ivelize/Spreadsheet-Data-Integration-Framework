package uk.ac.manchester.dstoolkit.service.impl.util.mutation;

public enum MutationParameterType {
	
	ONTOLOGY_FILE,
	
	//Mutation Rate can either be: Mild or Severe
	MUTATION_RATE, 
	
	MILD_MUTATION_RATE, SEVERE_MUTATION_RATE,
	
	//Mutation Mode for either edit-distance or n-gram or both
	MUTATION_MODE,
	
	EDIT_DISTANCE_MUTATION, NGRAM_MUTATION, BOTH_MUTATION,
	
	//Specify the Blooms cross-links file and the expectation alignment for the ground truth
	BLOOMS_ALIGNMENT,
	
	EXPECTATION_ALIGNMENT,	
	
	//Use previously generated mutations
	USE_MUTATIONS;
}
