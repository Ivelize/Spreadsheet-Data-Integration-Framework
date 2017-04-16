package uk.ac.manchester.dstoolkit.domain.annotation;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.user.User;

@Entity
@Table(name = "ANNOTATIONS")
public class Annotation extends DomainEntity {

	//TODO I'm currently not checking that the annotation value provided is of the correct type as specified in the corresponding ontologyTerm
	//TODO decide whether the same annotation should be used to annotate multiple constructs, e.g., all the tp(s) annotated by the same user, would probably make sense,
	//TODO but the annotated constructs might then be of different types, propagation currently assumes that the annotated constructs are of the same type to decide 
	//TODO which propagation method to call
	//TODO add constraining constructs, which could be of different types, e.g., the annotation of a canonicalModelConstruct might only apply to it when a 
	//TODO particular mapping was used to expand a particular query, but not otherwise
	//TODO should the annotation capture whether it's been inferred or should that be captured in the provenance trail? currently not captured at all

	/**
	 * 
	 */
	private static final long serialVersionUID = -8733411123015453421L;

	@Column(name = "ANNOTATION_VALUE", nullable = false)
	private String value;

	@Temporal(TemporalType.TIME)
	private Date timestamp;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ANNOTATION_ONTOLOGY_TERM_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_ANNOTATION_ONTOLOGY_TERM_ID")
	private OntologyTerm ontologyTerm;

	//this could be multiple constructs, e.g., a combination of resultField - resultvalue pairs
	//@ManyToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@JoinColumn(name = "ANNOTATION_CONSTRUCT_ID")
	//@org.hibernate.annotations.ForeignKey(name = "FK_ANNOTATION_CONSTRUCT_ID")
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "ANNOTATION_ANNOTATED_CONSTRUCTS", joinColumns = { @JoinColumn(name = "ANNOTATION_ID") }, inverseJoinColumns = { @JoinColumn(name = "ANNOTATED_CONSTRUCT_ID") })
	private Set<ModelManagementConstruct> annotatedModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();

	@ManyToMany
	@JoinTable(name = "ANNOTATION_CONSTRAINING_CONSTRUCTS", joinColumns = { @JoinColumn(name = "ANNOTATION_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONSTRAINING_CONSTRUCT_ID") })
	private Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ANNOTATION_USER_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_ANNOTATION_USER_ID")
	private User user;

	//TODO think about it: if there are multiple annotations for the same construct using the same ontology term - current version:
	//TODO (current version is only implemented in mappingServiceImpl for mappingAnnotation and queryResultServiceImpl for query result annotations)
	//TODO - if the values are different: store all of them
	//TODO - if the value are the same: update the timestamp of that value

	public Annotation() {
	}

	//TODO if OT has enumValues, make sure the correct terms are used
	public Annotation(String value, OntologyTerm ontologyTerm) {
		this.setValue(value);
		this.setOntologyTerm(ontologyTerm);
		timestamp = Calendar.getInstance().getTime();
	}

	public void updateTimestamp() {
		timestamp = Calendar.getInstance().getTime();
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
		timestamp = Calendar.getInstance().getTime();
	}

	//-----------------------timestamp-----------------

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	//-----------------------ontologyTerm-----------------

	/**
	 * @return the ontologyTerm
	 */
	public OntologyTerm getOntologyTerm() {
		return ontologyTerm;
	}

	/**
	 * @param ontologyTerm the ontologyTerm to set
	 */
	public void setOntologyTerm(OntologyTerm ontologyTerm) {
		/*
		if (this.ontologyTerm != null) {
			this.ontologyTerm.internalRemoveAnnotation(this);
		}
		*/
		this.ontologyTerm = ontologyTerm;
		/*
		if (ontologyTerm != null) {
			ontologyTerm.internalAddAnnotation(this);
		}
		*/
	}

	/*
	public void internalSetOntologyTerm(OntologyTerm ontologyTerm) {
		this.ontologyTerm = ontologyTerm;
	}
	*/

	//-----------------------annotatedModelManagementConstructs-----------------

	/**
	 * @return the annotatedModelManagementConstructs
	 */
	public Set<ModelManagementConstruct> getAnnotatedModelManagementConstructs() {
		//return Collections.unmodifiableSet(annotatedModelManagementConstructs);
		return annotatedModelManagementConstructs;
	}

	public void setAnnotatedModelManagementConstructs(Set<ModelManagementConstruct> annotatedModelManagementConstructs) {
		this.annotatedModelManagementConstructs = annotatedModelManagementConstructs;
		for (ModelManagementConstruct annotatedModelManagementConstruct : this.annotatedModelManagementConstructs) {
			annotatedModelManagementConstruct.internalAddAnnotation(this);
		}
	}

	/**
	 * @param annotatedModelManagementConstruct the annotatedModelManagementConstruct to add
	 */
	public void addAnnotatedModelManagementConstruct(ModelManagementConstruct annotatedModelManagementConstruct) {
		this.annotatedModelManagementConstructs.add(annotatedModelManagementConstruct);
		annotatedModelManagementConstruct.internalAddAnnotation(this);
		/*
		if (this.modelManagementConstruct != null) {
			this.modelManagementConstruct.internalRemoveAnnotation(this);
		}
		this.modelManagementConstruct = modelManagementConstruct;
		if (modelManagementConstruct != null) {
			modelManagementConstruct.internalAddAnnotation(this);
		}
		*/
	}

	public void internalAddAnnotatedModelManagementConstruct(ModelManagementConstruct annotatedModelManagementConstruct) {
		this.annotatedModelManagementConstructs.add(annotatedModelManagementConstruct);
	}

	public void removeAnnotatedModelManagementConstruct(ModelManagementConstruct annotatedModelManagementConstruct) {
		this.annotatedModelManagementConstructs.remove(annotatedModelManagementConstruct);
		annotatedModelManagementConstruct.internalRemoveAnnotation(this);
	}

	public void internalRemoveAnnotatedModelManagementConstruct(ModelManagementConstruct annotatedModelManagementConstruct) {
		this.annotatedModelManagementConstructs.remove(annotatedModelManagementConstruct);
	}

	/*
	public void internalSetModelManagementConstruct(ModelManagementConstruct modelManagementConstruct) {
		this.modelManagementConstruct = modelManagementConstruct;
	}
	*/

	//-----------------------constrainingModelManagementConstructs-----------------

	/**
	 * @return the constrainingModelManagementConstructs
	 */
	public Set<ModelManagementConstruct> getConstrainingModelManagementConstructs() {
		//return Collections.unmodifiableSet(constrainingModelManagementConstructs);
		return constrainingModelManagementConstructs;
	}

	public void setConstrainingModelManagementConstructs(Set<ModelManagementConstruct> constrainingModelManagementConstructs) {
		this.constrainingModelManagementConstructs = constrainingModelManagementConstructs;
	}

	/**
	 * @param constrainingModelManagementConstruct the constrainingModelManagementConstruct to add
	 */
	public void addConstrainingModelManagementConstruct(ModelManagementConstruct constrainingModelManagementConstruct) {
		this.constrainingModelManagementConstructs.add(constrainingModelManagementConstruct);
	}

	public void addAllConstrainingModelManagementConstructs(Set<ModelManagementConstruct> constrainingModelManagementConstructs) {
		this.constrainingModelManagementConstructs.addAll(constrainingModelManagementConstructs);
	}

	public void removeConstrainingModelManagementConstruct(ModelManagementConstruct constrainingModelManagementConstruct) {
		this.constrainingModelManagementConstructs.remove(constrainingModelManagementConstruct);
	}

	//-----------------------user-----------------

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Annotation [");
		builder.append("id: ").append(id).append(" ");
		if (annotatedModelManagementConstructs != null)
			builder.append("annotatedModelManagementConstructs=").append(annotatedModelManagementConstructs).append(", ");
		if (ontologyTerm != null)
			builder.append("ontologyTerm=").append(ontologyTerm).append(", ");
		if (timestamp != null)
			builder.append("timestamp=").append(timestamp).append(", ");
		if (user != null)
			builder.append("user=").append(user).append(", ");
		if (value != null)
			builder.append("value=").append(value).append(", ");
		if (id != null)
			builder.append("id=").append(id);
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
		result = prime * result + ((annotatedModelManagementConstructs == null) ? 0 : annotatedModelManagementConstructs.hashCode());
		result = prime * result + ((ontologyTerm == null) ? 0 : ontologyTerm.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Annotation))
			return false;
		Annotation other = (Annotation) obj;
		if (annotatedModelManagementConstructs == null) {
			if (other.annotatedModelManagementConstructs != null)
				return false;
		} else if (!annotatedModelManagementConstructs.equals(other.annotatedModelManagementConstructs))
			return false;
		if (ontologyTerm == null) {
			if (other.ontologyTerm != null)
				return false;
		} else if (!ontologyTerm.equals(other.ontologyTerm))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		/*
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
			*/
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/*
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotation other = (Annotation) obj;
		if (modelManagementConstructs == null) {
			if (other.modelManagementConstructs != null)
				return false;
		} else if (!modelManagementConstructs.equals(other.modelManagementConstructs))
			return false;
		if (ontologyTerm == null) {
			if (other.ontologyTerm != null)
				return false;
		} else if (!ontologyTerm.equals(other.ontologyTerm))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	*/

}
