package uk.ac.manchester.dstoolkit.domain.models.canonical;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;

/**
 * @author chedeler
 *
 */
@Entity
@Table(name = "PARTICIPATION_OF_CMC_IN_SUPER_RELATIONSHIP")
public class ParticipationOfCMCInSuperRelationship extends ModelManagementConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1829353672519506937L;

	/*
	public static class EmbId implements Serializable {

		private static final long serialVersionUID = -4645384242636385218L;

		@Column(name = "SUPER_RELATIONSHIP_ID", nullable = true)
		private Long superRelationshipId;

		@Column(name = "SUPER_ABSTRACT_ID", nullable = true)
		private Long superAbstractId;

		public EmbId() {
		}

		public EmbId(Long superRelationshipId, Long superAbstractId) {
			this.superRelationshipId = superRelationshipId;
			this.superAbstractId = superAbstractId;
		}

		@Override
		public boolean equals(Object o) {
			if (o != null && o instanceof EmbId) {
				EmbId that = (EmbId) o;
				return this.superRelationshipId.equals(that.superRelationshipId) && this.superAbstractId.equals(that.superAbstractId);
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return superRelationshipId.hashCode() + superAbstractId.hashCode();
		}
	}


	@EmbeddedId
	private EmbId embId = new EmbId();
	*/

	@Enumerated(EnumType.STRING)
	@Column(name = "ROLE_OF_CONSTRUCT_IN_SUPER_RELATIONSHIP")
	private SuperRelationshipRoleType role;

	@ManyToOne
	@JoinColumn(name = "SUPER_RELATIONSHIP_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_PARTICIPATION_SUPER_RELATIONSHIP_ID")
	private SuperRelationship superRelationship;

	@ManyToOne
	@JoinColumn(name = "CONSTRUCT_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_PARTICIPATION_CONSTRUCT_ID")
	private CanonicalModelConstruct canonicalModelConstruct;

	@ManyToMany(mappedBy = "specifiedSuperRelationships")
	private Set<SuperLexical> specifyingSuperLexicals = new HashSet<SuperLexical>();

	/**
	 * 
	 */
	public ParticipationOfCMCInSuperRelationship() {
		super();
	}

	/**
	 * @param superRelationship
	 * @param canonicalModelConstruct
	 */
	public ParticipationOfCMCInSuperRelationship(SuperRelationship superRelationship, CanonicalModelConstruct canonicalModelConstruct) {
		super();
		this.setSuperRelationship(superRelationship);
		this.setCanonicalModelConstruct(canonicalModelConstruct);

		//this.embId.superRelationshipId = superRelationship.getId();
		//this.embId.superAbstractId = superAbstract.getId();
	}

	/**
	 * @param role
	 * @param superRelationship
	 * @param canonicalModelConstruct
	 */
	public ParticipationOfCMCInSuperRelationship(SuperRelationshipRoleType role, SuperRelationship superRelationship,
			CanonicalModelConstruct canonicalModelConstruct) {
		this(superRelationship, canonicalModelConstruct);
		this.setRole(role);

		//this.embId.superRelationshipId = superRelationship.getId();
		//this.embId.superAbstractId = superAbstract.getId();
	}

	/**
	 * @param roleString
	 * @param superRelationship
	 * @param canonicalModelConstruct
	 */
	public ParticipationOfCMCInSuperRelationship(String roleString, SuperRelationship superRelationship,
			CanonicalModelConstruct canonicalModelConstruct) {
		//TODO these types need working on, names aren't ideal, should probably be type instead of class
		this(superRelationship, canonicalModelConstruct);
		if (roleString.equals("GeneralisationSuperType"))
			this.setRole(SuperRelationshipRoleType.SUPER_CLASS);
		else if (roleString.equals("GeneralisationSpecificType"))
			this.setRole(SuperRelationshipRoleType.SUB_CLASS);
		else if (roleString.equals("ReferenceRelationship"))
			this.role = SuperRelationshipRoleType.REFERENCING;
		//TODO check the referencing and referenced bit, may have to check for the two different ones here

		//this.embId.superRelationshipId = superRelationship.getId();
		//this.embId.superAbstractId = superAbstract.getId();
	}

	// ********************** Accessor Methods ********************** //	
	//TODO check I'm not loosing anything here, see ComponentOfForeignKey etc. in MIDST

	//-----------------------superRelationshipRoleType-----------------

	/**
	 * @return the role
	 */
	public SuperRelationshipRoleType getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(SuperRelationshipRoleType role) {
		this.role = role;
	}

	//-----------------------superRelationship-----------------

	/**
	 * @return the superRelationship
	 */
	public SuperRelationship getSuperRelationship() {
		return superRelationship;
	}

	/**
	 * @param superRelationship the superRelationship to set
	 */
	public void setSuperRelationship(SuperRelationship superRelationship) {
		if (this.superRelationship != null) {
			this.superRelationship.internalRemoveParticipationOfConstruct(this);
		}
		this.superRelationship = superRelationship;
		if (superRelationship != null) {
			superRelationship.internalAddParticipationOfConstruct(this);
		}
	}

	public void internalSetSuperRelationship(SuperRelationship superRelationship) {
		this.superRelationship = superRelationship;
	}

	//-----------------------canonicalModelConstruct-----------------

	/**
	 * @return the canonicalModelConstruct
	 */
	public CanonicalModelConstruct getCanonicalModelConstruct() {
		return canonicalModelConstruct;
	}

	/**
	 * @param canonicalModelConstruct the canonicalModelConstruct to set
	 */
	public void setCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		if (this.canonicalModelConstruct != null) {
			this.canonicalModelConstruct.internalRemoveParticipationInSuperRelationship(this);
		}
		this.canonicalModelConstruct = canonicalModelConstruct;
		if (canonicalModelConstruct != null) {
			canonicalModelConstruct.internalAddParticipationInSuperRelationship(this);
		}
	}

	public void internalSetCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		this.canonicalModelConstruct = canonicalModelConstruct;
	}

	//-----------------------specifyingSuperLexicals-----------------

	/**
	 * @return the specifyingSuperLexicals
	 */
	public Set<SuperLexical> getSpecifyingSuperLexicals() {
		//return Collections.unmodifiableSet(specifyingSuperLexicals);
		return specifyingSuperLexicals;
	}

	/**
	 * @param specifyingSuperLexicals the specifyingSuperLexicals to set
	 */
	public void setSpecifyingSuperLexicals(Set<SuperLexical> specifyingSuperLexicals) {
		this.specifyingSuperLexicals = specifyingSuperLexicals;
		for (SuperLexical specifyingSuperLexical : specifyingSuperLexicals) {
			specifyingSuperLexical.internalAddSpecifiedSuperRelationship(this);
		}
	}

	public void addSpecifyingSuperLexical(SuperLexical superLexical) {
		this.specifyingSuperLexicals.add(superLexical);
		superLexical.internalAddSpecifiedSuperRelationship(this);
	}

	public void internalAddSpecifyingSuperLexical(SuperLexical superLexical) {
		this.specifyingSuperLexicals.add(superLexical);
	}

	public void removeSpecifyingSuperLexical(SuperLexical superLexical) {
		this.specifyingSuperLexicals.remove(superLexical);
		superLexical.internalRemoveSpecifiedSuperRelationship(this);
	}

	public void internalRemoveSpeficyingSuperLexical(SuperLexical superLexical) {
		this.specifyingSuperLexicals.remove(superLexical);
	}

	/**
	 * @return the embId
	 */
	/*
	public EmbId getEmbId() {
		return embId;
	}
	*/

	/**
	 * @param embId the embId to set
	 */
	//@SuppressWarnings("unused")
	/*
	private void setEmbId(EmbId embId) {
		this.embId = embId;
	}
	*/
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((canonicalModelConstruct == null) ? 0 : canonicalModelConstruct.hashCode());
		result = prime * result + ((superRelationship == null) ? 0 : superRelationship.hashCode());
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
		ParticipationOfCMCInSuperRelationship other = (ParticipationOfCMCInSuperRelationship) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (canonicalModelConstruct == null) {
			if (other.canonicalModelConstruct != null)
				return false;
		} else if (!canonicalModelConstruct.equals(other.canonicalModelConstruct))
			return false;
		if (superRelationship == null) {
			if (other.superRelationship != null)
				return false;
		} else if (!superRelationship.equals(other.superRelationship))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParticipationOfSuperAbstractInSuperRelationship [");
		/*
		if (embId != null)
			builder.append("embId=").append(embId).append(", ");
		*/
		if (role != null)
			builder.append("role=").append(role);
		builder.append("]");
		return builder.toString();
	}

}