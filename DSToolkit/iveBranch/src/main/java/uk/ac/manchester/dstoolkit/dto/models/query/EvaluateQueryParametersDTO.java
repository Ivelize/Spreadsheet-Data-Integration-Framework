package uk.ac.manchester.dstoolkit.dto.models.query;

import java.util.ArrayList;
import java.util.List;

public class EvaluateQueryParametersDTO {

	private String name;
	private String queryId;
	private String queryName;
	private String queryString;
	private String schemaId;
	private String schemaName;
	private String selectedPrecisionOrRecall;
	private String selectedPrecisionOrRecallValue;
	private List<String> dataSources = new ArrayList<String>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the queryId
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	/**
	 * @return the queryName
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * @param queryName the queryName to set
	 */
	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @return the schemaId
	 */
	public String getSchemaId() {
		return schemaId;
	}

	/**
	 * @param schemaId the schemaId to set
	 */
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}

	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the selectedPrecisionOrRecall
	 */
	public String getSelectedPrecisionOrRecall() {
		return selectedPrecisionOrRecall;
	}

	/**
	 * @param selectedPrecisionOrRecall the selectedPrecisionOrRecall to set
	 */
	public void setSelectedPrecisionOrRecall(String selectedPrecisionOrRecall) {
		this.selectedPrecisionOrRecall = selectedPrecisionOrRecall;
	}

	/**
	 * @return the selectedPrecisionOrRecallValue
	 */
	public String getSelectedPrecisionOrRecallValue() {
		return selectedPrecisionOrRecallValue;
	}

	/**
	 * @param selectedPrecisionOrRecallValue the selectedPrecisionOrRecallValue to set
	 */
	public void setSelectedPrecisionOrRecallValue(String selectedPrecisionOrRecallValue) {
		this.selectedPrecisionOrRecallValue = selectedPrecisionOrRecallValue;
	}

	/**
	 * @return the dataSources
	 */
	public List<String> getDataSources() {
		return dataSources;
	}

	public void addDataSource(String id) {
		this.dataSources.add(id);
	}

	/**
	 * @param dataSources the dataSources to set
	 */
	public void setDataSources(List<String> dataSources) {
		this.dataSources = dataSources;
	}
}
