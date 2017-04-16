package uk.ac.manchester.dstoolkit.domain.models.canonical;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add a new constructor for storing an extra Property to SuperAbstract.
 */

@Entity
@Table(name = "SUPER_ABSTRACTS")
public class SuperAbstract extends CanonicalModelConstruct {

	private static final long serialVersionUID = 657650743489133588L;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_ABSTRACT_MIDST_SUPER_MODEL_TYPE")
	private SuperAbstractMIDSTSuperModelType midstSuperModelType;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_ABSTRACT_MODEL_SPECIFIC_TYPE")
	private SuperAbstractModelSpecificType modelSpecificType;

	//for query optimiser
	@Column(name = "SUPER_ABSTRACT_CARDINALITY")
	private int cardinality;

	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "PARENT_SUPER_ABSTRACT_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SUPER_ABSTRACT_PARENT_SUPER_ABSTRACT_ID")
	private SuperAbstract parentSuperAbstract;

	@OneToMany(mappedBy = "parentSuperAbstract")
	//, fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	private Set<SuperAbstract> childSuperAbstracts = new LinkedHashSet<SuperAbstract>();

	@OneToMany(mappedBy = "parentSuperAbstract", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Set<SuperLexical> superLexicals = new LinkedHashSet<SuperLexical>();

	//@OneToMany(mappedBy = "superAbstract")
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
	//private Set<ParticipationOfSuperAbstractInSuperRelationship> participationInSuperRelationships = new LinkedHashSet<ParticipationOfSuperAbstractInSuperRelationship>();

	@Transient
	private String variableName;

	/**
	 * 
	 */
	public SuperAbstract() {
		super();
		this.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
	}
	
	/**
	 * RDF - Add new namespace property for SuperAbstract
	 *  
	 * @param name - name of this SuperAbstract
	 * @param namespace - URI prefix or URI
	 * @param schema
	 * @param modelSpecificType
	 */
	public SuperAbstract(String name, CanonicalModelProperty extraProperty, Schema schema, SuperAbstractModelSpecificType modelSpecificType) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
		this.setModelSpecificType(modelSpecificType);		
		this.addProperty(extraProperty);
	}

	public SuperAbstract(String name, Set<CanonicalModelProperty> propSet, Schema schema, SuperAbstractModelSpecificType modelSpecificType) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
		this.setModelSpecificType(modelSpecificType);		
		this.addProperties(propSet);
		//this.addProperty(new Property("namespace",namespace_prefix));
	}
	
	/**
	 * @param name
	 * @param schema
	 */
	public SuperAbstract(String name, Schema schema) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_ABSTRACT);
	}

	/**
	 * @param name
	 * @param schema
	 * @param midstSuperModelType
	 * @param modelSpecificType
	 */
	public SuperAbstract(String name, Schema schema, SuperAbstractMIDSTSuperModelType midstSuperModelType,
			SuperAbstractModelSpecificType modelSpecificType) {
		this(name, schema);
		this.setMidstSuperModelType(midstSuperModelType);
		this.setModelSpecificType(modelSpecificType);
	}

	//-----------------------midstSuperModelType-----------------

	/**
	 * @return the midstSuperModelType
	 */
	public SuperAbstractMIDSTSuperModelType getMidstSuperModelType() {
		return midstSuperModelType;
	}

	/**
	 * @param midstSuperModelType the midstSuperModelType to set
	 */
	public void setMidstSuperModelType(SuperAbstractMIDSTSuperModelType midstSuperModelType) {
		this.midstSuperModelType = midstSuperModelType;
	}

	//-----------------------modelSpecificType-----------------

	/**
	 * @return the modelSpecificType
	 */
	public SuperAbstractModelSpecificType getModelSpecificType() {
		return modelSpecificType;
	}

	/**
	 * @param modelSpecificType the modelSpecificType to set
	 */
	public void setModelSpecificType(SuperAbstractModelSpecificType modelSpecificType) {
		this.modelSpecificType = modelSpecificType;
	}
	
	//-----------------------cardinality-----------------

	/**
	 * @return the cardinality
	 */
	public int getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}

	//-----------------------parentSuperAbstract-----------------

	/**
	 * @return the parentSuperAbstract
	 */
	public SuperAbstract getParentSuperAbstract() {
		return parentSuperAbstract;
	}

	/**
	 * @param parentSuperAbstract the parentSuperAbstract to set
	 */
	public void setParentSuperAbstract(SuperAbstract parentSuperAbstract) {
		if (this.parentSuperAbstract != null) {
			this.parentSuperAbstract.internalRemoveChildSuperAbstract(this);
		}
		this.parentSuperAbstract = parentSuperAbstract;
		if (parentSuperAbstract != null) {
			parentSuperAbstract.internalAddChildSuperAbstract(this);
		}
	}

	public void internalSetParentSuperAbstract(SuperAbstract parentSuperAbstract) {
		this.parentSuperAbstract = parentSuperAbstract;
	}

	//-----------------------childSuperAbstracts-----------------

	/**
	 * @return the childSuperAbstracts
	 */
	public Set<SuperAbstract> getChildSuperAbstracts() {
		//return Collections.unmodifiableSet(childSuperAbstracts);
		return childSuperAbstracts;
	}

	/**
	 * @param childSuperAbstracts the childSuperAbstracts to set
	 */
	public void setChildSuperAbstracts(Set<SuperAbstract> childSuperAbstracts) {
		this.childSuperAbstracts = childSuperAbstracts;
		for (SuperAbstract childSuperAbstract : childSuperAbstracts) {
			childSuperAbstract.internalSetParentSuperAbstract(this);
		}
	}

	public void addChildSuperAbstract(SuperAbstract childSuperAbstract) {
		this.childSuperAbstracts.add(childSuperAbstract);
		childSuperAbstract.internalSetParentSuperAbstract(this);
	}

	public void internalAddChildSuperAbstract(SuperAbstract childSuperAbstract) {
		this.childSuperAbstracts.add(childSuperAbstract);
	}

	public void removeChildSuperAbstract(SuperAbstract childSuperAbstract) {
		this.childSuperAbstracts.remove(childSuperAbstract);
		childSuperAbstract.internalSetParentSuperAbstract(null);
	}

	public void internalRemoveChildSuperAbstract(SuperAbstract childSuperAbstract) {
		this.childSuperAbstracts.remove(childSuperAbstract);
	}

	//-----------------------superLexicals-----------------

	/**
	 * @return the superLexicals
	 */
	public Set<SuperLexical> getSuperLexicals() {
		//return Collections.unmodifiableSet(superLexicals);
		return superLexicals;
	}

	/**
	 * @param superLexicals the superLexicals to set
	 */
	public void setSuperLexicals(Set<SuperLexical> superLexicals) {
		this.superLexicals = superLexicals;
		for (SuperLexical superLexical : superLexicals) {
			superLexical.internalSetParentSuperAbstract(this);
		}
	}

	public void addSuperLexical(SuperLexical superLexical) {
		this.superLexicals.add(superLexical);
		superLexical.internalSetParentSuperAbstract(this);
	}

	public void internalAddSuperLexical(SuperLexical superLexical) {
		this.superLexicals.add(superLexical);
	}

	public void removeSuperLexical(SuperLexical superLexical) {
		this.superLexicals.remove(superLexical);
		superLexical.internalSetParentSuperAbstract(null);
	}

	public void internalRemoveSuperLexical(SuperLexical superLexical) {
		this.superLexicals.remove(superLexical);
	}

	//-----------------------participationInSuperRelationships-----------------

	/**
	 * @return the participationInSuperRelationships
	 */
	/*
	public Set<ParticipationOfSuperAbstractInSuperRelationship> getParticipationInSuperRelationships() {
		return Collections.unmodifiableSet(participationInSuperRelationships);
	}
	*/

	/**
	 * @param participationInSuperRelationships the participationInSuperRelationships to set
	 */
	/*
	public void setParticipationInSuperRelationships(Set<ParticipationOfSuperAbstractInSuperRelationship> participationInSuperRelationships) {
		this.participationInSuperRelationships = participationInSuperRelationships;
		for (ParticipationOfSuperAbstractInSuperRelationship participation : participationInSuperRelationships) {
			participation.internalSetSuperAbstract(this);
		}
	}

	public void addParticipationInSuperRelationship(ParticipationOfSuperAbstractInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.add(participationInSuperRelationship);
		participationInSuperRelationship.internalSetSuperAbstract(this);
	}

	public void internalAddParticipationInSuperRelationship(ParticipationOfSuperAbstractInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.add(participationInSuperRelationship);
	}

	public void removeParticipationInSuperRelationship(ParticipationOfSuperAbstractInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.remove(participationInSuperRelationship);
		participationInSuperRelationship.internalSetSuperAbstract(null);
	}

	public void internalRemoveParticipationInSuperRelationship(ParticipationOfSuperAbstractInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.remove(participationInSuperRelationship);
	}
	*/

	//-----------------------variableName-----------------

	/**
	 * @param variableName the variableName to set
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		SuperAbstract other = (SuperAbstract) obj;
		if (!other.getName().equals(other.getName()))
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
		builder.append("SuperAbstract [cardinality=").append(cardinality).append(", ");
		if (midstSuperModelType != null)
			builder.append("midstSuperModelType=").append(midstSuperModelType).append(", ");
		if (modelSpecificType != null)
			builder.append("modelSpecificType=").append(modelSpecificType);
		builder.append("]");
		return builder.toString();
	}

	//TODO make sure the mappings between midst supermodel types, canonical model types and model specific types is ok

}