package uk.ac.manchester.dstoolkit.repository;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;

/**
 * @author chedeler
 *
 */

public interface ModelManagementConstructRepository extends GenericRepository<ModelManagementConstruct, Long> {

	public ModelManagementConstruct fetchAnnotations(ModelManagementConstruct construct);
}
