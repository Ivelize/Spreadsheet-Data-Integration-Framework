/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatchingsGeneratorServiceImplTestGetMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings {

	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;
	private Map<SuperLexical, DerivedOneToOneMatching> matchedSuperLexicalsWithDerivedMatching;
	private SuperAbstract sa1, sa2, sa3;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6;
	private DerivedOneToOneMatching m1, m2, m3, m4, m5, m6;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();
		matchedSuperLexicalsWithDerivedMatching = new HashMap<SuperLexical, DerivedOneToOneMatching>();

		sa1 = new SuperAbstract("sa1", null);
		sl1 = new SuperLexical("sl1", null);
		sl1.setParentSuperAbstract(sa1);
		SuperLexical otherSl = new SuperLexical("otherSl", null);
		m1 = new DerivedOneToOneMatching(sl1, otherSl);

		sl2 = new SuperLexical("sl2", null);
		sl2.setParentSuperAbstract(sa1);
		m2 = new DerivedOneToOneMatching(sl2, otherSl);

		sl3 = new SuperLexical("sl3", null);
		sl3.setParentSuperAbstract(sa1);
		m3 = new DerivedOneToOneMatching(sl3, otherSl);

		sa2 = new SuperAbstract("sa2", null);
		sl4 = new SuperLexical("sl4", null);
		sl4.setParentSuperAbstract(sa2);
		m4 = new DerivedOneToOneMatching(sl4, otherSl);

		sl5 = new SuperLexical("sl5", null);
		sl5.setParentSuperAbstract(sa2);
		m5 = new DerivedOneToOneMatching(sl5, otherSl);

		sa3 = new SuperAbstract("sa3", null);
		sl6 = new SuperLexical("sl6", null);
		sl6.setParentSuperAbstract(sa3);
		m6 = new DerivedOneToOneMatching(sl6, otherSl);

		matchedSuperLexicalsWithDerivedMatching.put(sl4, m4);
		matchedSuperLexicalsWithDerivedMatching.put(sl1, m1);
		matchedSuperLexicalsWithDerivedMatching.put(sl5, m5);
		matchedSuperLexicalsWithDerivedMatching.put(sl6, m6);
		matchedSuperLexicalsWithDerivedMatching.put(sl3, m3);
		matchedSuperLexicalsWithDerivedMatching.put(sl2, m2);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorServiceImpl#getMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical)}.
	 */
	@Test
	public void testGetMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings() {
		Map<SuperAbstract, Map<SuperLexical, DerivedOneToOneMatching>> matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings = derivedOneToOneMatchingsGeneratorServiceImpl
				.getMatchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings(matchedSuperLexicalsWithDerivedMatching);
		List<SuperAbstract> superAbstracts = new ArrayList<SuperAbstract>();
		superAbstracts.add(sa1);
		superAbstracts.add(sa2);
		superAbstracts.add(sa3);

		List<SuperLexical> superLexicals1 = new ArrayList<SuperLexical>();
		superLexicals1.add(sl1);
		superLexicals1.add(sl2);
		superLexicals1.add(sl3);
		assertEquals(3, superLexicals1.size());

		List<SuperLexical> superLexicals2 = new ArrayList<SuperLexical>();
		superLexicals2.add(sl4);
		superLexicals2.add(sl5);

		assertEquals(3, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.size());
		assertTrue(matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.keySet().containsAll(superAbstracts));

		assertEquals(3, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa1).size());
		assertTrue(matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa1).keySet().containsAll(superLexicals1));
		assertEquals(m1, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa1).get(sl1));
		assertEquals(m2, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa1).get(sl2));
		assertEquals(m3, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa1).get(sl3));

		assertEquals(2, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa2).size());
		assertTrue(matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa2).keySet().containsAll(superLexicals2));
		assertEquals(m4, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa2).get(sl4));
		assertEquals(m5, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa2).get(sl5));

		assertEquals(1, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa3).size());
		assertTrue(matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa3).keySet().contains(sl6));
		assertEquals(m6, matchedSuperLexicalsGroupedBySuperAbstractWithDerivedMatchings.get(sa3).get(sl6));

	}

}
