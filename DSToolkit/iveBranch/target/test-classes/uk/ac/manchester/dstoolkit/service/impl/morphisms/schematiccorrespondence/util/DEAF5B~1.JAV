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
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService;

public class DerivedOneToOneMatchingsGeneratorServiceImplTestGetMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings {

	private DerivedOneToOneMatchingsGeneratorService derivedOneToOneMatchingsGeneratorService;
	private SuperAbstract sa1, sa2, sa3, sa4;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6;
	private DerivedOneToOneMatching dm1, dm2, dm3, dm4, dm5;
	private CanonicalModelConstruct[] constructArray1, constructArray2, constructArray3, constructArray4; //, constructArray5;
	private Set<CanonicalModelConstruct[]> setOfConstructArrays;

	@Before
	public void setUp() throws Exception {
		Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap = new HashMap<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>>();
		Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap = new HashMap<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>>();

		derivedOneToOneMatchingsGeneratorService = new DerivedOneToOneMatchingsGeneratorServiceImpl();

		sa1 = new SuperAbstract("sa1", null);
		sa2 = new SuperAbstract("sa2", null);
		sa3 = new SuperAbstract("sa3", null);
		sa4 = new SuperAbstract("sa4", null);
		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);
		sl4 = new SuperLexical("sl4", null);
		sl5 = new SuperLexical("sl5", null);
		sl6 = new SuperLexical("sl6", null);

		constructArray1 = new CanonicalModelConstruct[2];
		constructArray1[0] = sa2;
		constructArray1[1] = sa3;

		constructArray2 = new CanonicalModelConstruct[2];
		constructArray2[0] = null;
		constructArray2[1] = sl2;

		constructArray3 = new CanonicalModelConstruct[2];
		constructArray3[0] = sl3;
		constructArray3[1] = sl4;

		constructArray4 = new CanonicalModelConstruct[2];
		constructArray4[0] = sl5;
		constructArray4[1] = null;

		//TODO this is currently ignored, as it's not generated, but should probably be tested
		//constructArray5 = new CanonicalModelConstruct[2];
		//constructArray5[0] = sa4;
		//constructArray5[1] = sl6;

		setOfConstructArrays = new HashSet<CanonicalModelConstruct[]>();
		setOfConstructArrays.add(constructArray1);
		setOfConstructArrays.add(constructArray2);
		setOfConstructArrays.add(constructArray3);
		setOfConstructArrays.add(constructArray4);

		dm1 = new DerivedOneToOneMatching(sa1, sa2);
		dm1.setSumOfMatchingScores(0.3);
		dm2 = new DerivedOneToOneMatching(sa1, sa3);
		dm2.setSumOfMatchingScores(0.4);
		dm3 = new DerivedOneToOneMatching(sl1, sl2);
		dm3.setSumOfMatchingScores(0.5);
		dm4 = new DerivedOneToOneMatching(sl1, sl3);
		dm4.setSumOfMatchingScores(0.6);
		dm5 = new DerivedOneToOneMatching(sl1, sl4);
		dm5.setSumOfMatchingScores(0.8);

		Map<SuperAbstract, DerivedOneToOneMatching> superAbstractMatchingMap = new HashMap<SuperAbstract, DerivedOneToOneMatching>();
		superAbstractMatchingMap.put(sa2, dm1);
		superAbstractMatchingMap.put(sa3, dm2);
		sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap.put(sa1, superAbstractMatchingMap);

		Map<SuperLexical, DerivedOneToOneMatching> superLexicalMatchingMap = new HashMap<SuperLexical, DerivedOneToOneMatching>();
		superLexicalMatchingMap.put(sl2, dm3);
		superLexicalMatchingMap.put(sl3, dm4);
		superLexicalMatchingMap.put(sl4, dm5);
		sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap.put(sl1, superLexicalMatchingMap);

		derivedOneToOneMatchingsGeneratorService
				.setSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap(sourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap);
		derivedOneToOneMatchingsGeneratorService
				.setSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap(sourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap);
	}

	@Test
	public void testGetMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchingsNoMatchings() {
		SuperAbstract sa = new SuperAbstract("sa", null);
		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructsWithDerivedMatchings = derivedOneToOneMatchingsGeneratorService
				.getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(sa, setOfConstructArrays, true);
		assertEquals(0, matchedConstructsWithDerivedMatchings.size());
	}

	@Test
	public void testGetMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchingsMatchingsBetweenSuperAbstracts() {
		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructsWithDerivedMatchings = derivedOneToOneMatchingsGeneratorService
				.getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(sa1, setOfConstructArrays, true);
		assertEquals(1, matchedConstructsWithDerivedMatchings.size());
		assertTrue(matchedConstructsWithDerivedMatchings.keySet().contains(constructArray1));
		Set<DerivedOneToOneMatching> matchings = matchedConstructsWithDerivedMatchings.get(constructArray1);
		assertEquals(2, matchings.size());
		assertTrue(matchings.contains(dm1));
		assertTrue(matchings.contains(dm2));
	}

	@Test
	public void testGetMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchingsMatchingsBetweenSuperLexicalsWithNullConstructArrayElement() {
		Map<CanonicalModelConstruct[], Set<DerivedOneToOneMatching>> matchedConstructsWithDerivedMatchings = derivedOneToOneMatchingsGeneratorService
				.getMatchedConstructsWithinSetOfArraysOfConstructsWithDerivedMatchings(sl1, setOfConstructArrays, true);
		DerivedOneToOneMatching derivedMatchingToNullElement = new DerivedOneToOneMatching(sl1, null);
		derivedMatchingToNullElement.setSumOfMatchingScores(0d);

		assertEquals(2, matchedConstructsWithDerivedMatchings.size());
		matchedConstructsWithDerivedMatchings.keySet().contains(constructArray2);
		matchedConstructsWithDerivedMatchings.keySet().contains(constructArray3);
		Set<DerivedOneToOneMatching> matchings1 = matchedConstructsWithDerivedMatchings.get(constructArray2);
		assertEquals(2, matchings1.size());
		assertTrue(matchings1.contains(dm3));
		assertTrue(matchings1.contains(derivedMatchingToNullElement));
		Set<DerivedOneToOneMatching> matchings2 = matchedConstructsWithDerivedMatchings.get(constructArray3);
		assertEquals(2, matchings2.size());
		assertTrue(matchings2.contains(dm4));
		assertTrue(matchings2.contains(dm5));
	}

}
