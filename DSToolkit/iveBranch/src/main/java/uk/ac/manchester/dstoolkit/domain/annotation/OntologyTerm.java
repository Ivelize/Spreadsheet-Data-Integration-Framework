package uk.ac.manchester.dstoolkit.domain.annotation;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;

@Entity
@Table(name = "ONTOLOGY_TERMS")
public class OntologyTerm extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5952011859196306097L;

	@Column(name = "ONTOLOGY_TERM_NAME", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "ONTOLOGY_TERM_DATA_TYPE")
	private DataType dataType;

	@org.hibernate.annotations.CollectionOfElements
	@JoinTable(name = "ONTOLOGY_TERM_ENUM_VALUES", joinColumns = @JoinColumn(name = "ENUM_ID"))
	@Column(name = "ENUM_VALUE", nullable = true)
	private Set<String> enumValues = new LinkedHashSet<String>();

	@ManyToOne
	@JoinColumn(name = "PARENT_ONTOLOGY_TERM_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_ONTOLOGY_TERM_PARENT_ONTOLOGY_TERM_ID")
	private OntologyTerm parentTerm;

	@OneToMany(mappedBy = "parentTerm")
	private Set<OntologyTerm> childTerms = new LinkedHashSet<OntologyTerm>();

	//@OneToMany(mappedBy = "ontologyTerm")
	//private Set<Annotation> annotations = new LinkedHashSet<Annotation>();

	public OntologyTerm() {
	}

	public OntologyTerm(String name, DataType dataType) {
		this.setName(name);
		this.setDataType(dataType);
	}

	public OntologyTerm(String name, Set<String> enumValues) {
		this.setName(name);
		this.setEnumValues(enumValues);
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

	//-----------------------dataType-----------------

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	//-----------------------enumValues-----------------

	/**
	 * @return the enumValues
	 */
	public Set<String> getEnumValues() {
		//return Collections.unmodifiableSet(enumValues);
		return enumValues;
	}

	public void addEnumValue(String enumValue) {
		this.enumValues.add(enumValue);
	}

	/**
	 * @param enumValues the enumValues to set
	 */
	public void setEnumValues(Set<String> enumValues) {
		this.enumValues = enumValues;
	}

	//-----------------------parentTerm-----------------

	/**
	 * @return the parentTerm
	 */
	public OntologyTerm getParentTerm() {
		return parentTerm;
	}

	/**
	 * @param parentTerm the parentTerm to set
	 */
	public void setParentTerm(OntologyTerm parentTerm) {
		if (this.parentTerm != null) {
			this.parentTerm.internalRemoveChildTerm(this);
		}
		this.parentTerm = parentTerm;
		if (parentTerm != null) {
			parentTerm.internalAddChildTerm(this);
		}
		this.parentTerm = parentTerm;
	}

	public void internalSetParentTerm(OntologyTerm parentTerm) {
		this.parentTerm = parentTerm;
	}

	//-----------------------childTerms-----------------

	/**
	 * @return the childTerms
	 */
	public Set<OntologyTerm> getChildTerms() {
		//return Collections.unmodifiableSet(childTerms);
		return childTerms;
	}

	/**
	 * @param childTerms the childTerms to set
	 */
	public void setChildTerms(Set<OntologyTerm> childTerms) {
		this.childTerms = childTerms;
		for (OntologyTerm childTerm : childTerms) {
			childTerm.internalSetParentTerm(this);
		}
	}

	public void addChildTerm(OntologyTerm childTerm) {
		childTerms.add(childTerm);
		childTerm.internalSetParentTerm(this);
	}

	public void internalAddChildTerm(OntologyTerm childTerm) {
		childTerms.add(childTerm);
	}

	public void removeChildTerm(OntologyTerm childTerm) {
		childTerms.remove(childTerm);
		childTerm.internalSetParentTerm(null);
	}

	public void internalRemoveChildTerm(OntologyTerm childTerm) {
		childTerms.remove(childTerm);
	}

	//-----------------------annotations-----------------

	/**
	 * @return the annotations
	 */
	/*
	public Set<Annotation> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}
	*/

	/**
	 * @param annotations the annotations to set
	 */
	/*
	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
		for (Annotation annotation : annotations) {
			annotation.internalSetOntologyTerm(this);
		}
	}
	*/

	/*
	public void addAnnotation(Annotation annotation) {
		this.annotations.add(annotation);
		annotation.internalSetOntologyTerm(this);
	}
	*/

	/*
	public void internalAddAnnotation(Annotation annotation) {
		this.annotations.add(annotation);
	}
	*/

	/*
	public void removeAnnotation(Annotation annotation) {
		this.annotations.remove(annotation);
		annotation.internalSetOntologyTerm(null);
	}
	*/

	/*
	public void internalRemoveAnnotation(Annotation annotation) {
		this.annotations.remove(annotation);
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OntologyTerm [");
		if (childTerms != null)
			builder.append("childTerms=").append(childTerms).append(", ");
		if (dataType != null)
			builder.append("dataType=").append(dataType).append(", ");
		if (enumValues != null)
			builder.append("enumValues=").append(enumValues).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (parentTerm != null)
			builder.append("parentTerm=").append(parentTerm);
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
		result = prime * result + ((childTerms == null) ? 0 : childTerms.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((enumValues == null) ? 0 : enumValues.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parentTerm == null) ? 0 : parentTerm.hashCode());
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
		if (!(obj instanceof OntologyTerm))
			return false;
		OntologyTerm other = (OntologyTerm) obj;
		if (childTerms == null) {
			if (other.childTerms != null)
				return false;
		} else if (!childTerms.equals(other.childTerms))
			return false;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (enumValues == null) {
			if (other.enumValues != null)
				return false;
		} else if (!enumValues.equals(other.enumValues))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentTerm == null) {
			if (other.parentTerm != null)
				return false;
		} else if (!parentTerm.equals(other.parentTerm))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
