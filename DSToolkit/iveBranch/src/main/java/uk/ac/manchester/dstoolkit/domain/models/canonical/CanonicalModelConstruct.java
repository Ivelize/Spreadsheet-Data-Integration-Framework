package uk.ac.manchester.dstoolkit.domain.models.canonical;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.Morphism;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *   1. Extra element added to the CanonicalModelConstruct to hold extra properties (i.e., namespace). 
 *  
 */

//TODO add indexes, constraint checks where necessary
//TODO add Annotation aspect
//TODO sort out and test equals and hash code 

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class CanonicalModelConstruct extends ModelManagementConstruct {

	private static final long serialVersionUID = -116117695300250563L;

	static Logger logger = Logger.getLogger(CanonicalModelConstruct.class);

	@Column(name = "CANONICAL_MODEL_CONSTRUCT_NAME", nullable = false)
	protected String name;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CANONICAL_MODEL_CONSTRUCT_TYPE")
	protected ConstructType typeOfConstruct;

	@OneToMany(mappedBy = "canonicalModelConstruct")
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Set<ParticipationOfCMCInSuperRelationship> participationInSuperRelationships = new LinkedHashSet<ParticipationOfCMCInSuperRelationship>();

	@Column(name = "CANONICAL_MODEL_CONSTRUCT_IS_VIRTUAL")
	protected boolean isVirtual;

	@Column(name = "CANONICAL_MODEL_CONSTRUCT_IS_GLOBAL")
	protected boolean isGlobal = true;

	@ManyToOne
	@JoinColumn(name = "CANONICAL_MODEL_CONSTRUCT_SCHEMA_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_CANONICAL_MODEL_CONSTRUCT_SCHEMA_ID")
	protected Schema schema;

	@OneToMany(mappedBy = "propertyOf", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	protected Set<CanonicalModelProperty> properties = new LinkedHashSet<CanonicalModelProperty>();
	
	//TODO think about placing provenance classes between canonical model and matchings, schematicCorrespondences, mappings

	@ManyToMany
	@JoinTable(name = "CONSTRUCTS_MORPHISMS", joinColumns = { @JoinColumn(name = "CONSTRUCT_ID") }, inverseJoinColumns = { @JoinColumn(name = "MORPHISM_ID") })
	private Set<Morphism> morphisms = new LinkedHashSet<Morphism>();

	//@ManyToMany(mappedBy = "canonicalModelConstructs")
	//protected Set<OneToOneMatching> oneToOneMatchings = new LinkedHashSet<OneToOneMatching>();

	//@OneToMany(mappedBy = "canonicalModelConstruct")
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//protected Set<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> applicationOfSchematicCorrespondenceToConstructs = new LinkedHashSet<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>();

	//@ManyToMany(mappedBy = "populatesConstructs")
	//protected Set<Mapping> populatedByMappings = new LinkedHashSet<Mapping>();

	/**
	 * 
	 */
	public CanonicalModelConstruct() {
		super();
	}

	
	/**
	 * 
	 * @param name
	 * @param schema
	 */
	public CanonicalModelConstruct(String name, Schema schema) {
		super();
		this.setName(name);
		this.setSchema(schema);
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


	
	//-----------------------typeOfConstruct-----------------

	/**
	 * @return the typeOfConstruct
	 */
	public ConstructType getTypeOfConstruct() {
		return typeOfConstruct;
	}

	/**
	 * @param typeOfConstruct the typeOfConstruct to set
	 */
	public void setTypeOfConstruct(ConstructType typeOfConstruct) {
		this.typeOfConstruct = typeOfConstruct;
	}

	//-----------------------participationInSuperRelationships-----------------

	/**
	 * @return the participationInSuperRelationships
	 */
	public Set<ParticipationOfCMCInSuperRelationship> getParticipationInSuperRelationships() {
		//return Collections.unmodifiableSet(participationInSuperRelationships);
		return participationInSuperRelationships;
	}

	/**
	 * @param participationInSuperRelationships the participationInSuperRelationships to set
	 */
	public void setParticipationInSuperRelationships(Set<ParticipationOfCMCInSuperRelationship> participationInSuperRelationships) {
		this.participationInSuperRelationships = participationInSuperRelationships;
		for (ParticipationOfCMCInSuperRelationship participation : participationInSuperRelationships) {
			participation.internalSetCanonicalModelConstruct(this);
		}
	}

	public void addParticipationInSuperRelationship(ParticipationOfCMCInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.add(participationInSuperRelationship);
		participationInSuperRelationship.internalSetCanonicalModelConstruct(this);
	}

	public void internalAddParticipationInSuperRelationship(ParticipationOfCMCInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.add(participationInSuperRelationship);
	}

	public void removeParticipationInSuperRelationship(ParticipationOfCMCInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.remove(participationInSuperRelationship);
		participationInSuperRelationship.internalSetCanonicalModelConstruct(null);
	}

	public void internalRemoveParticipationInSuperRelationship(ParticipationOfCMCInSuperRelationship participationInSuperRelationship) {
		this.participationInSuperRelationships.remove(participationInSuperRelationship);
	}

	//-----------------------isVirtual-----------------

	/**
	 * @return the isVirtual
	 */
	public boolean isVirtual() {
		return isVirtual;
	}

	/**
	 * @param isVirtual the isVirtual to set
	 */
	public void setVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	//-----------------------isGlobal-----------------

	/**
	 * @return the isGlobal
	 */
	public boolean isGlobal() {
		return isGlobal;
	}

	/**
	 * @param isGlobal the isGlobal to set
	 */
	public void setGlobal(boolean isGlobal) {
		this.isGlobal = isGlobal;
	}

	//-----------------------schema-----------------

	/**
	 * @return the schema
	 */
	public Schema getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(Schema schema) {
		if (this.schema != null) {
			this.schema.internalRemoveCanonicalModelConstruct(this);
		}
		this.schema = schema;
		if (schema != null) {
			schema.internalAddCanonicalModelConstruct(this);
		}
	}

	public void internalSetSchema(Schema schema) {
		this.schema = schema;
	}

	//-----------------------morphisms-----------------

	/**
	 * @return the morphisms
	 */
	public Set<Morphism> getMorphisms() {
		//return Collections.unmodifiableSet(morphisms);
		return morphisms;
	}

	/**
	 * @param morphisms the morphisms to set
	 */
	public void setMorphisms(Set<Morphism> morphisms) {
		this.morphisms = morphisms;
	}

	public void addMorphism(Morphism morphism) {
		logger.debug("in addMorphism: " + morphism);
		this.morphisms.add(morphism);
		logger.debug("added morphism");
	}

	public void removeMorphism(Morphism morphism) {
		this.morphisms.remove(morphism);
	}

	/**
	 * @return the oneToOneMatchings
	 */
	/*
	public Set<OneToOneMatching> getOneToOneMatchings() {
		return Collections.unmodifiableSet(oneToOneMatchings);
	}
	*/

	/**
	 * @param oneToOneMatchings the oneToOneMatchings to set
	 */
	/*
	public void setOneToOneMatchings(Set<OneToOneMatching> oneToOneMatchings) {
		this.oneToOneMatchings = oneToOneMatchings;
	}

	public void addOneToOneMatching(OneToOneMatching oneToOneMatching) {
		this.oneToOneMatchings.add(oneToOneMatching);
	}

	public void removeOneToOneMatching(OneToOneMatching oneToOneMatching) {
		this.oneToOneMatchings.remove(oneToOneMatching);
	}
	*/

	//-----------------------applicationOfSchematicCorrespondenceToConstructs-----------------

	/**
	 * @return the applicationOfSchematicCorrespondenceToConstructs
	 */
	/*
	public Set<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> getApplicationOfSchematicCorrespondenceToConstructs() {
		return Collections.unmodifiableSet(applicationOfSchematicCorrespondenceToConstructs);
	}
	*/

	/**
	 * @param applicationOfSchematicCorrespondenceToConstructs the applicationOfSchematicCorrespondenceToConstructs to set
	 */
	/*
	public void setApplicationOfSchematicCorrespondenceToConstructs(
			Set<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> applicationOfSchematicCorrespondenceToConstructs) {
		this.applicationOfSchematicCorrespondenceToConstructs = applicationOfSchematicCorrespondenceToConstructs;
		for (ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct application : applicationOfSchematicCorrespondenceToConstructs) {
			application.internalSetCanonicalModelConstruct(this);
		}
	}

	public void addApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.add(applicationOfSchematicCorrespondenceToConstruct);
		applicationOfSchematicCorrespondenceToConstruct.internalSetCanonicalModelConstruct(this);
	}

	public void internalAddApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.add(applicationOfSchematicCorrespondenceToConstruct);
	}

	public void removeApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.remove(applicationOfSchematicCorrespondenceToConstruct);
		applicationOfSchematicCorrespondenceToConstruct.internalSetCanonicalModelConstruct(null);
	}

	public void internalRemoveApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.remove(applicationOfSchematicCorrespondenceToConstruct);
	}
	*/

	//-----------------------populatedByMappings-----------------

	/**
	 * @return the populatedByMappings
	 */
	/*
	public Set<Mapping> getPopulatedByMappings() {
		return Collections.unmodifiableSet(populatedByMappings);
	}
	*/

	/**
	 * @param populatedByMappings the populatedByMappings to set
	 */
	/*
	public void setPopulatedByMappings(Set<Mapping> populatedByMappings) {
		this.populatedByMappings = populatedByMappings;
		for (Mapping mapping : populatedByMappings) {
			mapping.internalAddPopulatesConstruct(this);
		}
	}

	public void addPopulatedByMapping(Mapping populatedByMapping) {
		this.populatedByMappings.add(populatedByMapping);
		populatedByMapping.internalAddPopulatesConstruct(this);
	}

	public void internalAddPopulatedByMapping(Mapping populatedByMapping) {
		this.populatedByMappings.add(populatedByMapping);
	}

	public void removePopulatedByMapping(Mapping populatedByMapping) {
		this.populatedByMappings.remove(populatedByMapping);
		populatedByMapping.internalRemovePopulatesConstruct(this);
	}

	public void internalRemovePopulatedByMapping(Mapping populatedByMapping) {
		this.populatedByMappings.remove(populatedByMapping);
	}
	*/

	/* (non-Javadoc)
     * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isVirtual ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeOfConstruct == null) ? 0 : typeOfConstruct.hashCode());
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
		CanonicalModelConstruct other = (CanonicalModelConstruct) obj;
		if (isVirtual != other.isVirtual)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typeOfConstruct == null) {
			if (other.typeOfConstruct != null)
				return false;
		} else if (!typeOfConstruct.equals(other.typeOfConstruct))
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
	 * 
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("CanonicalModelConstruct [isVirtual=").append(isVirtual).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (typeOfConstruct != null)
			builder.append("typeOfConstruct=").append(typeOfConstruct);
		builder.append("]");
		return builder.toString();
	}


   /***
	* Extra properties for the CanonicalModelConstruct. Keep this methods generic to each model.
	* 
	* @return
	*/
	public Set<CanonicalModelProperty> getProperties() {
		return properties;
	}
	
	public CanonicalModelProperty getProperty(String propertyName) {
		for (CanonicalModelProperty property_element : properties) {
			if (property_element.getName().equals(propertyName)) {
			 return property_element;
		  }//end if
		}//end for
		return null;
	}	
	
	public void addProperty(CanonicalModelProperty property) {
		this.properties.add(property);
	}
	
	public void addProperties(Set<CanonicalModelProperty> propSet) {
		for (CanonicalModelProperty property : propSet) {
			this.properties.add(property);
		}//end for
	}

	public void setProperties(Set<CanonicalModelProperty> properties) {
		this.properties = properties;
	}

}//end class