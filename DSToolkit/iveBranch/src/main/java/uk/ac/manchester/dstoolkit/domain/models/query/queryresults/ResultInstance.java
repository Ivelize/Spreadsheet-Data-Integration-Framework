package uk.ac.manchester.dstoolkit.domain.models.query.queryresults;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "RESULT_INSTANCES")
public class ResultInstance extends ModelManagementConstruct { //implements Result {

	private static Logger logger = Logger.getLogger(ResultInstance.class);

	//TODO might have to change this into a modelManagementConstruct for the feedback
	/**
	 * 
	 */
	private static final long serialVersionUID = -7351055078506132071L;

	//, CascadeType.MERGE
	@OneToOne
	//(cascade = { CascadeType.PERSIST })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "RESULT_TYPE_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_RESULT_INSTANCE_RESULT_TYPE_ID")
	private ResultType resultType;

	//@ManyToMany
	//@ManyToOne
	//@JoinColumn(name = "RESULT_INSTANCE_QUERY_RESULT_ID")
	//@org.hibernate.annotations.ForeignKey(name = "FK_RESULT_INSTANCE_QUERY_RESULT_ID")
	//@JoinTable(name = "RESULT_INSTANCE_QUERY_RESULT", joinColumns = { @JoinColumn(name = "RESULT_INSTANCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "QUERY_RESULT_ID") })
	//private Set<QueryResult> queryResults = new LinkedHashSet<QueryResult>();

	@ManyToOne
	//(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "RESULT_INSTANCE_QUERY_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_RESULT_INSTANCE_QUERY_ID")
	private Query query;

	@ManyToMany
	//(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinTable(name = "RESULT_INSTANCE_MAPPING", joinColumns = { @JoinColumn(name = "RESULT_INSTANCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAPPING_ID") })
	private Set<Mapping> mappings = new LinkedHashSet<Mapping>();

	//TODO might need this, check this
	@Transient
	private boolean isEof;

	/*
	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = "RESULT_INSTANCE_RESULT_VALUE", joinColumns = @JoinColumn(name = "RESULT_INSTANCE_ID"))
	@org.hibernate.annotations.IndexColumn(name = "RESULT_VALUE_POSITION")
	@Column(name = "RESULT_VALUE")
	private List<String> resultValues = new ArrayList<String>();
	*/

	//TODO change this map to a collection of objects of class resultFieldNameValuePairs, so that the pairs can be annotated too

	//@org.hibernate.annotations.CollectionOfElements
	//@JoinTable(name = "RESULT_FIELD_NAME_RESULT_VALUE", joinColumns = @JoinColumn(name = "RESULT_INSTANCE_ID"))
	//@org.hibernate.annotations.MapKey(columns = @Column(name = "RESULT_FIELD_NAME"))
	//@Column(name = "RESULT_VALUE")
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//private Map<String, String> resultFieldNameResultValueMap = new HashMap<String, String>();

	//, CascadeType.MERGE
	@MapKey(name = "resultFieldName")
	@ManyToMany(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST })
	@JoinTable(name = "RESULT_INSTANCE_RESULT_VALUES", joinColumns = @JoinColumn(name = "RESULT_INSTANCE_ID"), inverseJoinColumns = @JoinColumn(name = "RESULT_FIELD_RESULT_VALUES_ID"))
	//@MapKeyColumn(name"orders_number")
	//@JoinTable(name = "user_property", joinColumns = @JoinColumn(name = "user_id"))
	//@org.hibernate.annotations.MapKey(columns = {@Column(name = "key")})
	//@Column(name = "value")
	//@JoinTable(name = "result_instance_result_values", joinColumns = @JoinColumn(name = "user_id"))
	//@OneToMany(fetch = FetchType.EAGER, mappedBy = "resultInstance", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	private Map<String, ResultValue> resultFieldNameResultValueMap = new LinkedHashMap<String, ResultValue>();

	/**
	 * 
	 */
	public ResultInstance() {
		super();
	}

	/**
	 * @param resultFieldNameResultValueMap
	 */
	public ResultInstance(Map<String, ResultValue> resultFieldNameResultValueMap) {
		this.setResultFieldNameResultValueMap(resultFieldNameResultValueMap);
	}

	/**
	 * @param lhsResultInstances
	 * @param rhsResultInstances
	 */
	public ResultInstance(ResultInstance lhs, ResultInstance rhs) {
		logger.debug("in ResultInstance(ResultInstance lhs, ResultInstance rhs)");
		resultFieldNameResultValueMap = new HashMap<String, ResultValue>(lhs.getResultFieldNameResultValueMap().size()
				+ rhs.getResultFieldNameResultValueMap().size());
		resultFieldNameResultValueMap.putAll(lhs.getResultFieldNameResultValueMap());
		resultFieldNameResultValueMap.putAll(rhs.getResultFieldNameResultValueMap());
	}

	//-----------------------isData-----------------

	/**
	 * @return isData true
	 */
	/*
	public boolean isData() {
		if (!isEof)
			return true;
		return false;
	}
	*/

	//-----------------------copy-----------------

	/**
	 * @return ResultInstance copy of resultInstance
	 */
	public ResultInstance copy() {
		ResultInstance newResultInstance = new ResultInstance();
		Set<String> resultFieldNames = resultFieldNameResultValueMap.keySet();
		for (String resultFieldName : resultFieldNames) {
			newResultInstance.getResultFieldNameResultValueMap().put(resultFieldName, resultFieldNameResultValueMap.get(resultFieldName));
		}
		return newResultInstance;
	}

	//-----------------------resultType-----------------

	/**
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}

	/**
	 * @param resultType the resultType to set
	 */
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	//-----------------------isEof-----------------

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the isEof
	 */
	public boolean isEof() {
		return isEof;
	}

	/**
	 * @param isEof the isEof to set
	 */
	public void setEof(boolean isEof) {
		this.isEof = isEof;
	}

	//-----------------------resultFieldNameResultValueMap-----------------

	/**
	 * @return the resultFieldNameResultValueMap
	 */
	public Map<String, ResultValue> getResultFieldNameResultValueMap() {
		//return Collections.unmodifiableMap(resultFieldNameResultValueMap);
		return resultFieldNameResultValueMap;
	}

	/**
	 * @param resultFieldNameResultValueMap the resultFieldNameResultValueMap to set
	 */
	public void setResultFieldNameResultValueMap(Map<String, ResultValue> resultFieldNameResultValueMap) {
		this.resultFieldNameResultValueMap = resultFieldNameResultValueMap;
	}

	/**
	 * @param resultFieldName
	 * @return
	 */
	public ResultValue getResultValue(String resultFieldName) {
		return this.resultFieldNameResultValueMap.get(resultFieldName);
	}

	public void addResultValue(String resultFieldName, ResultValue resultValue) {
		this.resultFieldNameResultValueMap.put(resultFieldName, resultValue);
	}

	public void removeResultValue(String resultFieldName) {
		this.resultFieldNameResultValueMap.remove(resultFieldName);
	}

	//-----------------------mappings-----------------

	/**
	 * @return the mappings
	 */
	public Set<Mapping> getMappings() {
		//return Collections.unmodifiableSet(mappings);
		return mappings;
	}

	/**
	 * @param mappings the mappings to set
	 */
	public void setMappings(Set<Mapping> mappings) {
		this.mappings = mappings;
		/*
		for (Mapping mapping : mappings) {
			mapping.internalAddResultInstance(this);
		}
		*/
	}

	public void addMapping(Mapping mapping) {
		mappings.add(mapping);
		//mapping.internalAddResultInstance(this);
	}

	/*
	public void internalAddMapping(Mapping mapping) {
		mappings.add(mapping);
	}
	*/

	public void addAllMappings(Set<Mapping> mappings) {
		this.mappings.addAll(mappings);
		/*
		for (Mapping mapping : mappings) {
			mapping.internalAddResultInstance(this);
		}
		*/
	}

	public void removeMapping(Mapping mapping) {
		mappings.remove(mapping);
		//mapping.internalRemoveResultInstance(this);
	}

	/*
	public void internalRemoveMapping(Mapping mapping) {
		mappings.remove(mapping);
		mapping.internalRemoveResultInstance(this);
	}
	*/

	public void removeAllMappings(Set<Mapping> mappings) {
		this.mappings.removeAll(mappings);
		/*
		for (Mapping mapping : mappings) {
			mapping.internalRemoveResultInstance(this);
		}
		*/
	}

	//-----------------------queryResults-----------------

	/**
	 * @return the queryResults
	 */
	/*
	public Set<QueryResult> getQueryResults() {
		return queryResults;
	}
	*/

	/**
	 * @param queryResults the queryResults to set
	 */
	/*
	public void setQueryResults(Set<QueryResult> queryResults) {
		this.queryResults = queryResults;
		for (QueryResult queryResult : this.queryResults) {
			queryResult.internalAddResultInstance(this);
		}
	}
	
	public void addQueryResult(QueryResult queryResult) {
		queryResults.add(queryResult);
		queryResult.internalAddResultInstance(this);
	}

	public void internalAddQueryResult(QueryResult queryResult) {
		queryResults.add(queryResult);
	}

	public void addAllQueryResult(Set<QueryResult> queryResults) {
		this.queryResults.addAll(queryResults);
		for (QueryResult queryResult : queryResults) {
			queryResult.internalAddResultInstance(this);
		}
	}

	public void removeQueryResult(QueryResult queryResult) {
		queryResults.remove(queryResult);
		queryResult.internalRemoveResultInstance(this);
	}

	
	public void internalRemoveQueryResult(QueryResult queryResult) {
		queryResults.remove(queryResult);
	}
	

	public void removeAllQueryResults(Set<QueryResult> queryResults) {
		this.queryResults.removeAll(mappings);
		for (QueryResult queryResult : queryResults) {
			queryResult.internalRemoveResultInstance(this);
		}
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResultInstance [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		builder.append("isEof=").append(isEof).append(", ");
		if (resultFieldNameResultValueMap != null)
			builder.append("resultFieldNameResultValueMap=").append(resultFieldNameResultValueMap).append(", ");
		if (resultType != null)
			builder.append("resultType=").append(resultType).append(", ");
		builder.append("version=").append(version).append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resultFieldNameResultValueMap == null) ? 0 : resultFieldNameResultValueMap.hashCode());
		result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultInstance other = (ResultInstance) obj;
		if (resultFieldNameResultValueMap == null) {
			if (other.resultFieldNameResultValueMap != null)
				return false;
		} else if (!resultFieldNameResultValueMap.equals(other.resultFieldNameResultValueMap))
			return false;
		if (resultType == null) {
			if (other.resultType != null)
				return false;
		} else if (!resultType.equals(other.resultType))
			return false;
		return true;
	}

	/*
	public boolean isEOF() {
		return isEof;
	}
	*/

}
