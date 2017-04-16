package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.JenaJungRDFVisualisationService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;

/***
 * For testing MatcherServiceImpl
 * 
 * This is more like a Client for testing various new functionality in terms of 
 * the new architecture for matching RDF instead of a proper JUnit test class.
 *  
 * //TODO: [S.O.S] This class extends the OLD RDFAbstractIntegrationTest, have this in mind
 * because some tests may not work. If this is the case consider changing it to extend the 
 * new RDFAbstractInitialisation class created specifically to setup the Dataspace to be used
 * with Linked Data 
 *  
 * @author klitos
 */
public class MatcherServiceImplTest extends RDFAbstractIntegrationTest {
	
	private static Logger logger = Logger.getLogger(MatcherServiceImplTest.class);
	
	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	@Autowired
	@Qualifier("jenaJungRDFVisualisationService")
	private JenaJungRDFVisualisationService jenaJungRDFVisualisationService;
	
	private final List<MatcherService> matcherList = new ArrayList<MatcherService>();
	private Map<ControlParameterType, ControlParameter> controlParameters;
	
	private Schema testSchema1 = null;
	private Schema testSchema2 = null;	
	
	@Override
	@Before
	public void setUp() {
		super.setUp();

		/*Get the two test schemas from the schemaRepository*/
		testSchema1 = schemaRepository.getSchemaByName("mangatuneRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchema");
     }//end setUp()	
	
	
	/***
	 * Na sinexisw apo edw aurio, vasika o rdfsmatcher tha einai opws ton name mather epidi 
	 * 8a xrisimopoia diafora strategies gia na kamei match ta strings apo ta rdfs:labels,
	 * 8a xrisimopoia tous 2 primitive matchers edit-diastance or n-gram. pou simenei oti to 
	 * structure tis conny einai kalo, alla 8a prospa8isw na to 3anakamw me to AnnotationMatcherService.
	 * 
	 * 
	 * @throws ExecutionException
	 */
	
	
	
	/***
	 * This method is to test the functionality in organising the information from namespaces into a 
	 * semantic matrix.
	 * 
	 * @throws ExecutionException
	 */
	@Test
	public void semanticMetadataService() throws ExecutionException {
		/*Pre-requirement: run the schema enrichment process to dereference URIs and store them in an SDBStore*/
		/*Step 1: Create new matchers and then call the schemaService.runMatch()*/
		/*Step 2: runMatch() should return a similarity cube with all the [][] sim matrices from individual matchers*/
		/*Step 3: This step should call the organiseMetadata() to organise semantic information into matrices*/
		/*Step 4: Retrieve a sim cube of semantic matrices from Step 3*/
		/*Step 5: First strategy: is to aggregate the sim cube with an aggregation function e.x MAX and produce a [][] final sim
		 * matrix*/
		/*Step 6: Call a method in MatcherServiceImpl to do the final adjustment by using an adjustment function and considering
		 *the level of precedence of each semantic-matrix*/
		/*Step 7: This is the final step for selection of the matches, for this example use a threshold*/
		//-------------------/
		
		//Pre-requiremnt: DONE
		//Step 1-2: I will assume this step, by creating a sim-cube that holds data from two matchers
		//float[][][] individualMatchersCube = generateDummySimCube();
	
		//For testing before the meeting
		
		/** NEW CODE START FROM HERE **/
		/* Pre-requirement: */
		
		/* Step 1: Here add the matches to be used */
		//MatcherService levenshteinMatcher = new LevenshteinMatcherServiceImpl();
		/* 1.1: Define the SELECTION type for this matcher */
		//ControlParameter thresholdSelectionForLevenshteinMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				//SelectionType.THRESHOLD.toString()); 
		/* 1.2: Define the THRESHOLD VALUE for the selection type  */
		//ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* 1.3: Add control parameters to the matcher */
		//levenshteinMatcher.addControlParameter(thresholdSelectionForLevenshteinMatcher);
		//levenshteinMatcher.addControlParameter(thresholdForMatcher);		
		//schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
					

		//matcherList.add(levenshteinMatcher);
		

		
		
		
		
		
		//Step3: Assuming that we have the sim matrices of the matchers, then proceed to Step 3, call organiseMetadata():
		//3.1 - prepare the semanticMatrices to build
		//[SETUP]	
		
		/***
		 * [Configuration]
		 *  - Use a HierarchySemanticMatrix with child semantic matrices: (a) SubsumptionSemanticMatrix (b) ShareSuperClassSemMatrix 
		 */
		/*logger.debug("SDBStore: " + this.getMetadataSDBStore());
		HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(this.getMetadataSDBStore());
		//Add control parameters to the hierarchy Matrix
		ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
		hierarchySemMatrix.addControlParameter(doConflictRes);
		
		//Use the SubsumptionSemMatrix
		SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(this.getMetadataSDBStore());
		hierarchySemMatrix.attachSemMatrix(subSemMatrix);
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
		subSemMatrix.addControlParameter(useReasoner);
				
		//Use the ShareSuperClassSemMatrix
		ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(this.getMetadataSDBStore());
		hierarchySemMatrix.attachSemMatrix(superSemMatrix);
		ControlParameter useReasoner2 = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
		superSemMatrix.addControlParameter(useReasoner2);
				
		//3.2 - after configuring the semantic matrix to create, attach it to the queue to be created
		List<SemanticMetadataService> queue = new ArrayList<SemanticMetadataService>();
		queue.add(hierarchySemMatrix);
		
		//3.3 - Call the method
		schemaService.organiseMetadata(testSchema1, testSchema2, queue);
		
		//Step5: skip, because only one sim matrix and one sem matrix in this example
		schemaService.applyAdjustementFunctionsStrategyA(testSchema1, testSchema2);*/
		
		
	}//end semanticMetadataService()
	
	//---- START: this is a temp code ----//
	public float[][][] generateDummySimCube() {
		float[][][] individualMatchersCube = new float[2][][];
		float[][] matcher1 = new float[30][37];
		float[][] matcher2 = new float[30][37];
		
		//Fill the [][] sim matrices with dummy numbers
		for (int i=0; i< matcher1.length; i++) {
			for (int j=0; j<matcher1[i].length; j++) {
				matcher1[i][j] = generateRandom(1.0F, 0.0F);
			}
		}
		
		for (int i=0; i< matcher2.length; i++) {
			for (int j=0; j<matcher2[i].length; j++) {
				matcher2[i][j] = generateRandom(1.0F, 0.0F);
			}
		}
		
		//Display the arrays
		//logger.debug(display(matcher1));		
		//logger.debug(display(matcher2));
		
		//Add them to the sim-cube
		individualMatchersCube[0] = matcher1;
		individualMatchersCube[1] = matcher2;	
		
		return individualMatchersCube;
	}	
	
	public float generateRandom(float max, float min) {
		return (float) (Math.random() * ( max - min ));
	}
	
	public String display(float [][] m) {
		logger.debug("in display(): ");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0; i<m.length; i++) {
			for (int j=0; j<m[i].length; j++) {
				stringBuilder.append(" " + m[i][j]);
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}
	
	//---- END: this is a temp code ----//	
		
	@Test
	public void schemaServiceImpl() throws ExecutionException {
		/*Create a new matcher and then call the schemaService.runMatch()*/
		RDFSLabelMatcherServiceImpl rdfsLabelMatcher = new RDFSLabelMatcherServiceImpl();
		rdfsLabelMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl()); //This is the string based matcher strategy
		
		/*Add this matcher to the matcher List*/
		matcherList.add(rdfsLabelMatcher);
		
		//Call schemService not necessary to add control parameters
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
		
	}
	

	/**************************************
	 * Test Cases For Syntactic Matchers  *
	 *                                    *
	 *************************************/
	
	/***
	 * Test 1: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): Levenshtein
	 *		- Aggregation Strategy: SimMax, however is not used in this case because only one matcher is used.
	 *		- Selection Strategy: Select All
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());				
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService1()
	
	/***
	 * Test 2: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): NGram | with length of NGram = 3
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Select All
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changed according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());				
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService2()
	
	/***
	 * Test 3: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Select All
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService3() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());				
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService3()	
	
	/***
	 * Test 4: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimMim
	 *		- Selection Strategy: Select All
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService4() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMIN.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());				
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService4()	
	
	/***
	 * Test 5: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimAverage
	 *		- Selection Strategy: Select All
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService5() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMAVERAGE.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());				
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService5()		
		
	
	/***
	 * Test 6: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: Weighted (Ngram: 0.7 | Levenshtein 0.3)
	 *		- Selection Strategy: Select All
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService6() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMWEIGHTED.toString());
		ControlParameter nGramWeight  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1, "0.7");
		ControlParameter editDistanceWeight  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2, "0.3");		
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());				
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(nGramWeight);
		constructBasedMatcher.addControlParameter(editDistanceWeight);
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService6()	
	
	/***
	 * Test 1.1: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): Levenshtein
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Threshold 0.4
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService1_1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService()

	/***
	 * Test 1.2: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): Levenshtein
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Threshold 0.6
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService1_2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.6");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService7_2()
	
	/***
	 * Test 2.1: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): NGram | with length of NGram = 3
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Threshold 0.4
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService2_1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService2()	
	
	/***
	 * Test 2.2: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): NGram | with length of NGram = 3
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Threshold 0.6
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService2_2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.6");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService2()		
	
	/***
	 * Test 3.1: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Threshold 0.4
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService3_1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService3()
	
	/***
	 * Test 3.2: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimMax
	 *		- Selection Strategy: Threshold 0.6
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService3_2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.6");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService3()	
	
	/***
	 * Test 4.1: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimMim
	 *		- Selection Strategy: Threshold 0.4
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService4_1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMIN.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService4()	
	
	/***
	 * Test 4.2: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimMim
	 *		- Selection Strategy: Threshold 0.6
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService4_2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMIN.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.6");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService4()	
	
	/***
	 * Test 5: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimAverage
	 *		- Selection Strategy: Threshold 0.4
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService5_1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMAVERAGE.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService5()
	
	/***
	 * Test 5: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: SimAverage
	 *		- Selection Strategy: Threshold 0.6
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService5_2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMAVERAGE.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.6");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService5()	
	
	/***
	 * Test 6: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: Weighted (Ngram: 0.7 | Levenshtein 0.3)
	 *		- Selection Strategy: Threshold 0.4
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService6_1() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMWEIGHTED.toString());
		ControlParameter nGramWeight  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1, "0.7");
		ControlParameter editDistanceWeight  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2, "0.3");		
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());	
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.4");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(nGramWeight);
		constructBasedMatcher.addControlParameter(editDistanceWeight);
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService6()	
	
	/***
	 * Test 6: Test the creation of a ConstructBasedMatcherService, with the following configuration:
	 *  	- Child matcher(s): 
	 *  				1. NGram | with length of NGram = 3
	 *  				2. Levenshtein		
	 *		- Aggregation Strategy: Weighted (Ngram: 0.7 | Levenshtein 0.3)
	 *		- Selection Strategy: Threshold 0.6
	 *
	 * Result: Output as expected
	 * @throws ExecutionException
	 */	
	@Test
	public void testConstructBasedMatcherService6_2() throws ExecutionException {
		/* This is a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMWEIGHTED.toString());
		ControlParameter nGramWeight  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1, "0.7");
		ControlParameter editDistanceWeight  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2, "0.3");		
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changes according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());	
		/* Set value for Threshold selection type */
		ControlParameter thresholdForMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.6");
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(nGramWeight);
		constructBasedMatcher.addControlParameter(editDistanceWeight);
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		constructBasedMatcher.addControlParameter(thresholdForMatcher);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher*/
		schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
	}//end testConstructBasedMatcherService6()			
}//end JUnit Class
