/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.MostSimilarSuperLexicalPerSuperAbstract;

/**
 * @author chedeler
 *
 */
public class EquivalentSuperLexicalsIdentifierServiceImplTestGenerateSetOfEquivalentSuperLexicals {

	private EquivalentSuperLexicalsIdentifierServiceImpl equivalentSuperLexicalsIdentifierServiceImpl;
	private Set<SuperLexical> equivalentSuperLexicals;
	private SuperLexical currentSuperLexical;
	private Map<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract> superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		equivalentSuperLexicalsIdentifierServiceImpl = new EquivalentSuperLexicalsIdentifierServiceImpl();
		equivalentSuperLexicals = new HashSet<SuperLexical>();
		superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical = new HashMap<SuperAbstract, MostSimilarSuperLexicalPerSuperAbstract>();

		currentSuperLexical = new SuperLexical("currentSl", null);
		SuperAbstract superAbstractOfCurrentSuperLexical = new SuperAbstract("saOfCurrentSl", null);
		currentSuperLexical.setParentSuperAbstract(superAbstractOfCurrentSuperLexical);

		SuperLexical sl1 = new SuperLexical("sl1", null);
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		sl1.setParentSuperAbstract(sa1);

		SuperLexical sl2 = new SuperLexical("sl2", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		sl2.setParentSuperAbstract(sa2);

		SuperLexical sl3 = new SuperLexical("sl3", null);
		SuperAbstract sa3 = new SuperAbstract("sa3", null);
		sl3.setParentSuperAbstract(sa3);

		MostSimilarSuperLexicalPerSuperAbstract mCurrentSlCurrentSa = new MostSimilarSuperLexicalPerSuperAbstract(superAbstractOfCurrentSuperLexical,
				currentSuperLexical, null);
		mCurrentSlCurrentSa.setD(1.0);
		MostSimilarSuperLexicalPerSuperAbstract mSl1Sa1 = new MostSimilarSuperLexicalPerSuperAbstract(sa1, sl1, null);
		mSl1Sa1.setD(0.7);
		MostSimilarSuperLexicalPerSuperAbstract mSl2Sa2 = new MostSimilarSuperLexicalPerSuperAbstract(sa2, sl2, null);
		mSl2Sa2.setD(1.8);
		MostSimilarSuperLexicalPerSuperAbstract mSl3Sa3 = new MostSimilarSuperLexicalPerSuperAbstract(sa3, sl3, null);
		mSl3Sa3.setD(0.9);

		superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical.put(superAbstractOfCurrentSuperLexical, mCurrentSlCurrentSa);
		superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical.put(sa1, mSl1Sa1);
		superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical.put(sa2, mSl2Sa2);
		superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical.put(sa3, mSl3Sa3);

		equivalentSuperLexicals.add(currentSuperLexical);
		equivalentSuperLexicals.add(sl1);
		equivalentSuperLexicals.add(sl2);
		equivalentSuperLexicals.add(sl3);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util.EquivalentSuperLexicalsIdentifierServiceImpl#generateSetOfEquivalentSuperLexicals(uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical, java.util.Map)}.
	 */
	@Test
	public void testGenerateListOfEquivalentSuperLexicals() {
		Set<SuperLexical> equivalentSuperLexicals = equivalentSuperLexicalsIdentifierServiceImpl.generateSetOfEquivalentSuperLexicals(
				currentSuperLexical, superLexicalPerSuperAbstractMostSimilarToCurrentSuperLexical);
		assertEquals(4, equivalentSuperLexicals.size());
		assertTrue(equivalentSuperLexicals.containsAll(equivalentSuperLexicals));
	}
}
