package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;

public class VerticalPartitioningVectorTestGenerateVector {

	private ELRChromosomeStub elrChromosomeStub;
	private VerticalPartitioningVector verticalPartitioningVector;
	private LinkedHashSet<SuperAbstract> entitySet;
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap;

	private SuperAbstract sa1, sa2, sa3;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8;

	@Before
	public void setUp() throws Exception {
		elrChromosomeStub = new ELRChromosomeStub(null, null, null);
		entitySet = new LinkedHashSet<SuperAbstract>();
		verticalPartitioningVector = new VerticalPartitioningVector(elrChromosomeStub, null, false);
		equivalentSuperLexicalsSetsMap = new HashMap<SuperLexical, Set<SuperLexical>>();

		sa1 = new SuperAbstract("sa1", null);
		sa2 = new SuperAbstract("sa2", null);
		sa3 = new SuperAbstract("sa3", null);

		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl3 = new SuperLexical("sl3", null);
		sl4 = new SuperLexical("sl4", null);
		sl5 = new SuperLexical("sl5", null);
		sl6 = new SuperLexical("sl6", null);
		sl7 = new SuperLexical("sl7", null);
		sl8 = new SuperLexical("sl8", null);

		sa1.addSuperLexical(sl1);
		sa1.addSuperLexical(sl2);
		sa1.addSuperLexical(sl3);

		sa2.addSuperLexical(sl4);
		sa2.addSuperLexical(sl5);

		sa3.addSuperLexical(sl6);
		sa3.addSuperLexical(sl7);
		sa3.addSuperLexical(sl8);

		Set<SuperLexical> equivalentSuperLexicals1 = new HashSet<SuperLexical>();
		equivalentSuperLexicals1.add(sl1);
		equivalentSuperLexicals1.add(sl5);
		equivalentSuperLexicals1.add(sl7);
		equivalentSuperLexicalsSetsMap.put(sl1, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl5, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl7, equivalentSuperLexicals1);

		Set<SuperLexical> equivalentSuperLexicals2 = new HashSet<SuperLexical>();
		equivalentSuperLexicals2.add(sl2);
		equivalentSuperLexicals2.add(sl8);
		equivalentSuperLexicalsSetsMap.put(sl2, equivalentSuperLexicals2);
		equivalentSuperLexicalsSetsMap.put(sl8, equivalentSuperLexicals2);

		elrChromosomeStub.setEquivalentSuperLexicalsSetsMap(equivalentSuperLexicalsSetsMap);
	}

	@Test
	public void testGenerateVectorAllEntitiesOfEquivalentSuperLexicalsInEntitySet() {
		entitySet.add(sa1);
		entitySet.add(sa2);
		entitySet.add(sa3);

		LinkedHashMap<CanonicalModelConstruct[], Double> vector = verticalPartitioningVector.generateVector(entitySet);

		assertEquals(7, vector.size());
		int i = 0;
		for (CanonicalModelConstruct[] constructs : vector.keySet()) {
			assertTrue(constructs.length == 1 || constructs.length == 3);
			assertEquals(new Double(0d), vector.get(constructs));
			switch (i) {
			case 0:
				assertEquals(3, constructs.length);
				assertEquals(sa1, constructs[0]);
				assertEquals(sa2, constructs[1]);
				assertEquals(sa3, constructs[2]);
				break;
			case 1:
				assertEquals(3, constructs.length);
				assertEquals(sl1, constructs[0]);
				assertEquals(sl5, constructs[1]);
				assertEquals(sl7, constructs[2]);
				break;
			case 2:
				assertEquals(1, constructs.length);
				assertTrue(constructs[0].equals(sl2) || constructs[0].equals(sl8));
				break;
			case 3:
				assertEquals(1, constructs.length);
				assertTrue(constructs[0].equals(sl2) || constructs[0].equals(sl8));
				break;
			case 4:
				assertEquals(1, constructs.length);
				assertEquals(sl3, constructs[0]);
				break;
			case 5:
				assertEquals(1, constructs.length);
				assertEquals(sl4, constructs[0]);
				break;
			case 6:
				assertEquals(1, constructs.length);
				assertEquals(sl6, constructs[0]);
				break;
			}
			i++;
		}
	}

	@Test
	public void testGenerateVectorNotAllEntitiesOfEquivalentSuperLexicalsInEntitySet() {
		entitySet.add(sa1);
		entitySet.add(sa3);

		LinkedHashMap<CanonicalModelConstruct[], Double> vector = verticalPartitioningVector.generateVector(entitySet);

		assertEquals(5, vector.size());
		int i = 0;
		for (CanonicalModelConstruct[] constructs : vector.keySet()) {
			assertTrue(constructs.length == 1 || constructs.length == 2);
			assertEquals(new Double(0d), vector.get(constructs));
			switch (i) {
			case 0:
				assertEquals(2, constructs.length);
				assertEquals(sa1, constructs[0]);
				assertEquals(sa3, constructs[1]);
				break;
			case 1:
				assertEquals(2, constructs.length);
				assertEquals(sl1, constructs[0]);
				assertEquals(sl7, constructs[1]);
				break;
			case 2:
				assertEquals(2, constructs.length);
				assertEquals(sl2, constructs[0]);
				assertEquals(sl8, constructs[1]);
				break;
			case 3:
				assertEquals(1, constructs.length);
				assertEquals(sl3, constructs[0]);
				break;
			case 4:
				assertEquals(1, constructs.length);
				assertEquals(sl6, constructs[0]);
				break;
			}
			i++;
		}
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
