/**
 * 
 */
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

/**
 * @author chedeler
 *
 */
public class ELRChromosomeTestPlaceMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap {

	private ELRChromosome elrChromosome;
	private Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		elrChromosome = new ELRChromosome(null, null, null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = new HashMap<SuperAbstract, Set<SuperAbstract>>();

	}

	@Test
	public void testPlaceMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMapTwoMatchedSuperAbstracts() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa2);

		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa1));
		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).contains(sa2));
	}

	@Test
	public void testPlaceMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMapAddTwoMatchedSuperAbstractsTwice() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa2);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa2);

		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa1));
		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).contains(sa2));
	}

	@Test
	public void testPlaceMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMapTwoDifferentPairsOfMatchedSuperAbstracts() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa2);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		SuperAbstract sa4 = new SuperAbstract("sa4", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa3, sa4);

		assertEquals(2, matchedSourceSuperAbstractTargetSuperAbstractsMap.size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa1));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa3));
		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).size());
		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).contains(sa2));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).contains(sa4));
	}

	@Test
	public void testPlaceMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMapTwoPairsOfMatchedSuperAbstractsWithSameSourceButDifferentTargetConstructs() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa2);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa3);

		Set<SuperAbstract> targetConstructs = new HashSet<SuperAbstract>();
		targetConstructs.add(sa2);
		targetConstructs.add(sa3);

		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa1));
		assertEquals(2, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).containsAll(targetConstructs));
	}

	@Test
	public void testPlaceMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMapTwoPairsOfMatchedSuperAbstractsWithDifferentSourceButSameTargetConstructs() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa1, sa2);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.placeMatchedSuperAbstractsInMatchedSourceSuperAbstractTargetSuperAbstractsMap(matchedSourceSuperAbstractTargetSuperAbstractsMap,
						sa3, sa2);

		assertEquals(2, matchedSourceSuperAbstractTargetSuperAbstractsMap.size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa1));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa3));
		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).size());
		assertEquals(1, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).contains(sa2));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).contains(sa2));
	}

}
