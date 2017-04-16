package uk.ac.manchester.dstoolkit.service.query.querytranslator;

import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;

public interface LocalQueryTranslator2SQLService {

	public String translate2SQL(EvaluateExternallyOperatorImpl evaluateExternallyOperator, Map<String, ControlParameter> controlParameters);
}
