/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.canonical;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface SuperRelationshipService extends GenericEntityService<SuperRelationship, Long> {

	/**
	 * @param superRelationshipId
	 * @return superRelationship
	 */
	public SuperRelationship findSuperRelationship(Long superRelationshipId);

	/**
	 * @param superRelationship
	 */
	public void addSuperRelationship(SuperRelationship superRelationship);

	/**
	 * @param superRelationshipId
	 */
	public void deleteSuperRelationship(Long superRelationshipId);

	/**
	 * @param superRelationship
	 * @param superLexical
	 */
	public void addSuperLexicalToSuperRelationship(SuperRelationship superRelationship, SuperLexical superLexical);

	/**
	 * @param superRelationship
	 * @param superLexical
	 */
	public void removeSuperLexicalFromSuperRelationship(SuperRelationship superRelationship, SuperLexical superLexical);
}
