package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryService;

public class VectorSpaceVectorFactoryServiceImplTestGenerateVectorSpaceVectorsForEntitySet {

	private VectorSpaceVectorFactoryService vectorSpaceVectorFactoryService;
	private ELRChromosomeStub elrChromosomeStub;
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap;
	private LinkedHashSet<SuperAbstract> entitySet;

	@Before
	public void setUp() throws Exception {
		vectorSpaceVectorFactoryService = new VectorSpaceVectorFactoryServiceImpl();
		elrChromosomeStub = new ELRChromosomeStub(null, null, null);
		entitySet = new LinkedHashSet<SuperAbstract>();
		equivalentSuperLexicalsSetsMap = new HashMap<SuperLexical, Set<SuperLexical>>();
	}

	@Test
	public void testGenerateVectorSpaceVectorsForEntitySetSingleEntityResultsInSingleVector() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperLexical sl1 = new SuperLexical("sl1", null);
		sl1.setParentSuperAbstract(sa1);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		sl2.setParentSuperAbstract(sa1);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		sl3.setParentSuperAbstract(sa1);

		entitySet.add(sa1);

		List<VectorSpaceVector> vectors = vectorSpaceVectorFactoryService.generateVectorSpaceVectorsForEntitySet(elrChromosomeStub, null, true,
				entitySet);

		assertEquals(1, vectors.size());

		VectorSpaceVector vector = vectors.get(0);
		assertTrue(vector instanceof SingleVector);

		//TODO the following tests probably shouldn't be here and are copied from the corresponding test of SingleVector
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightMap = vector.getConstructsWeightsMap();

		assertEquals(4, constructsWeightMap.size());
		int i = 0;
		for (CanonicalModelConstruct[] constructs : constructsWeightMap.keySet()) {
			assertEquals(1, constructs.length);
			assertEquals(new Double(0d), constructsWeightMap.get(constructs));
			switch (i) {
			case 0:
				assertEquals(sa1, constructs[0]);
				break;
			case 1:
				assertEquals(sl1, constructs[0]);
				break;
			case 2:
				assertEquals(sl2, constructs[0]);
				break;
			case 3:
				assertEquals(sl3, constructs[0]);
				break;
			}
			i++;
		}
	}

	@Test
	public void testGenerateVectorSpaceVectorsForEntitySetMultipleEntitiesResultInHorizontalAndVerticalPartitioningVectors() {
		SuperAbstract sa1, sa2, sa3;
		SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8;

		sa1 = new SuperAbstract("sa1", null);
		sa2 = new SuperAbstract("sa2", null);
		sa3 = new SuperAbstract("sa3", null);

		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);
		sl4 = new SuperLexical("sl4", null);
		sl5 = new SuperLexical("sl5", null);
		sl6 = new SuperLexical("sl6", null);
		sl7 = new SuperLexical("sl7", null);
		sl8 = new SuperLexical("sl8", null);

		sa1.addSuperLexical(sl1);
		sa1.addSuperLexical(sl2);
		sa1.addSuperLexical(sl3);

		sa2.addSuperLexical(sl4);
		sa2.addSuperLexical(sl5);

		sa3.addSuperLexical(sl6);
		sa3.addSuperLexical(sl7);
		sa3.addSuperLexical(sl8);

		Set<SuperLexical> equivalentSuperLexicals1 = new HashSet<SuperLexical>();
		equivalentSuperLexicals1.add(sl1);
		equivalentSuperLexicals1.add(sl5);
		equivalentSuperLexicals1.add(sl7);
		equivalentSuperLexicalsSetsMap.put(sl1, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl5, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl7, equivalentSuperLexicals1);

		Set<SuperLexical> equivalentSuperLexicals2 = new HashSet<SuperLexical>();
		equivalentSuperLexicals2.add(sl2);
		equivalentSuperLexicals2.add(sl8);
		equivalentSuperLexicalsSetsMap.put(sl2, equivalentSuperLexicals2);
		equivalentSuperLexicalsSetsMap.put(sl8, equivalentSuperLexicals2);

		elrChromosomeStub.setEquivalentSuperLexicalsSetsMap(equivalentSuperLexicalsSetsMap);

		entitySet.add(sa1);
		entitySet.add(sa2);
		entitySet.add(sa3);

		List<VectorSpaceVector> vectors = vectorSpaceVectorFactoryService.generateVectorSpaceVectorsForEntitySet(elrChromosomeStub, null, true,
				entitySet);

		assertEquals(2, vectors.size());

		VectorSpaceVector vector1 = vectors.get(0);
		assertTrue(vector1 instanceof HorizontalPartitioningVector);

		VectorSpaceVector vector2 = vectors.get(1);
		assertTrue(vector2 instanceof VerticalPartitioningVector);

		//TODO the following tests probably shouldn't be here and are copied from the corresponding tests of HorizontalPartitioningVector and VerticalPartitioningVector
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightMap1 = vector1.getConstructsWeightsMap();
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightMap2 = vector2.getConstructsWeightsMap();

		assertEquals(6, constructsWeightMap1.size());
		int i = 0;
		for (CanonicalModelConstruct[] constructs : constructsWeightMap1.keySet()) {
			assertEquals(3, constructs.length);
			assertEquals(new Double(0d), constructsWeightMap1.get(constructs));
			switch (i) {
			case 0:
				assertEquals(sa1, constructs[0]);
				assertEquals(sa2, constructs[1]);
				assertEquals(sa3, constructs[2]);
				break;
			case 1:
				assertEquals(sl1, constructs[0]);
				assertEquals(sl5, constructs[1]);
				assertEquals(sl7, constructs[2]);
				break;
			case 2:
				assertEquals(sl2, constructs[0]);
				assertEquals(null, constructs[1]);
				assertEquals(sl8, constructs[2]);
				break;
			case 3:
				assertEquals(sl3, constructs[0]);
				assertEquals(null, constructs[1]);
				assertEquals(null, constructs[2]);
				break;
			case 4:
				assertEquals(null, constructs[0]);
				assertEquals(sl4, constructs[1]);
				assertEquals(null, constructs[2]);
				break;
			case 5:
				assertEquals(null, constructs[0]);
				assertEquals(null, constructs[1]);
				assertEquals(sl6, constructs[2]);
				break;
			}
			i++;
		}

		assertEquals(7, constructsWeightMap2.size());
		i = 0;
		for (CanonicalModelConstruct[] constructs : constructsWeightMap2.keySet()) {
			assertTrue(constructs.length == 1 || constructs.length == 3);
			assertEquals(new Double(0d), constructsWeightMap2.get(constructs));
			switch (i) {
			case 0:
				assertEquals(3, constructs.length);
				assertEquals(sa1, constructs[0]);
				assertEquals(sa2, constructs[1]);
				assertEquals(sa3, constructs[2]);
				break;
			case 1:
				assertEquals(3, constructs.length);
				assertEquals(sl1, constructs[0]);
				assertEquals(sl5, constructs[1]);
				assertEquals(sl7, constructs[2]);
				break;
			case 2:
				assertEquals(1, constructs.length);
				assertTrue(constructs[0].equals(sl2) || constructs[0].equals(sl8));
				break;
			case 3:
				assertEquals(1, constructs.length);
				assertTrue(constructs[0].equals(sl2) || constructs[0].equals(sl8));
				break;
			case 4:
				assertEquals(1, constructs.length);
				assertEquals(sl3, constructs[0]);
				break;
			case 5:
				assertEquals(1, constructs.length);
				assertEquals(sl4, constructs[0]);
				break;
			case 6:
				assertEquals(1, constructs.length);
				assertEquals(sl6, constructs[0]);
				break;
			}
			i++;
		}
	}

	class ELRChromosomeStub extends ELRChromosome {

		private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap;

		/**
		 * @param sourceSchemas
		 * @param targetSchemas
		 * @param matchings
		 */
		public ELRChromosomeStub(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas, List<Matching> matchings) {
			super(sourceSchemas, targetSchemas, matchings);
		}

		@Override
		public Set<SuperLexical> getEquivalentSuperLexicalsForSuperLexical(SuperLexical superLexical, boolean isSource) {
			return equivalentSuperLexicalsSetsMap.get(superLexical);
		}

		/**
		 * @return the equivalentSuperLexicalsSetsMap
		 */
		public Map<SuperLexical, Set<SuperLexical>> getEquivalentSuperLexicalsSetsMap() {
			return equivalentSuperLexicalsSetsMap;
		}

		/**
		 * @param equivalentSuperLexicalsSetsMap the equivalentSuperLexicalsSetsMap to set
		 */
		public void setEquivalentSuperLexicalsSetsMap(Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap) {
			this.equivalentSuperLexicalsSetsMap = equivalentSuperLexicalsSetsMap;
		}
	}

}
