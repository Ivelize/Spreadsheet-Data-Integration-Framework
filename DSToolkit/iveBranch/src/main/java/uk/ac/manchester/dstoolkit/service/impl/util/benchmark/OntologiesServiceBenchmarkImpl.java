package uk.ac.manchester.dstoolkit.service.impl.util.benchmark;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.AggregationType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.BayesianMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.LevenshteinMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.EquivalenceSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.HierarchySemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ShareSuperClassSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SubsumptionSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.AbsoluteError;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasures;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.ErrorMeasuresTypes;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.SquaredError;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.AbstractKernelFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.BoxKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.EpanechnikovKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.GaussianKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelCaseType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelDenstityEstimator;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelSupportType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.LogTransformation;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.QuarticKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.TriangularKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.TriweightKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing.LaplaceSmoothing;
import uk.ac.manchester.dstoolkit.service.impl.util.training.SemEvidenceDataAnalysisUtilImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.SemEvidenceTrainingUtilImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.TrainingServiceUtilImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.training.VOCAB;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.CorrelationCoefficient;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.MeanAbsoluteError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.MeanSquaredError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceErrorTypes;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.PerformanceMeasures;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RelativeAbsoluteError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RelativeSquaredError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RootMeanSquaredError;
import uk.ac.manchester.dstoolkit.service.impl.util.training.evaluation.RootRelativeSquaredError;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.meta.SemanticMetadataService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.StringBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.util.benchmark.OntologiesServiceBenchmark;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.rits.cloning.Cloner;


/************************************************************************************************
 * 
 * This class is used also as a Service that provides 
 * 
 * 
 * 
 * 
 * The purpose of this benchmark experiment is to measure the effectiveness of the approach when 
 * the existence of semantic relations such as rdfs:subClassOf and owl:equivalentClass is simulated
 * by a given alignment produced by some alignment tool, like BLOOMS. BLOOMS is a tool for discovering
 * links at the conceptual level given two ontologies/ LOD ontologies in particular. LOD ontologies are
 * different from ordinary ontologies because LOD ontologies may have definitions of terms from various 
 * domains as opposed to an ordinary ontology that describes a single domain. 
 * 
 * Input: a source ontology (O1), a target ontology (O2) and their alignment (A)
 * 
 * Alignment (A) is given by a tool that discovers links at the conceptual schema, a tool such as
 * BLOOMS
 * 
 * 
 * 
 * //TODO: In the future this class can introduce in a random way more semantic annotations among the ontologies 
 * 
 * 
 * 
 * @author klitos
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class OntologiesServiceBenchmarkImpl extends TrainingServiceUtilImpl implements OntologiesServiceBenchmark {

	private static Logger logger = Logger.getLogger(OntologiesServiceBenchmarkImpl.class);

	@Autowired
	@Qualifier("evaluatorService")
	private EvaluatorService evaluatorService;	
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	public static String ONTOLOGY_DATA_URI   = "x-ns://source.data/onto/";
	public static String ALIGNMENT_GRAPH_URI = "x-ns://benchmark.data/onto/align"; //BLOOMS alignment
	
	public static String COMA_AVG_GRAPH = "x-ns://benchmark.data/onto/coma/avg/"; //Holds matches derived with COMA avg strategy
	public static String SYN_ONLY_GRAPH = "x-ns://benchmark.data/onto/bayesian/syn/"; //Holds matches derived with syntactic evidence only bayesian approach
	public static String SYN_SEM_GRAPH  = "x-ns://benchmark.data/onto/bayesian/sem/";	//Holds matches derived with syn & sem evidence bayesian approach
	
	public static String EXP_MATRIX_GRAPH  = "x-ns://benchmark.data/exp/matrix"; //Holds the expectation matrix as triples	
		
	//TODO: Add more matchers here to make their matches persistent
	//Ngram matcher
	public static String NGRAM_MATCHER_ABS_ERROR = "x-ns://matcher.ng/matches/abs";
	public static String NGRAM_MATCHER_SQUARED_ERROR = "x-ns://matcher.ng/matches/squared";

	//Edit-distance matcher
	public static String ED_MATCHER_ABS_ERROR = "x-ns://matcher.ed/matches/abs";
	public static String ED_MATCHER_SQUARED_ERROR = "x-ns://matcher.ed/matches/squared";
	

	private Map<String, String> propsMap = null;
	
	//Properties that hold the PMF etc.
	private static String DATA_ANALYSIS;
	private static String EVID_CLASSES;
	private static String EVID_PROPS;
	private static String ENDPOINT_DATA;	
	
	private TDBStoreServiceImpl tdbStore = null;
	private SDBStoreServiceImpl metaDataSDBStore = null;
	private PrefixMapping pmap = null;
	private BenchmarkType bType = null;
	
	private Schema sourceSchema = null;
	private Schema targetSchema = null;
	
	private SemanticMatrix expectationMatrix = null;
	private boolean persistMatches = false;	
	
	private List<PerformanceMeasures> aggregatedErrorMeasures = null;
	private ArrayList<ErrorMeasures> singleErrorMeasures = null;
	private float[][] actualValuesAsArray;
	private float[][] predictedValuesSynEvidenceOnly;
	private Map<PerformanceErrorTypes, Float> errorSynOnlyMap = null;
	
	//COMA Average 
	private Map<PerformanceErrorTypes, Float> error1COMAmap = null;
	private float[][] comaAggrSimMatrix = null;
	
	//Variable that hold the configuration parameters from the run.conf file in ./src/test/resources/runConf/expSimulate.properties
	List<SemanticMetadataService> semanticQueue = null; //Hold the list of semantic annotations to organise 
	
	
	/*Constructor*/
	public OntologiesServiceBenchmarkImpl(Map<String, String> propsMap,String DATA_ANALYSIS, String EVID_CLASSES,
										  String EVID_PROPS, String ENDPOINT_DATA, BenchmarkType bType,
										  TDBStoreServiceImpl tdb, SDBStoreServiceImpl sdbStore,
										  Schema sourceSchema, Schema targetSchema) {
		
		semanticQueue = new ArrayList<SemanticMetadataService>();
		
		this.propsMap = propsMap;
		
		this.DATA_ANALYSIS = DATA_ANALYSIS;
		this.EVID_CLASSES = EVID_CLASSES;
		this.EVID_PROPS = EVID_PROPS;
		this.ENDPOINT_DATA = ENDPOINT_DATA;
		
		this.tdbStore = tdb;
		this.metaDataSDBStore = sdbStore;
		this.bType = bType;
		
		this.sourceSchema = sourceSchema;
		this.targetSchema = targetSchema;
		
		/*Prepare a prefix map to be used to return Qnames of the properties*/
		if (pmap == null) {
			pmap = this.createPrefixMap();
		}		
	}//end constructor	
	
	
	/***
	 * MAIN method that runs the Experiment.
	 * 
	 * Inputs of this Experiment: Source Ontology, Target ontology, BLOOMS Alignment, Expectation Matrix
	 * 
	 * ControlParameters - whether to assimilate only the syntactic evidence or whether to assimilate everything.
	 */
	public void runExperiment(String confFilePath,  Map<ControlParameterType, ControlParameter> controlParameters) {
		
		//[1]. Load the Alignment produced by *BLOOMS* into the TDB store under the ALIGNMENT_GRAPH Uri
		logger.info("BLOOMS alignment file: " + propsMap.get("alignmentLocation"));
		tdbStore.loadDataToModelFromRDFDump(ALIGNMENT_GRAPH_URI, propsMap.get("alignmentLocation"));
		
		//[2]. Load the Expectation matrix alignment into the TDB Store under the EXP_MATRIX_GRAPH Uri
		logger.info("expectation Matrix: " + propsMap.get("expectationMatrix"));
		tdbStore.loadDataToModelFromRDFDump(EXP_MATRIX_GRAPH, propsMap.get("expectationMatrix"));	
				
		//[3]. Create a 2d structure that holds the Expectation Matrix
		this.expectationMatrix = schemaService.generateExpectationMatrix(this.sourceSchema, this.targetSchema, this.tdbStore, EXP_MATRIX_GRAPH);
				
		//[4]. From ControlParameters check which experiment to plot
		BenchmarkType benchmarkType = null;
		if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_TYPE))) {
			benchmarkType = controlParameters.get(ControlParameterType.PLOT_TYPE).getBenchmarkType();
		}
		
		logger.info("benchmarkType: " + benchmarkType);
				
		/***
		 * Part I : During the first part of the experiment, run each matcher, translate to degrees of belief,
		 * 			compare with the expectation matrix. Report: individual errors for each matcher and aggregated
		 * 			error using a performance measure.
		 */
				
		//[5] SemanticMatrix that holds matches that assimilated only syntactic evidence
		SemanticMatrix accumSynEvidenceMatrix = this.runConfSyntacticEvidence(confFilePath, controlParameters);
		
		
		/***
		 * Part II : Add the Semantic annotations
		 */
		
		//If Experiment is BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM do the following extra staff
		if (benchmarkType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			logger.info("benchmarkType: " + benchmarkType);
			
			this.runConfSemanticEvidence(accumSynEvidenceMatrix, confFilePath, controlParameters);	
			
		}//end if		
	}//end runExperiment()	
	
	
	//Reset all the Named Graphs associated with this benchmark 
	public void resetAlignmentExpectation() {
		tdbStore.removeNamedModelTrans(ALIGNMENT_GRAPH_URI);
		tdbStore.removeNamedModelTrans(EXP_MATRIX_GRAPH);
	}
	
	
	//Reset all the Named Graphs associated with this benchmark 
	public void resetAll() {
		tdbStore.removeNamedModelTrans(ALIGNMENT_GRAPH_URI);
		tdbStore.removeNamedModelTrans(EXP_MATRIX_GRAPH);

		tdbStore.removeNamedModelTrans(SYN_ONLY_GRAPH);
		tdbStore.removeNamedModelTrans(SYN_ONLY_GRAPH + "abs");
		tdbStore.removeNamedModelTrans(SYN_ONLY_GRAPH + "squared");
		
		tdbStore.removeNamedModelTrans(SYN_SEM_GRAPH);
		tdbStore.removeNamedModelTrans(SYN_SEM_GRAPH + "abs");
		tdbStore.removeNamedModelTrans(SYN_SEM_GRAPH + "squared");
	}//end resetAll()
	
	
	/**
	 * Method: This method is responsible for making matches persistent, this method will be responsible for just that
	 */
	public void persistMatches(SemanticMatrix semMatrix, float[][] error, ErrorMeasures errMeasure, BenchmarkType bType, MatcherType mType,
							    ControlParameter selectionType, ControlParameter rankingType, ControlParameter errorType, Set<SemanticMatrixCellIndex> iSet) {
		logger.debug("in persistMatches()");
		
		//Get the error measure type
		ErrorMeasuresTypes errMeasureType = errMeasure.getmType();
		
		//Choose a Named Graph URI
		String namedGraphURI = "";
		if ((bType != null) && bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY)) {		
			if (errMeasureType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
				namedGraphURI = SYN_ONLY_GRAPH + "abs";
			} else if (errMeasureType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
				namedGraphURI = SYN_ONLY_GRAPH + "squared";			
			}	
		} else if ((bType != null) && bType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM)) {
			if (errMeasureType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
				namedGraphURI = SYN_SEM_GRAPH + "abs";
			} else if (errMeasureType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
				namedGraphURI = SYN_SEM_GRAPH + "squared";	
			}	
		} else if ((bType != null) && bType.equals(BenchmarkType.COMA_APPROACH)) {
			if (errMeasureType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
				namedGraphURI = COMA_AVG_GRAPH + "abs";
			} else if (errMeasureType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
				namedGraphURI = COMA_AVG_GRAPH + "squared";	
			}	
		}		
			
		if ((mType != null) && (mType.equals(MatcherType.LEVENSHTEIN))) {
			if (errMeasureType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
				namedGraphURI = ED_MATCHER_ABS_ERROR;
			} else if (errMeasureType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
				namedGraphURI = ED_MATCHER_SQUARED_ERROR;
			}
		} else if ((mType != null) && (mType.equals(MatcherType.NGRAM))) {
			if (errMeasureType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
				namedGraphURI = NGRAM_MATCHER_ABS_ERROR;
			} else if (errMeasureType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
				namedGraphURI = NGRAM_MATCHER_SQUARED_ERROR;
			}
		} 		
		
		//[1]. Create a new BayesianMatcher instance
		
		//Setup a BayesianMatcherService with the semantic matrix available
		BayesianMatcherServiceImpl bayesianMatcherSyn = new BayesianMatcherServiceImpl(semMatrix);	
		bayesianMatcherSyn.addControlParameter(selectionType);
		bayesianMatcherSyn.addControlParameter(rankingType);	
		bayesianMatcherSyn.addControlParameter(errorType);		
		
		//Retieve a List of Matches 
		List<Matching> matchesFromBayes = null;
		
		if ((iSet == null) && (persistMatches)) {
			matchesFromBayes = bayesianMatcherSyn.produceAndSaveMatchesWithError(sourceSchema, targetSchema, error);
		} else {
			matchesFromBayes = bayesianMatcherSyn.produceAndSaveMatchesForSpecificCellsWithError(sourceSchema, targetSchema, error, iSet);
		}		
		
		//Open a new WRITE transaction		
		Dataset dataset = tdbStore.getDataset();
		dataset.begin(ReadWrite.WRITE);		
		try {
			//Get the right Named Graph to make the matches persistent
			Model namedModel = tdbStore.getModel(namedGraphURI);
						
			//If the model is not empty make it persistent 
			if (namedModel.isEmpty()) {						
			
				//Get the Model of this schema, schemas should have the same type so only one is needed
				ModelType schemaModelType = this.sourceSchema.getModelType();			
				logger.info("Model Type: " + schemaModelType);
						
				for (Matching matching : matchesFromBayes) {
					if (matching instanceof OneToOneMatching) {
						OneToOneMatching oneToOne = (OneToOneMatching) matching;
					
						//Get the Constructs from the Matching object			
						CanonicalModelConstruct construct1 = oneToOne.getConstruct1();
						CanonicalModelConstruct construct2 = oneToOne.getConstruct2();
						
						if ((construct1 != null) && (construct2 != null)) {
					
							String entity1 = this.getEntityURI(construct1);
							String entity2 = this.getEntityURI(construct2);
			
							logger.debug("entity1: " + entity1);
							logger.debug("entity2: " + entity2);
						
							if (errMeasureType.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
								this.createMatch(namedModel, entity1, entity2, oneToOne.getScore(), oneToOne.getAbsError(), errMeasure);
							} else if (errMeasureType.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
								this.createMatch(namedModel, entity1, entity2, oneToOne.getScore(), oneToOne.getSquaredError(), errMeasure);
							}//end if						
						}//end if					
					}//end if				
				}//end for	
		
				//commit the Triple store
				dataset.commit();
			}//end try
		} finally {
			dataset.end();
		}	
	}//end persistMatches()
			
	/**
	 * The following method will do the following:
	 *   (1) Run syntactic matchers over the Ontology Class Hierarchy
	 *   (2) Read the expectation matrix from the triple store
	 *   (2) Assume 2 matchers: {ng, ed}. For each matcher do:
	 *   		(2.1) Translate the results into degrees of belief
	 *   		(2.2) Calculate individual errors (Mng - Mexp) and (Med - Mexp)
	 *   		(2.3) Store individual errors 
	 *   		(2.3) Use the individual errors to calculate an aggregated error using some numeric performance measure
	 *   (3) Output the results into a .csv file and the GNUplot code
	 *   		
	 */
	private SemanticMatrix runConfSyntacticEvidence(String confFilePath, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in runConfSyntacticEvidence()");		
		SemanticMatrix accumSynEvidenceMatrix = null;
		
		//Hold a set of matchers
		List<MatcherService> matcherList = null;
				
		try {			
			if ((sourceSchema != null) && (targetSchema != null)) {
					logger.debug("Running: " + confFilePath);
		
					//Read the actual configuration file run.conf and setup the approach
					InputStream confProps = new FileInputStream(confFilePath);
					Properties runConfProps = new java.util.Properties();
					runConfProps.load(confProps);
		
					matcherList = new ArrayList<MatcherService>();
				
					//Get some configuration parameters for the matchers
					ControlParameter selectionType = null;
					SelectionType st = SelectionType.fromValue(runConfProps.getProperty("matcher_selection_type"));	
		
					if (st != null) {		
						logger.debug("selectionTYPE: " + st.toString());
						selectionType = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, st.toString());	
					}//end if				
				
					logger.debug(">> Preparing StringBasedMatchers..");	
				
					//Get the name of the syntactic matchers from file
					List<String> matchersArray = Arrays.asList(runConfProps.getProperty("matchers").split(","));
				
					StringBasedMatcherService editDistMatcher = null;
					StringBasedMatcherService nGramMatcher = null;
			
					if (matchersArray != null) {
						for (String matcherName : matchersArray) {				
							logger.debug("matcherName: " + matcherName);
							MatcherType mt = MatcherType.fromValue(matcherName);					
							if ((mt != null) && mt.equals(MatcherType.LEVENSHTEIN)) {
								editDistMatcher = new LevenshteinMatcherServiceImpl();						
								editDistMatcher.addControlParameter(selectionType);
						
							} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
								nGramMatcher = new NGramMatcherServiceImpl(3); 
								nGramMatcher.addControlParameter(selectionType);					
							}					
						}//end for				
					}//end if
								
					/**
					 * Attach a Probability Density Function (PDF) to each Matcher
					 */
					AbstractKernelFunction kernel = null;
					
					KernelType kType = KernelType.fromValue(runConfProps.getProperty("kernel_type"));	
					if (kType != null) {
						logger.debug("KernelType: " + kType.toString());
						
						if (kType.equals(KernelType.GAUSSIAN)) {
							kernel = new GaussianKernel();
						} else if (kType.equals(KernelType.EPANECHNIKOV)) {
							kernel = new EpanechnikovKernel();
						} else if (kType.equals(KernelType.BOX)) {
							kernel = new BoxKernel();
						} else if (kType.equals(KernelType.TRIANGULAR)) {
							kernel = new TriangularKernel();
						} else if (kType.equals(KernelType.BIWEIGHT)) {
							kernel = new QuarticKernel();
						} else if (kType.equals(KernelType.TRIWEIGHT)) {
							kernel = new TriweightKernel();
						}					
					}//end if
					
					//Kernel Density Estimation can be configured whether the Kernel has support or not				
					KernelDenstityEstimator kde_edit_tp  = null;
					KernelDenstityEstimator kde_edit_fp  = null;
					
					KernelDenstityEstimator kde_ngram_tp = null;
					KernelDenstityEstimator kde_ngram_fp = null;
					
					//Transformation function for the kde with support
					LogTransformation logTransformation = null;
					
					KernelSupportType kdeSupportType = KernelSupportType.fromValue(runConfProps.getProperty("kernel_support"));					
				
					if (kdeSupportType != null) {		
						logger.debug("KernelSupportType: " + kdeSupportType.toString());
						
						if ( kdeSupportType.equals(KernelSupportType.KDE_WITHOUT_SUPPORT) ) {						
							
							//For Matcher: LEVENSHTEIN
							if (editDistMatcher != null) {
								//KDE - TP Case
								double h_tp = 0.0970228; //we need the smoothing parameter, got this from Matlab, matlab uses some optimisation						
								kde_edit_tp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, kernel, h_tp);							
								kde_edit_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");
								ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(-2, 2, kde_edit_tp, KernelCaseType.TP_CASE); //remember to run pdf.createIntegralVector();
								pdfTP.createIntegralVector(); //This is important to generate in advance the vector that holds the pre-computed integrals
								editDistMatcher.attachPdfTP(pdfTP); //Attach PDF to the Matcher
								
								//KDE - FP Case
								double h_fp = 0.04;
								kde_edit_fp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, kernel, h_fp);
								kde_edit_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_Levenshtein.dat");
								ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(-2, 2, kde_edit_fp, KernelCaseType.FP_CASE);
								pdfFP.createIntegralVector(); //This is important generate the vector that holds the pre-computed integrals
								editDistMatcher.attachPdfFP(pdfFP); //Attach PDF to the Matcher
							}//end if
							
							//For Matcher: NGRAM
							if (nGramMatcher != null) {
								//KDE - TP Case
								double h_tp = 0.0906237;
								kde_ngram_tp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, kernel, h_tp);
								kde_ngram_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_NGram.dat");
								ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(-2, 2, kde_ngram_tp, KernelCaseType.TP_CASE);
								pdfTP.createIntegralVector();
								nGramMatcher.attachPdfTP(pdfTP);
								
								//KDE - FP Case
								double h_fp = 0.170286;
								kde_ngram_fp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, kernel, h_fp);
								kde_ngram_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_NGram.dat");
								ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(-2, 2, kde_ngram_fp, KernelCaseType.FP_CASE);
								pdfFP.createIntegralVector();
								nGramMatcher.attachPdfFP(pdfFP);							
							}//end if				
						} else if ( kdeSupportType.equals(KernelSupportType.KDE_WITH_SUPPORT) ) {
							
							//For Matcher: LEVENSHTEIN
							if (editDistMatcher != null) {	
								
								//KDE with support - TP Case
								double h_tp = 0.609385;	
								logTransformation = new LogTransformation(-0.1, 1.1); //Transformation function to be used for the support 
								kde_edit_tp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, kernel, h_tp, logTransformation);
								kde_edit_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");
								ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(0, 1, kde_edit_tp, KernelCaseType.TP_CASE);
								pdfTP.createIntegralVector();
								editDistMatcher.attachPdfTP(pdfTP);							
								
								//KDE with support - FP Case
								double h_fp = 0.27;					
								kde_edit_fp = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, kernel, h_fp, logTransformation);
								kde_edit_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_Levenshtein.dat");
								ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(0, 1, kde_edit_fp, KernelCaseType.FP_CASE);
								pdfFP.createIntegralVector();
								editDistMatcher.attachPdfFP(pdfFP);							
							}
							
							//For Matcher: NGRAM
							if (nGramMatcher != null) {
								
								//KDE - TP Case
								double h_tp = 0.588729;
								logTransformation = new LogTransformation(-0.1, 1.1);
								kde_ngram_tp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, kernel, h_tp, logTransformation);
								kde_ngram_tp.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_NGram.dat");
								ProbabilityDensityFunction pdfTP = new ProbabilityDensityFunction(0, 1, kde_ngram_tp, KernelCaseType.TP_CASE);
								pdfTP.createIntegralVector();
								nGramMatcher.attachPdfTP(pdfTP);
								
								//KDE - FP Case
								double h_fp = 0.816656;
								kde_ngram_fp = new KernelDenstityEstimator(KernelEstimatorType.NGRAM_KDE, kernel, h_fp, logTransformation);
								kde_ngram_fp.readDataPointsFromLocation("./src/test/resources/training/kernel/fp_NGram.dat");
								ProbabilityDensityFunction pdfFP = new ProbabilityDensityFunction(0, 1, kde_ngram_fp, KernelCaseType.FP_CASE);
								pdfFP.createIntegralVector();
								nGramMatcher.attachPdfFP(pdfFP);							
							}						
					
						}//end if		
					}//end if							
					
					/* Add to the list the matchers used to be assimilated with the Bayesian technique */
					matcherList = new ArrayList<MatcherService>();
					matcherList.add(editDistMatcher);
					matcherList.add(nGramMatcher);
										
					//This is a similarity cube that holds the results from each individual matcher as if it where run independently
					List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(sourceSchema, targetSchema, matcherList, controlParameters);		
										
					/***
					 * COMA - Aggregation
					 */
					//Before translating the similarity scores into degrees of belief, aggregate the similarity matrices from both
					//n-Gram and edit-distance with *COMA* AVG
					Map<ControlParameterType, ControlParameter> serviceParameters = this.getCOMAcontrolParameters();					
					comaAggrSimMatrix = MatcherServiceImpl.aggregate(simCubeOfMatchers, serviceParameters);
						
					//Print the aggregated COMA matrix
					graphvizDotGeneratorService.generateDOT(this.sourceSchema, this.targetSchema, comaAggrSimMatrix, "COMA_AVG");
														
					//Accumulate syntactic evidences from both matchers {ed, ng}
					accumSynEvidenceMatrix = schemaService.accumulateSyntacticEvidenceBayes(sourceSchema, targetSchema,
																							simCubeOfMatchers, null);
										
					//Prepare to calculate errors: Hold a matrix with degrees of belief from both matchers {Msyn}
					predictedValuesSynEvidenceOnly = accumSynEvidenceMatrix.getSemMatrixAsArray();
					
					//Prepare to calculate error: This is the expectation matrix as array {Mexp}
					actualValuesAsArray = this.expectationMatrix.getSemMatrixAsArray();
										
					/***
					 * Choose which measure to use to calculate individual errors.
					 * Choices are: SQUARED_ERROR, ABSOLUTE_ERROR
					 */
					singleErrorMeasures = new ArrayList<ErrorMeasures>();
					List<String> arrayFromPropertyFile = null;
					//Which individual error measure to use for the calculations
					arrayFromPropertyFile = Arrays.asList(runConfProps.getProperty("error_measure").split(","));
					logger.debug("measuresArray: " + arrayFromPropertyFile);
					if (arrayFromPropertyFile != null) {
						for (String name : arrayFromPropertyFile) {
							ErrorMeasuresTypes pet = ErrorMeasuresTypes.fromValue(name);
							if ((pet != null) && pet.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {								
								singleErrorMeasures.add( new SquaredError() );
							}  else if ((pet != null) && pet.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
								singleErrorMeasures.add( new AbsoluteError() );
							}
						}
					}//end if						
					
					//Send temp array to the garbage collector
					arrayFromPropertyFile = null;
					
					
					/**********************************************
					 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
					 */
					
					//Which aggregated error measure to use for the calculations
					aggregatedErrorMeasures = new ArrayList<PerformanceMeasures>();
					arrayFromPropertyFile = Arrays.asList(runConfProps.getProperty("performance_measures").split(","));	
					
					if (arrayFromPropertyFile != null) {
						for (String name : arrayFromPropertyFile) {				
							PerformanceErrorTypes pet = PerformanceErrorTypes.fromValue(name);
					
							if ((pet != null) && pet.equals(PerformanceErrorTypes.MEAN_SQUARED_ERROR)) {
								aggregatedErrorMeasures.add( new MeanSquaredError() );
							} else if ((pet != null) && pet.equals(PerformanceErrorTypes.ROOT_MEAN_SQUARED_ERROR)) {
								aggregatedErrorMeasures.add( new RootMeanSquaredError() );
							} else if ((pet != null) && pet.equals(PerformanceErrorTypes.MEAN_ABS_ERROR)) {
								aggregatedErrorMeasures.add( new MeanAbsoluteError() );
							} else if ((pet != null) && pet.equals(PerformanceErrorTypes.RELATIVE_SQUARED_ERROR)) {
								aggregatedErrorMeasures.add( new RelativeSquaredError() );
							} else if ((pet != null) && pet.equals(PerformanceErrorTypes.ROOT_RELATIVE_SQUARED_ERROR)) {
								aggregatedErrorMeasures.add( new RootRelativeSquaredError() );
							} else if ((pet != null) && pet.equals(PerformanceErrorTypes.RELATIVE_ABS_ERROR)) {
								aggregatedErrorMeasures.add( new RelativeAbsoluteError() );
							} else if ((pet != null) && pet.equals(PerformanceErrorTypes.CORRELATION_COEFFICIENT)) {
								aggregatedErrorMeasures.add( new CorrelationCoefficient() );
							}					
						}//end for
					}//end if	
					
					//Send temp array to the garbage collector
					arrayFromPropertyFile = null;
										
					//Check whether to persist matches or not
					String persist_matches = runConfProps.getProperty("persist_matches").trim();
					logger.debug("persist_matches: " + Boolean.parseBoolean(persist_matches));
					persistMatches = Boolean.parseBoolean(persist_matches);	
										
					//Plot type: SHOW_PERC_CHANGE
					ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
					controlParameters.put(showPercChange.getName(), showPercChange);	
										
					//For each Performance Measure store the error in a HashMap [this is error 2 in this case]
					errorSynOnlyMap = this.calculatePerformance(predictedValuesSynEvidenceOnly, actualValuesAsArray, aggregatedErrorMeasures);					
		
					//Then, calculate error1 for each Individual Matcher						
					for (MatcherInfo info : simCubeOfMatchers) {					
						
						MatcherType mt = info.getMatcherType();
						MatcherInfo mi = null;
						if ((mt != null) && mt.equals(MatcherType.LEVENSHTEIN)) {
							mi = info;
						} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
							mi = info;
						}				
						
						//Do not have to run the matcher again, just take the previously computed 2d matrix from above
						logger.info("Matcher Info for: " + mi.getMatcherType().toString());					
						List<MatcherInfo> newSimCubeOfMatchers = new ArrayList<MatcherInfo>(); 
						newSimCubeOfMatchers.add(mi);
						
						//From raw similarity scores to degress of beleif
						SemanticMatrix dobMatrixTemp = schemaService.accumulateSyntacticEvidenceBayes(sourceSchema, targetSchema,
							       																		newSimCubeOfMatchers, null);
						
						//Get a 2D representation of the 
						float[][] dobMatrix = dobMatrixTemp.getSemMatrixAsArray();
						
						//For each Performance Measure store the error in a HashMap
						Map<PerformanceErrorTypes, Float> error1Map = this.calculatePerformance(dobMatrix, actualValuesAsArray, aggregatedErrorMeasures);
						
						
						/***
						 * Call Graphviz to output the results using GNUPLOT
						 */					
						schemaService.getGraphvizDotGeneratorService().exportToCSVFileOnlySyntacticEvidence(error1Map,errorSynOnlyMap,mt,controlParameters);
						
					}//end for					
					
					//** Measure the aggregated error for COMA average***
					//For each Performance Measure store the error in a HashMap
					error1COMAmap = this.calculatePerformance(comaAggrSimMatrix, actualValuesAsArray, aggregatedErrorMeasures);
					schemaService.getGraphvizDotGeneratorService().exportToCSVFileOnlySyntacticEvidence(error1COMAmap,errorSynOnlyMap,MatcherType.COMA_AVG,controlParameters);
															
					
					/**********************************************
					 * PLOT MODE: PLOT_INDIVIDUAL_ERR_SYN_ONLY
					 */				
					
					//Given the measure to calculate individual errors calculate them
					//Firstly, derive individual errors between: M{syn} with M{exp} using some error measure
					for (ErrorMeasures measure : singleErrorMeasures) {
						logger.info("Single measure is: " + measure);
						
						//Calculate individual errors M{syn} with M{exp} [call it error2]
						float[][] error2 = measure.calc(predictedValuesSynEvidenceOnly, actualValuesAsArray);
						
						//Calculate individual errors M{avg} with M{exp} [call it error1COMA] 
						float[][] error1COMA = measure.calc(comaAggrSimMatrix, actualValuesAsArray);
						
						//Persist M{syn} with M{exp}
						ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
																			ControlParameterType.RANKING_DESC.toString());	
						
						ControlParameter errorType = new ControlParameter(ControlParameterType.ERROR_MEASURE_TYPE, measure.getmType());
						
						if (persistMatches) {
							this.persistMatches(accumSynEvidenceMatrix, error2, measure, BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY, null,
																								selectionType, rankingType, errorType, null);
							
							//Persist matches derived by COMA avg
							//this.persistMatches(comaAggrSimMatrix, error1COMA, measure, BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY, null,
									//selectionType, rankingType, errorType, null);
							
						}//end if
						
						//Then calculate M{ng} with M{exp} or M{ed} with M{exp}					
						for (MatcherInfo info : simCubeOfMatchers) {
			
							MatcherType mt = info.getMatcherType();
							MatcherInfo mi = null;
							if ((mt != null) && mt.equals(MatcherType.LEVENSHTEIN)) {
								mi = info;
							} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
								mi = info;
							}
							
							//Do not have to run the matcher again, just take the previously computed 2d matrix from above
							logger.info("Matcher Info for: " + mi.getMatcherType().toString());					
							List<MatcherInfo> newSimCubeOfMatchers = new ArrayList<MatcherInfo>(); 
							newSimCubeOfMatchers.add(mi);
							
							//From raw similarity scores to degrees of belief
							SemanticMatrix dobMatrixTemp = schemaService.accumulateSyntacticEvidenceBayes(sourceSchema, targetSchema,
									   													       			   newSimCubeOfMatchers, null);
							
							//Get a 2D representation of the matrix that has assimilated just syntactic evidence
							float[][] dobMatrix = dobMatrixTemp.getSemMatrixAsArray();							
							
							logger.info("Calculating error1(M_matcher, Mexp) matrices: ");
						
							//Calculate individual errors
							float[][] error1 = measure.calc(dobMatrix, actualValuesAsArray);
														
							//Make the Matches derived from the Matchers persistent
							if (persistMatches) {
								this.persistMatches(dobMatrixTemp, error1, measure, null, mt, selectionType, rankingType, errorType, null);	
							}
							
							/***
							 * Call Graphviz to output the results using GNUPLOT
							 */					
							schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynOnly(error1,error2,mt,measure,controlParameters);
						}//end for
												
						//For COMA AVG
						schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynOnly(error1COMA,error2,MatcherType.COMA_AVG,measure,controlParameters);
			
					}//end for				
		
				}//end if		
			} catch (FileNotFoundException exc) {
				logger.error("train.properties file not found: " + exc);
				exc.printStackTrace();
			} catch (IOException ioexc) {
				logger.error("train.properties file not found: ", ioexc);
				ioexc.printStackTrace();
		}//end catch		
		
		return accumSynEvidenceMatrix;		
		
	}//end runConfSyntacticEvidence()	
	
	/***
	 * Method: Given the alignment of the expectation matrix, this method is responsible for calculating the error of 
	 * 			a given match.
	 */
	//private calculateErrorFromExpectation() {
		
	//}	
	
	/***
	 * Supportive method: This method reads the run.conf file for this experiment.
	 * 
	 * For the assimilation of Syntactic evidence (only) the evaluator
	 * 
	 */
	private void runConfSemanticEvidence(SemanticMatrix accumSynEvidenceMatrix, String confFilePath, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in runConfSemanticEvidence");
		
		//Create a cloner so we can keep a deep copy of the Syntactic Similarity matrix before applying any Semantic Evidence
		Cloner cloner = null;
				
		try {			
			if ((sourceSchema != null) && (targetSchema != null)) {
				logger.debug("Running: " + confFilePath);
		
				//Read the actual configuration file run.conf and setup the approach
				InputStream confProps = new FileInputStream(confFilePath);
				Properties runConfProps = new java.util.Properties();
				runConfProps.load(confProps);
			
				/***
				 * STEP 3: Collect Semantic Evidence from RDF vocabularies (RDFS/OWL)
				 */
				String schema_enrich = runConfProps.getProperty("schema_enrich").trim();
				if (Boolean.parseBoolean(schema_enrich)) {
					//because I have dereferenced the data, do not run the enrichment process again.
					//if I would like to run the enrichment process again I should setup it in the conf file.
					schemaService.schemaEnrichment(sourceSchema, targetSchema);
				}//end if		
				
				//Indicate that this kind of experiment uses the BLOOMS alignment
				ControlParameter useBLOOMSAlignments = new ControlParameter(ControlParameterType.EXPERIMENT, BenchmarkType.SIMULATE_SEMANTIC_ANNOTATIONS);
				
				//Get the threshold to be used from the configuration file
				String threshold = this.propsMap.get("threshold");
				logger.info("threshold is: " + threshold);
				
				ControlParameter thresholdForAlignment = new ControlParameter(ControlParameterType.ALIGNMENT_THRESHOLD, threshold);
								
						
				HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(metaDataSDBStore);
				//Add control parameters to the hierarchy semantic matrix
				ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
				hierarchySemMatrix.addControlParameter(doConflictRes);
		
				//Setup Control Parameter for indicate whether to use a Reasoner or not
				ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
				
				//3.1: HierarchySemanticMatrix - EquivalenceSemanticMatrix
				EquivalenceSemanticMatrix equiSemMatrix = new EquivalenceSemanticMatrix(metaDataSDBStore);
				equiSemMatrix.attachTDBStoreService(this.tdbStore);
				equiSemMatrix.addControlParameter(useReasoner);
				equiSemMatrix.addControlParameter(useBLOOMSAlignments);
				equiSemMatrix.addControlParameter(thresholdForAlignment);				
				equiSemMatrix.setAlignGraphURI(ALIGNMENT_GRAPH_URI);
				hierarchySemMatrix.attachSemMatrix(equiSemMatrix);
		
				//3.2: HierarchySemanticMatrix - SubsumptionSemanticMatrix
				SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(metaDataSDBStore);
				subSemMatrix.attachTDBStoreService(this.tdbStore);
				subSemMatrix.addControlParameter(useReasoner);
				subSemMatrix.addControlParameter(useBLOOMSAlignments);
				subSemMatrix.addControlParameter(thresholdForAlignment);				
				subSemMatrix.setAlignGraphURI(ALIGNMENT_GRAPH_URI);
				hierarchySemMatrix.attachSemMatrix(subSemMatrix);
		
				//3.3: HierarchySemanticMatrix - ShareSuperClassSemMatrix
				ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(metaDataSDBStore);
				superSemMatrix.attachTDBStoreService(this.tdbStore);
				superSemMatrix.addControlParameter(useReasoner);
				superSemMatrix.addControlParameter(useBLOOMSAlignments);
				superSemMatrix.addControlParameter(thresholdForAlignment);				
				superSemMatrix.setAlignGraphURI(ALIGNMENT_GRAPH_URI);				
				hierarchySemMatrix.attachSemMatrix(superSemMatrix);
		
				//Add hierarchySemMatrix to the queue of semantic matrices to be created
				semanticQueue.add(hierarchySemMatrix);
		
				//Call method responsible of *organising* meta-data into semantic matrices
				List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(sourceSchema, targetSchema, semanticQueue);	
								
				/***
				 * STEP 4: Construct the likelihoods for semantic evidence
				 */
				//Import the RDF-graph that holds the LOV endpoint into a TDB Store
				String local_LOV_graph = runConfProps.getProperty("sparql_endpoint_data").trim();
				loadRDFSourceUtilService.loadTDBStore(tdbStore, local_LOV_graph, ENDPOINT_DATA);
				
				//Perform data Analysis over the RDF dump
				SemEvidenceDataAnalysisUtilImpl dataAnalysis = new SemEvidenceDataAnalysisUtilImpl(tdbStore, DATA_ANALYSIS, ENDPOINT_DATA);
					
				/* The method does the analysis and then makes it persistent as an RDF-graph, set arg to true to reset it */
				String analysis_update = runConfProps.getProperty("analysis_update").trim();
				logger.debug("analysis_update: " + Boolean.parseBoolean(analysis_update));
				dataAnalysis.doDataAnalysis(Boolean.parseBoolean(analysis_update));	
				
				/* Within the sets created from the DataAnalysis search for evidences*/	
				SemEvidenceTrainingUtilImpl trainService = new SemEvidenceTrainingUtilImpl(tdbStore, DATA_ANALYSIS, EVID_CLASSES, EVID_PROPS, ENDPOINT_DATA);
									
				//Create the likelihoods for Classes
				String update_classes_graph = runConfProps.getProperty("update_classes_contingency_graph").trim();
				logger.debug("update_classes_contingency_graph: " + Boolean.parseBoolean(update_classes_graph));
				trainService.createTrainingSetClasses(Boolean.parseBoolean(update_classes_graph));		
					
				//Create the likelihoods for Properties
				String update_props_graph = runConfProps.getProperty("update_props_contingency_graph").trim();
				logger.debug("update_props_contingency_graph: " + Boolean.parseBoolean(update_props_graph));
				trainService.createTrainingSetProps(Boolean.parseBoolean(update_props_graph));	
				
				//Read the Classes contingency graph from a given RDF file
				String read_classes_graph = runConfProps.getProperty("read_classes_contingency_praph").trim();				
				if (!read_classes_graph.equals("False") || !read_classes_graph.equals("FALSE") || !read_classes_graph.equals("false")) {
					//remove the named graph
					tdbStore.removeNamedModelTrans(this.EVID_CLASSES);					
					//read the given contingency graph in 
					tdbStore.loadDataToModelIfEmpty(this.EVID_CLASSES, read_classes_graph);
				}//end if				
				
				/*** 
				 * Get the PMF as contingency tables for both Classes and Properties, using
				 * Laplace Smoothing Method with k = 1 and |classes| = 2
				 * 
				 *  //TODO: need to check whether I need to use as |classes| the size of the dictionary. This is 
				 *  for the Laplace transformation formula.
				 */
				Map<BooleanVariables, ProbabilityMassFunction> pmfList = null;
				String laplace = runConfProps.getProperty("laplace_transformation").trim();
				if (Boolean.parseBoolean(laplace)) {
					LaplaceSmoothing laplaceSmoothing = new LaplaceSmoothing(1, 2);
					pmfList = trainService.constructPMF(laplaceSmoothing);
				} else {
					pmfList = trainService.constructPMF(null);
				}
				
				/***
				 * STEP 5: Prepare for the calculation of the ERROR using the expectation matrix
				 */
			
				//Collect information for the cells that will participate to the calculation of the ERROR with the Expectation matrix
				String cells_selection = runConfProps.getProperty("cells_selection").trim();
				if (cells_selection.equals("ALL_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ALL_SYN_SEM_CELLS, true);
					controlParameters.put(param.getName(), param);
				} else if (cells_selection.equals("ONLY_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ONLY_SYN_SEM_CELLS, true);
					controlParameters.put(param.getName(), param);
				}//end else
				
				
				/* Collecting parameters: the parameter for the Dampening Effect Policy. This only applies
				 * if cells_selection = ONLY_SYN_SEM_CELLS*/
				ControlParameter dampeningEffectPolicy = null;
				DampeningEffectPolicy dep = DampeningEffectPolicy.fromValue(runConfProps.getProperty("dampening_effect_policy"));

				if (dep != null) {
					logger.debug("dampeningEffectPolicy: " + dep.toString());					
					if (dep.equals(DampeningEffectPolicy.SOME_EVIDENCE)) {
						dampeningEffectPolicy = new ControlParameter(ControlParameterType.DAMPENING_EFFECT_POLICY, dep);
					} else if (dep.equals(DampeningEffectPolicy.COMBINATION_OF_EVIDENCE)) {
						dampeningEffectPolicy = new ControlParameter(ControlParameterType.DAMPENING_EFFECT_POLICY, dep);
					} 
				}//end if
				
				//Setup the dampening effect policy as a control parameter
				controlParameters.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicy);
						

				/****
				 * STEP 6: Get the list of Semantic Evidences to assimilate. Call the combinatorics method
				 */
					List<String> evidencesList = Arrays.asList(runConfProps.getProperty("evidences").split(","));
					logger.error("evidenceList: " + evidencesList);
					ArrayList<BooleanVariables> inputList = getBooleanVariablesList(evidencesList);
					logger.error("evidenceList: " + inputList);
					//send it to the garbage collector
					evidencesList = null;	
				
					/****
					 * STEP 6.1: Choose to assimilate all the Semantic Evidence from the list. For example if the list has:
					 * 			 CSN, CSR, CSP. This means that if a cell has any of the evidence from that list the cell will assimilate
					 * 			 it. [No combinations] 
					 * 
					 * 			- If combinatorics is true then a recursive method will choose which combinations of semantic 
					 * 			  evidence are allowed to be assimilated by a cell.
					 */
					String combinatorics = runConfProps.getProperty("combinatorics").trim();
					logger.debug("combinatorics: " + Boolean.parseBoolean(combinatorics));
					boolean findCombinatorics = Boolean.parseBoolean(combinatorics);	
				
					//Diversion1: Involve ALL the match instances 
					if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
						boolean allSynSemCells = controlParameters.get(ControlParameterType.ALL_SYN_SEM_CELLS).isBool();
						logger.info("allSynSemCells: " + allSynSemCells);					
						if (allSynSemCells) {		
							
							/***
							 * CASE 1: All cells (instances) are selected to contribute in the calculation of the Error with the Expectation Matrix.
							 * 		   Report Individual Errors and Aggregated Error 		  
							 */	
							
							//Choose whether do call the recursive method for combinations or not
							if (findCombinatorics) {
								//Mode: Call the recursion
								cloner = new Cloner();
								cloner.dontClone(Logger.class); //do not clone this class
												
								//Plot type: SHOW_PERC_CHANGE
								ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
								controlParameters.put(showPercChange.getName(), showPercChange);
												
								//Just call the recursive method. The Aggregated errors for Msyn - Mexp have been calculated before and therefore no
								//need to calculate them again.

								//Do recursion over different combinations of evidence
								for (int k=1; k <= inputList.size(); ++k) {
									BooleanVariables[] branch = new BooleanVariables[k];
									this.accumulateCombinationsOfSemanticEvidence(inputList, k, 0, branch, 0, cloner, accumSynEvidenceMatrix, predictedValuesSynEvidenceOnly,
											actualValuesAsArray, aggregatedErrorMeasures, errorSynOnlyMap, error1COMAmap, sourceSchema, targetSchema, semanticCube, pmfList, controlParameters, tdbStore);
								
								}//end for								
								
							} else {
								//Mode: Do not call the recursion, just assimilate any evidence from the list if a cell has it
								
								//If any of the semantic evidence is available from the list then assimilate it
								Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(inputList);
								logger.debug("Assimilate the following evidence: " + evidencesToAccumulate);
								
								//Assimilate *Semantic Evidence* and store the result in a SemanticMatrix
								SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidenceMatrix, sourceSchema, targetSchema,
																								semanticCube, pmfList, evidencesToAccumulate, controlParameters);
																
								//MEASURE the aggregated error M{syn&sem} with M{exp}
								
								//Plot type: SHOW_PERC_CHANGE
								ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
								controlParameters.put(showPercChange.getName(), showPercChange);
								
								//Transform accumSemEvidenceMatrix as Array
								float[][] predictedValuesSynSem = accumSemEvidenceMatrix.getSemMatrixAsArray();
								
								//Calculate the aggregated errors using the measures 
								Map<PerformanceErrorTypes, Float> errorSynSemMap = this.calculatePerformance(predictedValuesSynSem, actualValuesAsArray, aggregatedErrorMeasures);
																
								/**********************************************
								 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
								 */				
								schemaService.getGraphvizDotGeneratorService().exportToCSVFileSynSemEvidence(errorSynOnlyMap,errorSynSemMap,controlParameters);
								
								//Plot COMA M{avg} vs M{syn,sem} *COMA*
								schemaService.getGraphvizDotGeneratorService().exportToCSVFileSynSemEvidence(error1COMAmap,errorSynSemMap,controlParameters);								
								
								
								/**********************************************
								 * PLOT MODE: PLOT_INDIVIDUAL_ERR_SYN_ONLY
								 */
								for (ErrorMeasures measure : singleErrorMeasures) {
									
									//Calculate individual errors M{syn} with M{exp} [call it error1 in this case]
									float[][] errorSynIndividual = measure.calc(predictedValuesSynEvidenceOnly, actualValuesAsArray);
																		
									//Calculate individual errors M{avg} with M{exp} [call it error1COMA in this case]
									float[][] errorCOMAAvgIndividual = measure.calc(comaAggrSimMatrix, actualValuesAsArray);
																		
									//Calculate individual errors M{syn&sem} with M{exp} [call it error2 in this case] 
									float[][] errorSynSemIndividual = measure.calc(predictedValuesSynSem, actualValuesAsArray);
									
									//Persist Individual errors M{syn&sem} with M{exp}
									ControlParameter selectionType = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
																							SelectionType.SELECT_ALL.toString());
									
									ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
																						ControlParameterType.RANKING_DESC.toString());	
									
									ControlParameter errorType = new ControlParameter(ControlParameterType.ERROR_MEASURE_TYPE, measure.getmType());
									
									//This does not make persistent the individual matches for COMA avg
									if (persistMatches) {
										this.persistMatches(accumSemEvidenceMatrix, errorSynSemIndividual, measure, BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM, null,
															selectionType, rankingType, errorType, null);
									}
									
									//PLOT - Bayesian Techniques
									schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorSynIndividual,errorSynSemIndividual,measure,
																								MatcherType.BAYESIAN_APPROACH,controlParameters,evidencesToAccumulate);											
									
									//PLOT - **COMA** AVG with M{syn,sem}
									schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorCOMAAvgIndividual,errorSynSemIndividual,measure,
																								MatcherType.COMA_AVG,controlParameters,evidencesToAccumulate);
								}//end for
								
							}							
						}//end inner if
					} else if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ONLY_SYN_SEM_CELLS))) {
						logger.info("Mode: ONLY_SYN_SEM_CELLS");
											
						//Choose whether do call the recursive method for combinations or not
						if (findCombinatorics) {
							/* Not all cells are participating to the calculation of the error, only the ones that have assimilated both syntactic and
						  	semantic evidence. The Dampening effect policy is the one that determines whether is OR or AND */
							cloner = new Cloner();
							cloner.dontClone(Logger.class); //do not clone this class
							
							//Plot type: SHOW_PERC_CHANGE
							ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
							controlParameters.put(showPercChange.getName(), showPercChange);
						
							//Do recursion over different combinations of evidence
							for (int k=1; k <= inputList.size(); ++k) {
								BooleanVariables[] branch = new BooleanVariables[k];
								this.accumulateCombinationsOfSemanticEvidence(inputList, k, 0, branch, 0, cloner, accumSynEvidenceMatrix, predictedValuesSynEvidenceOnly,
										actualValuesAsArray, aggregatedErrorMeasures, errorSynOnlyMap, error1COMAmap, sourceSchema, targetSchema, semanticCube,
										pmfList, controlParameters, tdbStore);
						
							}//end for
						} else {
							//Mode: Do not call the recursion, just assimilate any evidence from the list if a cell has it
							//If any of the semantic evidence is available from the list then assimilate it
							Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(inputList);
							logger.debug("Assimilate the following evidence: " + evidencesToAccumulate);
							
							//Assimilate *Semantic Evidence* and store the result in a SemanticMatrix
							SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidenceMatrix, sourceSchema, targetSchema,
																							semanticCube, pmfList, evidencesToAccumulate, controlParameters);
																					
							//MEASURE the aggregated error M{syn&sem} with M{exp}
							
							//Plot type: SHOW_PERC_CHANGE
							ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
							controlParameters.put(showPercChange.getName(), showPercChange);
							
							//Transform accumSemEvidenceMatrix as Array
							float[][] predictedValuesSynSem = accumSemEvidenceMatrix.getSemMatrixAsArray();
							
							
							/**********************************************
							 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
							 */		
							
							//Overwrite the previous errorSynOnlyMap
							errorSynOnlyMap = this.calculatePerformanceForSpecificCells(predictedValuesSynEvidenceOnly,
																						actualValuesAsArray, aggregatedErrorMeasures,
																						accumSemEvidenceMatrix.getIndexesSet());	
							
							//*COMA* Approach - Aggregated score with specific cells
							error1COMAmap = this.calculatePerformanceForSpecificCells(comaAggrSimMatrix,
																						actualValuesAsArray, aggregatedErrorMeasures,
																						accumSemEvidenceMatrix.getIndexesSet());	
							
							//Calculate the errors using the measures 
							Map<PerformanceErrorTypes, Float> errorSynSemMap = this.calculatePerformanceForSpecificCells(predictedValuesSynSem,
																										 actualValuesAsArray, aggregatedErrorMeasures,
																										 accumSemEvidenceMatrix.getIndexesSet());
												
							//Save the result of calculating the errors in a CSV file 
							schemaService.getGraphvizDotGeneratorService().exportToCSVFile(errorSynOnlyMap,errorSynSemMap,
																							evidencesToAccumulate, MatcherType.BAYESIAN_APPROACH,controlParameters);
							
							//Save the result of calculating the errors in a CSV file [COMA AVG]
							schemaService.getGraphvizDotGeneratorService().exportToCSVFile(error1COMAmap,errorSynSemMap,
																							evidencesToAccumulate, MatcherType.COMA_AVG,controlParameters);
							
														
							/**********************************************
							 * PLOT MODE: PLOT_INDIVIDUAL_ERR_SYN_ONLY
							 */
							for (ErrorMeasures measure : singleErrorMeasures) {
								//Calculate individual errors M{syn} with M{exp} [call it error1 in this case]
								float[][] errorSynIndividual = measure.calc(predictedValuesSynEvidenceOnly, actualValuesAsArray,
																							accumSemEvidenceMatrix.getIndexesSet());
								
								//Calculate individual errors M{avg} with M{exp} [call it error1COMA in this case]
								float[][] errorCOMAAvgIndividual = measure.calc(comaAggrSimMatrix, actualValuesAsArray,
																							accumSemEvidenceMatrix.getIndexesSet());
																
								//Calculate individual errors M{syn&sem} with M{exp} [call it error2 in this case] 
								float[][] errorSynSemIndividual = measure.calc(predictedValuesSynSem, actualValuesAsArray,
																							accumSemEvidenceMatrix.getIndexesSet());
								
								//Persist Individual errors M{syn&sem} with M{exp} for specific cells
								ControlParameter selectionType = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
																						SelectionType.SELECT_ALL.toString());
								
								ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
																					ControlParameterType.RANKING_DESC.toString());	
								
								ControlParameter errorType = new ControlParameter(ControlParameterType.ERROR_MEASURE_TYPE, measure.getmType());
								
								if (persistMatches) {
									this.persistMatches(accumSemEvidenceMatrix, errorSynSemIndividual, measure, BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM, null,
													selectionType, rankingType, errorType, accumSemEvidenceMatrix.getIndexesSet());
								}
								
								//PLOT - Bayesian Techniques
								schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorSynIndividual,errorSynSemIndividual,measure,
																							MatcherType.BAYESIAN_APPROACH,controlParameters,evidencesToAccumulate);	
								
								//PLOT - **COMA** AVG with M{syn,sem}
								schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorCOMAAvgIndividual,errorSynSemIndividual,measure,
																							MatcherType.COMA_AVG,controlParameters,evidencesToAccumulate);
								
							}//end for							
						}//end else						
					}//end if				
				}//end if		
			} catch (FileNotFoundException exc) {
			 	logger.error("train.properties file not found: " + exc);
			 	exc.printStackTrace();
			} catch (IOException ioexc) {
			  	logger.error("train.properties file not found: ", ioexc);
			  	ioexc.printStackTrace();
			}//end catch	
	}//end runConfFile()
	
	
	/***
	 * Recursive method:
	 *  - For assimilating a combination of semantic evidence given a list of semantic evidence
	 */
	private void accumulateCombinationsOfSemanticEvidence(ArrayList<BooleanVariables> arr, int k, int startId, BooleanVariables[] branch, int numElem,
			Cloner cloner, SemanticMatrix synBayesMatrix, float[][] predictedValuesSynOnly, float[][] actualValuesAsArray,
			List<PerformanceMeasures> aggregatedErrorMeasures, Map<PerformanceErrorTypes, Float> errorSynOnlyMap, Map<PerformanceErrorTypes, Float> errorCOMAavgMap,   Schema testSchema1, Schema testSchema2, List<SemanticMatrix> semanticCube,
			Map<BooleanVariables, ProbabilityMassFunction> pmfList,
			Map<ControlParameterType, ControlParameter> controlParameters, TDBStoreServiceImpl tdbStore) {	
		
		
		if (numElem == k) {
			logger.debug(" >> START - Numeric Experiment Individual Errors << ");
			logger.debug("Assimilate the following evidences: " + (Arrays.toString(branch)));
			
			//Create a deep copy of the syntactic similarity Matrix
			SemanticMatrix accumSynEvidMatrixClone = cloner.deepClone(synBayesMatrix);
			
			//Assimilate only the evidences specified by the combination
			Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(Arrays.asList(branch));
			logger.debug("Assimilate the following evidences: " + evidencesToAccumulate);
			
			//Assimilate *semantic evidences* and store the result in a SemanticMatrix
			SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidMatrixClone, testSchema1, testSchema2,
					semanticCube, pmfList, evidencesToAccumulate, controlParameters);
			
			/***
			 * Hold matrix that considers both syntactic and semantic evidences with Bayes
			 * This is the same matrix but with updated posteriors in the presence of semantic evidence
			 */
			float[][] predictedValuesSynSem = accumSemEvidenceMatrix.getSemMatrixAsArray();			
			
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
				logger.info("Mode: ALL_SYN_SEM_CELLS");

				
				/**********************************************
				 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
				 * 
				 * 			If we want to calculate the error considering all cells (instances) then Error1 (Msyn - Mexp) is calculated only
				 * 			once (outside the recursion) and Error2 (Msyn&sem - Mexp) is calculated every time				
				 */				
				logger.info("");
				logger.info("Error2 (Msyn&sem - Mexp): ");
				//Calculate the error2 using different measures ( SynSem against Expectation matrix)
				Map<PerformanceErrorTypes, Float> errorSynSemMap = this.calculatePerformance(predictedValuesSynSem, actualValuesAsArray, aggregatedErrorMeasures);  
												
				/**********************************************
				 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
				 */				
				schemaService.getGraphvizDotGeneratorService().exportToCSVFileSynSemEvidence(errorSynOnlyMap,errorSynSemMap,controlParameters);
				
				/**********************************************
				 * PLOT MODE: PLOT_INDIVIDUAL_ERR_SYN_ONLY
				 */
				for (ErrorMeasures measure : singleErrorMeasures) {
					//Calculate individual errors M{syn} with M{exp} [call it error1 in this case]
					float[][] errorSynIndividual = measure.calc(predictedValuesSynEvidenceOnly, actualValuesAsArray);
					
					//Calculate individual errors M{avg} with M{exp} [call it error1COMA in this case]
					float[][] errorCOMAAvgIndividual = measure.calc(comaAggrSimMatrix, actualValuesAsArray);					
					
					//Calculate individual errors M{syn&sem} with M{exp} [call it error2 in this case] 
					float[][] errorSynSemIndividual = measure.calc(predictedValuesSynSem, actualValuesAsArray);
					
					//Persist Individual errors M{syn&sem} with M{exp}
					ControlParameter selectionType = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
																			SelectionType.SELECT_ALL.toString());
					
					ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
																		ControlParameterType.RANKING_DESC.toString());	
					
					ControlParameter errorType = new ControlParameter(ControlParameterType.ERROR_MEASURE_TYPE, measure.getmType());
					
					if (persistMatches) {
						this.persistMatches(accumSemEvidenceMatrix, errorSynSemIndividual, measure, BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM, null,
										selectionType, rankingType, errorType, null);
					}
					
					//PLOT
					schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorSynIndividual,errorSynSemIndividual,measure,
																				MatcherType.BAYESIAN_APPROACH,controlParameters,evidencesToAccumulate);
					
					//PLOT - **COMA** AVG with M{syn,sem}
					schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorCOMAAvgIndividual,errorSynSemIndividual,measure,
									MatcherType.COMA_AVG,controlParameters,evidencesToAccumulate);
					
					
				}//end for				
			} else if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ONLY_SYN_SEM_CELLS))) {
				logger.info("Mode: ONLY_SYN_SEM_CELLS");
				
				/**********************************************
				 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
				 */	
		
				//Calculate Error-1 using all the measures [Msyn - Mexp] using specific cells
				errorSynOnlyMap = this.calculatePerformanceForSpecificCells(predictedValuesSynOnly,
																			actualValuesAsArray,
																			aggregatedErrorMeasures,
																			accumSemEvidenceMatrix.getIndexesSet());
				
				//Calculate Error-1 *COMA* using all the measure [Mavg - Mexp]
				errorCOMAavgMap = this.calculatePerformanceForSpecificCells(comaAggrSimMatrix,
																			actualValuesAsArray,
																			aggregatedErrorMeasures,
																			accumSemEvidenceMatrix.getIndexesSet());
				
				//Calculate Error-2 using all the measures [Msyn&sem - Mexp] using specific cells
				Map<PerformanceErrorTypes, Float> errorSynSemMap = this.calculatePerformanceForSpecificCells(predictedValuesSynSem,
																												actualValuesAsArray,
																												aggregatedErrorMeasures,
																												accumSemEvidenceMatrix.getIndexesSet());
								
				/**********************************************
				 * PLOT MODE: OUTPUT_NUMERIC_AGGREGATED_ERROR
				 */				
				//Save the result of calculating the errors in a CSV file 
				schemaService.getGraphvizDotGeneratorService().exportToCSVFile(errorSynOnlyMap,errorSynSemMap,
																				evidencesToAccumulate, MatcherType.BAYESIAN_APPROACH,controlParameters);

				schemaService.getGraphvizDotGeneratorService().exportToCSVFile(errorCOMAavgMap,errorSynSemMap,
																	evidencesToAccumulate, MatcherType.COMA_AVG,controlParameters);
				
				/**********************************************
				 * PLOT MODE: PLOT_INDIVIDUAL_ERR_SYN_ONLY
				 */
				for (ErrorMeasures measure : singleErrorMeasures) {
				
					//Calculate individual errors M{syn} with M{exp} [call it error1 in this case]
					float[][] errorSynIndividual = measure.calc(predictedValuesSynEvidenceOnly, actualValuesAsArray,
																				accumSemEvidenceMatrix.getIndexesSet());
					
					//Calculate individual errors M{avg} with M{exp} [call it error1COMA in this case]
					float[][] errorCOMAAvgIndividual = measure.calc(comaAggrSimMatrix, actualValuesAsArray);	
													
					//Calculate individual errors M{syn&sem} with M{exp} [call it error2 in this case] 
					float[][] errorSynSemIndividual = measure.calc(predictedValuesSynSem, actualValuesAsArray,
																				accumSemEvidenceMatrix.getIndexesSet());
					//PLOT
					schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorSynIndividual,errorSynSemIndividual,measure,
																							MatcherType.BAYESIAN_APPROACH,controlParameters,evidencesToAccumulate);	
					
					//PLOT - **COMA** AVG with M{syn,sem}
					schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynSem(errorCOMAAvgIndividual,errorSynSemIndividual,measure,
																							MatcherType.COMA_AVG,controlParameters,evidencesToAccumulate);
				}//end for		
				
			}//end if		
			
			
			logger.debug(" >> END << ");		     
	        return;	
		}//end if		
		
		 for (int i = startId; i < arr.size(); ++i) {
		       branch[numElem++] = arr.get(i);
		       accumulateCombinationsOfSemanticEvidence(arr, k, ++startId, branch, numElem,	cloner,	synBayesMatrix, predictedValuesSynOnly,
		    		   		actualValuesAsArray, aggregatedErrorMeasures, errorSynOnlyMap, errorCOMAavgMap, 
	 						testSchema1, testSchema2, semanticCube, pmfList, controlParameters,
	 						tdbStore);
		       --numElem;
		}//end for	
	}//end accumulateCombinationsOfSemanticEvidence()
	
	/***
	 * Get entity URI for a SuperAbstract
	 */
	private String getEntityURI(CanonicalModelConstruct construct) {
		StringBuilder sb = new StringBuilder();
		
			if (construct instanceof SuperAbstract) {
				sb.append("http://").append(construct.getSchema().getDataSource().getName()).append("#").append(construct.getName());							
			} else if (construct instanceof SuperLexical) {
				//find the parent of this super abstract
				SuperAbstract parentSuperAbstract = ((SuperLexical) construct).getFirstAncestorSuperAbstract();
				sb.append("http://").append(construct.getSchema().getDataSource().getName()).append("#").append(parentSuperAbstract.getName())
							.append(".").append(construct.getName());					
			}//end if		
	
		return sb.toString();		
	}//end getEntityURI()

	/***
	 * Supportive Method: Return a BNode as a Resource to add a match along with the errors calculated using either Absolute or
	 * 					  Square error.
	 */
	private Resource createMatch(Model model, String sourceName, String targetName, double score, double error, ErrorMeasures measure) {
		
		Resource bNode = model.createResource();
		bNode.addLiteral( VOCAB.sourceEntity, sourceName );
		bNode.addLiteral( VOCAB.targetEntity, targetName );
		bNode.addLiteral( VOCAB.score, score );
		
		if (measure.getmType().equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
			bNode.addLiteral( VOCAB.absoluteError, error );
		} else if (measure.getmType().equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
			bNode.addLiteral( VOCAB.squareError, error );
		}//end if
		
		return bNode;
	}//end createMatch()
	
	/***
	 * Method: For all the aggregated performance measure as these are specified in the property configuration file, this method
	 * 		   produces a HashMap with the score obtained from each method calculated.
	 * predictedMatrix - values predicted by the algorithm
	 * observedMatrix - actual values
	 */
	public Map<PerformanceErrorTypes, Float> calculatePerformance(float[][] predictedMatrix, float[][] observedMatrix,
																	List<PerformanceMeasures> aggregatedErrorMeasures) {
		logger.info("in calculatePerformance()");
		Map<PerformanceErrorTypes, Float> petMap = new HashMap<PerformanceErrorTypes, Float>();
				
		for (PerformanceMeasures pm : aggregatedErrorMeasures) {
			//add each performance measure to the map
			PerformanceErrorTypes pet = pm.getmType(); 
			float res = pm.calc(predictedMatrix, observedMatrix);			
			petMap.put(pet, res);			
			//logger.info("PerformanceMeasure is: " + pet + " | Result: " + res);
		}
		
		return petMap;
	}//end calculatePerformance()
		
	
	/***
	 * Calculate performance of specific cells
	 * predictedMatrix - values predicted by the algorithm
	 * observedMatrix - actual values 	
	 */
	public Map<PerformanceErrorTypes, Float> calculatePerformanceForSpecificCells(float[][] predictedMatrix, float[][] observedMatrix,
																					List<PerformanceMeasures> aggregatedErrorMeasures,
																					Set<SemanticMatrixCellIndex> iSet) {

		logger.info("in calculatePerformanceForSpecificCells()");
		Map<PerformanceErrorTypes, Float> petMap = new HashMap<PerformanceErrorTypes, Float>();

		for (PerformanceMeasures pm : aggregatedErrorMeasures) {
			//add each performance measure to the map
			PerformanceErrorTypes pet = pm.getmType(); 			
			float res = pm.calc(predictedMatrix, observedMatrix, iSet);			
			petMap.put(pet, res);			
			//logger.info("PerformanceMeasure is: " + pet + " | Result: " + res);
		}//end for

		return petMap;
	}//end calculatePerformance()	
	
	/***
	 * Get a list of BooleanVariables from text
	 */
	private ArrayList<BooleanVariables> getBooleanVariablesList(List<String> evidencesList) {
		
		ArrayList<BooleanVariables> bList = new ArrayList<BooleanVariables>();
		
		for (String s : evidencesList) {
			bList.add(BooleanVariables.fromValue(s.trim()));
		}
		
		return bList;		
	}//end
	
	/***
	 * Method that returns control parameters for the COMA approach, used while aggregating COMA
	 */
	private Map<ControlParameterType, ControlParameter> getCOMAcontrolParameters() {
		Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		
		//COMA configuration: Selection Method (SELECT_ALL)
		ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, SelectionType.SELECT_ALL.toString());
		controlParameters.put(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, selectionTYPE);
				
		//COMA configuration: Aggregation Method (AVERAGE)
		ControlParameter aggregationTYPE = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE, AggregationType.SIMAVERAGE.toString());
		controlParameters.put(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE, aggregationTYPE);
		
		return controlParameters; 
	}//end getCOMAcontrolParameters()	
	
		
	/***
	 * Supportive Method: The data of an ontology is stored in the TDB store under the base URI x-ns://source.data/onto/
	 * @param ontologyName
	 * @return URI string
	 */
	private String getNamedGraphURIforOntology(String ontologyName) {
		return new StringBuilder(ONTOLOGY_DATA_URI).append(ontologyName).toString();
	}//end getNamedGraphURIforOntology()	
}//end OntologiesServiceBenchmarkImpl()
