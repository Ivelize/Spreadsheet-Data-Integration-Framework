package uk.ac.manchester.dstoolkit.service.query.queryevaluator;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;

public interface KeywordSearchEvaluationEngineService {

	public abstract List<ResultInstance> evaluateKeywordQuery(String keyword);

}