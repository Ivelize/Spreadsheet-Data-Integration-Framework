package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.util.DerivedOneToOneMatchingsGeneratorService;

public class ELREntityLevelRelationshipTestIdentifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource {

	private ELRChromosomeStub chromosomeStub;
	private ELREntityLevelRelationship elrEntityLevelRelationship;
	private Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget;
	private Map<CanonicalModelConstruct[], Double> matchedSourceConstructsArraysWithMaxAvgMatchingScore1,
			matchedSourceConstructsArraysWithMaxAvgMatchingScore2;
	private CanonicalModelConstruct[] sourceConstructsArray1, sourceConstructsArray2;
	private CanonicalModelConstruct[] targetConstructsArray1, targetConstructsArray2;
	private double avgMatchScore1, avgMatchScore2;
	private final double epsilon = 0.0000001;

	@Before
	public void setUp() throws Exception {
		chromosomeStub = new ELRChromosomeStub(null, null, null);
		elrEntityLevelRelationship = new ELREntityLevelRelationship(chromosomeStub, null, new LinkedHashSet<SuperAbstract>(),
				new LinkedHashSet<SuperAbstract>());
		targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget = new HashMap<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>>();

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		sourceConstructsArray1 = new CanonicalModelConstruct[2];
		sourceConstructsArray1[0] = sl1;
		sourceConstructsArray1[1] = sl2;

		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		targetConstructsArray1 = new CanonicalModelConstruct[2];
		targetConstructsArray1[0] = sl3;
		targetConstructsArray1[1] = sl4;

		avgMatchScore1 = 0.9d;

		matchedSourceConstructsArraysWithMaxAvgMatchingScore1 = new HashMap<CanonicalModelConstruct[], Double>();
		matchedSourceConstructsArraysWithMaxAvgMatchingScore1.put(sourceConstructsArray1, avgMatchScore1);
		targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.put(targetConstructsArray1,
				matchedSourceConstructsArraysWithMaxAvgMatchingScore1);

		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);
		sourceConstructsArray2 = new CanonicalModelConstruct[2];
		sourceConstructsArray2[0] = sl5;
		sourceConstructsArray2[1] = sl6;

		SuperLexical sl7 = new SuperLexical("sl7", null);
		SuperLexical sl8 = new SuperLexical("sl8", null);
		targetConstructsArray2 = new CanonicalModelConstruct[2];
		targetConstructsArray2[0] = sl7;
		targetConstructsArray2[1] = sl8;

		avgMatchScore2 = 0.8d;
		matchedSourceConstructsArraysWithMaxAvgMatchingScore2 = new HashMap<CanonicalModelConstruct[], Double>();
		matchedSourceConstructsArraysWithMaxAvgMatchingScore2.put(sourceConstructsArray2, avgMatchScore2);
		targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget.put(targetConstructsArray2,
				matchedSourceConstructsArraysWithMaxAvgMatchingScore2);

	}

	@Test
	public void testIdentifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSourceSingleSourceConstructsArrayForTargetConstructsArray() {
		Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource = elrEntityLevelRelationship
				.identifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource(targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget);
		assertEquals(2, matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.size());
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.containsKey(sourceConstructsArray1));
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.containsKey(sourceConstructsArray2));
		assertEquals(1, matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray1).size());
		assertEquals(1, matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray2).size());
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray1).containsKey(
				targetConstructsArray1));
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray2).containsKey(
				targetConstructsArray2));
		assertEquals(
				avgMatchScore1,
				matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray1).get(
						targetConstructsArray1), epsilon);
		assertEquals(
				avgMatchScore2,
				matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray2).get(
						targetConstructsArray2), epsilon);
	}

	@Test
	public void testIdentifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSourceMultipeSourceConstructsArrayForTargetConstructsArray() {
		SuperLexical sl9 = new SuperLexical("sl9", null);
		SuperLexical sl10 = new SuperLexical("sl10", null);
		CanonicalModelConstruct[] sourceConstructsArray3 = new CanonicalModelConstruct[2];
		sourceConstructsArray3[0] = sl9;
		sourceConstructsArray3[1] = sl10;
		double avgMatchScore3 = 0.5d;
		matchedSourceConstructsArraysWithMaxAvgMatchingScore2.put(sourceConstructsArray3, avgMatchScore3);

		Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource = elrEntityLevelRelationship
				.identifyMatchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource(targetConstructsArrayWithSourceConstructsArrayWithMaxAvgMatchingScoresFromSourceToTarget);
		assertEquals(2, matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.size());
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.containsKey(sourceConstructsArray1));
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.containsKey(sourceConstructsArray2));
		assertEquals(1, matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray1).size());
		assertEquals(1, matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray2).size());
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray1).containsKey(
				targetConstructsArray1));
		assertTrue(matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray2).containsKey(
				targetConstructsArray2));
		assertEquals(
				avgMatchScore1,
				matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray1).get(
						targetConstructsArray1), epsilon);
		assertEquals(
				avgMatchScore2,
				matchedSourceAndTargetConstructsArraysWithMaxAvgMatchingScoreFromTargetToSource.get(sourceConstructsArray2).get(
						targetConstructsArray2), epsilon);
	}

	class ELRChromosomeStub extends ELRChromosome {

		private final DerivedOneToOneMatchingsGeneratorService derivedOneToOneMatchingsGeneratorService = new DerivedOneToOneMatchingsGeneratorServiceImpl();

		/**
		 * @param sourceSchemas
		 * @param targetSchemas
		 * @param matchings
		 */
		public ELRChromosomeStub(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas, List<Matching> matchings) {
			super(sourceSchemas, targetSchemas, matchings);
		}

		@Override
		public Map<CanonicalModelConstruct[], Double> identifyConstructsArrayWithMaxAvgMatchingScores(
				Map<CanonicalModelConstruct[], Double> matchedConstructsWithAvgOfMatchingScore) {
			return this.derivedOneToOneMatchingsGeneratorService
					.identifyConstructsArrayWithMaxAvgMatchingScores(matchedConstructsWithAvgOfMatchingScore);
		}

	}

}
