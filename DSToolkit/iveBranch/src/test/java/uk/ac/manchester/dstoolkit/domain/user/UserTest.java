package uk.ac.manchester.dstoolkit.domain.user;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

	private User testUser;
	private final Role testRole = new Role(RoleType.ADMIN);

	@Before
	public void setUp() {
		testUser = new User();
		testUser.setUserName("guest");
		testUser.setPassword("guest");
		testUser.setFirstName("anonymous");
		testUser.setEmail("anonymous.guest@guest.co.uk");
	}

	//TODO add more tests

	@Test
	public void addRole() {
		assertEquals(1, testUser.getRoles().size());
		assertEquals(RoleType.USER, testUser.getRoles().iterator().next().getRoleType());
		testUser.addRole(testRole);
		assertEquals(2, testUser.getRoles().size());
	}

	@Test
	public void removeRole() {
		testUser.addRole(testRole);
		testUser.removeRole(RoleType.USER);
		assertEquals(1, testUser.getRoles().size());
		assertEquals(RoleType.ADMIN, testUser.getRoles().iterator().next().getRoleType());
		testUser.removeRole(RoleType.ADMIN);
		assertEquals(1, testUser.getRoles().size());
	}

	@Test
	public void addSameRoleTwice() {
		assertEquals(1, testUser.getRoles().size());
		assertEquals(RoleType.USER, testUser.getRoles().iterator().next().getRoleType());
		testUser.addRole(testRole);
		testUser.addRole(testRole);
		assertEquals(2, testUser.getRoles().size());
	}

	/*
	@Test(expected=IllegalStateException.class)
	public void addAfterPlace() {
		testOrder.place();
		testOrder.addProduct(testProduct1, 1);
	}
	
	@Test(expected=IllegalStateException.class)
	public void removeAfterPlace() {
		testOrder.place();
		testOrder.removeProduct(testProduct1, 1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removeProductNotYetAdded() {
		testOrder.addProduct(testProduct1, 1);
		testOrder.removeProduct(testProduct2, 1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void removingMoreThanExistingQuantity() {
		testOrder.addProduct(testProduct1, 1);
		testOrder.removeProduct(testProduct1, 2);
	}

	private LineItem getLineItemFor(Order order, Product product) {
		for (LineItem lineItem : order.getLineItems()) {
			if (lineItem.getProduct().equals(product)) {
				return lineItem;
			}
		}
		return null;
	}
	*/
}
