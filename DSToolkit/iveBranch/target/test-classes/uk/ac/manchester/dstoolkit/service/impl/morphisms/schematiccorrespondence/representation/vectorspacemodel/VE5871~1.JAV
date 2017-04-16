package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.vectorspacemodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;

public class VectorSpaceVectorTestCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs {

	private ELRChromosomeStub elrChromosomeStub;
	private ELRPhenotype elrPhenotype;
	private Map<SuperLexical, Set<SuperLexical>> equivalentSuperLexicalsSetsMap;
	private SingleVector singleVector;
	private HorizontalPartitioningVector horizontalPartitioningVector;
	private VerticalPartitioningVector verticalPartitioningVector;
	private CanonicalModelConstruct[] constructs;
	private SuperAbstract sa1, sa2, sa3, sa4;
	private SuperLexical sl1, sl2, sl3, sl4, sl5, sl6, sl7, sl8, sl9, sl10;

	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicalsSetsMap = new HashMap<SuperLexical, Set<SuperLexical>>();
		elrPhenotype = new ELRPhenotype(null);
		elrChromosomeStub = new ELRChromosomeStub(null, null, null);

		sa1 = new SuperAbstract("sa1", null);
		sl1 = new SuperLexical("sl1", null);
		sl2 = new SuperLexical("sl2", null);
		sl1.setParentSuperAbstract(sa1);
		sl2.setParentSuperAbstract(sa1);

		sa2 = new SuperAbstract("sa2", null);
		sl3 = new SuperLexical("sl3", null);
		sl4 = new SuperLexical("sl4", null);
		sl5 = new SuperLexical("sl5", null);
		sl3.setParentSuperAbstract(sa2);
		sl4.setParentSuperAbstract(sa2);
		sl5.setParentSuperAbstract(sa2);

		sa3 = new SuperAbstract("sa3", null);
		sl6 = new SuperLexical("sl6", null);
		sl7 = new SuperLexical("sl7", null);
		sl6.setParentSuperAbstract(sa3);
		sl7.setParentSuperAbstract(sa3);

		sa4 = new SuperAbstract("sa4", null);
		sl8 = new SuperLexical("sl8", null);
		sl9 = new SuperLexical("sl9", null);
		sl10 = new SuperLexical("sl10", null);
		sl8.setParentSuperAbstract(sa4);
		sl9.setParentSuperAbstract(sa4);
		sl10.setParentSuperAbstract(sa4);

		horizontalPartitioningVector = new HorizontalPartitioningVector(elrChromosomeStub, elrPhenotype, true);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap1 = new LinkedHashMap<CanonicalModelConstruct[], Double>();

		CanonicalModelConstruct[] constructs1 = { sa1, sa2 };
		constructsWeightsMap1.put(constructs1, null);
		CanonicalModelConstruct[] constructs2 = { sl1, sl3 };
		constructsWeightsMap1.put(constructs2, null);
		CanonicalModelConstruct[] constructs3 = { sl2, sl4 };
		constructsWeightsMap1.put(constructs3, null);
		CanonicalModelConstruct[] constructs4 = { null, sl5 };
		constructsWeightsMap1.put(constructs4, null);
		horizontalPartitioningVector.setConstructsWeightsMap(constructsWeightsMap1);

		verticalPartitioningVector = new VerticalPartitioningVector(elrChromosomeStub, elrPhenotype, true);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap2 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		CanonicalModelConstruct[] constructs5 = { sa1, sa2 };
		constructsWeightsMap2.put(constructs5, null);
		CanonicalModelConstruct[] constructs6 = { sl1, sl3 };
		constructsWeightsMap2.put(constructs6, null);
		CanonicalModelConstruct[] constructs7 = { sl2, sl4 };
		constructsWeightsMap2.put(constructs7, null);
		CanonicalModelConstruct[] constructs8 = { sl5 };
		constructsWeightsMap2.put(constructs8, null);
		verticalPartitioningVector.setConstructsWeightsMap(constructsWeightsMap2);

		singleVector = new SingleVector(elrChromosomeStub, elrPhenotype, true);
		LinkedHashMap<CanonicalModelConstruct[], Double> constructsWeightsMap3 = new LinkedHashMap<CanonicalModelConstruct[], Double>();
		CanonicalModelConstruct[] constructs9 = { sa3 };
		constructsWeightsMap3.put(constructs9, null);
		CanonicalModelConstruct[] constructs10 = { sl6 };
		constructsWeightsMap3.put(constructs10, null);
		CanonicalModelConstruct[] constructs11 = { sl7 };
		constructsWeightsMap3.put(constructs11, null);
		singleVector.setConstructsWeightsMap(constructsWeightsMap3);

		LinkedHashSet<SuperAbstract> entitySet1 = new LinkedHashSet<SuperAbstract>();
		entitySet1.add(sa1);
		entitySet1.add(sa2);

		ELREntityLevelRelationship elr1 = new ELREntityLevelRelationship(elrChromosomeStub, elrPhenotype, entitySet1, null);
		List<VectorSpaceVector> sourceVector1 = new ArrayList<VectorSpaceVector>();
		sourceVector1.add(horizontalPartitioningVector);
		sourceVector1.add(verticalPartitioningVector);
		elr1.setSourceVectors(sourceVector1);
		elrPhenotype.addELR(elr1);

		LinkedHashSet<SuperAbstract> entitySet2 = new LinkedHashSet<SuperAbstract>();
		entitySet2.add(sa3);

		ELREntityLevelRelationship elr2 = new ELREntityLevelRelationship(elrChromosomeStub, elrPhenotype, entitySet2, null);
		List<VectorSpaceVector> sourceVector2 = new ArrayList<VectorSpaceVector>();
		sourceVector2.add(singleVector);
		elr2.setSourceVectors(sourceVector2);
		elrPhenotype.addELR(elr2);

		elrPhenotype.addMatchedButUnassociatedSourceEntity(sa4);

		Set<SuperLexical> equivalentSuperLexicals1 = new HashSet<SuperLexical>();
		equivalentSuperLexicals1.add(sl1);
		equivalentSuperLexicals1.add(sl3);
		equivalentSuperLexicals1.add(sl6);
		equivalentSuperLexicals1.add(sl8);
		equivalentSuperLexicalsSetsMap.put(sl1, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl3, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl6, equivalentSuperLexicals1);
		equivalentSuperLexicalsSetsMap.put(sl8, equivalentSuperLexicals1);

		Set<SuperLexical> equivalentSuperLexicals2 = new HashSet<SuperLexical>();
		equivalentSuperLexicals2.add(sl2);
		equivalentSuperLexicals2.add(sl4);
		equivalentSuperLexicalsSetsMap.put(sl2, equivalentSuperLexicals2);
		equivalentSuperLexicalsSetsMap.put(sl4, equivalentSuperLexicals2);

		Set<SuperLexical> equivalentSuperLexicals3 = new HashSet<SuperLexical>();
		equivalentSuperLexicals3.add(sl7);
		equivalentSuperLexicals3.add(sl9);
		equivalentSuperLexicalsSetsMap.put(sl7, equivalentSuperLexicals3);
		equivalentSuperLexicalsSetsMap.put(sl9, equivalentSuperLexicals3);

		elrChromosomeStub.setEquivalentSuperLexicalsSetsMap(equivalentSuperLexicalsSetsMap);
	}

	@Test
	public void testCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructsForSuperAbstracts() {
		constructs = new CanonicalModelConstruct[2];
		constructs[0] = sa1;
		constructs[1] = sa2;
		int n = this.horizontalPartitioningVector.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);
		assertEquals(1, n);
	}

	@Test
	public void testCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructsForSuperLexicalsWithEquivalentSuperLexicalsInHPandVPvectors() {
		constructs = new CanonicalModelConstruct[2];
		constructs[0] = sl2;
		constructs[1] = sl4;
		int n = this.horizontalPartitioningVector.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);
		assertEquals(1, n);
	}

	@Test
	public void testCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructsForSuperLexicalsWithEquivSLsInHPVPSingleVectorsAndUnassociatedEnt() {
		constructs = new CanonicalModelConstruct[2];
		constructs[0] = sl1;
		constructs[1] = sl3;
		int n = this.horizontalPartitioningVector.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);
		assertEquals(3, n);
	}

	@Test
	public void testCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructsForSuperLexicalsWithNullConstruct() {
		constructs = new CanonicalModelConstruct[2];
		constructs[0] = null;
		constructs[1] = sl5;
		int n = this.horizontalPartitioningVector.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);
		assertEquals(1, n);
	}

	@Test
	public void testCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructsForSuperLexicalsNoEquivalentSuperLexicals() {
		constructs = new CanonicalModelConstruct[1];
		constructs[0] = sl5;
		int n = this.horizontalPartitioningVector.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);
		assertEquals(1, n);
	}

	@Test
	public void testCalculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructsForSuperLexicalsSLinSingleVectorAndUnassociatedEntity() {
		constructs = new CanonicalModelConstruct[2];
		constructs[0] = sl7;
		constructs[1] = sl9;
		int n = this.horizontalPartitioningVector.calculate_n_determineNumberOfVectorsInVectorSpaceOfELRWithEquivalentConstructs(constructs);
		assertEquals(2, n);
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
			if (equivalentSuperLexicalsSetsMap.containsKey(superLexical))
				return equivalentSuperLexicalsSetsMap.get(superLexical);
			return new HashSet<SuperLexical>();
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
