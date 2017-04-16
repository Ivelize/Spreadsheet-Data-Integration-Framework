package uk.ac.manchester.dstoolkit.repository;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.Dataspace;

/**
 * @author chedeler
 *
 */

public interface DataspaceRepository extends GenericRepository<Dataspace, Long> {

	public List<String> getAllDataspaceNames();

	public Dataspace getDataspaceWithName(String dataspaceName);
}
