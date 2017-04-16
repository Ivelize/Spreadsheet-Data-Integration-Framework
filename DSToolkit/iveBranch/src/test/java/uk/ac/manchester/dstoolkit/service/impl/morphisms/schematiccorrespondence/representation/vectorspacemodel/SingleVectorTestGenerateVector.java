package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class SingleVectorTestGenerateVector {

	private SingleVector singleVector;

	@Before
	public void setUp() throws Exception {
		singleVector = new SingleVector(null, null, false);
	}

	@Test
	public void testGenerateVector() {
		LinkedHashSet<SuperAbstract> entitySet = new LinkedHashSet<SuperAbstract>();

		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperLexical sl1 = new SuperLexical("sl1", null);
		sl1.setParentSuperAbstract(sa1);
		SuperLexical sl2 = new SuperLexical("sl2", null);
		sl2.setParentSuperAbstract(sa1);
		SuperLexical sl3 = new SuperLexical("sl3", null);
		sl3.setParentSuperAbstract(sa1);

		entitySet.add(sa1);

		LinkedHashMap<CanonicalModelConstruct[], Double> vector = singleVector.generateVector(entitySet);

		assertEquals(4, vector.size());
		int i = 0;
		for (CanonicalModelConstruct[] constructs : vector.keySet()) {
			assertEquals(1, constructs.length);
			assertEquals(new Double(0d), vector.get(constructs));
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

}
