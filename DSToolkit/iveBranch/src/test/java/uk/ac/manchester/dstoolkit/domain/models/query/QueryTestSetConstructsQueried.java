/**
 * 
 */
package uk.ac.manchester.dstoolkit.domain.models.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperationType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;

/**
 * @author chedeler
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class QueryTestSetConstructsQueried {

	Schema schema;

	@Before
	public void setup() {
		schema = new Schema("schema1", ModelType.RELATIONAL);
	}

	@Test
	public void testSetConstructsQueriedMappingOperatorJustScanOperator() {
		SuperAbstract sa = new SuperAbstract("sa1", schema);
		ScanOperator scan = new ScanOperator(sa);
		Query query = new Query();
		Set<SuperAbstract> superAbstractsQueried = query.setSuperAbstractsQueried(scan);
		assertEquals(1, superAbstractsQueried.size());
		assertTrue(superAbstractsQueried.contains(sa));
	}

	@Test
	public void testSetConstructsQueriedMappingOperatorScanAndReduceOperator() {
		SuperAbstract sa = new SuperAbstract("sa1", schema);
		ScanOperator scan = new ScanOperator(sa);
		ReduceOperator reduce = new ReduceOperator();
		reduce.setLhsInput(scan);
		Query query = new Query();
		Set<SuperAbstract> superAbstractsQueried = query.setSuperAbstractsQueried(scan);
		assertEquals(1, superAbstractsQueried.size());
		assertTrue(superAbstractsQueried.contains(sa));
	}

	@Test
	public void testSetConstructsQueriedMappingOperatorTwoScansAndSelfJoinOperator() {
		SuperAbstract sa = new SuperAbstract("sa1", schema);
		ScanOperator scan1 = new ScanOperator(sa);
		ScanOperator scan2 = new ScanOperator(sa);
		JoinOperator join = new JoinOperator(scan1, scan2);
		Query query = new Query();
		Set<SuperAbstract> superAbstractsQueried = query.setSuperAbstractsQueried(join);
		assertEquals(1, superAbstractsQueried.size());
		assertTrue(superAbstractsQueried.contains(sa));
	}

	@Test
	public void testSetConstructsQueriedMappingOperatorTwoScansAndJoinOperator() {
		SuperAbstract sa1 = new SuperAbstract("sa1", schema);
		SuperAbstract sa2 = new SuperAbstract("sa2", schema);
		Set<SuperAbstract> sas = new HashSet<SuperAbstract>();
		sas.add(sa1);
		sas.add(sa2);
		ScanOperator scan1 = new ScanOperator(sa1);
		ScanOperator scan2 = new ScanOperator(sa2);
		JoinOperator join = new JoinOperator(scan1, scan2);
		Query query = new Query();
		Set<SuperAbstract> superAbstractsQueried = query.setSuperAbstractsQueried(join);
		assertEquals(2, superAbstractsQueried.size());
		assertTrue(superAbstractsQueried.containsAll(sas));
	}

	@Test
	public void testSetConstructsQueriedMappingOperatorTwoScansAndSetOperator() {
		SuperAbstract sa1 = new SuperAbstract("sa1", schema);
		SuperAbstract sa2 = new SuperAbstract("sa2", schema);
		Set<SuperAbstract> sas = new HashSet<SuperAbstract>();
		sas.add(sa1);
		sas.add(sa2);
		ScanOperator scan1 = new ScanOperator(sa1);
		ScanOperator scan2 = new ScanOperator(sa2);
		SetOperator union = new SetOperator(scan1, scan2, SetOperationType.UNION);
		Query query = new Query();
		Set<SuperAbstract> superAbstractsQueried = query.setSuperAbstractsQueried(union);
		assertEquals(2, superAbstractsQueried.size());
		assertTrue(superAbstractsQueried.containsAll(sas));
	}

	@Test
	public void testSetConstructsQueriedMappingOperatorThreeScansJoinAndSetOperator() {
		SuperAbstract sa1 = new SuperAbstract("sa1", schema);
		SuperAbstract sa2 = new SuperAbstract("sa2", schema);
		SuperAbstract sa3 = new SuperAbstract("sa3", schema);
		Set<SuperAbstract> sas = new HashSet<SuperAbstract>();
		sas.add(sa1);
		sas.add(sa2);
		sas.add(sa3);
		ScanOperator scan1 = new ScanOperator(sa1);
		ScanOperator scan2 = new ScanOperator(sa2);
		JoinOperator join = new JoinOperator(scan1, scan2);
		ScanOperator scan3 = new ScanOperator(sa3);
		SetOperator union = new SetOperator(join, scan3, SetOperationType.UNION);
		Query query = new Query();
		Set<SuperAbstract> superAbstractsQueried = query.setSuperAbstractsQueried(union);
		assertEquals(3, superAbstractsQueried.size());
		assertTrue(superAbstractsQueried.containsAll(sas));
	}
}
