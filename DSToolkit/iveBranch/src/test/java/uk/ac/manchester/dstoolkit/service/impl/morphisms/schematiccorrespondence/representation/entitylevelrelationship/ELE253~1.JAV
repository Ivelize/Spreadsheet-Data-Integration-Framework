package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.SingleVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVectorFactoryService;

public class ELREntityLevelRelationshipTestGenerateSourceAndTargetVectorSpaceVectorsForSingleEntity {

	private ELRChromosome elrChromosome;
	private VectorSpaceVectorFactoryService vectorSpaceVectorFactoryService;
	private ELREntityLevelRelationship entityLevelRelationship;
	private SuperAbstract sourceEntity, targetEntity;

	@Before
	public void setUp() throws Exception {
		elrChromosome = new ELRChromosome(null, null, null);
		sourceEntity = new SuperAbstract("sa1", null);
		targetEntity = new SuperAbstract("sa2", null);
		vectorSpaceVectorFactoryService = new VectorSpaceVectorFactoryServiceImpl();
		entityLevelRelationship = new ELREntityLevelRelationship(elrChromosome, null, sourceEntity, targetEntity);
		entityLevelRelationship.setVectorSpaceVectorFactoryService(vectorSpaceVectorFactoryService);
	}

	@Test
	public void testGenerateSourceVectorSpaceVectorsSingleEntity() {
		List<VectorSpaceVector> sourceVectorSpaceVectors = entityLevelRelationship.generateSourceVectorSpaceVectors();
		assertEquals(1, sourceVectorSpaceVectors.size());

		VectorSpaceVector vector = sourceVectorSpaceVectors.get(0);
		assertTrue(vector instanceof SingleVector);
		assertNotNull(entityLevelRelationship.getSourceVectors());
		assertEquals(1, entityLevelRelationship.getSourceVectors().size());
		assertTrue(entityLevelRelationship.getSourceVectors().contains(vector));
	}

	@Test
	public void testGenerateTargetVectorSpaceVectorsSingleEntity() {
		List<VectorSpaceVector> targetVectorSpaceVectors = entityLevelRelationship.generateTargetVectorSpaceVectors();
		assertEquals(1, targetVectorSpaceVectors.size());

		VectorSpaceVector vector = targetVectorSpaceVectors.get(0);
		assertTrue(vector instanceof SingleVector);
		assertNotNull(entityLevelRelationship.getTargetVectors());
		assertEquals(1, entityLevelRelationship.getTargetVectors().size());
		assertTrue(entityLevelRelationship.getTargetVectors().contains(vector));
	}
}
