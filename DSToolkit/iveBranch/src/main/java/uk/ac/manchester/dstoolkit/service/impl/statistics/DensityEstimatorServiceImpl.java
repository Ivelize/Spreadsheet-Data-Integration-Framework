package uk.ac.manchester.dstoolkit.service.impl.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.statistics.DensityEstimator;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.statistics.DensityEstimatorRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.statistics.DensityEstimatorService;

/**
 * This is the service Class for the Density Estimator Repository
 * 
 * @author klitos
 *
 */

@Service(value = "densityEstimatorService")
public class DensityEstimatorServiceImpl extends GenericEntityServiceImpl<DensityEstimator, Long> implements DensityEstimatorService {
	
	@Autowired
	@Qualifier("densityEstimatorRepository")	
	private DensityEstimatorRepository densityEstimatorRepository;

	@Transactional
	public void addDensityEstimator(DensityEstimator densityEstimator) {
		densityEstimatorRepository.save(densityEstimator);
	}
	
	@Transactional
	public void deleteDensityEstimator(Long densityEstimatorId) {
		// TODO 
		densityEstimatorRepository.delete(densityEstimatorRepository.find(densityEstimatorId));
	}
	
	@Transactional(readOnly = true)
	public DensityEstimator findDensityEstimator(Long densityEstimatorId) {
		return densityEstimatorRepository.find(densityEstimatorId);
	}
	
	public void setDensityEstimatorRepository(DensityEstimatorRepository densityEstimatorRepository) {
		this.densityEstimatorRepository = densityEstimatorRepository;
	}

	/**
	 * @return the oneToOneMatchingRepository
	 */
	public DensityEstimatorRepository getDensityEstimatorRepository() {
		return densityEstimatorRepository;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public GenericRepository<DensityEstimator, Long> getRepository() {
		return densityEstimatorRepository;
	}

	
	
}//end class
