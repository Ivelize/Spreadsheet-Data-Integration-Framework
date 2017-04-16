/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.mapping;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface MappingService extends GenericEntityService<Mapping, Long> {

	//AnnotationService does that now
	//public void annotateMappingsUsedToProduceQueryResult(QueryResult queryResult, User user);

	//public void addAnnotationToMapping(Mapping mapping, String nameOfOntologyTerm, String annotation, User user);

	public Mapping fetchConstructs(Mapping mapping);

	/**
	 * @param mappingId
	 * @return mapping
	 */
	public Mapping findMapping(Long mappingId);

	/**
	 * @param mapping
	 */
	public void addMapping(Mapping mapping);

	/**
	 * @param mappingId
	 */
	public void deleteMapping(Long mappingId);
}
