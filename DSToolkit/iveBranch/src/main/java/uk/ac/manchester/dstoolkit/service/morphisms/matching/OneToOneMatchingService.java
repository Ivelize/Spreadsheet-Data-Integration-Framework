/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface OneToOneMatchingService extends GenericEntityService<OneToOneMatching, Long> {

	/**
	 * @param oneToOneMatchingId
	 * @return oneToOneMatching
	 */
	public OneToOneMatching findOneToOneMatching(Long oneToOneMatchingId);

	/**
	 * @param oneToOneMatching
	 */
	public void addOneToOneMatching(OneToOneMatching oneToOneMatching);

	/**
	 * @param oneToOneMatchingId
	 */
	public void deleteOneToOneMatching(Long oneToOneMatchingId);

}
