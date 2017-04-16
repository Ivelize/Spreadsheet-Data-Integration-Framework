package uk.ac.manchester.dstoolkit.repository.impl.hibernate.meta;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "dataSourceRepository")
public class HibernateDataSourceRepository extends HibernateGenericRepository<DataSource, Long> implements DataSourceRepository {

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.DataSourceRepository#getModelTypeOfDataSourceWithId(java.lang.Long)
	 */
	//@Transactional(readOnly = true)
	public ModelType getModelTypeOfDataSourceWithId(Long dataSourceId) {
		Query query = em.createQuery("select ds.schema.modelType from DataSource ds where ds.id = :dataSourceId");
		query.setParameter("dataSourceId", dataSourceId);
		try {
			return (ModelType) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.DataSourceRepository#getDataSourceWithId(java.lang.Long)
	 */
	//@Transactional(readOnly = true)
	public DataSource getDataSourceWithId(Long dataSourceId) {
		Query query = em.createQuery("select ds from DataSource ds where ds.id = :dataSourceId");
		query.setParameter("dataSourceId", dataSourceId);
		try {
			return (DataSource) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.DataSourceRepository#getDataSourceWithName(java.lang.String)
	 */
	//@Transactional(readOnly = true)
	public DataSource getDataSourceWithSchemaName(String schemaName) {
		Query query = em.createQuery("select ds from DataSource ds where ds.schema.name = :schemaName");
		query.setParameter("schemaName", schemaName);
		try {
			return (DataSource) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	//@Transactional(readOnly = true)
	public DataSource getDataSourceWithName(String dataSourceName) {
		Query query = em.createQuery("select ds from DataSource ds where ds.name = :dataSourceName");
		query.setParameter("dataSourceName", dataSourceName);
		try {
			return (DataSource) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

}
