package uk.ac.manchester.dstoolkit.dto.models.query.queryResults;

import java.util.ArrayList;
import java.util.List;

import uk.ac.manchester.dstoolkit.dto.models.morphisms.MorphismSetDTO;

public class QueryResultDTO {

	private Long id;
	private String name;
	private Long queryId;
	private String queryName;
	private String queryString;
	private Long schemaId;
	private String schemaName;
	private String selectedPrecisionOrRecall;
	private double selectedPrecisionOrRecallValue;
	private List<String> dataSources = new ArrayList<String>();
	private List<String> resultSetColumnNames = new ArrayList<String>();
	private String[][] resultSet;
	private MorphismSetDTO morphismSet;
	private double precision = -1;
	private double recall = -1;
	private long numberOfResultTuples = -1;

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
	public Long getQueryId() {
		return queryId;
	}

	/**
	 * @param queryId the queryId to set
	 */
	public void setQueryId(Long queryId) {
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
	public Long getSchemaId() {
		return schemaId;
	}

	/**
	 * @param schemaId the schemaId to set
	 */
	public void setSchemaId(Long schemaId) {
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
	public double getSelectedPrecisionOrRecallValue() {
		return selectedPrecisionOrRecallValue;
	}

	/**
	 * @param selectedPrecisionOrRecallValue the selectedPrecisionOrRecallValue to set
	 */
	public void setSelectedPrecisionOrRecallValue(double selectedPrecisionOrRecallValue) {
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

	/**
	 * @return the resultSetColumnNames
	 */
	public List<String> getResultSetColumnNames() {
		return resultSetColumnNames;
	}

	public void addResultColumnName(String columnName) {
		this.resultSetColumnNames.add(columnName);
	}

	/**
	 * @param resultSetColumnNames the resultSetColumnNames to set
	 */
	public void setResultSetColumnNames(List<String> resultSetColumnNames) {
		this.resultSetColumnNames = resultSetColumnNames;
	}

	/**
	 * @return the resultSet
	 */
	public String[][] getResultSet() {
		return resultSet;
	}

	/**
	 * @param resultSet the resultSet to set
	 */
	public void setResultSet(String[][] resultSet) {
		this.resultSet = resultSet;
	}

	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}

	/**
	 * @param recall the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}

	/**
	 * @param numberOfResultTuples the numberOfResultTuples to set
	 */
	public void setNumberOfResultTuples(long numberOfResultTuples) {
		this.numberOfResultTuples = numberOfResultTuples;
	}

	/**
	 * @return the numberOfResultTuples
	 */
	public long getNumberOfResultTuples() {
		return numberOfResultTuples;
	}

	/**
	 * @param morphismSet the morphismSet to set
	 */
	public void setMorphismSet(MorphismSetDTO morphismSet) {
		this.morphismSet = morphismSet;
	}

	/**
	 * @return the morphismSet
	 */
	public MorphismSetDTO getMorphismSet() {
		return morphismSet;
	}

}
