package uk.ac.manchester.dstoolkit.service.impl.util.training;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
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
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.DomainSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.EquivalenceSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.HierarchySemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.NameSpaceSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.RangeSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ShareSuperClassSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SubsumptionSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.BenchmarkType;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.DampeningEffectPolicy;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.MatchingServiceBenchmarkImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.PlotType;
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
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.EvaluatorService;

import com.hp.hpl.jena.query.QueryFactory;
import com.rits.cloning.Cloner;

/**
 * This is a singleton class
 * 
 * @author klitos
 * 
 * Evaluator class
 */
@Service(value = "evaluatorService")
public class EvaluatorServiceImpl implements EvaluatorService {
	
	static Logger logger = Logger.getLogger(EvaluatorServiceImpl.class);

	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	/*Hold performance measure to calculate*/
	private ArrayList<PerformanceMeasures> aggregatedErrorMeasures = null;
	private ArrayList<ErrorMeasures> singleErrorMeasures = null;
	
	/*Constructor*/
	public EvaluatorServiceImpl() {
		aggregatedErrorMeasures = new ArrayList<PerformanceMeasures>();
		singleErrorMeasures = new ArrayList<ErrorMeasures>();
	}	
	
	/***
	 * This method seeks to show that the assimilation with Bayes approach works. It does so by comparing each 
	 * piece of syntactic evidence returned by each matcher with the assimilation of the pieces of evidence 
	 * returned by both the syntactic matchers.
	 * 
	 * (1) Run a matcher {ed, ng} alone and obtain a similarity matrix. Use the similatiry matrix to obtain
	 * degrees of belief. 
	 * (2) Run both matchers and combine them using the Bayesian technique, obtain M{syn,sem}.
	 * 
	 */
	public void runNumericExpSyntacticOnly(String filePath, Schema testSchema1, Schema testSchema2, 
															Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in runNumericExpSyntacticOnly");
		
		List<MatcherService> matcherList = null;
		
		try {			
			if ((testSchema1 != null) && (testSchema2 != null)) {
				logger.debug("Running: " + filePath);	
				
				//Read the actual configuration file run.conf and setup the approach
				InputStream confProps = new FileInputStream(filePath);
				Properties runConfProps = new java.util.Properties();
				runConfProps.load(confProps);
				
				//Get some configuration parameters for the matchers
				ControlParameter selectionTYPE = null;
				SelectionType st = SelectionType.fromValue(runConfProps.getProperty("matcher_selection_type"));			

				if (st != null) {		
					logger.debug("selectionTYPE: " + st.toString());
					selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, st.toString());	
				}//end if
				
				/** 
				* Step 1: Prepare the two StringBasedMatchers as specified in the configuration files
				*/
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
							editDistMatcher.addControlParameter(selectionTYPE);
						
						} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
							nGramMatcher = new NGramMatcherServiceImpl(3); 
							nGramMatcher.addControlParameter(selectionTYPE);					
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
				
				//Call schemaService and run the matcher phase of the Dataspace, not necessary to add control parameters
				List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
				
				//Accumulate syntactic evidences from both matchers {ed, ng}
				SemanticMatrix accumSynEvidenceMatrix = schemaService.accumulateSyntacticEvidenceBayes(testSchema1, testSchema2,
						   																						simCubeOfMatchers, null);
				//Hold a matrix with degrees of belief from both matchers {Msyn}
				float[][] matrixSynOnlyAsDegreesOfBelief = accumSynEvidenceMatrix.getSemMatrixAsArray();
								
				
				/***
				 * STEP 2: Get the expectation matrix
				 */
				//Get the Expectation matrix
				String alignmentExpectation = runConfProps.getProperty("alignment").trim();
				SemanticMatrix gtMatrix = schemaService.generateExpectationMatrix(testSchema1, testSchema2, alignmentExpectation);
				
				//Convert semantic matrix to plain 2D structures (actual = real values)
				float[][] actualMatrix = gtMatrix.getSemMatrixAsArray();
								
				
				/****
				 * Collect which measures to use for the numeric evaluation (Individual Measures)
				 */
				List<String> measuresArray = null;
				
				/**
				 * If PLOT_TYPE = OUTPUT_NUMERIC_AGGREGATED_ERROR
				 */				
				if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_TYPE))) {
					PlotType pt = controlParameters.get(ControlParameterType.PLOT_TYPE).getPlotType();
					if ((pt != null) && (pt.equals(PlotType.OUTPUT_NUMERIC_AGGREGATED_ERROR))) {
						/****
						 * Collect which measures to use for the numeric evaluation (Aggregated Measures)
						 */
						//Read which performance measures to use from the conf file
						measuresArray = Arrays.asList(runConfProps.getProperty("performance_measures").split(","));				
				
						if (measuresArray != null) {
							for (String name : measuresArray) {				
								PerformanceErrorTypes pet = PerformanceErrorTypes.fromValue(name);
						
								if ((pet != null) && pet.equals(PerformanceErrorTypes.MEAN_SQUARED_ERROR)) {
									this.attactAggrErrMeasure( new MeanSquaredError() );
								} else if ((pet != null) && pet.equals(PerformanceErrorTypes.ROOT_MEAN_SQUARED_ERROR)) {
									this.attactAggrErrMeasure( new RootMeanSquaredError() );
								} else if ((pet != null) && pet.equals(PerformanceErrorTypes.MEAN_ABS_ERROR)) {
									this.attactAggrErrMeasure( new MeanAbsoluteError() );
								} else if ((pet != null) && pet.equals(PerformanceErrorTypes.RELATIVE_SQUARED_ERROR)) {
									this.attactAggrErrMeasure( new RelativeSquaredError() );
								} else if ((pet != null) && pet.equals(PerformanceErrorTypes.ROOT_RELATIVE_SQUARED_ERROR)) {
									this.attactAggrErrMeasure( new RootRelativeSquaredError() );
								} else if ((pet != null) && pet.equals(PerformanceErrorTypes.RELATIVE_ABS_ERROR)) {
									this.attactAggrErrMeasure( new RelativeAbsoluteError() );
								} else if ((pet != null) && pet.equals(PerformanceErrorTypes.CORRELATION_COEFFICIENT)) {
									this.attactAggrErrMeasure( new CorrelationCoefficient() );
								}					
							}//end for
						}//end if						
						
						//Plot type: SHOW_PERC_CHANGE
						ControlParameter showPercChange = new ControlParameter(ControlParameterType.SHOW_PERC_CHANGE, true);
						controlParameters.put(showPercChange.getName(), showPercChange);					
						
					} else {						
						//Which individual error measure to use for the calculations
						measuresArray = Arrays.asList(runConfProps.getProperty("error_measure").split(","));
						logger.debug("measuresArray: " + measuresArray);
						if (measuresArray != null) {
							for (String name : measuresArray) {
								ErrorMeasuresTypes pet = ErrorMeasuresTypes.fromValue(name);
								if ((pet != null) && pet.equals(ErrorMeasuresTypes.SQUARED_ERROR)) {
									this.attactSingErrMeasure( new SquaredError() );
								}  else if ((pet != null) && pet.equals(ErrorMeasuresTypes.ABSOLUTE_ERROR)) {
									this.attactSingErrMeasure( new AbsoluteError() );
								}
							}
						}//end if						
					}//end else
				}//end if selection of error measures
				
				//Send it to the garbage collector
				measuresArray = null;	
				
				//Collecting parameters for the selection of cells (instances) that will participate in the calculation of the errors
				String cells_selection = runConfProps.getProperty("cells_selection").trim();
				if (cells_selection.equals("ALL_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ALL_SYN_SEM_CELLS, true);
					controlParameters.put(param.getName(), param);
				} else if (cells_selection.equals("ONLY_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ONLY_SYN_SEM_CELLS, true);
					controlParameters.put(param.getName(), param);
				}				
				

									
												
				/***
				 * If PLOT_TYPE = OUTPUT_NUMERIC_AGGREGATED_ERROR, then ran each string-based matcher calculate the error with the
				 * expectation matrix and then output the numeric aggregated evaluation into a csv file.
				 */			
				if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.PLOT_TYPE))) {
					PlotType pt = controlParameters.get(ControlParameterType.PLOT_TYPE).getPlotType();
					if ((pt != null) && (pt.equals(PlotType.OUTPUT_NUMERIC_AGGREGATED_ERROR))) {					
						logger.info("Plot Type: " + PlotType.OUTPUT_NUMERIC_AGGREGATED_ERROR.toString());	
												
						//Firstly, derive Error2: which is the error of M{ng, ed} with M{exp}
						Map<PerformanceErrorTypes, Float> error2 = null;
						if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
							boolean allSynSemCells = controlParameters.get(ControlParameterType.ALL_SYN_SEM_CELLS).isBool();
							if (allSynSemCells) {
								logger.info("Calculating error2(M_syn, Mexp) matrices: ");
								//calculate error1 here because we would like to calculate it only once
								error2 = this.calculatePerformance(matrixSynOnlyAsDegreesOfBelief, actualMatrix);
							}					
						}//end if
						
						
						
						//Then, calculate error1 for each individual matcher						
						for (MatcherInfo info : simCubeOfMatchers) {
							matcherList = new ArrayList<MatcherService>();
					
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
							SemanticMatrix dobMatrixTemp = schemaService.accumulateSyntacticEvidenceBayes(testSchema1, testSchema2,
									   													       newSimCubeOfMatchers, null);
					
							//Get a 2D representation of the 
							float[][] dobMatrix = dobMatrixTemp.getSemMatrixAsArray();
					
							//Derive Error1: which is the error of a single matcher with M{exp}
							Map<PerformanceErrorTypes, Float> error1 = null;
					
							if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
								boolean allSynSemCells = controlParameters.get(ControlParameterType.ALL_SYN_SEM_CELLS).isBool();
								if (allSynSemCells) {
									logger.info("Calculating error1(M_syn, Mexp) matrices: ");
									//calculate error1 here because we would like to calulate it only once
									error1 = this.calculatePerformance(dobMatrix, actualMatrix);
								}					
							}//end if
					
							/***
							 * Output the results
							 */					
							schemaService.getGraphvizDotGeneratorService().exportToCSVFileOnlySyntacticEvidence(error1,error2,mt,controlParameters);
						}//end for			
					} else if ((pt != null) && (pt.equals(PlotType.PLOT_INDIVIDUAL_ERR_SYN_ONLY))) {	
						logger.info("Plot Type: " + PlotType.PLOT_INDIVIDUAL_ERR_SYN_ONLY.toString());
									
						//Firstly, derive individual errors between: M{syn} with M{exp} using some error measure
						for (ErrorMeasures measure : singleErrorMeasures) {
						
							//Calculate individual error  M{syn} with M{exp}
							float[][] error2 = measure.calc(matrixSynOnlyAsDegreesOfBelief, actualMatrix);
										
							
							//Then calculate M{ng} with M{exp} or M{ed} with M{exp}					
							for (MatcherInfo info : simCubeOfMatchers) {
								matcherList = new ArrayList<MatcherService>();
					
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
						
								//From raw similarity of individual matcher to degrees of belief
								SemanticMatrix dobMatrixTemp = schemaService.accumulateSyntacticEvidenceBayes(testSchema1, testSchema2,
								   													       					newSimCubeOfMatchers, null);
							
								//Get a 2D representation of the 
								float[][] dobMatrix = dobMatrixTemp.getSemMatrixAsArray();
						
								//Calculate individual errors
								float[][] error1 = measure.calc(dobMatrix, actualMatrix);
								
								//Print the results into a file
								schemaService.getGraphvizDotGeneratorService().exportIndividualPairErrorsSynOnly(error1,error2,mt,measure,controlParameters);
								
						
							}//end for
						
						
						}//end for	
					}//end PLOT_INDIVIDUAL_ERR_SYN_ONLY
				}//end if				
			}//end if
		} catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 	exc.printStackTrace();
		} catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		  	ioexc.printStackTrace();
		}//end catch		
	}//end runNumericExpSyntacticOnly()	
	
	/***
	 * This method is responsible for running the Bayesian framework up to the point of applying only the syntactic
	 * evidences and transforming similarity scores to degrees of belief. The method is responsible of running 
	 * various configuration files specified by the user from: 
	 *   ./src/test/resources/runConf/runXexp1.conf
	 *   
	 * 
	 * @return SemanticMatrix - syntactic similarity matrix for matching with the Bayesian technique
	 */
	public SemanticMatrix runBayesFromConfigFilesTopK(String filePath, Schema testSchema1, Schema testSchema2) {
		logger.debug("in runBayesFromConfigFiles [only syntactic aggregation]");
		
		List<MatcherService> matcherList = new ArrayList<MatcherService>();
		
		try {			
			if ((testSchema1 != null) && (testSchema2 != null)) {
				
				logger.debug("Running: " + filePath);	
				
				//Read the actual configuration file run.conf and setup the approach
				InputStream confProps = new FileInputStream(filePath);
				Properties runConfProps = new java.util.Properties();
				runConfProps.load(confProps);
				
				/** 
				* Step 1: Prepare the two StringBasedMatchers as specified in the configuration files
				*/
				logger.debug(">> Preparing StringBasedMatchers..");	
				
				//Get some configuration parameters for the matchers
				ControlParameter selectionTYPE = null;
				SelectionType st = SelectionType.fromValue(runConfProps.getProperty("matcher_selection_type"));			
			
				/***
				 * Each Matcher is responsible for individually doing the selection, because the aggregation, combination
				 * of evidences is done by the Bayesian approach. 
				 */
				if (st != null) {		
					logger.debug("selectionTYPE: " + st.toString());
					selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, st.toString());	
				}//end if
			
			
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
							editDistMatcher.addControlParameter(selectionTYPE);
						
						} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
							nGramMatcher = new NGramMatcherServiceImpl(3); 
							nGramMatcher.addControlParameter(selectionTYPE);					
						}					
					}//end for				
				}//end if	
				
				/***
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
																	
						//For Matcher: LEVENSHTEIN [KDE_WITH_SUPPORT]
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
						
						//For Matcher: NGRAM [KDE_WITH_SUPPORT]
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
												
				/* Add the matchers to the matcher list */
				matcherList.add(editDistMatcher);
				matcherList.add(nGramMatcher);	
				
				//Call schemaService and run the matcher phase of the Dataspace, not necessary to add control parameters
				Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
				List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
									
				
				//Accumulate syntactic evidences from the matchers
				SemanticMatrix accumSynEvidenceMatrix = schemaService.accumulateSyntacticEvidenceBayes(testSchema1, testSchema2,
																												simCubeOfMatchers, controlParameters);

				/***
				 * STEP 2: Return the matrix that considers only syntactic evidences with Bayes
				 */
				return accumSynEvidenceMatrix;				
				
			}//end if		
		} catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 	exc.printStackTrace();
		} catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		  	ioexc.printStackTrace();
		}//end catch
		
		return null;
	}//end runBayesFromConfigFiles()
		
	/***
	 * This method is responsible for running the Bayesian approach that assimilates both syntactic and
	 * semantic evidence. The method is using a configuration file to setup the approach and then runs it.
	 * Then the approach is responsible to calculate the Precision/Recall for the top k matches that are
	 * selected.
	 * 
	 * Note: My question here is if I will calculate the Precision/Recall only on the cells that have both
	 * syntactic and semantic evidence OR whether I am going to calculate P/R on all the matches? I need 
	 * to run it once and then observe. Answer: From the meeting on the 19/11 I will need to run this only on 
	 * the instances that have semantic evidence because I would like to eliminate the dampening effect.
	 * 
	 * 
	 * @param List<MatcherInfo> simCubeOfMatchers - is a similarity cube that I will need later to retrieve the 
	 * 												matches to calculate Precision/Recall.
	 * @param filePath - the location of the configuration file that setups the approach. Which matchers, which 
	 * 					 semantic evidences to assimilate etc.
	 * 
	 */
	public void runBayesFromConfigFilesTopKrecursive(String confFilePath, Schema testSchema1, Schema testSchema2,
													 String alignmentPropLoc,
													 SDBStoreServiceImpl metaDataSDBStore, TDBStoreServiceImpl tdbStore,
													 String DATA_ANALYSIS, String EVID_CLASSES, String EVID_PROPS, String ENDPOINT_DATA,
													 Map<ControlParameterType, ControlParameter> serviceParameters) {
		
		logger.debug("in runBayesFromConfigFilesTopKrecursive");
		//Create a cloner so we can keep a deep copy of the Syntactic Similarity matrix before applying any semantic evidence
		Cloner cloner = null;
		
		List<MatcherService> matcherList = new ArrayList<MatcherService>();
		
		try {
			
			if ((testSchema1 != null) && (testSchema2 != null)) {
				logger.debug("Running conf file: " + confFilePath);
				
				//Read the actual configuration file run.conf and setup the approach
				InputStream confProps = new FileInputStream(confFilePath);
				Properties runConfProps = new java.util.Properties();
				runConfProps.load(confProps);
				
				/****
				 ** Step 1: Prepare the two StringBasedMatchers as specified in the configuration files
				 ***/
				logger.debug(">> Preparing StringBasedMatchers..");			
			
				//Get some configuration parameters for the matchers
				ControlParameter selectionTYPE = null;
				SelectionType st = SelectionType.fromValue(runConfProps.getProperty("matcher_selection_type"));			
			
				if (st != null) {		
					logger.debug("selectionTYPE: " + st.toString());
					selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, st.toString());	
				}//end if
						
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
							editDistMatcher.addControlParameter(selectionTYPE);
						
						} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
							nGramMatcher = new NGramMatcherServiceImpl(3); 
							nGramMatcher.addControlParameter(selectionTYPE);					
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
												
				/* Add the matchers to the matcher list */
				matcherList.add(editDistMatcher);
				matcherList.add(nGramMatcher);	
				
				//Call schemaService and run the matcher phase of the Dataspace, not necessary to add control parameters
				//Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
										
				/** [S.O.S] SIMILARITY CUBE FROM THE MATCHERS, use it later to select which cells to take into account for the calculation of
				Precision & Recall for the COMA case **/
				List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(testSchema1, testSchema2, matcherList, null);
				
				/* Set Aggregation Type to be used to aggregate the results of a combined matcher that has child matchers*/
				ControlParameter aggregationTYPE = null;
				AggregationType at = AggregationType.fromValue(runConfProps.getProperty("matcher_aggregation_type"));
				
				if (at != null) {
					logger.debug("aggregationTYPE: " + at.toString());					
					if (at.equals(AggregationType.SIMMIN)) {
						aggregationTYPE = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE, at.toString());
					} else if (at.equals(AggregationType.SIMMAX)) {
						aggregationTYPE = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE, at.toString());
					} else if (at.equals(AggregationType.SIMAVERAGE)) {
						aggregationTYPE = new ControlParameter(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE, at.toString());
					}
				}//end if	

				//Setup the aggregation type as a control parameter
				serviceParameters.put(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE, aggregationTYPE);
			
				
				/***
				 * STEP 2.2: Proceed with the Baeysian assimilation of Syntactic evidences.
				 * 			 Use the simCubeOfMatchers from above
				 */				
				//Accumulate syntactic evidences from the matchers
				SemanticMatrix accumSynEvidenceMatrix = schemaService.accumulateSyntacticEvidenceBayes(testSchema1, testSchema2,
						   																						simCubeOfMatchers, null);
				
			/***
			 * STEP 3: Collect Semantic Evidence from RDF vocabularies (RDFS/OWL)
			 */
				String schema_enrich = runConfProps.getProperty("schema_enrich").trim();
				if (Boolean.parseBoolean(schema_enrich)) {
					 //because I have dereferenced the data, do not run the enrichment process again.
					 //if I would like to run the enrichment process again I should setup it in the conf file.
					schemaService.schemaEnrichment(testSchema1, testSchema2);
				}//end if
				
				/***
				 * Create a list that specifies which meta-data to collect, this is called the Semantic Queue List,
				 * the list will then be used to organised meta-data and derive the semCubeOfEvidences
				 */
				List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
				
				HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(metaDataSDBStore);
				//Add control parameters to the hierarchy semantic matrix
				ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
				hierarchySemMatrix.addControlParameter(doConflictRes);
				
				//3.1: HierarchySemanticMatrix - EquivalenceSemanticMatrix
				EquivalenceSemanticMatrix equiSemMatrix = new EquivalenceSemanticMatrix(metaDataSDBStore);
				ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
				equiSemMatrix.addControlParameter(useReasoner);
				hierarchySemMatrix.attachSemMatrix(equiSemMatrix);
				
				//3.2: HierarchySemanticMatrix - SubsumptionSemanticMatrix
				SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(metaDataSDBStore);
				subSemMatrix.addControlParameter(useReasoner);
				hierarchySemMatrix.attachSemMatrix(subSemMatrix);
				
				//3.3: HierarchySemanticMatrix - ShareSuperClassSemMatrix
				ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(metaDataSDBStore);
				superSemMatrix.addControlParameter(useReasoner);
				hierarchySemMatrix.attachSemMatrix(superSemMatrix);
				
				//Add hierarchySemMatrix to the queue of semantic matrices to be created
				semanticQueue.add(hierarchySemMatrix);
				
				//3.4: Other semantic matrices - NameSpaceSemMatrix
				NameSpaceSemMatrix nsSemMatrix = new NameSpaceSemMatrix();
				semanticQueue.add(nsSemMatrix);
				
				//3.5: Specific to properties (Domain / Range) - Should be combined together
				DomainSemanticMatrix domainSemMatrix = new DomainSemanticMatrix(metaDataSDBStore);
				domainSemMatrix.addControlParameter(useReasoner);
				semanticQueue.add(domainSemMatrix);
				
				RangeSemanticMatrix rangeSemMatrix = new RangeSemanticMatrix(metaDataSDBStore);
				rangeSemMatrix.addControlParameter(useReasoner);
				semanticQueue.add(rangeSemMatrix);
				
				//Call method responsible of organising meta-data into semantic matrices
				List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);	
				
			/***
			 * STEP 4: Construct likelihoods for semantic evidences.
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
			 * STEP 5: Prepare for the calculation of Precision/Recall vs. top-k 
			 */	
				/* Get from the configuration file the parameter for k */
				String sk = runConfProps.getProperty("size_of_k").trim();
				int size_of_k = 5;
				if ((sk != null) && !sk.equals("")) {
					size_of_k = Integer.parseInt(sk);
				}//end if				
				
				/* Collecting parameters for the selection of cells (instances) that will participate in the calculation of the 
				 * Precision/Recall vs. top-k */
				String cells_selection = runConfProps.getProperty("cells_selection").trim();
				if (cells_selection.equals("ALL_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ALL_SYN_SEM_CELLS, true);
					serviceParameters.put(param.getName(), param);
				} else if (cells_selection.equals("ONLY_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ONLY_SYN_SEM_CELLS, true);
					serviceParameters.put(param.getName(), param);
				}//end else
				
				/* Collecting parameters the parameter for the dampening effect policy. This only applies
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
				serviceParameters.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicy);
								
				/***
				 * CASE 1: All cells (instances) are selected to contribute in the calculation of the Precision/Recall. 
				 * 		   If this is the case we just run this calculation once.
				 */
				//Hold the matches from only the syntactic evidence aggregation either from COMA or Bayes
				List<Matching> matchesFromSynEvidence = null;
				
				//Setup the SELECT_ALL strategy for COMA / Bayesian approach
				serviceParameters.put(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, selectionTYPE);
				
				/***
				 * SETUP BENCHMARK SERVICE 
				 **/				
				//Which approach to use to aggregate syntactic only : (a) Bayesian or (b) COMA AVG strategy 
				BenchmarkType benchmarkType = null;
				if ((serviceParameters != null) && (serviceParameters.containsKey(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION))) {
					ControlParameter controlParam = serviceParameters.get(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION);
					benchmarkType = controlParam.getBenchmarkType();							
				}//end if	
				
				//Choose what to plot: (a) either to plot P/R vs. top-k or (b) plot difference_d
				BenchmarkType plotBenchmarkType = null;
				if ((serviceParameters != null) && (serviceParameters.containsKey(ControlParameterType.PLOT_TYPE))) {
					ControlParameter controlParam = serviceParameters.get(ControlParameterType.PLOT_TYPE);
					plotBenchmarkType = controlParam.getBenchmarkType();
				}//end if				
				
				//Diversion1: Involve all the match instances 
				if ((serviceParameters != null) && (serviceParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
					boolean allSynSemCells = serviceParameters.get(ControlParameterType.ALL_SYN_SEM_CELLS).isBool();
					logger.info("allSynSemCells: " + allSynSemCells);					
					if (allSynSemCells) {
						 //CASE: User has chosen to assimilate Syntactic evidence with Bayes
						 if ( benchmarkType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY) ) {
							logger.info("BAEYSIAN_APPROACH_SYN_ONLY");
							 
							/** Create a new Bayesian Matcher **/
							BayesianMatcherServiceImpl bayesianMatcherSyn = new BayesianMatcherServiceImpl(accumSynEvidenceMatrix);
							
							/* Select all the matches, even those with dof equal to 0. If I need to use a selection strategy, setup this
							* matcher with the same way as standard matchers */
							ControlParameter selectionTYPEforBayes = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
										SelectionType.SELECT_ALL.toString());
							ControlParameter rankingTYPEforBayes = new ControlParameter(ControlParameterType.MATCH_RANKING,
										ControlParameterType.RANKING_DESC.toString());				
							bayesianMatcherSyn.addControlParameter(selectionTYPEforBayes);
							bayesianMatcherSyn.addControlParameter(rankingTYPEforBayes);
							
							/* Use the Bayesian service to produce the matches */
							matchesFromSynEvidence = bayesianMatcherSyn.produceMatches(testSchema1, testSchema2);
							
							/***********************************************************************
							 *          SETUP BENCHMARK SERVICE 
							 * 
							 * CHOISES: (a) either to plot P/R vs. top-k or (b) plot difference_d
							 *  
							 * Note: Note that difference_d can only plotted when all instances are selected
							 */							
							//Create a new MatchingServiceBenchmarkImpl service to measure the performance
							MatchingServiceBenchmarkImpl benchmarkServiceBayesSynOnly = new MatchingServiceBenchmarkImpl
																(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY, tdbStore, alignmentPropLoc);

							//If we choose to PLOT_TOP_K_PRECISION_RECALL
							if (plotBenchmarkType.equals(PlotType.PLOT_TOP_K_PRECISION_RECALL)) {
								logger.info("PLOT TYPE IS: PLOT_TOP_K_PRECISION_RECALL");
								benchmarkServiceBayesSynOnly.resetAll(); 
								//Write results to a csv file
								benchmarkServiceBayesSynOnly.runTopKMatchesAggregationExperiment(matchesFromSynEvidence, size_of_k, null); //Run the experiment [matches, top k]
							} else if (plotBenchmarkType.equals(PlotType.PLOT_DIFFERENCE_D)) {	
								logger.info("PLOT TYPE IS: PLOT_DIFFERENCE_D");
								benchmarkServiceBayesSynOnly.resetAll(); 
								//Identify the position for each match in the list of matches
								benchmarkServiceBayesSynOnly.runDifferenceDExperiment(matchesFromSynEvidence);
							}//end if						 
						 } else if ( benchmarkType.equals(BenchmarkType.COMA_APPROACH) )  {
																
							 logger.info("Calculating Precision/Recall for the COMA aggregated similarity Matrix");

							 /* Call schemaService to produce and make matches persistent, rank is *not* needed, it is 
						   	    sorted out by the SPARQL query */
							 ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
							 														ControlParameterType.RANKING_DESC.toString());
							 serviceParameters.put(rankingType.getName(), rankingType);		
							 matchesFromSynEvidence = schemaService.produceAndSaveMatchings(testSchema1, testSchema2,
																						simCubeOfMatchers, serviceParameters);
						
							/***********************************************************************
							 *          SETUP BENCHMARK SERVICE 
							 * 
							 * CHOISES: (a) either to plot P/R vs. top-k or (b) plot difference_d
							 *  
							 * Note: Note that difference_d can only plotted when all instances are selected
							 */							 
							 /*Prepare the system for training by loading the property file: ./src/test/resources/training/benchmark/exp1.benchmark*/
							 MatchingServiceBenchmarkImpl benchmarkServiceCOMA = new MatchingServiceBenchmarkImpl
									 							(BenchmarkType.COMA_APPROACH, tdbStore, alignmentPropLoc);
							 
							//If we choose to PLOT_TOP_K_PRECISION_RECALL
							if (plotBenchmarkType.equals(PlotType.PLOT_TOP_K_PRECISION_RECALL)) {
								logger.info("PLOT TYPE IS: PLOT_TOP_K_PRECISION_RECALL");							 
								benchmarkServiceCOMA.resetAll(); //Reset the named graph necessary for the experiment
								benchmarkServiceCOMA.runTopKMatchesAggregationExperiment(matchesFromSynEvidence, size_of_k, null); //Run the experiment [matches, top k]
						 	} else if (plotBenchmarkType.equals(PlotType.PLOT_DIFFERENCE_D)) {
						 		benchmarkServiceCOMA.resetAll(); 
								//Identify the position for each match in the list of matches
						 		benchmarkServiceCOMA.runDifferenceDExperiment(matchesFromSynEvidence);
						 	}//end if
						}//end 
					}//end inner if					
				}//end if
								
			/***
			 * STEP 6: Get the list of semantic evidences to assimilate. Call the combinatorics method
			 */
				List<String> evidencesList = Arrays.asList(runConfProps.getProperty("evidences").split(","));
				logger.error("evidencesList: " + evidencesList);
				ArrayList<BooleanVariables> inputList = getBooleanVariablesList(evidencesList);
				logger.error("evidencesList: " + inputList);
				//send it to the garbage collector
				evidencesList = null;				
				
				//Before calling the combinatoric method to find the combinations, create a Cloner object that will be
				//used later on to create a deepCopy of the object				
				cloner = new Cloner();
				cloner.dontClone(Logger.class); //do not clone this class
				
				//This is the Aggregated similarity Matrix produced by the COMA approach.
				float[][] comaAggrSimMatrix = null;
				if ( benchmarkType.equals(BenchmarkType.COMA_APPROACH) ) {
					comaAggrSimMatrix = MatcherServiceImpl.aggregate(simCubeOfMatchers, serviceParameters);
				}//end if
				
				
				/***
				 * Run the PLOT_DIFFERENCE_D experiment
				 * For the diffD experiment the control parameters need to defined explicitly here. 
				 * We need to have Only_syn_sems and dampening effect policy COMBINATION_OF_EVIDENCE
				 */
				if ( (inputList != null) && (plotBenchmarkType.equals(PlotType.PLOT_DIFFERENCE_D)) ) {
					logger.info("Plot mode is: PLOT_DIFFERENCE_D");
					logger.info("call findCombinationsRecursiveDiffD()");
					Map<ControlParameterType, ControlParameter> diffControlParams
																	= new HashMap<ControlParameterType, ControlParameter>();
									
					//Need to have this control parameter so that it can discover the individual matches that have the semantic evidence
					ControlParameter discoverIndividuals = new ControlParameter(ControlParameterType.ONLY_SYN_SEM_CELLS, true);
					diffControlParams.put(discoverIndividuals.getName(), discoverIndividuals);
										
					//Consider the dampening effect as specified in the conf file, otherwise use the code below to configure it manually
					diffControlParams.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicy);
					
					//otherwise Configure the dampening effect manually
					//Set the dampening effect policy to combination evidence otherwise for this experiment it does not make sense
		            //ControlParameter dampeningEffectPolicyForDiffD = new ControlParameter(ControlParameterType.DAMPENING_EFFECT_POLICY,
		            	//																	DampeningEffectPolicy.COMBINATION_OF_EVIDENCE);
		            //diffControlParams.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicyForDiffD);				
					
					
					//From the individual matches that have some semantic evidence I will farther filter them by selecting 
					//the ones specified each time by the combination of the recursive method.			
					
					//Do recursion over different combinations of evidence
					for (int k=1; k <= inputList.size(); ++k) {
						BooleanVariables[] branch = new BooleanVariables[k];
						this.findCombinationsRecursiveDiffD(inputList, k, 0, branch, 0, cloner, accumSynEvidenceMatrix,
															testSchema1, testSchema2, semanticCube, pmfList, diffControlParams,
															tdbStore, alignmentPropLoc);
					}//end for					
				} else if ( (inputList != null) && (plotBenchmarkType.equals(PlotType.PLOT_TOP_K_PRECISION_RECALL))) {				
					logger.info("Plot mode is: PLOT_TOP_K_PRECISION_RECALL");
					logger.info("call findCombinationsRecursiveTopK()");					
					
					//Find all the combinations for the power set: 0 <= k < SIZE(boolean variables).
					for (int k=1; k <= inputList.size(); ++k) {
						BooleanVariables[] branch = new BooleanVariables[k];			
						this.findCombinationsRecursiveTopK(inputList, k, 0, branch, 0,
													accumSynEvidenceMatrix, matchesFromSynEvidence,
							                        testSchema1, testSchema2, semanticCube,
							                        pmfList, comaAggrSimMatrix, cloner,
							                        serviceParameters, tdbStore, alignmentPropLoc,
							                        size_of_k);	
			    	}//end if	
				}//end else				
			}//end if			
		} catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 	exc.printStackTrace();
		} catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		  	ioexc.printStackTrace();
		}//end catch		
	}//end runBayesFromConfigFilesTopKrecursive()	
			
	/***
	 * Plot mode is: PLOT_TOP_K_PRECISION_RECALL
	 * 
	 * Recursive method: This method will help us explore the space by applying the Bayesian assimilation of evidence
	 * approach using different combinations of Semantic evidence. This method is responsible for generating the
	 * combinations from an input list of BooleanVariables specified in a configuration file.
	 * 
	 * Note: This method is for the experiment with Precision/Recall vs. top-k
	 * 
	 * @param comaAggrSimMatrix - an array version of the COMA aggregated similarity matrix
	 * 
	 */
	private void findCombinationsRecursiveTopK(ArrayList<BooleanVariables> arr, int k, int startId, BooleanVariables[] branch, int numElem,
											SemanticMatrix synBayesMatrix, List<Matching> matchesFromSynEvidence, Schema testSchema1, Schema testSchema2, 
											List<SemanticMatrix> semanticCube, Map<BooleanVariables, ProbabilityMassFunction> pmfList,
											float[][] comaAggrSimMatrix, Cloner cloner, Map<ControlParameterType, ControlParameter> controlParameters,
											TDBStoreServiceImpl tdbStore, String alignmentPropLoc, int size_of_k) {
	
		if (numElem == k) {
			logger.debug(" >> START - Precision/Recall vs. top-k Experiment << ");
			logger.debug("Assimilate the following evidences: " + (Arrays.toString(branch)));
		
			//Create a deep copy of the synBayesMatrix using the Cloner object. Because the semantic evidences will change
			//and therefore this needs to be constant.
			SemanticMatrix accumSynEvidMatrixClone = cloner.deepClone(synBayesMatrix);
			
			//Assimilate only the evidences specified by the combination
			Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(Arrays.asList(branch));
			logger.debug("Assimilate the following evidences: " + evidencesToAccumulate);
			
			//Assimilate *semantic evidences* and store the result in a SemanticMatrix
			SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidMatrixClone, testSchema1, testSchema2,
																			semanticCube, pmfList, evidencesToAccumulate, controlParameters);
			
			/***
			 * STEP 6.1: hold matrix that considers both syntactic and semantic evidences with Bayes
			 * This is the same matrix but with updated posteriors in the presence of semantic evidence
			 */
			//float[][] matrixSynSem = accumSemEvidenceMatrix.getSemMatrixAsArray();	
			
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
				
				//Prepei na kamw revise to case on ALL_SYN_SEM_CELLS epidi kati den paei kala
				
				logger.info("");
				logger.info("Mode: " + ControlParameterType.ALL_SYN_SEM_CELLS.toString());
				
				/** Create a new Baeysian Matcher **/ 
				BayesianMatcherServiceImpl bayesianMatcher = new BayesianMatcherServiceImpl(accumSemEvidenceMatrix);
				/* Select all the matches, even those with dof equal to 0. If I need to use a selection strategy, setup this
				 * matcher with the same way as standard matchers */
				ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
						SelectionType.SELECT_ALL.toString());
				ControlParameter rankingType = new ControlParameter(ControlParameterType.MATCH_RANKING,
						ControlParameterType.RANKING_DESC.toString());				
				bayesianMatcher.addControlParameter(selectionTYPE);
				bayesianMatcher.addControlParameter(rankingType);
				
				/* Having a separate service for the bayesianMatcher I am then able to perform selection, aggregation, produce matches etc.
				   as having an ordinary Matcher service. */
				List<Matching> matchesFromBayes = bayesianMatcher.produceMatches(testSchema1, testSchema2);
										
				//Create a new MatchingServiceBenchmarkImpl service to measure the performance
				MatchingServiceBenchmarkImpl benchmarkServiceBayes = new MatchingServiceBenchmarkImpl(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM, tdbStore, alignmentPropLoc);
				benchmarkServiceBayes.resetClassifications(); //Reset the named graph necessary for the experiment
				benchmarkServiceBayes.runTopKMatchesAggregationExperiment(matchesFromBayes, size_of_k, evidencesToAccumulate); //Run the experiment [matches, top k]		
				
			} else if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ONLY_SYN_SEM_CELLS))) {
				logger.info("Mode: ONLY_SYN_SEM_CELLS");

				/***
				 * In this case we calculate Precision/Recall vs. top-k only with the cells (instances) that have both Syntactic
				 * and Semantic evidence.
				 */
				
				/* >> Step 1: Find the cells from the Semantic Matrices that has only assimilated the syntactic evidence and
				     calculate the Precision/Recall. Ranking is *not* needed because it is sorted by the SPARQL query. */		
				BenchmarkType benchmarkType = null;
				if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION))) {
					ControlParameter controlParam = controlParameters.get(ControlParameterType.SYNTACTIC_EVIDENCE_ASSIMILATION);
					benchmarkType = controlParam.getBenchmarkType();							
				}//end if				
				
				logger.info("benchmarkType: "+ benchmarkType);
				
				if (benchmarkType.equals(BenchmarkType.COMA_APPROACH)) {
					List<Matching> matchesFromCOMA = schemaService.produceMatchesForSpecificCells(testSchema1, testSchema2,
																					  comaAggrSimMatrix, controlParameters,
																					  accumSemEvidenceMatrix.getIndexesSet(), null);

					MatchingServiceBenchmarkImpl benchmarkServiceCOMA = new MatchingServiceBenchmarkImpl(BenchmarkType.COMA_APPROACH,
																									 tdbStore, alignmentPropLoc);
					benchmarkServiceCOMA.resetClassifications();
					benchmarkServiceCOMA.runTopKMatchesAggregationExperiment(matchesFromCOMA, size_of_k, evidencesToAccumulate); //Run the experiment [matches, top k]
				} else if (benchmarkType.equals(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY)) {
					
					logger.info("BAEYSIAN_APPROACH_SYN_ONLY eimai mesa");
					
					//Create a Bayesian matcher only for syntactic evidence
					BayesianMatcherServiceImpl bayesianMatcherSyn = new BayesianMatcherServiceImpl(synBayesMatrix);
					//Get a list of selected match individuals					
					List<Matching> specificMatchesSyn = bayesianMatcherSyn.produceMatchesForSpecificCells(testSchema1, testSchema2,
																								accumSemEvidenceMatrix.getIndexesSet());
					//Create a benchmark service to calculate P/R vs. top-k 
					MatchingServiceBenchmarkImpl benchmarkServiceBayesSyn = new MatchingServiceBenchmarkImpl(BenchmarkType.BAEYSIAN_APPROACH_SYN_ONLY, tdbStore, alignmentPropLoc);
					benchmarkServiceBayesSyn.resetClassifications();
					benchmarkServiceBayesSyn.runTopKMatchesAggregationExperiment(specificMatchesSyn, size_of_k, evidencesToAccumulate);
				}//end if
				
				/* >> Step 2: Run the Bayesian approach for the selected cells */
				BayesianMatcherServiceImpl bayesianMatcherSynSem = new BayesianMatcherServiceImpl(accumSemEvidenceMatrix);
				
				List<Matching> specificMatchesSynSem = bayesianMatcherSynSem.produceMatchesForSpecificCells(testSchema1, testSchema2,
																										accumSemEvidenceMatrix.getIndexesSet());
			
				MatchingServiceBenchmarkImpl benchmarkServiceBayesSynSem = new MatchingServiceBenchmarkImpl(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM, tdbStore, alignmentPropLoc);
				benchmarkServiceBayesSynSem.resetClassifications(); //Reset the named graph necessary for the experiment
				benchmarkServiceBayesSynSem.runTopKMatchesAggregationExperiment(specificMatchesSynSem, size_of_k, evidencesToAccumulate); //Run the experiment [matches, top k]		
			}//end if	
			
			logger.debug(" >> END << ");		     
	        return;
		}//end if
		
		 for (int i = startId; i < arr.size(); ++i) {
		       branch[numElem++] = arr.get(i);
		       findCombinationsRecursiveTopK(arr, k, ++startId, branch, numElem,
		    		   						synBayesMatrix, matchesFromSynEvidence,
		       								testSchema1, testSchema2, semanticCube, pmfList,
		       								comaAggrSimMatrix, cloner, controlParameters, tdbStore,
		       								alignmentPropLoc, size_of_k);
		       --numElem;
		}//end for		
	}//end findCombinationsRecursiveTopK()
		
	/***
	 * Plot mode is: PLOT_DIFFERENCE_D
	 * 
	 * Recursive method: This method is responsible for:
	 *   (1) assimilate a combination of semantic evidence, given a list of semantic evidence.
	 *   (2)
	 */
	private void findCombinationsRecursiveDiffD(ArrayList<BooleanVariables> arr, int k, int startId, BooleanVariables[] branch, int numElem,
												Cloner cloner, SemanticMatrix synBayesMatrix, Schema testSchema1, Schema testSchema2,
												List<SemanticMatrix> semanticCube, Map<BooleanVariables, ProbabilityMassFunction> pmfList,
												Map<ControlParameterType, ControlParameter> controlParameters, TDBStoreServiceImpl tdbStore,
												String alignmentPropLoc) {
		if (numElem == k) {
			logger.debug(" >> START - Difference D Experiment << ");
			logger.debug("Assimilate the following evidences: " + (Arrays.toString(branch)));
			
			//Create a deep copy of the syntactic similarity Matrix
			SemanticMatrix accumSynEvidMatrixClone = cloner.deepClone(synBayesMatrix);
			
			//Assimilate only the evidences specified by the combination
			Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(Arrays.asList(branch));
			logger.debug("Assimilate the following evidences: " + evidencesToAccumulate);
			
			//Assimilate *semantic evidences* and store the result in a SemanticMatrix
			SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidMatrixClone, testSchema1, testSchema2,
					semanticCube, pmfList, evidencesToAccumulate, controlParameters);
			
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
				
				//TODO: This is not implemeted, see findCombinationsRecursiveTopK() for an example
				logger.info("Mode: ALL_SYN_SEM_CELLS");
				logger.info("NOT IMPLEMENTED");
				
			} else if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ONLY_SYN_SEM_CELLS))) {
				logger.info("Mode: ONLY_SYN_SEM_CELLS");
			
				/***
				 * Create a new BayesianMatcherServiceImpl matcher
				 */
				BayesianMatcherServiceImpl bayesianMatcher = new BayesianMatcherServiceImpl(accumSemEvidenceMatrix);
		
				//Produce the matches to classify to T/F according to their presence in GT
				ControlParameter selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
																						SelectionType.SELECT_ALL.toString());
				bayesianMatcher.addControlParameter(selectionTYPE);
				List<Matching> allMatchesFromBayes = bayesianMatcher.produceAndSaveMatches(testSchema1, testSchema2);			
			
				//Create a new MatchingServiceBenchmarkImpl service to plot data for difference D
				MatchingServiceBenchmarkImpl benchmarkServiceBayesSynSem = new MatchingServiceBenchmarkImpl
													(BenchmarkType.BAEYSIAN_APPROACH_SYN_SEM, tdbStore, alignmentPropLoc);			
				//For each semantic evidence assimilated we reset the classification
				benchmarkServiceBayesSynSem.resetClassifications();
				//Identify and add the position for each match in the named graph
				benchmarkServiceBayesSynSem.runDifferenceDExperiment(allMatchesFromBayes);
			
				/***
				 * Operation: Plot the data for creating the Difference D Plots
				 */
				//Step1: Get the Matching object for the cells that have assimilate some evidence
				List<Matching> specificMatchesFromBayes = bayesianMatcher.produceMatchesForSpecificCells(testSchema1, testSchema2,
																							accumSemEvidenceMatrix.getIndexesSet());
				String fileName = benchmarkServiceBayesSynSem.generateDataForDiffDExperiment(specificMatchesFromBayes, evidencesToAccumulate);
			
				//Generate GNUplot (difference in syntactic position vs semantically enhanced position)
				benchmarkServiceBayesSynSem.plotDiffDexperiment(fileName, evidencesToAccumulate, "png");
						
				//Generate GNUplot (difference in syntactic dob vs semantically enhances dob)
				benchmarkServiceBayesSynSem.plotDiffDegreeOfBeliefexperiment(fileName, evidencesToAccumulate, "png");			
			}//end if
			
			logger.debug(" >> END << ");		     
	        return;
		}//end if
		
		for (int i = startId; i < arr.size(); ++i) {
		     branch[numElem++] = arr.get(i);
		     findCombinationsRecursiveDiffD(arr, k, ++startId, branch, numElem,	cloner,	synBayesMatrix,
		    		 						testSchema1, testSchema2, semanticCube, pmfList, controlParameters,
		    		 						tdbStore, alignmentPropLoc);
		       
		     --numElem;
		}//end for	
	}//end findCombinationsRecursiveDiffD()
	
	/***
	 * Experiment: This is the Numerical Evaluation Experiment
	 * 
	 * This method is responsible for running the ENTIRE approach based on the configurations files 
	 * specified by the user in various runXNumE.conf files from:
	 * 
	 * ./src/test/resources/runConf/expNumE.properties
	 *  
	 * This method applies the approach as described in the step in the RDFSchemaServiceMatchingBayesTest class
	 *  - at the beginning it performs syntactic similarity matching by considering the results as degrees of 
	 *    belief in the equivalence / non-equivalence of two constructs.
	 *  - then it uses semantic evidences to update the resulting posteriors by assimilating semantic evidence if
	 *  exist.
	 * 
	 * The error is calculated considering all the cells not just the cells that have both syn and sem evidences
	 *  
	 * @param filePath - the file path of the configuration file
	 * 
	 */
	public void runBayesFromConfigFiles(String filePath, Schema testSchema1, Schema testSchema2,
											SDBStoreServiceImpl metaDataSDBStore, TDBStoreServiceImpl tdbStore,
											String DATA_ANALYSIS, String EVID_CLASSES, String EVID_PROPS, String ENDPOINT_DATA) {
		
		logger.debug("in runBayesFromConfigFiles");
		//Create a cloner so we can keep a deep copy of the Syntactic Similarity matrix before applying any semantic evidence
		Cloner cloner = null;
		
		List<MatcherService> matcherList = new ArrayList<MatcherService>();
		
		try {			
			if ((testSchema1 != null) && (testSchema2 != null)) {
				logger.debug("Running: " + filePath);	
				
				//Read the actual configuration file run.conf
				InputStream confProps = new FileInputStream(filePath);
				Properties runConfProps = new java.util.Properties();
				runConfProps.load(confProps);
						
				/***
				 * As of RDFSchemaServiceMatchingBayesTest
				 * 
				 * Step 2: Prepare the two StringBasedMatchers as specified in the configuration files
				 */
				logger.debug(">> Preparing StringBasedMatchers..");			
			
				//Get some configuration parameters for the matchers
				ControlParameter selectionTYPE = null;
				SelectionType st = SelectionType.fromValue(runConfProps.getProperty("matcher_selection_type"));			
			
				if (st != null) {		
					logger.debug("selectionTYPE: " + st.toString());
					selectionTYPE = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE, st.toString());	
				}//end if
			
			
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
							editDistMatcher.addControlParameter(selectionTYPE);
						
						} else if ((mt != null) && mt.equals(MatcherType.NGRAM)) {
							nGramMatcher = new NGramMatcherServiceImpl(3); 
							nGramMatcher.addControlParameter(selectionTYPE);					
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
												
				/* Add the matchers to the matcher list */
				matcherList.add(editDistMatcher);
				matcherList.add(nGramMatcher);	
				
				//Call schemaService and run the matcher phase of the Dataspace, not necessary to add control parameters
				Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
				List<MatcherInfo> simCubeOfMatchers = schemaService.runMatch(testSchema1, testSchema2, matcherList, controlParameters);
				
				//Accumulate syntactic evidences from the matchers
				SemanticMatrix accumSynEvidenceMatrix = schemaService.accumulateSyntacticEvidenceBayes(testSchema1, testSchema2,
						   																						simCubeOfMatchers, null);

				/***
				 * STEP 2.1: Hold matrix that considers only syntactic evidences with Bayes
				 */
				float[][] matrixSynOnly = accumSynEvidenceMatrix.getSemMatrixAsArray();	
				
			/***
			 * STEP 3: Collect semantic evidence from RDF vocabularies (RDFS/OWL)
			 */
				String schema_enrich = runConfProps.getProperty("schema_enrich").trim();
				if (Boolean.parseBoolean(schema_enrich)) {
					 //because I have the data do not run the enrichment process again
					schemaService.schemaEnrichment(testSchema1, testSchema2);
				}//end if
				
				
				/***
				 * Create a list that specifies which meta-data to collect, this is called the semantic queue list,
				 * the list will then be used to organised meta-data and derive the semCubeOfEvidences
				 */
				List<SemanticMetadataService> semanticQueue = new ArrayList<SemanticMetadataService>();
				
				HierarchySemanticMatrix hierarchySemMatrix = new HierarchySemanticMatrix(metaDataSDBStore);
				//Add control parameters to the hierarchy semantic matrix
				ControlParameter doConflictRes = new ControlParameter(ControlParameterType.DO_CONFLICT_RESOLUTION, false);		
				hierarchySemMatrix.addControlParameter(doConflictRes);
				
				//3.1: HierarchySemanticMatrix - EquivalenceSemanticMatrix
				EquivalenceSemanticMatrix equiSemMatrix = new EquivalenceSemanticMatrix(metaDataSDBStore);
				ControlParameter useReasoner = new ControlParameter(ControlParameterType.USE_REASONER, false); //add control parameters
				equiSemMatrix.addControlParameter(useReasoner);
				hierarchySemMatrix.attachSemMatrix(equiSemMatrix);
				
				//3.2: HierarchySemanticMatrix - SubsumptionSemanticMatrix
				SubsumptionSemanticMatrix subSemMatrix = new SubsumptionSemanticMatrix(metaDataSDBStore);
				subSemMatrix.addControlParameter(useReasoner);
				hierarchySemMatrix.attachSemMatrix(subSemMatrix);
				
				//3.3: HierarchySemanticMatrix - ShareSuperClassSemMatrix
				ShareSuperClassSemMatrix superSemMatrix = new ShareSuperClassSemMatrix(metaDataSDBStore);
				superSemMatrix.addControlParameter(useReasoner);
				hierarchySemMatrix.attachSemMatrix(superSemMatrix);
				
				//Add hierarchySemMatrix to the queue of semantic matrices to be created
				semanticQueue.add(hierarchySemMatrix);
				
				//3.4: Other semantic matrices - NameSpaceSemMatrix
				NameSpaceSemMatrix nsSemMatrix = new NameSpaceSemMatrix();
				semanticQueue.add(nsSemMatrix);
				
				//3.5: Specific to properties (Domain / Range) - Should be combined together
				DomainSemanticMatrix domainSemMatrix = new DomainSemanticMatrix(metaDataSDBStore);
				domainSemMatrix.addControlParameter(useReasoner);
				semanticQueue.add(domainSemMatrix);
				
				RangeSemanticMatrix rangeSemMatrix = new RangeSemanticMatrix(metaDataSDBStore);
				rangeSemMatrix.addControlParameter(useReasoner);
				semanticQueue.add(rangeSemMatrix);
				
				//Call method responsible of organising meta-data into semantic matrices
				List<SemanticMatrix> semanticCube = schemaService.organiseMetadata(testSchema1, testSchema2, semanticQueue);	
				
			/***
			 * STEP 4: Construct likelihoods for semantic evidence.
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

				/** 
				 * Get the PMF as contingency tables for both Classes and Properties, using
				 * Laplace Smoothing Method with k = 1 and |classes| = 2
				 * 
				 *  //TODO: need to check whether I need to use as |classes| the size of the dictionary
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
			 * STEP 6: Get expectation matrix
			 */
				//Get the Expectation matrix
				String alignmentExpectation = runConfProps.getProperty("alignment").trim();
				SemanticMatrix gtMatrix = schemaService.generateExpectationMatrix(testSchema1, testSchema2, alignmentExpectation);
				
				//Convert matrices to plain 2D structures (observed = real values)
				float[][] actualMatrix = gtMatrix.getSemMatrixAsArray();
		
				
				//Read which performance measures to use from the conf file
				List<String> measuresArray = Arrays.asList(runConfProps.getProperty("performance_measures").split(","));				
				
				if (measuresArray != null) {
					for (String name : measuresArray) {				
						PerformanceErrorTypes pet = PerformanceErrorTypes.fromValue(name);
						
						if ((pet != null) && pet.equals(PerformanceErrorTypes.MEAN_SQUARED_ERROR)) {
							this.attactAggrErrMeasure( new MeanSquaredError() );	
						} else if ((pet != null) && pet.equals(PerformanceErrorTypes.ROOT_MEAN_SQUARED_ERROR)) {
							this.attactAggrErrMeasure( new RootMeanSquaredError() );
						} else if ((pet != null) && pet.equals(PerformanceErrorTypes.MEAN_ABS_ERROR)) {
							this.attactAggrErrMeasure( new MeanAbsoluteError() );
						} else if ((pet != null) && pet.equals(PerformanceErrorTypes.RELATIVE_SQUARED_ERROR)) {
							this.attactAggrErrMeasure( new RelativeSquaredError() );
						} else if ((pet != null) && pet.equals(PerformanceErrorTypes.ROOT_RELATIVE_SQUARED_ERROR)) {
							this.attactAggrErrMeasure( new RootRelativeSquaredError() );
						} else if ((pet != null) && pet.equals(PerformanceErrorTypes.RELATIVE_ABS_ERROR)) {
							this.attactAggrErrMeasure( new RelativeAbsoluteError() );
						} else if ((pet != null) && pet.equals(PerformanceErrorTypes.CORRELATION_COEFFICIENT)) {
							this.attactAggrErrMeasure( new CorrelationCoefficient() );
						}					
					}//end for
				}//end if
				
				//Send it to the garbage collector
				measuresArray = null;		
				
				//Collecting parameters for the selection of cells (instances) that will participate in the calculation of the errors
				String cells_selection = runConfProps.getProperty("cells_selection").trim();
				if (cells_selection.equals("ALL_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ALL_SYN_SEM_CELLS, true);
					controlParameters.put(param.getName(), param);
				} else if (cells_selection.equals("ONLY_SYN_SEM_CELLS")) {
					ControlParameter param = new ControlParameter(ControlParameterType.ONLY_SYN_SEM_CELLS, true);
					controlParameters.put(param.getName(), param);
				}
					
				//Dampening effect policy for ONLY_SYN_SEM_CELLS
				ControlParameter dampeningEffectPolicy = null;
				DampeningEffectPolicy dep = DampeningEffectPolicy.fromValue(runConfProps.getProperty("dampening_effect_policy"));

				if (dep != null) {
					logger.debug("dampeningEffectPolicy: " + dep.toString());					
					if (dep.equals(DampeningEffectPolicy.SOME_EVIDENCE)) {
						dampeningEffectPolicy = new ControlParameter(ControlParameterType.DAMPENING_EFFECT_POLICY, dep);
						controlParameters.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicy);
					} else if (dep.equals(DampeningEffectPolicy.COMBINATION_OF_EVIDENCE)) {
						dampeningEffectPolicy = new ControlParameter(ControlParameterType.DAMPENING_EFFECT_POLICY, dep);
						controlParameters.put(ControlParameterType.DAMPENING_EFFECT_POLICY, dampeningEffectPolicy);
					} 
				}//end if
								
				
				/***
				 * Add Control Parameters for: Instances selection considered for the calculation of the Error
				 * 
				 * CASE 1: All cells (instances) should contribute in the calculation of the performance measures. In this 
				 * 		   case error1 (D1) is calculated only once. Error 1 is the error between the M_syn with M_expectatio
				 * 		   matrices.
				 */
				Map<PerformanceErrorTypes, Float> error1 = null;
				if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
					boolean allSynSemCells = controlParameters.get(ControlParameterType.ALL_SYN_SEM_CELLS).isBool();
					logger.info("allSynSemCells: " + allSynSemCells);					
					if (allSynSemCells) {
						logger.info("Calculating error1(M_syn, Mexp) matrices: ");
						//calculate error1 here because we would like to calulate it only once
						error1 = this.calculatePerformance(matrixSynOnly, actualMatrix);
					}					
				}//end if
				
				
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
				
			ControlParameter plotPercChange = new ControlParameter(ControlParameterType.PLOT_PERC_CHANGE, true);
			controlParameters.put(plotPercChange.getName(), plotPercChange);
				
				
																
				
			/****************************************************
			 * NOTE: Recursion 
			 * 
			 * STEP 5: is done after step 6
			 * 
			 * Get the list of semantic evidences to accumulate
			 ****************************************************/
			
				List<String> evidencesList = Arrays.asList(runConfProps.getProperty("evidences").split(","));
				logger.error("evidencesList: " + evidencesList);
				ArrayList<BooleanVariables> inputList = getBooleanVariablesList(evidencesList);
				logger.error("evidencesList: " + inputList);
				//send it to the garbage collector
				evidencesList = null;
				
				
				/***
				 * Accumulate evidence for all combinations, from the above list
				 */				
				
				//Before calling the combinatoric method to find the combinations, create a Cloner object that will be
				//used later on to create a deepCopy of the object
				
				cloner = new Cloner();
				cloner.dontClone(Logger.class); //do not clone this class
							
			    //Find all the combinations for the power set: 0 <= k < SIZE(boolean variables).
			    for (int k=1; k <= inputList.size(); ++k) {
					BooleanVariables[] branch = new BooleanVariables[k];			
					this.findCombinationsWithLength(inputList, k, 0, branch, 0, accumSynEvidenceMatrix,
							                        testSchema1, testSchema2, semanticCube, pmfList,
							                        actualMatrix, matrixSynOnly, error1, cloner, controlParameters);	
					
			    }//end for		    
			        
			    
				
				
			}//end if			
		}  catch (FileNotFoundException exc) {
		 	logger.error("train.properties file not found: " + exc);
		 	exc.printStackTrace();
		} catch (IOException ioexc) {
		  	logger.error("train.properties file not found: ", ioexc);
		  	ioexc.printStackTrace();
		}//end catch		
	}//end runBayesFromConfigFiles()	
	
	
	/***
	 * Recursive method: This method will help us explore the space by applying the approach using different
	 * combinations of evidences. This method is responsible for generating the combinations from 
	 * an input list of BooleanVariables specified in a configuration file.
	 * 
	 * Note: This method is for the experiment with the Expectation Matrix
	 * 
	 * @param 
	 */
	private void findCombinationsWithLength(ArrayList<BooleanVariables> arr, int k, int startId, BooleanVariables[] branch, int numElem,
											SemanticMatrix synBayesMatrix, Schema testSchema1, Schema testSchema2, 
											List<SemanticMatrix> semanticCube, Map<BooleanVariables, ProbabilityMassFunction> pmfList,
											float[][] actualMatrix, float[][] matrixSynOnly, Map<PerformanceErrorTypes, Float> error1,
											Cloner cloner, Map<ControlParameterType, ControlParameter> controlParameters) {
	    if (numElem == k) {
    		    	
			logger.debug(" >> START << ");
			logger.debug("Accumulating the following evidences: " + (Arrays.toString(branch)));
		
			//Create a deep copy of the synBayesMatrix using the Cloner object
			SemanticMatrix accumSynEvidMatrixClone = cloner.deepClone(synBayesMatrix);
			
			
			//Accumulate only the evidences specified by the combination
			Set<BooleanVariables> evidencesToAccumulate = new HashSet<BooleanVariables>(Arrays.asList(branch));
			logger.debug("Accumulating the following evidences: " + evidencesToAccumulate);
	    	
			//SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(synBayesMatrix, testSchema1, testSchema2,
																					//semanticCube, pmfList, evidencesToAccumulate, null);
			
			SemanticMatrix accumSemEvidenceMatrix = schemaService.accumulateSemEvidenceBayes(accumSynEvidMatrixClone, testSchema1, testSchema2,
																					semanticCube, pmfList, evidencesToAccumulate, controlParameters);
	    	
		
			
			/***
			 * STEP 5.1: hold matrix that considers both syntactic and semantic evidences with Bayes
			 * This is the same matrix but with updated posteriors in the presence of semantic evidence
			 */
			float[][] matrixSynSem = accumSemEvidenceMatrix.getSemMatrixAsArray();	    	

			
			Map<PerformanceErrorTypes, Float> error2 = null;
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ALL_SYN_SEM_CELLS))) {
				/**
				 * If we want to calculate the error considering all cells (instances) then Error1 is calculated only
				 * once (outside the recursion) and Error2 is calculated every time				
				 */				
				logger.info("");
				logger.info("Error2: ");
				//Calculate the error2 using different measures ( SynSem against Expectation matrix)
				error2 = this.calculatePerformance(matrixSynSem, actualMatrix);  
			
			} else if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ONLY_SYN_SEM_CELLS))) {
				/**
				 * If we want to calculate the error only on specific cells (instances) then Error1 and Error2
				 * need to be calculated every time. Error1 needs to be calc every time as well here because each time
				 * different cells (instances) are selected to have both syn and sem evidence		
				 */
				logger.info("");
				logger.info("Error1: ");
				error1 = this.calculatePerformance(matrixSynOnly, actualMatrix, accumSemEvidenceMatrix.getIndexesSet());
				//logger.info("Indexes Set Error1: " + accumSemEvidenceMatrix.getIndexesSet());
				
				logger.info("");
				logger.info("Error2: ");
				error2 = this.calculatePerformance(matrixSynSem, actualMatrix, accumSemEvidenceMatrix.getIndexesSet());
				//logger.info("Indexes Set Error2: " + accumSemEvidenceMatrix.getIndexesSet());
			}//end if		
  	
	    	
			//Save the result of calculating the errors in a CSV file 
			schemaService.getGraphvizDotGeneratorService().exportToCSVFile(error1,error2,evidencesToAccumulate,null,controlParameters);
				
			
			logger.debug(" >> END << ");
     
	        return;
	    }//end if
	   
	    for (int i = startId; i < arr.size(); ++i) {
	        branch[numElem++] = arr.get(i);
	        findCombinationsWithLength(arr, k, ++startId, branch, numElem, synBayesMatrix,
	        							testSchema1, testSchema2, semanticCube, pmfList,
	        							actualMatrix, matrixSynOnly, error1, cloner, controlParameters);
	        --numElem;
	    }//end for
	}//end	
	
	
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
	
	/**
	 * Attach performance measures to calculate the error
	 */
	public void attactAggrErrMeasure(PerformanceMeasures pm) {
		aggregatedErrorMeasures.add(pm);
	}
	
	public void attactSingErrMeasure(ErrorMeasures em) {
		singleErrorMeasures.add(em);
	}
		
	/***
	 * predictedMatrix - values predicted by the algorithm
	 * observedMatrix - actual values
	 */
	public Map<PerformanceErrorTypes, Float> calculatePerformance(float[][] predictedMatrix, float[][] observedMatrix) {
		logger.info("in calculatePerformance()");
		Map<PerformanceErrorTypes, Float> petMap = new HashMap<PerformanceErrorTypes, Float>();
				
		for (PerformanceMeasures pm : aggregatedErrorMeasures) {
			//add each performance measure to the map
			PerformanceErrorTypes pet = pm.getmType(); 
			float res = pm.calc(predictedMatrix, observedMatrix);			
			petMap.put(pet, res);			
			//logger.info("PerformanceMeasure is: " + pet + " | Result: " + res);
		}
		//logger.info("");
		//logger.info("");
		
		return petMap;
	}//end calculatePerformance()
	
	
	/**
	 * @return - SPARQL ASK query returns true if match exists in the alignment
	 */
	private com.hp.hpl.jena.query.Query alignmentASKQuery(String entity1URI, String entity2URI) {
		 String queryString =  getNSPrefixes() +	
	        		" ASK " +
	        		" WHERE { " +
	        		"   ?s align:entity1 <" + entity1URI + "> ." + 
	        		"   ?s align:entity2 <" + entity2URI + "> ." +
	        		" } ";		
		
		return QueryFactory.create(queryString);		
	}//end alignmentQuery()	
	
	/**
	 * @return - Method that takes care of the namespaces part of a SPARQL query
	 */
	public String getNSPrefixes() {		
		String prefixes = 
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
    	        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
    	        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
    	        "PREFIX void: <http://vocab.deri.ie/void#> " +
    	        "PREFIX j.0: <x-ns://train.metadata/#> " +
    	        "PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment> ";
		
		return prefixes;
	}//end getNSPrefixes()	
	
	/***
	 * predictedMatrix - values predicted by the algorithm
	 * observedMatrix - actual values
	 */
	public Map<PerformanceErrorTypes, Float> calculatePerformance(float[][] predictedMatrix,
																	float[][] observedMatrix, Set<SemanticMatrixCellIndex> iSet) {
		logger.info("in calculatePerformance()");
		Map<PerformanceErrorTypes, Float> petMap = new HashMap<PerformanceErrorTypes, Float>();
				
		for (PerformanceMeasures pm : aggregatedErrorMeasures) {
			//add each performance measure to the map
			PerformanceErrorTypes pet = pm.getmType(); 
			float res = pm.calc(predictedMatrix, observedMatrix, iSet);			
			petMap.put(pet, res);			
			//logger.info("PerformanceMeasure is: " + pet + " | Result: " + res);
		}
		//logger.info("");
		//logger.info("");
		
		return petMap;
	}//end calculatePerformance()
}//end class
