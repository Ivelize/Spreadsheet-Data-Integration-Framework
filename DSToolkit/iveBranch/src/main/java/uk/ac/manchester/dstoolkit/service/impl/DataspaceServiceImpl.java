package uk.ac.manchester.dstoolkit.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "dataspaceService")
public class DataspaceServiceImpl extends GenericEntityServiceImpl<Dataspace, Long> implements DataspaceService {

	private static final Logger logger = Logger.getLogger(DataspaceServiceImpl.class);

	@Autowired
	@Qualifier("dataspaceRepository")
	private DataspaceRepository dataspaceRepository;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<Dataspace> getAllDataspaces() {
		System.out.println("dataspaceservice");
		return dataspaceRepository.findAll();
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.DataspaceService#addDataSourceToDataspace(uk.ac.manchester.dataspaces.domain.Dataspace, uk.ac.manchester.dataspaces.domain.models.meta.DataSource)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	//, rollbackFor=Exception.class
	@Transactional
	public void addDataSourceToDataspace(Dataspace dataspace, DataSource dataSource) {
		dataspace.addDataSource(dataSource);
		dataspaceRepository.update(dataspace);
		dataSourceRepository.update(dataSource);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.DataspaceService#addDataspace(uk.ac.manchester.dataspaces.domain.Dataspace)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addDataspace(Dataspace dataspace) {
		dataspaceRepository.save(dataspace);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.DataspaceService#addUserToDataspace(uk.ac.manchester.dataspaces.domain.Dataspace, uk.ac.manchester.dataspaces.domain.user.User)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addUserToDataspace(Dataspace dataspace, User user) {
		dataspace.addUser(user);
		dataspaceRepository.update(dataspace);
		userRepository.update(user);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.DataspaceService#createDataspace(uk.ac.manchester.dataspaces.domain.user.User)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public Dataspace createDataspace(String dataspaceName, User user) {
		Dataspace dataspace = new Dataspace(dataspaceName);
		dataspace.addUser(user);
		dataspaceRepository.save(dataspace);
		userRepository.update(user);
		return dataspace;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.DataspaceService#deleteDataspace(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteDataspace(Long dataspaceId) {
		//TODO needs to be an "empty" dataspace
		Dataspace dataspace = dataspaceRepository.find(dataspaceId);
		//TODO need to remove all associations with the dataspace first
		dataspaceRepository.delete(dataspace);
	}

	@Transactional(readOnly = true)
	public List<String> getAllDataspaceNames() {
		return this.dataspaceRepository.getAllDataspaceNames();
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.DataspaceService#findDataspace(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public Dataspace findDataspace(Long dataspaceId) {
		return dataspaceRepository.find(dataspaceId);
	}

	/**
	 * @param dataspaceRepository the dataspaceRepository to set
	 */
	public void setDataspaceRepository(DataspaceRepository dataspaceRepository) {
		this.dataspaceRepository = dataspaceRepository;
	}

	/**
	 * @return the dataspaceRepository
	 */
	public DataspaceRepository getDataspaceRepository() {
		return dataspaceRepository;
	}

	/**
	 * @param dataSourceRepository the dataSourceRepository to set
	 */
	public void setDataSourceRepository(DataSourceRepository dataSourceRepository) {
		this.dataSourceRepository = dataSourceRepository;
	}

	/**
	 * @return the dataSourceRepository
	 */
	public DataSourceRepository getDataSourceRepository() {
		return dataSourceRepository;
	}

	/**
	 * @param userRepository the userRepository to set
	 */
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * @return the userRepository
	 */
	public UserRepository getUserRepository() {
		return userRepository;
	}

	/**
	* Repository getter used by the {@link GenericEntityServiceImpl}.
	*/
	@Override
	public GenericRepository<Dataspace, Long> getRepository() {
		return dataspaceRepository;
	}

}
