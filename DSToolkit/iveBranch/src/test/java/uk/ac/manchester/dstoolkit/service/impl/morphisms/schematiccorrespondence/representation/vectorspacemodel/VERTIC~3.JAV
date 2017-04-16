package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class VerticalPartitioningVectorTestIdentifyEquivalentSuperLexicalsForEntitiesInAllEntities {

	private VerticalPartitioningVector verticalPartitioningVector;

	private Set<SuperLexical> equivalentSuperLexicals;
	private List<SuperAbstract> allEntitiesList;
	private Set<SuperLexical> processedSuperLexicals;

	private SuperLexical sl1, sl2, sl3;
	private SuperAbstract sa1, sa2, sa3;

	@Before
	public void setUp() throws Exception {
		verticalPartitioningVector = new VerticalPartitioningVector(null, null, false);

		sa1 = new SuperAbstract("sa1", null);
		sa2 = new SuperAbstract("sa2", null);
		sa3 = new SuperAbstract("sa3", null);

		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);

		sa1.addSuperLexical(sl1);
		sa2.addSuperLexical(sl2);
		sa3.addSuperLexical(sl3);

		equivalentSuperLexicals = new HashSet<SuperLexical>();
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);
		equivalentSuperLexicals.add(sl3);

		allEntitiesList = new ArrayList<SuperAbstract>();
	}

	@Test
	public void testIdentifyEquivalentSuperLexicalsForEntitiesInAllEntitiesNoSuperLexicalsProcessed() {
		SuperLexical superLexical = sl1;

		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		processedSuperLexicals = new HashSet<SuperLexical>();

		Set<SuperLexical> equivalentSuperLexicalsForEntities = verticalPartitioningVector.identifyEquivalentSuperLexicalsForEntitiesInAllEntities(
				superLexical, equivalentSuperLexicals, allEntitiesList, processedSuperLexicals);

		assertEquals(3, equivalentSuperLexicalsForEntities.size());
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl1));
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl2));
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl3));
	}

	@Test
	public void testIdentifyEquivalentSuperLexicalsForEntitiesInAllEntitiesOneSuperLexicalProcessed() {
		SuperLexical superLexical = sl1;

		allEntitiesList = new ArrayList<SuperAbstract>();
		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		processedSuperLexicals = new HashSet<SuperLexical>();
		processedSuperLexicals.add(sl2);

		Set<SuperLexical> equivalentSuperLexicalsForEntities = verticalPartitioningVector.identifyEquivalentSuperLexicalsForEntitiesInAllEntities(
				superLexical, equivalentSuperLexicals, allEntitiesList, processedSuperLexicals);

		assertEquals(2, equivalentSuperLexicalsForEntities.size());
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl1));
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl3));
	}

	@Test
	public void testIdentifyEquivalentSuperLexicalsForEntitiesInAllEntitiesSuperAbstractNotInAllEntitiesList() {
		SuperLexical superLexical = sl1;

		SuperAbstract sa4 = new SuperAbstract("sa4", null);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		sa4.addSuperLexical(sl4);
		equivalentSuperLexicals.add(sl4);

		allEntitiesList = new ArrayList<SuperAbstract>();
		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		processedSuperLexicals = new HashSet<SuperLexical>();

		Set<SuperLexical> equivalentSuperLexicalsForEntities = verticalPartitioningVector.identifyEquivalentSuperLexicalsForEntitiesInAllEntities(
				superLexical, equivalentSuperLexicals, allEntitiesList, processedSuperLexicals);

		assertEquals(3, equivalentSuperLexicalsForEntities.size());
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl1));
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl2));
		assertTrue(equivalentSuperLexicalsForEntities.contains(sl3));
	}

}
