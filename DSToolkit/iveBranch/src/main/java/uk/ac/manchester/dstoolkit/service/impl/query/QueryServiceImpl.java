/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.coinor.opents.BestEverAspirationCriteria;
import org.coinor.opents.MoveManager;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.SimpleTabuList;
import org.coinor.opents.SingleThreadedTabuSearch;
import org.coinor.opents.Solution;
import org.coinor.opents.TabuList;
import org.coinor.opents.TabuSearch;
import org.coinor.opents.TabuSearchAdapter;
import org.coinor.opents.TabuSearchEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.operators.MappingOperatorRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultTypeRepository;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.QueryEvaluationEngineService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryexpander.QueryExpanderService;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.PhysicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "queryService")
public class QueryServiceImpl extends GenericEntityServiceImpl<Query, Long> implements QueryService {

	static Logger logger = Logger.getLogger(QueryServiceImpl.class);

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("mappingOperatorRepository")
	private MappingOperatorRepository mappingOperatorRepository;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("queryResultService")
	private QueryResultService queryResultService;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("ontologyTermRepository")
	private OntologyTermRepository ontologyTermRepository;

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	@Autowired
	@Qualifier("annotationService")
	private AnnotationService annotationService;

	@Autowired
	@Qualifier("resultTypeRepository")
	private ResultTypeRepository resultTypeRepository;

	@Autowired
	@Qualifier("sqlQueryParserService")
	private SQLQueryParserService parser;

	@Autowired
	@Qualifier("globalQueryTranslatorService")
	private GlobalQueryTranslatorService globalTranslator;

	@Autowired
	@Qualifier("queryExpanderService")
	private QueryExpanderService queryExpander;

	@Autowired
	@Qualifier("logicalQueryOptimiserService")
	private LogicalQueryOptimiserService logicalQueryOptimiser;

	@Autowired
	@Qualifier("physicalQueryOptimiserService")
	private PhysicalQueryOptimiserService physicalOptimiser;

	@Autowired
	@Qualifier("queryEvaluationEngineService")
	private QueryEvaluationEngineService queryEvaluationEngine;

	public QueryResult evaluateQuery(String queryString, String queryName, Set<Schema> schemasToEvaluateQueryOver,
			Set<DataSource> dataSourcesToEvaluateQueryOver, Set<Mapping> mappingsToUtilise, User user,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		Query query = new Query(queryName, queryString);
		query.setSchemas(schemasToEvaluateQueryOver);
		query.setDataSources(dataSourcesToEvaluateQueryOver);
		//query.setMappings(mappingsToUtilise);
		return evaluateQuery(query, mappingsToUtilise, user, controlParameters);
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public QueryResult evaluateQuery(Query query, Set<Mapping> mappingsToUtilise, User user,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		//TODO user isn't used here
		//TODO sort out rename
		//TODO this is calling quite a few methods in other services with their own transactional annotation
		logger.debug("in QueryServiceImpl.evaluate");

		//Query query = this.findQuery(q.getId());

		query = queryRepository.update(query);

		logger.debug("query: " + query);
		logger.debug("queryName: " + query.getQueryName());
		String queryString = query.getQueryString();
		logger.debug("queryString: " + queryString);
		Set<Schema> schemasToEvaluateQueryOver = query.getSchemas();
		logger.debug("schemasToEvaluateQueryOver: " + schemasToEvaluateQueryOver);
		if (schemasToEvaluateQueryOver != null) {
			for (Schema schema : schemasToEvaluateQueryOver)
				logger.debug("schema.name: " + schema.getName());
		}
		logger.debug("user: " + user);
		logger.debug("controlParameters: " + controlParameters);

		//Set<DataSource> dataSourcesToEvaluateQueryOver = queryRepository.getAllDataSourcesForQueryWithId(query.getId());
		Set<DataSource> dataSourcesToEvaluateQueryOver = query.getDataSources();
		logger.debug("dataSourcesToEvaluateQueryOver: " + dataSourcesToEvaluateQueryOver);
		if (dataSourcesToEvaluateQueryOver != null) {
			for (DataSource dataSource : dataSourcesToEvaluateQueryOver)
				logger.debug("dataSource.url: " + dataSource.getConnectionURL());
		}
		//Set<Mapping> mappingsToUtilise = queryRepository.getAllMappingsForQueryWithId(query.getId());
		//Set<Mapping> mappingsToUtilise = query.getMappings();
		/*
		Set<Mapping> mappingsToUtilise;
		if (mappingsList != null && !mappingsList.isEmpty())
			mappingsToUtilise = (Set<Mapping>) mappingsList;
		else
			mappingsToUtilise = new HashSet<Mapping>();
		*/
		//TODO deciding whether to use mappingSelection is same code as in QueryExpander - decide where it should go
		logger.debug("mappingsToUtilise: " + mappingsToUtilise);
		if (mappingsToUtilise != null && !mappingsToUtilise.isEmpty()) {
			for (Mapping mapping : mappingsToUtilise)
				logger.debug("mapping: " + mapping.getQuery1String() + " " + mapping.getQuery2String());
			if (controlParameters != null
					&& (controlParameters.containsKey(ControlParameterType.PRECISION_THRESHOLD) || controlParameters
							.containsKey(ControlParameterType.RECALL_THRESHOLD))) {
				logger.debug("controlParameters contain precision_threshold or recall_threshold - use selectMappings");

				ControlParameterType controlParameterType = null;
				if (controlParameters.containsKey(ControlParameterType.PRECISION_THRESHOLD))
					controlParameterType = ControlParameterType.PRECISION_THRESHOLD;
				else if (controlParameters.containsKey(ControlParameterType.RECALL_THRESHOLD))
					controlParameterType = ControlParameterType.RECALL_THRESHOLD;
				else
					logger.error("didn't find Precision_Threshold or Recall_threshold");

				double thresholdValue = Double.valueOf(controlParameters.get(controlParameterType).getValue()).doubleValue();
				logger.debug("controlParameterType: " + controlParameterType);
				logger.debug("thresholdValue: " + thresholdValue);
				//Set<Mapping> selectedMappings
				mappingsToUtilise = this.selectMappings(query, mappingsToUtilise, controlParameterType, thresholdValue);
				logger.debug("mappingsToUtilise: " + mappingsToUtilise);
				//query.setMappings(selectedMappings); //TODO check whether this works - not sure
			}
		}

		//TODO add controlParametersType ... 
		//TODO control parameters might have to go elsewhere anyway ...
		//TODO provenance needs to get sorted out
		//TODO implement maxNumberOfResults and fetchSize so that it's applied by the evaluateExternally operator, but none of the other operators
		//TODO this means that the restrictions won't necessarily be met by the final result
		//TODO only introducing this so that I can limit the number of results returned for each superAbstract for the instance based matching in which case
		//TODO it will only be scan queries anyway, so it will meet the restrictions
		//TODO think about this a bit more to see whether it can be implemented so that it's met by the final result too ...
		//TODO could just restrict the final result to the max number but might need different restrictions for the other operators, as they could increase
		//TODO or decrease the number of results returned ... check what databases do, as they will have to deal with this too

		if (controlParameters != null) {
			int maxNumberOfResults = -1;
			if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
				maxNumberOfResults = new Integer(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS).getValue()).intValue();
				logger.debug("maxNumberOfResults: " + maxNumberOfResults);
			}
			int fetchSize = -1;
			if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
				fetchSize = new Integer(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue()).intValue();
				logger.debug("fetchSize: " + fetchSize);
			}
		}

		if (query.getRootOperator() == null) {
			logger.debug("didn't find rootOperator");
			CommonTree ast = parser.parseSQL(queryString);
			logger.debug("ast: " + ast.toStringTree());
			if (schemasToEvaluateQueryOver != null && schemasToEvaluateQueryOver.size() > 0) {
				logger.debug("passing schema into globalTranslator");
				logger.debug("schemasToEvaluateQueryOver: " + schemasToEvaluateQueryOver);
				query = globalTranslator.translateAstIntoQuery(query, queryString, ast, schemasToEvaluateQueryOver);
			} else
				query = globalTranslator.translateAstIntoQuery(query, queryString, ast);
			logger.debug("query: " + query);
			logger.debug("query.getRootOperotor: " + query.getRootOperator());
			logger.debug("query,getRootOperatorOfExpandedQuery: " + query.getRootOperatorOfExpandedQuery());
			mappingOperatorRepository.update(query.getRootOperator());
			queryRepository.update(query);
		} else
			logger.debug("found rootOperator");

		logger.debug("before calling queryExpander");
		query = queryExpander.expandQuery(query, mappingsToUtilise, controlParameters);
		if (mappingsToUtilise == null || mappingsToUtilise.size() == 0)
			mappingsToUtilise = query.getRootOperatorOfExpandedQuery().getMappingsUsedForExpansion(); //this assumes that the used mappings are propagated through the expanded query
		//queryRepository.update(query);
		logger.debug("finished queryExpander");
		logger.debug("query: " + query);

		//TODO add rename to logical optimiser
		logger.debug("before calling logicalQueryOptimiser");
		EvaluatorOperator evaluatorOp = logicalQueryOptimiser.optimise(query, controlParameters);
		logger.debug("finished logicalQueryOptimiser");
		logger.debug("evaluatorOp: " + evaluatorOp);

		logger.debug("before calling physicalOptimiser.chooseJoinOperators");
		evaluatorOp = physicalOptimiser.chooseJoinOperators(evaluatorOp, null);
		logger.debug("evaluatorOp: " + evaluatorOp);

		logger.debug("before calling queryEvaluationEngine.evaluateQuery");
		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(evaluatorOp);
		logger.debug("resultInstances.size: " + resultInstances.size());

		QueryResult queryResult = new QueryResult(query);
		//queryResult.setMappings(mappingsToUtilise);
		//logger.debug("queryResult.getMappings(): " + queryResult.getMappings());
		Set<Mapping> mappingsUtilised = new LinkedHashSet<Mapping>();
		Set<Schema> schemasOfDataSourcesQueried = new LinkedHashSet<Schema>();

		//queryResult.setMappings(mappingsToUtilise); //TODO this list may be different from the mappings actually used in the end - sort this out
		queryResult.setResultInstances(resultInstances);
		if (resultInstances != null && resultInstances.size() > 0) {
			ResultType resultType = resultInstances.iterator().next().getResultType();
			if (resultType == null) {
				logger.error("no resultType for resultInstance");
			} else {
				queryResult.setResultType(resultType);
				//resultTypeRepository.update(resultType);
				//resultTypeService.addResultType(resultType);
			}
			logger.debug("resultType: " + resultType);
			logger.debug("resultType.getId: " + resultType.getId());
			for (ResultInstance resultInstance : resultInstances) {
				logger.debug("resultInstance: " + resultInstance);
				logger.debug("resultInstance.getResultType(): " + resultInstance.getResultType());
				logger.debug("resultInstance.getResultType().getId(): " + resultInstance.getResultType().getId());
				if (!resultInstance.getResultType().equals(resultType))
					logger.error("resultInstances have different resultTypes");
				logger.debug("resultInstance.getMappings(): " + resultInstance.getMappings());
				resultInstance.setQuery(query);
				//mappingsUtilised.addAll(resultInstance.getMappings());
				for (Mapping mapping : resultInstance.getMappings()) {
					logger.debug("mapping: " + mapping);
					if (mappingsToUtilise.contains(mapping)) {
						logger.debug("found mapping in mappingsToUtilise");
						mappingsUtilised.add(mapping); //this will only return the mappings that have actually returned results - think about this
						for (CanonicalModelConstruct construct1 : mapping.getConstructs1()) {
							logger.debug("construct1.getSchema: " + construct1.getSchema());
							if (schemasToEvaluateQueryOver.contains(construct1.getSchema())
									&& !dataSourcesToEvaluateQueryOver.contains(construct1.getSchema().getDataSource())) {
								logger.debug("schemasToEvaluateQueryOver contains construct1.getSchema: " + construct1.getSchema());
								logger.debug("and dataSourcesToEvaluateQueryOver doesn't contain corresponding dataSource");
								//looks like constructs1 in this mappings are those from the schema over which the query is posed,
								//so assume that constructs2 in this mappings are those from the datasources that have been queried
								for (CanonicalModelConstruct construct2 : mapping.getConstructs2()) {
									if (dataSourcesToEvaluateQueryOver.contains(construct2.getSchema().getDataSource())) {
										logger.debug("dataSourcesToEvaluteQueryOver contains construct2.getSchema.getDataSource: "
												+ construct2.getSchema().getDataSource());
										schemasOfDataSourcesQueried.add(construct2.getSchema());
									} else {
										logger.error("dataSourcesToEvaluteQueryOver doesn't contain construct2.getSchema.getDataSource: "
												+ construct2.getSchema().getDataSource());
										if (schemasToEvaluateQueryOver.contains(construct2.getSchema())) {
											logger.error("but schemasToEvaluateQueryOver contains construct2.getSchema ... TODO sort this");
										} else {
											logger.error("schemasToEvaluateQueryOver doesn't contain construct2.getSchema ... : "
													+ construct2.getSchema());
										}
									}
								}
								break;
							} else if (!schemasToEvaluateQueryOver.contains(construct1.getSchema())
									&& dataSourcesToEvaluateQueryOver.contains(construct1.getSchema().getDataSource())) {
								logger.debug("schemasToEvaluateQueryOver doesn't contain construct1.getSchema: " + construct1.getSchema());
								logger.debug("and dataSourcesToEvaluateQueryOver contains corresponding dataSource");
								//looks like constructs2 in this mappings are those from the schema over which the query is posed,
								//so assume that constructs1 in this mappings are those from the datasources that have been queried
								for (CanonicalModelConstruct construct2 : mapping.getConstructs2()) {
									if (schemasToEvaluateQueryOver.contains(construct2.getSchema())) {
										logger.debug("schemasToEvaluateQueryOver contains construct2.getSchema: " + construct2.getSchema());
										schemasOfDataSourcesQueried.add(construct1.getSchema());
										break;
									} else {
										logger.debug("schemasToEvaluateQueryOver doesn't contain construct2.getSchema ... : "
												+ construct2.getSchema());
										if (dataSourcesToEvaluateQueryOver.contains(construct2.getSchema().getDataSource())) {
											logger.error("but dataSourcesToEvaluteQueryOver contains construct2.getSchema.getDataSource: "
													+ construct2.getSchema().getDataSource());
										} else {
											logger.error("dataSourcesToEvaluteQueryOver doesn't contain construct2.getSchema.getDataSource: "
													+ construct2.getSchema().getDataSource());
										}
									}
								}
							} else if (schemasToEvaluateQueryOver.contains(construct1.getSchema())
									&& dataSourcesToEvaluateQueryOver.contains(construct1.getSchema().getDataSource())) {
								logger.debug("schemasToEvaluateQueryOver contains construct1.getSchema and dataSourcesToEvaluateQueryOver contains corresponding datasource");
								logger.debug("check constructs2");
								for (CanonicalModelConstruct construct2 : mapping.getConstructs2()) {
									logger.debug("construct2.getSchema: " + construct2.getSchema());
									if (schemasToEvaluateQueryOver.contains(construct2.getSchema())
											&& !dataSourcesToEvaluateQueryOver.contains(construct2.getSchema().getDataSource())) {
										logger.debug("schemasToEvaluateQueryOver contains construct2.getSchema and dataSourcesToEvaluateQueryOver doesn't contain datasource of construct2");
										//looks like constructs2 in this mappings are those from the schema over which the query is posed,
										//and that constructs1 in this mappings are those from the datasources that have been queried
										schemasOfDataSourcesQueried.add(construct1.getSchema());
										break;
									} else if (!schemasToEvaluateQueryOver.contains(construct2.getSchema())
											&& dataSourcesToEvaluateQueryOver.contains(construct2.getSchema().getDataSource())) {
										//looks like constructs1 in this mappings are those from the schema over which the query is posed,
										//and that constructs2 in this mappings are those from the datasources that have been queried
										schemasOfDataSourcesQueried.add(construct2.getSchema());
									} else if (schemasToEvaluateQueryOver.contains(construct2.getSchema())
											&& dataSourcesToEvaluateQueryOver.contains(construct2.getSchema().getDataSource())) {
										logger.debug("schemasToEvaluateQueryOver contains construct2.getSchema and dataSourcesToEvaluateQueryOver contains corresponding datasource");
										logger.debug("bit of an issue here ... need to decide which side is the schema and which side the datasources ... ");
									} else if (!schemasToEvaluateQueryOver.contains(construct2.getSchema())
											&& !dataSourcesToEvaluateQueryOver.contains(construct2.getSchema().getDataSource())) {
										logger.error("schemasToEvaluateQueryOver doesn't contain construct2.getSchema and dataSourcesToEvaluateQueryOver doesn't contain corresponding datasource");
										logger.error("TODO sort this - bit of an issue here ...");
									}
								}
							}

						}
					} else {
						logger.debug("didn't find mapping in mappingsToUtilise, resultInstance can be obtained by multiple mappings - don't add mapping");
					}
				}
			}
		}
		queryResult.setSchemasOfDataSourcesQueried(schemasOfDataSourcesQueried);
		queryResult.setDataspace(query.getDataspace());
		queryResult.setMappings(mappingsUtilised);
		for (Mapping mapping : queryResult.getMappings()) {
			mapping = mappingRepository.fetchConstructs(mapping);
			mapping = mappingRepository.fetchAnnotations(mapping);
		}
		//queryResult.setMappings(query.getMappings());
		//resultTypeRepository.update(queryResult.getResultType());
		//query.setRootOperator(null); //TODO this is a hack to get rid of the issue with the detached resultType that came through the mappingOperator (cascade)
		queryResultService.addQueryResult(queryResult);
		logger.debug("added queryResult");

		Set<Annotation> annotationsToPropagate = new LinkedHashSet<Annotation>();
		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		for (Mapping mapping : queryResult.getMappings()) {
			constrainingModelManagementConstructs.add(mapping);
			List<Annotation> annotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(mapping, user);
			if (annotations != null && annotations.size() > 0)
				annotationsToPropagate.addAll(annotations);
		}

		Set<ModelManagementConstruct> queryResultToAnnotate = new LinkedHashSet<ModelManagementConstruct>();
		queryResultToAnnotate.add(queryResult);
		if (annotationsToPropagate.size() > 0) {
			logger.debug("found annotationsToPropagate: " + annotationsToPropagate.size());
			annotationService.propagateAnnotation(annotationsToPropagate, queryResultToAnnotate, constrainingModelManagementConstructs, false, user);
		}

		// calculate estimated precision, recall, and f-measure for the query result

		//resultTypeRepository.update(queryResult.getResultType());
		//resultTypeRepository.save(queryResult.getResultType());
		//queryRepository.update(query);

		//queryResultRepository.update(queryResult);

		//queryResultService.addQueryResult(queryResult);
		//queryRepository.update(query);
		logger.debug("queryResult.getQuery(): " + queryResult.getQuery());
		logger.debug("queryResult.getQuery().getId(): " + queryResult.getQuery().getId());
		logger.debug("queryResult.getId(): " + queryResult.getId());

		//queryRepository.update(query);
		//queryResultRepository.update(queryResult);
		//queryResultService.addQueryResult(queryResult);
		//logger.debug("queryResult.getQuery(): " + queryResult.getQuery());
		//logger.debug("queryResult.getQuery().getId(): " + queryResult.getQuery().getId());

		//TODO decide whether the queryResult should always be made persistent as default or whether the user should actually be able to choose.
		//TODO for now make all of them persistent, so that we can calculate what percentage of the result tuples has been annotated with feedback
		//TODO but shouldn't be made persistent in here, decide where to do that

		query = null;
		schemasToEvaluateQueryOver = null;
		dataSourcesToEvaluateQueryOver = null;
		mappingsToUtilise = null;

		return queryResult;

		//return null;
	}

	public Set<Mapping> selectMappings(Query query, Set<Mapping> mappings, ControlParameterType thresholdType, double thresholdValue) {
		logger.debug("in selectMappings");
		logger.debug("query: " + query);
		logger.debug("mappings.size(): " + mappings.size());
		logger.debug("mappings: " + mappings);
		logger.debug("thresholdType: " + thresholdType);
		logger.debug("thresholdValue: " + thresholdValue);
		Set<Mapping> selMappings = new LinkedHashSet<Mapping>();

		int num_iterations = 10;

		List<Mapping> candidateMappings = new ArrayList<Mapping>();
		candidateMappings.addAll(mappings);

		logger.debug("candidateMappings: " + candidateMappings);

		ObjectiveFunction objFunc = new MapSelectionObjectiveFunction(query, candidateMappings, thresholdType, thresholdValue);
		((MapSelectionObjectiveFunction) objFunc).setAnnotationService(annotationService);
		((MapSelectionObjectiveFunction) objFunc).setQueryRepository(queryRepository);
		Solution initialSolution = new MapSelectionSolution(candidateMappings);
		MoveManager moveManager = new MapSelectionMoveManager();
		TabuList tabuList = new SimpleTabuList(10); // In OpenTS package

		// Create Tabu Search object
		// could use multithreaded version of it TODO check this out
		logger.debug("initialSolution: " + initialSolution);
		logger.debug("moveManager: " + moveManager);
		logger.debug("objFunc: " + objFunc);
		logger.debug("tabuList: " + tabuList);
		final TabuSearch tabuSearch = new SingleThreadedTabuSearch(initialSolution, moveManager, objFunc, tabuList, new BestEverAspirationCriteria(),
				true); // maximizing = yes/no; false means minimizing, // In OpenTS package
		logger.debug("tabuSearch: " + tabuSearch);
		logger.debug("tabuSearch.isMaximizing(): " + tabuSearch.isMaximizing());
		logger.debug("tabuSearch.getMoveManager(): " + tabuSearch.getMoveManager());
		logger.debug("tabuSearch.getObjectiveFunction(): " + tabuSearch.getObjectiveFunction());
		logger.debug("tabuSearch.getCurrentSolution(): " + tabuSearch.getCurrentSolution());

		// Start solving
		tabuSearch.setIterationsToGo(num_iterations);

		tabuSearch.addTabuSearchListener(new TabuSearchAdapter() {

			@Override
			public void unimprovingMoveMade(TabuSearchEvent evt) {
				logger.debug("unimprovingMoveMade, evt: " + evt);
				logger.debug("tabuSearch.getIterationsCompleted(): " + tabuSearch.getIterationsCompleted());
				logger.debug("tabuSearch.getIterationsToGo(): " + tabuSearch.getIterationsToGo());
				logger.debug("tabuSearch.getBestSolution(): " + tabuSearch.getBestSolution());
				logger.debug("tabuSearch.getCurrentSolution(): " + tabuSearch.getCurrentSolution());
			}

			@Override
			public void newBestSolutionFound(TabuSearchEvent evt) {
				logger.debug("newBestSolutionFound, evt: " + evt);
				logger.debug("tabuSearch.getIterationsCompleted(): " + tabuSearch.getIterationsCompleted());
				logger.debug("tabuSearch.getIterationsToGo(): " + tabuSearch.getIterationsToGo());
				logger.debug("tabuSearch.getBestSolution(): " + tabuSearch.getBestSolution());
				logger.debug("tabuSearch.getCurrentSolution(): " + tabuSearch.getCurrentSolution());
			}

			@Override
			public void newCurrentSolutionFound(TabuSearchEvent evt) {
				logger.debug("newCurrentSolutionFound, evt: " + evt);
				logger.debug("tabuSearch.getIterationsCompleted(): " + tabuSearch.getIterationsCompleted());
				logger.debug("tabuSearch.getIterationsToGo(): " + tabuSearch.getIterationsToGo());
				logger.debug("tabuSearch.getBestSolution(): " + tabuSearch.getBestSolution());
				logger.debug("tabuSearch.getCurrentSolution(): " + tabuSearch.getCurrentSolution());
			}

			@Override
			public void tabuSearchStarted(TabuSearchEvent evt) {
				logger.debug("tabuSearchStarted, evt: " + evt);
				logger.debug("tabuSearch.getIterationsCompleted(): " + tabuSearch.getIterationsCompleted());
				logger.debug("tabuSearch.getIterationsToGo(): " + tabuSearch.getIterationsToGo());
				logger.debug("tabuSearch.getBestSolution(): " + tabuSearch.getBestSolution());
				logger.debug("tabuSearch.getCurrentSolution(): " + tabuSearch.getCurrentSolution());
			}

			@Override
			public void tabuSearchStopped(TabuSearchEvent evt) {
				logger.debug("tabuSearchStopped, evt: " + evt);
				logger.debug("tabuSearch.getIterationsCompleted(): " + tabuSearch.getIterationsCompleted());
				logger.debug("tabuSearch.getIterationsToGo(): " + tabuSearch.getIterationsToGo());
				logger.debug("tabuSearch.getBestSolution(): " + tabuSearch.getBestSolution());
				logger.debug("tabuSearch.getCurrentSolution(): " + tabuSearch.getCurrentSolution());
			}
		});

		logger.debug("tabuSearch.getIterationsCompleted(): " + tabuSearch.getIterationsCompleted());
		logger.debug("tabuSearch.getIterationsToGo(): " + tabuSearch.getIterationsToGo());
		logger.debug("before calling startSolving");
		tabuSearch.startSolving();
		logger.debug("after solving");

		// Show solution
		MapSelectionSolution best = (MapSelectionSolution) tabuSearch.getBestSolution();
		logger.debug("Best Solution:" + best);
		boolean[] selectedMappings = best.getSelectedMappings();
		logger.debug("best.selectedMappings: " + selectedMappings);

		for (int i = 0; i < selectedMappings.length; i++) {
			logger.debug("selectedMappings[i]: " + selectedMappings[i]);
			logger.debug("candidate mapping " + candidateMappings.get(i).toString() + "\t" + selectedMappings[i]);
			if (selectedMappings[i]) {
				logger.debug("candidate mapping selected");
				selMappings.add(candidateMappings.get(i));
			}
		}

		logger.debug("selMappings: " + selMappings);
		return selMappings;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.QueryService#addQuery(uk.ac.manchester.dataspaces.domain.models.query.Query)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void addQuery(Query query) {
		String queryString = query.getQueryString();
		logger.debug("queryString: " + queryString);
		Set<Schema> schemasToEvaluateQueryOver = query.getSchemas();
		CommonTree ast = parser.parseSQL(queryString);
		logger.debug("ast: " + ast.toStringTree());
		if (schemasToEvaluateQueryOver != null && schemasToEvaluateQueryOver.size() > 0) {
			logger.debug("passing schema into globalTranslator");
			logger.debug("schemasToEvaluateQueryOver: " + schemasToEvaluateQueryOver);
			query = globalTranslator.translateAstIntoQuery(query, queryString, ast, schemasToEvaluateQueryOver);
		} else
			query = globalTranslator.translateAstIntoQuery(query, queryString, ast);
		logger.debug("query: " + query);

		if (query.getRootOperator() != null) {
			MappingOperator rootOperator = query.getRootOperator();
			this.saveResultTypeForMappingOperator(rootOperator);

		}

		queryRepository.save(query);
		queryRepository.flush();
	}

	//same code as in predefinedMappingsLoaderServiceImpl and mappingServiceImpl
	/*
	private Set<ScanOperator> getScanOperatorsOfQuery(MappingOperator mappingOperator) {
		//logger.debug("in getScanOperatorsOfMapping");
		Set<ScanOperator> scanOperators = new HashSet<ScanOperator>();
		//logger.debug("mappingOperator: " + mappingOperator);
		mappingOperator.addMappingUsedForExpansion(mappingOperator.getMapping());
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			scanOperators.addAll(getScanOperatorsOfQuery(mappingOperator.getLhsInput()));
			scanOperators.addAll(getScanOperatorsOfQuery(mappingOperator.getRhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ReduceOperator) {
			scanOperators.addAll(getScanOperatorsOfQuery(mappingOperator.getLhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ScanOperator) {
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			scanOperators.add(scanOperator);
			return scanOperators;
		} else
			logger.error("unexpected operator");
		return scanOperators;
	}
	*/

	//same code as in mappingServiceImpl
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	private void saveResultTypeForMappingOperator(MappingOperator mappingOperator) {
		ResultType resultType = mappingOperator.getResultType();
		resultTypeRepository.save(resultType);
		if (mappingOperator.getLhsInput() != null)
			saveResultTypeForMappingOperator(mappingOperator.getLhsInput());
		if (mappingOperator.getRhsInput() != null)
			saveResultTypeForMappingOperator(mappingOperator.getRhsInput());
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.QueryService#deleteQuery(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteQuery(Long queryId) {
		// TODO
		queryRepository.delete(queryRepository.find(queryId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.QueryService#findQuery(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public Query findQuery(Long queryId) {
		return queryRepository.find(queryId);
	}

	/**
	 * @param queryRepository the queryRepository to set
	 */
	public void setQueryRepository(QueryRepository queryRepository) {
		this.queryRepository = queryRepository;
	}

	/**
	 * @return the queryRepository
	 */
	public QueryRepository getQueryRepository() {
		return queryRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<Query, Long> getRepository() {
		return queryRepository;
	}

}
