/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.SchematicCorrespondence;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface SchematicCorrespondenceService extends GenericEntityService<SchematicCorrespondence, Long> {

	public List<SchematicCorrespondence> compose(List<SchematicCorrespondence> schematicCorrespondences1,
			List<SchematicCorrespondence> schematicCorrespondences2);

	/**
	 * @param schematicCorrespondenceId
	 * @return schematicCorrespondence
	 */
	public SchematicCorrespondence findSchematicCorrespondence(Long schematicCorrespondenceId);
	
	
	public SchematicCorrespondence findSchematicCorrespondenceByName(String schematicCorrespondenceName);

	/**
	 * @param schematicCorrespondence
	 */
	public void addSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence);

	/**
	 * @param schematicCorrespondenceId
	 */
	public void deleteSchematicCorrespondence(Long schematicCorrespondenceId);
	
	public SchematicCorrespondence invert(SchematicCorrespondence schematicCorrespondence);
	
	public List<Long> findAllSchematicCorrespondences();
}
