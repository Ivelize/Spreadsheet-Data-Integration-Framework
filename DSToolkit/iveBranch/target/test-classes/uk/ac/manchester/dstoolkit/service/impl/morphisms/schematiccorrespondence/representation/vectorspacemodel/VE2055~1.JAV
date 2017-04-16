package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

public class VectorSpaceVectorTestCalculate_T_determineTotalNumberOfConstructsInThisVector {

	SingleVector singleVector;
	LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap;

	@Before
	public void setUp() throws Exception {
		singleVector = new SingleVector(null, null, false);
		constructsWeightsMap = new LinkedHashMap<CanonicalModelConstruct[], Double>();
	}

	@Test
	public void testCalculate_T_determineTotalNumberOfConstructsInThisVectorEmptyConstructsWeightsMap() {
		assertEquals(0, singleVector.calculate_T_determineTotalNumberOfConstructsInThisVector(this.constructsWeightsMap));
	}

	@Test
	public void testCalculate_T_determineTotalNumberOfConstructsInThisVectorConstructsWeightsMapWithSingleEmptyArrayOfSpecifiedLength() {
		CanonicalModelConstruct[] array1 = new CanonicalModelConstruct[3];
		constructsWeightsMap.put(array1, null);
		assertEquals(3, singleVector.calculate_T_determineTotalNumberOfConstructsInThisVector(this.constructsWeightsMap));
	}

	@Test
	public void testCalculate_T_determineTotalNumberOfConstructsInThisVectorConstructsWeightsMapWithTwoEmptyArraysOfSpecifiedLength() {
		CanonicalModelConstruct[] array1 = new CanonicalModelConstruct[3];
		CanonicalModelConstruct[] array2 = new CanonicalModelConstruct[3];
		constructsWeightsMap.put(array1, null);
		constructsWeightsMap.put(array2, null);
		assertEquals(6, singleVector.calculate_T_determineTotalNumberOfConstructsInThisVector(this.constructsWeightsMap));
	}

	@Test
	public void testCalculate_T_determineTotalNumberOfConstructsInThisVectorConstructsWeightsMapWithTwoArraysOfSpecifiedLengthWithNullElements() {
		CanonicalModelConstruct[] array1 = new CanonicalModelConstruct[2];
		array1[0] = null;
		array1[1] = null;
		CanonicalModelConstruct[] array2 = new CanonicalModelConstruct[1];
		array2[0] = null;
		constructsWeightsMap.put(array1, null);
		constructsWeightsMap.put(array2, null);
		assertEquals(3, singleVector.calculate_T_determineTotalNumberOfConstructsInThisVector(this.constructsWeightsMap));
	}
}
