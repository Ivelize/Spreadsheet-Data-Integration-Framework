package uk.ac.manchester.dstoolkit.domain.models.canonical;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;

/**
 * @author klitos
 *
 * Generic class for representing extra properties of the CanonicalModelConstruct
 * 
 * Revision (klitos):
 *  1. Added constructor.
 *  2. Add new columns to hold language and dataType for rdfs:label properties (Matching).
 *  3. Override equals() and hashCode().
 */

@Entity
@Table(name = "PROPERTIES")
public class CanonicalModelProperty extends DomainEntity {
	
	/*Universal version identifier for a Serializable class*/
	private static final long serialVersionUID = -116221645300251593L;

	static Logger logger = Logger.getLogger(CanonicalModelConstruct.class);
	
	@Column(name = "PROPERTY_NAME", nullable = false, updatable = false)
	private String name;
	
	@Column(name = "PROPERTY_VALUE", nullable = false, updatable = false)
	private String value;
	
	@Column(name = "PROPERTY_LANG", nullable = true, updatable = false)
	private String language;
	
	@Column(name = "PROPERTY_DATA_TYPE", nullable = true, updatable = false)
	private String dataType;
	
	@ManyToOne
	@JoinColumn(name = "PROPERTY_ID", nullable = false, updatable = false)
	@org.hibernate.annotations.ForeignKey(name = "FK_PROPERTY_CONSTRUCT_ID")
	private CanonicalModelConstruct propertyOf;

	/**
	 * Default constructor
	 */
	public CanonicalModelProperty() {
		super();
	}
	
	/**
	 * Constuctor
	 * 
	 * @param name
	 * @param schema
	 */
	public CanonicalModelProperty(String name, String value) {
		super();
		this.setName(name);
		this.setValue(value);
	}	
	
	public CanonicalModelProperty(String name, String value, String lang, String dType) {
		super();
		this.setName(name);
		this.setValue(value);
		this.setLanguage(lang);
		this.setDataType(dType);
	}
	
	
    /*----------- name ------------*/  
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
    /*----------- value ------------*/  
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	/*-------- language ------------*/
	
	public String getLanguage() {
		return this.language;
	}
	public void setLanguage(String lang) {
		this.language = lang;
	}
	
	/*-------- dataType -----------*/
	
	public String getDataType() {
		return this.dataType;
	}	
	public void setDataType(String dType) {
		this.dataType = dType;
	}	
	
    /*----------- CanonicalModel ------------*/  
	
	public CanonicalModelConstruct getPropertyOf() {
		return propertyOf;
	}
	
	/**
	 * 
	 * @param propertyOf
	 */
	
	public void setPropertyOf(CanonicalModelConstruct propertyOf) {
		this.propertyOf = propertyOf;
	}

	
	/*Overridden methods*/  
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((propertyOf == null) ? 0 : propertyOf.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CanonicalModelProperty other = (CanonicalModelProperty) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (propertyOf == null) {
			if (other.propertyOf != null)
				return false;
		} else if (!propertyOf.equals(other.propertyOf))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}//end class