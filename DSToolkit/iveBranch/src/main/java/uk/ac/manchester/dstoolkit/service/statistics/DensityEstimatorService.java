package uk.ac.manchester.dstoolkit.service.statistics;

import uk.ac.manchester.dstoolkit.domain.models.statistics.DensityEstimator;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/***
 * Interface for the DensityEstimatorServiceImpl Class at the service layer
 * 
 * @author klitos
 *
 */

public interface DensityEstimatorService extends GenericEntityService<DensityEstimator, Long> {

	public void addDensityEstimator(DensityEstimator densityEstimator);

	public void deleteDensityEstimator(Long densityEstimatorId);
	
	public DensityEstimator findDensityEstimator(Long densityEstimatorId);	

}