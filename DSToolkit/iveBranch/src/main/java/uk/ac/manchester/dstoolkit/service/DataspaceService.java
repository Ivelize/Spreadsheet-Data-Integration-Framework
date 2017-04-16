package uk.ac.manchester.dstoolkit.service;

import java.util.List;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.user.User;

/**
 * @author chedeler
 *
 */
public interface DataspaceService {

	public List<Dataspace> getAllDataspaces();

	/**
	 * @param dataspaceName
	 * @param user
	 * @return dataspace
	 */
	public Dataspace createDataspace(String dataspaceName, User user);

	/**
	 * @param dataspaceId
	 * @return dataspace
	 */
	public Dataspace findDataspace(Long dataspaceId);

	/**
	 * @param dataspace
	 */
	public void addDataspace(Dataspace dataspace);

	/**
	 * @param dataspaceId
	 */
	public void deleteDataspace(Long dataspaceId);

	/**
	 * @param dataspace
	 * @param dataSource
	 */
	public void addDataSourceToDataspace(Dataspace dataspace, DataSource dataSource);

	/**
	 * @param dataspace
	 * @param user
	 */
	public void addUserToDataspace(Dataspace dataspace, User user);

}
