package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

@Scope("prototype")
@Service
public class JoinOperatorImpl extends EvaluatorOperatorImpl {

	private static Logger logger = Logger.getLogger(JoinOperatorImpl.class);

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

	public JoinOperatorImpl(Set<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
		setPredicates(predicates);
	}

	public JoinOperatorImpl(Set<Predicate> predicates, String reconcilingExpression, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput,
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

	public void setReconcilingExpression(List<Predicate> predicates) {
		logger.debug("in setReconcilingExpression, predicates: " + predicates);
		int i = 0;
		StringBuilder expressionBuilder = new StringBuilder();
		//TODO getting parentSuperAbstract might not work in all cases, e.g., XML - sort this
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

	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	public ResultInstance next() { //throws InterruptedException, OperatorException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean open() { //throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
