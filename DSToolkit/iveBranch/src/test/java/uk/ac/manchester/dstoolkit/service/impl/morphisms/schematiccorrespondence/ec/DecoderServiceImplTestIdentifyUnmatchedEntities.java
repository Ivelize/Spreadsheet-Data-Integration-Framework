package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

public class DecoderServiceImplTestIdentifyUnmatchedEntities {

	private DecoderServiceImpl decoderServiceImpl;
	private Set<SuperAbstract> superAbstractsInSchemas;
	private Set<SuperAbstract> matchedSuperAbstractsInSchemas;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6;

	@Before
	public void setUp() throws Exception {
		decoderServiceImpl = new DecoderServiceImpl();
		superAbstractsInSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInSchemas = new HashSet<SuperAbstract>();

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
	public void testIdentifyUnmatchedEntitiesNoMatchedSuperAbstractsInSchemas() {
		Set<SuperAbstract> unmatchedEntities = decoderServiceImpl.identifyUnmatchedEntities(superAbstractsInSchemas, matchedSuperAbstractsInSchemas);
		assertEquals(6, unmatchedEntities.size());
		assertTrue(unmatchedEntities.containsAll(superAbstractsInSchemas));
	}

	@Test
	public void testIdentifyUnmatchedEntitiesWithMatchedSuperAbstractsInSchemas() {
		matchedSuperAbstractsInSchemas.add(superAbstract1);
		matchedSuperAbstractsInSchemas.add(superAbstract3);
		Set<SuperAbstract> unmatchedEntities = decoderServiceImpl.identifyUnmatchedEntities(superAbstractsInSchemas, matchedSuperAbstractsInSchemas);
		assertEquals(4, unmatchedEntities.size());
		assertTrue(unmatchedEntities.contains(superAbstract2));
		assertTrue(unmatchedEntities.contains(superAbstract4));
		assertTrue(unmatchedEntities.contains(superAbstract5));
		assertTrue(unmatchedEntities.contains(superAbstract6));
	}

}
