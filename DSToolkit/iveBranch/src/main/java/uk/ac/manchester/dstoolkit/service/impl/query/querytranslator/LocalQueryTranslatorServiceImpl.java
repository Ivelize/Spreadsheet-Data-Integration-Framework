package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SPARQLService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SQLService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2XQueryService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslatorService;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add localQueryTranslator2SPARQL to translate()
 */

//@Transactional(readOnly = true)
@Service(value = "localQueryTranslatorService")
public class LocalQueryTranslatorServiceImpl implements LocalQueryTranslatorService {

	private static Logger logger = Logger.getLogger(LocalQueryTranslatorServiceImpl.class);

	@Autowired
	@Qualifier("localQueryTranslator2SQLService")
	private LocalQueryTranslator2SQLService localQueryTranslator2SQL;

	@Autowired
	@Qualifier("localQueryTranslator2XQueryService")
	private LocalQueryTranslator2XQueryService localQueryTranslator2XQuery;

	/*SPARQL*/
	@Autowired
	@Qualifier("localQueryTranslator2SPARQLService")
	private LocalQueryTranslator2SPARQLService localQueryTranslator2SPARQL;
	
	/**
	 * Query translator starts from here 
	 */
	public String translate(EvaluateExternallyOperatorImpl plan, Map<String, ControlParameter> controlParameters) {
		if (plan.getModelType().equals(ModelType.RELATIONAL))
			return localQueryTranslator2SQL.translate2SQL(plan, controlParameters);
		else if (plan.getModelType().equals(ModelType.XSD))
			return localQueryTranslator2XQuery.translate2XQuery(plan, controlParameters);
		else if (plan.getModelType().equals(ModelType.RDF))
			return localQueryTranslator2SPARQL.translate2SPARQL(plan, controlParameters);
		return null;
	}

	protected String removeUnionFromString(String stringWithUnion) {
		logger.debug("in removeUnionFromString");
		logger.debug("stringWithUnion: " + stringWithUnion);
		StringBuffer stringWithoutUnion = new StringBuffer();
		Pattern p = Pattern.compile("union\\d+\\.");
		Matcher m = p.matcher(stringWithUnion);
		boolean result = m.find();
		while (result) {
			m.appendReplacement(stringWithoutUnion, "");
			result = m.find();
		}
		// Add the last segment of input to 
		// the new String
		m.appendTail(stringWithoutUnion);
		logger.debug("stringWithoutUnion: " + stringWithoutUnion);
		return stringWithoutUnion.toString();
	}
}
