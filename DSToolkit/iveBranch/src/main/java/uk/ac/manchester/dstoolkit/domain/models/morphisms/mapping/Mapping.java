package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.Morphism;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "MAPPINGS")
public class Mapping extends Morphism {

	//TODO test

	/**
	 * 
	 */
	private static final long serialVersionUID = -1649221095170783786L;

	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinColumn(name = "MAPPING_QUERY1_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MAPPING_QUERY1_ID")
	private Query query1;

	@OneToOne(fetch = FetchType.EAGER)
	//,cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinColumn(name = "MAPPING_QUERY2_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MAPPING_QUERY2_ID")
	private Query query2;

	@Column(name = "MAPPING_QUERY1_STRING", length = 50000)
	private String query1String;

	@Column(name = "MAPPING_QUERY2_STRING", length = 50000)
	private String query2String;

	/*
	@ManyToMany
	//(fetch = FetchType.EAGER)
	@JoinTable(name = "MAPPING_QUERY1_CONSTRUCTS", joinColumns = { @JoinColumn(name = "MAPPING_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONSTRUCT_ID") })
	private Set<CanonicalModelConstruct> query1Constructs = new LinkedHashSet<CanonicalModelConstruct>();

	@ManyToMany
	//(fetch = FetchType.EAGER)
	@JoinTable(name = "MAPPING_QUERY2_CONSTRUCTS", joinColumns = { @JoinColumn(name = "MAPPING_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONSTRUCT_ID") })
	private Set<CanonicalModelConstruct> query2Constructs = new LinkedHashSet<CanonicalModelConstruct>();
	*/

	//TODO sort out provenance
	//@OneToOne(mappedBy = "mapping")
	//private SchematicCorrespondencesToMappingProvenance derivedFromSchematicCorrespondences;

	//@ManyToMany(mappedBy = "mappings")
	//private Set<QueryResult> queryResults = new LinkedHashSet<QueryResult>();

	//@ManyToMany(mappedBy = "mappings")
	//private Set<ResultInstance> resultInstances = new LinkedHashSet<ResultInstance>();

	/**
	 * 
	 */
	public Mapping() {
		super();
	}

	/**
	 * @param query1String
	 * @param query2String
	 */
	public Mapping(String query1String, String query2String) {
		super();
		this.setQuery1String(query1String);
		this.setQuery2String(query2String);
	}

	/**
	 * @param query1
	 * @param query2
	 */
	public Mapping(Query query1, Query query2) {
		super();
		this.setQuery1(query1);
		assignMappingToMappingOperators(this.query1.getRootOperator());
		this.setQuery2(query2);
		assignMappingToMappingOperators(this.query2.getRootOperator());
	}

	//-----------------------query1-----------------

	/**
	 * @return the query1
	 */
	public Query getQuery1() {
		return query1;
	}

	/**
	 * @param query1 the query1 to set
	 */
	//TODO test this
	public void setQuery1(Query query1) {
		this.query1 = query1;
		assignMappingToMappingOperators(this.query1.getRootOperator());
		if (this.getConstructs1().isEmpty()) {
			Set<SuperAbstract> sas1 = query1.getSuperAbstractsQueried();
			Set<CanonicalModelConstruct> constructs1 = new HashSet<CanonicalModelConstruct>();
			constructs1.addAll(sas1);
			this.setConstructs1(constructs1);
		}
	}

	//-----------------------query1String-----------------

	/**
	 * @return the query1String
	 */
	public String getQuery1String() {
		return query1String;
	}

	/**
	 * @param query1String the query1String to set
	 */
	public void setQuery1String(String query1String) {
		this.query1String = query1String;
	}

	//-----------------------query2-----------------

	/**
	 * @return the query2
	 */
	public Query getQuery2() {
		return query2;
	}

	/**
	 * @param query2 the query2 to set
	 */
	//TODO test this
	public void setQuery2(Query query2) {
		this.query2 = query2;
		assignMappingToMappingOperators(this.query2.getRootOperator());
		if (this.getConstructs2().isEmpty()) {
			Set<SuperAbstract> sas2 = query2.getSuperAbstractsQueried();
			Set<CanonicalModelConstruct> constructs2 = new HashSet<CanonicalModelConstruct>();
			constructs2.addAll(sas2);
			this.setConstructs2(constructs2);
		}
	}

	//-----------------------assignMappingToMappingOperators-----------------

	//TODO write test
	protected void assignMappingToMappingOperators(MappingOperator mappingOperator) {
		mappingOperator.setMapping(this);
		if (mappingOperator.getLhsInput() != null)
			assignMappingToMappingOperators(mappingOperator.getLhsInput());
		if (mappingOperator.getRhsInput() != null)
			assignMappingToMappingOperators(mappingOperator.getRhsInput());
	}

	//-----------------------query2String-----------------

	/**
	 * @return the query2String
	 */
	public String getQuery2String() {
		return query2String;
	}

	/**
	 * @param query2String the query2String to set
	 */
	public void setQuery2String(String query2String) {
		this.query2String = query2String;
	}

	//-----------------------dataspace-----------------

	/**
	 * @return the dataspace
	 */
	/*
	@Override
	public Dataspace getDataspace() {
		return dataspace;
	}
	*/

	/**
	 * @param dataspace the dataspace to set
	 */
	/*
	@Override
	public void setDataspace(Dataspace dataspace) {
		if (this.dataspace != null) {
			this.dataspace.internalRemoveMapping(this);
		}
		this.dataspace = dataspace;
		if (dataspace != null) {
			dataspace.internalAddMapping(this);
		}
	}

	public void internalSetDataspace(Dataspace dataspace) {
		this.dataspace = dataspace;
	}
	*/

	//-----------------------query1Constructs-----------------

	/**
	 * @return the query1Constructs
	 */
	/*
	public Set<CanonicalModelConstruct> getQuery1Constructs() {
		return Collections.unmodifiableSet(query1Constructs);
	}
	*/

	/**
	 * @param query1Constructs the query1Constructs to set
	 */
	/*
	public void setQuery1Constructs(Set<CanonicalModelConstruct> query1Constructs) {
		this.query1Constructs = query1Constructs;
		/*
		for (CanonicalModelConstruct query1Construct : query1Constructs) {
			query1Construct.internalAddPopulatedByMapping(this);
		}

	}

	public void addQuery1Construct(CanonicalModelConstruct query1Construct) {
		this.query1Constructs.add(query1Construct);
		//query1Construct.internalAddPopulatedByMapping(this);
	}
	*/

	/*
	public void internalConstruct1(CanonicalModelConstruct query1Construct) {
		query1Constructs.add(query1Construct);
	}
	*/

	/*
	public void removeQuery1Construct(CanonicalModelConstruct query1Construct) {
		this.query1Constructs.remove(query1Construct);
		//query1Construct.internalRemovePopulatedByMapping(this);
	}
	*/

	/*
	public void internalRemoveQuery1Construct(CanonicalModelConstruct query1Construct) {
		query1Constructs.remove(query1Construct);
	}
	*/

	//-----------------------query2Constructs-----------------

	/**
	 * @return the query2Constructs
	 */
	/*
	public Set<CanonicalModelConstruct> getQuery2Constructs() {
		return Collections.unmodifiableSet(query2Constructs);
	}
	*/

	/**
	 * @param query2Constructs the query2Constructs to set
	 */
	/*
	public void setQuery2Constructs(Set<CanonicalModelConstruct> query2Constructs) {
		this.query2Constructs = query2Constructs;
	}

	public void addQuery2Construct(CanonicalModelConstruct query2Construct) {
		this.query2Constructs.add(query2Construct);
	}

	public void removeQuery2Construct(CanonicalModelConstruct query2Construct) {
		this.query2Constructs.remove(query2Construct);
	}
	*/

	//-----------------------queryResults-----------------

	/**
	 * @return the queryResults
	 */
	/*
	public Set<QueryResult> getQueryResults() {
		return Collections.unmodifiableSet(queryResults);
	}
	*/

	/**
	 * @param queryResults the queryResults to set
	 */
	/*
	public void setQueryResults(Set<QueryResult> queryResults) {
		this.queryResults = queryResults;
		for (QueryResult queryResult : queryResults) {
			queryResult.internalAddMapping(this);
		}
	}

	public void addQueryResult(QueryResult queryResult) {
		this.queryResults.add(queryResult);
		queryResult.internalAddMapping(this);
	}

	public void internalAddQueryResult(QueryResult queryResult) {
		this.queryResults.add(queryResult);
	}

	public void removeQueryResult(QueryResult queryResult) {
		this.queryResults.remove(queryResult);
		queryResult.internalRemoveMapping(this);
	}

	public void internalRemoveQueryResult(QueryResult queryResult) {
		this.queryResults.remove(queryResult);
	}
	*/

	//-----------------------resultInstances-----------------

	/**
	 * @return the resultInstances
	 */
	/*
	public Set<ResultInstance> getResultInstances() {
		return Collections.unmodifiableSet(resultInstances);
	}
	*/

	/**
	 * @param resultInstances the resultInstances to set
	 */
	/*
	public void setResultInstances(Set<ResultInstance> resultInstances) {
		this.resultInstances = resultInstances;
		for (ResultInstance resultInstance : resultInstances) {
			resultInstance.internalAddMapping(this);
		}
	}
	*/

	/*
	public void addResultInstance(ResultInstance resultInstance) {
		this.resultInstances.add(resultInstance);
		resultInstance.internalAddMapping(this);
	}
	*/

	/*
	public void internalAddResultInstance(ResultInstance resultInstance) {
		this.resultInstances.add(resultInstance);
	}
	*/

	/*
	public void removeResultInstance(ResultInstance resultInstance) {
		this.resultInstances.remove(resultInstance);
		resultInstance.internalRemoveMapping(this);
	}
	*/

	/*
	public void internalRemoveResultInstance(ResultInstance resultInstance) {
		this.resultInstances.remove(resultInstance);
	}
	*/

	//-----------------------derivedFromSchematicCorrespondences---provenance-----------------

	/**
	 * @return the derivedFromSchematicCorrespondences
	 */
	/*
	public SchematicCorrespondencesToMappingProvenance getDerivedFromSchematicCorrespondences() {
		return derivedFromSchematicCorrespondences;
	}
	*/

	/**
	 * @param derivedFromSchematicCorrespondences the derivedFromSchematicCorrespondences to set
	 */
	/*
	public void setDerivedFromSchematicCorrespondences(SchematicCorrespondencesToMappingProvenance derivedFromSchematicCorrespondences) {
		this.derivedFromSchematicCorrespondences = derivedFromSchematicCorrespondences;
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((query1String == null) ? 0 : query1String.hashCode());
		result = prime * result + ((query2String == null) ? 0 : query2String.hashCode());
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
		Mapping other = (Mapping) obj;
		if (query1String == null) {
			if (other.query1String != null)
				return false;
		} else if (!query1String.equals(other.query1String))
			return false;
		if (query2String == null) {
			if (other.query2String != null)
				return false;
		} else if (!query2String.equals(other.query2String))
			return false;
		if (!query1.getDataSources().equals(other.getQuery1().getDataSources()))
			return false;
		if (!query2.getDataSources().equals(other.getQuery2().getDataSources()))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Mapping [");
		builder.append("id: ");
		builder.append(id);
		builder.append(",");
		if (query1String != null)
			builder.append("query1String=").append(query1String).append(", ");
		if (query2String != null)
			builder.append("query2String=").append(query2String).append(", ");
		if (query1 != null) {
			if (query1.getDataSources() != null)
				builder.append("query1.dataSources: " + query1.getDataSources()).append(", ");
		}
		if (query2 != null) {
			if (query2.getDataSources() != null)
				builder.append("query2.dataSources: " + query2.getDataSources()).append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

}