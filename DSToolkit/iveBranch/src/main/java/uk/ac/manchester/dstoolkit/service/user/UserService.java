/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.user;

import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;

/**
 * @author chedeler
 *
 */
public interface UserService extends GenericEntityService<User, Long> {

	/**
	 * @param userId
	 * @return user
	 */
	public User findUser(Long userId);

	/**
	 * @param user
	 */
	public void addUser(User user);

	/**
	 * @param userId
	 */
	public void deleteUser(Long userId);

}
