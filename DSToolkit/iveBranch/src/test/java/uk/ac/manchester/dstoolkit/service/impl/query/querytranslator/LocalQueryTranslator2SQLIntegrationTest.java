/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.util.Assert.isInstanceOf;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.JoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ReduceOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SQLService;

/**
 * @author chedeler
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class LocalQueryTranslator2SQLIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(LocalQueryTranslator2SQLIntegrationTest.class);

	@Autowired
	@Qualifier("localQueryTranslator2SQLService")
	private LocalQueryTranslator2SQLService localQueryTranslator2SQL;

	@Autowired
	@Qualifier("logicalQueryOptimiserService")
	private LogicalQueryOptimiserService logicalQueryOptimiser;

	@Autowired
	@Qualifier("globalQueryTranslatorService")
	private GlobalQueryTranslatorService globalTranslator;

	@Autowired
	@Qualifier("sqlQueryParserService")
	private SQLQueryParserService parser;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("superAbstractRepository")
	private SuperAbstractRepository superAbstractRepository;

	@Test
	public void testTranslate2SQLSelectStarQueryOneSource() {
		String selectStarFromQuery = "Select * from citye";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye;",
				queryString);
		assertEquals("SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye;",
				evaluateExternallyOp.getQueryString());

	}

	@Test
	public void testTranslate2SQLSelectStarFromWhereQueryOneSource() {
		String selectStarFromWhereQuery = "Select * from citye where citye.countrye = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereQueryAst = parser.parseSQL(selectStarFromWhereQuery);
		logger.debug("selectStarFromWhereQueryAst: " + selectStarFromWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereQuery, selectStarFromWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye WHERE citye.countrye = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye WHERE citye.countrye = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromWhereAndQueryOneSource() {
		String selectStarFromWhereAndQuery = "Select * from citye where citye.countrye = 'GB' and citye.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereAndQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereAndQueryAst = parser.parseSQL(selectStarFromWhereAndQuery);
		logger.debug("selectStarFromWhereAndQueryAst: " + selectStarFromWhereAndQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereAndQuery, selectStarFromWhereAndQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye WHERE citye.countrye = 'GB' and citye.namee = 'Manchester';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye WHERE citye.countrye = 'GB' and citye.namee = 'Manchester';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromWhereOrQueryOneSource() {
		String selectStarFromWhereOrQuery = "Select * from citye where citye.countrye = 'GB' or citye.countrye = 'D'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereOrQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereOrQueryAst = parser.parseSQL(selectStarFromWhereOrQuery);
		logger.debug("selectStarFromWhereOrQueryAst: " + selectStarFromWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereOrQuery, selectStarFromWhereOrQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye WHERE citye.countrye = 'GB' or citye.countrye = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee FROM citye citye WHERE citye.countrye = 'GB' or citye.countrye = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromQueryOneSource() {
		String selectSuperLexicalsFromQuery = "Select namee, countrye, provincee, populatione from citye";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromQueryAst = parser.parseSQL(selectSuperLexicalsFromQuery);
		logger.debug("selectSuperLexicalsFromQueryAst: " + selectSuperLexicalsFromQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromQuery, selectSuperLexicalsFromQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye;", queryString);
		assertEquals("SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromWhereQueryOneSource() {
		String selectSuperLexicalsFromWhereQuery = "Select namee, countrye, provincee, populatione from citye where citye.countrye = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromWhereQueryAst = parser.parseSQL(selectSuperLexicalsFromWhereQuery);
		logger.debug("selectSuperLexicalsFromWhereQuery: " + selectSuperLexicalsFromWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromWhereQuery, selectSuperLexicalsFromWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye WHERE citye.countrye = 'GB';",
				queryString);
		assertEquals("SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye WHERE citye.countrye = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromWhereAndQueryOneSource() {
		String selectSuperLexicalsFromWhereAndQuery = "Select namee, countrye, provincee, populatione from citye where citye.countrye = 'GB' and citye.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereAndQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromWhereAndQueryAst = parser.parseSQL(selectSuperLexicalsFromWhereAndQuery);
		logger.debug("selectSuperLexicalsFromWhereAndQuery: " + selectSuperLexicalsFromWhereAndQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromWhereAndQuery, selectSuperLexicalsFromWhereAndQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye WHERE citye.countrye = 'GB' and citye.namee = 'Manchester';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye WHERE citye.countrye = 'GB' and citye.namee = 'Manchester';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromWhereOrQueryOneSource() {
		String selectSuperLexicalsFromWhereOrQuery = "Select namee, countrye, provincee, populatione from citye where citye.countrye = 'GB' or citye.countrye = 'D'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereOrQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromWhereOrQueryAst = parser.parseSQL(selectSuperLexicalsFromWhereOrQuery);
		logger.debug("selectSuperLexicalsFromWhereOrQuery: " + selectSuperLexicalsFromWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromWhereOrQuery, selectSuperLexicalsFromWhereOrQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye WHERE citye.countrye = 'GB' or citye.countrye = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione FROM citye citye WHERE citye.countrye = 'GB' or citye.countrye = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromQueryWithVariableNameOneSource() {
		String selectStarFromQueryWithVariableName = "Select * from citye c";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromQueryWithVariableNameAst = parser.parseSQL(selectStarFromQueryWithVariableName);
		logger.debug("selectStarFromQueryWithVariableNameAst: " + selectStarFromQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQueryWithVariableName, selectStarFromQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c;", queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromWhereQueryWithVariableNameOneSource() {
		String selectStarFromWhereQueryWithVariableName = "Select * from citye c where c.countrye = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereQueryWithVariableNameAst = parser.parseSQL(selectStarFromWhereQueryWithVariableName);
		logger.debug("selectStarFromWhereQueryWithVariableNameAst: " + selectStarFromWhereQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereQueryWithVariableName, selectStarFromWhereQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c WHERE c.countrye = 'GB';",
				queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c WHERE c.countrye = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromWhereAndQueryWithVariableNameOneSource() {
		String selectStarFromWhereAndQueryWithVariableName = "Select * from citye c where c.countrye = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereAndQueryWithVariableNameAst = parser.parseSQL(selectStarFromWhereAndQueryWithVariableName);
		logger.debug("selectStarFromWhereAndQueryWithVariableNameAst: " + selectStarFromWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereAndQueryWithVariableName,
				selectStarFromWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c WHERE c.countrye = 'GB' and c.namee = 'Manchester';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c WHERE c.countrye = 'GB' and c.namee = 'Manchester';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromWhereOrQueryWithVariableNameOneSource() {
		String selectStarFromWhereOrQueryWithVariableName = "Select * from citye c where c.countrye = 'GB' or c.countrye = 'D'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereOrQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereOrQueryWithVariableNameAst = parser.parseSQL(selectStarFromWhereOrQueryWithVariableName);
		logger.debug("selectStarFromWhereOrQueryWithVariableNameAst: " + selectStarFromWhereOrQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereOrQueryWithVariableName,
				selectStarFromWhereOrQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c WHERE c.countrye = 'GB' or c.countrye = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee FROM citye c WHERE c.countrye = 'GB' or c.countrye = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, c.populatione from citye c";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromQueryWithVariableNameAst: " + selectSuperLexicalsFromQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromQueryWithVariableName,
				selectSuperLexicalsFromQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c;", queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c;", evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromWhereQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromWhereQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, c.populatione from citye c where c.countrye = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromWhereQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromWhereQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromWhereQueryWithVariableNameAst: " + selectSuperLexicalsFromWhereQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromWhereQueryWithVariableName,
				selectSuperLexicalsFromWhereQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c WHERE c.countrye = 'GB';", queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c WHERE c.countrye = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromWhereAndQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromWhereAndQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, c.populatione from citye c where c.countrye = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromWhereAndQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromWhereAndQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromWhereAndQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromWhereAndQueryWithVariableName,
				selectSuperLexicalsFromWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c WHERE c.countrye = 'GB' and c.namee = 'Manchester';",
				queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c WHERE c.countrye = 'GB' and c.namee = 'Manchester';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromWhereOrQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromWhereOrQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, c.populatione from citye c where c.countrye = 'GB' or c.countrye = 'D'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereOrQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromWhereOrQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromWhereOrQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromWhereOrQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromWhereOrQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromWhereOrQueryWithVariableName,
				selectSuperLexicalsFromWhereOrQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c WHERE c.countrye = 'GB' or c.countrye = 'D';", queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, c.populatione FROM citye c WHERE c.countrye = 'GB' or c.countrye = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinQueryOneSource() {
		String selectStarFromJoinQuery = "Select * from citye, countrye where citye.countrye = countrye.codee";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinWhereQueryOneSource() {
		String selectStarFromJoinWhereQuery = "Select * from citye, countrye where citye.countrye = countrye.codee and countrye.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinWhereQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinWhereQueryAst = parser.parseSQL(selectStarFromJoinWhereQuery);
		logger.debug("selectStarFromJoinWhereQueryAst: " + selectStarFromJoinWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereQuery, selectStarFromJoinWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinWhereAndQueryOneSource() {
		String selectStarFromJoinWhereAndQuery = "Select * from citye, countrye where citye.countrye = countrye.codee and countrye.codee = 'GB' and citye.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinWhereAndQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinWhereAndQueryAst = parser.parseSQL(selectStarFromJoinWhereAndQuery);
		logger.debug("selectStarFromJoinWhereAndQueryAst: " + selectStarFromJoinWhereAndQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereAndQuery, selectStarFromJoinWhereAndQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectStarFromJoinWhereOrQueryOneSource() {
		String selectStarFromJoinWhereOrQuery = "Select * from citye, countrye where citye.countrye = countrye.codee and (countrye.codee = 'GB' or countrye.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinWhereOrQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinWhereOrQueryAst = parser.parseSQL(selectStarFromJoinWhereOrQuery);
		logger.debug("selectStarFromJoinWhereOrQueryAst: " + selectStarFromJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereOrQuery, selectStarFromJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinQueryOneSource() {
		String selectSuperLexicalsFromJoinQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale from citye, countrye where citye.countrye = countrye.codee";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinQuery);
		logger.debug("selectSuperLexicalsFromJoinQueryAst: " + selectSuperLexicalsFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinQuery, selectSuperLexicalsFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereQueryOneSource() {
		String selectSuperLexicalsFromJoinWhereQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale from citye, countrye where citye.countrye = countrye.codee and countrye.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinWhereQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinWhereQuery);
		logger.debug("selectSuperLexicalsFromJoinWhereQueryAst: " + selectSuperLexicalsFromJoinWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereQuery, selectSuperLexicalsFromJoinWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereAndQueryOneSource() {
		String selectSuperLexicalsFromJoinWhereAndQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale from citye, countrye where citye.countrye = countrye.codee and countrye.codee = 'GB' and citye.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereAndQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinWhereAndQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinWhereAndQuery);
		logger.debug("selectSuperLexicalsFromJoinWhereAndQueryAst: " + selectSuperLexicalsFromJoinWhereAndQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereAndQuery, selectSuperLexicalsFromJoinWhereAndQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereOrQueryOneSource() {
		String selectSuperLexicalsFromJoinWhereOrQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale from citye, countrye where citye.countrye = countrye.codee and (countrye.codee = 'GB' or countrye.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereOrQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinWhereOrQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinWhereOrQuery);
		logger.debug("selectSuperLexicalsFromJoinWhereOrQueryAst: " + selectSuperLexicalsFromJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereOrQuery, selectSuperLexicalsFromJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinQueryWithVariableNameOneSource() {
		String selectStarFromJoinQueryWithVariableName = "Select * from citye c, countrye o where c.countrye = o.codee";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinQueryWithVariableName);
		logger.debug("selectStarFromJoinQueryWithVariableNameAst: " + selectStarFromJoinQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQueryWithVariableName, selectStarFromJoinQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinWhereQueryWithVariableNameOneSource() {
		String selectStarFromJoinWhereQueryWithVariableName = "Select * from citye c, countrye o where c.countrye = o.codee and o.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinWhereQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinWhereQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinWhereQueryWithVariableName);
		logger.debug("selectStarFromJoinWhereQueryWithVariableNameAst: " + selectStarFromJoinWhereQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereQueryWithVariableName,
				selectStarFromJoinWhereQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinWhereAndQueryWithVariableNameOneSource() {
		String selectStarFromJoinWhereAndQueryWithVariableName = "Select * from citye c, countrye o where c.countrye = o.codee and o.codee = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinWhereAndQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinWhereAndQueryWithVariableName);
		logger.debug("selectStarFromJoinWhereAndQueryWithVariableNameAst: " + selectStarFromJoinWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereAndQueryWithVariableName,
				selectStarFromJoinWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectStarFromJoinWhereOrQueryWithVariableNameOneSource() {
		String selectStarFromJoinWhereOrQueryWithVariableName = "Select * from citye c, countrye o where c.countrye = o.codee and (o.codee = 'GB' or o.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinWhereOrQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinWhereOrQueryWithVariableName);
		logger.debug("selectStarFromJoinWhereOrQueryWithVariableNameAst: " + selectStarFromJoinWhereOrQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereOrQueryWithVariableName,
				selectStarFromJoinWhereOrQueryWithVariableNameAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale from citye c, countrye o where c.countrye = o.codee";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromJoinQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinQueryWithVariableNameAst: " + selectSuperLexicalsFromJoinQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinQueryWithVariableName,
				selectSuperLexicalsFromJoinQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM citye c, countrye o WHERE c.countrye = o.codee;", queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM citye c, countrye o WHERE c.countrye = o.codee;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinWhereQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale from citye c, countrye o where c.countrye = o.codee and o.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinWhereQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromJoinWhereQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinWhereQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinWhereQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereQueryWithVariableName,
				selectSuperLexicalsFromJoinWhereQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM citye c, countrye o WHERE c.countrye = o.codee and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM citye c, countrye o WHERE c.countrye = o.codee and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereAndQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinWhereAndQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale from citye c, countrye o where c.countrye = o.codee and o.codee = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinWhereAndQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinWhereAndQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinWhereAndQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereAndQueryWithVariableName,
				selectSuperLexicalsFromJoinWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM citye c, countrye o WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM citye c, countrye o WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereOrQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinWhereOrQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale from citye c, countrye o where c.countrye = o.codee and (o.codee = 'GB' or o.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinWhereOrQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinWhereOrQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinWhereOrQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinWhereOrQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereOrQueryWithVariableName,
				selectSuperLexicalsFromJoinWhereOrQueryWithVariableNameAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinJoinQueryOneSource() {
		String selectStarFromJoinJoinQuery = "Select * from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinQueryAst = parser.parseSQL(selectStarFromJoinJoinQuery);
		logger.debug("selectStarFromJoinJoinQueryAst: " + selectStarFromJoinJoinQueryAst);
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinQuery, selectStarFromJoinJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinJoinWhereQueryOneSource() {
		String selectStarFromJoinJoinWhereQuery = "Select * from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinWhereQueryAst;
		selectStarFromJoinJoinWhereQueryAst = parser.parseSQL(selectStarFromJoinJoinWhereQuery);
		logger.debug("selectStarFromJoinJoinWhereQueryAst: " + selectStarFromJoinJoinWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereQuery, selectStarFromJoinJoinWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinJoinWhereAndQueryOneSource() {
		String selectStarFromJoinJoinWhereAndQuery = "Select * from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' and citye.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereAndQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinWhereAndQueryAst = parser.parseSQL(selectStarFromJoinJoinWhereAndQuery);
		logger.debug("selectStarFromJoinJoinWhereAndQueryAst: " + selectStarFromJoinJoinWhereAndQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereAndQuery, selectStarFromJoinJoinWhereAndQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, countrye countrye, borderse borderse WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, countrye countrye, borderse borderse WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectStarFromJoinJoinWhereOrQueryOneSource() {
		String selectStarFromJoinJoinWhereOrQuery = "Select * from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e and (countrye.codee = 'GB' or countrye.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereOrQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinWhereOrQueryAst = parser.parseSQL(selectStarFromJoinJoinWhereOrQuery);
		logger.debug("selectStarFromJoinJoinWhereOrQueryAst: " + selectStarFromJoinJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereOrQuery, selectStarFromJoinJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinJoinQuery);
		logger.debug("selectSuperLexicalsFromJoinJoinQueryAst: " + selectSuperLexicalsFromJoinJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinQuery, selectSuperLexicalsFromJoinJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinWhereQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinJoinWhereQuery);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereQueryAst: " + selectSuperLexicalsFromJoinJoinWhereQueryAst.toStringTree());
		query = globalTranslator
				.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereQuery, selectSuperLexicalsFromJoinJoinWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, countrye countrye, borderse borderse WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinWhereAndQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereAndQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' and citye.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereAndQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereAndQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinJoinWhereAndQuery);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereAndQueryAst: " + selectSuperLexicalsFromJoinJoinWhereAndQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereAndQuery,
				selectSuperLexicalsFromJoinJoinWhereAndQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, countrye countrye, borderse borderse WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, countrye countrye, borderse borderse WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinWhereOrQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereOrQuery = "Select citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e from citye, countrye, borderse where citye.countrye = countrye.codee and countrye.codee = borderse.country1e and (countrye.codee = 'GB' or countrye.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereOrQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereOrQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinJoinWhereOrQuery);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereOrQueryAst: " + selectSuperLexicalsFromJoinJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereOrQuery,
				selectSuperLexicalsFromJoinJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinJoinQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinQueryWithVariableName = "Select * from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinJoinQueryWithVariableName);
		logger.debug("selectStarFromJoinJoinQueryWithVariableNameAst: " + selectStarFromJoinJoinQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinQueryWithVariableName,
				selectStarFromJoinJoinQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinJoinWhereQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinWhereQueryWithVariableName = "Select * from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinWhereQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinJoinWhereQueryWithVariableName);
		logger.debug("selectStarFromJoinJoinWhereQueryWithVariableNameAst: " + selectStarFromJoinJoinWhereQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereQueryWithVariableName,
				selectStarFromJoinJoinWhereQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinJoinWhereAndQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinWhereAndQueryWithVariableName = "Select * from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinWhereAndQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinJoinWhereAndQueryWithVariableName);
		logger.debug("selectStarFromJoinJoinWhereAndQueryWithVariableNameAst: "
				+ selectStarFromJoinJoinWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereAndQueryWithVariableName,
				selectStarFromJoinJoinWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM borderse b, citye c, countrye o WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM borderse b, citye c, countrye o WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectStarFromJoinJoinWhereOrQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinWhereOrQueryWithVariableName = "Select * from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e and (o.codee = 'GB' or o.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromJoinJoinWhereOrQueryWithVariableNameAst = parser.parseSQL(selectStarFromJoinJoinWhereOrQueryWithVariableName);
		logger.debug("selectStarFromJoinJoinWhereOrQueryWithVariableNameAst: " + selectStarFromJoinJoinWhereOrQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereOrQueryWithVariableName,
				selectStarFromJoinJoinWhereOrQueryWithVariableNameAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinQueryWithVariableNameAst = parser.parseSQL(selectSuperLexicalsFromJoinJoinQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinJoinQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinJoinQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinQueryWithVariableName,
				selectSuperLexicalsFromJoinJoinQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinWhereQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinJoinWhereQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinJoinWhereQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereQueryWithVariableName,
				selectSuperLexicalsFromJoinJoinWhereQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM borderse b, citye c, countrye o WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName,
				selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM borderse b, citye c, countrye o WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM borderse b, citye c, countrye o WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e from citye c, countrye o, borderse b where c.countrye = o.codee and o.codee = b.country1e and (o.codee = 'GB' or o.codee = 'D')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableName,
				selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableNameAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinQueryTwoSources() {
		String selectStarFromJoinQuery = "Select * from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeWRSchema = schemaRepository
				.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addSchema(mondialLanguageEconomyReligionOfCountriesEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNull(input0Operator.getDataSource());

		EvaluatorOperator input2Operator = input0Operator.getLhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		String queryString2 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp2, null);

		assertEquals(
				"SELECT countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM countrye countrye;",
				queryString2);
		assertEquals(
				"SELECT countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM countrye countrye;",
				evaluateExternallyOp2.getQueryString());

		EvaluatorOperator input1Operator = input0Operator.getRhsInput();

		assertNotNull(input1Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input1Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp1 = (EvaluateExternallyOperatorImpl) input1Operator;

		String queryString1 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp1, null);

		assertEquals("SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe;", queryString1);
		assertEquals("SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe;",
				evaluateExternallyOp1.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectStarFromJoinWhereAndQueryTwoSources() {
		String selectStarFromJoinQuery = "Select * from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye and countrye.codee = 'GB' and ethnicgroupe.namee = 'English'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeWRSchema = schemaRepository
				.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addSchema(mondialLanguageEconomyReligionOfCountriesEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNull(input0Operator.getDataSource());

		EvaluatorOperator input2Operator = input0Operator.getRhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		String queryString2 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp2, null);

		assertEquals(
				"SELECT countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM countrye countrye WHERE countrye.codee = 'GB';",
				queryString2);
		assertEquals(
				"SELECT countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM countrye countrye WHERE countrye.codee = 'GB';",
				evaluateExternallyOp2.getQueryString());

		EvaluatorOperator input1Operator = input0Operator.getLhsInput();

		assertNotNull(input1Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input1Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp1 = (EvaluateExternallyOperatorImpl) input1Operator;

		String queryString1 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp1, null);

		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				queryString1);
		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				evaluateExternallyOp1.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinWhereAndQueryTwoSources() {
		String selectStarFromJoinQuery = "Select countrye.namee, countrye.codee, countrye.capitale, ethnicgroupe.namee, ethnicgroupe.percentagee from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye and countrye.codee = 'GB' and ethnicgroupe.namee = 'English'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeWRSchema = schemaRepository
				.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addSchema(mondialLanguageEconomyReligionOfCountriesEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNull(input0Operator.getDataSource());

		EvaluatorOperator input2Operator = input0Operator.getRhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		String queryString2 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp2, null);

		assertEquals(
				"SELECT countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM countrye countrye WHERE countrye.codee = 'GB';",
				queryString2);
		assertEquals(
				"SELECT countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM countrye countrye WHERE countrye.codee = 'GB';",
				evaluateExternallyOp2.getQueryString());

		EvaluatorOperator input1Operator = input0Operator.getLhsInput();

		assertNotNull(input1Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input1Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp1 = (EvaluateExternallyOperatorImpl) input1Operator;

		String queryString1 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp1, null);

		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				queryString1);
		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				evaluateExternallyOp1.getQueryString());
	}

	@Test
	public void testTranslate2SQLSelectSuperLexicalsFromJoinJoinJoinWhereAndQueryWithVariableNameTwoSources() {
		String selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale, o.populatione, b.country1e, b.country2e, g.namee, g.percentagee from citye c, countrye o, borderse b, ethnicgroupe g where c.countrye = o.codee and o.codee = b.country1e and o.codee = g.countrye and o.codee = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeWRSchema = schemaRepository
				.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addSchema(mondialLanguageEconomyReligionOfCountriesEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName,
				selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst);

		SuperAbstract citySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("citye", "MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract countrySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("countrye",
				"MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract bordersSa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("borderse",
				"MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract ethnicGroupSa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("ethnicgroupe",
				"MondialLanguageEconomyReligionOfCountriesEuropeWR");

		citySa.setCardinality(5000);
		countrySa.setCardinality(500);
		bordersSa.setCardinality(2000);
		ethnicGroupSa.setCardinality(20);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNotNull(rootOperator.getVariableName());
		assertEquals("o", rootOperator.getVariableName()); //TODO shoudn't be "o", should be null!!!
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(10, ((ReduceOperatorImpl) rootOperator).getSuperLexicals().size());
		assertEquals("c.namee, c.countrye, c.provincee, o.namee, o.capitale, o.populatione, b.country1e, b.country2e, g.namee, g.percentagee",
				((ReduceOperatorImpl) rootOperator).getReconcilingExpression());
		assertNull(rootOperator.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[10];
		String[] superAbstractNamesArray = new String[10];
		int i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "namee", "capitale", "populatione", "country1e", "country2e",
				"namee", "percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "countrye", "countrye", "countrye", "borderse", "borderse",
				"ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[10];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "c.namee", "c.countrye", "c.provincee", "o.namee", "o.capitale", "o.populatione",
				"b.country1e", "b.country2e", "g.namee", "g.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperator.getResultType());
		ResultType resultType1 = rootOperator.getResultType();
		assertEquals(10, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "c.namee", "c.countrye", "c.provincee", "o.namee", "o.capitale", "o.populatione", "b.country1e",
				"b.country2e", "g.namee", "g.percentagee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING,
				DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "namee", "capitale", "populatione", "country1e",
				"country2e", "namee", "percentagee" };
		String[] expectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "countrye", "countrye", "countrye", "borderse",
				"borderse", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray1 = new String[10];
		DataType[] actualResultFieldTypesArray1 = new DataType[10];
		String[] actualCanonicalModelConstructNamesArray1 = new String[10];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[10];

		int j = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[j] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[j] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[j] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[j] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			j++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(expectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator joinBordersOperator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, joinBordersOperator);
		assertNotNull(joinBordersOperator.getVariableName());
		assertEquals("o", joinBordersOperator.getVariableName()); //TODO shouldn't be "o", should be null!!!
		assertNotNull(((JoinOperatorImpl) joinBordersOperator).getReconcilingExpression());
		assertEquals("o.codee = b.country1e", ((JoinOperatorImpl) joinBordersOperator).getReconcilingExpression()); //TODO decide whether it should be the variable name here instead
		assertNotNull(joinBordersOperator.getLhsInput());
		assertNotNull(joinBordersOperator.getRhsInput());
		assertEquals(joinBordersOperator.getInput(), joinBordersOperator.getLhsInput());

		assertNull(joinBordersOperator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) joinBordersOperator).getPredicates().size());
		Predicate predicate0 = ((JoinOperatorImpl) joinBordersOperator).getPredicates().iterator().next();
		assertEquals("codee", predicate0.getSuperLexical1().getName());
		assertEquals("countrye", predicate0.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate0.getOperator());
		assertEquals("country1e", predicate0.getSuperLexical2().getName());
		assertEquals("borderse", predicate0.getSuperLexical2().getParentSuperAbstract().getName());
		assertEquals("and", predicate0.getAndOr());
		assertNull(predicate0.getLiteral1());
		assertNull(predicate0.getLiteral2());

		assertNotNull(joinBordersOperator.getResultType());
		ResultType resultType2 = joinBordersOperator.getResultType();
		assertEquals(18, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione", "g.countrye", "g.namee", "g.percentagee",
				"b.country1e", "b.country2e", "b.lengthe" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER,
				DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee",
				"codee", "capitale", "provincee", "areae", "populatione", "countrye", "namee", "percentagee", "country1e", "country2e", "lengthe" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye", "ethnicgroupe", "ethnicgroupe", "ethnicgroupe", "borderse", "borderse", "borderse" };

		String[] actualResultFieldNamesArray2 = new String[18];
		DataType[] actualResultFieldTypesArray2 = new DataType[18];
		String[] actualCanonicalModelConstructNamesArray2 = new String[18];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[18];

		i = 0;
		Map<String, ResultField> resultFields2 = resultType2.getResultFields();
		Set<String> resultFieldNames2 = resultFields2.keySet();
		for (String resultFieldName : resultFieldNames2) {
			actualResultFieldNamesArray2[i] = resultFields2.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray2[i] = resultFields2.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray2[i] = resultFields2.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields2.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray2[i] = ((SuperLexical) resultFields2.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray2, actualResultFieldNamesArray2);
		assertArrayEquals(expectedResultFieldTypesArray2, actualResultFieldTypesArray2);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray2, actualCanonicalModelConstructNamesArray2);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray2, actualParentCanonicalModelConstructNamesArray2);

		EvaluatorOperator joinEthnicGroupOperator = joinBordersOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, joinEthnicGroupOperator);
		assertNull(joinEthnicGroupOperator.getVariableName()); //TODO should be "o"
		//assertEquals("o", joinEthnicGroupOperator.getVariableName()); //TODO should be "o"
		assertNotNull(((JoinOperatorImpl) joinEthnicGroupOperator).getReconcilingExpression());
		assertEquals("o.codee = g.countrye", ((JoinOperatorImpl) joinEthnicGroupOperator).getReconcilingExpression()); //TODO decide whether it should be the variable name here instead
		assertNotNull(joinEthnicGroupOperator.getLhsInput());
		assertNotNull(joinEthnicGroupOperator.getRhsInput());
		assertEquals(joinEthnicGroupOperator.getInput(), joinEthnicGroupOperator.getLhsInput());

		assertNull(joinEthnicGroupOperator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) joinEthnicGroupOperator).getPredicates().size());
		Predicate predicateJEg = ((JoinOperatorImpl) joinEthnicGroupOperator).getPredicates().iterator().next();
		assertEquals("codee", predicateJEg.getSuperLexical1().getName());
		assertEquals("countrye", predicateJEg.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicateJEg.getOperator());
		assertEquals("countrye", predicateJEg.getSuperLexical2().getName());
		assertEquals("ethnicgroupe", predicateJEg.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicateJEg.getLiteral1());
		assertNull(predicateJEg.getLiteral2());
		assertEquals("and", predicateJEg.getAndOr());

		assertNotNull(joinEthnicGroupOperator.getResultType());
		ResultType resultTypeJEg = joinEthnicGroupOperator.getResultType();
		assertEquals(15, resultTypeJEg.getResultFields().size());
		String[] expectedResultFieldNamesArrayJEg = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione", "g.countrye", "g.namee", "g.percentagee" };
		DataType[] expectedResultFieldTypesArrayJEg = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER,
				DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArrayJEg = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee",
				"namee", "codee", "capitale", "provincee", "areae", "populatione", "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArrayJEg = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye",
				"countrye", "countrye", "countrye", "countrye", "countrye", "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArrayJEg = new String[15];
		DataType[] actualResultFieldTypesArrayJEg = new DataType[15];
		String[] actualCanonicalModelConstructNamesArrayJEg = new String[15];
		String[] actualParentCanonicalModelConstructNamesArrayJEg = new String[15];

		i = 0;
		Map<String, ResultField> resultFieldsJEg = resultTypeJEg.getResultFields();
		Set<String> resultFieldNamesJEg = resultFieldsJEg.keySet();
		for (String resultFieldName : resultFieldNamesJEg) {
			actualResultFieldNamesArrayJEg[i] = resultFieldsJEg.get(resultFieldName).getFieldName();
			actualResultFieldTypesArrayJEg[i] = resultFieldsJEg.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArrayJEg[i] = resultFieldsJEg.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsJEg.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArrayJEg[i] = ((SuperLexical) resultFieldsJEg.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArrayJEg, actualResultFieldNamesArrayJEg);
		assertArrayEquals(expectedResultFieldTypesArrayJEg, actualResultFieldTypesArrayJEg);
		assertArrayEquals(expectedCanonicalModelConstructNamesArrayJEg, actualCanonicalModelConstructNamesArrayJEg);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArrayJEg, actualParentCanonicalModelConstructNamesArrayJEg);

		EvaluatorOperator scanBordersExternallyOperator = joinBordersOperator.getRhsInput();

		assertNotNull(scanBordersExternallyOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, scanBordersExternallyOperator);
		assertNull(scanBordersExternallyOperator.getLhsInput());
		assertNull(scanBordersExternallyOperator.getRhsInput());
		assertNotNull(scanBordersExternallyOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, scanBordersExternallyOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateScanBordersExternallyOp = (EvaluateExternallyOperatorImpl) scanBordersExternallyOperator;

		assertNotNull(evaluateScanBordersExternallyOp.getVariableName());
		assertEquals("b", evaluateScanBordersExternallyOp.getVariableName());
		assertNull(evaluateScanBordersExternallyOp.getLhsInput());
		assertNull(evaluateScanBordersExternallyOp.getRhsInput());

		String queryString1 = localQueryTranslator2SQL.translate2SQL(evaluateScanBordersExternallyOp, null);

		assertEquals("SELECT b.country1e, b.country2e, b.lengthe FROM borderse b;", queryString1);
		assertEquals("SELECT b.country1e, b.country2e, b.lengthe FROM borderse b;", evaluateScanBordersExternallyOp.getQueryString());

		EvaluatorOperator joinCityCountryExternallyOperator = joinEthnicGroupOperator.getLhsInput();

		assertNotNull(joinCityCountryExternallyOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, joinCityCountryExternallyOperator);
		assertNull(joinCityCountryExternallyOperator.getLhsInput());
		assertNull(joinCityCountryExternallyOperator.getRhsInput());
		assertNotNull(joinCityCountryExternallyOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, joinCityCountryExternallyOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateJoinCityCountryExternallyOp = (EvaluateExternallyOperatorImpl) joinCityCountryExternallyOperator;

		assertNotNull(evaluateJoinCityCountryExternallyOp.getVariableName());
		assertEquals("o", evaluateJoinCityCountryExternallyOp.getVariableName());
		assertNull(evaluateJoinCityCountryExternallyOp.getLhsInput());
		assertNull(evaluateJoinCityCountryExternallyOp.getRhsInput());

		String queryString2 = localQueryTranslator2SQL.translate2SQL(evaluateJoinCityCountryExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, "
						+ "o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString2);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, "
						+ "o.populatione FROM citye c, countrye o WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateJoinCityCountryExternallyOp.getQueryString());

		EvaluatorOperator input2Operator = joinEthnicGroupOperator.getRhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		assertNotNull(evaluateExternallyOp2.getVariableName());
		assertEquals("g", evaluateExternallyOp2.getVariableName());
		assertNull(evaluateExternallyOp2.getLhsInput());
		assertNull(evaluateExternallyOp2.getRhsInput());

		String queryString3 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp2, null);

		assertEquals("SELECT g.countrye, g.namee, g.percentagee FROM ethnicgroupe g;", queryString3);
		assertEquals("SELECT g.countrye, g.namee, g.percentagee FROM ethnicgroupe g;", evaluateExternallyOp2.getQueryString());
	}
}
