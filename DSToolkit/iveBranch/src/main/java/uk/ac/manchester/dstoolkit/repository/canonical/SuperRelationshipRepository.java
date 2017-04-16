package uk.ac.manchester.dstoolkit.repository.canonical;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface SuperRelationshipRepository extends GenericRepository<SuperRelationship, Long> {
	
	/**
	 * @param superRelationshipName
	 * @param schemaName
	 * @return superRelationship
	 */
	public SuperRelationship getSuperRelationshipByNameInSchemaWithName(String superRelationshipName, String schemaName);
    
	/**
	 * @param schemaName
	 * @return superRelationships
	 */
	public List<SuperRelationship> getAllSuperRelationshipsInSchemaWithName(String schemaName);
    
	/**
	 * @param superRelationshipId
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsOfSuperRelationship(Long superRelationshipId);
    
	/**
	 * @param superRelationshipName
	 * @param schemaName
	 * @return superLexicals
	 */
	public List<SuperLexical> getAllSuperLexicalsOfSuperRelationshipWithNameInSchemaWithName(String superRelationshipName, String schemaName);
}
