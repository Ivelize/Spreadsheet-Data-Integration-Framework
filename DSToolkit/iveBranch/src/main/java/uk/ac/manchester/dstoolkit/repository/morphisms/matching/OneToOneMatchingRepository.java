package uk.ac.manchester.dstoolkit.repository.morphisms.matching;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface OneToOneMatchingRepository extends GenericRepository<OneToOneMatching, Long> {

	public List<OneToOneMatching> getAllOneToOneMatchingsBetweenSourceSchemaAndTargetSchema(Schema sourceSchema, Schema targetSchema);
}
