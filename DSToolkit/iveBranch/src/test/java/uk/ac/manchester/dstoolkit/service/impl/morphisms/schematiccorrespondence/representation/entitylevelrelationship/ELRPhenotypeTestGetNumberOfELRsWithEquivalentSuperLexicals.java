package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.HorizontalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.SingleVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VerticalPartitioningVector;

public class ELRPhenotypeTestGetNumberOfELRsWithEquivalentSuperLexicals {

	private ELRPhenotype elrPhenotype;
	private LinkedHashSet<SuperLexical> equivalentSuperLexicals;
	private SuperLexical sl1, sl2, sl3;

	//TODO sort out tests

	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicals = new LinkedHashSet<SuperLexical>();
		elrPhenotype = new ELRPhenotype(null);
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);
		SuperLexical sl7 = new SuperLexical("sl7", null);

		SingleVector singleVector = new SingleVector(null, null, false);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap1 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		CanonicalModelConstruct[] constructs1 = { sa1 };
		constructsWeightsMap1.put(constructs1, null);
		CanonicalModelConstruct[] constructs2 = { sl4 };
		constructsWeightsMap1.put(constructs2, null);
		CanonicalModelConstruct[] constructs3 = { sl5 };
		constructsWeightsMap1.put(constructs3, null);
		singleVector.setConstructsWeightsMap(constructsWeightsMap1);

		HorizontalPartitioningVector horizontalPartitioningVector = new HorizontalPartitioningVector(null, null, false);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap2 = new LinkedHashMap<CanonicalModelConstruct[], Double>();

		CanonicalModelConstruct[] constructs4 = { sa2, sa3 };
		constructsWeightsMap2.put(constructs4, null);
		CanonicalModelConstruct[] constructs5 = { sl6, sl4 };
		constructsWeightsMap2.put(constructs5, null);
		CanonicalModelConstruct[] constructs6 = { sl1, sl2 };
		constructsWeightsMap2.put(constructs6, null);
		horizontalPartitioningVector.setConstructsWeightsMap(constructsWeightsMap2);

		VerticalPartitioningVector verticalPartitioningVector = new VerticalPartitioningVector(null, null, false);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap3 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		CanonicalModelConstruct[] constructs7 = { sa2, sa3 };
		constructsWeightsMap3.put(constructs7, null);
		CanonicalModelConstruct[] constructs8 = { sl6, sl4 };
		constructsWeightsMap3.put(constructs8, null);
		CanonicalModelConstruct[] constructs9 = { sl5 };
		constructsWeightsMap3.put(constructs9, null);
		CanonicalModelConstruct[] constructs10 = { sl3 };
		constructsWeightsMap3.put(constructs10, null);
		verticalPartitioningVector.setConstructsWeightsMap(constructsWeightsMap3);

		ELREntityLevelRelationship elr1 = new ELREntityLevelRelationship(null, elrPhenotype, sa1, null);
		List<VectorSpaceVector> sourceVector = new ArrayList<VectorSpaceVector>();
		sourceVector.add(singleVector);
		List<VectorSpaceVector> targetVectors = new ArrayList<VectorSpaceVector>();
		targetVectors.add(horizontalPartitioningVector);
		targetVectors.add(verticalPartitioningVector);
		elr1.setSourceVectors(sourceVector);
		elr1.setTargetVectors(targetVectors);
		elrPhenotype.addELR(elr1);

		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);
		equivalentSuperLexicals.add(sl3);
	}

	@Test
	public void testGetNumberOfELRsWithEquivalentSuperLexicalsSourceEntitiesSingleElrNoEquivalentSuperLexicalsInSourceSingleVector() {

		assertEquals(0, elrPhenotype.getNumberOfELRsWithEquivalentSuperLexicals(equivalentSuperLexicals, true));
	}

	@Test
	public void testGetNumberOfELRsWithEquivalentSuperLexicalsTargetEntitiesSingleElrMultipleEquivalentSuperLexicalsInTargetHorizontalAndVerticalVectors() {

		assertEquals(1, elrPhenotype.getNumberOfELRsWithEquivalentSuperLexicals(equivalentSuperLexicals, false));
	}

}
