package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;

@Scope("prototype")
@Service
public class ScanOperatorImpl extends EvaluatorOperatorImpl {

	private static Logger logger = Logger.getLogger(ScanOperatorImpl.class);

	private SuperAbstract superAbstract;

	private Set<Predicate> predicates;

	public ScanOperatorImpl(SuperAbstract superAbstract, String reconcilingExpression, Set<Predicate> predicates, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(resultType, cardinality, joinPredicatesCarried, dataSource);
		this.setSuperAbstract(superAbstract);
		this.setReconcilingExpression(reconcilingExpression);
		this.setPredicates(predicates);
	}

	public ScanOperatorImpl(SuperAbstract superAbstract, Set<Predicate> predicates, ResultType resultType, long cardinality,
			Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(resultType, cardinality, joinPredicatesCarried, dataSource);
		this.setSuperAbstract(superAbstract);
		this.setPredicates(predicates);
	}

	/*
	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return false;
	}
	*/

	/*
	public Set<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	/*
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
	 * @param superAbstract the superAbstract to set
	 */
	public void setSuperAbstract(SuperAbstract superAbstract) {
		this.superAbstract = superAbstract;
	}

	/**
	 * @return the superAbstract
	 */
	public SuperAbstract getSuperAbstract() {
		return superAbstract;
	}

	/**
	 * @return the predicates
	 */
	public Set<Predicate> getPredicates() {
		return predicates;
	}

	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(Set<Predicate> predicates) {
		logger.debug("in setPredicates: " + predicates);
		this.predicates = predicates;
		/*
		if (getReconcilingExpression() == null || getReconcilingExpression().length() == 0)
			setReconcilingExpression(predicates);
		*/
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScanOperatorImpl [");
		if (superAbstract != null)
			builder.append("superAbstract=").append(superAbstract.getName()).append(", ");
		if (this.getPredicates() != null)
			builder.append("predicates=").append(this.getPredicates()).append(", ");
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
