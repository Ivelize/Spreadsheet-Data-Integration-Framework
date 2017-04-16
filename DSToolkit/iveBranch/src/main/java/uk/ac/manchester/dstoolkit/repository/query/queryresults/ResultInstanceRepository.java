package uk.ac.manchester.dstoolkit.repository.query.queryresults;

import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface ResultInstanceRepository extends GenericRepository<ResultInstance, Long> {

	public ResultInstance getResultInstanceForQueryWithSameResultValuesAsGivenResultInstance(Query query, ResultInstance resultInstance,
			boolean getUserSpecifiedInstances);

}
