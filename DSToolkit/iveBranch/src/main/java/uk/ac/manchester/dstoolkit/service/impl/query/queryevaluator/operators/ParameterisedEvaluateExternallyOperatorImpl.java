package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

@Scope("prototype")
@Service
public class ParameterisedEvaluateExternallyOperatorImpl extends EvaluateExternallyOperatorImpl {

	private static Logger logger = Logger.getLogger(ParameterisedEvaluateExternallyOperatorImpl.class);

	//TODO might need to include parameters here if known, should be available from joinpredicates

	public ParameterisedEvaluateExternallyOperatorImpl(EvaluatorOperator planRootEvaluatorOperator, ResultType resultType, long cardinality,
			Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(planRootEvaluatorOperator, resultType, cardinality, joinPredicatesCarried, dataSource);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterisedEvaluateExternallyOperatorImpl [");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	/*
	public ParameterisedExternalCallOperator(Operator inputPlan, long cardinality, Collection<Predicate> joinPredicatesCarried, Long sourceId) {
		super(inputPlan, cardinality, joinPredicatesCarried, sourceId);
	}

	public Long getSourceIdOfInput() { return sourceIds[0]; }
	private void setSourceIdOfInput(Long sourceId) { sourceIds[0] = sourceId;}
	     
	public Operator getInputOperator() {
	    return inputs[0];
	}
	
	public String toString() {
	    StringBuffer b = new StringBuffer();
		b.append("PARAMETERISED EXTERNAL CALL OPERATOR {");
	    b.append( inputs[0].toString() );
	    b.append("}");
	    return b.toString();
	}
	*/

}
