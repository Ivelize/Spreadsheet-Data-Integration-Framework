package uk.ac.manchester.dstoolkit.repository.annotation;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface AnnotationRepository extends GenericRepository<Annotation, Long> {

	public List<Annotation> getAnnotationsForModelManagementConstruct(ModelManagementConstruct construct);

	public List<Annotation> getAnnotationsForModelManagementConstructProvidedByUser(ModelManagementConstruct construct, User user);

	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTerm(ModelManagementConstruct construct, OntologyTerm ontologyTerm);

	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTermName(ModelManagementConstruct construct, String ontologyTermName);

	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTermProvidedByUser(ModelManagementConstruct construct,
			OntologyTerm ontologyTerm, User user);

	public List<Annotation> getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(ModelManagementConstruct construct,
			String ontologyTermName, User user);
}
