package uk.ac.manchester.dstoolkit.dto.models.query.queryResults;

import java.util.HashMap;
import java.util.Map;

public class ResultTupleDTO {

	private Long id;
	private Long queryResultId;
	private Map<Long, ResultValueDTO> resultValues = new HashMap<Long, ResultValueDTO>();

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the queryResultId
	 */
	public Long getQueryResultId() {
		return queryResultId;
	}

	/**
	 * @param queryResultId the queryResultId to set
	 */
	public void setQueryResultId(Long queryResultId) {
		this.queryResultId = queryResultId;
	}

	/**
	 * @return the resultValues
	 */
	public Map<Long, ResultValueDTO> getResultValues() {
		return resultValues;
	}

	public void addResultValue(ResultValueDTO resultValue) {
		this.resultValues.put(resultValue.getId(), resultValue);
	}

	/**
	 * @param resultValues the resultValues to set
	 */
	public void setResultValues(Map<Long, ResultValueDTO> resultValues) {
		this.resultValues = resultValues;
	}

}
