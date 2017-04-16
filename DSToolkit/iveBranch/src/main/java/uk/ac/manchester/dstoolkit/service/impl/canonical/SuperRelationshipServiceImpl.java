/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.canonical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.service.canonical.SuperRelationshipService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "superRelationshipService")
public class SuperRelationshipServiceImpl extends GenericEntityServiceImpl<SuperRelationship, Long> implements SuperRelationshipService {

	@Autowired
	@Qualifier("superRelationshipRepository")
	private SuperRelationshipRepository superRelationshipRepository;

	@Autowired
	@Qualifier("superLexicalRepository")
	private SuperLexicalRepository superLexicalRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperRelationshipService#addSuperLexicalToSuperRelationship(uk.ac.manchester.dataspaces.domain.models.canonical.SuperRelationship, uk.ac.manchester.dataspaces.domain.models.canonical.SuperLexical)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addSuperLexicalToSuperRelationship(SuperRelationship superRelationship, SuperLexical superLexical) {
		superRelationship.addSuperLexical(superLexical);
		superRelationshipRepository.update(superRelationship);
		superLexicalRepository.update(superLexical);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperRelationshipService#addSuperRelationship(uk.ac.manchester.dataspaces.domain.models.canonical.SuperRelationship)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void addSuperRelationship(SuperRelationship superRelationship) {
		superRelationshipRepository.save(superRelationship);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperRelationshipService#deleteSuperRelationship(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteSuperRelationship(Long superRelationshipId) {
		// TODO doesn't take into account any associations with anything
		superRelationshipRepository.delete(superRelationshipRepository.find(superRelationshipId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperRelationshipService#findSuperRelationship(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public SuperRelationship findSuperRelationship(Long superRelationshipId) {
		return superRelationshipRepository.find(superRelationshipId);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperRelationshipService#removeSuperLexicalFromSuperRelationship(uk.ac.manchester.dataspaces.domain.models.canonical.SuperRelationship, uk.ac.manchester.dataspaces.domain.models.canonical.SuperLexical)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void removeSuperLexicalFromSuperRelationship(SuperRelationship superRelationship, SuperLexical superLexical) {
		superRelationship.removeSuperLexical(superLexical);
		superRelationshipRepository.update(superRelationship);
		superLexicalRepository.update(superLexical);
	}

	/**
	 * @param superRelationshipRepository the superRelationshipRepository to set
	 */
	public void setSuperRelationshipRepository(SuperRelationshipRepository superRelationshipRepository) {
		this.superRelationshipRepository = superRelationshipRepository;
	}

	/**
	 * @return the superRelationshipRepository
	 */
	public SuperRelationshipRepository getSuperRelationshipRepository() {
		return superRelationshipRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<SuperRelationship, Long> getRepository() {
		return superRelationshipRepository;
	}

}
