package uk.ac.manchester.dstoolkit.repository.annotation;

import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface OntologyTermRepository extends GenericRepository<OntologyTerm, Long> {

	public OntologyTerm getOntologyTermWithName(String name);
}
