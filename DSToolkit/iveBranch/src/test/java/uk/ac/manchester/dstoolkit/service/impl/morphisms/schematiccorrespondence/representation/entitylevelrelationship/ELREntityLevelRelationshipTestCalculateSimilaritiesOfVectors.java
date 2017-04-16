package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class ELREntityLevelRelationshipTestCalculateSimilaritiesOfVectors {

	private ELREntityLevelRelationship elrEntityLevelRelationship;
	private final double epsilon = 0.000000001;
	private LinkedHashMap<CanonicalModelConstruct[], Double> sourceVectorConstructsWeightMap;
	private LinkedHashMap<CanonicalModelConstruct[], Double> targetVectorConstructsWeightMap;
	private Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap;
	private Map<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>> equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap;
	private CanonicalModelConstruct[] constructArray1, constructArray2, constructArray3, constructArray4;
	private double weight1, weight2, weight3, weight4;
	private double maxAvgMatchSimilarity1, maxAvgMatchSimilarity2, maxAvgMatchSimilarity3, maxAvgMatchSimilarity4;

	@Before
	public void setUp() throws Exception {
		elrEntityLevelRelationship = new ELREntityLevelRelationship(null, null, new LinkedHashSet<SuperAbstract>(),
				new LinkedHashSet<SuperAbstract>());
		sourceVectorConstructsWeightMap = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		targetVectorConstructsWeightMap = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap = new HashMap<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>>();
		equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap = new HashMap<CanonicalModelConstruct[], Map<CanonicalModelConstruct[], Double>>();

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);

		constructArray1 = new CanonicalModelConstruct[2];
		constructArray1[0] = sl1;
		constructArray1[1] = sl2;

		constructArray2 = new CanonicalModelConstruct[2];
		constructArray2[0] = sl3;
		constructArray2[1] = sl4;

		constructArray3 = new CanonicalModelConstruct[2];
		constructArray3[0] = sl5;
		constructArray3[1] = null;

		constructArray4 = new CanonicalModelConstruct[2];
		constructArray4[0] = null;
		constructArray4[1] = sl6;

		weight1 = 0.6d;
		weight2 = 0.3d;
		weight3 = 0.5d;
		weight4 = 0.7d;

		sourceVectorConstructsWeightMap.put(constructArray1, weight1);
		sourceVectorConstructsWeightMap.put(constructArray3, weight3);

		targetVectorConstructsWeightMap.put(constructArray2, weight2);
		targetVectorConstructsWeightMap.put(constructArray4, weight4);

		maxAvgMatchSimilarity1 = 0.9d;
		maxAvgMatchSimilarity2 = 0.8d;
		maxAvgMatchSimilarity3 = 0.5d;
		maxAvgMatchSimilarity4 = 0.4d;

		LinkedHashMap<CanonicalModelConstruct[], Double> equivalentConstructArrayWithMaxAvgMatchScore1 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		equivalentConstructArrayWithMaxAvgMatchScore1.put(constructArray2, maxAvgMatchSimilarity1);
		equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.put(constructArray1, equivalentConstructArrayWithMaxAvgMatchScore1);

		LinkedHashMap<CanonicalModelConstruct[], Double> equivalentConstructArrayWithMaxAvgMatchScore2 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		equivalentConstructArrayWithMaxAvgMatchScore2.put(constructArray4, maxAvgMatchSimilarity2);
		equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap.put(constructArray3, equivalentConstructArrayWithMaxAvgMatchScore2);

		LinkedHashMap<CanonicalModelConstruct[], Double> equivalentConstructArrayWithMaxAvgMatchScore3 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		equivalentConstructArrayWithMaxAvgMatchScore3.put(constructArray3, maxAvgMatchSimilarity3);
		equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.put(constructArray2, equivalentConstructArrayWithMaxAvgMatchScore3);

		LinkedHashMap<CanonicalModelConstruct[], Double> equivalentConstructArrayWithMaxAvgMatchScore4 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		equivalentConstructArrayWithMaxAvgMatchScore4.put(constructArray1, maxAvgMatchSimilarity4);
		equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap.put(constructArray4, equivalentConstructArrayWithMaxAvgMatchScore4);
	}

	@Test
	public void testCalculateSimilaritiesOfVectors() {
		double similarityOfVectors = elrEntityLevelRelationship.calculateSimilaritiesOfVectors(sourceVectorConstructsWeightMap,
				targetVectorConstructsWeightMap, equivalentSourceConstructsTargetConstructsMaxAvgMatchingScoresMap,
				equivalentTargetConstructsSourceConstructsMaxAvgMatchingScoresMap);

		double expectedDenominator = Math.sqrt((weight1 * weight1) + (weight3 * weight3)) * Math.sqrt((weight2 * weight2) + (weight4 * weight4));
		double expectedNumeratorSourceToTarget = (maxAvgMatchSimilarity1 * weight1 * weight2) + (maxAvgMatchSimilarity2 * weight3 * weight4);
		double expectedNumeratorTargetToSource = (maxAvgMatchSimilarity3 * weight2 * weight3) + (maxAvgMatchSimilarity4 * weight4 * weight1);

		double expectedSimilarityScore = ((expectedNumeratorSourceToTarget / expectedDenominator) + (expectedNumeratorTargetToSource / expectedDenominator)) / 2;

		assertEquals(expectedSimilarityScore, similarityOfVectors, epsilon);
	}

}
