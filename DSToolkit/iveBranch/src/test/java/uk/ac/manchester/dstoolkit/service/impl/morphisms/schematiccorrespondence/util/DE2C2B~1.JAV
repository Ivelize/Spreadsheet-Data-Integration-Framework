/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatchingsGeneratorServiceImplTestQuickSortDerivedSlMatchingsDescendingBySumOfMatchingScores {

	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;
	private List<DerivedOneToOneMatching> unorderedListOfDerivedOneToOneMatchings;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();
		unorderedListOfDerivedOneToOneMatchings = new ArrayList<DerivedOneToOneMatching>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		DerivedOneToOneMatching m1 = new DerivedOneToOneMatching(sa1, sa2);
		m1.setSumOfMatchingScores(0.23d);
		unorderedListOfDerivedOneToOneMatchings.add(m1);
		DerivedOneToOneMatching m2 = new DerivedOneToOneMatching(sa1, sa2);
		m2.setSumOfMatchingScores(0.86d);
		unorderedListOfDerivedOneToOneMatchings.add(m2);
		DerivedOneToOneMatching m3 = new DerivedOneToOneMatching(sa1, sa2);
		m3.setSumOfMatchingScores(0.14d);
		unorderedListOfDerivedOneToOneMatchings.add(m3);
		DerivedOneToOneMatching m4 = new DerivedOneToOneMatching(sa1, sa2);
		m4.setSumOfMatchingScores(0.85d);
		unorderedListOfDerivedOneToOneMatchings.add(m4);
		DerivedOneToOneMatching m5 = new DerivedOneToOneMatching(sa1, sa2);
		m5.setSumOfMatchingScores(0.15d);
		unorderedListOfDerivedOneToOneMatchings.add(m5);
		DerivedOneToOneMatching m6 = new DerivedOneToOneMatching(sa1, sa2);
		m6.setSumOfMatchingScores(0.56d);
		unorderedListOfDerivedOneToOneMatchings.add(m6);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorServiceImpl#quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(java.util.List, boolean)}.
	 */
	@Test
	public void testQuickSortDerivedSlMatchingsDescendingBySumOfMatchingScores() {
		List<DerivedOneToOneMatching> listOfMatchingsOrderedDescending = derivedOneToOneMatchingsGeneratorServiceImpl
				.quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(unorderedListOfDerivedOneToOneMatchings, false);
		for (int i = 0; i < listOfMatchingsOrderedDescending.size() - 1; i++)
			assertTrue(listOfMatchingsOrderedDescending.get(i).getSumOfMatchingScores() >= listOfMatchingsOrderedDescending.get(i + 1)
					.getSumOfMatchingScores());
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorServiceImpl#quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(java.util.List, boolean)}.
	 */
	@Test
	public void testQuickSortDerivedSlMatchingsAscendingBySumOfMatchingScores() {
		List<DerivedOneToOneMatching> listOfMatchingsOrderedDescending = derivedOneToOneMatchingsGeneratorServiceImpl
				.quickSortDerivedSlMatchingsDescendingBySumOfMatchingScores(unorderedListOfDerivedOneToOneMatchings, true);
		for (int i = 0; i < listOfMatchingsOrderedDescending.size() - 1; i++)
			assertTrue(listOfMatchingsOrderedDescending.get(i).getSumOfMatchingScores() <= listOfMatchingsOrderedDescending.get(i + 1)
					.getSumOfMatchingScores());
	}

}
