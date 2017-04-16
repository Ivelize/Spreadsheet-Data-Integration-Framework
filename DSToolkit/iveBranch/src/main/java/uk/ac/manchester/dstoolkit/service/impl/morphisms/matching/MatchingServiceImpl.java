/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.matching.MatchingRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "matchingService")
public class MatchingServiceImpl extends GenericEntityServiceImpl<Matching, Long> implements MatchingService {

	@Autowired
	@Qualifier("matchingRepository")
	private MatchingRepository matchingRepository;

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.MatchingService#addMatching(uk.ac.manchester.dataspaces.domain.models.matching.Matching)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addMatching(Matching matching) {
		matchingRepository.save(matching);
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.MatchingService#deleteMatching(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteMatching(Long matchingId) {
		// TODO
		matchingRepository.delete(matchingRepository.find(matchingId));
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.MatchingService#findMatching(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public Matching findMatching(Long matchingId) {
		return matchingRepository.find(matchingId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.matching.MatchingService#findAllMatching(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public java.util.List<Matching> findAllMatching() {
		return matchingRepository.findAll();
	}

	/**
	 * @param matchingRepository the matchingRepository to set
	 */
	public void setMatchingRepository(MatchingRepository matchingRepository) {
		this.matchingRepository = matchingRepository;
	}

	/**
	 * @return the matchingRepository
	 */
	public MatchingRepository getMatchingRepository() {
		return matchingRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<Matching, Long> getRepository() {
		return matchingRepository;
	}

}
