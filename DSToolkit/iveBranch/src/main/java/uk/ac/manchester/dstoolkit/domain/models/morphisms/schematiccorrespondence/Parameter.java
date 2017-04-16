package uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "PARAMETERS")
public class Parameter extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5759594591163065105L;

	@Column(name = "PARAMETER_NAME", nullable = false, updatable = false)
	private String name;

	@Column(name = "PARAMETER_VALUE", nullable = false, updatable = false)
	private String value;

	@Enumerated(EnumType.STRING)
	@Column(name = "PARAMETER_DIRECTION", nullable = false, updatable = false)
	private DirectionalityType direction;

	@ManyToOne
	@JoinColumn(name = "SCHEMATIC_CORRESPONDENCE_ID", nullable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "FK_PARAMETER_SCHEMATIC_CORRESPONDENCE_ID")
	private SchematicCorrespondence schematicCorrespondence;

	@ManyToMany
	@JoinTable(name = "PARAMETER_APPLIED_TO_CONSTRUCT", joinColumns = @JoinColumn(name = "PARAMETER_ID"), inverseJoinColumns = @JoinColumn(name = "CONSTRUCT_ID"))
	private Set<CanonicalModelConstruct> appliedTo = new LinkedHashSet<CanonicalModelConstruct>();

	/**
	 *
	 */
	public Parameter() {
	}

	/**
	 * @param name
	 * @param value
	 * @param direction
	 * @param schematicCorrespondence
	 */
	public Parameter(String name, String value, DirectionalityType direction, SchematicCorrespondence schematicCorrespondence) {
		this.setName(name);
		this.setValue(value);
		this.setDirection(direction);
		this.setSchematicCorrespondence(schematicCorrespondence);
	}

	//-----------------------name-----------------

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

	//-----------------------value-----------------

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

	//-----------------------direction-----------------

	/**
	 * @return the direction
	 */
	public DirectionalityType getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(DirectionalityType direction) {
		this.direction = direction;
	}

	//-----------------------schematicCorrespondence-----------------

	/**
	 * @return the schematicCorrespondence
	 */
	public SchematicCorrespondence getSchematicCorrespondence() {
		return schematicCorrespondence;
	}

	/**
	 * @param schematicCorrespondence the schematicCorrespondence to set
	 */
	public void setSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence) {
		if (this.schematicCorrespondence != null) {
			this.schematicCorrespondence.internalRemoveParameter(this);
		}
		this.schematicCorrespondence = schematicCorrespondence;
		if (schematicCorrespondence != null) {
			schematicCorrespondence.internalAddParameter(this);
		}
	}

	public void internalSetSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence) {
		this.schematicCorrespondence = schematicCorrespondence;
	}

	//-----------------------appliedTo-----------------

	/**
	 * @return the appliedTo
	 */
	public Set<CanonicalModelConstruct> getAppliedTo() {
		//return Collections.unmodifiableSet(appliedTo);
		return appliedTo;
	}

	/**
	 * @param appliedTo the appliedTo to set
	 */
	public void setAppliedTo(Set<CanonicalModelConstruct> appliedTo) {
		this.appliedTo = appliedTo;
	}

	public void addAppliedTo(CanonicalModelConstruct construct) {
		this.appliedTo.add(construct);
	}

	public void removeAppliedTo(CanonicalModelConstruct construct) {
		this.appliedTo.remove(construct);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Parameter [");
		if (appliedTo != null)
			builder.append("appliedTo=").append(appliedTo).append(", ");
		if (direction != null)
			builder.append("direction=").append(direction).append(", ");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (schematicCorrespondence != null)
			builder.append("schematicCorrespondence=").append(schematicCorrespondence).append(", ");
		if (value != null)
			builder.append("value=").append(value).append(", ");
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
		result = prime * result + ((appliedTo == null) ? 0 : appliedTo.hashCode());
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((schematicCorrespondence == null) ? 0 : schematicCorrespondence.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		if (appliedTo == null) {
			if (other.appliedTo != null)
				return false;
		} else if (!appliedTo.equals(other.appliedTo))
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (schematicCorrespondence == null) {
			if (other.schematicCorrespondence != null)
				return false;
		} else if (!schematicCorrespondence.equals(other.schematicCorrespondence))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/*
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof Parameter)) return false;

	    final Parameter parameter = (Parameter) o;

	    if (id != parameter.getId()) return false;
	   
	    return true;
	}

	public int hashCode() {
	    int result;
	    result = id.hashCode();
	    return result;
	}
	*/
}
