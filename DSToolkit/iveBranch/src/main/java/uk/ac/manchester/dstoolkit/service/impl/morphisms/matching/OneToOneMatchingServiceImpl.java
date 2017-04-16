/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.matching.OneToOneMatchingRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.OneToOneMatchingService;

/**
 * @author chedeler
 * @author klitos
 *
 */
//@Transactional(readOnly = true)
@Service(value = "oneToOneMatchingService")
public class OneToOneMatchingServiceImpl extends GenericEntityServiceImpl<OneToOneMatching, Long> implements OneToOneMatchingService {

	@Autowired
	@Qualifier("oneToOneMatchingRepository")
	private OneToOneMatchingRepository oneToOneMatchingRepository;

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.OneToOneMatchingService#addOneToOneMatching(uk.ac.manchester.dataspaces.domain.models.matching.OneToOneMatching)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addOneToOneMatching(OneToOneMatching oneToOneMatching) {
		oneToOneMatchingRepository.save(oneToOneMatching);
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.OneToOneMatchingService#deleteOneToOneMatching(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteOneToOneMatching(Long oneToOneMatchingId) {
		// TODO 
		oneToOneMatchingRepository.delete(oneToOneMatchingRepository.find(oneToOneMatchingId));
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.OneToOneMatchingService#findOneToOneMatching(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public OneToOneMatching findOneToOneMatching(Long oneToOneMatchingId) {
		return oneToOneMatchingRepository.find(oneToOneMatchingId);
	}

	/**
	 * @param oneToOneMatchingRepository the oneToOneMatchingRepository to set
	 */
	public void setOneToOneMatchingRepository(OneToOneMatchingRepository oneToOneMatchingRepository) {
		this.oneToOneMatchingRepository = oneToOneMatchingRepository;
	}

	/**
	 * @return the oneToOneMatchingRepository
	 */
	public OneToOneMatchingRepository getOneToOneMatchingRepository() {
		return oneToOneMatchingRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<OneToOneMatching, Long> getRepository() {
		return oneToOneMatchingRepository;
	}

}
