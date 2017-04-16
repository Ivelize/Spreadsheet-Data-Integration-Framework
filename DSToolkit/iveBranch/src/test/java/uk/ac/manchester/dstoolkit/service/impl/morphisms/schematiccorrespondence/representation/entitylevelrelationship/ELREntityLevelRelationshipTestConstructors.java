package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

public class ELREntityLevelRelationshipTestConstructors {

	private ELRChromosome elrChromosome;

	@Before
	public void setUp() throws Exception {
		elrChromosome = new ELRChromosome(null, null, null);
	}

	@Test
	public void testELREntityLevelRelationshipELRChromosomeELRPhenotypeSuperAbstractSuperAbstract() {
		SuperAbstract sourceEntity = new SuperAbstract("sa1", null);
		SuperAbstract targetEntity = new SuperAbstract("sa2", null);

		ELREntityLevelRelationship entityLevelRelationship = new ELREntityLevelRelationship(elrChromosome, null, sourceEntity, targetEntity);

		assertEquals(1, entityLevelRelationship.getSourceEntitySet().size());
		assertEquals(1, entityLevelRelationship.getTargetEntitySet().size());
		assertTrue(entityLevelRelationship.getSourceEntitySet().contains(sourceEntity));
		assertTrue(entityLevelRelationship.getTargetEntitySet().contains(targetEntity));
	}

	@Test
	public void testELREntityLevelRelationshipELRChromosomeELRPhenotypeLinkedHashSetOfSuperAbstractLinkedHashSetOfSuperAbstract() {
		LinkedHashSet<SuperAbstract> sourceEntitySet = new LinkedHashSet<SuperAbstract>();
		LinkedHashSet<SuperAbstract> targetEntitySet = new LinkedHashSet<SuperAbstract>();
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		sourceEntitySet.add(sa1);
		sourceEntitySet.add(sa2);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		SuperAbstract sa4 = new SuperAbstract("sa4", null);
		targetEntitySet.add(sa3);
		targetEntitySet.add(sa4);

		ELREntityLevelRelationship entityLevelRelationship = new ELREntityLevelRelationship(elrChromosome, null, sourceEntitySet, targetEntitySet);

		assertEquals(2, entityLevelRelationship.getSourceEntitySet().size());
		assertEquals(2, entityLevelRelationship.getTargetEntitySet().size());
		assertTrue(entityLevelRelationship.getSourceEntitySet().contains(sa1));
		assertTrue(entityLevelRelationship.getSourceEntitySet().contains(sa2));
		assertTrue(entityLevelRelationship.getTargetEntitySet().contains(sa3));
		assertTrue(entityLevelRelationship.getTargetEntitySet().contains(sa4));
	}
}
