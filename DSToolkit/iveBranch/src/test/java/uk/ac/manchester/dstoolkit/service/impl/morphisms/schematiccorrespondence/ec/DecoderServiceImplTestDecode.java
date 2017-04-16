package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.PairOfEntitySets;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRChromosome;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELREntityLevelRelationship;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship.ELRPhenotype;

public class DecoderServiceImplTestDecode {

	private DecoderServiceImpl decoderServiceImpl;
	private Set<PairOfEntitySets> pairsOfEntitySets;
	private ELRChromosomeStub chromosomeStub;
	private SuperAbstract superAbstract1, superAbstract2, superAbstract3, superAbstract4, superAbstract5, superAbstract6;

	@Before
	public void setUp() throws Exception {
		decoderServiceImpl = new DecoderServiceImpl();
		pairsOfEntitySets = new HashSet<PairOfEntitySets>();
		superAbstract1 = new SuperAbstract("sa1", null);
		superAbstract2 = new SuperAbstract("sa2", null);
		superAbstract3 = new SuperAbstract("sa3", null);
		superAbstract4 = new SuperAbstract("sa4", null);
		superAbstract5 = new SuperAbstract("sa5", null);
		superAbstract6 = new SuperAbstract("sa6", null);

		chromosomeStub = new ELRChromosomeStub(null, null, null);
		Set<SuperAbstract> superAbstractsInSourceSchemas = new HashSet<SuperAbstract>();
		superAbstractsInSourceSchemas.add(superAbstract1);
		superAbstractsInSourceSchemas.add(superAbstract2);
		superAbstractsInSourceSchemas.add(superAbstract3);

		Set<SuperAbstract> superAbstractsInTargetSchemas = new HashSet<SuperAbstract>();
		superAbstractsInTargetSchemas.add(superAbstract4);
		superAbstractsInTargetSchemas.add(superAbstract5);
		superAbstractsInTargetSchemas.add(superAbstract6);

		Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInSourceSchemas.add(superAbstract1);
		matchedSuperAbstractsInSourceSchemas.add(superAbstract2);
		matchedSuperAbstractsInSourceSchemas.add(superAbstract3);

		Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas = new HashSet<SuperAbstract>();
		matchedSuperAbstractsInTargetSchemas.add(superAbstract4);
		matchedSuperAbstractsInTargetSchemas.add(superAbstract5);
		matchedSuperAbstractsInTargetSchemas.add(superAbstract6);

		chromosomeStub.setSuperAbstractsInSourceSchemas(superAbstractsInSourceSchemas);
		chromosomeStub.setSuperAbstractsInTargetSchemas(superAbstractsInTargetSchemas);
		chromosomeStub.setMatchedSuperAbstractsInSourceSchemas(matchedSuperAbstractsInSourceSchemas);
		chromosomeStub.setMatchedSuperAbstractsInTargetSchemas(matchedSuperAbstractsInTargetSchemas);

		PairOfEntitySets pairOfEntitySets1 = new PairOfEntitySets(superAbstract1, superAbstract4);
		pairsOfEntitySets.add(pairOfEntitySets1);

		Set<SuperAbstract> sourceEntitySet = new HashSet<SuperAbstract>();
		sourceEntitySet.add(superAbstract2);
		sourceEntitySet.add(superAbstract3);
		Set<SuperAbstract> targetEntitySet = new HashSet<SuperAbstract>();
		targetEntitySet.add(superAbstract5);

		PairOfEntitySets pairOfEntitySets2 = new PairOfEntitySets(sourceEntitySet, targetEntitySet);
		pairsOfEntitySets.add(pairOfEntitySets2);
	}

	@Test
	public void testDecode() {
		ELRPhenotype phenotype = decoderServiceImpl.decode(pairsOfEntitySets, chromosomeStub);
		assertEquals(2, phenotype.getElrs().size());
		assertEquals(0, phenotype.getMatchedButUnassociatedSourceEntities().size());
		assertEquals(1, phenotype.getMatchedButUnassociatedTargetEntities().size());
		assertTrue(phenotype.getMatchedButUnassociatedTargetEntities().contains(superAbstract6));

		for (ELREntityLevelRelationship elr : phenotype.getElrs()) {
			if (elr.getSourceEntitySet().size() == 1) {
				assertEquals(1, elr.getSourceEntitySet().size());
				assertTrue(elr.getSourceEntitySet().contains(superAbstract1));
				assertEquals(1, elr.getTargetEntitySet().size());
				assertTrue(elr.getTargetEntitySet().contains(superAbstract4));
			} else {
				assertEquals(2, elr.getSourceEntitySet().size());
				assertTrue(elr.getSourceEntitySet().contains(superAbstract2));
				assertTrue(elr.getSourceEntitySet().contains(superAbstract3));
				assertEquals(1, elr.getTargetEntitySet().size());
				assertTrue(elr.getTargetEntitySet().contains(superAbstract5));
			}
		}
	}

	class ELRChromosomeStub extends ELRChromosome {

		public ELRChromosomeStub(LinkedHashSet<Schema> sourceSchemas, LinkedHashSet<Schema> targetSchemas, List<Matching> matchings) {
			super(sourceSchemas, targetSchemas, matchings);
		}

		@Override
		protected void setSuperAbstractsInSourceSchemas(Set<SuperAbstract> superAbstractsInSourceSchemas) {
			super.setSuperAbstractsInSourceSchemas(superAbstractsInSourceSchemas);
		}

		@Override
		protected void setSuperAbstractsInTargetSchemas(Set<SuperAbstract> superAbstractsInTargetSchemas) {
			super.setSuperAbstractsInTargetSchemas(superAbstractsInTargetSchemas);
		}

		@Override
		protected void setMatchedSuperAbstractsInSourceSchemas(Set<SuperAbstract> matchedSuperAbstractsInSourceSchemas) {
			super.setMatchedSuperAbstractsInSourceSchemas(matchedSuperAbstractsInSourceSchemas);
		}

		@Override
		protected void setMatchedSuperAbstractsInTargetSchemas(Set<SuperAbstract> matchedSuperAbstractsInTargetSchemas) {
			super.setMatchedSuperAbstractsInTargetSchemas(matchedSuperAbstractsInTargetSchemas);
		}

	}

}
