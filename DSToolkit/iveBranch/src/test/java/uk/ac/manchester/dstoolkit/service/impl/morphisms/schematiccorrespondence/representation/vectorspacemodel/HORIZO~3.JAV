package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class HorizontalPartitioningVectorTestGenerateArrayForEquivalentLexicalsElementAndSetAllNull {

	private HorizontalPartitioningVector horizontalPartitioningVector;

	@Before
	public void setUp() throws Exception {
		horizontalPartitioningVector = new HorizontalPartitioningVector(null, null, false);
	}

	@Test
	public void testGenerateArrayForEquivalentLexicalsElementAndSetAllNullLength0() {
		int length = 0;
		SuperLexical[] equivalentAttributes = this.horizontalPartitioningVector.generateArrayForEquivalentLexicalsElementAndSetAllNull(length);
		assertEquals(length, equivalentAttributes.length);
	}

	@Test
	public void testGenerateArrayForEquivalentLexicalsElementAndSetAllNullLength1() {
		int length = 1;
		SuperLexical[] equivalentAttributes = this.horizontalPartitioningVector.generateArrayForEquivalentLexicalsElementAndSetAllNull(length);
		assertEquals(length, equivalentAttributes.length);
		assertEquals(null, equivalentAttributes[0]);
	}

	@Test
	public void testGenerateArrayForEquivalentLexicalsElementAndSetAllNullLength2() {
		int length = 2;
		SuperLexical[] equivalentAttributes = this.horizontalPartitioningVector.generateArrayForEquivalentLexicalsElementAndSetAllNull(length);
		assertEquals(length, equivalentAttributes.length);
		assertEquals(null, equivalentAttributes[0]);
		assertEquals(null, equivalentAttributes[1]);
	}

}
