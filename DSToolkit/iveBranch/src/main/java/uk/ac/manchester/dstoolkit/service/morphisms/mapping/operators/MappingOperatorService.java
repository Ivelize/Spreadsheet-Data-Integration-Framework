/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.mapping.operators;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface MappingOperatorService extends GenericEntityService<MappingOperator, Long> {

	/**
	 * @param mappingOperatorId
	 * @return mappingOperator
	 */
	public MappingOperator findMappingOperator(Long mappingOperatorId);

	/**
	 * @param mappingOperator
	 */
	public void addMappingOperator(MappingOperator mappingOperator);

	/**
	 * @param mappingOperatorId
	 */
	public void deleteMappingOperator(Long mappingOperatorId);
}
