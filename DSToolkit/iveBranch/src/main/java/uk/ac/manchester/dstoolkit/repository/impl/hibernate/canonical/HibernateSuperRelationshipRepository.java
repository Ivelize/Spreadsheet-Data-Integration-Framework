package uk.ac.manchester.dstoolkit.repository.impl.hibernate.canonical;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "superRelationshipRepository")
public class HibernateSuperRelationshipRepository extends HibernateGenericRepository<SuperRelationship, Long> implements SuperRelationshipRepository {

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperRelationshipRepository#getSuperRelationshipByNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@Transactional(readOnly = true)
	public SuperRelationship getSuperRelationshipByNameInSchemaWithName(String superRelationshipName, String schemaName) {
		Query query = em.createQuery("select sr from SuperRelationship sr where sr.name = :name and sr.schema.name = :schemaName");
		query.setParameter("name", superRelationshipName).setParameter("schemaName", schemaName);
		try {
			return (SuperRelationship) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperRelationshipRepository#getAllSuperRelationshipsInSchemaWithName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperRelationship> getAllSuperRelationshipsInSchemaWithName(String schemaName) {
		Query query = em.createQuery("select sr from SuperRelationship sr where sr.schema.name = :schemaName");
		query.setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperRelationshipRepository#getAllSuperLexicalsOfSuperRelationship(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsOfSuperRelationship(Long superRelationshipId) {
		Query query = em.createQuery("select sl from SuperLexical sl where sl.parentSuperRelationship.id = :superRelationshipId");
		query.setParameter("superRelationshipId", superRelationshipId);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.SuperRelationshipRepository#getAllSuperLexicalsOfSuperRelationshipWithNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<SuperLexical> getAllSuperLexicalsOfSuperRelationshipWithNameInSchemaWithName(String superRelationshipName, String schemaName) {
		Query query = em
				.createQuery("select sl from SuperLexical sl where sl.parentSuperRelationship.name = :superRelationshipName and sl.schema.name = :schemaName");
		query.setParameter("name", superRelationshipName).setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
	}

}
