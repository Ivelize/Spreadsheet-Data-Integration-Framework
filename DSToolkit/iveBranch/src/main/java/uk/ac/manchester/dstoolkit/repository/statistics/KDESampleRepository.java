package uk.ac.manchester.dstoolkit.repository.statistics;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.statistics.KDESample;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

public interface KDESampleRepository extends GenericRepository<KDESample, Long>  {

	public KDESample getKDESampleById(Long sampleId);
		
	public KDESample getKDESampleByIdandEstimatorName(Long sampleId, String estimatorName);
	
	public KDESample getKDESampleByIdandEstimatorId(Long sampleId, Long estimatorId);
	
	public List<KDESample> getAllSamplePointsOfDensityEstimatorWithName(String estimatorName);
	
	public List<KDESample> getAllSamplePointsOfDensityEstimatorWithID(Long densityEstimatorId);	

	public KDESample getKDESamplePointOfDensityEstimatorWithID(Long estimatorId);
	
	public Long countKDESamplePointsOfDensityEstimatorWithID(Long estimatorId);
}
