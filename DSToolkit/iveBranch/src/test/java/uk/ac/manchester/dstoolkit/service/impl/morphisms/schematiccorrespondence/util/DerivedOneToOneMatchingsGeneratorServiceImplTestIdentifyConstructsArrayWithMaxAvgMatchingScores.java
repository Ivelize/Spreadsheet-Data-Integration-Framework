package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService;

public class DerivedOneToOneMatchingsGeneratorServiceImplTestIdentifyConstructsArrayWithMaxAvgMatchingScores {

	private final double epsilon = 0.000000001d;
	private DerivedOneToOneMatchingsGeneratorService derivedOneToOneMatchingsGeneratorService;
	private CanonicalModelConstruct[] constructsArray, constructsWithMaxAvgMatchingScores;
	private Map<CanonicalModelConstruct[], Double> matchedConstructsWithSumOfMatchingScore;

	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorService = new DerivedOneToOneMatchingsGeneratorServiceImpl();

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);

		constructsArray = new CanonicalModelConstruct[2];
		constructsArray[0] = sl1;
		constructsArray[1] = sl2;

		constructsWithMaxAvgMatchingScores = new CanonicalModelConstruct[2];
		constructsWithMaxAvgMatchingScores[0] = sl4;
		constructsWithMaxAvgMatchingScores[0] = sl5;

		CanonicalModelConstruct[] constructsArray1 = new CanonicalModelConstruct[1];
		constructsArray1[0] = sl3;

		CanonicalModelConstruct[] constructsArray2 = new CanonicalModelConstruct[2];
		constructsArray2[0] = sl6;
		constructsArray2[1] = null;

		matchedConstructsWithSumOfMatchingScore = new HashMap<CanonicalModelConstruct[], Double>();
		matchedConstructsWithSumOfMatchingScore.put(constructsArray1, 0.9d);
		matchedConstructsWithSumOfMatchingScore.put(constructsWithMaxAvgMatchingScores, 1.6d);
		matchedConstructsWithSumOfMatchingScore.put(constructsArray2, 0.4d);
	}

	@Test
	public void testIdentifyConstructsArrayWithMaxAvgMatchingScores() {
		Map<CanonicalModelConstruct[], Double> matchedConstructsWithMaxAvgOfMatchingScores = derivedOneToOneMatchingsGeneratorService
				.identifyConstructsArrayWithMaxAvgMatchingScores(matchedConstructsWithSumOfMatchingScore);
		assertEquals(1, matchedConstructsWithMaxAvgOfMatchingScores.size());
		assertTrue(matchedConstructsWithMaxAvgOfMatchingScores.containsKey(constructsWithMaxAvgMatchingScores));
		assertEquals(1.6d, matchedConstructsWithMaxAvgOfMatchingScores.get(constructsWithMaxAvgMatchingScores), epsilon);
	}

	@Test
	public void testIdentifyConstructsArrayWithMaxAvgMatchingScoresWithEmptyMapAsInput() {
		Map<CanonicalModelConstruct[], Double> matchedConstructsWithMaxAvgOfMatchingScores = derivedOneToOneMatchingsGeneratorService
				.identifyConstructsArrayWithMaxAvgMatchingScores(new HashMap<CanonicalModelConstruct[], Double>());
		assertEquals(0, matchedConstructsWithMaxAvgOfMatchingScores.size());
	}

}
