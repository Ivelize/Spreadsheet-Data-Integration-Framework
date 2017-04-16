package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class VectorSpaceVectorTestContainsAnEquivalentSuperLexical {

	Set<SuperLexical> equivalentSuperLexicals;
	SuperLexical sl1, sl2, sl3;

	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicals = new HashSet<SuperLexical>();
		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);
		equivalentSuperLexicals.add(sl3);
	}

	@Test
	public void testContainsAnEquivalentSuperLexicalSingleVectorNoEquivalentSuperLexicals() {
		SingleVector singleVector = new SingleVector(null, null, false);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		CanonicalModelConstruct[] constructs1 = { sa1 };
		constructsWeightsMap.put(constructs1, null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		CanonicalModelConstruct[] constructs2 = { sl4 };
		constructsWeightsMap.put(constructs2, null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		CanonicalModelConstruct[] constructs3 = { sl5 };
		constructsWeightsMap.put(constructs3, null);
		singleVector.setConstructsWeightsMap(constructsWeightsMap);

		assertFalse(singleVector.containsAnEquivalentSuperLexical(equivalentSuperLexicals));
	}

	@Test
	public void testContainsAnEquivalentSuperLexicalHorizontalPartitioningVectorWithEquivalentSuperLexicals() {
		HorizontalPartitioningVector horizontalPartitioningVector = new HorizontalPartitioningVector(null, null, false);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		CanonicalModelConstruct[] constructs1 = { sa1, sa2 };
		constructsWeightsMap.put(constructs1, null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		CanonicalModelConstruct[] constructs2 = { sl3, sl4 };
		constructsWeightsMap.put(constructs2, null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		CanonicalModelConstruct[] constructs3 = { sl1, sl5 };
		constructsWeightsMap.put(constructs3, null);
		horizontalPartitioningVector.setConstructsWeightsMap(constructsWeightsMap);

		assertTrue(horizontalPartitioningVector.containsAnEquivalentSuperLexical(equivalentSuperLexicals));
	}

}
