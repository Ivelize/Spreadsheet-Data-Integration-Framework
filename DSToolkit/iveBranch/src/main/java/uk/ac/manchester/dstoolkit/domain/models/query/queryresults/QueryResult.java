package uk.ac.manchester.dstoolkit.domain.models.query.queryresults;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "QUERY_RESULTS")
public class QueryResult extends ModelManagementConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2935633744393838963L;

	@ManyToOne
	@JoinColumn(name = "QUERY_RESULT_DATASPACE_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_RESULT_DATASPACE_ID")
	private Dataspace dataspace;

	@OneToOne(fetch = FetchType.EAGER)
	//(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "QUERY_RESULT_QUERY_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_RESULT_QUERY_ID")
	private Query query;

	//CascadeType.PERSIST, 
	@OneToOne(fetch = FetchType.EAGER)
	//(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "QUERY_RESULT_RESULT_TYPE_ID")
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_RESULT_RESULT_TYPE_ID")
	private ResultType resultType;

	//, CascadeType.MERGE
	@ManyToMany(fetch = FetchType.EAGER)
	//, mappedBy = "queryResults")
	//@OneToMany(fetch = FetchType.EAGER)
	//(cascade = { CascadeType.PERSIST })
	//@JoinColumn(name = "QUERY_RESULT_ID")
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
	@JoinTable(name = "QUERY_RESULT_RESULT_INSTANCE", joinColumns = { @JoinColumn(name = "QUERY_RESULT_ID") }, inverseJoinColumns = { @JoinColumn(name = "RESULT_INSTANCE_ID") })
	//, uniqueConstraints = @UniqueConstraint(columnNames = {"QUERY_RESULT_ID", "RESULT_INSTANCE_ID" })
	private List<ResultInstance> resultInstances = new ArrayList<ResultInstance>();

	@ManyToMany
	@JoinTable(name = "QUERY_RESULT_SCHEMA_OF_DATA_SOURCE", joinColumns = { @JoinColumn(name = "QUERY_RESULT_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEMA_OF_DATA_SOURCE_ID") })
	private Set<Schema> schemasOfDataSourcesQueried = new LinkedHashSet<Schema>();

	@ManyToMany
	//(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinTable(name = "QUERY_RESULT_MAPPING", joinColumns = { @JoinColumn(name = "QUERY_RESULT_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAPPING_ID") })
	private Set<Mapping> mappings = new LinkedHashSet<Mapping>();

	/**
	 * 
	 */
	public QueryResult() {
		super();
	}

	/**
	 * @param query
	 */
	public QueryResult(Query query) {
		super();
		this.setQuery(query);
	}

	/**
	 * @param query
	 * @param resultType
	 */
	public QueryResult(Query query, ResultType resultType) {
		super();
		this.setQuery(query);
		this.setResultType(resultType);
	}

	//-----------------------dataspace-----------------

	/**
	 * @return the dataspace
	 */
	@Override
	public Dataspace getDataspace() {
		return dataspace;
	}

	/**
	 * @param dataspace the dataspace to set
	 */
	@Override
	public void setDataspace(Dataspace dataspace) {
		this.dataspace = dataspace;
	}

	//-----------------------query-----------------

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

	//-----------------------resultInstances-----------------

	/**
	 * @return the resultInstances
	 */
	public List<ResultInstance> getResultInstances() {
		//return Collections.unmodifiableList(resultInstances);
		return resultInstances;
	}

	/**
	 * @param resultInstances the resultInstances to set
	 */
	public void setResultInstances(List<ResultInstance> resultInstances) {
		this.resultInstances = resultInstances;
		//for (ResultInstance resultInstance : resultInstances) {
		//	resultInstance.internalAddQueryResult(this);
		//}
	}

	public void setResultInstanceAtIndex(int index, ResultInstance resultInstance) {
		this.resultInstances.set(index, resultInstance);
		//resultInstance.internalAddQueryResult(this);
	}

	public void addResultInstance(ResultInstance resultInstance) {
		this.resultInstances.add(resultInstance);
		//resultInstance.internalAddQueryResult(this);
	}

	public void removeResultInstance(ResultInstance resultInstance) {
		this.resultInstances.remove(resultInstance);
		//resultInstance.internalRemoveQueryResult(this);
	}

	/*
	public void internalAddResultInstance(ResultInstance resultInstance) {
		this.resultInstances.add(resultInstance);
	}

	public void internalRemoveResultInstance(ResultInstance resultInstance) {
		this.resultInstances.remove(resultInstance);
	}
	*/

	//-----------------------dataSources-----------------

	/**
	 * @return the schemasOfDataSourcesQueried
	 */
	public Set<Schema> getSchemasOfDataSourcesQueried() {
		return schemasOfDataSourcesQueried;
	}

	/**
	 * @param schemasOfDataSourcesQueried the schemasOfDataSourcesQueried to set
	 */
	public void setSchemasOfDataSourcesQueried(Set<Schema> schemasOfDataSourcesQueried) {
		this.schemasOfDataSourcesQueried = schemasOfDataSourcesQueried;
	}

	public void addSchemaOfDataSourcesQueried(Schema schemaOfDataSourcesQueried) {
		this.schemasOfDataSourcesQueried.add(schemaOfDataSourcesQueried);
	}

	public void removeSchemaOfDataSourcesQueried(Schema schemaOfDataSourcesQueried) {
		this.schemasOfDataSourcesQueried.remove(schemaOfDataSourcesQueried);
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
			mapping.internalAddQueryResult(this);
		}
		*/
	}

	public void addMapping(Mapping mapping) {
		this.mappings.add(mapping);
		//mapping.internalAddQueryResult(this);
	}

	/*
	public void internalAddMapping(Mapping mapping) {
		this.mappings.add(mapping);
	}
	*/

	public void removeMapping(Mapping mapping) {
		this.mappings.remove(mapping);
		//mapping.internalRemoveQueryResult(this);
	}

	/*
	public void internalRemoveMapping(Mapping mapping) {
		this.mappings.remove(mapping);
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QueryResult [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		//if (mappings != null)
		//	builder.append("mappings=").append(mappings).append(", ");
		if (query != null)
			builder.append("query=").append(query).append(", ");
		//if (resultInstances != null)
		//	builder.append("resultInstances=").append(resultInstances).append(", ");
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
		result = prime * result + ((mappings == null) ? 0 : mappings.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
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
		QueryResult other = (QueryResult) obj;
		if (mappings == null) {
			if (other.mappings != null)
				return false;
		} else if (!mappings.equals(other.mappings))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (resultType == null) {
			if (other.resultType != null)
				return false;
		} else if (!resultType.equals(other.resultType))
			return false;
		return true;
	}

}
