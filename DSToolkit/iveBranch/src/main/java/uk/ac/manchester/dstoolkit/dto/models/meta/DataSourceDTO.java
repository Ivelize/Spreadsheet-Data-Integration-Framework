package uk.ac.manchester.dstoolkit.dto.models.meta;

public class DataSourceDTO {

	private Long id;
	private String name;
	private String schemaName;
	private String connectionURL;
	private String schemaURL;
	private String driverClass;
	private String userName;
	private String password;
	private String description;
	private String schemaElementsToExcludeXmlFileLocation;

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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaElementsToExcludeXmlFileLocation the schemaElementsToExcludeXmlFileLocation to set
	 */
	public void setSchemaElementsToExcludeXmlFileLocation(String schemaElementsToExcludeXmlFileLocation) {
		this.schemaElementsToExcludeXmlFileLocation = schemaElementsToExcludeXmlFileLocation;
	}

	/**
	 * @return the schemaElementsToExcludeXmlFileLocation
	 */
	public String getSchemaElementsToExcludeXmlFileLocation() {
		return schemaElementsToExcludeXmlFileLocation;
	}

}
