package uk.ac.manchester.dstoolkit.dto.annotation;

public class AnnotationDTO {

	private Long id;
	private Long annotatedConstructId;
	private String annotationTerm;
	private String annotationValue;

	/**
	 * @return the annotatedConstructId
	 */
	public Long getAnnotatedConstructId() {
		return annotatedConstructId;
	}

	/**
	 * @param annotatedConstructId the annotatedConstructId to set
	 */
	public void setAnnotatedConstructId(Long annotatedConstructId) {
		this.annotatedConstructId = annotatedConstructId;
	}

	/**
	 * @return the annotationTerm
	 */
	public String getAnnotationTerm() {
		return annotationTerm;
	}

	/**
	 * @param annotationTerm the annotationTerm to set
	 */
	public void setAnnotationTerm(String annotationTerm) {
		this.annotationTerm = annotationTerm;
	}

	/**
	 * @return the annotationValue
	 */
	public String getAnnotationValue() {
		return annotationValue;
	}

	/**
	 * @param annotationValue the annotationValue to set
	 */
	public void setAnnotationValue(String annotationValue) {
		this.annotationValue = annotationValue;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

}
