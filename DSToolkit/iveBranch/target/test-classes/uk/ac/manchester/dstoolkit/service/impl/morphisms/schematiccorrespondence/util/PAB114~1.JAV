package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;

public class PairsOfEntitySetsGeneratorServiceImplTestPlacePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet {

	private PairsOfEntitySetsGeneratorServiceImpl pairsOfEntitySetsGeneratorServiceImpl;
	private Set<SuperAbstract> superAbstracts;
	private Map<SuperAbstract, PairOfEntitySets> superAbstractPairOfEntitySetsMap;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3;
	private PairOfEntitySets pairOfEntitySets1, pairOfEntitySets2, pairOfEntitySets3, newPairOfEntitySets;

	@Before
	public void setUp() throws Exception {
		pairsOfEntitySetsGeneratorServiceImpl = new PairsOfEntitySetsGeneratorServiceImpl();
		superAbstracts = new HashSet<SuperAbstract>();
		superAbstractPairOfEntitySetsMap = new HashMap<SuperAbstract, PairOfEntitySets>();

		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);

		pairOfEntitySets1 = new PairOfEntitySets(superAbstract1, null);
		pairOfEntitySets2 = new PairOfEntitySets(superAbstract2, null);
		pairOfEntitySets3 = new PairOfEntitySets(superAbstract3, null);

		superAbstracts.add(superAbstract1);
		superAbstracts.add(superAbstract2);

		superAbstractPairOfEntitySetsMap.put(superAbstract1, pairOfEntitySets1);
		superAbstractPairOfEntitySetsMap.put(superAbstract2, pairOfEntitySets2);
		superAbstractPairOfEntitySetsMap.put(superAbstract3, pairOfEntitySets3);
	}

	@Test
	public void testPlacePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet() {
		newPairOfEntitySets = new PairOfEntitySets(superAbstracts, new HashSet<SuperAbstract>());
		assertEquals(3, superAbstractPairOfEntitySetsMap.size());
		assertTrue(superAbstractPairOfEntitySetsMap.get(superAbstract1).equals(pairOfEntitySets1));
		assertTrue(superAbstractPairOfEntitySetsMap.get(superAbstract2).equals(pairOfEntitySets2));
		assertTrue(superAbstractPairOfEntitySetsMap.get(superAbstract3).equals(pairOfEntitySets3));
		superAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.placePairOfEntitySetsInSuperAbstractPairOfEntitySetsMapForAllSuperAbstractsInSet(newPairOfEntitySets, superAbstracts,
						superAbstractPairOfEntitySetsMap);
		assertEquals(3, superAbstractPairOfEntitySetsMap.size());
		assertTrue(superAbstractPairOfEntitySetsMap.get(superAbstract1).equals(newPairOfEntitySets));
		assertTrue(superAbstractPairOfEntitySetsMap.get(superAbstract2).equals(newPairOfEntitySets));
		assertFalse(superAbstractPairOfEntitySetsMap.get(superAbstract3).equals(newPairOfEntitySets));
		assertTrue(superAbstractPairOfEntitySetsMap.get(superAbstract3).equals(pairOfEntitySets3));
	}

}
