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
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

/***
 * Experimental Task 2 (from slides)
 * 
 * This experiment takes two Schemas (S) and (S') and then uses a configuration file to run the following:
 *   - syntactically match schemas and aggregate them using COMA average strategy
 *   - select all the matches with similarity score > 0
 *   - rank the macthes in descending order
 *   - measure Precision/Recall and plot top k % matches
 *   then
 *   - runs again the syntactic matchers 
 *   - uses the PDF to convert the syntactic matchers to degrees of belief
 *   - select all the matches with degree of belief > 0 
 *   - rank the matches in descending order
 *   - measure Precision/Recall and plot top k % matches
 */
public class RDFSchemaServiceMatchingComaBayesExp1 extends RDFAbstractInitialisation {
	
	private static Logger logger = Logger.getLogger(RDFSchemaServiceMatchingComaBayesExp1.class);
	
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
	
	private final List<MatcherService> matcherList = new ArrayList<MatcherService>();
	private Map<ControlParameterType, ControlParameter> controlParameters;
	
	/*Hold a reference to the TDBStore*/
	private TDBStoreServiceImpl tdbStore = null;

	/*Hold a reference to the schemata to match*/
	private Schema sourceSchema = null;
	private Schema targetSchema = null;	
	
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
	 * Experimental Task 2 (from slides)
	 * 
	 * This experiment takes two Schemas (S) and (S') and then uses a configuration file to run the following:
	 *   - syntactically match schemas and aggregate them using COMA average strategy
	 *   - select all the matches with similarity score > 0.0 (using a threshold)
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall and plot top k % matches
	 *   then
	 *   - runs again the syntactic matchers 
	 *   - uses the PDF to convert the syntactic matchers to degrees of belief
	 *   - select all the matches with degree of belief > 0 (using a threshold)
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall and plot top k % matches
	 */
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
		 * 
		 */
		
		/* Setup a combined matcher, add child matchers to the combined matcher */
		ConstructBasedMatcherService constructBasedMatcher = new ConstructBasedMatcherServiceImpl();
		constructBasedMatcher.addChildMatcher(new NGramMatcherServiceImpl(3));
		constructBasedMatcher.addChildMatcher(new LevenshteinMatcherServiceImpl());
		/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
		ControlParameter aggregationTYPE  = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE,
				AggregationType.SIMAVERAGE.toString());
		/* Also set the Selection Type for the matcher. All the matches will be selected because the syntactic matrix will then
		 * be changed according to information from the semantic matrices */
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.SELECT_ALL.toString());	
		
		
		/* Add control parameters to the matcher */
		constructBasedMatcher.addControlParameter(aggregationTYPE);
		constructBasedMatcher.addControlParameter(selectionTYPE);
		
		/* Add the matcher to the matcher list */
		matcherList.add(constructBasedMatcher);
		
		//Control parameters
		controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		
		//Call schemaService and run the matcher phase of the Dataspace, not necessary to add control parameters
		List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(sourceSchema, targetSchema, matcherList, controlParameters);
		
		//Call schemaService to produce and make matches persistent, also rank the matches in descending order	
		ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
																					ControlParameterType.RANKING_DESC.toString());
		controlParameters.put(rankingType.getName(), rankingType);		
		List<Matching> matches = schemaService.produceAndSaveMatchings(sourceSchema, targetSchema, simCubeOfMatchers, controlParameters);

		logger.debug("** Running COMA++ framework **");
		
		/*Get the reference to the TDB store*/		
		tdbStore = this.getTDBStore();
		
		/*Setup the benchmark for this experiment and run it*/
		
		/*Prepare the system for training by loading the property file: ./src/test/resources/training/benchmark/exp1.benchmark*/
		Map<String, String> alignMap = this.loadConfigAlignments(alignmentPropLoc);

		//Create a MatchingServiceBenchmarkImpl service for the COMA Approach
		MatchingServiceBenchmarkImpl benchmarkServiceCOMA = new MatchingServiceBenchmarkImpl(BenchmarkType.COMA_APPROACH, tdbStore,
																								ALIGN_GRAPH, CLASSIFY_TP_SYN, CLASSIFY_FP_SYN,
																								CLASSIFY_TP_SYN_SEM, CLASSIFY_FP_SYN_SEM);
		
		benchmarkServiceCOMA.resetAll(); //Reset the named graph necessary for the experiment
		benchmarkServiceCOMA.addAlignments(alignMap); //List of the alignment files, although here a single alignment file is used
		benchmarkServiceCOMA.runTopKPercMatchesAggregationExperiment(matches, 10); //Run the experiment [matches, top k %]
		
						
		/***************************************
		 * [2] Run the Bayesian technique:
		 *  	- Assimilate only syntactic evidences from syntactic matchers.
		 *   
		 * 		Note: in this framework similarity between constructs is captured as degrees of belief on
		 * 		the equivalence / non-equivalence of the two constructs. 
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
				
				//Run the method for each configuration file - when considering all cells for measuring the error
				SemanticMatrix matrixSynOnly = evaluatorService.runBayesFromConfigFilesTopK(filePath, sourceSchema, targetSchema);	
		
				/**
				 * The Bayesian framework can be treated as a different way of performing the matching task and therefore 
				 * the BayesianMatcherServiceImpl implements a MatcherService for the approach as if it where a standard 
				 * matcher
				 */				
				BayesianMatcherServiceImpl bayesianMatcher = new BayesianMatcherServiceImpl(matrixSynOnly);
				
				/*Select all the matches, even those with dof equal to 0*/
				bayesianMatcher.addControlParameter(selectionTYPE);
				bayesianMatcher.addControlParameter(rankingType);
				
				/* Having a separate service for the bayesianMatcher I am then able to perform selection, aggregation, produce matches etc.
				   as having an ordinary Matcher service. */
				List<Matching> matchesFromBayes = bayesianMatcher.produceAndSaveMatches(sourceSchema, targetSchema);

            	//Create a new MatchingServiceBenchmarkImpl service for the Bayesian Approach
				MatchingServiceBenchmarkImpl benchmarkServiceBayes = new MatchingServiceBenchmarkImpl(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY, tdbStore,
																										ALIGN_GRAPH, CLASSIFY_TP_SYN, CLASSIFY_FP_SYN,
																										CLASSIFY_TP_SYN_SEM, CLASSIFY_FP_SYN_SEM);
				
				benchmarkServiceBayes.resetAll(); //Reset the named graph necessary for the experiment
				benchmarkServiceBayes.addAlignments(alignMap); //List of the alignment files, although here a single alignment file is used
				benchmarkServiceBayes.runTopKPercMatchesAggregationExperiment(matchesFromBayes, 10); //Run the experiment [matches, top k %]
			
			}//end if
		}//end for		
	}//end test
	
		
	/***
	 * Experimental Task 2 (from slides) : This experiment will add semantic evidences as well
	 * 
	 * This experiment takes two Schemas (S) and (S') and then uses a configuration file to run the following:
	 *   - syntactically match schemas and aggregate them using COMA average strategy
	 *   - select all the matches with similarity score > 0
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall and plot top k % matches
	 *   then
	 *   - runs again the syntactic matchers 
	 *   - uses the PDF to convert the syntactic matchers to degrees of belief (Returns a SemanticMatrix)
	 *   - select all the matches with degree of belief > 0 
	 *   - rank the matches in descending order
	 *   - measure Precision/Recall and plot top k % matches
	 *   then 
	 *   - get the SemanticMatrix and apply combinations of semantic evidences (means that the SemanticMatrix needs to be cloned)
	 *   - for each combination of semantic evidence 
	 *   	- create a new BayesianMatcherServiceImpl which will produce the final matches and rank them
	 *		- use the matches to calculate Precision/Recall
	 *
	 */
	
	
	
	
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
}//end class