package uk.ac.manchester.dstoolkit.repository.canonical;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface CanonicalModelConstructRepository extends GenericRepository<CanonicalModelConstruct, Long> {

	/**
	 * @param constructName
	 * @param schemaName
	 * @return canonicalModelConstruct
	 */
	public CanonicalModelConstruct getCanonicalModelConstructByNameInSchemaWithName(String constructName, String schemaName);

	public CanonicalModelConstruct getCanonicalModelConstructOfTypeByNameInSchemaWithName(String constructName, ConstructType typeOfConstruct,
			String schemaName);

	/**
	 * @param schemaName
	 * @return canonicalModelConstructs
	 */
	public List<CanonicalModelConstruct> getAllCanonicalModelConstructsInSchemaWithName(String schemaName);

}
