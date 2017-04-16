package uk.ac.manchester.dstoolkit.service.query.queryoptimisier;

import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

public interface LogicalQueryOptimiserService extends QueryOptimiserService {

	/**
	 * @param rootOperator
	 * @param controlParameters TODO
	 * @return the root evaluatorOperator of the optimised query plan
	 */
	public abstract EvaluatorOperator optimise(MappingOperator rootOperator, Map<ControlParameterType, ControlParameter> controlParameters);

	/**
	 * @param query
	 * @param controlParameters TODO
	 * @return the root evaluatorOperator of the optimised query plan
	 */
	public abstract EvaluatorOperator optimise(Query query, Map<ControlParameterType, ControlParameter> controlParameters);

	/**
	 * @return the plan
	 */
	public abstract EvaluatorOperator getPlan();

}