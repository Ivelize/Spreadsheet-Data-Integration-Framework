package uk.ac.manchester.dstoolkit.service.query.queryresults;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface ResultTypeService extends GenericEntityService<ResultType, Long> {

	/**
	 * @param resultTypeId
	 * @return resultType
	 */
	public ResultType findResultType(Long resultTypeId);

	/**
	 * @param resultType
	 */
	public void addResultType(ResultType resultType);

	/**
	 * @param resultTypeId
	 */
	public void deleteResultType(Long resultTypeId);

	/**
	 * @param resultType
	 * @param resultField
	 */
	public void addResultFieldToResultType(ResultType resultType, ResultField resultField);

	/**
	 * @param resultType
	 * @param resultField
	 */
	public void removeResultFieldFromResultType(ResultType resultType, ResultField resultField);
}
