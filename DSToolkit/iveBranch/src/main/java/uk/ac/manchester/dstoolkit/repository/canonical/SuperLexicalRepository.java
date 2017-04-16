package uk.ac.manchester.dstoolkit.repository.canonical;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface SuperLexicalRepository extends GenericRepository<SuperLexical, Long> {

	/**
	 * @param superLexicalName
	 * @param schemaName
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsWithNameInSchemaWithName(String superLexicalName, String schemaName);

	/**
	 * @param schemaName
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsInSchemaWithName(String schemaName);

	/**
	 * @param superLexicalName
	 * @param superAbstractName
	 * @param schemaName
	 * @return superLexical
	 */
	public SuperLexical getSuperLexicalWithNameOfSuperAbstractWithNameInSchemaWithName(String superLexicalName, String superAbstractName,
			String schemaName);

	public SuperLexical getSuperLexicalWithNameOfSuperAbstract(String superLexicalName, SuperAbstract superAbstract);

	/**
	 * @param superAbstractId
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsOfSuperAbstract(Long superAbstractId);

	/**
	 * @param superAbstractId
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsOfSuperAbstractOrderedById(Long superAbstractId);

	/**
	 * @param superAbstractName
	 * @param schemaName
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsOfSuperAbstractWithNameInSchemaWithName(String superAbstractName, String schemaName);

	public SuperLexical getSuperLexicalWithNameOfParentSuperLexicalWithNameInSchemaWithName(String superLexicalName, String parentSuperLexicalName,
			String schemaName);

	/**
	 * @param superAbstractName
	 * @param schemaName
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllPrimaryKeySuperLexicalsOfSuperAbstractWithNameInSchemaWithName(String superAbstractName, String schemaName);

}
