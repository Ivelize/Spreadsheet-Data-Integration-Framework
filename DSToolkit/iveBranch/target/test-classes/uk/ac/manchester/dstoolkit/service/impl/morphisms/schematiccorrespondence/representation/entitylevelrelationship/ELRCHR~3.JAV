package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;

public class ELRChromosomeTestGetPairOfMatchedSourceAndTargetSuperAbstractsFromOneToOneMatching {

	private ELRChromosome chromosome;

	@Before
	public void setUp() throws Exception {
		chromosome = new ELRChromosome(null, null, null);
	}

	@Test
	public void testGetPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatchingNoSuperAbstractsInSchemas() {
		Set<SuperAbstract> superAbstractsInSource = new HashSet<SuperAbstract>();
		Set<SuperAbstract> superAbstractsInTarget = new HashSet<SuperAbstract>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		OneToOneMatching oneToOneMatching = new OneToOneMatching(sa1, sa2, 0.3, null);

		SuperAbstract[] sourceTargetSuperAbstract = chromosome.getPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatching(
				superAbstractsInSource, superAbstractsInTarget, oneToOneMatching);

		assertNull(sourceTargetSuperAbstract);
	}

	@Test
	public void testGetPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatchingMatchingBetweenSourceAndTargetSuperAbstracts() {
		Set<SuperAbstract> superAbstractsInSource = new HashSet<SuperAbstract>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		superAbstractsInSource.add(sa1);
		Set<SuperAbstract> superAbstractsInTarget = new HashSet<SuperAbstract>();
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		superAbstractsInTarget.add(sa2);
		OneToOneMatching oneToOneMatching = new OneToOneMatching(sa1, sa2, 0.3, null);

		SuperAbstract[] sourceTargetSuperAbstract = chromosome.getPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatching(
				superAbstractsInSource, superAbstractsInTarget, oneToOneMatching);

		assertNotNull(sourceTargetSuperAbstract);
		assertEquals(2, sourceTargetSuperAbstract.length);
		assertEquals(sa1, sourceTargetSuperAbstract[0]);
		assertEquals(sa2, sourceTargetSuperAbstract[1]);
	}

	@Test
	public void testGetPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatchingMatchingBetweenTargetAndSourceSuperAbstracts() {
		Set<SuperAbstract> superAbstractsInSource = new HashSet<SuperAbstract>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		superAbstractsInSource.add(sa1);
		Set<SuperAbstract> superAbstractsInTarget = new HashSet<SuperAbstract>();
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		superAbstractsInTarget.add(sa2);
		OneToOneMatching oneToOneMatching = new OneToOneMatching(sa2, sa1, 0.3, null);

		SuperAbstract[] sourceTargetSuperAbstract = chromosome.getPairOfMatchedSourceAndTargetSuperAbstractFromOneToOneMatching(
				superAbstractsInSource, superAbstractsInTarget, oneToOneMatching);

		assertNotNull(sourceTargetSuperAbstract);
		assertEquals(2, sourceTargetSuperAbstract.length);
		assertEquals(sa1, sourceTargetSuperAbstract[0]);
		assertEquals(sa2, sourceTargetSuperAbstract[1]);
	}

}
