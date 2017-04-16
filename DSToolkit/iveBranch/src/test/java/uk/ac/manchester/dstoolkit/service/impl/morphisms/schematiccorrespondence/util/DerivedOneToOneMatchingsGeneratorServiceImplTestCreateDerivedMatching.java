/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation.DerivedOneToOneMatching;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatchingsGeneratorServiceImplTestCreateDerivedMatching {

	private DerivedOneToOneMatchingsGeneratorServiceImpl derivedOneToOneMatchingsGeneratorServiceImpl;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		derivedOneToOneMatchingsGeneratorServiceImpl = new DerivedOneToOneMatchingsGeneratorServiceImpl();
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorServiceImpl#createDerivedMatching(uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, java.util.Set)}.
	 */
	@Test
	public void testCreateDerivedMatchingSingleMatchingBetweenSourceAndTargetConstruct() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		Matching oneToOneMatching = new OneToOneMatching(sa1, sa2, 0.5d, "NGram");
		Set<Matching> matchings = new HashSet<Matching>();
		matchings.add(oneToOneMatching);
		DerivedOneToOneMatching derivedMatching = derivedOneToOneMatchingsGeneratorServiceImpl.createDerivedMatching(sa1, sa2, matchings);
		assertEquals(sa1, derivedMatching.getConstruct1());
		assertEquals(sa2, derivedMatching.getConstruct2());
		assertEquals(1, derivedMatching.getNumberOfMatchings());
		assertTrue(derivedMatching.getSumOfMatchingScores() == 0.5d);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorServiceImpl#createDerivedMatching(uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, java.util.Set)}.
	 */
	@Test
	public void testCreateDerivedMatchingSingleMatchingBetweenTargetAndSourceConstruct() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		Matching oneToOneMatching = new OneToOneMatching(sa2, sa1, 0.5d, "NGram");
		Set<Matching> matchings = new HashSet<Matching>();
		matchings.add(oneToOneMatching);
		DerivedOneToOneMatching derivedMatching = derivedOneToOneMatchingsGeneratorServiceImpl.createDerivedMatching(sa1, sa2, matchings);
		assertEquals(sa1, derivedMatching.getConstruct1());
		assertEquals(sa2, derivedMatching.getConstruct2());
		assertEquals(1, derivedMatching.getNumberOfMatchings());
		assertTrue(derivedMatching.getSumOfMatchingScores() == 0.5d);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.ec.DerivedOneToOneMatchingsGeneratorServiceImpl#createDerivedMatching(uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct, java.util.Set)}.
	 */
	@Test
	public void testCreateDerivedMatchingTwoMatchingsBetweenSourceAndTargetConstruct() {
		SuperAbstract sa1 = new SuperAbstract("sa1", null);
		SuperAbstract sa2 = new SuperAbstract("sa2", null);
		Matching oneToOneMatching1 = new OneToOneMatching(sa1, sa2, 0.5d, "NGram");
		Matching oneToOneMatching2 = new OneToOneMatching(sa1, sa2, 0.94d, "Instance");
		Set<Matching> matchings = new HashSet<Matching>();
		matchings.add(oneToOneMatching1);
		matchings.add(oneToOneMatching2);
		DerivedOneToOneMatching derivedMatching = derivedOneToOneMatchingsGeneratorServiceImpl.createDerivedMatching(sa1, sa2, matchings);
		assertEquals(sa1, derivedMatching.getConstruct1());
		assertEquals(sa2, derivedMatching.getConstruct2());
		assertEquals(2, derivedMatching.getNumberOfMatchings());
		assertTrue(derivedMatching.getSumOfMatchingScores() == 1.44d);
	}

}
