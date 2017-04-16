package uk.ac.manchester.dstoolkit.domain.models.meta;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "SCHEMAS")
public class Schema extends ModelManagementConstruct {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8534870266981076529L;

	@ManyToMany(mappedBy = "schemas")
	private Set<Dataspace> dataspaces = new LinkedHashSet<Dataspace>();

	@Column(name = "SCHEMA_NAME", length = 255, nullable = false, updatable = false)
	// hack for now TODO, unique = true)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "MODEL_TYPE", updatable = false)
	private ModelType modelType;

	@OneToOne
	@JoinColumn(name = "DATA_SOURCE_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_SCHEMA_DATASOURCE_ID")
	private DataSource dataSource;

	@OneToMany(mappedBy = "schema", fetch = FetchType.EAGER)
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	private Set<CanonicalModelConstruct> canonicalModelConstructs = new LinkedHashSet<CanonicalModelConstruct>();
	
	@Transient
	private Map<Integer, SchematicCorrespondence> schematicCorrespondenceSchemaMerged;
	
	@Transient
	private Set<SchematicCorrespondence> schematicCorrespondenceMinimumModel;
	
	/**
	 * 
	 */
	public Schema() {
		super();
	}

	/**
	 * @param name
	 * @param modelType
	 */
	public Schema(String name, ModelType modelType) {
		super();
		this.setName(name);
		this.setModelType(modelType);
	}

	public Set<CanonicalModelConstruct> getSuperAbstractsAndSuperLexicals() {
		Set<CanonicalModelConstruct> constructsSet = new HashSet<CanonicalModelConstruct>();
		for (CanonicalModelConstruct construct : canonicalModelConstructs) {
			if (!(construct instanceof SuperRelationship)) {
				constructsSet.add(construct);
			}
		}
		return constructsSet;
	}

	public LinkedHashSet<CanonicalModelConstruct> getSuperAbstractsAndSuperLexicalsWithPredictableIterationOrder() {
		LinkedHashSet<CanonicalModelConstruct> constructsSet = new LinkedHashSet<CanonicalModelConstruct>();
		for (CanonicalModelConstruct construct : canonicalModelConstructs) {
			if (!(construct instanceof SuperRelationship)) {
				constructsSet.add(construct);
			}
		}
		return constructsSet;
	}

	public Set<SuperAbstract> getSuperAbstracts() {
		Set<SuperAbstract> superAbstractsSet = new HashSet<SuperAbstract>();
		for (CanonicalModelConstruct construct : canonicalModelConstructs) {
			if (construct instanceof SuperAbstract) {
				superAbstractsSet.add((SuperAbstract) construct);
			}
		}
		return superAbstractsSet;
	}

	public LinkedHashSet<SuperAbstract> getSuperAbstractsWithPredictableIterationOrder() {
		LinkedHashSet<SuperAbstract> superAbstractsSet = new LinkedHashSet<SuperAbstract>();
		for (CanonicalModelConstruct construct : canonicalModelConstructs) {
			if (construct instanceof SuperAbstract) {
				superAbstractsSet.add((SuperAbstract) construct);
			}
		}
		return superAbstractsSet;
	}

	//-----------------------dataspaces-----------------

	/**
	 * @return the dataspaces
	 */
	public Set<Dataspace> getDataspaces() {
		//return Collections.unmodifiableSet(dataspaces);
		return dataspaces;
	}

	/**
	 * @param dataspaces the dataspaces to set
	 */
	public void setDataspaces(Set<Dataspace> dataspaces) {
		this.dataspaces = dataspaces;
		for (Dataspace dataspace : dataspaces) {
			dataspace.internalAddSchema(this);
		}
	}

	public void addDataspace(Dataspace dataspace) {
		dataspaces.add(dataspace);
		dataspace.internalAddSchema(this);
	}

	public void internalAddDataspace(Dataspace dataspace) {
		dataspaces.add(dataspace);
	}

	public void removeDataspace(Dataspace dataspace) {
		dataspaces.remove(dataspace);
		dataspace.internalRemoveSchema(this);
	}

	public void internalRemoveDataspace(Dataspace dataspace) {
		dataspaces.remove(dataspace);
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

	//-----------------------modelType-----------------

	/**
	 * @return the modelType
	 */
	public ModelType getModelType() {
		return modelType;
	}

	/**
	 * @param modelType the modelType to set
	 */
	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}

	//-----------------------datasource-----------------

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		if (this.dataSource != null) {
			this.dataSource.internalSetSchema(null);
		}
		this.dataSource = dataSource;
		if (dataSource != null) {
			dataSource.internalSetSchema(this);
		}
	}

	public void internalSetDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	//-----------------------canonicalModelConstruct-----------------

	/**
	 * @return the canonicalModelConstructs
	 */
	public Set<CanonicalModelConstruct> getCanonicalModelConstructs() {
		//return Collections.unmodifiableSet(canonicalModelConstructs);
		return canonicalModelConstructs;
	}

	/**
	 * @param canonicalModelConstructs the canonicalModelConstructs to set
	 */
	public void setCanonicalModelConstructs(Set<CanonicalModelConstruct> canonicalModelConstructs) {
		this.canonicalModelConstructs = canonicalModelConstructs;
		for (CanonicalModelConstruct canonicalModelConstruct : canonicalModelConstructs) {
			canonicalModelConstruct.internalSetSchema(this);
		}
	}

	public void addCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		this.canonicalModelConstructs.add(canonicalModelConstruct);
		canonicalModelConstruct.internalSetSchema(this);
	}

	public void internalAddCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		this.canonicalModelConstructs.add(canonicalModelConstruct);
	}

	public void removeCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		this.canonicalModelConstructs.remove(canonicalModelConstruct);
		canonicalModelConstruct.internalSetSchema(null);
	}

	public void internalRemoveCanonicalModelConstruct(CanonicalModelConstruct canonicalModelConstruct) {
		this.canonicalModelConstructs.remove(canonicalModelConstruct);
	}
	
	public Map<Integer, SchematicCorrespondence> getSchematicCorrespondenceSchemaMerged() {
		return schematicCorrespondenceSchemaMerged;
	}

	public void setSchematicCorrespondenceSchemaMerged(
			Map<Integer, SchematicCorrespondence> schematicCorrespondenceSchemaMerged) {
		this.schematicCorrespondenceSchemaMerged = schematicCorrespondenceSchemaMerged;
	}

	public Set<SchematicCorrespondence> getSchematicCorrespondenceMinimumModel() {
		return schematicCorrespondenceMinimumModel;
	}

	public void setSchematicCorrespondenceMinimumModel(
			Set<SchematicCorrespondence> schematicCorrespondenceMinimumModel) {
		this.schematicCorrespondenceMinimumModel = schematicCorrespondenceMinimumModel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Schema [");
		if (dataSource != null)
			builder.append("dataSource=").append(dataSource.getId()).append(", ");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (modelType != null)
			builder.append("modelType=").append(modelType).append(", ");
		if (name != null)
			builder.append("name=").append(name).append(", ");
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
		result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
		result = prime * result + ((modelType == null) ? 0 : modelType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Schema other = (Schema) obj;
		if (dataSource == null) {
			if (other.dataSource != null)
				return false;
		} else if (!dataSource.equals(other.dataSource))
			return false;
		if (modelType == null) {
			if (other.modelType != null)
				return false;
		} else if (!modelType.equals(other.modelType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	

	/*
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Schema))
			return false;
		final Schema schema = (Schema) other;
		return (this.name.equals(schema.getName()));
	}

	public int hashCode() {
		int result;
		result = name.hashCode();
		return result;
	}
	*/

}