package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.entitylevelrelationship;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashSet;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;

public class ELRPhenotypeTestGetNumberOfMatchedButUnassociatedEntitiesWithEquivalentSuperLexicals {

	private ELRPhenotype elrPhenotype;
	private LinkedHashSet<SuperAbstract> unassociatedEntities;
	private LinkedHashSet<SuperLexical> equivalentSuperLexicals;
	private SuperLexical sl1, sl2, sl3;

	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicals = new LinkedHashSet<SuperLexical>();
		elrPhenotype = new ELRPhenotype(null);
		unassociatedEntities = new LinkedHashSet<SuperAbstract>();
		SuperAbstract unassociatedEntity1 = new SuperAbstract("sa1", null);
		sl1 = new SuperLexical("sl1", null);
		sl1.setParentSuperAbstract(unassociatedEntity1);
		sl2 = new SuperLexical("sl2", null);
		sl2.setParentSuperAbstract(unassociatedEntity1);
		SuperAbstract unassociatedEntity2 = new SuperAbstract("sa2", null);
		sl3 = new SuperLexical("sl3", null);
		sl3.setParentSuperAbstract(unassociatedEntity2);
		SuperLexical sl4 = new SuperLexical("sl4", null);
		sl4.setParentSuperAbstract(unassociatedEntity2);
		unassociatedEntities.add(unassociatedEntity1);
		unassociatedEntities.add(unassociatedEntity2);
	}

	@Test
	public void testGetNumberOfUnassociatedEntitiesWithEquivalentSuperLexicalsSourceEntitiesNoEquivalentSuperLexicals() {
		elrPhenotype.setMatchedButUnassociatedSourceEntities(unassociatedEntities);
		SuperLexical sl5 = new SuperLexical("sl5", null);
		SuperLexical sl6 = new SuperLexical("sl6", null);
		equivalentSuperLexicals.add(sl5);
		equivalentSuperLexicals.add(sl6);

		assertEquals(0, elrPhenotype.getNumberOfMatchedButUnassociatedEntitiesWithEquivalentSuperLexicals(equivalentSuperLexicals, true));
	}

	@Test
	public void testGetNumberOfUnassociatedEntitiesWithEquivalentSuperLexicalsTargetEntitiesMultipleEquivalentSuperLexicalsInSingleEntity() {
		elrPhenotype.setMatchedButUnassociatedTargetEntities(unassociatedEntities);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);

		assertEquals(1, elrPhenotype.getNumberOfMatchedButUnassociatedEntitiesWithEquivalentSuperLexicals(equivalentSuperLexicals, false));
	}

	@Test
	public void testGetNumberOfUnassociatedEntitiesWithEquivalentSuperLexicalsSourceEntitiesSingleEquivalentSuperLexicalsInMultipleEntities() {
		elrPhenotype.setMatchedButUnassociatedSourceEntities(unassociatedEntities);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl3);

		assertEquals(2, elrPhenotype.getNumberOfMatchedButUnassociatedEntitiesWithEquivalentSuperLexicals(equivalentSuperLexicals, true));
	}

}
