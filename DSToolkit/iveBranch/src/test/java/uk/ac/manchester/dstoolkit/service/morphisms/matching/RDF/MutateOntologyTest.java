package uk.ac.manchester.dstoolkit.service.morphisms.matching.RDF;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.service.impl.util.mutation.MutationParameter;
import uk.ac.manchester.dstoolkit.service.impl.util.mutation.MutationParameterType;
import uk.ac.manchester.dstoolkit.service.impl.util.mutation.MutationServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class MutateOntologyTest {


	
	//Store the control parameters
	private Map<MutationParameterType, MutationParameter> controlParameters;
	
	@Before
	public void setUp() {	
	}//end setUp()	
	
	/***
	 * This method is responsible for running the approach that mutates a given ontology.
	 * 
	 */
	@Test
	public void mutateOntologyTest() {

		/******************************
		 * SETUP the Mutation approach
		 */		
		
		//controlParameters
		controlParameters = new HashMap<MutationParameterType, MutationParameter>();
		
		
		/****
		 * Choose : Specify input documents
		 */
		
		
		//<INPUT>: Location of a single ontology to mutate
		String ontologyLocation = "./src/test/resources/training/benchmark/oaei_2012/Conference.owl";
	
		//Set the file-path for the Ontology file to mutate
		MutationParameter ontologyFile = new MutationParameter(MutationParameterType.ONTOLOGY_FILE, ontologyLocation);
		controlParameters.put(MutationParameterType.ONTOLOGY_FILE, ontologyFile);
		
		/*<INPUT> [optional]: If you would like to use a previously set of Mutations that have been randomly chosen from a previous run of the approach
		  specify the file that holds them here [USE EXISTING MUTATIONS FROM A PREVIOUS RUN]*/
		/*String mutationFileLocation = "./src/test/resources/training/benchmark/mutations/Conference.mutations";
		MutationParameter existingMutationFile = new MutationParameter(MutationParameterType.USE_MUTATIONS, mutationFileLocation);
		controlParameters.put(MutationParameterType.USE_MUTATIONS, existingMutationFile); */
		
		//<INPUT>: Location of the BLOOMS alignment file
		String bloomsFileAlignment = "./src/test/resources/training/benchmark/alignments/blooms/ekaw_conference.rdf";
		MutationParameter bloomsFile = new MutationParameter(MutationParameterType.BLOOMS_ALIGNMENT, bloomsFileAlignment);
		controlParameters.put(MutationParameterType.BLOOMS_ALIGNMENT, bloomsFile);

		//<INPUT>: Location of the EXPECTATION Matrix alignment file
		String expMatrixAlignment = "./src/test/resources/training/benchmark/alignments/expectation/ekaw_conference_exp.rdf";
		MutationParameter expectationFile = new MutationParameter(MutationParameterType.EXPECTATION_ALIGNMENT, expMatrixAlignment);
		controlParameters.put(MutationParameterType.EXPECTATION_ALIGNMENT, expectationFile);
		
		
		/****
		 * Choose : MILD or SEVERE mutation rate
		 */
		
		
		//Set mutation rate: can be either Mild or Severe
		MutationParameter mutationRate = new MutationParameter(MutationParameterType.MUTATION_RATE, MutationParameterType.SEVERE_MUTATION_RATE.toString());
		controlParameters.put(MutationParameterType.MUTATION_RATE, mutationRate);
		
		//Set mutation mode: can be either Edit-Distance, n-Gram or both (Choose one)
		MutationParameter mutationMode = new MutationParameter(MutationParameterType.MUTATION_MODE, MutationParameterType.BOTH_MUTATION.toString());		
		controlParameters.put(MutationParameterType.MUTATION_MODE, mutationMode);

		//MutationParameter mutationMode = new MutationParameter(MutationParameterType.MUTATION_MODE, MutationParameterType.NGRAM_MUTATION.toString());		
		//controlParameters.put(MutationParameterType.MUTATION_MODE, mutationMode);
		
		//MutationParameter mutationMode = new MutationParameter(MutationParameterType.MUTATION_MODE, MutationParameterType.BOTH_MUTATION.toString());		
		//controlParameters.put(MutationParameterType.MUTATION_MODE, mutationMode);		
			

		/*****************************
		 * Call the mutation approach
		 */
		MutationServiceImpl mutationServiceImpl = new MutationServiceImpl(controlParameters);
		mutationServiceImpl.runMutation();		
	}//end		
		
}//end class