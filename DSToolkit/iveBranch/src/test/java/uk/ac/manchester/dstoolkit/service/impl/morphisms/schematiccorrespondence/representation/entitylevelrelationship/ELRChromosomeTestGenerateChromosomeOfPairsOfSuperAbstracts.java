package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

public class ELRChromosomeTestGenerateChromosomeOfPairsOfSuperAbstracts {

	private ELRChromosome chromosome;

	@Before
	public void setUp() throws Exception {
		chromosome = new ELRChromosome(null, null, null);
	}

	@Test
	public void testGenerateChromosomeOfPairsOfSuperAbstracts() {
		Map<SuperAbstract, Set<SuperAbstract>> mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts = new LinkedHashMap<SuperAbstract, Set<SuperAbstract>>();

		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		Set<SuperAbstract> matchedSuperAbstracts1 = new LinkedHashSet<SuperAbstract>();
		matchedSuperAbstracts1.add(sa3);
		mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts.put(sa1, matchedSuperAbstracts1);

		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		SuperAbstract sa4 = new SuperAbstract("sa4", null);
		SuperAbstract sa5 = new SuperAbstract("sa5", null);
		Set<SuperAbstract> matchedSuperAbstracts2 = new LinkedHashSet<SuperAbstract>();
		matchedSuperAbstracts2.add(sa4);
		matchedSuperAbstracts2.add(sa5);
		mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts.put(sa2, matchedSuperAbstracts2);

		List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts = chromosome
				.generateChromosomeOfPairsOfSuperAbstracts(mapOfMatchedSourceSuperAbstractAndTargetSuperAbstracts);

		assertEquals(3, chromosomeOfPairsOfSuperAbstracts.size());
		assertEquals(sa1, chromosomeOfPairsOfSuperAbstracts.get(0)[0]);
		assertEquals(sa3, chromosomeOfPairsOfSuperAbstracts.get(0)[1]);
		assertEquals(sa2, chromosomeOfPairsOfSuperAbstracts.get(1)[0]);
		assertEquals(sa4, chromosomeOfPairsOfSuperAbstracts.get(1)[1]);
		assertEquals(sa2, chromosomeOfPairsOfSuperAbstracts.get(2)[0]);
		assertEquals(sa5, chromosomeOfPairsOfSuperAbstracts.get(2)[1]);
	}

}
