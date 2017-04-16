package uk.ac.manchester.dstoolkit.repository.impl.hibernate.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;

import uk.ac.manchester.dstoolkit.domain.user.Role;
import uk.ac.manchester.dstoolkit.domain.user.RoleType;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.impl.GenericRepositoryTest;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;

@DirtiesContext
public class UserRepositoryTest extends GenericRepositoryTest<User, Long> {

	@Autowired
	@Qualifier("userRepository")
	UserRepository userRepository;

	private User testUser1;
	private User testUser2;
	private Long id1;
	private final Role testRole = new Role(RoleType.ADMIN);

	@Before
	public void setUp() {
		testUser1 = new User();
		testUser1.setUserName("guest");
		testUser1.setPassword("guest");
		testUser1.setFirstName("anonymous");
		testUser1.setEmail("anonymous.guest@guest.co.uk");

		testUser2 = new User(RoleType.ADMIN);
		testUser2.setUserName("connie");
		testUser2.setPassword("secret");
		testUser2.setFirstName("connie");
		testUser2.setLastName("hedeler");
		testUser2.setEmail("chedeler@cs.man.ac.uk");

		userRepository.save(testUser1);
		flush();
		id1 = testUser1.getId();
	}

	@Test
	public void find() {
		User user = verifyFindExisting(id1);
		Collection<Role> roles = user.getRoles();
		assertNotNull(roles);
		assertEquals(1, roles.size());
	}

	@Test
	public void create() {
		User savedUser = verifySave(testUser2);
		assertEquals(1, savedUser.getRoles().size());
	}

	@Test
	public void update() {
		User user = userRepository.find(id1);
		user.addRole(testRole);
		assertEquals(2, user.getRoles().size());
	}

	/*
	@Test
	public void delete() {
		verifyDelete(id1);
	}
	*/

	@Override
	public GenericRepository<User, Long> getRepository() {
		return userRepository;
	}

}
