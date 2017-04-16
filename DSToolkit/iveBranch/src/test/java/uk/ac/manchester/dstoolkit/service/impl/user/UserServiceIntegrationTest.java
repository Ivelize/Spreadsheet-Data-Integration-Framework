package uk.ac.manchester.dstoolkit.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.service.user.UserService;

//@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceIntegrationTest extends AbstractIntegrationTest {

	@Autowired
	UserService userService;

	/*
	@Test
	public void cancelOrder() {
		Order order = orderService.findOrder(2001L);
		orderService.cancelOrder(order);
		assertFalse(order.isPlaced());
		orderService.place(order);
		assertTrue(order.isPlaced());
	}

	@Test
	public void orderManipulation() {
		Order order = new Order();
		orderService.updateOrder(order);
		order = orderService.findOrder(order.getId());
		assertNotNull(order.getId());

		orderService.addProduct(order, productService.findProduct(1003L), 2);
		assertEquals(1, order.getLineItems().size());

		orderService.removeProduct(order, productService.findProduct(1003L), 1);
		LineItem lineItem = (LineItem) order.getLineItems().toArray()[0];
		assertEquals(1, lineItem.getQuantity());
		orderService.removeProduct(order, productService.findProduct(1003L), 1);
		assertEquals(0, order.getLineItems().size());
	}

	@Test
	public void findOrders() {
		Collection<Order> orders = orderService.findOrders();
		assertEquals(4, orders.size());
	}

	@Test
	public void deleteOrder() {
		Order order = orderService.findOrder(2004L);
		orderService.deleteOrder(2004L);
		assertNull(orderService.findOrder(2004L));
	}

	@Test
	public void addProductExistingInventoryItem() {
		Product product = productService.findProduct(1001L);
		inventoryService.addProduct(product, 100);

		assertTrue(inventoryService.isProductAvailable(product, 99));
		assertTrue(inventoryService.isProductAvailable(product, 100));
		assertFalse(inventoryService.isProductAvailable(product, 101));
	}

	@Test
	public void addProductNonExistingInventoryItem() {
		Product product = productService.findProduct(1004L);
		inventoryService.addProduct(product, 100);

		assertTrue(inventoryService.isProductAvailable(product, 99));
		assertTrue(inventoryService.isProductAvailable(product, 100));
		assertFalse(inventoryService.isProductAvailable(product, 101));
	}

	@Test
	public void removeProduct() {
		Product product = productService.findProduct(1001L);
		inventoryService.addProduct(product, 100);
		inventoryService.removeProduct(product, 50);

		assertTrue(inventoryService.isProductAvailable(product, 49));
		assertTrue(inventoryService.isProductAvailable(product, 50));
		assertFalse(inventoryService.isProductAvailable(product, 51));
	}

	@Test(expected = InventoryException.class)
	public void removeProductInsufficientInventory() {
		Product product = productService.findProduct(1001L);
		inventoryService.addProduct(product, 100);
		inventoryService.removeProduct(product, 101);
	}
	
	@Test
	public void load() {
		// nothing to do here
	}
	
	@Test
	public void findProducts() {
		List<Product> plist = productService.findProducts();
		assertNotNull(plist);
		assertTrue(plist.size() > 0);
		assertNotNull(plist.get(0).getId());
		assertNotNull(plist.get(0).getName());
	}
	
	@Test
	public void createProduct() {
		Product newProduct = new Product();
		newProduct.setName("newProduct");
		productService.updateProduct(newProduct);
		
		assertNotNull(productService.findProduct(newProduct.getId()));
	}
	
	@Test
	public void deleteProduct() {
		// Delete product without a corresponding inventory item
		productService.deleteProduct(1004L);
		
		assertNull(productService.findProduct(1004L));
	}
	
	@Test
	public void updateProduct() {
		
	}
	*/
}
