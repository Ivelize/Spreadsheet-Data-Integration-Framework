package uk.ac.manchester.dstoolkit.dto.models.morphisms;

import java.util.ArrayList;
import java.util.List;

public class MorphismDTO {

	private Long id;
	private List<Long> constructSet1 = new ArrayList<Long>();
	private List<Long> constructSet2 = new ArrayList<Long>();

	private MorphismDTO parentMorphism;
	private List<MorphismDTO> childMorphisms = new ArrayList<MorphismDTO>();

	private List<ParameterDTO> parameters = new ArrayList<ParameterDTO>();

	//mapping
	private String query1String;
	private String query2String;
	private double precision = -1;
	private double recall = -1;

	//schematic correspondence
	private String name;
	private String shortName;
	private String correspondenceType;
	private String description;

	//match
	private String score;

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
	 * @return the constructSet1
	 */
	public List<Long> getConstructSet1() {
		return constructSet1;
	}

	public void addConstructSet1(Long id) {
		this.constructSet1.add(id);
	}

	/**
	 * @param constructSet1 the constructSet1 to set
	 */
	public void setConstructSet1(List<Long> constructSet1) {
		this.constructSet1 = constructSet1;
	}

	/**
	 * @return the constructSet2
	 */
	public List<Long> getConstructSet2() {
		return constructSet2;
	}

	public void addConstructSet2(Long id) {
		this.constructSet2.add(id);
	}

	/**
	 * @param constructSet2 the constructSet2 to set
	 */
	public void setConstructSet2(List<Long> constructSet2) {
		this.constructSet2 = constructSet2;
	}

	/**
	 * @return the parentMorphism
	 */
	public MorphismDTO getParentMorphism() {
		return parentMorphism;
	}

	/**
	 * @param parentMorphism the parentMorphism to set
	 */
	public void setParentMorphism(MorphismDTO parentMorphism) {
		this.parentMorphism = parentMorphism;
	}

	/**
	 * @return the childMorphisms
	 */
	public List<MorphismDTO> getChildMorphisms() {
		return childMorphisms;
	}

	public void addChildMorphism(MorphismDTO childMorphism) {
		this.childMorphisms.add(childMorphism);
	}

	/**
	 * @param childMorphisms the childMorphisms to set
	 */
	public void setChildMorphisms(List<MorphismDTO> childMorphisms) {
		this.childMorphisms = childMorphisms;
	}

	/**
	 * @return the parameters
	 */
	public List<ParameterDTO> getParameters() {
		return parameters;
	}

	public void addParameter(ParameterDTO parameter) {
		this.parameters.add(parameter);
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<ParameterDTO> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @return the query1String
	 */
	public String getQuery1String() {
		return query1String;
	}

	/**
	 * @param query1String the query1String to set
	 */
	public void setQuery1String(String query1String) {
		this.query1String = query1String;
	}

	/**
	 * @return the query2String
	 */
	public String getQuery2String() {
		return query2String;
	}

	/**
	 * @param query2String the query2String to set
	 */
	public void setQuery2String(String query2String) {
		this.query2String = query2String;
	}

	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}

	/**
	 * @param recall the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
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
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	/**
	 * @return the correspondenceType
	 */
	public String getCorrespondenceType() {
		return correspondenceType;
	}

	/**
	 * @param correspondenceType the correspondenceType to set
	 */
	public void setCorrespondenceType(String correspondenceType) {
		this.correspondenceType = correspondenceType;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the score
	 */
	public String getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(String score) {
		this.score = score;
	}

}
