package uk.ac.manchester.dstoolkit.domain.models.canonical;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;

/**
 * @author chedeler
 * 
 * Revision (klitos):
 *  1. Add a new constructor for storing an extra Property to SuperLexical.
 */

@Entity
@Table(name = "SUPER_LEXICALS")
public class SuperLexical extends CanonicalModelConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8534690206282441530L;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_LEXICAL_DATA_TYPE")
	private DataType dataType;

	@Column(name = "SUPER_LEXICAL_MAX_VALUE_SIZE")
	private int maxValueSize;

	@Column(name = "SUPER_LEXICAL_IS_IDENTIFIER")
	private Boolean isIdentifier;

	@Column(name = "SUPER_LEXICAL_IS_NULLABLE")
	private Boolean isNullable;

	@Column(name = "SUPER_LEXICAL_IS_OPTIONAL")
	private Boolean isOptional;

	@Column(name = "SUPER_LEXICAL_NUMBER_OF_DISTINCT_VALUES")
	private int numberOfDistinctValues;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_LEXICAL_MIDST_SUPER_MODEL_TYPE")
	private SuperLexicalMIDSTSuperModelType midstSuperModelType;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_LEXICAL_MODEL_SPECIFIC_TYPE")
	private SuperLexicalModelSpecificType modelSpecificType;

	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "SUPER_LEXICAL_PARENT_SUPER_ABSTRACT_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SUPER_LEXICAL_PARENT_SUPER_ABSTRACT_ID")
	private SuperAbstract parentSuperAbstract;

	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "PARENT_SUPER_RELATIONSHIP_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SUPER_LEXICAL_PARENT_SUPER_RELATIONSHIP_ID")
	private SuperRelationship parentSuperRelationship;

	@ManyToMany
	@JoinTable(name = "PARTICIPATION_SPECIFYING_SUPER_LEXICAL", joinColumns = @JoinColumn(name = "SUPER_LEXICAL_ID"), inverseJoinColumns = @JoinColumn(name = "PARTICIPATION_IN_SUPER_RELATIONSHIP_ID"))
	private Set<ParticipationOfCMCInSuperRelationship> specifiedSuperRelationships = new LinkedHashSet<ParticipationOfCMCInSuperRelationship>();

	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "PARENT_SUPER_LEXICAL_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SUPER_LEXICAL_PARENT_SUPER_LEXICAL_ID")
	private SuperLexical parentSuperLexical;

	@OneToMany(mappedBy = "parentSuperLexical")
	//, fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	private Set<SuperLexical> childSuperLexicals = new LinkedHashSet<SuperLexical>();
	
	@Transient
	private List<String> instances = new ArrayList<String>();

	/**
	 * 
	 */
	public SuperLexical() {
		super();
		this.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
	}

	/**
	 * @param name
	 * @param schema
	 */
	public SuperLexical(String name, Schema schema) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
	}

	/**
	 * RDF
	 * 
	 * @param name
	 * @param extraProperty - namespace
	 * @param schema
	 * @param dataType
	 * @param isNullable
	 * @param modelSpecificType
	 */
	public SuperLexical(String name, CanonicalModelProperty extraProperty, Schema schema, DataType dataType, Boolean isNullable,
			SuperLexicalModelSpecificType modelSpecificType) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
		this.setDataType(dataType);
		this.setIsNullable(isNullable);
		this.setModelSpecificType(modelSpecificType);
		this.addProperty(extraProperty);
	}
	
	public SuperLexical(String name, Set<CanonicalModelProperty> propSet, Schema schema, DataType dataType, Boolean isNullable,
			SuperLexicalModelSpecificType modelSpecificType) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_LEXICAL);
		this.setDataType(dataType);
		this.setIsNullable(isNullable);
		this.setModelSpecificType(modelSpecificType);
		this.addProperties(propSet);
	}

	/**
	 * @param name
	 * @param schema
	 * @param dataType
	 * @param isNullable
	 * @param midstSuperModelType
	 * @param modelSpecificType
	 */
	public SuperLexical(String name, Schema schema, DataType dataType, Boolean isNullable, SuperLexicalMIDSTSuperModelType midstSuperModelType,
			SuperLexicalModelSpecificType modelSpecificType) {
		this(name, schema);
		this.setDataType(dataType);
		this.setIsNullable(isNullable);
		this.setMidstSuperModelType(midstSuperModelType);
		this.setModelSpecificType(modelSpecificType);
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

	//-----------------------isIdentifier-----------------

	/**
	 * @return the isIdentifier
	 */
	public Boolean getIsIdentifier() {
		return isIdentifier;
	}

	/**
	 * @param isIdentifier the isIdentifier to set
	 */
	public void setIsIdentifier(Boolean isIdentifier) {
		this.isIdentifier = isIdentifier;
	}

	//-----------------------isNullable-----------------

	/**
	 * @return the isNullable
	 */
	public Boolean getIsNullable() {
		return isNullable;
	}

	/**
	 * @param isNullable the isNullable to set
	 */
	public void setIsNullable(Boolean isNullable) {
		this.isNullable = isNullable;
	}

	//-----------------------isOptional-----------------

	/**
	 * @return the isOptional
	 */
	public Boolean getIsOptional() {
		return isOptional;
	}

	/**
	 * @param isOptional the isOptional to set
	 */
	public void setIsOptional(Boolean isOptional) {
		this.isOptional = isOptional;
	}

	//-----------------------numberOfDistinctValues-----------------

	/**
	 * @return the numberOfDistinctValues
	 */
	public int getNumberOfDistinctValues() {
		return numberOfDistinctValues;
	}

	/**
	 * @param numberOfDistinctValues the numberOfDistinctValues to set
	 */
	public void setNumberOfDistinctValues(int numberOfDistinctValues) {
		this.numberOfDistinctValues = numberOfDistinctValues;
	}

	//-----------------------midstSuperModelType-----------------

	/**
	 * @return the midstSuperModelType
	 */
	public SuperLexicalMIDSTSuperModelType getMidstSuperModelType() {
		return midstSuperModelType;
	}

	/**
	 * @param midstSuperModelType the midstSuperModelType to set
	 */
	public void setMidstSuperModelType(SuperLexicalMIDSTSuperModelType midstSuperModelType) {
		this.midstSuperModelType = midstSuperModelType;
	}

	//-----------------------modelSpecificType-----------------

	/**
	 * @return the modelSpecificType
	 */
	public SuperLexicalModelSpecificType getModelSpecificType() {
		return modelSpecificType;
	}

	/**
	 * @param modelSpecificType the modelSpecificType to set
	 */
	public void setModelSpecificType(SuperLexicalModelSpecificType modelSpecificType) {
		this.modelSpecificType = modelSpecificType;
	}

	//-----------------------parentSuperAbstract-----------------

	/**
	 * @return the parentSuperAbstract
	 */
	public SuperAbstract getParentSuperAbstract() {
		return parentSuperAbstract;
	}

	public boolean hasParentSuperAbstract() {
		if (parentSuperAbstract == null)
			return false;
		return true;
	}

	/**
	 * @param parentSuperAbstract the parentSuperAbstract to set
	 */
	public void setParentSuperAbstract(SuperAbstract parentSuperAbstract) {
		if (this.parentSuperAbstract != null) {
			this.parentSuperAbstract.internalRemoveSuperLexical(this);
		}
		this.parentSuperAbstract = parentSuperAbstract;
		if (parentSuperAbstract != null) {
			parentSuperAbstract.internalAddSuperLexical(this);
		}
	}

	public void internalSetParentSuperAbstract(SuperAbstract parentSuperAbstract) {
		this.parentSuperAbstract = parentSuperAbstract;
	}

	//-----------------------parentSuperRelationship-----------------

	/**
	 * @return the parentSuperRelationship
	 */
	public SuperRelationship getParentSuperRelationship() {
		return parentSuperRelationship;
	}

	public boolean hasParentSuperRelationship() {
		if (parentSuperRelationship == null)
			return false;
		return true;
	}

	/**
	 * @param parentSuperRelationship the parentSuperRelationship to set
	 */
	public void setParentSuperRelationship(SuperRelationship parentSuperRelationship) {
		if (this.parentSuperRelationship != null) {
			this.parentSuperRelationship.internalRemoveSuperLexical(this);
		}
		this.parentSuperRelationship = parentSuperRelationship;
		if (parentSuperRelationship != null) {
			parentSuperRelationship.internalAddSuperLexical(this);
		}
	}

	public void internalSetParentSuperRelationship(SuperRelationship parentSuperRelationship) {
		this.parentSuperRelationship = parentSuperRelationship;
	}

	//-----------------------specifiedSuperRelationships-----------------

	/**
	 * @return the specifiedSuperRelationships
	 */
	public Set<ParticipationOfCMCInSuperRelationship> getSpecifiedSuperRelationships() {
		//return Collections.unmodifiableSet(specifiedSuperRelationships);
		return specifiedSuperRelationships;
	}

	/**
	 * @param specifiedSuperRelationships the specifiedSuperRelationships to set
	 */
	public void setSpecifiedSuperRelationships(Set<ParticipationOfCMCInSuperRelationship> specifiedSuperRelationships) {
		this.specifiedSuperRelationships = specifiedSuperRelationships;
		for (ParticipationOfCMCInSuperRelationship specifies : specifiedSuperRelationships) {
			specifies.internalAddSpecifyingSuperLexical(this);
		}
	}

	public void addSpecifiedSuperRelationship(ParticipationOfCMCInSuperRelationship specifiedSuperRelationship) {
		this.specifiedSuperRelationships.add(specifiedSuperRelationship);
		specifiedSuperRelationship.internalAddSpecifyingSuperLexical(this);
	}

	public void internalAddSpecifiedSuperRelationship(ParticipationOfCMCInSuperRelationship specifiedSuperRelationship) {
		this.specifiedSuperRelationships.add(specifiedSuperRelationship);
	}

	public void removeSpecifiedSuperRelationship(ParticipationOfCMCInSuperRelationship specifiedSuperRelationship) {
		this.specifiedSuperRelationships.remove(specifiedSuperRelationship);
		specifiedSuperRelationship.internalAddSpecifyingSuperLexical(null);
	}

	public void internalRemoveSpecifiedSuperRelationship(ParticipationOfCMCInSuperRelationship specifiedSuperRelationship) {
		this.specifiedSuperRelationships.remove(specifiedSuperRelationship);
	}

	public SuperAbstract getFirstAncestorSuperAbstract() {
		if (this.hasParentSuperAbstract()) {
			return this.getParentSuperAbstract();
		} else if (this.hasParentSuperLexical()) {
			return this.getParentSuperLexical().getFirstAncestorSuperAbstract();
		}
		return null;
	}

	//-----------------------parentSuperLexical-----------------

	/**
	 * @return the parentSuperLexical
	 */
	public SuperLexical getParentSuperLexical() {
		return parentSuperLexical;
	}

	public boolean hasParentSuperLexical() {
		if (parentSuperLexical == null)
			return false;
		return true;
	}

	/**
	 * @param parentSuperLexical the parentSuperLexical to set
	 */
	public void setParentSuperLexical(SuperLexical parentSuperLexical) {
		if (this.parentSuperLexical != null) {
			this.parentSuperLexical.internalRemoveChildSuperLexical(this);
		}
		this.parentSuperLexical = parentSuperLexical;
		if (parentSuperLexical != null) {
			parentSuperLexical.internalAddChildSuperLexical(this);
		}
	}

	public void internalSetParentSuperLexical(SuperLexical parentSuperLexical) {
		this.parentSuperLexical = parentSuperLexical;
	}

	//-----------------------childSuperLexicals-----------------

	/**
	 * @return the childSuperLexicals
	 */
	public Set<SuperLexical> getChildSuperLexicals() {
		//return Collections.unmodifiableSet(childSuperLexicals);
		return childSuperLexicals;
	}

	/**
	 * @param childSuperLexicals the childSuperLexicals to set
	 */
	public void setChildSuperLexicals(Set<SuperLexical> childSuperLexicals) {
		this.childSuperLexicals = childSuperLexicals;
		for (SuperLexical childSuperLexical : childSuperLexicals) {
			childSuperLexical.internalSetParentSuperLexical(this);
		}
	}

	public void addChildSuperLexical(SuperLexical childSuperLexical) {
		this.childSuperLexicals.add(childSuperLexical);
		childSuperLexical.internalSetParentSuperLexical(this);
	}

	public void internalAddChildSuperLexical(SuperLexical childSuperLexical) {
		this.childSuperLexicals.add(childSuperLexical);
	}

	public void removeChildSuperLexical(SuperLexical childSuperLexical) {
		this.childSuperLexicals.remove(childSuperLexical);
		childSuperLexical.internalSetParentSuperLexical(null);
	}

	public void internalRemoveChildSuperLexical(SuperLexical childSuperLexical) {
		this.childSuperLexicals.remove(childSuperLexical);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((isIdentifier == null) ? 0 : isIdentifier.hashCode());
		result = prime * result + ((isNullable == null) ? 0 : isNullable.hashCode());
		result = prime * result + ((isOptional == null) ? 0 : isOptional.hashCode());
		result = prime * result + ((midstSuperModelType == null) ? 0 : midstSuperModelType.hashCode());
		result = prime * result + ((modelSpecificType == null) ? 0 : modelSpecificType.hashCode());
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
		SuperLexical other = (SuperLexical) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (isIdentifier == null) {
			if (other.isIdentifier != null)
				return false;
		} else if (!isIdentifier.equals(other.isIdentifier))
			return false;
		if (isNullable == null) {
			if (other.isNullable != null)
				return false;
		} else if (!isNullable.equals(other.isNullable))
			return false;
		if (isOptional == null) {
			if (other.isOptional != null)
				return false;
		} else if (!isOptional.equals(other.isOptional))
			return false;
		if (midstSuperModelType == null) {
			if (other.midstSuperModelType != null)
				return false;
		} else if (!midstSuperModelType.equals(other.midstSuperModelType))
			return false;
		if (modelSpecificType == null) {
			if (other.modelSpecificType != null)
				return false;
		} else if (!modelSpecificType.equals(other.modelSpecificType))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("SuperLexical [");
		if (dataType != null)
			builder.append("dataType=").append(dataType).append(", ");
		if (isIdentifier != null)
			builder.append("isIdentifier=").append(isIdentifier).append(", ");
		if (isNullable != null)
			builder.append("isNullable=").append(isNullable).append(", ");
		if (isOptional != null)
			builder.append("isOptional=").append(isOptional).append(", ");
		if (midstSuperModelType != null)
			builder.append("midstSuperModelType=").append(midstSuperModelType).append(", ");
		if (modelSpecificType != null)
			builder.append("modelSpecificType=").append(modelSpecificType).append(", ");
		builder.append("numberOfDistinctValues=").append(numberOfDistinctValues).append("]");
		return builder.toString();
	}

	/**
	 * @return the maxValueSize
	 */
	public int getMaxValueSize() {
		return maxValueSize;
	}

	/**
	 * @param maxValueSize the maxValueSize to set
	 */
	public void setMaxValueSize(int maxValueSize) {
		this.maxValueSize = maxValueSize;
	}



	//-----------------------instancesSuperLexicals-----------------
	
	public List<String> getInstances() {
		return instances;
	}

	public void setInstances(List<String> instances) {
		this.instances = instances;
	}
	

	// ********************** Accessor Methods ********************** //	
	//TODO make sure superlexical can only belong to either a superrelationship or a superabstract
	//TODO make sure the mappings between midst supermodel types, canonical model types and model specific types is ok

}