package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;

public class PairsOfEntitySetsGeneratorServiceImplTestGeneratePairsOfSourceAndTargetEntitySetsAndGetPairsOfEntitySets {

	private PairsOfEntitySetsGeneratorServiceImpl pairsOfEntitySetsGeneratorServiceImpl;
	private ELRGenotypeBitVectorIndividualStub elrGenotypeBitVectorIndividualStub;
	private List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5;

	@Before
	public void setUp() throws Exception {
		pairsOfEntitySetsGeneratorServiceImpl = new PairsOfEntitySetsGeneratorServiceImpl();

		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);
		superAbstract4 = new SuperAbstract("sa4", null);
		superAbstract5 = new SuperAbstract("sa5", null);

		SuperAbstract[] pair1 = { superAbstract1, superAbstract3 };
		SuperAbstract[] pair2 = { superAbstract1, superAbstract4 };
		SuperAbstract[] pair3 = { superAbstract2, superAbstract3 };
		SuperAbstract[] pair4 = { superAbstract2, superAbstract5 };
		SuperAbstract[] pair5 = { superAbstract1, superAbstract5 };

		chromosomeOfPairsOfSuperAbstracts = new ArrayList<SuperAbstract[]>();
		chromosomeOfPairsOfSuperAbstracts.add(pair1);
		chromosomeOfPairsOfSuperAbstracts.add(pair2);
		chromosomeOfPairsOfSuperAbstracts.add(pair3);
		chromosomeOfPairsOfSuperAbstracts.add(pair4);
		chromosomeOfPairsOfSuperAbstracts.add(pair5);
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsNoPairSelected() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = false;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = false;
		elrGenotypeBitVectorIndividualStub.genome[4] = false;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(0, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(0, targetSuperAbstractPairOfEntitySetsMap.size());

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(0, pairsOfEntitySets.size());
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsOnePairSelected() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = false;
		elrGenotypeBitVectorIndividualStub.genome[4] = false;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(1, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(1, targetSuperAbstractPairOfEntitySetsMap.size());

		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract3));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));

		PairOfEntitySets pairOfEntitySets = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);

		assertEquals(1, pairOfEntitySets.getSourceEntitySet().size());
		assertEquals(1, pairOfEntitySets.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract1));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract3));

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(1, pairsOfEntitySets.size());
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets));
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsTwoPairsSelectedNoOverlapInEntitySets() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = true;
		elrGenotypeBitVectorIndividualStub.genome[4] = false;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(2, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(2, targetSuperAbstractPairOfEntitySetsMap.size());

		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1));
		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract3));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract5));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));

		PairOfEntitySets pairOfEntitySets1 = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);
		PairOfEntitySets pairOfEntitySets2 = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2);

		assertEquals(1, pairOfEntitySets1.getSourceEntitySet().size());
		assertEquals(1, pairOfEntitySets1.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets1.getSourceEntitySet().contains(superAbstract1));
		assertTrue(pairOfEntitySets1.getTargetEntitySet().contains(superAbstract3));

		assertEquals(1, pairOfEntitySets2.getSourceEntitySet().size());
		assertEquals(1, pairOfEntitySets2.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets2.getSourceEntitySet().contains(superAbstract2));
		assertTrue(pairOfEntitySets2.getTargetEntitySet().contains(superAbstract5));

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(2, pairsOfEntitySets.size());
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets1));
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets2));
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsTwoPairsSelectedOverlapInSourceEntitySets() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = true;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = false;
		elrGenotypeBitVectorIndividualStub.genome[4] = false;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(1, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(2, targetSuperAbstractPairOfEntitySetsMap.size());

		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract3));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract4));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract4));

		PairOfEntitySets pairOfEntitySets = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);

		assertEquals(1, pairOfEntitySets.getSourceEntitySet().size());
		assertEquals(2, pairOfEntitySets.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract1));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract3));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract4));

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(1, pairsOfEntitySets.size());
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets));
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsTwoPairsSelectedOverlapInTargetEntitySets() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = true;
		elrGenotypeBitVectorIndividualStub.genome[3] = false;
		elrGenotypeBitVectorIndividualStub.genome[4] = false;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(2, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(1, targetSuperAbstractPairOfEntitySetsMap.size());

		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1));
		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract3));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));

		PairOfEntitySets pairOfEntitySets = targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3);

		assertEquals(2, pairOfEntitySets.getSourceEntitySet().size());
		assertEquals(1, pairOfEntitySets.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract1));
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract2));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract3));

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(1, pairsOfEntitySets.size());
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets));
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsTwoPairsSelectedOverlapInSourceAndTargetEntitySetsDifferentEntitySets() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = true;
		elrGenotypeBitVectorIndividualStub.genome[4] = true;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(2, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(2, targetSuperAbstractPairOfEntitySetsMap.size());

		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1));
		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract3));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract5));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2));
		assertEquals(targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));

		PairOfEntitySets pairOfEntitySets = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);

		assertEquals(2, pairOfEntitySets.getSourceEntitySet().size());
		assertEquals(2, pairOfEntitySets.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract1));
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract2));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract3));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract5));

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(1, pairsOfEntitySets.size());
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets));
	}

	@Test
	public void testGeneratePairsOfSourceAndTargetEntitySetsTwoPairsSelectedOverlapInSourceAndTargetEntitySetsSameEntitySets() {
		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(5);
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = true;
		elrGenotypeBitVectorIndividualStub.genome[3] = true;
		elrGenotypeBitVectorIndividualStub.genome[4] = true;

		pairsOfEntitySetsGeneratorServiceImpl.generatePairsOfSourceAndTargetEntitySets(elrGenotypeBitVectorIndividualStub,
				chromosomeOfPairsOfSuperAbstracts);

		Map<SuperAbstract, PairOfEntitySets> sourceSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getSourceSuperAbstractPairOfEntitySetsMap();
		Map<SuperAbstract, PairOfEntitySets> targetSuperAbstractPairOfEntitySetsMap = pairsOfEntitySetsGeneratorServiceImpl
				.getTargetSuperAbstractPairOfEntitySetsMap();

		assertEquals(2, sourceSuperAbstractPairOfEntitySetsMap.size());
		assertEquals(2, targetSuperAbstractPairOfEntitySetsMap.size());

		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract1));
		assertTrue(sourceSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract2));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract3));
		assertTrue(targetSuperAbstractPairOfEntitySetsMap.containsKey(superAbstract5));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2));
		assertEquals(targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));

		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract3));
		assertEquals(sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract2), targetSuperAbstractPairOfEntitySetsMap.get(superAbstract5));

		PairOfEntitySets pairOfEntitySets = sourceSuperAbstractPairOfEntitySetsMap.get(superAbstract1);

		assertEquals(2, pairOfEntitySets.getSourceEntitySet().size());
		assertEquals(2, pairOfEntitySets.getTargetEntitySet().size());
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract1));
		assertTrue(pairOfEntitySets.getSourceEntitySet().contains(superAbstract2));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract3));
		assertTrue(pairOfEntitySets.getTargetEntitySet().contains(superAbstract5));

		Set<PairOfEntitySets> pairsOfEntitySets = pairsOfEntitySetsGeneratorServiceImpl.getPairsOfEntitySets();
		assertEquals(1, pairsOfEntitySets.size());
		assertTrue(pairsOfEntitySets.contains(pairOfEntitySets));
	}

	class ELRGenotypeBitVectorIndividualStub extends ELRGenotypeBitVectorIndividual {

		@Override
		public void setGenomeLength(int length) {
			super.genome = new boolean[length];
		}

		@Override
		public String toString() {
			return "Stub: " + super.genome.toString();
		}
	}

}
