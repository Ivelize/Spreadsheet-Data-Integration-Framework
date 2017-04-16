/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.matching;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface MatchingService extends GenericEntityService<Matching, Long> {

	/**
	 * @param matchingId
	 * @return matching
	 */
	public Matching findMatching(Long matchingId);

	/**
	 * @param matching
	 */
	public void addMatching(Matching matching);

	/**
	 * @param matchingId
	 */
	public void deleteMatching(Long matchingId);
	
	
	public java.util.List<Matching> findAllMatching();

}
