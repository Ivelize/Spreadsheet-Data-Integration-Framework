package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractInitialisation;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.AggregationType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.ConstructBasedMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.LevenshteinMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.DomainSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.EquivalenceSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.HierarchySemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.NameSpaceSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.RangeSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ShareSuperClassSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SubsumptionSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.MeanSquaredError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RootMeanSquaredError;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.meta.SemanticMetadataService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;


//NOTE: Change the RDFAbstractInitialisation to an Abstract Test Class only for generating GT.
// This means that I need to create the new Abstract Test Class :)
//The class will load all the schemas at once.
//NOTE: Change this class to a test class that does the evaluation after reading the Alignment
//from the XML document
public class GenerateExpectationMatrixTest extends RDFAbstractInitialisation {
	
	private static Logger logger = Logger.getLogger(GenerateExpectationMatrixTest.class);
		
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;
		
	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("evaluatorService")
	private EvaluatorService evaluatorService;	
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	

	/**
	 * Location of the Schemas to be used to generate the Expectation Matrix
	 */
	private static String jamendoRDFPropsLoc = "./src/test/resources/datasources/JamendoRDFSource.properties";	
	private static String jamendoRDFmutationPropsLoc = "./src/test/resources/datasources/JamendoRDFmutation.properties";
	private static String jamendoGTAlignment = "./src/test/resources/training/jamendo_expMatrix.xml";
	
	private Schema testSchema1 = null;
	private Schema testSchema2 = null;	
	
	private final List<MatcherService> matcherList = new ArrayList<MatcherService>();
	private Map<ControlParameterType, ControlParameter> controlParameters;
	
	
	@Override
	@Before
	public void setUp() {
		super.setUp();	
	}//end setUp()	
	
	@Test
	public void testGenerateExpectationMatrix() throws ExecutionException, IOException {
		
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/**
		 * Experiment 1: This experiment will measure the 
		 */
		
		/*** Step1: Get an Expectation matrix ***/
		SemanticMatrix gtMatrix = schemaService.generateExpectationMatrix(testSchema1, testSchema2, jamendoGTAlignment);
		
		
		/*** Step2: Setup a Matcher and run it to get the syntactic Matrix that has the syntactic similarities ***/
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/*2.1 Set the control parameters of this matcher*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMMAX.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changed according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());	
		
		/*2.2 - Add the control parameters to the matcher*/
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		//constructBasedMatcher.addControlParameter(selectionTYPEthreshold);
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		/* Call schemaService runMatch() to run the matcher(s)*/
		List<MatcherInfo> syntacticCube = schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
		
		/*Step3: Measure the error between syntactic scores and expectation Matrix*/
		float[][] predictedMatrix = syntacticCube.get(0).getSimMatrix();
		float[][] observedMatrix = gtMatrix.getSemMatrixAsArray();
		

		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
		HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(this.getMetadataSDBStore());
		
		//Add control parameters to the hierarchy semantic matrix
		ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
		hierarchySemMatrix.addControlParameter(doConflictRes);	
		
		//4.1: HierarchySemanticMatrix - EquivalenceSemanticMatrix
		EquivalenceSemanticMatrix equiSemMatrix = new EquivalenceSemanticMatrix(this.getMetadataSDBStore());
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
		equiSemMatrix.addControlParameter(useReasoner);
		hierarchySemMatrix.attachSemMatrix(equiSemMatrix);
		
		//4.2: HierarchySemanticMatrix - SubsumptionSemanticMatrix
		SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(this.getMetadataSDBStore());
		subSemMatrix.addControlParameter(useReasoner);
		hierarchySemMatrix.attachSemMatrix(subSemMatrix);

		//4.3: HierarchySemanticMatrix - ShareSuperClassSemMatrix
		ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(this.getMetadataSDBStore());
		superSemMatrix.addControlParameter(useReasoner);
		hierarchySemMatrix.attachSemMatrix(superSemMatrix);
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(hierarchySemMatrix);
		
		//4.4: DomainSemanticMatrix
		DomainSemanticMatrix domainSemMatrix = new DomainSemanticMatrix(this.getMetadataSDBStore());
		//Add control parameters to the domain semantic matrix
		domainSemMatrix.addControlParameter(useReasoner);
		
		//Add domainSemMatrix to the queue
		semanticQueue.add(domainSemMatrix);
						
		//4.5: NameSpaceSemMatrix
		//NameSpaceSemMatrix nsSemMatrix = new NameSpaceSemMatrix();
		//Add control parameters to the ns semantic matrix
		//semanticQueue.add(nsSemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);
		
		//Print adjusted Matrix
		/** Graphviz used to visualise the set of syntactic matrices **/
		//graphvizDotGeneratorService.generateDOTSyn(sourceConstructs, targetConstructs, simCubeOfMatchers);
		
		
		/*** Run Evaluator Again ***/
		//Attach performance measures to use for calculating the errors
		evaluatorService.attactAggrErrMeasure( new MeanSquaredError() );
		evaluatorService.attactAggrErrMeasure( new RootMeanSquaredError() );
		
		evaluatorService.calculatePerformance(predictedMatrix, observedMatrix);
		
		
		
		
		//logger.debug("semanticCube: " + semanticCube);
		/*** END ***/
		
		
		//NOTE: This class can read in a mappings manually specified by the user. Use this class later on to do the whole cycle
		//to adjust or train if you like the Adj. functions.
		
	}//end testGenerateGroundTruth()	
	
	
	/***
	 * Below check each semantic matrix independently 
	 * @throws ExecutionException
	 */
	@Test
	public void testNameSpaceSemMatrix() throws ExecutionException {
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();		
		NameSpaceSemMatrix nsSemMatrix = new NameSpaceSemMatrix();
		//Add control parameters to the ns semantic matrix
		
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(nsSemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);		
	}	
	
	
	@Test
	public void testDomainSemanticMatrix() throws ExecutionException {
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
		
		DomainSemanticMatrix domainSemMatrix = new DomainSemanticMatrix(this.getMetadataSDBStore());
		//Add control parameters to the domain semantic matrix
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters	
		domainSemMatrix.addControlParameter(useReasoner);
		
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(domainSemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);		
	}	
	
	@Test
	public void testRangeSemanticMatrix() throws ExecutionException {
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
		
		RangeSemanticMatrix rangeSemMatrix = new RangeSemanticMatrix(this.getMetadataSDBStore());
		//Add control parameters to the range semantic matrix
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters	
		rangeSemMatrix.addControlParameter(useReasoner);
		
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(rangeSemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);		
	}	
	
	@Test
	public void testShareSuperClassSemMatrix() throws ExecutionException {
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
		HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(this.getMetadataSDBStore());
		
		//Add control parameters to the hierarchy semantic matrix
		ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
		hierarchySemMatrix.addControlParameter(doConflictRes);	
		
		//4.1: HierarchySemanticMatrix - ShareSuperClassSemMatrix
		ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(this.getMetadataSDBStore());
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
		superSemMatrix.addControlParameter(useReasoner);
		hierarchySemMatrix.attachSemMatrix(superSemMatrix);
		
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(hierarchySemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);		
	}
	
	
	@Test
	public void testSubsumptionSemanticMatrix() throws ExecutionException {
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
		HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(this.getMetadataSDBStore());
		
		//Add control parameters to the hierarchy semantic matrix
		ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
		hierarchySemMatrix.addControlParameter(doConflictRes);	
		
		//4.1: HierarchySemanticMatrix - SubsumptionSemanticMatrix
		SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(this.getMetadataSDBStore());
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
		subSemMatrix.addControlParameter(useReasoner);
		hierarchySemMatrix.attachSemMatrix(subSemMatrix);
			
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(hierarchySemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);
		
	}//end method
	
	@Test
	public void testEquivalenceSemanticMatrix() throws ExecutionException {
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			DataSource jamendoRDFSource 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());			
		}//end if
		
		//Load Schema: S'
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmplPrime","jamendoRDFSchemaPrime") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFmutationPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the data, not needed for this test
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
			
			 DataSource jamendoRDFSourcePrime 
			 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
					 passWord, isRDFSource);
			
			currentDataspace.addDataSource(jamendoRDFSourcePrime);
			currentDataspace.addSchema(jamendoRDFSourcePrime.getSchema());
		
			logger.debug("TEST_RDF_Source : " + jamendoRDFSourcePrime);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSourcePrime.getSchema());			
		}//end if*/

		//Get the schemas
		testSchema1 = schemaRepository.getSchemaByName("jamendoRDFSchema");
		testSchema2 = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/*** Step 4: Choose what meta-data to collect and add them to the queue ***/
		List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
		HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(this.getMetadataSDBStore());
		
		//Add control parameters to the hierarchy semantic matrix
		ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
		hierarchySemMatrix.addControlParameter(doConflictRes);	
		
		//4.1: HierarchySemanticMatrix - EquivalenceSemanticMatrix
		EquivalenceSemanticMatrix equiSemMatrix = new EquivalenceSemanticMatrix(this.getMetadataSDBStore());
		ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
		equiSemMatrix.addControlParameter(useReasoner);
		hierarchySemMatrix.attachSemMatrix(equiSemMatrix);
		
		//Add them to the queue of semantic matrices to be created, the next method is used for creating the semantic matrices
		semanticQueue.add(hierarchySemMatrix);
		
		//Call method responsible of organising meta-data into semantic matrices
		List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);
	}


}//end class
