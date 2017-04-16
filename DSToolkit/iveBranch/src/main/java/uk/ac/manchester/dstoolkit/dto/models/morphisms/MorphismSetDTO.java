package uk.ac.manchester.dstoolkit.dto.models.morphisms;

import java.util.ArrayList;
import java.util.List;

public class MorphismSetDTO {

	private String id;
	private List<Long> constructSet1IDs = new ArrayList<Long>();
	private List<Long> constructSet2IDs = new ArrayList<Long>();
	private String type;
	private String name;
	private List<MorphismDTO> morphisms = new ArrayList<MorphismDTO>();

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the constructSet1IDs
	 */
	public List<Long> getConstructSet1IDs() {
		return constructSet1IDs;
	}

	public void addConstructSet1ID(Long id) {
		this.constructSet1IDs.add(id);
	}

	/**
	 * @param constructSet1IDs the constructSet1IDs to set
	 */
	public void setConstructSet1IDs(List<Long> constructSet1IDs) {
		this.constructSet1IDs = constructSet1IDs;
	}

	/**
	 * @return the constructSet2IDs
	 */
	public List<Long> getConstructSet2IDs() {
		return constructSet2IDs;
	}

	public void addConstructSet2ID(Long id) {
		this.constructSet2IDs.add(id);
	}

	/**
	 * @param constructSet2IDs the constructSet2IDs to set
	 */
	public void setConstructSet2IDs(List<Long> constructSet2IDs) {
		this.constructSet2IDs = constructSet2IDs;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the morphisms
	 */
	public List<MorphismDTO> getMorphisms() {
		return morphisms;
	}

	public void addMorphism(MorphismDTO morphism) {
		this.morphisms.add(morphism);
	}

	/**
	 * @param morphisms the morphisms to set
	 */
	public void setMorphisms(List<MorphismDTO> morphisms) {
		this.morphisms = morphisms;
	}

}
