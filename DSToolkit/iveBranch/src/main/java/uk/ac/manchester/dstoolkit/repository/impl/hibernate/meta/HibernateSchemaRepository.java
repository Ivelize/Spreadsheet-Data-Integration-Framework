package uk.ac.manchester.dstoolkit.repository.impl.hibernate.meta;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "schemaRepository")
public class HibernateSchemaRepository extends HibernateGenericRepository<Schema, Long> implements SchemaRepository {

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.SchemaRepository#getSchemaByName(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public Schema getSchemaByName(String schemaName) {
		Query query = em.createQuery("select s from Schema s where s.name = :schemaName");
		query.setParameter("schemaName", schemaName);
		try {
			return (Schema)query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Schema> getAllSchemaExtracted() {
		Query query = em.createQuery("select s from Schema s where s.name like '%Extract%'");
		try {
			List<Schema> lstSchema = query.getResultList();
			return lstSchema;
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Schema getSchemaForDataSource(DataSource dataSource) {
		Query query = em.createQuery("select s from Schema s where s.dataSource.id = :dataSourceId");
		query.setParameter("dataSourceId", dataSource.getId());
		try {
			return (Schema) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.SchemaRepository#getAllConstructsInSchema(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsInSchema(Schema schema) {
		Query query = em.createQuery("select c from CanonicalModelConstruct c where c.schema.id = :schemaId");
		query.setParameter("schemaId", schema.getId());
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.SchemaRepository#getAllConstructsOfTypeInSchema(uk.ac.manchester.dataspaces.domain.models.canonical.ConstructType, uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsOfTypeInSchema(ConstructType constructType, Schema schema) {
		Query query = em.createQuery("select c from CanonicalModelConstruct c where c.typeOfConstruct = :constructType and c.schema.id = :schemaId");
		query.setParameter("constructType", constructType).setParameter("schemaId", schema.getId());
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.SchemaRepository#getAllSuperAbstractsInSchema(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperAbstract> getAllSuperAbstractsInSchema(Schema schema) {
		Query query = em.createQuery("select sa from SuperAbstract sa where sa.schema.id = :schemaId");
		query.setParameter("schemaId", schema.getId());
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.SchemaRepository#getAllSuperLexicalsInSchema(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsInSchema(Schema schema) {
		Query query = em.createQuery("select sl from SuperLexical sl where sl.schema.id = :schemaId");
		query.setParameter("schemaId", schema.getId());
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.meta.SchemaRepository#getAllSuperRelationshipsInSchema(uk.ac.manchester.dataspaces.domain.models.meta.Schema)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperRelationship> getAllSuperRelationshipsInSchema(Schema schema) {
		Query query = em.createQuery("select sr from SuperRelationship sr where sr.schema.id = :schemaId");
		query.setParameter("schemaId", schema.getId());
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

}
