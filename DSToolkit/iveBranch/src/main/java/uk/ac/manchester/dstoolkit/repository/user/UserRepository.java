package uk.ac.manchester.dstoolkit.repository.user;

import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;

/**
 * @author chedeler
 *
 */

public interface UserRepository extends GenericRepository<User, Long> {

	public User getUserWithUserName(String userName);
	
	public void addUser(User user);
	

}
