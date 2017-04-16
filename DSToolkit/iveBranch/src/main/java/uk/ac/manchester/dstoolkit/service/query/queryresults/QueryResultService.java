/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.query.queryresults;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface QueryResultService extends GenericEntityService<QueryResult, Long> {

	//public void addExpectancyAnnotationToQueryResultInstance(Long queryResultInstanceId, Boolean expected, Boolean notExpected, User user);

	/**
	 * @param queryResultId
	 * @return queryResult
	 */
	public QueryResult findQueryResult(Long queryResultId);

	/**
	 * @param queryResult
	 */
	public void addQueryResult(QueryResult queryResult);

	/**
	 * @param queryResultId
	 */
	public void deleteQueryResult(Long queryResultId);
}
