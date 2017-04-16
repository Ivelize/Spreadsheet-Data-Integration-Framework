package uk.ac.manchester.dstoolkit.service.query.queryevaluator;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

public interface QueryEvaluationEngineService {

	public abstract List<ResultInstance> evaluateQuery(EvaluatorOperator rootOperator);

}