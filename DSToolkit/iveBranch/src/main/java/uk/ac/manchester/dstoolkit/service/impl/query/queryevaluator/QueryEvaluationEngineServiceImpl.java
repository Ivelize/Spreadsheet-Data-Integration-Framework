package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.QueryEvaluationEngineService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

//@Transactional(readOnly = true)
@Service(value = "queryEvaluationEngineService")
public class QueryEvaluationEngineServiceImpl implements QueryEvaluationEngineService {

	private static Logger logger = Logger.getLogger(QueryEvaluationEngineServiceImpl.class);

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.query.queryevaluator.QueryEvaluationEngineService#evaluateQuery(uk.ac.manchester.dataspaces.service.query.queryevaluator.operators.EvaluatorOperator)
	 */
	public List<ResultInstance> evaluateQuery(EvaluatorOperator rootOperator) {
		logger.debug("in evaluateQuery, rootOperator: " + rootOperator);
		List<ResultInstance> resultInstances = new ArrayList<ResultInstance>();
		if (rootOperator != null) {
			boolean retVal = false;
			//try {
			retVal = rootOperator.open();
			//} catch (Exception e) {
			//	logger.error("error occurred while opening rootOperator, ex: " + e);
			//	e.printStackTrace();
			//}
			logger.debug("opened rootOperator, retVal: " + retVal);
			ResultInstance outResultInstance = null;
			if (retVal) {
				do {
					//try {
					outResultInstance = rootOperator.next();
					logger.debug("outResultInstance: " + outResultInstance);
					if (outResultInstance != null) {
						logger.debug("outResultInstance.getMappings(): " + outResultInstance.getMappings());
						logger.debug("outResultInstance.getResultType(): " + outResultInstance.getResultType());
					}
					//} catch (OperatorException e) {
					//	logger.error("OperatorException occurred while calling next of rootOperator, e: " + e);
					//	e.printStackTrace();
					//} catch (InterruptedException e) {
					//	logger.error("InterruptedException occurred while calling next of rootOperator, e: " + e);
					//	e.printStackTrace();
					//}
					logger.debug("outResultInstance: " + outResultInstance);
					//if (outResultInstance != null)
					resultInstances.add(outResultInstance);
				} while (!outResultInstance.isEof());
				if (outResultInstance.isEof())
					resultInstances.remove(outResultInstance);
				logger.debug("got EOF");
				rootOperator.close();
				logger.debug("closed rootOperator");
			}
		}

		return resultInstances;
	}
}
