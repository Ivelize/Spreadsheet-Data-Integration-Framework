package uk.ac.manchester.dstoolkit.repository.impl.hibernate.canonical;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.repository.canonical.CanonicalModelConstructRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "canonicalModelConstructRepository")
public class HibernateCanonicalModelConstructRepository extends HibernateGenericRepository<CanonicalModelConstruct, Long> implements
		CanonicalModelConstructRepository {

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.CanonicalModelConstructRepository#getCanonicalModelConstructByNameInSchemaWithName(java.lang.String, java.lang.String)
	 */
	@Transactional(readOnly = true)
	public CanonicalModelConstruct getCanonicalModelConstructByNameInSchemaWithName(String constructName, String schemaName) {
		Query query = em.createQuery("select c from CanonicalModelConstruct c where c.name = :name and c.schema.name = :schemaName");
		query.setParameter("name", constructName).setParameter("schemaName", schemaName);
		try {
			return (CanonicalModelConstruct) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public CanonicalModelConstruct getCanonicalModelConstructOfTypeByNameInSchemaWithName(String constructName, ConstructType typeOfConstruct,
			String schemaName) {
		Query query = em
				.createQuery("select c from CanonicalModelConstruct c where c.name = :name and c.typeOfConstruct = :typeOfConstruct and c.schema.name = :schemaName");
		query.setParameter("name", constructName).setParameter("typeOfConstruct", typeOfConstruct).setParameter("schemaName", schemaName);
		try {
			return (CanonicalModelConstruct) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.repository.canonical.CanonicalModelConstructRepository#getAllCanonicalModelConstructsInSchemaWithName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsInSchemaWithName(String schemaName) {
		Query query = em.createQuery("select c from CanonicalModelConstruct c where c.schema.name = :schemaName");
		query.setParameter("schemaName", schemaName);
		try {
			return query.getResultList();
		} catch (NoResultException ex) {
			return null;
		}
		//return new HashSet<CanonicalModelConstruct>(list);
	}

	/*
	@NamedQueries({
	   @NamedQuery(
	    name = "findItemsByDescription",
	    query = "select i from Item i where i.description like :desc)",
	    hints = {
	     @QueryHint(name = "org.hibernate.comment", value = "My Comment"),
	     @QueryHint(name = "org.hibernate.fetchSize", value = "50"),
	     @QueryHint(name = "org.hibernate.flushMode", value = "never"),
	     @QueryHint(name = "org.hibernate.readOnly", value = "true"),
	     @QueryHint(name = "org.hibernate.timeout", value = "60")
	    })
	})
	*/

}
