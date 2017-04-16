package uk.ac.manchester.dstoolkit.repository.impl.hibernate.canonical;

import org.springframework.stereotype.Repository;

import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.repository.canonical.ParticipationOfCMCInSuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;

/**
 * @author chedeler
 *
 */
@Repository(value = "participationOfCMCInSuperRelationshipRepository")
public class HibernateParticipationOfCMCInSuperRelationshipRepository extends HibernateGenericRepository<ParticipationOfCMCInSuperRelationship, Long>
		implements ParticipationOfCMCInSuperRelationshipRepository {

}
