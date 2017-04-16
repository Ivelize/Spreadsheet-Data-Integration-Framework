package uk.ac.manchester.dstoolkit.service.impl.query.queryoptimisier;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.exceptions.LookupException;
import uk.ac.manchester.dstoolkit.exceptions.OptimisationException;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.CartesianProductOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.HashJoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.HashLoopsJoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.IndexNestedLoopsJoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.JoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.NestedLoopsJoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ScanOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ThetaJoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.PhysicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslatorService;

//@Transactional(readOnly = true)
@Service(value = "physicalQueryOptimiserService")
public class PhysicalQueryOptimiserServiceImpl extends QueryOptimiserServiceImpl implements PhysicalQueryOptimiserService {

	private static Logger logger = Logger.getLogger(PhysicalQueryOptimiserServiceImpl.class);

	@Autowired
	@Qualifier("localQueryTranslatorService")
	private LocalQueryTranslatorService localQueryTranslator;

	private EvaluatorOperator rootOperator = null;

	/*
	 * Determines whether the optimiser will select hash loops join
	 */
	private static final boolean allowHashLoopsJoin = true;

	//private final Map<MappingOperator, String> schemaNameOperatorTreeMap = new HashMap<MappingOperator, String>();

	public PhysicalQueryOptimiserServiceImpl() {
	}

	/**
	 * Annotates join operators with one of the following physical  
	 * join algorithms:
	 * - Nested loop join
	 * - Index nested loop join
	 * - Hash join
	 * - Hash loops join
	 * - Theta join
	 * - Cartesian product
	 */
	public EvaluatorOperator chooseJoinOperators(EvaluatorOperator rootOperator, Map<String, ControlParameter> controlParameters)
			throws OptimisationException {
		logger.debug("in chooseJoinOperators");
		this.rootOperator = rootOperator;
		try {
			chooseJoinOperator(rootOperator, null, controlParameters);
		} catch (LookupException e) {
			throw new OptimisationException("Exception accessing metadata", e);
		}
		logger.debug("Plan after choosing join operators: " + rootOperator.toString());
		return this.rootOperator;
	}

	//TODO sort out setops

	/*
	 * Recursively examine each operator in the plan and replace
	 * Join operators.
	 */
	private void chooseJoinOperator(EvaluatorOperator operator, EvaluatorOperator parent, Map<String, ControlParameter> controlParameters)
			throws LookupException {
		logger.debug("in chooseJoinOperator");
		logger.debug("operator: " + operator);
		logger.debug("parent: " + parent);
		logger.debug("operator.getDataSource(): " + operator.getDataSource());
		logger.debug("operator.getMappingsUsedForExpansion(): " + operator.getMappingsUsedForExpansion());

		if (operator instanceof JoinOperatorImpl && operator.getDataSource() == null) { //|| !operator.getLhsInput().getDataSource().equals(operator.getRhsInput().getDataSource())) {
			logger.debug("operator is JoinOperatorImpl and join is between inputs from different sources");
			JoinOperatorImpl join = (JoinOperatorImpl) operator;
			Set<Predicate> preds = join.getPredicates();
			logger.debug("predicates: " + preds);

			logger.debug("join: " + join);
			logger.debug("parent: " + parent);
			operator = insertHashJoin(join, parent);
			logger.debug("operator, hashJoin: " + operator);

			/*
			float rCard = join.getRhsInput().getCardinality();
			float lCard = join.getLhsInput().getCardinality();
			logger.debug("lCard: " + lCard);
			logger.debug("rCard: " + rCard);

			logger.debug("calculating cost for hash loops join");
			float costToBuildHashTableWithLeftInput = 0.01f * lCard;
			logger.debug("costToBuildHashTableWithLeftInput: " + costToBuildHashTableWithLeftInput);
			float costToProbeHashTableForMatches = 1 * Math.max(lCard / 4096, 1f) * rCard;
			logger.debug("costToProbeHashTableForMatches: " + costToProbeHashTableForMatches);
			float costToRequestDataFromDataSourceForRightInput = 1500f * Math.min(rCard, 4096);
			logger.debug("costToRequestDataFromDataSourceForRightInput: " + costToRequestDataFromDataSourceForRightInput);
			float costHashLoopsJoin = costToRequestDataFromDataSourceForRightInput + costToBuildHashTableWithLeftInput
					+ costToProbeHashTableForMatches;
			logger.debug("costHashLoopsJoin: " + costHashLoopsJoin);

			logger.debug("calculating cost for hash join");
			float costHashJoin = costToBuildHashTableWithLeftInput + costToProbeHashTableForMatches;
			logger.debug("costHashJoin: " + costHashJoin);

			logger.debug("calculating cost for index nested loop join");
			float sizeOfResultFromRightInputPerCallKey = 1f; //right input is key
			logger.debug("sizeOfResultFromRightInputPerCallKey: " + sizeOfResultFromRightInputPerCallKey);
			float costToRequestJoinedDataFromDataSourceForRightInputWithKey = lCard * 1500f * Math.min(sizeOfResultFromRightInputPerCallKey, 4096);
			logger.debug("costToRequestJoinedDataFromDataSourceForRightInputWithKey: " + costToRequestJoinedDataFromDataSourceForRightInputWithKey);
			float costIndexNestedLoopsJoin = costToRequestJoinedDataFromDataSourceForRightInputWithKey; //cost to retrieve data from leftInput is ignored as it's the same for hash loops join
			logger.debug("costIndexNestedLoopsJoin: " + costIndexNestedLoopsJoin);

			logger.debug("calculating cost for nested loop join");
			float sizeOfResultFromRightInputPerCallNonKey = rCard / 10f;
			logger.debug("sizeOfResultFromRightInputPerCallNonKey: " + sizeOfResultFromRightInputPerCallNonKey);
			float costToRequestJoinedDataFromDataSourceForRightInputWithoutKey = lCard * 1500f
					* Math.min(sizeOfResultFromRightInputPerCallNonKey, 4096);
			logger.debug("costToRequestJoinedDataFromDataSourceForRightInputWithoutKey: "
					+ costToRequestJoinedDataFromDataSourceForRightInputWithoutKey);
			float costNestedLoopsJoin = costToRequestJoinedDataFromDataSourceForRightInputWithoutKey;
			logger.debug("costNestedLoopsJoin: " + costNestedLoopsJoin);

			if (preds == null || preds.size() == 0) {
				logger.debug("no predicates, choosing cartesian product join");
				//if there are no predicates, a cartesian product is inserted

				operator = insertCartesianProduct(join, parent);
				logger.debug("cartesianProduct: " + operator);
			} else if (allEqualityPreds(preds)) {
				logger.debug("allEqualityPreds(preds): " + allEqualityPreds(preds));
				logger.debug("predicates are all equality predicates, nestedLoop, hash, and hash look joins to be considered");
				//Nested loop join, Index nested loop join, Hash join and hash loops join can be considered
				
				double cardRatio = 1; //default cardinality ratio
				logger.debug("before calculating cardRatio: " + cardRatio);
				try {
					cardRatio = rCard / lCard;
					logger.debug("updated cardRatio: " + cardRatio);
				} catch (Exception e) {
					
					 // If there are any problems, e.g. casting large cards to
					 // a double or cardinality estimates of 0 (both of these 
					 // range from very unlikely to impossible depending on
					 // the logical optimiser code) then go with the default card
					 // ratio. 
					 
					logger.warn("Exception computing cardinality ratio " + e);
				}
				boolean rightInputKey = false;
				logger.debug("check whether there is a single join attribute that is the key of the right input");
				 
				 // is there a single join attribute that is the key of the right input?
				 // - this is a condition for selection of a hash-loops join
				 // (note composite keys are ignored here)
				 

				
				 // It may be the case that the join is joining the result
				 // of a sub query - the one exception to the left-deep join
				 // tree that the query compiler produces. A hash loops join
				 // can only be inserted if the right hand input is a
				 // scan operator. 
				 
				boolean hashLoopsPossible = false;
				EvaluatorOperator rightInput = join.getRhsInput();
				logger.debug("rightInput = join.getRhsInput(): " + rightInput);
				if (rightInput instanceof ScanOperatorImpl) {
					logger.debug("right input is ScanOperatorImpl, check whether any of joinPreds is key");
					hashLoopsPossible = true;
					logger.debug("hashLoopsPossible: " + hashLoopsPossible);
					SuperAbstract sa = ((ScanOperatorImpl) rightInput).getSuperAbstract();
					logger.debug("sa: " + sa);
					//Iterator<Predicate> it = preds.iterator();
					for (Predicate p : preds) {
						logger.debug("predicate: " + p);
						//Predicate p = (Predicate) it.next();   
						SuperLexical sl1 = p.getSuperLexical1();
						SuperLexical sl2 = p.getSuperLexical2();
						logger.debug("sl1: " + sl1);
						logger.debug("sl2: " + sl2);
						if (sl1 != null && isSuperLexicalOfSuperAbstract(sl1, sa)) {
							logger.debug("sl1 != null && isSuperLexicalOfSuperAbstract(sl1, sa)");
							if (isPrimaryKey(sl1))
								rightInputKey = true;
							logger.debug("checked sl1, rightInputKey: " + rightInputKey);
						}
						if (sl2 != null && isSuperLexicalOfSuperAbstract(sl2, sa)) {
							logger.debug("sl2 != null && isSuperLexicalOfSuperAbstract(sl2, sa)");
							if (isPrimaryKey(sl2))
								rightInputKey = true;
							logger.debug("checked sl2, rightInputKey: " + rightInputKey);
						}
					}
				} else {
					logger.debug("rightInput isn't ScanOperatorImpl, check whether it's ReduceOperatorImpl");
					if (rightInput instanceof ReduceOperatorImpl) {
						logger.debug("rightInput instanceof ReduceOperatorImpl");
						if (((ReduceOperatorImpl) rightInput).getSuperLexicals() != null) {
							logger.debug("reduceOperator has SuperLexicals");
							if (rightInput.getLhsInput() instanceof ScanOperatorImpl) {
								logger.debug("lhsInput of rightInput(reduceOperatorImpl) is ScanOperatorImpl");
								hashLoopsPossible = true;
							} else
								logger.debug("lhsInput of rightInput(reduceOperatorImpl) isn't ScanOperatorImpl, lhsInput: "
										+ rightInput.getLhsInput());
						} else
							logger.debug("no superLexicals for reduceOperatorImpl");
					} else
						logger.debug("rightInput not ReduceOperatorImpl, rightInput: " + rightInput);
				}

				logger.debug("hashLoopsPossible: " + hashLoopsPossible);

				//TODO depending on join chosen, will have to replace the evaluateExternallyOperator with the ParameterisedEvaluateExternallyOperator

				
				// Possibly prevent the selection of hash loops join if the optimiser.
				 
				if (!allowHashLoopsJoin) {
					logger.debug("allowHashLoopsJoin");
					hashLoopsPossible = false;
					logger.debug("hashLoopsPossible");
				}
				// if there is a key to use for the right input relation,
				// and the right input is at least twice the size of the
				// left input, the place a hash loops join operator
				if ((cardRatio > 2) && (rightInputKey) && (hashLoopsPossible)) {
					//TODO assuming here that when key exists, then index on key exists, check this assumption
					logger
							.debug("ratio > 2, right input is key && hashLoopsPossible: hash loops join or index nested loop join; compare costs of both");
					logger.debug("costHashLoopsJoin: " + costHashLoopsJoin);
					logger.debug("costIndexNestedLoopsJoin: " + costIndexNestedLoopsJoin);

					if (costHashLoopsJoin < costIndexNestedLoopsJoin) {
						logger.debug("costHashLoopsJoin < costIndexNestedLoopsJoin");
						logger.debug("Choosing hash loops join (cardRatio=" + cardRatio + ",rInputKey=" + rightInputKey + ")");
						logger.debug("join: " + join);
						logger.debug("parent: " + parent);
						logger.debug("preds: " + preds);
						operator = insertHashLoopsJoinOperator(join, parent, preds);
						logger.debug("operator, hashLoopsJoin: " + operator);
					} else {
						logger.debug("costHashLoopsJoin >= costIndexNestedLoopsJoin");
						logger.debug("Choosing index nested loops join (cardRatio=" + cardRatio + ",rInputKey=" + rightInputKey + ")");
						logger.debug("join: " + join);
						logger.debug("parent: " + parent);
						logger.debug("preds: " + preds);
						operator = insertIndexNestedLoopsJoinOperator(join, parent);
						logger.debug("operator, indexNestedLoopsJoin: " + operator);
					}

				} else {
					if (costHashJoin < costNestedLoopsJoin) {
						logger.debug("costHashJoin < costNestedLoopsJoin");
						logger.debug("Choosing hash join (cardRatio=" + cardRatio + ",rInputKey=" + rightInputKey + ")");
						// the join is a hash join 
						logger.debug("join: " + join);
						logger.debug("parent: " + parent);
						operator = insertHashJoin(join, parent);
						logger.debug("operator, hashJoin: " + operator);
					} else {
						logger.debug("costHashJoin >= costNestedLoopsJoin");
						logger.debug("Choosing nested loops join (cardRatio=" + cardRatio + ",rInputKey=" + rightInputKey + ")");
						// the join is a nested loops join 
						logger.debug("join: " + join);
						logger.debug("parent: " + parent);
						operator = insertNestedLoopsJoinOperator(join, parent);
						logger.debug("operator, nestedLoopsJoin: " + operator);
					}
				}
			} else if (containsEqualityPred(preds)) {
				logger.debug("containsEqualityPred(preds): " + containsEqualityPred(preds));
				
				// Its possible to insert the hash join. The evaluator supports
				// hash join with additional inequality predicates as long as
				// there is one equality predicate.
				
				//logger.debug("Choosing a hash join for set of predicates which includes inequalities");
				//operator = insertHashJoin(join,parent);

				if (costHashJoin < costNestedLoopsJoin) {
					logger.debug("costHashJoin < costNestedLoopsJoin");
					// the join is a hash join 
					logger.debug("join: " + join);
					logger.debug("parent: " + parent);
					operator = insertHashJoin(join, parent);
					logger.debug("operator, hashJoin: " + operator);
				} else {
					logger.debug("costHashJoin >= costNestedLoopsJoin");
					// the join is a nested loops join 
					logger.debug("join: " + join);
					logger.debug("parent: " + parent);
					operator = insertNestedLoopsJoinOperator(join, parent);
					logger.debug("operator, nestedLoopsJoin: " + operator);
				}

			} else {
				logger.debug("Choosing theta join");
				// its a theta join 
				logger.debug("join: " + join);
				logger.debug("parent: " + parent);
				operator = insertThetaJoin(join, parent);
				logger.debug("operator, thetaJoin: " + operator);
			}
			}
			*/
		}

		if (!(operator instanceof EvaluateExternallyOperatorImpl)) {
			logger.debug("operator not EvaluateExternallyOperatorImpl, check inputs for joins");
			if (operator.getLhsInput() != null) {
				logger.debug("operator.getLhsInput(): " + operator.getLhsInput());
				chooseJoinOperator(operator.getLhsInput(), operator, controlParameters);
			}
			if (operator.getRhsInput() != null) {
				logger.debug("operator.getRhsInput(): " + operator.getRhsInput());
				chooseJoinOperator(operator.getRhsInput(), operator, controlParameters);
			}
		} else {
			logger.debug("operator is EvaluateExternallyOperatorImpl, translate query into SQL or XQuery and check whether it needs to be replaced by ParameterisedExternalCallOperator");

			String queryString = localQueryTranslator.translate((EvaluateExternallyOperatorImpl) operator, null);
			logger.debug("translated evaluateExternallyOperator into query");
			logger.debug("operator: " + operator);
			logger.debug("queryString>>: " + queryString);

			/*
			if (parent instanceof IndexNestedLoopsJoinOperatorImpl || parent instanceof NestedLoopsJoinOperatorImpl) {
				logger
						.debug("parent operator is indexNestedLoopsJoin or nestedLoopsJoin => replace externalCallOperator with parameterisedExternalCallOperator");
				ParameterisedEvaluateExternallyOperatorImpl peco = new ParameterisedEvaluateExternallyOperatorImpl(
						((EvaluateExternallyOperatorImpl) operator).getPlanRootEvaluatorOperator(), operator.getResultType(), operator
								.getCardinality(), operator.getJoinPredicatesCarried(), operator.getDataSource());

				parent.replaceInput(operator, peco);
			}
			*/
		}
	}

	private EvaluatorOperator insertCartesianProduct(JoinOperatorImpl join, EvaluatorOperator parent) {
		logger.debug("in insertThetaJoin");

		CartesianProductOperatorImpl cartProd = new CartesianProductOperatorImpl(join.getPredicates(), join.getLhsInput(), join.getRhsInput(),
				join.getResultType(), join.getCardinality(), join.getJoinPredicatesCarried(), join.getDataSource());
		cartProd.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
		cartProd.setAndOr(join.getAndOr());
		cartProd.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
		cartProd.setReconcilingExpression(join.getReconcilingExpression());
		cartProd.addAllMappingsUsedForExpansion(join.getMappingsUsedForExpansion());

		logger.debug("resultType: " + cartProd.getResultType());
		logger.debug("resultType.getFields: " + cartProd.getResultType().getResultFields());
		logger.debug("cartProd.getMappingsUsedForExpansion(): " + cartProd.getMappingsUsedForExpansion());
		logger.debug("cartProd: " + cartProd);

		parent.replaceInput(join, cartProd);
		return cartProd;

	}

	private EvaluatorOperator insertThetaJoin(JoinOperatorImpl join, EvaluatorOperator parent) {
		logger.debug("in insertThetaJoin");

		ThetaJoinOperatorImpl thetaJoin = new ThetaJoinOperatorImpl(join.getPredicates(), join.getLhsInput(), join.getRhsInput(),
				join.getResultType(), join.getCardinality(), join.getJoinPredicatesCarried(), join.getDataSource());
		thetaJoin.setAndOr(join.getAndOr());
		thetaJoin.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
		thetaJoin.setReconcilingExpression(join.getReconcilingExpression());
		thetaJoin.addAllMappingsUsedForExpansion(join.getMappingsUsedForExpansion());

		logger.debug("thetaJoin.getMappingsUsedForExpansion(): " + thetaJoin.getMappingsUsedForExpansion());
		logger.debug("resultType: " + thetaJoin.getResultType());
		logger.debug("resultType.getFields: " + thetaJoin.getResultType().getResultFields());

		parent.replaceInput(join, thetaJoin);
		return thetaJoin;
	}

	private EvaluatorOperator insertHashJoin(JoinOperatorImpl join, EvaluatorOperator parent) {
		logger.debug("in insertHashJoin");

		HashJoinOperatorImpl hashJoin = new HashJoinOperatorImpl(join.getPredicates(), join.getLhsInput(), join.getRhsInput(), join.getResultType(),
				join.getCardinality(), join.getJoinPredicatesCarried(), join.getDataSource());
		hashJoin.setAndOr(join.getAndOr());
		hashJoin.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
		hashJoin.setReconcilingExpression(join.getReconcilingExpression());
		hashJoin.addAllMappingsUsedForExpansion(join.getMappingsUsedForExpansion());

		logger.debug("hashJoin.getMappingsUsedForExpansion(): " + hashJoin.getMappingsUsedForExpansion());
		logger.debug("resultType: " + hashJoin.getResultType());
		logger.debug("resultType.getFields: " + hashJoin.getResultType().getResultFields());

		parent.replaceInput(join, hashJoin);
		return hashJoin;
	}

	private EvaluatorOperator insertIndexNestedLoopsJoinOperator(JoinOperatorImpl join, EvaluatorOperator parent) {
		logger.debug("in insertIndexNestedLoopsJoinOperator");
		IndexNestedLoopsJoinOperatorImpl inlj = new IndexNestedLoopsJoinOperatorImpl(join.getPredicates(), join.getLhsInput(), join.getRhsInput(),
				join.getResultType(), join.getCardinality(), join.getJoinPredicatesCarried(), join.getDataSource());
		inlj.setAndOr(join.getAndOr());
		inlj.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
		inlj.setReconcilingExpression(join.getReconcilingExpression());
		inlj.addAllMappingsUsedForExpansion(join.getMappingsUsedForExpansion());

		logger.debug("inlj.getMappingsUsedForExpansion(): " + inlj.getMappingsUsedForExpansion());
		logger.debug("resultType: " + inlj.getResultType());
		logger.debug("resultType.getFields: " + inlj.getResultType().getResultFields());

		parent.replaceInput(join, inlj);
		return inlj;
	}

	private EvaluatorOperator insertNestedLoopsJoinOperator(JoinOperatorImpl join, EvaluatorOperator parent) {
		logger.debug("in insertNestedLoopsJoinOperator");

		NestedLoopsJoinOperatorImpl nlj = new NestedLoopsJoinOperatorImpl(join.getPredicates(), join.getLhsInput(), join.getRhsInput(),
				join.getResultType(), join.getCardinality(), join.getJoinPredicatesCarried(), join.getDataSource());
		nlj.setAndOr(join.getAndOr());
		nlj.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
		nlj.setReconcilingExpression(join.getReconcilingExpression());
		nlj.addAllMappingsUsedForExpansion(join.getMappingsUsedForExpansion());

		logger.debug("nlj.getMappingsUsedForExpansion(): " + nlj.getMappingsUsedForExpansion());
		logger.debug("resultType: " + nlj.getResultType());
		logger.debug("resultType.getFields: " + nlj.getResultType().getResultFields());

		parent.replaceInput(join, nlj);
		return nlj;
	}

	/*
	 * Insert a hash loops join operator. It should have already been determined that
	 * the logicalJoin right input is a scan.
	 * 
	 */
	private EvaluatorOperator insertHashLoopsJoinOperator(JoinOperatorImpl join, EvaluatorOperator parent, Set<Predicate> predicates) {
		logger.debug("in insertHashLoopsJoinOperator");
		EvaluatorOperator rhsInput = join.getRhsInput();

		if (rhsInput instanceof ScanOperatorImpl) {
			ScanOperatorImpl scan = (ScanOperatorImpl) rhsInput;
			logger.debug("rhsInput of join is scan: " + scan);
			/*
			 * Assign the above operators. There will always be a scan 
			 * operator. A project operator between the scan/join is 
			 * possible. A parent projection is also possible.
			 */
			//TODO check that the hashloopjoin is actually evaluated on the same sources as the scan op	
			HashLoopsJoinOperatorImpl hlj = new HashLoopsJoinOperatorImpl(predicates, join.getLhsInput(), join.getRhsInput(), join.getResultType(),
					join.getCardinality(), join.getJoinPredicatesCarried(), join.getDataSource());
			hlj.setAndOr(join.getAndOr());
			hlj.setJoinOperatorsCarried(join.getJoinOperatorsCarried());
			hlj.setReconcilingExpression(join.getReconcilingExpression());
			hlj.addAllMappingsUsedForExpansion(join.getMappingsUsedForExpansion());

			logger.debug("hlj.getMappingsUsedForExpansion(): " + hlj.getMappingsUsedForExpansion());
			logger.debug("resultType: " + hlj.getResultType());
			logger.debug("resultType.getFields: " + hlj.getResultType().getResultFields());

			parent.replaceInput(join, hlj);
			return hlj;
		} else
			logger.error("rhsInput of join isn't the expected scanOperatorImpl - TODO check this, rhsInput: " + rhsInput);
		return null;
	}

	/*
	 * Determines whether one of the collection of predicates
	 * involves an equality based predicate
	 */
	private boolean containsEqualityPred(Collection<Predicate> predicates) {
		logger.debug("in containsEqualityPred");
		for (Predicate p : predicates)
			if (p.getOperator().equals("="))
				return true;
		return false;
	}

	/*
	 * Determines whether all predicates a related by an equality
	 */
	private boolean allEqualityPreds(Collection<Predicate> predicates) {
		logger.debug("in allEqualityPreds");
		for (Predicate p : predicates)
			if (p.isInequality())
				return false;
		return true;
	}

	/**
	 * Inserts additional projections to eliminated tuple fields
	 * that are not required further up the tree. Technically, this
	 * is logical optimisation. Physical optimisation may change
	 * the structure of the query plan (because hash loops
	 * join replaces a scan operator as well as a join) however,
	 * so inserting the projections here eliminated the need to
	 * merge/insert more projections are the selection of a hash
	 * loops join.
	 */
	//TODO remove resultFields from reduceOperators that aren't required for further processing
	/*
	public void assignAdditionalProjectOperators() throws OptimisationException {
		logger.debug("in assignAdditionalProjectOperators");
		insertProjectOperators(rootOperator);
		logger.debug("Plan after inserting projections: " + rootOperator.toString());
	}
	*/

}
