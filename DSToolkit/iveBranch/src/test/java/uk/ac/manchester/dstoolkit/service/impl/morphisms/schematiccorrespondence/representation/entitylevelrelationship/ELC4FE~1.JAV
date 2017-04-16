package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.HorizontalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VerticalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryService;

public class ELREntityLevelRelationshipTestGenerateSourceAndTargetVectorSpaceVectorsForMultipleEntities {

	private ELRChromosomeStub elrChromosomeStub;
	private VectorSpaceVectorFactoryService vectorSpaceVectorFactoryService;
	private ELREntityLevelRelationship entityLevelRelationship;
	private LinkedHashSet<SuperAbstract> sourceEntitySet, targetEntitySet;

	@Before
	public void setUp() throws Exception {
		elrChromosomeStub = new ELRChromosomeStub(null, null, null);
		sourceEntitySet = new LinkedHashSet<SuperAbstract>();
		targetEntitySet = new LinkedHashSet<SuperAbstract>();

		Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap = new HashMap<SuperLexical, Set<SuperLexical>>();

		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		SuperAbstract sa4 = new SuperAbstract("sa4", null);

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);
		SuperLexical sl7 = new SuperLexical("sl7", null);
		SuperLexical sl8 = new SuperLexical("sl8", null);

		sa1.addSuperLexical(sl1);
		sa1.addSuperLexical(sl2);

		sa2.addSuperLexical(sl3);
		sa2.addSuperLexical(sl4);

		sa3.addSuperLexical(sl5);
		sa3.addSuperLexical(sl6);

		sa4.addSuperLexical(sl7);
		sa4.addSuperLexical(sl8);

		Set<SuperLexical> equivalentSuperLexicals1 = new HashSet<SuperLexical>();
		equivalentSuperLexicals1.add(sl1);
		equivalentSuperLexicals1.add(sl3);
		equivalentSuperLexicalsSetsMap.put(sl1, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl3, equivalentSuperLexicals1);

		Set<SuperLexical> equivalentSuperLexicals2 = new HashSet<SuperLexical>();
		equivalentSuperLexicals2.add(sl6);
		equivalentSuperLexicals2.add(sl8);
		equivalentSuperLexicalsSetsMap.put(sl6, equivalentSuperLexicals2);
		equivalentSuperLexicalsSetsMap.put(sl8, equivalentSuperLexicals2);

		elrChromosomeStub.setEquivalentSuperLexicalsSetsMap(equivalentSuperLexicalsSetsMap);

		sourceEntitySet.add(sa1);
		sourceEntitySet.add(sa2);
		targetEntitySet.add(sa3);
		targetEntitySet.add(sa4);

		vectorSpaceVectorFactoryService = new VectorSpaceVectorFactoryServiceImpl();
		entityLevelRelationship = new ELREntityLevelRelationship(elrChromosomeStub, null, sourceEntitySet, targetEntitySet);
		entityLevelRelationship.setVectorSpaceVectorFactoryService(vectorSpaceVectorFactoryService);
	}

	@Test
	public void testGenerateSourceVectorSpaceVectors() {
		List<VectorSpaceVector> sourceVectorSpaceVectors = entityLevelRelationship.generateSourceVectorSpaceVectors();

		assertEquals(2, sourceVectorSpaceVectors.size());

		VectorSpaceVector vector1 = sourceVectorSpaceVectors.get(0);
		assertTrue(vector1 instanceof HorizontalPartitioningVector);
		assertNotNull(entityLevelRelationship.getSourceVectors());
		assertEquals(2, entityLevelRelationship.getSourceVectors().size());
		assertTrue(entityLevelRelationship.getSourceVectors().contains(vector1));

		VectorSpaceVector vector2 = sourceVectorSpaceVectors.get(1);
		assertTrue(vector2 instanceof VerticalPartitioningVector);
		assertNotNull(entityLevelRelationship.getSourceVectors());
		assertEquals(2, entityLevelRelationship.getSourceVectors().size());
		assertTrue(entityLevelRelationship.getSourceVectors().contains(vector2));
	}

	@Test
	public void testGenerateTargetVectorSpaceVectors() {
		List<VectorSpaceVector> targetVectorSpaceVectors = entityLevelRelationship.generateTargetVectorSpaceVectors();

		assertEquals(2, targetVectorSpaceVectors.size());

		VectorSpaceVector vector1 = targetVectorSpaceVectors.get(0);
		assertTrue(vector1 instanceof HorizontalPartitioningVector);
		assertNotNull(entityLevelRelationship.getTargetVectors());
		assertEquals(2, entityLevelRelationship.getTargetVectors().size());
		assertTrue(entityLevelRelationship.getTargetVectors().contains(vector1));

		VectorSpaceVector vector2 = targetVectorSpaceVectors.get(1);
		assertTrue(vector2 instanceof VerticalPartitioningVector);
		assertNotNull(entityLevelRelationship.getTargetVectors());
		assertEquals(2, entityLevelRelationship.getTargetVectors().size());
		assertTrue(entityLevelRelationship.getTargetVectors().contains(vector2));
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
