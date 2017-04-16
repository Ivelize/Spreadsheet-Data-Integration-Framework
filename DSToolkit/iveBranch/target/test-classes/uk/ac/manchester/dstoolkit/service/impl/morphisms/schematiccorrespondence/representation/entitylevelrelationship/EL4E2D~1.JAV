package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class ELREntityLevelRelationshipTestCalculateDenominatorOfSimilarityFunction {

	private ELREntityLevelRelationship elrEntityLevelRelationship;
	private LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightMap1, constructsWeightMap2;
	private final double epsilon = 0.00000001d;
	private double weight1, weight2, weight3, weight4;

	@Before
	public void setUp() throws Exception {
		elrEntityLevelRelationship = new ELREntityLevelRelationship(null, null, new LinkedHashSet<SuperAbstract>(),
				new LinkedHashSet<SuperAbstract>());
		constructsWeightMap1 = new LinkedHashMap<CanonicalModelConstruct[], Double>();

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		CanonicalModelConstruct[] constructsArray1 = new CanonicalModelConstruct[2];
		constructsArray1[0] = sl1;
		constructsArray1[1] = sl2;

		weight1 = 0.4;

		SuperLexical sl3 = new SuperLexical("sl3", null);
		CanonicalModelConstruct[] constructsArray2 = new CanonicalModelConstruct[2];
		constructsArray2[0] = sl3;
		constructsArray2[1] = null;

		weight2 = 0.3;

		constructsWeightMap1.put(constructsArray1, weight1);
		constructsWeightMap1.put(constructsArray2, weight2);

		constructsWeightMap2 = new LinkedHashMap<CanonicalModelConstruct[], Double>();

		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		CanonicalModelConstruct[] constructsArray3 = new CanonicalModelConstruct[2];
		constructsArray3[0] = sl4;
		constructsArray3[1] = sl5;

		weight3 = 0.6;

		SuperLexical sl6 = new SuperLexical("sl6", null);
		CanonicalModelConstruct[] constructsArray4 = new CanonicalModelConstruct[2];
		constructsArray4[0] = null;
		constructsArray4[1] = sl6;

		weight4 = 0.9;

		constructsWeightMap2.put(constructsArray3, weight3);
		constructsWeightMap2.put(constructsArray4, weight4);
	}

	@Test
	public void testCalculateDemoninatorOfSimilarityFunction() {
		double denominator = elrEntityLevelRelationship.calculateDemoninatorOfSimilarityFunction(constructsWeightMap1, constructsWeightMap2);
		assertEquals(Math.sqrt((weight1 * weight1) + (weight2 * weight2)) * Math.sqrt((weight3 * weight3) + (weight4 * weight4)), denominator,
				epsilon);
	}

}
