package uk.ac.manchester.dstoolkit.dto.models.meta;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.manchester.dstoolkit.dto.models.canonical.EntityDTO;

public class SchemaDTO {

	private Long id;
	private String name;
	//private String datasourceName;
	//private String size;

	private Set<EntityDTO> entities = new LinkedHashSet<EntityDTO>();

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

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

	/**
	 * @return the datasourceName
	 */
	/*
	public String getDatasourceName() {
		return datasourceName;
	}
	*/

	/**
	 * @param datasourceName the datasourceName to set
	 */
	/*
	public void setDatasourceName(String datasourceName) {
		this.datasourceName = datasourceName;
	}
	*/

	/**
	 * @return the size
	 */
	/*
	public String getSize() {
		return size;
	}
	*/

	/**
	 * @param size the size to set
	 */
	/*
	public void setSize(String size) {
		this.size = size;
	}
	*/

	/**
	 * @return the entities
	 */
	public Set<EntityDTO> getEntities() {
		return Collections.unmodifiableSet(entities);
	}

	public void addEntity(EntityDTO entity) {
		this.entities.add(entity);
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(Set<EntityDTO> entities) {
		this.entities = entities;
	}

}
