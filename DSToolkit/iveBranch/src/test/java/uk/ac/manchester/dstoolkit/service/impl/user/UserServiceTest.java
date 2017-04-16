package uk.ac.manchester.dstoolkit.service.impl.user;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import uk.ac.manchester.dstoolkit.repository.user.UserRepository;

public class UserServiceTest {

	private UserServiceImpl testUserService;

	@Mock
	private UserRepository userRepository;

	@Before
	public void setup() {
		initMocks(this);
		testUserService = new UserServiceImpl();
		ReflectionTestUtils.setField(testUserService, "userRepository", userRepository);
	}

	//TODO add more logic to UserService, e.g., deleting a user and taking care of the roles, that non-working functionality might have to come out of the userRepository
	//TODO add tests
	/*
	@Test(expected=InventoryException.class)
	public void insufficientInventoryOrdering() {
	    doThrow(new InventoryException("")).when(inventoryService).removeProduct(product, 1);
	    testOrderService.addProduct(order, product, 1);
	}
	
	
	@Test
	public void sufficientInventoryOrdering() {
	    testOrderService.addProduct(order, product, 1);
	    verify(inventoryService).removeProduct(product, 1);
	}
	*/
}
