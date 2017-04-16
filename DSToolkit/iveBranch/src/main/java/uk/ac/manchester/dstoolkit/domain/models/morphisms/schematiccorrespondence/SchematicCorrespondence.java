package uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.Morphism;

/**
 * @author chedeler
 *
 */

/*
 * get all (primary/secondary) schematic correspondences (of a particular type) in which (collection of) construct(s) is involved
 * get all (primary/secondary) schematic correspondences (of a particular type) between two (collection of) construct(s)
 */

@Entity
@Table(name = "SCHEMATIC_CORRESPONDENCES")
public class SchematicCorrespondence extends Morphism {

	//TODO add score, might have to move it up into morphism
	//TODO sort out reconcilingExpression, should be removed here

	/**
	 * 
	 */
	private static final long serialVersionUID = -5418574351553353098L;

	//@ManyToOne
	//@JoinColumn(name = "SCHEMATIC_CORRESPONDENCE_DATASPACE_ID")
	//@org.hibernate.annotations.ForeignKey(name = "FK_SCHEMATIC_CORRESPONDENCE_DATASPACE_ID")
	//private Dataspace dataspace;

	@Column(name = "SCHEMATIC_CORRESPONDENCE_NAME", length = 255)
	private String name;

	@Column(name = "SCHEMATIC_CORRESPONDENCE_SHORT_NAME", length = 255)
	private String shortName;

	@Enumerated(EnumType.STRING)
	@Column(name = "CONSTRUCT_RELATED_SCHEMATIC_CORRESPONDENCE_TYPE")
	private ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType;

	@Enumerated(EnumType.STRING)
	@Column(name = "SCHEMATIC_CORRESPONDENCE_TYPE")
	private SchematicCorrespondenceType schematicCorrespondenceType;

	@Column(name = "SCHEMATIC_CORRESPONDENCE_DESCRIPTION", length = 255)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "PARAMETER_DIRECTION", nullable = false, updatable = false)
	private DirectionalityType direction;

	@OneToMany(mappedBy = "schematicCorrespondence", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Parameter> parameters = new LinkedHashSet<Parameter>();

	//@OneToMany(mappedBy = "schematicCorrespondence", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//, fetch = FetchType.EAGER
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//private Set<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct> applicationOfSchematicCorrespondenceToConstructs = new LinkedHashSet<ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct>();

	@ManyToOne
	@JoinColumn(name = "PARENT_SCHEMATIC_CORRESPONDENCE_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SCHEMATIC_CORRESPONDENCE_PARENT_CORRESPONDENCE_ID")
	private SchematicCorrespondence parentSchematicCorrespondence;

	@OneToMany(mappedBy = "parentSchematicCorrespondence", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private Set<SchematicCorrespondence> childSchematicCorrespondences = new LinkedHashSet<SchematicCorrespondence>();

	//@OneToOne(mappedBy = "schematicCorrespondence")
	//private ReconcilingExpression reconcilingExpression;

	// Begin Added by Lu

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "SCHEMATIC_CORRESPONDENCE_RECONCILING_EXPRESSIONS_1", joinColumns = { @JoinColumn(name = "SCHEMATIC_CORRESPONDENCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "RECONCILING_EXPRESSION_ID") })
	private Set<ReconcilingExpression> reconcilingExpressions1 = new LinkedHashSet<ReconcilingExpression>();

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "SCHEMATIC_CORRESPONDENCE_RECONCILING_EXPRESSIONS_2", joinColumns = { @JoinColumn(name = "SCHEMATIC_CORRESPONDENCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "RECONCILING_EXPRESSION_ID") })
	private Set<ReconcilingExpression> reconcilingExpressions2 = new LinkedHashSet<ReconcilingExpression>();


	// End Added by Lu

	//@ManyToMany(mappedBy = "schematicCorrespondences")
	//private Set<SchematicCorrespondencesToMappingProvenance> utilisedForMappings = new LinkedHashSet<SchematicCorrespondencesToMappingProvenance>();

	/**
	 * 
	 */
	public SchematicCorrespondence() {
		super();
	}

	/**
	 * @param name
	 * @param shortName
	 * @param schematicCorrespondenceType
	 */
	public SchematicCorrespondence(String name, String shortName, SchematicCorrespondenceType schematicCorrespondenceType) {
		this.setName(name);
		this.setShortName(shortName);
		this.setSchematicCorrespondenceType(schematicCorrespondenceType);
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

	//-----------------------dataspace-----------------

	/**
	 * @return the dataspace
	 */
	/*
	@Override
	public Dataspace getDataspace() {
		return dataspace;
	}
	*/

	/**
	 * @param dataspace the dataspace to set
	 */
	/*
	@Override
	public void setDataspace(Dataspace dataspace) {
		if (this.dataspace != null) {
			this.dataspace.internalRemoveCorrespondence(this);
		}
		this.dataspace = dataspace;
		if (dataspace != null) {
			dataspace.internalAddCorrespondence(this);
		}
	}

	public void internalSetDataspace(Dataspace dataspace) {
		this.dataspace = dataspace;
	}
	*/

	//-----------------------shortName-----------------

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	//-----------------------schematicCorrespondenceType-----------------

	/**
	 * @return the schematicCorrespondenceType
	 */
	public SchematicCorrespondenceType getSchematicCorrespondenceType() {
		return schematicCorrespondenceType;
	}

	/**
	 * @param schematicCorrespondenceType the schematicCorrespondenceType to set
	 */
	public void setSchematicCorrespondenceType(SchematicCorrespondenceType schematicCorrespondenceType) {
		this.schematicCorrespondenceType = schematicCorrespondenceType;
	}

	//-----------------------description-----------------

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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

	//-----------------------parameters-----------------

	/**
	 * @return the parameters
	 */
	public Set<Parameter> getParameters() {
		//return Collections.unmodifiableSet(parameters);
		return parameters;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Set<Parameter> parameters) {
		this.parameters = parameters;
		for (Parameter parameter : parameters) {
			parameter.internalSetSchematicCorrespondence(this);
		}
	}

	public void addParameter(Parameter parameter) {
		this.parameters.add(parameter);
		parameter.internalSetSchematicCorrespondence(this);
	}

	public void internalAddParameter(Parameter parameter) {
		this.parameters.add(parameter);
	}

	public void removeParameter(Parameter parameter) {
		this.parameters.remove(parameter);
		parameter.internalSetSchematicCorrespondence(null);
	}

	public void internalRemoveParameter(Parameter parameter) {
		this.parameters.remove(parameter);
	}

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
			application.internalSetSchematicCorrespondence(this);
		}
	}

	public void addApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.add(applicationOfSchematicCorrespondenceToConstruct);
		applicationOfSchematicCorrespondenceToConstruct.internalSetSchematicCorrespondence(this);
	}

	public void internalAddApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.add(applicationOfSchematicCorrespondenceToConstruct);
	}

	public void removeApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.remove(applicationOfSchematicCorrespondenceToConstruct);
		applicationOfSchematicCorrespondenceToConstruct.internalSetSchematicCorrespondence(null);
	}

	public void internalRemoveApplicationOfSchematicCorrespondenceToConstruct(
			ApplicationOfSchematicCorrespondenceToCanonicalModelConstruct applicationOfSchematicCorrespondenceToConstruct) {
		this.applicationOfSchematicCorrespondenceToConstructs.remove(applicationOfSchematicCorrespondenceToConstruct);
	}
	*/

	//-----------------------parentSchematicCorrespondence-----------------

	/**
	 * @return the parentSchematicCorrespondence
	 */
	public SchematicCorrespondence getParentSchematicCorrespondence() {
		return parentSchematicCorrespondence;
	}

	/**
	 * @param parentSchematicCorrespondence the parentSchematicCorrespondence to set
	 */
	public void setParentSchematicCorrespondence(SchematicCorrespondence parentSchematicCorrespondence) {
		if (this.parentSchematicCorrespondence != null) {
			this.parentSchematicCorrespondence.internalRemoveChildSchematicCorrespondence(this);
		}
		this.parentSchematicCorrespondence = parentSchematicCorrespondence;
		if (parentSchematicCorrespondence != null) {
			parentSchematicCorrespondence.internalAddChildSchematicCorrespondence(this);
		}
	}

	public void internalSetParentSchematicCorrespondence(SchematicCorrespondence parentSchematicCorrespondence) {
		this.parentSchematicCorrespondence = parentSchematicCorrespondence;
	}
	
	

	//-----------------------childSchematicCorrespondences-----------------

	/**
	 * @return the childSchematicCorrespondences
	 */
	public Set<SchematicCorrespondence> getChildSchematicCorrespondences() {
		//return Collections.unmodifiableSet(childSchematicCorrespondences);
		return childSchematicCorrespondences;
	}

	/**
	 * @param childSchematicCorrespondences the childSchematicCorrespondences to set
	 */
	public void setChildSchematicCorrespondences(Set<SchematicCorrespondence> childSchematicCorrespondences) {
		this.childSchematicCorrespondences = childSchematicCorrespondences;
		for (SchematicCorrespondence childSchematicCorrespondence : childSchematicCorrespondences) {
			childSchematicCorrespondence.internalSetParentSchematicCorrespondence(this);
		}
	}

	public void addChildSchematicCorrespondence(SchematicCorrespondence childSchematicCorrespondence) {
		this.childSchematicCorrespondences.add(childSchematicCorrespondence);
		childSchematicCorrespondence.internalSetParentSchematicCorrespondence(this);
	}

	public void addAllChildSchematicCorrespondence(Set<SchematicCorrespondence> childSchematicCorrespondences) {
		this.childSchematicCorrespondences.addAll(childSchematicCorrespondences);
		for (SchematicCorrespondence childSchematicCorrespondence : childSchematicCorrespondences)
			childSchematicCorrespondence.internalSetParentSchematicCorrespondence(this);
	}

	public void internalAddChildSchematicCorrespondence(SchematicCorrespondence childSchematicCorrespondence) {
		this.childSchematicCorrespondences.add(childSchematicCorrespondence);
	}

	public void removeChildSchematicCorrespondence(SchematicCorrespondence childSchematicCorrespondence) {
		this.childSchematicCorrespondences.remove(childSchematicCorrespondence);
		childSchematicCorrespondence.internalSetParentSchematicCorrespondence(null);
	}

	public void internalRemoveChildSchematicCorrespondence(SchematicCorrespondence childSchematicCorrespondence) {
		this.childSchematicCorrespondences.remove(childSchematicCorrespondence);
	}

	//-----------------------reconcilingExpressions1-----------------

	/**
	 * @return the reconcilingExpressions1
	 */
	public Set<ReconcilingExpression> getReconcilingExpressions1() {
		//return Collections.unmodifiableSet(reconcilingExpressions1);
		return reconcilingExpressions1;
	}

	/**
	 * @param reconcilingExpressions1 the reconcilingExpressions1 to set
	 */
	public void setReconcilingExpressions1(Set<ReconcilingExpression> reconcilingExpressions1) {
		this.reconcilingExpressions1 = reconcilingExpressions1;
	}

	// Begin added by Lu

	public void addReconcilingExpression1(ReconcilingExpression reconcilingExpression1) {
		this.reconcilingExpressions1.add(reconcilingExpression1);
	}

	// End added by Lu

	//-----------------------reconcilingExpressions2-----------------

	/**
	 * @return the reconcilingExpressions2
	 */
	public Set<ReconcilingExpression> getReconcilingExpressions2() {
		//return Collections.unmodifiableSet(reconcilingExpressions2);
		return reconcilingExpressions2;
	}

	/**
	 * @param reconcilingExpressions2 the reconcilingExpressions2 to set
	 */
	public void setReconcilingExpressions2(Set<ReconcilingExpression> reconcilingExpressions2) {
		this.reconcilingExpressions2 = reconcilingExpressions2;
	}

	// Begin added by Lu

	/**
	 * @param reconcilingExpression
	 */
	public void addReconcilingExpression2(ReconcilingExpression reconcilingExpression) {
		this.reconcilingExpressions2.add(reconcilingExpression);
	}

	// End added by Lu

	//-----------------------utilisedForMappings-----------------

	/**
	 * @return the utilisedForMappings
	 */
	/*
	public Set<SchematicCorrespondencesToMappingProvenance> getUtilisedForMappings() {
		return Collections.unmodifiableSet(utilisedForMappings);
	}
	*/

	/**
	 * @param utilisedForMappings the utilisedForMappings to set
	 */
	/*
	public void setUtilisedForMappings(Set<SchematicCorrespondencesToMappingProvenance> utilisedForMappings) {
		this.utilisedForMappings = utilisedForMappings;
	}

	public void addUtilisedForMappings(SchematicCorrespondencesToMappingProvenance utilisedForMappings) {
		this.utilisedForMappings.add(utilisedForMappings);
	}

	public void removeUtilisedForMappings(SchematicCorrespondencesToMappingProvenance utilisedForMappings) {
		this.utilisedForMappings.remove(utilisedForMappings);
	}
	*/

	//-----------------------constructRelatedSchematicCorrespondenceType-----------------

	//TODO constructRelatedSchematicCorrespondenceType could be inferred
	/**
	 * @return the constructRelatedSchematicCorrespondenceType
	 */
	public ConstructRelatedSchematicCorrespondenceType getConstructRelatedSchematicCorrespondenceType() {
		return constructRelatedSchematicCorrespondenceType;
	}

	/**
	 * @param constructRelatedSchematicCorrespondenceType the constructRelatedSchematicCorrespondenceType to set
	 */
	public void setConstructRelatedSchematicCorrespondenceType(ConstructRelatedSchematicCorrespondenceType constructRelatedSchematicCorrespondenceType) {
		this.constructRelatedSchematicCorrespondenceType = constructRelatedSchematicCorrespondenceType;
	}

	//-----------------------cardinalityType-----------------

	/**
	 * @return the cardinalityType
	 */
	/*
	public CardinalityType getCardinalityType() {
		return cardinalityType;
	}
	*/

	/**
	 * @param cardinalityType the cardinalityType to set
	 */
	/*
	public void setCardinalityType(CardinalityType cardinalityType) {
		this.cardinalityType = cardinalityType;
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SchematicCorrespondence [");
		//if (applicationOfSchematicCorrespondenceToConstructs != null)
		//	builder.append("applicationOfSchematicCorrespondenceToConstructs=").append(applicationOfSchematicCorrespondenceToConstructs).append(", ");
		//if (cardinalityType != null)
		//	builder.append("cardinalityType=").append(cardinalityType).append(", ");
		if (childSchematicCorrespondences != null)
			//builder.append("childSchematicCorrespondences=").append(childSchematicCorrespondences).append(", ");
		if (constructRelatedSchematicCorrespondenceType != null)
			builder.append("constructRelatedSchematicCorrespondenceType=").append(constructRelatedSchematicCorrespondenceType).append(", ");
		if (description != null)
			builder.append("description=").append(description).append(", ");
		if (direction != null)
			builder.append("direction=").append(direction).append(", ");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
		if (parameters != null)
			builder.append("parameters=").append(parameters).append(", ");
		if (parentSchematicCorrespondence != null)
			builder.append("parentSchematicCorrespondence=").append(parentSchematicCorrespondence).append(", ");
		if (reconcilingExpressions1 != null)
			builder.append("reconcilingExpressions1=").append(reconcilingExpressions1).append(", ");
		if (reconcilingExpressions2 != null)
			builder.append("reconcilingExpressions2=").append(reconcilingExpressions2).append(", ");
		if (schematicCorrespondenceType != null)
			builder.append("schematicCorrespondenceType=").append(schematicCorrespondenceType).append(", ");
		if (shortName != null)
			builder.append("shortName=").append(shortName).append(", ");
		//if (utilisedForMappings != null)
		//	builder.append("utilisedForMappings=").append(utilisedForMappings).append(", ");
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
		//result = prime * result
		//		+ ((applicationOfSchematicCorrespondenceToConstructs == null) ? 0 : applicationOfSchematicCorrespondenceToConstructs.hashCode());
		//result = prime * result + ((cardinalityType == null) ? 0 : cardinalityType.hashCode());
		//result = prime * result + ((childSchematicCorrespondences == null) ? 0 : childSchematicCorrespondences.hashCode());
		result = prime * result
				+ ((constructRelatedSchematicCorrespondenceType == null) ? 0 : constructRelatedSchematicCorrespondenceType.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((parentSchematicCorrespondence == null) ? 0 : parentSchematicCorrespondence.hashCode());
		result = prime * result + ((reconcilingExpressions1 == null) ? 0 : reconcilingExpressions1.hashCode());
		result = prime * result + ((reconcilingExpressions2 == null) ? 0 : reconcilingExpressions2.hashCode());
		result = prime * result + ((schematicCorrespondenceType == null) ? 0 : schematicCorrespondenceType.hashCode());
		result = prime * result + ((shortName == null) ? 0 : shortName.hashCode());
		//result = prime * result + ((utilisedForMappings == null) ? 0 : utilisedForMappings.hashCode());
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
		SchematicCorrespondence other = (SchematicCorrespondence) obj;
		//if (applicationOfSchematicCorrespondenceToConstructs == null) {
		//	if (other.applicationOfSchematicCorrespondenceToConstructs != null)
		//		return false;
		//} else if (!applicationOfSchematicCorrespondenceToConstructs.equals(other.applicationOfSchematicCorrespondenceToConstructs))
		//	return false;
		//if (cardinalityType == null) {
		//	if (other.cardinalityType != null)
		//		return false;
		//} else if (!cardinalityType.equals(other.cardinalityType))
		//	return false;
		if (childSchematicCorrespondences == null) {
			if (other.childSchematicCorrespondences != null)
				return false;
		} else if (!childSchematicCorrespondences.equals(other.childSchematicCorrespondences))
			return false;
		if (constructRelatedSchematicCorrespondenceType == null) {
			if (other.constructRelatedSchematicCorrespondenceType != null)
				return false;
		} else if (!constructRelatedSchematicCorrespondenceType.equals(other.constructRelatedSchematicCorrespondenceType))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (parentSchematicCorrespondence == null) {
			if (other.parentSchematicCorrespondence != null)
				return false;
		} else if (!parentSchematicCorrespondence.equals(other.parentSchematicCorrespondence))
			return false;
		if (reconcilingExpressions1 == null) {
			if (other.reconcilingExpressions1 != null)
				return false;
		} else if (!reconcilingExpressions1.equals(other.reconcilingExpressions1))
			return false;
		if (reconcilingExpressions2 == null) {
			if (other.reconcilingExpressions2 != null)
				return false;
		} else if (!reconcilingExpressions2.equals(other.reconcilingExpressions2))
			return false;
		if (schematicCorrespondenceType == null) {
			if (other.schematicCorrespondenceType != null)
				return false;
		} else if (!schematicCorrespondenceType.equals(other.schematicCorrespondenceType))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		//if (utilisedForMappings == null) {
		//	if (other.utilisedForMappings != null)
		//		return false;
		//} else if (!utilisedForMappings.equals(other.utilisedForMappings))
		//	return false;
		return true;
	}

	/*
	@Override
	public String toString() {
		return "SchematicCorrespondenceToConstruct [id=" + id + "name=" + name + ", Applications=" + applicationOfSchematicCorrespondenceToConstructs
				+ "]\n";
	}
	*/

	//TODO might not be the best option, think about alternatives
	/*
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof SchematicCorrespondence)) return false;

	    final SchematicCorrespondence schematicCorrespondence = (SchematicCorrespondence) o;

	    if (id != schematicCorrespondence.getId()) return false;
	   
	    return true;
	}

	public int hashCode() {
	    int result;
	    result = id.hashCode();
	    return result;
	}
	*/
}
