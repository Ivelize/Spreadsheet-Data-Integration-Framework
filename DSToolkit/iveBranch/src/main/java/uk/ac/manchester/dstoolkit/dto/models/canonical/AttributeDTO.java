package uk.ac.manchester.dstoolkit.dto.models.canonical;

public class AttributeDTO {

	private Long id;
	private String name;
	private String dataType;
	private boolean isNullable;

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
	 * @return the fieldName
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param type the type to set
	 */
	public void setDataType(String type) {
		this.dataType = type;
	}

	/**
	 * @return the isNull
	 */
	public boolean getIsNullable() {
		return isNullable;
	}

	/**
	 * @param isNull the isNull to set
	 */
	public void setIsNullable(boolean isNull) {
		this.isNullable = isNull;
	}

}
