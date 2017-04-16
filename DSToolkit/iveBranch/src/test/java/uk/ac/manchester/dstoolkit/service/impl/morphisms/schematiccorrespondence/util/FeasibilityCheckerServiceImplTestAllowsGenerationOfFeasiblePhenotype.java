package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

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

public class FeasibilityCheckerServiceImplTestAllowsGenerationOfFeasiblePhenotype {

	private FeasibilityCheckerServiceImpl feasibilityCheckerServiceImpl;
	private Set<PairOfEntitySets> pairsOfEntitySets;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6;
	private Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas;
	private Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas;
	private Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap;

	@Before
	public void setUp() throws Exception {
		feasibilityCheckerServiceImpl = new FeasibilityCheckerServiceImpl();
		matchedSuperAbstractsInSourceSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInTargetSchemas = new HashSet<SuperAbstract>();
		matchedSourceSuperAbstractTargetSuperAbstractsMap = new HashMap<SuperAbstract, Set<SuperAbstract>>();

		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);
		superAbstract4 = new SuperAbstract("sa4", null);
		superAbstract5 = new SuperAbstract("sa5", null);
		superAbstract6 = new SuperAbstract("sa6", null);

		matchedSuperAbstractsInSourceSchemas.add(superAbstract1);
		matchedSuperAbstractsInSourceSchemas.add(superAbstract3);

		matchedSuperAbstractsInTargetSchemas.add(superAbstract4);
		matchedSuperAbstractsInTargetSchemas.add(superAbstract5);

		Set<SuperAbstract> matchedWithSuperAbstract1 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract1.add(superAbstract4);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract1, matchedWithSuperAbstract1);

		Set<SuperAbstract> matchedWithSuperAbstract3 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract3.add(superAbstract5);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract3, matchedWithSuperAbstract3);

		pairsOfEntitySets = new HashSet<PairOfEntitySets>();
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToBothSuperAbstractsNotMatchedAtAllSourceToTarget() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract2);
		targetSuperAbstracts1.add(superAbstract6);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToBothSuperAbstractsNotMatchedAtAllTargetToSource() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract6);
		targetSuperAbstracts1.add(superAbstract2);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToTargetSuperAbstractNotMatchedAtAllSourceToTarget() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract1);
		targetSuperAbstracts1.add(superAbstract6);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToTargetSuperAbstractNotMatchedAtAllTargetToSource() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract6);
		targetSuperAbstracts1.add(superAbstract1);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToSuperAbstractsMatchedButNotWithEachOtherSourceToTarget() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract1);
		targetSuperAbstracts1.add(superAbstract5);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToSuperAbstractsMatchedButNotWithEachOtherTargetToSource() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract5);
		targetSuperAbstracts1.add(superAbstract1);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToNotAllSuperAbstractsMatchedWithEachOtherSourceToTarget() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract1);
		sourceSuperAbstracts1.add(superAbstract3);
		targetSuperAbstracts1.add(superAbstract4);
		targetSuperAbstracts1.add(superAbstract5);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeNotFeasiblePhenotypeDueToNotAllSuperAbstractsMatchedWithEachOtherTargetToSource() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract5);
		sourceSuperAbstracts1.add(superAbstract4);
		targetSuperAbstracts1.add(superAbstract3);
		targetSuperAbstracts1.add(superAbstract1);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertFalse(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeFeasiblePhenotypeSourceToTarget() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract1);
		targetSuperAbstracts1.add(superAbstract4);
		Set<SuperAbstract> sourceSuperAbstracts2 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts2 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts2.add(superAbstract3);
		targetSuperAbstracts2.add(superAbstract5);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertTrue(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAllowsGenerationOfFeasiblePhenotypeFeasiblePhenotypeTargetToSource() {
		Set<SuperAbstract> sourceSuperAbstracts1 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts1 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts1.add(superAbstract4);
		targetSuperAbstracts1.add(superAbstract1);
		Set<SuperAbstract> sourceSuperAbstracts2 = new HashSet<SuperAbstract>();
		Set<SuperAbstract> targetSuperAbstracts2 = new HashSet<SuperAbstract>();
		sourceSuperAbstracts2.add(superAbstract5);
		targetSuperAbstracts2.add(superAbstract3);

		PairOfEntitySets pairOfEntitySets = new PairOfEntitySets(sourceSuperAbstracts1, targetSuperAbstracts1);
		pairsOfEntitySets.add(pairOfEntitySets);

		assertTrue(feasibilityCheckerServiceImpl.allowsGenerationOfFeasiblePhenotype(pairsOfEntitySets, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas, matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

}
