package uk.ac.manchester.dstoolkit.dto.models.canonical;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class EntityDTO {

	private Long id;
	private String name;
	private Set<EntityDTO> entities = new LinkedHashSet<EntityDTO>();

	private Set<AttributeDTO> attributes = new LinkedHashSet<AttributeDTO>();

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
	 * @param tableName the tableName to set
	 */
	public void setName(String tableName) {
		this.name = tableName;
	}

	/**
	 * @return the attributes
	 */
	public Set<AttributeDTO> getAttributes() {
		return Collections.unmodifiableSet(attributes);
	}

	public void addAttribute(AttributeDTO attribute) {
		this.attributes.add(attribute);
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Set<AttributeDTO> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(Set<EntityDTO> entities) {
		this.entities = entities;
	}

	/**
	 * @return the entities
	 */
	public Set<EntityDTO> getEntities() {
		return entities;
	}

}
