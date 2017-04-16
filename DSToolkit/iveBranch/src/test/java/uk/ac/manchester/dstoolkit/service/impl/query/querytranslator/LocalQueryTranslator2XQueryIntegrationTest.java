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
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2XQueryService;

/**
 * @author chedeler
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
public class LocalQueryTranslator2XQueryIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(LocalQueryTranslator2XQueryIntegrationTest.class);

	@Autowired
	@Qualifier("localQueryTranslator2XQueryService")
	private LocalQueryTranslator2XQueryService localQueryTranslator2XQuery;

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
	public void testTranslate2XQuerySelectStarQueryOneSource() {
		String selectStarFromQuery = "Select * from city";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<city.name> {fn:data($city/name)} </city.name>\n"
				+ "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n" + "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<city.name> {fn:data($city/name)} </city.name>\n"
				+ "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n" + "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());

	}

	@Test
	public void testTranslate2XQuerySelectStarFromWhereQueryOneSource() {
		String selectStarFromWhereQuery = "Select * from city where city.country = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromWhereQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromWhereAndQueryOneSource() {
		String selectStarFromWhereAndQuery = "Select * from city where city.country = 'GB' and city.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromWhereAndQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' and $city/name='Manchester'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' and $city/name='Manchester'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromWhereOrQueryOneSource() {
		String selectStarFromWhereOrQuery = "Select * from city where city.country = 'GB' or city.country = 'D'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromWhereOrQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' or $city/@country='D'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' or $city/@country='D'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromQueryOneSource() {
		String selectSuperLexicalsFromQuery = "Select name, country, province, population from city";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<city.name> {fn:data($city/name)} </city.name>\n"
				+ "<city.country> {fn:data($city/@country)} </city.country>\n" + "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<city.name> {fn:data($city/name)} </city.name>\n"
				+ "<city.country> {fn:data($city/@country)} </city.country>\n" + "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromWhereQueryOneSource() {
		String selectSuperLexicalsFromWhereQuery = "Select name, country, province, population from city where city.country = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromWhereAndQueryOneSource() {
		String selectSuperLexicalsFromWhereAndQuery = "Select name, country, province, population from city where city.country = 'GB' and city.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereAndQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' and $city/name='Manchester'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' and $city/name='Manchester'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromWhereOrQueryOneSource() {
		String selectSuperLexicalsFromWhereOrQuery = "Select name, country, province, population from city where city.country = 'GB' or city.country = 'D'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereOrQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' or $city/@country='D'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $city in $MondialEuropeXML//city\n" + "where $city/@country='GB' or $city/@country='D'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromQueryWithVariableNameOneSource() {
		String selectStarFromQueryWithVariableName = "Select * from city c";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n" + "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.id> {fn:data($c/@id)} </c.id>\n"
				+ "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n" + "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.id> {fn:data($c/@id)} </c.id>\n"
				+ "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromWhereQueryWithVariableNameOneSource() {
		String selectStarFromWhereQueryWithVariableName = "Select * from city c where c.country = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromWhereQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB'\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n" + "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.id> {fn:data($c/@id)} </c.id>\n"
				+ "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB'\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n" + "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.id> {fn:data($c/@id)} </c.id>\n"
				+ "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromWhereAndQueryWithVariableNameOneSource() {
		String selectStarFromWhereAndQueryWithVariableName = "Select * from city c where c.country = 'GB' and c.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromWhereAndQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' and $c/name='Manchester'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n"
				+ "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n" + "<c.population> {fn:data($c/population)} </c.population>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.id> {fn:data($c/@id)} </c.id>\n" + "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' and $c/name='Manchester'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n"
				+ "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n" + "<c.population> {fn:data($c/population)} </c.population>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.id> {fn:data($c/@id)} </c.id>\n" + "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromWhereOrQueryWithVariableNameOneSource() {
		String selectStarFromWhereOrQueryWithVariableName = "Select * from city c where c.country = 'GB' or c.country = 'D'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromWhereOrQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' or $c/@country='D'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n"
				+ "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n" + "<c.population> {fn:data($c/population)} </c.population>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.id> {fn:data($c/@id)} </c.id>\n" + "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' or $c/@country='D'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.longitude> {fn:data($c/longitude)} </c.longitude>\n"
				+ "<c.latitude> {fn:data($c/latitude)} </c.latitude>\n" + "<c.population> {fn:data($c/population)} </c.population>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.id> {fn:data($c/@id)} </c.id>\n" + "<c.is_state_cap> {fn:data($c/@is_state_cap)} </c.is_state_cap>\n"
				+ "<c.is_country_cap> {fn:data($c/@is_country_cap)} </c.is_country_cap>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromQueryWithVariableName = "Select c.name, c.country, c.province, c.population from city c";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n" + "} </result>", evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromWhereQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromWhereQueryWithVariableName = "Select c.name, c.country, c.province, c.population from city c where c.country = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB'\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB'\n" + "return <tuple>\n" + "<c.name> {fn:data($c/name)} </c.name>\n"
				+ "<c.country> {fn:data($c/@country)} </c.country>\n" + "<c.province> {fn:data($c/@province)} </c.province>\n"
				+ "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n" + "} </result>", evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromWhereAndQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromWhereAndQueryWithVariableName = "Select c.name, c.country, c.province, c.population from city c where c.country = 'GB' and c.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereAndQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' and $c/name='Manchester'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n"
				+ "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' and $c/name='Manchester'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n"
				+ "} </result>", evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromWhereOrQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromWhereOrQueryWithVariableName = "Select c.name, c.country, c.province, c.population from city c where c.country = 'GB' or c.country = 'D'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromWhereOrQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' or $c/@country='D'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n"
				+ "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $c in $MondialEuropeXML//city\n" + "where $c/@country='GB' or $c/@country='D'\n" + "return <tuple>\n"
				+ "<c.name> {fn:data($c/name)} </c.name>\n" + "<c.country> {fn:data($c/@country)} </c.country>\n"
				+ "<c.province> {fn:data($c/@province)} </c.province>\n" + "<c.population> {fn:data($c/population)} </c.population>\n" + "</tuple>\n"
				+ "} </result>", evaluateExternallyOp.getQueryString());
	}

	//TODO check whether the translator works correctly for two selection predicates on same complex element

	@Test
	public void testTranslate2XQuerySelectStarFromJoinQueryOneSource() {
		String selectStarFromJoinQuery = "Select * from city, country where city.country = country.car_code";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		//TODO not checking for all ancestor superLexicals ... country.province ... missing

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $country in $MondialEuropeXML//country\n" + "for $city in $MondialEuropeXML//city\n"
				+ "where $city/@country=$country/@car_code\n" + "return <tuple>\n" + "<city.name> {fn:data($city/name)} </city.name>\n"
				+ "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n" + "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n"
				+ "<country.name> {fn:data($country/name)} </country.name>\n" + "<country.area> {fn:data($country/@area)} </country.area>\n"
				+ "<country.memberships> {fn:data($country/@memberships)} </country.memberships>\n"
				+ "<country.capital> {fn:data($country/@capital)} </country.capital>\n"
				+ "<country.car_code> {fn:data($country/@car_code)} </country.car_code>\n"
				+ "<country.population> {fn:data($country/population)} </country.population>\n"
				+ "<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>\n"
				+ "<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>\n"
				+ "<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>\n"
				+ "<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>\n"
				+ "<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>\n"
				+ "<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>\n"
				+ "<country.inflation> {fn:data($country/inflation)} </country.inflation>\n"
				+ "<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>\n"
				+ "<country.government> {fn:data($country/government)} </country.government>\n"
				+ "<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>\n"
				+ "<country.religions> {fn:data($country/religions)} </country.religions>\n"
				+ "<country.languages> {fn:data($country/languages)} </country.languages>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $country in $MondialEuropeXML//country\n" + "for $city in $MondialEuropeXML//city\n"
				+ "where $city/@country=$country/@car_code\n" + "return <tuple>\n" + "<city.name> {fn:data($city/name)} </city.name>\n"
				+ "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n" + "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n"
				+ "<country.name> {fn:data($country/name)} </country.name>\n" + "<country.area> {fn:data($country/@area)} </country.area>\n"
				+ "<country.memberships> {fn:data($country/@memberships)} </country.memberships>\n"
				+ "<country.capital> {fn:data($country/@capital)} </country.capital>\n"
				+ "<country.car_code> {fn:data($country/@car_code)} </country.car_code>\n"
				+ "<country.population> {fn:data($country/population)} </country.population>\n"
				+ "<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>\n"
				+ "<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>\n"
				+ "<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>\n"
				+ "<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>\n"
				+ "<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>\n"
				+ "<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>\n"
				+ "<country.inflation> {fn:data($country/inflation)} </country.inflation>\n"
				+ "<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>\n"
				+ "<country.government> {fn:data($country/government)} </country.government>\n"
				+ "<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>\n"
				+ "<country.religions> {fn:data($country/religions)} </country.religions>\n"
				+ "<country.languages> {fn:data($country/languages)} </country.languages>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereQueryOneSource() {
		String selectStarFromJoinWhereQuery = "Select * from city, country where city.country = country.car_code and country.car_code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinWhereQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $country in $MondialEuropeXML//country\n" + "for $city in $MondialEuropeXML//city\n"
				+ "where $city/@country=$country/@car_code and $country/@car_code='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n"
				+ "<country.name> {fn:data($country/name)} </country.name>\n" + "<country.area> {fn:data($country/@area)} </country.area>\n"
				+ "<country.memberships> {fn:data($country/@memberships)} </country.memberships>\n"
				+ "<country.capital> {fn:data($country/@capital)} </country.capital>\n"
				+ "<country.car_code> {fn:data($country/@car_code)} </country.car_code>\n"
				+ "<country.population> {fn:data($country/population)} </country.population>\n"
				+ "<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>\n"
				+ "<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>\n"
				+ "<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>\n"
				+ "<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>\n"
				+ "<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>\n"
				+ "<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>\n"
				+ "<country.inflation> {fn:data($country/inflation)} </country.inflation>\n"
				+ "<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>\n"
				+ "<country.government> {fn:data($country/government)} </country.government>\n"
				+ "<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>\n"
				+ "<country.religions> {fn:data($country/religions)} </country.religions>\n"
				+ "<country.languages> {fn:data($country/languages)} </country.languages>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $country in $MondialEuropeXML//country\n" + "for $city in $MondialEuropeXML//city\n"
				+ "where $city/@country=$country/@car_code and $country/@car_code='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n"
				+ "<country.name> {fn:data($country/name)} </country.name>\n" + "<country.area> {fn:data($country/@area)} </country.area>\n"
				+ "<country.memberships> {fn:data($country/@memberships)} </country.memberships>\n"
				+ "<country.capital> {fn:data($country/@capital)} </country.capital>\n"
				+ "<country.car_code> {fn:data($country/@car_code)} </country.car_code>\n"
				+ "<country.population> {fn:data($country/population)} </country.population>\n"
				+ "<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>\n"
				+ "<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>\n"
				+ "<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>\n"
				+ "<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>\n"
				+ "<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>\n"
				+ "<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>\n"
				+ "<country.inflation> {fn:data($country/inflation)} </country.inflation>\n"
				+ "<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>\n"
				+ "<country.government> {fn:data($country/government)} </country.government>\n"
				+ "<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>\n"
				+ "<country.religions> {fn:data($country/religions)} </country.religions>\n"
				+ "<country.languages> {fn:data($country/languages)} </country.languages>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
	}

	@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereAndQueryOneSource() {
		String selectStarFromJoinWhereAndQuery = "Select * from city, country where city.country = country.car_code and country.car_code = 'GB' and city.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinWhereAndQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		/*
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $country in $MondialEuropeXML//country\n" + "for $city in $MondialEuropeXML//city\n"
				+ "where $city/@country=$country/@car_code and $city/name='Manchester' and $country/@car_code='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n"
				+ "<country.name> {fn:data($country/name)} </country.name>\n" + "<country.area> {fn:data($country/@area)} </country.area>\n"
				+ "<country.memberships> {fn:data($country/@memberships)} </country.memberships>\n"
				+ "<country.capital> {fn:data($country/@capital)} </country.capital>\n"
				+ "<country.car_code> {fn:data($country/@car_code)} </country.car_code>\n"
				+ "<country.population> {fn:data($country/population)} </country.population>\n"
				+ "<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>\n"
				+ "<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>\n"
				+ "<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>\n"
				+ "<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>\n"
				+ "<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>\n"
				+ "<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>\n"
				+ "<country.inflation> {fn:data($country/inflation)} </country.inflation>\n"
				+ "<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>\n"
				+ "<country.government> {fn:data($country/government)} </country.government>\n"
				+ "<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>\n"
				+ "<country.religions> {fn:data($country/religions)} </country.religions>\n"
				+ "<country.languages> {fn:data($country/languages)} </country.languages>\n" + "</tuple>\n" + "} </result>", queryString);
		assertEquals("<result>{\n"
				+ "let $MondialEuropeXML := doc(\"xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml\")\n"
				+ "for $country in $MondialEuropeXML//country\n" + "for $city in $MondialEuropeXML//city\n"
				+ "where $city/@country=$country/@car_code and $city/name='Manchester' and $country/@car_code='GB'\n" + "return <tuple>\n"
				+ "<city.name> {fn:data($city/name)} </city.name>\n" + "<city.longitude> {fn:data($city/longitude)} </city.longitude>\n"
				+ "<city.latitude> {fn:data($city/latitude)} </city.latitude>\n"
				+ "<city.population> {fn:data($city/population)} </city.population>\n"
				+ "<city.province> {fn:data($city/@province)} </city.province>\n" + "<city.country> {fn:data($city/@country)} </city.country>\n"
				+ "<city.id> {fn:data($city/@id)} </city.id>\n" + "<city.is_state_cap> {fn:data($city/@is_state_cap)} </city.is_state_cap>\n"
				+ "<city.is_country_cap> {fn:data($city/@is_country_cap)} </city.is_country_cap>\n"
				+ "<country.name> {fn:data($country/name)} </country.name>\n" + "<country.area> {fn:data($country/@area)} </country.area>\n"
				+ "<country.memberships> {fn:data($country/@memberships)} </country.memberships>\n"
				+ "<country.capital> {fn:data($country/@capital)} </country.capital>\n"
				+ "<country.car_code> {fn:data($country/@car_code)} </country.car_code>\n"
				+ "<country.population> {fn:data($country/population)} </country.population>\n"
				+ "<country.population_growth> {fn:data($country/population_growth)} </country.population_growth>\n"
				+ "<country.infant_mortality> {fn:data($country/infant_mortality)} </country.infant_mortality>\n"
				+ "<country.gdp_total> {fn:data($country/gdp_total)} </country.gdp_total>\n"
				+ "<country.gdp_agri> {fn:data($country/gdp_agri)} </country.gdp_agri>\n"
				+ "<country.gdp_ind> {fn:data($country/gdp_ind)} </country.gdp_ind>\n"
				+ "<country.gdp_serv> {fn:data($country/gdp_serv)} </country.gdp_serv>\n"
				+ "<country.inflation> {fn:data($country/inflation)} </country.inflation>\n"
				+ "<country.indep_date> {fn:data($country/indep_date)} </country.indep_date>\n"
				+ "<country.government> {fn:data($country/government)} </country.government>\n"
				+ "<country.ethnicgroups> {fn:data($country/ethnicgroups)} </country.ethnicgroups>\n"
				+ "<country.religions> {fn:data($country/religions)} </country.religions>\n"
				+ "<country.languages> {fn:data($country/languages)} </country.languages>\n" + "</tuple>\n" + "} </result>",
				evaluateExternallyOp.getQueryString());
				*/
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereOrQueryOneSource() {
		String selectStarFromJoinWhereOrQuery = "Select * from city, country where city.country = country.car_code and (country.car_code = 'GB' or country.car_code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinWhereOrQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

		CommonTree selectStarFromJoinWhereOrQueryAst = parser.parseSQL(selectStarFromJoinWhereOrQuery);
		logger.debug("selectStarFromJoinWhereOrQueryAst: " + selectStarFromJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinWhereOrQuery, selectStarFromJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinQueryOneSource() {
		String selectSuperLexicalsFromJoinQuery = "Select city.name, city.country, city.province, country.name, country.capital from city, country where city.country = country.car_code";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereQueryOneSource() {
		String selectSuperLexicalsFromJoinWhereQuery = "Select city.name, city.country, city.province, country.name, country.capital from city, country where city.country = country.code and country.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereAndQueryOneSource() {
		String selectSuperLexicalsFromJoinWhereAndQuery = "Select city.name, city.country, city.province, country.name, country.capital from city, country where city.country = country.code and country.code = 'GB' and city.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereAndQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereOrQueryOneSource() {
		String selectSuperLexicalsFromJoinWhereOrQuery = "Select city.name, city.country, city.province, country.name, country.capital from city, country where city.country = country.code and (country.code = 'GB' or country.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereOrQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

		CommonTree selectSuperLexicalsFromJoinWhereOrQueryAst = parser.parseSQL(selectSuperLexicalsFromJoinWhereOrQuery);
		logger.debug("selectSuperLexicalsFromJoinWhereOrQueryAst: " + selectSuperLexicalsFromJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectSuperLexicalsFromJoinWhereOrQuery, selectSuperLexicalsFromJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale FROM citye citye, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinQueryWithVariableNameOneSource() {
		String selectStarFromJoinQueryWithVariableName = "Select * from city c, country o where c.country = o.code";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereQueryWithVariableNameOneSource() {
		String selectStarFromJoinWhereQueryWithVariableName = "Select * from city c, country o where c.country = o.code and o.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinWhereQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereAndQueryWithVariableNameOneSource() {
		String selectStarFromJoinWhereAndQueryWithVariableName = "Select * from city c, country o where c.country = o.code and o.code = 'GB' and c.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereOrQueryWithVariableNameOneSource() {
		String selectStarFromJoinWhereOrQueryWithVariableName = "Select * from city c, country o where c.country = o.code and (o.code = 'GB' or o.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital from city c, country o where c.country = o.code";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals("SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee;", queryString);
		assertEquals("SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinWhereQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital from city c, country o where c.country = o.code and o.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereAndQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinWhereAndQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital from city c, country o where c.country = o.code and o.code = 'GB' and c.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereOrQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinWhereOrQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital from city c, country o where c.country = o.code and (o.code = 'GB' or o.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale FROM countrye o, citye c WHERE c.countrye = o.codee and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinQueryOneSource() {
		String selectStarFromJoinJoinQuery = "Select * from city, country, borders where city.country = country.code and country.code = borders.country1";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinWhereQueryOneSource() {
		String selectStarFromJoinJoinWhereQuery = "Select * from city, country, borders where city.country = country.code and country.code = borders.country1 and country.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinWhereAndQueryOneSource() {
		String selectStarFromJoinJoinWhereAndQuery = "Select * from city, country, borders where city.country = country.code and country.code = borders.country1 and country.code = 'GB' and city.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereAndQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM borderse borderse, citye citye, countrye countrye WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM borderse borderse, citye citye, countrye countrye WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinWhereOrQueryOneSource() {
		String selectStarFromJoinJoinWhereOrQuery = "Select * from city, country, borders where city.country = country.code and country.code = borders.country1 and (country.code = 'GB' or country.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereOrQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

		CommonTree selectStarFromJoinJoinWhereOrQueryAst = parser.parseSQL(selectStarFromJoinJoinWhereOrQuery);
		logger.debug("selectStarFromJoinJoinWhereOrQueryAst: " + selectStarFromJoinJoinWhereOrQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinJoinWhereOrQuery, selectStarFromJoinJoinWhereOrQueryAst);

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, borderse.country1e, borderse.country2e, borderse.lengthe FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinQuery = "Select city.name, city.country, city.province, country.name, country.capital, borders.country2 from city, country, borders where city.country = country.code and country.code = borders.country1";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinWhereQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereQuery = "Select city.name, city.country, city.province, country.name, country.capital, borders.country2 from city, country, borders where city.country = country.code and country.code = borders.country1 and country.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinWhereAndQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereAndQuery = "Select city.name, city.country, city.province, country.name, country.capital, borders.country2 from city, country, borders where city.country = country.code and country.code = borders.country1 and country.code = 'GB' and city.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereAndQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM borderse borderse, citye citye, countrye countrye WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM borderse borderse, citye citye, countrye countrye WHERE countrye.codee = borderse.country1e and citye.countrye = countrye.codee and citye.namee = 'Manchester' and countrye.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinWhereOrQueryOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereOrQuery = "Select city.name, city.country, city.province, country.name, country.capital, borders.country2 from city, country, borders where city.country = country.code and country.code = borders.country1 and (country.code = 'GB' or country.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereOrQuery);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale, borderse.country2e FROM citye citye, borderse borderse, countrye countrye WHERE citye.countrye = countrye.codee and countrye.codee = borderse.country1e and countrye.codee = 'GB' or countrye.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinQueryWithVariableName = "Select * from city c, country o, borders b where c.country = o.code and o.code = b.country1";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinWhereQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinWhereQueryWithVariableName = "Select * from city c, country o, borders b where c.country = o.code and o.code = b.country1 and o.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinWhereAndQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinWhereAndQueryWithVariableName = "Select * from city c, country o, borders b where c.country = o.code and o.code = b.country1 and o.code = 'GB' and c.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinJoinWhereOrQueryWithVariableNameOneSource() {
		String selectStarFromJoinJoinWhereOrQueryWithVariableName = "Select * from city c, country o, borders b where c.country = o.code and o.code = b.country1 and (o.code = 'GB' or o.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectStarFromJoinJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione, b.country1e, b.country2e, b.lengthe FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital, b.country2 from city c, country o, borders b where c.country = o.code and o.code = b.country1";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e;",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinWhereQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital, b.country2 from city c, country o, borders b where c.country = o.code and o.code = b.country1 and o.code = 'GB'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital, b.country2 from city c, country o, borders b where c.country = o.code and o.code = b.country1 and o.code = 'GB' and c.name = 'Manchester'";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE o.codee = b.country1e and c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableNameOneSource() {
		String selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableName = "Select c.name, c.country, c.province, o.name, o.capital, b.country2 from city c, country o, borders b where c.country = o.code and o.code = b.country1 and (o.code = 'GB' or o.code = 'D')";
		String queryName = "queryName";
		Schema mondialEuropeXMLSchema = schemaRepository.getSchemaByName("MondialEuropeXML");

		DataSource mondialEuropeXMLDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
		logger.debug("mondialEuropeXMLDS: " + mondialEuropeXMLDS);

		Query query = new Query(queryName, selectSuperLexicalsFromJoinJoinWhereOrQueryWithVariableName);
		query.addSchema(mondialEuropeXMLSchema);
		query.addDataSource(mondialEuropeXMLDS);

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
		assertEquals(mondialEuropeXMLDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		String queryString = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				queryString);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e FROM countrye o, citye c, borderse b WHERE c.countrye = o.codee and o.codee = b.country1e and o.codee = 'GB' or o.codee = 'D';",
				evaluateExternallyOp.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinQueryTwoSources() {
		String selectStarFromJoinQuery = "Select * from countrye, ethnicgroupe where countrye.codee = ethnicgroupe.countrye";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialEuropeXML");
		Schema mondialLanguageEconomyReligionOfCountriesEuropeWRSchema = schemaRepository
				.getSchemaByName("MondialLanguageEconomyReligionOfCountriesEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository.getDataSourceWithSchemaName("MondialEuropeXML");
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

		String queryString2 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp2, null);

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

		String queryString1 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp1, null);

		assertEquals("SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe;", queryString1);
		assertEquals("SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe;",
				evaluateExternallyOp1.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectStarFromJoinWhereAndQueryTwoSources() {
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

		String queryString2 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp2, null);

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

		String queryString1 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp1, null);

		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				queryString1);
		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				evaluateExternallyOp1.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinWhereAndQueryTwoSources() {
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

		String queryString2 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp2, null);

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

		String queryString1 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp1, null);

		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				queryString1);
		assertEquals(
				"SELECT ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee FROM ethnicgroupe ethnicgroupe WHERE ethnicgroupe.namee = 'English';",
				evaluateExternallyOp1.getQueryString());
	}

	//@Test
	public void testTranslate2XQuerySelectSuperLexicalsFromJoinJoinJoinWhereAndQueryWithVariableNameTwoSources() {
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

		String queryString1 = localQueryTranslator2XQuery.translate2XQuery(evaluateScanBordersExternallyOp, null);

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

		String queryString2 = localQueryTranslator2XQuery.translate2XQuery(evaluateJoinCityCountryExternallyOp, null);

		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, "
						+ "o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
				queryString2);
		assertEquals(
				"SELECT c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, "
						+ "o.populatione FROM countrye o, citye c WHERE c.countrye = o.codee and c.namee = 'Manchester' and o.codee = 'GB';",
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

		String queryString3 = localQueryTranslator2XQuery.translate2XQuery(evaluateExternallyOp2, null);

		assertEquals("SELECT g.countrye, g.namee, g.percentagee FROM ethnicgroupe g;", queryString3);
		assertEquals("SELECT g.countrye, g.namee, g.percentagee FROM ethnicgroupe g;", evaluateExternallyOp2.getQueryString());
	}
}
