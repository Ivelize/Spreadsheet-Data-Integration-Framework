package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRGenotypeBitVectorIndividual;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.FeasibilityCheckerService;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.PairsOfEntitySetsGeneratorService;
import ec.EvolutionState;
import ec.util.MersenneTwisterFast;

public class SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImplTestFindFeasibleIndividualByRandomlyChangingBitsBetweenTrueAndFalseAndViceVersa {

	private SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl;
	private ELRGenotypeBitVectorIndividualStub elrGenotypeBitVectorIndividualStub;
	private ELRChromosomeStub elrChromosomeStub;
	private PairsOfEntitySetsGeneratorService pairsOfEntitySetsGeneratorService;
	private FeasibilityCheckerService feasibilityChecker;
	private int thread;
	private EvolutionState state;

	@Before
	public void setUp() throws Exception {
		searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl = new SearchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl();
		pairsOfEntitySetsGeneratorService = new PairsOfEntitySetsGeneratorServiceImpl();
		feasibilityChecker = new FeasibilityCheckerServiceImpl();
		searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl
				.setPairsOfEntitySetsGeneratorService(pairsOfEntitySetsGeneratorService);
		searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl.setFeasibilityChecker(feasibilityChecker);

		elrChromosomeStub = new ELRChromosomeStub(null, null, null);

		elrGenotypeBitVectorIndividualStub = new ELRGenotypeBitVectorIndividualStub();
		elrGenotypeBitVectorIndividualStub.setGenomeLength(6);

		SuperAbstract superAbstract1 = new SuperAbstract("sa1", null);
		SuperAbstract superAbstract2 = new SuperAbstract("sa2", null);
		SuperAbstract superAbstract3 = new SuperAbstract("sa3", null);
		SuperAbstract superAbstract4 = new SuperAbstract("sa4", null);
		SuperAbstract superAbstract5 = new SuperAbstract("sa5", null);
		SuperAbstract superAbstract6 = new SuperAbstract("sa6", null);
		SuperAbstract superAbstract7 = new SuperAbstract("sa7", null);
		SuperAbstract superAbstract8 = new SuperAbstract("sa8", null);

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
	public void testFindFeasibleIndividualByRandomlyChangingBitsBetweenTrueAndFalseAndViceVersaStartWithInfeasibleIndividual() {
		elrGenotypeBitVectorIndividualStub.genome[0] = true;
		elrGenotypeBitVectorIndividualStub.genome[1] = false;
		elrGenotypeBitVectorIndividualStub.genome[2] = false;
		elrGenotypeBitVectorIndividualStub.genome[3] = true;
		elrGenotypeBitVectorIndividualStub.genome[4] = true;
		elrGenotypeBitVectorIndividualStub.genome[5] = false;

		assertFalse(searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl.isFeasibleIndividual(
				elrGenotypeBitVectorIndividualStub, elrChromosomeStub));

		ELRGenotypeBitVectorIndividual newIndividual = searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl
				.findFeasibleIndividualByRandomlyChangingBitsBetweenTrueAndFalseAndViceVersa(elrGenotypeBitVectorIndividualStub, elrChromosomeStub,
						state, thread);

		assertTrue(elrGenotypeBitVectorIndividualStub.equals(newIndividual));//same individual just with bits changed ... only has phenotype, so shouldn't be a problem
		assertTrue(searchNeighbourhoodOfInfeasibleIndividualForFeasibleIndividualServiceImpl.isFeasibleIndividual(newIndividual, elrChromosomeStub));
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
