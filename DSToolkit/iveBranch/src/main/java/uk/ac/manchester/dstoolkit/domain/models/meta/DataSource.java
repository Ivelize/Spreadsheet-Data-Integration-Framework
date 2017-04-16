package uk.ac.manchester.dstoolkit.domain.models.meta;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;

/**
 * @author chedeler
 *
 *   
 * Revision (klitos):
 *  1. Add column "is_RDF" - indicate that this source is an RDF source
 *  2. Add getter/setter methods for IsRDFSource
 *  3. Add constructor to take into account is_RDF
 *  4. Add new column to store the URI of a SPARQL end-point
 */

@Entity
@Table(name = "DATASOURCES")
public class DataSource extends ModelManagementConstruct {

	private static final long serialVersionUID = 4429693100924226155L;

	@Column(name = "DATA_SOURCE_NAME")
	private String name;

	/*Hold the connection URL of the database to find the source*/
	@Column(name = "CONNECTION_URL", nullable = false)
	private String connectionURL;

	@Column(name = "SPARQL_URL", nullable = true)
	private String sparqlURL;
	
	@Column(name = "SCHEMA_URL", nullable = true)
	private String schemaURL;

	@Column(name = "is_RDF", nullable = true)
	private String isRDFSource;
	
	@Column(name = "DRIVER_CLASS", nullable = true)
	private String driverClass;

	@Column(name = "USER_NAME", nullable = true)
	private String userName;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "DESCRIPTION")
	private String description;

	//TODO to be able to maintain the system in case of changes to the sources, might have to be
	//able to associate each dataSource with multiple schemas, i.e., multiple versions of the same schema
	//need a timestamp on them to determine which one is the latest - do we need timestamps on
	//other modelManagementConstructs?

	@OneToOne(mappedBy = "dataSource")
	private Schema schema;

	@ManyToMany(mappedBy = "dataSources")
	private Set<Dataspace> dataspaces = new LinkedHashSet<Dataspace>();

	/**
	 * 
	 */
	public DataSource() {
		super();
	}

	/**
	 * @param connectionURL
	 * @param driverClass
	 * @param userName
	 * @param password
	 */
	public DataSource(String connectionURL, String driverClass, String userName, String password) {
		super();
		this.setConnectionURL(connectionURL);
		this.setDriverClass(driverClass);
		this.setUserName(userName);
		this.setPassword(password);
	}

	
	/***
	 * Constructor for Datasource using Jena TDB
	 * 
	 */
	public DataSource(String dataSourceName, String sourceURI, String schemaURL) {
		super();
		this.setName(dataSourceName);
		this.setConnectionURL(sourceURI);
		this.setSchemaURL(schemaURL);
		this.setIsRDFSource("true");		
	}
	
	
	public DataSource(String connectionURL, String schemaURL, String driverClass, String userName, String password) {
		super();
		this.setConnectionURL(connectionURL);
		this.setSchemaURL(schemaURL);
		this.setDriverClass(driverClass);
		this.setUserName(userName);
		this.setPassword(password);
	}

	/**
	 * Constructor: For SPARQL end-point data source
	 * 
	 *   - SPARQLServiceURL = will also be used as the name of the Name Graph in the SDBStore.
	 *   - connectionURL = the URL that points to the actual source. In case of a sparql endpoint this will be
	 *   the URI of the sparlq endpoint e.g., http://example.com/sparql/
	 */
	public DataSource(String connectionURL, String sparqlURL, String schemaURL, String driverClass, String userName, String password) {
		super();
		this.setConnectionURL(connectionURL);
		this.setSparqlURL(sparqlURL);
		this.setSchemaURL(schemaURL);
		this.setDriverClass(driverClass);
		this.setUserName(userName);
		this.setPassword(password);
		this.setIsRDFSource("true");
	}
	
	/***
	 * Constructor: For RDF-Dump data sources
	 * 
	 * @param sourceName
	 * @param connectionURL
	 * @param schemaURL
	 * @param driverClass
	 * @param userName
	 * @param password
	 * @param isRDFSource
	 */
	public DataSource(String dataSourceName, String connectionURL, String schemaURL, String driverClass, String userName,
													String password, String isRDFSource) {
		super();
		this.setName(dataSourceName);
		this.setConnectionURL(connectionURL);
		this.setSchemaURL(schemaURL);
		this.setDriverClass(driverClass);
		this.setUserName(userName);
		this.setPassword(password);
		this.setIsRDFSource(isRDFSource);
	}
	
	//-----------------------engineType--------------------
	/**
	 * @return the engineType
	 */
	public String getEngineType() {
		String result = "";
	 	String driverClass = this.getDriverClass().toLowerCase(); 
		if (driverClass.contains("mysql")){
			result = "mysql";
		} else if (driverClass.contains("postgresql")) {
			result = "postgresql";
		} else if (driverClass.contains("derby")) {
			result = "derby";			
		}	
		return result;
	}

	//-----------------------dataSourceName-----------------
	
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
	
	//-----------------------connectionURL-----------------

	/**
	 * @return the connectionURL
	 */
	public String getConnectionURL() {
		return connectionURL;
	}

	/**
	 * @param connectionURL the connectionURL to set
	 */
	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}
	
	//-----------------------sparqlURL-----------------

	/**
	 * @return the sparqlURL
	 */
	public String getSparqlURL() {
		return this.sparqlURL;
	}

	/**
	 * @param sparqlURL - URL that points to the s
	 */
	public void setSparqlURL(String sparqlURL) {
		this.sparqlURL = sparqlURL;
	}
	

	//-----------------------schemaURL-----------------

	/**
	 * @return the schemaURL
	 */
	public String getSchemaURL() {
		return schemaURL;
	}

	/**
	 * @param schemaURL the schemaURL to set
	 */
	public void setSchemaURL(String schemaURL) {
		this.schemaURL = schemaURL;
	}

	//-----------------------driverClass-----------------

	/**
	 * @return the driverClass
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass the driverClass to set
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	//-----------------------userName-----------------

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	//-----------------------password-----------------

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	//-----------------------is_RDF_Source-----------------

	/**
	 * @return String true/false. if this source is an RDF Source or not
	 */
	public String getIsRDFSource() {
		return this.isRDFSource;
	}

	/**
	 * @param String true/false
	 */
	public void setIsRDFSource(String rdf) {
		this.isRDFSource = rdf;
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

	//-----------------------schema-----------------

	/**
	 * @return the schema
	 */
	public Schema getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(Schema schema) {
		if (this.schema != null) {
			this.schema.internalSetDataSource(null);
		}
		this.schema = schema;
		if (schema != null) {
			schema.internalSetDataSource(this);
		}
	}

	public void internalSetSchema(Schema schema) {
		this.schema = schema;
	}

	//-----------------------dataspaces-----------------

	/**
	 * @return the dataspaces
	 */
	public Set<Dataspace> getDataspaces() {
		//return Collections.unmodifiableSet(dataspaces);
		return dataspaces;
	}

	/**
	 * @param dataspaces the dataspaces to set
	 */
	public void setDataspaces(Set<Dataspace> dataspaces) {
		this.dataspaces = dataspaces;
		for (Dataspace dataspace : dataspaces) {
			dataspace.internalAddDataSource(this);
		}
	}

	public void addDataspace(Dataspace dataspace) {
		dataspaces.add(dataspace);
		dataspace.internalAddDataSource(this);
	}

	public void internalAddDataspace(Dataspace dataspace) {
		dataspaces.add(dataspace);
	}

	public void removeDataspace(Dataspace dataspace) {
		dataspaces.remove(dataspace);
		dataspace.internalRemoveDataSource(this);
	}

	public void internalRemoveDataspace(Dataspace dataspace) {
		dataspaces.remove(dataspace);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSource [");
		builder.append("name=").append(name).append(", ");
		if (schema != null)
			builder.append("schema=").append(schema.getName()).append(", ");
		if (sparqlURL != null)
			builder.append("sparqlURL=").append(sparqlURL).append(", ");
		/*
		if (dataspaces != null)
			builder.append("dataspaces=").append(dataspaces).append(", ");
		if (driverClass != null)
			builder.append("driverClass=").append(driverClass).append(", ");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (password != null)
			builder.append("password=").append(password).append(", ");
		if (schema != null)
			builder.append("schema=").append(schema).append(", ");
		if (userName != null)
			builder.append("userName=").append(userName).append(", ");
			*/
		builder.append("version=").append(version).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionURL == null) ? 0 : connectionURL.hashCode());
		result = prime * result + ((driverClass == null) ? 0 : driverClass.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataSource other = (DataSource) obj;
		if (connectionURL == null) {
			if (other.connectionURL != null)
				return false;
		} else if (!connectionURL.equals(other.connectionURL))
			return false;
		if (driverClass == null) {
			if (other.driverClass != null)
				return false;
		} else if (!driverClass.equals(other.driverClass))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;

		return true;
	}//end equals()
}//end Class
