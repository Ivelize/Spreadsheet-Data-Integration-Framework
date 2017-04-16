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

public class FeasibilityCheckerServiceImplTestAreMatchedWithEachOther {

	private FeasibilityCheckerServiceImpl feasibilityCheckerServiceImpl;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6;
	private Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap;

	@Before
	public void setUp() throws Exception {
		feasibilityCheckerServiceImpl = new FeasibilityCheckerServiceImpl();
		matchedSourceSuperAbstractTargetSuperAbstractsMap = new HashMap<SuperAbstract, Set<SuperAbstract>>();

		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);
		superAbstract4 = new SuperAbstract("sa4", null);
		superAbstract5 = new SuperAbstract("sa5", null);
		superAbstract6 = new SuperAbstract("sa6", null);

		Set<SuperAbstract> matchedWithSuperAbstract1 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract1.add(superAbstract4);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract1, matchedWithSuperAbstract1);

		Set<SuperAbstract> matchedWithSuperAbstract3 = new HashSet<SuperAbstract>();
		matchedWithSuperAbstract3.add(superAbstract5);
		matchedSourceSuperAbstractTargetSuperAbstractsMap.put(superAbstract3, matchedWithSuperAbstract3);
	}

	@Test
	public void testAreMatchedWithEachOtherSuperAbstractsNotMatchedAtAllBothDirections() {
		assertFalse(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract2, superAbstract6,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
		assertFalse(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract6, superAbstract2,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAreMatchedWithEachOtherSuperAbstractsMatchedSourceToTarget() {
		assertTrue(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract1, superAbstract4,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
		assertTrue(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract3, superAbstract5,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAreMatchedWithEachOtherSuperAbstractsMatchedTargetToSource() {
		assertTrue(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract4, superAbstract1,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
		assertTrue(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract5, superAbstract3,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAreMatchedWithEachOtherSuperAbstractsMatchedButNotWithEachOtherSourceToTarget() {
		assertFalse(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract1, superAbstract5,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
		assertFalse(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract3, superAbstract4,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}

	@Test
	public void testAreMatchedWithEachOtherSuperAbstractsMatchedButNotWithEachOtherTargetToSource() {
		assertFalse(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract5, superAbstract1,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
		assertFalse(feasibilityCheckerServiceImpl.areMatchedWithEachOther(superAbstract4, superAbstract3,
				matchedSourceSuperAbstractTargetSuperAbstractsMap));
	}
}
