package uk.ac.manchester.dstoolkit.repository.impl.hibernate.canonical;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "superAbstractRepository")
public class HibernateSuperAbstractRepository extends HibernateGenericRepository<SuperAbstract, Long> implements SuperAbstractRepository {

	static Logger logger = Logger.getLogger(HibernateSuperAbstractRepository.class);

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getAllSuperAbstractsWithName(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<SuperAbstract> getAllSuperAbstractsWithName(String superAbstractName) {
		logger.debug("in getAllSuperAbstractsWithName");
		logger.debug("superAbstractName: " + superAbstractName);
		Query query = em.createQuery("select sa from SuperAbstract sa where sa.name = :name");
		query.setParameter("name", superAbstractName);
		try {
			List<SuperAbstract> superAbstracts = query.getResultList();
			logger.debug("got superAbstracts: " + superAbstracts);
			return superAbstracts;
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getSuperAbstractByNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@Transactional(readOnly = true)
	public SuperAbstract getSuperAbstractByNameInSchemaWithName(String superAbstractName, String schemaName) {
		logger.debug("in getSuperAbstractByNameInSchemaWithName");
		logger.debug("superAbstractName: " + superAbstractName);
		logger.debug("schemaName: " + schemaName);
		Query query = em.createQuery("select sa from SuperAbstract sa where sa.name = :name and sa.schema.name = :schemaName");
		query.setParameter("name", superAbstractName).setParameter("schemaName", schemaName);
		try {
			SuperAbstract superAbstract = (SuperAbstract) query.getSingleResult();
			logger.debug("got superAbstract: " + superAbstract);
			return superAbstract;
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public List<SuperAbstract> getAllSuperAbstractsByNameInSchemaWithName(String superAbstractName, String schemaName) {
		logger.debug("in getSuperAbstractByNameInSchemaWithName");
		logger.debug("superAbstractName: " + superAbstractName);
		logger.debug("schemaName: " + schemaName);
		Query query = em.createQuery("select sa from SuperAbstract sa where sa.name = :name and sa.schema.name = :schemaName");
		query.setParameter("name", superAbstractName).setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
/*		} catch (NoResultException ex) {
			return null;*/
		} catch (Exception e){
			logger.debug("ruhai : " + e.toString());
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperAbstractRepository#getAllSuperAbstractsInSchemaWithName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperAbstract> getAllSuperAbstractsInSchemaWithName(String schemaName) {
		Query query = em.createQuery("select sa from SuperAbstract sa where sa.schema.name = :schemaName");
		query.setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

}
