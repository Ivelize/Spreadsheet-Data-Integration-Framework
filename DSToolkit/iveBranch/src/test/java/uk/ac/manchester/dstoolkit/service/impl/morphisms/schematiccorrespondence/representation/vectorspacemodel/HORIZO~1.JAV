package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class HorizontalPartitioningVectorTestFillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities {

	private HorizontalPartitioningVector horizontalPartitioningVector;
	private SuperLexical superLexical;
	private Set<SuperLexical> equivalentSuperLexicals;
	private List<SuperAbstract> allEntitiesList;
	private SuperLexical[] equivalentSuperLexicalsWithNullAsPlaceholder;
	private Set<SuperLexical> processedSuperLexicals;

	private SuperLexical sl1, sl2, sl3;
	private SuperAbstract sa1, sa2, sa3;

	@Before
	public void setUp() throws Exception {
		horizontalPartitioningVector = new HorizontalPartitioningVector(null, null, false);

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

		superLexical = sl1;

		equivalentSuperLexicalsWithNullAsPlaceholder = new SuperLexical[3];
		equivalentSuperLexicalsWithNullAsPlaceholder[0] = null;
		equivalentSuperLexicalsWithNullAsPlaceholder[1] = null;
		equivalentSuperLexicalsWithNullAsPlaceholder[2] = null;

		processedSuperLexicals = new HashSet<SuperLexical>();
	}

	@Test
	public void testFillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntitiesSameNumberOfSuperLexicalsAsEntitiesNoSuperLexicalsProcessed() {
		equivalentSuperLexicals.add(sl3);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);

		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		SuperLexical[] vectorElementArray = this.horizontalPartitioningVector.fillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities(
				superLexical, equivalentSuperLexicals, allEntitiesList, equivalentSuperLexicalsWithNullAsPlaceholder, processedSuperLexicals);
		assertEquals(3, vectorElementArray.length);
		assertEquals(sl1, vectorElementArray[0]);
		assertEquals(sl2, vectorElementArray[1]);
		assertEquals(sl3, vectorElementArray[2]);
	}

	@Test
	public void testFillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntitiesLessNumberOfSuperLexicalsThanEntitiesNoSuperLexicalsProcessed() {
		equivalentSuperLexicals.add(sl3);
		equivalentSuperLexicals.add(sl2);

		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		SuperLexical[] vectorElementArray = this.horizontalPartitioningVector.fillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities(
				superLexical, equivalentSuperLexicals, allEntitiesList, equivalentSuperLexicalsWithNullAsPlaceholder, processedSuperLexicals);
		assertEquals(3, vectorElementArray.length);
		assertEquals(null, vectorElementArray[0]);
		assertEquals(sl2, vectorElementArray[1]);
		assertEquals(sl3, vectorElementArray[2]);
	}

	@Test
	public void testFillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntitiesSameNumberOfSuperLexicalsAsEntitiesOneSuperLexicalsProcessed() {
		equivalentSuperLexicals.add(sl3);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);

		allEntitiesList.add(sa1);
		allEntitiesList.add(sa2);
		allEntitiesList.add(sa3);

		processedSuperLexicals.add(sl2);

		SuperLexical[] vectorElementArray = this.horizontalPartitioningVector.fillVectorElementArrayWithEquivalentSuperLexicalsIfPresentForEntities(
				superLexical, equivalentSuperLexicals, allEntitiesList, equivalentSuperLexicalsWithNullAsPlaceholder, processedSuperLexicals);
		assertEquals(3, vectorElementArray.length);
		assertEquals(sl1, vectorElementArray[0]);
		assertEquals(null, vectorElementArray[1]);
		assertEquals(sl3, vectorElementArray[2]);
	}

}
