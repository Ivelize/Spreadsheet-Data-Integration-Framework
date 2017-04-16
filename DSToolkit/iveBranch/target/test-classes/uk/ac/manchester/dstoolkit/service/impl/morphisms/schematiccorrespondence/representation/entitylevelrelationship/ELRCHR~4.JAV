package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;

public class ELRChromosomeTestConstructorAndPublicHelperMethodsOverMatchedSuperAbstractsInSourceSchemas {

	private ELRChromosome elrChromosome;
	private LinkedHashSet<Schema> sourceSchemas, targetSchemas;
	private List<Matching> matchings;

	private SuperAbstract sa1, sa2, sa3, sa4, sa5, sa6;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9, sl10, sl11, sl12, sl13, sl14;

	@Before
	public void setUp() throws Exception {
		//TODO equivalentSuperLexicalsIdentifierService and derivedOneToOneMatchingsGeneratorService not tested from here, only tested separately

		Schema schema1 = new Schema("schema1", null);
		Schema schema2 = new Schema("schema2", null);

		sourceSchemas = new LinkedHashSet<Schema>();
		sourceSchemas.add(schema1);
		targetSchemas = new LinkedHashSet<Schema>();
		targetSchemas.add(schema2);

		sa1 = new SuperAbstract("sa1", schema1);
		sa2 = new SuperAbstract("sa2", schema1);
		sa3 = new SuperAbstract("sa3", schema1);

		sl1 = new SuperLexical("sl1", schema1);
		sl2 = new SuperLexical("sl2", schema1);
		sl3 = new SuperLexical("sl3", schema1);
		sl4 = new SuperLexical("sl4", schema1);
		sl5 = new SuperLexical("sl5", schema1);
		sl6 = new SuperLexical("sl6", schema1);
		sl7 = new SuperLexical("sl7", schema1);
		sl8 = new SuperLexical("sl8", schema1);

		sa1.addSuperLexical(sl1);
		sa1.addSuperLexical(sl2);
		sa1.addSuperLexical(sl3);

		sa2.addSuperLexical(sl4);
		sa2.addSuperLexical(sl5);

		sa3.addSuperLexical(sl6);
		sa3.addSuperLexical(sl7);
		sa3.addSuperLexical(sl8);

		sa4 = new SuperAbstract("sa4", schema2);
		sa5 = new SuperAbstract("sa5", schema2);
		sa6 = new SuperAbstract("sa6", schema2);

		sl9 = new SuperLexical("sl9", schema2);
		sl10 = new SuperLexical("sl10", schema2);
		sl11 = new SuperLexical("sl11", schema2);
		sl12 = new SuperLexical("sl12", schema2);
		sl13 = new SuperLexical("sl13", schema2);
		sl14 = new SuperLexical("sl14", schema2);

		sa4.addSuperLexical(sl9);
		sa4.addSuperLexical(sl10);

		sa5.addSuperLexical(sl11);
		sa5.addSuperLexical(sl12);
		sa5.addSuperLexical(sl13);

		matchings = new ArrayList<Matching>();

		matchings.add(new OneToOneMatching(sa1, sa4, 0.3d, null));
		matchings.add(new OneToOneMatching(sl1, sl10, 0.9d, null));
		matchings.add(new OneToOneMatching(sl2, sl12, 0.4d, null));
		matchings.add(new OneToOneMatching(sl3, sl9, 0.8d, null));
		matchings.add(new OneToOneMatching(sl6, sl10, 0.2d, null));
		matchings.add(new OneToOneMatching(sl7, sl11, 0.5d, null));
		matchings.add(new OneToOneMatching(sl8, sl13, 0.3d, null));

		elrChromosome = new ELRChromosome(sourceSchemas, targetSchemas, matchings);
	}

	@Test
	public void testELRChromosome() {
		Set<SuperAbstract> superAbstractsInSource = elrChromosome.getSuperAbstractsInSourceSchemas();
		assertEquals(3, superAbstractsInSource.size());
		assertTrue(superAbstractsInSource.contains(sa1));
		assertTrue(superAbstractsInSource.contains(sa2));
		assertTrue(superAbstractsInSource.contains(sa3));

		Set<SuperAbstract> superAbstractsInTarget = elrChromosome.getSuperAbstractsInTargetSchemas();
		assertEquals(3, superAbstractsInTarget.size());
		assertTrue(superAbstractsInTarget.contains(sa4));
		assertTrue(superAbstractsInTarget.contains(sa5));
		assertTrue(superAbstractsInTarget.contains(sa6));

		Map<SuperAbstract, Set<SuperAbstract>> matchedSourceSuperAbstractTargetSuperAbstractsMap = elrChromosome
				.getMatchedSourceSuperAbstractTargetSuperAbstractsMap();
		assertEquals(2, matchedSourceSuperAbstractTargetSuperAbstractsMap.size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa1));
		assertEquals(2, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).contains(sa4));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa1).contains(sa5));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.containsKey(sa3));
		assertEquals(2, matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).size());
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).contains(sa4));
		assertTrue(matchedSourceSuperAbstractTargetSuperAbstractsMap.get(sa3).contains(sa5));

		Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas = elrChromosome.getMatchedSuperAbstractsInSourceSchemas();
		assertEquals(2, matchedSuperAbstractsInSourceSchemas.size());
		assertTrue(matchedSuperAbstractsInSourceSchemas.contains(sa1));
		assertTrue(matchedSuperAbstractsInSourceSchemas.contains(sa3));

		Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas = elrChromosome.getMatchedSuperAbstractsInTargetSchemas();
		assertEquals(2, matchedSuperAbstractsInTargetSchemas.size());
		assertTrue(matchedSuperAbstractsInTargetSchemas.contains(sa4));
		assertTrue(matchedSuperAbstractsInTargetSchemas.contains(sa5));

		List<SuperAbstract[]> chromosomeOfPairsOfSuperAbstracts = elrChromosome.getChromosomeOfPairsOfSuperAbstracts();
		assertEquals(4, chromosomeOfPairsOfSuperAbstracts.size());

		for (SuperAbstract[] pairs : chromosomeOfPairsOfSuperAbstracts) {
			assertTrue((pairs[0].equals(sa1) && pairs[1].equals(sa4)) || (pairs[0].equals(sa1) && pairs[1].equals(sa5))
					|| (pairs[0].equals(sa3) && pairs[1].equals(sa4)) || (pairs[0].equals(sa3) && pairs[1].equals(sa5)));
		}

		/*
		//TODO fragile part of test, order isn't guaranteed to be the same all the time ... 
		assertEquals(sa1, chromosomeOfPairsOfSuperAbstracts.get(2)[0]);
		assertEquals(sa4, chromosomeOfPairsOfSuperAbstracts.get(2)[1]);
		assertEquals(sa1, chromosomeOfPairsOfSuperAbstracts.get(3)[0]);
		assertEquals(sa5, chromosomeOfPairsOfSuperAbstracts.get(3)[1]);
		assertEquals(sa3, chromosomeOfPairsOfSuperAbstracts.get(0)[0]);
		assertEquals(sa4, chromosomeOfPairsOfSuperAbstracts.get(0)[1]);
		assertEquals(sa3, chromosomeOfPairsOfSuperAbstracts.get(1)[0]);
		assertEquals(sa5, chromosomeOfPairsOfSuperAbstracts.get(1)[1]);
		 */
	}

	@Test
	public void testIsMatched() {
		assertTrue(elrChromosome.isMatched(sa1));
		assertTrue(elrChromosome.isMatched(sa3));
		assertTrue(elrChromosome.isMatched(sa4));
		assertTrue(elrChromosome.isMatched(sa5));
		assertFalse(elrChromosome.isMatched(sa2));
		assertFalse(elrChromosome.isMatched(sa6));
	}

	@Test
	public void testAreMatchedWithEachOther() {
		assertTrue(elrChromosome.areMatchedWithEachOther(sa1, sa4));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa4, sa1));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa1, sa5));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa5, sa1));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa3, sa4));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa4, sa3));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa3, sa5));
		assertTrue(elrChromosome.areMatchedWithEachOther(sa5, sa3));

		assertFalse(elrChromosome.areMatchedWithEachOther(sa1, sa6));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa6, sa1));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa2, sa4));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa4, sa2));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa2, sa5));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa5, sa2));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa2, sa6));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa6, sa2));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa3, sa6));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa6, sa3));

		assertFalse(elrChromosome.areMatchedWithEachOther(sa1, sa2));
		assertFalse(elrChromosome.areMatchedWithEachOther(sa2, sa1));
	}

	@Test
	public void testGetTargetSuperAbstractsMatchedWithSourceSuperAbstract() {
		assertEquals(2, elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa1).size());
		assertTrue(elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa1).contains(sa4));
		assertTrue(elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa1).contains(sa5));
		assertEquals(0, elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa2).size());
		assertEquals(2, elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa3).size());
		assertTrue(elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa3).contains(sa4));
		assertTrue(elrChromosome.getTargetSuperAbstractsMatchedWithSourceSuperAbstract(sa3).contains(sa5));
	}

}
