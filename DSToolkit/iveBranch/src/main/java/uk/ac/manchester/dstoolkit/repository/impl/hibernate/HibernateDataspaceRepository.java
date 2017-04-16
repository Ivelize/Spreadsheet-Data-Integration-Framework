package uk.ac.manchester.dstoolkit.repository.impl.hibernate;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "dataspaceRepository")
public class HibernateDataspaceRepository extends HibernateGenericRepository<Dataspace, Long> implements DataspaceRepository {

	@Transactional(readOnly = true)
	public List<String> getAllDataspaceNames() {
		Query query = em.createQuery("select d.dataspaceName from Dataspace d");
		try {
			List<String> resultList = query.getResultList();			
			return resultList;
		} catch (NoResultException ex) {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Dataspace getDataspaceWithName(String dataspaceName) {
		Query query = em.createQuery("select d from Dataspace d where d.dataspaceName = :dataspaceName");
		query.setParameter("dataspaceName", dataspaceName);
		try {
			return (Dataspace) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
	

}
