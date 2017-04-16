package uk.ac.manchester.dstoolkit.service.annotation;

import java.util.Map;
import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface AnnotationService extends GenericEntityService<Annotation, Long> {

	public Map<String, Double> calculatePrecisionRecallAndFMeasure(long numberOfTPs, long numberOfFPs, long numberOfFNs, double beta);

	public void annotate(String ontologyTermName, String annotationValue, Long modelManagementConstructId, Long parentConstructId,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user);

	public void annotate(String ontologyTermName, String annotationValue, ModelManagementConstruct modelManagementConstruct,
			ModelManagementConstruct parentConstruct, Set<ModelManagementConstruct> constrainingModelManagementConstructs,
			boolean duplicateAnnotationAllowed, User user);

	public void annotate(Annotation annotation, ModelManagementConstruct modelManagementConstruct, ModelManagementConstruct parentConstruct,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user);

	public void propagateAnnotation(Set<Annotation> annotationsToPropagate, Set<ModelManagementConstruct> constructsToPropagateAnnotationsTo,
			Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed, User user);

	public Annotation findAnnotation(Long annotationId);

	public void addAnnotation(Annotation annotation);

	public void deleteAnnotation(Long annotationId);
}
