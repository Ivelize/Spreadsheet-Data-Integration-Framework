package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.HorizontalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.SingleVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VerticalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.FeasibilityCheckerServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.ec.DecoderService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.FeasibilityCheckerService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorService;
import ec.EvolutionState;
import ec.util.MersenneTwisterFast;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class FeasibleInitializerServiceImplTestCheckFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt {

	private FeasibleInitializerServiceImpl feasibleInitializerServiceImpl;
	private SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl;
	private ELRGenotypeBitVectorIndividualStub elrGenotypeBitVectorIndividualStub;
	private ELRChromosomeStub elrChromosomeStub;
	private PairsOfEntitySetsGeneratorService pairsOfEntitySetsGeneratorService;
	private FeasibilityCheckerService feasibilityChecker;
	private DecoderService decoderService;
	private int thread;
	private EvolutionState state;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6, superAbstract7,
			superAbstract8;

	@Before
	public void setUp() throws Exception {
		elrChromosomeStub = new ELRChromosomeStub(null, null, null);

		pairsOfEntitySetsGeneratorService = new PairsOfEntitySetsGeneratorServiceImpl();
		feasibilityChecker = new FeasibilityCheckerServiceImpl();

		searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl = new SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl();
		searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl
				.setPairsOfEntitySetsGeneratorService(pairsOfEntitySetsGeneratorService);
		searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl.setFeasibilityChecker(feasibilityChecker);

		decoderService = new DecoderServiceImpl();
		decoderService.setChromosome(elrChromosomeStub);

		feasibleInitializerServiceImpl = new FeasibleInitializerServiceImpl();
		feasibleInitializerServiceImpl
				.setSearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualService(searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl);
		feasibleInitializerServiceImpl.setDecoderService(decoderService);
		feasibleInitializerServiceImpl.setFeasibilityChecker(feasibilityChecker);
		feasibleInitializerServiceImpl.setPairsOfEntitySetsGeneratorService(pairsOfEntitySetsGeneratorService);

		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(6);

		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);
		superAbstract4 = new SuperAbstract("sa4", null);
		superAbstract5 = new SuperAbstract("sa5", null);
		superAbstract6 = new SuperAbstract("sa6", null);
		superAbstract7 = new SuperAbstract("sa7", null);
		superAbstract8 = new SuperAbstract("sa8", null);

		Set<SuperAbstract> superAbstractsInSourceSchemas = new HashSet<SuperAbstract>();
		superAbstractsInSourceSchemas.add(superAbstract1);
		superAbstractsInSourceSchemas.add(superAbstract2);
		superAbstractsInSourceSchemas.add(superAbstract3);
		superAbstractsInSourceSchemas.add(superAbstract4);
		elrChromosomeStub.setSuperAbstractsInSourceSchemas(superAbstractsInSourceSchemas);

		Set<SuperAbstract> superAbstractsInTargetSchemas = new HashSet<SuperAbstract>();
		superAbstractsInTargetSchemas.add(superAbstract5);
		superAbstractsInTargetSchemas.add(superAbstract6);
		superAbstractsInTargetSchemas.add(superAbstract7);
		superAbstractsInTargetSchemas.add(superAbstract8);
		elrChromosomeStub.setSuperAbstractsInTargetSchemas(superAbstractsInTargetSchemas);

		List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts = new ArrayList<SuperAbstract[]>();
		SuperAbstract[] pair0 = { superAbstract1, superAbstract5 };
		SuperAbstract[] pair1 = { superAbstract1, superAbstract6 };
		SuperAbstract[] pair2 = { superAbstract2, superAbstract8 };
		SuperAbstract[] pair3 = { superAbstract3, superAbstract5 };
		SuperAbstract[] pair4 = { superAbstract3, superAbstract7 };
		SuperAbstract[] pair5 = { superAbstract4, superAbstract7 };
		chromosomeOfPairsOfSuperAbstracts.add(pair0);
		chromosomeOfPairsOfSuperAbstracts.add(pair1);
		chromosomeOfPairsOfSuperAbstracts.add(pair2);
		chromosomeOfPairsOfSuperAbstracts.add(pair3);
		chromosomeOfPairsOfSuperAbstracts.add(pair4);
		chromosomeOfPairsOfSuperAbstracts.add(pair5);
		elrChromosomeStub.setChromosomeOfPairsOfSuperAbstracts(chromosomeOfPairsOfSuperAbstracts);

		Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInSourceSchemas.add(superAbstract1);
		matchedSuperAbstractsInSourceSchemas.add(superAbstract2);
		matchedSuperAbstractsInSourceSchemas.add(superAbstract3);
		matchedSuperAbstractsInSourceSchemas.add(superAbstract4);
		elrChromosomeStub.setMatchedSuperAbstractsInSourceSchemas(matchedSuperAbstractsInSourceSchemas);

		Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInTargetSchemas.add(superAbstract5);
		matchedSuperAbstractsInTargetSchemas.add(superAbstract6);
		matchedSuperAbstractsInTargetSchemas.add(superAbstract7);
		matchedSuperAbstractsInTargetSchemas.add(superAbstract8);
		elrChromosomeStub.setMatchedSuperAbstractsInTargetSchemas(matchedSuperAbstractsInTargetSchemas);

		Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap = new HashMap<SuperAbstract, Set<SuperAbstract>>();
		Set<SuperAbstract> matchedWithSuperAbstract1 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract1.add(superAbstract5);
		matchedWithSuperAbstract1.add(superAbstract6);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract1, matchedWithSuperAbstract1);
		Set<SuperAbstract> matchedWithSuperAbstract2 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract2.add(superAbstract8);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract2, matchedWithSuperAbstract2);
		Set<SuperAbstract> matchedWithSuperAbstract3 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract3.add(superAbstract5);
		matchedWithSuperAbstract3.add(superAbstract7);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract3, matchedWithSuperAbstract3);
		Set<SuperAbstract> matchedWithSuperAbstract4 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract4.add(superAbstract7);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract4, matchedWithSuperAbstract4);
		elrChromosomeStub.setMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap);

		state = new EvolutionState();
		state.random = new MersenneTwisterFast[1];
		state.random[0] = new MersenneTwisterFast(2341);
		thread = 0;
	}

	@Test
	public void testCheckFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForItFeasibleIndividual() {
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = true;
		elrGenotypeBitVectorIndividualStub.genome[4] = false;
		elrGenotypeBitVectorIndividualStub.genome[5] = true;

		ELRPhenotype phenotype = feasibleInitializerServiceImpl.checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(
				elrGenotypeBitVectorIndividualStub, state, thread);

		assertNotNull(phenotype.getChromosome());
		assertEquals(elrChromosomeStub, phenotype.getChromosome());
		assertEquals(2, phenotype.getElrs().size());
		assertEquals(1, phenotype.getMatchedButUnassociatedSourceEntities().size());
		assertEquals(2, phenotype.getMatchedButUnassociatedTargetEntities().size());
		assertTrue(phenotype.getMatchedButUnassociatedSourceEntities().contains(superAbstract2));
		assertTrue(phenotype.getMatchedButUnassociatedTargetEntities().contains(superAbstract6));
		assertTrue(phenotype.getMatchedButUnassociatedTargetEntities().contains(superAbstract8));

		for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
			assertNotNull(elr.getChromosome());
			assertEquals(elrChromosomeStub, elr.getChromosome());
			assertNotNull(elr.getPhenotype());
			assertEquals(phenotype, elr.getPhenotype());
		}

		Iterator<ELREntityLevelRelationship> elrIt = phenotype.getElrs().iterator();
		ELREntityLevelRelationship elr1 = elrIt.next();
		ELREntityLevelRelationship elr2 = elrIt.next();
		assertNotNull(elr1);
		assertNotNull(elr2);
		assertFalse(elr1.equals(elr2));
		assertTrue((elr1.getNumberOfSourceEntities() == 1 && elr2.getNumberOfSourceEntities() == 2)
				|| (elr1.getNumberOfSourceEntities() == 2 && elr2.getNumberOfSourceEntities() == 1));
		if (elr1.getNumberOfSourceEntities() == 2) {
			assertEquals(2, elr1.getNumberOfSourceEntities());
			assertEquals(1, elr1.getNumberOfTargetEntities());
			assertTrue(elr1.getSourceEntitySet().contains(superAbstract1));
			assertTrue(elr1.getSourceEntitySet().contains(superAbstract3));
			assertTrue(elr1.getTargetEntitySet().contains(superAbstract5));
			assertEquals(2, elr1.getSourceVectors().size());
			assertTrue(elr1.getSourceVectors().get(0) instanceof HorizontalPartitioningVector
					|| elr1.getSourceVectors().get(0) instanceof VerticalPartitioningVector);
			assertTrue(elr1.getSourceVectors().get(1) instanceof HorizontalPartitioningVector
					|| elr1.getSourceVectors().get(1) instanceof VerticalPartitioningVector);
			assertTrue((elr1.getSourceVectors().get(0) instanceof HorizontalPartitioningVector && elr1.getSourceVectors().get(1) instanceof VerticalPartitioningVector)
					|| (elr1.getSourceVectors().get(0) instanceof VerticalPartitioningVector && elr1.getSourceVectors().get(1) instanceof HorizontalPartitioningVector));
			assertEquals(1, elr1.getTargetVectors().size());
			assertTrue(elr1.getTargetVectors().get(0) instanceof SingleVector);

			assertEquals(1, elr2.getNumberOfSourceEntities());
			assertEquals(1, elr2.getNumberOfTargetEntities());
			assertTrue(elr2.getSourceEntitySet().contains(superAbstract4));
			assertTrue(elr2.getTargetEntitySet().contains(superAbstract7));
			assertTrue(elr2.getSourceVectors().get(0) instanceof SingleVector);
			assertTrue(elr2.getTargetVectors().get(0) instanceof SingleVector);
		} else if (elr1.getNumberOfSourceEntities() == 1) {
			assertEquals(1, elr1.getNumberOfSourceEntities());
			assertEquals(1, elr1.getNumberOfTargetEntities());
			assertTrue(elr1.getSourceEntitySet().contains(superAbstract4));
			assertTrue(elr1.getTargetEntitySet().contains(superAbstract7));
			assertTrue(elr1.getSourceVectors().get(0) instanceof SingleVector);
			assertTrue(elr1.getTargetVectors().get(0) instanceof SingleVector);

			assertEquals(2, elr2.getNumberOfSourceEntities());
			assertEquals(1, elr2.getNumberOfTargetEntities());
			assertTrue(elr2.getSourceEntitySet().contains(superAbstract1));
			assertTrue(elr2.getSourceEntitySet().contains(superAbstract3));
			assertTrue(elr2.getTargetEntitySet().contains(superAbstract5));
			assertEquals(2, elr2.getSourceVectors().size());
			assertTrue(elr2.getSourceVectors().get(0) instanceof HorizontalPartitioningVector
					|| elr2.getSourceVectors().get(0) instanceof VerticalPartitioningVector);
			assertTrue(elr2.getSourceVectors().get(1) instanceof HorizontalPartitioningVector
					|| elr2.getSourceVectors().get(1) instanceof VerticalPartitioningVector);
			assertTrue((elr2.getSourceVectors().get(0) instanceof HorizontalPartitioningVector && elr2.getSourceVectors().get(1) instanceof VerticalPartitioningVector)
					|| (elr2.getSourceVectors().get(0) instanceof VerticalPartitioningVector && elr2.getSourceVectors().get(1) instanceof HorizontalPartitioningVector));
			assertEquals(1, elr2.getTargetVectors().size());
			assertTrue(elr2.getTargetVectors().get(0) instanceof SingleVector);
		}
	}

	@Test
	public void testCheckFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForItInFeasibleIndividual() {
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = true;
		elrGenotypeBitVectorIndividualStub.genome[4] = true;
		elrGenotypeBitVectorIndividualStub.genome[5] = false;

		ELRPhenotype phenotype = feasibleInitializerServiceImpl.checkFeasibilityGenerateFeasibleIndividualAndGeneratePhenotypeForIt(
				elrGenotypeBitVectorIndividualStub, state, thread);

		assertNotNull(phenotype.getChromosome());
		assertEquals(elrChromosomeStub, phenotype.getChromosome());

		for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
			assertNotNull(elr.getChromosome());
			assertEquals(elrChromosomeStub, elr.getChromosome());
			assertNotNull(elr.getPhenotype());
			assertEquals(phenotype, elr.getPhenotype());
		}
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

	class ELRChromosomeStub extends ELRChromosome {

		public ELRChromosomeStub(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas, List<Matching> matchings) {
			super(sourceSchemas, targetSchemas, matchings);
		}

		@Override
		protected void setSuperAbstractsInSourceSchemas(Set<SuperAbstract> superAbstractsInSourceSchemas) {
			super.setSuperAbstractsInSourceSchemas(superAbstractsInSourceSchemas);
		}

		@Override
		protected void setSuperAbstractsInTargetSchemas(Set<SuperAbstract> superAbstractsInTargetSchemas) {
			super.setSuperAbstractsInTargetSchemas(superAbstractsInTargetSchemas);
		}

		@Override
		protected void setMatchedSuperAbstractsInSourceSchemas(Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas) {
			super.setMatchedSuperAbstractsInSourceSchemas(matchedSuperAbstractsInSourceSchemas);
		}

		@Override
		protected void setMatchedSuperAbstractsInTargetSchemas(Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas) {
			super.setMatchedSuperAbstractsInTargetSchemas(matchedSuperAbstractsInTargetSchemas);
		}

		@Override
		protected void setMatchedSourceSuperAbstractTargetSuperAbstractsMap(
				Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap) {
			super.setMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap);
		}

		@Override
		protected void setChromosomeOfPairsOfSuperAbstracts(List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts) {
			super.setChromosomeOfPairsOfSuperAbstracts(chromosomeOfPairsOfSuperAbstracts);
		}
	}

}
