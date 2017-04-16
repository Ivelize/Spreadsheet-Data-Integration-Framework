package uk.ac.manchester.dstoolkit.service.impl.query.queryexpander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.RenameOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperationType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.TypeCastOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryexpander.QueryExpanderService;

@Transactional(readOnly = true)
@Service(value = "queryExpanderService")
public class QueryExpanderServiceImpl implements QueryExpanderService {

	//TODO if there isn't a distinct in the original query, use union all rather than union in expansion
	//TODO test expander properly, test that each mappingOperator has the correct mapping associated with it
	//TODO sort out rename

	private static Logger logger = Logger.getLogger(QueryExpanderServiceImpl.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	//@Autowired
	//@Qualifier("superAbstractRepository")
	//private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("superLexicalRepository")
	private SuperLexicalRepository superLexicalRepository;

	//private MappingOperator queryRootOperator;
	private Query expandedQuery;
	private Set<Schema> schemasQueryIsPosedOver;
	private Set<DataSource> dataSourcesToEvaluateQueryOver;
	private Set<Mapping> mappingsToUseForExpansion;
	private Set<Mapping> mappingsUsedForExpansion;
	private Set<Schema> schemasForDataSourcesToEvaluateQueryOver;
	private Map<CanonicalModelConstruct, List<ScanOperator>> constructsQueriedScanOpsMap;
	private Set<ScanOperator> scanOperatorsExpanded;

	//TODO only the root mappingOperator of a mapping used for expansion has the mappingsUsedForExpansion adjusted to contain the mapping, none of the others have
	//TODO not sure whether that's gonna be a problem

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.query.queryexpander.QueryExpanderService#expandQuery(uk.ac.manchester.dataspaces.domain.models.query.Query)
	 */
	public Query expandQuery(Query queryToExpand, Set<Mapping> mappingsToUtiliseForExpansion,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in expandQuery");
		mappingsUsedForExpansion = new LinkedHashSet<Mapping>();
		this.mappingsToUseForExpansion = mappingsToUtiliseForExpansion;
		queryToExpand.getSuperAbstractsQueried();
		this.constructsQueriedScanOpsMap = new HashMap<CanonicalModelConstruct, List<ScanOperator>>();
		this.scanOperatorsExpanded = new HashSet<ScanOperator>();
		this.schemasQueryIsPosedOver = queryToExpand.getSchemas();
		logger.debug("schemasQueryIsPosedOver: " + schemasQueryIsPosedOver);
		//this.queryRootOperator = queryToExpand.getRootOperator();
		//this.dataSourcesToEvaluateQueryOver = queryRepository.getAllDataSourcesForQueryWithId(queryToExpand.getId());
		this.dataSourcesToEvaluateQueryOver = queryToExpand.getDataSources();
		logger.debug("dataSourcesToEvaluateQueryOver: " + dataSourcesToEvaluateQueryOver);
		/*
		if (mappingsToUseForExpansion != null && controlParameters != null && controlParameters.containsKey("thresholdType")
				&& controlParameters.containsKey("thresholdValue"))
			this.mappingsToUseForExpansion = mappingsToUseForExpansion;
		//else
		//	this.mappingsToUseForExpansion = queryRepository.getAllMappingsForQueryWithId(queryToExpand.getId());
		 */
		logger.debug("mappingsToUseForExpansion: " + this.mappingsToUseForExpansion);

		if (dataSourcesToEvaluateQueryOver == null || dataSourcesToEvaluateQueryOver.size() == 0) {
			dataSourcesToEvaluateQueryOver = getAllDataSourcesToEvaluateQueryOver();
			queryToExpand.setDataSources(dataSourcesToEvaluateQueryOver);
		}

		this.schemasForDataSourcesToEvaluateQueryOver = getSchemasForDataSourceToEvaluateQueryOver(dataSourcesToEvaluateQueryOver);
		logger.debug("schemasForDataSourcesToEvaluateQueryOver: " + schemasForDataSourcesToEvaluateQueryOver);

		if (this.mappingsToUseForExpansion == null || this.mappingsToUseForExpansion.size() == 0) {
			Set<SuperAbstract> superAbstractsQueried = queryToExpand.getSuperAbstractsQueried();
			if (superAbstractsQueried != null && !superAbstractsQueried.isEmpty()) {
				logger.debug("found superAbstractsQueried: " + superAbstractsQueried);
				Set<CanonicalModelConstruct> constructsQueried = new HashSet<CanonicalModelConstruct>();
				for (SuperAbstract sa : superAbstractsQueried) {
					constructsQueried.add(sa);
				}
				logger.debug("get mappings between constructsQueried and targetSchema");

				this.mappingsToUseForExpansion = getAllMappingsBetweenConstructsQueriedAndTargetSchemas(constructsQueried,
						schemasForDataSourcesToEvaluateQueryOver);
			} else {
				logger.debug("didn't find constructsQueried - get mappings between source- and target schema");
				this.mappingsToUseForExpansion = getAllMappingsBetweenSourceAndTargetSchemas(schemasQueryIsPosedOver,
						schemasForDataSourcesToEvaluateQueryOver);
			}

			logger.debug("mappingsToUseForExpansion: " + this.mappingsToUseForExpansion);

			if (controlParameters != null && controlParameters.containsKey("thresholdType") && controlParameters.containsKey("thresholdValue")) {
				logger.debug("controlParameters contain thresholdType and thresholdValue - use selectMappings");
				ControlParameterType thresholdType = null;
				if (controlParameters.containsKey(ControlParameterType.PRECISION_THRESHOLD))
					thresholdType = ControlParameterType.PRECISION_THRESHOLD;
				else if (controlParameters.containsKey(ControlParameterType.RECALL_THRESHOLD))
					thresholdType = ControlParameterType.RECALL_THRESHOLD;
				else
					logger.error("unexpected thresholdType: " + controlParameters.get("thresholdType"));
				double thresholdValue = Double.valueOf(controlParameters.get(thresholdType).getValue()).doubleValue();
				logger.debug("thresholdType: " + thresholdType);
				logger.debug("thresholdValue: " + thresholdValue);
				Set<Mapping> selectedMappings = queryService.selectMappings(queryToExpand, this.mappingsToUseForExpansion, thresholdType,
						thresholdValue);
				this.mappingsToUseForExpansion = selectedMappings;
			}

			//queryToExpand.setMappings(mappingsToUseForExpansion);
		}
		logger.debug("mappingsToUseForExpansion: " + this.mappingsToUseForExpansion);

		if (this.mappingsToUseForExpansion != null && this.mappingsToUseForExpansion.size() > 0) {
			logger.debug("found mappings to expand query with");
			expandedQuery = expand(queryToExpand, this.mappingsToUseForExpansion);

			/*
			MappingOperator rootOperatorOfExpandedQuery = expandedQuery.getRootOperatorOfExpandedQuery();
			if (this.queryRootOperator != null && this.queryRootOperator instanceof ReduceOperator) {
				queryRootOperator.setLhsInput(rootOperatorOfExpandedQuery);
				queryRootOperator.addAllMappingsUsedForExpansion(rootOperatorOfExpandedQuery.getMappingsUsedForExpansion());
				expandedQuery.setRootOperatorOfExpandedQuery(queryRootOperator);
			}
			*/

			/*
			if (!expandedQuery.getDataSources().equals(queryRepository.getAllDataSourcesForQueryWithId(queryToExpand.getId()))) {
				logger.error("dataSources different for query before and after expansion");
				expandedQuery.setDataSources(queryToExpand.getDataSources());
			}
			;
			if (expandedQuery.getDataspace() == null || !expandedQuery.getDataspace().equals(queryToExpand.getDataspace())) {
				logger.error("dataspace different for query before and after expansion");
				logger.error("queryToExpand.getDataspace(): " + queryToExpand.getDataspace());
				logger.error("expandedQuery.getDataspace(): " + expandedQuery.getDataspace());
				expandedQuery.setDataspace(queryToExpand.getDataspace());
			}
			;
			if (!expandedQuery.getMappings().equals(mappingsUsedForExpansion)) {
				logger.error("mappings for expandedQuery different from mappungsUsedForExpansion");
				expandedQuery.setMappings(mappingsUsedForExpansion);
			}
			;
			if (!expandedQuery.getQueryName().equals(queryToExpand.getQueryName())) {
				logger.error("queryName different for query before and after expansion");
				expandedQuery.setQueryName(queryToExpand.getQueryName());
			}
			;
			if (!expandedQuery.getQueryString().equals(queryToExpand.getQueryString())) {
				logger.error("queryString different for query before and after expansion");
				expandedQuery.setQueryString(queryToExpand.getQueryString());
			}
			;
			if (!expandedQuery.getDescription().equals(queryToExpand.getDescription())) {
				logger.error("description different for query before and after expansion");
				expandedQuery.setDescription(queryToExpand.getDescription());
			}
			if (!expandedQuery.getSchemas().equals(queryToExpand.getSchemas())) {
				logger.error("schemas different for query before and after expansion");
				expandedQuery.setSchemas(queryToExpand.getSchemas());
			}
			;
			if (expandedQuery.getUser() == null || !expandedQuery.getUser().equals(queryToExpand.getUser())) {
				logger.error("user different for query before and after expansion");
				expandedQuery.setUser(queryToExpand.getUser());
			}
			;
			*/
		} else {
			logger.debug("no mappings found - return same query");
			expandedQuery = queryToExpand;
			expandedQuery.setRootOperatorOfExpandedQuery(queryToExpand.getRootOperator());
		}

		return expandedQuery;
	}

	protected Query expand(Query queryToExpand, Set<Mapping> mappingsToUtilise) {
		logger.debug("in expand");
		//TODO deal with rename\

		//Set<Mapping> mappingsToUtilise = queryToExpand.getMappings();
		logger.debug("mappingsToUtilise: " + mappingsToUtilise);
		logger.debug("mappingsToUtilise.size(): " + mappingsToUtilise.size());
		Set<DataSource> dataSourcesToEvaluateQueryOver = queryRepository.getAllDataSourcesForQueryWithId(queryToExpand.getId());
		logger.debug("dataSourcesToEvaluateQueryOver: " + dataSourcesToEvaluateQueryOver);
		if (dataSourcesToEvaluateQueryOver != null)
			logger.debug("dataSourcesToEvaluateQueryOver.size(): " + dataSourcesToEvaluateQueryOver.size());

		Map<ScanOperator, MappingOperator> scanOperatorsInQueryWithParentMap = getAllScanOperatorsInQueryWithTheirParentOperatorAndFillConstructsQueriedScanOpsMap(
				queryToExpand.getRootOperator(), null);

		logger.debug("scanOperatorsInQueryWithParentMap.size(): " + scanOperatorsInQueryWithParentMap.size());
		Set<ScanOperator> scanOperators = scanOperatorsInQueryWithParentMap.keySet();
		logger.debug("scanOperators: " + scanOperators);
		logger.debug("scanOperators.size(): " + scanOperators.size());
		Map<ScanOperator, List<Mapping>> mappingsGroupedByScanOperator = groupMappingsByScanOperator(scanOperators, mappingsToUtilise);

		List<List<Mapping>> groupedMappingsList = generateGroupedMappingsList(mappingsGroupedByScanOperator);
		List<List<Mapping>> crossProductOfMappings = cartesianProduct(groupedMappingsList);

		ReduceOperator rootReduceOperator = null; //should be reduceOperator
		MappingOperator previousRootOperator = null;
		int i = 0;
		for (List<Mapping> currentMappings : crossProductOfMappings) {
			logger.debug("currentMappings: " + currentMappings);
			logger.debug("queryToExpand.getRootOperator(): " + queryToExpand.getRootOperator());
			logger.debug("queryToExpand.getRootOperator().getId(): " + queryToExpand.getRootOperator().getId());
			this.scanOperatorsExpanded.clear();
			//Set<Mapping> mappingsForTargetDataSource = getMappingsForTargetDataSource(mappingsToUtilise, dataSource);
			//logger.debug("mappingsForTargetDataSource: " + mappingsForTargetDataSource);
			MappingOperator rootOperator = null;
			//if (i == 0) {
			//	logger.debug("i == 0, take original operatorTree");
			//	rootOperator = queryToExpand.getRootOperator();
			//} else {
			//	logger.debug("i != 0, i: " + i + ", clone operatorTree");
			rootOperator = cloneMappingOperatorTreeAndReplaceScansWithMappings(queryToExpand.getRootOperator(), null, currentMappings);
			//}
			logger.debug("rootOperator after cloneAndExpand: " + rootOperator);
			logger.debug("rootOperator.getId(): " + rootOperator.getId());
			if (rootReduceOperator == null && rootOperator instanceof ReduceOperator) {
				logger.debug("rootOperator is reduceOperator");
				rootReduceOperator = (ReduceOperator) rootOperator;
			}
			logger.debug("rootReduceOperator: " + rootReduceOperator);
			//rootOperator = expand(rootOperator, null, mappingsForTargetDataSource);
			//logger.debug("rootOperator after expand: " + rootOperator);
			String subQueryPrefix = "";
			if (previousRootOperator == null) {
				logger.debug("no previousRootOperator");
				previousRootOperator = rootOperator;
			} else {
				logger.debug("create union");
				logger.debug("previousRootOperator: " + previousRootOperator);
				logger.debug("rootOperator: " + rootOperator);

				if (!(previousRootOperator instanceof SetOperator)) {
					logger.debug("previousRootOperator not setOperator");
					previousRootOperator.setVariableName("exUnion1");
					rootOperator.setVariableName("exUnion2");
					subQueryPrefix = "exUnion1.";
				} else {
					logger.debug("previousRootOperator is setOperator");
					previousRootOperator.setVariableName(subQueryPrefix + "exUnion1");
					rootOperator.setVariableName(subQueryPrefix + "exUnion2");
					subQueryPrefix = subQueryPrefix + "exUnion1";
				}

				SetOperator setOperator = new SetOperator(previousRootOperator, rootOperator, SetOperationType.UNION);
				setOperator.setResultType(previousRootOperator.getResultType());
				setOperator.setVariableName(previousRootOperator.getVariableName() + "_" + rootOperator.getVariableName());
				setOperator.addAllMappingsUsedForExpansion(previousRootOperator.getMappingsUsedForExpansion());
				setOperator.addAllMappingsUsedForExpansion(rootOperator.getMappingsUsedForExpansion());
				previousRootOperator = setOperator;
				logger.debug("setOperator: " + setOperator);
				logger.debug("setOperator.getDataSource(): " + setOperator.getDataSource());
				//TODO think about this - currently no check or attempt to set dataSource, optimiser treats each of the
				//inputs of the union as subquery anyway, so don't think it would make a difference, but need to check
				//this in the optimiser whether it checks - I think it does, so might need some more work here
				//and some more tests
			}
			i++;
		}
		logger.debug("rootReduceOperator: " + rootReduceOperator);
		if (rootReduceOperator != null && !(previousRootOperator instanceof ReduceOperator)) {
			logger.debug("got rootReduceOperator, add to expanded queryPlan");
			ReduceOperator newReduceOperator = new ReduceOperator();
			newReduceOperator.setAndOr(rootReduceOperator.getAndOr());
			newReduceOperator.setDataSource(previousRootOperator.getDataSource());
			newReduceOperator.setReconcilingExpression(rootReduceOperator.getReconcilingExpression());
			newReduceOperator.setResultType(rootReduceOperator.getResultType());
			newReduceOperator.setSuperLexicals(rootReduceOperator.getSuperLexicals());
			newReduceOperator.setVariableName(rootReduceOperator.getVariableName());

			newReduceOperator.setLhsInput(previousRootOperator);
			newReduceOperator.setMappingsUsedForExpansion(previousRootOperator.getMappingsUsedForExpansion());
			logger.debug("newReduceOperator.getDataSource(): " + newReduceOperator.getDataSource());
			previousRootOperator = newReduceOperator;
		}
		queryToExpand.setRootOperatorOfExpandedQuery(previousRootOperator);
		/*
		if (queryToExpand.getMappings() == null || !queryToExpand.getMappings().equals(mappingsUsedForExpansion)) {
			logger.debug("mappingsUsedForExpansion: " + mappingsUsedForExpansion);
			queryToExpand.setMappings(mappingsUsedForExpansion);
		}
		*/

		return queryToExpand;
	}

	protected List<List<Mapping>> generateGroupedMappingsList(Map<ScanOperator, List<Mapping>> mappingsGroupedByScanOperator) {
		logger.debug("in generateGroupedMappingsList");
		List<List<Mapping>> groupedMappingsList = new ArrayList<List<Mapping>>();
		Set<ScanOperator> scanOperators = mappingsGroupedByScanOperator.keySet();
		for (ScanOperator scanOperator : scanOperators) {
			logger.debug("scanOperator: " + scanOperator);
			List<Mapping> mappings = mappingsGroupedByScanOperator.get(scanOperator);
			groupedMappingsList.add(mappings);
			logger.debug("added mappings to groupedMappingsList, mappings.size(): " + mappings.size());
		}
		return groupedMappingsList;
	}

	protected Map<ScanOperator, MappingOperator> getAllScanOperatorsInQueryWithTheirParentOperatorAndFillConstructsQueriedScanOpsMap(
			MappingOperator mappingOperator, MappingOperator parent) {
		//has side effect that this.constructsQueriedScanOpsMap is filled too
		logger.debug("in getAllScanOperatorsInQueryWithTheirParentOperator");
		Map<ScanOperator, MappingOperator> scanOperatorsInQueryParentMap = new HashMap<ScanOperator, MappingOperator>();
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			Map<ScanOperator, MappingOperator> lhsInputOperatorMap = getAllScanOperatorsInQueryWithTheirParentOperatorAndFillConstructsQueriedScanOpsMap(
					mappingOperator.getLhsInput(), mappingOperator);
			Map<ScanOperator, MappingOperator> rhsInputOperatorMap = getAllScanOperatorsInQueryWithTheirParentOperatorAndFillConstructsQueriedScanOpsMap(
					mappingOperator.getRhsInput(), mappingOperator);
			if (!lhsInputOperatorMap.isEmpty()) {
				logger.debug("!lhsInputOperatorMap.isEmpty()");
				Set<ScanOperator> scanOperators = lhsInputOperatorMap.keySet();
				for (ScanOperator scanOp : scanOperators) {
					logger.debug("scanOp: " + scanOp);
					logger.debug("lhsInputOperatorMap.get(scanOp): " + lhsInputOperatorMap.get(scanOp));
					scanOperatorsInQueryParentMap.put(scanOp, lhsInputOperatorMap.get(scanOp));
					CanonicalModelConstruct construct = scanOp.getSuperAbstract();
					logger.debug("construct: " + construct);
					if (this.constructsQueriedScanOpsMap.containsKey(construct)) {
						logger.debug("constructsQueriedScanOpsMap.containsKey(construct)");
						this.constructsQueriedScanOpsMap.get(construct).add(scanOp);
					} else {
						logger.debug("not constructsQueriedScanOpsMap.containsKey(construct)");
						List<ScanOperator> scanOpsList = new ArrayList<ScanOperator>();
						scanOpsList.add(scanOp);
						this.constructsQueriedScanOpsMap.put(construct, scanOpsList);
					}
				}
			}
			if (!rhsInputOperatorMap.isEmpty()) {
				logger.debug("!rhsInputOperatorMap.isEmpty()");
				Set<ScanOperator> scanOperators = rhsInputOperatorMap.keySet();
				for (ScanOperator scanOp : scanOperators) {
					logger.debug("scanOp: " + scanOp);
					logger.debug("rhsInputOperatorMap.get(scanOp): " + rhsInputOperatorMap.get(scanOp));
					scanOperatorsInQueryParentMap.put(scanOp, rhsInputOperatorMap.get(scanOp));
					CanonicalModelConstruct construct = scanOp.getSuperAbstract();
					logger.debug("construct: " + construct);
					if (this.constructsQueriedScanOpsMap.containsKey(construct)) {
						logger.debug("constructsQueriedScanOpsMap.containsKey(construct)");
						this.constructsQueriedScanOpsMap.get(construct).add(scanOp);
					} else {
						logger.debug("not constructsQueriedScanOpsMap.containsKey(construct)");
						List<ScanOperator> scanOpsList = new ArrayList<ScanOperator>();
						scanOpsList.add(scanOp);
						this.constructsQueriedScanOpsMap.put(construct, scanOpsList);
					}
				}
			}
			return scanOperatorsInQueryParentMap;
		} else if (mappingOperator instanceof ReduceOperator) {
			Map<ScanOperator, MappingOperator> inputOperatorMap = getAllScanOperatorsInQueryWithTheirParentOperatorAndFillConstructsQueriedScanOpsMap(
					mappingOperator.getLhsInput(), mappingOperator);
			if (!inputOperatorMap.isEmpty()) {
				Set<ScanOperator> scanOperators = inputOperatorMap.keySet();
				for (ScanOperator scanOp : scanOperators) {
					logger.debug("scanOp: " + scanOp);
					logger.debug("inputOperatorMap(scanOp): " + inputOperatorMap.get(scanOp));
					scanOperatorsInQueryParentMap.put(scanOp, inputOperatorMap.get(scanOp));
					CanonicalModelConstruct construct = scanOp.getSuperAbstract();
					logger.debug("construct: " + construct);
					if (this.constructsQueriedScanOpsMap.containsKey(construct)) {
						logger.debug("constructsQueriedScanOpsMap.containsKey(construct)");
						this.constructsQueriedScanOpsMap.get(construct).add(scanOp);
					} else {
						logger.debug("not constructsQueriedScanOpsMap.containsKey(construct)");
						List<ScanOperator> scanOpsList = new ArrayList<ScanOperator>();
						scanOpsList.add(scanOp);
						this.constructsQueriedScanOpsMap.put(construct, scanOpsList);
					}
				}
			}
			return scanOperatorsInQueryParentMap;
		} else if (mappingOperator instanceof ScanOperator) {
			logger.debug("got ScanOperator");
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			logger.debug("scanOperator: " + scanOperator);
			logger.debug("parent: " + parent);
			scanOperatorsInQueryParentMap.put(scanOperator, parent);
			CanonicalModelConstruct construct = scanOperator.getSuperAbstract();
			logger.debug("construct: " + construct);
			if (this.constructsQueriedScanOpsMap.containsKey(construct)) {
				logger.debug("constructsQueriedScanOpsMap.containsKey(construct)");
				this.constructsQueriedScanOpsMap.get(construct).add(scanOperator);
			} else {
				logger.debug("not constructsQueriedScanOpsMap.containsKey(construct)");
				List<ScanOperator> scanOpsList = new ArrayList<ScanOperator>();
				scanOpsList.add(scanOperator);
				this.constructsQueriedScanOpsMap.put(construct, scanOpsList);
			}
			return scanOperatorsInQueryParentMap;
		} else
			logger.error("unexpected operator");
		return scanOperatorsInQueryParentMap;
	}

	protected MappingOperator cloneMappingOperatorTreeAndReplaceScansWithMappings(MappingOperator queryOperatorToCloneOrReplace,
			MappingOperator parent, List<Mapping> currentMappings) {
		logger.debug("in cloneMappingOperatorTreeAndReplaceScansWithMappings");
		logger.debug("queryOperatorToCloneOrReplace: " + queryOperatorToCloneOrReplace);
		logger.debug("queryOperatorToCloneOrReplace.getId(): " + queryOperatorToCloneOrReplace.getId());
		logger.debug("parent: " + parent);
		if (parent != null)
			logger.debug("parent.getId(): " + parent.getId());
		logger.debug("currentMappings: " + currentMappings);
		MappingOperator newMappingOperator = null;
		if (queryOperatorToCloneOrReplace instanceof ReduceOperator) {
			logger.debug("queryOperatorToCloneOrReplace is ReduceOperator - clone");
			ReduceOperator reduceOperator = (ReduceOperator) queryOperatorToCloneOrReplace;
			newMappingOperator = new ReduceOperator();
			ReduceOperator newReduceOperator = (ReduceOperator) newMappingOperator;
			newReduceOperator.setSuperLexicals(reduceOperator.getSuperLexicals());
		} else if (queryOperatorToCloneOrReplace instanceof JoinOperator) {
			logger.debug("queryOperatorToCloneOrReplace is JoinOperator - clone");
			JoinOperator joinOperator = (JoinOperator) queryOperatorToCloneOrReplace;
			newMappingOperator = new JoinOperator();
			JoinOperator newJoinOperator = (JoinOperator) newMappingOperator;
			newJoinOperator.setPredicates(joinOperator.getPredicates());
		} else if (queryOperatorToCloneOrReplace instanceof SetOperator) {
			logger.debug("queryOperatorToCloneOrReplace is SetOperator - clone");
			SetOperator setOperator = (SetOperator) queryOperatorToCloneOrReplace;
			newMappingOperator = new SetOperator();
			SetOperator newSetOperator = (SetOperator) newMappingOperator;
			newSetOperator.setSetOpType(setOperator.getSetOpType());
		} else if (queryOperatorToCloneOrReplace instanceof ScanOperator) {
			logger.debug("queryOperatorToCloneOrReplace is ScanOperator - expand");
			ScanOperator scanOperator = (ScanOperator) queryOperatorToCloneOrReplace;
			newMappingOperator = getExpansionForOperator(scanOperator, parent, currentMappings);
			logger.debug("newMappingOperator: " + newMappingOperator);
			logger.debug("newMappingOperator.getMapping(): " + newMappingOperator.getMapping());
			logger.debug("newMappingOperator.lhsInput: " + newMappingOperator.getLhsInput());
			logger.debug("newMappingOperator.rhsInput: " + newMappingOperator.getRhsInput());
			mappingsUsedForExpansion.add(newMappingOperator.getMapping());
			/*
			newMappingOperator = new ScanOperator();
			ScanOperator newScanOperator = (ScanOperator) newMappingOperator;
			newScanOperator.setSuperAbstract(scanOperator.getSuperAbstract());
			newScanOperator.setPredicates(scanOperator.getPredicates());
			*/
		} else
			logger.error("unexpected operator, queryOperatorToCloneOrReplace: " + queryOperatorToCloneOrReplace);
		logger.debug("queryOperatorToCloneOrReplace.getAndOr(): " + queryOperatorToCloneOrReplace.getAndOr());
		logger.debug("queryOperatorToCloneOrReplace.getReconcilingExpression(): " + queryOperatorToCloneOrReplace.getReconcilingExpression());
		logger.debug("queryOperatorToCloneOrReplace.getResultType(): " + queryOperatorToCloneOrReplace.getResultType());
		logger.debug("queryOperatorToCloneOrReplace.getVariableName(): " + queryOperatorToCloneOrReplace.getVariableName());

		logger.debug("newMappingOperator.getAndOr(): " + newMappingOperator.getAndOr());
		logger.debug("newMappingOperator.getReconcilingExpression(): " + newMappingOperator.getReconcilingExpression());
		logger.debug("newMappingOperator.getResultType(): " + newMappingOperator.getResultType());
		logger.debug("newMappingOperator.getVariableName(): " + newMappingOperator.getVariableName());
		if (newMappingOperator.getAndOr() == null && queryOperatorToCloneOrReplace.getAndOr() != null)
			newMappingOperator.setAndOr(queryOperatorToCloneOrReplace.getAndOr());
		if (newMappingOperator.getReconcilingExpression() == null && queryOperatorToCloneOrReplace.getReconcilingExpression() != null)
			newMappingOperator.setReconcilingExpression(queryOperatorToCloneOrReplace.getReconcilingExpression());
		if (newMappingOperator.getResultType() == null && queryOperatorToCloneOrReplace.getResultType() != null)
			newMappingOperator.setResultType(queryOperatorToCloneOrReplace.getResultType());
		if (newMappingOperator.getVariableName() == null && queryOperatorToCloneOrReplace.getVariableName() != null)
			newMappingOperator.setVariableName(queryOperatorToCloneOrReplace.getVariableName());

		logger.debug("newMappingOperator.getAndOr(): " + newMappingOperator.getAndOr());
		logger.debug("newMappingOperator.getReconcilingExpression(): " + newMappingOperator.getReconcilingExpression());
		logger.debug("newMappingOperator.getResultType(): " + newMappingOperator.getResultType());
		logger.debug("newMappingOperator.getVariableName(): " + newMappingOperator.getVariableName());

		logger.debug("newMappingOperator (should have reconcilingExpression): " + newMappingOperator);
		logger.debug("newMappingOperator.getReconcilingExpression: " + newMappingOperator.getReconcilingExpression());
		//newMappingOperator.setDataSource(mappingOperator.getDataSource());
		if (queryOperatorToCloneOrReplace.getLhsInput() != null) {
			newMappingOperator.setLhsInput(cloneMappingOperatorTreeAndReplaceScansWithMappings(queryOperatorToCloneOrReplace.getLhsInput(),
					newMappingOperator, currentMappings));
			newMappingOperator.addAllMappingsUsedForExpansion(newMappingOperator.getLhsInput().getMappingsUsedForExpansion());
		}
		if (queryOperatorToCloneOrReplace.getRhsInput() != null) {
			newMappingOperator.setRhsInput(cloneMappingOperatorTreeAndReplaceScansWithMappings(queryOperatorToCloneOrReplace.getRhsInput(),
					newMappingOperator, currentMappings));
			newMappingOperator.addAllMappingsUsedForExpansion(newMappingOperator.getRhsInput().getMappingsUsedForExpansion());
		}
		logger.debug("newMappingOperator: " + newMappingOperator);
		logger.debug("newMappingOperator.getDataSource(): " + newMappingOperator.getDataSource());
		logger.debug("about to set dataSource");
		if (newMappingOperator.getLhsInput() != null) {
			if (newMappingOperator.getRhsInput() != null) {
				DataSource lhsDataSource = newMappingOperator.getLhsInput().getDataSource();
				DataSource rhsDataSource = newMappingOperator.getRhsInput().getDataSource();
				logger.debug("lhsDataSource: " + lhsDataSource);
				logger.debug("rhsDataSource: " + rhsDataSource);
				if (lhsDataSource != null && rhsDataSource != null) {
					logger.debug("lhsDataSource && rhsDataSource != null");
					if (lhsDataSource.equals(rhsDataSource)) {
						logger.debug("lhsDataSource == rhsDataSource, set dataSource of newMappingOperator to same dataSource");
						logger.debug("lhsDataSource: " + lhsDataSource);
						logger.debug("rhsDataSource: " + rhsDataSource);
						logger.debug("newMappingOperator.getDatasource(): " + newMappingOperator.getDataSource());
						if (newMappingOperator.getDataSource() != null && newMappingOperator.getDataSource().equals(lhsDataSource))
							logger.debug("newMappingOperator has already correct dataSource - nothing to do");
						else {
							logger.debug("newMappingOperator on wrong datasource or null dataSource, change this");
							newMappingOperator.setDataSource(lhsDataSource);
						}
					} else {
						logger.debug("lhsDataSource and rhsDataSource different - set dataSource of newMappingOperator to null");
						logger.debug("lhsDataSource: " + lhsDataSource);
						logger.debug("rhsDataSource: " + rhsDataSource);
						logger.debug("newMappingOperator.getDatasource(): " + newMappingOperator.getDataSource());
						if (newMappingOperator.getDataSource() != null) {
							logger.debug("dataSource of newMappingOperator != null - change this");
							newMappingOperator.setDataSource(null);
						} else
							logger.debug("newMappingOperator already on null dataSource - nothing to do");
					}
				} else {
					logger.debug("lhsDataSource || rhsDataSource == null, set dataSource of mappingOperator to null");
					newMappingOperator.setDataSource(null);
				}
			} else {
				logger.debug("newMappingOperator without RhsInput, could be reduce, check data source is correct");
				DataSource lhsDataSource = newMappingOperator.getLhsInput().getDataSource();
				logger.debug("lhsDataSource: " + lhsDataSource);
				if (lhsDataSource != null) {
					logger.debug("lhsDataSource != null");
					if (newMappingOperator.getDataSource() != null && newMappingOperator.getDataSource().equals(lhsDataSource))
						logger.debug("newMappingOperator already on same dataSource as lhsDataSource - nothing to do");
					else {
						logger.debug("newMappingOperator on different dataSource than lhsDataSource or null - ignore this");
						//newMappingOperator.setDataSource(lhsDataSource);
						logger.debug("newMappingOperator.getDataSource(): " + newMappingOperator.getDataSource());
					}
				}
				/*
				if (newMappingOperator instanceof ReduceOperator) {
					logger.debug("newMappingOperator is Reduce - check resultType");
					MappingOperator lhsInput = newMappingOperator.getLhsInput();
					ResultType lhsInputResultType = lhsInput.getResultType();
					Map<String, ResultField> resultFields = new HashMap<String, ResultField>();

					int fieldNumber = 0;
					String reconcilingExpression = newMappingOperator.getReconcilingExpression().getExpression();
					logger.debug("reconcilingExpression: " + reconcilingExpression);
					Map<String, SuperLexical> superLexicals = ((ReduceOperator) newMappingOperator).getSuperLexicals();
					logger.debug("superLexicals: " + superLexicals);
					logger.debug("compare order");
					Set<String> resultFieldNames = newMappingOperator.getResultType().getResultFields().keySet();
					logger.debug("resultFieldNames: " + resultFieldNames);
					for (String resultFieldName : resultFieldNames) {
						logger.debug("resultFieldName of newMappingOperator resultType: " + resultFieldName);
						if (resultFieldName.contains(".")) {
							logger.debug("resultFieldName contains .");
							resultFieldName = resultFieldName.substring(resultFieldName.indexOf("."));
							logger.debug("resultFieldName without .: " + resultFieldName);
						}

						if (lhsInputResultType.getResultFields().containsKey(resultFieldName)) {
							logger.debug("lhsInputResultType has resultField with same name: " + resultFieldName);

							ResultField newResultField = new ResultField(resultFieldName, lhsInputResultType.getResultFields().get(resultFieldName)
									.getFieldType());
							newResultField.setCanonicalModelConstruct(lhsInputResultType.getResultFields().get(resultFieldName)
									.getCanonicalModelConstruct());
							int index = resultFields.size();
							if (lhsInputResultType.getResultFields().get(resultFieldName).getIndex() != index) {
								logger.error("index of resultField in new resultType different from old resultField - use new index for now");
								newResultField.setIndex(index);
							} else
								newResultField.setIndex(lhsInputResultType.getResultFields().get(resultFieldName).getIndex());
							resultFields.put(resultFieldName, newResultField);

							if (superLexicals.containsKey(resultFieldName)) {
								superLexicals.put(resultFieldName, (SuperLexical) lhsInputResultType.getResultFields().get(resultFieldName)
										.getCanonicalModelConstruct());
							}
						}

					}
				}
				*/
			}
		} else {
			logger.debug("newMappingOperator without input, should be scans and should have correct data source - TODO check this though");
			logger.debug("newMappingOperator.getDataSource: " + newMappingOperator.getDataSource());
			if (newMappingOperator.getDataSource() == null) {
				logger.error("newMappingOperator without input on null dataSource - TODO sort this");
				logger.error("newMappingOperator: " + newMappingOperator);
			}
		}
		logger.debug("finished setting dataSource, newMappingOperator.getDataSource(): " + newMappingOperator.getDataSource());
		return newMappingOperator;
	}

	/*
	private Set<Mapping> getMappingsForTargetDataSource(Set<Mapping> mappings, DataSource targetDataSource) {
		Set<Mapping> mappingsForTargetDataSource = new HashSet<Mapping>();
		for (Mapping mapping : mappings) {
			Query query2 = mapping.getQuery2();
			if (query2.getDataSources().contains(targetDataSource))
				mappingsForTargetDataSource.add(mapping);
		}
		return mappingsForTargetDataSource;
	}
	*/

	/*
	private MappingOperator expand(MappingOperator mappingOperator, MappingOperator parent, Set<Mapping> mappings) {
		logger.debug("in expand mappingOperator");
		logger.debug("mappingOperator: " + mappingOperator);
		logger.debug("mappings:" + mappings);
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			logger.debug("mappingOperator is joinOperator or setOperator");
			expand(mappingOperator.getLhsInput(), mappingOperator, mappings);
			expand(mappingOperator.getRhsInput(), mappingOperator, mappings);
			DataSource lhsDataSource = mappingOperator.getLhsInput().getDataSource();
			DataSource rhsDataSource = mappingOperator.getRhsInput().getDataSource();
			logger.debug("after exanding input operators");
			logger.debug("mappingOperator: " + mappingOperator);
			logger.debug("lhsDataSource: " + lhsDataSource);
			logger.debug("rhsDataSource: " + rhsDataSource);
			logger.debug("mappingOperator.dataSource: " + mappingOperator.getDataSource());
			if (lhsDataSource.equals(rhsDataSource)) {
				logger.debug("lhsDataSource and rhsDataSource are same");
				if (mappingOperator.getDataSource() == null || !mappingOperator.getDataSource().equals(lhsDataSource)) {
					logger.error("mappingOperator on different source than lhs or rhs, or source is null, should be same, change to same");
					mappingOperator.setDataSource(lhsDataSource);
				} else
					logger.debug("mappingOperator on same source as its input operators, nothing to do");
			} else {
				logger.debug("lhsDataSource and rhsDataSource are different, change dataSource of mappingOperator to null");
				if (mappingOperator.getDataSource() != null)
					logger.debug("mappingOperator.dataSource != null, should be null, change it");
				mappingOperator.setDataSource(null);
			}
		} else if (mappingOperator instanceof ReduceOperator) {
			logger.debug("mappingOperator is reduceOperator");
			expand(mappingOperator.getLhsInput(), mappingOperator, mappings);
		} else if (mappingOperator instanceof ScanOperator) {
			logger.debug("mappingOperator is scanOperator - expand using mappings");
			mappingOperator = getExpansionForOperator(mappingOperator, parent, mappings);
		} else
			logger.error("unexpected operator: " + mappingOperator);

		return mappingOperator;
	}
	*/

	//TODO check which constructs the query goes over, check whether there are many-to-many mappings to be used for expansion, only use many-to-many when all the constructs involved are in query

	protected MappingOperator getExpansionForOperator(MappingOperator operatorToExpand, MappingOperator parent, List<Mapping> mappings) {
		//assume for now it's just a scanoperator
		//TODO sort out rename
		//TODO what about mappings that are the other way round, can that happen - assume that it can
		logger.debug("in getExpansionForOperator");
		logger.debug("operatorToExpand: " + operatorToExpand);
		logger.debug("parent: " + parent);
		logger.debug("mappings: " + mappings);
		if (operatorToExpand instanceof ScanOperator) {
			logger.debug("operator is scanOperator");
			ScanOperator scanOperator = (ScanOperator) operatorToExpand;
			logger.debug("scanOperator: " + scanOperator);
			SuperAbstract superAbstract = scanOperator.getSuperAbstract();
			logger.debug("superAbstract: " + superAbstract);
			for (Mapping mapping : mappings) {
				logger.debug("mapping: " + mapping);
				mappingRepository.fetchConstructs(mapping);
				Query query1 = mapping.getQuery1();
				logger.debug("query1: " + query1);
				//MappingOperator rootQuery1Operator = query1.getRootOperator();
				//logger.debug("rootQuery1Operator: " + rootQuery1Operator);
				//if (mappingRefersToSuperAbstract(rootQuery1Operator, superAbstract)) {

				//TODO sort this out, it doesn't work, the operatorToExpand is from the mapping that was used previously to expand query, not sure why
				//assume to now that if a cmc with the same name is found in query1Constructs, it's the one we're looking for
				//works for the predefinedMappingLoaderService, but need to sort this

				if (mapping.getConstructs1().contains(superAbstract) || mapping.getConstructs2().contains(superAbstract)) {

					boolean foundSAInConstructs1 = false;
					boolean foundSAInConstructs2 = false;

					/*
					logger.debug("check whether a construct with same name as superAbstract is found in query1Constructs");
					for (CanonicalModelConstruct cmc : mapping.getQuery1Constructs()) {
						logger.debug("cmc: " + cmc);
						logger.debug("cmc.getId(): " + cmc.getId());

						if (cmc.getName().equals(superAbstract.getName())) {
							logger.debug("found superAbstract with same name in query1Constructs - TODO rename !!!");
							//TODO sort out rename
							foundSAInQuery1Constructs = true;
						}
					}
					logger.debug("foundSAInQuery1Constructs: " + foundSAInQuery1Constructs);

					if (!foundSAInQuery1Constructs) {
						logger.debug("check whether a construct with same name as superAbstract is found in query2Constructs");
						for (CanonicalModelConstruct cmc : mapping.getQuery2Constructs()) {
							logger.debug("cmc: " + cmc);
							logger.debug("cmc.getId(): " + cmc.getId());

							if (cmc.getName().equals(superAbstract.getName())) {
								logger.debug("found superAbstract with same name in query2Constructs - TODO rename !!!");
								//TODO sort out rename
								foundSAInQuery2Constructs = true;
							}
						}
					}

					if (foundSAInQuery1Constructs || foundSAInQuery2Constructs) {
					*/
					logger.debug("found superAbstract in constructs1 or constructs2");
					logger.debug("mapping.getConstructs1().size(): " + mapping.getConstructs1().size());
					logger.debug("mapping.getConstructs2().size(): " + mapping.getConstructs2().size());

					boolean oneToOneMapping = false;
					boolean oneToManyMapping = false;
					boolean manyToOneMapping = false;
					boolean manyToManyMapping = false;

					//TODO this doesn't work - TODO sort this out

					if (mapping.getConstructs1().contains(superAbstract)) {
						logger.debug("found superAbstract in constructs1");
						foundSAInConstructs1 = true;
					} else {
						logger.debug("found superAbstract in constructs2");
						foundSAInConstructs2 = true;
					}

					logger.debug("foundSAInConstructs1: " + foundSAInConstructs1);
					logger.debug("foundSAInConstructs2: " + foundSAInConstructs2);

					if (mapping.getConstructs1().size() > 1) {
						if (mapping.getConstructs2().size() > 1) {
							logger.debug("manyToManyMapping");
							manyToManyMapping = true;
						} else {
							if (foundSAInConstructs1) {
								logger.debug("manyToOneMapping");
								manyToOneMapping = true;
							} else {
								logger.debug("oneToManyMapping");
								oneToManyMapping = true;
							}
						}
					} else {
						if (mapping.getConstructs2().size() > 1) {
							if (foundSAInConstructs1) {
								logger.debug("oneToManyMapping");
								oneToManyMapping = true;
							} else {
								logger.debug("manyToOneMapping");
								manyToOneMapping = true;
							}
						} else {
							logger.debug("oneToOneMapping");
							oneToOneMapping = true;
						}
					}

					logger.debug("oneToOneMapping: " + oneToOneMapping);
					logger.debug("oneToManyMapping: " + oneToManyMapping);
					logger.debug("manyToOneMapping: " + manyToOneMapping);
					logger.debug("manyToManyMapping: " + manyToManyMapping);

					//TODO tidy up code and finish this

					//TODO think about this: not sure how to expand with toMany Mapping - first choice: make sure the other scanOperator(s) are in query too and 
					//replace all of them with the mapping, don't know what it'll look like though - test this

					//TODO think about this - don't think I need to check which scanOp it is, all of them need to get the information, but is it really
					//the scanOp that should get the information ... isn't it the rootOperator of the mapping?

					//TODO need to do this for the other operators too, i.e., reduce, union, join etc. but mappings have only scanOperators at the moment
					//- probably not, as this needs to be done for rootOperator of mapping, but what if rootOp doesn't have reconcilingExpression, it does,
					//as it's associated with mappingOperator, but it's still got its place with the scanOperator rather than the rootOperator, however,
					//if there is a join operator and the reconcilingExpression isn't actually applicable before the join?
					//TODO check what's in the reconcilingExpression - it's the predicates that need setting/adding, not sure the reconcilingExpression does
					//might have to address this in the optimiser which currently assumes that all the selection predicates are as far down as possible
					//- add predicates to scanOps, as well as and/or
					//if there is a joinOperator in the mapping, need to check what the predicates apply to, but then 
					//variableName might have to be given to scanOperator too - might not be an issue, but I'm not sure

					//TODO check whether it's a one-to-one, many-to-one, one-to-many or many-to-many and make decision based on type of mapping what to do
					//many can be hp, vp or a combination of them
					//TODO add to reconcilingExpression how to deal with another reconcilingExpression that needs to be added to an existing - but
					//it's not the reconcilingExpression that needs setting/adding, it's the predicates - TODO check what's in the reconcilingExpression

					//TODO
					//One To-one with rename: rename operator needs to be applied to predicates in scanoperator to be expanded, 
					//probably applies to all cases with rename but haven't thought about others yet

					//One-to-many with rename: rename could be anywhere in operator tree, can i make sure it's pushed down?
					//Could write method to enforce push-down of rename in mappings

					//One scanoperator to expand with operator tree over many constructs, could be hp, vp or combination, 
					//can check though by checking operator tree, checking provenance or attaching type of correspondence to mapping generated

					//And/or should go in same operator as predicate

					//If many is hp, predicates should probably go to all scanops that go into union with the and/or

					Query queryToReplaceScanOperatorWith;

					if (foundSAInConstructs1) {
						//TODO this might be wrong now - check this
						logger.debug("foundSAInConstructs1 - get query2");
						queryToReplaceScanOperatorWith = mapping.getQuery2();
					} else {
						logger.debug("!foundSAInConstructs2 - get query1");
						queryToReplaceScanOperatorWith = mapping.getQuery1();
					}
					logger.debug("queryToReplaceScanOperatorWith: " + queryToReplaceScanOperatorWith);
					MappingOperator rootQueryOperator = queryToReplaceScanOperatorWith.getRootOperator();
					logger.debug("rootQueryOperator: " + rootQueryOperator);

					if (oneToOneMapping) {
						logger.debug("oneToOneMapping");
						if (this.mappingContainsRenameOperator(rootQueryOperator)) {
							//TODO check whether the construct to be renamed appears in the predicate of the scanop to be expanded
							//could be multiple renameOperators, could be superAbstract that gets renamed, so won't just be the predicate that needs renaming
							logger.debug("found renameOperator - TODO");
						} else {
							logger.debug("no rename operator, should just be a straight replacing of scanOperator with mapping");
							Set<ScanOperator> scanOperators = getScanOperatorsOfMapping(rootQueryOperator);
							logger.debug("scanOperatorsOfMapping.size(), should be 1: " + scanOperators.size());
							if (scanOperators.size() > 1)
								logger.error("more than one scanOperator in mapping for  oneToOneMapping - something wrong here");
							for (ScanOperator mappingScanOp : scanOperators) {
								logger.debug("mappingScanOp: " + mappingScanOp);

								/*
									if (queryScanOp.getSuperAbstract().getName().equals(superAbstract.getName())) {
									logger.debug("found superAbstract with same name - TODO rename !!!");
									//TODO sort out rename
								*/
								Set<Predicate> predicates = scanOperator.getPredicates();
								logger.debug("predicates.size(): " + predicates.size());
								for (Predicate predicate : predicates) {
									logger.debug("predicate: " + predicate);
									SuperLexical sl1 = predicate.getSuperLexical1();
									logger.debug("sl1: " + sl1);
									SuperLexical sl2 = predicate.getSuperLexical2();
									logger.debug("sl2: " + sl2);
									Predicate newPredicate = new Predicate();
									newPredicate.setAndOr(predicate.getAndOr());
									newPredicate.setLiteral1(predicate.getLiteral1());
									newPredicate.setLiteral2(predicate.getLiteral2());
									newPredicate.setOperator(predicate.getOperator());
									if (sl1 != null) {
										String sl1Name = sl1.getName();
										logger.debug("sl1Name: " + sl1Name);
										SuperAbstract mappingSA = mappingScanOp.getSuperAbstract();
										logger.debug("mappingSA: " + mappingSA);
										SuperLexical mappingSL = superLexicalRepository.getSuperLexicalWithNameOfSuperAbstract(sl1Name, mappingSA);
										logger.debug("mappingSL: " + mappingSL);
										newPredicate.setSuperLexical1(mappingSL);
									}
									if (sl2 != null) {
										String sl2Name = sl2.getName();
										logger.debug("sl2Name: " + sl2Name);
										SuperAbstract mappingSA = mappingScanOp.getSuperAbstract();
										logger.debug("mappingSA: " + mappingSA);
										SuperLexical mappingSL = superLexicalRepository.getSuperLexicalWithNameOfSuperAbstract(sl2Name, mappingSA);
										logger.debug("mappingSL: " + mappingSL);
										newPredicate.setSuperLexical2(mappingSL);
									}
									mappingScanOp.addPredicate(newPredicate);
									logger.debug("added newPredicate to mappingScanOp, newPredicate: " + newPredicate);
								}
								mappingScanOp.setAndOr(scanOperator.getAndOr()); //TODO not sure I shouldn't check first, probably should
								mappingScanOp.setReconcilingExpression(scanOperator.getReconcilingExpression());
								mappingScanOp.setVariableName(scanOperator.getVariableName());
								logger.debug("given mappingScanOp andOr, reconcilingExpression and VariableName from scanOperator");
								logger.debug("mappingScanOp.getMapping(): " + mappingScanOp.getMapping());
							}
						}
					} else if (oneToManyMapping) {
						logger.debug("oneToManyMapping");

						if (this.mappingContainsRenameOperator(rootQueryOperator)) {
							//TODO check whether the construct to be renamed appears in the predicate of the scanop to be expanded
							//could be multiple renameOperators, could be superAbstract that gets renamed, so won't just be the predicate that needs renaming
							logger.debug("found renameOperator - TODO");
						} else {
							logger.debug("no rename operator, should just be a straight replacing of scanOperator with mapping");
							Set<ScanOperator> scanOperators = getScanOperatorsOfMapping(rootQueryOperator);
							logger.debug("scanOperatorsOfMapping.size(), should be >1: " + scanOperators.size());
							for (ScanOperator mappingScanOp : scanOperators) {
								logger.debug("mappingScanOp: " + mappingScanOp);
								logger.debug("mappingScanOp.variableName: " + mappingScanOp.getVariableName());

								/*
									if (queryScanOp.getSuperAbstract().getName().equals(superAbstract.getName())) {
									logger.debug("found superAbstract with same name - TODO rename !!!");
									//TODO sort out rename
								*/
								Set<Predicate> predicates = scanOperator.getPredicates();
								logger.debug("predicates.size(): " + predicates.size());
								for (Predicate predicate : predicates) {
									logger.debug("predicate: " + predicate);
									SuperLexical sl1 = predicate.getSuperLexical1();
									logger.debug("sl1: " + sl1);
									SuperLexical sl2 = predicate.getSuperLexical2();
									logger.debug("sl2: " + sl2);
									Predicate newPredicate = new Predicate();
									newPredicate.setAndOr(predicate.getAndOr());
									newPredicate.setLiteral1(predicate.getLiteral1());
									newPredicate.setLiteral2(predicate.getLiteral2());
									newPredicate.setOperator(predicate.getOperator());
									boolean needToFindSl1 = false;
									boolean needToFindSl2 = false;
									boolean foundSl1 = false;
									boolean foundSl2 = false;
									if (sl1 != null) {
										needToFindSl1 = true;
										String sl1Name = sl1.getName();
										logger.debug("sl1Name: " + sl1Name);
										SuperAbstract mappingSA = mappingScanOp.getSuperAbstract();
										logger.debug("mappingSA: " + mappingSA);
										SuperLexical mappingSL = superLexicalRepository.getSuperLexicalWithNameOfSuperAbstract(sl1Name, mappingSA);
										logger.debug("mappingSL: " + mappingSL);
										if (mappingSL != null) {
											foundSl1 = true;
											newPredicate.setSuperLexical1(mappingSL);
										}
									}
									if (sl2 != null) {
										needToFindSl2 = true;
										String sl2Name = sl2.getName();
										logger.debug("sl2Name: " + sl2Name);
										SuperAbstract mappingSA = mappingScanOp.getSuperAbstract();
										logger.debug("mappingSA: " + mappingSA);
										SuperLexical mappingSL = superLexicalRepository.getSuperLexicalWithNameOfSuperAbstract(sl2Name, mappingSA);
										logger.debug("mappingSL: " + mappingSL);
										if (mappingSL != null) {
											foundSl2 = true;
											newPredicate.setSuperLexical2(mappingSL);
										}
									}
									if ((!needToFindSl1 || foundSl1) && (!needToFindSl2 || foundSl2)) {
										mappingScanOp.addPredicate(newPredicate);
										logger.debug("added newPredicate to mappingScanOp, newPredicate: " + newPredicate);
									}
								}
								if (mappingScanOp.getAndOr() == null || mappingScanOp.getAndOr().equals(""))
									mappingScanOp.setAndOr(scanOperator.getAndOr()); //TODO not sure I shouldn't check first, probably should
								else {
									logger.error("mappingScanOp already has andOr: " + mappingScanOp.getAndOr());
									logger.error("scanOperator.andOd: " + scanOperator.getAndOr());
								}

								if (mappingScanOp.getReconcilingExpression() == null)
									mappingScanOp.setReconcilingExpression(scanOperator.getReconcilingExpression());
								else {
									logger.error("mappingScanOp already has reconcilingExpression: " + mappingScanOp.getReconcilingExpression());
									logger.error("scanOperator.reconcilingExpression: " + scanOperator.getReconcilingExpression());
								}

								if (mappingScanOp.getVariableName() == null || mappingScanOp.getVariableName().equals(""))
									mappingScanOp.setVariableName(scanOperator.getVariableName());
								else {
									logger.error("mappingScanOp already got variableName: " + mappingScanOp.getVariableName());
									logger.error("scanOperator.variableName: " + scanOperator.getVariableName());
								}

								logger.debug("given mappingScanOp andOr, reconcilingExpression and VariableName from scanOperator");
								logger.debug("mappingScanOp: " + mappingScanOp);
								logger.debug("mappingScanOp.getMapping(): " + mappingScanOp.getMapping());
							}
						}

					} else if (manyToOneMapping) {
						logger.debug("manyToOneMapping - TODO");

					} else if (manyToManyMapping) {
						logger.debug("manyToManyMapping - TODO");

					} else
						logger.error("unknown kind of mapping");

					/*
					if (this.mappingContainsJoinOperator(rootQueryOperator)) {
						logger.debug("mappingContainsJoinOperator");
						if (rootQueryOperator.getReconcilingExpression() != null) {
							logger.debug("rootQueryOperator.getReconcilingExpression(): " + rootQueryOperator.getReconcilingExpression());
							logger.error("found reconcilingExpression in rootOperator ... need to add reconcilingExpression of scanOp somewhere");
						} else {
							rootQueryOperator.setAndOr(scanOperator.getAndOr());
							rootQueryOperator.setVariableName(scanOperator.getVariableName());
							rootQueryOperator.setReconcilingExpression(scanOperator.getReconcilingExpression());
						}
					} else {
						Set<ScanOperator> scanOperators = getScanOperatorsOfMapping(rootQueryOperator);
						for (ScanOperator queryScanOp : scanOperators) {
							//query2ScanOp = scanOperators.iterator().next();
							logger.debug("queryScanOp: " + queryScanOp);

							
								if (queryScanOp.getSuperAbstract().getName().equals(superAbstract.getName())) {
								logger.debug("found superAbstract with same name - TODO rename !!!");
								//TODO sort out rename
								queryScanOp.setAndOr(scanOperator.getAndOr()); //TODO not sure I shouldn't check first, probably should
								queryScanOp.setReconcilingExpression(scanOperator.getReconcilingExpression());
								queryScanOp.setVariableName(scanOperator.getVariableName());
								logger.debug("given queryScanOp andOr, reconcilingExpression and VariableName from scanOperator");
							}
							
						}
					}
					*/

					//this doesn't work as operatorToExpand is the operator to be replaced, but parent is already a cloned operator, i.e., won't have
					//operatorToExpand as child - don't think it's required anyway
					//parent.replaceInput(operatorToExpand, rootQueryOperator);
					//logger.debug("replaced input of parent with rootQueryOperator");
					//logger.debug("parent: " + parent);
					logger.debug("oldOp, operatorToExpand: " + operatorToExpand);
					logger.debug("newOp: rootQuery2Operator: " + rootQueryOperator);
					parent.addAllMappingsUsedForExpansion(rootQueryOperator.getMappingsUsedForExpansion());
					parent.addMappingUsedForExpansion(rootQueryOperator.getMapping());
					logger.debug("rootQueryOperator.getMapping(): " + rootQueryOperator.getMapping());
					return rootQueryOperator;
				} else
					logger.debug("didn't find mapping for operator");
			}
		} else
			logger.error("unexpected operator: " + operatorToExpand);
		return operatorToExpand;
	}

	protected MappingOperator applyRenameOperatorsToPredicatesOfOperatorToExpand(MappingOperator operatorToExpand, MappingOperator rootMappingOperator) {
		return null;
	}

	protected Set<RenameOperator> getRenameOperatorsOfMapping(MappingOperator mappingOperator) {
		//logger.debug("in getRenameOperatorsOfMapping");
		Set<RenameOperator> renameOperators = new HashSet<RenameOperator>();
		//logger.debug("mappingOperator: " + mappingOperator);
		//mappingOperator.addMappingUsedForExpansion(mappingOperator.getMapping());
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			renameOperators.addAll(getRenameOperatorsOfMapping(mappingOperator.getLhsInput()));
			renameOperators.addAll(getRenameOperatorsOfMapping(mappingOperator.getRhsInput()));
			return renameOperators;
		} else if (mappingOperator instanceof ReduceOperator || mappingOperator instanceof TypeCastOperator) {
			renameOperators.addAll(getRenameOperatorsOfMapping(mappingOperator.getLhsInput()));
			return renameOperators;
		} else if (mappingOperator instanceof RenameOperator) {
			RenameOperator renameOperator = (RenameOperator) mappingOperator;
			renameOperators.add(renameOperator);
			return renameOperators;
		} else
			logger.error("unexpected operator");
		return renameOperators;
	}

	protected Set<ScanOperator> getScanOperatorsOfMapping(MappingOperator mappingOperator) {
		//logger.debug("in getScanOperatorsOfMapping");
		Set<ScanOperator> scanOperators = new HashSet<ScanOperator>();
		//logger.debug("mappingOperator: " + mappingOperator);
		mappingOperator.addMappingUsedForExpansion(mappingOperator.getMapping());
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getLhsInput()));
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getRhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ReduceOperator || mappingOperator instanceof RenameOperator
				|| mappingOperator instanceof TypeCastOperator) {
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getLhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ScanOperator) {
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			scanOperators.add(scanOperator);
			return scanOperators;
		} else
			logger.error("unexpected operator");
		return scanOperators;
	}

	//TODO test this
	protected boolean mappingContainsRenameOperator(MappingOperator mappingOperator) {
		if (mappingOperator instanceof RenameOperator)
			return true;
		else {
			boolean foundRenameOp = false;
			if (mappingOperator.getLhsInput() != null) {
				foundRenameOp = mappingContainsRenameOperator(mappingOperator.getLhsInput());
				if (foundRenameOp)
					return foundRenameOp;
			}
			if (mappingOperator.getRhsInput() != null) {
				foundRenameOp = mappingContainsRenameOperator(mappingOperator.getRhsInput());
				if (foundRenameOp)
					return foundRenameOp;
			}
		}
		return false;
	}

	//TODO test this
	protected boolean mappingContainsJoinOperator(MappingOperator mappingOperator) {
		if (mappingOperator instanceof JoinOperator)
			return true;
		else {
			boolean foundJoinOp = false;
			if (mappingOperator.getLhsInput() != null) {
				foundJoinOp = mappingContainsJoinOperator(mappingOperator.getLhsInput());
				if (foundJoinOp)
					return foundJoinOp;
			}
			if (mappingOperator.getRhsInput() != null) {
				foundJoinOp = mappingContainsJoinOperator(mappingOperator.getRhsInput());
				if (foundJoinOp)
					return foundJoinOp;
			}
		}
		return false;
	}

	//TODO test this
	protected boolean mappingContainsSetOperator(MappingOperator mappingOperator) {
		if (mappingOperator instanceof SetOperator)
			return true;
		else {
			boolean foundSetOp = false;
			if (mappingOperator.getLhsInput() != null) {
				foundSetOp = mappingContainsSetOperator(mappingOperator.getLhsInput());
				if (foundSetOp)
					return foundSetOp;
			}
			if (mappingOperator.getRhsInput() != null) {
				foundSetOp = mappingContainsSetOperator(mappingOperator.getRhsInput());
				if (foundSetOp)
					return foundSetOp;
			}
		}
		return false;
	}

	protected Map<ScanOperator, List<Mapping>> groupMappingsByScanOperator(Set<ScanOperator> scanOperators, Set<Mapping> mappings) {
		logger.debug("in groupMappingsByScanOperator");
		logger.debug("mappings: " + mappings);
		Map<ScanOperator, List<Mapping>> mappingsGroupedByScanOperator = new HashMap<ScanOperator, List<Mapping>>();
		for (ScanOperator scanOperator : scanOperators) {
			logger.debug("scanOperator: " + scanOperator);
			logger.debug("superAbstract: " + scanOperator.getSuperAbstract());
			mappingsGroupedByScanOperator.put(scanOperator, new ArrayList<Mapping>());
			for (Mapping mapping : mappings) {
				logger.debug("mapping: " + mapping);
				//MappingOperator query1RootOperator = mapping.getQuery1().getRootOperator();
				//MappingOperator query2RootOperator = mapping.getQuery2().getRootOperator();
				//if (mappingRefersToSuperAbstract(query1RootOperator, scanOperator.getSuperAbstract())
				//		|| mappingRefersToSuperAbstract(query2RootOperator, scanOperator.getSuperAbstract())) {
				logger.debug("mapping.getConstructs1(): " + mapping.getConstructs1());
				if (mapping.getConstructs1().contains(scanOperator.getSuperAbstract())) {
					logger.debug("found superAbstract in constructs1 of mapping");
					mappingsGroupedByScanOperator.get(scanOperator).add(mapping);
					logger.debug("added mapping for scanOperator");
					logger.debug("scanOperator: " + scanOperator);
					logger.debug("mapping: " + mapping);
				}
				//superAbstract should normally only be found in either constructs1 or constructs2, but keep it general here, might cause issues though - TODO check this
				logger.debug("mapping.getConstructs2(): " + mapping.getConstructs2());
				if (mapping.getConstructs2().contains(scanOperator.getSuperAbstract())) {
					logger.debug("found superAbstract in constructs2 of mapping");
					mappingsGroupedByScanOperator.get(scanOperator).add(mapping);
					logger.debug("added mapping for scanOperator");
					logger.debug("scanOperator: " + scanOperator);
					logger.debug("mapping: " + mapping);
				}
			}
		}
		return mappingsGroupedByScanOperator;
	}

	/*
	private boolean mappingRefersToSuperAbstract(MappingOperator mappingOperator, SuperAbstract superAbstract) {
		logger.debug("in mappingRefersToSuperAbstract");
		logger.debug("mappingOperator: " + mappingOperator);
		logger.debug("superAbstract: " + superAbstract);
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			return (mappingRefersToSuperAbstract(mappingOperator.getLhsInput(), superAbstract) || mappingRefersToSuperAbstract(mappingOperator
					.getRhsInput(), superAbstract));
		} else if (mappingOperator instanceof ReduceOperator) {
			return mappingRefersToSuperAbstract(mappingOperator.getLhsInput(), superAbstract);
		} else if (mappingOperator instanceof ScanOperator) {
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			if (scanOperator.getSuperAbstract().equals(superAbstract)) {
				logger.debug("true");
				return true;
			}
		} else
			logger.error("unexpected operator");
		logger.debug("false");
		return false;
	}
	*/

	protected Set<Mapping> getAllMappingsBetweenConstructsQueriedAndTargetSchemas(Set<CanonicalModelConstruct> constructsQueried,
			Set<Schema> targetSchemas) {
		Set<Mapping> mappingsToBeUtilisedForExpansion = new HashSet<Mapping>();
		for (CanonicalModelConstruct constructQueried : constructsQueried) {
			logger.debug("constructQueried: " + constructQueried);
			for (Schema targetSchema : targetSchemas) {
				logger.debug("targetSchema: " + targetSchema);
				Set<Mapping> mappings = mappingRepository.getAllMappingsBetweenConstructQueriedAndTargetSchema(constructQueried, targetSchema);
				for (Mapping mapping : mappings) {
					boolean keepMapping = true;
					Set<CanonicalModelConstruct> constructs1 = mapping.getConstructs1();
					Set<CanonicalModelConstruct> constructs2 = mapping.getConstructs2();
					if (constructs1.contains(constructQueried)) {
						for (CanonicalModelConstruct construct2 : constructs2) {
							if (targetSchemas.contains(construct2.getSchema())) {
								logger.debug("targetSchemas contains schema of construct2");
							} else {
								logger.debug("targetSchemas doesn't contain schema of construct2");
								keepMapping = false;
							}

						}
					} else if (constructs2.contains(constructQueried)) {
						for (CanonicalModelConstruct construct1 : constructs1) {
							if (targetSchemas.contains(construct1.getSchema())) {
								logger.debug("targetSchemas contains schema of construct1");
							} else {
								logger.debug("targetSchemas doesn't contain schema of construct1");
								keepMapping = false;
							}

						}
					}
					if (keepMapping)
						mappingsToBeUtilisedForExpansion.add(mapping);
				}
				logger.debug("mappingsBetweenConstructQueriedAndTargetSchemas: " + mappingsToBeUtilisedForExpansion);
			}
		}
		return mappingsToBeUtilisedForExpansion;
	}

	protected Set<Mapping> getAllMappingsBetweenSourceAndTargetSchemas(Set<Schema> sourceSchemas, Set<Schema> targetSchemas) {
		Set<Mapping> mappingsBetweenSourceAndTargetSchemas = new HashSet<Mapping>();
		for (Schema sourceSchema : sourceSchemas) {
			logger.debug("sourceSchema: " + sourceSchema);
			for (Schema targetSchema : targetSchemas) {
				logger.debug("targetSchema: " + targetSchema);
				mappingsBetweenSourceAndTargetSchemas.addAll(mappingRepository.getAllMappingsBetweenSourceSchemaAndTargetSchema(sourceSchema,
						targetSchema));
				logger.debug("mappingsBetweenSourceAndTargetSchemas: " + mappingsBetweenSourceAndTargetSchemas);
			}
		}
		return mappingsBetweenSourceAndTargetSchemas;
	}

	protected Set<DataSource> getAllDataSourcesToEvaluateQueryOver() {
		List<DataSource> allDataSources = dataSourceRepository.findAll();
		return new HashSet<DataSource>(allDataSources);
	}

	protected Set<Schema> getSchemasForDataSourceToEvaluateQueryOver(Set<DataSource> dataSourcesToEvaluateQueryOver) {
		Set<Schema> schemasForDataSourcesToEvaluateQueryOver = new HashSet<Schema>();

		for (DataSource dataSource : dataSourcesToEvaluateQueryOver) {
			Schema schema = schemaRepository.getSchemaForDataSource(dataSource);
			schemasForDataSourcesToEvaluateQueryOver.add(schema);
		}

		return schemasForDataSourcesToEvaluateQueryOver;
	}

	@SuppressWarnings("unchecked")
	//TODO not sure whether the inner collection should be a set or a list
	protected <T> List<List<T>> cartesianProduct(List<List<T>> list) {
		logger.debug("in cartesianProduct");
		logger.debug("list: " + list);
		List<List<T>> toRet = new ArrayList<List<T>>();

		for (int i = 0; i < numberOfTuples(list); i++) {
			toRet.add(new ArrayList<T>());
		}

		int setIndex = 0;
		int productOfPreviousSetSizes = 1;
		for (List<T> set : list) {
			logger.debug("set: " + set);
			logger.debug("setIndex: " + setIndex);
			logger.debug("productOfPreviousSetSizes: " + productOfPreviousSetSizes);
			int index = 0;
			for (int i = 0; i < numberOfTuples(list); i++) {
				logger.debug("index: " + index);
				logger.debug("i: " + i);
				logger.debug("index % set.size(): " + index % set.size());
				toRet.get(i).add((T) set.toArray()[index % set.size()]);
				logger.debug("toRet.get(i): " + toRet.get(i));
				if (setIndex > 0) {
					logger.debug("setIndex > 0");
					if ((i + 1) % productOfPreviousSetSizes == 0) {
						logger.debug("i % productOfPreviousSetSizes == 0");
						index++;
					}
				} else
					index++;
			}
			setIndex++;
			productOfPreviousSetSizes *= set.size();
		}
		logger.debug("toRet: " + toRet);
		return toRet;
	}

	private <T> int numberOfTuples(List<List<T>> list) {
		int product = 1;
		for (List<T> set : list) {
			product *= set.size();
		}
		return product;
	}
}
