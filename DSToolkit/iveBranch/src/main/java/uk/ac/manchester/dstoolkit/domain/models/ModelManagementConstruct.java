package uk.ac.manchester.dstoolkit.domain.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;

/**
 * @author chedeler
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ModelManagementConstruct extends DomainEntity {

	//TODO tidy up stuff, sort out equals, toString and hash for all domain classes, use id from domainObject
	//test equals and hashCode

	/**
	 * 
	 */
	private static final long serialVersionUID = -7176200344390326963L;

	//TODO decide what to do with the dataspace - more than one?
	@ManyToOne
	@JoinColumn(name = "MMC_DATASPACE_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MMC_DATASPACE_ID")
	private Dataspace dataspace;

	@Column(name = "CONSTRUCT_IS_USER_SPECIFIED")
	private boolean isUserSpecified = false;

	@ManyToMany(mappedBy = "annotatedModelManagementConstructs")
	private List<Annotation> annotations = new ArrayList<Annotation>();

	public ModelManagementConstruct() {
		super();
	}

	//-----------------------dataspace-----------------

	/**
	 * @return the dataspace
	 */
	public Dataspace getDataspace() {
		return dataspace;
	}

	/**
	 * @param dataspace the dataspace to set
	 */
	public void setDataspace(Dataspace dataspace) {
		/*
		if (this.dataspace != null) {
			this.dataspace.internalRemoveCanonicalModelConstruct(this);
		}
		*/
		this.dataspace = dataspace;
		/*
		if (dataspace != null) {
			dataspace.internalAddCanonicalModelConstruct(this);
		}
		*/
	}

	//-----------------------annotations-----------------

	/**
	 * @return the annotations
	 */
	public List<Annotation> getAnnotations() {
		//return Collections.unmodifiableList(annotations);
		return annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
		for (Annotation annotation : annotations) {
			annotation.internalAddAnnotatedModelManagementConstruct(this);
		}
	}

	public void addAnnotation(Annotation annotation) {
		annotations.add(annotation);
		annotation.internalAddAnnotatedModelManagementConstruct(this);
	}

	public void internalAddAnnotation(Annotation annotation) {
		annotations.add(annotation);
	}

	public void removeAnnotation(Annotation annotation) {
		annotations.remove(annotation);
		annotation.internalRemoveAnnotatedModelManagementConstruct(this);
	}

	public void internalRemoveAnnotation(Annotation annotation) {
		annotations.remove(annotation);
	}

	//-----------------------isUserSpecified-----------------

	/**
	 * @param isUserSpecified the isUserSpecified to set
	 */
	public void setUserSpecified(boolean isUserSpecified) {
		this.isUserSpecified = isUserSpecified;
	}

	/**
	 * @return the isUserSpecified
	 */
	public boolean isUserSpecified() {
		return isUserSpecified;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + ((dataspace == null) ? 0 : dataspace.hashCode());
		result = prime * result + (isUserSpecified ? 1231 : 1237);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof ModelManagementConstruct))
			return false;
		ModelManagementConstruct other = (ModelManagementConstruct) obj;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (dataspace == null) {
			if (other.dataspace != null)
				return false;
		} else if (!dataspace.equals(other.dataspace))
			return false;
		if (isUserSpecified != other.isUserSpecified)
			return false;
		return true;
	}

}
