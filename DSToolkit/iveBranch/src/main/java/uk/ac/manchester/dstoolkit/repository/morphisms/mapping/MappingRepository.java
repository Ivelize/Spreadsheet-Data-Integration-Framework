package uk.ac.manchester.dstoolkit.repository.morphisms.mapping;

import java.util.Set;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface MappingRepository extends GenericRepository<Mapping, Long> {

	public Mapping fetchConstructs(Mapping mapping);

	public Mapping fetchAnnotations(Mapping mapping);

	public Set<Mapping> getAllMappingsBetweenSourceSchemaAndTargetSchema(Schema sourceSchema, Schema targetSchema);

	public Set<Mapping> getAllMappingsBetweenConstructQueriedAndTargetSchema(CanonicalModelConstruct constructQueried, Schema targetSchema);
	
	//public String getAnnotationValueForMappingAndGivenOntologyTermName(Mapping mapping, String ontologyTermName);
}
