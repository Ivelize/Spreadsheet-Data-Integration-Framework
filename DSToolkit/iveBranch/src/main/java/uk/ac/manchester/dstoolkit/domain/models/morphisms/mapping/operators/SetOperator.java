package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @author chedeler
 *
 */

@Entity
@DiscriminatorValue(value = "SETOP")
public class SetOperator extends MappingOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8207982659121009520L;
	@Enumerated(EnumType.STRING)
	@Column(name = "SETOP_TYPE")
	private SetOperationType setOpType;

	/**
	 * 
	 */
	public SetOperator() {
	}

	/**
	 * @param leftInput
	 * @param rightInput
	 * @param setOpType
	 */
	public SetOperator(MappingOperator leftInput, MappingOperator rightInput, SetOperationType setOpType) {
		super(leftInput, rightInput);
		this.setSetOpType(setOpType);
	}

	@Override
	public boolean isJustScanOperator() {
		return false;
	}

	//-----------------------setOpType-----------------

	/**
	 * @return the setOpType
	 */
	public SetOperationType getSetOpType() {
		return setOpType;
	}

	/**
	 * @param setOpType the setOpType to set
	 */
	public void setSetOpType(SetOperationType setOpType) {
		this.setOpType = setOpType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString("SETOP");
	}

	protected String toString(String setOperator) {
		StringBuilder builder = new StringBuilder();
		builder.append(setOperator);
		builder.append(": SetOperator [");
		if (setOpType != null)
			builder.append("setOpType=").append(setOpType).append(", ");
		if (lhsInput != null)
			builder.append("lhsInput=").append(lhsInput).append(", ");
		if (rhsInput != null)
			builder.append("rhsInput=").append(rhsInput).append(", ");
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
		result = prime * result + ((setOpType == null) ? 0 : setOpType.hashCode());
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
		SetOperator other = (SetOperator) obj;
		if (setOpType == null) {
			if (other.setOpType != null)
				return false;
		} else if (!setOpType.equals(other.setOpType))
			return false;
		return true;
	}

	/*
	public String toString() {
		StringBuffer b = new StringBuffer(setOpType.toString());
		b.append("(");
		b.append(")");
		b.append("[ " + input1.toString() + ", " + input2.toString() + "]");
		return b.toString();
	}
	*/

}
