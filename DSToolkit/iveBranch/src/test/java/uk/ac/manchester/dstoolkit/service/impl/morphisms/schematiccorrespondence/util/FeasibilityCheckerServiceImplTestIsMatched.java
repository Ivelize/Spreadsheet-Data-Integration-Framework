package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

public class FeasibilityCheckerServiceImplTestIsMatched {

	private FeasibilityCheckerServiceImpl feasibilityCheckerServiceImpl;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6;
	private Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas;
	private Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas;

	@Before
	public void setUp() throws Exception {
		feasibilityCheckerServiceImpl = new FeasibilityCheckerServiceImpl();
		matchedSuperAbstractsInSourceSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInTargetSchemas = new HashSet<SuperAbstract>();

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
	}

	@Test
	public void testIsMatchedSuperAbstractNotMatched() {
		assertFalse(feasibilityCheckerServiceImpl.isMatched(superAbstract2, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas));
		assertFalse(feasibilityCheckerServiceImpl.isMatched(superAbstract6, matchedSuperAbstractsInSourceSchemas,
				matchedSuperAbstractsInTargetSchemas));
	}

	@Test
	public void testIsMatchedSourceSuperAbstractMatched() {
		assertTrue(feasibilityCheckerServiceImpl
				.isMatched(superAbstract1, matchedSuperAbstractsInSourceSchemas, matchedSuperAbstractsInTargetSchemas));
		assertTrue(feasibilityCheckerServiceImpl
				.isMatched(superAbstract3, matchedSuperAbstractsInSourceSchemas, matchedSuperAbstractsInTargetSchemas));
	}

	@Test
	public void testIsMatchedTargetSuperAbstractMatched() {
		assertTrue(feasibilityCheckerServiceImpl
				.isMatched(superAbstract4, matchedSuperAbstractsInSourceSchemas, matchedSuperAbstractsInTargetSchemas));
		assertTrue(feasibilityCheckerServiceImpl
				.isMatched(superAbstract5, matchedSuperAbstractsInSourceSchemas, matchedSuperAbstractsInTargetSchemas));
	}

}
