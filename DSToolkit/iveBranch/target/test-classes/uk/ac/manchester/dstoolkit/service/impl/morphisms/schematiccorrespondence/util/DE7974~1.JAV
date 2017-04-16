/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatchingsGeneratorServiceImplTestGroupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings {

	private final double epsilon = 0.000000001;
	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;
	private CanonicalModelConstruct[] constructsArray, constructsArray1, constructsArray2, constructsArray3, constructsArray4, constructsArray5;
	private List<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>> constructMatchedWithConstructArraysAndDerivedMatchingsList;
	private DerivedOneToOneMatching dm1, dm2, dm3, dm4, dm5, dm6, dm7, dm8, dm9, dm10;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();
		constructMatchedWithConstructArraysAndDerivedMatchingsList = new ArrayList<Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>>();

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);
		SuperLexical sl7 = new SuperLexical("sl7", null);
		SuperLexical sl8 = new SuperLexical("sl8", null);

		constructsArray = new CanonicalModelConstruct[2];
		constructsArray[0] = sl1;
		constructsArray[1] = sl2;

		constructsArray1 = new CanonicalModelConstruct[1];
		constructsArray1[0] = sl3;

		constructsArray2 = new CanonicalModelConstruct[2];
		constructsArray2[0] = sl4;
		constructsArray2[1] = sl5;

		constructsArray3 = new CanonicalModelConstruct[2];
		constructsArray3[0] = sl6;
		constructsArray3[1] = null;

		constructsArray4 = new CanonicalModelConstruct[1];
		constructsArray4[0] = sl7;

		constructsArray5 = new CanonicalModelConstruct[1];
		constructsArray5[0] = sl8;

		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> constructsArraysDerivedMatchingsMap1 = new HashMap<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>();

		Set<DerivedOneToOneMatching> derivedMatchingsSet1 = new HashSet<DerivedOneToOneMatching>();
		dm1 = new DerivedOneToOneMatching(sl1, sl3);
		dm1.setSumOfMatchingScores(0.3);
		derivedMatchingsSet1.add(dm1);
		constructsArraysDerivedMatchingsMap1.put(constructsArray1, derivedMatchingsSet1);

		Set<DerivedOneToOneMatching> derivedMatchingsSet2 = new HashSet<DerivedOneToOneMatching>();
		dm2 = new DerivedOneToOneMatching(sl1, sl4);
		dm2.setSumOfMatchingScores(0.8);
		derivedMatchingsSet2.add(dm2);
		dm3 = new DerivedOneToOneMatching(sl1, sl5);
		dm3.setSumOfMatchingScores(0.4);
		derivedMatchingsSet2.add(dm3);
		constructsArraysDerivedMatchingsMap1.put(constructsArray2, derivedMatchingsSet2);

		Set<DerivedOneToOneMatching> derivedMatchingsSet3 = new HashSet<DerivedOneToOneMatching>();
		dm4 = new DerivedOneToOneMatching(sl1, sl6);
		dm4.setSumOfMatchingScores(0.2);
		derivedMatchingsSet3.add(dm4);
		constructsArraysDerivedMatchingsMap1.put(constructsArray3, derivedMatchingsSet3);

		Set<DerivedOneToOneMatching> derivedMatchingsSet4 = new HashSet<DerivedOneToOneMatching>();
		dm5 = new DerivedOneToOneMatching(sl1, sl7);
		dm5.setSumOfMatchingScores(0.5);
		derivedMatchingsSet4.add(dm5);
		constructsArraysDerivedMatchingsMap1.put(constructsArray4, derivedMatchingsSet4);

		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> constructsArraysDerivedMatchingsMap2 = new HashMap<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>();

		Set<DerivedOneToOneMatching> derivedMatchingsSet5 = new HashSet<DerivedOneToOneMatching>();
		dm6 = new DerivedOneToOneMatching(sl2, sl6);
		dm6.setSumOfMatchingScores(0.2);
		derivedMatchingsSet5.add(dm6);
		constructsArraysDerivedMatchingsMap2.put(constructsArray3, derivedMatchingsSet5);

		Set<DerivedOneToOneMatching> derivedMatchingsSet6 = new HashSet<DerivedOneToOneMatching>();
		dm7 = new DerivedOneToOneMatching(sl2, sl7);
		dm7.setSumOfMatchingScores(0.9);
		derivedMatchingsSet6.add(dm7);
		constructsArraysDerivedMatchingsMap2.put(constructsArray4, derivedMatchingsSet6);

		Set<DerivedOneToOneMatching> derivedMatchingsSet7 = new HashSet<DerivedOneToOneMatching>();
		dm8 = new DerivedOneToOneMatching(sl2, sl4);
		dm8.setSumOfMatchingScores(0.8);
		derivedMatchingsSet7.add(dm8);
		dm9 = new DerivedOneToOneMatching(sl2, sl5);
		dm9.setSumOfMatchingScores(0.3);
		derivedMatchingsSet7.add(dm9);
		constructsArraysDerivedMatchingsMap2.put(constructsArray2, derivedMatchingsSet7);

		Set<DerivedOneToOneMatching> derivedMatchingsSet8 = new HashSet<DerivedOneToOneMatching>();
		dm10 = new DerivedOneToOneMatching(sl2, sl8);
		dm10.setSumOfMatchingScores(0.4);
		derivedMatchingsSet8.add(dm10);
		constructsArraysDerivedMatchingsMap2.put(constructsArray5, derivedMatchingsSet8);

		constructMatchedWithConstructArraysAndDerivedMatchingsList.add(constructsArraysDerivedMatchingsMap1);
		constructMatchedWithConstructArraysAndDerivedMatchingsList.add(constructsArraysDerivedMatchingsMap2);
	}

	@Test
	public void testGroupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings() {
		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> groupedMatchedConstructsArraysWithCollatedDerivedMatchings = derivedOneToOneMatchingsGeneratorServiceImpl
				.groupMatchedConstructsArraysAndCollateDerivedOneToOneMatchings(constructMatchedWithConstructArraysAndDerivedMatchingsList);
		assertEquals(5, groupedMatchedConstructsArraysWithCollatedDerivedMatchings.size());
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.containsKey(constructsArray1));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.containsKey(constructsArray2));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.containsKey(constructsArray3));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.containsKey(constructsArray4));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.containsKey(constructsArray5));

		assertEquals(1, groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray1).size());
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray1).contains(dm1));

		assertEquals(4, groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray2).size());
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray2).contains(dm2));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray2).contains(dm3));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray2).contains(dm8));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray2).contains(dm9));

		assertEquals(2, groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray3).size());
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray3).contains(dm4));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray3).contains(dm6));

		assertEquals(2, groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray4).size());
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray4).contains(dm5));
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray4).contains(dm7));

		assertEquals(1, groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray5).size());
		assertTrue(groupedMatchedConstructsArraysWithCollatedDerivedMatchings.get(constructsArray5).contains(dm10));
	}

}
