/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.user.UserService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "userService")
public class UserServiceImpl extends GenericEntityServiceImpl<User, Long> implements UserService {

	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.user.UserService#addUser(uk.ac.manchester.dataspaces.domain.user.User)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addUser(User user) {
		userRepository.addUser(user);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.user.UserService#deleteUser(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteUser(Long userId) {
		// TODO
		userRepository.delete(userRepository.find(userId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.user.UserService#findUser(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public User findUser(Long userId) {
		return userRepository.find(userId);
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

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<User, Long> getRepository() {
		return userRepository;
	}
}
