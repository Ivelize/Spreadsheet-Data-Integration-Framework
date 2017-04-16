package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

/**
 * @author chedeler
 *
 */

@Entity
@DiscriminatorValue(value = "RENAME")
public class RenameOperator extends MappingOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7231820518300394608L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RENAME_CANONICAL_MODEL_CONSTRUCT_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_RENAME_OPERATOR_CANONICAL_MODEL_CONSTRUCT_ID")
	private CanonicalModelConstruct constructToRename;

	@Column(name = "RENAME_NEW_NAME")
	private String newName;

	/**
	 * 
	 */
	public RenameOperator() {
	}

	/**
	 * @param constructToRename
	 * @param newName
	 */
	public RenameOperator(CanonicalModelConstruct constructToRename, String newName) {
		super();
		this.setConstructToRename(constructToRename);
		this.setNewName(newName);
	}

	@Override
	public boolean isJustScanOperator() {
		return false;
	}

	//-----------------------constructToRename-----------------

	/**
	 * @return the constructToRename
	 */
	public CanonicalModelConstruct getConstructToRename() {
		return constructToRename;
	}

	/**
	 * @param constructToRename the constructToRename to set
	 */
	public void setConstructToRename(CanonicalModelConstruct constructToRename) {
		this.constructToRename = constructToRename;
	}

	//-----------------------newName-----------------

	/**
	 * @return the newName
	 */
	public String getNewName() {
		return newName;
	}

	/**
	 * @param newName the newName to set
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString("RENAME");
	}

	protected String toString(String renameOperator) {
		StringBuilder builder = new StringBuilder();
		builder.append(renameOperator);
		builder.append(": RenameOperator [");
		if (constructToRename != null)
			builder.append("constructToRename=").append(constructToRename.getName()).append(", ");
		if (newName != null)
			builder.append("newName=").append(newName).append(", ");
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
		result = prime * result + ((constructToRename == null) ? 0 : constructToRename.hashCode());
		result = prime * result + ((newName == null) ? 0 : newName.hashCode());
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
		RenameOperator other = (RenameOperator) obj;
		if (constructToRename == null) {
			if (other.constructToRename != null)
				return false;
		} else if (!constructToRename.equals(other.constructToRename))
			return false;
		if (newName == null) {
			if (other.newName != null)
				return false;
		} else if (!newName.equals(other.newName))
			return false;
		return true;
	}

	/*
		@Override
		public String toString() {
			String s = "RENAME ";
			s = s + " (";
			s = s + " newName: " + newName;
			s = s + ")";
			s = s + " [";
			if (input1 != null) {
				s = s + input1.toString();
			} else if (constructToRename != null) {
				s = s + constructToRename.toString();
			}
			s = s + "]";
			return s;
		}
	*/
}
