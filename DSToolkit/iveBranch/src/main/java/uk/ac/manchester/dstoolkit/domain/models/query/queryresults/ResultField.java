package uk.ac.manchester.dstoolkit.domain.models.query.queryresults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "RESULT_FIELDS")
public class ResultField extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2061733146500876548L;

	//this is used to capture the order of the resultFields of a resultType, as they don't seem to be returned in the same order even though a linkedMap is used
	//numbering starts with 0
	@Column(name = "RESULT_FIELD_INDEX")
	private int index = -1;

	@Column(name = "RESULT_FIELD_NAME")
	private String fieldName;

	//CascadeType.PERSIST,

	//@ManyToOne
	//(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	//@JoinColumn(name = "RESULT_TYPE_ID", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.ForeignKey(name = "FK_RESULT_FIELD_RESULT_TYPE_ID")
	//private ResultType resultType;

	@Enumerated(EnumType.STRING)
	@Column(name = "RESULT_FIELD_TYPE")
	private DataType fieldType;

	@ManyToOne
	@JoinColumn(name = "CONSTRUCT_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_RESULT_FIELD_CANONICAL_MODEL_CONSTRUCT_ID")
	private CanonicalModelConstruct canonicalModelConstruct;

	/**
	 * 
	 */
	public ResultField() {
		super();
	}

	/**
	 * @param fieldName
	 * @param fieldType
	 */
	public ResultField(String fieldName, DataType fieldType) {
		super();
		this.setFieldName(fieldName);
		this.setFieldType(fieldType);
	}

	//-----------------------index-----------------

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	//-----------------------fieldName-----------------

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	//-----------------------resultType-----------------

	/**
	 * @param resultType the resultType to set
	 */
	/*
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
	*/

	/**
	 * @return the resultType
	 */
	/*
	public ResultType getResultType() {
		return resultType;
	}
	*/

	//-----------------------fieldType-----------------

	/**
	 * @return the fieldType
	 */
	public DataType getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(DataType fieldType) {
		this.fieldType = fieldType;
	}

	//-----------------------canonicalModelConstruct-----------------

	/**
	 * @return the canonicalModelConstruct
	 */
	public CanonicalModelConstruct getCanonicalModelConstruct() {
		return canonicalModelConstruct;
	}

	/**
	 * @param canonicalModelConstruct the canonicalModelConstruct to set
	 */
	public void setCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		this.canonicalModelConstruct = canonicalModelConstruct;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResultField [");
		if (canonicalModelConstruct != null)
			builder.append("construct=").append(canonicalModelConstruct).append(", ");
		if (fieldName != null)
			builder.append("fieldName=").append(fieldName).append(", ");
		if (fieldType != null)
			builder.append("fieldType=").append(fieldType).append(", ");
		if (id != null)
			builder.append("id=").append(id).append(", ");
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
		result = prime * result + ((canonicalModelConstruct == null) ? 0 : canonicalModelConstruct.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
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
		ResultField other = (ResultField) obj;
		if (canonicalModelConstruct == null) {
			if (other.canonicalModelConstruct != null)
				return false;
		} else if (!canonicalModelConstruct.equals(other.canonicalModelConstruct))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (fieldType == null) {
			if (other.fieldType != null)
				return false;
		} else if (!fieldType.equals(other.fieldType))
			return false;
		return true;
	}

}
