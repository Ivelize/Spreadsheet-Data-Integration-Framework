package uk.ac.manchester.dstoolkit.repository.impl.hibernate.statistics;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.statistics.DensityEstimator;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.statistics.DensityEstimatorRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.statistics.kernels.KernelEstimatorType;

/**
 * Data Access Object (DAO) pattern is now handled by Spring/Hibernate
 * 
 * A repository can be used to get access to the persistent object during run time, just like memoery but
 * in this case we are using a db to make java objects persistent
 * 
 * @author klitos
 */
@Repository(value = "densityEstimatorRepository")
public class HibernateDensityEstimatorRepository extends HibernateGenericRepository<DensityEstimator, Long> implements DensityEstimatorRepository {
	
	static Logger logger = Logger.getLogger(HibernateDensityEstimatorRepository.class);
		
	@Transactional(readOnly = true)
	public DensityEstimator getDensityEstimatorByName(String estimatorName) {
		logger.debug("in getDensityEstimatorByName");
		logger.debug("estimatorName: " + estimatorName);
		Query query = em.createQuery("select d from DensityEstimator d where d.estimatorName = :name");
		query.setParameter("name", estimatorName);
		try {
			DensityEstimator kde = (DensityEstimator) query.getSingleResult();
			logger.debug("got densityEstimator: " + kde);
			return kde;
		} catch (NoResultException ex) {
			return null;
		}
	}	
		
	@Transactional(readOnly = true)
	public List<DensityEstimator> getAllDensityEstimatorsOfSpecificType(KernelEstimatorType estimatorType) {
		logger.debug("in getAllDensityEstimatorsOfSpecificType");
		Query query = em.createQuery("select d from DensityEstimator d where d.typeOfEstimator = :etype");
		query.setParameter("etype", estimatorType);

		try {
			List<DensityEstimator> kdes = query.getResultList();
			return kdes;
		} catch (NoResultException ex) {
			return null;
		}
	}

	
}//end class