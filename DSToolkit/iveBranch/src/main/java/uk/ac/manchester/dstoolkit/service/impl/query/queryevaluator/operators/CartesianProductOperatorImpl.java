package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Collection;
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
public class CartesianProductOperatorImpl extends JoinOperatorImpl {

	public CartesianProductOperatorImpl(Set<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	private static final Logger logger = Logger.getLogger(CartesianProductOperatorImpl.class);

	/** Collection of resultInstances in the stored/lhs input */
	private Collection<ResultInstance> mLhsResultInstances;

	/**
	
	
	public CartesianProductOperatorImpl(List<Predicate> predicates, EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType,
			long cardinality, List<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	public CartesianProductOperatorImpl(List<Predicate> predicates, String reconcilingExpression, EvaluatorOperator lhsInput,
			EvaluatorOperator rhsInput, ResultType resultType, long cardinality, List<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(predicates, reconcilingExpression, lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}

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
		builder.append("CartesianProductOperatorImpl [");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

}
