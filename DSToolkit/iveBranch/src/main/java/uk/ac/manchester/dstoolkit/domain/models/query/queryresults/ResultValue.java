package uk.ac.manchester.dstoolkit.domain.models.query.queryresults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;

@Entity
@Table(name = "RESULT_FIELD_RESULT_VALUES")
public class ResultValue extends ModelManagementConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = 799601527289614472L;

	//@ManyToOne
	//(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@JoinColumn(name = "RESULT_INSTANCE_ID", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.ForeignKey(name = "FK_RES_FIELD_RES_VALUE_RESULT_INSTANCE_ID")
	//private ResultInstance resultInstance;

	//@ManyToOne (fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@JoinColumn(name = "RESULT_FIELD_ID", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.ForeignKey(name = "FK_RES_FIELD_RES_VALUE_RESULT_FIELD_ID")
	//private ResultField resultField;

	@Column(name = "RESULT_FIELD_NAME", length = 10000)
	private String resultFieldName;

	//@Enumerated(EnumType.STRING)
	//@Column(name = "RESULT_FIELD_DATA_TYPE")
	//private DataType dataType;

	@Column(name = "RESULT_VALUE_VALUE", length = 50000)
	private String value;

	public ResultValue() {

	}

	//public ResultValue(ResultField resultField, String resultValue) {
	public ResultValue(String resultFieldName, String value) {
		//this.resultField = resultField;
		//this.resultFieldName = resultField.getFieldName();
		this.setResultFieldName(resultFieldName);
		//this.dataType = resultField.getFieldType();
		this.setValue(value);
	}

	/**
	 * @param resultInstance the resultInstance to set
	 */
	/*
	public void setResultInstance(ResultInstance resultInstance) {
		this.resultInstance = resultInstance;
	}
	*/

	/**
	 * @return the resultInstance
	 */
	/*
	public ResultInstance getResultInstance() {
		return resultInstance;
	}
	*/

	/**
	 * @return the resultField
	 */
	/*
	public ResultField getResultField() {
		return resultField;
	}
	*/

	/**
	 * @param resultField the resultField to set
	 */
	/*
	public void setResultField(ResultField resultField) {
		this.resultField = resultField;
	}
	*/

	//-----------------------resultFieldName-----------------

	/**
	 * @return the resultFieldName
	 */
	public String getResultFieldName() {
		return resultFieldName;
	}

	/**
	 * @param resultFieldName the resultFieldName to set
	 */
	public void setResultFieldName(String resultFieldName) {
		this.resultFieldName = resultFieldName;
	}

	//-----------------------dataType-----------------

	/**
	 * @return the dataType
	 */
	/*
	public DataType getDataType() {
		return dataType;
	}
	*/

	/**
	 * @param dataType the dataType to set
	 */
	/*
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	*/

	//-----------------------resultValue-----------------

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResultValue [");
		if (resultFieldName != null)
			builder.append("resultFieldName=").append(resultFieldName).append(", ");
		if (value != null)
			builder.append("value=").append(value).append(", ");
		if (id != null)
			builder.append("id=").append(id);
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((resultFieldName == null) ? 0 : resultFieldName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ResultValue))
			return false;
		ResultValue other = (ResultValue) obj;
		if (resultFieldName == null) {
			if (other.resultFieldName != null)
				return false;
		} else if (!resultFieldName.equals(other.resultFieldName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
