package uk.ac.manchester.dstoolkit.service.impl.query.queryoptimisier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.exceptions.LookupException;
import uk.ac.manchester.dstoolkit.exceptions.OptimisationException;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.JoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ReduceOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ScanOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.SetOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;

//TODO change as much as possible to interfaces instead of classes, in all classes not just here

//@Transactional(readOnly = true)
@Service(value = "logicalQueryOptimiserService")
public class LogicalQueryOptimiserServiceImpl extends QueryOptimiserServiceImpl implements LogicalQueryOptimiserService {

	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	private static Logger logger = Logger.getLogger(LogicalQueryOptimiserServiceImpl.class);

	//@Autowired
	//@Qualifier("globalQueryTranslatorService")
	//private GlobalQueryTranslatorService globalQueryTranslatorService;

	/*
	 * The translated query.
	 */
	private Query query;
	private MappingOperator rootOperator;

	//TODO orderBy
	//TODO aggregates
	//TODO functions
	//TODO check optimiser for unions as there could be multiple unions now which should be optimised the same way the joins are optimised, possibly

	private Set<JoinOperator> joinOperators = new LinkedHashSet<JoinOperator>();
	private Hashtable<String, ScanOperator> scanOperators = new Hashtable<String, ScanOperator>();
	private Hashtable<String, SetOperator> setOperators = new Hashtable<String, SetOperator>();
	private Hashtable<String, MappingOperator> subQueries = new Hashtable<String, MappingOperator>();
	private ReduceOperator reduceOperator = null; //should initially be only one reduceOperator, may be wrong though

	//TODO add additional reduceOperators to reduce amount of information that needs to be passed

	/*
	 * The root operator of the optimised query plan.
	 */
	private EvaluatorOperator plan;

	private EvaluatorOperator workingPlan;

	private boolean isInitialCheckForSuperAbstractToScanFirst;

	/*
	 * The collection of bound variables (Strings)
	 */
	private Set<String> boundVar = new HashSet<String>();

	/*
	 * Relations not yet scanned by the query plan.
	 */
	private Collection<ScanOperator> remainingScanOperators;

	private Collection<JoinOperator> remainingJoinOperators;

	private Collection<String> remainingSubQueries;

	/*
	 * Functions not yet evaluated by the query plan/
	 */
	//TODO functions
	//private Collection<Function> remainingFunctions;

	/*
	 * Current estimated cardinality of the query plan.
	 */
	private long currentPlanCardinality;

	private long currentWorkingPlanCardinality;

	private long currentOverallCardinality;

	/*
	 * Optimised sub-queries
	 */
	private Hashtable<String, SubQueryPlan> optimisedSubQueries = new Hashtable<String, SubQueryPlan>();

	public LogicalQueryOptimiserServiceImpl() {
	}

	public LogicalQueryOptimiserServiceImpl(ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService) {
		this.externalDataSourcePoolUtilService = externalDataSourcePoolUtilService;
	}

	//TODO sort out how to carry the information from which mappings the operator are coming from forward to the evaluator

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryoptimisier.LogicalQueryOptimiserService#optimise(uk.ac.manchester.dataspaces.domain.models.query.Query)
	 */
	public EvaluatorOperator optimise(Query query, Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in LogicalQueryOptimiserServiceImpl, optimise(query)");
		logger.debug("query: " + query);
		logger.debug("externalDataSourcePoolUtilService: " + externalDataSourcePoolUtilService);

		this.query = query;

		logger.debug("this.query: " + this.query);
		logger.debug("queryString: " + query.getQueryString());
		logger.debug("about to generate optimised plan");
		//logger.debug("reduceOperator: " + reduceOperator);

		if (query.getRootOperatorOfExpandedQuery() == null)
			query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator optimisedPlanRootOperator = optimise(query.getRootOperatorOfExpandedQuery(), controlParameters);

		logger.debug("finished generating optimised plan");
		logger.debug("optimisedPlanRootOperator: " + optimisedPlanRootOperator);
		logger.debug("optimisedPlanRootOperator.getMappingsUsedForExpansion(): " + optimisedPlanRootOperator.getMappingsUsedForExpansion());

		if (optimisedPlanRootOperator.getDataSource() != null && !(optimisedPlanRootOperator instanceof EvaluateExternallyOperatorImpl)) {
			logger.debug("source of last operator known and optimisedPlanRootOperator isn't EvaluateExternallyOperatorImpl, add EvaluateExternallyOperatorImpl");

			logger.debug("optimisedPlanRootOperator.getDataSource(): " + optimisedPlanRootOperator.getDataSource());

			//TODO not sure whether there could be multiple mappings here for an operator ...

			EvaluateExternallyOperatorImpl evaluateExternallyOp = new EvaluateExternallyOperatorImpl(optimisedPlanRootOperator,
					optimisedPlanRootOperator.getResultType(), optimisedPlanRootOperator.getCardinality(),
					optimisedPlanRootOperator.getJoinPredicatesCarried(), optimisedPlanRootOperator.getDataSource());
			evaluateExternallyOp.setVariableName(optimisedPlanRootOperator.getVariableName());
			evaluateExternallyOp.setJoinOperatorsCarried(optimisedPlanRootOperator.getJoinOperatorsCarried());
			evaluateExternallyOp.setAndOr(optimisedPlanRootOperator.getAndOr());
			evaluateExternallyOp.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);
			evaluateExternallyOp.addAllMappingsUsedForExpansion(optimisedPlanRootOperator.getMappingsUsedForExpansion());

			if (controlParameters != null) {
				if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
					int maxNumberOfResults = -1;
					maxNumberOfResults = new Integer(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS).getValue()).intValue();
					logger.debug("maxNumberOfResults: " + maxNumberOfResults);
					evaluateExternallyOp.setMaxNumberOfResults(maxNumberOfResults);
				}

				if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
					int fetchSize = -1;
					fetchSize = new Integer(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue()).intValue();
					logger.debug("fetchSize: " + fetchSize);
					evaluateExternallyOp.setFetchSize(fetchSize);
				}
			}

			logger.debug("evaluateExternallyOp.getVariableName():" + evaluateExternallyOp.getVariableName());
			logger.debug("evaluateExternallyOp.getDataSource(): " + evaluateExternallyOp.getDataSource());
			logger.debug("evaluateExternallyOp.getResultType(): " + evaluateExternallyOp.getResultType());
			logger.debug("evaluateExternallyOp.getJoinPredicatesCarried(): " + evaluateExternallyOp.getJoinPredicatesCarried());
			logger.debug("evaluateExternallyOp.getJoinOperatorsCarried(): " + evaluateExternallyOp.getJoinOperatorsCarried());
			logger.debug("evaluateExternallyOp.getExternalDataSourcePoolUtilService: " + evaluateExternallyOp.getExternalDataSourcePoolUtilService());
			logger.debug("evaluateExternallyOp.getMappingsUsedForExpansion(): " + evaluateExternallyOp.getMappingsUsedForExpansion());
			logger.debug("evaluateExternallOp.getMaxNumberOfResults: " + evaluateExternallyOp.getMaxNumberOfResults());
			logger.debug("evaluateExternallyOp.getFetchSize: " + evaluateExternallyOp.getFetchSize());

			optimisedPlanRootOperator = evaluateExternallyOp;
		}

		logger.debug("finished generating optimised plan");
		logger.debug("optimisedPlanRootOperator: " + optimisedPlanRootOperator);

		return optimisedPlanRootOperator;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryoptimisier.LogicalQueryOptimiserService#optimise(uk.ac.manchester.dataspaces.domain.models.mapping.operators.MappingOperator)
	 */
	public EvaluatorOperator optimise(MappingOperator rootOperator, Map<ControlParameterType, ControlParameter> controlParameters) {

		joinOperators = new LinkedHashSet<JoinOperator>();
		scanOperators = new Hashtable<String, ScanOperator>();
		setOperators = new Hashtable<String, SetOperator>();
		subQueries = new Hashtable<String, MappingOperator>();

		reduceOperator = null;
		plan = null;
		workingPlan = null;
		isInitialCheckForSuperAbstractToScanFirst = true;
		boundVar = new HashSet<String>();
		currentPlanCardinality = 0;
		currentWorkingPlanCardinality = 0;
		currentOverallCardinality = 0;
		optimisedSubQueries = new Hashtable<String, SubQueryPlan>();

		this.rootOperator = rootOperator;

		//logger.debug("reduceOperator: " + reduceOperator);
		logger.debug("in LogicalQueryOptimiserServiceImpl, optimise(rootOperator)");
		logger.debug("rootOperator: " + rootOperator);
		logger.debug("about to generate optimised plan");
		//logger.debug("reduceOperator: " + reduceOperator);

		gatherJoinsSetScanOpsAndSubqueries(rootOperator, controlParameters);
		EvaluatorOperator optimisedPlanRootOperator = optimiseQuery(rootOperator, controlParameters);

		logger.debug("finished generating optimised plan");
		logger.debug("optimisedPlanRootOperator: " + optimisedPlanRootOperator);

		return optimisedPlanRootOperator;
		//return null;
	}

	private void gatherJoinsSetScanOpsAndSubqueries(MappingOperator mappingOperator, Map<ControlParameterType, ControlParameter> controlParameters) {
		//logger.debug("reduceOperator: " + reduceOperator);
		logger.debug("in gatherJoinsSetScanOpsAndSubqueries, mappingOperator: " + mappingOperator);
		if (mappingOperator instanceof SetOperator) {
			logger.debug("mappingOperator is SetOperator, add to setOperators, inputOperators are treated as subqueries that are optimised separately");
			logger.debug("mappingOperator.getVariableName() (could be null): " + mappingOperator.getVariableName());
			//this doesn't quite work, creating a new optimiser to the subqueries and then returning the optimised subquery, 
			//but got datastructure to place optimised subqueries, might work
			LogicalQueryOptimiserService logicalOptimiser1 = new LogicalQueryOptimiserServiceImpl(externalDataSourcePoolUtilService);
			String union1VarName = mappingOperator.getLhsInput().getVariableName();
			logger.debug("union1VarName (could be null): " + union1VarName);//should be null
			EvaluatorOperator union1Plan = logicalOptimiser1.optimise(mappingOperator.getLhsInput(), controlParameters);
			SubQueryPlan union1 = new SubQueryPlan(union1Plan, union1Plan.getCardinality());
			String u1FullVarName = union1.plan.getVariableName();
			logger.debug("u1FullVarName: " + u1FullVarName);
			String u1VarName = u1FullVarName;
			if (u1FullVarName != null && u1FullVarName.contains("union")) {
				if (!u1FullVarName.substring(u1FullVarName.lastIndexOf(".")).contains("union")) {
					u1VarName = u1FullVarName.substring(0, u1FullVarName.lastIndexOf(".")); //keep only the name(s) of the union - might not work though - TODO check this
				}
			}
			logger.debug("u1VarName: " + u1VarName);
			mappingOperator.getLhsInput().setVariableName(u1VarName);
			union1Plan.setVariableName(u1VarName);
			logger.debug("mappingOperator.getLhsInput().getVariableName(): " + mappingOperator.getLhsInput().getVariableName());
			logger.debug("union1Plan.getVariableName(): " + union1Plan.getVariableName());
			optimisedSubQueries.put(u1VarName, union1);
			subQueries.put(u1VarName, mappingOperator.getLhsInput());

			LogicalQueryOptimiserService logicalOptimiser2 = new LogicalQueryOptimiserServiceImpl(externalDataSourcePoolUtilService);
			String union2VarName = mappingOperator.getRhsInput().getVariableName();
			logger.debug("union2VarName (should be null): " + union2VarName);//should be null
			EvaluatorOperator union2Plan = logicalOptimiser2.optimise(mappingOperator.getRhsInput(), controlParameters);
			SubQueryPlan union2 = new SubQueryPlan(union2Plan, union2Plan.getCardinality());
			String u2FullVarName = union2.plan.getVariableName();
			logger.debug("u2FullVarName: " + u2FullVarName);
			String u2VarName = u2FullVarName;
			if (u2FullVarName != null && u2FullVarName.contains("union")) {
				if (!u2FullVarName.substring(u2FullVarName.lastIndexOf(".")).contains("union")) {
					u2VarName = u2FullVarName.substring(0, u2FullVarName.lastIndexOf(".")); //keep only the name(s) of the union - might not work though - TODO check this
				}
			}
			logger.debug("u2VarName: " + u2VarName);
			mappingOperator.getRhsInput().setVariableName(u2VarName);
			union2Plan.setVariableName(u2VarName);
			logger.debug("mappingOperator.getRhsInput().getVariableName(): " + mappingOperator.getRhsInput().getVariableName());
			logger.debug("union2Plan.getVariableName(): " + union2Plan.getVariableName());
			optimisedSubQueries.put(u2VarName, union2);
			subQueries.put(u2VarName, mappingOperator.getRhsInput());

			String setOperatorVarName = u1VarName + "_" + u2VarName;
			logger.debug("setOperatorVarName: " + setOperatorVarName);
			mappingOperator.setVariableName(setOperatorVarName);

			setOperators.put(setOperatorVarName, (SetOperator) mappingOperator);

		} else if (mappingOperator instanceof ReduceOperator && mappingOperator != reduceOperator
				&& (reduceOperator != null || mappingOperator != rootOperator)) {
			logger.debug("already got different reduceOperator, only expecting one per (sub)query, or reduceOperator which isn't root operator, may be indication that this is a subquery, assume this");
			LogicalQueryOptimiserService logicalOptimiser = new LogicalQueryOptimiserServiceImpl(externalDataSourcePoolUtilService);
			String subqueryVarName = mappingOperator.getVariableName();
			EvaluatorOperator subqueryPlan = logicalOptimiser.optimise(mappingOperator, controlParameters);
			SubQueryPlan subquery = new SubQueryPlan(subqueryPlan, subqueryPlan.getCardinality());
			optimisedSubQueries.put(subqueryVarName, subquery);
			subQueries.put(subqueryVarName, mappingOperator);
			logger.debug("added subquery to subqueries and optimisedSubQueries, varName: " + subqueryVarName);
			logger.debug("subQueries: " + subQueries);
			logger.debug("optimisedSubQueries: " + optimisedSubQueries);

		} else {
			logger.debug("no setOperator and no additional reduceOperator");
			if (mappingOperator.getLhsInput() != null) {
				logger.debug("mappingOperator.getLhsInput(): " + mappingOperator.getLhsInput());
				gatherJoinsSetScanOpsAndSubqueries(mappingOperator.getLhsInput(), controlParameters);
			}
			if (mappingOperator.getRhsInput() != null) {
				logger.debug("mappingOperator.getRhsInput(): " + mappingOperator.getRhsInput());
				gatherJoinsSetScanOpsAndSubqueries(mappingOperator.getRhsInput(), controlParameters);
			}

			if (mappingOperator instanceof ReduceOperator) {
				logger.debug("mappingOperator is ReduceOperator");
				if (reduceOperator != null) {
					logger.debug("already got reduceOperator, only expecting one per (sub)query, should have already been caught earlier - check this");

				} else
					reduceOperator = (ReduceOperator) mappingOperator;
			} else if (mappingOperator instanceof ScanOperator) {
				logger.debug("mappingOperator is ScanOperator, add to scanOperators");
				logger.debug("scanOperator: " + mappingOperator);
				logger.debug("scanOperator.VariableName: " + mappingOperator.getVariableName());
				scanOperators.put(mappingOperator.getVariableName(), (ScanOperator) mappingOperator);
			} else if (mappingOperator instanceof JoinOperator) {
				logger.debug("mappingOperator is JoinOperator, add to joinOperators");
				joinOperators.add((JoinOperator) mappingOperator);
				logger.debug("joinOperator: " + mappingOperator);
			} else {
				//TODO add rename and typecast operators
				logger.error("missed operator - TODO " + mappingOperator);
			}
		}
	}

	/*
	 * Optimise a query
	 */
	private EvaluatorOperator optimiseQuery(MappingOperator rootOperator, Map<ControlParameterType, ControlParameter> controlParameters)
			throws OptimisationException {
		logger.debug("in optimise");
		EvaluatorOperator rootEvaluatorOperator = null;
		//TODO sort out location of execution of union operator, will have to go in physical optimiser, same as join
		//TODO come back here and sort out optimisation properly, use working plan, might have to adjust algo for union
		//can be multiple setops in a query now, not just one, so this here doesn't necessarily work
		//already generated plans for unions in gatherOps
		if (!setOperators.isEmpty()) {
			logger.debug("found setOperators");
			/* union queries always have two sub queries, 
			 * optimise both queries independently and insert
			 * the correct union operator. 
			 */
			//logger.debug("before generating plan for unionQueryA");
			//Operator union1 = generatePlan(query.getUnionQueryA());
			//logger.debug("after generating plan for unionQueryA");
			//logger.debug("before generating plan for UnionQueryB");
			//Operator union2 = generatePlan(query.getUnionQueryB());
			//logger.debug("after generating plan for UnionQueryB");
			logger.debug("setOperators.size(): " + setOperators.size());
			for (SetOperator setOperator : setOperators.values()) {
				String union1VarName = setOperator.getLhsInput().getVariableName();
				logger.debug("union1VarName: " + union1VarName);
				String union2VarName = setOperator.getRhsInput().getVariableName();
				logger.debug("union2VarName: " + union2VarName);

				SubQueryPlan union1SubQueryPlan = optimisedSubQueries.get(union1VarName);
				logger.debug("union1SubQueryPlan: " + union1SubQueryPlan);
				EvaluatorOperator union1RootOperator = union1SubQueryPlan.plan;
				logger.debug("union1RootOperator: " + union1RootOperator);
				long union1Cardinality = union1SubQueryPlan.cardinality;
				logger.debug("union1Cardinality: " + union1Cardinality);

				SubQueryPlan union2SubQueryPlan = optimisedSubQueries.get(union2VarName);
				logger.debug("union2SubQueryPlan: " + union2SubQueryPlan);
				EvaluatorOperator union2RootOperator = union2SubQueryPlan.plan;
				logger.debug("union2RootOperator: " + union2RootOperator);
				long union2Cardinality = union2SubQueryPlan.cardinality;
				logger.debug("union2Cardinality: " + union2Cardinality);

				/*
				 * A basic estimate for the cardinality of the result. This cardinality
				 * is not used by the optimiser, it is just a rough estimate assigned
				 * for the sake of consistency with other operators, which all have a 
				 * cardinality estimate.
				 */

				long sumCards = union1Cardinality + union2Cardinality;
				logger.debug("sumCards: " + sumCards);
				long unionCard = (long) Math.max(1, sumCards * 0.75);
				logger.debug("unionCard: " + unionCard);
				logger.debug("create unionOperator");

				//TODO check this is ok with respect to the sources
				//TODO sort out union queries
				//TODO check whether there could be a workingPlan or a plan
				logger.debug("workingPlan: " + workingPlan);
				logger.debug("plan: " + getPlan());
				//TODO not comparing with source of workingPlan, don't think there will be workingPlan though, but could be wrong
				if (workingPlan != null || getPlan() != null)
					logger.warn("*************plan or workingPlan not null, compare sources of union queries with source of workingPlan*****************************");
				logger.debug("union1RootOperator: " + union1RootOperator);
				logger.debug("union2RootOperator: " + union2RootOperator);
				logger.debug("union1RootOperator.dataSource: " + union1RootOperator.getDataSource());
				logger.debug("union2RootOperator.dataSource: " + union2RootOperator.getDataSource());

				//TODO assumption: plan == null and workingPlan == null, check whether that's really the case
				//if union1 and union2 are on same source, create union op and set as workingPlan
				//if union1 and union2 are on different sources or on unknown sources, i.e., most likely different ones, create union op and set as plan

				//TODO check setOperators when both unionSubQueries are on the same sources - the setOp goes into workingPlan, which normally gets put into an
				//TODO (cont.) evaluateExternallyOperator, i.e., could stay as MappingOperators that get translated in the evaluateExternallyOperator, but doesn't
				//TODO (cont.) work for setOperators, but might be ok if I change the localTranslator to work over EvaluatorOperators, need to add the 
				//TODO (cont.) necessary attributes though - might be worth doing it this way ...

				/*
				if (union1RootOperator.getDataSource() != null && union2RootOperator.getDataSource() != null
						&& union1RootOperator.getDataSource().equals(union2RootOperator.getDataSource())) {
					logger.debug("(last operator of) both union queries on same source: add union operator to workingPlan; union1.dataSource: "
							+ union1RootOperator.getDataSource());
					//TODO check whether there could be any joinPredicates to carry forward

					logger.debug("setOperator.getResultType(): " + setOperator.getResultType());
					//logger.debug("query.getResultTuple().get(0): " + query.getResultTuple().get(0));

					SetOperatorImpl setOperatorImpl = new SetOperatorImpl(union1RootOperator, union2RootOperator, setOperator.getSetOpType(),
							setOperator.getResultType(), unionCard, null, union1RootOperator.getDataSource());
					setOperatorImpl.setAndOr(setOperator.getAndOr());
					setOperatorImpl.addAllMappingsUsedForExpansion(setOperator.getMappingsUsedForExpansion());
					setOperatorImpl.addAllMappingsUsedForExpansion(union1RootOperator.getMappingsUsedForExpansion());
					setOperatorImpl.addAllMappingsUsedForExpansion(union2RootOperator.getMappingsUsedForExpansion());
					logger.debug("created setOperatorImpl: " + setOperatorImpl);
					logger.debug("union1RootOperator: " + union1RootOperator);
					logger.debug("lhsInput: " + setOperatorImpl.getLhsInput());
					logger.debug("union2RootOperator: " + union2RootOperator);
					logger.debug("rhsInput: " + setOperatorImpl.getRhsInput());
					logger.debug("setOpType: " + setOperatorImpl.getSetOpType());
					logger.debug("resultType: " + setOperatorImpl.getResultType());
					logger.debug("unionCard: " + setOperatorImpl.getCardinality());
					logger.debug("joinPredsCarried: " + setOperatorImpl.getJoinPredicatesCarried());
					logger.debug("joinOperatorsCarried: " + setOperatorImpl.getJoinOperatorsCarried());
					logger.debug("dataSource: " + setOperatorImpl.getDataSource());
					logger.debug("mapping: " + setOperatorImpl.getMappingsUsedForExpansion());
					setOperatorImpl.setVariableName(setOperator.getVariableName());
					logger.debug("variableName: " + setOperatorImpl.getVariableName());
					if (setOperator.getReconcilingExpression() != null)
						setOperatorImpl.setReconcilingExpression(setOperator.getReconcilingExpression().getExpression());
					logger.debug("reconcilingExpression: " + setOperatorImpl.getReconcilingExpression());
					setOperatorImpl.setAndOr(setOperator.getAndOr());
					logger.debug("andOr: " + setOperatorImpl.getAndOr());
					logger.debug("setOperatorImpl.getResultType(): " + setOperatorImpl.getResultType());

					logger.debug("setOperatorImpl: " + setOperatorImpl);

					workingPlan = setOperatorImpl;
					logger.debug("workingPlan: " + workingPlan);
					logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
					logger.debug("workingPlan.getJoinOperatorsCarried(): " + workingPlan.getJoinOperatorsCarried());

					logger.debug("before updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					currentWorkingPlanCardinality = unionCard;
					logger.debug("updated currentWorkingPlanCardinality with unionCard: " + currentWorkingPlanCardinality);
					if (getPlan() != null) {
						logger.debug("plan != null; update currentOverallCardinality with overallJoinCardinality - don't think it should get here though *********");
						long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), null);
						logger.debug("estimated overall joinCard: " + overallJoinCard);
						currentOverallCardinality = overallJoinCard;
					}

					logger.debug("after updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					if (reduceOperator != null) {
						logger.debug("found reduceOperator: " + reduceOperator);
						logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());
						logger.debug("reduceOperator.getDataSource(): " + reduceOperator.getDataSource());
						if (reduceOperator.getDataSource() != null && !reduceOperator.getDataSource().equals(workingPlan.getDataSource()))
							logger.error("reduceOperator and workingPlan on different datasources - TODO check this");
						logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());
						logger.debug("reduceOperator.getDataSource(): " + reduceOperator.getDataSource());
						ReduceOperatorImpl reduceOperatorImpl = new ReduceOperatorImpl(workingPlan, reduceOperator.getReconcilingExpression()
								.getExpression(), reduceOperator.getSuperLexicals(), reduceOperator.getResultType(), currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
						reduceOperatorImpl.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						reduceOperatorImpl.setAndOr(reduceOperator.getAndOr());
						reduceOperatorImpl.addAllMappingsUsedForExpansion(reduceOperator.getMappingsUsedForExpansion());
						reduceOperatorImpl.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
						logger.debug("reduceOperatorImpl.getResultType(): " + reduceOperatorImpl.getResultType());
						logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + reduceOperatorImpl.getJoinPredicatesCarried());
						logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + reduceOperatorImpl.getJoinOperatorsCarried());
						logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + reduceOperatorImpl.getMappingsUsedForExpansion());
						logger.debug("added reduceOperatorImpl: " + reduceOperatorImpl);
						logger.debug("reduceOperator.getVariableName: " + reduceOperator.getVariableName());
						logger.debug("workingPlan.getVariableName: " + workingPlan.getVariableName());
						if (reduceOperator.getVariableName() == null)
							reduceOperatorImpl.setVariableName(workingPlan.getVariableName());
						else {
							if (!reduceOperator.getVariableName().equals(workingPlan.getVariableName()))
								logger.error("reduceOperator and workingPlan have different VariableNames - TODO check this");
							reduceOperatorImpl.setVariableName(reduceOperator.getVariableName());
						}
						logger.debug("reduceOperatorImpl.getVariableName(): " + reduceOperatorImpl.getVariableName());
						workingPlan = reduceOperatorImpl;
					}

				} else {
				
				*/
				//logger.debug("(last operator of) the two union queries on different sources or on unknown sources: add workingPlan to plan, start new workingPlan with union operator");
				logger.debug("not checking whether they're on different datasources, should always be a new subplan, as localtranslator doesn't process union yet");
				logger.debug("union1RootOperator.getDataSource(): " + union1RootOperator.getDataSource());
				logger.debug("union2RootOperator.getDataSource(): " + union2RootOperator.getDataSource());
				logger.debug("union1RootOperator: " + union1RootOperator);
				logger.debug("union2RootOperator: " + union2RootOperator);
				if (getPlan() == null && workingPlan == null) {
					logger.debug("plan == null and workingPlan == null, plan = union op");

					logger.debug("setOperator.getResultTuple(): " + setOperator.getResultType());
					//logger.debug("query.getResultTuple().get(0): " + query.getResultTuple().get(0));

					if (union1RootOperator.getDataSource() != null && !(union1RootOperator instanceof EvaluateExternallyOperatorImpl)) {
						logger.debug("union1RootOperator isn't evaluateExternallyOperatorImpl");
						logger.debug("add evaluateExternallyOp to union1RootOperator");
						EvaluateExternallyOperatorImpl evaluateExternallyOp = new EvaluateExternallyOperatorImpl(union1RootOperator,
								union1RootOperator.getResultType(), union1RootOperator.getCardinality(),
								union1RootOperator.getJoinPredicatesCarried(), union1RootOperator.getDataSource());
						evaluateExternallyOp.setVariableName(union1RootOperator.getVariableName());
						evaluateExternallyOp.setJoinOperatorsCarried(union1RootOperator.getJoinOperatorsCarried());
						evaluateExternallyOp.setAndOr(union1RootOperator.getAndOr());
						evaluateExternallyOp.addAllMappingsUsedForExpansion(union1RootOperator.getMappingsUsedForExpansion());
						evaluateExternallyOp.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);

						if (controlParameters != null) {
							if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
								int maxNumberOfResults = -1;
								maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)
										.getValue());
								logger.debug("maxNumberOfResults: " + maxNumberOfResults);
								evaluateExternallyOp.setMaxNumberOfResults(maxNumberOfResults);
							}

							if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
								int fetchSize = -1;
								fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
								logger.debug("fetchSize: " + fetchSize);
								evaluateExternallyOp.setFetchSize(fetchSize);
							}
						}

						//TODO could change that to setting the external data source itself instead of the dataSourcePoolUtil

						logger.debug("evaluateExternallyOp.getMaxNumberOfResults: " + evaluateExternallyOp.getMaxNumberOfResults());
						logger.debug("evaluateExternallyOp.getFetchSize: " + evaluateExternallyOp.getFetchSize());
						logger.debug("evaluateExternallyOp.getVariableName(): " + evaluateExternallyOp.getVariableName());
						logger.debug("evaluateExternallyOp.getDataSource(): " + evaluateExternallyOp.getDataSource());
						logger.debug("evaluateExternallyOp.getResultType: " + evaluateExternallyOp.getResultType());
						logger.debug("evaluateExternallyOp.getJoinPredicatesCarried(): " + evaluateExternallyOp.getJoinPredicatesCarried());
						logger.debug("evaluateExternallyOp.getJoinOperatorsCarried(): " + evaluateExternallyOp.getJoinOperatorsCarried());
						logger.debug("evaluateExternallyOp.getExternalDataSourcePoolUtilService: "
								+ evaluateExternallyOp.getExternalDataSourcePoolUtilService());
						logger.debug("evaluateExternallyOp.getMappingsUsedForExpansion(): " + evaluateExternallyOp.getMappingsUsedForExpansion());
						union1RootOperator = evaluateExternallyOp;
						logger.debug("union1RootOperator: " + union1RootOperator);
					}

					if (union2RootOperator.getDataSource() != null && !(union2RootOperator instanceof EvaluateExternallyOperatorImpl)) {
						logger.debug("union2RootOperator isn't evaluateExternallyOperatorImpl");
						logger.debug("add evaluateExternallyOp to union2RootOperator");
						EvaluateExternallyOperatorImpl evaluateExternallyOp = new EvaluateExternallyOperatorImpl(union2RootOperator,
								union2RootOperator.getResultType(), union2RootOperator.getCardinality(),
								union2RootOperator.getJoinPredicatesCarried(), union2RootOperator.getDataSource());
						evaluateExternallyOp.setVariableName(union2RootOperator.getVariableName());
						evaluateExternallyOp.setJoinOperatorsCarried(union2RootOperator.getJoinOperatorsCarried());
						evaluateExternallyOp.setAndOr(union2RootOperator.getAndOr());
						evaluateExternallyOp.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);
						evaluateExternallyOp.addAllMappingsUsedForExpansion(union2RootOperator.getMappingsUsedForExpansion());

						if (controlParameters != null) {
							if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
								int maxNumberOfResults = -1;
								maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)
										.getValue());
								logger.debug("maxNumberOfResults: " + maxNumberOfResults);
								evaluateExternallyOp.setMaxNumberOfResults(maxNumberOfResults);
							}

							if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
								int fetchSize = -1;
								fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
								logger.debug("fetchSize: " + fetchSize);
								evaluateExternallyOp.setFetchSize(fetchSize);
							}
						}

						//TODO could change that to setting the external data source itself instead of the dataSourcePoolUtil

						logger.debug("evaluateExternallyOp.getMaxNumberOfResults: " + evaluateExternallyOp.getMaxNumberOfResults());
						logger.debug("evaluateExternallyOp.getFetchSize: " + evaluateExternallyOp.getFetchSize());
						logger.debug("evaluateExternallyOp.getVariableName(): " + evaluateExternallyOp.getVariableName());
						logger.debug("evaluateExternallyOp.getDataSource(): " + evaluateExternallyOp.getDataSource());
						logger.debug("evaluateExternallyOp.getResultType: " + evaluateExternallyOp.getResultType());
						logger.debug("evaluateExternallyOp.getJoinPredicatesCarried(): " + evaluateExternallyOp.getJoinPredicatesCarried());
						logger.debug("evaluateExternallyOp.getJoinOperatorsCarried(): " + evaluateExternallyOp.getJoinOperatorsCarried());
						logger.debug("evaluateExternallyOp.getExternalDataSourcePoolUtilService: "
								+ evaluateExternallyOp.getExternalDataSourcePoolUtilService());
						logger.debug("evaluateExternallyOp.getMappingsUsedForExpansion(): " + evaluateExternallyOp.getMappingsUsedForExpansion());
						union2RootOperator = evaluateExternallyOp;
						logger.debug("union2RootOperator: " + union2RootOperator);
					}

					SetOperatorImpl setOperatorImpl = new SetOperatorImpl(union1RootOperator, union2RootOperator, setOperator.getSetOpType(),
							setOperator.getResultType(), unionCard, null, null);
					setOperatorImpl.setAndOr(setOperator.getAndOr());
					setOperatorImpl.addAllMappingsUsedForExpansion(setOperator.getMappingsUsedForExpansion());
					setOperatorImpl.addAllMappingsUsedForExpansion(union1RootOperator.getMappingsUsedForExpansion());
					setOperatorImpl.addAllMappingsUsedForExpansion(union2RootOperator.getMappingsUsedForExpansion());
					logger.debug("created setOperatorImpl: " + setOperatorImpl);
					logger.debug("union1RootOperator: " + union1RootOperator);
					logger.debug("lhsInput: " + setOperatorImpl.getLhsInput());
					logger.debug("union2RootOperator: " + union2RootOperator);
					logger.debug("rhsInput: " + setOperatorImpl.getRhsInput());
					logger.debug("setOpType: " + setOperatorImpl.getSetOpType());
					logger.debug("resultType: " + setOperatorImpl.getResultType());
					logger.debug("unionCard: " + setOperatorImpl.getCardinality());
					logger.debug("joinPredsCarried: " + setOperatorImpl.getJoinPredicatesCarried());
					logger.debug("joinOperatorsCarried: " + setOperatorImpl.getJoinOperatorsCarried());
					logger.debug("dataSource: " + setOperatorImpl.getDataSource());
					logger.debug("mappingsUsedForExpansion: " + setOperatorImpl.getMappingsUsedForExpansion());
					setOperatorImpl.setVariableName(setOperator.getVariableName());
					logger.debug("variableName: " + setOperatorImpl.getVariableName());
					if (setOperator.getReconcilingExpression() != null)
						setOperatorImpl.setReconcilingExpression(setOperator.getReconcilingExpression().getExpression());
					logger.debug("reconcilingExpression: " + setOperatorImpl.getReconcilingExpression());
					setOperatorImpl.setAndOr(setOperator.getAndOr());
					logger.debug("andOr: " + setOperatorImpl.getAndOr());
					logger.debug("setOperatorImpl.getResultType: " + setOperatorImpl.getResultType());

					logger.debug("setOperatorImpl: " + setOperatorImpl);
					setPlan(setOperatorImpl);

					if (getPlan() != null) {
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
						logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());
					} else
						logger.debug("plan == null");

					logger.debug("before updating currentPlanCardinality");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					currentPlanCardinality = unionCard;

					logger.debug("after updating currentPlanCardinality with unionCard");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					if (reduceOperator != null) {
						logger.debug("found reduceOperator: " + reduceOperator);
						if (reduceOperator.getDataSource() != null && !reduceOperator.getDataSource().equals(plan.getDataSource()))
							logger.error("reduceOperator and plan on different datasources - TODO check this");
						logger.debug("plan.getDataSource(): " + plan.getDataSource());
						logger.debug("reduceOperator.getDataSource(): " + reduceOperator.getDataSource());
						ReduceOperatorImpl reduceOperatorImpl = new ReduceOperatorImpl(plan, reduceOperator.getReconcilingExpression()
								.getExpression(), reduceOperator.getSuperLexicals(), reduceOperator.getResultType(), currentPlanCardinality,
								plan.getJoinPredicatesCarried(), plan.getDataSource());
						reduceOperatorImpl.setJoinOperatorsCarried(plan.getJoinOperatorsCarried());
						reduceOperatorImpl.setAndOr(reduceOperator.getAndOr());
						reduceOperatorImpl.addAllMappingsUsedForExpansion(reduceOperator.getMappingsUsedForExpansion());
						reduceOperatorImpl.addAllMappingsUsedForExpansion(plan.getMappingsUsedForExpansion());
						logger.debug("reduceOperatorImpl.getResultType(): " + reduceOperatorImpl.getResultType());
						logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + reduceOperatorImpl.getJoinPredicatesCarried());
						logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + reduceOperatorImpl.getJoinOperatorsCarried());
						logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + reduceOperatorImpl.getMappingsUsedForExpansion());
						logger.debug("added reduceOperatorImpl: " + reduceOperatorImpl);
						logger.debug("reduceOperator.getVariableName: " + reduceOperator.getVariableName());
						logger.debug("plan.getVariableName: " + plan.getVariableName());
						if (reduceOperator.getVariableName() == null)
							reduceOperatorImpl.setVariableName(plan.getVariableName());
						else {
							if (!reduceOperator.getVariableName().equals(plan.getVariableName()))
								logger.error("reduceOperator and plan have different VariableNames - TODO check this");
							reduceOperatorImpl.setVariableName(reduceOperator.getVariableName());
						}
						logger.debug("reduceOperatorImpl.getVariableName(): " + reduceOperatorImpl.getVariableName());
						plan = reduceOperatorImpl;
					}

				} else {
					logger.debug("plan or workingPlan != null");
					logger.debug("plan: " + getPlan());
					logger.debug("workingPlan: " + workingPlan);
					logger.error("**********************not sure it'll ever end up in here though *****************************, but looks like it has************");

					/*
					logger.debug("plan != null; add join between plan and working plan using joinPredicate carried, make sure it's not null");
					logger.debug("plan: " + plan);
					logger.debug("joinPredicatesCarried in workingPlan: " + workingPlan.getJoinPredicatesCarried());
					
					if (workingPlan.getJoinPredicatesCarried() == null)
						logger.error("no joinPredicatesCarried - TODO proper error handling");
					//TODO no joinPredicatesCarried - proper error handling
					
					long planJoinCard = estimateJoinCardinality(currentPlanCardinality,currentWorkingPlanCardinality,workingPlan.getJoinPredicatesCarried(),null);
					logger.debug("planJoinCard: " + planJoinCard);
					
					JoinOperator join = new JoinOperator(
							plan,
							workingPlan,
							workingPlan.getJoinPredicatesCarried(),
							planJoinCard,
							null,
							"unknown"
						);
					plan = join;
					logger.debug("plan.getJoinPredicatesCarried(): " + plan.getJoinPredicatesCarried());
					
					logger.debug("before updating currentPlanCardinality");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
					             	
					currentPlanCardinality = planJoinCard;
					logger.debug("after updating currentPlanCardinality");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
					*/
				}

				/*
				plan.setJoinPredicatesCarried(null);
				logger.debug("set joinPredicatesCarried of plan to null");
				
				logger.debug("workingPlan should be null");
				logger.debug("workingPlan: " + workingPlan);
				if (workingPlan != null)
					logger.debug("*********************but isn't - check this***************************");
				
				//workingPlan = bestSubQueryPlan.plan;
				//workingPlan.setJoinPredicatesCarried(bestJoinPreds);
				//logger.debug("workinPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
				
				logger.debug("before updating workingPlan and overallcardinalities");
				logger.debug("currentPlanCardinality: " + currentPlanCardinality);
				logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
				logger.debug("currentOverallCardinality: " + currentOverallCardinality);
				
				//logger.debug("bestSubQueryPlan.plan.cardinality: " + bestSubQueryPlan.plan.getCardinality());
				//currentWorkingPlanCardinality = bestSubQueryPlan.plan.getCardinality();
				
				//long overallJoinCard = estimateJoinCardinality(currentPlanCardinality,currentWorkingPlanCardinality,bestJoinPreds,null);
				logger.debug("estimated overall joinCard: " + overallJoinCard);
				currentOverallCardinality = overallJoinCard;
				
				logger.debug("after updating workingPlan and overall Cardinality");
				logger.debug("currentPlanCardinality: " + currentPlanCardinality);
				logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
				logger.debug("currentOverallCardinality: " + currentOverallCardinality);   
				*/
			}
			//}

			//UnionOperator union = new UnionOperator(union1,union2,query.getUnionType(),query.getResultTuple(),unionCard, null, null);
			//plan = union;

			//TODO sort out orderBy
			/*
			if (query.getOrderByList() != null) {
				logger.debug("query has orderBy, add orderByOperator");
				// ORDER BY - each item refers to a column
				//List<OrderByItem> orderByElements = new ArrayList<OrderByItem>();
				//TODO working plan!!!
				//plan = new OrderByOperator(union,union.getCardinality(),query.getOrderByList());
			}
			*/
		} else {
			logger.debug("no setOperators");
			/*
			 * Process non-UNION query.
			 */
			try {
				logger.debug("before calling initialiseQueryPlan");
				initialiseQueryPlan(controlParameters);
				logger.debug("after calling initialiseQueryPlan");
				logger.debug("before calling iterate");
				iterate(controlParameters);
				logger.debug("after calling iterate");
			} catch (LookupException e) {
				throw new OptimisationException("Exception accessing metadata", e);
			}
			logger.debug("before calling finalise");
			finalise(controlParameters);
			logger.debug("after calling finalise");
		}

		if (plan != null)
			rootEvaluatorOperator = plan;
		else if (workingPlan != null)
			rootEvaluatorOperator = workingPlan;
		else
			logger.error("both plan and workingPlan are null - TODO check this");
		return rootEvaluatorOperator;
	}

	/*
	 * Choose a superAbstract to scan and insert the operator.
	 */
	private void initialiseQueryPlan(Map<ControlParameterType, ControlParameter> controlParameters) throws LookupException, OptimisationException {
		logger.debug("in initialiseQueryPlan");
		/* initialise variables */
		initialiseOptimiser();
		this.isInitialCheckForSuperAbstractToScanFirst = true;
		logger.debug("is initial check for superabstract to scan first");
		/*
		 * Determine which SuperAbstract to scan first.
		 */
		ScanOperator bestCandidate = null;
		Set<Predicate> applicableScanPreds = null;
		long bestScanCard = Long.MAX_VALUE;
		logger.debug("bestCandidate: " + bestCandidate);
		logger.debug("applicableScanPreds: " + applicableScanPreds);
		logger.debug("bestScanCard, Long.MaxValue: " + bestScanCard);
		/*
		 * bestCard remembers the best cardinality obtained after
		 * applying a subsequent operator. This allows the optimiser to determine
		 * the relation to scan initially which produces the lowest estimated
		 * cardinality after applying another subsequent operation. For example,
		 * this allows the optimiser to discover an optimal first join rather than
		 * just the smallest relation after applying scan predicates.
		 */
		long bestCard = Long.MAX_VALUE;
		logger.debug("bestCard: " + bestCard);

		logger.debug("query: " + query);

		//Collection<SuperAbstract> superAbstracts = query.getSuperAbstractReferencesValues();
		//Iterator<SuperAbstractReference> it = superAbstractReferences.iterator();
		logger.debug("about to check ScanOps for best candidate to scan first");
		List<ScanOperator> scans = new ArrayList<ScanOperator>();
		Collection<ScanOperator> scanOps = scanOperators.values();
		for (ScanOperator scanOp : scanOps) {
			scans.add(scanOp);
		}

		for (ScanOperator scanOp : scans) {
			//SuperAbstractReference r = (SuperAbstractReference) it.next();
			logger.debug("isInitialCheckForSuperAbstractToScanFirst: " + isInitialCheckForSuperAbstractToScanFirst);
			logger.debug("current ScanOperator: " + scanOp);
			logger.debug("scanOp.getVariableName(): " + scanOp.getVariableName());
			long scanCard = scanOp.getSuperAbstract().getCardinality();
			logger.debug("scanCard before applying preds: " + scanCard);
			//Collection<Predicate> preds = query.getApplicablePredicates(r.getVariableName(), boundVar);
			Set<Predicate> preds = scanOp.getPredicates();
			//logger.debug("applicable preds: " + preds);
			logger.debug("preds: " + preds);
			scanCard = estimateCardinality(scanCard, preds);
			logger.debug("scanCard after applying preds: " + scanCard);
			/*
			 * In order to determine the cardinality after applying a subsequent operator,
			 * create a new plan and invoke iteration()
			 */
			logger.debug("create plan to determine cardinality after applying another operator");
			logger.debug("scanOp.getResultType(): " + scanOp.getResultType());
			ScanOperatorImpl scanOperator = new ScanOperatorImpl(scanOp.getSuperAbstract(), scanOp.getPredicates(), scanOp.getResultType(),
					currentPlanCardinality, null, scanOp.getDataSource());
			scanOperator.setAndOr(scanOp.getAndOr());
			scanOperator.setVariableName(scanOp.getVariableName());
			scanOperator.addAllMappingsUsedForExpansion(scanOp.getMappingsUsedForExpansion());

			this.remainingScanOperators.remove(scanOp);
			logger.debug("removed scanOp from remainingScanOperators, scanOp: " + scanOp);
			logger.debug("remainingScanOperators: " + remainingScanOperators);

			logger.debug("scanOperator.getVariableName(): " + scanOperator.getVariableName());
			logger.debug("scanOperator.getAndOr(): " + scanOperator.getAndOr());
			logger.debug("scanOperator.getMappingsUsedForExpansion(): " + scanOperator.getMappingsUsedForExpansion());
			if (scanOp.getReconcilingExpression() != null)
				scanOperator.setReconcilingExpression(scanOp.getReconcilingExpression().getExpression());
			setPlan(scanOperator);
			boundVar.add(scanOperator.getVariableName());
			this.currentOverallCardinality = scanCard;
			logger.debug("currentOverallCardinality: " + currentOverallCardinality);
			//TODO check whether applicableScanPreds here shouldn't be preds, may be ok though
			logger.debug("plan: " + getPlan());
			logger.debug("plan.getResultType(): " + plan.getResultType());
			remainingScanOperators.remove(scanOp);
			logger.debug("removed scanOp from remainingScanOperators, scanOp: " + scanOp);
			// check there are at least some operators that can be added to the plan
			if (!(remainingScanOperators.isEmpty() && remainingSubQueries.isEmpty())) { //&& remainingFunctions.isEmpty()
				iteration(controlParameters);
			}
			//remember the best candidate
			if (currentPlanCardinality < bestCard) {
				logger.debug("currentPlanCardinality < bestCard");
				bestCard = currentPlanCardinality;
				bestScanCard = scanCard;
				bestCandidate = scanOp;
				applicableScanPreds = preds;
				logger.debug("bestCandidate: " + bestCandidate);
			} else if (currentPlanCardinality == bestCard) {
				/*
				 * If the estimated cardinality after 2 operators is the same
				 * as the remembered bestCard, try and break the tie by looking
				 * at the cardinality immediately after the relation is scanned. 
				 */
				logger.debug("currentPlanCardinality == bestCard");
				if (scanCard <= bestScanCard) {
					bestCard = currentPlanCardinality;
					bestScanCard = scanCard;
					bestCandidate = scanOp;
					applicableScanPreds = preds;
					logger.debug("bestCandidate: " + bestCandidate);
				}
			}
			/*
			 * Need to reset all optimiser variables here. The next iteration will form a new plan
			 * and estimate the cardinality after applying two operators.
			 */
			initialiseOptimiser();
			logger.debug("after initialiseOptimisier");
		}
		this.isInitialCheckForSuperAbstractToScanFirst = false;
		logger.debug("finished initial check for superabstract to scan first; identified superAbstract to scan first, add to workingPlan");
		logger.debug("bestCandidate: " + bestCandidate);
		logger.debug("bestCandidate.varName: " + bestCandidate.getVariableName());
		logger.debug("bestCandidate.dataSource: " + bestCandidate.getDataSource());
		logger.debug("bestCandidate.resultType: " + bestCandidate.getResultType());
		currentWorkingPlanCardinality = bestScanCard;
		logger.debug("currentWorkingPlanCardinality = bestScanCard: " + currentWorkingPlanCardinality);
		boundVar.add(bestCandidate.getVariableName());
		logger.debug("added varName to boundVars");
		logger.debug("boundVars: " + boundVar);
		logger.debug("Initialising working query plan with scan(" + bestCandidate.getSuperAbstract().getName() + ")" + ", Estimated cardinality = "
				+ currentWorkingPlanCardinality);

		ScanOperatorImpl scanOperator = new ScanOperatorImpl(bestCandidate.getSuperAbstract(), bestCandidate.getPredicates(),
				bestCandidate.getResultType(), currentWorkingPlanCardinality, null, bestCandidate.getDataSource());
		scanOperator.setAndOr(bestCandidate.getAndOr());
		scanOperator.setVariableName(bestCandidate.getVariableName());
		scanOperator.addAllMappingsUsedForExpansion(bestCandidate.getMappingsUsedForExpansion());
		if (bestCandidate.getReconcilingExpression() != null)
			scanOperator.setReconcilingExpression(bestCandidate.getReconcilingExpression().getExpression());
		logger.debug("scanOperator.getAndOr(): " + scanOperator.getAndOr());
		logger.debug("scanOperator.getVariablename(): " + scanOperator.getVariableName());
		logger.debug("scanOperator.getReconcilingExpression(): " + scanOperator.getReconcilingExpression());
		logger.debug("scanOperator.getJoinPredicatesCarried(): " + scanOperator.getJoinPredicatesCarried());
		logger.debug("scanOperator.getJoinOperatorsCarried(): " + scanOperator.getJoinOperatorsCarried());
		logger.debug("scanOperator.getCardinality(): " + scanOperator.getCardinality());
		logger.debug("scanOperator.getMappingsUsedForExpansion(): " + scanOperator.getMappingsUsedForExpansion());

		workingPlan = scanOperator;

		logger.debug("workingPlan.getResultType(): " + workingPlan.getResultType());
		logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());

		//workingPlan.setDataSource(bestCandidate.getDataSource());
		//workingPlan.setJoinPredicatesCarried(null);
		/*
		List<SuperLexical> superLexicals = bestCandidate.getSuperLexicals();
		ResultType resultType = new ResultType();
		for (SuperLexicalReference superLexicalReference : superLexicalReferences) {
			String fieldName = superLexicalReference.getSuperLexicalName();
			List<DataType> dataTypes = superLexicalReference.getDataTypes();
			logger.debug("dataTypes.size(): " + dataTypes.size() + " should only be 1"); //TODO check this
			ResultField resultField = new ResultField(fieldName, dataTypes.get(0));
			resultType.getResultFields().add(resultField);
		}
		*/
		//workingPlan.setResultType(bestCandidate.getResultType());

		remainingScanOperators.remove(bestCandidate);
		logger.debug("removed bestCandidate from remainingScanOperators, bestCandidate: " + bestCandidate);
	}

	private void initialiseOptimiser() throws OptimisationException {
		logger.debug("in initialiseOptimiser");

		//optimisedSubQueries = new Hashtable<String, SubQueryPlan>();

		plan = null;
		workingPlan = null;
		currentPlanCardinality = 0;
		currentWorkingPlanCardinality = 0;

		boundVar = new HashSet<String>();
		remainingScanOperators = new ArrayList<ScanOperator>();
		Collection<ScanOperator> scanOps = scanOperators.values();
		logger.debug("scanOperators.values(): " + scanOperators.values());
		for (ScanOperator scanOp : scanOps) {
			remainingScanOperators.add(scanOp);
		}
		logger.debug("remainingScanOperators: " + remainingScanOperators);

		remainingJoinOperators = new ArrayList<JoinOperator>();
		logger.debug("joinOperators: " + joinOperators);
		for (JoinOperator joinOp : joinOperators) {
			remainingJoinOperators.add(joinOp);
		}
		logger.debug("remainingJoinOperators: " + remainingJoinOperators);

		remainingSubQueries = new ArrayList<String>();
		logger.debug("subQueries: " + subQueries);
		Collection<String> subQueryNames = subQueries.keySet();
		for (String subQueryName : subQueryNames) {
			remainingSubQueries.add(subQueryName);
		}
		logger.debug("remainingSubQueries: " + remainingSubQueries);

		//TODO sort out functions
		//remainingFunctions = query.getFunctions();
		/* loads the subQueries collection */
		//logger.debug("before processingSubQueries");
		//processSubQueries();
		//logger.debug("after processingSubQueries");
	}

	/*
	 * Order joins, subqueries and function calls.
	 */
	private void iterate(Map<ControlParameterType, ControlParameter> controlParameters) throws OptimisationException, LookupException {
		logger.debug("in iterate; before calling iteration on remaining queryElements, if any");
		while (!(remainingScanOperators.isEmpty() && remainingSubQueries.isEmpty()))
			//&& remainingFunctions.isEmpty()
			iteration(controlParameters);
		logger.debug("after calling iterating on remaining queryElements, before calling processSubQueryPredicates");
		//TODO check this here, might have to do this with workingPlan or after finalise - check this

		//if (getPlan() != null)
		//	processSubQueryPredicates(getPlan());
	}

	/*
	 * Add a scan+join, subQuery+join or function call to the current plan
	 */
	private void iteration(Map<ControlParameterType, ControlParameter> controlParameters) throws OptimisationException, LookupException {
		logger.debug("in iteration");
		MappingOperator bestCandidate = null;
		Set<Predicate> bestScanPreds = null;
		Set<Predicate> bestJoinPreds = null;
		Set<JoinOperator> bestJoinOps = null;
		//TODO sort out functions
		//Collection<Predicate> bestFunctionPreds = null;
		SubQueryPlan bestSubQueryPlan = null;
		//remembers the right input size of the best join
		long rightInputCard = Long.MAX_VALUE;
		//remembers the card of the best candidate
		long bestCard = Long.MAX_VALUE;
		//first consider scanning each of the remaining relations and planting
		//a join
		//Iterator<SuperAbstractReference> it = remainingSuperAbstractReferences.iterator();
		logger.debug("before checking remaining ScanOperators");
		for (ScanOperator scanOp : remainingScanOperators) {
			logger.debug("current scanOperator: " + scanOp);
			logger.debug("scanOp.getVariableName(): " + scanOp.getVariableName());
			//SuperAbstractReference r = (SuperAbstractReference) it.next();
			long card = scanOp.getSuperAbstract().getCardinality();
			logger.debug("card before applying scanPreds: " + card);
			//Collection<Predicate> preds = query.getApplicablePredicates(r.getVariableName(), boundVar);
			Set<Predicate> scanPreds = scanOp.getPredicates();
			logger.debug("scanPreds of scanOp: " + scanPreds);

			//TODO probably won't need the following as scan and join predicates are kept separate, but changed getApplicablePredicates to return both, so it doesn't break anything - check this
			//Collection<Predicate> joinPreds = new HashSet<Predicate>();
			//Collection<Predicate> scanPreds = new HashSet<Predicate>();
			//Iterator<Predicate> predIt = preds.iterator();
			/*
			for (Predicate p : preds) {
				//Predicate p = (Predicate) predIt.next();
				if (isJoinPredicate(p, r)) {
					joinPreds.add(p);
				} else
					scanPreds.add(p);
			}
			*/
			card = estimateCardinality(card, scanPreds);
			logger.debug("card after applying applicable scanPreds: " + card);
			/* this needs to be remembered as the right input cardinality
			 * is a join is added
			 */
			//long rightCard = estimateCardinality(card, scanPreds);
			long rightCard = card;

			logger.debug("rightCard: " + rightCard);

			//TODO not sure this should be done here, think about it

			logger.debug("scanOp.getVariableName(): " + scanOp.getVariableName());
			Set<JoinOperator> applicableJoinOperators = getApplicableJoinOperators(scanOp.getVariableName(), boundVar);

			Set<Predicate> joinPreds = new LinkedHashSet<Predicate>();
			for (JoinOperator joinOperator : applicableJoinOperators) {
				logger.debug("applicableJoinOperator: " + joinOperator);
				//if ((joinOperator.getLhsInput().equals(plan) && joinOperator.getRhsInput().equals(scanOp))
				//		|| (joinOperator.getRhsInput().equals(plan) && joinOperator.getLhsInput().equals(scanOp))) {
				//	logger.debug("found applicable joinOperator, add joinPredicates");
				logger.debug("joinPredicates: " + joinOperator.getPredicates());
				joinPreds = joinOperator.getPredicates();
				//}
			}
			logger.debug("got all applicable joinPreds: " + joinPreds);

			card = estimateJoinCardinality(currentOverallCardinality, rightCard, joinPreds, scanOp);
			logger.debug("estimated joinCardinality: " + card);
			if (card <= bestCard) {
				logger.debug("card <= bestCard");
				//remember the cardinality of the right hand input
				rightInputCard = rightCard;
				bestCard = card;
				bestCandidate = scanOp;
				bestJoinPreds = joinPreds;
				bestJoinOps = applicableJoinOperators;
				bestScanPreds = scanPreds;
				logger.debug("rightInputCard: " + rightInputCard);
				logger.debug("bestCard: " + bestCard);
				logger.debug("bestCandidate: " + bestCandidate);
				logger.debug("bestJoinPreds: " + bestJoinPreds);
				logger.debug("bestJoinOps: " + bestJoinOps);
				logger.debug("bestScanPreds: " + bestScanPreds);
			}
		}
		//consider each function that can be added
		//TODO sort out functions
		/*
		logger.debug("before checking remaining functions");
		Collection<Function> funcs = query.getApplicableFunctions(boundVar);
		funcs.retainAll(remainingFunctions);
		//Iterator<Function> fit = funcs.iterator();
		for (Function f : funcs) {
			//Function f = (Function) fit.next();
			logger.debug("current function: " + f);
			Collection<Predicate> preds = query.getApplicablePredicates(f.getVariableName(), boundVar);
			logger.debug("applicable preds: " + preds);
			long card = this.estimateCardinality(currentOverallCardinality, preds);
			logger.debug("card after applying preds: " + card);
			if (card <= bestCard) {
				bestCard = card;
				bestCandidate = f;
				bestFunctionPreds = preds;
			}
		}
		*/
		//consider each sub-query that can be added
		logger.debug("before checking remaining subQueries");
		//Iterator<String> sqit = subQueries.keySet().iterator();
		for (String varName : remainingSubQueries) {
			//String varName = (String) sqit.next();
			logger.debug("current varName: " + varName);
			SubQueryPlan q = optimisedSubQueries.get(varName);
			MappingOperator mappingOperator = subQueries.get(varName);
			logger.debug("corresponding SubQueryPlan: " + q);
			/*
			 * The result variable name is required here rather than
			 * the sub query variable name. This is because predicates
			 * in the outer query will reference the result of the sub-query
			 * rather than the sub-query itself.
			 */
			String resultVarName = getSubQueryResultVarName(mappingOperator);
			logger.debug("resultVarName: " + resultVarName);
			//Collection<Predicate> joinPreds = query.getApplicablePredicates(resultVarName, boundVar);
			Set<JoinOperator> applicableJoinOperators = getApplicableJoinOperators(resultVarName, boundVar);
			Set<Predicate> joinPreds = new LinkedHashSet<Predicate>();
			for (JoinOperator joinOperator : applicableJoinOperators) {
				Set<Predicate> joinPredicates = joinOperator.getPredicates();
				joinPreds.addAll(joinPredicates);
			}
			logger.debug("joinPreds: " + joinPreds);
			//there should be one predicate (attribute IN sub-query)
			//which if applied will be converted to attribute = sub-query result
			long card = estimateJoinCardinality(currentOverallCardinality, q.cardinality, joinPreds, null);
			logger.debug("estimated joinCard: " + card);
			if (card <= bestCard) {
				logger.debug("card <= bestCard");
				bestCard = card;
				bestCandidate = mappingOperator;
				bestSubQueryPlan = q;
				bestJoinPreds = joinPreds;
				bestJoinOps = applicableJoinOperators;
				logger.debug("bestCard: " + bestCard);
				logger.debug("bestCandidate: " + bestCandidate);
				logger.debug("bestSubQueryPlan: " + bestSubQueryPlan);
				logger.debug("bestJoinPreds: " + bestJoinPreds);
				logger.debug("bestJoinOps: " + bestJoinOps);
				logger.debug("bestScanPreds: " + bestScanPreds);
			}
		}

		logger.debug("before adding new op to plan or workingPlan");
		logger.debug("plan: " + getPlan());
		if (getPlan() != null) {
			logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
			logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());
		}
		logger.debug("workingPlan: " + workingPlan);
		if (workingPlan != null) {
			logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
			logger.debug("workingPlan.getJoinOperatorsCarried(): " + workingPlan.getJoinOperatorsCarried());
		}

		logger.debug("checked all remaining queryElements - found bestCandidate - check what it is");
		if (bestCandidate instanceof MappingOperator && subQueries.containsKey(bestCandidate.getVariableName())) {
			logger.debug("bestCandidate is SubQuery");
			String varName = bestCandidate.getVariableName();
			//boundVar.add(varName);
			//logger.debug("boundVar: " + boundVar);
			logger.debug("varName: " + varName);
			remainingSubQueries.remove(varName);
			logger.debug("remainingSubQueries after removing bestCandidate subQuery: " + remainingSubQueries);
			//SubQuery s = (SubQuery) bestCandidate;
			SubQueryPlan s = optimisedSubQueries.get(varName);
			logger.debug("Adding subquery " + s.plan.getVariableName() + " applying " + bestJoinPreds.size() + " predicates"
					+ ", Estimated cardinality = " + bestCard);
			/* if joining a subquery, there will be only zero/one predicate */
			if (bestJoinPreds.size() > 1)
				throw new OptimisationException("Joining subquery, expected <2 predicates" + ", got " + bestJoinPreds.size());
			if (bestJoinOps.size() > 1)
				throw new OptimisationException("Joining subquery, expected <2 joinOps" + ", got " + bestJoinOps.size());
			if (this.isInitialCheckForSuperAbstractToScanFirst) {
				logger.debug("is initial check for superabstract to scan first, add join of subquery to plan");

				ResultType resultType = new ResultType(getPlan().getResultType(), bestSubQueryPlan.plan.getResultType());
				logger.debug("resultType: " + resultType);

				JoinOperatorImpl join = new JoinOperatorImpl(bestJoinPreds, getPlan(), bestSubQueryPlan.plan, resultType, bestCard, null, null);
				//join.setReconcilingExpression(bestJoinPreds);
				//TODO I'm assuming there is only one joinOp ... check this
				logger.debug("bestJoinOps.get(0).getReconcilingExpression().getExpression(): "
						+ bestJoinOps.iterator().next().getReconcilingExpression().getExpression());
				join.setReconcilingExpression(bestJoinOps.iterator().next().getReconcilingExpression().getExpression());
				join.setAndOr(bestJoinOps.iterator().next().getAndOr());
				join.addAllMappingsUsedForExpansion(bestJoinOps.iterator().next().getMappingsUsedForExpansion());
				join.addAllMappingsUsedForExpansion(getPlan().getMappingsUsedForExpansion());
				join.addAllMappingsUsedForExpansion(bestSubQueryPlan.plan.getMappingsUsedForExpansion());
				if (bestJoinOps.size() > 1)
					logger.error("more than one joinOperator in bestJoinOps - TODO sort this, setting reconcilingExpression won't work properly");
				logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());

				StringBuilder joinVarName = new StringBuilder();

				int i = 0;
				for (JoinOperator joinOperator : bestJoinOps) {
					if (i > 0) {
						joinVarName.append(", ");
						logger.debug("more than one bestJoinOp");
					}
					joinVarName.append(joinOperator.getVariableName());
					i++;
				}

				if (!joinVarName.toString().equals("null"))
					join.setVariableName(joinVarName.toString());
				else
					join.setVariableName(null);

				logger.debug("bestJoinOps: " + bestJoinOps);

				logger.debug("joinOP: " + join);
				logger.debug("join.getResultType: " + join.getResultType());
				logger.debug("join.getVariableName(): " + join.getVariableName());
				logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
				logger.debug("join.getCardinality(): " + join.getCardinality());
				logger.debug("join.getJoinPredicatesCarried(): " + join.getJoinPredicatesCarried());
				logger.debug("join.getJoinOperatorsCarried(): " + join.getJoinOperatorsCarried());
				logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());

				logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
				logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

				//TODO check resultType, not sure which one is right
				//join.setResultType(getPlan().getResultType());

				setPlan(join);
				logger.debug("plan: " + getPlan());

				logger.debug("about to remove bestJoinOps from remainingJoinOperators: " + bestJoinOps);
				logger.debug("remainingJoinOperators: " + remainingJoinOperators);
				remainingJoinOperators.removeAll(bestJoinOps);
				logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);

			} else {
				logger.debug("not initial check for superabstract; check sourceId to decide whether to add to plan or working plan");
				if (bestSubQueryPlan.plan.getDataSource() != null && workingPlan.getDataSource() != null
						&& bestSubQueryPlan.plan.getDataSource().equals(workingPlan.getDataSource())) {
					logger.debug("(last operator of) subQuery on same source as last operator in workingPlan: add join of subQueryPlan to workingPlan; sourceId: "
							+ workingPlan.getDataSource());
					long workingPlanJoinCard = estimateJoinCardinality(currentWorkingPlanCardinality, bestSubQueryPlan.cardinality, bestJoinPreds,
							null);
					logger.debug("estimated workingPlan joinCard: " + workingPlanJoinCard);
					logger.debug("bestCard: " + bestCard + " updated with estimated workingPlan joinCard");

					ResultType resultType = new ResultType(workingPlan.getResultType(), bestSubQueryPlan.plan.getResultType());
					logger.debug("resultType: " + resultType);

					JoinOperatorImpl join = new JoinOperatorImpl(bestJoinPreds, workingPlan, bestSubQueryPlan.plan, resultType, workingPlanJoinCard,
							workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
					join.setReconcilingExpression(bestJoinOps.iterator().next().getReconcilingExpression().getExpression());
					join.setAndOr(bestJoinOps.iterator().next().getAndOr());
					join.addAllMappingsUsedForExpansion(bestJoinOps.iterator().next().getMappingsUsedForExpansion());
					join.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
					join.addAllMappingsUsedForExpansion(bestSubQueryPlan.plan.getMappingsUsedForExpansion());
					if (bestJoinOps.size() > 1)
						logger.error("more than one bestJoinOps, setting reconcilingExpression won't work - TODO sort this out");
					join.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());

					StringBuilder joinVarName = new StringBuilder();

					int i = 0;
					for (JoinOperator joinOperator : bestJoinOps) {
						if (i > 0) {
							joinVarName.append(", ");
							logger.debug("more than one bestJoinOp");
						}
						joinVarName.append(joinOperator.getVariableName());
						i++;
					}

					if (!joinVarName.toString().equals("null"))
						join.setVariableName(joinVarName.toString());
					else
						join.setVariableName(null);

					logger.debug("joinOP: " + join);
					logger.debug("join.getResultType: " + join.getResultType());
					logger.debug("joinOp.getVariableName(): " + join.getVariableName());
					logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
					logger.debug("join.getCardinality(): " + join.getCardinality());
					logger.debug("join.getJoinPredicatesCarried(): " + join.getJoinPredicatesCarried());
					logger.debug("join.getJoinOperatorsCarried(): " + join.getJoinOperatorsCarried());
					logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());

					logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
					logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

					//TODO check resultType, not sure which one is right
					//join.setResultType(workingPlan.getResultType());
					workingPlan = join;
					logger.debug("workingPlan: " + workingPlan);
					logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
					logger.debug("workingPlan.getJoinOperatorsCarried(): " + workingPlan.getJoinOperatorsCarried());

					logger.debug("before updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					currentWorkingPlanCardinality = workingPlanJoinCard;
					long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality, bestJoinPreds, null);
					logger.debug("estimated overall joinCard: " + overallJoinCard);
					currentOverallCardinality = overallJoinCard;

					logger.debug("after updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					logger.debug("about to remove bestJoinOps from remainingJoinOperators: " + bestJoinOps);
					logger.debug("remainingJoinOperators: " + remainingJoinOperators);
					remainingJoinOperators.removeAll(bestJoinOps);
					logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);
				} else {
					logger.debug("(last operator of) subQuery on different or unknown source as last operator in workingPlan: add workingPlan to plan, start new workingPlan with join of subQueryPlan");
					logger.debug("bestSubQueryPlan.plan.getDataSource(): " + bestSubQueryPlan.plan.getDataSource());
					logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());
					if (getPlan() == null) {
						logger.debug("plan == null, plan = workingPlan, but place workingPlan into externalCallOperator as whole workingPlan to be executed over single source");
						if (workingPlan.getDataSource() == null)
							logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
						logger.debug("workingPlan.getResultType: " + workingPlan.getResultType());
						EvaluateExternallyOperatorImpl evaluateExternallyOperator = new EvaluateExternallyOperatorImpl(workingPlan,
								workingPlan.getResultType(), currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(),
								workingPlan.getDataSource());
						evaluateExternallyOperator.setVariableName(workingPlan.getVariableName());
						evaluateExternallyOperator.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						evaluateExternallyOperator.setAndOr(workingPlan.getAndOr());
						evaluateExternallyOperator.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
						evaluateExternallyOperator.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);

						if (controlParameters != null) {
							if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
								int maxNumberOfResults = -1;
								maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)
										.getValue());
								logger.debug("maxNumberOfResults: " + maxNumberOfResults);
								evaluateExternallyOperator.setMaxNumberOfResults(maxNumberOfResults);
							}

							if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
								int fetchSize = -1;
								fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
								logger.debug("fetchSize: " + fetchSize);
								evaluateExternallyOperator.setFetchSize(fetchSize);
							}
						}

						logger.debug("evaluateExternallyOperator.getMaxNumberOfResults: " + evaluateExternallyOperator.getMaxNumberOfResults());
						logger.debug("evaluateExternallyOperator.getFetchSize: " + evaluateExternallyOperator.getFetchSize());
						logger.debug("evaluatorExternallyOperator.getVariableName(): " + evaluateExternallyOperator.getVariableName());
						logger.debug("evaluateExternallyOperator.getDataSource(): " + evaluateExternallyOperator.getDataSource());
						logger.debug("evaluateExternallyOperator.getResultType(): " + evaluateExternallyOperator.getResultType());
						logger.debug("evaluateExternallyOperator.getJoinPredicatesCarried(): "
								+ evaluateExternallyOperator.getJoinPredicatesCarried());
						logger.debug("evaluateExternallyOperator.getJoinOperatorsCarried(): " + evaluateExternallyOperator.getJoinOperatorsCarried());
						logger.debug("evaluateExternallyOperator.getMappingsUsedForExpansion(): "
								+ evaluateExternallyOperator.getMappingsUsedForExpansion());
						//plan = workingPlan;
						//evaluateExternallyOperator.setResultType(workingPlan.getResultType());
						setPlan(evaluateExternallyOperator);
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
						logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());

						logger.debug("before updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

						currentPlanCardinality = currentWorkingPlanCardinality;

						logger.debug("after updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					} else {
						logger.debug("plan != null; add join between plan and working plan using joinPredicate carried, make sure it's not null");
						logger.debug("plan: " + getPlan());
						logger.debug("joinPredicatesCarried in workingPlan: " + workingPlan.getJoinPredicatesCarried());
						logger.debug("joinOperatorsCarried in workingPlan: " + workingPlan.getJoinOperatorsCarried());

						if (workingPlan.getJoinPredicatesCarried() == null)
							logger.error("no joinPredicatesCarried - TODO proper error handling");
						//TODO no joinPredicatesCarried - proper error handling

						if (workingPlan.getDataSource() == null)
							logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
						logger.debug("workingPlan.getResultType: " + workingPlan.getResultType());
						EvaluateExternallyOperatorImpl evaluateExternallyOperator = new EvaluateExternallyOperatorImpl(workingPlan,
								workingPlan.getResultType(), currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(),
								workingPlan.getDataSource());
						evaluateExternallyOperator.setVariableName(workingPlan.getVariableName());
						evaluateExternallyOperator.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						evaluateExternallyOperator.setAndOr(workingPlan.getAndOr());
						evaluateExternallyOperator.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
						evaluateExternallyOperator.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);

						if (controlParameters != null) {
							if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
								int maxNumberOfResults = -1;
								maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)
										.getValue());
								logger.debug("maxNumberOfResults: " + maxNumberOfResults);
								evaluateExternallyOperator.setMaxNumberOfResults(maxNumberOfResults);
							}

							if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
								int fetchSize = -1;
								fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
								logger.debug("fetchSize: " + fetchSize);
								evaluateExternallyOperator.setFetchSize(fetchSize);
							}
						}

						logger.debug("evaluateExternallyOperator.getMaxNumberOfResults: " + evaluateExternallyOperator.getMaxNumberOfResults());
						logger.debug("evaluateExternallyOperator.getFetchSize: " + evaluateExternallyOperator.getFetchSize());
						logger.debug("evaluateExternallyOperator.getVariableName(): " + evaluateExternallyOperator.getVariableName());
						logger.debug("evaluateExternallyOperator.getDataSource(): " + evaluateExternallyOperator.getDataSource());
						logger.debug("evaluateExternallyOperator.getResultType(): " + evaluateExternallyOperator.getResultType());
						logger.debug("evaluateExternallyOperator.getJoinPredicatesCarried(): "
								+ evaluateExternallyOperator.getJoinPredicatesCarried());
						logger.debug("evaluateExternallyOperator.getJoinOperatorsCarried(): " + evaluateExternallyOperator.getJoinOperatorsCarried());
						logger.debug("evaluateExternallyOperator.getMappingsUsedForExpansion(): "
								+ evaluateExternallyOperator.getMappingsUsedForExpansion());
						workingPlan = evaluateExternallyOperator;
						//evaluateExternallyOperator.setResultType(workingPlan.getResultType());

						long planJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), null);
						logger.debug("planJoinCard: " + planJoinCard);

						ResultType resultType = new ResultType(getPlan().getResultType(), workingPlan.getResultType());
						logger.debug("resultType: " + resultType);

						JoinOperatorImpl join = new JoinOperatorImpl(workingPlan.getJoinPredicatesCarried(), getPlan(), workingPlan, resultType,
								planJoinCard, null, null);
						join.setReconcilingExpression(workingPlan.getJoinOperatorsCarried().iterator().next().getReconcilingExpression()
								.getExpression());
						join.setAndOr(workingPlan.getJoinOperatorsCarried().iterator().next().getAndOr());
						join.addAllMappingsUsedForExpansion(workingPlan.getJoinOperatorsCarried().iterator().next().getMappingsUsedForExpansion());
						join.addAllMappingsUsedForExpansion(getPlan().getMappingsUsedForExpansion());
						join.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
						if (workingPlan.getJoinOperatorsCarried().size() > 1)
							logger.error("more than one joinOp carried, setting reconcilingExpression won't work properly - TODO sort this out");

						StringBuilder joinVarName = new StringBuilder();

						int i = 0;
						for (JoinOperator joinOperator : workingPlan.getJoinOperatorsCarried()) {
							if (i > 0) {
								joinVarName.append(", ");
								logger.debug("more than one bestJoinOp");
							}
							joinVarName.append(joinOperator.getVariableName());
							i++;
						}

						if (!joinVarName.toString().equals("null"))
							join.setVariableName(joinVarName.toString());
						else
							join.setVariableName(null);

						logger.debug("join.getResultType: " + join.getResultType());
						logger.debug("join.getVariableName(): " + join.getVariableName());
						logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
						logger.debug("join.getCardinality(): " + join.getCardinality());
						logger.debug("join.getJoinPredicatesCarried(): " + join.getJoinPredicatesCarried());
						logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());

						logger.debug("about to remove workingPlan.getJoinOperatorsCarried from remainingJoinOperators: "
								+ workingPlan.getJoinOperatorsCarried());
						logger.debug("remainingJoinOperators: " + remainingJoinOperators);
						remainingJoinOperators.removeAll(workingPlan.getJoinOperatorsCarried());
						logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);

						logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
						logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

						//TODO check resultType, not sure which one is right
						//join.setResultType(getPlan().getResultType());
						setPlan(join);
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
						logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());

						logger.debug("before updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

						currentPlanCardinality = planJoinCard;
						logger.debug("after updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);
					}

					getPlan().setJoinPredicatesCarried(null);
					getPlan().setJoinOperatorsCarried(null);
					logger.debug("set joinPredicatesCarried of plan to null");

					workingPlan = bestSubQueryPlan.plan;
					workingPlan.setJoinPredicatesCarried(bestJoinPreds);
					workingPlan.setJoinOperatorsCarried(bestJoinOps);
					logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
					logger.debug("workingPlan.getJoinOperatorsCarried(): " + workingPlan.getJoinOperatorsCarried());

					logger.debug("before updating workingPlan and overallcardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					logger.debug("bestSubQueryPlan.plan.cardinality: " + bestSubQueryPlan.plan.getCardinality());
					currentWorkingPlanCardinality = bestSubQueryPlan.plan.getCardinality();

					long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality, bestJoinPreds, null);
					logger.debug("estimated overall joinCard: " + overallJoinCard);
					currentOverallCardinality = overallJoinCard;

					logger.debug("after updating workingPlan and overall Cardinality");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
				}
			}
		}
		//TODO sort out functions
		/*
		else if (bestCandidate instanceof Function) {
			logger.debug("bestCandidate is function");
			remainingFunctions.remove(bestCandidate);
			Function f = (Function) bestCandidate;
			logger.debug("Adding function " + f.getVariableName() + " applying " + bestFunctionPreds.size() + " predicates"
					+ ", Estimated cardinality = " + bestCard);
			if (this.isInitialCheckForSuperAbstractToScanFirst) {
				logger.debug("is initial check for superabstract to scan first, add operation call op to plan");
				OperationCallOperator op = new OperationCallOperator(f, getPlan(), bestFunctionPreds, bestCard, null, f.getSourceId());
				logger.debug("operation call op: " + op);
				setPlan(op);
				logger.debug("plan: " + getPlan());
			} else {
				logger.debug("not initial check for superabstract; check sourceId to decide whether to add to plan or working plan");
				if (!f.getSourceId().equals(-1L) && !workingPlan.getSourceId().equals(-1L) && f.getSourceId().equals(workingPlan.getSourceId())) {
					logger.debug("function f on same source as last operator in workingPlan: add operation call op to workingPlan; sourceId: "
							+ workingPlan.getSourceId());

					long workingPlanCard = this.estimateCardinality(currentWorkingPlanCardinality, bestFunctionPreds);
					logger.debug("estimated workingPlanCard: " + workingPlanCard);
					logger.debug("bestCard: " + bestCard + " updated with estimated workingPlan joinCard");

					OperationCallOperator op = new OperationCallOperator(f, workingPlan, bestFunctionPreds, workingPlanCard, workingPlan
							.getJoinPredicatesCarried(), f.getSourceId());
					logger.debug("operation call op: " + op);

					workingPlan = op;
					logger.debug("workingPlan: " + workingPlan);
					logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());

					logger.debug("before updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					currentWorkingPlanCardinality = workingPlanCard;
					long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality, workingPlan
							.getJoinPredicatesCarried(), null);
					logger.debug("estimated overall joinCard: " + overallJoinCard);
					currentOverallCardinality = overallJoinCard;

					logger.debug("after updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
				} else {
					logger
							.debug("(last operator of) subQuery on different or unknown source as last operator in workingPlan: add workingPlan to plan, start new workingPlan with operation call op");
					logger.debug("function f.getSourceId(): " + f.getSourceId());
					logger.debug("workingPlan.getSourceId(): " + workingPlan.getSourceId());
					if (getPlan() == null) {
						logger.debug("plan == null, plan = workingPlan after placing workingPlan into externalCallOperator");
						if (workingPlan.getSourceId().equals(-1L))
							logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
						ExternalCallOperator externalCallOperator = new ExternalCallOperator(workingPlan, currentWorkingPlanCardinality, workingPlan
								.getJoinPredicatesCarried(), workingPlan.getSourceId());
						workingPlan = externalCallOperator;
						externalCallOperator.setResultType(workingPlan.getResultType());
						setPlan(workingPlan);
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());

						logger.debug("before updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

						currentPlanCardinality = currentWorkingPlanCardinality;

						logger.debug("after updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					} else {
						logger.debug("plan != null; add join between plan and working plan using joinPredicate carried, make sure it's not null");
						logger.debug("plan: " + getPlan());
						logger.debug("joinPredicatesCarried in workingPlan: " + workingPlan.getJoinPredicatesCarried());

						if (workingPlan.getJoinPredicatesCarried() == null)
							logger.error("no joinPredicatesCarried - TODO proper error handling");
						//TODO no joinPredicatesCarried - proper error handling

						if (workingPlan.getSourceId().equals(-1L))
							logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
						ExternalCallOperator externalCallOperator = new ExternalCallOperator(workingPlan, currentWorkingPlanCardinality, workingPlan
								.getJoinPredicatesCarried(), workingPlan.getSourceId());
						workingPlan = externalCallOperator;
						externalCallOperator.setResultType(workingPlan.getResultType());

						long planJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality, workingPlan
								.getJoinPredicatesCarried(), null);
						logger.debug("planJoinCard: " + planJoinCard);

						JoinOperator join = new JoinOperator(getPlan(), workingPlan, workingPlan.getJoinPredicatesCarried(), planJoinCard, null,
								new Long(-1));
						join.setResultType(getPlan().getResultType());
						setPlan(join);
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());

						logger.debug("before updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

						currentPlanCardinality = planJoinCard;
						logger.debug("after updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);
					}

					getPlan().setJoinPredicatesCarried(null);
					logger.debug("set joinPredicatesCarried of plan to null");

					long workingPlanCard = this.estimateCardinality(currentWorkingPlanCardinality, bestFunctionPreds);
					logger.debug("estimated workingPlanCard: " + workingPlanCard);
					logger.debug("bestCard: " + bestCard + " updated with estimated workingPlan joinCard");

					OperationCallOperator op = new OperationCallOperator(f, workingPlan, bestFunctionPreds, workingPlanCard, bestJoinPreds, f
							.getSourceId());
					logger.debug("operation call op: " + op);

					workingPlan = op;
					//workingPlan.setJoinPredicatesCarried(bestJoinPreds);
					logger.debug("workinPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());

					logger.debug("before updating workingPlan and overallcardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					logger.debug("op.cardinality: " + op.getCardinality());
					currentWorkingPlanCardinality = op.getCardinality();

					long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality, bestJoinPreds, null);
					logger.debug("estimated overall joinCard: " + overallJoinCard);
					currentOverallCardinality = overallJoinCard;

					logger.debug("after updating workingPlan and overall Cardinality");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
				}
			}
		} 
		*/
		else if (bestCandidate instanceof ScanOperator) {
			logger.debug("bestCandidate is ScanOperator");
			remainingScanOperators.remove(bestCandidate);
			ScanOperator r = (ScanOperator) bestCandidate;
			logger.debug("ScanOperator: " + r);
			logger.debug("Adding ScanOperator for " + r.getSuperAbstract().getName() + " applying " + bestJoinPreds.size() + " predicates"
					+ ", Estimated cardinality = " + bestCard);
			//boundVar.add(r.getVariableName());
			//logger.debug("boundVar: " + boundVar);
			if (this.isInitialCheckForSuperAbstractToScanFirst) {
				logger.debug("is initial check for superabstract to scan first, add scan and join to plan");
				logger.debug("superAbstract.cardinality: " + r.getSuperAbstract().getCardinality());
				logger.debug("rightInputCard: " + rightInputCard);
				if (r.getSuperAbstract().getCardinality() != rightInputCard && (r.getPredicates() == null || r.getPredicates().size() == 0))
					logger.error("no predicates, but cardinalities different, something wrong here - TODO fix it");
				logger.debug("r.getResultType: " + r.getResultType());
				ScanOperatorImpl scan = new ScanOperatorImpl(r.getSuperAbstract(), r.getPredicates(), r.getResultType(), rightInputCard, null,
						r.getDataSource());
				scan.setVariableName(r.getVariableName());
				scan.setAndOr(r.getAndOr());
				scan.addAllMappingsUsedForExpansion(r.getMappingsUsedForExpansion());
				if (r.getReconcilingExpression() != null)
					scan.setReconcilingExpression(r.getReconcilingExpression().getExpression());
				logger.debug("scan.getResultType: " + scan.getResultType());
				logger.debug("scan.getReconcilingExpression(): " + scan.getReconcilingExpression());
				logger.debug("scan.getJoinPredicatesCarried(): " + scan.getJoinPredicatesCarried());
				logger.debug("scan.getJoinOperatorsCarried(): " + scan.getJoinOperatorsCarried());
				logger.debug("scan.getVariableName(): " + scan.getVariableName());
				logger.debug("scan.getAndOr(): " + scan.getAndOr());
				logger.debug("scan.getMappingsUsedForExpansion(): " + scan.getMappingsUsedForExpansion());
				/*
				List<SuperLexicalReference> superLexicalReferences = r.getSuperLexicalReferences();
				ResultType resultType = new ResultType();
				for (SuperLexicalReference superLexicalReference : superLexicalReferences) {
					String superLexicalName = superLexicalReference.getSuperLexicalName();
					List<DataType> dataTypes = superLexicalReference.getDataTypes();
					logger.debug("dataTypes.size(): " + dataTypes.size() + " should only be 1");//TODO check this
					ResultField resultField = new ResultField(superLexicalName, dataTypes.get(0));
					resultType.getResultFields().add(resultField);
					//queryElements.add(queryElement);
				}
				*/
				//resultType.addAll(queryElements);
				logger.debug("scan: " + scan);
				//long rightCard = r.getSuperAbstract().getCardinality();

				ResultType resultType = new ResultType(getPlan().getResultType(), scan.getResultType());
				logger.debug("resultType: " + resultType);
				logger.debug("bestJoinPreds: " + bestJoinPreds);
				logger.debug("bestJoinOps: " + bestJoinOps);
				JoinOperatorImpl join = new JoinOperatorImpl(bestJoinPreds, getPlan(), scan, resultType, bestCard, null, null);
				logger.debug("bestJoinOps.iterator().next().getReconcilingExpression(): " + bestJoinOps.iterator().next().getReconcilingExpression());
				join.setReconcilingExpression(bestJoinOps.iterator().next().getReconcilingExpression().getExpression());
				join.setAndOr(bestJoinOps.iterator().next().getAndOr());
				join.addAllMappingsUsedForExpansion(bestJoinOps.iterator().next().getMappingsUsedForExpansion());
				join.addAllMappingsUsedForExpansion(getPlan().getMappingsUsedForExpansion());
				join.addAllMappingsUsedForExpansion(scan.getMappingsUsedForExpansion());
				if (bestJoinOps.size() > 1)
					logger.error("more than one joinOperator - TODO sort this out, setting reconcilingExpression won't work properly then");

				StringBuilder joinVarName = new StringBuilder();

				int i = 0;
				for (JoinOperator joinOperator : bestJoinOps) {
					if (i > 0) {
						joinVarName.append(", ");
						logger.debug("more than one bestJoinOp");
					}
					joinVarName.append(joinOperator.getVariableName());
					i++;
				}

				if (!joinVarName.toString().equals("null"))
					join.setVariableName(joinVarName.toString());
				else
					join.setVariableName(null);

				logger.debug("join: " + join);
				logger.debug("join.getResultType: " + join.getResultType());
				logger.debug("join.getVariableName(): " + join.getVariableName());
				logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
				logger.debug("join.getCardinality(): " + join.getCardinality());
				logger.debug("join.getJoinPredicatesCarried(): " + join.getJoinPredicatesCarried());
				logger.debug("join.getJoinOperatorsCarried(): " + join.getJoinOperatorsCarried());
				logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());

				logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
				logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

				logger.debug("about to remove bestJoinOps from remainingJoinOperators: " + bestJoinOps);
				logger.debug("remainingJoinOperators: " + remainingJoinOperators);
				remainingJoinOperators.removeAll(bestJoinOps);
				logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);

				//TODO check resultType, not sure which one is right
				//join.setResultType(getPlan().getResultType());
				setPlan(join);
				logger.debug("plan: " + getPlan());
			} else {
				if (r.getDataSource() != null && workingPlan.getDataSource() != null && r.getDataSource().equals(workingPlan.getDataSource())) {
					logger.debug("ScanOperator on same source as last operator in workingPlan: add scan and join to workingPlan; dataSource: "
							+ workingPlan.getDataSource());
					logger.debug("superAbstract.cardinality: " + r.getSuperAbstract().getCardinality());
					logger.debug("rightInputCard: " + rightInputCard);
					if ((r.getPredicates() == null || r.getPredicates().size() == 0) && r.getSuperAbstract().getCardinality() != rightInputCard)
						logger.error(", no scanPredicates, but different cardinalities; something wrong with cardinality - TODO fix it");
					logger.debug("r.getResultType: " + r.getResultType());
					ScanOperatorImpl scan = new ScanOperatorImpl(r.getSuperAbstract(), r.getPredicates(), r.getResultType(), rightInputCard,
							workingPlan.getJoinPredicatesCarried(), r.getDataSource());
					scan.setAndOr(r.getAndOr());
					scan.setVariableName(r.getVariableName());
					scan.addAllMappingsUsedForExpansion(r.getMappingsUsedForExpansion());
					scan.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
					if (r.getReconcilingExpression() != null)
						scan.setReconcilingExpression(r.getReconcilingExpression().getExpression());
					logger.debug("scan.getReconcilingExpression(): " + scan.getReconcilingExpression());
					logger.debug("scan.getResultType: " + scan.getResultType());
					logger.debug("scan.getJoinPredicatesCarried(): " + scan.getJoinPredicatesCarried());
					logger.debug("scan.getJoinOperatorsCarried(): " + scan.getJoinOperatorsCarried());
					logger.debug("scan.getVariableName(): " + scan.getVariableName());
					logger.debug("scan.getAndOr(): " + scan.getAndOr());
					logger.debug("scan.getMappingsUsedForExpansion(): " + scan.getMappingsUsedForExpansion());

					/*
					List<SuperLexicalReference> superLexicalReferences = r.getSuperLexicalReferences();
					ResultType resultType = new ResultType();
					//List<QueryElement> queryElements = new ArrayList<QueryElement>();
					for (SuperLexicalReference superLexicalReference : superLexicalReferences) {
						//queryElements.add(queryElement);
						String fieldName = superLexicalReference.getSuperLexicalName();
						DataType dataType = superLexicalReference.getDataType();
						ResultField resultField = new ResultField(fieldName, dataType);
						resultType.addResultField(resultField);
					}
					//resultType.addAll(queryElements);
					*/
					logger.debug("scan: " + scan);
					//long rightCard = r.getSuperAbstract().getCardinality();

					/*
					 rightInputCard = rightCard;
					 bestCard = card;
					 bestCandidate = r;
					 bestJoinPreds = joinPreds;
					 bestScanPreds = scanPreds;
					
					card = estimateCardinality(card,scanPreds); 
					logger.debug("card after applying applicable scanPreds: " + card);
					/* this needs to be remembered as the right input cardinality
					 * is a join is added
					 */
					/*
					long rightCard = estimateCardinality(card,scanPreds);
					logger.debug("rightCard: " + rightCard);
					*/

					logger.debug("rightInputCard: " + rightInputCard);
					logger.debug("scan.getCardinality(): " + scan.getCardinality());
					logger.debug("not sure whether those two should be the same - TODO check");
					//TODO check whether rightInputCard and scan.cardinality should be the same

					long workingPlanJoinCard = estimateJoinCardinality(currentWorkingPlanCardinality, scan.getCardinality(), bestJoinPreds, r);
					logger.debug("estimated workingPlan joinCard: " + workingPlanJoinCard);
					logger.debug("bestCard: " + bestCard + " updated with estimated workingPlan joinCard");

					ResultType resultType = new ResultType(workingPlan.getResultType(), scan.getResultType());
					logger.debug("resultType: " + resultType);

					JoinOperatorImpl join = new JoinOperatorImpl(bestJoinPreds, workingPlan, scan, resultType, workingPlanJoinCard,
							workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
					join.setReconcilingExpression(bestJoinOps.iterator().next().getReconcilingExpression().getExpression());
					join.setAndOr(bestJoinOps.iterator().next().getAndOr());
					join.addAllMappingsUsedForExpansion(bestJoinOps.iterator().next().getMappingsUsedForExpansion());
					join.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
					join.addAllMappingsUsedForExpansion(scan.getMappingsUsedForExpansion());
					if (bestJoinOps.size() > 1)
						logger.error("more than one joinOperator in bestJoinOps - TODO sort this out, setting reconcilingExpression won't work properly");
					join.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());

					StringBuilder joinVarName = new StringBuilder();

					int i = 0;
					for (JoinOperator joinOperator : bestJoinOps) {
						if (i > 0) {
							joinVarName.append(", ");
							logger.debug("more than one bestJoinOp");
						}
						joinVarName.append(joinOperator.getVariableName());
						i++;
					}

					if (!joinVarName.toString().equals("null"))
						join.setVariableName(joinVarName.toString());
					else
						join.setVariableName(null);

					//TODO check resultType, not sure which one is right
					//join.setResultType(workingPlan.getResultType());
					logger.debug("join: " + join);
					logger.debug("join.getResultType: " + join.getResultType());
					logger.debug("join.getVariableName(): " + join.getVariableName());
					logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
					logger.debug("join.getCardinality(): " + join.getCardinality());
					logger.debug("join.getJoinPredicatesCarried(): " + join.getJoinPredicatesCarried());
					logger.debug("join.getJoinOperatorsCarried(): " + join.getJoinOperatorsCarried());
					logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());

					logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
					logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

					logger.debug("about to remove bestJoinOps from remainingJoinOperators: " + bestJoinOps);
					logger.debug("remainingJoinOperators: " + remainingJoinOperators);
					remainingJoinOperators.removeAll(bestJoinOps);
					logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);

					workingPlan = join;
					logger.debug("workingPlan: " + workingPlan);
					logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
					logger.debug("workingPlan.getJoinOperatorsCarried(): " + workingPlan.getJoinOperatorsCarried());

					logger.debug("before updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					currentWorkingPlanCardinality = workingPlanJoinCard;
					if (workingPlan.getJoinPredicatesCarried() != null) {
						long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), null);
						logger.debug("estimated overall joinCard: " + overallJoinCard);
						currentOverallCardinality = overallJoinCard;
					} else
						logger.debug("no update of currentOverallCardinality, as workingPlan.joinPredicatesCarried == null");

					logger.debug("after updating cardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
				} else {
					logger.debug("ScanOperator on different or unknown source as last operator in workingPlan: add workingPlan to plan, start new workingPlan with scan of superAbstract");
					logger.debug("scanOperator.getDataSource(): " + r.getDataSource());
					logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());
					if (getPlan() == null) {
						logger.debug("plan == null, plan = workingPlan after placing workingPlan into externalCallOperator");
						if (workingPlan.getDataSource() == null)
							logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
						logger.debug("workingPlan.getResultType: " + workingPlan.getResultType());

						logger.debug("add reduce operator for all superlexicals");

						Map<String, SuperLexical> varNameSuperLexicalsMap = new LinkedHashMap<String, SuperLexical>();
						StringBuilder reconcilingExpressionBuilder = new StringBuilder();

						ResultType resultTypeOfCurrentRootOperator = workingPlan.getResultType();
						Map<String, ResultField> resultFields = resultTypeOfCurrentRootOperator.getResultFields();
						Set<String> resultFieldNames = resultFields.keySet();
						int j = 0;
						//for (ResultField field : resultFields) {
						for (String resultFieldName : resultFieldNames) {
							if (j > 0)
								reconcilingExpressionBuilder.append(", ");
							//reconcilingExpressionBuilder.append(field.getFieldName());
							reconcilingExpressionBuilder.append(resultFieldName);
							//CanonicalModelConstruct cmc = field.getCanonicalModelConstruct();
							CanonicalModelConstruct cmc = resultFields.get(resultFieldName).getCanonicalModelConstruct();
							SuperLexical sl = null;
							if (cmc instanceof SuperLexical)
								sl = (SuperLexical) cmc;

							//varNameSuperLexicalsMap.put(field.getFieldName(), sl);
							varNameSuperLexicalsMap.put(resultFieldName, sl);
							j++;
						}

						ReduceOperatorImpl reduceOperatorImpl = new ReduceOperatorImpl(workingPlan, reconcilingExpressionBuilder.toString(),
								varNameSuperLexicalsMap, workingPlan.getResultType(), currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
						reduceOperatorImpl.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						reduceOperatorImpl.setVariableName(workingPlan.getVariableName());
						reduceOperatorImpl.setAndOr(workingPlan.getAndOr());
						reduceOperatorImpl.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());

						logger.debug("reduceOperatorImpl.getResultType(): " + reduceOperatorImpl.getResultType());
						logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + reduceOperatorImpl.getJoinPredicatesCarried());
						logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + reduceOperatorImpl.getJoinOperatorsCarried());
						logger.debug("added reduceOperatorImpl: " + reduceOperatorImpl);
						logger.debug("reduceOperator.getVariableName: " + reduceOperator.getVariableName());
						logger.debug("workingPlan.getVariableName: " + workingPlan.getVariableName());
						logger.debug("reduceOperatorImpl.getVariableName(): " + reduceOperatorImpl.getVariableName());
						logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + reduceOperatorImpl.getMappingsUsedForExpansion());
						workingPlan = reduceOperatorImpl;

						EvaluateExternallyOperatorImpl evaluateExternallyOperator = new EvaluateExternallyOperatorImpl(workingPlan,
								workingPlan.getResultType(), currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(),
								workingPlan.getDataSource());
						evaluateExternallyOperator.setVariableName(workingPlan.getVariableName());
						evaluateExternallyOperator.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						evaluateExternallyOperator.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
						evaluateExternallyOperator.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);

						if (controlParameters != null) {
							if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
								int maxNumberOfResults = -1;
								maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)
										.getValue());
								logger.debug("maxNumberOfResults: " + maxNumberOfResults);
								evaluateExternallyOperator.setMaxNumberOfResults(maxNumberOfResults);
							}

							if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
								int fetchSize = -1;
								fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
								logger.debug("fetchSize: " + fetchSize);
								evaluateExternallyOperator.setFetchSize(fetchSize);
							}
						}

						//evaluateExternallyOperator.setResultType(workingPlan.getResultType());

						logger.debug("evaluateExternallyOperator.getMaxNumberOfResults: " + evaluateExternallyOperator.getMaxNumberOfResults());
						logger.debug("evaluateExternallyOperator.getFetchSize: " + evaluateExternallyOperator.getFetchSize());
						logger.debug("evaluateExternallyOperator.getVariableName(): " + evaluateExternallyOperator.getVariableName());
						logger.debug("evaluateExternallyOperator.getDataSource(): " + evaluateExternallyOperator.getDataSource());
						logger.debug("evaluateExternallyOperator.getResultType(): " + evaluateExternallyOperator.getResultType());
						logger.debug("evaluateExternallyOperator.getMappingsUsedForExpansion(): "
								+ evaluateExternallyOperator.getMappingsUsedForExpansion());
						logger.debug("evaluateExternallyOperator.getJoinPredicatesCarried(): "
								+ evaluateExternallyOperator.getJoinPredicatesCarried());
						logger.debug("evaluateExternallyOperator.getJoinOperatorsCarried(): " + evaluateExternallyOperator.getJoinOperatorsCarried());
						workingPlan = evaluateExternallyOperator;
						setPlan(workingPlan);
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
						logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());

						logger.debug("before updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

						currentPlanCardinality = currentWorkingPlanCardinality;

						logger.debug("after updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					} else {
						//TODO sort out code duplication

						logger.debug("plan != null; add join between plan and working plan using joinPredicate carried, make sure it's not null");
						logger.debug("plan: " + getPlan());
						logger.debug("joinPredicatesCarried in workingPlan: " + workingPlan.getJoinPredicatesCarried());
						logger.debug("joinOperatorsCarried in workingPlan: " + workingPlan.getJoinOperatorsCarried());

						if (workingPlan.getJoinPredicatesCarried() == null)
							logger.error("no joinPredicatesCarried - TODO proper error handling");
						//TODO no joinPredicatesCarried - proper error handling

						if (workingPlan.getDataSource() == null)
							logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
						logger.debug("workingPlan.getResultType: " + workingPlan.getResultType());

						logger.debug("add reduce operator for all superlexicals");

						Map<String, SuperLexical> varNameSuperLexicalsMap = new LinkedHashMap<String, SuperLexical>();
						StringBuilder reconcilingExpressionBuilder = new StringBuilder();

						ResultType resultTypeOfCurrentRootOperator = workingPlan.getResultType();
						Map<String, ResultField> resultFields = resultTypeOfCurrentRootOperator.getResultFields();
						Set<String> resultFieldNames = resultFields.keySet();
						int j = 0;
						//for (ResultField field : resultFields) {
						for (String resultFieldName : resultFieldNames) {
							if (j > 0)
								reconcilingExpressionBuilder.append(", ");
							//reconcilingExpressionBuilder.append(field.getFieldName());
							reconcilingExpressionBuilder.append(resultFieldName);
							//CanonicalModelConstruct cmc = field.getCanonicalModelConstruct();
							CanonicalModelConstruct cmc = resultFields.get(resultFieldName).getCanonicalModelConstruct();
							SuperLexical sl = null;
							if (cmc instanceof SuperLexical)
								sl = (SuperLexical) cmc;

							//varNameSuperLexicalsMap.put(field.getFieldName(), sl);
							varNameSuperLexicalsMap.put(resultFieldName, sl);
							j++;
						}

						ReduceOperatorImpl reduceOperatorImpl = new ReduceOperatorImpl(workingPlan, reconcilingExpressionBuilder.toString(),
								varNameSuperLexicalsMap, workingPlan.getResultType(), currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
						reduceOperatorImpl.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						reduceOperatorImpl.setVariableName(workingPlan.getVariableName());
						reduceOperatorImpl.setAndOr(workingPlan.getAndOr());
						reduceOperatorImpl.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());

						logger.debug("reduceOperatorImpl.getResultType(): " + reduceOperatorImpl.getResultType());
						logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + reduceOperatorImpl.getJoinPredicatesCarried());
						logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + reduceOperatorImpl.getJoinOperatorsCarried());
						logger.debug("added reduceOperatorImpl: " + reduceOperatorImpl);
						logger.debug("reduceOperator.getVariableName: " + reduceOperator.getVariableName());
						logger.debug("workingPlan.getVariableName: " + workingPlan.getVariableName());
						logger.debug("reduceOperatorImpl.getVariableName(): " + reduceOperatorImpl.getVariableName());
						logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + reduceOperatorImpl.getMappingsUsedForExpansion());
						workingPlan = reduceOperatorImpl;

						EvaluateExternallyOperatorImpl evaluateExternallyOperator = new EvaluateExternallyOperatorImpl(workingPlan,
								workingPlan.getResultType(), currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(),
								workingPlan.getDataSource());
						evaluateExternallyOperator.setVariableName(workingPlan.getVariableName());
						evaluateExternallyOperator.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
						evaluateExternallyOperator.setAndOr(workingPlan.getAndOr());
						evaluateExternallyOperator.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
						evaluateExternallyOperator.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);

						if (controlParameters != null) {
							if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
								int maxNumberOfResults = -1;
								maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)
										.getValue());
								logger.debug("maxNumberOfResults: " + maxNumberOfResults);
								evaluateExternallyOperator.setMaxNumberOfResults(maxNumberOfResults);
							}

							if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
								int fetchSize = -1;
								fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
								logger.debug("fetchSize: " + fetchSize);
								evaluateExternallyOperator.setFetchSize(fetchSize);
							}
						}

						logger.debug("evaluateExternallyOperator.getMaxNumberOfResults: " + evaluateExternallyOperator.getMaxNumberOfResults());
						logger.debug("evaluateExternallyOperator.getFetchSize: " + evaluateExternallyOperator.getFetchSize());
						logger.debug("evaluateExternallyOperator.getVariableName(): " + evaluateExternallyOperator.getVariableName());
						logger.debug("evaluateExternallyOperator.getDataSource(): " + evaluateExternallyOperator.getDataSource());
						logger.debug("evaluateExternallyOperator.getResultType(): " + evaluateExternallyOperator.getResultType());
						logger.debug("evaluateExternallyOperator.getMappingsUsedForExpansion(): "
								+ evaluateExternallyOperator.getMappingsUsedForExpansion());
						logger.debug("evaluateExternallyOperator.getJoinPredicatesCarried(): "
								+ evaluateExternallyOperator.getJoinPredicatesCarried());
						logger.debug("evaluateExternallyOperator.getJoinOperatorsCarried(): " + evaluateExternallyOperator.getJoinOperatorsCarried());
						//evaluateExternallyOperator.setResultType(workingPlan.getResultType());
						workingPlan = evaluateExternallyOperator;

						long planJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality,
								workingPlan.getJoinPredicatesCarried(), null);
						logger.debug("planJoinCard: " + planJoinCard);

						ResultType resultType = new ResultType(getPlan().getResultType(), workingPlan.getResultType());
						logger.debug("resultType: " + resultType);

						JoinOperatorImpl join = new JoinOperatorImpl(workingPlan.getJoinPredicatesCarried(), getPlan(), workingPlan, resultType,
								planJoinCard, null, null);
						join.setReconcilingExpression(workingPlan.getJoinOperatorsCarried().iterator().next().getReconcilingExpression()
								.getExpression());
						join.setAndOr(workingPlan.getJoinOperatorsCarried().iterator().next().getAndOr());
						join.addAllMappingsUsedForExpansion(workingPlan.getJoinOperatorsCarried().iterator().next().getMappingsUsedForExpansion());
						join.addAllMappingsUsedForExpansion(getPlan().getMappingsUsedForExpansion());
						join.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());

						if (workingPlan.getJoinOperatorsCarried().size() > 1)
							logger.error("more than one joinOperator in workingPlan.getJoinOperatorsCarried - TODO sort this out");

						StringBuilder joinVarName = new StringBuilder();

						int i = 0;
						for (JoinOperator joinOperator : workingPlan.getJoinOperatorsCarried()) {
							if (i > 0) {
								joinVarName.append(", ");
								logger.debug("more than one bestJoinOp");
							}
							joinVarName.append(joinOperator.getVariableName());
							i++;
						}

						if (!joinVarName.toString().equals("null"))
							join.setVariableName(joinVarName.toString());
						else
							join.setVariableName(null);

						//TODO check resultType, not sure which one is right
						//join.setResultType(getPlan().getResultType());
						setPlan(join);
						logger.debug("join.getResultType(): " + join.getResultType());
						logger.debug("join.getVariableName(): " + join.getVariableName());
						logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
						logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());
						logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
						logger.debug("join.getCardinality(): " + join.getCardinality());
						logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());
						logger.debug("join.getJoinPredicatesCarried(): " + join.getJoinPredicatesCarried());
						logger.debug("join.getJoinOperatorsCarried(): " + join.getJoinOperatorsCarried());

						logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
						logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

						logger.debug("about to remove workingPlan.getJoinOperatorsCarried() from remainingJoinOperators: "
								+ workingPlan.getJoinOperatorsCarried());
						logger.debug("remainingJoinOperators: " + remainingJoinOperators);
						remainingJoinOperators.removeAll(workingPlan.getJoinOperatorsCarried());
						logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);

						logger.debug("before updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);

						currentPlanCardinality = planJoinCard;
						logger.debug("after updating currentPlanCardinality");
						logger.debug("currentPlanCardinality: " + currentPlanCardinality);
						logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
						logger.debug("currentOverallCardinality: " + currentOverallCardinality);
					}

					getPlan().setJoinPredicatesCarried(null);
					getPlan().setJoinOperatorsCarried(null);
					logger.debug("set joinPredicatesCarried of plan to null");

					logger.debug("superAbstract.cardinality: " + r.getSuperAbstract().getCardinality());
					logger.debug("rightInputCard: " + rightInputCard);
					if (r.getSuperAbstract().getCardinality() != rightInputCard)
						logger.error("something wrong with cardinality - TODO fix it");
					logger.debug("r.getResultType: " + r.getResultType());
					ScanOperatorImpl scan = new ScanOperatorImpl(r.getSuperAbstract(), r.getPredicates(), r.getResultType(), rightInputCard,
							bestJoinPreds, r.getDataSource());
					scan.setVariableName(r.getVariableName());
					scan.setAndOr(r.getAndOr());
					scan.addAllMappingsUsedForExpansion(r.getMappingsUsedForExpansion());
					scan.setJoinOperatorsCarried(bestJoinOps);
					if (r.getReconcilingExpression() != null)
						scan.setReconcilingExpression(r.getReconcilingExpression().getExpression());
					logger.debug("scan.getResultType: " + scan.getResultType());
					logger.debug("scan.getReconcilingExpression(): " + scan.getReconcilingExpression());
					logger.debug("scan.getJoinPredicatesCarried(): " + scan.getJoinPredicatesCarried());
					logger.debug("scan.getJoinOperatorsCarried(): " + scan.getJoinOperatorsCarried());
					logger.debug("scan.getVariableName(): " + scan.getVariableName());
					logger.debug("scan.getAndOr(): " + scan.getAndOr());
					logger.debug("scan.getMappingsUsedForExpansion(): " + scan.getMappingsUsedForExpansion());
					/*
					List<SuperLexicalReference> superLexicalReferences = r.getSuperLexicalReferences();
					ResultType resultType = new ResultType();
					//List<QueryElement> queryElements = new ArrayList<QueryElement>();
					for (SuperLexicalReference superLexicalReference : superLexicalReferences) {
						String fieldName = superLexicalReference.getSuperLexicalName();
						DataType dataType = superLexicalReference.getDataType();
						ResultField resultField = new ResultField(fieldName, dataType);
						resultType.addResultField(resultField);
					}
					//resultType.addAll(queryElements);
					*/
					logger.debug("scan: " + scan);

					workingPlan = scan;
					//workingPlan.setJoinPredicatesCarried(bestJoinPreds);
					//workingPlan.setJoinOperatorsCarried(bestJoinOperators);
					logger.debug("workingPlan.getJoinPredicatesCarried(): " + workingPlan.getJoinPredicatesCarried());
					logger.debug("workingPlan.getJoinOperatorsCarried(): " + workingPlan.getJoinOperatorsCarried());

					logger.debug("before updating workingPlan and overallcardinalities");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);

					logger.debug("scan.cardinality: " + scan.getCardinality());
					currentWorkingPlanCardinality = scan.getCardinality();

					long overallJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality, bestJoinPreds, r);
					logger.debug("estimated overall joinCard: " + overallJoinCard);
					currentOverallCardinality = overallJoinCard;

					logger.debug("after updating workingPlan and overall Cardinality");
					logger.debug("currentPlanCardinality: " + currentPlanCardinality);
					logger.debug("currentWorkingPlanCardinality: " + currentWorkingPlanCardinality);
					logger.debug("currentOverallCardinality: " + currentOverallCardinality);
				}
			}
		}
		//currentCardinality = bestCard;
		//logger.debug("currentCardinality: " + currentCardinality);
		if (bestCandidate instanceof MappingOperator && subQueries.containsKey(bestCandidate.getVariableName())) {
			logger.debug("bestCandidate is subQuery, sort out boundVariables");
			/* if a sub query was added, the bound variable referenced
			 * within the outer query is the sub-query result, not
			 * the sub-query itself
			 */
			boundVar.add(getSubQueryResultVarName(bestCandidate));
		} else
			boundVar.add(bestCandidate.getVariableName());
		logger.debug("boundVar: " + boundVar);
	}

	/*
	 * Optimises all sub queries, placing the resulting
	 * plans in a hashtable for future use.
	 */
	/*
	private void processSubQueries() throws OptimisationException {
		logger.debug("in processSubQueries");
		
		//Collection<SubQuery> queries = query.getSubQueries();
		//Iterator<SubQuery> it = queries.iterator();
		//while (it.hasNext()) {
		//	SubQuery sq = it.next();
		//	logger.debug("before optimising subQuery: " + sq);
		//	subQueries.put(sq.getVariableName(), optimiseSubQuery(sq));
		//}
		
		for (String subQueryVarName : subQueries.keySet()) {
			logger.debug("before optimising subquery with varName: " + subQueryVarName);
			MappingOperator subQueryRootOperator = subQueries.get(subQueryVarName);
			logger.debug("subQueryRootOperator: " + subQueryRootOperator);
			optimisedSubQueries.put(subQueryVarName, optimiseSubQuery(subQueryVarName, subQueryRootOperator));
		}
	}
	*/

	/*
	 * Generate the plan for a sub-query.
	 */
	/*
	private SubQueryPlan optimiseSubQuery(String subQueryVarName, MappingOperator mappingOperator) throws OptimisationException {
		logger.debug("in optimiseSubQuery");
		logger.debug("Generating logical query plan for sub-query " + subQueryVarName);
		LogicalQueryOptimiserServiceImpl optimiser = new LogicalQueryOptimiserServiceImpl(externalDataSourcePoolUtilService);
		logger.debug("before calling optimise for subQuery");
		EvaluatorOperator rootOperator = optimiser.optimise(mappingOperator);
		logger.debug("after calling optimise for subQuery");
		logger.debug("Logical plan for sub-query " + subQueryVarName + " " + rootOperator);
		//TODO check whether anything else needs to be returned, i.e., working plan, location where subquery is evaluated, joinPredicates ...
		//TODO finalise subquery
		return new SubQueryPlan(optimiser.plan, optimiser.currentOverallCardinality);
	}
	*/

	/*
	 * Estimate the cardinality of applying the given predicates in scan or 
	 * operator call operators (not join operators).
	 */
	private long estimateCardinality(long inputCardinality, Set<Predicate> predicates) throws OptimisationException, LookupException {
		logger.debug("in estimateCardinality");
		logger.debug("inputCardinality: " + inputCardinality);
		logger.debug("predicates: " + predicates);
		Iterator<Predicate> it = predicates.iterator();
		long card = inputCardinality;
		while (it.hasNext()) {
			card = Math.max(1, estimateApplyPredicate(card, it.next()));
		}
		logger.debug("card: " + card);
		return card;
	}

	/*
	 * Estimate the cardinality of applying join predicates 
	 */
	private long estimateJoinCardinality(long leftCard, long rightCard, Set<Predicate> joinPredicates, ScanOperator scanOperator)
			throws OptimisationException, LookupException {
		logger.debug("in estimateJoinCardinality");
		logger.debug("leftCard: " + leftCard);
		logger.debug("rightCard: " + rightCard);
		logger.debug("joinPredicates: " + joinPredicates);
		logger.debug("scanOperator: " + scanOperator);
		if (scanOperator != null)
			logger.debug("superAbstract: " + scanOperator.getSuperAbstract());
		long card = leftCard * rightCard;
		logger.debug("card: " + card);
		SuperAbstract superAbstract = null;
		if (scanOperator != null)
			superAbstract = scanOperator.getSuperAbstract();
		//Collection<Predicate> joinPredicates = joinOperator.getPredicates();
		Iterator<Predicate> it = joinPredicates.iterator();
		while (it.hasNext()) {
			Predicate p = it.next();
			logger.debug("predicate: " + p);
			//divide by 3 for each inequality
			if (p.isInequality()) {
				logger.debug("predicate contains < or >");
				try {
					card = Math.max(1, card / 3);
					logger.debug("card: " + card);
				} catch (ArithmeticException e) {
					/* cards less then 3 will cause a divide
					 * by zero1
					 */
					card = 1;
					logger.debug("exception, divide by zero, card: " + card);
				}
			} else {
				/*
				 * Consider whether attributes are keys. Considers 
				 * non-composite primary keys only.
				 */
				logger.debug("check whether attributes are keys");
				boolean lKey = false;//the left relation attribute is a key
				boolean rKey = false;//the right relation attribute is a key

				//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
				logger.debug("p.getSuperLexical1(): " + p.getSuperLexical1());
				if (p.getSuperLexical1() != null) {
					SuperLexical attr1 = p.getSuperLexical1();
					logger.debug("superLexical1: " + attr1);
					logger.debug("isPrimaryKey(attr1): " + isPrimaryKey(attr1));
					logger.debug("superAbstract != null: " + (superAbstract != null));
					if (isPrimaryKey(attr1) && (superAbstract != null)) {
						logger.debug("isPrimaryKey");
						if (attr1.getParentSuperAbstract() == superAbstract)
							rKey = true;
						else
							lKey = true;
					}
					logger.debug("rKey: " + rKey);
					logger.debug("lKey: " + lKey);
				}
				logger.debug("p.getSuperLexical2(): " + p.getSuperLexical2());
				if (p.getSuperLexical2() != null) {
					SuperLexical attr2 = p.getSuperLexical2();
					logger.debug("superLexical2: " + attr2);
					logger.debug("isPrimaryKey(attr2): " + isPrimaryKey(attr2));
					logger.debug("superAbstract != null: " + (superAbstract != null));
					if (isPrimaryKey(attr2) && (superAbstract != null)) {
						logger.debug("isPrimaryKey");
						if (attr2.getParentSuperAbstract() == superAbstract)
							rKey = true;
						else
							lKey = true;
					}
					logger.debug("rKey: " + rKey);
					logger.debug("lKey: " + lKey);
				}
				//divide by max(r,l) if both keys
				if (lKey && rKey) {
					logger.debug("both are keys");
					try {
						card = Math.max(1, card / (Math.max(leftCard, rightCard)));
						logger.debug("leftCard: " + leftCard);
						logger.debug("rightCard: " + rightCard);
						logger.debug("card: " + card);
					} catch (ArithmeticException e) {
						logger.debug("exception");
						/*
						 * cases where max(left,right) = 0
						 */
						card = 1;
						logger.debug("card: " + card);
					}
					//divide by R if L is the only key    
				} else if (lKey) {
					logger.debug("only lKey");
					try {
						card = Math.max(1, card / rightCard);
						logger.debug("card: " + card);
						logger.debug("rightCard: " + rightCard);
					} catch (ArithmeticException e) {
						card = 1;
						logger.debug("exception, card: " + card);
					}
					//divide by L if R is the only key    
				} else if (rKey) {
					logger.debug("only rKey");
					try {
						card = Math.max(1, card / leftCard);
						logger.debug("card: " + card);
						logger.debug("leftCard: " + leftCard);
					} catch (ArithmeticException e) {
						card = 1;
						logger.debug("exception, card: " + card);
					}
					//if neither are keys divide by max(R/5,L/5)    
				} else {
					logger.debug("neither are keys");
					try {
						card = Math.max(1, card / (Math.max(rightCard / 5, leftCard / 5)));
						logger.debug("card: " + card);
						logger.debug("rightCard: " + rightCard);
						logger.debug("leftCard: " + leftCard);
					} catch (ArithmeticException e) {
						/*
						 * can happen with small cardinalities < 5 
						 */
						card = 1;
						logger.debug("exception, card: " + card);
					}
				}
			}
		}
		logger.debug("card: " + card);
		return card;
	}

	/*
	 * Estimate the cardinality of applying the given predicate in scan or 
	 * operation call operators (not join operators).
	 */
	private long estimateApplyPredicate(long inputCardinality, Predicate predicate) throws LookupException, OptimisationException {
		//TODO this doesn't take into account the case that the key is a composite key
		logger.debug("in estimateApplyPredicates");
		if (predicate.isInequality()) {
			return (inputCardinality / 3);
		} else if (predicate.isSuperLexicalEquatedToConstant()) {
			SuperLexical attr = null;
			if (predicate.getSuperLexical1() != null)
				attr = predicate.getSuperLexical1();
			else
				attr = predicate.getSuperLexical2();
			if (isPrimaryKey(attr))
				return 1;
			else
				return (inputCardinality / 10);
		}
		/*else if (predicate.isFunctionEquatedToConstant()) {
			return (inputCardinality / 10);
		} else if (predicate.isFunctionEquatedToFunction()) {
			return (inputCardinality / 5);
		} 
		*/
		else if (predicate.isSuperLexicalEquatedToSuperLexical()) {
			return (inputCardinality / 20);
		}
		/*else if (predicate.isSuperLexicalReferenceEquatedToFunction()) {
			return (inputCardinality / 5);
		}
		*/
		throw new OptimisationException("Unable to estimate cardinality for " + "predicate " + predicate.toString());
	}

	/*
	 * Following ordering of joins, subqueries and function calls,
	 * we may be left with some predicates of the form
	 * argument IN subquery (in opCall and join ops only) 
	 * where the sub-query has been incorporated
	 * into the query plan by a join or cartesian product.
	 * All 'IN' operators therefore have to be replaced by '=' operators
	 */
	//should already have been replaced - TODO make sure that's the case
	/*
	private void processSubQueryPredicates(EvaluatorOperator evaluatorOperator) {
		logger.debug("in processSubQueryPredicates");
		logger.debug("evaluatorOperator: " + evaluatorOperator);
		List<Predicate> predicates = null;
		if (evaluatorOperator instanceof JoinOperatorImpl)
			predicates = ((JoinOperator) evaluatorOperator).getPredicates();
		
		//TODO sort out operationCallOperators - Functions?
		//else if (evaluatorOperator instanceof OperationCallOperator)
		//	predicates = ((OperationCallOperator) operator).getPredicates();
		
		if (predicates != null) {
			for (Predicate p : predicates) {
				if (!p.getOperator().equalsIgnoreCase("in"))
					continue;
				p.setOperator("=");
			}
		}
		if (evaluatorOperator.getLhsInput() != null)
			processSubQueryPredicates(evaluatorOperator.getLhsInput());
		if (evaluatorOperator.getRhsInput() != null)
			processSubQueryPredicates(evaluatorOperator.getRhsInput());
	}
	*/

	/*
	 * A predicate is a join predicate if one attribute is a bound variable
	 * and the other predicate is an attribute of the relation
	 */
	/*
	private boolean isJoinPredicate(Predicate predicate, SuperAbstract superAbstract) {
		SuperLexical arg1 = predicate.getSuperLexical1();
		SuperLexical arg2 = predicate.getSuperLexical2();
		if (isBoundVariable(arg1)) {
			if (arg1.getSuperAbstract().equals(superAbstract))
				return true;
			
			//if (isSuperLexicalOfSuperAbstract(arg2, superAbstract))
			//	return true;
			
		} else if (isBoundVariable(arg2)) {
			
			//if (isSuperLexicalOfSuperAbstract(arg1, superAbstract))
			//	return true;
			
			if (arg1.getSuperAbstract().equals(superAbstract))
				return true;
		}
		return false;
	}
	*/

	/* (non-Javadoc)
	 * @see uk.org.ogsadai.dqp.querycompiler.translator.Query#getApplicablePredicates(java.util.Collection,String)
	 */
	private Set<JoinOperator> getApplicableJoinOperators(String varName, Set<String> boundVars) {
		//need to add the specified variable to the bound variables
		//already pushed other predicates down to scan operators, i.e., only need to sort out join ops here
		logger.debug("in getApplicableJoinOperators");
		logger.debug("varName: " + varName);
		logger.debug("boundVars: " + boundVars);
		//boundVars = new HashSet<String>(boundVars);
		boundVars.add(varName);
		Set<JoinOperator> applicableJoinOps = new LinkedHashSet<JoinOperator>();
		//Iterator<Predicate> it = predicates.iterator();
		logger.debug("before checking joinOperators");
		for (JoinOperator joinOp : remainingJoinOperators) {
			logger.debug("joinOp: " + joinOp);
			boolean involvesSpecifiedVar = false;
			if (referencesVariable(joinOp.getLhsInput(), varName))
				involvesSpecifiedVar = true;
			if (referencesVariable(joinOp.getRhsInput(), varName))
				involvesSpecifiedVar = true;
			if (!involvesSpecifiedVar)
				continue;
			if (boundVariable(joinOp.getLhsInput(), boundVars) && boundVariable(joinOp.getRhsInput(), boundVars)) {
				logger.debug("add joinOp to applicableJoinOps");
				applicableJoinOps.add(joinOp);
			}
		}
		boundVars.remove(varName);
		return applicableJoinOps;
	}

	/*
	 * Returns true if the function can be evaluated given the bound
	 * variables.
	 */
	//TODO sort out functions
	/*
	private static boolean canEvaluate(Function function, Collection<String> boundVars) {
		List<QueryElement> parameters = function.getParameters();
		for (int i = 0; i < parameters.size(); i++) {
			QueryElement e = parameters.get(i);
			if (!boundVariable(e, boundVars))
				return false;
		}
		return true;
	}
	*/

	/*
	 * Returns true if the element is a variable with the specified
	 * name or an attribute from a table with the specified variable name
	 */
	private boolean referencesVariable(MappingOperator mappingOperator, String varName) {
		logger.debug("in referencesVariable");
		logger.debug("mappingOperator: " + mappingOperator);
		logger.debug("varName: " + varName);
		String variableName = mappingOperator.getVariableName();
		logger.debug("variableName: " + variableName);
		if (variableName.equals(varName)) {
			logger.debug("return true");
			return true;
		}
		/*
		if (element instanceof SuperLexicalReference) {
			String rVar = ((SuperLexicalReference) element).getSuperAbstractReference().getVariableName();
			if (rVar.equals(varName))
				return true;
		} else if (element instanceof Variable) {
			String var = ((Variable) element).getVariableName();
			if (var.equals(varName))
				return true;
		}
		*/
		logger.debug("return false");
		return false;
	}

	/*
	 * Returns true if the element is in the bound variables.
	 * If the element is a literal, the method will return TRUE.
	 */
	private boolean boundVariable(MappingOperator mappingOperator, Set<String> boundVars) {
		logger.debug("in boundVariable");
		//if ( element instanceof Function) {
		//    return canEvaluate((Function)element,boundVars);
		logger.debug("mappingOperator: " + mappingOperator);
		String variableName = mappingOperator.getVariableName();
		logger.debug("variableName: " + variableName);
		logger.debug("boundVars: " + boundVars);
		if (!boundVars.contains(variableName)) {
			logger.debug("return false");
			return false;
		}
		/*
		if (element instanceof Function) {
			String rVar = ((Function) element).getVariableName();
			if (!boundVars.contains(rVar))
				return false;
		} else if (element instanceof SuperLexicalReference) {
			String rVar = ((SuperLexicalReference) element).getSuperAbstractReference().getVariableName();
			if (!boundVars.contains(rVar))
				return false;
		} else if (element instanceof Variable) {
			String var = ((Variable) element).getVariableName();
			if (!boundVars.contains(var))
				return false;
		}
		*/
		logger.debug("return true");
		return true;
	}

	/* (non-Javadoc)
	 * @see uk.org.ogsadai.dqp.querycompiler.translator.Query#getApplicableFunction(java.util.Collection)
	 */
	//TODO sort out Functions
	/*
	public Collection<Function> getApplicableFunctions(Collection<String> boundVars) {
		Iterator<Function> it = functions.values().iterator();
		HashSet<Function> applicableFn = new HashSet<Function>();
		while (it.hasNext()) {
			Function f = it.next();
			if (canEvaluate(f, boundVars))
				applicableFn.add(f);
		}
		return applicableFn;
	}
	*/

	/*
	 * Returns true if the variable is bound or the element
	 * is an attribute of a bound variable
	 */
	@SuppressWarnings("unused")
	private boolean isBoundVariable(MappingOperator element) {
		logger.debug("in isBoundVariable");
		logger.debug("mappingOperator: " + element);
		String varName = null;
		//Variable = {Function, Aggregate, SubQuery, SuperAbstractReference}
		if (element.getVariableName() != null & element.getVariableName().length() > 0) {
			varName = element.getVariableName();
		}
		/*
		else if (element instanceof SuperLexicalReference) {
			varName = ((SuperLexicalReference) element).getSuperAbstractReference().getVariableName();
		}
		*/
		if (boundVar == null)
			return false;
		return (boundVar.contains(varName));
	}

	/*
	 * THIS METHOD IS USED WHEN A PREDICATE CONTAINS A SUBQUERY.
	 * The variable name of a sub-query needs to be bound as
	 * subqueryvar.var rather than just subqueryvar.
	 * e.g. If a subquery is bound to subquery1, its result
	 * is bound to subquery1.person.name; this method extracts
	 * such a result var name from the sub-query.
	 * Note that this method is only used for currently supported
	 * subqueries which can be found in predicates of the following form only:
	 * x in (subquery)
	 * x = (aggregate subquery) 
	 * Because the sub-queries found in predicates are as shown,
	 * it is safe to assume that the result of the subquery returns
	 * a single field. If a subquery contains multiple fields here, it won't 
	 * produce an error, it will just use the first field. So, a predicate
	 * x in (select a and b from table) will just function the same as
	 * x in (select a from table). The type checker decides whether or not
	 * to allow this or not. 
	 * NOTE: direct children of a UNION operation are sub-queries, but
	 * they are not handled here - this method is used when a sub-query
	 * is found in a predicate. 
	 */
	private String getSubQueryResultVarName(MappingOperator mappingOperator) {
		/*
		Iterator<QueryElement> it = subQuery.getQuery().getResultTuple().iterator();
		QueryElement field = it.next();
		String s = null;
		// field must be an attribute or variable
		if (field instanceof SuperLexicalReference) {
			s = (((SuperLexicalReference) field).getSuperAbstractReference().getVariableName());
		} else if (field instanceof Variable) {
			s = (((Variable) field).getVariableName());
		}
		return s;
		*/
		/*
		EvaluatorOperator evaluatorOperator = subQueryPlan.plan;
		String variableName = evaluatorOperator.getVariableName();
		logger.debug("variableName of rootOperator of subQuery: " + variableName);
		//TODO check this, not sure it'll return the right variableName
		return variableName;
		*/
		String variableName = mappingOperator.getVariableName();
		return variableName;
	}

	/*
	 * Finalise the plan by adding any aggregate operator
	 * and a reduce operator to select the result tuple.
	 * This method is invoked for non-UNION queries only.
	 */

	//TODO sort out code duplication
	private void finalise(Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in finalise");
		//TODO sort out finalise of subQueries and union queries
		//TODO sort out aggregates and order by
		if (getPlan() == null) {
			logger.debug("plan == null: plan = workingPlan");
			logger.debug("cardinalities should have correct values - TODO check");
			//TODO check cardinalities have correct values
			if (workingPlan.getDataSource() == null)
				logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
			logger.debug("workingPlan.getResultType: " + workingPlan.getResultType());
			if (reduceOperator != null) {
				logger.debug("found reduceOperator: " + reduceOperator);
				if (reduceOperator.getDataSource() != null && !reduceOperator.getDataSource().equals(workingPlan.getDataSource()))
					logger.error("reduceOperator and workingPlan on different datasources");
				logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());
				logger.debug("reduceOperator.getDataSource(): " + reduceOperator.getDataSource());
				ReduceOperatorImpl reduceOperatorImpl = new ReduceOperatorImpl(workingPlan,
						reduceOperator.getReconcilingExpression().getExpression(), reduceOperator.getSuperLexicals(), reduceOperator.getResultType(),
						currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
				reduceOperatorImpl.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
				reduceOperatorImpl.setAndOr(reduceOperator.getAndOr());
				reduceOperatorImpl.addAllMappingsUsedForExpansion(reduceOperator.getMappingsUsedForExpansion());
				reduceOperatorImpl.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
				logger.debug("reduceOperatorImpl.getResultType(): " + reduceOperatorImpl.getResultType());
				logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + reduceOperatorImpl.getJoinPredicatesCarried());
				logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + reduceOperatorImpl.getJoinOperatorsCarried());
				logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + reduceOperatorImpl.getMappingsUsedForExpansion());
				logger.debug("added reduceOperatorImpl: " + reduceOperatorImpl);
				logger.debug("reduceOperator.getVariableName: " + reduceOperator.getVariableName());
				logger.debug("workingPlan.getVariableName: " + workingPlan.getVariableName());
				if (reduceOperator.getVariableName() == null)
					reduceOperatorImpl.setVariableName(workingPlan.getVariableName());
				else {
					if (!reduceOperator.getVariableName().equals(workingPlan.getVariableName()))
						logger.error("reduceOperator and workingPlan have different VariableNames - TODO check this");
					reduceOperatorImpl.setVariableName(reduceOperator.getVariableName());
				}
				logger.debug("reduceOperatorImpl.getVariableName(): " + reduceOperatorImpl.getVariableName());
				workingPlan = reduceOperatorImpl;
			}

			/*
			EvaluateExternallyOperatorImpl evaluateExternallyOp = new EvaluateExternallyOperatorImpl(workingPlan, workingPlan.getResultType(),
					currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
			evaluateExternallyOp.setVariableName(workingPlan.getVariableName());
			evaluateExternallyOp.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
			logger.debug("evaluateExternallyOp.getVariableName():" + evaluateExternallyOp.getVariableName());
			logger.debug("evaluateExternallyOp.getDataSource(): " + evaluateExternallyOp.getDataSource());
			logger.debug("evaluateExternallyOp.getResultType(): " + evaluateExternallyOp.getResultType());
			logger.debug("evaluateExternallyOp.getJoinPredicatesCarried(): " + evaluateExternallyOp.getJoinPredicatesCarried());
			logger.debug("evaluateExternallyOp.getJoinOperatorsCarried(): " + evaluateExternallyOp.getJoinOperatorsCarried());
			logger.debug("evaluateExternallyOp.getExternalDataSourcePoolUtilService: " + evaluateExternallyOp.getExternalDataSourcePoolUtilService());
			//evaluateExternallyOp.setResultType(workingPlan.getResultType());
			workingPlan = evaluateExternallyOp;
			*/
			setPlan(workingPlan);
			logger.debug("plan: " + getPlan());
			workingPlan = null;
		} else {
			logger.debug("plan != null: add join between plan and workingPlan");
			logger.debug("cardinalities should have correct values - TODO check");
			//TODO check cardinalities have correct values

			logger.debug("plan: " + getPlan());
			logger.debug("workingPlan: " + workingPlan);

			logger.debug("joinPredicatesCarried in workingPlan: " + workingPlan.getJoinPredicatesCarried());
			logger.debug("joinOperatorsCarried in workingPlan: " + workingPlan.getJoinOperatorsCarried());

			if (workingPlan.getJoinPredicatesCarried() == null)
				logger.error("no joinPredicatesCarried - TODO proper error handling");
			//TODO no joinPredicatesCarried - proper error handling

			if (workingPlan.getDataSource() == null)
				logger.error("all ops in working plan should be on same source, but source is unknown - something wrong here");
			logger.debug("workingPlan.getResultType: " + workingPlan.getResultType());

			logger.debug("add reduce operator for all superlexicals to workingPlan");

			Map<String, SuperLexical> varNameSuperLexicalsMap = new LinkedHashMap<String, SuperLexical>();
			StringBuilder reconcilingExpressionBuilder = new StringBuilder();

			ResultType resultTypeOfCurrentRootOperator = workingPlan.getResultType();
			Map<String, ResultField> resultFields = resultTypeOfCurrentRootOperator.getResultFields();
			Set<String> resultFieldNames = resultFields.keySet();
			int j = 0;
			//for (ResultField field : resultFields) {
			for (String resultFieldName : resultFieldNames) {
				if (j > 0)
					reconcilingExpressionBuilder.append(", ");
				//reconcilingExpressionBuilder.append(field.getFieldName());
				reconcilingExpressionBuilder.append(resultFieldName);
				//CanonicalModelConstruct cmc = field.getCanonicalModelConstruct();
				CanonicalModelConstruct cmc = resultFields.get(resultFieldName).getCanonicalModelConstruct();
				SuperLexical sl = null;
				if (cmc instanceof SuperLexical)
					sl = (SuperLexical) cmc;

				//varNameSuperLexicalsMap.put(field.getFieldName(), sl);
				varNameSuperLexicalsMap.put(resultFieldName, sl);
				j++;
			}

			ReduceOperatorImpl reduceOperatorImpl = new ReduceOperatorImpl(workingPlan, reconcilingExpressionBuilder.toString(),
					varNameSuperLexicalsMap, workingPlan.getResultType(), currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(),
					workingPlan.getDataSource());
			reduceOperatorImpl.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
			reduceOperatorImpl.setVariableName(workingPlan.getVariableName());
			reduceOperatorImpl.setAndOr(workingPlan.getAndOr());
			reduceOperatorImpl.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());

			logger.debug("reduceOperatorImpl.getResultType(): " + reduceOperatorImpl.getResultType());
			logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + reduceOperatorImpl.getJoinPredicatesCarried());
			logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + reduceOperatorImpl.getJoinOperatorsCarried());
			logger.debug("added reduceOperatorImpl: " + reduceOperatorImpl);
			logger.debug("reduceOperator.getVariableName: " + reduceOperator.getVariableName());
			logger.debug("workingPlan.getVariableName: " + workingPlan.getVariableName());
			logger.debug("reduceOperatorImpl.getVariableName(): " + reduceOperatorImpl.getVariableName());
			logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + reduceOperatorImpl.getMappingsUsedForExpansion());
			workingPlan = reduceOperatorImpl;

			logger.debug("add evaluateExternallyOp to workingPlan");
			EvaluateExternallyOperatorImpl evaluateExternallyOp = new EvaluateExternallyOperatorImpl(workingPlan, workingPlan.getResultType(),
					currentWorkingPlanCardinality, workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
			evaluateExternallyOp.setVariableName(workingPlan.getVariableName());
			evaluateExternallyOp.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
			evaluateExternallyOp.setAndOr(workingPlan.getAndOr());
			evaluateExternallyOp.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
			evaluateExternallyOp.setExternalDataSourcePoolUtilService(externalDataSourcePoolUtilService);
			//TODO could change that to setting the external data source itself instead of the dataSourcePoolUtil

			if (controlParameters != null) {
				if (controlParameters.containsKey(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS)) {
					int maxNumberOfResults = -1;
					maxNumberOfResults = Integer.getInteger(controlParameters.get(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS).getValue());
					logger.debug("maxNumberOfResults: " + maxNumberOfResults);
					evaluateExternallyOp.setMaxNumberOfResults(maxNumberOfResults);
				}

				if (controlParameters.containsKey(ControlParameterType.QUERY_FETCH_SIZE)) {
					int fetchSize = -1;
					fetchSize = Integer.getInteger(controlParameters.get(ControlParameterType.QUERY_FETCH_SIZE).getValue());
					logger.debug("fetchSize: " + fetchSize);
					evaluateExternallyOp.setFetchSize(fetchSize);
				}
			}

			logger.debug("evaluateExternallyOp.getMaxNumberOfResults: " + evaluateExternallyOp.getMaxNumberOfResults());
			logger.debug("evaluateExternallyOp.getFetchSize: " + evaluateExternallyOp.getFetchSize());
			logger.debug("evaluateExternallyOp.getVariableName(): " + evaluateExternallyOp.getVariableName());
			logger.debug("evaluateExternallyOp.getDataSource(): " + evaluateExternallyOp.getDataSource());
			logger.debug("evaluateExternallyOp.getResultType: " + evaluateExternallyOp.getResultType());
			logger.debug("evaluateExternallyOp.getJoinPredicatesCarried(): " + evaluateExternallyOp.getJoinPredicatesCarried());
			logger.debug("evaluateExternallyOp.getJoinOperatorsCarried(): " + evaluateExternallyOp.getJoinOperatorsCarried());
			logger.debug("evaluateExetrnallyOp.getMappingsUsedForExpansion(): " + evaluateExternallyOp.getMappingsUsedForExpansion());
			logger.debug("evaluateExternallyOp.getExternalDataSourcePoolUtilService: " + evaluateExternallyOp.getExternalDataSourcePoolUtilService());
			workingPlan = evaluateExternallyOp;

			logger.debug("workingPlan = evaluateExternallyOp: " + workingPlan);

			long planJoinCard = estimateJoinCardinality(currentPlanCardinality, currentWorkingPlanCardinality,
					workingPlan.getJoinPredicatesCarried(), null);
			logger.debug("planJoinCard: " + planJoinCard);

			ResultType resultType = new ResultType(getPlan().getResultType(), workingPlan.getResultType());
			logger.debug("resultType: " + resultType);

			JoinOperatorImpl join = new JoinOperatorImpl(workingPlan.getJoinPredicatesCarried(), getPlan(), workingPlan, resultType, planJoinCard,
					null, null);
			join.setReconcilingExpression(workingPlan.getJoinOperatorsCarried().iterator().next().getReconcilingExpression().getExpression());
			join.setAndOr(workingPlan.getJoinOperatorsCarried().iterator().next().getAndOr());
			join.addAllMappingsUsedForExpansion(workingPlan.getJoinOperatorsCarried().iterator().next().getMappingsUsedForExpansion());
			join.addAllMappingsUsedForExpansion(getPlan().getMappingsUsedForExpansion());
			join.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());

			if (workingPlan.getJoinOperatorsCarried().size() > 1)
				logger.error("more than one joinOperator in workingPlan.getJoinOperatorsCarried - TODO sort this outew");

			StringBuilder joinVarName = new StringBuilder();

			for (int i = 0; i < workingPlan.getJoinOperatorsCarried().size(); i++) {
				if (i > 0) {
					joinVarName.append(", ");
					logger.debug("more than one bestJoinOp");
				}
				joinVarName.append(workingPlan.getJoinOperatorsCarried().iterator().next().getVariableName());
			}

			if (!joinVarName.toString().equals("null"))
				join.setVariableName(joinVarName.toString());
			else
				join.setVariableName(null);

			logger.debug("join.getResultType: " + join.getResultType());
			logger.debug("join.getVariableName(): " + join.getVariableName());
			logger.debug("join.getReconcilingExpression(): " + join.getReconcilingExpression());
			logger.debug("join.getCardinality(): " + join.getCardinality());
			logger.debug("join.getMappingsUsedForExpansion(): " + join.getMappingsUsedForExpansion());

			logger.debug("join.getLhsInput().getVariableName(): " + join.getLhsInput().getVariableName());
			logger.debug("join.getRhsInput().getVariableName(): " + join.getRhsInput().getVariableName());

			logger.debug("about to remove workingPlan.getJoinOperatorsCarried() from remainingJoinOperators: "
					+ workingPlan.getJoinOperatorsCarried());
			logger.debug("remainingJoinOperators: " + remainingJoinOperators);
			remainingJoinOperators.removeAll(workingPlan.getJoinOperatorsCarried());
			logger.debug("remainingJoinOperators after removing joinOps: " + remainingJoinOperators);

			workingPlan = join;
			logger.debug("workingPlan = join: " + workingPlan);

			//TODO check resultType, not sure which one is right
			//join.setResultType(getPlan().getResultType());

			if (reduceOperator != null) {
				logger.debug("found reduceOperator: " + reduceOperator);
				if (reduceOperator.getDataSource() != null && !reduceOperator.getDataSource().equals(workingPlan.getDataSource()))
					logger.error("reduceOperator and workingPlan on different datasources");
				logger.debug("workingPlan.getDataSource(): " + workingPlan.getDataSource());
				logger.debug("reduceOperator.getDataSource(): " + reduceOperator.getDataSource());
				ReduceOperatorImpl finalReduceOperatorImpl = new ReduceOperatorImpl(workingPlan, reduceOperator.getReconcilingExpression()
						.getExpression(), reduceOperator.getSuperLexicals(), reduceOperator.getResultType(), currentWorkingPlanCardinality,
						workingPlan.getJoinPredicatesCarried(), workingPlan.getDataSource());
				finalReduceOperatorImpl.setAndOr(reduceOperator.getAndOr());
				finalReduceOperatorImpl.setJoinOperatorsCarried(workingPlan.getJoinOperatorsCarried());
				finalReduceOperatorImpl.addAllMappingsUsedForExpansion(workingPlan.getMappingsUsedForExpansion());
				logger.debug("added finalReduceOperatorImpl: " + finalReduceOperatorImpl);
				logger.debug("reduceOperatorImpl.getMappingsUsedForExpansion(): " + finalReduceOperatorImpl.getMappingsUsedForExpansion());
				logger.debug("reduceOperatorImpl.getJoinPredicatesCarried(): " + finalReduceOperatorImpl.getJoinPredicatesCarried());
				logger.debug("reduceOperatorImpl.getJoinOperatorsCarried(): " + finalReduceOperatorImpl.getJoinOperatorsCarried());
				logger.debug("reduceOperator.getVariableName: " + finalReduceOperatorImpl.getVariableName());
				logger.debug("workingPlan.getVariableName: " + workingPlan.getVariableName());
				if (reduceOperator.getVariableName() == null)
					finalReduceOperatorImpl.setVariableName(workingPlan.getVariableName());
				else {
					if (!reduceOperator.getVariableName().equals(workingPlan.getVariableName()))
						logger.error("reduceOperator and workingPlan have different VariableNames - TODO check this");
					finalReduceOperatorImpl.setVariableName(reduceOperator.getVariableName());
				}
				logger.debug("finalReduceOperatorImpl.getVariableName(): " + finalReduceOperatorImpl.getVariableName());
				workingPlan = finalReduceOperatorImpl;
				logger.debug("workingPlan = finalReduceOperatorImpl: " + workingPlan);
			}

			logger.debug("workingPlan: " + workingPlan);

			setPlan(workingPlan);
			logger.debug("plan: " + plan);
			logger.debug("workingPlan: " + workingPlan);
			logger.debug("plan.getJoinPredicatesCarried(): " + getPlan().getJoinPredicatesCarried());
			logger.debug("plan.getJoinOperatorsCarried(): " + getPlan().getJoinOperatorsCarried());
			workingPlan = null;
			logger.debug("workingPlan: " + workingPlan);
			logger.debug("plan: " + plan);
		}

		//TODO sort out aggregate
		/*
		if (query.getAggregate() != null) {
			addAggregate(query.getAggregate());
		}
		*/
		/*
		 * ORDER BY
		 */
		//TODO sort out orderBy
		/*
		if (query.getOrderByList() != null) {
			//TODO sort out orderBy
			//ORDER BY - each item refers to an attribute
			//List<OrderByItem> orderByElements = new ArrayList<OrderByItem>();
			//plan = new OrderByOperator(plan,plan.getCardinality(),query.getOrderByList());
		}
		*/
		/* NOTE - following method invocation also sets PLAN = REDUCE */
		//TODO should already have a reduce operator as the root if required, check this though
		//setResultType(query.getResultType());
	}

	/*
	 * Only one aggregate is permitted.
	 */
	//TODO sort out aggregates
	/*
	private void addAggregate(Aggregate aggregate) {
		logger.debug("Adding aggregate operator: " + aggregate.getVariableName());
		currentOverallCardinality = 1;
		ReduceOperator reduce = new ReduceOperator(getPlan(), aggregate, currentOverallCardinality, null, getPlan().getSourceId());
		reduce.setResultType(getPlan().getResultType());
		setPlan(reduce);
	}
	*/

	/*
	 * Add a reduce operator to the plan
	 */
	//TODO sort out reduce operator
	/*
	private Operator setResultTuple(List<QueryElement> selectList) {
		if (logger.isDebugEnabled()) {
			String s = ("Adding reduce operator ( ");
			Iterator<QueryElement> it = selectList.iterator();
			while (it.hasNext()) {
				QueryElement e = it.next();
				if (e instanceof Variable) {
					s = s + ((Variable) e).getVariableName();
				} else if (e instanceof SuperLexicalReference) {
					SuperLexicalReference a = (SuperLexicalReference) e;
					s = s + a.getSuperAbstractReference().getSuperAbstractName() + "." + a.getSuperLexicalName() + " ";
				}
			}
			logger.debug(s + ")");
		}
		if (getPlan() instanceof ExternalCallOperator) {
			Operator inputToExternalCallOperator = getPlan().getInput(0);
			ReduceOperator reduceOperator = new ReduceOperator(inputToExternalCallOperator, selectList, currentOverallCardinality, null,
					inputToExternalCallOperator.getSourceId());
			getPlan().replaceInput(inputToExternalCallOperator, reduceOperator);
			return getPlan();
		} else {
			ReduceOperator reduce = new ReduceOperator(getPlan(), selectList, currentOverallCardinality, null, getPlan().getSourceId());
			setPlan(reduce);
			return reduce;
		}
	}
	*/

	/**
	 * @param plan the plan to set
	 */
	public void setPlan(EvaluatorOperator plan) {
		this.plan = plan;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.operators.query.queryoptimisier.LogicalQueryOptimiserService#getPlan()
	 */
	public EvaluatorOperator getPlan() {
		return plan;
	}

}

/**
 * Represents an optimised sub-query plan. Used during logical optimisation
 * to store the query plans of already optimised sub-queries.
 */
class SubQueryPlan {

	EvaluatorOperator plan;//the root operator of the plan
	long cardinality;//estimated cardinality of the plan

	SubQueryPlan(EvaluatorOperator plan, long cardinality) {
		this.plan = plan;
		this.cardinality = cardinality;
	}

}
