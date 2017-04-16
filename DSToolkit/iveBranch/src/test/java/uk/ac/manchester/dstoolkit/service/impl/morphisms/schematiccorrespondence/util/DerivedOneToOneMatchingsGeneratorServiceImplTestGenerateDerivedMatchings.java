/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatchingsGeneratorServiceImplTestGenerateDerivedMatchings {

	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;

	private LinkedHashSet<Schema> sourceSchemas;
	private LinkedHashSet<Schema> targetSchemas;
	private List<Matching> matchings;

	private SuperAbstract sa1, sa2;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7;
	private DerivedOneToOneMatching dm1, dm2, dm3, dm4, dm5, dm6;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();

		sourceSchemas = new LinkedHashSet<Schema>();
		targetSchemas = new LinkedHashSet<Schema>();
		matchings = new ArrayList<Matching>();

		Schema schema1 = new Schema("schema1", null);
		sourceSchemas.add(schema1);
		Schema schema2 = new Schema("schema2", null);
		targetSchemas.add(schema2);

		sa1 = new SuperAbstract("sa1", schema1);
		sa2 = new SuperAbstract("sa2", schema2);
		Matching m1 = new OneToOneMatching(sa1, sa2, 0.3, "NGram");
		matchings.add(m1);

		sl1 = new SuperLexical("sl1", schema1);
		sa1.addSuperLexical(sl1);
		sl2 = new SuperLexical("sl2", schema2);
		sa2.addSuperLexical(sl2);
		Matching m2 = new OneToOneMatching(sl1, sl2, 0.54, "NGram");
		Matching m3 = new OneToOneMatching(sl1, sl2, 0.4, "Instance");
		matchings.add(m2);
		matchings.add(m3);

		sl3 = new SuperLexical("sl3", schema2);
		sa2.addSuperLexical(sl3);
		Matching m4 = new OneToOneMatching(sl1, sl3, 0.5, "NGram");
		matchings.add(m4);

		sl4 = new SuperLexical("sl4", schema1);
		sa1.addSuperLexical(sl4);
		sl5 = new SuperLexical("sl5", schema2);
		sa2.addSuperLexical(sl5);
		Matching m5 = new OneToOneMatching(sl5, sl4, 0.61, "NGram");
		Matching m6 = new OneToOneMatching(sl4, sl5, 0.4, "Instance");
		matchings.add(m5);
		matchings.add(m6);

		sl6 = new SuperLexical("sl6", schema2);
		sa2.addSuperLexical(sl6);
		Matching m7 = new OneToOneMatching(sl4, sl6, 0.9, "NGram");
		matchings.add(m7);

		sl7 = new SuperLexical("sl7", schema2);
		sa2.addSuperLexical(sl7);
		Matching m8 = new OneToOneMatching(sl4, sl7, 0.82, "NGram");
		matchings.add(m8);

		dm1 = new DerivedOneToOneMatching(sa1, sa2);
		dm1.setNumberOfMatchings(1);
		dm1.setSumOfMatchingScores(0.3);
		dm2 = new DerivedOneToOneMatching(sl1, sl2);
		dm2.setNumberOfMatchings(2);
		dm2.setSumOfMatchingScores(0.94);
		dm3 = new DerivedOneToOneMatching(sl1, sl3);
		dm3.setNumberOfMatchings(1);
		dm3.setSumOfMatchingScores(0.5);
		dm4 = new DerivedOneToOneMatching(sl4, sl5);
		dm4.setNumberOfMatchings(2);
		dm4.setSumOfMatchingScores(1.01);
		dm5 = new DerivedOneToOneMatching(sl4, sl6);
		dm5.setNumberOfMatchings(1);
		dm5.setSumOfMatchingScores(0.9);
		dm6 = new DerivedOneToOneMatching(sl4, sl7);
		dm6.setNumberOfMatchings(1);
		dm6.setSumOfMatchingScores(0.82);

	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorServiceImpl#generateDerivedMatchings(java.util.Map)}.
	 */
	@Test
	public void testGenerateDerivedMatchings() {
		List<DerivedOneToOneMatching> derMatchings = derivedOneToOneMatchingsGeneratorServiceImpl.generateDerivedMatchings(sourceSchemas,
				targetSchemas, matchings);
		assertEquals(6, derMatchings.size());
		assertTrue(derMatchings.contains(dm1));
		assertTrue(derMatchings.contains(dm2));
		assertTrue(derMatchings.contains(dm3));
		assertTrue(derMatchings.contains(dm4));
		assertTrue(derMatchings.contains(dm5));
		assertTrue(derMatchings.contains(dm6));

		Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> sourceSuperAbstractTargetSuperAbstractDerivedMatchingsMap = derivedOneToOneMatchingsGeneratorServiceImpl
				.getSourceSuperAbstractTargetSuperAbstractsDerivedOneToOneMatchingMap();
		assertEquals(1, sourceSuperAbstractTargetSuperAbstractDerivedMatchingsMap.size());
		assertTrue(sourceSuperAbstractTargetSuperAbstractDerivedMatchingsMap.containsKey(sa1));
		assertEquals(1, sourceSuperAbstractTargetSuperAbstractDerivedMatchingsMap.get(sa1).size());
		assertTrue(sourceSuperAbstractTargetSuperAbstractDerivedMatchingsMap.get(sa1).containsKey(sa2));
		assertEquals(dm1, sourceSuperAbstractTargetSuperAbstractDerivedMatchingsMap.get(sa1).get(sa2));

		Map<SuperAbstract, Map<SuperAbstract, DerivedOneToOneMatching>> targetSuperAbstractSourceSuperAbstractDerivedMatchingsMap = derivedOneToOneMatchingsGeneratorServiceImpl
				.getTargetSuperAbstractSourceSuperAbstractsDerivedOneToOneMatchingMap();
		assertEquals(1, targetSuperAbstractSourceSuperAbstractDerivedMatchingsMap.size());
		assertTrue(targetSuperAbstractSourceSuperAbstractDerivedMatchingsMap.containsKey(sa2));
		assertEquals(1, targetSuperAbstractSourceSuperAbstractDerivedMatchingsMap.get(sa2).size());
		assertTrue(targetSuperAbstractSourceSuperAbstractDerivedMatchingsMap.get(sa2).containsKey(sa1));
		assertEquals(dm1, targetSuperAbstractSourceSuperAbstractDerivedMatchingsMap.get(sa2).get(sa1));

		Map<SuperLexical, Map<SuperLexical, DerivedOneToOneMatching>> sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap = derivedOneToOneMatchingsGeneratorServiceImpl
				.getSourceSuperLexicalTargetSuperLexicalsDerivedOneToOneMatchingMap();
		assertEquals(2, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.size());
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.containsKey(sl1));
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.containsKey(sl4));
		assertEquals(2, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl1).size());
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl1).containsKey(sl2));
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl1).containsKey(sl3));
		assertEquals(dm2, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl1).get(sl2));
		assertEquals(dm3, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl1).get(sl3));
		assertEquals(3, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).size());
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).containsKey(sl5));
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).containsKey(sl6));
		assertTrue(sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).containsKey(sl7));
		assertEquals(dm4, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).get(sl5));
		assertEquals(dm5, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).get(sl6));
		assertEquals(dm6, sourceSuperLexicalTargetSuperLexicalDerivedMatchingsMap.get(sl4).get(sl7));
	}
}
