package uk.ac.manchester.dstoolkit.service.morphisms.matching.RDF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractInitialisation;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.AggregationType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.BayesianMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.ConstructBasedMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.LevenshteinMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.BenchmarkType;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.MatchingServiceBenchmarkImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.PlotType;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

/***
 * This class is the same experiment like the RDFSchemaServiceMatchingComaBayesExp1.class however, instead
 * of producing a top k % graph, we produce a *top k* graph. Where k is the size of the bins we would like 
 * to have.
 * 
 * Experimental Tasks:
 *  [1] To justify the correctness of our framework (at the syntactic level) we compared the results of only considering
 *  syntactic evidences and assimilating them (from different matchers, that have been run independently), and compared
 *  them with the COMA++ best aggregation strategy (AVG).
 *  [2] We would like to assimilate *semantic evidence* and measure the effect on Precision/Recall in contrast with the COMA
 *  approach. The configuration of this experiment has an incompatibility issue because it plots the top-k Precision/Recall
 *  where COMA is using similarity scores [we fix that].
 *  [3] 
 */
public class RDFSchemaServiceMatchingComaBayesExp2 extends RDFAbstractInitialisation {
	
	private static Logger logger = Logger.getLogger(RDFSchemaServiceMatchingComaBayesExp2.class);
	
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
	 * Location of the Schemas to match
	 */
	private static String jamendoRDFPropsLoc = "./src/test/resources/datasources/JamendoRDFSource.properties";	
	private static String jamendoRDFmutationPropsLoc = "./src/test/resources/datasources/JamendoRDFmutation.properties";
	
	/*Benchmark properties, read the URI of the named graph that will hold the alignment*/
	private static String alignmentPropLoc = "./src/test/resources/training/benchmark/exp1.benchmark";
	
	/*Property file that holds a list of configuration files to run the Bayesian framework*/
	private static String runConfProp = "./src/test/resources/runConf/exp1run.properties";	
	
	/*Configuration files, load the URIs of the local RDF named graphs*/
	private static String trainModelsPropLoc = "./src/main/resources/datasources/train.properties";
	
	private final List<MatcherService> matcherList = new ArrayList<MatcherService>();
	private Map<ControlParameterType, ControlParameter> controlParameters;
	
	/*Hold a reference to the TDBStore*/
	private TDBStoreServiceImpl tdbStore = null;

	/*Hold a reference to the schemata to match*/
	private Schema sourceSchema = null;
	private Schema targetSchema = null;	

	/*Properties for ./src/main/resources/datasources/train.properties*/
	protected static String DATA_ANALYSIS;
	protected static String	EVID_CLASSES;
	protected static String EVID_PROPS;
	protected static String ENDPOINT_DATA;
	
	/*Properties for ./src/test/resources/training/benchmark/benchmark.properties*/
	protected static String ALIGN_GRAPH;
	protected static String CLASSIFY_TP_SYN;
	protected static String CLASSIFY_FP_SYN;	
	protected static String CLASSIFY_TP_SYN_SEM;
	protected static String CLASSIFY_FP_SYN_SEM;
		
	@Override
	@Before
	public void setUp() {
		super.setUp();	
	}//end setUp()	
	
	
	/***	
	 * [1] Experimental Task 2 (from slides)
	 * 
	 * This experiment takes two Schemas (S) and (S') and then uses a configuration file to run the following:
	 *   - syntactically match schemas and aggregate them using COMA average strategy
	 *   - select all the matches with all similarity scores (all means even the ones with sim score = 0) 
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall etc and plot top k matches
	 *   THEN
	 *   - runs again the syntactic matchers for the Bayesian approach
	 *   - uses the PDFs to convert the syntactic matchers to degrees of belief
	 *   - select all the matches with degree of belief even those that have dof of 0.0096
	 *                                          which is the equivalent of 0 for sim score.
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall etc. and plot top k matches
	 *   
	 * This is the experiment where we prove that aggregating with COMA AVG or Bayes is extremely 
	 * similar. This proves that the approach that we have for converting similarity scores is
	 * sound and it is not compromised at all. */
	@Test
	public void testMatchingAggregationPeformance() throws ExecutionException, IOException {
		/****
		 * STEP 1: Load the schemas to match using Baye's
		 */
		
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
										
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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
										
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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

		//Get the schemas (source schema and target schema)
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/***************************************
		 * [1] Setup COMA++ with:
		 *     - SELECT STRATEGY: Select ALL
		 *     - AGGREGATE STRATEGY: AVG
		 */
		
		/* Setup a combined matcher, add child matchers to the combined matcher */
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
		
		//Control parameters
		controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		
		//Call schemaService and run the Match phase of the Dataspace, not necessary to add control parameters
		List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(sourceSchema, targetSchema, matcherList, controlParameters);
		
		//Call schemaService to produce and make matches persistent, also rank the matches in descending order. Now is necessary 
		//so I can get the position of each match right
		ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
																					ControlParameterType.RANKING_DESC.toString());
		controlParameters.put(rankingType.getName(), rankingType);		
		List<Matching> matches = schemaService.produceAndSaveMatchings(sourceSchema, targetSchema, simCubeOfMatchers, controlParameters);

		logger.debug("** Running COMA++ framework **");
		
		/*Get the reference to the TDB store*/		
		tdbStore = this.getTDBStore();
		
		/*** > SETUP the BENCHMARK of this EXPERIMENT and run it < ***/
		
		/*Prepare the system for training by loading the property file: ./src/test/resources/training/benchmark/exp1.benchmark*/
		Map<String, String> alignMap = this.loadConfigAlignments(alignmentPropLoc);
		
		//Create a MatchingServiceBenchmarkImpl service for the COMA Approach
		MatchingServiceBenchmarkImpl benchmarkServiceCOMA = new MatchingServiceBenchmarkImpl(BenchmarkType.COMA_APPROACH, tdbStore,
																								ALIGN_GRAPH, CLASSIFY_TP_SYN, CLASSIFY_FP_SYN,
																								CLASSIFY_TP_SYN_SEM, CLASSIFY_FP_SYN_SEM);
		benchmarkServiceCOMA.resetAll(); //Reset the named graphs necessary for the experiment
		benchmarkServiceCOMA.addAlignments(alignMap); //List of the alignment files, although here a single alignment file is used
		benchmarkServiceCOMA.runTopKMatchesAggregationExperiment(matches, 5, null); //Run the experiment [matches, top k]
		
						
		/***************************************
		 * [2] Run the Bayesian technique:
		 *  	- Assimilate only syntactic evidences from syntactic matchers.
		 *   
		 * 		Note: in this framework similarity between constructs is captured as degrees of belief on
		 * 		the equivalence / non-equivalence of the two constructs. 
		 * 
		 * 		For my Thesis: This experiment can evaluate the approach in terms of syntactic evidence only from localnames
		 */
		
		logger.debug("** Running the Bayesian framework **");
		
		//Read the property file with the list of conf files to run: ./src/test/resources/runConf/exp1run.properties 
		InputStream runConf = new FileInputStream(runConfProp);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
		
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
				
				//Call EvaluatorService - Run the method for each configuration file - when considering all cells for measuring the error
				SemanticMatrix matrixSynOnly = evaluatorService.runBayesFromConfigFilesTopK(filePath, sourceSchema, targetSchema);	
		
				/**
				 * The Bayesian framework can be treated as a different way of performing the matching task and therefore 
				 * the BayesianMatcherServiceImpl implements a MatcherService for the approach as if it where a standard 
				 * matcher
				 */				
				BayesianMatcherServiceImpl bayesianMatcher = new BayesianMatcherServiceImpl(matrixSynOnly);
				
				/*Select all the matches, even those with dof equal to 0. If I need to use a selection strategy, setup this
				 * matcher with the same way as standard matchers */
				bayesianMatcher.addControlParameter(selectionTYPE);
				bayesianMatcher.addControlParameter(rankingType);
				
				/* Having a separate service for the bayesianMatcher I am then able to perform selection, aggregation, produce matches etc.
				   as having an ordinary Matcher service. */
				List<Matching> matchesFromBayes = bayesianMatcher.produceAndSaveMatches(sourceSchema, targetSchema);

				//Create a new MatchingServiceBenchmarkImpl service for the Bayesian Approach
				MatchingServiceBenchmarkImpl benchmarkServiceBayes = new MatchingServiceBenchmarkImpl(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY, tdbStore,
																										ALIGN_GRAPH, CLASSIFY_TP_SYN, CLASSIFY_FP_SYN,
																										CLASSIFY_TP_SYN_SEM, CLASSIFY_FP_SYN_SEM);
				benchmarkServiceBayes.resetAll(); //Reset the named graphs necessary for the experiment
				benchmarkServiceBayes.addAlignments(alignMap); //List of the alignment files, although here a single alignment file is used
				benchmarkServiceBayes.runTopKMatchesAggregationExperiment(matchesFromBayes, 5, null); //Run the experiment [matches, top k]
			
			}//end if
		}//end for		
	}//end test
		
	/***
	 * [2] Experimental Task 2 (from slides): This experiment will add *semantic evidence* as well
	 * 
	 * This experiment takes two Schemas (S) and (S') and then uses a configuration file to run the following:
	 *   - syntactically match schemas and aggregate them using COMA average strategy
	 *   - select all the matches with similarity score >= 0 (all)
	 *   - rank the matches in descending order (this might not be necessary since this is executed using SPARQL)
	 *   - measure Precision/Recall and plot top k 
	 *   then
	 *   - runs again the syntactic matchers 
	 *   - uses the PDF to convert the syntactic matchers to degrees of belief (Returns a SemanticMatrix)
	 *   - select all the matches with degree of belief >= 0 (all)
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall and plot top k 
	 *   then 
	 *   - get the SemanticMatrix corresponding to the assimilation of Syntactic evidences and apply
	 *     combinations of *semantic evidences* (means that the SemanticMatrix needs to be cloned)
	 *   - for each combination of semantic evidence 
	 *   	- create a new BayesianMatcherServiceImpl which will produce the final matches and rank them
	 *		- use the matches to calculate Precision/Recall
	 *
	 */
	@Test
	public void testMatchingTopKPeformanceWithSemanticEvidence() throws ExecutionException, IOException {
		/****
		 * STEP 1: Load the schemas to match using Baye's
		 */
		
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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
	
									
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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

		//Get the schemas (source schema and target schema)
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/**************************************************************************************************
		 * Run the Bayesian technique :
		 * 	    - Firstly match the schemas and obtain a similarity cube.
		 * 		- Use a strategy to aggregate the cube into a single matrix. This is the COMA result.
		 * 		- For the COMA result calculate Precision/Recall.
		 * 
		 * 		- Then proceed with the Bayesian assimilation of evidence.
		 *  	- Assimilate syntactic evidences from syntactic matchers.
		 *      - Then assimilate semantic evidences available.
		 *   
		 * 		Note: in this framework similarity between constructs is captured as degrees of belief on
		 * 		the equivalence / non-equivalence of the two constructs. 
		 */
		
		logger.debug("** Running the Bayesian framework **");		
		
		/* Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties		
		this property file holds the uris of the local named graphs - Named Graphs that perform the counts for the pmf*/
		this.loadConfigTraining(trainModelsPropLoc);	
		
		/** The Bayesian framework needs to be configured by a property file. We point to a property file
		 * that can point to *many* different configuration files, the property file can be found in this location:
		 * ./src/test/resources/runConf/exp1run.properties -> run1Exp1 **/
		InputStream runConf = new FileInputStream(runConfProp);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);

		/* Setup some control parameters before running the evaluator Service, the parameters below control the type of
		 * assimilating semantic evidence, either aggregating using COMA or aggregating using Bayes. Also we setup the
		 * type of plot we would like to output */
		Map<ControlParameterType, ControlParameter> serviceParameters = new HashMap<ControlParameterType, ControlParameter>();
		ControlParameter benchmarkType = new ControlParameter(BenchmarkType.COMA_APPROACH);
		ControlParameter plotType = new ControlParameter(PlotType.PLOT_TOP_K_PRECISION_RECALL);				
		
		/** Add the parameters before calling the EvaluatorService **/ 
		//We choose to assimilate syntactic evidence using: COMA AVG in this case
		serviceParameters.put(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION, benchmarkType);
		//We choose to PLOT the TOP_K_PRECISION_RECALL
		serviceParameters.put(ControlParameterType.PLOT_TYPE, plotType);
		
		/***
		 * From the property file, the approach is possible to run multiple configurations
		 */
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
								
				/* Run the method for each configuration file. */
				evaluatorService.runBayesFromConfigFilesTopKrecursive(filePath, sourceSchema, targetSchema,
																	  alignmentPropLoc,
																	  this.getMetadataSDBStore(), this.getTDBStore(),
																	  DATA_ANALYSIS, EVID_CLASSES, EVID_PROPS, ENDPOINT_DATA,
																	  serviceParameters);
			
			}//end if
		}//end for	
	}//end testMatchingTopKPeformanceWithSemanticEvidence()	
	
	/***
	 * [3] Experimental Task 2 (from slides): This experiment will add *semantic evidence* as well
	 * 
	 * This experiment takes two Schemas (S) and (S') and then uses a configuration file to run the following:
	 *   - syntactically match schemas and aggregate them using Bayes   
	 *   - select all the matches with similarity score >= 0 (all)
	 *   - rank the matches in descending order
	 *   then
	 *   - runs again the syntactic matchers 
	 *   - uses the PDF to convert the syntactic matchers to degrees of belief (Returns a SemanticMatrix)
	 *   - select all the matches with degree of belief >= 0 (all)
	 *   - rank the matches in descending order
	 *   then 
	 *   - get the SemanticMatrix corresponding to the assimilation of Syntactic evidences and apply
	 *     combinations of *semantic evidences* (means that the SemanticMatrix needs to be cloned)
	 *   - for each combination of semantic evidence 
	 *   	- create a new BayesianMatcherServiceImpl which will produce the final matches and rank them
	 *		- use a set of selected matches to print Difference in position between individuals
	 *
	 */
	@Test
	public void testMatchingTopKPeformanceWithSemanticEvidence2() throws ExecutionException, IOException {
	
		/***
		 * Get the configuration from here:
		 */
		
		/*Property file that holds a list of configuration files to run the Bayesian framework*/
		String runConfTopK = "./src/test/resources/runConf/expTopK.properties";	
		
		/****
		 * STEP 1: Load the schemas to match using Baye's
		 */
		
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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
	
									
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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

		//Get the schemas (source schema and target schema)
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/**********************************************************************
		 * Evaluate the Bayesian technique:
		 * 	Part A:  
		 */
		
		logger.debug("** Running the Bayesian framework **");		
		
		/* Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties		
		this property file holds the uris of the local named graphs - Named Graphs that perform the counts for the pmf*/
		this.loadConfigTraining(trainModelsPropLoc);	
		
		/** The Bayesian framework needs to be configured by a property file. We point to a property file
		 * that can point to *many* different configuration files, the property file can be found in this location:
		 * ./src/test/resources/runConf/expTopK.properties -> run?TopK.conf **/
		InputStream runConf = new FileInputStream(runConfTopK);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
		
		/***
		 * In the previous experiment I have used COMA approach to aggregate and generate the matches. The result of
		 * each match is an aggregated similarity score and then I calculate the Precision/Recall. This is not right 
		 * because I am then comparing the Precision/Recall of the top-k graph with the Bayesian approach. Since in a
		 * previous experiment I have already proved that matching with Bayes or COMA is almost the same I can aggregate 
		 * with Bayes. The following parameter will run the approach making sure of the following:
		 *   - At the beginning, aggregate just syntactic similarity using Bayes. Then calculate Precision/Recall.
		 *   - Then, assimilate different kinds of semantic evidence, then calculate Precision/Recall. 
		 *   
		 * The above is set with the BenchmarkType as shown below  
		 */
		
		/* Setup some control parameters before running the evaluator Service, the parameters below control the type of
		 * assimilating semantic evidence, either aggregating using COMA or aggregating using Bayes. Also we setup the
		 * type of plot we would like to output */
		Map<ControlParameterType, ControlParameter> serviceParameters = new HashMap<ControlParameterType, ControlParameter>();
		ControlParameter benchmarkType = new ControlParameter(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY);		
		ControlParameter plotType = new ControlParameter(PlotType.PLOT_TOP_K_PRECISION_RECALL);				
	
		/* Add the parameters */ 
		//Use Bayes instead of COMA to assimilate the syntactic evidence
		serviceParameters.put(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION, benchmarkType);
		serviceParameters.put(ControlParameterType.PLOT_TYPE, plotType);	
		
		/***
		 * From the property file, the approach is possible to run multiple configurations
		 */
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
				
				/* Run the method for each configuration file. */
				evaluatorService.runBayesFromConfigFilesTopKrecursive(filePath, sourceSchema, targetSchema,
																	  alignmentPropLoc,
																	  this.getMetadataSDBStore(), this.getTDBStore(),
																	  DATA_ANALYSIS, EVID_CLASSES, EVID_PROPS, ENDPOINT_DATA,
																	  serviceParameters);
		
			}//end if
		}//end for	
	}//end testMatchingTopKPeformanceWithSemanticEvidence2	
	
	
	/***
	 * [4] Description of experiment: 
	 *  - In this experiment the Bayesian framework is used to assimilate syntactic evidence from two string-based matchers
	 *    namely edit-distance and n-gram. The matches are then stored in a triple store and ordered in Descending order, the 
	 *    position of each match that has only syntactic evidence is then stored in the triple store as well. We refer to position
	 *    as 'purely syntactic position'.
	 *    
	 *  - Then, a List of semantic evidence is given to the framework, then the framework assimilates the semantic evidence in
	 *    different combinations. This produces another list of matches that this time have assimilated semantic evidence. These
	 *    matches are then made persistent and their position is stores. We refer to position as 'semantically informed position' 
	 * 
	 *  - The purpose of this experiment is to plot the:
	 *     1. Difference in positions: 'semantically informed position' vs. 'purely syntactic position' and observe
	 *    the difference. 
	 *     2. Difference in degrees of belief: 'dob of the semantically informed' vs. 'dob of syntactic'
	 *    
	 *  - Notice that we only plot the individuals that have the combination of semantic evidence provided.
	 *  
	 *  Configuration: 
	 *    - We use Bayes to aggregate syntactic evidence from matches instead of using COMA.  
	 *    - We then use Bayes to assimilate semantic evidence available given a list of evidence to assimilate.
	 *    
	 * @throws ExecutionException
	 * @throws IOException
	 */
	@Test
	public void testMatchingDifferenceDExperiment() throws ExecutionException, IOException {
		
		/***
		 * Get the configuration file for this experiment:
		 */
		
		/*Property file that holds a list of configuration files to run the Bayesian framework*/
		String runConfDiffD = "./src/test/resources/runConf/expDiffD.properties";	
		
		
		/****
		 * STEP 1: Load the schemas to match using Baye's
		 */
		
		//Load Schema: S
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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
	
									
			/*IMPORTANT: Here we do not load the instance data, not needed for this test
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

		//Get the schemas (source schema and target schema)
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
		
		/**********************************************************************
		 * Evaluate the Bayesian technique:
		 * 	Part A:  
		 */
		
		logger.debug("** Running the Bayesian framework - PLOT_DIFFERENCE_D **");		
		
		/* Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties		
		this property file holds the uris of the local named graphs - Named Graphs that perform the counts for the pmf*/
		this.loadConfigTraining(trainModelsPropLoc);	
		
		/** The Bayesian framework needs to be configured by a property file. We point to a property file
		 * that can point to *many* different configuration files, the property file can be found in this location:
		 * ./src/test/resources/runConf/expDiffD.properties -> run1DiffD **/
		InputStream runConf = new FileInputStream(runConfDiffD);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
		
		/***
		 * In the previous experiment I have used COMA approach to aggregate and generate the matches. The result of
		 * each match is an aggregated similarity score and then I calculate the Precision/Recall. This is not right 
		 * because I am then comparing the Precision/Recall of the top-k graph with the Bayesian approach. Since in a
		 * previous experiment I have already proved that matching with Bayes or COMA is almost the same I can aggregate 
		 * with Bayes. The following parameter will run the approach making sure of the following:
		 *   - At the beginning, aggregate just syntactic similarity using Bayes. Then calculate Precision/Recall.
		 *   - Then, assimilate different kinds of semantic evidence, then calculate Precision/Recall. 
		 *   
		 * The above is set with the BenchmarkType as shown below  
		 */
		
		/* Setup some control parameters before running the evaluator Service, the parameters below control the type of
		 * assimilating syntatic evidence, either aggregating using COMA or aggregating using Bayes. Also we setup the
		 * type of plot we would like to output */
		Map<ControlParameterType, ControlParameter> serviceParameters = new HashMap<ControlParameterType, ControlParameter>();
		ControlParameter benchmarkType = new ControlParameter(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY);

		//Plot mode: PLOT_DIFFERENCE_D
		//This is both difference D in positions and degree of belief
		ControlParameter plotType = new ControlParameter(PlotType.PLOT_DIFFERENCE_D);
				
		/* Add the parameters */ 
		serviceParameters.put(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION, benchmarkType);
		serviceParameters.put(ControlParameterType.PLOT_TYPE, plotType);	
		
		/***
		 * From the property file, the approach is possible to run multiple configurations
		 */
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
				
				/* Run the method for each configuration file. */
				evaluatorService.runBayesFromConfigFilesTopKrecursive(filePath, sourceSchema, targetSchema,
																	  alignmentPropLoc,
																	  this.getMetadataSDBStore(), this.getTDBStore(),
																	  DATA_ANALYSIS, EVID_CLASSES, EVID_PROPS, ENDPOINT_DATA,
																	  serviceParameters);
		
			}//end if
		}//end for	
	}//end testMatchingDifferenceDExperiment()	
	
	
	/***
	 * Load alignments property file ./src/test/resources/training/benchmark/exp1.benchmark
	 * @param filePath
	 */
	protected Map<String, String> loadConfigAlignments(String filePath) {
		logger.debug("Loading alignments..." + filePath);
		Map<String, String> alignMap = null;		
		try {
			alignMap = new HashMap<String, String>();
			InputStream propertyStream = new FileInputStream(filePath);
			Properties alignProps = new java.util.Properties();
			alignProps.load(propertyStream);			
			ALIGN_GRAPH = alignProps.getProperty("ground_truth");
			//For only syntactic evidence either by aggregating with COMA or Bayesian use the following graphs
			CLASSIFY_TP_SYN = alignProps.getProperty("train_classify_tp_syn");  
			CLASSIFY_FP_SYN = alignProps.getProperty("train_classify_fp_syn");
			
			//For when syntactic & semantic evidence is assimilated using Bayes
			CLASSIFY_TP_SYN_SEM = alignProps.getProperty("train_classify_tp_syn_sem");  
			CLASSIFY_FP_SYN_SEM = alignProps.getProperty("train_classify_fp_syn_sem");
			
			//Load alignment file location
			for(String key : alignProps.stringPropertyNames()) {
				if (key.contains("alignFile")) {
				  String value = alignProps.getProperty(key);
				  alignMap.put(key, value);
				}//end if
			}//end for 			 
		 } catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 } catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		 }//end catch
		return alignMap;				
	}//end loadConfigTraining()	
	
	 /* This property file holds the names of the named graphs used for the construction of semantic evidence
	 * likelihoods: 
	 * 		data_analysis = x-ns://train.metadata/analysis/data
	 * 		evid_classes = x-ns://train.metadata/evidence/classes
	 * 		evid_props = x-ns://train.metadata/evidence/props
	 * 		endpoint_data_uri = x-ns://train.metadata/endpoint/data
	 * 
	 * Load properties file ./src/main/resources/datasources/train.properties
	 * @param fileName
	 */
	protected void loadConfigTraining(String filePath) {
	 try {
		 logger.debug("in loadConfTraining:" + filePath);
		 InputStream propertyStream = new FileInputStream(filePath);
		 Properties connectionProperties = new java.util.Properties();
		 connectionProperties.load(propertyStream);
		 DATA_ANALYSIS = connectionProperties.getProperty("data_analysis");
		 EVID_CLASSES = connectionProperties.getProperty("evid_classes");
		 EVID_PROPS = connectionProperties.getProperty("evid_props");
		 ENDPOINT_DATA = connectionProperties.getProperty("endpoint_data_uri");		 
	 	} catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 	exc.printStackTrace();
		 } catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		  	ioexc.printStackTrace();
		 }//end catch
	 }//end loadConfigTraining()	
}//end class