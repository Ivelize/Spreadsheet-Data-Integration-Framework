/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

/**
 * @author chedeler
 *
 */
public class EquivalentSuperLexicalsIdentifierServiceImplTestGenerateCombinatorialCombinationsOfSuperAbstracts {

	private EquivalentSuperLexicalsIdentifierServiceImpl equivalentSuperLexicalsIdentifierServiceImpl;
	private SuperLexical currentSuperLexical;
	private SuperAbstract superAbstractOfCurrentSuperLexical;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicalsIdentifierServiceImpl = new EquivalentSuperLexicalsIdentifierServiceImpl();
		currentSuperLexical = new SuperLexical("sl1", null);
		superAbstractOfCurrentSuperLexical = new SuperAbstract("saOfCurrentSl", null);
		currentSuperLexical.setParentSuperAbstract(superAbstractOfCurrentSuperLexical);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#generateCombinatorialCombinationsOfSuperAbstractsWithEachContainingSuperAbstractOfCurrentSuperLexical(java.util.Set, uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical)}.
	 */
	@Test
	public void testGenerateCombinatorialCombinationsOfSuperAbstractsWithEachContainingSuperAbstractOfCurrentSuperLexical() {
		Set<SuperAbstract> superAbstracts = new HashSet<SuperAbstract>();
		superAbstracts.add(superAbstractOfCurrentSuperLexical);
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		superAbstracts.add(sa1);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		superAbstracts.add(sa2);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		superAbstracts.add(sa3);

		List<List<SuperAbstract>> combinatorialCombinations = equivalentSuperLexicalsIdentifierServiceImpl
				.generateCombinatorialCombinationsOfSuperAbstractsWithEachContainingSuperAbstractOfCurrentSuperLexical(superAbstracts,
						currentSuperLexical);
		assertEquals(8, combinatorialCombinations.size());

		//sa1 sa2 sa3 saOfCurrentSl
		// 0   0   0      1
		// 0   0   1      1
		// 0   1   0      1
		// 0   1   1      1
		// 1   0   0      1
		// 1   0   1      1
		// 1   1   0      1
		// 1   1   1      1

		int noOfCombinationsOfSize1 = 0;
		int noOfCombinationsOfSize2 = 0;
		int noOfCombinationsOfSize3 = 0;
		int noOfCombinationsOfSize4 = 0;

		boolean foundSa1InCombsOfSize2 = false;
		boolean foundSa2InCombsOfSize2 = false;
		boolean foundSa3InCombsOfSize2 = false;

		boolean foundSa1AndSa2InCombsOfSize3 = false;
		boolean foundSa2AndSa3InCombsOfSize3 = false;
		boolean foundSa1AndSa3InCombsOfSize3 = false;

		for (List<SuperAbstract> superAbstractsCom : combinatorialCombinations) {
			assertTrue(superAbstractsCom.contains(superAbstractOfCurrentSuperLexical));
			assertTrue(superAbstractsCom.size() > 0);
			if (superAbstractsCom.size() == 1) {
				noOfCombinationsOfSize1++;
			} else if (superAbstractsCom.size() == 2) {
				noOfCombinationsOfSize2++;
				if (superAbstractsCom.contains(sa1))
					foundSa1InCombsOfSize2 = true;
				else if (superAbstractsCom.contains(sa2))
					foundSa2InCombsOfSize2 = true;
				else if (superAbstractsCom.contains(sa3))
					foundSa3InCombsOfSize2 = true;
			} else if (superAbstractsCom.size() == 3) {
				noOfCombinationsOfSize3++;
				if (superAbstractsCom.contains(sa1) && superAbstractsCom.contains(sa2))
					foundSa1AndSa2InCombsOfSize3 = true;
				if (superAbstractsCom.contains(sa2) && superAbstractsCom.contains(sa3))
					foundSa2AndSa3InCombsOfSize3 = true;
				if (superAbstractsCom.contains(sa1) && superAbstractsCom.contains(sa3))
					foundSa1AndSa3InCombsOfSize3 = true;
			} else if (superAbstractsCom.size() == 4) {
				noOfCombinationsOfSize4++;
				assertTrue(superAbstractsCom.contains(sa1));
				assertTrue(superAbstractsCom.contains(sa2));
				assertTrue(superAbstractsCom.contains(sa3));
			}
		}
		assertEquals(1, noOfCombinationsOfSize1);
		assertEquals(3, noOfCombinationsOfSize2);
		assertEquals(3, noOfCombinationsOfSize3);
		assertEquals(1, noOfCombinationsOfSize4);

		assertTrue(foundSa1InCombsOfSize2);
		assertTrue(foundSa2InCombsOfSize2);
		assertTrue(foundSa3InCombsOfSize2);

		assertTrue(foundSa1AndSa2InCombsOfSize3);
		assertTrue(foundSa2AndSa3InCombsOfSize3);
		assertTrue(foundSa1AndSa3InCombsOfSize3);
	}

}
