package uk.ac.manchester.dstoolkit.repository.statistics;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.statistics.DensityEstimator;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;

/**
 * This is an interface at the repository level, for HibernateDensityEstimatorRepository Class
 * @author klitos
 */
public interface DensityEstimatorRepository extends GenericRepository<DensityEstimator, Long> {

	public DensityEstimator getDensityEstimatorByName(String estimatorName);
	
	public List<DensityEstimator> getAllDensityEstimatorsOfSpecificType(KernelEstimatorType estimatorType);
	
}
