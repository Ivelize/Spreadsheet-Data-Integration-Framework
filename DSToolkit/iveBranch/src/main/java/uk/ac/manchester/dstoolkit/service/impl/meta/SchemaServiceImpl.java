/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.smartcardio.ATR;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelProperty;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipType;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.CardinalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.Morphism;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperationType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ConstructRelatedSchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.DirectionalityType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpressionType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondenceType;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.schematiccorrespondence.SchematicCorrespondenceRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.DatatypeMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherInfo;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.MatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NGramMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.NameMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.SelectionType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.StatisticsMatcherServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ActionStatus;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.DomainSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.HierarchySemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.NameSpaceSemMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.RangeSemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrix;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixCellIndex;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixComparator;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixEntry;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.SemanticMatrixType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.MatchingProfilerServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.benchmark.DampeningEffectPolicy;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.BooleanVariables;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix.ImportExpMatrixServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.BayesEntry;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityDensityFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.distributions.ProbabilityMassFunction;
import uk.ac.manchester.dstoolkit.service.impl.util.training.GenerateExpectationMatrixImpl;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.meta.SemanticMetadataService;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.AnnotationMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.ConstructBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.InstanceBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.StringBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.InferCorrespondenceService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.ExpMatrix.ImportExpMatrixService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.DereferenceURIAgentService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.Constant;
import uk.ac.manchester.dstoolkit.service.util.training.GenerateExpectationMatrix;

/**
 * @author chedeler
 * @author lmao
 * @author ruhaila
 * @author klitos
 * 
 * Revision (klitos):
 *  1. Add new matchers for matching RDF sources.
 *  2. Rewrote the match() now is called runMatch().
 *  3. Schema enrichment method to support new matching architecture based on meta-data from namespaces.
 *  4. Implement framework for using Bayes inference to accumulate evidence.
 *  5. Rewrote method for producing and storing the final matches.
 * 
 */
//@Transactional(readOnly = true)
@Service(value = "schemaService")
public class SchemaServiceImpl extends GenericEntityServiceImpl<Schema, Long> implements SchemaService {

	//TODO test this properly

	static Logger logger = Logger.getLogger(SchemaServiceImpl.class);
	
	/*Configuration files for SDBStores, sdb_graphs and sdb_metadata*/
	private String jenaSDBMetaDataPropLoc = "./src/main/resources/datasources/jenaSDBmeta.properties";
	private String jenaSDBGraphsPropLoc   = "./src/main/resources/datasources/jenaSDBgraphs.properties";
	
	protected static User currentUser;
	protected static Dataspace currentDataspace;
	protected static DataSource currentDS;

	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	@Autowired
	@Qualifier("matchingProfilerServiceImpl")
	private MatchingProfilerServiceImpl matchingProfilerServiceImpl;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("inferCorrespondenceService")
	private InferCorrespondenceService inferCorrespondenceService;

	@Autowired
	@Qualifier("matchingService")
	private MatchingService matchingService;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("dereferenceURIAgentService")
	private DereferenceURIAgentService dereferenceURIAgentService;
	
	@Autowired
	@Qualifier("mappingService")
	private MappingService mappingService;
	
	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;
	
	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;
	
	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Autowired
	@Qualifier("dataspaceRepository")
	private DataspaceRepository dataspaceRepository;
	
	@Autowired
	@Qualifier("schematicCorrespondenceRepository")
	private SchematicCorrespondenceRepository schematicCorrespondenceRepository;
	
	@Autowired
	@Qualifier("dataspaceService")
	private DataspaceService dataspaceService;
	
	@Autowired
	private MatchingProducerService matchingProducerService;
	
	/*Hold a reference to the SDBStore that holds metadata*/
	private SDBStoreServiceImpl metaDataSDBStore = null;
	private SDBStoreServiceImpl graphsSDBStore = null;
	
	/*Run individual matchers holds results into a sim-cube*/
	private List<MatcherInfo> simCubeOfMatchers = null;
	
	/*Organise semantic annotations and hold them into a sim-cube*/
	//private Map<SemanticMatrixType, SemanticMatrix> simCubeOfSemanticMatrices;
	private List<SemanticMatrix> simCubeOfSemanticMatrices = null;
	
	private List<SuperRelationship> srTempVector = new ArrayList<SuperRelationship>();

	private Schema schema1;
	private Schema schema2;
	private Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2;

	//Output merge
	private Schema mergedSchema;
	private Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndMergedSchema;
	private Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema2AndMergedSchema;

	//Output diff
	private Set<SchematicCorrespondence> schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2;
	private Set<SuperAbstract> superabstractsOfSchema1MissingInSchema2;
	private Set<SuperAbstract> superabstractsOfSchema2MissingInSchema1;

	//Output viewGen
	private Set<Mapping> mappingsBetweenSchema1AndSchema2;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	//private ArrayList<CanonicalModelConstruct> sourceConstructs;
	//private ArrayList<CanonicalModelConstruct> targetConstructs;

	public void inferCorrespondence(Set<Schema> sourceSchemas, Set<Schema> targetSchemas, List<Matching> matchings,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		//TODO need to get maxMatchingScore ...
		//TODO should go into ControlParameters
		double maxMatchingScore = 1.0d;
		inferCorrespondenceService.inferCorrespondences(sourceSchemas, targetSchemas, matchings, maxMatchingScore, controlParameters);
	}

	public Set<SchematicCorrespondence> inferCorrespondences(Set<Schema> sourceSchemas, Set<Schema> targetSchemas, List<Matching> matchings,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		//TODO need to get maxMatchingScore ...
		//TODO should go into ControlParameters
		double maxMatchingScore = 1.0d;
		
		 Set<SchematicCorrespondence> scAux = new HashSet<SchematicCorrespondence>();
		 scAux = inferCorrespondenceService.inferCorrespondences(sourceSchemas, targetSchemas, matchings, maxMatchingScore, controlParameters);
		
		return scAux;
	}	
	
	
	//TODO add other matchers from LSMA
	//TODO sort out selection and aggregation in all matchers, check whether the corresponding config parameters are actually there
	//TODO make matches persistent

	/***
	 * MATCH Strategy 1: This is the default match strategy where a predefined combination of individual matchers are executed.
	 */
	public List<Matching> match(Schema schema1, Schema schema2, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in match with default matcher configuration");
		logger.debug("schema1: " + schema1);
		logger.debug("schema2: " + schema2);

		List<MatcherService> defaultMatchers = new ArrayList<MatcherService>();

		MatcherService ngramMatcherForNameMatcher = new NGramMatcherServiceImpl(3);

		/*NameMatcher: performs Syntactical name matching*/
		MatcherService nameMatcher = new NameMatcherServiceImpl();
		ControlParameter thresholdSelectionForNameMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		ControlParameter thresholdForNameMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.3");
		nameMatcher.addControlParameter(thresholdSelectionForNameMatcher);
		nameMatcher.addControlParameter(thresholdForNameMatcher);
		nameMatcher.addChildMatcher(ngramMatcherForNameMatcher);

		MatcherService ngramMatcherForNamePathMatcher = new NGramMatcherServiceImpl(3); //TODO think about whether the length of the ngram should be a controlParameter too

		MatcherService namePathMatcher = new NameMatcherServiceImpl();
		ControlParameter thresholdSelectionForNamePathMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		ControlParameter thresholdForNamePathMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.3");
		namePathMatcher.addControlParameter(thresholdSelectionForNamePathMatcher);
		namePathMatcher.addControlParameter(thresholdForNamePathMatcher);
		namePathMatcher.addChildMatcher(ngramMatcherForNamePathMatcher);

		MatcherService statisticsMatcher = new StatisticsMatcherServiceImpl();
		ControlParameter thresholdSelectionForStatisticsMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		ControlParameter thresholdForStatisticsMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.3");
		statisticsMatcher.addControlParameter(thresholdSelectionForStatisticsMatcher);
		statisticsMatcher.addControlParameter(thresholdForStatisticsMatcher);

		MatcherService datatypeMatcher = new DatatypeMatcherServiceImpl();
		ControlParameter thresholdSelectionForDatatypeMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_SELECTION_TYPE,
				SelectionType.THRESHOLD.toString());
		ControlParameter thresholdForDatatypeMatcher = new ControlParameter(ControlParameterType.MATCH_SELECT_THRESHOLD_VALUE, "0.5");
		datatypeMatcher.addControlParameter(thresholdSelectionForDatatypeMatcher);
		datatypeMatcher.addControlParameter(thresholdForDatatypeMatcher);

		defaultMatchers.add(nameMatcher);
		defaultMatchers.add(namePathMatcher);
		defaultMatchers.add(statisticsMatcher);
		defaultMatchers.add(datatypeMatcher);
		logger.debug("defaultMatchers: " + defaultMatchers);
		
		
		//TODO (klitos): I may need to consider implementing a semantic matching approach using WordNet

		//This method calls the next method in order to get a Matching result  result.
		return match(schema1, schema2, defaultMatchers, new HashMap<ControlParameterType, ControlParameter>());
	}

	/***
	 * MATCH Strategy 2: In this match strategy the user constructs a list of matchers and asks the system to execute them.
	 * This is the old match method I will do my own. 
	 */
	//@Transactional
	public List<Matching> match(Schema schema1, Schema schema2, List<MatcherService> matchers,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in match with matchers as input");
		logger.debug("schema1: " + schema1);
		logger.debug("schema2: " + schema2);
		logger.debug("matchers: " + matchers);
		logger.debug("matchers.size: " + matchers.size());
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		logger.debug("numberOfCores: " + numberOfCores);
		List<Matching> matches = new ArrayList<Matching>();
		
		if (schema1 != null && schema2 != null && matchers != null && matchers.size() > 0) {
			ArrayList<CanonicalModelConstruct> constructs1 = getConstructs(schema1.getCanonicalModelConstructs());
			ArrayList<CanonicalModelConstruct> constructs2 = getConstructs(schema2.getCanonicalModelConstructs());
			
			logger.debug("constructs1: " + constructs1);
			logger.debug("constructs2: " + constructs2);
			
			for (MatcherService matcher : matchers) {
			logger.debug("matcher: " + matcher.getMatcherType());
			
				
				if (matcher instanceof StringBasedMatcherService) {
					logger.debug("--> matcher is StringBasedMatcher");
					float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
					StringBasedMatcherService stringBasedMatcher = (StringBasedMatcherService) matcher;
					/**
					* Here check if the matcher is a combined matcher and if yes it needs to getChildMatchers 
					*/
					if (stringBasedMatcher.getChildMatchers().size() > 0)
					logger.error("stringBasedMatcher has childMatcher ... not processed right now - TODO sort this");
					logger.info("constructs1.size(): " + constructs1.size());
					logger.info("constructs2.size(): " + constructs2.size());
					for (CanonicalModelConstruct cons1 : constructs1) {
					System.out.print(cons1.getName()+"�");
					}
					System.out.println("");
					for (CanonicalModelConstruct cons2 : constructs2) {
					System.out.print(cons2.getName()+"�");
					}
					
					List<Matching> oneToOneMatches = new ArrayList<Matching>();
					long startTime = System.nanoTime();
					logger.info("start matching all constructs with StringBasedMatcher: " + startTime);
					for (CanonicalModelConstruct construct1 : constructs1) {
						for (CanonicalModelConstruct construct2 : constructs2) {
							long startTimeSingleMatch = System.nanoTime();
							logger.info("start matching two constructs with StringBasedMatcher: " + startTimeSingleMatch);
							Matching match =stringBasedMatcher.match(construct1, construct2, controlParameters);
							if (match!=null){
								logger.debug("match!=null");
								oneToOneMatches.add(match);
							}
					
					
					
							long endTimeSingleMatch = System.nanoTime();
							logger.info("finished matching two constructs with StringBasedMatcher: " + endTimeSingleMatch);
							logger.info("duration for matching two constructs: " + (endTimeSingleMatch - startTimeSingleMatch) / 1.0e9);
							logger.debug("simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)]: "
								+ simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)]);
						}
					}
					long endTime = System.nanoTime();
					System.out.println("");
					System.out.println("constructs1.size(): " + constructs1.size());
					System.out.println("constructs2.size(): " + constructs2.size());
					logger.info("finished matching all constructs with StringBasedMatcher: " + endTime);
					logger.info("duration for matching all constructs: " + (endTime - startTime) / 1.0e9);
					System.out.println("oneToOneMatches.size(): " + oneToOneMatches.size());
					logger.debug("oneToOneMatches: " + oneToOneMatches);
					matcher.setMatchings(oneToOneMatches);
					matches.addAll(oneToOneMatches);
				}
			}
			saveMatches(matches);  // <-- consider uncomment this for making macthes persistent
			return matches;
		} else
			return null;
	}//end OLD MATCH METHOD

	/***
	 * This is the new method for performing the matching task. 
	 *	
	 * Control parameters in arguments to be used later when creating the final oneTOone matches.
	 */
	public List<MatcherInfo> runMatch(Schema sourceSchema, Schema targetSchema, List<MatcherService> matchers,
														Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("*** NEW MATCH ***");		
		logger.debug("in runMatcher with matchers as input");
		logger.debug("sourceSchema: " + sourceSchema);
		logger.debug("targetSchema: " + targetSchema);
		logger.debug("matchers: " + matchers);
		logger.debug("matchers.size: " + matchers.size());
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		logger.debug("numberOfCores: " + numberOfCores);
		
		/*Make sure that a reference to the SDBStore exists*/
		if (metaDataSDBStore == null) {
			metaDataSDBStore = loadRDFSourceUtilService.getSDBStoreForDB(jenaSDBMetaDataPropLoc);
			logger.debug("metaDataSDBStore : " + metaDataSDBStore);
			/*Initialise URI Agent on that SDBStore*/
			dereferenceURIAgentService.initiliaseURIagent(metaDataSDBStore);
		}//end if
		
		/*Hold the simMatrices of all Matchers*/
		simCubeOfMatchers = new ArrayList<MatcherInfo>(); 
		
		if (sourceSchema != null && targetSchema != null && matchers != null && matchers.size() > 0) {
			/*Get the constructs to match from the schemas*/
			ArrayList<CanonicalModelConstruct> sourceConstructs = getConstructs(sourceSchema.getCanonicalModelConstructs());
			ArrayList<CanonicalModelConstruct> targetConstructs = getConstructs(targetSchema.getCanonicalModelConstructs());
			
			logger.debug("sourceConstructs: " + sourceConstructs);
			logger.debug("targetConstructs: " + targetConstructs);	
			
			/*Step 1: Get an unmodified list of matchers. This will make sure that the index of each matcher remains the same*/
			matchers = Collections.unmodifiableList(matchers);
			logger.debug("Matcher Names: " + matchers);
				
			/*Step 2: From the list of matchers execute each matcher individually*/
			for (MatcherService matcher : matchers) {
				if (matcher instanceof ConstructBasedMatcherService) {					
					logger.debug("Matcher Type is: ConstructBasedMatcherService");
					//StringBasedMatcherService stringBasedMatcher = (StringBasedMatcherService) matcher;
					logger.debug("Start the matching process...");					
					logger.info("constructs1.size(): " + sourceConstructs.size());
					logger.info("constructs2.size(): " + targetConstructs.size());
					/*Before matching collect some information for this matcher*/
					String matcherClassName = matcher.getClass().getName();
					logger.debug("matcherClassName: " + matcherClassName);
					MatcherInfo info = new MatcherInfo(matcher, matchers.indexOf(matcher));
					long startTime = System.nanoTime();
					logger.info("start matching constructs with ConstructBasedMatcher: " + startTime);
					float[][] simMatrix = ((ConstructBasedMatcherService) matcher).match(sourceConstructs, targetConstructs);										
					long endTime = System.nanoTime();
					logger.info("finished matching constructs with ConstructBasedMatcher: " + endTime);
					logger.info("duration for matching constructs: " + (endTime - startTime) / 1.0e9);					
					/*Save matrix to the simCube List*/
					info.addSimMatrix(simMatrix);		
					simCubeOfMatchers.add(info);
					/* OUTPUT: Use graphviz to output a single syntactic similarity matrix into a visual form*/
					//graphvizDotGeneratorService.generateDOT(sourceConstructs, targetConstructs, simMatrix);					
				} else if (matcher instanceof InstanceBasedMatcherService) {
					logger.debug("Matcher Type is: InstanceBasedMatcherService");					
				} else if (matcher instanceof AnnotationMatcherService) {
					logger.debug("Matcher Type is: AnnotationMatcherService");
					AnnotationMatcherService annotationMatcher = (AnnotationMatcherService) matcher;
					annotationMatcher.attachMetaDataSDBStore(this.metaDataSDBStore);					
					logger.debug("Start the matching process...");					
					logger.info("constructs1.size(): " + sourceConstructs.size());
					logger.info("constructs2.size(): " + targetConstructs.size());
					/*Before matching collect some information for this matcher*/
					String matcherClassName = matcher.getClass().getName();
					logger.debug("matcherClassName: " + matcherClassName);
					MatcherInfo info = new MatcherInfo(matcherClassName, matchers.indexOf(matcher));
					long startTime = System.nanoTime();
					logger.info("start matching constructs with StringBasedMatcher: " + startTime);
					float[][] simMatrix = annotationMatcher.match(sourceConstructs, targetConstructs);
					/*Save matrix to the simCube List*/
					info.addSimMatrix(simMatrix);	
					
				} //TODO - other matchers to be added here
				
			}//end for		
		
			/** Graphviz used to visualise the set of syntactic matrices **/
			graphvizDotGeneratorService.generateDOTSyn(sourceConstructs, targetConstructs, simCubeOfMatchers);
			
			/*At the end of the method return a similarity cube that has all the syntactic matrices, along with the matcher reference*/
			return simCubeOfMatchers;
		} else {
			return null;
		}
	}//end runMatch()	
		
	
	/****
	 * Calling runMatching() will produce a similarity cube [matcher][][], holding the similarity matrix for each matcher.
	 * A matcher can be anything, a combined matcher like ConstructBasedMatcher that has child matchers or other type of 
	 * primitive matchers. Having obtained a similarity cube this method allows the creation of the final matchings according
	 * to an aggregation and a selection strategy.
	 * 
	 *   - If we have matched using the COMA++ strategy then we need to see whether we need to aggregate the results of the 
	 *   matchers and then whether we would like to apply a selection strategy to remove matchings with very low score.
	 * 
	 * @return List<Matching> - final Matchings after aggregation and selection strategies applied to them 
	 */
	public List<Matching> produceAndSaveMatchings(Schema sourceSchema, Schema targetSchema, List<MatcherInfo> simCubeOfMatchers,
																	Map<ControlParameterType, ControlParameter> controlParameters) {
		/***
		 * List that holds the final matchers after aggregation and selection.
		 */		
		List<Matching> matches = null;
				
		if (sourceSchema != null && targetSchema != null) {	
			
			ArrayList<CanonicalModelConstruct> sourceConstructs = null;
			ArrayList<CanonicalModelConstruct> targetConstructs = null;
			matches = new ArrayList<Matching>();
									
			//COMA++ Approach
			sourceConstructs = getConstructs(sourceSchema.getCanonicalModelConstructs());
			targetConstructs = getConstructs(targetSchema.getCanonicalModelConstructs());
				
			/***
			 * Means that the similarity cube has not been aggregated yet. Most Matchers sort out aggregation,
			 * selection within their implementation but it might be the case that they do not, thus in such cases
			 * this piece of code will sort out the aggregation of the simCube and then the selection of matches.
			 */
			if (simCubeOfMatchers.size() > 1) {
				//Need to aggregate the results from different matchers
				float[][] simMatrix = MatcherServiceImpl.aggregate(simCubeOfMatchers, controlParameters);
				
				//Selection
				simMatrix = MatcherServiceImpl.selectMethod(simMatrix, controlParameters);
				
				//Produce final matchings
				matches = matchingProducerService.produceFinalMatches(simMatrix, sourceConstructs, 
																						targetConstructs, controlParameters, null);
				
				//Persist each matching
				for (Matching matching : matches)
					matchingService.addMatching(matching);
				
				//Write matches in a file
				//matchingProducerService.writeMatchingsToFile(simMatrix, sourceConstructs, targetConstructs, controlParameters);
									
			} else {
				//simCube has been aggregated and matchings selected by each matcher already
				if ((simCubeOfMatchers != null) || (simCubeOfMatchers.size() != 0)) {
					float[][] simMatrix = simCubeOfMatchers.get(0).getSimMatrix();
					
					/* In this case the similarity matrix is aggregated and then the matches have been selected,
					the matcher was responsible for both the aggregation strategy and the selection. It is the 
					responsibility of the matcher to do the selection. Then the produceFinalMatches method is
					responsible of making the matches persistent and then return them as a list of Matching 
					objects List<Matching> */ 
					
					//Produce the Matching objects for the matches
					matches = matchingProducerService.produceFinalMatches(simMatrix, sourceConstructs, 
																							targetConstructs, controlParameters, null);
						
					//Persist each matching
					for (Matching matching : matches)
						matchingService.addMatching(matching);						
					
					//Write matches in a file
					//matchingProducerService.writeMatchingsToFile(simMatrix, sourceConstructs, targetConstructs, controlParameters);						
				}//end if
			}//end else						
		}//end if

		return matches;
	}//end producingAndSaveMatchings()	
	
	/***
	 * This method is responsible for producing Matching objects for selected cells. It makes a call to the matchingProducerService service
	 * for producing the Matching objects.
	 * 
	 * @param cellsSet
	 * @return List<Matching>
	 */
	public List<Matching> produceMatchesForSpecificCells(Schema sourceSchema, Schema targetSchema,
			 											final float[][] simMatrix, final Map<ControlParameterType, ControlParameter> controlParameters,
			 											Set<SemanticMatrixCellIndex> cellsSet, final MatcherService matcherService) {
		List<Matching> matches = null;
		
		if (sourceSchema != null && targetSchema != null) {	
			ArrayList<CanonicalModelConstruct> sourceConstructs = null;
			ArrayList<CanonicalModelConstruct> targetConstructs = null;
									
			sourceConstructs = getConstructs(sourceSchema.getCanonicalModelConstructs());
			targetConstructs = getConstructs(targetSchema.getCanonicalModelConstructs());

			if (sourceConstructs != null && targetConstructs != null) {				
				matches = matchingProducerService.produceMatchesForSpecificCells(sourceConstructs, targetConstructs,
																				 simMatrix, controlParameters, cellsSet, matcherService);
				
			}//end if			
		}//end if
		
		return matches;		
	}//end produceMatchesForSpecificCells()
	
	
	//TODO too many hacks in the matchers ... incl. the concurrentInstanceMatcher
	/* Schema-based matching plus Instance-based matching */
	//@Transactional
	public List<Matching> match(Schema schema1, Schema schema2, List<MatcherService> matchers, DataSource dataSource1, DataSource dataSource2,
			Map<ControlParameterType, ControlParameter> controlParameters) throws ExecutionException {
		// TOOD think about using bloom filters for instance based matching

		logger.debug("in match with datasources as input");
		logger.debug("schema1: " + schema1);
		logger.debug("schema2: " + schema2);
		logger.debug("matchers: " + matchers);
		logger.debug("matchers.size: " + matchers.size());
		logger.debug("datasource1: " + dataSource1);
		logger.debug("datasource2: " + dataSource2);
		List<Matching> matches = new ArrayList<Matching>();

		if (schema1 != null && schema2 != null && matchers != null && matchers.size() > 0) {
			//Set<CanonicalModelConstruct> constructs1Set = schema1.getSuperAbstractsAndSuperLexicals();
			//Set<CanonicalModelConstruct> constructs2Set = schema2.getSuperAbstractsAndSuperLexicals();

			//TOOD this is a hack ...
			//List<CanonicalModelConstruct> constructs1 = new ArrayList<CanonicalModelConstruct>();
			//for (CanonicalModelConstruct construct1 : constructs1Set)
			//	constructs1.add(construct1);
			//List<CanonicalModelConstruct> constructs2 = new ArrayList<CanonicalModelConstruct>();
			//for (CanonicalModelConstruct construct2 : constructs2Set)
			//	constructs2.add(construct2);

			List<CanonicalModelConstruct> constructs1 = this.getConstructs(schema1.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> constructs2 = this.getConstructs(schema2.getCanonicalModelConstructs());

			logger.debug("constructs1 : " + constructs1.toString());
			logger.debug("constructs2 : " + constructs2.toString());

			List<MatcherService> nonInstanceBasedMatcherList = new ArrayList<MatcherService>();

			for (MatcherService matcher : matchers) {
				logger.debug("matcher: " + matcher);

				if (matcher instanceof InstanceBasedMatcherService) {
					logger.debug("matcher is InstanceBasedMatcherService");
					logger.debug("before calling match of matcher");
					InstanceBasedMatcherService instanceMatcher = (InstanceBasedMatcherService) matcher;
					//instanceMatcher.setQueryService(queryService);
					//instanceMatcher.setQueryRepository(queryRepository);
					//instanceMatcher.setQueryResultRepository(queryResultRepository);
					//instanceMatcher.setMatchingProfilerServiceImpl(matchingProfilerServiceImpl);
					logger.info("constructs1.size(): " + constructs1.size());
					logger.info("constructs2.size(): " + constructs2.size());
					long startTime = System.nanoTime();
					logger.info("start matching instances: " + startTime);
					List<Matching> oneToOneMatches = instanceMatcher.match(constructs1, constructs2, dataSource1, dataSource2, controlParameters);
					long endTime = System.nanoTime();
					logger.info("constructs1.size(): " + constructs1.size());
					logger.info("constructs2.size(): " + constructs2.size());
					logger.info("finished matching instances: " + endTime);
					logger.info("duration for instance matching: " + (endTime - startTime) / 1.0e9);
					//logger.debug("simMatrix: " + simMatrix);
					logger.debug("matcher: " + matcher);
					logger.debug("matcher is InstanceBasedMatcherService");
					//logger.debug("before calling produceMatches");
					//startTime = System.nanoTime();
					//logger.info("start producing matches: " + startTime);
					//List<Matching> oneToOneMatches = produceAndSaveMatches(simMatrix, constructs1, constructs2, matcher, controlParameters);
					//endTime = System.nanoTime();
					//logger.info("finished producing matches: " + endTime);
					//logger.info("duration for producing matches: " + (endTime - startTime) / 1.0e9);
					logger.debug("oneToOneMatches.size(): " + oneToOneMatches.size());
					logger.debug("oneToOneMatches: " + oneToOneMatches);
					// matcher.setMatchings(oneToOneMatches);
					matches.addAll(oneToOneMatches);
				} else {
					logger.debug("matcher: " + matcher);
					logger.debug("matcher isn't InstanceBasedMatcherService");
					logger.debug("add to list and run later");

					nonInstanceBasedMatcherList.add(matcher);
				}
			}

			if (!nonInstanceBasedMatcherList.isEmpty()) {
				logger.debug("found non instance based matchers - pass on to schema-based match");
				logger.debug("nonInstanceBasedMatcherList.size: " + nonInstanceBasedMatcherList.size());
				List<Matching> oneToOneMatches = this.match(schema1, schema2, nonInstanceBasedMatcherList, controlParameters);
				logger.debug("oneToOneMatches.size(): " + oneToOneMatches.size());
				logger.debug("oneToOneMatches: " + oneToOneMatches);
				matches.addAll(oneToOneMatches);
			}

			logger.info("before saving matches of size: " + matches.size());
			saveMatches(matches);
			logger.info("before writing matchProfileToFile");
			this.matchingProfilerServiceImpl.printMatchingProfileToFile();
			return matches;
		} else
			return null;
	}
	
	/***
	 * This method is responsible for generating the Expectation Matrix to be used for evaluating the new 
	 * Matching framework. Basically this method constructs a 2D matrix with the scores obtained by 
	 * aggregating the results of the survey.
	 * 
	 * @param alignURL - the path of the xml document that holds the expectation matrix
	 */
	public SemanticMatrix generateExpectationMatrix(Schema schema1, Schema schema2, String alignUrl) {	 
		logger.debug("in generateExpectationMatrix");
		logger.debug("schema1: " + schema1);
		logger.debug("schema2: " + schema2);	
		SemanticMatrix expMatrix = null;
		
		if (schema1 != null && schema2 != null) {
			
			/*Get constructs from each Schema*/
			List<CanonicalModelConstruct> sourceConstructs = this.getConstructs(schema1.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> targetConstructs = this.getConstructs(schema2.getCanonicalModelConstructs());
			
			logger.debug("sourceConstructs : " + sourceConstructs.toString());
			logger.debug("targetConstructs : " + targetConstructs.toString());			

			/*Call class to Read the xml file that holds the Expectation Matrix as XML elements*/
			ImportExpMatrixService importExpMatrix = new ImportExpMatrixServiceImpl();
			importExpMatrix.readMatrixFromXML(alignUrl);	
			
			/*Attach a service object that reads the XML file to the generate GenerateExpectationMatrix service*/
			GenerateExpectationMatrix expService = new GenerateExpectationMatrixImpl(importExpMatrix); 
			
			/*Call class to generate the Expectation Matrix*/
			expMatrix = expService.generateExpectationMatrix(sourceConstructs, targetConstructs);
			
			/*Output it as a matrix, I may need not to use this here and just in the class that calls it*/
			graphvizDotGeneratorService.expectationMatrixDOTSem(sourceConstructs, targetConstructs, expMatrix, true);

		}//end if		
		
		return expMatrix;
	}//end generateGroundTruth()	
	
	/***
	 * This method is responsible for generating the Expectation Matrix to be used for evaluating the new 
	 * Matching framework. Basically this method constructs a 2D matrix with the scores obtained by 
	 * aggregating the results of the survey.
	 * 
	 * @param expModelURI - the named Graph that holds the GT as alignments
	 * @param tdbStore - a reference to a Jena TDB Store
	 */
	public SemanticMatrix generateExpectationMatrix(Schema schema1, Schema schema2, TDBStoreServiceImpl tdbStore, String expModelURI) {
		logger.debug("in generateExpectationMatrix from Jena TDB Store");
		logger.debug("schema1: " + schema1);
		logger.debug("schema2: " + schema2);	
		SemanticMatrix expMatrix = null;
		
		if (schema1 != null && schema2 != null) {
			/*Get constructs from each Schema*/
			List<CanonicalModelConstruct> sourceConstructs = this.getConstructs(schema1.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> targetConstructs = this.getConstructs(schema2.getCanonicalModelConstructs());
			
			/*Attach the tdbStore to the service responsible to create the expectation matrix from a Jena Model*/
			GenerateExpectationMatrix expService = new GenerateExpectationMatrixImpl(tdbStore);
			
			/*Call class to generate the Expectation Matrix*/
			expMatrix = expService.generateExpectationMatrixFromModel(sourceConstructs, targetConstructs, expModelURI);
			
			/*Output it as a matrix, I may need not to use this here and just in the class that calls it*/
			graphvizDotGeneratorService.expectationMatrixDOTSem(sourceConstructs, targetConstructs, expMatrix, true);			
		}//end if	
		
		return expMatrix;		
	}//end generateExpectationMatrix()	
	
	/***
	 * RDF - Schema Enrichment method: As part of the new match architecture. This method will call the dereference agent 
	 * 
	 * 
	 * NOTE: When dereferencing is up to the publishers what is returned back. For example when dereferencing an 
	 * ontology (vocabulary) the whole RDF-Document that describes the ontology is usually returned. However, dereferencing 
	 * instance data it will return all only specific RDF-triples (this allows the follow-your-nose). It will basically
	 * return all resources that have the uri as either a subject, predicate or object
	 * 
	 * 
	 * TODO: Temporarily this method does not return anything
	 * 
	 * @param schema1
	 * @param schema2
	 * @param dataSource1
	 * @param dataSource2
	 * @throws ExecutionException
	 */
	public void schemaEnrichment(Schema schema1, Schema schema2) {	 
		logger.debug("in schemaEnrichment for RDF");
		logger.debug("schema1: " + schema1);
		logger.debug("schema2: " + schema2);	
		
		if (schema1 != null && schema2 != null) {

			List<CanonicalModelConstruct> constructs1 = this.getConstructs(schema1.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> constructs2 = this.getConstructs(schema2.getCanonicalModelConstructs());

			logger.debug("constructs1 : " + constructs1.toString());
			logger.debug("constructs2 : " + constructs2.toString());
			
			/*Make sure that a reference to the SDBStore exists*/
			if (metaDataSDBStore == null) {
				metaDataSDBStore = loadRDFSourceUtilService.getSDBStoreForDB(jenaSDBMetaDataPropLoc);
				logger.debug("metaDataSDBStore : " + metaDataSDBStore);
				/*Initialise URI Agent on that SDBStore*/
				dereferenceURIAgentService.initiliaseURIagent(metaDataSDBStore);
			}//end if
			
			//Start monitoring the time since the dereference URI started
			long startTime = System.nanoTime();
			logger.info("start schema enrichment process (nanoseconds): " + startTime);

			//int status = dereferenceURIAgentService.dereferenceURI("http://www.example.org/foo/bar", "http://xmlns.com/foaf/0.1/Person");
			//boolean test = dereferenceURIAgentService.isGraphExists("http://www.example.org/foo/bar");
			//logger.info("Does exists (by ASK query) " + test);
			
			/*SCHEMA: Enrichment of schema 1 constructs*/
			for (CanonicalModelConstruct constr_element1 : constructs1) {
				logger.info("In Schema >: " + schema1.getName());
				String dereferencedStatus = null;
				CanonicalModelProperty constructPropURI = null;
				CanonicalModelProperty constructPropNS = null;				
				String constructURI = null;
				String namespaceURI = null;
			
				/*Get the constructURI of this construct*/
				if (constr_element1 instanceof SuperAbstract) {
					logger.info("Construct is SA : " + constr_element1.getName());
					constructPropURI = constr_element1.getProperty("rdfTypeValue");
				} else {
					constructPropURI = constr_element1.getProperty("constructURI");
				}
				
				/*Get the constructURI string if there is none then "idk"*/
				if (constructPropURI == null) { 
					/*If URI is not available the use the string "idk"*/
					constructURI = "idk";
				} else {
					constructURI = constructPropURI.getValue();
				}
				
				logger.info("Construct constructURI is : " + constructURI);
				
				/*Get the namespace URI of this construct, which is attached to the construct as a Property*/
				constructPropNS = constr_element1.getProperty("namespaceURI");
				if (constructPropNS != null) { 
					namespaceURI = constructPropNS.getValue();
				} else {
					/*If URI is not available the use the string "idk"*/
					namespaceURI = "idk";
				}		
				
				logger.info("Construct namespaceURI is : " + namespaceURI);
				
				/*Check whether the URI exists in the SDBStore*/
				//boolean existsq = dereferenceURIAgentService.isGraphExists(namespaceURI);
			    //logger.info("Does exists (by ASK query): " + existsq);
			    
				boolean exists = dereferenceURIAgentService.isGraphExists(namespaceURI);
			    logger.info("Named graph with URI: " + namespaceURI + " | exists : " + exists);
				 
				/*Use the dereferencing agent to fetch the RDF data for that URI*/
			    ActionStatus statusSource = null;
				if (!exists && !namespaceURI.equals("idk")) {
					try {
					    logger.info("Dereferencing URI : " + constructURI);
					    statusSource = dereferenceURIAgentService.dereferenceURI(namespaceURI, constructURI);				
					    logger.info("Dereferencing status : " + statusSource.getStatus() + " , reason: " + statusSource.getReason());
					} catch (Exception exe) {
						logger.debug("Cannot retrieve URI: " + namespaceURI);
						dereferencedStatus = exe.getLocalizedMessage();
					}//end catch					
				}//end if			
	
				//logger.debug("constructName: " + constr_element1.getName() + " , URI: " + constructURI);	
				
				
				/*Property to hold the status of dereferencing because some URIs may not exists or for some 
				 * reason cannot be dereferenced, therefore we would like to monitor this process.*/
				if (namespaceURI.equals("idk")) {
					dereferencedStatus = "Construct's URI is empty";					
				} else if (exists) {
					dereferencedStatus = "Already dereferenced";
				} else if (statusSource != null ) {
					dereferencedStatus = statusSource.getReason();
				}
				
				CanonicalModelProperty statusProp = new CanonicalModelProperty("dereferenceStatus",dereferencedStatus);				
				constr_element1.addProperty(statusProp);				
				statusProp.setPropertyOf(constr_element1);				
			}//end for
				
			/*SCHEMA: Enrichment of schema 2 constructs*/
			for (CanonicalModelConstruct constr_element2 : constructs2) {		
				logger.info("In Schema >: " + schema2.getName());
				String dereferencedStatus = null;
				CanonicalModelProperty constructPropURI = null;
				CanonicalModelProperty constructPropNS = null;				
				String constructURI = null;
				String namespaceURI = null;

				/*Get the constructURI of this construct*/
				if (constr_element2 instanceof SuperAbstract) {
					logger.info("Construct is SA : " + constr_element2.getName());
					constructPropURI = constr_element2.getProperty("rdfTypeValue");
				} else {
					constructPropURI = constr_element2.getProperty("constructURI");
				}
				
				/*Get the constructURI string if there is none then "idk"*/
				if (constructPropURI == null) { 
					/*If URI is not available the use the string "idk"*/
					constructURI = "idk";
				} else {
					constructURI = constructPropURI.getValue();
				}
				
				logger.info("Construct constructURI is : " + constructURI);
				
				/*Get the namespace URI of this construct, which is attached to the construct as a Property*/
				constructPropNS = constr_element2.getProperty("namespaceURI");
				if (constructPropNS != null) { 
					namespaceURI = constructPropNS.getValue();
				} else {
					/*If URI is not available the use the string "idk"*/
					namespaceURI = "idk";
				}		
				
				logger.info("Construct namespaceURI is : " + namespaceURI);
				
				/*Check whether the URI exists in the SDBStore*/
				//boolean existsq = dereferenceURIAgentService.isGraphExists(namespaceURI);
			    //logger.info("Does exists (by ASK query): " + existsq);
			    
				boolean exists = dereferenceURIAgentService.isGraphExists(namespaceURI);
			    logger.info("Named graph with URI: " + namespaceURI + " | exists : " + exists);
				 
				/*Use the dereferencing agent to fetch the RDF data for that URI*/
			    ActionStatus statusTarget = null;
				if (!exists && !namespaceURI.equals("idk")) {
					try {
					    logger.info("Dereferencing URI : " + constructURI);
						statusTarget = dereferenceURIAgentService.dereferenceURI(namespaceURI, constructURI);				
					    logger.info("Dereferencing status : " + statusTarget.getStatus() + " , reason: " + statusTarget.getReason());
					} catch (Exception exe) {
						logger.debug("Cannot retrieve URI: " + namespaceURI);	
					}//end catch					
				}//end if				
				
				/*Property to hold the status of dereferencing because some URIs may not exists or for some 
				 * reason cannot be dereferenced, therefore we would like to monitor this process.*/
				if (namespaceURI.equals("idk")) {
					dereferencedStatus = "Construct URI is empty";					
				} else if (exists) {
					dereferencedStatus = "Already dereferenced";
				} else if (statusTarget != null ) {
					dereferencedStatus = statusTarget.getReason();
				}
				
				CanonicalModelProperty statusProp = new CanonicalModelProperty("dereferenceStatus",dereferencedStatus);				
				constr_element2.addProperty(statusProp);				
				statusProp.setPropertyOf(constr_element2);				
			}//end for			
		
			/*Record some statistics*/
			long endTime = System.nanoTime();
			logger.info("finished schema enrichment process: " + endTime);
			double duration = (endTime - startTime) / 1.0e9; 
			logger.info("total duration of schema enrichment process (in seconds): " + duration);		
			
			/*Close the metadata SDBStore to release any resources*/
			metaDataSDBStore.closeSDBStore();			
		}//end if		
	}//end schemaEnrichement	
	
	
	/***
	 * This method will be used to organise various semantic data from RDF vocabularies into a structure
	 * similar to a similarity matrix
	 * 	- agr0: list of source schema constructs
	 *  - arg1: list of target schema constructs
	 *  - arg2: a list of the semantic matrices we are looking to create
	 *  
	 * @param constructs
     * @return ArrayList<SemanticMatrix> - a 3D cube that organises all semantic matrices
	 */
	public List<SemanticMatrix> organiseMetadata(Schema sourceSchema, Schema targetSchema, List<SemanticMetadataService> semMatricesToCreate) {
		logger.debug("in organiseMetadata");
		logger.debug("sourceSchema: " + sourceSchema);
		logger.debug("targetSchema: " + targetSchema);	
		
		if (sourceSchema != null && targetSchema != null) {
		
			simCubeOfSemanticMatrices = new ArrayList<SemanticMatrix>(); 

			List<CanonicalModelConstruct> sourceConstructs = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> targetConstructs = this.getConstructs(targetSchema.getCanonicalModelConstructs());

			logger.debug("constructs1 : " + sourceConstructs.toString());
			logger.debug("constructs2 : " + targetConstructs.toString());
			
			/*Loop the list of semantic matrices and create them*/
		
			for (SemanticMetadataService matrix : semMatricesToCreate) {
				/*Generate HierarchySemanticMatrix*/
				if (matrix instanceof HierarchySemanticMatrix) {
					List<SemanticMatrix> result = ((HierarchySemanticMatrix) matrix).generateSemanticMatrices(sourceConstructs, targetConstructs);
					/* A HierarchySemanticMatrix may have 3 child semantic matrices or aggregated into only one using conflict resolution*/
					simCubeOfSemanticMatrices.addAll(result);
				} else if (matrix instanceof DomainSemanticMatrix) {
					SemanticMatrix domainMatrix = ((DomainSemanticMatrix) matrix).generateSemanticMatrix(sourceConstructs, targetConstructs);
					simCubeOfSemanticMatrices.add(domainMatrix);
				} else if (matrix instanceof RangeSemanticMatrix) {
					SemanticMatrix rangeMatrix = ((RangeSemanticMatrix) matrix).generateSemanticMatrix(sourceConstructs, targetConstructs);
					simCubeOfSemanticMatrices.add(rangeMatrix);					
				} else if (matrix instanceof NameSpaceSemMatrix) {
					SemanticMatrix nsMatrix = ((NameSpaceSemMatrix) matrix).generateSemanticMatrix(sourceConstructs, targetConstructs);
					simCubeOfSemanticMatrices.add(nsMatrix);					
				} 
			}//end for			

			/** Graphviz used to visualise the set of semantic matrices **/		
			graphvizDotGeneratorService.generateDOTSem(sourceConstructs, targetConstructs, simCubeOfSemanticMatrices);
			
			return simCubeOfSemanticMatrices;
		} else {
			return null;
		}
	}//end organiseMetadata()	
	
	
	/****
	 * New Strategy: Use Bayes to accumulate different types of evidences both Syntactic, given by
	 * syntactic matchers and Semantic, given by semantic annotation from namespaces
	 * 
	 * 
	 * 
	 * This matrix only accumulates evidences from syntactic matchers however the way I store the information
	 * about Bayes is optimal. The next matrix that will accumulate evidences from semantic evidences, will 
	 * just need to use the latestPrior from each entry. All pair of constructs start with a uniform prior.
	 * 
	 * 
	 * @param syntacticCube - contains the similarity matrix for each matcher along with the matcher instance 
	 * @throws IOException 
	 * 
	 */	
	public SemanticMatrix accumulateSyntacticEvidenceBayes(Schema sourceSchema, Schema targetSchema, List<MatcherInfo> syntacticCube,
																			Map<ControlParameterType, ControlParameter> controlParameters) throws IOException {
		logger.debug("in accumulateSyntacticEvidenceBayes()");
		
		//This matrix will hold the probabilities obtained from similarity scores for each matcher
		SemanticMatrix synBayesMatrix = null;
				
		if (sourceSchema != null && targetSchema != null && syntacticCube != null) {
			
			/* Get Schema constructs */
			List<CanonicalModelConstruct> sourceConstructs = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> targetConstructs = this.getConstructs(targetSchema.getCanonicalModelConstructs());	
			int rows = sourceConstructs.size();
			int columns = targetConstructs.size();
			//Create the matrix that will accumulate 
			synBayesMatrix = new SemanticMatrix(rows, columns);
			synBayesMatrix.setType(SemanticMatrixType.BAYES);
		
			
			//Create the entry of the semantic matrix
			for (CanonicalModelConstruct sourceConstruct : sourceConstructs) {		
				for (CanonicalModelConstruct targetConstruct : targetConstructs) {
					int rowIndex = sourceConstructs.indexOf(sourceConstruct);
					int colIndex = targetConstructs.indexOf(targetConstruct);
					
					//Create a new Bayes entry here, at the beginning with uniform pior
					BayesEntry entry = null;
					/** Assume a uniform PRIOR **/
					//double uniformPrior = 0.5;
					double uniformPrior = 0.5;
					
					entry = new BayesEntry(uniformPrior);					
					
					//For each similarity matrix in the Similarity Cube
					for (MatcherInfo info : syntacticCube) {
						
						/**
						 * Get the Probability Density Functions (PDFs)
						 */
						ProbabilityDensityFunction pdfTP = info.getMatcherService().getPdfTP();
						ProbabilityDensityFunction pdfFP = info.getMatcherService().getPdfFP();						
				
						//Get score at the certain position
						float simScore = info.getSimMatrix()[rowIndex][colIndex];						
						
						/***
						* SOS: consider using : approximateIntegralWithH() instead of specifying the width to 0.1
						*/
												
						//we calculate the integrals using the mid-point rule
						double likelihood    = pdfTP.approximateIntegralWithWidthSmart(simScore, 0.1);						
						double negLikelihood = pdfFP.approximateIntegralWithWidthSmart(simScore, 0.1);
						
						//logger.debug("likelihood[1]: " + likelihood);
						//logger.debug("negLikelihood[1]: " + negLikelihood);
			
						entry.setLikelihood(likelihood);
						entry.setNegLikelihood(negLikelihood);
						
						//Update the POSTERIOR in the presence of evidence
						entry.updatePosterior();	
						
						//The latest calculated posterior will be the new PRIOR for this entry
						entry.updatePrior(entry.getLastPosterior());						
					}//end syntacticSimCube
					
					//Save the Bayes entry in the Semantic Matrix
					logger.debug("Bayes entry is: " + entry);
					ArrayList<SemanticMatrixEntry> columnList = synBayesMatrix.getRow(rowIndex);
					columnList.add(colIndex, entry);					
					
				}//end for
			}//end for	
						
			/** Graphviz used to visualise the new Bayes Matrix (only Syntactic)**/		
			graphvizDotGeneratorService.generateDOTBayes(sourceConstructs, targetConstructs, synBayesMatrix, syntacticCube, null, true);	
			
		}//end if
		
		return synBayesMatrix;
	}//end accumulateSyntacticEvidenceBayes()
	
	
	/***
	 * To complete the Baye's approach this method is responsible for accumulating evidences from semantic 
	 * annotations. 
	 * 
	 * Because we have started from syntactic evidences with a uniform prior in this method 
	 * the priors are the latest Posteriors. The method has as an argument the semantic matrix created to capture
	 * syntactic evidences, and this method will build on that 
	 * 
	 * I will run the approach by using bayes to make the decision, but if the results are not logical 
	 * i may introduce some logic to when each evidence is considered.
	 * 
	 * 
	 * @param synBayesMatrix - a reference to the previous semantic matcher that accumulates evidence from syntactic matchers
	 *
	 */
	public SemanticMatrix accumulateSemEvidenceBayes(SemanticMatrix synBayesMatrix, Schema sourceSchema, Schema targetSchema, 
										List<SemanticMatrix> semanticCube, Map<BooleanVariables, ProbabilityMassFunction> pmfList,
										Set<BooleanVariables> evidencesToAccumulate, Map<ControlParameterType,
										ControlParameter> controlParameters) {
		
		logger.debug("in accumulateSemEvidenceBayes()");		
		logger.debug("pmfList: " + pmfList);
		
		if (sourceSchema != null && targetSchema != null && semanticCube != null) {

			/* Use comparator Class to sort List<SemanticMatrix> according to precedence */
			SemanticMatrixComparator comparator = new SemanticMatrixComparator();
			logger.info("Sorting semantic matrices according to precedence...");
			//logger.debug("semanticCube : " + semanticCube);
			Collections.sort(semanticCube, comparator);
			//logger.debug("semanticCube sorted : " + semanticCube);
			
			/* Get access to any control parameters */
			boolean onlySynSemCells = false;
			Set<SemanticMatrixCellIndex> indexesSet = null;		
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.ONLY_SYN_SEM_CELLS))) {
				onlySynSemCells = controlParameters.get(ControlParameterType.ONLY_SYN_SEM_CELLS).isBool();
				logger.info("onlySynSemCells: " + onlySynSemCells);
			}			
			
			/***
			 * When selection policy is: ONLY_SYN_SEM_CELLS we have the choice to choose the dumpening effect policy.
			 * SOME_EVIDENCE: CSP or CSN or (CSP and CSN)
			 * COMBINATION_OF_EVIDENCE: (CSP and CSN) - This is the right behaviour according to my supervisors
			 * Updated on: 06-03-14
			 */			
			DampeningEffectPolicy dep = null;
			if ((controlParameters != null) && (controlParameters.containsKey(ControlParameterType.DAMPENING_EFFECT_POLICY))) {
				ControlParameter controlParam = controlParameters.get(ControlParameterType.DAMPENING_EFFECT_POLICY);
				dep = controlParam.getDampeningEffectPolicy();
				logger.info("DAMPENING_EFFECT_POLICY: " + dep);
			}//end if					

			
			if (onlySynSemCells) {
				/* Store the index of this cell, to be used later to measure the error for the
				 * cells that have both syntactic and semantic evidences applied to them */
				indexesSet = null;
				indexesSet = new HashSet<SemanticMatrixCellIndex>();				
			}//end if
					
			
			/* Get Schema constructs */
			List<CanonicalModelConstruct> sourceConstructs = this.getConstructs(sourceSchema.getCanonicalModelConstructs());
			List<CanonicalModelConstruct> targetConstructs = this.getConstructs(targetSchema.getCanonicalModelConstructs());	

			
			for (CanonicalModelConstruct sourceConstruct : sourceConstructs) {		
				for (CanonicalModelConstruct targetConstruct : targetConstructs) {
					int rowIndex = sourceConstructs.indexOf(sourceConstruct);
					int colIndex = targetConstructs.indexOf(targetConstruct);
				
					BayesEntry entry = null;
					entry = (BayesEntry) synBayesMatrix.getCellSemanticEntry(rowIndex, colIndex);
				
					//For each semantic matrix in the semantic cube
					for (SemanticMatrix matrix : semanticCube) {
						//logger.debug("SemanticMatrixType: " + matrix.getType());
						SemanticMatrixEntry cell = matrix.getCellSemanticEntry(rowIndex, colIndex);
						
						if (cell != null) {
							Set<BooleanVariables> cellValueList = cell.getCellValue();							
							
							//Loop through each variable(s) from each semantic matrix and apply Baye's
							for (BooleanVariables bVar : cellValueList) {						
								/*Search whether the evidence can be accumulated - If the evidence is in the list 
								 of evidence to accumulate then proceed, otherwise do not accumulate*/
								if (evidencesToAccumulate.contains(bVar)) {
									logger.debug("BooleanVariable: " + bVar);
																		
									/**
									 * When the dampening policy is: SOME_EVIDENCE choose all the cells that have
									 * assimilated some evidence.
									 */
									if (onlySynSemCells) {
										if ( (dep != null) && (dep.equals(DampeningEffectPolicy.SOME_EVIDENCE)) ) {
											indexesSet.add(new SemanticMatrixCellIndex(rowIndex, colIndex));
										}
									}//end if
								
									//Assimilate the evidences
									if (bVar.equals(BooleanVariables.CSURI) || bVar.equals(BooleanVariables.PSURI)) {
										entry.setLastPosterior(1.0);
										entry.updatePrior(entry.getLastPosterior());
										
										//Add semantic evidence to history for this cell
										entry.addSemEvidenceInHistory(bVar);										
									} else {
															
										//Get the probability mass function for this cell according to its Boolean Variable
										ProbabilityMassFunction pmf = pmfList.get(bVar);
										if (pmf != null) {
											logger.debug("Contingency table for: " + pmf.getEvidenceVariable()); 
											
											logger.debug("Contingency table: " + pmf.toString());
								
											//("Pr(Evidence | Equiv)";	
											double likelihood    = 	pmf.getContingencyTable().Pr(bVar.toString(), "Equiv");					
											double negLikelihood =	pmf.getContingencyTable().Pr(bVar.toString(), "NoEquiv");
											
											logger.debug("likelihood: " + likelihood); 
											logger.debug("negLikelihood: " + negLikelihood); 
								
											entry.setLikelihood(likelihood);
											entry.setNegLikelihood(negLikelihood);
								
											//Update the POSTERIOR in the presence of evidence
											entry.updatePosterior();	
								
											//The latest calculated posterior will be the new PRIOR for this entry
											entry.updatePrior(entry.getLastPosterior());
											
											//Add semantic evidence to history for this cell
											entry.addSemEvidenceInHistory(bVar);
										}
									}//end else
								
									/**
									 * When the dampening policy is: COMBINATION_OF_EVIDENCE choose the cells that have
									 * assimilated that particular combination of evidence. In doing so I have created 
									 * the getHistoryOfSemEvidence() that keeps track for each cell in the matrix which
									 * semantic evidence the cell has assimilated.
									 */
									if (onlySynSemCells) {
										if ( (dep != null) &&  (dep.equals(DampeningEffectPolicy.COMBINATION_OF_EVIDENCE)) ) {
											if (evidencesToAccumulate.equals(entry.getHistoryOfSemEvidence())) {									
												indexesSet.add(new SemanticMatrixCellIndex(rowIndex, colIndex));
											}
										}//end if								
									}//end onlySynSemCells if
								}//end if
							}//end for
						}//end if						
					}//end for				
				}//end for
			}//end for
			
			
			//Save the set of cell indexes that have assimilated both syntactic and semantic evidences
			if (indexesSet != null) {
				synBayesMatrix.attachIndexesSet(indexesSet);
			}			
			
			/** Graphviz used to visualise the new Bayes Matrix (both Syntactic & Semantic)**/		
			graphvizDotGeneratorService.generateDOTBayes(sourceConstructs, targetConstructs, synBayesMatrix, null, evidencesToAccumulate, true);			
					
		}//end if
		
		//This matrix returns an updated version of the synBayesMatrix		
		return synBayesMatrix;
	}//end accumulateSemEvidenceBayes()	
	
	
		

	//TODO codeduplication from InstanceBasedMatcherServiceImpl
	private List<SuperAbstract> getSuperAbstracts(List<CanonicalModelConstruct> constructs) {
		logger.debug("in getSuperAbstracts");
		List<SuperAbstract> superAbstracts = new ArrayList<SuperAbstract>();
		for (CanonicalModelConstruct construct : constructs) {
			if (construct.getTypeOfConstruct() == ConstructType.SUPER_ABSTRACT) {
				superAbstracts.add((SuperAbstract) construct);
				logger.debug("got superAbstract: " + construct.getName());
			}
		}
		return superAbstracts;
	}
	
	private void saveMatches(List<Matching> matches) {
		logger.info("in saveMatches");
		logger.info("matches.size(): " + matches.size());
		for (Matching matching : matches)
			matchingService.addMatching(matching);
	}

	//NOTE: This is the reason where SUPER_RELATIONSHIPS are missing from when importing the RDF inferred schema
	//the method below ignores them
	private ArrayList<CanonicalModelConstruct> getConstructs(Set<CanonicalModelConstruct> inputConstructs) {
		logger.debug("in getConstructs");
		ArrayList<CanonicalModelConstruct> outputConstructs = new ArrayList<CanonicalModelConstruct>();

		/*Do not include SuperRelationships, why? - anw in RDF this is ignored*/
		for (CanonicalModelConstruct inputConstruct : inputConstructs) {
			//logger.debug("inputConstruct.getSchema.getName: " + inputConstruct.getSchema().getName());
			if (!inputConstruct.getTypeOfConstruct().equals(ConstructType.SUPER_RELATIONSHIP)) {
				//logger.debug("inputConstruct.type: " + inputConstruct.getTypeOfConstruct());				
				//logger.debug("outputConstructs.size: " + outputConstructs.size());
				outputConstructs.add(inputConstruct);
				//logger.debug("added inputConstruct.name: " + inputConstruct.getName());
				//logger.debug("outputConstructs.size: " + outputConstructs.size());
			} 
		}
		return outputConstructs;
	}

	@Transactional
	public Schema merge(Schema schema1, Schema schema2, Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2) {

		this.schema1 = schema1;
		this.schema2 = schema2;
		this.schematicCorrespondencesBetweenSchema1AndSchema2 = schematicCorrespondencesBetweenSchema1AndSchema2;
		this.mergedSchema = new Schema("mergedSchema", null);
		this.schematicCorrespondencesBetweenSchema1AndMergedSchema = new HashSet<SchematicCorrespondence>();
		this.schematicCorrespondencesBetweenSchema2AndMergedSchema = new HashSet<SchematicCorrespondence>();

		//List<CanonicalModelConstruct> tempConstSet1 = new ArrayList<CanonicalModelConstruct>(schema1.getCanonicalModelConstructs());
		//List<CanonicalModelConstruct> tempConstSet2 = new ArrayList<CanonicalModelConstruct>(schema2.getCanonicalModelConstructs());

		List<SchematicCorrespondence> tempSCSet = new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2);
		// for each sa-to-sa correspondences sc
		//if sc is o2o
		for (int i = 0; i < tempSCSet.size(); i++) {
			SchematicCorrespondence sc = tempSCSet.get(i);
			if (sc.getCardinalityType() == CardinalityType.MANY_TO_MANY || sc.getCardinalityType() == CardinalityType.ONE_TO_MANY
					|| sc.getCardinalityType() == CardinalityType.MANY_TO_ONE) {
				if (sc.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT) {
					Set<CanonicalModelConstruct> constructs1 = sc.getConstructs1();
					Set<CanonicalModelConstruct> constructs2 = sc.getConstructs2();
					/*
					List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> appSet = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
							sc.getApplicationOfSchematicCorrespondenceToConstructs());
					*/
					boolean hasParent = false;
					for (CanonicalModelConstruct construct : constructs1) {
						if (construct instanceof SuperAbstract) {
							if (((SuperAbstract) construct).getParentSuperAbstract() != null)
								hasParent = true;
						}
					}
					for (CanonicalModelConstruct construct : constructs2) {
						if (construct instanceof SuperAbstract) {
							if (((SuperAbstract) construct).getParentSuperAbstract() != null)
								hasParent = true;
						}
					}
					
					if (!hasParent)
						reconcileM2MSACR(sc);
					//TODO sort out reconcile when parent was found
				}

			}
		}
		// for each sa-to-sa correspondences sc
		//if sc is o2o
		for (int i = 0; i < tempSCSet.size(); i++) {
			SchematicCorrespondence sc = tempSCSet.get(i);
			if (sc.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
				if (sc.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT) {
					Set<CanonicalModelConstruct> constructs1 = sc.getConstructs1();
					Set<CanonicalModelConstruct> constructs2 = sc.getConstructs2();
					/*
					List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> appSet = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
							sc.getApplicationOfSchematicCorrespondenceToConstructs());
					*/
					boolean hasParent = false;
					for (CanonicalModelConstruct construct : constructs1) {
						if (construct instanceof SuperAbstract) {
							if (((SuperAbstract) construct).getParentSuperAbstract() != null)
								hasParent = true;
						}
					}
					for (CanonicalModelConstruct construct : constructs2) {
						if (construct instanceof SuperAbstract) {
							if (((SuperAbstract) construct).getParentSuperAbstract() != null)
								hasParent = true;
						}
					}
					
					if (!hasParent) {
						reconcileO2OSACR(sc);
					}
				}

			}
		}
		// for each sa in schema 1 not in any correspondences and sa has no parents
		List<CanonicalModelConstruct> saTempSet = new ArrayList<CanonicalModelConstruct>(schema1.getCanonicalModelConstructs());
		for (int i = 0; i < saTempSet.size(); i++) {
			if (saTempSet.get(i) instanceof SuperAbstract) {
				SuperAbstract saLonely1 = (SuperAbstract) saTempSet.get(i);
				if (findCorrespondingSuperAbstracts(saLonely1, schema2).size() == 0
						&& findCorrespondingSuperAbstracts(saLonely1, mergedSchema).size() == 0 && saLonely1.getParentSuperAbstract() == null) {
					// create merged sa
					SuperAbstract sa_merged = new SuperAbstract("from::" + saLonely1.getSchema().getName() + ":" + saLonely1.getName(), mergedSchema);
					//sa_merged.setId(15);
					sa_merged.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
					mergedSchema.addCanonicalModelConstruct(sa_merged);
					// create 1-merge corr
					SchematicCorrespondence sa1ToMerge = new SchematicCorrespondence("o2oSA2SA", "DNSC",
							SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
					//sa1ToMerge.setId(15);
					schematicCorrespondencesBetweenSchema1AndMergedSchema.add(sa1ToMerge);
					sa1ToMerge
							.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
					sa1ToMerge.setDirection(DirectionalityType.FIRST_TO_SECOND);
					sa1ToMerge.setCardinalityType(CardinalityType.ONE_TO_ONE);
					sa1ToMerge.addConstruct1(saLonely1);
					sa1ToMerge.addConstruct2(sa_merged);
					
					cloneSA(saLonely1, sa_merged, null, 1);
				}
			}
		}

		// for each sa in schema 2 not in any correspondences and sa has no parents
		saTempSet = new ArrayList<CanonicalModelConstruct>(schema2.getCanonicalModelConstructs());
		for (int i = 0; i < saTempSet.size(); i++) {
			if (saTempSet.get(i) instanceof SuperAbstract) {
				SuperAbstract saLonely2 = (SuperAbstract) saTempSet.get(i);
				if (findCorrespondingSuperAbstracts(saLonely2, schema1).size() == 0
						&& findCorrespondingSuperAbstracts(saLonely2, mergedSchema).size() == 0 && saLonely2.getParentSuperAbstract() == null) {
					// create merged sa
					SuperAbstract sa_merged = new SuperAbstract("from::" + saLonely2.getSchema().getName() + ":" + saLonely2.getName(), mergedSchema);
					//sa_merged.setId(15);
					sa_merged.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
					mergedSchema.addCanonicalModelConstruct(sa_merged);
					// create 1-merge corr
					SchematicCorrespondence sa2ToMerge = new SchematicCorrespondence("o2oSA2SA", "DNSC",
							SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
					//sa2ToMerge.setId(15);
					schematicCorrespondencesBetweenSchema2AndMergedSchema.add(sa2ToMerge);
					sa2ToMerge
							.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
					sa2ToMerge.setDirection(DirectionalityType.FIRST_TO_SECOND);
					sa2ToMerge.setCardinalityType(CardinalityType.ONE_TO_ONE);
					sa2ToMerge.addConstruct1(saLonely2);
					sa2ToMerge.addConstruct2(sa_merged);
					
					cloneSA(saLonely2, sa_merged, null, 2);
				}
			}
		}
		// for each sr in schema 1
		List<CanonicalModelConstruct> tempConst = new ArrayList<CanonicalModelConstruct>(schema1.getCanonicalModelConstructs());
		for (int i = 0; i < tempConst.size(); i++) {
			if (tempConst.get(i) instanceof SuperRelationship) {
				SuperRelationship sr = (SuperRelationship) tempConst.get(i);
				if (sr.getSuperRelationshipType() == SuperRelationshipType.GENERALISATION) {
					propagatingGeneralisationSr(sr);
				} else if (sr.getSuperRelationshipType() == SuperRelationshipType.REFERENCE) {
					propagatingReferenceSr(sr);
				}
			}
		}
		// for each sr in schema 2
		tempConst = new ArrayList<CanonicalModelConstruct>(schema2.getCanonicalModelConstructs());
		for (int i = 0; i < tempConst.size(); i++) {
			if (tempConst.get(i) instanceof SuperRelationship) {
				SuperRelationship sr = (SuperRelationship) tempConst.get(i);
				if (sr.getSuperRelationshipType() == SuperRelationshipType.GENERALISATION) {
					propagatingGeneralisationSr(sr);
				} else if (sr.getSuperRelationshipType() == SuperRelationshipType.REFERENCE) {
					propagatingReferenceSr(sr);
				}
			}
		}

		return mergedSchema;
	}

	public Set<SchematicCorrespondence> diff(Schema schema1, Schema schema2,
			Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2) {

		this.schema1 = schema1;
		this.schema2 = schema2;
		this.schematicCorrespondencesBetweenSchema1AndSchema2 = schematicCorrespondencesBetweenSchema1AndSchema2;
		superabstractsOfSchema1MissingInSchema2 = new HashSet<SuperAbstract>();
		superabstractsOfSchema2MissingInSchema1 = new HashSet<SuperAbstract>();
		schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2 = new HashSet<SchematicCorrespondence>();

		// Step 1: retrieve superabstracts or superlexicals do not participate in any correspondences 
		// (currently except superrelationship)
		//
		List<CanonicalModelConstruct> consSet1 = new ArrayList<CanonicalModelConstruct>(schema1.getCanonicalModelConstructs());
		List<CanonicalModelConstruct> consSet2 = new ArrayList<CanonicalModelConstruct>(schema2.getCanonicalModelConstructs());
		List<SchematicCorrespondence> scSet = new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2);
		List<SuperAbstract> matchSuperAbstractsOfSchema2 = new ArrayList<SuperAbstract>();
		for (int i = 0; i < consSet1.size(); i++) {
			if (consSet1.get(i) instanceof SuperAbstract) {
				SuperAbstract sa1 = (SuperAbstract) consSet1.get(i);
				boolean foundMatchSa = false;
				for (int j = 0; j < consSet2.size(); j++) {
					if (consSet2.get(j) instanceof SuperAbstract) {
						SuperAbstract sa2 = (SuperAbstract) consSet2.get(j);
						for (int k = 0; k < scSet.size(); k++) {
							SchematicCorrespondence sc = scSet.get(k);
							if (participateInSaSC(sa1, sa2, sc)) {
								foundMatchSa = true;
								if (!matchSuperAbstractsOfSchema2.contains(sa2))
									matchSuperAbstractsOfSchema2.add(sa2);
								// to find missing lexicals
								List<SuperLexical> lexSet1 = new ArrayList<SuperLexical>(sa1.getSuperLexicals());
								List<SuperLexical> lexSet2 = new ArrayList<SuperLexical>(sa2.getSuperLexicals());
							}
							if (sc.getSchematicCorrespondenceType() == SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT) {
								if (!schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2.contains(sc))
									schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2.add(sc);
							} else if (sc.getSchematicCorrespondenceType() == SchematicCorrespondenceType.MISSING_SUPER_LEXICAL) {
								if (!schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2.contains(sc))
									schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2.add(sc);
							}
						}
					}
				}
				if (!foundMatchSa)
					this.addSuperAbstractsMissingIn2(sa1);
			}
		}
		for (int i = 0; i < consSet2.size(); i++) {
			if (consSet2.get(i) instanceof SuperAbstract)
				if (!matchSuperAbstractsOfSchema2.contains(consSet2.get(i))) {
					this.addSuperAbstractsMissingIn1((SuperAbstract) consSet2.get(i));
				}
		}// end Step 1

		return schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2;
	}

	public Set<Mapping> viewGen(Set<SchematicCorrespondence> schematicCorrespondencesBetweenSchema1AndSchema2) {

		this.schematicCorrespondencesBetweenSchema1AndSchema2 = schematicCorrespondencesBetweenSchema1AndSchema2;
		this.mappingsBetweenSchema1AndSchema2 = new HashSet<Mapping>();

		List<SchematicCorrespondence> scSet = new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2);
		for (int i = 0; i < scSet.size(); i++) {
			SchematicCorrespondence sc = scSet.get(i);
			if (sc.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT) {
				if (sc.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
					mappingsBetweenSchema1AndSchema2.add(createViewForOtOSACorrespondence(sc));
					break; //Ive: I did not understand this break, but I did not delete, because I am waiting to understand better the code.
				} else if (sc.getCardinalityType() == CardinalityType.ONE_TO_MANY || sc.getCardinalityType() == CardinalityType.MANY_TO_MANY
						|| sc.getCardinalityType() == CardinalityType.MANY_TO_ONE) {
					mappingsBetweenSchema1AndSchema2.addAll(createViewForM2MSACorrespondences(sc));
					break;
				}

			}
		}

		return mappingsBetweenSchema1AndSchema2;
	}

	//-----------------------viewGen

	private Set<Mapping> createViewForM2MSACorrespondences(SchematicCorrespondence sc) {
		if (sc.getSchematicCorrespondenceType() == SchematicCorrespondenceType.VERTICAL_PARTITIONING) {
			return createViewForVPSACorrespondences(sc);
		} else if (sc.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_PARTITIONING) {
			return createViewForHPSACorrespondences(sc);
		} else if (sc.getSchematicCorrespondenceType() == SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING) {
			return createViewForHVPSACorrespondences(sc);
		}
		return null;
	}

	private Set<Mapping> createViewForVPSACorrespondences(SchematicCorrespondence sc) {
		Set<Mapping> mappingSet = new HashSet();
		
		Set<CanonicalModelConstruct> constructsSource = sc.getConstructs1();
		Set<CanonicalModelConstruct> constructsTarget = sc.getConstructs2();
		
		// Retrieve left and right selection predicates
		List<ReconcilingExpression> spre_set_1 = getSelPredicates(sc.getReconcilingExpressions1());
		ReconcilingExpression spre_1 = null;
		if (spre_set_1.size() > 0 && spre_set_1.get(0) != null)
			spre_1 = spre_set_1.get(0);
		List<ReconcilingExpression> spre_set_2 = getSelPredicates(sc.getReconcilingExpressions2());
		ReconcilingExpression spre_2 = null;
		if (spre_set_2.size() > 0 && spre_set_2.get(0) != null)
			spre_2 = spre_set_2.get(0);
		// Retrieve head of the join left and right
		SuperAbstract headOfTheJoin1 = null;
		List<SuperAbstract> source_sa_set = new ArrayList<SuperAbstract>();
		SuperAbstract headOfTheJoin2 = null;
		List<SuperAbstract> target_sa_set = new ArrayList<SuperAbstract>();
		for (CanonicalModelConstruct sourceConstruct : constructsSource)
			if (sourceConstruct instanceof SuperAbstract)
				headOfTheJoin1 = (SuperAbstract) sourceConstruct;
		for (CanonicalModelConstruct targetConstruct : constructsTarget)
			if (targetConstruct instanceof SuperAbstract)
				headOfTheJoin2 = (SuperAbstract) targetConstruct;
		
		source_sa_set.add(0, headOfTheJoin1);
		//for (int i = 0; i < app_Source.size(); i++) {
		for (CanonicalModelConstruct sourceConstruct : constructsSource) {
			//	ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app_Temp = app_Source.get(i);
			//if (app_Temp.getCanonicalModelConstruct() instanceof SuperRelationship) {
			if (sourceConstruct instanceof SuperRelationship) {
				//SuperRelationship sr_temp = (SuperRelationship) app_Temp.getCanonicalModelConstruct();
				SuperRelationship sr_temp = (SuperRelationship) sourceConstruct;
				if (sr_temp.getSuperRelationshipType() == SuperRelationshipType.FOREIGNKEY) {
					List<ParticipationOfCMCInSuperRelationship> par_set = new ArrayList<ParticipationOfCMCInSuperRelationship>(
							sr_temp.getParticipationsOfConstructs());
					for (int j = 0; j < par_set.size(); j++) {
						if (par_set.get(j).getCanonicalModelConstruct() != headOfTheJoin1) {
							source_sa_set.add((SuperAbstract) par_set.get(j).getCanonicalModelConstruct());
						}
					}
				}
			}
		}
		target_sa_set.add(0, headOfTheJoin2);
		//for (int i = 0; i < app_Target.size(); i++) {
		for (CanonicalModelConstruct targetConstruct : constructsTarget) {
			//ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct app_Temp = app_Target.get(i);
			//if (app_Temp.getCanonicalModelConstruct() instanceof SuperRelationship) {
			if (targetConstruct instanceof SuperRelationship) {
				//SuperRelationship sr_temp = (SuperRelationship) app_Temp.getCanonicalModelConstruct();
				SuperRelationship sr_temp = (SuperRelationship) targetConstruct;
				if (sr_temp.getSuperRelationshipType() == SuperRelationshipType.FOREIGNKEY) {
					List<ParticipationOfCMCInSuperRelationship> par_set = new ArrayList<ParticipationOfCMCInSuperRelationship>(
							sr_temp.getParticipationsOfConstructs());
					for (int j = 0; j < par_set.size(); j++) {
						if (par_set.get(j).getCanonicalModelConstruct() != headOfTheJoin2) {
							target_sa_set.add((SuperAbstract) par_set.get(j).getCanonicalModelConstruct());
						}
					}
				}
			}
		}
		// Begin constructing mappings from source to target =========================================
		// create source join array
		List<ReconcilingExpression> jpre_set_1 = getJoinPredicates(sc.getReconcilingExpressions1());
		MappingOperator rootJoin1 = null;
		//if (app_Source.size() == 1)
		if (constructsSource.size() == 1)
			rootJoin1 = new ScanOperator(headOfTheJoin1, spre_1.getExpression());
		// retrieve join predicate from sc
		if (jpre_set_1.size() != 0) {
			ReconcilingExpression re = jpre_set_1.get(0);
			SuperAbstract joinReight = null;
			if (headOfTheJoin1 == re.getJoinPred1())
				joinReight = re.getJoinPred2();
			else if (headOfTheJoin1 == re.getJoinPred2())
				joinReight = re.getJoinPred1();
			MappingOperator scanLeft = new ScanOperator(headOfTheJoin1, spre_1.getExpression());
			MappingOperator scanRight = new ScanOperator(joinReight);
			rootJoin1 = new JoinOperator(scanLeft, scanRight, re.getExpression());
		}
		// create join array
		for (int i = 1; i < jpre_set_1.size(); i++) {
			ReconcilingExpression re = jpre_set_1.get(i);
			SuperAbstract joinReight = null;
			if (headOfTheJoin1 == re.getJoinPred1())
				joinReight = re.getJoinPred2();
			else if (headOfTheJoin1 == re.getJoinPred2())
				joinReight = re.getJoinPred1();
			MappingOperator scanRight = new ScanOperator(joinReight);
			rootJoin1 = new JoinOperator(rootJoin1, scanRight, re.getExpression());
		}

		Query q1 = new Query();
		Mapping m = new Mapping();
		Query q2 = new Query();
		MappingOperator scan_target = new ScanOperator(headOfTheJoin2);
		q2.setRootOperator(scan_target);
		String sourceProjectionList = getProjectionList(source_sa_set, headOfTheJoin2, new ArrayList<SchematicCorrespondence>(
				schematicCorrespondencesBetweenSchema1AndSchema2));
		MappingOperator reduce_op_source = new ReduceOperator(rootJoin1, sourceProjectionList);
		q1.setRootOperator(reduce_op_source);
		m.setQuery1(q1);
		m.setQuery2(q2);
		mappingSet.add(m);
		for (int i = 0; i < target_sa_set.size(); i++) {
			if (target_sa_set.get(i) != headOfTheJoin2) {
				Mapping m2 = new Mapping();
				q1 = new Query();
				q2 = new Query();
				scan_target = new ScanOperator(target_sa_set.get(i));
				q2.setRootOperator(scan_target);
				String sourceProjectionList2 = getProjectionList(source_sa_set, target_sa_set.get(i), new ArrayList<SchematicCorrespondence>(
						schematicCorrespondencesBetweenSchema1AndSchema2));
				reduce_op_source = new ReduceOperator(rootJoin1, sourceProjectionList2);
				q1.setRootOperator(reduce_op_source);
				m2.setQuery1(q1);
				m2.setQuery2(q2);
				mappingSet.add(m2);
			}
		}
		// End constructing mappings from source to target ===========================================

		// Begin constructing mappings from target to source =========================================
		// create target join array
		List<ReconcilingExpression> jpre_set_2 = getJoinPredicates(sc.getReconcilingExpressions2());
		MappingOperator rootJoin2 = null;
		//if (app_Target.size() == 1)
		if (constructsTarget.size() == 1)
			rootJoin2 = new ScanOperator(headOfTheJoin2, spre_2.getExpression());
		if (jpre_set_2.size() != 0) {
			ReconcilingExpression re = jpre_set_2.get(0);
			SuperAbstract joinReight = null;
			if (headOfTheJoin2 == re.getJoinPred1())
				joinReight = re.getJoinPred2();
			else if (headOfTheJoin2 == re.getJoinPred2())
				joinReight = re.getJoinPred1();
			MappingOperator scanLeft = new ScanOperator(headOfTheJoin2, spre_2.getExpression());
			MappingOperator scanRight = new ScanOperator(joinReight);
			rootJoin2 = new JoinOperator(scanLeft, scanRight, re.getExpression());
		}
		for (int i = 1; i < jpre_set_2.size(); i++) {
			ReconcilingExpression re = jpre_set_2.get(i);
			SuperAbstract joinReight = null;
			if (headOfTheJoin1 == re.getJoinPred1())
				joinReight = re.getJoinPred2();
			else if (headOfTheJoin1 == re.getJoinPred2())
				joinReight = re.getJoinPred1();
			MappingOperator scanRight = new ScanOperator(joinReight);
			rootJoin2 = new JoinOperator(rootJoin2, scanRight, re.getExpression());
		}
		m = new Mapping();
		q2 = new Query();
		q1 = new Query();
		MappingOperator scan_source = new ScanOperator(headOfTheJoin1);
		q1.setRootOperator(scan_source);
		String targetProjectionList = getProjectionList(target_sa_set, headOfTheJoin1, new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2));
		MappingOperator reduce_op_target = new ReduceOperator(rootJoin2, targetProjectionList);
		q2.setRootOperator(reduce_op_target);
		m.setQuery1(q1);
		m.setQuery2(q2);
		mappingSet.add(m);
		for (int i = 0; i < source_sa_set.size(); i++) {
			if (source_sa_set.get(i) != headOfTheJoin1) {
				Mapping m2 = new Mapping();
				q1 = new Query();
				q2 = new Query();
				scan_source = new ScanOperator(source_sa_set.get(i));
				q1.setRootOperator(scan_source);
				targetProjectionList = getProjectionList(target_sa_set, source_sa_set.get(i), new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2));
				reduce_op_target = new ReduceOperator(rootJoin2, targetProjectionList);
				q2.setRootOperator(reduce_op_target);
				m2.setQuery1(q1);
				m2.setQuery2(q2);
				mappingSet.add(m2);
			}
		}
		// End constructing mappings from target to source ===========================================
		return mappingSet;
	}

	private String getProjectionList(List<SuperAbstract> sourceSet, SuperAbstract targetSa, List<SchematicCorrespondence> scSet) {
		String sourceProjectionList = "";
		List<SuperLexical> target_lex_set = new ArrayList<SuperLexical>(targetSa.getSuperLexicals());
		for (int i = 0; i < target_lex_set.size(); i++) {
			SuperLexical lex_target = target_lex_set.get(i);
			boolean hasCorrespLex = false;
			for (int j = 0; j < sourceSet.size(); j++) {
				SuperAbstract sa_source = sourceSet.get(j);
				List<SuperLexical> source_lex_set = new ArrayList<SuperLexical>(sa_source.getSuperLexicals());
				for (int k = 0; k < source_lex_set.size(); k++) {
					SuperLexical lex_source = source_lex_set.get(k);
					for (int m = 0; m < scSet.size(); m++) {
						SchematicCorrespondence sc = scSet.get(m);
						if (participateInLexSC(lex_source, lex_target, sc)) {
							if (i == target_lex_set.size() - 1)
								sourceProjectionList += lex_source.getName();
							else
								sourceProjectionList += lex_source.getName() + ", ";
							hasCorrespLex = true;
							break;
						}
					}
					if (hasCorrespLex)
						break;
				}
				if (hasCorrespLex)
					break;
			}
			if (!hasCorrespLex)
				if (i == target_lex_set.size() - 1)
					sourceProjectionList += "'null'";
				else
					sourceProjectionList += "'null', ";

		}
		return sourceProjectionList;
	}

	private Set<Mapping> createViewForHPSACorrespondences(SchematicCorrespondence sc) {
		Set<Mapping> mappingSet = new HashSet<Mapping>();
		
	/*	CanonicalModelConstruct sourceConstruct = sc.getConstructs1().iterator().next();
		CanonicalModelConstruct targetConstruct = sc.getConstructs2().iterator().next();
		
		List<SchematicCorrespondence> sc_Set = new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2);
		
	 	//Analyse the schematic correspondence and from the directions create a query of mapping.
	
		String sourceProjectList = "";
		String mappingString="";
		System.out.println("in mappingOneToOne :"+sc_Set);
		
		SuperAbstract sa_source = (SuperAbstract) sourceConstruct;
		SuperAbstract sa_target = (SuperAbstract) targetConstruct;
		
		String globalSchema = sa_target.getName();
        String fromSchema = sa_source.getName();
        String alias = fromSchema.substring(fromSchema.indexOf(".")+1,fromSchema.indexOf(".")+2);
                 
        mappingString = globalSchema+" SELECT ";
        int i = 0;
        for (SchematicCorrespondence childsc:sc_Set.iterator().next().getChildSchematicCorrespondences()){
						
        	   String shortName=childsc.getShortName();
		       System.out.println("mapping shortname=" + shortName);
		       System.out.println("mappin direction=" + childsc.getDirection().toString());
		       String[] s =shortName.split("_");
		      
		       if (!childsc.getDirection().equals(DirectionalityType.FIRST_TO_SECOND)){
		      
		            String type = "";
		            String asName = "";
		            String fieldName = "";
		            
		            type = s[0];
		            asName = s[1];
		            	
		            if (i>0){
		            	mappingString += ", ";
		            	sourceProjectList += ", ";
		            }
		            if(!type.equals("MSL")){
		               	fieldName = s[2];
		               	sourceProjectList += s[2];
		            	mappingString += alias+"."+fieldName+" as "+asName;
		            }else{
		            	asName = asName.substring(asName.lastIndexOf(".")+1);
		            	mappingString += "Null as "+asName;
		            	sourceProjectList += "Null";
		            }
		      
		        }
		        i++;
		}
				
		mappingString+=" FROM "+fromSchema+" "+alias;
		System.out.println("mappingString:"+mappingString);
	      
		Query q1 = new Query();
		Query q2 = new Query();
		
		q1.setQueryString(mappingString);
		q2.setQueryString(mappingString);
		Mapping m = new Mapping();
		m.setQuery1(q1);
		m.setQuery2(q2);
		m.setQuery1String(mappingString);
		m.addConstruct1(sourceConstruct);
		m.addConstruct2(targetConstruct);*/
		
		/*
		Set<CanonicalModelConstruct> constructsSource = sc.getConstructs1();
		Set<CanonicalModelConstruct> constructsTarget = sc.getConstructs2();
		
		List<SuperAbstract> source_sa_set = new ArrayList<SuperAbstract>();
		List<SuperAbstract> target_sa_set = new ArrayList<SuperAbstract>();
		
		List<SuperLexical> source_sl_set = new ArrayList<SuperLexical>();
		List<SuperLexical> target_sl_set = new ArrayList<SuperLexical>();
		
		for (CanonicalModelConstruct sourceConstruct : constructsSource) {
	
			if (sourceConstruct instanceof SuperAbstract)
				source_sa_set.add((SuperAbstract) sourceConstruct);

		}
		
		for (CanonicalModelConstruct targetConstruct : constructsTarget) {
			
			if (targetConstruct instanceof SuperAbstract)
				target_sa_set.add((SuperAbstract) targetConstruct);
			
		}
		
		for (SchematicCorrespondence scChild : sc.getChildSchematicCorrespondences()) {
			
			for (CanonicalModelConstruct cm : scChild.getConstructs1()) {
				
				if (cm instanceof SuperLexical)
					source_sl_set.add((SuperLexical) cm);
			}
		}
		
		HashMap<SuperLexical, ArrayList<String>> source = new HashMap<SuperLexical, ArrayList<String>>();
		List<ArrayList<String>> lstArray = new ArrayList<ArrayList<String>>();
		ArrayList<String> lstInstances1 = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7"));
		ArrayList<String> lstInstances2 = new ArrayList<String>(Arrays.asList("1","10","9","1","10","9","10"));
		ArrayList<String> lstInstances3 = new ArrayList<String>(Arrays.asList("N","N","Y","Y","Y","Y","N"));
		ArrayList<String> lstInstances4 = new ArrayList<String>(Arrays.asList("3","3","3","3","3","3","3"));
		ArrayList<String> lstInstances5 = new ArrayList<String>(Arrays.asList("4","3","3","3","3","3","3"));
		lstArray.add(lstInstances1);
		lstArray.add(lstInstances2);
		lstArray.add(lstInstances3);
		lstArray.add(lstInstances4);
		lstArray.add(lstInstances5);
		
		int i = 0;
		for (SuperLexical source_sl : source_sl_set) {
			ArrayList<String> lstInstances = lstArray.get(i);
			source.put(source_sl, lstInstances);
			i++;
		}
		
		//this.createConditionHP(source);
		*/
		return mappingSet;
	}
	
	public void createConditionHP(HashMap<SuperLexical, ArrayList<String>> source){
		
		//Map<column, instances, instance_quantity>
		HashMap<SuperLexical, HashMap<String, Integer>> histogram = new HashMap<SuperLexical, HashMap<String, Integer>>();
		
		//get the data(attributes and their respective instances)
		for (Entry<SuperLexical, ArrayList<String>> lstInstance : source.entrySet()) {
		
			HashMap<String, Integer> instancesQuantity = new HashMap<String, Integer>();
			
			//counts the repetition number for each instance
			for (String instance : lstInstance.getValue()) {
				if(instancesQuantity == null || !instancesQuantity.containsKey(instance)){
					instancesQuantity.put(instance, 1);
				}else{
					Integer aux = instancesQuantity.get(instance);
					aux++;
					instancesQuantity.put(instance, aux);
				}
			}
			
			histogram.put(lstInstance.getKey(), instancesQuantity);
		}
		
		//for while we don't have a logic to choose the partitioning, that's why is random ;)
		Random random = new Random();
		List<SuperLexical> keys = new ArrayList<SuperLexical>(histogram.keySet());
		SuperLexical randomKey = keys.get( random.nextInt(keys.size()) );
		HashMap<String, Integer> value = histogram.get(randomKey);
		
		List<String> lstConditions = new ArrayList<String>();
		//the partition chosen needs to have 2 or more sub partitioning
		if(value.size() > 1){
			for (String instanceValue : value.keySet()) {
				lstConditions.add("WHERE " + randomKey.getName() + " = " + instanceValue);
			}
		}
		
		for (String string : lstConditions) {
			System.out.println(string);
		}
				
	}


	private Set<Mapping> createViewForHVPSACorrespondences(SchematicCorrespondence sc) {
		Set<Mapping> mappingSet = new HashSet<Mapping>();
		Set<CanonicalModelConstruct> constructsSource = sc.getConstructs1();
		Set<CanonicalModelConstruct> constructsTarget = sc.getConstructs2();
		
		// Retrieve left and right selection predicates
		List<ReconcilingExpression> spre_set_1 = getSelPredicates(sc.getReconcilingExpressions1());
		ReconcilingExpression spre_1 = null;
		if (spre_set_1.size() > 0 && spre_set_1.get(0) != null)
			spre_1 = spre_set_1.get(0);
		if (spre_1 == null)
			spre_1 = new ReconcilingExpression("*");
		List<ReconcilingExpression> spre_set_2 = getSelPredicates(sc.getReconcilingExpressions2());
		ReconcilingExpression spre_2 = null;
		if (spre_set_2.size() > 0 && spre_set_2.get(0) != null)
			spre_2 = spre_set_2.get(0);
		if (spre_2 == null)
			spre_2 = new ReconcilingExpression("*");
		// Retrieve head of the join left and right
		SuperAbstract headOfTheJoin1 = null;
		List<SuperAbstract> source_sa_set = new ArrayList<SuperAbstract>();
		SuperAbstract headOfTheUnion2 = null;
		List<SuperAbstract> target_sa_set = new ArrayList<SuperAbstract>();
		//TODO this following loop looks a bit odd
		for (CanonicalModelConstruct sourceConstruct : constructsSource)
			if (sourceConstruct instanceof SuperAbstract)
				headOfTheJoin1 = (SuperAbstract) sourceConstruct;
		CanonicalModelConstruct targetConstruct = constructsTarget.iterator().next();
		if (targetConstruct != null && targetConstruct instanceof SuperAbstract)
			headOfTheUnion2 = (SuperAbstract) targetConstruct;
		
		source_sa_set.add(0, headOfTheJoin1);
		
		for (CanonicalModelConstruct sourceConstruct : constructsSource) {
			
			if (sourceConstruct instanceof SuperRelationship) {
				
				SuperRelationship sr_temp = (SuperRelationship) sourceConstruct;
				
				if (sr_temp.getSuperRelationshipType() == SuperRelationshipType.FOREIGNKEY) {
					List<ParticipationOfCMCInSuperRelationship> par_set = new ArrayList<ParticipationOfCMCInSuperRelationship>(
							sr_temp.getParticipationsOfConstructs());
					for (int j = 0; j < par_set.size(); j++) {
						if (par_set.get(j).getCanonicalModelConstruct() != headOfTheJoin1) {
							source_sa_set.add((SuperAbstract) par_set.get(j).getCanonicalModelConstruct());
						}
					}
				}
			}
		}
		target_sa_set.add(0, headOfTheUnion2);
		for (CanonicalModelConstruct targetConstr : constructsTarget) {
			
			if (targetConstr instanceof SuperAbstract)
				target_sa_set.add((SuperAbstract) targetConstr);
			
		}

		// Begin constructing mappings from source to target =========================================
		// create source join array
		List<ReconcilingExpression> jpre_set_1 = getJoinPredicates(sc.getReconcilingExpressions1());
		// retireve horizontal partitioning predicates
		List<ReconcilingExpression> hpp_set_1 = getHPPredicates(sc.getReconcilingExpressions1());
		MappingOperator rootJoin1 = null;
		
		if (constructsSource.size() == 1)
			rootJoin1 = new ScanOperator(headOfTheJoin1, spre_1.getExpression());

		for (int i = 0; i < target_sa_set.size(); i++) {
			SuperAbstract target = target_sa_set.get(i);
			String re_hpp = null;
			for (int j = 0; j < hpp_set_1.size(); j++) {
				ReconcilingExpression re_temp = hpp_set_1.get(j);
				if (re_temp.getSelectionTargetSuperAbstract() != null && re_temp.getSelectionTargetSuperAbstract() == target) {
					re_hpp = re_temp.getExpression();
				}
			}
			if (re_hpp == null)
				re_hpp = "*";
			// retrieve join predicate from sc
			if (jpre_set_1.size() != 0) {
				ReconcilingExpression re = jpre_set_1.get(0);
				SuperAbstract joinReight = null;
				if (headOfTheJoin1 == re.getJoinPred1())
					joinReight = re.getJoinPred2();
				else if (headOfTheJoin1 == re.getJoinPred2())
					joinReight = re.getJoinPred1();
				MappingOperator scanLeft = new ScanOperator(headOfTheJoin1);
				MappingOperator scanRight = new ScanOperator(joinReight);
				rootJoin1 = new JoinOperator(scanLeft, scanRight, re.getExpression());
			}
			for (int j = 1; j < jpre_set_1.size(); j++) {
				ReconcilingExpression re = jpre_set_1.get(j);
				SuperAbstract joinReight = null;
				if (headOfTheJoin1 == re.getJoinPred1())
					joinReight = re.getJoinPred2();
				else if (headOfTheJoin1 == re.getJoinPred2())
					joinReight = re.getJoinPred1();
				MappingOperator scanRight = new ScanOperator(joinReight);
				rootJoin1 = new JoinOperator(rootJoin1, scanRight, re.getExpression());
			}
			MappingOperator scanRootJoin = new ScanOperator(rootJoin1, re_hpp);
			Query q1 = new Query();
			Mapping m = new Mapping();
			Query q2 = new Query();
			String sourceProjectionList1 = getProjectionList(source_sa_set, target, new ArrayList<SchematicCorrespondence>(
					schematicCorrespondencesBetweenSchema1AndSchema2));
			MappingOperator source_reduce = new ReduceOperator(scanRootJoin, sourceProjectionList1);
			MappingOperator scan_target = new ScanOperator(target);
			q2.setRootOperator(scan_target);
			q1.setRootOperator(source_reduce);
			m.setQuery1(q1);
			m.setQuery2(q2);
			mappingSet.add(m);

		}

		MappingOperator rootUnion2 = null;
		
		if (constructsTarget.size() == 1)
			rootUnion2 = new ScanOperator(headOfTheUnion2, spre_2);

		for (int i = 0; i < source_sa_set.size(); i++) {
			SuperAbstract source = source_sa_set.get(i);
			
			if (constructsTarget.size() == 1)
				rootUnion2 = new ScanOperator(headOfTheUnion2, spre_2);
			else
				rootUnion2 = new ScanOperator(headOfTheUnion2);
			for (CanonicalModelConstruct targetConstr : constructsTarget) {
				
				SuperAbstract unionRight = null;
				if (targetConstr != null && targetConstr instanceof SuperAbstract) {
					
					unionRight = (SuperAbstract) targetConstr;
					MappingOperator scanRight = new ScanOperator(unionRight);
					rootUnion2 = new SetOperator(rootUnion2, scanRight, SetOperationType.UNION_ALL);
				}
			}
			
			if (constructsTarget.size() > 1)
				rootUnion2 = new ScanOperator(rootUnion2, spre_2);
			Query q1 = new Query();
			Mapping m = new Mapping();
			Query q2 = new Query();
			String sourceProjectionList1 = getProjectionList(target_sa_set, source, new ArrayList<SchematicCorrespondence>(
					schematicCorrespondencesBetweenSchema1AndSchema2));
			MappingOperator target_reduce = new ReduceOperator(rootUnion2, sourceProjectionList1);
			MappingOperator scan_source = new ScanOperator(source);
			q2.setRootOperator(target_reduce);
			q1.setRootOperator(scan_source);
			m.setQuery1(q1);
			m.setQuery2(q2);
			mappingSet.add(m);

		}
		return mappingSet;
	}

	private List<ReconcilingExpression> getJoinPredicates(Set<ReconcilingExpression> re_set) {
		List<ReconcilingExpression> output = new ArrayList<ReconcilingExpression>();
		List<ReconcilingExpression> input = new ArrayList<ReconcilingExpression>(re_set);
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).getTypeOfReconcilingExpression() == ReconcilingExpressionType.JOIN_PREDICATE)
				output.add(input.get(i));
		}
		return output;
	}

	private List<ReconcilingExpression> getHPPredicates(Set<ReconcilingExpression> re_set) {
		List<ReconcilingExpression> output = new ArrayList<ReconcilingExpression>();
		List<ReconcilingExpression> input = new ArrayList<ReconcilingExpression>(re_set);
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).getTypeOfReconcilingExpression() == ReconcilingExpressionType.HOPA_PREDICATE)
				output.add(input.get(i));
		}
		return output;
	}

	private List<ReconcilingExpression> getSelPredicates(Set<ReconcilingExpression> re_set) {
		List<ReconcilingExpression> output = new ArrayList<ReconcilingExpression>();
		List<ReconcilingExpression> input = new ArrayList<ReconcilingExpression>(re_set);
		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE)
				output.add(input.get(i));
		}
		return output;
	}

	private Mapping createViewForOtOSACorrespondence(SchematicCorrespondence sc) {

		if (sc.getConstructRelatedSchematicCorrespondenceType() != ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT
				|| sc.getCardinalityType() != CardinalityType.ONE_TO_ONE)
			return null;
		CanonicalModelConstruct sourceConstruct = sc.getConstructs1().iterator().next();
		CanonicalModelConstruct targetConstruct = sc.getConstructs2().iterator().next();
		
		Query q1 = new Query();
		Query q2 = new Query();
		
		if (!(sourceConstruct instanceof SuperAbstract) || !(targetConstruct instanceof SuperAbstract))
			return null;
		// create selection operator
		MappingOperator scan_op_source = new ScanOperator((SuperAbstract) sourceConstruct);
		MappingOperator scan_op_target = new ScanOperator((SuperAbstract) targetConstruct);
		ReconcilingExpression re_source_to_target = null;
		ReconcilingExpression re_target_to_source = null;
		List<ReconcilingExpression> re_Set_1 = new ArrayList<ReconcilingExpression>(sc.getReconcilingExpressions1());
		List<ReconcilingExpression> re_Set_2 = new ArrayList<ReconcilingExpression>(sc.getReconcilingExpressions2());
		if (re_Set_1 != null && !re_Set_1.isEmpty() && re_Set_1.get(0) != null && re_Set_1.get(0).getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE){
			re_source_to_target = re_Set_1.get(0);
			scan_op_source.setReconcilingExpression(re_source_to_target.getExpression());
		}
		if (re_Set_2 != null && !re_Set_2.isEmpty() && re_Set_2.get(0) != null && re_Set_2.get(0).getTypeOfReconcilingExpression() == ReconcilingExpressionType.SELECTION_PREDICATE){
			re_target_to_source = re_Set_2.get(0);
			scan_op_target.setReconcilingExpression(re_target_to_source.getExpression());
		}
		// end create selection operator
		// create project operator
		// TODO many-to-many lexical correspondences
		MappingOperator reduce_op_source = new ReduceOperator(scan_op_source, "");
		SuperAbstract sa_source = (SuperAbstract) sourceConstruct;
		SuperAbstract sa_target = (SuperAbstract) targetConstruct;
		
		List<SchematicCorrespondence> sc_Set = new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2);
		
	 	//Analyse the schematic correspondence and from the directions create a query of mapping.
	
		String sourceProjectList = "";
		String mappingString="";
		System.out.println("in mappingOneToOne :"+sc_Set);
		
		String globalSchema = sa_target.getName();
        String fromSchema = sa_source.getName();
        String alias = fromSchema;
                 
        mappingString = globalSchema+" SELECT ";
        int i = 0;
        for (SchematicCorrespondence childsc:sc_Set.iterator().next().getChildSchematicCorrespondences()){
						
        	   String shortName=childsc.getShortName();
		       System.out.println("mapping shortname=" + shortName);
		       System.out.println("mappin direction=" + childsc.getDirection().toString());
		       String[] s =shortName.split("_");
		      
		       if (!childsc.getDirection().equals(DirectionalityType.FIRST_TO_SECOND)){
		      
		            String type = "";
		            String asName = "";
		            String fieldName = "";
		            
		            type = s[0];
		            asName = s[1];
		            	
		            if (i>0){
		            	mappingString += ", ";
		            	sourceProjectList += ", ";
		            }
		            if(!type.equals("MSL")){
		               	fieldName = s[2];
		               	sourceProjectList += s[2];
		            	mappingString += alias+"."+fieldName+" as "+asName;
		            }else{
		            	asName = asName.substring(asName.lastIndexOf(".")+1);
		            	mappingString += "Null as "+asName;
		            	sourceProjectList += "Null";
		            }
		      
		        }
		        i++;
		}
				
		mappingString+=" FROM "+fromSchema+" "+alias;
		System.out.println("mappingString:"+mappingString);
	      
		reduce_op_source.setReconcilingExpression(sourceProjectList);
		
		q1.setQueryString(mappingString);
		q1.setRootOperator(reduce_op_source);
		q2.setQueryString(mappingString);
		q2.setRootOperator(reduce_op_source);
		Mapping m = new Mapping();
		m.setQuery1(q1);
		m.setQuery2(q2);
		m.setQuery1String(mappingString);
		m.addConstruct1(sourceConstruct);
		m.addConstruct2(targetConstruct);
		
		mappingService.addMapping(m);
		
		return m;
	}

	//--------------------------diff

	private void addSuperAbstractsMissingIn2(SuperAbstract sa) {
		this.superabstractsOfSchema1MissingInSchema2.add(sa);
	}

	private void addSuperAbstractsMissingIn1(SuperAbstract sa) {
		this.superabstractsOfSchema2MissingInSchema1.add(sa);
	}

	private void addDifferentiatingCorrespondences(SchematicCorrespondence sc) {
		this.schematicCorrespondencesExpressingDifferenceBetweenSchema1AndSchema2.add(sc);
	}

	//--------------------------merge

	private boolean participateInSuperRelationship(SuperAbstract sa_1, SuperAbstract sa_2, SuperRelationshipType srType) {
		List<ParticipationOfCMCInSuperRelationship> par_1_Set = new ArrayList<ParticipationOfCMCInSuperRelationship>(
				sa_1.getParticipationInSuperRelationships());
		List<ParticipationOfCMCInSuperRelationship> par_2_Set = new ArrayList<ParticipationOfCMCInSuperRelationship>(
				sa_2.getParticipationInSuperRelationships());
		for (int i = 0; i < par_1_Set.size(); i++) {
			ParticipationOfCMCInSuperRelationship par_1 = par_1_Set.get(i);
			for (int j = 0; j < par_2_Set.size(); j++) {
				ParticipationOfCMCInSuperRelationship par_2 = par_2_Set.get(j);
				if (par_1.getSuperRelationship() == par_2.getSuperRelationship() && par_1.getSuperRelationship().getSuperRelationshipType() == srType
						&& par_2.getSuperRelationship().getSuperRelationshipType() == srType)
					return true;
			}
		}
		return false;
	}

	private void propagatingGeneralisationSr(SuperRelationship srSource) {
		// retrieve superTypeSourceSource
		SuperAbstract superType = srSource.getGeneralisedSuperAbstract();
		// retrieve all the superabstracts in the merged schema that are conceptually equivalent to superType
		List<SuperAbstract> saGeneralInMergedSet = findCorrespondingSuperAbstracts(superType, mergedSchema);
		List<ParticipationOfCMCInSuperRelationship> parSourceSr = new ArrayList<ParticipationOfCMCInSuperRelationship>(
				srSource.getParticipationsOfConstructs());
		// to retrieve all the specific type superabstracts
		for (int i = 0; i < parSourceSr.size(); i++) {
			ParticipationOfCMCInSuperRelationship parS = parSourceSr.get(i);
			if (parS.getCanonicalModelConstruct() != superType) {
				List<SuperAbstract> specificTypeSASet = findCorrespondingSuperAbstracts((SuperAbstract) parS.getCanonicalModelConstruct(),
						mergedSchema);
				// for each specific type superabstract
				// retrieve all the conceptually equivalent superabstracts in the merged schema
				for (int j = 0; j < specificTypeSASet.size(); j++) {
					SuperAbstract mergedSpecific = specificTypeSASet.get(j);
					// create a generalisation relationship between equivalent specific superabstract with
					// the generalised superabstract
					// insert the superrelationship into the merged schema
					for (int k = 0; k < saGeneralInMergedSet.size(); k++) {
						SuperAbstract mergedGeneral = saGeneralInMergedSet.get(k);
						// create the srMerged only if there are no superrelationships yet created 
						// between the corresponding superabstracts
						if (!participateInSuperRelationship(mergedGeneral, mergedSpecific, srSource.getSuperRelationshipType())) {
							SuperRelationship srMerged = new SuperRelationship("Generalisation", mergedSchema);
							srMerged.setSuperRelationshipType(SuperRelationshipType.GENERALISATION);
							srMerged.setGeneralisedSuperAbstract(mergedGeneral);
							
							// connect superrelationship(merged) with generalised and with specific sa
							ParticipationOfCMCInSuperRelationship parMergedFrom = new ParticipationOfCMCInSuperRelationship(
									"GeneralisationSuperType", srMerged, mergedGeneral);
							ParticipationOfCMCInSuperRelationship parMergedTo = new ParticipationOfCMCInSuperRelationship(
									"GeneralisationSpecificType", srMerged, mergedSpecific);
							mergedSchema.addCanonicalModelConstruct(srMerged);
						}
					}
				}
			}
		}
		return;
	}

	/**
	 * 
	 *  To retrieve all the superabstracts in the <code>schemaTarget</code> corresponded to <code>saSource</code> 
	 * 
	 * @param saSource
	 * @param schemaTarget
	 * @return
	 */
	private List<SuperAbstract> findCorrespondingSuperAbstracts(SuperAbstract saSource, Schema schemaTarget) {
		List<SuperAbstract> saSet = new ArrayList<SuperAbstract>();
		//List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> appSource = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
		//		saSource.getApplicationOfSchematicCorrespondenceToConstructs());
		Set<Morphism> morphisms = saSource.getMorphisms();
		//for (int i = 0; i < appSource.size(); i++) {
		for (Morphism morphism : morphisms) {
			//ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct appS = appSource.get(i);
			if (morphism instanceof SchematicCorrespondence) {
				SchematicCorrespondence sc = (SchematicCorrespondence) morphisms;
				//SchematicCorrespondence sc = appS.getSchematicCorrespondence();
				Set<CanonicalModelConstruct> constructsSource = sc.getConstructs1();
				Set<CanonicalModelConstruct> constructsTarget = sc.getConstructs2();
				//List<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> appTarget = new ArrayList<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>(
				//		sc.getApplicationOfSchematicCorrespondenceToConstructs());
				//TODO this might not work properly - check this
				for (CanonicalModelConstruct construct : constructsSource) {
					if (construct instanceof SuperAbstract && construct.getSchema().equals(schemaTarget))
						saSet.add((SuperAbstract) construct);
				}
				for (CanonicalModelConstruct construct : constructsTarget) {
					if (construct instanceof SuperAbstract && construct.getSchema().equals(schemaTarget))
						saSet.add((SuperAbstract) construct);
				}
				
			}
		}
		return saSet;
	}

	private void propagatingReferenceSr(SuperRelationship srSource) {

		// retireve the participation of sa in srSource
		List<ParticipationOfCMCInSuperRelationship> parSourceSet_1 = new ArrayList<ParticipationOfCMCInSuperRelationship>(
				srSource.getParticipationsOfConstructs());
		List<ParticipationOfCMCInSuperRelationship> parSourceSet_2 = new ArrayList<ParticipationOfCMCInSuperRelationship>(
				srSource.getParticipationsOfConstructs());
		// nested-for-loop into the two sets of participated sa
		for (int i = 0; i < parSourceSet_1.size(); i++) {
			ParticipationOfCMCInSuperRelationship parS_1 = parSourceSet_1.get(i);
			SuperAbstract sa_1 = (SuperAbstract) parS_1.getCanonicalModelConstruct();
			for (int j = 0; j < parSourceSet_2.size(); j++) {
				ParticipationOfCMCInSuperRelationship parS_2 = parSourceSet_2.get(j);
				SuperAbstract sa_2 = (SuperAbstract) parS_2.getCanonicalModelConstruct();
				// for each pair of participated sa_1 and sa_2, if they are not the same
				if (sa_1 != sa_2) {
					List<SuperAbstract> sa_1_merged_set = findCorrespondingSuperAbstracts(sa_1, mergedSchema);
					List<SuperAbstract> sa_2_merged_set = findCorrespondingSuperAbstracts(sa_2, mergedSchema);
					// for each equivalent sa in the merged schema to sa_1
					for (int m = 0; m < sa_1_merged_set.size(); m++) {
						SuperAbstract sa_merged_1 = sa_1_merged_set.get(m);
						// for each equivalent sa in the merged schema to sa_2
						for (int n = 0; n < sa_2_merged_set.size(); n++) {
							SuperAbstract sa_merged_2 = sa_2_merged_set.get(n);
							// create a merged_sr between sa_1_merged and sa_2_merged
							// if there has been yet one 
							if (!participateInSuperRelationship(sa_merged_1, sa_merged_2, srSource.getSuperRelationshipType())) {
								SuperRelationship srMerged = new SuperRelationship("from-" + sa_merged_1.getName() + "-to-" + sa_merged_2.getName(),
										mergedSchema);
								srMerged.setSuperRelationshipType(SuperRelationshipType.REFERENCE);
								//srMerged.setId(15);
								// connect superrelationship(merged) with generalised and with specific sa
								ParticipationOfCMCInSuperRelationship parMergedFrom = new ParticipationOfCMCInSuperRelationship(
										"ReferenceRelationship", srMerged, sa_merged_1);
								ParticipationOfCMCInSuperRelationship parMergedTo = new ParticipationOfCMCInSuperRelationship(
										"ReferenceRelationship", srMerged, sa_merged_2);
								mergedSchema.addCanonicalModelConstruct(srMerged);
							}

						}
					}
				}
			}
		}

	}

	private SuperAbstract reconcileO2OSACR(SchematicCorrespondence sc) {

		SuperAbstract sa_merged = null;
		if (sc.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT
				&& sc.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
			// TODO sa2sa corr
			// retrieve sa_1
			// retrieve sa_2
			SuperAbstract sa_1 = null;
			SuperAbstract sa_2 = null;
			// retrieve sa_1 and sa_2 from sc 
			Set<CanonicalModelConstruct> constructsSource = sc.getConstructs1();
			Set<CanonicalModelConstruct> constructsTarget = sc.getConstructs2();
			sa_1 = (SuperAbstract) constructsSource.iterator().next();
			sa_2 = (SuperAbstract) constructsTarget.iterator().next();
			
			// create merged sa
			sa_merged = new SuperAbstract("from::" + sa_1.getSchema().getName() + "&&" + sa_2.getSchema().getName() + ":" + sa_1.getName() + "&&"
					+ sa_2.getName(), mergedSchema);
			//sa_merged.setId(15);
			sa_merged.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
			mergedSchema.addCanonicalModelConstruct(sa_merged);
			// create 1-merge corr
			SchematicCorrespondence sa1ToMerge = new SchematicCorrespondence("o2oSA2SA", "DNSC",
					SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
			//sa1ToMerge.setId(15);
			schematicCorrespondencesBetweenSchema1AndMergedSchema.add(sa1ToMerge);
			sa1ToMerge.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
			sa1ToMerge.setDirection(DirectionalityType.FIRST_TO_SECOND);
			sa1ToMerge.setCardinalityType(CardinalityType.ONE_TO_ONE);
			sa1ToMerge.addConstruct1(sa_1);
			sa1ToMerge.addConstruct2(sa_merged);
			
			// create 2-merge corr
			SchematicCorrespondence sa2ToMerge = new SchematicCorrespondence("o2oSA2SA", "DNSC",
					SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
			//sa2ToMerge.setId(15);
			schematicCorrespondencesBetweenSchema2AndMergedSchema.add(sa2ToMerge);
			sa2ToMerge.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
			sa2ToMerge.setDirection(DirectionalityType.FIRST_TO_SECOND);
			sa2ToMerge.setCardinalityType(CardinalityType.ONE_TO_ONE);
			sa2ToMerge.addConstruct1(sa_2);
			sa2ToMerge.addConstruct2(sa_merged);
			
			// reconcile lexical corr
			List<SchematicCorrespondence> scSet = new ArrayList<SchematicCorrespondence>(schematicCorrespondencesBetweenSchema1AndSchema2);
			List<SuperLexical> tempLexSet_1 = new ArrayList<SuperLexical>(sa_1.getSuperLexicals());
			List<SuperLexical> tempLexSet_2 = new ArrayList<SuperLexical>(sa_2.getSuperLexicals());
			List<SuperLexical> handledLexSet_1 = new ArrayList<SuperLexical>();
			List<SuperLexical> handledLexSet_2 = new ArrayList<SuperLexical>();
			// loop into every lexicals of sa_1
			for (int i = 0; i < tempLexSet_1.size(); i++) {
				SuperLexical lex_1 = tempLexSet_1.get(i);
				if (handledLexSet_1.contains(lex_1))
					continue;
				boolean isLonelyLexical_1 = true;
				// loop into every lexicals of sa_2
				for (int j = 0; j < tempLexSet_2.size(); j++) {
					SuperLexical lex_2 = tempLexSet_2.get(j);
					if (handledLexSet_2.contains(lex_2))
						continue;
					// loop into every sc of input scSet
					for (int k = 0; k < scSet.size(); k++) {
						SchematicCorrespondence scTemp = scSet.get(k);
						// to check whether lex_1 and lex_2 participate in the same correspondence
						if (participateInLexSC(lex_1, lex_2, scTemp)) {
							handledLexSet_2.add(lex_2);
							isLonelyLexical_1 = false;
							// create reconciled lexical
							SuperLexical mergedLex = new SuperLexical(lex_1.getName(), mergedSchema);
							mergedLex.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
							mergedLex.setParentSuperAbstract(sa_merged);
							
							// create 1-m lex corr
							SchematicCorrespondence lex1Merge = new SchematicCorrespondence("Lex2Lex", "LEPA",
									SchematicCorrespondenceType.LEXICAL_PARTITIONING);
							//lex1Merge.setId(15);
							schematicCorrespondencesBetweenSchema1AndMergedSchema.add(lex1Merge);
							lex1Merge
									.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
							lex1Merge.setDirection(DirectionalityType.FIRST_TO_SECOND);
							// create 2-m lex corr
							SchematicCorrespondence lex2Merge = new SchematicCorrespondence("Lex2Lex", "LEPA",
									SchematicCorrespondenceType.LEXICAL_PARTITIONING);
							schematicCorrespondencesBetweenSchema2AndMergedSchema.add(lex2Merge);
							lex2Merge
									.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
							lex2Merge.setDirection(DirectionalityType.FIRST_TO_SECOND);
							switch (scTemp.getCardinalityType()) {
							case ONE_TO_ONE: {
								lex1Merge.setCardinalityType(CardinalityType.ONE_TO_ONE);
								lex1Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
								lex2Merge.setCardinalityType(CardinalityType.ONE_TO_ONE);
								lex2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
							}
							case ONE_TO_MANY: {
								lex1Merge.setCardinalityType(CardinalityType.ONE_TO_ONE);
								lex1Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
								lex2Merge.setCardinalityType(CardinalityType.MANY_TO_ONE);
								lex2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.LEXICAL_PARTITIONING);
							}
							case MANY_TO_ONE: {
								lex1Merge.setCardinalityType(CardinalityType.MANY_TO_ONE);
								lex1Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.LEXICAL_PARTITIONING);
								lex2Merge.setCardinalityType(CardinalityType.ONE_TO_ONE);
								lex2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
							}
							case MANY_TO_MANY:
								lex1Merge.setCardinalityType(CardinalityType.MANY_TO_ONE);
								lex1Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.LEXICAL_PARTITIONING);
								lex2Merge.setCardinalityType(CardinalityType.MANY_TO_ONE);
								lex1Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.LEXICAL_PARTITIONING);
							}
							lex1Merge.addConstruct2(mergedLex);
							lex2Merge.addConstruct2(mergedLex);
							
							Set<CanonicalModelConstruct> constructs1 = scTemp.getConstructs1();
							Set<CanonicalModelConstruct> constructs2 = scTemp.getConstructs2();
							
							// FOR EACH constructs connected to scTemp
							for (CanonicalModelConstruct construct : constructs1) {
								lex1Merge.addConstruct1(construct);
								handledLexSet_1.add((SuperLexical) construct);
							}
							for (CanonicalModelConstruct construct : constructs2) {
								lex2Merge.addConstruct1(construct);
								handledLexSet_2.add((SuperLexical) construct);
							}
							

						}// END to check whether lex_1 and lex_2 participate in the same correspondence
					}// END loop into every sc of input scSet
				}// END loop into every lexicals of sa_2

				// reconcile lonely lexicals of sa_1
				if (isLonelyLexical_1) {
					handledLexSet_1.add(lex_1);
					SuperLexical lonelyLex_1 = new SuperLexical(lex_1.getName(), mergedSchema);
					//lonelyLex_1.setId(15);
					lonelyLex_1.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
					lonelyLex_1.setParentSuperAbstract(sa_merged);
					//sa_merged.getSuperLexicals().add(lonelyLex_1);
					SchematicCorrespondence lex1Merge = new SchematicCorrespondence("1:1Lex2Lex", "SNSC",
							SchematicCorrespondenceType.LEXICAL_PARTITIONING);
					schematicCorrespondencesBetweenSchema1AndMergedSchema.add(lex1Merge);
					//lex1Merge.setId(15);
					lex1Merge
							.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
					lex1Merge.setDirection(DirectionalityType.FIRST_TO_SECOND);
					lex1Merge.addConstruct1(lex_1);
					lex1Merge.addConstruct2(lonelyLex_1);
					
				}
			}// END loop into every lexicals of sa_1

			// reconcile lonely lexicals of sa_2
			for (int i = 0; i < tempLexSet_2.size(); i++) {
				SuperLexical lex_2 = tempLexSet_2.get(i);
				if (!handledLexSet_2.contains(lex_2)) {
					SuperLexical lonelyLex_2 = new SuperLexical(lex_2.getName(), mergedSchema);
					//lonelyLex_2.setId(15);
					lonelyLex_2.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
					lonelyLex_2.setParentSuperAbstract(sa_merged);
					//sa_merged.getSuperLexicals().add(lonelyLex_2);
					SchematicCorrespondence lex2Merge = new SchematicCorrespondence("1:1Lex2Lex", "SNSC",
							SchematicCorrespondenceType.LEXICAL_PARTITIONING);
					schematicCorrespondencesBetweenSchema2AndMergedSchema.add(lex2Merge);
					//lex2Merge.setId(15);
					lex2Merge
							.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
					lex2Merge.setDirection(DirectionalityType.FIRST_TO_SECOND);
					lex2Merge.addConstruct1(lex_2);
					lex2Merge.addConstruct2(lonelyLex_2);
					
				}
			}
			// reconcile child superabstracts
			List<SuperAbstract> handledSaSet_1 = new ArrayList<SuperAbstract>();
			List<SuperAbstract> handledSaSet_2 = new ArrayList<SuperAbstract>();
			List<SuperAbstract> childSet_1 = new ArrayList<SuperAbstract>(sa_1.getChildSuperAbstracts());
			List<SuperAbstract> childSet_2 = new ArrayList<SuperAbstract>(sa_2.getChildSuperAbstracts());
			for (int i = 0; i < childSet_1.size(); i++) {
				SuperAbstract sa_child_1 = childSet_1.get(i);
				if (handledSaSet_1.contains(sa_child_1))
					continue;
				boolean isLonelySa_1 = true;
				for (int j = 0; j < childSet_2.size(); j++) {
					SuperAbstract sa_child_2 = childSet_2.get(j);
					if (handledSaSet_2.contains(sa_child_2))
						continue;
					for (int k = 0; k < scSet.size(); k++) {
						SchematicCorrespondence scTemp = scSet.get(k);
						if (participateInSaSC(sa_child_1, sa_child_2, scTemp)) {
							isLonelySa_1 = false;
							if (scTemp.getCardinalityType() == CardinalityType.ONE_TO_ONE) {
								SuperAbstract sa_child_merged = reconcileO2OSACR(scTemp);
								sa_merged.addChildSuperAbstract(sa_child_merged);
								handledSaSet_1.add(sa_child_1);
								handledSaSet_2.add(sa_child_2);
							} else if (scTemp.getCardinalityType() == CardinalityType.ONE_TO_MANY
									|| scTemp.getCardinalityType() == CardinalityType.MANY_TO_MANY
									|| scTemp.getCardinalityType() == CardinalityType.MANY_TO_MANY) {
								SuperAbstract sa_child_merged = reconcileM2MSACR(scTemp);
								sa_merged.addChildSuperAbstract(sa_child_merged);
								Set<CanonicalModelConstruct> constructs1 = sc.getConstructs1();
								Set<CanonicalModelConstruct> constructs2 = sc.getConstructs2();
								
								// FOR EACH constructs connected to scTemp
								for (CanonicalModelConstruct construct : constructs1) {
									if (!handledSaSet_1.contains(construct))
										handledSaSet_1.add((SuperAbstract) construct);
								}
								for (CanonicalModelConstruct construct : constructs2) {
									if (!handledSaSet_2.contains(construct))
										handledSaSet_2.add((SuperAbstract) construct);
								}
								
							}
						}
					}
				}
				if (isLonelySa_1) {
					handledSaSet_1.add(sa_child_1);
					SuperAbstract sa_lonely_merged_1 = new SuperAbstract("from::" + sa_child_1.getSchema().getName() + ":" + sa_child_1.getName(),
							mergedSchema);
					//sa_lonely_merged_1.setId(15);
					sa_merged.addChildSuperAbstract(sa_lonely_merged_1);
					sa_lonely_merged_1.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
					mergedSchema.addCanonicalModelConstruct(sa_lonely_merged_1);
					SchematicCorrespondence saLonely1ToMerge = new SchematicCorrespondence("o2oSA2SA", "DNSC",
							SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
					//saLonely1ToMerge.setId(15);
					schematicCorrespondencesBetweenSchema1AndMergedSchema.add(saLonely1ToMerge);
					saLonely1ToMerge
							.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
					saLonely1ToMerge.setDirection(DirectionalityType.FIRST_TO_SECOND);
					saLonely1ToMerge.setCardinalityType(CardinalityType.ONE_TO_ONE);
					saLonely1ToMerge.addConstruct1(sa_child_1);
					saLonely1ToMerge.addConstruct2(sa_lonely_merged_1);
					
					cloneSA(sa_child_1, sa_lonely_merged_1, null, 1);
				}
			}
			// reconcile lonely child superabstract
			for (int i = 0; i < childSet_2.size(); i++) {
				SuperAbstract sa_lonely_child_2 = childSet_2.get(i);
				if (!handledSaSet_2.contains(sa_lonely_child_2)) {
					SuperAbstract sa_lonely_merged_2 = new SuperAbstract("from::" + sa_lonely_child_2.getSchema().getName() + ":"
							+ sa_lonely_child_2.getName(), mergedSchema);
					//sa_lonely_merged_2.setId(15);
					sa_lonely_merged_2.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
					sa_merged.addChildSuperAbstract(sa_lonely_merged_2);
					mergedSchema.addCanonicalModelConstruct(sa_lonely_merged_2);
					SchematicCorrespondence saLonely2ToMerge = new SchematicCorrespondence("o2oSA2SA", "DNSC",
							SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);
					//saLonely2ToMerge.setId(15);
					schematicCorrespondencesBetweenSchema2AndMergedSchema.add(saLonely2ToMerge);
					saLonely2ToMerge
							.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
					saLonely2ToMerge.setDirection(DirectionalityType.FIRST_TO_SECOND);
					saLonely2ToMerge.setCardinalityType(CardinalityType.ONE_TO_ONE);
					saLonely2ToMerge.addConstruct1(sa_lonely_child_2);
					saLonely2ToMerge.addConstruct2(sa_lonely_merged_2);
					
					cloneSA(sa_lonely_child_2, sa_lonely_merged_2, null, 2);
				}
			}
		}
		return sa_merged;
	}

	private SuperAbstract reconcileM2MSACR(SchematicCorrespondence scParent) {
		srTempVector = new ArrayList<SuperRelationship>();
		SuperAbstract saGen = new SuperAbstract("GeneralisedSA", mergedSchema);
		//TODO sort out name
		SuperRelationship srGen = new SuperRelationship("Generalisation", mergedSchema);
		srGen.setSuperRelationshipType(SuperRelationshipType.GENERALISATION);
		srGen.setGeneralisedSuperAbstract(saGen);
		ParticipationOfCMCInSuperRelationship par = new ParticipationOfCMCInSuperRelationship("Generalisation", srGen, saGen);
		mergedSchema.addCanonicalModelConstruct(saGen);
		mergedSchema.addCanonicalModelConstruct(srGen);

		// create two SA2SA corr
		SchematicCorrespondence scOne2Merge = new SchematicCorrespondence("m2mSA2SA", "HOVEPA",
				SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING);
		schematicCorrespondencesBetweenSchema1AndMergedSchema.add(scOne2Merge);
		scOne2Merge.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
		scOne2Merge.setDirection(DirectionalityType.FIRST_TO_SECOND);
		scOne2Merge.setCardinalityType(CardinalityType.MANY_TO_MANY);

		SchematicCorrespondence scTwo2Merge = new SchematicCorrespondence("m2mSA2SA", "HOVEPA",
				SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING);
		schematicCorrespondencesBetweenSchema2AndMergedSchema.add(scTwo2Merge);
		scTwo2Merge.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
		scTwo2Merge.setDirection(DirectionalityType.FIRST_TO_SECOND);
		scOne2Merge.setCardinalityType(CardinalityType.MANY_TO_MANY);

		switch (scParent.getSchematicCorrespondenceType()) {
		case HORIZONTAL_PARTITIONING: {
			scOne2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_PARTITIONING);
			scTwo2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_PARTITIONING);
		}
		case VERTICAL_PARTITIONING: {
			scOne2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.VERTICAL_PARTITIONING);
			scTwo2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.VERTICAL_PARTITIONING);
		}
		default: {
			scOne2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING);
			scTwo2Merge.setSchematicCorrespondenceType(SchematicCorrespondenceType.HORIZONTAL_VS_VERTICAL_PARTITIONING);
		}
		}
		// END create two SA2SA corr

		// get each app from scParent
		List<SuperRelationship> srTempSet = new ArrayList<SuperRelationship>();
		Set<CanonicalModelConstruct> constructs1 = scParent.getConstructs1();
		Set<CanonicalModelConstruct> constructs2 = scParent.getConstructs2();

		for (CanonicalModelConstruct construct : constructs1) {
			if (construct instanceof SuperAbstract) {
				scOne2Merge.addConstruct1(construct);
				// Copy app.getConstruct() and all its descendant constructs to mergedSchema 
				SuperAbstract saTop = new SuperAbstract("from:" + ((SuperAbstract) construct).getSchema().getName() + "::"
						+ ((SuperAbstract) construct).getName(), mergedSchema);
				scOne2Merge.addConstruct2(saTop);
				ParticipationOfCMCInSuperRelationship parSub = new ParticipationOfCMCInSuperRelationship("Generalisation", srGen, saTop);
				cloneSA((SuperAbstract) construct, saTop, null, 1);
			} else if (construct instanceof SuperRelationship) {
				srTempSet.add((SuperRelationship) construct);
			}
		}
		for (CanonicalModelConstruct construct : constructs2) {
			if (construct instanceof SuperAbstract) {
				scTwo2Merge.addConstruct1(construct);
				// Copy app.getConstruct() and all its descendant constructs to mergedSchema 
				SuperAbstract saTop = new SuperAbstract("from:" + ((SuperAbstract) construct).getSchema().getName() + "::"
						+ ((SuperAbstract) construct).getName(), mergedSchema);
				scTwo2Merge.addConstruct2(saTop);
				ParticipationOfCMCInSuperRelationship parSub = new ParticipationOfCMCInSuperRelationship("Generalisation", srGen, saTop);
				cloneSA((SuperAbstract) construct, saTop, null, 2);
			} else if (construct instanceof SuperRelationship) {
				srTempSet.add((SuperRelationship) construct);
			}
		}
		for (CanonicalModelConstruct construct : constructs1) {
			if (construct instanceof SuperRelationship) {
				scOne2Merge.addConstruct1(construct);
				for (CanonicalModelConstruct tempConstruct : srTempVector) {
					if (tempConstruct.getName().equals(construct.getName())) {
						scOne2Merge.addConstruct2(tempConstruct); //TODO this looks a bit odd - check this
					}
				}
			}
		}
		for (CanonicalModelConstruct construct : constructs2) {
			if (construct instanceof SuperRelationship) {
				scTwo2Merge.addConstruct1(construct);
				for (CanonicalModelConstruct tempConstruct : srTempVector) {
					if (tempConstruct.getName().equals(construct.getName())) {
						scTwo2Merge.addConstruct2(tempConstruct); //TODO this looks a bit odd - check this
					}
				}
			}
		}

		return saGen;
	}

	private void cloneSA(SuperAbstract sa, SuperAbstract saMerged, SuperRelationship processedSr, int group) {

		//Clone lex
		if (sa.getSuperLexicals() != null) {
			List<SuperLexical> lexSet = new ArrayList<SuperLexical>(sa.getSuperLexicals());
			for (int i = 0; i < lexSet.size(); i++) {
				boolean alreadyExist = false;
				List<SuperLexical> lexSetMerged = new ArrayList<SuperLexical>(saMerged.getSuperLexicals());
				for (int j = 0; j < lexSetMerged.size(); j++) {
					if (lexSet.get(i).getName().equals(lexSetMerged.get(j)))
						alreadyExist = true;
				}
				if (!alreadyExist) {
					SuperLexical lexSource = lexSet.get(i);
					SuperLexical lexMerged = new SuperLexical(lexSet.get(i).getName(), mergedSchema);
					//lexMerged.setId(15);
					lexMerged.setDataType(lexSet.get(i).getDataType());
					lexMerged.setIsIdentifier(lexSet.get(i).getIsIdentifier());
					lexMerged.setParentSuperAbstract(saMerged);
					//saMerged.addSuperLexical(lexMerged);
					if (sa.getSchema() == schema1)
						create1to1LexCorrespondence(lexSource, lexMerged, this.schematicCorrespondencesBetweenSchema1AndMergedSchema);
					else
						create1to1LexCorrespondence(lexSource, lexMerged, this.schematicCorrespondencesBetweenSchema2AndMergedSchema);
				}
			}
		}
		//Clone sr
		//Recursively clone related SA
		if (sa.getParticipationInSuperRelationships() != null) {
			if (saMerged.getParticipationInSuperRelationships() == null)
				saMerged.setParticipationInSuperRelationships(new LinkedHashSet<ParticipationOfCMCInSuperRelationship>());
			List<ParticipationOfCMCInSuperRelationship> parFromSet = new ArrayList<ParticipationOfCMCInSuperRelationship>(
					sa.getParticipationInSuperRelationships());
			//clone parFrom
			for (int i = 0; i < parFromSet.size(); i++) {
				// get parFrom
				ParticipationOfCMCInSuperRelationship parFrom = parFromSet.get(i);
				// clone sr
				SuperRelationship srSource = parFrom.getSuperRelationship();
				if (srSource == processedSr || srSource.getSuperRelationshipType() != SuperRelationshipType.FOREIGNKEY)
					continue;
				SuperRelationship srMerged = new SuperRelationship(srSource.getName(), mergedSchema);
				srMerged.setSuperRelationshipType(SuperRelationshipType.FOREIGNKEY);
				srMerged.setSuperRelationshipType(srSource.getSuperRelationshipType());
				srTempVector.add(srMerged);
				mergedSchema.addCanonicalModelConstruct(srMerged);
				// clone lex of sr
				List<SuperLexical> lexOfSrSet = new ArrayList<SuperLexical>(srSource.getSuperLexicals());
				for (int j = 0; j < lexOfSrSet.size(); j++) {
					SuperLexical lexSource = lexOfSrSet.get(j);
					SuperLexical lexMerged = new SuperLexical(lexOfSrSet.get(j).getName(), mergedSchema);
					//lexMerged.setId(15);
					lexMerged.setDataType(lexOfSrSet.get(j).getDataType());
					lexMerged.setIsIdentifier(lexOfSrSet.get(j).getIsIdentifier());
					lexMerged.setParentSuperRelationship(srMerged);
					//srMerged.addSuperLexical(lexMerged);
					if (srSource.getSchema() == schema1)
						create1to1LexCorrespondence(lexSource, lexMerged, this.schematicCorrespondencesBetweenSchema1AndMergedSchema);
					else
						create1to1LexCorrespondence(lexSource, lexMerged, this.schematicCorrespondencesBetweenSchema2AndMergedSchema);
				}
				// clone parFrom
				// TODO changed to the normal constructor
				// TODO clone specifyingSuperLexicals
				ParticipationOfCMCInSuperRelationship parFromMerged = new ParticipationOfCMCInSuperRelationship(parFrom.getRole(), srMerged, saMerged);
				// get parTo
				List<ParticipationOfCMCInSuperRelationship> parToSet = new ArrayList<ParticipationOfCMCInSuperRelationship>(
						srSource.getParticipationsOfConstructs());
				for (int j = 0; j < parToSet.size(); j++) {
					//get the saTo
					ParticipationOfCMCInSuperRelationship parTo = parToSet.get(j);
					SuperAbstract saTo = (SuperAbstract) parTo.getCanonicalModelConstruct();
					if (srSource.getGeneralisedSuperAbstract() != null && srSource.getGeneralisedSuperAbstract() == saTo)
						srMerged.setGeneralisedSuperAbstract(saTo);
					//clone it to the merge saMerged
					if (saTo != sa) {
						SuperAbstract saToMerged = new SuperAbstract("from:" + saTo.getSchema().getName() + "::" + saTo.getName(), mergedSchema);
						mergedSchema.addCanonicalModelConstruct(saTo);
						//create 1:1 sa2sa correspondence between saTo and saMerged
						//clone a parTo
						// TODO changed to the normal constructor
						// TODO clone specifyingSuperLexicals
						ParticipationOfCMCInSuperRelationship parToMerged = new ParticipationOfCMCInSuperRelationship(parTo.getRole(), srMerged,
								saToMerged);
						//recursively call cloneSA on saMerged
						cloneSA(saTo, saToMerged, srSource, group);
					}
				}
			}
		}
		//Recursive clone nested sa
		if (sa.getChildSuperAbstracts() != null) {
			List<SuperAbstract> childSet = new ArrayList<SuperAbstract>(sa.getChildSuperAbstracts());
			for (int i = 0; i < childSet.size(); i++) {
				SuperAbstract saChild = childSet.get(i);
				SuperAbstract saChildMerged = new SuperAbstract("from:" + saChild.getSchema().getName() + "::" + saChild.getName(), mergedSchema);
				saMerged.addChildSuperAbstract(saChildMerged);
				cloneSA(saChild, saChildMerged, null, group);

				SchematicCorrespondence scSaChild2SaChildMerged = new SchematicCorrespondence("o2oSA2SA", "SNSC",
						SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT);
				if (group == 1)
					schematicCorrespondencesBetweenSchema1AndMergedSchema.add(scSaChild2SaChildMerged);
				if (group == 2)
					schematicCorrespondencesBetweenSchema2AndMergedSchema.add(scSaChild2SaChildMerged);
				scSaChild2SaChildMerged
						.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				scSaChild2SaChildMerged.setDirection(DirectionalityType.FIRST_TO_SECOND);
				scSaChild2SaChildMerged.setCardinalityType(CardinalityType.ONE_TO_ONE);
				scSaChild2SaChildMerged.addConstruct1(saChild);
				scSaChild2SaChildMerged.addConstruct2(saChildMerged);
				
			}
		}
	}

	private void create1to1LexCorrespondence(SuperLexical lex1, SuperLexical lex2, Set<SchematicCorrespondence> outputCrSet) {

		SchematicCorrespondence lexCorr = new SchematicCorrespondence("1:1Lex2Lex", "DNSC", SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT);

		outputCrSet.add(lexCorr);
		lexCorr.addConstruct1(lex1);
		lexCorr.addConstruct2(lex2);
		
		lexCorr.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPER_LEXICAL);
		lexCorr.setCardinalityType(CardinalityType.ONE_TO_ONE);
		lexCorr.setDirection(DirectionalityType.BIDIRECTIONAL);

	}

	//--------------------------merge and diff

	private boolean participateInSaSC(SuperAbstract sa_1, SuperAbstract sa_2, SchematicCorrespondence sc) {
		boolean doParticipate1 = false;
		boolean doParticipate2 = false;
		if (sc.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT) {
			Set<CanonicalModelConstruct> constructs1 = sc.getConstructs1();
			Set<CanonicalModelConstruct> constructs2 = sc.getConstructs2();

			for (CanonicalModelConstruct construct1 : constructs1) {
				if (construct1.equals(sa_1))
					doParticipate1 = true;
				else if (construct1.equals(sa_2))
					doParticipate2 = true;
			}
			for (CanonicalModelConstruct construct2 : constructs2) {
				if (construct2.equals(sa_1))
					doParticipate1 = true;
				else if (construct2.equals(sa_2))
					doParticipate2 = true;
			}
			
		}
		return doParticipate1 && doParticipate2;
	}

	//------------------------------merge and viewGen

	private boolean participateInLexSC(SuperLexical lex_1, SuperLexical lex_2, SchematicCorrespondence sc) {
		boolean doParticipate1 = false;
		boolean doParticipate2 = false;
		
		//IVE: I commented this code, because I saw that in my case never is true, and I am trying to understand the logic fo this.
		//if (sc.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_LEXICAL_TO_SUPERLEXICAL) {
			
			Set<CanonicalModelConstruct> constructs1 = sc.getConstructs1();
			Set<CanonicalModelConstruct> constructs2 = sc.getConstructs2();

			for (CanonicalModelConstruct construct1 : constructs1) {
				if (construct1.equals(lex_1))
					doParticipate1 = true;
				else if (construct1.equals(lex_2))
					doParticipate2 = true;
			}
			for (CanonicalModelConstruct construct2 : constructs2) {
				if (construct2.equals(lex_1))
					doParticipate1 = true;
				else if (construct2.equals(lex_2))
					doParticipate2 = true;
			}
			
		//}
		return doParticipate1 && doParticipate2;
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Schema merge(Set<SchematicCorrespondence> schematicCorrespondenceBetweenSchema1AndSchema2) {

		Schema mergedSchema = new Schema();
		
		for (SchematicCorrespondence sc : schematicCorrespondenceBetweenSchema1AndSchema2) {
			
			if (sc.getCardinalityType().equals(CardinalityType.ONE_TO_ONE)){
				if (sc.getConstructRelatedSchematicCorrespondenceType().equals(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT)){
					
					SuperAbstract sa = new SuperAbstract();
					sa.setName(sc.getConstructs1().iterator().next().getName()+sc.getConstructs2().iterator().next().getName());
					System.out.println("Merged Schema SuperAbstract: "+ sa.getName());
					
					Set<SuperLexical> listSL = new HashSet<SuperLexical>();
					
					for (SchematicCorrespondence childsc:sc.getChildSchematicCorrespondences()){
						SuperLexical sl = new SuperLexical();
						sl.setSchema(mergedSchema);
						sl.setParentSuperAbstract(sa);
					    String shortName=childsc.getShortName();
					    String direction = childsc.getDirection().toString();
					    logger.debug("MERGE SC shortname="+shortName);
					    logger.debug("MERGE SC direction="+direction);
					    String[] s =shortName.split("_");
					    				      
			            String asName = "";
			            asName = s[1];
				        sl.setName(asName);
				        listSL.add(sl);
				        System.out.println("Merged Schema SuperLexical: "+ asName);
					}	
					sa.setSuperLexicals(listSL);
					mergedSchema.addCanonicalModelConstruct(sa);
					System.out.println("Merged Schema CanonicalModel: "+sa);
					System.out.println("");
					
					mergedSchema.setName("Merged_Schema_"+sa.getName());
					mergedSchema.setSchematicCorrespondenceSchemaMerged(getMergedSchemaCorrespondence(sc, mergedSchema));
					mergedSchema.setDataspace(sc.getConstructs1().iterator().next().getSchema().getDataspace());
					
					// does not make sense the merge schema has a datasource, 
					//but it is necessary to save the schema in the database, so the datasource is the same of the source schema
					mergedSchema.setDataSource(sc.getConstructs1().iterator().next().getSchema().getDataSource());
				}
			}
					
		}
		
		mergedSchema.setModelType(ModelType.INTEGRATION);
		schemaRepository.save(mergedSchema);
		
		return mergedSchema;
	}
	
	public Map<Integer, SchematicCorrespondence> getMergedSchemaCorrespondence(SchematicCorrespondence sc, Schema mergedSchema){
		
		Map<Integer, SchematicCorrespondence> schematicCorrespondences = new HashMap<Integer, SchematicCorrespondence>();	
		Map<Integer, SchematicCorrespondence> schematicCorrespondencesChild = new HashMap<Integer, SchematicCorrespondence>();	

		
		if (sc.getCardinalityType().equals(CardinalityType.ONE_TO_ONE)){
					
			if (sc.getConstructRelatedSchematicCorrespondenceType().equals(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT)){
				
				schematicCorrespondences = getMergeSchematicCorrepondence(sc, mergedSchema.getSuperAbstracts().iterator().next());
				
				for (SchematicCorrespondence childsc:sc.getChildSchematicCorrespondences()){

					String shortName=childsc.getShortName();
					String[] s =shortName.split("_");	

					String asName = s[1];
					
					for (CanonicalModelConstruct cm : mergedSchema.getSuperAbstractsAndSuperLexicals()) {
						
						if ((cm instanceof SuperLexical) && (cm.getName().equals(asName))){
							schematicCorrespondencesChild = getMergeSchematicCorrepondence(childsc, cm);
							schematicCorrespondences.get(1).addChildSchematicCorrespondence(schematicCorrespondencesChild.get(1));
							schematicCorrespondences.get(2).addChildSchematicCorrespondence(schematicCorrespondencesChild.get(2));
						}
						
					}
	    		
				}
				
			}
		}
		return schematicCorrespondences;
	}
	
	
	
	private Map<Integer, SchematicCorrespondence> getMergeSchematicCorrepondence(SchematicCorrespondence sc, CanonicalModelConstruct mergedCM) {
		
		SchematicCorrespondence schematicCorrespondence1 = null;
		SchematicCorrespondence schematicCorrespondence2 = null;
		Map<Integer, SchematicCorrespondence> schematicCorrespondenceGeneratebyMerge = new HashMap<Integer, SchematicCorrespondence>();
		
		String shortName=sc.getShortName();
		Scanner s = new Scanner(shortName);
		s.useDelimiter(Pattern.compile("_"));	
		  
		String type = "";
		String asName = "";
		String fieldName = "";
		
		if (s.hasNext())
			type = s.next();
		if (s.hasNext())
			asName = s.next();
		if (s.hasNext())
			fieldName = s.next();
		
		try{
			
		    SchematicCorrespondenceType schematicCorrespondenceType1 = null;
		    SchematicCorrespondenceType schematicCorrespondenceType2 = null;
		    
		    CanonicalModelConstruct sourceConstruct1 = sc.getConstructs1().iterator().next();
		    CanonicalModelConstruct sourceConstruct2 = sc.getConstructs2().iterator().next();
			CanonicalModelConstruct targetConstruct = mergedCM;
			
		    String shortNameSC1 = null;
			String name1 = null;
			String shortNameSC2 = null;
			String name2 = null;
			
			schematicCorrespondence1 = new SchematicCorrespondence();
			schematicCorrespondence2 = new SchematicCorrespondence();
						
			if (!type.equalsIgnoreCase("MSL")){
				
				if (asName.equals(fieldName)) {
					logger.debug("both constructs have same name - create SNSC correspondence");
					
					name1 = "SameNameSameConstruct_" + sourceConstruct1.getSchema().getName() + "." + sourceConstruct1.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC1 = "SNSC" + "_" + sourceConstruct1.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType1 = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
					
					name2 = "SameNameSameConstruct_" + sourceConstruct2.getSchema().getName() + "." + sourceConstruct2.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC2 = "SNSC" + "_" + sourceConstruct2.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType2 = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
					
				} else {
					logger.debug("different name for the two constructs - create DNSC correspondence");
					
					name1 = "DifferentNameSameConstruct_" + sourceConstruct1.getSchema().getName() + "." + sourceConstruct1.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC1 = "DNSC_" + sourceConstruct1.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType1 = SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT;
					
					name2 = "DifferentNameSameConstruct_" + sourceConstruct2.getSchema().getName() + "." + sourceConstruct2.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC2 = "DNSC_" + sourceConstruct2.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType2 = SchematicCorrespondenceType.DIFFERENT_NAME_SAME_CONSTRUCT;
				}

				schematicCorrespondence1.setName(name1);
				schematicCorrespondence1.setShortName(shortNameSC1);
				schematicCorrespondence1.setSchematicCorrespondenceType(schematicCorrespondenceType1);
				schematicCorrespondence1.addConstruct1(sourceConstruct1);
				schematicCorrespondence1.addConstruct2(targetConstruct);
				schematicCorrespondence1.setDirection(DirectionalityType.BIDIRECTIONAL);
				schematicCorrespondence1
						.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				schematicCorrespondence1.setCardinalityType(CardinalityType.ONE_TO_ONE);
				
				schematicCorrespondence2.setName(name2);
				schematicCorrespondence2.setShortName(shortNameSC2);
				schematicCorrespondence2.setSchematicCorrespondenceType(schematicCorrespondenceType2);
				schematicCorrespondence2.addConstruct1(sourceConstruct2);
				schematicCorrespondence2.addConstruct2(targetConstruct);
				schematicCorrespondence2.setDirection(DirectionalityType.BIDIRECTIONAL);
				schematicCorrespondence2
						.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				schematicCorrespondence2.setCardinalityType(CardinalityType.ONE_TO_ONE);
				
			} else if (type.equalsIgnoreCase("MSL")){
			
				schematicCorrespondence1 = new SchematicCorrespondence();
											
				if (sc.getDirection().equals(DirectionalityType.SECOND_TO_FIRST)){
					schematicCorrespondence1.setDirection(DirectionalityType.SECOND_TO_FIRST);
					shortNameSC1 = "MSL_" + asName + "_NULL";
					name1 ="MissingSuperLexical_in_source_"+ sc.getParentSchematicCorrespondence().getConstructs1().iterator().next().getSchema().getName() + "." + sourceConstruct1.getName();
					schematicCorrespondenceType1 = SchematicCorrespondenceType.MISSING_SUPER_LEXICAL;
					
					schematicCorrespondence2.setDirection(DirectionalityType.BIDIRECTIONAL);
					name2 = "SameNameSameConstruct_" + sourceConstruct1.getSchema().getName() + "." + sourceConstruct1.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC2 = "SNSC" + "_" + sourceConstruct1.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType2 = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
					
				} else if (sc.getDirection().equals(DirectionalityType.FIRST_TO_SECOND)){
					
					schematicCorrespondence1.setDirection(DirectionalityType.BIDIRECTIONAL);
					name1 = "SameNameSameConstruct_" + sourceConstruct2.getSchema().getName() + "." + sourceConstruct2.getName() + "_"
							+ targetConstruct.getSchema().getName() + "." + targetConstruct.getName();
					shortNameSC1 = "SNSC" + "_" + sourceConstruct2.getName() + "_" + targetConstruct.getName();
					schematicCorrespondenceType1 = SchematicCorrespondenceType.SAME_NAME_SAME_CONSTRUCT;
					
					schematicCorrespondence2.setDirection(DirectionalityType.SECOND_TO_FIRST);
					shortNameSC2 = "MSL_" + asName + "_NULL";
					name2 ="MissingSuperLexical_in_source_"+ sc.getParentSchematicCorrespondence().getConstructs2().iterator().next().getSchema().getName() + "." + sourceConstruct2.getName();
					schematicCorrespondenceType2 = SchematicCorrespondenceType.MISSING_SUPER_LEXICAL;
				}
				
				schematicCorrespondence1.setName(name1);
				schematicCorrespondence1.setShortName(shortNameSC1);
				schematicCorrespondence1.setSchematicCorrespondenceType(schematicCorrespondenceType1);
				schematicCorrespondence1.addConstruct1(sourceConstruct1);
				schematicCorrespondence1.addConstruct2(targetConstruct);
				schematicCorrespondence1.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				schematicCorrespondence1.setCardinalityType(CardinalityType.ONE_TO_ONE);
				
				schematicCorrespondence2.setName(name2);
				schematicCorrespondence2.setShortName(shortNameSC2);
				schematicCorrespondence2.setSchematicCorrespondenceType(schematicCorrespondenceType2);
				schematicCorrespondence2.addConstruct1(sourceConstruct2);
				schematicCorrespondence2.addConstruct2(targetConstruct);
				schematicCorrespondence2.setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT);
				schematicCorrespondence2.setCardinalityType(CardinalityType.ONE_TO_ONE);

			}
		
			schematicCorrespondenceGeneratebyMerge.put(1, schematicCorrespondence1);
			schematicCorrespondenceGeneratebyMerge.put(2, schematicCorrespondence2);
						
		}catch(Exception e){
			logger.error("ERROR: "+e);
		}
		
		return schematicCorrespondenceGeneratebyMerge;
	}

	private boolean isUnion(Map<Integer, SchematicCorrespondence> sc){
		
		boolean isUnion = true;
		
		for (int i = 1; i <= sc.size(); i++) {
			
			SchematicCorrespondence schematicCorrespondence = sc.get(i);
			
			String shortName = schematicCorrespondence.getShortName();
			Scanner s = new Scanner(shortName);
			s.useDelimiter(Pattern.compile("_"));
			
			  
			String type = "";
			
			if (s.hasNext())
				type = s.next();
			
			if (type.equalsIgnoreCase("MSL")){
				isUnion = false;
				break;
			}
			
			
			if (schematicCorrespondence.getCardinalityType().equals(CardinalityType.ONE_TO_ONE)){
				
				if (schematicCorrespondence.getConstructRelatedSchematicCorrespondenceType().equals(ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT)){
					
					for (SchematicCorrespondence scChild : schematicCorrespondence.getChildSchematicCorrespondences()) {
						
						shortName = scChild.getShortName();
						s = new Scanner(shortName);
						s.useDelimiter(Pattern.compile("_"));
						
						if (s.hasNext())
							type = s.next();
						
						if (type.equalsIgnoreCase("MSL")){
							isUnion = false;
							break;
						}
						
					}
					
				}
			}
		}
		
		return isUnion;
	}
	
	public Mapping viewGen(Map<Integer, SchematicCorrespondence> sc){
		
		Mapping mapMerge = new Mapping();
		boolean isUnion = this.isUnion(sc);
		boolean isMerge = false;
		boolean isFirstTime = true;
		boolean isSecondTime = false;
		String mappingString=" SELECT ";
		String atribute1 = "";
		String atribute2 = "";
		List<String> lstAtributtes =  new ArrayList<String>();
		String fieldName1 = new String();
		String fromSchema1 = new String();
		
		Query q1 = new Query();
		Query q2 = new Query();
	
		
		for (int j = 1; j <= sc.size(); j++) {
			
			SchematicCorrespondence schematicCorrespondence = sc.get(j);
	
		    String fromSchema = schematicCorrespondence.getConstructs1().iterator().next().getName();
	        mapMerge.addConstruct1(schematicCorrespondence.getConstructs1().iterator().next());
	        mapMerge.addConstruct2(schematicCorrespondence.getConstructs2().iterator().next());
	        
	    	MappingOperator scan_op_source = new ScanOperator((SuperAbstract) schematicCorrespondence.getConstructs1().iterator().next());
			MappingOperator scan_op_target = new ScanOperator((SuperAbstract) schematicCorrespondence.getConstructs2().iterator().next());
			MappingOperator reduce_op_source = new ReduceOperator(scan_op_source, "");
			MappingOperator reduce_op_target = new ReduceOperator(scan_op_target, "");
	 
	        int i = 0;
	        for (SchematicCorrespondence childsc:schematicCorrespondence.getChildSchematicCorrespondences()){
							
	        	   String shortName=childsc.getShortName();
			       System.out.println("mapping shortname=" + shortName);
			       System.out.println("mappin direction=" + childsc.getDirection().toString());
			       Scanner s = new Scanner(shortName);
			       s.useDelimiter(Pattern.compile("_"));
			       boolean attributesIsNotEqual = true;

			       if (!childsc.getDirection().equals(DirectionalityType.FIRST_TO_SECOND)){
			      
			    	   	String type = "";
						String asName = "";
						String fieldName = "";
						
						if (s.hasNext())
							type = s.next();
						if (s.hasNext())
							asName = s.next();
						if (s.hasNext())
							fieldName = s.next();
			            	
			           
			            if(!type.equals("MSL")){
			            	if(isFirstTime){
			            		atribute1 = fromSchema+"."+fieldName;
			            		fromSchema1 = fromSchema;
			            		fieldName1 = fieldName;
			            		isFirstTime = false;
			            	}else if(isSecondTime && !isUnion){
			            		if( fieldName1.equals(fieldName) ){
			            			atribute2 = fromSchema+"."+fieldName;
			            			isSecondTime = false;
			            		}
			            	}
			            	 
			            	for (int k = 0; k < lstAtributtes.size(); k++) {
		            			 if(fieldName.equals(lstAtributtes.get(k)) && !isUnion)
		            				 attributesIsNotEqual = false;
							}
			            	
			            	if(attributesIsNotEqual){
				            	if (i>0){
				            		 lstAtributtes.add(fieldName);
						             mappingString += ", " + fromSchema + "." + fieldName;
				            	}else{
				            		 
				            		 lstAtributtes.add(fieldName);
				            		 mappingString += fromSchema + "." + fieldName;
				            		 
				            	}
			            	}
			            	
			            }
			      
			        }
			        i++;

	        }
	        
			if (isUnion){
				 if(!isSecondTime)
					 mappingString += " FROM " + fromSchema + " UNION SELECT ";
				 else
					 mappingString += " FROM " + fromSchema;
			}else if(isMerge){
				mappingString += " FROM "+fromSchema1+", "+ fromSchema ;
				mappingString += " WHERE " + atribute1 + "=" + atribute2; 
	        }else{
	        	isMerge = true;
	        }
			
			isSecondTime = true;

			q1.setQueryString(mappingString);
			q2.setQueryString(mappingString);
			q1.setRootOperator(reduce_op_source);
			q2.setRootOperator(reduce_op_target);
		}
		
		
		mapMerge.setQuery1(q1);
		mapMerge.setQuery1(q2);
		mapMerge.setQuery1String(mappingString);
		
		
		mappingService.addMapping(mapMerge);
		
		return mapMerge;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public Set<Schema> extract(List<SchematicCorrespondence> sc){		
		
		Set<Schema> lstSchemaExtracted = new HashSet<Schema>();
		
		/*Map<String, String> locality = new HashMap<String, String>();
		locality.put("longitude", "latitude");
		collection.put("locality", locality);
		
		Map<String, String> taxonId = new HashMap<String, String>();
		taxonId.put("kindom", "phylum");
		taxonId.put("family","order");
		taxonId.put("genus","species");
		collection.put("taxonId", taxonId);*/
		
		for (SchematicCorrespondence schematicCorrespondence : sc) {
			
			String[] aux = schematicCorrespondence.getName().split("_");
			
			Schema schemaExtracted = new Schema("SchemaExtracted_" + aux[3] + "_" + aux[6], ModelType.INTEGRATION);
			Set<SchematicCorrespondence> setSC = new HashSet<SchematicCorrespondence>();
			Set<SchematicCorrespondence>setChildSchematicCorrespondences = new HashSet<SchematicCorrespondence>();
			Map<String, Map<String, String>> collection = new HashMap<String, Map<String, String>>();
			
			SuperAbstract sa = new SuperAbstract();
			sa.setName(schematicCorrespondence.getConstructs1().iterator().next().getName());
			System.out.println("Schema Extracted SuperAbstract: "+ sa.getName());
			
			boolean dependency = false;
			
			Set<SuperLexical> listSL = new HashSet<SuperLexical>();
			
			SchematicCorrespondence scExtrated = new SchematicCorrespondence();

	        for (SchematicCorrespondence childsc:schematicCorrespondence.getChildSchematicCorrespondences()){
							
	        	   SchematicCorrespondence scChildExtrated = new SchematicCorrespondence(); 
	        	   String shortName=childsc.getShortName();
			       System.out.println("shortname=" + shortName);
			       System.out.println("direction=" + childsc.getDirection().toString());
			       String[] s =shortName.split("_");

			       if (childsc.getDirection().equals(DirectionalityType.BIDIRECTIONAL) || dependency == true){
			      
			    	   dependency = false;
			    	   SuperLexical sl = new SuperLexical();
			    	   sl.setSchema(schemaExtracted);
			    	   sl.setParentSuperAbstract(sa);
			    	   String asName = "";
			           asName = s[1];
				       sl.setName(asName);
				       
				       if(collection.containsKey(asName) || collection.containsValue(asName)){
				    	   dependency = true;
				       }
				       
				       
				       listSL.add(sl);
				       
				       scChildExtrated.setName(childsc.getName());
				       scChildExtrated.setShortName(childsc.getShortName());
				       scChildExtrated.setSchematicCorrespondenceType(childsc.getSchematicCorrespondenceType());
				       scChildExtrated.addConstruct1(childsc.getConstructs1().iterator().next());
				       scChildExtrated.addConstruct2(childsc.getConstructs2().iterator().next());
				       scChildExtrated.setDirection(DirectionalityType.BIDIRECTIONAL);
				       scChildExtrated.setConstructRelatedSchematicCorrespondenceType(childsc.getConstructRelatedSchematicCorrespondenceType());
				       scChildExtrated.setCardinalityType(childsc.getCardinalityType());
				       
				       setChildSchematicCorrespondences.add(scChildExtrated);
				       
				       scExtrated.setChildSchematicCorrespondences(setChildSchematicCorrespondences);
				      
				       
				       
			        }else  if (childsc.getDirection().equals(DirectionalityType.SECOND_TO_FIRST)){
					      
					       scChildExtrated.setName(childsc.getName());
					       scChildExtrated.setShortName(childsc.getShortName());
					       scChildExtrated.setSchematicCorrespondenceType(childsc.getSchematicCorrespondenceType());
					       scChildExtrated.addConstruct1(childsc.getConstructs1().iterator().next());
					       scChildExtrated.addConstruct2(childsc.getConstructs2().iterator().next());
					       scChildExtrated.setDirection(DirectionalityType.SECOND_TO_FIRST);
					       scChildExtrated.setConstructRelatedSchematicCorrespondenceType(childsc.getConstructRelatedSchematicCorrespondenceType());
					       scChildExtrated.setCardinalityType(childsc.getCardinalityType());
					       
					       setChildSchematicCorrespondences.add(scChildExtrated);
					       
					       scExtrated.setChildSchematicCorrespondences(setChildSchematicCorrespondences);
					      
				     }
			       
			       
	        }
	        
	       scExtrated.setName(schematicCorrespondence.getName());
     	   scExtrated.setShortName(schematicCorrespondence.getShortName());
     	   scExtrated.setSchematicCorrespondenceType(schematicCorrespondence.getSchematicCorrespondenceType());
     	   scExtrated.addConstruct1(schematicCorrespondence.getConstructs1().iterator().next());
     	   scExtrated.addConstruct2(schematicCorrespondence.getConstructs2().iterator().next());
     	   scExtrated.setDirection(schematicCorrespondence.getDirection());
     	   scExtrated.setConstructRelatedSchematicCorrespondenceType(schematicCorrespondence.getConstructRelatedSchematicCorrespondenceType());
     	   scExtrated.setCardinalityType(schematicCorrespondence.getCardinalityType());
     	   setSC.add(scExtrated);
	        
	        sa.setSuperLexicals(listSL);
	        schemaExtracted.addCanonicalModelConstruct(sa);
			System.out.println("Extracted Schema CanonicalModel: " + sa);
			System.out.println("");
			schemaExtracted.setSchematicCorrespondenceMinimumModel(setSC);
			
			
			if (userRepository.getUserWithUserName("teste") != null) {
				currentUser = userRepository.getUserWithUserName("teste");
			}

			if (dataspaceRepository.getDataspaceWithName("spreadsheetDS1") != null) {
				currentDataspace = dataspaceRepository.getDataspaceWithName("spreadsheetDS1");
			}
			
			if (dataSourceService.findDataSourceByName("schema_source_"+sa.getName()) != null) {

				currentDS = dataSourceService.getById(dataSourceService.findDataSourceByName("schema_source_"+sa.getName()).getId());
			}
			
			schemaExtracted.setDataspace(currentDataspace);
			schemaExtracted.setDataSource(currentDS);
			schemaRepository.save(schemaExtracted);
			
			currentDS.setSchema(schemaExtracted);
			
			currentDataspace.addDataSource(currentDS);
			currentDataspace.addSchema(schemaExtracted);
			lstSchemaExtracted.add(schemaExtracted);
	
		}
		
		
		return lstSchemaExtracted;
		
	}
	
	public void generateResultsToGraph(List<SchematicCorrespondence> sc) throws IOException{

	
		File file = new File(Constant.DIRECTORY_FILE_SPREADSHEETS_CORRESPONDENCE_RESULT_GRAPH);
		PrintWriter pw;
		
	    // if file doesnt exists, then create it
	    if (!file.exists()) {
	        file.createNewFile();
	        pw = new PrintWriter(file.getAbsoluteFile());
	    }else{
	    	pw = new PrintWriter(new FileOutputStream(file.getAbsoluteFile(), true));
	    }

		for (SchematicCorrespondence schematicCorrespondence : sc) {
			
						
			if(schematicCorrespondence.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT){
			
				String[] aux = schematicCorrespondence.getName().split("_");
				
				String sourceSpreadsheet[] = aux[3].split("[.]");
				String targetSpreadsheet[] = aux[6].split("[.]");
				
				int numberCorrespondences = 0;
				int numberTotalAtributes = 0;
				Float correspondencesweight = 0.00f;
				DecimalFormat df = new DecimalFormat("0.00");
				Integer numberMissingCorrespondencesSecToFirst = 0;
				
				if(!(sourceSpreadsheet[0].equals(targetSpreadsheet[0]))){
	
			        for (SchematicCorrespondence childsc:schematicCorrespondence.getChildSchematicCorrespondences()){
			        				        	   		
			        	   String shortName=childsc.getShortName();
					       System.out.println("shortname=" + shortName);
					       System.out.println("direction=" + childsc.getDirection().toString());
					       
					       if (childsc.getDirection().equals(DirectionalityType.BIDIRECTIONAL)){
					      
						       numberCorrespondences = numberCorrespondences + 1;
						       numberTotalAtributes = numberTotalAtributes + 1;
						       
					        }else  if (childsc.getDirection().equals(DirectionalityType.SECOND_TO_FIRST)){
							      
					        	numberMissingCorrespondencesSecToFirst = numberMissingCorrespondencesSecToFirst + 1;
							      
						    }else  if (childsc.getDirection().equals(DirectionalityType.FIRST_TO_SECOND)){
						    	
						    	numberTotalAtributes = numberTotalAtributes + 1;
						    }
			        }
			        
			       correspondencesweight = (float) (numberCorrespondences * 100) / numberTotalAtributes;
			       // pw.println(sourceSpreadsheet[0] + "," + targetSpreadsheet[0] + "," + df.format(correspondencesweight));
			      // pw.println(schematicCorrespondence.getConstructs1().iterator().next().getId() + "," + schematicCorrespondence.getConstructs2().iterator().next().getId()  + "," + df.format(correspondencesweight)); 
			       pw.println(sourceSpreadsheet[0] + "," + targetSpreadsheet[0]  + "," + df.format(correspondencesweight)); 
			       System.out.println(sourceSpreadsheet + "," + targetSpreadsheet + "," + numberCorrespondences);
      
				}
			}
	
		}
		
		pw.close();	
	}
	
	public Schema getMinimumModel(){
		
		Schema minimumModelSchema = new Schema("MinimumModel", ModelType.INTEGRATION);
		SuperAbstract sa = new SuperAbstract();
		sa.setSchema(minimumModelSchema);
		
		Map<SuperLexical, Integer> modelMin = new HashMap<SuperLexical, Integer>();
		List<Schema> lstSchema = schemaRepository.getAllSchemaExtracted();
		
		for (Schema schema : lstSchema) {
			Set<CanonicalModelConstruct> setCanonicalModel = schema.getSuperAbstractsAndSuperLexicals();
			
			for (CanonicalModelConstruct canonicalModelConstruct : setCanonicalModel) {
				
				if(canonicalModelConstruct instanceof SuperLexical){
					
					if(!modelMin.containsKey(canonicalModelConstruct)){
						modelMin.put((SuperLexical)canonicalModelConstruct, 1);
					}else{
						Integer aux = modelMin.get(canonicalModelConstruct);
						aux = aux +1;
						modelMin.put((SuperLexical)canonicalModelConstruct, aux);
					}
				}
			}			
		}
		
		for(Map.Entry<SuperLexical, Integer> attribute: modelMin.entrySet()){
			Integer media = attribute.getValue() / lstSchema.size();
			
			if(media >= 0.7){
				SuperLexical sl = attribute.getKey();
				sl.setSchema(minimumModelSchema);
				sl.setParentSuperAbstract(sa);
			}
			
		}
		
		
		if (userRepository.getUserWithUserName("teste") != null) {

			currentUser = userRepository.getUserWithUserName("teste");
		}

		if (dataspaceRepository.getDataspaceWithName("spreadsheetDS1") != null) {

			currentDataspace = dataspaceRepository.getDataspaceWithName("spreadsheetDS1");
		}
		
		if (dataSourceService.findDataSourceByName("s1") != null) {

			currentDS = dataSourceService.getById(dataSourceService.findDataSourceByName("s1").getId());
		}
		
		
		minimumModelSchema.setDataspace(currentDataspace);
		minimumModelSchema.setDataSource(currentDS);
		schemaRepository.save(minimumModelSchema);
		
		currentDS.setSchema(minimumModelSchema);
		
		currentDataspace.addDataSource(currentDS);
		currentDataspace.addSchema(minimumModelSchema);
		
		return minimumModelSchema;
	}
	
	public Integer getModelRepresentativity(SchematicCorrespondence sc){
		
		Integer percentage = 0;
		Integer numberCorrespondeces = 0;
		Integer totalAttributes = 0;
		
		for (SchematicCorrespondence childsc:sc.getChildSchematicCorrespondences()){
			
			if(childsc.getDirection().equals(DirectionalityType.BIDIRECTIONAL)){
				numberCorrespondeces = numberCorrespondeces + 1;
				totalAttributes = totalAttributes + 1;
			}else if(childsc.getDirection().equals(DirectionalityType.SECOND_TO_FIRST)){
				totalAttributes = totalAttributes + 1;
			}
			
		}
		
		percentage = numberCorrespondeces / totalAttributes;
		
		return percentage;
	}
	
	public void createAttributesCorrelation(List<SchematicCorrespondence> sc) throws IOException{
		
		Map<String,Integer> attributesCorrelation = new HashMap<String, Integer>();
		Map<Integer, Integer> numberOfCorrespondences = new HashMap<Integer, Integer>();
		Integer indexMatch = 0;
		
		for (SchematicCorrespondence schematicCorrespondence : sc) {
			
			if(schematicCorrespondence.getConstructRelatedSchematicCorrespondenceType() == ConstructRelatedSchematicCorrespondenceType.SUPER_ABSTRACT_TO_SUPER_ABSTRACT){
	
		        for (SchematicCorrespondence childsc:schematicCorrespondence.getChildSchematicCorrespondences()){
		        	
		        	String shortName=childsc.getShortName();
		        	Scanner s = new Scanner(shortName);
				    s.useDelimiter(Pattern.compile("_"));
		        	
		        	if (childsc.getDirection().equals(DirectionalityType.BIDIRECTIONAL)){
					      
			    	   	String type = "";
						String asName = "";
						String fieldName = "";
						
						if (s.hasNext())
							type = s.next();
						if (s.hasNext())
							asName = s.next();
						if (s.hasNext())
							fieldName = s.next();
						
						if(asName.equals(fieldName)){
							if(attributesCorrelation.containsKey(asName)){
								
								numberOfCorrespondences.put(attributesCorrelation.get(asName), numberOfCorrespondences.get(attributesCorrelation.get(asName)) + 1);									
								
							}else{
								attributesCorrelation.put(asName, indexMatch);
								indexMatch = indexMatch + 1;
								numberOfCorrespondences.put(attributesCorrelation.get(asName), 1);	
							}
						}else{
							if(attributesCorrelation.containsKey(asName) && attributesCorrelation.containsKey(fieldName)){
								
								numberOfCorrespondences.put(attributesCorrelation.get(asName), numberOfCorrespondences.get(attributesCorrelation.get(asName)) + 1);		
								
								
							}else if(attributesCorrelation.containsKey(asName) && !attributesCorrelation.containsKey(fieldName)){
									
									attributesCorrelation.put(fieldName, attributesCorrelation.get(asName));
									numberOfCorrespondences.put(attributesCorrelation.get(asName), numberOfCorrespondences.get(attributesCorrelation.get(asName)) + 1);		

							}else if(!attributesCorrelation.containsKey(asName) && attributesCorrelation.containsKey(fieldName)){
								
								attributesCorrelation.put(asName, attributesCorrelation.get(fieldName));
								numberOfCorrespondences.put(attributesCorrelation.get(fieldName), numberOfCorrespondences.get(attributesCorrelation.get(fieldName)) + 1);		
								
							}else if(!attributesCorrelation.containsKey(asName) && !attributesCorrelation.containsKey(fieldName)){
								
								attributesCorrelation.put(asName, indexMatch);
								attributesCorrelation.put(fieldName, indexMatch);
								indexMatch = indexMatch + 1;
								numberOfCorrespondences.put(attributesCorrelation.get(asName), 1);
								
							}
						}
		        	}
		        }
			}       
		}
		
		File file = new File(Constant.DIRECTORY_FILE_SPREADSHEETS_LEMA_ATTRIBUTES_CORRESPONDENCE_RESULT_GRAPH);
		PrintWriter pw;
		
	    // if file doesnt exists, then create it
	    if (!file.exists()) {
	        file.createNewFile();
	        pw = new PrintWriter(file.getAbsoluteFile());
	    }else{
	    	pw = new PrintWriter(new FileOutputStream(file.getAbsoluteFile(), true));
	    }
	    
	    for (Entry<String, Integer> attributeCorrelated : attributesCorrelation.entrySet()) {
	    	
	    	pw.println(attributeCorrelated.getKey() + "," + attributeCorrelated.getValue() + "," + numberOfCorrespondences.get(attributeCorrelated.getValue()));
			
		}
	    
	    
	    pw.close();
	    
	    
	}
	
	
	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.meta.SchemaService#addSchema(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void addSchema(Schema schema) {
		schemaRepository.save(schema);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.meta.SchemaService#deleteSchema(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteSchema(Long schemaId) {
		// TODO
		schemaRepository.delete(schemaRepository.find(schemaId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.meta.SchemaService#findSchema(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public Schema findSchema(Long schemaId) {
		return schemaRepository.find(schemaId);
	}
	
	@Transactional(readOnly = true)
	public Schema getSchemaByName(String schemaName) {
		return schemaRepository.getSchemaByName(schemaName);
	}

	/**
	 * @return the schemaRepository
	 */
	public SchemaRepository getSchemaRepository() {
		return schemaRepository;
	}
	
	public GraphvizDotGeneratorService getGraphvizDotGeneratorService() {
		return graphvizDotGeneratorService;
	}

	/**
	 * @param schemaRepository the schemaRepository to set
	 */
	public void setSchemaRepository(SchemaRepository schemaRepository) {
		this.schemaRepository = schemaRepository;
	}

	public Set<SchematicCorrespondence> getSchematicCorrespondencesBetweenSchema1AndMergedSchema() {
		return schematicCorrespondencesBetweenSchema1AndMergedSchema;
	}

	public Set<SchematicCorrespondence> getSchematicCorrespondencesBetweenSchema2AndMergedSchema() {
		return schematicCorrespondencesBetweenSchema2AndMergedSchema;
	}

	public Schema getMergedSchema() {
		return mergedSchema;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<Schema, Long> getRepository() {
		return schemaRepository;
	}

	
}
