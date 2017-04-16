package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.OperatorException;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

public abstract class EvaluatorJoinOperatorImpl extends EvaluatorOperatorImpl {

	private static Logger logger = Logger.getLogger(EvaluatorJoinOperatorImpl.class);

	private Set<Predicate> predicates;

	/** Label identifying the stored input. Used to annotate error messages. */
	static final String STORED_INPUT_LABEL = "left";

	/** 
	 * Label identifying the streamed input.  Used to annotate error messages. 
	 */
	static final String STREAMED_INPUT_LABEL = "right";

	/** 
	 * Interator into the stored resultInstances.  For each streamed tuple we will run
	 * through the stored resultInstance collection using this iterator.
	 */
	private Iterator<ResultInstance> mLhsResultInstanceIterator; //stored

	/**
	 * The tuple currently be processed in the streamed input.
	 */
	private ResultInstance mCurrentRhsResultInstance; //streamed

	public EvaluatorJoinOperatorImpl(Set<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
		setPredicates(predicates);
	}

	public EvaluatorJoinOperatorImpl(Set<Predicate> predicates, String reconcilingExpression, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput,
			ResultType resultType, long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
		this.setPredicates(predicates);
		this.setReconcilingExpression(reconcilingExpression);
	}

	/*
	public JoinOperatorImpl(EvaluatorOperator workingPlan, EvaluatorOperator plan, Collection<Predicate> bestJoinPreds, long workingPlanJoinCard,
			Collection<Predicate> joinPredicatesCarried, DataSource dataSource) {
		// TODO Auto-generated constructor stub
	}
	*/

	/*
	public List<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	/** initializes the operator and calls open on the input
	* @throws OperatorException throws a general exception in case of any error
	* @return boolean - true/false denoting success/failure
	*/
	public boolean open() { //throws Exception {
		logger.debug("Entering EvaluatorJoinOperatorImpl: open: " + this.toString());
		logger.debug("EvaluatorJoinOperatorImpl - this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());

		// Open the stored/lhs input operator
		openInputOperator(this.getLhsInput(), STORED_INPUT_LABEL, logger);

		readInputResultInstance(this.getLhsInput());

		// Close the stored input/lhs operator
		this.getLhsInput().close();

		// Open the streamed/rhs input
		openInputOperator(this.getRhsInput(), STREAMED_INPUT_LABEL, logger);

		// Set that we have no current streamed input tuple
		mCurrentRhsResultInstance = null;

		logger.debug("Exiting EvaluatorJoinOperatorImpl: open: " + this.toString());

		// All has gone successfully
		return true;
	}

	/** Fetches the next resultInstance by calling next on the input operator
	 * @throws OperatorException thrown if there is a problem of the next on input fails
	 * @return the next tuple after computation in the current operator
	 */
	public ResultInstance next() {// throws InterruptedException, OperatorException {
		logger.debug("Entering EvaluatorJoinOperatorImpl: next: " + this.toString());
		logger.debug("this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		ResultInstance result = null;
		logger.debug("result - should be null: " + result);

		while (result == null) {
			logger.debug("in while result == null");
			// If we don't have a current streamed resultInstance or we have iterated
			// right through the stored resultInstance collection then we need to get
			// the next streamed resultInstance and reset the iterator.

			// This is a 'while' rather than an 'if' because this will cause
			// the whole streamed input to be read if the stored input has zero
			// resultInstances.
			while (mCurrentRhsResultInstance == null || !mLhsResultInstanceIterator.hasNext()) {
				logger.debug("in while (mCurrentRhsResultInstance == null || !mLhsResultInstanceIterator.hasNext())");
				ResultInstance resultInstance = this.getRhsInput().next(); //streamed
				logger.debug("resultInstance: " + resultInstance);
				if (!resultInstance.isEof()) {
					logger.debug("!resultInstance.isEof()");
					mCurrentRhsResultInstance = resultInstance;
					logger.debug("mCurrentRhsResultInstance: " + mCurrentRhsResultInstance);
					mLhsResultInstanceIterator = getCandidateMatches(mCurrentRhsResultInstance); //lhs = stored, rhs = streamed
					logger.debug("mLhsResultInstanceIterator: " + mLhsResultInstanceIterator);
				} else {
					logger.debug("eof");
					// End of the streamed input means we are done
					logger.debug("resultInstance, should be eof: " + resultInstance);
					return resultInstance; //is EOF
				}
			}

			// Join the current streamed resultInstance and the next stored resultInstance to
			// produce the candidate result resultInstance.            
			ResultInstance lhsResultInstance = mLhsResultInstanceIterator.next();
			logger.debug("lhsResultInstance: " + lhsResultInstance);
			ResultInstance candidateResult = new ResultInstance(lhsResultInstance, mCurrentRhsResultInstance);
			candidateResult.setResultType(this.getResultType());
			candidateResult.addAllMappings(this.getMappingsUsedForExpansion());
			candidateResult.addAllMappings(lhsResultInstance.getMappings());
			candidateResult.addAllMappings(mCurrentRhsResultInstance.getMappings());
			logger.debug("candidateResult: " + candidateResult);
			logger.debug("candiateResult.getResultResultType(): " + candidateResult.getResultType());
			logger.debug("candidateResult.getMappings(): " + candidateResult.getMappings());

			// If the candidate result satisfies the expression, or there is no
			// expression, then it can be output.
			if (predicates != null) {
				logger.debug("predicates != null");
				boolean satisfiesPredicate = true;
				logger.debug("satisfiesPredicate: " + satisfiesPredicate);
				for (Predicate predicate : predicates) {
					logger.debug("predicate: " + predicate);
					logger.debug("predicate.getAndOr(): " + predicate.getAndOr());
					if (predicate.getAndOr() != null && predicate.getAndOr().equals("or")) {
						logger.debug("or");
						satisfiesPredicate = satisfiesPredicate || predicate.evaluate(candidateResult);
						logger.debug("satisfiesPredicate: " + satisfiesPredicate);
					} else {
						logger.debug("andOr null");
						satisfiesPredicate = satisfiesPredicate && predicate.evaluate(candidateResult);
						logger.debug("satisfiesPredicate: " + satisfiesPredicate);
					}
				}
				if (satisfiesPredicate) {
					logger.debug("satisfiesPredicate: " + satisfiesPredicate);
					result = candidateResult;
					logger.debug("result: " + result);
				}
			} else
				result = candidateResult;
		}
		logger.debug("result: " + result);
		return result;
	}

	/** closes the open inputs, streams and the current operator
	 * @return true/false
	 */
	public boolean close() {
		logger.debug("Entering JoinOperatorImpl: close: " + this.toString());

		this.getRhsInput().close(); //streamed input
		this.setLhsInput(null); //stored input
		this.setRhsInput(null); //streamed input
		mLhsResultInstanceIterator = null;
		predicates = null;
		tidyUp();

		cleanup();

		logger.debug("Exiting JoinAndEvaluateBaseOperator: close: " + this.toString());
		return true;
	}

	/**
	 * Reads the input resultInstances and adds them to store.
	 * 
	 * @param operator    Operator to read from
	 * 
	 * @throws OperatorException if an error occurs reading from the operator.
	 * @throws InterruptedException if the thread is interrupted.
	 */
	private void readInputResultInstance(EvaluatorOperator operator) { //throws OperatorException, InterruptedException {
		logger.debug("in readInputResultInstance");
		for (ResultInstance resultInstance = operator.next(); !resultInstance.isEof(); resultInstance = operator.next()) {
			logger.debug("resultInstance: " + resultInstance);
			storeResultInstance(resultInstance);
		}
	}

	@Override
	protected void cleanup() {
		logger.debug("in cleanup");
		this.setLhsInput(null);
		this.setRhsInput(null);
		this.setResultType(null);
	}

	/**
	 * Stores the give resultInstance.
	 * 
	 * @param resultInstance resultInstance to store.
	 */
	protected abstract void storeResultInstance(ResultInstance resultInstance);

	/**
	 * Retrieves the candidate matches for a given resultInstance.  These candidate
	 * matches will be joined to the resultInstance and the resulting joined resultInstance
	 * will be evaluated against the expression.  If the result of this
	 * expression evaluation is true the joined resultInstance will be output by the
	 * operator.
	 * 
	 * @param resultInstance right hand side resultInstance to get candidate matches for
	 * 
	 * @return Iterator to the candidate matches.
	 */
	protected abstract Iterator<ResultInstance> getCandidateMatches(ResultInstance resultInstance);

	/**
	 * Tidies up after the operator is complete.
	 */
	protected abstract void tidyUp();

	public void setReconcilingExpression(List<Predicate> predicates) {
		//TODO getting the parentSuperLexical might not work in all cases, e.g., XML - sort this
		logger.debug("in setReconcilingExpression, predicates: " + predicates);
		int i = 0;
		StringBuilder expressionBuilder = new StringBuilder();
		for (Predicate predicate : predicates) {
			if (i > 0)
				expressionBuilder.append(predicate.getAndOr());
			else
				logger.debug("first predicate, andOr: " + predicate.getAndOr());
			if (predicate.getSuperLexical1() != null) {
				expressionBuilder.append(predicate.getSuperLexical1().getParentSuperAbstract().getName());
				expressionBuilder.append(".");
				expressionBuilder.append(predicate.getSuperLexical1().getName());
			} else if (predicate.getLiteral1() != null)
				expressionBuilder.append(predicate.getLiteral1());
			expressionBuilder.append(" ");
			expressionBuilder.append(predicate.getOperator());
			expressionBuilder.append(" ");
			if (predicate.getSuperLexical2() != null) {
				expressionBuilder.append(predicate.getSuperLexical2().getParentSuperAbstract().getName());
				expressionBuilder.append(".");
				expressionBuilder.append(predicate.getSuperLexical2().getName());
			} else if (predicate.getLiteral2() != null)
				expressionBuilder.append(predicate.getLiteral2());
		}
		logger.debug("expression: " + expressionBuilder.toString());
		this.setReconcilingExpression(expressionBuilder.toString());
	}

	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(Set<Predicate> predicates) {
		logger.debug("in setPredicates");
		this.predicates = predicates;
		/*
		if (this.getReconcilingExpression() == null || this.getReconcilingExpression().length() == 0)
			setReconcilingExpression(predicates);
		*/
	}

	/**
	 * @return the predicates
	 */
	public Set<Predicate> getPredicates() {
		return predicates;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JoinOperatorImpl [");
		if (predicates != null)
			builder.append("predicates=").append(predicates).append(", ");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	public void setQueryString(String queryString) {
		// TODO Auto-generated method stub

	}

}
