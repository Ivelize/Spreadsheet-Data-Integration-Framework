package uk.ac.manchester.dstoolkit.service.morphisms.matching.RDF;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractInitialisation;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.LevenshteinMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.DomainSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.EquivalenceSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.HierarchySemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.NameSpaceSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.RangeSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ShareSuperClassSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SubsumptionSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.BenchmarkType;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.DampeningEffectPolicy;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.OntologiesServiceBenchmarkImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.PlotType;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.GaussianKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelCaseType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelDenstityEstimator;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing.LaplaceSmoothing;
import uk.ac.manchester.dstoolkit.service.impl.util.training.SemEvidenceDataAnalysisUtilImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.SemEvidenceTrainingUtilImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceErrorTypes;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RelativeSquaredError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RootRelativeSquaredError;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.meta.SemanticMetadataService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.StringBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

import com.hp.hpl.jena.ontology.OntDocumentManager;

/**************************************************************************************************
 * 
 * Empirical Evaluation of the approach using: NUMERIC EVALUATION
 * 
 * BAYES INFERENCE  ** MEGA TEST CLASS ** 
 * 
 * The idea is to use Bayes to accumulate evidences from both syntactic and semantic evidences. we can configure the 
 * following:
 *   - laplace no laplace transformation
 *   - kernel support/ no kernel support
 *   - mid point rule for the calculation of the probabilities
 *   
 * In this experiment we measure how close the matrices are from the *expectation matrix*. The expectation matrix has been
 * build by observing the testimonies from data integrators. In this experiment a performance measure like the Relative
 * absolute mean square is used to meaure the error. Other performance measure have been implemented as well.  
 * 
 * @author klitos
 *
 */
public class RDFSchemaServiceMatchingBayesTest extends RDFAbstractInitialisation {
	
	private static Logger logger = Logger.getLogger(RDFSchemaServiceMatchingBayesTest.class);
			
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
	
	/**
	 * Location of the local copy of the LOV RDF-graph that aggregates data from various namespaces,
	 * to be used for the construction of the likelihoods for the semantic evidences.
	 */
	private static String local_LOV1_graph = "./src/test/resources/sparql_endpoints/LOV_1.sparql";	
	private static String local_LOV2_graph = "./src/test/resources/sparql_endpoints/LOV_2.sparql";	
	
	/*Configuration files, load the URIs of the local RDF named graphs*/
	private static String trainModelsPropLoc = "./src/main/resources/datasources/train.properties";
	
	/*Properties for ./src/main/resources/datasources/train.properties*/
	protected static String DATA_ANALYSIS;
	protected static String	EVID_CLASSES;
	protected static String EVID_PROPS;
	protected static String ENDPOINT_DATA;
	
	/*Hold a reference to the TDBStore (for semantic metadata)*/
	private TDBStoreServiceImpl tdbStore = null;	
	
	private Schema sourceSchema = null;
	private Schema targetSchema = null;	
	
	private final List<MatcherService> matcherList = new ArrayList<MatcherService>();
	private Map<ControlParameterType, ControlParameter> controlParameters;
	

	@Override
	@Before
	public void setUp() {
		super.setUp();	
	}//end setUp()	
	
	/***
	 * This test has been implemented as an agent for testing the new functionality on matching linked data
	 * by accumulating evidence using Bayes considering both syntactic and semantic evidences. 
	 * 	- Syntactic evidences are given as probability distributions that they have been constructed by 
	 * 	  observing the behaviour of different matchers when matching pairs of constructs that are known 
	 *    to be equivalent/non-equivalent. Distributions for other different matchers can also be constructed
	 *    with a similar way. The probability distributions are modelled as probability density functions using
	 *    a non-parametric distribution technique that builds on Kernels (called Kernel Density Estimation)
	 *  
	 *  - Semantic evidence have been collected by dereferencing URIs to namespaces and organised in semantic
	 *    matrices. For each evidence collected a Probability Mass Function (PMF) is constructed by observing a 
	 *    sample on how LD vocabulary publishers publish their vocabularies. For that purpose we have materialised
	 *    a copy of the LOV SPARQL endpoint that has collected all the vocabularies used in the Linked open data 
	 *    cloud. We then search for equivalent constructs that are either classes or properties by issuing SPARQL
	 *    queries over the endpoint. Doing so we have been able to produce the PMF for every semantic evidence that 
	 *    we are organising. 
	 *    
	 * Experiment: to test our approach we have run it just by using only the evidence obtained from syntactic matchers
	 * on two schemas that have been generated syntheticaly, and then by using evidence from both syntactic and 
	 * semantic evidence. Our aim is to measure the error with the expectation matrix that has been constructed by 
	 * collecting answers from several data integrators. 
	 *     
	 * Test Scenario 1: Specify manually the list of semantic evidence to assimilate, implemented by testMatchingWithBayes()
	 * 					test case.	 
	 *
	 * Test Scenario 2: Measure the error for selected individuals to minimise the dampening effect using different combinations
	 * 					of semantic evidence. This is implemented by the testMatchingWithBayesCombinations() test case. 
	 *
	 */	
	@Test
	public void testMatchingWithBayes() throws ExecutionException, IOException {
		
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

		//Get the schemas
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");
				
		/*************************************************************************
		 * Experiment 1: Use Bayes to accumulate evidences
		 * 
		 * 		Setup:
		 *   		- Syntactic Matchers: nGram, Edit-distance
		 *   		- Kernel Density Estimation: WITH OUT Support [L,U]
		 *   		- Kernel Type: Gaussian (Normal)
		 */
		
		/**
		 * STEP 2: Prepare two StringBasedMatchers (Ngram & Edit-distance) over the constructs of the schemata
		 * 			 - Note that the matchers here are run individually and they are not child mathers within 
		 * 			   another matcher.
		 */
			logger.debug(">> Preparing StringBasedMatchers..");
			StringBasedMatcherService editDistMatcher = new LevenshteinMatcherServiceImpl();
			ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
																  SelectionType.SELECT_ALL.toString());		
			editDistMatcher.addControlParameter(selectionTYPE);

			StringBasedMatcherService nGramMatcher = new NGramMatcherServiceImpl(3); 
			nGramMatcher.addControlParameter(selectionTYPE);
					
			/**
			 * Attach a Probability Density Function (PDF) to each Matcher
			 */
		
			//For Edit-Distance
			GaussianKernel normalKernel = new GaussianKernel();
			double h_tp = 0.0970228; //we need the smoothing parameter, got this from Matlab, matlab uses some optimisation 
			KernelDenstityEstimator kdeNoSupport_tp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, normalKernel, h_tp);
			kdeNoSupport_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");
			ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(-2, 2, kdeNoSupport_tp, KernelCaseType.TP_CASE); //remember to run pdf.createIntegralVector();
			pdfTP.createIntegralVector(); //This is important to generate in advance the vector that holds the pre-computed integrals
			editDistMatcher.attachPdfTP(pdfTP); //Attach PDF to the Matcher
			
			double h_fp = 0.04;
			KernelDenstityEstimator kdeNoSupport_fp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, normalKernel, h_fp);
			kdeNoSupport_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_Levenshtein.dat");
			ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(-2, 2, kdeNoSupport_fp, KernelCaseType.FP_CASE);
			pdfFP.createIntegralVector(); //This is important generate the vector that holds the pre-computed integrals
			editDistMatcher.attachPdfFP(pdfFP); //Attach PDF to the Matcher
		
			//For nGram 
			h_tp = 0.0906237;
			kdeNoSupport_tp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, normalKernel, h_tp);
			kdeNoSupport_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_NGram.dat");
			pdfTP = new ProbabilityDensityFunction(-2, 2, kdeNoSupport_tp, KernelCaseType.TP_CASE);
			pdfTP.createIntegralVector();
			nGramMatcher.attachPdfTP(pdfTP);
		
			h_fp = 0.170286;
			kdeNoSupport_fp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, normalKernel, h_fp);
			kdeNoSupport_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_NGram.dat");
			pdfFP = new ProbabilityDensityFunction(-2, 2, kdeNoSupport_fp, KernelCaseType.FP_CASE);
			pdfFP.createIntegralVector();
			nGramMatcher.attachPdfFP(pdfFP);
			
			/* Add the matchers to the matcher list */
			matcherList.add(editDistMatcher);
			matcherList.add(nGramMatcher);
			
			//Call schemaService and run the matcher phase of the Dataspace, not necessary to add control parameters
			List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(sourceSchema, targetSchema, matcherList, controlParameters);
		
		/***
		 * STEP 2: Represent similarity scores derived by running a set of individual matchers as probabilities, use Bayes'
		 * to accumulate evidences from the syntactic matchers into a new matrix that holds the Posterior of each pair of constructs.
		 * This matrix represents the equivalence of constructs as probabilities instead of raw similarity scores by accumulating 
		 * evidences from two syntactic matchers (edit-distance and n-gram).
		 */
		
			SemanticMatrix accumSynEvidenceMatrix = schemaService.accumulateSyntacticEvidenceBayes(sourceSchema, targetSchema,
																									   simCubeOfMatchers, null);
			 		
			
			/***
			 * STEP 2.1: Hold matrix that considers only syntactic evidence with Bayes
			 * 			 The new array[][] created is an entirely new object and not a reference to an object
			 */
			float[][] matrixSynOnly = accumSynEvidenceMatrix.getSemMatrixAsArray();	
			
						
			
		/***
		 * STEP 3: Collect semantic evidences from RDF vocabularies (RDFS/OWL)
		 */
			
			/* Run schema enrichment method this method is responsible for dereferencing URIs and store them in an SDBStore
			   this is a pre-requirement for the approach to work, the enrichment data remain persistent to a relational
			   database therefore there is no need to call this method every time
			   //TODO: make sure that the schemaEnrichment method can read specific vocabularies from files
			   */
			
			//schemaService.schemaEnrichment(sourceSchema, targetSchema); //because I have the data do not run the enrichment process again
			
			
			/***
			 * Create a list that specifies which meta-data to collect, this is called the semantic queue list,
			 * the list will then be used to organised meta-data and derive the semCubeOfEvidences
			 */
			List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
			
			HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(this.getMetadataSDBStore());
			//Add control parameters to the hierarchy semantic matrix
			ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
			hierarchySemMatrix.addControlParameter(doConflictRes);
			
			//3.1: HierarchySemanticMatrix - EquivalenceSemanticMatrix
			EquivalenceSemanticMatrix equiSemMatrix = new EquivalenceSemanticMatrix(this.getMetadataSDBStore());
			ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
			equiSemMatrix.addControlParameter(useReasoner);
			hierarchySemMatrix.attachSemMatrix(equiSemMatrix);
			
			//3.2: HierarchySemanticMatrix - SubsumptionSemanticMatrix
			SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(this.getMetadataSDBStore());
			subSemMatrix.addControlParameter(useReasoner);
			hierarchySemMatrix.attachSemMatrix(subSemMatrix);
			
			//3.3: HierarchySemanticMatrix - ShareSuperClassSemMatrix
			ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(this.getMetadataSDBStore());
			superSemMatrix.addControlParameter(useReasoner);
			hierarchySemMatrix.attachSemMatrix(superSemMatrix);
			
			//Add hierarchySemMatrix to the queue of semantic matrices to be created
			semanticQueue.add(hierarchySemMatrix);
			
			//3.4: Other semantic matrices - NameSpaceSemMatrix
			NameSpaceSemMatrix nsSemMatrix = new NameSpaceSemMatrix();
			semanticQueue.add(nsSemMatrix);
			
			//3.5: Specific to properties (Domain / Range)
			DomainSemanticMatrix domainSemMatrix = new DomainSemanticMatrix(this.getMetadataSDBStore());
			domainSemMatrix.addControlParameter(useReasoner);
			semanticQueue.add(domainSemMatrix);
			
			RangeSemanticMatrix rangeSemMatrix = new RangeSemanticMatrix(this.getMetadataSDBStore());
			rangeSemMatrix.addControlParameter(useReasoner);
			semanticQueue.add(rangeSemMatrix);
						

			//Add more semantic matrices here to collect
			
			
			
			//Call method responsible of organising meta-data into semantic matrices
			List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(sourceSchema, targetSchema, semanticQueue);	
			
			
		/***
		 * STEP 4: Construct likelihoods for semantic evidence. The likelihoods for each semantic evidence (described as a Boolean
		 * variable) will be summarised in Contingency tables. An object that holds access to the contingency tables will then 
		 * pass as an argument to the schemaService.accumulateSemEvidenceBayes() method, and will be used by that method to update
		 * the posterior probabilities in the presence of semantic evidence available. New posteriors will assimilate both syntactic
		 * and semantic evidence.
		 */					
			
			//Hold a reference to the TDB store (for semantic metadata)		
			tdbStore = this.getTDBStore();
			
			//Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties
			//this property file holds the uris of the local named graphs
			this.loadConfigTraining(trainModelsPropLoc);
			
			//Import the RDF-graph that holds the LOV endpoint into a TDB Store
			loadRDFSourceUtilService.loadTDBStore(tdbStore, local_LOV1_graph, ENDPOINT_DATA);
			
			//Perform data Analysis over the RDF dump
			SemEvidenceDataAnalysisUtilImpl dataAnalysis = new SemEvidenceDataAnalysisUtilImpl(tdbStore, DATA_ANALYSIS, ENDPOINT_DATA);
			
			/* The method does the analysis and then makes it persistent as an RDF-graph, set arg to true to reset it */
			dataAnalysis.doDataAnalysis(false);			
			
			/* Within the sets created from the DataAnalysis search for evidences.
			 * This is the class that implements the SPARQL queries.
			 * */	
			SemEvidenceTrainingUtilImpl trainService = new SemEvidenceTrainingUtilImpl(tdbStore, DATA_ANALYSIS, EVID_CLASSES, EVID_PROPS, ENDPOINT_DATA);
									
							
			//Create the likelihoods for Classes - created once used many
			trainService.createTrainingSetClasses(false);		
			
			//Create the likelihoods for Properties - created once used many
			trainService.createTrainingSetProps(false);	

			//Get the PMF for both Classes and Properties, using Laplace Smoothing (aka add-one) Method with k = 1 and |classes| = 2
			LaplaceSmoothing laplaceSmoothing = new LaplaceSmoothing(1, 2);
			Map<BooleanVariables, ProbabilityMassFunction> pmfList = trainService.constructPMF(laplaceSmoothing);
		

		/***
		 * STEP 5: Use Baye's again to accumulate more evidence and revised our beliefs. 
		 * 
		 * Below we specify which evidences we allow the approach to assimilate.
		 */
			//Specify which evidences will allows the approach to accumulate, in this test case we are accumulating all the evidences
			//Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(Arrays.asList(BooleanVariables.values()));
						
			//Specify a list with the BooleanVariables to assimilate, instead of the whole list
			Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>();	
			evidencesToAccumulate.add(BooleanVariables.CSURI);
			//evidencesToAccumulate.add(BooleanVariables.CSN);
			//evidencesToAccumulate.add(BooleanVariables.CSP);
			//evidencesToAccumulate.add(BooleanVariables.CSR);
			
						
			//This parameter is responsible for saving the indexes of cells that have assimilate both syntactic and semantic evidences
			Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
			ControlParameter onlySynSemCells = new ControlParameter(ControlParameterType.ONLY_SYN_SEM_CELLS, true); 
			controlParameters.put(onlySynSemCells.getName(), onlySynSemCells);
			
			//If select ONLY_SYN_SEM_CELLS, you need to specify the Dampening effect policy
			//Here I used: COMBINATION_OF_EVIDENCE which is the default behaviour
			//With this policy only the cells tha have assimilated ALL the evidence specified in the list above will be selected
			ControlParameter dampeningEffectPolicy = new ControlParameter(ControlParameterType.DAMPENING_EFFECT_POLICY,
																						DampeningEffectPolicy.COMBINATION_OF_EVIDENCE);			
			controlParameters.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicy);
			
			
			SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidenceMatrix, sourceSchema, targetSchema,
																			semanticCube, pmfList, evidencesToAccumulate, controlParameters);
			
			/***
			 * STEP 5.1: hold matrix that considers both syntactic and semantic evidences with Bayes
			 * This is the same matrix but with updated posteriors in the presence of semantic evidence
			 */
			float[][] matrixSynSem = accumSemEvidenceMatrix.getSemMatrixAsArray();	
			
		
		/****
		 * STEP: 6
		 *
         *   - Results from survey consider both syntactic and semantic knowledge, therefore we can consider them as
         *     degrees of belied.
         *   - We then use the distributions derived to express similarity scores as degrees of belief, this is the 
         *     accumSynEvidenceMatrix, that I have derived in STEP 2.
         *   - Then we calculate the error between < accumSynEvidenceMatrix and expectationMatrix >
         *   
         *   - We then use the Probability Mass Functions for each semantic evidence to update the posteriors in the 
         *   presence of semantic evidence.
         *    - Then we calculate the error between < accumSemEvidenceMatrix and expectationMatrix >
         *    
         *	  - We compare error1 and error2
		 */
		if (true) {	
			//Get the Expectation matrix
			SemanticMatrix gtMatrix = schemaService.generateExpectationMatrix(sourceSchema, targetSchema, jamendoGTAlignment);
			
			//Convert matrices to plain 2D structures (observed = real values)
			float[][] observedMatrix = gtMatrix.getSemMatrixAsArray();
	
			//Attach performance measures to use for calculating the errors
			//evaluatorService.attactMeasure( new MeanSquaredError() );			
			//evaluatorService.attactMeasure( new RootMeanSquaredError() );
			//evaluatorService.attactMeasure( new MeanAbsoluteError() );
			
			evaluatorService.attactAggrErrMeasure( new RelativeSquaredError() );
			evaluatorService.attactAggrErrMeasure( new RootRelativeSquaredError() ); //I have choose this one
			
			//evaluatorService.attactMeasure( new RelativeAbsoluteError() );			
			//evaluatorService.attactMeasure( new CorrelationCoefficient() );
					
			//Calculate the error1 using different measures ( Syntactic against Expectation matrix)
			logger.info("");
			logger.info("Error1: ");		
			Map<PerformanceErrorTypes, Float> error1 = evaluatorService.calculatePerformance(matrixSynOnly,
																								observedMatrix, accumSemEvidenceMatrix.getIndexesSet());
			//Map<PerformanceErrorTypes, Float> error1 = evaluatorService.calculatePerformance(matrixSynOnly, observedMatrix); 
			logger.info("");
			logger.info("Error2: ");
			//Calculate the error2 using different measures ( SynSem against Expectation matrix)
			Map<PerformanceErrorTypes, Float> error2 = evaluatorService.calculatePerformance(matrixSynSem,
																								observedMatrix, accumSemEvidenceMatrix.getIndexesSet());	
						
			//Map<PerformanceErrorTypes, Float> error2 = evaluatorService.calculatePerformance(matrixSynSem, observedMatrix);			
			
			/*********************************
			 * Other Control Parameters 
			 * for saving the CSV data files	
			 *********************************/
			
			//SHOW_PERC_INCR_DECR and SHOW_PERC_CHANGE cannot be both true	
			ControlParameter showPercIncrDecr = new ControlParameter(ControlParameterType.SHOW_PERC_INCR_DECR, false);
			controlParameters.put(showPercIncrDecr.getName(), showPercIncrDecr);
				
			ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
			controlParameters.put(showPercChange.getName(), showPercChange);
			
			//PLOT_PERC_INCR_DECR and PLOT_PERC_CHANGE cannot be both true
			ControlParameter plotPercIncrDecr = new ControlParameter(ControlParameterType.PLOT_PERC_INCR_DECR, false);
			controlParameters.put(plotPercIncrDecr.getName(), plotPercIncrDecr);
				
			ControlParameter plotPercChange = new ControlParameter(ControlParameterType.PLOT_PERC_CHANGE, false);
			controlParameters.put(plotPercChange.getName(), plotPercChange);
				
			schemaService.getGraphvizDotGeneratorService().exportToCSVFile(error1, error2, evidencesToAccumulate, null, controlParameters);
		}//end if
	}//end testMatchingWithBayes()	
	

	/***
	 * This class will apply the technique with different parameters for the approach. The parameters to 
	 * explore are: 
	 * 		Kernel: with support / without support
	 * 		Syntactic matchers: nNgram / editDistance
	 * 		Semantic evidences: BooleanVariables
	 * 		Laplacian Transformation
	 * 
	 * This is for measuring the error with the expectation matrix. This method uses a conf file to setup the
	 * approach. It will then measure the error according to the setup.
	 * 
	 * 
	 * @throws ExecutionException
	 * @throws IOException
	 */
	@Test
	public void testMatchingWithBayesCombinations() throws ExecutionException, IOException {
	
		/*Property file that holds a list of configuration files to run this experiment*/
		String runConfNumE = "./src/test/resources/runConf/expNumE.properties";		
		
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

		//Get the schemas
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");	
		
		
		//Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties
		//this property file holds the uris of the local named graphs
		this.loadConfigTraining(trainModelsPropLoc);		
		
		
		/***
		 * Follow the same procedure as method testMatchingWithBayes(), however this time
		 * get the configuration of the entire system from a file.
		 */
		
		//Read the property file to get a configuration file for each run.conf
		InputStream runConf = new FileInputStream(runConfNumE);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
		
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
				
				//Run the method for each configuration file - when considering all cells for measuring the error
				evaluatorService.runBayesFromConfigFiles(filePath, sourceSchema, targetSchema, this.getMetadataSDBStore(), this.getTDBStore(),
																					DATA_ANALYSIS, EVID_CLASSES, EVID_PROPS, ENDPOINT_DATA);	
				
			
			}//end if
		}//end for
		
	}//end testMatchingWithBayesCombinations()
	
		
	/*****
	 * To prove that the technique of assimilating evidence we ran the following numeric experiment to see whether we get any improvement
	 * by taking into account only syntactic evidence from the syntactic matchers available: edit-distance and nGram. We first run a matcher
	 * and we use the likelihoods to get probabilities for the raw similarity scores derived by the matcher. We then calculate the error from the
	 * results produced by a matcher {ed, ng} with the ones obtained from the survey; in this experiment we call this {error_1}. We then use
	 * our Bayesian framework to assimilate syntactic evidence from both matchers and then we calculate the error with the expectation
	 * matrix, this is {error_2}. 
	 * 
	 * @throws ExecutionException
	 * @throws IOException
	 */
	@Test
	public void testSyntacticEvidenceOnlyAgainstBayes() throws ExecutionException, IOException {
		/*Property file that holds a list of configuration files to run this experiment*/
		String runConfNumE = "./src/test/resources/runConf/expSynOnly.properties";		
		
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

		//Get the schemas
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");	
		
		
		//Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties
		//this property file holds the uris of the local named graphs
		this.loadConfigTraining(trainModelsPropLoc);		
		
		/***
		 * Follow the same procedure as method testMatchingWithBayes(), however this time
		 * get the configuration of the entire system from a file.
		 */
		
		//Read the property file to get a configuration file for each run.conf
		InputStream runConf = new FileInputStream(runConfNumE);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
				
		//Setup some control parameters for this experiment
		Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		
		//In this experiment output a csv file with the numeric evaluation using the performance measures.
		ControlParameter outputNumericError = new ControlParameter(ControlParameterType.PLOT_TYPE, PlotType.OUTPUT_NUMERIC_AGGREGATED_ERROR);	
		controlParameters.put(outputNumericError.getName(), outputNumericError);
				
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
								
				//Run the method for each configuration file 
				evaluatorService.runNumericExpSyntacticOnly(filePath, sourceSchema, targetSchema, controlParameters);			
			}//end if
		}//end for		
		
	}//end testSyntacticEvidenceOnlyAgainstBayes()	
	
	@Test
	public void testNumericExperimentDifferenceD() throws ExecutionException, IOException {
		
		/*Property file that holds a list of configuration files to run this experiment*/
		String runConfNumE = "./src/test/resources/runConf/expNumE.properties";		
		
		
	}//end testNumericExperimentDifferenceD()
	
	
	/***
	 * The purpose of this experiment is to show that the Bayesian technique on assimilating syntactic evidence only
	 * works. This experiment calculates and plots the individual error for each pair of matches. At the beginning it
	 * calculates the error between one of the matchers {ng} or {ed} with the expectation matrix, we refer to that error
	 * as {error1}. Then it calculates the error between the combination of evidence from {ng, ed} on localnames using 
	 * the Bayesian framework to aggregate the evidence. It then measures the error between the aggregated score with 
	 * the expectation matrix, will call this error 2.
	 * 
	 * In this experiment we plot the individual errors and not an aggregated error using some performance measure.
	 * For each pair we calculate the error and then we plot it.
	 * 
	 *  **Note**: At the moment this experiment on measuring the individual errors does not take into account the semantic 
	 *  evidence available. The idea is that after taking the semantic evidence into account we meaure again the individual
	 *  errors. It might be a good idea afterall to store the calculated individual errors into the triple store, and then
	 *  order them and then print them. To make sure I get the same pairs on the axis
	 *  
	 */
	@Test
	public void testSyntacticEvidenceOnlyAgainstBayesWithPlots() throws ExecutionException, IOException {
		/*Property file that holds a list of configuration files to run this experiment*/
		String runConfNumE = "./src/test/resources/runConf/expNumE.properties";		
		
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

		//Get the schemas
		sourceSchema = schemaRepository.getSchemaByName("jamendoRDFSchema");
		targetSchema = schemaRepository.getSchemaByName("jamendoRDFSchemaPrime");	
		
		//Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties
		//this property file holds the uris of the local named graphs
		this.loadConfigTraining(trainModelsPropLoc);		
		
		/***
		 * Follow the same procedure as method testMatchingWithBayes(), however this time
		 * get the configuration of the entire system from a file.
		 */
		
		//Read the property file to get a configuration file for each run.conf
		InputStream runConf = new FileInputStream(runConfNumE);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
		
		//Setup some control parameters for this experiment
		Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();

		//Plot the error between (Mng - Mexp) and (Med - Mexp) & (Med - Mexp) and (Med - Mexp)		
		ControlParameter plotType1 = new ControlParameter(ControlParameterType.PLOT_TYPE, PlotType.PLOT_INDIVIDUAL_ERR_SYN_ONLY);	
		controlParameters.put(plotType1.getName(), plotType1);
		
		//Uncomment this and comment the above if want to use semantic annotations
		//Plot the error between (Msyn - Mexp) and (Msyn&sem - Mexp)
		//ControlParameter plotType2 = new ControlParameter(ControlParameterType.PLOT_TYPE, PlotType.PLOT_INDIVIDUAL_ERR_SYN_SEM);	
		//controlParameters.put(plotType2.getName(), plotType2);		
		
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
				
				//Run the method for each configuration file 
				evaluatorService.runNumericExpSyntacticOnly(filePath, sourceSchema, targetSchema, controlParameters);	
				
			
			}//end if
		}//end for	
	}//end testSyntacticEvidenceOnlyAgainstBayesWithPlots()	
	
	/***
	 * NEW FAMILY OF EXPERIMENTS
	 * 
	 * In this experiment a pair of given ontologies, e.g., from the OAEI group are given to the approach. 
	 * In addition, an alignment of the pair of ontologies is given as well to provide the linkages that 
	 * the approach is utilising. The idea is that having links at the conceptual level can be used as 
	 * a *kind* of semantic evidence (because we have more semantic evidence) to improve the accuracy of
	 * purely syntactic matchers that work on the schema level often utilising just syntactic evidence.
	 * 
	 * In all my experiments as a base line I have the performance of two syntactic matchers on localnames
	 * only. I then measure the error of having assimilated just syntactic evidence with the expectation 
	 * matrix. Then from the other side I have semantic annotations, a mistake that we have been doing is
	 * the fact that we have been emphasising the semantic annotations only on semantic relations. This 
	 * provide us with part of the story. As semantic annotations we should also consider, values from 
	 * rdfs:label, and rdfs:comment that may provide evidence in a human readable form. We should then
	 * assimilate all evidence available, meaning both semantic relations, whether they belong to the 
	 * same namespace and also the similarities on the values of rdfs:label and rdfs:comment.
	 * 
	 * Note: Regarding the implementation of the new matcher on rdfs:label and rdfs:comments I should
	 * store for each superAbstract, superLexical the URI, as I have been doing and then in the case
	 * I would like to retrieve the values of rdfs:label and comment I just look up the ontology.
	 * 
	 */
	@Test
	public void testOntologyAlignmentExperiment() throws ExecutionException, IOException {

		//Hold a reference to the TDB store (for semantic metadata)		
		tdbStore = this.getTDBStore();

		/****
		 * START FROM HERE: Choose Configuration file to run the experiment here
		 ****/
		
		//Pair 1: Ekaw - Conference [no mutation] 		
		String propertyFile = "./src/test/resources/ontologies/ekaw_conference.properties";
		
		//Ekaw - Conference [Mutation rate, Mutation mode] 		
		//String propertyFile = "./src/test/resources/ontologies/ekaw_conference_mutation.properties";			
		
		
		//Get the properties needed from the configuration file.
		Map<String, String> propsMap = loadConfigFile(propertyFile);
		
		/* Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties		
		this property file holds the uris of the local named graphs - Named Graphs that perform the counts for the pmf*/
		this.loadConfigTraining(trainModelsPropLoc);		
		
		//Setup a document manager that does not retrieve import ontologies automatically
		OntDocumentManager docMgr = new OntDocumentManager();
		docMgr.setProcessImports(false);

		//Load Source ontology. The TBox of the ontology is mapped to the internal representation of DSToolkit supermodel
		if (externalDataSourcePoolUtilService.getExternalJenaModelFromTDBStore(propsMap.get("sourceOntoName"),
																						propsMap.get("sourceOntoSchemaName")) == null) {
			
			DataSource source = dataSourceService.addDataSource(tdbStore, propsMap.get("sourceOntoName"), propsMap.get("sourceOntoSchemaName"),
												propsMap.get("sourceOntoLocation"), propsMap.get("sourceOntoLocation"), ModelType.ONTOLOGY, docMgr, null);			
			
			currentDataspace.addDataSource(source);
			currentDataspace.addSchema(source.getSchema());			
			
			//logger.debug("Source : " + source);
			logger.debug("Source Schema : " + source.getSchema());
			
			//logger.debug("Source : " + source.getSchema().getCanonicalModelConstructs());
		}//end if 
		
		//Load Target ontology. The TBox of the ontology is mapped to the internal representation of DSToolkit supermodel
		if (externalDataSourcePoolUtilService.getExternalJenaModelFromTDBStore("conference",null) == null) {
		
			DataSource target = dataSourceService.addDataSource(tdbStore, propsMap.get("targetOntoName"), propsMap.get("targetOntoSchemaName"),
					propsMap.get("targetOntoLocation"), propsMap.get("targetOntoLocation"), ModelType.ONTOLOGY, docMgr, null);
			
			currentDataspace.addDataSource(target);
			currentDataspace.addSchema(target.getSchema());			
			
			//logger.debug("Target : " + target);
			logger.debug("Target Schema : " + target.getSchema());
			
			//logger.debug("Source : " + target.getSchema().getCanonicalModelConstructs());
		}//end if
		
		
		//Input: Get the CLASS HIERARCHIES for each Ontology
		sourceSchema = schemaRepository.getSchemaByName(propsMap.get("sourceOntoSchemaName"));
		targetSchema = schemaRepository.getSchemaByName(propsMap.get("targetOntoSchemaName"));
		
		//String dotString = graphvizDotGeneratorService.generateDot(sourceSchema, "vertical", true, false);
		//File temp = graphvizDotGeneratorService.exportAsDOTFile(dotString, "Schema", null);	
		//graphvizDotGeneratorService.exportDOT2PNG(temp, "png", null, null);		
				
		//Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties
		//this property file holds the uris of the local named graphs
		this.loadConfigTraining(trainModelsPropLoc);		
		
		//Get the BLOOMS alignment from property file
		String bloomsAlignment = propsMap.get("alignmentLocation");		
		
		//Property file that holds a list of configuration files to run this experiment (with the BLOOMS alignment)
		String bloomsConfig = "./src/test/resources/runConf/expSimulate.properties";	
		
		/*Add controlParameters, like: assimilate the syntactic evidence and then print the individual errors along with the 
		 							 : assimilate both the syn & sem evidence and then print the individual errors along with the */
		controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		
		
		//Read the property file to get a configuration file for each run.conf
		InputStream runConf = new FileInputStream(bloomsConfig);
		Properties runProps = new java.util.Properties();
		runProps.load(runConf);
		
		//Run the configuration from file
		for(String key : runProps.stringPropertyNames()) {
			if (key.contains("run")) {				
				//Get the path of the configuration file from run.properties
				String filePath = runProps.getProperty(key);
				logger.debug("Configuration file: " + filePath);
				
				// [ 1 ] Setup the control parameters for only the assimilation of syntactic evidence
				//ControlParameter onlySynEvidence = new ControlParameter(ControlParameterType.PLOT_TYPE, BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY);
				//controlParameters.put(onlySynEvidence.getName(), onlySynEvidence);
								
				/**
				 * If this parameter is decided then the experiment will do the following:
				 *   (1) Run syntactic matchers over the Ontology Class Hierarchy
				 *   (2) Read the expectation matrix from the triple store
				 *   (2) Assume 2 matchers: {ng, ed}. For each matcher do:
				 *   		(2.1) Transalte the results into degrees of belief
				 *   		(2.2) Calculate individual errors (Mng - Mexp) and (Med - Mexp)
				 *   		(2.3) Calculate COMA average individual/aggregated errors
				 *   		(2.3) Use the individual errors to calculate an aggregated error using some 
				 *   		
				 */
				// [ 1 ] Uncomment the following line for proceeding with the semantic evidence.
				ControlParameter synSemEvidence = new ControlParameter(ControlParameterType.PLOT_TYPE, BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM);				
				controlParameters.put(synSemEvidence.getName(), synSemEvidence);								
				
				//This is a test remove
  			    //this.getTDBStore().printNamedModel("x-ns://train.metadata/evidence/classes");
						
				
				// [ 2 ] Setup the Ontology Benchmark
				OntologiesServiceBenchmarkImpl ontologiesServiceBenchmarkBLOOMS = new OntologiesServiceBenchmarkImpl(propsMap, DATA_ANALYSIS, EVID_CLASSES,
																						EVID_PROPS, ENDPOINT_DATA, BenchmarkType.SIMULATE_SEMANTIC_ANNOTATIONS,
																						this.getTDBStore(),	this.getMetadataSDBStore(),
																						sourceSchema, targetSchema);
				
				// [ 3 ] 				
				ontologiesServiceBenchmarkBLOOMS.resetAll(); //Reset all the Named Graphs used by the experiment
				//ontologiesServiceBenchmarkBLOOMS.resetAlignmentExpectation(); //Reset the Named Graphs for the Alignment and Expectation 
				ontologiesServiceBenchmarkBLOOMS.runExperiment(filePath, controlParameters);
				
			}//end if		
		}//end for		
		
		
		//TODO: Include in the set of semantic annotations syntactic evidence from rdfs:label and rdfs:comments
		
		
	}//end testOntologyAlignmentExperiment()	
	
	
	
	/***
	 * This property file holds the names of the named graphs used for the construction of semantic evidence
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