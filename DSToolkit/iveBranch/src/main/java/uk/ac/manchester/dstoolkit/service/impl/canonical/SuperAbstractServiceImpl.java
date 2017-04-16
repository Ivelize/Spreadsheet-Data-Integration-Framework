/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.canonical;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.service.canonical.SuperAbstractService;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "superAbstractService")
public class SuperAbstractServiceImpl extends GenericEntityServiceImpl<SuperAbstract, Long> implements SuperAbstractService {

	@Autowired
	@Qualifier("superAbstractRepository")
	private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("superLexicalRepository")
	private SuperLexicalRepository superLexicalRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperAbstractService#addSuperAbstract(uk.ac.manchester.dataspaces.domain.models.canonical.SuperAbstract)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void addSuperAbstract(SuperAbstract superAbstract) {
		superAbstractRepository.save(superAbstract);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperAbstractService#addSuperLexicalToSuperAbstract(uk.ac.manchester.dataspaces.domain.models.canonical.SuperAbstract, uk.ac.manchester.dataspaces.domain.models.canonical.SuperLexical)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addSuperLexicalToSuperAbstract(SuperAbstract superAbstract, SuperLexical superLexical) {
		superAbstract.addSuperLexical(superLexical);
		superAbstractRepository.update(superAbstract);
		superLexicalRepository.update(superLexical);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperAbstractService#deleteSuperAbstract(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteSuperAbstract(Long superAbstractId) {
		// TODO doesn't take into account any associations with anything
		superAbstractRepository.delete(superAbstractRepository.find(superAbstractId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperAbstractService#findSuperAbstract(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public SuperAbstract findSuperAbstract(Long superAbstractId) {
		return superAbstractRepository.find(superAbstractId);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.canonical.SuperAbstractService#removeSuperLexicalFromSuperAbstract(uk.ac.manchester.dataspaces.domain.models.canonical.SuperAbstract, uk.ac.manchester.dataspaces.domain.models.canonical.SuperLexical)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void removeSuperLexicalFromSuperAbstract(SuperAbstract superAbstract, SuperLexical superLexical) {
		superAbstract.removeSuperLexical(superLexical);
		superAbstractRepository.update(superAbstract);
		superLexicalRepository.update(superLexical);
	}

	/**
	 * @param superAbstractRepository the superAbstractRepository to set
	 */
	public void setSuperAbstractRepository(SuperAbstractRepository superAbstractRepository) {
		this.superAbstractRepository = superAbstractRepository;
	}

	/**
	 * @return the superAbstractRepository
	 */
	public SuperAbstractRepository getSuperAbstractRepository() {
		return superAbstractRepository;
	}

	/**
	 * @return the superLexicalRepository
	 */
	public SuperLexicalRepository getSuperLexicalRepository() {
		return superLexicalRepository;
	}

	/**
	 * @param superLexicalRepository the superLexicalRepository to set
	 */
	public void setSuperLexicalRepository(SuperLexicalRepository superLexicalRepository) {
		this.superLexicalRepository = superLexicalRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<SuperAbstract, Long> getRepository() {
		return superAbstractRepository;
	}

}
