package uk.ac.manchester.dstoolkit.service.query.querytranslator;

import java.util.Map;

import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;

public interface LocalQueryTranslatorService {

	public String translate(EvaluateExternallyOperatorImpl plan, Map<String, ControlParameter> controlParameters);
}
