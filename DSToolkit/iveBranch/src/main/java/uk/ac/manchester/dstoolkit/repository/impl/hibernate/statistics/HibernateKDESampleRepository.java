package uk.ac.manchester.dstoolkit.repository.impl.hibernate.statistics;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.statistics.KDESample;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.statistics.KDESampleRepository;

/**
 * Data Access Object (DAO) pattern is now handled by Spring/Hibernate. 
 * DSToolkit stores the obects in DB at run time and then destroys them when execution finishes, just like memory
 * 
 * https://code.google.com/p/simplejpa/wiki/JPAQuery
 * 
 * @author klitos
 */
@Repository(value = "kdeSampleRepository")
public class HibernateKDESampleRepository extends HibernateGenericRepository<KDESample, Long> implements KDESampleRepository {
	
	static Logger logger = Logger.getLogger(HibernateKDESampleRepository.class);
	
	@Transactional(readOnly = true)
	public KDESample getKDESampleById(Long sampleId) {
		logger.debug("in getKDESampleById");
		Query query = em.createQuery("select s from KDESample s where s.id = :sampleId");
		query.setParameter("sampleId", sampleId);	
		
		try {
			KDESample sample = (KDESample) query.getSingleResult();
			return sample;
		} catch (NoResultException ex) {
			return null;
		}
	}	
	
	@Transactional(readOnly = true)
	public KDESample getKDESampleByIdandEstimatorName(Long sampleId, String estimatorName) {
		logger.debug("in getDensityEstimatorByName");
		logger.debug("estimatorName: " + estimatorName);
		Query query = em.createQuery("select s from KDESample s where s.id = :sampleId" +
																"and s.sampleOf.estimatorName = :estimatorName");
		query.setParameter("sampleId", sampleId).setParameter("estimatorName", estimatorName);	
				
		try {
			KDESample sample = (KDESample) query.getSingleResult();
			return sample;
		} catch (NoResultException ex) {
			return null;
		}
	}	
	
	@Transactional(readOnly = true)
	public KDESample getKDESampleByIdandEstimatorId(Long sampleId, Long estimatorId) {
		logger.debug("in getDensityEstimatorByName");
		logger.debug("estimatorId: " + estimatorId);
		Query query = em.createQuery("select s from KDESample s where s.id = :sampleId" +
																"and s.sampleOf.id = :estimatorId");
		query.setParameter("sampleId", sampleId).setParameter("estimatorId", estimatorId);	
		
		try {
			KDESample sample = (KDESample) query.getSingleResult();
			return sample;
		} catch (NoResultException ex) {
			return null;
		}
	}	
	
	@Transactional(readOnly = true)
	public List<KDESample> getAllSamplePointsOfDensityEstimatorWithName(String estimatorName) {
		List<KDESample> sampleDataPoints = new ArrayList<KDESample>();
		Query query = em.createQuery("select s from KDESample s where s.sampleOf.estimatorName = :estimatorName");	
		query.setParameter("estimatorName", estimatorName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}//end method
	
	/**
	 * Get all sample data points for
	 * 
	 * @param superAbstractId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<KDESample> getAllSamplePointsOfDensityEstimatorWithID(Long densityEstimatorId) {
		logger.debug("in getAllSamplePointsOfDensityEstimatorDAO");
		logger.debug("densityEstimatorId: " + densityEstimatorId);
		
		List<KDESample> sampleDataPoints = new ArrayList<KDESample>();
		Query query = em.createQuery("select s from KDESample s where s.sampleOf.id = :densityEstimatorId");
		query.setParameter("densityEstimatorId", densityEstimatorId);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}//end method	
	
	@Transactional(readOnly = true)
	public KDESample getKDESamplePointOfDensityEstimatorWithID(Long estimatorId) {
		logger.debug("in getKDESamplePointOfDensityEstimatorWithID");
		logger.debug("estimatorId: " + estimatorId);
		Query query = em.createQuery("select s from KDESample s where s.sampleOf.id = :estimatorId order by s.sampleOf");
		query.setParameter("estimatorId", estimatorId).setMaxResults(1);	
		
		try {
			KDESample sample = (KDESample) query.getSingleResult();
			return sample;
		} catch (NoResultException ex) {
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public Long countKDESamplePointsOfDensityEstimatorWithID(Long estimatorId) {
		logger.debug("in countKDESamplePointsOfDensityEstimatorWithID");
		logger.debug("estimatorId: " + estimatorId);
		Query query = em.createQuery("select count(s) from KDESample s where s.sampleOf.id = :estimatorId");
		query.setParameter("estimatorId", estimatorId);	
				
		try {
			List obs = query.getResultList();
			Long count = (Long) obs.get(0);
			return count;
		} catch (NoResultException ex) {
			return null;
		}
	}
	
}//end class
