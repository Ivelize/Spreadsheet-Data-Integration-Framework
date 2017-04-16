/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.canonical;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface SuperAbstractService extends GenericEntityService<SuperAbstract, Long> {

	/**
	 * @param superAbstractId
	 * @return superAbstract
	 */
	public SuperAbstract findSuperAbstract(Long superAbstractId);

	/**
	 * @param superAbstract
	 */
	public void addSuperAbstract(SuperAbstract superAbstract);

	/**
	 * @param superAbstractId
	 */
	public void deleteSuperAbstract(Long superAbstractId);

	/**
	 * @param superAbstract
	 * @param superLexical
	 */
	public void addSuperLexicalToSuperAbstract(SuperAbstract superAbstract, SuperLexical superLexical);

	/**
	 * @param superAbstract
	 * @param superLexical
	 */
	public void removeSuperLexicalFromSuperAbstract(SuperAbstract superAbstract, SuperLexical superLexical);
}
