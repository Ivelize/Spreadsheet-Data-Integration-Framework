package uk.ac.manchester.dstoolkit.repository.impl.hibernate.annotation;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.repository.annotation.OntologyTermRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "ontologyTermRepository")
public class HibernateOntologyTermRepository extends HibernateGenericRepository<OntologyTerm, Long> implements OntologyTermRepository {

	static Logger log = Logger.getLogger(HibernateOntologyTermRepository.class);

	@Transactional(readOnly = true)
	public OntologyTerm getOntologyTermWithName(String name) {
		Query query = em.createQuery("select o from OntologyTerm o where o.name = :name");
		query.setParameter("name", name);
		//query.setFlushMode(FlushModeType.COMMIT);
		try {
			return (OntologyTerm) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

}
