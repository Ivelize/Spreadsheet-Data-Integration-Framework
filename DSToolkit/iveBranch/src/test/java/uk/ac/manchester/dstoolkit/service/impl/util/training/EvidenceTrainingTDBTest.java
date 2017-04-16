package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractInitialisation;
import uk.ac.manchester.dstoolkit.domain.models.statistics.DensityEstimator;
import uk.ac.manchester.dstoolkit.domain.models.statistics.KDESample;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.statistics.DensityEstimatorRepository;
import uk.ac.manchester.dstoolkit.repository.statistics.KDESampleRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.JaroWinklerServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.LevenshteinMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.GaussianKernel;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelCaseType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelDenstityEstimator;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelType;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.LogTransformation;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.smoothing.LaplaceSmoothing;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.statistics.DensityEstimatorService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.JenaJungRDFVisualisationService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;


/***
 * This JUnit class will be responsible for calling the appropriate methods to create probability distributions
 * (likelihoods) for all the Semantic Evidences. The probability distributions for semantic evidences are probability
 * mass functions (PMF). In doing so the LOV aggregator SPARQL endpoint will be used. 
 * This endpoint tries to aggregate all the vocabularies that exist in the LOD cloud under a single endpoint. For the 
 * experiments I have managed to get a stable version of it and I am planning to use this materialised copy to do the 
 * training instead to accessing the SPARQL endpoint directly through SPARQL federated queries using the SERVICE keyword
 * all the time. Having access to the data this class proceeds as follows:
 * 
 *  1. import the RDF-graph that holds the LOV endpoint into a TDB Store
 * 
 * 
 * @author klitos
 */
public class EvidenceTrainingTDBTest extends RDFAbstractInitialisation {
	
	private static Logger logger = Logger.getLogger(EvidenceTrainingTDBTest.class);
	
	/*Properties for ./src/main/resources/datasources/train.properties*/
	protected static String DATA_ANALYSIS;
	protected static String	EVID_CLASSES;
	protected static String	EVID_PROPS;	
	protected static String ENDPOINT_DATA;
	
	/*Properties for ./src/test/resources/training/benchmark/benchmark.properties*/
	protected static String ALIGN_GRAPH;
	protected static String CLASSIFY_TP;
	protected static String CLASSIFY_FP;	
		
	@Autowired
	@Qualifier("densityEstimatorService")	
	private DensityEstimatorService densityEstimatorService;	
	
	@Autowired
	@Qualifier("densityEstimatorRepository")	
	private DensityEstimatorRepository densityEstimatorRepository;	

	@Autowired
	@Qualifier("kdeSampleRepository")	
	private KDESampleRepository kdeSampleRepository;
	
	///	
	
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
	@Qualifier("jenaJungRDFVisualisationService")
	private JenaJungRDFVisualisationService jenaJungRDFVisualisationService;
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;	
	
	/*Hold a reference to the TDBStore*/
	private TDBStoreServiceImpl tdbStore = null;
	
	/*Configuration files*/
	private static String trainModelsPropLoc = "./src/main/resources/datasources/train.properties";
	
	/*Location of the local LOV RDF-graph.*/
	private static String local_LOV1_graph = "./src/test/resources/sparql_endpoints/LOV_1.sparql";	
	private static String local_LOV2_graph = "./src/test/resources/sparql_endpoints/LOV_2.sparql";	
	
	/*Benchmark alignment files*/
	private static String alignmentPropLoc = "./src/test/resources/training/benchmark/benchmark.properties";
	
	@Override
	@Before
	public void setUp() {
		super.setUp();	
	}//end setUp()	
	
	
	/******************************
	 * Semantic Evidence Training *
	 ******************************/
	
	/**
	 * Gia to creation ton semantic evidences kai contingency tables I will do the tests here
	 */
	
	
	@Test
	public void testGenerateSemanticEvidence() {
		logger.debug("testGenerateSemanticEvidence()");
		
		/*Get the reference to the TDB store*/		
		tdbStore = this.getTDBStore();

		/*Prepare the system for training by loading the property file: ./src/main/resources/datasources/train.properties*/
		this.loadConfigTraining(trainModelsPropLoc);

		/**1. Load the Data**/
		
		/*Load the RDF data that will be used for training to the TDBStore*/
		loadRDFSourceUtilService.loadTDBStore(tdbStore, local_LOV1_graph, ENDPOINT_DATA);
		
		/****/
		
		/*Perform data Analysis over the RDF dump, and make the results persistent as an RDF-Graph*/
		SemEvidenceDataAnalysisUtilImpl dataAnalysis = new SemEvidenceDataAnalysisUtilImpl(tdbStore, DATA_ANALYSIS, ENDPOINT_DATA);
		dataAnalysis.doDataAnalysis(false);
			
		/* Within the sets created from the DataAnalysis search for evidences*/	
		SemEvidenceTrainingUtilImpl trainService = new SemEvidenceTrainingUtilImpl(tdbStore, DATA_ANALYSIS, EVID_CLASSES,
																										EVID_PROPS, ENDPOINT_DATA);
						
		//Create the likelihoods for Classes
		trainService.createTrainingSetClasses(false);	
		
		//Create the likelihoods for Props
		trainService.createTrainingSetProps(false);	
				
		
		//Construct Contingency Tables
		LaplaceSmoothing laplaceSmoothing = new LaplaceSmoothing(1, 2);
		Map<BooleanVariables, ProbabilityMassFunction> pmfList = trainService.constructPMF(laplaceSmoothing);
		
		
		//Loop the list of PMF and print the contingency tables to view them, for debugging 
		for (ProbabilityMassFunction pmf : pmfList.values()) {
			logger.debug("\n");
			logger.debug("\n" + pmf.getContingencyTable().toString());
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		/***
		 * The idea is the following, first I call the dataAnalysis.doDataAnalysis to do the counts for each axiom used to
		 * identify pairs of equivalent/non-equivalent constructs (Classes, properties). Then to calculate the sums for the
		 * set of equivalent classes, set of equivalent properties, set of non-equivalent props, set of non-equivalent props
		 * the dataAnalysis.doDataAnalysis() method makes a call to the private createResultsTable(), this method will then 
		 * calculate the total sums for each set and make them persistent as an RDF Graph. If I want to access the final 
		 * total sums I need to write a SPARQL query to search for the subject that has an rdf:type Dataset property.
		 */
		
		
		
		
		
		
		
		
		
		
		
		
		
		tdbStore.close();
	
		//Visualise the data analysis model
		//Model resultsModel = tdbStore.getModel(DATA_ANALYSIS);
		//jenaJungRDFVisualisationService.visualiseRDFGraph(resultsModel, true, false);
		
		/****/	
		//Construct the likelihoods		
		//SemEvidenceTrainingUtilImpl trainService = new SemEvidenceTrainingUtilImpl(tdbStore, DATA_ANALYSIS, ENDPOINT_DATA, EQUI_CLASS,
											//							    NON_EQUI_CLASS, EQUI_PROPS, NON_EQUI_PROPS);
		
		//Create the likelihoods for Classes
		//trainService.createTrainingSetClasses(false);

		//Create the likelihoods for Properties
		//trainService.createTrainingSetProps(false);
		
		
		/****/		
		
		
		
		
		//trainingDataService.attach(dataAnalysis);
		/*Create the sets necessary for training*/
		//TrainingDataService tds = new TrainingDataServiceImpl(tdbStore);
		//tds.createTrainingSets();
		
		/*Load the sdb_metadata*/
		//trainingDataService.loadMetaDataSDBStore(metaDataSDBStore);
		
		/*Initialise the training data service class*/
		//trainingDataService.init();
						
		/*Load the SPARQL endpoint in sdb_metadata*/
		
		//Dataset dataset = metaDataSDBStore.getDataset();

		//Iterator<String> graphNames = dataset.listNames();
		//while (graphNames.hasNext()) {
		  //  String graphName = graphNames.next();
		  //  logger.debug("graph: " + graphName);
		//}
		
		
		//metaDataSDBStore.loadDataToModelFile(sparqlURL, sparqlDumpPath);
		
		
		//ActionStatus status = trainingDataService.loadSPARQLendpoint(sparqlURL, sparqlDumpPath);
		
		//Iterator<String> graphNames2 = dataset.listNames();
		//while (graphNames2.hasNext()) {
		//    String graphName = graphNames2.next();
		//    logger.debug("graph2: " + graphName);
		//}
		
		
		//ActionStatus status = trainingDataService.loadSPARQLendpoint(sparqlURL, sparqlDumpPath);
		
		//if (status.getStatus() == 1) {
			//Model model = metaDataSDBStore.getModel(modelName);
			//logger.debug("status: model has been loaded");	
		//}
		
		
		//Model model = trainingDataService.loadSPARQLendpoint(rdfSourceName, connectionURL);
		//logger.debug("status: " + model);		
		
		
		//trainingDataService.createSetOfEquivalentClassConstructs(sparqlName, sparqlURL, sparqlDumpPath, false);
	}//end method
	
	
	/******************************
	 * Syntactic Evidence Training *
	 ******************************/
	
	/**
	 * Run the benchmark to get the likelihoods for each matcher: edit-distance, ngram matcher.
	 * The output of this method is a set of .csv files, that contain sample data points used later for
	 * the construction of the Kernel Density Estimator
	 * 
	 */	
	@Test
	public void testGenerateSyntacticEvidence() {
		logger.debug("** Start Benchmark Experiment - EditDistance & N-Gram **");
		
		/*Get the reference to the TDB store*/		
		tdbStore = this.getTDBStore();

		/*Prepare the system for training by loading the property file: ./src/test/resources/training/benchmark/benchmark.properties*/
		Map<String, String> alignMap = this.loadConfigAlignments(alignmentPropLoc);
		
		/*Setup the Benchmark class (the results of this process are made persistent to the TDB store)*/
		SynEvidenceDataAnalysisUtilImpl benchmarkService = new SynEvidenceDataAnalysisUtilImpl(tdbStore, ALIGN_GRAPH, CLASSIFY_TP, CLASSIFY_FP);
		benchmarkService.resetAll(); //Reset all the named graphs necessary for the experiment
		benchmarkService.addAlignments(alignMap); //List of the alignment files
		benchmarkService.addMatcher(new NGramMatcherServiceImpl(3));
		benchmarkService.addMatcher(new LevenshteinMatcherServiceImpl());
		benchmarkService.runExperiment();
	}//end testRunBenchmark	
	
	
	/***
	 * This method will do the training of the JaroWinklerDistance matcher on the rdfs:labels. This method computes the 
	 * Jaro-Winkler distance on the values of rdfs:label of owl:Classes that are known to be equivalent from the alignments
	 * provided by the OAEI group.
	 */
	@Test
	public void testGenerateSyntacticEvidenceJaroWinkler() {
		logger.debug("** Start Benchmark Experiment - JaroWinkler **");
		
		/*Get the reference to the TDB store*/		
		tdbStore = this.getTDBStore();

		/*Prepare the system for training by loading the property file: ./src/test/resources/training/benchmark/benchmark.properties*/
		Map<String, String> alignMap = this.loadConfigAlignments(alignmentPropLoc);
		
		/*Setup the Benchmark class (the results of this process are made persistent to the TDB store)*/
		SynEvidenceDataAnalysisUtilImpl benchmarkService = new SynEvidenceDataAnalysisUtilImpl(tdbStore, ALIGN_GRAPH, CLASSIFY_TP, CLASSIFY_FP);
		benchmarkService.resetAll(); //Reset all the named graphs necessary for the experiment
		benchmarkService.addAlignments(alignMap); //List of the alignment files
		
		//Get access to an instance of the Jaro-Winkler implementation with the parameters as of the paper
		JaroWinklerServiceImpl jaroWinklerMatcher = JaroWinklerServiceImpl.JARO_WINKLER_DISTANCE;
		benchmarkService.addMatcher(jaroWinklerMatcher);
		benchmarkService.runExperiment();
	}//end testRunBenchmark
	
	
	/**
	 * This class shows how the Kernel Density Estimator can be persistent
	 */
	@Test
	public void testPersistentKernelDesnityEstimatorClasses() {
		
		Set<KDESample> sampleDataPoints = new LinkedHashSet<KDESample>();
		
		KDESample p1 = new KDESample(0.41);
		KDESample p2 = new KDESample(0.42);
		
		sampleDataPoints.add(p1);
		sampleDataPoints.add(p2);
				
		DensityEstimator e1 = new DensityEstimator("kde_test", KernelEstimatorType.LEVENSHTEIN_KDE, false, KernelType.GAUSSIAN, 0.023, KernelCaseType.TP_CASE, sampleDataPoints);
		
		System.out.println("Estimator 1: >>> " + e1.getEstimatorName());
				
		for (KDESample cmp : sampleDataPoints) {
			cmp.setSampleOf(e1);
		}//end for
		
		System.out.println("Estimator 1: >>> " + p1.getSampleOf().getEstimatorName());
		
		densityEstimatorService.addDensityEstimator(e1);		
		densityEstimatorService.save(e1);		
		
		//Retrieve Density Estimator from db and get the index of the first point
		DensityEstimator r1 = densityEstimatorRepository.getDensityEstimatorByName("kde_test");
		KDESample r1SampleIndex = kdeSampleRepository.getKDESamplePointOfDensityEstimatorWithID(r1.getId());
				
		System.out.println("Count : >>> " + kdeSampleRepository.countKDESamplePointsOfDensityEstimatorWithID(r1.getId()) );
		
	}//end test
	
	
	/***
	 * Test the implementation functionality of the Kernel Density Estimator  
	 * @throws IOException 
	 */
	@Test
	public void testKernelDesnityEstimatorClasses() throws IOException {
		logger.debug("in testKernelDesnityEstimatorClasses()");
		
		/***
		 * Kernel Density Estimator without support K(*) is Gaussian Kernel,
		 * below is an example on how to setup a Kernel Estimator without support
		 */
		GaussianKernel normalKernel = new GaussianKernel();
		double h1 = 0.0970228; //we need the smoothing parameter
		KernelDenstityEstimator kdeNoSupport = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, normalKernel, h1);
		kdeNoSupport.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");		
		
		//Find the density of point x from the PDF
		double x = 0.971;
		
		//Call the KDE estimator without support
		double y1 = kdeNoSupport.fx(x);		
		logger.debug("Result (kde - no support): " + y1);
		
		//double a = -2;
		//double b = 2;
		
		//logger.debug("Definite Integral: " + kdeNoSupport.calcIntegral(a, b));
				
		
		/***
		 * Kernel Density Estimator with support
		 */

		LogTransformation logTransformation = new LogTransformation(-0.1, 1.1);
		double h2 = 0.609385;		
		KernelDenstityEstimator kdeWithSupport = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, normalKernel, h2, logTransformation);
		kdeWithSupport.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");
		
		//Call the KDE estimator with support
		double y2 = kdeWithSupport.fx(x);		
		logger.debug("Result (kde - with support): " + y2);
		
	}//end test
	
	/***
	 * Test the behaviour of approximating an integral using one step simpson's rule 
	 * @throws IOException
	 */
	@Test
	public void testPDFCreationWithSimpsonRule1() throws IOException {		
		logger.debug("in testPDFCreationWithSimpsonRule()");

		GaussianKernel normalKernel = new GaussianKernel();
		double h1 = 0.0970228; //we need the smoothing parameter
		KernelDenstityEstimator kdeNoSupport = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, normalKernel, h1);
		kdeNoSupport.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");		
		
		/* Create a ProbabilityDensityFunction, each syntactic matcher will have a PDF that is
		a non-parametric function using kernels */ 
		ProbabilityDensityFunction pdf = new ProbabilityDensityFunction(-2, 2, kdeNoSupport, KernelCaseType.TP_CASE);
		pdf.createIntegralVector(); //This will approximate the integral into a vector
		
		double a = 0.55;
		double b = 0.65;
		double pr = pdf.approximateIntegralSmart(a, b);			
		logger.debug("Pr : " + pr);		
	}//end test
	
	/***
	 * Test the behaviour of approximating an integral using one step simpson's rule,
	 * with KERNEL SUPPORT
	 *  
	 * @throws IOException
	 */
	@Test
	public void testPDFCreationWithSimpsonRule2() throws IOException {		
		logger.debug("in testPDFCreationWithSimpsonRule()");

		GaussianKernel normalKernel = new GaussianKernel();
		double h_tp = 0.609385; //smoothing parameter for this case
		LogTransformation logTransformation = new LogTransformation(-0.1, 1.1);
		KernelDenstityEstimator kdeWithSupport = new KernelDenstityEstimator(KernelEstimatorType.LEVENSHTEIN_KDE, normalKernel, h_tp, logTransformation);
		kdeWithSupport.readDataPointsFromLocation("./src/test/resources/training/kernel/tp_Levenshtein.dat");
		
		
		/* Create a ProbabilityDensityFunction, each syntactic matcher will have a PDF that is
		a non-parametric function using kernels */ 
		
		//NOTE: Now the boundaries need to be from 0 to -1
		
		ProbabilityDensityFunction pdf = new ProbabilityDensityFunction(0, 1, kdeWithSupport, KernelCaseType.TP_CASE);
		pdf.createIntegralVector(); //This will approximate the integral into a vector
		
		double a = 0.7;
		double b = 0.7;
		double pr = pdf.approximateIntegralSmart(a, b);			
		logger.debug("Pr : " + pr);		
	}//end test	
	
	/***
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
	
	/***
	 * Load alignments property file ./src/test/resources/training/benchmark/benchmark.properties
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
			ALIGN_GRAPH = alignProps.getProperty("train_matcher_align");
			CLASSIFY_TP = alignProps.getProperty("train_classify_tp");  
			CLASSIFY_FP = alignProps.getProperty("train_classify_fp");			
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