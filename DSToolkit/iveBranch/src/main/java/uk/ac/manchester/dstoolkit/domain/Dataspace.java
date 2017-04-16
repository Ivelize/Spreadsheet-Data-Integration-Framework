package uk.ac.manchester.dstoolkit.domain;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.user.User;

/**
 * @author chedeler
 *
 */
@Entity
@Table(name = "DATASPACES")
public class Dataspace extends DomainEntity implements Serializable {

	//TODO check whether all lists and sets should actually be linkedHashSets
	//TODO sort out and test equals and hash for all domain classes

	/**
	 * Because the class implements Serializable it needs this ID 
	 */
	private static final long serialVersionUID = 969231398816950438L;

	@Column(name = "DATASPACE_NAME")
	private String dataspaceName;
	
	/**
	 * This annotation says to create a N:M relationship via a new table that will hold two foreign keys.
	 */
	@ManyToMany
	@JoinTable(name = "DATASPACE_DATASOURCES", joinColumns = { @JoinColumn(name = "DATASPACE_ID") }, inverseJoinColumns = { @JoinColumn(name = "DATASOURCE_ID") })
	private Set<DataSource> dataSources = new LinkedHashSet<DataSource>();

	@ManyToMany
	@JoinTable(name = "DATASPACE_SCHEMAS", joinColumns = { @JoinColumn(name = "DATASPACE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEMA_ID") })
	private Set<Schema> schemas = new LinkedHashSet<Schema>();

	@ManyToMany
	@JoinTable(name = "DATASPACE_USERS", joinColumns = { @JoinColumn(name = "DATASPACE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
	private Set<User> users = new LinkedHashSet<User>();

	@OneToMany(mappedBy = "dataspace")
	private Set<Query> queries = new LinkedHashSet<Query>();

	//@OneToMany(mappedBy = "dataspace")
	//private Set<QueryResult> queryResults = new LinkedHashSet<QueryResult>();

	//@OneToMany(mappedBy = "dataspace")
	//private Set<ModelManagementConstructs> mappings = new LinkedHashSet<ModelManagementConstructs>();

	//@OneToMany(mappedBy = "dataspace")
	//private Set<SchematicCorrespondence> correspondences = new LinkedHashSet<SchematicCorrespondence>();

	//@OneToMany(mappedBy = "dataspace")
	//private Set<Matching> matchings = new LinkedHashSet<Matching>();

	/**
	 * 
	 */
	public Dataspace() {
		super();
	}

	/**
	 * @param dataspaceName
	 */
	public Dataspace(String dataspaceName) {
		super();
		this.setDataspaceName(dataspaceName);
	}

	//-----------------------dataspaceName-----------------

	/**
	 * @return the dataspaceName
	 */
	public String getDataspaceName() {
		return dataspaceName;
	}

	/**
	 * @param dataspaceName the dataspaceName to set
	 */
	public void setDataspaceName(String dataspaceName) {
		this.dataspaceName = dataspaceName;
	}

	//-----------------------dataSources-----------------

	/**
	 * @return the dataSources
	 */
	public Set<DataSource> getDataSources() {
		return dataSources;
		//return Collections.unmodifiableSet(dataSources);
	}

	/**
	 * @param dataSources the dataSources to set
	 */
	public void setDataSources(Set<DataSource> dataSources) {
		this.dataSources = dataSources;
		for (DataSource dataSource : dataSources) {
			dataSource.internalAddDataspace(this);
		}
	}

	public void addDataSource(DataSource dataSource) {
		this.dataSources.add(dataSource);
		dataSource.internalAddDataspace(this);
	}

	public void internalAddDataSource(DataSource dataSource) {
		this.dataSources.add(dataSource);
	}

	public void removeDataSource(DataSource dataSource) {
		this.dataSources.remove(dataSource);
		dataSource.internalRemoveDataspace(this);
	}

	public void internalRemoveDataSource(DataSource dataSource) {
		this.dataSources.remove(dataSource);
	}

	//-----------------------schemas-----------------

	/**
	 * @return the schemas
	 */
	public Set<Schema> getSchemas() {
		return schemas;
		//return Collections.unmodifiableSet(schemas);
	}

	/**
	 * @param schemas the schemas to set
	 */
	public void setSchemas(Set<Schema> schemas) {
		this.schemas = schemas;
		for (Schema schema : schemas) {
			schema.internalAddDataspace(this);
		}
	}

	public void addSchema(Schema schema) {
		schemas.add(schema);
		schema.internalAddDataspace(this);
	}

	public void internalAddSchema(Schema schema) {
		schemas.add(schema);
	}

	public void removeSchema(Schema schema) {
		schemas.remove(schema);
		schema.internalRemoveDataspace(this);
	}

	public void internalRemoveSchema(Schema schema) {
		schemas.remove(schema);
	}

	//-----------------------users-----------------

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
		//return Collections.unmodifiableSet(users);
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
		for (User user : users) {
			user.internalAddDataspace(this);
		}
	}

	public void addUser(User user) {
		users.add(user);
		user.internalAddDataspace(this);
	}

	public void internalAddUser(User user) {
		users.add(user);
	}

	public void removeUser(User user) {
		users.remove(user);
		user.internalRemoveDataspace(this);
	}

	public void internalRemoveUser(User user) {
		users.remove(user);
	}

	//-----------------------queries-----------------

	/**
	 * @return the queries
	 */
	public Set<Query> getQueries() {
		return queries;
		//return Collections.unmodifiableSet(queries);
	}

	/**
	 * @param queries the queries to set
	 */
	public void setQueries(Set<Query> queries) {
		this.queries = queries;
		for (Query query : queries) {
			query.internalSetDataspace(this);
		}
	}

	public void addQuery(Query query) {
		queries.add(query);
		query.internalSetDataspace(this);
	}

	public void internalAddQuery(Query query) {
		queries.add(query);
	}

	public void removeQuery(Query query) {
		queries.remove(query);
		query.internalSetDataspace(null);
	}

	public void internalRemoveQuery(Query query) {
		queries.remove(query);
	}

	/**
	 * @param queryResults the queryResults to set
	 */
	/*
	public void setQueryResults(Set<QueryResult> queryResults) {
		this.queryResults = queryResults;
	}
	*/

	/*
	public void addQueryResult(QueryResult queryResult) {
		queryResults.add(queryResult);
	}
	*/

	/**
	 * @return the queryResults
	 */
	/*
	public Set<QueryResult> getQueryResults() {
		return queryResults;
	}
	*/

	//-----------------------mappings-----------------

	/**
	 * @return the mappings
	 */
	/*
	public Set<Mapping> getMappings() {
		return Collections.unmodifiableSet(mappings);
	}
	*/

	/**
	 * @param mappings the mappings to set
	 */
	/*
	public void setMappings(Set<Mapping> mappings) {
		this.mappings = mappings;
		for (Mapping mapping : mappings) {
			mapping.internalSetDataspace(this);
		}
	}

	public void addMapping(Mapping mapping) {
		mappings.add(mapping);
		mapping.internalSetDataspace(this);
	}

	public void internalAddMapping(Mapping mapping) {
		mappings.add(mapping);
	}

	public void removeMapping(Mapping mapping) {
		mappings.remove(mapping);
		mapping.internalSetDataspace(null);
	}

	public void internalRemoveMapping(Mapping mapping) {
		mappings.remove(mapping);
	}
	*/

	//-----------------------schematicCorrespondences-----------------

	/**
	 * @return the correspondences
	 */
	/*
	public Set<SchematicCorrespondence> getCorrespondences() {
		return Collections.unmodifiableSet(correspondences);
	}
	*/

	/**
	 * @param correspondences the correspondences to set
	 */
	/*
	public void setCorrespondences(Set<SchematicCorrespondence> correspondences) {
		this.correspondences = correspondences;
		for (SchematicCorrespondence correspondence : correspondences) {
			correspondence.internalSetDataspace(this);
		}
	}

	public void addCorrespondence(SchematicCorrespondence correspondence) {
		correspondences.add(correspondence);
		correspondence.internalSetDataspace(this);
	}

	public void internalAddCorrespondence(SchematicCorrespondence correspondence) {
		correspondences.add(correspondence);
	}

	public void removeCorrespondence(SchematicCorrespondence correspondence) {
		correspondences.remove(correspondence);
		correspondence.internalSetDataspace(null);
	}

	public void internalRemoveCorrespondence(SchematicCorrespondence correspondence) {
		correspondences.remove(correspondence);
	}
	*/

	//-----------------------matchings-----------------

	/**
	 * @return the matchings
	 */
	/*
	public Set<Matching> getMatchings() {
		return Collections.unmodifiableSet(matchings);
	}
	*/

	/**
	 * @param matchings the matchings to set
	 */
	/*
	public void setMatchings(Set<Matching> matchings) {
		this.matchings = matchings;
		for (Matching matching : matchings) {
			matching.internalSetDataspace(this);
		}
	}

	public void addMatching(Matching matching) {
		matchings.add(matching);
		matching.internalSetDataspace(this);
	}

	public void internalAddMatching(Matching matching) {
		matchings.add(matching);
	}

	public void removeMatching(Matching matching) {
		matchings.remove(matching);
		matching.internalSetDataspace(null);
	}

	public void internalRemoveMatching(Matching matching) {
		matchings.remove(matching);
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataspaceName == null) ? 0 : dataspaceName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Dataspace other = (Dataspace) obj;
		if (dataspaceName == null) {
			if (other.dataspaceName != null)
				return false;
		} else if (!dataspaceName.equals(other.dataspaceName))
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
		builder.append("Dataspace [");
		if (dataspaceName != null)
			builder.append("dataspaceName=").append(dataspaceName);
		builder.append("]");
		return builder.toString();
	}

}
