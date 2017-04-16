/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.query.queryresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.queryresults.ResultInstanceService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "resultInstanceService")
public class ResultInstanceServiceImpl extends GenericEntityServiceImpl<ResultInstance, Long> implements ResultInstanceService {

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultInstanceService#addResultInstance(uk.ac.manchester.dataspaces.domain.models.query.queryresults.ResultInstance)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addResultInstance(ResultInstance resultInstance) {
		resultInstanceRepository.save(resultInstance);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultInstanceService#deleteResultInstance(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void deleteResultInstance(Long resultInstanceId) {
		// TODO 
		resultInstanceRepository.delete(resultInstanceRepository.find(resultInstanceId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.ResultInstanceService#findResultInstance(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public ResultInstance findResultInstance(Long resultInstanceId) {
		return resultInstanceRepository.find(resultInstanceId);
	}

	/**
	 * @param resultInstanceRepository the resultInstanceRepository to set
	 */
	public void setResultInstanceRepository(ResultInstanceRepository resultInstanceRepository) {
		this.resultInstanceRepository = resultInstanceRepository;
	}

	/**
	 * @return the resultInstanceRepository
	 */
	public ResultInstanceRepository getResultInstanceRepository() {
		return resultInstanceRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<ResultInstance, Long> getRepository() {
		return resultInstanceRepository;
	}

}
