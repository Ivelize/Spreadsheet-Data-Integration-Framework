package uk.ac.manchester.dstoolkit.repository.query.queryresults;

import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface QueryResultRepository extends GenericRepository<QueryResult, Long> {

	public Set<QueryResult> getQueryResultsThatResultInstancesBelongTo(Set<ResultInstance> resultInstances);

	public QueryResult getQueryResultForQuery(Query query);
}
