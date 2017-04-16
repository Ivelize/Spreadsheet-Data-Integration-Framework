/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.MostSimilarSuperLexicalPerSuperAbstract;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatchingsGeneratorServiceImplTestGetMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatches {

	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;
	private Map<SuperLexical, DerivedOneToOneMatching> derivedOneToOneMatchings;
	private SuperAbstract sa1;
	private SuperLexical mostSimilarSl;
	private DerivedOneToOneMatching derivedOneToOneMatchingWithMaxSimilarityScore;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();
		derivedOneToOneMatchings = new HashMap<SuperLexical, DerivedOneToOneMatching>();
		sa1 = new SuperAbstract("sa1", null);
		SuperLexical sl1 = new SuperLexical("sl1", null);
		sl1.setParentSuperAbstract(sa1);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		sl2.setParentSuperAbstract(sa1);
		DerivedOneToOneMatching m1 = new DerivedOneToOneMatching(sl1, sl2);
		m1.setSumOfMatchingScores(3.2d);
		derivedOneToOneMatchings.put(sl1, m1);

		SuperLexical sl3 = new SuperLexical("sl3", null);
		sl3.setParentSuperAbstract(sa1);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		sl4.setParentSuperAbstract(sa1);
		DerivedOneToOneMatching m2 = new DerivedOneToOneMatching(sl3, sl4);
		m2.setSumOfMatchingScores(2.6d);
		derivedOneToOneMatchings.put(sl3, m2);

		SuperLexical sl5 = new SuperLexical("sl5", null);
		sl5.setParentSuperAbstract(sa1);
		SuperLexical sl6 = new SuperLexical("sl6", null);
		sl6.setParentSuperAbstract(sa1);
		DerivedOneToOneMatching m3 = new DerivedOneToOneMatching(sl5, sl6);
		m3.setSumOfMatchingScores(6.4d);
		derivedOneToOneMatchings.put(sl5, m3);

		mostSimilarSl = sl5;
		derivedOneToOneMatchingWithMaxSimilarityScore = m3;
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorServiceImpl#getMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatches(java.util.Map)}.
	 */
	@Test
	public void testGetMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatchesWithThreeDerivedMatchings() {
		MostSimilarSuperLexicalPerSuperAbstract mostSimilarSuperLexicalPerSuperAbstract = derivedOneToOneMatchingsGeneratorServiceImpl
				.getMostSimilarSuperLexicalWithLargestSumOfMatchingScoresWithinGroupOfMatches(derivedOneToOneMatchings);
		assertEquals(sa1, mostSimilarSuperLexicalPerSuperAbstract.getSuperAbstract());
		assertEquals(mostSimilarSl, mostSimilarSuperLexicalPerSuperAbstract.getSuperLexical());
		assertEquals(derivedOneToOneMatchingWithMaxSimilarityScore, mostSimilarSuperLexicalPerSuperAbstract.getDerivedOneToOneMatching());
	}

}
