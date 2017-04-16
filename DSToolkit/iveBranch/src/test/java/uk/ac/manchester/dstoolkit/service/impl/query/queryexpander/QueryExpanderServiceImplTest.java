package uk.ac.manchester.dstoolkit.service.impl.query.queryexpander;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class QueryExpanderServiceImplTest {

	private final QueryExpanderServiceImpl queryExpanderService = new QueryExpanderServiceImpl();

	@Test
	public void testCartesianProduct1x1() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(2);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(1, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(2), iter1.next());
	}

	@Test
	public void testCartesianProduct1x2() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(2);
		set2.add(3);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(2, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(2), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(2, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(1), iter2.next());
		assertEquals(new Integer(3), iter2.next());
	}

	@Test
	public void testCartesianProduct2x1() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		set1.add(2);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(3);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(2, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(3), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(2, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(2), iter2.next());
		assertEquals(new Integer(3), iter2.next());
	}

	@Test
	public void testCartesianProduct1x3() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(2);
		set2.add(3);
		set2.add(4);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(3, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(2), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(2, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(1), iter2.next());
		assertEquals(new Integer(3), iter2.next());

		List<Integer> comb3 = cartProduct.get(2);
		assertEquals(2, comb3.size());
		Iterator<Integer> iter3 = comb3.iterator();
		assertEquals(new Integer(1), iter3.next());
		assertEquals(new Integer(4), iter3.next());
	}

	@Test
	public void testCartesianProduct3x1() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		set1.add(2);
		set1.add(3);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(4);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(3, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(4), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(2, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(2), iter2.next());
		assertEquals(new Integer(4), iter2.next());

		List<Integer> comb3 = cartProduct.get(2);
		assertEquals(2, comb3.size());
		Iterator<Integer> iter3 = comb3.iterator();
		assertEquals(new Integer(3), iter3.next());
		assertEquals(new Integer(4), iter3.next());
	}

	@Test
	public void testCartesianProduct3x2() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		set1.add(2);
		set1.add(3);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(4);
		set2.add(5);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(6, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(4), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(2, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(2), iter2.next());
		assertEquals(new Integer(4), iter2.next());

		List<Integer> comb3 = cartProduct.get(2);
		assertEquals(2, comb3.size());
		Iterator<Integer> iter3 = comb3.iterator();
		assertEquals(new Integer(3), iter3.next());
		assertEquals(new Integer(4), iter3.next());

		List<Integer> comb4 = cartProduct.get(3);
		assertEquals(2, comb4.size());
		Iterator<Integer> iter4 = comb4.iterator();
		assertEquals(new Integer(1), iter4.next());
		assertEquals(new Integer(5), iter4.next());

		List<Integer> comb5 = cartProduct.get(4);
		assertEquals(2, comb5.size());
		Iterator<Integer> iter5 = comb5.iterator();
		assertEquals(new Integer(2), iter5.next());
		assertEquals(new Integer(5), iter5.next());

		List<Integer> comb6 = cartProduct.get(5);
		assertEquals(2, comb6.size());
		Iterator<Integer> iter6 = comb6.iterator();
		assertEquals(new Integer(3), iter6.next());
		assertEquals(new Integer(5), iter6.next());
	}

	@Test
	public void testCartesianProduct2x3() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		set1.add(2);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(3);
		set2.add(4);
		set2.add(5);
		elements.add(set2);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(6, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(2, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(3), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(2, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(2), iter2.next());
		assertEquals(new Integer(3), iter2.next());

		List<Integer> comb3 = cartProduct.get(2);
		assertEquals(2, comb3.size());
		Iterator<Integer> iter3 = comb3.iterator();
		assertEquals(new Integer(1), iter3.next());
		assertEquals(new Integer(4), iter3.next());

		List<Integer> comb4 = cartProduct.get(3);
		assertEquals(2, comb4.size());
		Iterator<Integer> iter4 = comb4.iterator();
		assertEquals(new Integer(2), iter4.next());
		assertEquals(new Integer(4), iter4.next());

		List<Integer> comb5 = cartProduct.get(4);
		assertEquals(2, comb5.size());
		Iterator<Integer> iter5 = comb5.iterator();
		assertEquals(new Integer(1), iter5.next());
		assertEquals(new Integer(5), iter5.next());

		List<Integer> comb6 = cartProduct.get(5);
		assertEquals(2, comb6.size());
		Iterator<Integer> iter6 = comb6.iterator();
		assertEquals(new Integer(2), iter6.next());
		assertEquals(new Integer(5), iter6.next());
	}

	@Test
	public void testCartesianProduct2x2x2() {
		List<List<Integer>> elements = new ArrayList<List<Integer>>();
		List<Integer> set1 = new ArrayList<Integer>();
		set1.add(1);
		set1.add(2);
		elements.add(set1);
		List<Integer> set2 = new ArrayList<Integer>();
		set2.add(3);
		set2.add(4);
		elements.add(set2);
		List<Integer> set3 = new ArrayList<Integer>();
		set3.add(5);
		set3.add(6);
		elements.add(set3);

		List<List<Integer>> cartProduct = queryExpanderService.cartesianProduct(elements);

		assertEquals(8, cartProduct.size());

		List<Integer> comb1 = cartProduct.get(0);
		assertEquals(3, comb1.size());
		Iterator<Integer> iter1 = comb1.iterator();
		assertEquals(new Integer(1), iter1.next());
		assertEquals(new Integer(3), iter1.next());
		assertEquals(new Integer(5), iter1.next());

		List<Integer> comb2 = cartProduct.get(1);
		assertEquals(3, comb2.size());
		Iterator<Integer> iter2 = comb2.iterator();
		assertEquals(new Integer(2), iter2.next());
		assertEquals(new Integer(3), iter2.next());
		assertEquals(new Integer(5), iter2.next());

		List<Integer> comb3 = cartProduct.get(2);
		assertEquals(3, comb3.size());
		Iterator<Integer> iter3 = comb3.iterator();
		assertEquals(new Integer(1), iter3.next());
		assertEquals(new Integer(4), iter3.next());
		assertEquals(new Integer(5), iter3.next());

		List<Integer> comb4 = cartProduct.get(3);
		assertEquals(3, comb4.size());
		Iterator<Integer> iter4 = comb4.iterator();
		assertEquals(new Integer(2), iter4.next());
		assertEquals(new Integer(4), iter4.next());
		assertEquals(new Integer(5), iter4.next());

		List<Integer> comb5 = cartProduct.get(4);
		assertEquals(3, comb5.size());
		Iterator<Integer> iter5 = comb5.iterator();
		assertEquals(new Integer(1), iter5.next());
		assertEquals(new Integer(3), iter5.next());
		assertEquals(new Integer(6), iter5.next());

		List<Integer> comb6 = cartProduct.get(5);
		assertEquals(3, comb6.size());
		Iterator<Integer> iter6 = comb6.iterator();
		assertEquals(new Integer(2), iter6.next());
		assertEquals(new Integer(3), iter6.next());
		assertEquals(new Integer(6), iter6.next());

		List<Integer> comb7 = cartProduct.get(6);
		assertEquals(3, comb7.size());
		Iterator<Integer> iter7 = comb7.iterator();
		assertEquals(new Integer(1), iter7.next());
		assertEquals(new Integer(4), iter7.next());
		assertEquals(new Integer(6), iter7.next());

		List<Integer> comb8 = cartProduct.get(7);
		assertEquals(3, comb8.size());
		Iterator<Integer> iter8 = comb8.iterator();
		assertEquals(new Integer(2), iter8.next());
		assertEquals(new Integer(4), iter8.next());
		assertEquals(new Integer(6), iter8.next());
	}
}
