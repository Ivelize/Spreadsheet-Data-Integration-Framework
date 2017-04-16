package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Iterator;
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
public class NestedLoopsJoinOperatorImpl extends EvaluatorJoinOperatorImpl {// implements AttributeSensitiveOperator {

	private static Logger logger = Logger.getLogger(NestedLoopsJoinOperatorImpl.class);

	public NestedLoopsJoinOperatorImpl(Set<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	public NestedLoopsJoinOperatorImpl(Set<Predicate> predicates, String reconcilingExpression, EvaluatorOperator lhsInput,
			EvaluatorOperator rhsInput, ResultType resultType, long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, reconcilingExpression, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	/*
	NestedLoopsJoinOperatorImpl inlj = new NestedLoopsJoinOperatorImpl(logicalJoin.getLeftInput(), logicalJoin.getRightInput(), logicalJoin
			.getJoinPredicates(), logicalJoin.getCardinality(), logicalJoin.getJoinPredicatesCarried(), logicalJoin.getSourceId());
	
	NestedLoopsJoinOperatorImpl inlj = NestedLoopsJoinOperatorImpl(List<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, List<Predicate> joinPredicatesCarried, DataSource dataSource) 
	*/

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	@Override
	public List<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	@Override
	public ResultInstance next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NestedLoopsJoinOperatorImpl [");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	@Override
	protected Iterator<ResultInstance> getCandidateMatches(ResultInstance resultInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void storeResultInstance(ResultInstance resultInstance) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void tidyUp() {
		// TODO Auto-generated method stub

	}

}
