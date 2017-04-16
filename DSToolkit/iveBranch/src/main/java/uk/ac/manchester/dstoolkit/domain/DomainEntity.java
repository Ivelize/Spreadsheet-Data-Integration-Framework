package uk.ac.manchester.dstoolkit.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.log4j.Logger;

/**
 * @author chedeler
 *
 */
@MappedSuperclass
public abstract class DomainEntity implements Serializable {

	//TODO check toString, equal and hashCode methods in all domain entities

	/**
	 * 
	 */
	private static final long serialVersionUID = -6818194569951250940L;

	protected static Logger log = Logger.getLogger(DomainEntity.class);

	@Id
	@GeneratedValue
	@Column(name = "ID")
	protected Long id = null;

	@Version
	@Column(name = "OBJ_VERSION")
	protected int version = 0;

	public DomainEntity() {
	}

	//-----------------------id-----------------

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	//-----------------------version-----------------

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	@SuppressWarnings("unused")
	private void setVersion(int version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DomainEntity [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		builder.append("version=").append(version).append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		//TODO take version into account?
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DomainEntity other = (DomainEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
