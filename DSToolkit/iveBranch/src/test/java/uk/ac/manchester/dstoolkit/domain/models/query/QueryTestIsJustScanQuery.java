/**
 * 
 */
package uk.ac.manchester.dstoolkit.domain.models.query;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperationType;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;

/**
 * @author chedeler
 *
 */
public class QueryTestIsJustScanQuery {

	Schema schema;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		schema = new Schema("schema1", ModelType.RELATIONAL);
	}

	/**
	 * Test method for {@link uk.ac.manchester.dstoolkit.domain.models.query.Query#isJustScanQuery()}.
	 */
	@Test
	public void testIsJustScanQueryForJustScanReturnsTrue() {
		SuperAbstract sa = new SuperAbstract("sa1", schema);
		ScanOperator scan = new ScanOperator(sa);
		Query query = new Query();
		query.setRootOperator(scan);
		assertTrue(query.isJustScanQuery());
	}

	@Test
	public void testIsJustScanQueryForTwoScansAndJoinReturnsFalse() {
		SuperAbstract sa1 = new SuperAbstract("sa1", schema);
		SuperAbstract sa2 = new SuperAbstract("sa2", schema);
		ScanOperator scan1 = new ScanOperator(sa1);
		ScanOperator scan2 = new ScanOperator(sa2);
		JoinOperator join = new JoinOperator(scan1, scan2);
		Query query = new Query();
		query.setRootOperator(join);
		assertFalse(query.isJustScanQuery());
	}

	@Test
	public void testIsJustScanQueryForTwoScansAndSetOperatorReturnsFalse() {
		SuperAbstract sa1 = new SuperAbstract("sa1", schema);
		SuperAbstract sa2 = new SuperAbstract("sa2", schema);
		ScanOperator scan1 = new ScanOperator(sa1);
		ScanOperator scan2 = new ScanOperator(sa2);
		SetOperator union = new SetOperator(scan1, scan2, SetOperationType.UNION);
		Query query = new Query();
		query.setRootOperator(union);
		assertFalse(query.isJustScanQuery());
	}

	//TODO add tests for reduce
}
