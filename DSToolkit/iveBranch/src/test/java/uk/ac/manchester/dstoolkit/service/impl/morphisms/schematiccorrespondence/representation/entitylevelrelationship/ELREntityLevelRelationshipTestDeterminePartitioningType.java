package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.PartitioningType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.HorizontalPartitioningVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.SingleVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VectorSpaceVector;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel.VerticalPartitioningVector;

public class ELREntityLevelRelationshipTestDeterminePartitioningType {

	private ELREntityLevelRelationship elrEntityLevelRelationship;
	private VectorSpaceVector vectorSpaceVector1, vectorSpaceVector2;

	@Before
	public void setUp() throws Exception {
		elrEntityLevelRelationship = new ELREntityLevelRelationship(null, null, new LinkedHashSet<SuperAbstract>(),
				new LinkedHashSet<SuperAbstract>());
	}

	@Test
	public void testDeterminePartitioningTypeTwoSingleVectors() {
		vectorSpaceVector1 = new SingleVector(null, null, true);
		vectorSpaceVector2 = new SingleVector(null, null, false);

		assertNull(elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeSingleVectorAndHorizontalPartitioningVector() {
		vectorSpaceVector1 = new SingleVector(null, null, true);
		vectorSpaceVector2 = new HorizontalPartitioningVector(null, null, false);

		assertEquals(PartitioningType.HP_VS_HP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeSingleVectorAndVerticalPartitioningVector() {
		vectorSpaceVector1 = new SingleVector(null, null, true);
		vectorSpaceVector2 = new VerticalPartitioningVector(null, null, false);

		assertEquals(PartitioningType.HP_VS_VP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeHorizontalPartitioningVectorAndSingleVector() {
		vectorSpaceVector1 = new HorizontalPartitioningVector(null, null, true);
		vectorSpaceVector2 = new SingleVector(null, null, false);

		assertEquals(PartitioningType.HP_VS_HP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeHorizontalPartitioningVectorAndHorizontalVector() {
		vectorSpaceVector1 = new HorizontalPartitioningVector(null, null, true);
		vectorSpaceVector2 = new HorizontalPartitioningVector(null, null, false);

		assertEquals(PartitioningType.HP_VS_HP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeHorizontalPartitioningVectorAndVerticalVector() {
		vectorSpaceVector1 = new HorizontalPartitioningVector(null, null, true);
		vectorSpaceVector2 = new VerticalPartitioningVector(null, null, false);

		assertEquals(PartitioningType.HP_VS_VP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeVerticalPartitioningVectorAndSingleVector() {
		vectorSpaceVector1 = new VerticalPartitioningVector(null, null, true);
		vectorSpaceVector2 = new SingleVector(null, null, false);

		assertEquals(PartitioningType.VP_VS_HP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeVerticalPartitioningVectorAndHorizontalPartitioningVector() {
		vectorSpaceVector1 = new VerticalPartitioningVector(null, null, true);
		vectorSpaceVector2 = new HorizontalPartitioningVector(null, null, false);

		assertEquals(PartitioningType.VP_VS_HP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

	@Test
	public void testDeterminePartitioningTypeVerticalPartitioningVectorAndVerticalPartitioningVector() {
		vectorSpaceVector1 = new VerticalPartitioningVector(null, null, true);
		vectorSpaceVector2 = new VerticalPartitioningVector(null, null, false);

		assertEquals(PartitioningType.VP_VS_VP, elrEntityLevelRelationship.determinePartitioningType(vectorSpaceVector1, vectorSpaceVector2));
	}

}
