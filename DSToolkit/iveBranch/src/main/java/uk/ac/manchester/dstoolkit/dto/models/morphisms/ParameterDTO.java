package uk.ac.manchester.dstoolkit.dto.models.morphisms;

import java.util.ArrayList;
import java.util.List;

public class ParameterDTO {

	private Long id;
	private String name;
	private String value;
	private String direction;
	private List<Long> appliedTo = new ArrayList<Long>();

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
	}

	/**
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * @return the appliedTo
	 */
	public List<Long> getAppliedTo() {
		return appliedTo;
	}

	public void addAppliedTo(Long id) {
		this.appliedTo.add(id);
	}

	/**
	 * @param appliedTo the appliedTo to set
	 */
	public void setAppliedTo(List<Long> appliedTo) {
		this.appliedTo = appliedTo;
	}

}
