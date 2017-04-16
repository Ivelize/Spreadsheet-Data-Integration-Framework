package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class VerticalPartitioningVectorTestGenerateVectorElementArrasy {

	private VerticalPartitioningVector verticalPartitioningVector;

	private Set<SuperLexical> equivalentSuperLexicals;
	private List<SuperAbstract> allEntitiesList;

	private SuperLexical sl1, sl2, sl3;
	private SuperAbstract sa1, sa2, sa3;

	@Before
	public void setUp() throws Exception {
		verticalPartitioningVector = new VerticalPartitioningVector(null, null, false);

		equivalentSuperLexicals = new HashSet<SuperLexical>();
		allEntitiesList = new ArrayList<SuperAbstract>();

		sa1 = new SuperAbstract("sa1", null);
		sa2 = new SuperAbstract("sa2", null);
		sa3 = new SuperAbstract("sa3", null);

		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);

		sa1.addSuperLexical(sl1);
		sa2.addSuperLexical(sl2);
		sa3.addSuperLexical(sl3);
	}

	@Test
	public void testGenerateVectorElementArraysSameNumberOfSuperLexicalsAsEntities() {

		equivalentSuperLexicals.add(sl3);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);

		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		List<SuperLexical[]> vectorElementArrays = this.verticalPartitioningVector.generateVectorElementArrays(allEntitiesList,
				equivalentSuperLexicals);

		assertEquals(1, vectorElementArrays.size());
		assertEquals(3, vectorElementArrays.get(0).length);
		SuperLexical[] vectorElementArray = vectorElementArrays.get(0);
		assertEquals(sl1, vectorElementArray[0]);
		assertEquals(sl2, vectorElementArray[1]);
		assertEquals(sl3, vectorElementArray[2]);
	}

	@Test
	public void testGenerateVectorElementArraysLessNumberOfSuperLexicalsThanEntities() {

		equivalentSuperLexicals.add(sl3);
		equivalentSuperLexicals.add(sl2);

		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		List<SuperLexical[]> vectorElementArrays = this.verticalPartitioningVector.generateVectorElementArrays(allEntitiesList,
				equivalentSuperLexicals);

		assertEquals(2, vectorElementArrays.size());

		SuperLexical[] vectorElementArray1 = vectorElementArrays.get(0);
		assertEquals(1, vectorElementArray1.length);
		assertTrue(vectorElementArray1[0].equals(sl2) || vectorElementArray1[0].equals(sl3));

		SuperLexical[] vectorElementArray2 = vectorElementArrays.get(1);
		assertEquals(1, vectorElementArray2.length);
		assertTrue(vectorElementArray2[0].equals(sl2) || vectorElementArray2[0].equals(sl3));

		assertFalse(vectorElementArray1[0].equals(vectorElementArray2[0]));
	}

}
