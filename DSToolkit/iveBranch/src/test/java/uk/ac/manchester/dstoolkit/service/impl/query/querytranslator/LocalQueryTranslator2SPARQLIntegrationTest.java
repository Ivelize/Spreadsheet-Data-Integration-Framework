package uk.ac.manchester.dstoolkit.service.impl.query.querytranslator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.util.Assert.isInstanceOf;

import org.antlr.runtime.tree.CommonTree;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SPARQLService;

/**
 * 
 * @author Klitos Christodoulou 
 */ 

public class LocalQueryTranslator2SPARQLIntegrationTest extends RDFAbstractIntegrationTest {

	private static Logger logger = Logger.getLogger(LocalQueryTranslator2SPARQLIntegrationTest.class);	
		
	@Autowired
	@Qualifier("localQueryTranslator2SPARQLService")
	private LocalQueryTranslator2SPARQLService localQueryTranslator2SPARQLService;
	
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
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;	
	
	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;
	
	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;

	/*Query 1*/
	@Test
	public void testTranslate2SPARQLSelectStarQueryOneSource1() {
		String selectStarFromQuery = "SELECT * FROM students ";
		String queryName = "sparlqQuery1";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
		
	}	

	/*Query 2*/
	@Test
	public void testTranslate2SPARQLSelectStarFromWhereQueryOneSource() {
		String selectStarFromQuery = "SELECT * FROM students S WHERE S.students_FirstName = 'Yolanda'";
		String queryName = "sparlqQuery2";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
		
	}
	
	/*Query 3: */
	@Test
	public void testTranslate2SPARQLSelectSuperLexicalsFromQueryOneSource() {
		String selectStarFromQuery = "SELECT S.students_FirstName, S.students_Email FROM students S";
		String queryName = "sparlqQuery3";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
	}//end testTranslate2SPARQLSelectStarFromWhereQueryOneSource()
		
	
	/*Query 4: */
	@Test
	public void testTranslate2SPARQLSelectSuperLexicalsFromWhere4() {
		String selectStarFromQuery = "SELECT students_FirstName FROM students WHERE students_FirstName = 'Yolanda'";
		String queryName = "sparlqQuery4";
			
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
					
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
			
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
			
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
			
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		//TODO: Uncommentn this
		//String queryString2 = evaluateExternallyOp.getQueryString();
		//assertEquals(queryString, queryString2);

		logger.debug("RESULT: " + queryString);		

	}
	
	/*Query 5:*/
	@Test
	public void testTranslate2SPARQLSelectStarFromWhereAndQueryOneSource5() {
		String selectStarFromQuery = "SELECT S.students_FirstName, S.students_Email FROM students S WHERE S.students_FirstName = 'Yolanda'";
		String queryName = "sparlqQuery5";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
	}//end testTranslate2SPARQLSelectStarFromWhereQueryOneSource()
	
	
	/*Query 6: Where clause variable does not exists*/
	@Test
	public void testTranslate2SPARQLSelectStarQueryOneSource6() {
		String selectStarFromQuery = "SELECT S.students_Email FROM students S WHERE S.students_FirstName = 'Yolanda'";
		String queryName = "sparlqQuery6";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
		
	}
	
	/*Query 7: AND*/
	@Test
	public void testTranslate2SPARQLSelectStarFromWhereAndQueryOneSource() {
		String selectStarFromQuery = "SELECT S.students_FirstName FROM students S WHERE S.students_FirstName = 'Yolanda' AND S.students_LastName = 'Gil'";
		String queryName = "sparlqQuery7";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
	}//end testTranslate2SPARQLSelectStarFromWhereQueryOneSource()
	
	
	/*Query 8: OR*/
	@Test
	public void testTranslate2SPARQLSelectStarFromWhereOrQueryOneSource() {
		String selectStarFromQuery = "SELECT S.students_FirstName FROM students S WHERE S.students_FirstName = 'Yolanda' OR S.students_LastName = 'Gil'";
		String queryName = "sparlqQuery8";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarQueryAst = parser.parseSQL(selectStarFromQuery);
		logger.debug("selectStarQueryAst: " + selectStarQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromQuery, selectStarQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);
	}//end testTranslate2SPARQLSelectStarFromWhereQueryOneSource()

	
	/*Query 10: JOIN*/	
	@Test
	public void testTranslate2SPARQLSelectStarFromJoinQueryOneSource() {
		String selectStarFromJoinQuery = "Select S.students_FirstName, M.modules_ModuleName from students S, modules M where S.enrol = M";
		String queryName = "sparlqQuery10";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarFromJoinWhereQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarQueryAst: " + selectStarFromJoinWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);		
	}	

	/*Query 10: JOIN*/	
	@Test
	public void testTranslate2SPARQLSelectStarFromJoinWhereAndQueryOneSource() {
		String selectStarFromJoinQuery = "Select S.students_FirstName, M.modules_ModuleName from students S, modules M where S.enrol = M AND M.modules_ModuleName = 'Operating Sys'";
		String queryName = "sparlqQuery11";
		
		//Check whether schema and DataSource exist
		Schema studentsRDFSchema = schemaRepository.getSchemaByName("studentsRDFSchema");
		logger.debug("studentsRDFSchema: " + studentsRDFSchema);
				
		DataSource studentsRDFSourceDS = dataSourceRepository
				.getDataSourceWithSchemaName("studentsRDFSchema");
		logger.debug("studentsRDFSourceDS: " + studentsRDFSourceDS);

		//Query Compiler - create & register query in a particular schema
		Query query = new Query(queryName, selectStarFromJoinQuery);
		query.addSchema(studentsRDFSchema);
		query.addDataSource(studentsRDFSourceDS);
		
		//Query Compiler - parse query & expand
		CommonTree selectStarFromJoinWhereQueryAst = parser.parseSQL(selectStarFromJoinQuery);
		logger.debug("selectStarQueryAst: " + selectStarFromJoinWhereQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromJoinQuery, selectStarFromJoinWhereQueryAst);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		
		//Testing
		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(studentsRDFSourceDS, rootOperator.getDataSource());
		
		//Get the root operator
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		//Call localQueryTranslator2SPARQLService.translate2SPARQL recursive method to do the translation
		String queryString = localQueryTranslator2SPARQLService.translate2SPARQL(evaluateExternallyOp, null);
		
		logger.debug("RESULT: " + queryString);		
	}

		
}//end class
