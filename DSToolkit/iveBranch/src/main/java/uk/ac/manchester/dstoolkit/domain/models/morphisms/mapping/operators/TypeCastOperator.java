package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;

/**
 * @author chedeler
 *
 */

@Entity
@DiscriminatorValue(value = "TYPE_CAST")
public class TypeCastOperator extends MappingOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6267178222496315730L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TYPE_CAST_CANONICAL_MODEL_CONSTRUCT_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_TYPE_CAST_CANONICAL_MODEL_ID")
	private CanonicalModelConstruct constructToTypeCast;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE_CAST_NEW_TYPE")
	private DataType newType;

	/**
	 * 
	 */
	public TypeCastOperator() {
	}

	/**
	 * @param constructToTypeCast
	 * @param newType
	 */
	public TypeCastOperator(CanonicalModelConstruct constructToTypeCast, DataType newType) {
		super();
		this.setConstructToTypeCast(constructToTypeCast);
		this.setNewType(newType);
	}

	/**
	 * @param inputOperator
	 * @param newType
	 */
	public TypeCastOperator(MappingOperator inputOperator, DataType newType) {
		super(inputOperator);
		this.setNewType(newType);
	}

	@Override
	public boolean isJustScanOperator() {
		return false;
	}

	//-----------------------constructToTypeCast-----------------

	/**
	 * @return the constructToTypeCast
	 */
	public CanonicalModelConstruct getConstructToTypeCast() {
		return constructToTypeCast;
	}

	/**
	 * @param constructToTypeCast the constructToTypeCast to set
	 */
	public void setConstructToTypeCast(CanonicalModelConstruct constructToTypeCast) {
		this.constructToTypeCast = constructToTypeCast;
	}

	//-----------------------newType-----------------

	/**
	 * @return the newType
	 */
	public DataType getNewType() {
		return newType;
	}

	/**
	 * @param newType the newType to set
	 */
	public void setNewType(DataType newType) {
		this.newType = newType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString("TYPE_CAST");
	}

	protected String toString(String typeCastOperator) {
		StringBuilder builder = new StringBuilder();
		builder.append(typeCastOperator);
		builder.append(": TypeCastOperator [");
		if (constructToTypeCast != null)
			builder.append("constructToTypeCast=").append(constructToTypeCast.getName()).append(", ");
		if (newType != null)
			builder.append("newType=").append(newType).append(", ");
		if (reconcilingExpression != null)
			builder.append("reconcilingExpression=").append(reconcilingExpression.getExpression());
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
		result = prime * result + ((constructToTypeCast == null) ? 0 : constructToTypeCast.hashCode());
		result = prime * result + ((newType == null) ? 0 : newType.hashCode());
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
		if (getClass() != obj.getClass())
			return false;
		TypeCastOperator other = (TypeCastOperator) obj;
		if (constructToTypeCast == null) {
			if (other.constructToTypeCast != null)
				return false;
		} else if (!constructToTypeCast.equals(other.constructToTypeCast))
			return false;
		if (newType == null) {
			if (other.newType != null)
				return false;
		} else if (!newType.equals(other.newType))
			return false;
		return true;
	}

	/*
	@Override
	public String toString() {
		String s = "TYPE_CAST ";
		s = s + " (";
		s = s + " newType: " + newType;
		s = s + ")";
		s = s + " [";
		if (input1 != null) {
			s = s + input1.toString();
		} else if (constructToTypeCast != null) {
			s = s + constructToTypeCast.toString();
		}
		s = s + "]";
		return s;
	}
	*/

}
