/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * @author chedeler
 *
 */
public class EquivalentSuperLexicalsIdentifierServiceImplTestCalculateD {

	private EquivalentSuperLexicalsIdentifierServiceImpl equivalentSuperLexicalsIdentifierServiceImpl;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicalsIdentifierServiceImpl = new EquivalentSuperLexicalsIdentifierServiceImpl();
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#calculateD(double, double, double)}.
	 */
	@Test
	public void testCalculateDWithDGreaterThan0() {
		double tolerance = 0.00001;

		double scoreCurrentMatchedSuperLexical = 0.9;
		double sumOfMatchingScores = 0.8;
		double maxMatchingScore = 1.0;

		assertEquals(0.7,
				equivalentSuperLexicalsIdentifierServiceImpl.calculateD(scoreCurrentMatchedSuperLexical, maxMatchingScore, sumOfMatchingScores),
				tolerance);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#calculateD(double, double, double)}.
	 */
	@Test
	public void testCalculateDWithDLessThan0() {
		double tolerance = 0.00001;

		double scoreCurrentMatchedSuperLexical = 0.9;
		double sumOfMatchingScores = 0.8;
		double maxMatchingScore = 2.0;

		assertEquals(0,
				equivalentSuperLexicalsIdentifierServiceImpl.calculateD(scoreCurrentMatchedSuperLexical, maxMatchingScore, sumOfMatchingScores),
				tolerance);
	}

}
