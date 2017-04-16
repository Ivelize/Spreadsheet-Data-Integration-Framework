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
public class EquivalentSuperLexicalsIdentifierServiceImplTestCalculateDAndSumOfDOfGroupOfMostSimilarSuperLexicals {

	private EquivalentSuperLexicalsIdentifierServiceImpl equivalentSuperLexicalsIdentifierServiceImpl;
	private Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> mostSimilarSuperLexicalPerSuperAbstract;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicalsIdentifierServiceImpl = new EquivalentSuperLexicalsIdentifierServiceImpl();
		mostSimilarSuperLexicalPerSuperAbstract = new HashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>();

		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperLexical sl1 = new SuperLexical("sl1", null);
		sl1.setParentSuperAbstract(sa1);

		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		sl2.setParentSuperAbstract(sa2);

		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		sl3.setParentSuperAbstract(sa3);

		DerivedOneToOneMatching dm1 = new DerivedOneToOneMatching(sl1, null);
		dm1.setSumOfMatchingScores(0.46);
		MostSimilarSuperLexicalPerSuperAbstract m1 = new MostSimilarSuperLexicalPerSuperAbstract(sa1, sl1, dm1);

		DerivedOneToOneMatching dm2 = new DerivedOneToOneMatching(sl2, null);
		dm2.setSumOfMatchingScores(0.32);
		MostSimilarSuperLexicalPerSuperAbstract m2 = new MostSimilarSuperLexicalPerSuperAbstract(sa2, sl2, dm2);

		DerivedOneToOneMatching dm3 = new DerivedOneToOneMatching(sl3, null);
		dm3.setSumOfMatchingScores(0.97);
		MostSimilarSuperLexicalPerSuperAbstract m3 = new MostSimilarSuperLexicalPerSuperAbstract(sa3, sl3, dm3);

		mostSimilarSuperLexicalPerSuperAbstract.put(sa1, m1);
		mostSimilarSuperLexicalPerSuperAbstract.put(sa2, m2);
		mostSimilarSuperLexicalPerSuperAbstract.put(sa3, m3);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract(double, double, java.util.Map)}.
	 */
	@Test
	public void testCalculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstractAllDAbove0() {
		double tolerance = 0.00001;
		double scoreCurrentMatchedSuperLexical = 0.9;
		double maxMatchingScore = 1.0;
		double sumD = equivalentSuperLexicalsIdentifierServiceImpl.calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract(
				scoreCurrentMatchedSuperLexical, maxMatchingScore, mostSimilarSuperLexicalPerSuperAbstract);
		assertEquals(1.45, sumD, tolerance);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract(double, double, java.util.Map)}.
	 */
	@Test
	public void testCalculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstractSomeDBelow0() {
		double tolerance = 0.00001;
		double scoreCurrentMatchedSuperLexical = 0.6;
		double maxMatchingScore = 1.0;
		double sumD = equivalentSuperLexicalsIdentifierServiceImpl.calculateDAndSumOfDOfGroupOfMostSimilarSuperLexicalsPerSuperAbstract(
				scoreCurrentMatchedSuperLexical, maxMatchingScore, mostSimilarSuperLexicalPerSuperAbstract);
		assertEquals(0.63, sumD, tolerance);
	}

}
