package uk.ac.manchester.dstoolkit.repository.impl.hibernate.user;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.impl.hibernate.HibernateGenericRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;

/**
 * @author chedeler
 *
 */

@Repository(value = "userRepository")
public class HibernateUserRepository extends HibernateGenericRepository<User, Long> implements UserRepository {

	@Transactional(readOnly = true)
	public User getUserWithUserName(String userName) {
		Query query = em.createQuery("select u from User u where u.userName = :userName");
		query.setParameter("userName", userName);
		try {
			return (User) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	
	@Transactional
	public void addUser(User user) {
		this.save(user);
		this.flush();
	}

	/**
	 * @param user
	 */
	/*
	public void deleteUser(User user) {
		//TODO this doesn't work, check this, possibly use cascade in User domain entity
		em.remove(user);
	}
	*/

}
