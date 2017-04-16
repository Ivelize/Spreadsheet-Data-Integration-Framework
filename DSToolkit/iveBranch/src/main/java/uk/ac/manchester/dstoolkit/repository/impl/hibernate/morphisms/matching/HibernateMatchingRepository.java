package uk.ac.manchester.dstoolkit.repository.impl.hibernate.morphisms.matching;

import org.springframework.stereotype.Repository;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.matching.MatchingRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "matchingRepository")
public class HibernateMatchingRepository extends HibernateGenericRepository<Matching, Long> implements MatchingRepository {

}
