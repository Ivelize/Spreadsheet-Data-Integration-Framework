package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

public class DerivedOneToOneMatchingsGeneratorServiceImplTestCalculateAvgOfMatchingScoresForMatchedConstructsArrays {

	private final double epsilon = 0.000000001;
	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;
	private CanonicalModelConstruct[] constructsArray, constructsArray1, constructsArray2, constructsArray3;
	private Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructsArraysWithCollatedDerivedMatchings;

	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();
		matchedConstructsArraysWithCollatedDerivedMatchings = new HashMap<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>>();

		constructsArray = new CanonicalModelConstruct[2];
		constructsArray1 = new CanonicalModelConstruct[1];
		constructsArray2 = new CanonicalModelConstruct[2];
		constructsArray3 = new CanonicalModelConstruct[2];
	}

	@Test
	public void testCalculateAvgOfMatchingScoresForMatchedConstructsArraysElementsInConstructArrays() {

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);

		constructsArray[0] = sl1;
		constructsArray[1] = sl2;

		constructsArray1[0] = sl3;

		constructsArray2[0] = sl4;
		constructsArray2[1] = sl5;

		constructsArray3[0] = sl6;
		constructsArray3[1] = null;

		Set<DerivedOneToOneMatching> derivedMatchingsSet1 = new HashSet<DerivedOneToOneMatching>();
		DerivedOneToOneMatching dm1 = new DerivedOneToOneMatching(sl1, sl3);
		dm1.setSumOfMatchingScores(0.3);
		derivedMatchingsSet1.add(dm1);
		matchedConstructsArraysWithCollatedDerivedMatchings.put(constructsArray1, derivedMatchingsSet1);

		Set<DerivedOneToOneMatching> derivedMatchingsSet2 = new HashSet<DerivedOneToOneMatching>();
		DerivedOneToOneMatching dm2 = new DerivedOneToOneMatching(sl1, sl4);
		dm2.setSumOfMatchingScores(0.8);
		derivedMatchingsSet2.add(dm2);
		DerivedOneToOneMatching dm3 = new DerivedOneToOneMatching(sl1, sl5);
		dm3.setSumOfMatchingScores(0.4);
		derivedMatchingsSet2.add(dm3);
		DerivedOneToOneMatching dm4 = new DerivedOneToOneMatching(sl2, sl4);
		dm4.setSumOfMatchingScores(0.9);
		derivedMatchingsSet2.add(dm4);
		DerivedOneToOneMatching dm5 = new DerivedOneToOneMatching(sl2, sl5);
		dm5.setSumOfMatchingScores(0.3);
		derivedMatchingsSet2.add(dm5);
		matchedConstructsArraysWithCollatedDerivedMatchings.put(constructsArray2, derivedMatchingsSet2);

		Set<DerivedOneToOneMatching> derivedMatchingsSet3 = new HashSet<DerivedOneToOneMatching>();
		DerivedOneToOneMatching dm6 = new DerivedOneToOneMatching(sl1, sl6);
		dm6.setSumOfMatchingScores(0.2);
		derivedMatchingsSet3.add(dm6);
		DerivedOneToOneMatching dm7 = new DerivedOneToOneMatching(sl2, sl6);
		dm7.setSumOfMatchingScores(0.2);
		derivedMatchingsSet3.add(dm7);
		DerivedOneToOneMatching dm8 = new DerivedOneToOneMatching(sl1, null);
		dm8.setSumOfMatchingScores(0d);
		derivedMatchingsSet3.add(dm8);
		DerivedOneToOneMatching dm9 = new DerivedOneToOneMatching(sl2, null);
		dm9.setSumOfMatchingScores(0d);
		derivedMatchingsSet3.add(dm9);
		matchedConstructsArraysWithCollatedDerivedMatchings.put(constructsArray3, derivedMatchingsSet3);

		Map<CanonicalModelConstruct[], Double> matchedConstructsArraysWithAvgOfMatchingScores = derivedOneToOneMatchingsGeneratorServiceImpl
				.calculateAvgOfMatchingScoresForMatchedConstructsArrays(constructsArray, matchedConstructsArraysWithCollatedDerivedMatchings);
		assertEquals(3, matchedConstructsArraysWithAvgOfMatchingScores.size());
		assertTrue(matchedConstructsArraysWithAvgOfMatchingScores.containsKey(constructsArray1));
		assertTrue(matchedConstructsArraysWithAvgOfMatchingScores.containsKey(constructsArray2));
		assertTrue(matchedConstructsArraysWithAvgOfMatchingScores.containsKey(constructsArray3));
		assertEquals(0.3, matchedConstructsArraysWithAvgOfMatchingScores.get(constructsArray1), epsilon);
		assertEquals(0.6, matchedConstructsArraysWithAvgOfMatchingScores.get(constructsArray2), epsilon);
		assertEquals(0.1, matchedConstructsArraysWithAvgOfMatchingScores.get(constructsArray3), epsilon);
	}

}
