package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

/**
 * Operator that implements hash joins.  Hash joins are joins where the
 * primary join expression is an equality between tuples from one side of the
 * join and tuples from the other.
 * <p>
 * This implementation stores the left hand side tuples and streams the right
 * hand side tuples.  The left hand side tuples are stored according in a hash
 * strutcure for quick retrieval.  The key to this hash structure is derived
 * from the values of those fields of the tuple that must equal the other
 * tuple.
 */
//@Transactional(readOnly = true)
@Scope("prototype")
@Service
public class HashJoinOperatorImpl extends EvaluatorJoinOperatorImpl {

	private static final Logger logger = Logger.getLogger(HashJoinOperatorImpl.class);

	/** ResultInstances in the stored input */
	private List<ResultInstance>[] mStoredResultInstances;

	/** 
	 * Field names of the left hand side types that are used in the
	 * equi-join.
	 */
	private final String[] mLeftNames;

	/** 
	 * Field names of the right hand side types that are used in the
	 * equi-join.
	 */
	private final String[] mRightNames;

	/** 2^mHashTableSizePW2 = size of the has table */
	private final int mHashTableSizePW2;

	public HashJoinOperatorImpl(Set<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
		logger.debug("in HashJoinOperatorImpl");

		List<SuperLexical> joinAttr = new ArrayList<SuperLexical>();
		logger.debug("predicates.size(): " + predicates.size());

		//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
		for (Predicate predicate : predicates) {
			logger.debug("predicate: " + predicate);
			SuperLexical sl1 = predicate.getSuperLexical1();
			SuperAbstract sa1 = sl1.getParentSuperAbstract();
			logger.debug("sl1: " + sl1);
			logger.debug("sa1: " + sa1);
			joinAttr.add(sl1);

			SuperLexical sl2 = predicate.getSuperLexical2();
			SuperAbstract sa2 = sl1.getParentSuperAbstract();
			logger.debug("sl2: " + sl2);
			logger.debug("sa2: " + sa2);
			joinAttr.add(sl2);
		}

		// Create the store
		int hashTableSize = 4096;
		mHashTableSizePW2 = (int) (Math.log(hashTableSize) / Math.log(2));

		mStoredResultInstances = new LinkedList[hashTableSize];
		for (int i = 0; i < hashTableSize; ++i) {
			mStoredResultInstances[i] = new LinkedList<ResultInstance>();
		}

		// Calculate the positions in the left tuple
		mLeftNames = getResultFieldNames(lhsInput.getResultType(), joinAttr);
		logger.debug("mLeftNames: " + mLeftNames);
		mRightNames = getResultFieldNames(rhsInput.getResultType(), joinAttr);
		logger.debug("mRightPositions: " + mRightNames);
	}

	/*
	HashJoinOperatorImpl hashJoin = new HashJoinOperatorImpl(join.getLhsInput(), join.getRhsInput(), join.getPredicates(), join.getCardinality(), join
			.getJoinPredicatesCarried(), join.getDataSource());
	
	HashJoinOperatorImpl hashJoin = new  HashJoinOperatorImpl(List<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, List<Predicate> joinPredicatesCarried, DataSource dataSource) 
	*/

	@Override
	protected void storeResultInstance(final ResultInstance resultInstance) {
		logger.debug("in storeResultInstance");
		logger.debug("resultInstance: " + resultInstance);
		int hashKey = getHashKey(resultInstance, mLeftNames);
		logger.debug("hashKey: " + hashKey);
		mStoredResultInstances[hashKey].add(resultInstance);
	}

	@Override
	protected Iterator getCandidateMatches(final ResultInstance resultInstance) {
		logger.debug("in getCandidateMatch");
		logger.debug("resultInstance: " + resultInstance);
		int hashKey = getHashKey(resultInstance, mRightNames);
		logger.debug("hashKey: " + hashKey);
		return mStoredResultInstances[hashKey].iterator();
	}

	@Override
	protected void tidyUp() {
		logger.debug("in tidyUp");
		mStoredResultInstances = null;
	}

	/** 
	 * Gets the fieldNames of superLexicals within the result type that
	 * appears in the given list of superLexicals.
	 * 
	 * @param resultType   result type.
	 * @param superLexicals superLexicals we wish positions for
	 * 
	 * @return the array of fieldNames for those superLexicals that are in
	 *         the superLexicals list.
	 */
	private String[] getResultFieldNames(final ResultType resultType, final List<SuperLexical> superLexicals) {
		logger.debug("in getResultFieldNames");
		logger.debug("resultType: " + resultType);
		logger.debug("superLexicals: " + superLexicals);
		List<String> resultList = new ArrayList<String>();
		Map<String, ResultField> inField = resultType.getResultFields();
		logger.debug("inField: " + inField);
		Set<String> inFieldNames = inField.keySet();
		//for (int posCount = 0; posCount < inField.size(); posCount++) {
		for (String inFieldName : inFieldNames) {
			//logger.debug("posCount: " + posCount);
			logger.debug("inFieldName: " + inFieldName);
			//CanonicalModelConstruct cmc = inField.get(posCount).getCanonicalModelConstruct();
			CanonicalModelConstruct cmc = inField.get(inFieldName).getCanonicalModelConstruct();
			logger.debug("cmc: " + cmc);
			for (SuperLexical superLexical : superLexicals) {
				logger.debug("superLexical: " + superLexical);
				if (cmc == superLexical) {
					logger.debug("cmc == superLexical");
					//resultList.add(inField.get(posCount).getFieldName());
					resultList.add(inFieldName);
					//logger.debug("add FieldName to resultList: " + inField.get(posCount).getFieldName());
					logger.debug("add FieldName to resultList: " + inFieldName);
					break;
				}
			}
		}

		String[] result = new String[resultList.size()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = (resultList.get(i));
		}
		logger.debug("result: " + result);
		return result;
	}

	/**
	 * Calculates a has value for the given resultInstance with relation to the values
	 * of the given fields.
	 * <p>
	 * The implementation writes all the values of all the resultFields involved
	 * in the hash into a <code>String</code> and computes a hash for that
	 * <code>String</code>. 
	 * 
	 * @param resultInstance            the resultInstance
	 * @param positionsToHash  array of indexes to the fields of the resultInstance
	 *                         that are to be part of the hash
	 * 
	 * @return the hash key
	 */
	private int getHashKey(final ResultInstance resultInstance, final String[] resultFieldsToHash) {
		logger.debug("in getHashKey");
		logger.debug("resultInstance: " + resultInstance);
		logger.debug("resultFieldsToHash: " + resultFieldsToHash);
		StringBuffer sb = new StringBuffer();
		for (String element : resultFieldsToHash) {
			logger.debug("element: " + element);
			if (resultInstance.getResultValue(element) != null) {
				sb.append(resultInstance.getResultValue(element).getValue());
				logger.debug("appended to string to hash: " + resultInstance.getResultValue(element).getValue());
			} else {
				sb.append("null");
				logger.debug("appended to string to hash: null as no value for element");
			}
		}

		return bkdrHash(sb.toString());
	}

	/**
	 * This hash function comes from Brian Kernighan and Dennis Ritchie's book
	 * "The C Programming Language". It is a simple hash function using a set 
	 * of possible seeds which all constitute a pattern of 31....31...31 etc,
	 * it seems to have a good data distribution feature, and is reasonably 
	 * efficient.
	 * To be specific :
	 * For a HASH TABLE SIZE = 4069
	 * Using Goterms database ( 11369 tuples; sample key : GO:0003673 )
	 * This function produces 4064 distinct keys;
	 * The total hashing time was 5.0 milisec. on a Pentium 1.5 GHz machine
	 *
	 * @return integer hashKey
	 * @param str String input
	 **/
	private int bkdrHash(final String str) {
		logger.debug("in bkdrHash");
		logger.debug("str: " + str);
		long seed = 13131; // 31 131 1313 13131 131313 etc..
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}
		hash = hash & 0x7FFFFFFF;
		hash = hash & ((1 << mHashTableSizePW2) - 1);
		logger.debug("hash: " + hash);
		return (int) hash;
	}

	/*
	@Override
	public List<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HashJoinOperatorImpl [");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
