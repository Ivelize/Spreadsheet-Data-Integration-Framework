/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.service.annotation.OntologyTermService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "ontologyTermService")
public class OntologyTermServiceImpl extends GenericEntityServiceImpl<OntologyTerm, Long> implements OntologyTermService {

	@Autowired
	@Qualifier("ontologyTermRepository")
	private OntologyTermRepository ontologyTermRepository;

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void addOntologyTerm(OntologyTerm ontologyTerm) {
		ontologyTermRepository.save(ontologyTerm);
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteOntologyTerm(Long ontologyTermId) {
		//TODO doesn't take into account any associations with anything
		//TODO might have to delete all the annotations first ... 
		ontologyTermRepository.delete(ontologyTermRepository.find(ontologyTermId));
	}

	@Transactional(readOnly = true)
	public OntologyTerm findOntologyTerm(Long ontologyTermId) {
		return ontologyTermRepository.find(ontologyTermId);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<OntologyTerm, Long> getRepository() {
		return ontologyTermRepository;
	}

}
