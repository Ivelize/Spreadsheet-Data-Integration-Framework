package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;

public class DecoderServiceImplTestIdentifyMatchedButUnassociatedEntities {

	private DecoderServiceImpl decoderServiceImpl;
	private Set<PairOfEntitySets> pairsOfEntitySets;
	private Set<SuperAbstract> superAbstractsInSchemas; //normally there should be one set for superAbstractsInSourceSchema and another for superAbstractsInTargetSchema, but doesn't make any different here
	private Set<SuperAbstract> unmatchedSuperAbstracts; //same as above
	private boolean isSource;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6;

	@Before
	public void setUp() throws Exception {
		decoderServiceImpl = new DecoderServiceImpl();
		pairsOfEntitySets = new HashSet<PairOfEntitySets>();
		superAbstractsInSchemas = new HashSet<SuperAbstract>();
		unmatchedSuperAbstracts = new HashSet<SuperAbstract>();

		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);
		superAbstract4 = new SuperAbstract("sa4", null);
		superAbstract5 = new SuperAbstract("sa5", null);
		superAbstract6 = new SuperAbstract("sa6", null);

		superAbstractsInSchemas.add(superAbstract1);
		superAbstractsInSchemas.add(superAbstract2);
		superAbstractsInSchemas.add(superAbstract3);
		superAbstractsInSchemas.add(superAbstract4);
		superAbstractsInSchemas.add(superAbstract5);
		superAbstractsInSchemas.add(superAbstract6);
	}

	@Test
	public void testIdentifyMatchedButUnassociatedEntitiesEmptyUnmatchedAndEmptySetOfPairOfEntitySets() {
		Set<SuperAbstract> unassociatedEntities = decoderServiceImpl.identifyMatchedButUnassociatedEntities(pairsOfEntitySets,
				superAbstractsInSchemas, unmatchedSuperAbstracts, true);
		assertEquals(6, unassociatedEntities.size());
		assertTrue(unassociatedEntities.containsAll(superAbstractsInSchemas));
	}

	@Test
	public void testIdentifyMatchedButUnassociatedEntitiesWithUnMatchedEntitiesButEmptySetOfPairOfEntitySets() {
		unmatchedSuperAbstracts.add(superAbstract1);
		unmatchedSuperAbstracts.add(superAbstract5);
		Set<SuperAbstract> unassociatedEntities = decoderServiceImpl.identifyMatchedButUnassociatedEntities(pairsOfEntitySets,
				superAbstractsInSchemas, unmatchedSuperAbstracts, true);
		assertEquals(4, unassociatedEntities.size());
		assertTrue(unassociatedEntities.contains(superAbstract2));
		assertTrue(unassociatedEntities.contains(superAbstract3));
		assertTrue(unassociatedEntities.contains(superAbstract4));
		assertTrue(unassociatedEntities.contains(superAbstract6));
	}

	@Test
	public void testIdentifyMatchedButUnassociatedEntitiesWithUnMatchedEntitiesAndPairsOfEntitySetsSource() {
		unmatchedSuperAbstracts.add(superAbstract1);
		unmatchedSuperAbstracts.add(superAbstract5);

		PairOfEntitySets pairOfEntitySets1 = new PairOfEntitySets(superAbstract2, superAbstract3);
		pairsOfEntitySets.add(pairOfEntitySets1);
		PairOfEntitySets pairOfEntitySets2 = new PairOfEntitySets(superAbstract4, superAbstract6);
		pairsOfEntitySets.add(pairOfEntitySets2);

		Set<SuperAbstract> unassociatedEntities = decoderServiceImpl.identifyMatchedButUnassociatedEntities(pairsOfEntitySets,
				superAbstractsInSchemas, unmatchedSuperAbstracts, true);
		assertEquals(2, unassociatedEntities.size());
		assertTrue(unassociatedEntities.contains(superAbstract3));
		assertTrue(unassociatedEntities.contains(superAbstract6));
	}

	@Test
	public void testIdentifyMatchedButUnassociatedEntitiesWithUnMatchedEntitiesAndPairsOfEntitySetsTarget() {
		unmatchedSuperAbstracts.add(superAbstract1);
		unmatchedSuperAbstracts.add(superAbstract5);

		PairOfEntitySets pairOfEntitySets1 = new PairOfEntitySets(superAbstract2, superAbstract3);
		pairsOfEntitySets.add(pairOfEntitySets1);
		PairOfEntitySets pairOfEntitySets2 = new PairOfEntitySets(superAbstract4, superAbstract6);
		pairsOfEntitySets.add(pairOfEntitySets2);

		Set<SuperAbstract> unassociatedEntities = decoderServiceImpl.identifyMatchedButUnassociatedEntities(pairsOfEntitySets,
				superAbstractsInSchemas, unmatchedSuperAbstracts, false);
		assertEquals(2, unassociatedEntities.size());
		assertTrue(unassociatedEntities.contains(superAbstract2));
		assertTrue(unassociatedEntities.contains(superAbstract4));
	}
}
