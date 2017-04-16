/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.query.queryexpander;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.util.Assert.isInstanceOf;

import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.HashJoinOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ReduceOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.QueryEvaluationEngineService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.PhysicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SQLService;

/**
 * @author chedeler
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class QueryExpanderIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(QueryExpanderIntegrationTest.class);

	@Autowired
	@Qualifier("queryEvaluationEngineService")
	private QueryEvaluationEngineService queryEvaluationEngine;

	@Autowired
	@Qualifier("localQueryTranslator2SQLService")
	private LocalQueryTranslator2SQLService localQueryTranslator2SQL;

	@Autowired
	@Qualifier("physicalQueryOptimiserService")
	private PhysicalQueryOptimiserService physicalOptimiser;

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

	//TODO test expander properly

	@Test
	public void testExpandSelectStarQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("194345", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("10.1", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("56.1", resultInstances.get(0).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("13.3", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(1).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(2).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("1358540", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("12.55", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("55.6833", resultInstances.get(3).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("502337", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("-6.35", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("53.3667", resultInstances.get(4).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(5).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(7).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(7).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(7).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(8).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(9).getResultValue("citye.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(9).getResultValue("citye.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(9).getResultValue("citye.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromWhereQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(0).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(2).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());

	}

	@Test
	public void testEvaluateSelectStarFromWhereAndQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromWhereOrQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("13.3", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(0).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(1).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(2).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(4).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(6).getResultValue("citye.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("194345", resultInstances.get(0).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(1).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(2).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("1358540", resultInstances.get(3).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("502337", resultInstances.get(4).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(5).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(6).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(7).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(8).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(9).getResultValue("citye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromWhereQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("citye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromWhereAndQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("citye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromWhereOrQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("citye.populatione").getValue());

		assertEquals(4, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("citye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("194345", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("10.1", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("56.1", resultInstances.get(0).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("3472009", resultInstances.get(1).getResultValue("c.populatione").getValue());
		assertEquals("13.3", resultInstances.get(1).getResultValue("c.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(1).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(2).getResultValue("c.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(2).getResultValue("c.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(2).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("1358540", resultInstances.get(3).getResultValue("c.populatione").getValue());
		assertEquals("12.55", resultInstances.get(3).getResultValue("c.longitudee").getValue());
		assertEquals("55.6833", resultInstances.get(3).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("502337", resultInstances.get(4).getResultValue("c.populatione").getValue());
		assertEquals("-6.35", resultInstances.get(4).getResultValue("c.longitudee").getValue());
		assertEquals("53.3667", resultInstances.get(4).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("1705872", resultInstances.get(5).getResultValue("c.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(5).getResultValue("c.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(5).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(6).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(7).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(7).getResultValue("c.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(7).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(8).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("c.provincee").getValue());
		assertEquals("1290079", resultInstances.get(9).getResultValue("c.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(9).getResultValue("c.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(9).getResultValue("c.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromWhereQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(0).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(2).getResultValue("c.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(2).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromWhereAndQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("c.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromWhereOrQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("13.3", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(0).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("c.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(1).getResultValue("c.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(1).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("c.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(2).getResultValue("c.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(2).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(4).getResultValue("c.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(4).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("c.latitudee").getValue());

		assertEquals(6, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("c.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(6).getResultValue("c.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(6).getResultValue("c.latitudee").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("194345", resultInstances.get(0).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("3472009", resultInstances.get(1).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(2).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("1358540", resultInstances.get(3).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("502337", resultInstances.get(4).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("1705872", resultInstances.get(5).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(6).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(7).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(8).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("c.provincee").getValue());
		assertEquals("1290079", resultInstances.get(9).getResultValue("c.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromWhereQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("c.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromWhereAndQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("c.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromWhereOrQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(4, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("c.populatione").getValue());

		assertEquals(4, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("c.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("194345", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("10.1", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("56.1", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("Copenhagen", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("43070.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("5249632", resultInstances.get(0).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("13.3", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(1).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(1).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(2).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(2).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("1358540", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("12.55", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("55.6833", resultInstances.get(3).getResultValue("citye.latitudee").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("43070.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("5249632", resultInstances.get(3).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("502337", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("-6.35", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("53.3667", resultInstances.get(4).getResultValue("citye.latitudee").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("countrye.codee").getValue());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("countrye.capitale").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("countrye.provincee").getValue());
		assertEquals("70280.0", resultInstances.get(4).getResultValue("countrye.areae").getValue());
		assertEquals("3566833", resultInstances.get(4).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(5).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(5).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(5).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(6).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(6).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(6).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(6).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(7).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(7).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(7).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(7).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(7).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(7).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(7).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(8).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(8).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(8).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(8).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(8).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(8).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(9).getResultValue("citye.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(9).getResultValue("citye.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(9).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(9).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(9).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(9).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(9).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(9).getResultValue("countrye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinWhereQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(2).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(2).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("countrye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinWhereAndQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
	}

	//@Test
	public void testEvaluateSelectStarFromJoinWhereOrQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("13.3", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(0).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(1).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(2).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(2).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(4).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(4).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(4).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(5).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(5).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(5).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(5).getResultValue("countrye.populatione").getValue());

		assertEquals(12, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(6).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(6).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(6).getResultValue("countrye.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("Copenhagen", resultInstances.get(0).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("Germany", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("Germany", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(6).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(7).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(7).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(8).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(8).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("citye.provincee").getValue());
		assertEquals("Germany", resultInstances.get(9).getResultValue("countrye.namee").getValue());
		assertEquals("Berlin", resultInstances.get(9).getResultValue("countrye.capitale").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinWhereQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinWhereAndQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("Germany", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("Germany", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("London", resultInstances.get(5).getResultValue("countrye.capitale").getValue());

		assertEquals(5, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("Germany", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.capitale").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("194345", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("10.1", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("56.1", resultInstances.get(0).getResultValue("c.latitudee").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("o.codee").getValue());
		assertEquals("Copenhagen", resultInstances.get(0).getResultValue("o.capitale").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("o.provincee").getValue());
		assertEquals("43070.0", resultInstances.get(0).getResultValue("o.areae").getValue());
		assertEquals("5249632", resultInstances.get(0).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("3472009", resultInstances.get(1).getResultValue("c.populatione").getValue());
		assertEquals("13.3", resultInstances.get(1).getResultValue("c.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(1).getResultValue("c.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("o.codee").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("o.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("o.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(1).getResultValue("o.areae").getValue());
		assertEquals("83536115", resultInstances.get(1).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(2).getResultValue("c.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(2).getResultValue("c.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(2).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(2).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(2).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("1358540", resultInstances.get(3).getResultValue("c.populatione").getValue());
		assertEquals("12.55", resultInstances.get(3).getResultValue("c.longitudee").getValue());
		assertEquals("55.6833", resultInstances.get(3).getResultValue("c.latitudee").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("o.codee").getValue());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("o.capitale").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("o.provincee").getValue());
		assertEquals("43070.0", resultInstances.get(3).getResultValue("o.areae").getValue());
		assertEquals("5249632", resultInstances.get(3).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("502337", resultInstances.get(4).getResultValue("c.populatione").getValue());
		assertEquals("-6.35", resultInstances.get(4).getResultValue("c.longitudee").getValue());
		assertEquals("53.3667", resultInstances.get(4).getResultValue("c.latitudee").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("o.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("o.codee").getValue());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("o.capitale").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("o.provincee").getValue());
		assertEquals("70280.0", resultInstances.get(4).getResultValue("o.areae").getValue());
		assertEquals("3566833", resultInstances.get(4).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("1705872", resultInstances.get(5).getResultValue("c.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(5).getResultValue("c.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(5).getResultValue("c.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(5).getResultValue("o.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("o.codee").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("o.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("o.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(5).getResultValue("o.areae").getValue());
		assertEquals("83536115", resultInstances.get(5).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(6).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(6).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(6).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(6).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(6).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(6).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(6).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(7).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(7).getResultValue("c.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(7).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(7).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(7).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(7).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(7).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(8).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(8).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(8).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(8).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(8).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(8).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("c.provincee").getValue());
		assertEquals("1290079", resultInstances.get(9).getResultValue("c.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(9).getResultValue("c.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(9).getResultValue("c.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(9).getResultValue("o.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("o.codee").getValue());
		assertEquals("Berlin", resultInstances.get(9).getResultValue("o.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(9).getResultValue("o.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(9).getResultValue("o.areae").getValue());
		assertEquals("83536115", resultInstances.get(9).getResultValue("o.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinWhereQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(0).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(2).getResultValue("c.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(2).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(2).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(2).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("o.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinWhereAndQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("o.populatione").getValue());
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(12, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("c.populatione").getValue());
		assertEquals("13.3", resultInstances.get(0).getResultValue("c.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(0).getResultValue("c.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("o.codee").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("o.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("o.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(0).getResultValue("o.areae").getValue());
		assertEquals("83536115", resultInstances.get(0).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("c.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(1).getResultValue("c.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(1).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("c.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(2).getResultValue("c.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(2).getResultValue("c.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("o.codee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("o.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("o.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(2).getResultValue("o.areae").getValue());
		assertEquals("83536115", resultInstances.get(2).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(4).getResultValue("c.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(4).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(4).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(4).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("c.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("c.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("c.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(5).getResultValue("o.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("o.codee").getValue());
		assertEquals("London", resultInstances.get(5).getResultValue("o.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(5).getResultValue("o.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(5).getResultValue("o.areae").getValue());
		assertEquals("58489975", resultInstances.get(5).getResultValue("o.populatione").getValue());

		assertEquals(12, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("c.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(6).getResultValue("c.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(6).getResultValue("c.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(6).getResultValue("o.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("o.codee").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("o.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("o.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(6).getResultValue("o.areae").getValue());
		assertEquals("83536115", resultInstances.get(6).getResultValue("o.populatione").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("Denmark", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("Copenhagen", resultInstances.get(0).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("Germany", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("DK", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("Denmark", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("Copenhagen", resultInstances.get(3).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("Ireland", resultInstances.get(4).getResultValue("o.namee").getValue());
		assertEquals("Dublin", resultInstances.get(4).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("Germany", resultInstances.get(5).getResultValue("o.namee").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(6).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(6).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(7).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(7).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(7).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(7).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(7).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(8).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(8).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(8).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(8).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(9).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(9).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(9).getResultValue("c.provincee").getValue());
		assertEquals("Germany", resultInstances.get(9).getResultValue("o.namee").getValue());
		assertEquals("Berlin", resultInstances.get(9).getResultValue("o.capitale").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinWhereQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("o.capitale").getValue());
	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinWhereAndQueryWithVariableNameOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("o.capitale").getValue());
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY c.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("Germany", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("Germany", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(5).getResultValue("o.namee").getValue());
		assertEquals("London", resultInstances.get(5).getResultValue("o.capitale").getValue());

		assertEquals(5, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("c.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("c.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("c.provincee").getValue());
		assertEquals("Germany", resultInstances.get(6).getResultValue("o.namee").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("o.capitale").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinJoinQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(15, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("13.3", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("borderse.country1e").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("borderse.country2e").getValue());
		assertEquals("68.0", resultInstances.get(0).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(1).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(1).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(1).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(2).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(2).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("borderse.country1e").getValue());
		assertEquals("DK", resultInstances.get(2).getResultValue("borderse.country2e").getValue());
		assertEquals("68.0", resultInstances.get(2).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(3).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(3).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(4).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(4).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(4).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(4).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(5).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(5).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(5).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(5).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(5).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(5).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(6).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(6).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(6).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("borderse.country1e").getValue());
		assertEquals("DK", resultInstances.get(6).getResultValue("borderse.country2e").getValue());
		assertEquals("68.0", resultInstances.get(6).getResultValue("borderse.lengthe").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinJoinWhereQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(4, resultInstances.size());

		assertEquals(15, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(0).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(0).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(1).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(1).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(2).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(2).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(2).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(2).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(3).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(3).getResultValue("borderse.lengthe").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinJoinWhereAndQueryOneSource() {
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(15, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(0).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(0).getResultValue("borderse.lengthe").getValue());
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

		queryString = queryString.substring(0, queryString.indexOf(";")) + " ORDER BY citye.namee";
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(15, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("3472009", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("13.3", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.45", resultInstances.get(0).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(0).getResultValue("borderse.country1e").getValue());
		assertEquals("DK", resultInstances.get(0).getResultValue("borderse.country2e").getValue());
		assertEquals("68.0", resultInstances.get(0).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(1).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(1).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(1).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Hamburg", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("1705872", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("9.96667", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("53.55", resultInstances.get(2).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(2).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("borderse.country1e").getValue());
		assertEquals("DK", resultInstances.get(2).getResultValue("borderse.country2e").getValue());
		assertEquals("68.0", resultInstances.get(2).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(3).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(3).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(4).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(4).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(4).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(4).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(4).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.latitudee").getValue());
		assertEquals("United Kingdom", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(5).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(5).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(5).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(5).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(5).getResultValue("borderse.country1e").getValue());
		assertEquals("IRL", resultInstances.get(5).getResultValue("borderse.country2e").getValue());
		assertEquals("360.0", resultInstances.get(5).getResultValue("borderse.lengthe").getValue());

		assertEquals(15, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Bayern", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("1290079", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("11.5667", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("48.15", resultInstances.get(6).getResultValue("citye.latitudee").getValue());
		assertEquals("Germany", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(6).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(6).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(6).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(6).getResultValue("borderse.country1e").getValue());
		assertEquals("DK", resultInstances.get(6).getResultValue("borderse.country2e").getValue());
		assertEquals("68.0", resultInstances.get(6).getResultValue("borderse.lengthe").getValue());
	}

	//@Test
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
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
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
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
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
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM borderse borderse, citye citye, countrye countrye WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM borderse borderse, citye citye, countrye countrye WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
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

	//@Test
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
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
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
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
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
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
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

	//@Test
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
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
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
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
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
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
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
	public void testEvaluateSelectStarFromJoinQueryTwoSources() {
		String selectStarFromJoinQuery = "Select * from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		rootOperator = physicalOptimiser.chooseJoinOperators(rootOperator, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(HashJoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((HashJoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((HashJoinOperatorImpl) input0Operator).getReconcilingExpression());
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

		queryString2 = queryString2.substring(0, queryString2.indexOf(";")) + " ORDER BY countrye.namee";
		evaluateExternallyOp2.setQueryString(queryString2);

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

		queryString1 = queryString1.substring(0, queryString1.indexOf(";")) + " ORDER BY ethnicgroupe.namee";
		evaluateExternallyOp1.setQueryString(queryString1);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(10, resultInstances.size());

		assertEquals(9, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("English", resultInstances.get(0).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("81.5", resultInstances.get(0).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Germany", resultInstances.get(1).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(1).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(1).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(1).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(1).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("German", resultInstances.get(1).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("95.1", resultInstances.get(1).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Germany", resultInstances.get(2).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(2).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(2).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(2).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Greek", resultInstances.get(2).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("0.4", resultInstances.get(2).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(3).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(3).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Irish", resultInstances.get(3).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("2.4", resultInstances.get(3).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Germany", resultInstances.get(4).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(4).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(4).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(4).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(4).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(4).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(4).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Italian", resultInstances.get(4).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("0.7", resultInstances.get(4).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Germany", resultInstances.get(5).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(5).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(5).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Polish", resultInstances.get(5).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("0.4", resultInstances.get(5).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(6).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(6).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(6).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(6).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(6).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(6).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Scottish", resultInstances.get(6).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("9.6", resultInstances.get(6).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(7).getResultFieldNameResultValueMap().size());
		assertEquals("Germany", resultInstances.get(7).getResultValue("countrye.namee").getValue());
		assertEquals("D", resultInstances.get(7).getResultValue("countrye.codee").getValue());
		assertEquals("Berlin", resultInstances.get(7).getResultValue("countrye.capitale").getValue());
		assertEquals("Berlin", resultInstances.get(7).getResultValue("countrye.provincee").getValue());
		assertEquals("356910.0", resultInstances.get(7).getResultValue("countrye.areae").getValue());
		assertEquals("83536115", resultInstances.get(7).getResultValue("countrye.populatione").getValue());
		assertEquals("D", resultInstances.get(7).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Turkish", resultInstances.get(7).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("2.3", resultInstances.get(7).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(8).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(8).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(8).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(8).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(8).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Ulster", resultInstances.get(8).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("1.8", resultInstances.get(8).getResultValue("ethnicgroupe.percentagee").getValue());

		assertEquals(9, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(9).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(9).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(9).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(9).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(9).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(9).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(9).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("Welsh", resultInstances.get(9).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("1.9", resultInstances.get(9).getResultValue("ethnicgroupe.percentagee").getValue());
	}

	@Test
	public void testEvaluateSelectStarFromJoinWhereAndQueryTwoSources() {
		String selectStarFromJoinQuery = "Select * from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye and countrye.codee = 'GB' and ethnicgroupe.namee = 'English'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		rootOperator = physicalOptimiser.chooseJoinOperators(rootOperator, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(HashJoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((HashJoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((HashJoinOperatorImpl) input0Operator).getReconcilingExpression());
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

		queryString2 = queryString2.substring(0, queryString2.indexOf(";")) + " ORDER BY countrye.namee";
		evaluateExternallyOp2.setQueryString(queryString2);

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

		queryString1 = queryString1.substring(0, queryString1.indexOf(";")) + " ORDER BY ethnicgroupe.namee";
		evaluateExternallyOp1.setQueryString(queryString1);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(9, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("countrye.provincee").getValue());
		assertEquals("244820.0", resultInstances.get(0).getResultValue("countrye.areae").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("countrye.populatione").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("ethnicgroupe.countrye").getValue());
		assertEquals("English", resultInstances.get(0).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("81.5", resultInstances.get(0).getResultValue("ethnicgroupe.percentagee").getValue());

	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinWhereAndQueryTwoSources() {
		String selectStarFromJoinQuery = "Select countrye.namee, countrye.codee, countrye.capitale, ethnicgroupe.namee, ethnicgroupe.percentagee from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye and countrye.codee = 'GB' and ethnicgroupe.namee = 'English'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectStarFromJoinQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarFromJoinQueryAst: " + selectStarFromJoinQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		rootOperator = physicalOptimiser.chooseJoinOperators(rootOperator, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(HashJoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((HashJoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((HashJoinOperatorImpl) input0Operator).getReconcilingExpression());
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

		queryString2 = queryString2.substring(0, queryString2.indexOf(";")) + " ORDER BY countrye.namee";
		evaluateExternallyOp2.setQueryString(queryString2);

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

		queryString1 = queryString1.substring(0, queryString1.indexOf(";")) + " ORDER BY ethnicgroupe.namee";
		evaluateExternallyOp1.setQueryString(queryString1);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(1, resultInstances.size());

		assertEquals(5, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("countrye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("countrye.codee").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("countrye.capitale").getValue());
		assertEquals("English", resultInstances.get(0).getResultValue("ethnicgroupe.namee").getValue());
		assertEquals("81.5", resultInstances.get(0).getResultValue("ethnicgroupe.percentagee").getValue());

	}

	@Test
	public void testEvaluateSelectSuperLexicalsFromJoinJoinJoinWhereAndQueryWithVariableNameTwoSources() {
		String selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName = "Select c.namee, c.countrye, c.provincee, o.namee, o.capitale, o.populatione, "
				+ "b.country1e, b.country2e, g.namee, g.percentagee from citye c, countrye o, borderse b, ethnicgroupe g where c.countrye = o.codee and "
				+ "o.codee = b.country1e and o.codee = g.countrye and o.codee = 'GB' and c.namee = 'Manchester'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialLanguageEconomyReligionOfCountriesEuropeWR");
		logger.debug("mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS: " + mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS);

		CommonTree selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst = parser
				.parseSQL(selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		logger.debug("selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst: "
				+ selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName,
				selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		rootOperator = physicalOptimiser.chooseJoinOperators(rootOperator, null);
		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		assertEquals(5, resultInstances.size());

		assertEquals(10, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(0).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(0).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(0).getResultValue("o.namee").getValue());
		assertEquals("58489975", resultInstances.get(0).getResultValue("o.populatione").getValue());
		assertEquals("London", resultInstances.get(0).getResultValue("o.capitale").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("b.country1e").getValue());
		assertEquals("IRL", resultInstances.get(0).getResultValue("b.country2e").getValue());
		assertEquals("English", resultInstances.get(0).getResultValue("g.namee").getValue());
		assertEquals("81.5", resultInstances.get(0).getResultValue("g.percentagee").getValue());

		assertEquals(10, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(1).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(1).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(1).getResultValue("o.namee").getValue());
		assertEquals("58489975", resultInstances.get(1).getResultValue("o.populatione").getValue());
		assertEquals("London", resultInstances.get(1).getResultValue("o.capitale").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("b.country1e").getValue());
		assertEquals("IRL", resultInstances.get(1).getResultValue("b.country2e").getValue());
		assertEquals("Irish", resultInstances.get(1).getResultValue("g.namee").getValue());
		assertEquals("2.4", resultInstances.get(1).getResultValue("g.percentagee").getValue());

		assertEquals(10, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(2).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(2).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(2).getResultValue("o.namee").getValue());
		assertEquals("58489975", resultInstances.get(2).getResultValue("o.populatione").getValue());
		assertEquals("London", resultInstances.get(2).getResultValue("o.capitale").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("b.country1e").getValue());
		assertEquals("IRL", resultInstances.get(2).getResultValue("b.country2e").getValue());
		assertEquals("Scottish", resultInstances.get(2).getResultValue("g.namee").getValue());
		assertEquals("9.6", resultInstances.get(2).getResultValue("g.percentagee").getValue());

		assertEquals(10, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(3).getResultValue("o.namee").getValue());
		assertEquals("58489975", resultInstances.get(3).getResultValue("o.populatione").getValue());
		assertEquals("London", resultInstances.get(3).getResultValue("o.capitale").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("b.country1e").getValue());
		assertEquals("IRL", resultInstances.get(3).getResultValue("b.country2e").getValue());
		assertEquals("Ulster", resultInstances.get(3).getResultValue("g.namee").getValue());
		assertEquals("1.8", resultInstances.get(3).getResultValue("g.percentagee").getValue());

		assertEquals(10, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(4).getResultValue("c.namee").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("c.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(4).getResultValue("c.provincee").getValue());
		assertEquals("United Kingdom", resultInstances.get(4).getResultValue("o.namee").getValue());
		assertEquals("58489975", resultInstances.get(4).getResultValue("o.populatione").getValue());
		assertEquals("London", resultInstances.get(4).getResultValue("o.capitale").getValue());
		assertEquals("GB", resultInstances.get(4).getResultValue("b.country1e").getValue());
		assertEquals("IRL", resultInstances.get(4).getResultValue("b.country2e").getValue());
		assertEquals("Welsh", resultInstances.get(4).getResultValue("g.namee").getValue());
		assertEquals("1.9", resultInstances.get(4).getResultValue("g.percentagee").getValue());
	}

	//@Test
	//TODO this doesn't work as the rename mapping is missing and messes up the resultInstanceFieldNames etc.
	public void testEvaluateSelectStarFromWhereUnionQueryTwoSources() {
		String selectStarFromWhereUnionQuery = "Select * from citye where citye.countrye = 'GB' union select * from citya where citya.countrya = 'MA'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialCityProvinceCountryContinentAfricaWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfricaWR");
		logger.debug("mondialCityProvinceCountryContinentAfricaWithRenameDS: " + mondialCityProvinceCountryContinentAfricaWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereUnionQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialCityProvinceCountryContinentAfricaWithRenameDS);

		CommonTree selectStarFromWhereUnionQueryAst = parser.parseSQL(selectStarFromWhereUnionQuery);
		logger.debug("selectStarFromWhereUnionQueryAst: " + selectStarFromWhereUnionQueryAst.toStringTree());

		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereUnionQuery, selectStarFromWhereUnionQueryAst);

		MappingOperator rootOperator = query.getRootOperator();

		isInstanceOf(ReduceOperator.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(6, ((ReduceOperator) rootOperator).getSuperLexicals().size());
		assertEquals(
				"union1.citye.namee, union1.citye.countrye, union1.citye.provincee, union1.citye.populatione, union1.citye.longitudee, union1.citye.latitudee",
				((ReduceOperator) rootOperator).getReconcilingExpression().getExpression());

		assertNull(rootOperator.getDataSource());

		EvaluatorOperator rootEvaluatorOperator = logicalQueryOptimiser.optimise(query, null);
		rootEvaluatorOperator = physicalOptimiser.chooseJoinOperators(rootEvaluatorOperator, null);
		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootEvaluatorOperator);

		assertEquals(7, resultInstances.size());

		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(0).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("citye.countrye").getValue());
		assertEquals("West Midlands", resultInstances.get(0).getResultValue("citye.provincee").getValue());
		assertEquals("1008400", resultInstances.get(0).getResultValue("citye.populatione").getValue());
		assertEquals("-1.93333", resultInstances.get(0).getResultValue("citye.longitudee").getValue());
		assertEquals("52.4833", resultInstances.get(0).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(1).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(1).getResultValue("citye.countrye").getValue());
		assertEquals("West Yorkshire", resultInstances.get(1).getResultValue("citye.provincee").getValue());
		assertEquals("724400", resultInstances.get(1).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(1).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(2).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(2).getResultValue("citye.countrye").getValue());
		assertEquals("Greater London", resultInstances.get(2).getResultValue("citye.provincee").getValue());
		assertEquals("6967500", resultInstances.get(2).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(2).getResultValue("citye.longitudee").getValue());
		assertEquals("51.4833", resultInstances.get(2).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(3).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(3).getResultValue("citye.namee").getValue());
		assertEquals("GB", resultInstances.get(3).getResultValue("citye.countrye").getValue());
		assertEquals("Greater Manchester", resultInstances.get(3).getResultValue("citye.provincee").getValue());
		assertEquals("431100", resultInstances.get(3).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(3).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(4).getResultFieldNameResultValueMap().size());
		assertEquals("Casablanca", resultInstances.get(4).getResultValue("citye.namee").getValue());
		assertEquals("MA", resultInstances.get(4).getResultValue("citye.countrye").getValue());
		assertEquals("Morocco", resultInstances.get(4).getResultValue("citye.provincee").getValue());
		assertEquals("2940623", resultInstances.get(4).getResultValue("citye.populatione").getValue());
		assertEquals("-7.65", resultInstances.get(4).getResultValue("citye.longitudee").getValue());
		assertEquals("33.5833", resultInstances.get(4).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Marrakech", resultInstances.get(5).getResultValue("citye.namee").getValue());
		assertEquals("MA", resultInstances.get(5).getResultValue("citye.countrye").getValue());
		assertEquals("Morocco", resultInstances.get(5).getResultValue("citye.provincee").getValue());
		assertEquals("745541", resultInstances.get(5).getResultValue("citye.populatione").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.longitudee").getValue());
		assertEquals("0.0", resultInstances.get(5).getResultValue("citye.latitudee").getValue());

		assertEquals(6, resultInstances.get(6).getResultFieldNameResultValueMap().size());
		assertEquals("Rabat", resultInstances.get(6).getResultValue("citye.namee").getValue());
		assertEquals("MA", resultInstances.get(6).getResultValue("citye.countrye").getValue());
		assertEquals("Morocco", resultInstances.get(6).getResultValue("citye.provincee").getValue());
		assertEquals("1385872", resultInstances.get(6).getResultValue("citye.populatione").getValue());
		assertEquals("-6.5", resultInstances.get(6).getResultValue("citye.longitudee").getValue());
		assertEquals("33.6", resultInstances.get(6).getResultValue("citye.latitudee").getValue());
	}

	/**
	 * Test method for {@link uk.ac.manchester.dataspaces.service.impl.operators.query.querytranslator.GlobalQueryTranslatorServiceImpl#translateAstIntoQuery(org.antlr.runtime.tree.CommonTree)}.
	 */
	//@Test
	public void testTranslateAstIntoQuerySelectStarFromWhereUnionUnionQueryTwoSources() {
		String selectStarFromWhereUnionUnionQuery = "Select * from citye where citye.countrye = 'GB' union select * from citya where citya.countrya = 'MA' union select * from citye where citye.countrye = 'IRL'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		DataSource mondialCityProvinceCountryContinentAfricaWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfricaWR");
		logger.debug("mondialCityProvinceCountryContinentAfricaWithRenameDS: " + mondialCityProvinceCountryContinentAfricaWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereUnionUnionQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		query.addDataSource(mondialCityProvinceCountryContinentAfricaWithRenameDS);

		CommonTree selectStarFromWhereUnionUnionQueryAst = parser.parseSQL(selectStarFromWhereUnionUnionQuery);
		logger.debug("selectStarFromWhereUnionQueryAst: " + selectStarFromWhereUnionUnionQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereUnionUnionQuery, selectStarFromWhereUnionUnionQueryAst);

		MappingOperator rootOperator = query.getRootOperator();

		isInstanceOf(ReduceOperator.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(6, ((ReduceOperator) rootOperator).getSuperLexicals().size());
		assertEquals(
				"union1.union1.citye.namee, union1.union1.citye.countrye, union1.union1.citye.provincee, union1.union1.citye.populatione, union1.union1.citye.longitudee, union1.union1.citye.latitudee",
				((ReduceOperator) rootOperator).getReconcilingExpression().getExpression());

		assertNull(rootOperator.getDataSource());

	}

}
