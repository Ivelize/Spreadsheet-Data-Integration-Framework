/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.query.queryresults;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface ResultInstanceService extends GenericEntityService<ResultInstance, Long> {

	/**
	 * @param resultInstanceId
	 * @return resultInstance
	 */
	public ResultInstance findResultInstance(Long resultInstanceId);

	/**
	 * @param resultInstance
	 */
	public void addResultInstance(ResultInstance resultInstance);

	/**
	 * @param resultInstanceId
	 */
	public void deleteResultInstance(Long resultInstanceId);
}
