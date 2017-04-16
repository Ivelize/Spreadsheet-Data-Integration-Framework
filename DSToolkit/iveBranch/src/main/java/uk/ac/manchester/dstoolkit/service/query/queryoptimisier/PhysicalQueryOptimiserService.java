package uk.ac.manchester.dstoolkit.service.query.queryoptimisier;

import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

public interface PhysicalQueryOptimiserService extends QueryOptimiserService {

	public EvaluatorOperator chooseJoinOperators(EvaluatorOperator rootOperator, Map<String, ControlParameter> controlParameters);

}
