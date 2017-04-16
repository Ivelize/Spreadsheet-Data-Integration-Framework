package uk.ac.manchester.dstoolkit.repository.canonical;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface SuperAbstractRepository extends GenericRepository<SuperAbstract, Long> {

	/**
	 * @param superAbstractName
	 * @return superAbstracts
	 */
	public List<SuperAbstract> getAllSuperAbstractsWithName(String superAbstractName);

	/**
	 * @param superAbstractName
	 * @param schemaName
	 * @return superAbstract
	 */
	public List<SuperAbstract> getAllSuperAbstractsByNameInSchemaWithName(String superAbstractName, String schemaName);

	/**
	 * @param superAbstractName
	 * @param schemaName
	 * @return superAbstract
	 */
	public SuperAbstract getSuperAbstractByNameInSchemaWithName(String superAbstractName, String schemaName);

	/**
	 * @param schemaName
	 * @return superAbstracts
	 */
	public List<SuperAbstract> getAllSuperAbstractsInSchemaWithName(String schemaName);
}
