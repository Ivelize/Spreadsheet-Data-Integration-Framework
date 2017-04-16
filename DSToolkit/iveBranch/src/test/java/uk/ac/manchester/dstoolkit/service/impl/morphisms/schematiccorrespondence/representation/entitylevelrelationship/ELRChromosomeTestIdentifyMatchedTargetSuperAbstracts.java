package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

public class ELRChromosomeTestIdentifyMatchedTargetSuperAbstracts {

	private ELRChromosome chromosome;

	@Before
	public void setUp() throws Exception {
		chromosome = new ELRChromosome(null, null, null);
	}

	@Test
	public void testIdentifyMatchedTargetSuperAbstracts() {
		Map<SuperAbstract, Set<SuperAbstract>> mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts = new HashMap<SuperAbstract, Set<SuperAbstract>>();

		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		Set<SuperAbstract> matchedSuperAbstracts1 = new HashSet<SuperAbstract>();
		matchedSuperAbstracts1.add(sa3);
		mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts.put(sa1, matchedSuperAbstracts1);

		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		SuperAbstract sa4 = new SuperAbstract("sa4", null);
		SuperAbstract sa5 = new SuperAbstract("sa5", null);
		Set<SuperAbstract> matchedSuperAbstracts2 = new HashSet<SuperAbstract>();
		matchedSuperAbstracts2.add(sa4);
		matchedSuperAbstracts2.add(sa5);
		mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts.put(sa2, matchedSuperAbstracts2);

		Set<SuperAbstract> matchedTargetSuperAbstracts = chromosome
				.identifyMatchedTargetSuperAbstracts(mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts);

		assertEquals(3, matchedTargetSuperAbstracts.size());
		assertTrue(matchedTargetSuperAbstracts.contains(sa3));
		assertTrue(matchedTargetSuperAbstracts.contains(sa4));
		assertTrue(matchedTargetSuperAbstracts.contains(sa5));
	}

}
