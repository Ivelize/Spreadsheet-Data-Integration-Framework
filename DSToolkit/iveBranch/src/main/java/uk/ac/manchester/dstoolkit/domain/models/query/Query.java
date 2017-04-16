package uk.ac.manchester.dstoolkit.domain.models.query;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.user.User;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "QUERIES")
public class Query extends ModelManagementConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5278737632969741896L;

	static Logger logger = Logger.getLogger(Query.class);

	@Column(name = "QUERY_STRING", length = 50000)
	private String queryString;

	@Column(name = "QUERY_NAME")
	private String queryName;

	@Column(name = "QUERY_DESCRIPTION")
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "QUERY_USER_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_USER_ID")
	private User user;

	@ManyToOne
	//(fetch = FetchType.EAGER)
	@JoinColumn(name = "QUERY_DATASPACE_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_DATASPACE_ID")
	private Dataspace dataspace;

	//TODO check all domain entities with respect to the use of lists or sets

	//TODO think about whether a query could be asked over multiple schemas, I think it could, would save having to do the integration, more pay-as-you-go
	//@ManyToOne
	//@JoinColumn(name = "QUERY_SCHEMA_ID")
	//@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_SCHEMA_ID")
	@ManyToMany
	//(fetch = FetchType.EAGER)
	@JoinTable(name = "QUERY_SCHEMAS", joinColumns = { @JoinColumn(name = "QUERY_ID") }, inverseJoinColumns = { @JoinColumn(name = "SCHEMA_ID") })
	private Set<Schema> schemas = new LinkedHashSet<Schema>();

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "QUERY_DATASOURCES", joinColumns = { @JoinColumn(name = "QUERY_ID") }, inverseJoinColumns = { @JoinColumn(name = "DATASOURCE_ID") })
	private Set<DataSource> dataSources = new LinkedHashSet<DataSource>();

	//@ManyToMany
	//(fetch = FetchType.EAGER)
	//@JoinTable(name = "QUERY_MAPPINGS", joinColumns = { @JoinColumn(name = "QUERY_ID") }, inverseJoinColumns = { @JoinColumn(name = "MAPPING_ID") })
	//private Set<Mapping> mappings = new LinkedHashSet<Mapping>();

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	//, cascade = { CascadeType.PERSIST })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "QUERY_ROOT_OPERATOR_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_QUERY_ROOT_OPERATOR_ID")
	private MappingOperator rootOperator;

	@ManyToMany
	//(fetch = FetchType.EAGER)
	@JoinTable(name = "QUERY_SUPER_ABSTRACTS", joinColumns = { @JoinColumn(name = "QUERY_ID") }, inverseJoinColumns = { @JoinColumn(name = "SUPER_ABSTRACT_ID") })
	private Set<SuperAbstract> superAbstractsQueried = new LinkedHashSet<SuperAbstract>();

	@Transient
	private MappingOperator rootOperatorOfExpandedQuery;

	/**
	 * 
	 */
	public Query() {
		super();
	}

	/**
	 * @param queryName
	 */
	public Query(String queryName) {
		super();
		this.setQueryName(queryName);
	}

	/**
	 * @param queryName
	 * @param queryString
	 */
	public Query(String queryName, String queryString) {
		super();
		this.setQueryName(queryName);
		this.setQueryString(queryString);
	}

	/**
	 * @param queryName
	 * @param schema
	 */
	public Query(String queryName, Schema schema) {
		super();
		this.setQueryName(queryName);
		this.addSchema(schema);
	}

	public Query(String queryName, Set<Schema> schemas) {
		super();
		this.setQueryName(queryName);
		this.setSchemas(schemas);
	}

	protected Set<SuperAbstract> setSuperAbstractsQueried(MappingOperator mappingOperator) {
		Set<SuperAbstract> constructs = new HashSet<SuperAbstract>();
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			constructs.addAll(setSuperAbstractsQueried(mappingOperator.getLhsInput()));
			constructs.addAll(setSuperAbstractsQueried(mappingOperator.getRhsInput()));
			return constructs;
		} else if (mappingOperator instanceof ReduceOperator) {
			constructs.addAll(setSuperAbstractsQueried(mappingOperator.getLhsInput()));
			return constructs;
		} else if (mappingOperator instanceof ScanOperator) {
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			constructs.add(scanOperator.getSuperAbstract());
			return constructs;
		} else
			logger.error("unexpected operator");
		return constructs;
	}

	//TODO finish off testing
	public boolean isJustScanQuery() {
		logger.debug("in isJustScanQuery");
		logger.debug("rootOperator: " + rootOperator);
		boolean isJustScanQuery = false;
		isJustScanQuery = rootOperator.isJustScanOperator();
		logger.debug("isJustScanQuery: " + isJustScanQuery);
		return isJustScanQuery;
	}

	//-----------------------queryString-----------------

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

	//-----------------------queryName-----------------

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

	//-----------------------description-----------------

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	//-----------------------user-----------------

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		if (this.user != null) {
			this.user.internalRemoveQuery(this);
		}
		this.user = user;
		if (user != null) {
			user.internalAddQuery(this);
		}
	}

	public void internalSetUser(User user) {
		this.user = user;
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
		if (this.dataspace != null) {
			this.dataspace.internalRemoveQuery(this);
		}
		this.dataspace = dataspace;
		if (dataspace != null) {
			dataspace.internalAddQuery(this);
		}
	}

	public void internalSetDataspace(Dataspace dataspace) {
		this.dataspace = dataspace;
	}

	//-----------------------schemas-----------------

	/**
	 * @return the schemas
	 */
	public Set<Schema> getSchemas() {
		//return Collections.unmodifiableSet(schemas);
		return schemas;
	}

	/**
	 * @param schemas the schemas to set
	 */
	public void setSchemas(Set<Schema> schemas) {
		this.schemas = schemas;
	}

	public void addSchema(Schema schema) {
		this.schemas.add(schema);
	}

	public void removeSchema(Schema schema) {
		this.schemas.remove(schema);
	}

	//-----------------------dataSources-----------------

	/**
	 * @return the dataSources
	 */
	public Set<DataSource> getDataSources() {
		//return Collections.unmodifiableSet(dataSources);
		return dataSources;
	}

	/**
	 * @param dataSources the dataSources to set
	 */
	public void setDataSources(Set<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public void addDataSource(DataSource dataSource) {
		dataSources.add(dataSource);
	}

	public void removeDataSource(DataSource dataSource) {
		dataSources.remove(dataSource);
	}

	//-----------------------superAbstractsQueried-----------------

	/**
	 * @return the superAbstractsQueried
	 */
	public Set<SuperAbstract> getSuperAbstractsQueried() {
		//return Collections.unmodifiableSet(constructsQueried);
		return superAbstractsQueried;
	}

	/**
	 * @param constructsQueried the constructsQueried to set
	 */
	public void setSuperAbstractQueried(Set<SuperAbstract> superAbstractsQueried) {
		//TODO might be worth checking whether they're already set
		this.superAbstractsQueried = superAbstractsQueried;
	}

	public void addSuperAbstractQueried(SuperAbstract superAbstractQueried) {
		this.superAbstractsQueried.add(superAbstractQueried);
	}

	public void removeSuperAbstractQueried(SuperAbstract superAbstractQueried) {
		this.superAbstractsQueried.remove(superAbstractQueried);
	}

	//-----------------------mappings-----------------

	/**
	 * @return the mappings
	 */
	/*
	public Set<Mapping> getMappings() {
		//return Collections.unmodifiableSet(mappings);
		return mappings;
	}
	*/

	/**
	 * @param mappings the mappings to set
	 */
	/*
	public void setMappings(Set<Mapping> mappings) {
		this.mappings = mappings;
	}

	public void addMapping(Mapping mapping) {
		mappings.add(mapping);
	}

	public void removeMapping(Mapping mapping) {
		mappings.remove(mapping);
	}
	*/

	//-----------------------rootOperator-----------------

	/**
	 * @return the rootOperator
	 */
	public MappingOperator getRootOperator() {
		return rootOperator;
	}

	/**
	 * @param rootOperator the rootOperator to set
	 */
	public void setRootOperator(MappingOperator rootOperator) {
		this.rootOperator = rootOperator;
		if (superAbstractsQueried.isEmpty()) {
			this.superAbstractsQueried = setSuperAbstractsQueried(rootOperator);
		}
	}

	//-----------------------rootOperatorOfExpandedQuery-----------------

	/**
	 * @param rootOperatorOfExpandedQuery the rootOperatorOfExpandedQuery to set
	 */
	public void setRootOperatorOfExpandedQuery(MappingOperator rootOperatorOfExpandedQuery) {
		this.rootOperatorOfExpandedQuery = rootOperatorOfExpandedQuery;
	}

	/**
	 * @return the rootOperatorOfExpandedQuery
	 */
	public MappingOperator getRootOperatorOfExpandedQuery() {
		return rootOperatorOfExpandedQuery;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((queryName == null) ? 0 : queryName.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		Query other = (Query) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (queryName == null) {
			if (other.queryName != null)
				return false;
		} else if (!queryName.equals(other.queryName))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Query [");
		builder.append("id=").append(this.getId()).append(", ");
		builder.append("version=").append(this.getVersion()).append(", ");
		if (queryName != null)
			builder.append("queryName=").append(queryName).append(", ");
		if (queryString != null)
			builder.append("queryString=").append(queryString).append(", ");
		builder.append("]");
		return builder.toString();
	}

}
