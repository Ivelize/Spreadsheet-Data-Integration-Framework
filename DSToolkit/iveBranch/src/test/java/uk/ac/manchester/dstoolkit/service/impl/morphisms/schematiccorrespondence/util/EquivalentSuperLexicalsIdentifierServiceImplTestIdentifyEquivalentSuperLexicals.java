/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

/**
 * @author chedeler
 *
 */
public class EquivalentSuperLexicalsIdentifierServiceImplTestIdentifyEquivalentSuperLexicals {

	private EquivalentSuperLexicalsIdentifierServiceImpl equivalentSuperLexicalsIdentifierServiceImpl;
	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;

	private LinkedHashSet<Schema> sourceSchemas;
	private LinkedHashSet<Schema> targetSchemas;
	private List<Matching> matchings;

	private SuperAbstract sa1, sa2, sa3, sa4;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9, sl10, sl11;
	private List<DerivedOneToOneMatching> derivedMatchings;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicalsIdentifierServiceImpl = new EquivalentSuperLexicalsIdentifierServiceImpl();
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();

		sourceSchemas = new LinkedHashSet<Schema>();
		targetSchemas = new LinkedHashSet<Schema>();
		matchings = new ArrayList<Matching>();

		Schema schema1 = new Schema("schema1", null);
		sourceSchemas.add(schema1);
		Schema schema2 = new Schema("schema2", null);
		targetSchemas.add(schema2);

		sourceSchemas.add(schema1);
		targetSchemas.add(schema2);

		sa1 = new SuperAbstract("sa1", schema1);
		sl1 = new SuperLexical("sl1", schema1);
		sl2 = new SuperLexical("sl2", schema1);
		sl3 = new SuperLexical("sl3", schema1);
		sa1.addSuperLexical(sl1);
		sa1.addSuperLexical(sl2);
		sa1.addSuperLexical(sl3);

		sa2 = new SuperAbstract("sa2", schema1);
		sl4 = new SuperLexical("sl4", schema1);
		sl5 = new SuperLexical("sl5", schema1);
		sa2.addSuperLexical(sl4);
		sa2.addSuperLexical(sl5);

		sa3 = new SuperAbstract("sa3", schema1);
		sl6 = new SuperLexical("sl6", schema1);
		sl7 = new SuperLexical("sl7", schema1);
		sl8 = new SuperLexical("sl8", schema1);
		sl9 = new SuperLexical("sl9", schema1);
		sa3.addSuperLexical(sl6);
		sa3.addSuperLexical(sl7);
		sa3.addSuperLexical(sl8);
		sa3.addSuperLexical(sl9);

		sa4 = new SuperAbstract("sa4", schema2);
		sl10 = new SuperLexical("sl10", schema2);
		sl11 = new SuperLexical("sl11", schema2);
		sa4.addSuperLexical(sl10);
		sa4.addSuperLexical(sl11);

		Matching m1 = new OneToOneMatching(sa1, sa4, 0.35, "NGram");
		matchings.add(m1);

		Matching m2 = new OneToOneMatching(sl1, sl10, 0.3, "NGram");
		matchings.add(m2);

		Matching m3 = new OneToOneMatching(sl2, sl11, 0.9, "Instance");
		matchings.add(m3);

		Matching m4 = new OneToOneMatching(sl3, sl10, 0.6, "Instance");
		matchings.add(m4);

		Matching m5 = new OneToOneMatching(sl4, sl10, 0.8, "NGram");
		matchings.add(m5);

		Matching m6 = new OneToOneMatching(sl5, sl10, 0.35, "NGram");
		matchings.add(m6);

		Matching m7 = new OneToOneMatching(sl6, sl11, 1.0, "Instance");
		matchings.add(m7);

		Matching m8 = new OneToOneMatching(sl7, sl10, 0.75, "NGram");
		matchings.add(m8);

		Matching m9 = new OneToOneMatching(sl8, sl11, 0.62, "NGram");
		matchings.add(m9);

		Matching m10 = new OneToOneMatching(sl9, sl10, 0.89, "Instance");
		matchings.add(m10);

		derivedMatchings = derivedOneToOneMatchingsGeneratorServiceImpl.generateDerivedMatchings(sourceSchemas, targetSchemas, matchings);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#identifyEquivalentSuperLexicals(uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService, double, boolean)}.
	 */
	@Test
	public void testIdentifyEquivalentSuperLexicals() {
		assertNotNull(derivedMatchings);
		Map<SuperLexical, Set<SuperLexical>> equivalentSetsOfSuperLexicals = equivalentSuperLexicalsIdentifierServiceImpl
				.identifyEquivalentSuperLexicals(derivedOneToOneMatchingsGeneratorServiceImpl, 1.0d, true);
		assertEquals(5, equivalentSetsOfSuperLexicals.size());
		Set<SuperLexical> keySet = equivalentSetsOfSuperLexicals.keySet();
		assertTrue(keySet.contains(sl2));
		assertTrue(keySet.contains(sl6));
		assertTrue(keySet.contains(sl3));
		assertTrue(keySet.contains(sl4));
		assertTrue(keySet.contains(sl9));
		for (SuperLexical superLexical : keySet) {
			if (superLexical.equals(sl2) || superLexical.equals(sl6)) {
				Set<SuperLexical> equivalentSuperLexicals = equivalentSetsOfSuperLexicals.get(superLexical);
				assertTrue(equivalentSuperLexicals.contains(sl2));
				assertTrue(equivalentSuperLexicals.contains(sl6));
			} else if (superLexical.equals(sl3) || superLexical.equals(sl4) || superLexical.equals(sl9)) {
				Set<SuperLexical> equivalentSuperLexicals = equivalentSetsOfSuperLexicals.get(superLexical);
				assertTrue(equivalentSuperLexicals.contains(sl3));
				assertTrue(equivalentSuperLexicals.contains(sl4));
				assertTrue(equivalentSuperLexicals.contains(sl9));
			}
		}
	}

}
