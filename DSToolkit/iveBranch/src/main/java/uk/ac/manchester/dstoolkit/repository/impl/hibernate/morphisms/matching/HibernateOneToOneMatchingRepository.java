package uk.ac.manchester.dstoolkit.repository.impl.hibernate.morphisms.matching;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.matching.OneToOneMatchingRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "oneToOneMatchingRepository")
public class HibernateOneToOneMatchingRepository extends HibernateGenericRepository<OneToOneMatching, Long> implements OneToOneMatchingRepository {

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.repository.morphisms.matching.OneToOneMatchingRepository#getAllOneToOneMatchingsBetweenSourceSchemaAndTargetSchema(uk.ac.manchester.dstoolkit.domain.models.meta.Schema, uk.ac.manchester.dstoolkit.domain.models.meta.Schema)
	 */
	public List<OneToOneMatching> getAllOneToOneMatchingsBetweenSourceSchemaAndTargetSchema(Schema sourceSchema, Schema targetSchema) {
		Query query = em
				.createQuery("select m from OneToOneMatching m where (m.construct1.schema.id = :sourceSchemaId and m.construct2.schema.id = :targetSchemaId) or (m.construct1.schema.id = :targetSchemaId and m.construct2.schema.id = :sourceSchemaId)");

		query.setParameter("sourceSchemaId", sourceSchema.getId());
		query.setParameter("targetSchemaId", targetSchema.getId());

		try {
			List<OneToOneMatching> oneToOneMatchings = query.getResultList();
			return oneToOneMatchings;
		} catch (NoResultException ex) {
			return null;
		}
	}

}
