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

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add a new constructor for storing an extra Property to SuperLexical.
 */

@Entity
@Table(name = "SUPER_RELATIONSHIPS")
public class SuperRelationship extends CanonicalModelConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3861253634148947525L;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_RELATIONSHIP_MIDST_SUPER_MODEL_TYPE")
	private SuperRelationshipMIDSTSuperModelType midstSuperModelType;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_RELATIONSHIP_MODEL_SPECIFIC_TYPE")
	private SuperRelationshipModelSpecificType modelSpecificType;

	@OneToMany(mappedBy = "parentSuperRelationship", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//, fetch = FetchType.EAGER
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private Set<SuperLexical> superLexicals = new LinkedHashSet<SuperLexical>();

	@OneToMany(mappedBy = "superRelationship", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private Set<ParticipationOfCMCInSuperRelationship> participationsOfConstructs = new LinkedHashSet<ParticipationOfCMCInSuperRelationship>();

	//TODO check this, this is already in the modelSpecificType
	@Enumerated(EnumType.STRING)
	@Column(name = "SUPER_RELATIONSHIP_TYPE")
	private SuperRelationshipType superRelationshipType;

	@ManyToOne
	@JoinColumn(name = "SUPER_RELATIONSHIP_GENERALISED_SUPER_ABSTRACT_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SUPER_RELATIONSHIP_GENERALISED_SUPER_ABSTRACT_ID")
	private SuperAbstract generalisedSuperAbstract;

	/**
	 * 
	 */
	public SuperRelationship() {
		this.setTypeOfConstruct(ConstructType.SUPER_RELATIONSHIP);
	}

	/**
	 * @param name
	 * @param schema
	 */
	public SuperRelationship(String name, Schema schema) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_RELATIONSHIP);
	}

	/***
	 * RDF
	 * @param name
	 * @param namespace
	 * @param schema
	 * @param modelSpecificType
	 */
	public SuperRelationship(String name, CanonicalModelProperty extraProperty, Schema schema,SuperRelationshipModelSpecificType modelSpecificType) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_RELATIONSHIP);
		this.setModelSpecificType(modelSpecificType);
		this.addProperty(extraProperty);
	}	
	
	public SuperRelationship(String name, Set<CanonicalModelProperty> propSet, Schema schema,SuperRelationshipModelSpecificType modelSpecificType) {
		super(name, schema);
		this.setTypeOfConstruct(ConstructType.SUPER_RELATIONSHIP);
		this.setModelSpecificType(modelSpecificType);
		this.addProperties(propSet);
	}	
	
	/**
	 * @param name
	 * @param schema
	 * @param midstSuperModelType
	 * @param modelSpecificType
	 */
	public SuperRelationship(String name, Schema schema, SuperRelationshipMIDSTSuperModelType midstSuperModelType,
			SuperRelationshipModelSpecificType modelSpecificType) {
		this(name, schema);
		this.setMidstSuperModelType(midstSuperModelType);
		this.setModelSpecificType(modelSpecificType);
	}

	//----------------------midstSuperModelType------------------

	/**
	 * @return the midstSuperModelType
	 */
	public SuperRelationshipMIDSTSuperModelType getMidstSuperModelType() {
		return midstSuperModelType;
	}

	/**
	 * @param midstSuperModelType the midstSuperModelType to set
	 */
	public void setMidstSuperModelType(SuperRelationshipMIDSTSuperModelType midstSuperModelType) {
		this.midstSuperModelType = midstSuperModelType;
	}

	//----------------------modelSpecificType------------------

	/**
	 * @return the modelSpecificType
	 */
	public SuperRelationshipModelSpecificType getModelSpecificType() {
		return modelSpecificType;
	}

	/**
	 * @param modelSpecificType the modelSpecificType to set
	 */
	public void setModelSpecificType(SuperRelationshipModelSpecificType modelSpecificType) {
		this.modelSpecificType = modelSpecificType;
	}

	//----------------------superLexicals------------------

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
			superLexical.internalSetParentSuperRelationship(this);
		}
	}

	public void addSuperLexical(SuperLexical superLexical) {
		this.superLexicals.add(superLexical);
		superLexical.internalSetParentSuperRelationship(this);
	}

	public void internalAddSuperLexical(SuperLexical superLexical) {
		this.superLexicals.add(superLexical);
	}

	public void removeSuperLexical(SuperLexical superLexical) {
		this.superLexicals.remove(superLexical);
		superLexical.internalSetParentSuperRelationship(null);
	}

	public void internalRemoveSuperLexical(SuperLexical superLexical) {
		this.superLexicals.remove(superLexical);
	}

	//----------------------participatingConstructs------------------

	/**
	 * @return the participatingConstructs
	 */
	public Set<ParticipationOfCMCInSuperRelationship> getParticipationsOfConstructs() {
		//return Collections.unmodifiableSet(participationsOfConstructs);
		return participationsOfConstructs;
	}

	/**
	 * @param participationsOfConstructs the participationsOfConstructs to set
	 */
	public void setParticipationsOfConstructs(Set<ParticipationOfCMCInSuperRelationship> participationsOfConstructs) {
		this.participationsOfConstructs = participationsOfConstructs;
		for (ParticipationOfCMCInSuperRelationship participation : participationsOfConstructs) {
			participation.internalSetSuperRelationship(this);
		}
	}

	public void addParticipationOfConstruct(ParticipationOfCMCInSuperRelationship participationOfConstruct) {
		this.participationsOfConstructs.add(participationOfConstruct);
		participationOfConstruct.internalSetSuperRelationship(this);
	}

	public void internalAddParticipationOfConstruct(ParticipationOfCMCInSuperRelationship participationOfConstruct) {
		this.participationsOfConstructs.add(participationOfConstruct);
	}

	public void removeParticipationOfConstruct(ParticipationOfCMCInSuperRelationship participationOfConstruct) {
		this.participationsOfConstructs.remove(participationOfConstruct);
		participationOfConstruct.internalSetSuperRelationship(null);
	}

	public void internalRemoveParticipationOfConstruct(ParticipationOfCMCInSuperRelationship participationOfConstruct) {
		this.participationsOfConstructs.remove(participationOfConstruct);
	}

	//----------------------superRelationshipType------------------

	/**
	 * @return the superRelationshipType
	 */
	public SuperRelationshipType getSuperRelationshipType() {
		return superRelationshipType;
	}

	/**
	 * @param superRelationshipType the superRelationshipType to set
	 */
	public void setSuperRelationshipType(SuperRelationshipType superRelationshipType) {
		this.superRelationshipType = superRelationshipType;
	}

	//----------------------generalisedSuperAbstract------------------

	/**
	 * @param generalisedSuperAbstract the generalisedSuperAbstract to set
	 */
	public void setGeneralisedSuperAbstract(SuperAbstract generalisedSuperAbstract) {
		this.generalisedSuperAbstract = generalisedSuperAbstract;
	}

	/**
	 * @return the generalisedSuperAbstract
	 */
	public SuperAbstract getGeneralisedSuperAbstract() {
		return generalisedSuperAbstract;
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
		//result = prime * result + ((superRelationshipType == null) ? 0 : superRelationshipType.hashCode());
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
		SuperRelationship other = (SuperRelationship) obj;
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
		/*
		if (superRelationshipType == null) {
			if (other.superRelationshipType != null)
				return false;
		} else if (!superRelationshipType.equals(other.superRelationshipType))
			return false;
		*/
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
		builder.append("SuperRelationship [");
		if (midstSuperModelType != null)
			builder.append("midstSuperModelType=").append(midstSuperModelType).append(", ");
		if (modelSpecificType != null)
			builder.append("modelSpecificType=").append(modelSpecificType);
		/*
		if (superRelationshipType != null)
			builder.append("superRelationshipType=").append(superRelationshipType).append(", ");
		*/
		builder.append("]");
		return builder.toString();
	}

	//TODO make sure the mappings between midst supermodel types, canonical model types and model specific types is ok

}