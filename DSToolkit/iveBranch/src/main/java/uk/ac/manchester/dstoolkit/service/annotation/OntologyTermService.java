package uk.ac.manchester.dstoolkit.service.annotation;

import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface OntologyTermService extends GenericEntityService<OntologyTerm, Long> {

	public OntologyTerm findOntologyTerm(Long annotationId);

	public void addOntologyTerm(OntologyTerm ontologyTerm);

	public void deleteOntologyTerm(Long ontologyTermId);
}
