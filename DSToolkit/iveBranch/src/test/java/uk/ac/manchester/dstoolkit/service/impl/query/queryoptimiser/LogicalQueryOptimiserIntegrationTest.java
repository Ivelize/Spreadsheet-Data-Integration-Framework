package uk.ac.manchester.dstoolkit.service.impl.query.queryoptimiser;

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
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.ScanOperatorImpl;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.SetOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryparser.SQLQueryParserService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.GlobalQueryTranslatorService;

//@RunWith(SpringJUnit4ClassRunner.class)
public class LogicalQueryOptimiserIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(LogicalQueryOptimiserIntegrationTest.class);

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
	@Qualifier("superAbstractRepository")
	private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	//TODO add selection on borders

	/**
	 * @throws java.lang.Exception
	 */

	/*
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
	}
	*/

	//TODO check cardinalities

	//---------------------------------------------------------------------

	/**
	 * Test method for {@link uk.ac.manchester.dataspaces.service.impl.operators.query.queryoptimiser.LogicalQueryOptimiserServiceImpl#optimise(Query, Map)}.
	 */
	@Test
	public void testOptimiseSelectStarQueryOneSource() {
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

		assertEquals("citye", evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		//for (ResultField resultField : resultFields) {
		for (String resultFieldName : resultFieldNames) {
			//actualResultFieldNamesArray[i] = resultField.getFieldName();
			actualResultFieldNamesArray[i] = resultFieldName;
			//actualResultFieldTypesArray[i] = resultField.getFieldType();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			//actualCanonicalModelConstructNamesArray[i] = resultField.getCanonicalModelConstruct().getName();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			//isInstanceOf(SuperLexical.class, resultField.getCanonicalModelConstruct());
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			//actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultField.getCanonicalModelConstruct()).getSuperAbstract().getName();
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNotNull(evaluatorOperator1.getVariableName());
		assertEquals("citye", evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee",
				((ReduceOperatorImpl) evaluatorOperator1).getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione",
				"citye.longitudee", "citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType1 = evaluatorOperator1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		/*
		for (ResultField resultField : resultFields1) {
			actualResultFieldNamesArray1[i] = resultField.getFieldName();
			actualResultFieldTypesArray1[i] = resultField.getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultField.getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultField.getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultField.getCanonicalModelConstruct()).getSuperAbstract()
					.getName();
			i++;
		}
		*/
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFieldName;
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input1Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1Operator);
		assertEquals("citye", input1Operator.getVariableName());
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1Operator).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) input1Operator).getPredicates().size());

		assertNotNull(input1Operator.getResultType());
		ResultType resultType0 = input1Operator.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray0 = new String[6];
		DataType[] actualResultFieldTypesArray0 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray0 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		/*
		for (ResultField resultField : resultFields0) {
			actualResultFieldNamesArray0[i] = resultField.getFieldName();
			actualResultFieldTypesArray0[i] = resultField.getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultField.getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultField.getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultField.getCanonicalModelConstruct()).getSuperAbstract()
					.getName();
			i++;
		}
		*/
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFieldName;
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);
	}

	@Test
	public void testOptimiseSelectStarFromWhereQueryOneSource() {
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

		assertEquals("citye", evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFieldName;
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}
		/*
		for (ResultField resultField : resultFields) {
			actualResultFieldNamesArray[i] = resultField.getFieldName();
			actualResultFieldTypesArray[i] = resultField.getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultField.getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultField.getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultField.getCanonicalModelConstruct()).getSuperAbstract().getName();
			i++;
		}
		*/

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNotNull(evaluatorOperator1.getVariableName());
		assertEquals("citye", evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee",
				((ReduceOperatorImpl) evaluatorOperator1).getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione",
				"citye.longitudee", "citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType1 = evaluatorOperator1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFieldName;
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}
		/*
		for (ResultField resultField : resultFields1) {
			actualResultFieldNamesArray1[i] = resultField.getFieldName();
			actualResultFieldTypesArray1[i] = resultField.getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultField.getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultField.getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultField.getCanonicalModelConstruct()).getSuperAbstract()
					.getName();
			i++;
		}
		*/

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input1Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1Operator);
		assertEquals("citye", input1Operator.getVariableName());
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals("citye.countrye = 'GB'", ((ScanOperatorImpl) input1Operator).getReconcilingExpression());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input1Operator).getPredicates().size());
		Predicate predicate1 = ((ScanOperatorImpl) input1Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate1.getSuperLexical1().getName());
		assertEquals("citye", predicate1.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate1.getOperator());
		assertEquals("'GB'", predicate1.getLiteral2());
		assertNull(predicate1.getLiteral1());
		assertNull(predicate1.getSuperLexical2());

		assertNotNull(input1Operator.getResultType());
		ResultType resultType2 = input1Operator.getResultType();
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray2 = new String[6];
		DataType[] actualResultFieldTypesArray2 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray2 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields2 = resultType2.getResultFields();
		Set<String> resultFieldNames2 = resultFields2.keySet();
		for (String resultFieldName : resultFieldNames2) {
			actualResultFieldNamesArray2[i] = resultFieldName;
			actualResultFieldTypesArray2[i] = resultFields2.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray2[i] = resultFields2.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields2.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray2[i] = ((SuperLexical) resultFields2.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}
		/*
		for (ResultField resultField : resultFields2) {
			actualResultFieldNamesArray2[i] = resultField.getFieldName();
			actualResultFieldTypesArray2[i] = resultField.getFieldType();
			actualCanonicalModelConstructNamesArray2[i] = resultField.getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultField.getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray2[i] = ((SuperLexical) resultField.getCanonicalModelConstruct()).getSuperAbstract()
					.getName();
			i++;
		}
		*/

		assertArrayEquals(expectedResultFieldNamesArray2, actualResultFieldNamesArray2);
		assertArrayEquals(expectedResultFieldTypesArray2, actualResultFieldTypesArray2);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray2, actualCanonicalModelConstructNamesArray2);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray2, actualParentCanonicalModelConstructNamesArray2);
	}

	@Test
	public void testOptimiseSelectStarFromWhereAndQueryOneSource() {
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

		assertEquals("citye", evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNotNull(evaluatorOperator1.getVariableName());
		assertEquals("citye", evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee",
				((ReduceOperatorImpl) evaluatorOperator1).getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione",
				"citye.longitudee", "citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType1 = evaluatorOperator1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input1Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1Operator);
		assertEquals("citye", input1Operator.getVariableName());
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals("citye.countrye = 'GB' and citye.namee = 'Manchester'", ((ScanOperatorImpl) input1Operator).getReconcilingExpression());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		assertEquals(2, ((ScanOperatorImpl) input1Operator).getPredicates().size());

		Set<Predicate> predicates = ((ScanOperatorImpl) input1Operator).getPredicates();

		int k = 0;
		for (Predicate predicate : predicates) {
			if (k == 0) {
				//Predicate predicate1 = ((ScanOperatorImpl) input1Operator).getPredicates().get(0);
				assertEquals("countrye", predicate.getSuperLexical1().getName());
				assertEquals("citye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
				assertEquals("=", predicate.getOperator());
				assertEquals("'GB'", predicate.getLiteral2());
				assertNull(predicate.getLiteral1());
				assertNull(predicate.getSuperLexical2());
			} else if (k == 1) {
				//Predicate predicate2 = ((ScanOperatorImpl) input1Operator).getPredicates().get(1);
				assertEquals("namee", predicate.getSuperLexical1().getName());
				assertEquals("citye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
				assertEquals("=", predicate.getOperator());
				assertEquals("'Manchester'", predicate.getLiteral2());
				assertEquals("and", predicate.getAndOr());
				assertNull(predicate.getLiteral1());
				assertNull(predicate.getSuperLexical2());
			}
			k++;
		}

		assertNotNull(input1Operator.getResultType());
		ResultType resultType2 = input1Operator.getResultType();
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray2 = new String[6];
		DataType[] actualResultFieldTypesArray2 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray2 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[6];

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
	}

	@Test
	public void testOptimiseSelectSuperLexicalsFromWhereOrQueryOneSource() {
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

		assertEquals("citye", evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(4, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[4];
		DataType[] actualResultFieldTypesArray = new DataType[4];
		String[] actualCanonicalModelConstructNamesArray = new String[4];
		String[] actualParentCanonicalModelConstructNamesArray = new String[4];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNotNull(evaluatorOperator1.getVariableName());
		assertEquals("citye", evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(4, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, citye.populatione", ((ReduceOperatorImpl) evaluatorOperator1)
				.getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[4];
		String[] superAbstractNamesArray = new String[4];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[4];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType1 = evaluatorOperator1.getResultType();
		assertEquals(4, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[4];
		DataType[] actualResultFieldTypesArray1 = new DataType[4];
		String[] actualCanonicalModelConstructNamesArray1 = new String[4];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[4];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input1Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1Operator);
		assertEquals("citye", input1Operator.getVariableName());
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals("citye.countrye = 'GB' or citye.countrye = 'D'", ((ScanOperatorImpl) input1Operator).getReconcilingExpression());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		assertEquals(2, ((ScanOperatorImpl) input1Operator).getPredicates().size());

		Set<Predicate> predicates = ((ScanOperatorImpl) input1Operator).getPredicates();

		int k = 0;

		for (Predicate predicate : predicates) {
			if (k == 0) {
				//Predicate predicate1 = ((ScanOperatorImpl) input1Operator).getPredicates().get(0);
				assertEquals("countrye", predicate.getSuperLexical1().getName());
				assertEquals("citye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
				assertEquals("=", predicate.getOperator());
				assertEquals("'GB'", predicate.getLiteral2());
				assertNull(predicate.getLiteral1());
				assertNull(predicate.getSuperLexical2());
			} else if (k == 1) {
				//Predicate predicate2 = ((ScanOperatorImpl) input1Operator).getPredicates().get(1);
				assertEquals("countrye", predicate.getSuperLexical1().getName());
				assertEquals("citye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
				assertEquals("=", predicate.getOperator());
				assertEquals("'D'", predicate.getLiteral2());
				assertEquals("or", predicate.getAndOr());
				assertNull(predicate.getLiteral1());
				assertNull(predicate.getSuperLexical2());
			}
			k++;
		}

		assertNotNull(input1Operator.getResultType());
		ResultType resultType2 = input1Operator.getResultType();
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] expectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray2 = new String[6];
		DataType[] actualResultFieldTypesArray2 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray2 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[6];

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
		assertArrayEquals(expectedParentCanonicalModelConstructNamesArray2, actualParentCanonicalModelConstructNamesArray2);
	}

	@Test
	public void testOptimiseSelectSuperLexicalsFromJoinWhereAndQueryOneSource() {
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

		SuperAbstract citySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("citye", "MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract countrySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("countrye",
				"MondialCityProvinceCountryContinentEuropeWR");

		citySa.setCardinality(5000);
		countrySa.setCardinality(500);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		logger.debug("evaluateExternallyOp.getVariableName: " + evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(5, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "countrye.namee", "countrye.capitale" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "namee", "capitale" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray = new String[5];
		DataType[] actualResultFieldTypesArray = new DataType[5];
		String[] actualCanonicalModelConstructNamesArray = new String[5];
		String[] actualParentCanonicalModelConstructNamesArray = new String[5];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNull(evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(5, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, countrye.namee, countrye.capitale", ((ReduceOperatorImpl) evaluatorOperator1)
				.getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[5];
		String[] superAbstractNamesArray = new String[5];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "namee", "capitale" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "countrye", "countrye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[5];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "countrye.namee", "countrye.capitale" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType0 = evaluatorOperator1.getResultType();
		assertEquals(5, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "citye.namee", "citye.countrye", "citye.provincee", "countrye.namee", "countrye.capitale" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "countrye", "provincee", "namee", "capitale" };
		String[] expectedParentCanonicalModelConstructNamesArray0 = { "citye", "citye", "citye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray0 = new String[5];
		DataType[] actualResultFieldTypesArray0 = new DataType[5];
		String[] actualCanonicalModelConstructNamesArray0 = new String[5];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[5];

		int j = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[j] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[j] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[j] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[j] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			j++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(expectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input1Operator = evaluatorOperator1.getLhsInput();

		logger.debug("input1Operator.getVariableName(): " + input1Operator.getVariableName());

		isInstanceOf(JoinOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals("citye.countrye = countrye.codee", ((JoinOperatorImpl) input1Operator).getReconcilingExpression());
		assertNotNull(input1Operator.getLhsInput());
		assertNotNull(input1Operator.getRhsInput());
		assertEquals(input1Operator.getInput(), input1Operator.getLhsInput());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input1Operator).getPredicates().size());
		Predicate predicate = ((JoinOperatorImpl) input1Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate.getSuperLexical1().getName());
		assertEquals("citye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate.getOperator());
		assertEquals("codee", predicate.getSuperLexical2().getName());
		assertEquals("countrye", predicate.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate.getLiteral1());
		assertNull(predicate.getLiteral2());

		assertNotNull(input1Operator.getResultType());
		ResultType resultType1 = input1Operator.getResultType();
		assertEquals(12, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee", "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee",
				"codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray1 = new String[12];
		DataType[] actualResultFieldTypesArray1 = new DataType[12];
		String[] actualCanonicalModelConstructNamesArray1 = new String[12];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[12];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input11Operator = input1Operator.getLhsInput();
		isInstanceOf(ScanOperatorImpl.class, input11Operator);
		assertEquals("citye", input11Operator.getVariableName());
		assertNull(input11Operator.getLhsInput());
		assertNull(input11Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input11Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("citye.namee = 'Manchester'", ((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) input11Operator).getAndOr());

		assertNotNull(input11Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input11Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input11Operator).getPredicates().size());
		Predicate predicate1 = ((ScanOperatorImpl) input11Operator).getPredicates().iterator().next();
		assertEquals("namee", predicate1.getSuperLexical1().getName());
		assertEquals("citye", predicate1.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate1.getOperator());
		assertEquals("'Manchester'", predicate1.getLiteral2());
		assertEquals("and", predicate1.getAndOr());
		assertNull(predicate1.getLiteral1());
		assertNull(predicate1.getSuperLexical2());

		assertNotNull(input11Operator.getResultType());
		ResultType resultType2 = input11Operator.getResultType();
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray2 = new String[6];
		DataType[] actualResultFieldTypesArray2 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray2 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[6];

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

		EvaluatorOperator input12Operator = input1Operator.getRhsInput();
		isInstanceOf(ScanOperatorImpl.class, input12Operator);
		assertEquals("countrye", input12Operator.getVariableName());
		assertNull(input12Operator.getLhsInput());
		assertNull(input12Operator.getRhsInput());
		assertEquals("countrye", ((ScanOperatorImpl) input12Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("countrye.codee = 'GB'", ((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) input12Operator).getAndOr());

		assertNotNull(input12Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input12Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input12Operator).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) input12Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate2.getSuperLexical1().getName());
		assertEquals("countrye", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'GB'", predicate2.getLiteral2());
		assertEquals("and", predicate2.getAndOr());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(input12Operator.getResultType());
		ResultType resultType3 = input12Operator.getResultType();
		assertEquals(6, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray3 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray3 = new String[6];
		DataType[] actualResultFieldTypesArray3 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray3 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);
	}

	@Test
	public void testOptimiseSelectSuperLexicalsFromJoinJoinWhereAndQueryWithVariableNameOneSource() {
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

		SuperAbstract citySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("citye", "MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract countrySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("countrye",
				"MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract bordersSa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("borderse",
				"MondialCityProvinceCountryContinentEuropeWR");

		citySa.setCardinality(5000);
		countrySa.setCardinality(500);
		bordersSa.setCardinality(2000);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		assertNull(evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "c.namee", "c.countrye", "c.provincee", "o.namee", "o.capitale", "b.country2e" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING,
				DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "namee", "capitale", "country2e" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "countrye", "countrye", "borderse" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNull(evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("c.namee, c.countrye, c.provincee, o.namee, o.capitale, b.country2e", ((ReduceOperatorImpl) evaluatorOperator1)
				.getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "namee", "capitale", "country2e" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "countrye", "countrye", "borderse" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "c.namee", "c.countrye", "c.provincee", "o.namee", "o.capitale", "b.country2e" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType1 = evaluatorOperator1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "c.namee", "c.countrye", "c.provincee", "o.namee", "o.capitale", "b.country2e" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING,
				DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "namee", "capitale", "country2e" };
		String[] expectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "countrye", "countrye", "borderse" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

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

		EvaluatorOperator input0Operator = evaluatorOperator1.getLhsInput();
		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("o.codee = b.country1e", ((JoinOperatorImpl) input0Operator).getReconcilingExpression()); //TODO decide whether it should be the variable name here instead
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNotNull(input0Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input0Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input0Operator).getPredicates().size());
		Predicate predicate0 = ((JoinOperatorImpl) input0Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate0.getSuperLexical1().getName());
		assertEquals("countrye", predicate0.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate0.getOperator());
		assertEquals("country1e", predicate0.getSuperLexical2().getName());
		assertEquals("borderse", predicate0.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate0.getLiteral1());
		assertNull(predicate0.getLiteral2());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType2 = input0Operator.getResultType();
		assertEquals(15, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione", "b.country1e", "b.country2e", "b.lengthe" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER,
				DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee",
				"codee", "capitale", "provincee", "areae", "populatione", "country1e", "country2e", "lengthe" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye", "borderse", "borderse", "borderse" };

		String[] actualResultFieldNamesArray2 = new String[15];
		DataType[] actualResultFieldTypesArray2 = new DataType[15];
		String[] actualCanonicalModelConstructNamesArray2 = new String[15];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[15];

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

		EvaluatorOperator input1Operator = input0Operator.getLhsInput();
		isInstanceOf(JoinOperatorImpl.class, input1Operator);
		assertNotNull(input1Operator.getVariableName());
		assertEquals("o", input1Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals("c.countrye = o.codee", ((JoinOperatorImpl) input1Operator).getReconcilingExpression()); //TODO decide whether it should be the variable name here instead
		assertNotNull(input1Operator.getLhsInput());
		assertNotNull(input1Operator.getRhsInput());
		assertEquals(input1Operator.getInput(), input1Operator.getLhsInput());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input1Operator).getPredicates().size());
		Predicate predicate1 = ((JoinOperatorImpl) input1Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate1.getSuperLexical1().getName());
		assertEquals("citye", predicate1.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate1.getOperator());
		assertEquals("codee", predicate1.getSuperLexical2().getName());
		assertEquals("countrye", predicate1.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate1.getLiteral1());
		assertNull(predicate1.getLiteral2());

		assertNotNull(input1Operator.getResultType());
		ResultType resultType3 = input1Operator.getResultType();
		assertEquals(12, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray3 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee",
				"codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray3 = new String[12];
		DataType[] actualResultFieldTypesArray3 = new DataType[12];
		String[] actualCanonicalModelConstructNamesArray3 = new String[12];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[12];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);

		EvaluatorOperator input2Operator = input0Operator.getRhsInput();
		isInstanceOf(ScanOperatorImpl.class, input2Operator);
		assertNotNull(input2Operator.getVariableName());
		assertEquals("b", input2Operator.getVariableName());
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertEquals("borderse", ((ScanOperatorImpl) input2Operator).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) input2Operator).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) input2Operator).getPredicates().size());

		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		assertNotNull(input2Operator.getResultType());
		ResultType resultType4 = input2Operator.getResultType();
		assertEquals(3, resultType4.getResultFields().size());
		String[] expectedResultFieldNamesArray4 = { "b.country1e", "b.country2e", "b.lengthe" };
		DataType[] expectedResultFieldTypesArray4 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray4 = { "country1e", "country2e", "lengthe" };
		String[] exptectedParentCanonicalModelConstructNamesArray4 = { "borderse", "borderse", "borderse" };

		String[] actualResultFieldNamesArray4 = new String[3];
		DataType[] actualResultFieldTypesArray4 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray4 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray4 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields4 = resultType4.getResultFields();
		Set<String> resultFieldNames4 = resultFields4.keySet();
		for (String resultFieldName : resultFieldNames4) {
			actualResultFieldNamesArray4[i] = resultFields4.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray4[i] = resultFields4.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray4[i] = resultFields4.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields4.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray4[i] = ((SuperLexical) resultFields4.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray4, actualResultFieldNamesArray4);
		assertArrayEquals(expectedResultFieldTypesArray4, actualResultFieldTypesArray4);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray4, actualCanonicalModelConstructNamesArray4);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray4, actualParentCanonicalModelConstructNamesArray4);

		EvaluatorOperator input11Operator = input1Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input11Operator);
		assertNotNull(input11Operator.getVariableName());
		assertEquals("c", input11Operator.getVariableName());
		assertNull(input11Operator.getLhsInput());
		assertNull(input11Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input11Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("c.namee = 'Manchester'", ((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) input11Operator).getAndOr());

		assertNotNull(input11Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input11Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input11Operator).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) input11Operator).getPredicates().iterator().next();
		assertEquals("namee", predicate2.getSuperLexical1().getName());
		assertEquals("citye", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'Manchester'", predicate2.getLiteral2());
		assertEquals("and", predicate2.getAndOr());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(input11Operator.getResultType());
		ResultType resultType5 = input11Operator.getResultType();
		assertEquals(6, resultType5.getResultFields().size());
		String[] expectedResultFieldNamesArray5 = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee" };
		DataType[] expectedResultFieldTypesArray5 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray5 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray5 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray5 = new String[6];
		DataType[] actualResultFieldTypesArray5 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray5 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray5 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields5 = resultType5.getResultFields();
		Set<String> resultFieldNames5 = resultFields5.keySet();
		for (String resultFieldName : resultFieldNames5) {
			actualResultFieldNamesArray5[i] = resultFields5.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray5[i] = resultFields5.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray5[i] = resultFields5.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields5.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray5[i] = ((SuperLexical) resultFields5.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray5, actualResultFieldNamesArray5);
		assertArrayEquals(expectedResultFieldTypesArray5, actualResultFieldTypesArray5);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray5, actualCanonicalModelConstructNamesArray5);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray5, actualParentCanonicalModelConstructNamesArray5);

		EvaluatorOperator input12Operator = input1Operator.getRhsInput();

		isInstanceOf(ScanOperatorImpl.class, input12Operator);
		assertNotNull(input12Operator.getVariableName());
		assertEquals("o", input12Operator.getVariableName());
		assertNull(input12Operator.getLhsInput());
		assertNull(input12Operator.getRhsInput());
		assertEquals("countrye", ((ScanOperatorImpl) input12Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("o.codee = 'GB'", ((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) input12Operator).getAndOr());

		assertNotNull(input12Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input12Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input12Operator).getPredicates().size());
		Predicate predicate3 = ((ScanOperatorImpl) input12Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate3.getSuperLexical1().getName());
		assertEquals("countrye", predicate3.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate3.getOperator());
		assertEquals("'GB'", predicate3.getLiteral2());
		assertEquals("and", predicate3.getAndOr());
		assertNull(predicate3.getLiteral1());
		assertNull(predicate3.getSuperLexical2());

		assertNotNull(input12Operator.getResultType());
		ResultType resultType6 = input12Operator.getResultType();
		assertEquals(6, resultType6.getResultFields().size());
		String[] expectedResultFieldNamesArray6 = { "o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		DataType[] expectedResultFieldTypesArray6 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray6 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray6 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray6 = new String[6];
		DataType[] actualResultFieldTypesArray6 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray6 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray6 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields6 = resultType6.getResultFields();
		Set<String> resultFieldNames6 = resultFields6.keySet();
		for (String resultFieldName : resultFieldNames6) {
			actualResultFieldNamesArray6[i] = resultFields6.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray6[i] = resultFields6.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray6[i] = resultFields6.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields6.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray6[i] = ((SuperLexical) resultFields6.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray6, actualResultFieldNamesArray6);
		assertArrayEquals(expectedResultFieldTypesArray6, actualResultFieldTypesArray6);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray6, actualCanonicalModelConstructNamesArray6);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray6, actualParentCanonicalModelConstructNamesArray6);
	}

	@Test
	public void testOptimiseSelectStarSubqueryQueryOneSource() {
		String selectStarSubqueryQuery = "Select * from citye where citye.namee in (select citye.namee from citye where citye.countrye = 'GB')";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarSubqueryQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarSubqueryQueryAst = parser.parseSQL(selectStarSubqueryQuery);
		logger.debug("selectStarSubqueryQueryAst: " + selectStarSubqueryQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarSubqueryQuery, selectStarSubqueryQueryAst);

		SuperAbstract citySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("citye", "MondialCityProvinceCountryContinentEuropeWR");

		citySa.setCardinality(5000);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		logger.debug("evaluateExternallyOp.getVariableName: " + evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(7, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee", "subquery1.citye.namee" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[7];
		DataType[] actualResultFieldTypesArray = new DataType[7];
		String[] actualCanonicalModelConstructNamesArray = new String[7];
		String[] actualParentCanonicalModelConstructNamesArray = new String[7];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNull(evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(7, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee, subquery1.citye.namee",
				((ReduceOperatorImpl) evaluatorOperator1).getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[7];
		String[] superAbstractNamesArray = new String[7];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[7];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione",
				"citye.longitudee", "citye.latitudee", "subquery1.citye.namee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType1 = evaluatorOperator1.getResultType();
		assertEquals(7, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee", "subquery1.citye.namee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee" };
		String[] expectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[7];
		DataType[] actualResultFieldTypesArray1 = new DataType[7];
		String[] actualCanonicalModelConstructNamesArray1 = new String[7];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[7];

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

		EvaluatorOperator input0Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("citye.namee = subquery1.citye.namee", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input0Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input0Operator).getPredicates().size());
		Predicate predicate = ((JoinOperatorImpl) input0Operator).getPredicates().iterator().next();
		assertEquals("namee", predicate.getSuperLexical1().getName());
		assertEquals("citye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate.getOperator());
		assertEquals("namee", predicate.getSuperLexical2().getName());
		assertEquals("citye", predicate.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate.getLiteral1());
		assertNull(predicate.getLiteral2());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType0 = input0Operator.getResultType();
		assertEquals(7, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee", "subquery1.citye.namee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "citye", "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray0 = new String[7];
		DataType[] actualResultFieldTypesArray0 = new DataType[7];
		String[] actualCanonicalModelConstructNamesArray0 = new String[7];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[7];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input1Operator = input0Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1Operator);
		assertEquals("citye", input1Operator.getVariableName());
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1Operator).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) input1Operator).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) input1Operator).getPredicates().size());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		assertNotNull(input1Operator.getResultType());
		ResultType resultType10 = input1Operator.getResultType();
		assertEquals(6, resultType10.getResultFields().size());
		String[] expectedResultFieldNamesArray10 = { "citye.namee", "citye.countrye", "citye.provincee", "citye.populatione", "citye.longitudee",
				"citye.latitudee" };
		DataType[] expectedResultFieldTypesArray10 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray10 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray10 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray10 = new String[6];
		DataType[] actualResultFieldTypesArray10 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray10 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray10 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields10 = resultType10.getResultFields();
		Set<String> resultFieldNames10 = resultFields10.keySet();
		for (String resultFieldName : resultFieldNames10) {
			actualResultFieldNamesArray10[i] = resultFields10.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray10[i] = resultFields10.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray10[i] = resultFields10.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields10.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray10[i] = ((SuperLexical) resultFields10.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray10, actualResultFieldNamesArray10);
		assertArrayEquals(expectedResultFieldTypesArray10, actualResultFieldTypesArray10);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray10, actualCanonicalModelConstructNamesArray10);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray10, actualParentCanonicalModelConstructNamesArray10);

		EvaluatorOperator input2Operator = input0Operator.getRhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input2Operator);
		assertEquals("subquery1", input2Operator.getVariableName());
		assertNotNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(((ReduceOperatorImpl) input2Operator).getReconcilingExpression());
		assertEquals("subquery1.citye.namee", ((ReduceOperatorImpl) input2Operator).getReconcilingExpression());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		assertNotNull(input2Operator.getResultType());
		ResultType resultType2 = input2Operator.getResultType();
		assertEquals(1, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "subquery1.citye.namee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye" };

		String[] actualResultFieldNamesArray2 = new String[1];
		DataType[] actualResultFieldTypesArray2 = new DataType[1];
		String[] actualCanonicalModelConstructNamesArray2 = new String[1];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[1];

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

		EvaluatorOperator input21Operator = input2Operator.getLhsInput();

		//TODO carry on from here

		isInstanceOf(ScanOperatorImpl.class, input21Operator);
		assertEquals("subquery1.citye", input21Operator.getVariableName());
		assertNull(input21Operator.getLhsInput());
		assertNull(input21Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input21Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input21Operator).getReconcilingExpression());
		assertEquals("subquery1.citye.countrye = 'GB'", ((ScanOperatorImpl) input21Operator).getReconcilingExpression());
		assertNotNull(input21Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input21Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input21Operator).getPredicates().size());
		Predicate predicate21 = ((ScanOperatorImpl) input21Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate21.getSuperLexical1().getName());
		assertEquals("citye", predicate21.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate21.getOperator());
		assertEquals("'GB'", predicate21.getLiteral2());
		assertNull(predicate21.getLiteral1());
		assertNull(predicate21.getSuperLexical2());

		assertNotNull(input21Operator.getResultType());
		ResultType resultType3 = input21Operator.getResultType();
		assertEquals(6, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "subquery1.citye.namee", "subquery1.citye.countrye", "subquery1.citye.provincee",
				"subquery1.citye.populatione", "subquery1.citye.longitudee", "subquery1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray3 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray3 = new String[6];
		DataType[] actualResultFieldTypesArray3 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray3 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);
	}

	@Test
	public void testOptimiseSelectStarFromWhereUnionQueryOneSource() {
		String selectStarFromWhereUnionQuery = "Select * from citye where citye.countrye = 'GB' union select * from citye where citye.countrye = 'D'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereUnionQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereUnionQueryAst = parser.parseSQL(selectStarFromWhereUnionQuery);
		logger.debug("selectStarFromWhereUnionQueryAst: " + selectStarFromWhereUnionQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereUnionQuery, selectStarFromWhereUnionQueryAst);

		SuperAbstract citySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("citye", "MondialCityProvinceCountryContinentEuropeWR");

		citySa.setCardinality(5000);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		logger.debug("evaluateExternallyOp.getVariableName: " + evaluateExternallyOp.getVariableName());
		assertNotNull(evaluateExternallyOp.getVariableName());
		assertEquals("union1_union2", evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNotNull(evaluatorOperator1.getVariableName());
		assertEquals("union1_union2", evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals(
				"union1.citye.namee, union1.citye.countrye, union1.citye.provincee, union1.citye.populatione, union1.citye.longitudee, union1.citye.latitudee",
				((ReduceOperatorImpl) evaluatorOperator1).getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType0 = evaluatorOperator1.getResultType();
		assertEquals(6, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray0 = new String[6];
		DataType[] actualResultFieldTypesArray0 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray0 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input0Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(SetOperatorImpl.class, input0Operator);
		assertNotNull(input0Operator.getVariableName());
		assertEquals("union1_union2", input0Operator.getVariableName());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());
		assertNull(((SetOperatorImpl) input0Operator).getReconcilingExpression());

		assertNotNull(input0Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input0Operator.getDataSource());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType12 = input0Operator.getResultType();
		assertEquals(6, resultType12.getResultFields().size());
		String[] expectedResultFieldNamesArray12 = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray12 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray12 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray12 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray12 = new String[6];
		DataType[] actualResultFieldTypesArray12 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray12 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray12 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields12 = resultType12.getResultFields();
		Set<String> resultFieldNames12 = resultFields12.keySet();
		for (String resultFieldName : resultFieldNames12) {
			actualResultFieldNamesArray12[i] = resultFields12.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray12[i] = resultFields12.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray12[i] = resultFields12.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields12.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray12[i] = ((SuperLexical) resultFields12.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray12, actualResultFieldNamesArray12);
		assertArrayEquals(expectedResultFieldTypesArray12, actualResultFieldTypesArray12);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray12, actualCanonicalModelConstructNamesArray12);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray12, actualParentCanonicalModelConstructNamesArray12);

		EvaluatorOperator input1Operator = input0Operator.getLhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input1Operator);
		assertNotNull(input1Operator.getVariableName());
		assertEquals("union1", input1Operator.getVariableName());
		assertNotNull(input1Operator.getInput());
		assertNotNull(input1Operator.getLhsInput());
		assertEquals(input1Operator.getInput(), input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) input1Operator).getSuperLexicals().size());
		assertEquals(
				"union1.citye.namee, union1.citye.countrye, union1.citye.provincee, union1.citye.populatione, union1.citye.longitudee, union1.citye.latitudee",
				((ReduceOperatorImpl) input1Operator).getReconcilingExpression());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		Collection<SuperLexical> superLexicals10 = ((ReduceOperatorImpl) input1Operator).getSuperLexicals().values();
		String[] superLexicalNamesArray10 = new String[6];
		String[] superAbstractNamesArray10 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals10) {
			superLexicalNamesArray10[i] = superLexical.getName();
			superAbstractNamesArray10[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray10 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray10, superLexicalNamesArray10);
		String[] expectedSuperAbstractNamesArray10 = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray10, superAbstractNamesArray10);
		Set<String> superLexicalVariableNames10 = ((ReduceOperatorImpl) input1Operator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray10 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames10) {
			superLexicalVariableNamesArray10[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray10 = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray10, superLexicalVariableNamesArray10);

		assertNotNull(input1Operator.getResultType());
		ResultType resultType10 = input1Operator.getResultType();
		assertEquals(6, resultType10.getResultFields().size());
		String[] expectedResultFieldNamesArray10 = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray10 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray10 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray10 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray10 = new String[6];
		DataType[] actualResultFieldTypesArray10 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray10 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray10 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields10 = resultType10.getResultFields();
		Set<String> resultFieldNames10 = resultFields10.keySet();
		for (String resultFieldName : resultFieldNames10) {
			actualResultFieldNamesArray10[i] = resultFields10.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray10[i] = resultFields10.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray10[i] = resultFields10.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields10.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray10[i] = ((SuperLexical) resultFields10.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray10, actualResultFieldNamesArray10);
		assertArrayEquals(expectedResultFieldTypesArray10, actualResultFieldTypesArray10);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray10, actualCanonicalModelConstructNamesArray10);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray10, actualParentCanonicalModelConstructNamesArray10);

		EvaluatorOperator input11Operator = input1Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input11Operator);
		assertEquals("union1.citye", input11Operator.getVariableName());
		assertNull(input11Operator.getLhsInput());
		assertNull(input11Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input11Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("union1.citye.countrye = 'GB'", ((ScanOperatorImpl) input11Operator).getReconcilingExpression());

		assertNotNull(input11Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input11Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input11Operator).getPredicates().size());
		Predicate predicate1 = ((ScanOperatorImpl) input11Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate1.getSuperLexical1().getName());
		assertEquals("citye", predicate1.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate1.getOperator());
		assertEquals("'GB'", predicate1.getLiteral2());
		assertNull(predicate1.getLiteral1());
		assertNull(predicate1.getSuperLexical2());

		assertNotNull(input11Operator.getResultType());
		ResultType resultType1 = input11Operator.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "union1.citye.namee", "union1.citye.countrye", "union1.citye.provincee",
				"union1.citye.populatione", "union1.citye.longitudee", "union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input2Operator = input0Operator.getRhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input2Operator);
		assertNotNull(input2Operator.getVariableName());
		assertEquals("union2", input2Operator.getVariableName());
		assertNotNull(input2Operator.getInput());
		assertNotNull(input2Operator.getLhsInput());
		assertEquals(input2Operator.getInput(), input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) input2Operator).getSuperLexicals().size());
		assertEquals(
				"union2.citye.namee, union2.citye.countrye, union2.citye.provincee, union2.citye.populatione, union2.citye.longitudee, union2.citye.latitudee",
				((ReduceOperatorImpl) input2Operator).getReconcilingExpression());

		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		Collection<SuperLexical> superLexicals20 = ((ReduceOperatorImpl) input2Operator).getSuperLexicals().values();
		String[] superLexicalNamesArray20 = new String[6];
		String[] superAbstractNamesArray20 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals20) {
			superLexicalNamesArray20[i] = superLexical.getName();
			superAbstractNamesArray20[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray20 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray20, superLexicalNamesArray20);
		String[] expectedSuperAbstractNamesArray20 = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray20, superAbstractNamesArray20);
		Set<String> superLexicalVariableNames20 = ((ReduceOperatorImpl) input2Operator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray20 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames20) {
			superLexicalVariableNamesArray20[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray20 = { "union2.citye.namee", "union2.citye.countrye", "union2.citye.provincee",
				"union2.citye.populatione", "union2.citye.longitudee", "union2.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray20, superLexicalVariableNamesArray20);

		assertNotNull(input2Operator.getResultType());
		ResultType resultType20 = input2Operator.getResultType();
		assertEquals(6, resultType20.getResultFields().size());
		String[] expectedResultFieldNamesArray20 = { "union2.citye.namee", "union2.citye.countrye", "union2.citye.provincee",
				"union2.citye.populatione", "union2.citye.longitudee", "union2.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray20 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray20 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray20 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray20 = new String[6];
		DataType[] actualResultFieldTypesArray20 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray20 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray20 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields20 = resultType20.getResultFields();
		Set<String> resultFieldNames20 = resultFields20.keySet();
		for (String resultFieldName : resultFieldNames20) {
			actualResultFieldNamesArray20[i] = resultFields20.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray20[i] = resultFields20.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray20[i] = resultFields20.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields20.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray20[i] = ((SuperLexical) resultFields20.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray20, actualResultFieldNamesArray20);
		assertArrayEquals(expectedResultFieldTypesArray20, actualResultFieldTypesArray20);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray20, actualCanonicalModelConstructNamesArray20);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray20, actualParentCanonicalModelConstructNamesArray20);

		EvaluatorOperator input12Operator = input2Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input12Operator);
		assertEquals("union2.citye", input12Operator.getVariableName());
		assertNull(input12Operator.getLhsInput());
		assertNull(input12Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input12Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("union2.citye.countrye = 'D'", ((ScanOperatorImpl) input12Operator).getReconcilingExpression());

		assertNotNull(input12Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input12Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input12Operator).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) input12Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate2.getSuperLexical1().getName());
		assertEquals("citye", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'D'", predicate2.getLiteral2());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(input12Operator.getResultType());
		ResultType resultType2 = input12Operator.getResultType();
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "union2.citye.namee", "union2.citye.countrye", "union2.citye.provincee",
				"union2.citye.populatione", "union2.citye.longitudee", "union2.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray2 = new String[6];
		DataType[] actualResultFieldTypesArray2 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray2 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[6];

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
	}

	@Test
	public void testOptimiseSelectStarFromWhereUnionUnionQueryOneSource() {
		String selectStarFromWhereUnionUnionQuery = "Select * from citye where citye.countrye = 'GB' union select * from citye where citye.countrye = 'D' union select * from citye where citye.countrye = 'IRL'";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query = new Query(queryName, selectStarFromWhereUnionUnionQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		CommonTree selectStarFromWhereUnionUnionQueryAst = parser.parseSQL(selectStarFromWhereUnionUnionQuery);
		logger.debug("selectStarFromWhereUnionQueryAst: " + selectStarFromWhereUnionUnionQueryAst.toStringTree());
		query = globalTranslator.translateAstIntoQuery(query, selectStarFromWhereUnionUnionQuery, selectStarFromWhereUnionUnionQueryAst);

		SuperAbstract citySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("citye", "MondialCityProvinceCountryContinentEuropeWR");

		citySa.setCardinality(5000);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		assertNotNull(rootOperator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;

		logger.debug("evaluateExternallyOp.getVariableName: " + evaluateExternallyOp.getVariableName());
		assertNotNull(evaluateExternallyOp.getVariableName());
		assertEquals("union1.union1_union2.union1_union2", evaluateExternallyOp.getVariableName());
		assertNull(evaluateExternallyOp.getLhsInput());
		assertNull(evaluateExternallyOp.getRhsInput());

		assertNotNull(evaluateExternallyOp.getResultType());
		ResultType resultType = evaluateExternallyOp.getResultType();
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		int i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator evaluatorOperator1 = evaluateExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator1);
		assertNotNull(evaluatorOperator1.getVariableName());
		assertEquals("union1.union1_union2.union1_union2", evaluatorOperator1.getVariableName());
		assertNotNull(evaluatorOperator1.getInput());
		assertNotNull(evaluatorOperator1.getLhsInput());
		assertEquals(evaluatorOperator1.getInput(), evaluatorOperator1.getLhsInput());
		assertNull(evaluatorOperator1.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().size());
		assertEquals(
				"union1.union1.citye.namee, union1.union1.citye.countrye, union1.union1.citye.provincee, union1.union1.citye.populatione, union1.union1.citye.longitudee, union1.union1.citye.latitudee",
				((ReduceOperatorImpl) evaluatorOperator1).getReconcilingExpression());

		assertNotNull(evaluatorOperator1.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator1.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) evaluatorOperator1).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "union1.union1.citye.namee", "union1.union1.citye.countrye",
				"union1.union1.citye.provincee", "union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(evaluatorOperator1.getResultType());
		ResultType resultType0 = evaluatorOperator1.getResultType();
		assertEquals(6, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray0 = new String[6];
		DataType[] actualResultFieldTypesArray0 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray0 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input0Operator = evaluatorOperator1.getLhsInput();

		isInstanceOf(SetOperatorImpl.class, input0Operator);
		assertNotNull(input0Operator.getVariableName());
		assertEquals("union1.union1_union2.union1_union2", input0Operator.getVariableName());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());
		assertNull(((SetOperatorImpl) input0Operator).getReconcilingExpression());

		assertNotNull(input0Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input0Operator.getDataSource());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType12 = input0Operator.getResultType();
		assertEquals(6, resultType12.getResultFields().size());
		String[] expectedResultFieldNamesArray12 = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray12 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray12 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray12 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray12 = new String[6];
		DataType[] actualResultFieldTypesArray12 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray12 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray12 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields12 = resultType12.getResultFields();
		Set<String> resultFieldNames12 = resultFields12.keySet();
		for (String resultFieldName : resultFieldNames12) {
			actualResultFieldNamesArray12[i] = resultFields12.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray12[i] = resultFields12.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray12[i] = resultFields12.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields12.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray12[i] = ((SuperLexical) resultFields12.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray12, actualResultFieldNamesArray12);
		assertArrayEquals(expectedResultFieldTypesArray12, actualResultFieldTypesArray12);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray12, actualCanonicalModelConstructNamesArray12);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray12, actualParentCanonicalModelConstructNamesArray12);

		EvaluatorOperator input1Operator = input0Operator.getLhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input1Operator);
		assertNotNull(input1Operator.getVariableName());
		assertEquals("union1.union1_union2.union1", input1Operator.getVariableName());
		assertNotNull(input1Operator.getInput());
		assertNotNull(input1Operator.getLhsInput());
		assertEquals(input1Operator.getInput(), input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) input1Operator).getSuperLexicals().size());
		assertEquals(
				"union1.union1.citye.namee, union1.union1.citye.countrye, union1.union1.citye.provincee, union1.union1.citye.populatione, union1.union1.citye.longitudee, union1.union1.citye.latitudee",
				((ReduceOperatorImpl) input1Operator).getReconcilingExpression());

		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		Collection<SuperLexical> superLexicals10 = ((ReduceOperatorImpl) input1Operator).getSuperLexicals().values();
		String[] superLexicalNamesArray10 = new String[6];
		String[] superAbstractNamesArray10 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals10) {
			superLexicalNamesArray10[i] = superLexical.getName();
			superAbstractNamesArray10[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray10 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray10, superLexicalNamesArray10);
		String[] expectedSuperAbstractNamesArray10 = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray10, superAbstractNamesArray10);
		Set<String> superLexicalVariableNames10 = ((ReduceOperatorImpl) input1Operator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray10 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames10) {
			superLexicalVariableNamesArray10[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray10 = { "union1.union1.citye.namee", "union1.union1.citye.countrye",
				"union1.union1.citye.provincee", "union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray10, superLexicalVariableNamesArray10);

		assertNotNull(input1Operator.getResultType());
		ResultType resultType10 = input1Operator.getResultType();
		assertEquals(6, resultType10.getResultFields().size());
		String[] expectedResultFieldNamesArray10 = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray10 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray10 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray10 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray10 = new String[6];
		DataType[] actualResultFieldTypesArray10 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray10 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray10 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields10 = resultType10.getResultFields();
		Set<String> resultFieldNames10 = resultFields10.keySet();
		for (String resultFieldName : resultFieldNames10) {
			actualResultFieldNamesArray10[i] = resultFields10.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray10[i] = resultFields10.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray10[i] = resultFields10.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields10.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray10[i] = ((SuperLexical) resultFields10.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray10, actualResultFieldNamesArray10);
		assertArrayEquals(expectedResultFieldTypesArray10, actualResultFieldTypesArray10);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray10, actualCanonicalModelConstructNamesArray10);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray10, actualParentCanonicalModelConstructNamesArray10);

		EvaluatorOperator input11Operator = input1Operator.getLhsInput();

		isInstanceOf(SetOperatorImpl.class, input11Operator);
		assertNotNull(input11Operator.getVariableName());
		assertEquals("union1.union1_union2.union1", input11Operator.getVariableName());
		assertNotNull(input11Operator.getLhsInput());
		assertNotNull(input11Operator.getRhsInput());
		assertEquals(input11Operator.getInput(), input11Operator.getLhsInput());
		assertNull(((SetOperatorImpl) input11Operator).getReconcilingExpression());

		assertNotNull(input11Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input11Operator.getDataSource());

		assertNotNull(input11Operator.getResultType());
		ResultType resultType110 = input11Operator.getResultType();
		assertEquals(6, resultType110.getResultFields().size());
		String[] expectedResultFieldNamesArray110 = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray110 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray110 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray110 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray110 = new String[6];
		DataType[] actualResultFieldTypesArray110 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray110 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray110 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields110 = resultType110.getResultFields();
		Set<String> resultFieldNames110 = resultFields110.keySet();
		for (String resultFieldName : resultFieldNames110) {
			actualResultFieldNamesArray110[i] = resultFields110.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray110[i] = resultFields110.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray110[i] = resultFields110.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields110.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray110[i] = ((SuperLexical) resultFields110.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray110, actualResultFieldNamesArray110);
		assertArrayEquals(expectedResultFieldTypesArray110, actualResultFieldTypesArray110);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray110, actualCanonicalModelConstructNamesArray110);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray110, actualParentCanonicalModelConstructNamesArray110);

		EvaluatorOperator input2Operator = input0Operator.getRhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input2Operator);
		assertNotNull(input2Operator.getVariableName());
		assertEquals("union2", input2Operator.getVariableName());
		assertNotNull(input2Operator.getInput());
		assertNotNull(input2Operator.getLhsInput());
		assertEquals(input2Operator.getInput(), input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) input2Operator).getSuperLexicals().size());
		assertEquals(
				"union2.citye.namee, union2.citye.countrye, union2.citye.provincee, union2.citye.populatione, union2.citye.longitudee, union2.citye.latitudee",
				((ReduceOperatorImpl) input2Operator).getReconcilingExpression());

		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input2Operator.getDataSource());

		Collection<SuperLexical> superLexicals20 = ((ReduceOperatorImpl) input2Operator).getSuperLexicals().values();
		String[] superLexicalNamesArray20 = new String[6];
		String[] superAbstractNamesArray20 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals20) {
			superLexicalNamesArray20[i] = superLexical.getName();
			superAbstractNamesArray20[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray20 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray20, superLexicalNamesArray20);
		String[] expectedSuperAbstractNamesArray20 = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray20, superAbstractNamesArray20);
		Set<String> superLexicalVariableNames20 = ((ReduceOperatorImpl) input2Operator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray20 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames20) {
			superLexicalVariableNamesArray20[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray20 = { "union2.citye.namee", "union2.citye.countrye", "union2.citye.provincee",
				"union2.citye.populatione", "union2.citye.longitudee", "union2.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray20, superLexicalVariableNamesArray20);

		assertNotNull(input2Operator.getResultType());
		ResultType resultType20 = input2Operator.getResultType();
		assertEquals(6, resultType20.getResultFields().size());
		String[] expectedResultFieldNamesArray20 = { "union2.citye.namee", "union2.citye.countrye", "union2.citye.provincee",
				"union2.citye.populatione", "union2.citye.longitudee", "union2.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray20 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray20 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray20 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray20 = new String[6];
		DataType[] actualResultFieldTypesArray20 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray20 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray20 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields20 = resultType20.getResultFields();
		Set<String> resultFieldNames20 = resultFields20.keySet();
		for (String resultFieldName : resultFieldNames20) {
			actualResultFieldNamesArray20[i] = resultFields20.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray20[i] = resultFields20.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray20[i] = resultFields20.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields20.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray20[i] = ((SuperLexical) resultFields20.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray20, actualResultFieldNamesArray20);
		assertArrayEquals(expectedResultFieldTypesArray20, actualResultFieldTypesArray20);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray20, actualCanonicalModelConstructNamesArray20);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray20, actualParentCanonicalModelConstructNamesArray20);

		EvaluatorOperator input12Operator = input2Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input12Operator);
		assertEquals("union2.citye", input12Operator.getVariableName());
		assertNull(input12Operator.getLhsInput());
		assertNull(input12Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input12Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("union2.citye.countrye = 'IRL'", ((ScanOperatorImpl) input12Operator).getReconcilingExpression());

		assertNotNull(input12Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input12Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input12Operator).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) input12Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate2.getSuperLexical1().getName());
		assertEquals("citye", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'IRL'", predicate2.getLiteral2());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(input12Operator.getResultType());
		ResultType resultType1 = input12Operator.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "union2.citye.namee", "union2.citye.countrye", "union2.citye.provincee",
				"union2.citye.populatione", "union2.citye.longitudee", "union2.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator input121Operator = input11Operator.getLhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input121Operator);
		assertNotNull(input121Operator.getVariableName());
		assertEquals("union1.union1", input121Operator.getVariableName());
		assertNotNull(input121Operator.getInput());
		assertNotNull(input121Operator.getLhsInput());
		assertEquals(input121Operator.getInput(), input121Operator.getLhsInput());
		assertNull(input121Operator.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) input121Operator).getSuperLexicals().size());
		assertEquals(
				"union1.union1.citye.namee, union1.union1.citye.countrye, union1.union1.citye.provincee, union1.union1.citye.populatione, union1.union1.citye.longitudee, union1.union1.citye.latitudee",
				((ReduceOperatorImpl) input121Operator).getReconcilingExpression());

		assertNotNull(input121Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input121Operator.getDataSource());

		Collection<SuperLexical> superLexicals1210 = ((ReduceOperatorImpl) input121Operator).getSuperLexicals().values();
		String[] superLexicalNamesArray1210 = new String[6];
		String[] superAbstractNamesArray1210 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals1210) {
			superLexicalNamesArray1210[i] = superLexical.getName();
			superAbstractNamesArray1210[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray1210 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray1210, superLexicalNamesArray1210);
		String[] expectedSuperAbstractNamesArray1210 = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray1210, superAbstractNamesArray1210);
		Set<String> superLexicalVariableNames1210 = ((ReduceOperatorImpl) input121Operator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray1210 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames1210) {
			superLexicalVariableNamesArray1210[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray1210 = { "union1.union1.citye.namee", "union1.union1.citye.countrye",
				"union1.union1.citye.provincee", "union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray1210, superLexicalVariableNamesArray1210);

		assertNotNull(input121Operator.getResultType());
		ResultType resultType1210 = input121Operator.getResultType();
		assertEquals(6, resultType1210.getResultFields().size());
		String[] expectedResultFieldNamesArray1210 = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1210 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1210 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1210 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1210 = new String[6];
		DataType[] actualResultFieldTypesArray1210 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1210 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1210 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1210 = resultType1210.getResultFields();
		Set<String> resultFieldNames1210 = resultFields1210.keySet();
		for (String resultFieldName : resultFieldNames1210) {
			actualResultFieldNamesArray1210[i] = resultFields1210.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1210[i] = resultFields1210.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1210[i] = resultFields1210.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1210.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1210[i] = ((SuperLexical) resultFields1210.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1210, actualResultFieldNamesArray1210);
		assertArrayEquals(expectedResultFieldTypesArray1210, actualResultFieldTypesArray1210);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1210, actualCanonicalModelConstructNamesArray1210);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1210, actualParentCanonicalModelConstructNamesArray1210);

		EvaluatorOperator input1211Operator = input121Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1211Operator);
		assertEquals("union1.union1.citye", input1211Operator.getVariableName());
		assertNull(input1211Operator.getLhsInput());
		assertNull(input1211Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1211Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input1211Operator).getReconcilingExpression());
		assertEquals("union1.union1.citye.countrye = 'GB'", ((ScanOperatorImpl) input1211Operator).getReconcilingExpression());

		assertNotNull(input1211Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1211Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input1211Operator).getPredicates().size());
		Predicate predicate11 = ((ScanOperatorImpl) input1211Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate11.getSuperLexical1().getName());
		assertEquals("citye", predicate11.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate11.getOperator());
		assertEquals("'GB'", predicate11.getLiteral2());
		assertNull(predicate11.getLiteral1());
		assertNull(predicate11.getSuperLexical2());

		assertNotNull(input1211Operator.getResultType());
		ResultType resultType2 = input1211Operator.getResultType();
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "union1.union1.citye.namee", "union1.union1.citye.countrye", "union1.union1.citye.provincee",
				"union1.union1.citye.populatione", "union1.union1.citye.longitudee", "union1.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray2 = new String[6];
		DataType[] actualResultFieldTypesArray2 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray2 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[6];

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

		EvaluatorOperator input112Operator = input11Operator.getRhsInput();

		isInstanceOf(ReduceOperatorImpl.class, input112Operator);
		assertNotNull(input112Operator.getVariableName());
		assertEquals("union2.union1", input112Operator.getVariableName());
		assertNotNull(input112Operator.getInput());
		assertNotNull(input112Operator.getLhsInput());
		assertEquals(input112Operator.getInput(), input112Operator.getLhsInput());
		assertNull(input112Operator.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) input112Operator).getSuperLexicals().size());
		assertEquals(
				"union2.union1.citye.namee, union2.union1.citye.countrye, union2.union1.citye.provincee, union2.union1.citye.populatione, union2.union1.citye.longitudee, union2.union1.citye.latitudee",
				((ReduceOperatorImpl) input112Operator).getReconcilingExpression());

		assertNotNull(input112Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input112Operator.getDataSource());

		Collection<SuperLexical> superLexicals1120 = ((ReduceOperatorImpl) input112Operator).getSuperLexicals().values();
		String[] superLexicalNamesArray1120 = new String[6];
		String[] superAbstractNamesArray1120 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals1120) {
			superLexicalNamesArray1120[i] = superLexical.getName();
			superAbstractNamesArray1120[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray1120 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		assertArrayEquals(expectedSuperLexicalNamesArray1120, superLexicalNamesArray1120);
		String[] expectedSuperAbstractNamesArray1120 = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray1120, superAbstractNamesArray1120);
		Set<String> superLexicalVariableNames1120 = ((ReduceOperatorImpl) input112Operator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray1120 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames1120) {
			superLexicalVariableNamesArray1120[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray1120 = { "union2.union1.citye.namee", "union2.union1.citye.countrye",
				"union2.union1.citye.provincee", "union2.union1.citye.populatione", "union2.union1.citye.longitudee", "union2.union1.citye.latitudee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray1120, superLexicalVariableNamesArray1120);

		assertNotNull(input112Operator.getResultType());
		ResultType resultType1120 = input112Operator.getResultType();
		assertEquals(6, resultType1120.getResultFields().size());
		String[] expectedResultFieldNamesArray1120 = { "union2.union1.citye.namee", "union2.union1.citye.countrye", "union2.union1.citye.provincee",
				"union2.union1.citye.populatione", "union2.union1.citye.longitudee", "union2.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray1120 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray1120 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray1120 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray1120 = new String[6];
		DataType[] actualResultFieldTypesArray1120 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1120 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1120 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1120 = resultType1120.getResultFields();
		Set<String> resultFieldNames1120 = resultFields1120.keySet();
		for (String resultFieldName : resultFieldNames1120) {
			actualResultFieldNamesArray1120[i] = resultFields1120.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1120[i] = resultFields1120.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1120[i] = resultFields1120.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1120.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1120[i] = ((SuperLexical) resultFields1120.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1120, actualResultFieldNamesArray1120);
		assertArrayEquals(expectedResultFieldTypesArray1120, actualResultFieldTypesArray1120);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1120, actualCanonicalModelConstructNamesArray1120);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1120, actualParentCanonicalModelConstructNamesArray1120);

		EvaluatorOperator input1121Operator = input112Operator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input1121Operator);
		assertEquals("union2.union1.citye", input1121Operator.getVariableName());
		assertNull(input1121Operator.getLhsInput());
		assertNull(input1121Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input1121Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input1121Operator).getReconcilingExpression());
		assertEquals("union2.union1.citye.countrye = 'D'", ((ScanOperatorImpl) input1121Operator).getReconcilingExpression());

		assertNotNull(input1121Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1121Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input1121Operator).getPredicates().size());
		Predicate predicate12 = ((ScanOperatorImpl) input1121Operator).getPredicates().iterator().next();
		assertEquals("countrye", predicate12.getSuperLexical1().getName());
		assertEquals("citye", predicate12.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate12.getOperator());
		assertEquals("'D'", predicate12.getLiteral2());
		assertNull(predicate12.getLiteral1());
		assertNull(predicate12.getSuperLexical2());

		assertNotNull(input1121Operator.getResultType());
		ResultType resultType3 = input1121Operator.getResultType();
		assertEquals(6, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "union2.union1.citye.namee", "union2.union1.citye.countrye", "union2.union1.citye.provincee",
				"union2.union1.citye.populatione", "union2.union1.citye.longitudee", "union2.union1.citye.latitudee" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray3 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray3 = new String[6];
		DataType[] actualResultFieldTypesArray3 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray3 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);
	}

	@Test
	public void testOptimiseSelectStarFromJoinQueryTwoSources() {
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

		SuperAbstract countrySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("countrye",
				"MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract ethnicGroupSa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("ethnicgroupe",
				"MondialLanguageEconomyReligionOfCountriesEuropeWR");

		countrySa.setCardinality(500);
		ethnicGroupSa.setCardinality(20);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(9, ((ReduceOperatorImpl) rootOperator).getSuperLexicals().size());
		assertEquals(
				"countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee",
				((ReduceOperatorImpl) rootOperator).getReconcilingExpression());

		assertNull(rootOperator.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[9];
		String[] superAbstractNamesArray = new String[9];
		int i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "codee", "capitale", "provincee", "areae", "populatione", "countrye", "namee",
				"percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye", "ethnicgroupe",
				"ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[9];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee",
				"countrye.areae", "countrye.populatione", "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperator.getResultType());
		ResultType resultType0 = rootOperator.getResultType();
		assertEquals(9, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione", "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "codee", "capitale", "provincee", "areae", "populatione", "countrye",
				"namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye",
				"ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray0 = new String[9];
		DataType[] actualResultFieldTypesArray0 = new DataType[9];
		String[] actualCanonicalModelConstructNamesArray0 = new String[9];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[9];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNull(input0Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input0Operator).getPredicates().size());
		Predicate predicate = ((JoinOperatorImpl) input0Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate.getSuperLexical1().getName());
		assertEquals("countrye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate.getOperator());
		assertEquals("countrye", predicate.getSuperLexical2().getName());
		assertEquals("ethnicgroupe", predicate.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate.getLiteral1());
		assertNull(predicate.getLiteral2());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType = input0Operator.getResultType();
		assertEquals(9, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee", "countrye.namee",
				"countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae", "countrye.populatione" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.STRING, DataType.STRING,
				DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray = { "countrye", "namee", "percentagee", "namee", "codee", "capitale", "provincee",
				"areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray = new String[9];
		DataType[] actualResultFieldTypesArray = new DataType[9];
		String[] actualCanonicalModelConstructNamesArray = new String[9];
		String[] actualParentCanonicalModelConstructNamesArray = new String[9];

		i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator input2Operator = input0Operator.getLhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		logger.debug("evaluateExternallyOp2.getVariableName: " + evaluateExternallyOp2.getVariableName());
		assertNotNull(evaluateExternallyOp2.getVariableName());
		assertEquals("ethnicgroupe", evaluateExternallyOp2.getVariableName());
		assertNull(evaluateExternallyOp2.getLhsInput());
		assertNull(evaluateExternallyOp2.getRhsInput());

		assertNotNull(evaluateExternallyOp2.getResultType());
		ResultType resultType2 = evaluateExternallyOp2.getResultType();
		assertEquals(3, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray2 = new String[3];
		DataType[] actualResultFieldTypesArray2 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray2 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[3];

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

		EvaluatorOperator evaluatorOperator21 = evaluateExternallyOp2.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator21);
		assertNotNull(evaluatorOperator21.getVariableName());
		assertEquals("ethnicgroupe", evaluatorOperator21.getVariableName());
		assertNotNull(evaluatorOperator21.getInput());
		assertNotNull(evaluatorOperator21.getLhsInput());
		assertEquals(evaluatorOperator21.getInput(), evaluatorOperator21.getLhsInput());
		assertNull(evaluatorOperator21.getRhsInput());
		assertEquals(3, ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().size());
		assertEquals("ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee", ((ReduceOperatorImpl) evaluatorOperator21)
				.getReconcilingExpression());

		assertNotNull(evaluatorOperator21.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator21.getDataSource());

		Collection<SuperLexical> superLexicals21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().values();
		String[] superLexicalNamesArray21 = new String[3];
		String[] superAbstractNamesArray21 = new String[3];
		i = 0;
		for (SuperLexical superLexical : superLexicals21) {
			superLexicalNamesArray21[i] = superLexical.getName();
			superAbstractNamesArray21[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray21 = { "countrye", "namee", "percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray21, superLexicalNamesArray21);
		String[] expectedSuperAbstractNamesArray21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray21, superAbstractNamesArray21);
		Set<String> superLexicalVariableNames21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray21 = new String[3];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames21) {
			superLexicalVariableNamesArray21[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray21 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray21, superLexicalVariableNamesArray21);

		assertNotNull(evaluatorOperator21.getResultType());
		ResultType resultType21 = evaluatorOperator21.getResultType();
		assertEquals(3, resultType21.getResultFields().size());
		String[] expectedResultFieldNamesArray21 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray21 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray21 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray21 = new String[3];
		DataType[] actualResultFieldTypesArray21 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray21 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray21 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields21 = resultType21.getResultFields();
		Set<String> resultFieldNames21 = resultFields21.keySet();
		for (String resultFieldName : resultFieldNames21) {
			actualResultFieldNamesArray21[i] = resultFields21.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray21[i] = resultFields21.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray21[i] = resultFields21.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields21.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray21[i] = ((SuperLexical) resultFields21.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray21, actualResultFieldNamesArray21);
		assertArrayEquals(expectedResultFieldTypesArray21, actualResultFieldTypesArray21);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray21, actualCanonicalModelConstructNamesArray21);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray21, actualParentCanonicalModelConstructNamesArray21);

		EvaluatorOperator evaluatorOperator211 = evaluatorOperator21.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator211);
		assertEquals("ethnicgroupe", evaluatorOperator211.getVariableName());
		assertNull(evaluatorOperator211.getLhsInput());
		assertNull(evaluatorOperator211.getRhsInput());
		assertEquals("ethnicgroupe", ((ScanOperatorImpl) evaluatorOperator211).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) evaluatorOperator211).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) evaluatorOperator211).getPredicates().size());

		assertNotNull(evaluatorOperator211.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator211.getDataSource());

		assertNotNull(evaluatorOperator211.getResultType());
		ResultType resultType3 = evaluatorOperator211.getResultType();
		assertEquals(3, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray3 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray3 = new String[3];
		DataType[] actualResultFieldTypesArray3 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray3 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);

		EvaluatorOperator input1Operator = input0Operator.getRhsInput();

		assertNotNull(input1Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp1 = (EvaluateExternallyOperatorImpl) input1Operator;

		assertNotNull(evaluateExternallyOp1.getVariableName());
		assertEquals("countrye", evaluateExternallyOp1.getVariableName());
		assertNull(evaluateExternallyOp1.getLhsInput());
		assertNull(evaluateExternallyOp1.getRhsInput());

		assertNotNull(evaluateExternallyOp1.getResultType());
		ResultType resultType1 = evaluateExternallyOp1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator evaluatorOperator11 = evaluateExternallyOp1.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator11);
		assertNotNull(evaluatorOperator11.getVariableName());
		assertEquals("countrye", evaluatorOperator11.getVariableName());
		assertNotNull(evaluatorOperator11.getInput());
		assertNotNull(evaluatorOperator11.getLhsInput());
		assertEquals(evaluatorOperator11.getInput(), evaluatorOperator11.getLhsInput());
		assertNull(evaluatorOperator11.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().size());
		assertEquals("countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione",
				((ReduceOperatorImpl) evaluatorOperator11).getReconcilingExpression());

		assertNotNull(evaluatorOperator11.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator11.getDataSource());

		Collection<SuperLexical> superLexicals11 = ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().values();
		String[] superLexicalNamesArray11 = new String[6];
		String[] superAbstractNamesArray11 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals11) {
			superLexicalNamesArray11[i] = superLexical.getName();
			superAbstractNamesArray11[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray11 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		assertArrayEquals(expectedSuperLexicalNamesArray11, superLexicalNamesArray11);
		String[] expectedSuperAbstractNamesArray11 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };
		assertArrayEquals(expectedSuperAbstractNamesArray11, superAbstractNamesArray11);
		Set<String> superLexicalVariableNames11 = ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray11 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames11) {
			superLexicalVariableNamesArray11[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray11 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee",
				"countrye.areae", "countrye.populatione" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray11, superLexicalVariableNamesArray11);

		assertNotNull(evaluatorOperator11.getResultType());
		ResultType resultType11 = evaluatorOperator11.getResultType();
		assertEquals(6, resultType11.getResultFields().size());
		String[] expectedResultFieldNamesArray11 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray11 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray11 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray11 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray11 = new String[6];
		DataType[] actualResultFieldTypesArray11 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray11 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray11 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields11 = resultType11.getResultFields();
		Set<String> resultFieldNames11 = resultFields11.keySet();
		for (String resultFieldName : resultFieldNames11) {
			actualResultFieldNamesArray11[i] = resultFields11.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray11[i] = resultFields11.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray11[i] = resultFields11.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields11.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray11[i] = ((SuperLexical) resultFields11.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray11, actualResultFieldNamesArray11);
		assertArrayEquals(expectedResultFieldTypesArray11, actualResultFieldTypesArray11);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray11, actualCanonicalModelConstructNamesArray11);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray11, actualParentCanonicalModelConstructNamesArray11);

		EvaluatorOperator evaluatorOperator111 = evaluatorOperator11.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator111);
		assertEquals("countrye", evaluatorOperator111.getVariableName());
		assertNull(evaluatorOperator111.getLhsInput());
		assertNull(evaluatorOperator111.getRhsInput());
		assertEquals("countrye", ((ScanOperatorImpl) evaluatorOperator111).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) evaluatorOperator111).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) evaluatorOperator111).getPredicates().size());

		assertNotNull(evaluatorOperator111.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator111.getDataSource());

		assertNotNull(evaluatorOperator111.getResultType());
		ResultType resultType4 = evaluatorOperator111.getResultType();
		assertEquals(6, resultType4.getResultFields().size());
		String[] expectedResultFieldNamesArray4 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray4 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray4 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray4 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray4 = new String[6];
		DataType[] actualResultFieldTypesArray4 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray4 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray4 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields4 = resultType4.getResultFields();
		Set<String> resultFieldNames4 = resultFields4.keySet();
		for (String resultFieldName : resultFieldNames4) {
			actualResultFieldNamesArray4[i] = resultFields4.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray4[i] = resultFields4.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray4[i] = resultFields4.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields4.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray4[i] = ((SuperLexical) resultFields4.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray4, actualResultFieldNamesArray4);
		assertArrayEquals(expectedResultFieldTypesArray4, actualResultFieldTypesArray4);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray4, actualCanonicalModelConstructNamesArray4);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray4, actualParentCanonicalModelConstructNamesArray4);
	}

	@Test
	public void testOptimiseSelectStarFromJoinWhereAndQueryTwoSources() {
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

		SuperAbstract countrySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("countrye",
				"MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract ethnicGroupSa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("ethnicgroupe",
				"MondialLanguageEconomyReligionOfCountriesEuropeWR");

		countrySa.setCardinality(500);
		ethnicGroupSa.setCardinality(20);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(9, ((ReduceOperatorImpl) rootOperator).getSuperLexicals().size());
		assertEquals(
				"countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione, ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee",
				((ReduceOperatorImpl) rootOperator).getReconcilingExpression());

		assertNull(rootOperator.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[9];
		String[] superAbstractNamesArray = new String[9];
		int i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "codee", "capitale", "provincee", "areae", "populatione", "countrye", "namee",
				"percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye", "ethnicgroupe",
				"ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[9];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee",
				"countrye.areae", "countrye.populatione", "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperator.getResultType());
		ResultType resultType0 = rootOperator.getResultType();
		assertEquals(9, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione", "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "codee", "capitale", "provincee", "areae", "populatione", "countrye",
				"namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye",
				"ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray0 = new String[9];
		DataType[] actualResultFieldTypesArray0 = new DataType[9];
		String[] actualCanonicalModelConstructNamesArray0 = new String[9];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[9];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNull(input0Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input0Operator).getPredicates().size());
		Predicate predicate = ((JoinOperatorImpl) input0Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate.getSuperLexical1().getName());
		assertEquals("countrye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate.getOperator());
		assertEquals("countrye", predicate.getSuperLexical2().getName());
		assertEquals("ethnicgroupe", predicate.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate.getLiteral1());
		assertNull(predicate.getLiteral2());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType = input0Operator.getResultType();
		assertEquals(9, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee", "countrye.namee",
				"countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae", "countrye.populatione" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.STRING, DataType.STRING,
				DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray = { "countrye", "namee", "percentagee", "namee", "codee", "capitale", "provincee",
				"areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray = new String[9];
		DataType[] actualResultFieldTypesArray = new DataType[9];
		String[] actualCanonicalModelConstructNamesArray = new String[9];
		String[] actualParentCanonicalModelConstructNamesArray = new String[9];

		i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator input2Operator = input0Operator.getLhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		assertNotNull(evaluateExternallyOp2.getVariableName());
		assertEquals("ethnicgroupe", evaluateExternallyOp2.getVariableName());
		assertNull(evaluateExternallyOp2.getLhsInput());
		assertNull(evaluateExternallyOp2.getRhsInput());

		assertNotNull(evaluateExternallyOp2.getResultType());
		ResultType resultType2 = evaluateExternallyOp2.getResultType();
		assertEquals(3, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray2 = new String[3];
		DataType[] actualResultFieldTypesArray2 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray2 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[3];

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

		EvaluatorOperator evaluatorOperator21 = evaluateExternallyOp2.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator21);
		assertNotNull(evaluatorOperator21.getVariableName());
		assertEquals("ethnicgroupe", evaluatorOperator21.getVariableName());
		assertNotNull(evaluatorOperator21.getInput());
		assertNotNull(evaluatorOperator21.getLhsInput());
		assertEquals(evaluatorOperator21.getInput(), evaluatorOperator21.getLhsInput());
		assertNull(evaluatorOperator21.getRhsInput());
		assertEquals(3, ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().size());
		assertEquals("ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee", ((ReduceOperatorImpl) evaluatorOperator21)
				.getReconcilingExpression());

		assertNotNull(evaluatorOperator21.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator21.getDataSource());

		Collection<SuperLexical> superLexicals21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().values();
		String[] superLexicalNamesArray21 = new String[3];
		String[] superAbstractNamesArray21 = new String[3];
		i = 0;
		for (SuperLexical superLexical : superLexicals21) {
			superLexicalNamesArray21[i] = superLexical.getName();
			superAbstractNamesArray21[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray21 = { "countrye", "namee", "percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray21, superLexicalNamesArray21);
		String[] expectedSuperAbstractNamesArray21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray21, superAbstractNamesArray21);
		Set<String> superLexicalVariableNames21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray21 = new String[3];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames21) {
			superLexicalVariableNamesArray21[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray21 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray21, superLexicalVariableNamesArray21);

		assertNotNull(evaluatorOperator21.getResultType());
		ResultType resultType21 = evaluatorOperator21.getResultType();
		assertEquals(3, resultType21.getResultFields().size());
		String[] expectedResultFieldNamesArray21 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray21 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray21 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray21 = new String[3];
		DataType[] actualResultFieldTypesArray21 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray21 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray21 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields21 = resultType21.getResultFields();
		Set<String> resultFieldNames21 = resultFields21.keySet();
		for (String resultFieldName : resultFieldNames21) {
			actualResultFieldNamesArray21[i] = resultFields21.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray21[i] = resultFields21.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray21[i] = resultFields21.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields21.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray21[i] = ((SuperLexical) resultFields21.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray21, actualResultFieldNamesArray21);
		assertArrayEquals(expectedResultFieldTypesArray21, actualResultFieldTypesArray21);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray21, actualCanonicalModelConstructNamesArray21);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray21, actualParentCanonicalModelConstructNamesArray21);

		EvaluatorOperator evaluatorOperator211 = evaluatorOperator21.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator211);
		assertEquals("ethnicgroupe", evaluatorOperator211.getVariableName());
		assertNull(evaluatorOperator211.getLhsInput());
		assertNull(evaluatorOperator211.getRhsInput());
		assertEquals("ethnicgroupe", ((ScanOperatorImpl) evaluatorOperator211).getSuperAbstract().getName());
		assertEquals("ethnicgroupe.namee = 'English'", ((ScanOperatorImpl) evaluatorOperator211).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) evaluatorOperator211).getAndOr());

		assertEquals(1, ((ScanOperatorImpl) evaluatorOperator211).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) evaluatorOperator211).getPredicates().iterator().next();
		assertEquals("namee", predicate2.getSuperLexical1().getName());
		assertEquals("ethnicgroupe", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'English'", predicate2.getLiteral2());
		assertEquals("and", predicate2.getAndOr());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(evaluatorOperator211.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator211.getDataSource());

		assertNotNull(evaluatorOperator211.getResultType());
		ResultType resultType3 = evaluatorOperator211.getResultType();
		assertEquals(3, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray3 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray3 = new String[3];
		DataType[] actualResultFieldTypesArray3 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray3 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);

		EvaluatorOperator input1Operator = input0Operator.getRhsInput();

		assertNotNull(input1Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp1 = (EvaluateExternallyOperatorImpl) input1Operator;

		assertNotNull(evaluateExternallyOp1.getVariableName());
		assertEquals("countrye", evaluateExternallyOp1.getVariableName());
		assertNull(evaluateExternallyOp1.getLhsInput());
		assertNull(evaluateExternallyOp1.getRhsInput());

		assertNotNull(evaluateExternallyOp1.getResultType());
		ResultType resultType1 = evaluateExternallyOp1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator evaluatorOperator11 = evaluateExternallyOp1.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator11);
		assertNotNull(evaluatorOperator11.getVariableName());
		assertEquals("countrye", evaluatorOperator11.getVariableName());
		assertNotNull(evaluatorOperator11.getInput());
		assertNotNull(evaluatorOperator11.getLhsInput());
		assertEquals(evaluatorOperator11.getInput(), evaluatorOperator11.getLhsInput());
		assertNull(evaluatorOperator11.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().size());
		assertEquals("countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione",
				((ReduceOperatorImpl) evaluatorOperator11).getReconcilingExpression());

		assertNotNull(evaluatorOperator11.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator11.getDataSource());

		Collection<SuperLexical> superLexicals11 = ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().values();
		String[] superLexicalNamesArray11 = new String[6];
		String[] superAbstractNamesArray11 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals11) {
			superLexicalNamesArray11[i] = superLexical.getName();
			superAbstractNamesArray11[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray11 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		assertArrayEquals(expectedSuperLexicalNamesArray11, superLexicalNamesArray11);
		String[] expectedSuperAbstractNamesArray11 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };
		assertArrayEquals(expectedSuperAbstractNamesArray11, superAbstractNamesArray11);
		Set<String> superLexicalVariableNames11 = ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray11 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames11) {
			superLexicalVariableNamesArray11[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray11 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee",
				"countrye.areae", "countrye.populatione" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray11, superLexicalVariableNamesArray11);

		assertNotNull(evaluatorOperator11.getResultType());
		ResultType resultType11 = evaluatorOperator11.getResultType();
		assertEquals(6, resultType11.getResultFields().size());
		String[] expectedResultFieldNamesArray11 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray11 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray11 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray11 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray11 = new String[6];
		DataType[] actualResultFieldTypesArray11 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray11 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray11 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields11 = resultType11.getResultFields();
		Set<String> resultFieldNames11 = resultFields11.keySet();
		for (String resultFieldName : resultFieldNames11) {
			actualResultFieldNamesArray11[i] = resultFields11.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray11[i] = resultFields11.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray11[i] = resultFields11.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields11.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray11[i] = ((SuperLexical) resultFields11.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray11, actualResultFieldNamesArray11);
		assertArrayEquals(expectedResultFieldTypesArray11, actualResultFieldTypesArray11);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray11, actualCanonicalModelConstructNamesArray11);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray11, actualParentCanonicalModelConstructNamesArray11);

		EvaluatorOperator evaluatorOperator111 = evaluatorOperator11.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator111);
		assertEquals("countrye", evaluatorOperator111.getVariableName());
		assertNull(evaluatorOperator111.getLhsInput());
		assertNull(evaluatorOperator111.getRhsInput());
		assertEquals("countrye", ((ScanOperatorImpl) evaluatorOperator111).getSuperAbstract().getName());
		assertEquals("countrye.codee = 'GB'", ((ScanOperatorImpl) evaluatorOperator111).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) evaluatorOperator111).getAndOr());

		assertEquals(1, ((ScanOperatorImpl) evaluatorOperator111).getPredicates().size());
		Predicate predicate1 = ((ScanOperatorImpl) evaluatorOperator111).getPredicates().iterator().next();
		assertEquals("codee", predicate1.getSuperLexical1().getName());
		assertEquals("countrye", predicate1.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate1.getOperator());
		assertEquals("'GB'", predicate1.getLiteral2());
		assertEquals("and", predicate1.getAndOr());
		assertNull(predicate1.getLiteral1());
		assertNull(predicate1.getSuperLexical2());

		assertNotNull(evaluatorOperator111.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator111.getDataSource());

		assertNotNull(evaluatorOperator111.getResultType());
		ResultType resultType4 = evaluatorOperator111.getResultType();
		assertEquals(6, resultType4.getResultFields().size());
		String[] expectedResultFieldNamesArray4 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray4 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray4 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray4 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray4 = new String[6];
		DataType[] actualResultFieldTypesArray4 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray4 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray4 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields4 = resultType4.getResultFields();
		Set<String> resultFieldNames4 = resultFields4.keySet();
		for (String resultFieldName : resultFieldNames4) {
			actualResultFieldNamesArray4[i] = resultFields4.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray4[i] = resultFields4.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray4[i] = resultFields4.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields4.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray4[i] = ((SuperLexical) resultFields4.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray4, actualResultFieldNamesArray4);
		assertArrayEquals(expectedResultFieldTypesArray4, actualResultFieldTypesArray4);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray4, actualCanonicalModelConstructNamesArray4);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray4, actualParentCanonicalModelConstructNamesArray4);
	}

	@Test
	public void testOptimiseSelectSuperLexicalsFromJoinWhereAndQueryTwoSources() {
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

		SuperAbstract countrySa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("countrye",
				"MondialCityProvinceCountryContinentEuropeWR");
		SuperAbstract ethnicGroupSa = superAbstractRepository.getSuperAbstractByNameInSchemaWithName("ethnicgroupe",
				"MondialLanguageEconomyReligionOfCountriesEuropeWR");

		countrySa.setCardinality(500);
		ethnicGroupSa.setCardinality(20);

		query.setRootOperatorOfExpandedQuery(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);

		isInstanceOf(ReduceOperatorImpl.class, rootOperator);
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(5, ((ReduceOperatorImpl) rootOperator).getSuperLexicals().size());
		assertEquals("countrye.namee, countrye.codee, countrye.capitale, ethnicgroupe.namee, ethnicgroupe.percentagee",
				((ReduceOperatorImpl) rootOperator).getReconcilingExpression());

		assertNull(rootOperator.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[5];
		String[] superAbstractNamesArray = new String[5];
		int i = 0;
		for (SuperLexical superLexical : superLexicals) {
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "namee", "codee", "capitale", "namee", "percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "countrye", "countrye", "countrye", "ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperatorImpl) rootOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[5];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "countrye.namee", "countrye.codee", "countrye.capitale", "ethnicgroupe.namee",
				"ethnicgroupe.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperator.getResultType());
		ResultType resultType0 = rootOperator.getResultType();
		assertEquals(5, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "countrye.namee", "countrye.codee", "countrye.capitale", "ethnicgroupe.namee",
				"ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "codee", "capitale", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "countrye", "countrye", "countrye", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray0 = new String[5];
		DataType[] actualResultFieldTypesArray0 = new DataType[5];
		String[] actualCanonicalModelConstructNamesArray0 = new String[5];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[5];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFields0.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		EvaluatorOperator input0Operator = rootOperator.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, input0Operator);
		assertNull(input0Operator.getVariableName());
		assertNotNull(((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertEquals("countrye.codee = ethnicgroupe.countrye", ((JoinOperatorImpl) input0Operator).getReconcilingExpression());
		assertNotNull(input0Operator.getLhsInput());
		assertNotNull(input0Operator.getRhsInput());
		assertEquals(input0Operator.getInput(), input0Operator.getLhsInput());

		assertNull(input0Operator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) input0Operator).getPredicates().size());
		Predicate predicate = ((JoinOperatorImpl) input0Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate.getSuperLexical1().getName());
		assertEquals("countrye", predicate.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate.getOperator());
		assertEquals("countrye", predicate.getSuperLexical2().getName());
		assertEquals("ethnicgroupe", predicate.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicate.getLiteral1());
		assertNull(predicate.getLiteral2());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType = input0Operator.getResultType();
		assertEquals(9, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee", "countrye.namee",
				"countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae", "countrye.populatione" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.STRING, DataType.STRING,
				DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray = { "countrye", "namee", "percentagee", "namee", "codee", "capitale", "provincee",
				"areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe", "countrye", "countrye",
				"countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray = new String[9];
		DataType[] actualResultFieldTypesArray = new DataType[9];
		String[] actualCanonicalModelConstructNamesArray = new String[9];
		String[] actualParentCanonicalModelConstructNamesArray = new String[9];

		i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator input2Operator = input0Operator.getLhsInput();

		assertNotNull(input2Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input2Operator);
		assertNull(input2Operator.getLhsInput());
		assertNull(input2Operator.getRhsInput());
		assertNotNull(input2Operator.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, input2Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) input2Operator;

		assertNotNull(evaluateExternallyOp2.getVariableName());
		assertEquals("ethnicgroupe", evaluateExternallyOp2.getVariableName());
		assertNull(evaluateExternallyOp2.getLhsInput());
		assertNull(evaluateExternallyOp2.getRhsInput());

		assertNotNull(evaluateExternallyOp2.getResultType());
		ResultType resultType2 = evaluateExternallyOp2.getResultType();
		assertEquals(3, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray2 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray2 = new String[3];
		DataType[] actualResultFieldTypesArray2 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray2 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray2 = new String[3];

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

		EvaluatorOperator evaluatorOperator21 = evaluateExternallyOp2.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator21);
		assertNotNull(evaluatorOperator21.getVariableName());
		assertEquals("ethnicgroupe", evaluatorOperator21.getVariableName());
		assertNotNull(evaluatorOperator21.getInput());
		assertNotNull(evaluatorOperator21.getLhsInput());
		assertEquals(evaluatorOperator21.getInput(), evaluatorOperator21.getLhsInput());
		assertNull(evaluatorOperator21.getRhsInput());
		assertEquals(3, ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().size());
		assertEquals("ethnicgroupe.countrye, ethnicgroupe.namee, ethnicgroupe.percentagee", ((ReduceOperatorImpl) evaluatorOperator21)
				.getReconcilingExpression());

		assertNotNull(evaluatorOperator21.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator21.getDataSource());

		Collection<SuperLexical> superLexicals21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().values();
		String[] superLexicalNamesArray21 = new String[3];
		String[] superAbstractNamesArray21 = new String[3];
		i = 0;
		for (SuperLexical superLexical : superLexicals21) {
			superLexicalNamesArray21[i] = superLexical.getName();
			superAbstractNamesArray21[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray21 = { "countrye", "namee", "percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArray21, superLexicalNamesArray21);
		String[] expectedSuperAbstractNamesArray21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArray21, superAbstractNamesArray21);
		Set<String> superLexicalVariableNames21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray21 = new String[3];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames21) {
			superLexicalVariableNamesArray21[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray21 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray21, superLexicalVariableNamesArray21);

		assertNotNull(evaluatorOperator21.getResultType());
		ResultType resultType21 = evaluatorOperator21.getResultType();
		assertEquals(3, resultType21.getResultFields().size());
		String[] expectedResultFieldNamesArray21 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray21 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray21 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray21 = new String[3];
		DataType[] actualResultFieldTypesArray21 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray21 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray21 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields21 = resultType21.getResultFields();
		Set<String> resultFieldNames21 = resultFields21.keySet();
		for (String resultFieldName : resultFieldNames21) {
			actualResultFieldNamesArray21[i] = resultFields21.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray21[i] = resultFields21.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray21[i] = resultFields21.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields21.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray21[i] = ((SuperLexical) resultFields21.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray21, actualResultFieldNamesArray21);
		assertArrayEquals(expectedResultFieldTypesArray21, actualResultFieldTypesArray21);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray21, actualCanonicalModelConstructNamesArray21);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray21, actualParentCanonicalModelConstructNamesArray21);

		EvaluatorOperator evaluatorOperator211 = evaluatorOperator21.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator211);
		assertEquals("ethnicgroupe", evaluatorOperator211.getVariableName());
		assertNull(evaluatorOperator211.getLhsInput());
		assertNull(evaluatorOperator211.getRhsInput());
		assertEquals("ethnicgroupe", ((ScanOperatorImpl) evaluatorOperator211).getSuperAbstract().getName());
		assertEquals("ethnicgroupe.namee = 'English'", ((ScanOperatorImpl) evaluatorOperator211).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) evaluatorOperator211).getAndOr());

		assertEquals(1, ((ScanOperatorImpl) evaluatorOperator211).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) evaluatorOperator211).getPredicates().iterator().next();
		assertEquals("namee", predicate2.getSuperLexical1().getName());
		assertEquals("ethnicgroupe", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'English'", predicate2.getLiteral2());
		assertEquals("and", predicate2.getAndOr());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(evaluatorOperator211.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator211.getDataSource());

		assertNotNull(evaluatorOperator211.getResultType());
		ResultType resultType3 = evaluatorOperator211.getResultType();
		assertEquals(3, resultType3.getResultFields().size());
		String[] expectedResultFieldNamesArray3 = { "ethnicgroupe.countrye", "ethnicgroupe.namee", "ethnicgroupe.percentagee" };
		DataType[] expectedResultFieldTypesArray3 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray3 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray3 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray3 = new String[3];
		DataType[] actualResultFieldTypesArray3 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray3 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray3 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields3 = resultType3.getResultFields();
		Set<String> resultFieldNames3 = resultFields3.keySet();
		for (String resultFieldName : resultFieldNames3) {
			actualResultFieldNamesArray3[i] = resultFields3.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray3[i] = resultFields3.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray3[i] = resultFields3.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields3.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray3[i] = ((SuperLexical) resultFields3.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray3, actualResultFieldNamesArray3);
		assertArrayEquals(expectedResultFieldTypesArray3, actualResultFieldTypesArray3);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray3, actualCanonicalModelConstructNamesArray3);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray3, actualParentCanonicalModelConstructNamesArray3);

		EvaluatorOperator input1Operator = input0Operator.getRhsInput();

		assertNotNull(input1Operator);
		isInstanceOf(EvaluateExternallyOperatorImpl.class, input1Operator);
		assertNull(input1Operator.getLhsInput());
		assertNull(input1Operator.getRhsInput());
		assertNotNull(input1Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input1Operator.getDataSource());

		EvaluateExternallyOperatorImpl evaluateExternallyOp1 = (EvaluateExternallyOperatorImpl) input1Operator;

		assertNotNull(evaluateExternallyOp1.getVariableName());
		assertEquals("countrye", evaluateExternallyOp1.getVariableName());
		assertNull(evaluateExternallyOp1.getLhsInput());
		assertNull(evaluateExternallyOp1.getRhsInput());

		assertNotNull(evaluateExternallyOp1.getResultType());
		ResultType resultType1 = evaluateExternallyOp1.getResultType();
		assertEquals(6, resultType1.getResultFields().size());
		String[] expectedResultFieldNamesArray1 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray1 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray1 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray1 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray1 = new String[6];
		DataType[] actualResultFieldTypesArray1 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray1 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray1 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields1 = resultType1.getResultFields();
		Set<String> resultFieldNames1 = resultFields1.keySet();
		for (String resultFieldName : resultFieldNames1) {
			actualResultFieldNamesArray1[i] = resultFields1.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray1[i] = resultFields1.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray1[i] = resultFields1.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields1.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray1[i] = ((SuperLexical) resultFields1.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray1, actualResultFieldNamesArray1);
		assertArrayEquals(expectedResultFieldTypesArray1, actualResultFieldTypesArray1);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray1, actualCanonicalModelConstructNamesArray1);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray1, actualParentCanonicalModelConstructNamesArray1);

		EvaluatorOperator evaluatorOperator11 = evaluateExternallyOp1.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator11);
		assertNotNull(evaluatorOperator11.getVariableName());
		assertEquals("countrye", evaluatorOperator11.getVariableName());
		assertNotNull(evaluatorOperator11.getInput());
		assertNotNull(evaluatorOperator11.getLhsInput());
		assertEquals(evaluatorOperator11.getInput(), evaluatorOperator11.getLhsInput());
		assertNull(evaluatorOperator11.getRhsInput());
		assertEquals(6, ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().size());
		assertEquals("countrye.namee, countrye.codee, countrye.capitale, countrye.provincee, countrye.areae, countrye.populatione",
				((ReduceOperatorImpl) evaluatorOperator11).getReconcilingExpression());

		assertNotNull(evaluatorOperator11.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator11.getDataSource());

		Collection<SuperLexical> superLexicals11 = ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().values();
		String[] superLexicalNamesArray11 = new String[6];
		String[] superAbstractNamesArray11 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals11) {
			superLexicalNamesArray11[i] = superLexical.getName();
			superAbstractNamesArray11[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray11 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		assertArrayEquals(expectedSuperLexicalNamesArray11, superLexicalNamesArray11);
		String[] expectedSuperAbstractNamesArray11 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };
		assertArrayEquals(expectedSuperAbstractNamesArray11, superAbstractNamesArray11);
		Set<String> superLexicalVariableNames11 = ((ReduceOperatorImpl) evaluatorOperator11).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray11 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames11) {
			superLexicalVariableNamesArray11[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray11 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee",
				"countrye.areae", "countrye.populatione" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray11, superLexicalVariableNamesArray11);

		assertNotNull(evaluatorOperator11.getResultType());
		ResultType resultType11 = evaluatorOperator11.getResultType();
		assertEquals(6, resultType11.getResultFields().size());
		String[] expectedResultFieldNamesArray11 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray11 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray11 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray11 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray11 = new String[6];
		DataType[] actualResultFieldTypesArray11 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray11 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray11 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields11 = resultType11.getResultFields();
		Set<String> resultFieldNames11 = resultFields11.keySet();
		for (String resultFieldName : resultFieldNames11) {
			actualResultFieldNamesArray11[i] = resultFields11.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray11[i] = resultFields11.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray11[i] = resultFields11.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields11.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray11[i] = ((SuperLexical) resultFields11.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray11, actualResultFieldNamesArray11);
		assertArrayEquals(expectedResultFieldTypesArray11, actualResultFieldTypesArray11);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray11, actualCanonicalModelConstructNamesArray11);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray11, actualParentCanonicalModelConstructNamesArray11);

		EvaluatorOperator evaluatorOperator111 = evaluatorOperator11.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator111);
		assertEquals("countrye", evaluatorOperator111.getVariableName());
		assertNull(evaluatorOperator111.getLhsInput());
		assertNull(evaluatorOperator111.getRhsInput());
		assertEquals("countrye", ((ScanOperatorImpl) evaluatorOperator111).getSuperAbstract().getName());
		assertEquals("countrye.codee = 'GB'", ((ScanOperatorImpl) evaluatorOperator111).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) evaluatorOperator111).getAndOr());

		assertEquals(1, ((ScanOperatorImpl) evaluatorOperator111).getPredicates().size());
		Predicate predicate1 = ((ScanOperatorImpl) evaluatorOperator111).getPredicates().iterator().next();
		assertEquals("codee", predicate1.getSuperLexical1().getName());
		assertEquals("countrye", predicate1.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate1.getOperator());
		assertEquals("'GB'", predicate1.getLiteral2());
		assertEquals("and", predicate1.getAndOr());
		assertNull(predicate1.getLiteral1());
		assertNull(predicate1.getSuperLexical2());

		assertNotNull(evaluatorOperator111.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, evaluatorOperator111.getDataSource());

		assertNotNull(evaluatorOperator111.getResultType());
		ResultType resultType4 = evaluatorOperator111.getResultType();
		assertEquals(6, resultType4.getResultFields().size());
		String[] expectedResultFieldNamesArray4 = { "countrye.namee", "countrye.codee", "countrye.capitale", "countrye.provincee", "countrye.areae",
				"countrye.populatione" };
		DataType[] expectedResultFieldTypesArray4 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray4 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray4 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray4 = new String[6];
		DataType[] actualResultFieldTypesArray4 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray4 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray4 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields4 = resultType4.getResultFields();
		Set<String> resultFieldNames4 = resultFields4.keySet();
		for (String resultFieldName : resultFieldNames4) {
			actualResultFieldNamesArray4[i] = resultFields4.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray4[i] = resultFields4.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray4[i] = resultFields4.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields4.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray4[i] = ((SuperLexical) resultFields4.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray4, actualResultFieldNamesArray4);
		assertArrayEquals(expectedResultFieldTypesArray4, actualResultFieldTypesArray4);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray4, actualCanonicalModelConstructNamesArray4);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray4, actualParentCanonicalModelConstructNamesArray4);
	}

	@Test
	public void testOptimiseSelectSuperLexicalsFromJoinJoinJoinWhereAndQueryWithVariableNameTwoSources() {
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

		assertNotNull(evaluateScanBordersExternallyOp.getResultType());
		ResultType resultType = evaluateScanBordersExternallyOp.getResultType();
		assertEquals(3, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "b.country1e", "b.country2e", "b.lengthe" };
		DataType[] expectedResultFieldTypesArray = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "country1e", "country2e", "lengthe" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "borderse", "borderse", "borderse" };

		String[] actualResultFieldNamesArray = new String[3];
		DataType[] actualResultFieldTypesArray = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray = new String[3];

		i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFields.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		EvaluatorOperator reduceScanBordersOperator = evaluateScanBordersExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, reduceScanBordersOperator);
		assertNotNull(reduceScanBordersOperator.getVariableName());
		assertEquals("b", reduceScanBordersOperator.getVariableName());
		assertNotNull(reduceScanBordersOperator.getInput());
		assertNotNull(reduceScanBordersOperator.getLhsInput());
		assertEquals(reduceScanBordersOperator.getInput(), reduceScanBordersOperator.getLhsInput());
		assertNull(reduceScanBordersOperator.getRhsInput());
		assertEquals(3, ((ReduceOperatorImpl) reduceScanBordersOperator).getSuperLexicals().size());
		assertEquals("b.country1e, b.country2e, b.lengthe", ((ReduceOperatorImpl) reduceScanBordersOperator).getReconcilingExpression());

		assertNotNull(reduceScanBordersOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, reduceScanBordersOperator.getDataSource());

		Collection<SuperLexical> superLexicals21 = ((ReduceOperatorImpl) reduceScanBordersOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray21 = new String[3];
		String[] superAbstractNamesArray21 = new String[3];
		i = 0;
		for (SuperLexical superLexical : superLexicals21) {
			superLexicalNamesArray21[i] = superLexical.getName();
			superAbstractNamesArray21[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArray21 = { "country1e", "country2e", "lengthe" };
		assertArrayEquals(expectedSuperLexicalNamesArray21, superLexicalNamesArray21);
		String[] expectedSuperAbstractNamesArray21 = { "borderse", "borderse", "borderse" };
		assertArrayEquals(expectedSuperAbstractNamesArray21, superAbstractNamesArray21);
		Set<String> superLexicalVariableNames21 = ((ReduceOperatorImpl) reduceScanBordersOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray21 = new String[3];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames21) {
			superLexicalVariableNamesArray21[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray21 = { "b.country1e", "b.country2e", "b.lengthe" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray21, superLexicalVariableNamesArray21);

		assertNotNull(reduceScanBordersOperator.getResultType());
		ResultType resultType21 = reduceScanBordersOperator.getResultType();
		assertEquals(3, resultType21.getResultFields().size());
		String[] expectedResultFieldNamesArray21 = { "b.country1e", "b.country2e", "b.lengthe" };
		DataType[] expectedResultFieldTypesArray21 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray21 = { "country1e", "country2e", "lengthe" };
		String[] exptectedParentCanonicalModelConstructNamesArray21 = { "borderse", "borderse", "borderse" };

		String[] actualResultFieldNamesArray21 = new String[3];
		DataType[] actualResultFieldTypesArray21 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray21 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray21 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields21 = resultType21.getResultFields();
		Set<String> resultFieldNames21 = resultFields21.keySet();
		for (String resultFieldName : resultFieldNames21) {
			actualResultFieldNamesArray21[i] = resultFields21.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray21[i] = resultFields21.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray21[i] = resultFields21.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields21.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray21[i] = ((SuperLexical) resultFields21.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray21, actualResultFieldNamesArray21);
		assertArrayEquals(expectedResultFieldTypesArray21, actualResultFieldTypesArray21);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray21, actualCanonicalModelConstructNamesArray21);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray21, actualParentCanonicalModelConstructNamesArray21);

		EvaluatorOperator scanBordersOperator = reduceScanBordersOperator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, scanBordersOperator);
		assertNotNull(scanBordersOperator.getVariableName());
		assertEquals("b", scanBordersOperator.getVariableName());
		assertNull(scanBordersOperator.getLhsInput());
		assertNull(scanBordersOperator.getRhsInput());
		assertEquals("borderse", ((ScanOperatorImpl) scanBordersOperator).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) scanBordersOperator).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) scanBordersOperator).getPredicates().size());

		assertNotNull(scanBordersOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, scanBordersOperator.getDataSource());

		assertNotNull(scanBordersOperator.getResultType());
		ResultType resultTypeSBorders = scanBordersOperator.getResultType();
		assertEquals(3, resultTypeSBorders.getResultFields().size());
		String[] expectedResultFieldNamesArraySBorders = { "b.country1e", "b.country2e", "b.lengthe" };
		DataType[] expectedResultFieldTypesArraySBorders = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArraySBorders = { "country1e", "country2e", "lengthe" };
		String[] exptectedParentCanonicalModelConstructNamesArraySBorders = { "borderse", "borderse", "borderse" };

		String[] actualResultFieldNamesArraySBorders = new String[3];
		DataType[] actualResultFieldTypesArraySBorders = new DataType[3];
		String[] actualCanonicalModelConstructNamesArraySBorders = new String[3];
		String[] actualParentCanonicalModelConstructNamesArraySBorders = new String[3];

		i = 0;
		Map<String, ResultField> resultFieldsSBorders = resultTypeSBorders.getResultFields();
		Set<String> resultFieldNamesSBorders = resultFieldsSBorders.keySet();
		for (String resultFieldName : resultFieldNamesSBorders) {
			actualResultFieldNamesArraySBorders[i] = resultFieldsSBorders.get(resultFieldName).getFieldName();
			actualResultFieldTypesArraySBorders[i] = resultFieldsSBorders.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArraySBorders[i] = resultFieldsSBorders.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsSBorders.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArraySBorders[i] = ((SuperLexical) resultFieldsSBorders.get(resultFieldName)
					.getCanonicalModelConstruct()).getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArraySBorders, actualResultFieldNamesArraySBorders);
		assertArrayEquals(expectedResultFieldTypesArraySBorders, actualResultFieldTypesArraySBorders);
		assertArrayEquals(expectedCanonicalModelConstructNamesArraySBorders, actualCanonicalModelConstructNamesArraySBorders);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArraySBorders, actualParentCanonicalModelConstructNamesArraySBorders);

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

		assertNotNull(evaluateJoinCityCountryExternallyOp.getResultType());
		ResultType resultTypeEJCOEx = evaluateJoinCityCountryExternallyOp.getResultType();
		assertEquals(12, resultTypeEJCOEx.getResultFields().size());
		String[] expectedResultFieldNamesArrayEJCOEx = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		DataType[] expectedResultFieldTypesArrayEJCOEx = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArrayEJCOEx = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee",
				"namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArrayEJCOEx = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye",
				"countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArrayEJCOEx = new String[12];
		DataType[] actualResultFieldTypesArrayEJCOEx = new DataType[12];
		String[] actualCanonicalModelConstructNamesArrayEJCOEx = new String[12];
		String[] actualParentCanonicalModelConstructNamesArrayEJCOEx = new String[12];

		i = 0;
		Map<String, ResultField> resultFieldsEJCOEx = resultTypeEJCOEx.getResultFields();
		Set<String> resultFieldNamesEJCOEx = resultFieldsEJCOEx.keySet();
		for (String resultFieldName : resultFieldNamesEJCOEx) {
			actualResultFieldNamesArrayEJCOEx[i] = resultFieldsEJCOEx.get(resultFieldName).getFieldName();
			actualResultFieldTypesArrayEJCOEx[i] = resultFieldsEJCOEx.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArrayEJCOEx[i] = resultFieldsEJCOEx.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsEJCOEx.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArrayEJCOEx[i] = ((SuperLexical) resultFieldsEJCOEx.get(resultFieldName)
					.getCanonicalModelConstruct()).getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArrayEJCOEx, actualResultFieldNamesArrayEJCOEx);
		assertArrayEquals(expectedResultFieldTypesArrayEJCOEx, actualResultFieldTypesArrayEJCOEx);
		assertArrayEquals(expectedCanonicalModelConstructNamesArrayEJCOEx, actualCanonicalModelConstructNamesArrayEJCOEx);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArrayEJCOEx, actualParentCanonicalModelConstructNamesArrayEJCOEx);

		EvaluatorOperator reduceJoinCityCountry = evaluateJoinCityCountryExternallyOp.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, reduceJoinCityCountry);
		assertNotNull(reduceJoinCityCountry.getVariableName());
		assertEquals("o", reduceJoinCityCountry.getVariableName());
		assertNotNull(reduceJoinCityCountry.getInput());
		assertNotNull(reduceJoinCityCountry.getLhsInput());
		assertEquals(reduceJoinCityCountry.getInput(), reduceJoinCityCountry.getLhsInput());
		assertNull(reduceJoinCityCountry.getRhsInput());
		assertEquals(12, ((ReduceOperatorImpl) reduceJoinCityCountry).getSuperLexicals().size());
		assertEquals(
				"c.namee, c.countrye, c.provincee, c.populatione, c.longitudee, c.latitudee, o.namee, o.codee, o.capitale, o.provincee, o.areae, o.populatione",
				((ReduceOperatorImpl) reduceJoinCityCountry).getReconcilingExpression());

		assertNotNull(reduceJoinCityCountry.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, reduceJoinCityCountry.getDataSource());

		Collection<SuperLexical> superLexicalsRJCO = ((ReduceOperatorImpl) reduceJoinCityCountry).getSuperLexicals().values();
		String[] superLexicalNamesArrayRJCO = new String[12];
		String[] superAbstractNamesArrayRJCO = new String[12];
		i = 0;
		for (SuperLexical superLexical : superLexicalsRJCO) {
			superLexicalNamesArrayRJCO[i] = superLexical.getName();
			superAbstractNamesArrayRJCO[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArrayRJCO = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee", "namee", "codee",
				"capitale", "provincee", "areae", "populatione" };
		assertArrayEquals(expectedSuperLexicalNamesArrayRJCO, superLexicalNamesArrayRJCO);
		String[] expectedSuperAbstractNamesArrayRJCO = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye", "countrye", "countrye",
				"countrye", "countrye", "countrye" };
		assertArrayEquals(expectedSuperAbstractNamesArrayRJCO, superAbstractNamesArrayRJCO);
		Set<String> superLexicalVariableNamesRJCO = ((ReduceOperatorImpl) reduceJoinCityCountry).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArrayRJCO = new String[12];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNamesRJCO) {
			superLexicalVariableNamesArrayRJCO[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArrayRJCO = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee",
				"c.latitudee", "o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArrayRJCO, superLexicalVariableNamesArrayRJCO);

		assertNotNull(reduceJoinCityCountry.getResultType());
		ResultType resultTypeRJCO = reduceJoinCityCountry.getResultType();
		assertEquals(12, resultTypeRJCO.getResultFields().size());
		String[] expectedResultFieldNamesArrayRJCO = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		DataType[] expectedResultFieldTypesArrayRJCO = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArrayRJCO = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee",
				"namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] expectedParentCanonicalModelConstructNamesArrayRJCO = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye",
				"countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArrayRJCO = new String[12];
		DataType[] actualResultFieldTypesArrayRJCO = new DataType[12];
		String[] actualCanonicalModelConstructNamesArrayRJCO = new String[12];
		String[] actualParentCanonicalModelConstructNamesArrayRJCO = new String[12];

		j = 0;
		Map<String, ResultField> resultFieldsRJCO = resultTypeRJCO.getResultFields();
		Set<String> resultFieldNamesRJCO = resultFieldsRJCO.keySet();
		for (String resultFieldName : resultFieldNamesRJCO) {
			actualResultFieldNamesArrayRJCO[j] = resultFieldsRJCO.get(resultFieldName).getFieldName();
			actualResultFieldTypesArrayRJCO[j] = resultFieldsRJCO.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArrayRJCO[j] = resultFieldsRJCO.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsRJCO.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArrayRJCO[j] = ((SuperLexical) resultFieldsRJCO.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			j++;
		}

		assertArrayEquals(expectedResultFieldNamesArrayRJCO, actualResultFieldNamesArrayRJCO);
		assertArrayEquals(expectedResultFieldTypesArrayRJCO, actualResultFieldTypesArrayRJCO);
		assertArrayEquals(expectedCanonicalModelConstructNamesArrayRJCO, actualCanonicalModelConstructNamesArrayRJCO);
		assertArrayEquals(expectedParentCanonicalModelConstructNamesArrayRJCO, actualParentCanonicalModelConstructNamesArrayRJCO);

		EvaluatorOperator joinCityCountryOperator = reduceJoinCityCountry.getLhsInput();

		isInstanceOf(JoinOperatorImpl.class, joinCityCountryOperator);
		assertNotNull(joinCityCountryOperator.getVariableName());
		assertEquals("o", joinCityCountryOperator.getVariableName());
		assertNotNull(((JoinOperatorImpl) joinCityCountryOperator).getReconcilingExpression());
		assertEquals("c.countrye = o.codee", ((JoinOperatorImpl) joinCityCountryOperator).getReconcilingExpression()); //TODO decide whether it should be the variable name here instead
		assertNotNull(joinCityCountryOperator.getLhsInput());
		assertNotNull(joinCityCountryOperator.getRhsInput());
		assertEquals(joinCityCountryOperator.getInput(), joinCityCountryOperator.getLhsInput());

		assertNotNull(joinCityCountryOperator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, joinCityCountryOperator.getDataSource());

		assertEquals(1, ((JoinOperatorImpl) joinCityCountryOperator).getPredicates().size());
		Predicate predicateJCO = ((JoinOperatorImpl) joinCityCountryOperator).getPredicates().iterator().next();
		assertEquals("countrye", predicateJCO.getSuperLexical1().getName());
		assertEquals("citye", predicateJCO.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicateJCO.getOperator());
		assertEquals("codee", predicateJCO.getSuperLexical2().getName());
		assertEquals("countrye", predicateJCO.getSuperLexical2().getParentSuperAbstract().getName());
		assertNull(predicateJCO.getLiteral1());
		assertNull(predicateJCO.getLiteral2());

		assertNotNull(joinCityCountryOperator.getResultType());
		ResultType resultTypeJCO = joinCityCountryOperator.getResultType();
		assertEquals(12, resultTypeJCO.getResultFields().size());
		String[] expectedResultFieldNamesArrayJCO = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee",
				"o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		DataType[] expectedResultFieldTypesArrayJCO = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT, DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT, DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArrayJCO = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee",
				"namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArrayJCO = { "citye", "citye", "citye", "citye", "citye", "citye", "countrye",
				"countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArrayJCO = new String[12];
		DataType[] actualResultFieldTypesArrayJCO = new DataType[12];
		String[] actualCanonicalModelConstructNamesArrayJCO = new String[12];
		String[] actualParentCanonicalModelConstructNamesArrayJCO = new String[12];

		i = 0;
		Map<String, ResultField> resultFieldsJCO = resultTypeJCO.getResultFields();
		Set<String> resultFieldNamesJCO = resultFieldsJCO.keySet();
		for (String resultFieldName : resultFieldNamesJCO) {
			actualResultFieldNamesArrayJCO[i] = resultFieldsJCO.get(resultFieldName).getFieldName();
			actualResultFieldTypesArrayJCO[i] = resultFieldsJCO.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArrayJCO[i] = resultFieldsJCO.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsJCO.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArrayJCO[i] = ((SuperLexical) resultFieldsJCO.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArrayJCO, actualResultFieldNamesArrayJCO);
		assertArrayEquals(expectedResultFieldTypesArrayJCO, actualResultFieldTypesArrayJCO);
		assertArrayEquals(expectedCanonicalModelConstructNamesArrayJCO, actualCanonicalModelConstructNamesArrayJCO);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArrayJCO, actualParentCanonicalModelConstructNamesArrayJCO);

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

		assertNotNull(evaluateExternallyOp2.getResultType());
		ResultType resultTypeEx2 = evaluateExternallyOp2.getResultType();
		assertEquals(3, resultTypeEx2.getResultFields().size());
		String[] expectedResultFieldNamesArrayEx2 = { "g.countrye", "g.namee", "g.percentagee" };
		DataType[] expectedResultFieldTypesArrayEx2 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArrayEx2 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArrayEx2 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArrayEx2 = new String[3];
		DataType[] actualResultFieldTypesArrayEx2 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArrayEx2 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArrayEx2 = new String[3];

		i = 0;
		Map<String, ResultField> resultFieldsEx2 = resultTypeEx2.getResultFields();
		Set<String> resultFieldNamesEx2 = resultFieldsEx2.keySet();
		for (String resultFieldName : resultFieldNamesEx2) {
			actualResultFieldNamesArrayEx2[i] = resultFieldsEx2.get(resultFieldName).getFieldName();
			actualResultFieldTypesArrayEx2[i] = resultFieldsEx2.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArrayEx2[i] = resultFieldsEx2.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsEx2.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArrayEx2[i] = ((SuperLexical) resultFieldsEx2.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArrayEx2, actualResultFieldNamesArrayEx2);
		assertArrayEquals(expectedResultFieldTypesArrayEx2, actualResultFieldTypesArrayEx2);
		assertArrayEquals(expectedCanonicalModelConstructNamesArrayEx2, actualCanonicalModelConstructNamesArrayEx2);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArrayEx2, actualParentCanonicalModelConstructNamesArrayEx2);

		EvaluatorOperator evaluatorOperator21 = evaluateExternallyOp2.getPlanRootEvaluatorOperator();

		isInstanceOf(ReduceOperatorImpl.class, evaluatorOperator21);
		assertNotNull(evaluatorOperator21.getVariableName());
		assertEquals("g", evaluatorOperator21.getVariableName());
		assertNotNull(evaluatorOperator21.getInput());
		assertNotNull(evaluatorOperator21.getLhsInput());
		assertEquals(evaluatorOperator21.getInput(), evaluatorOperator21.getLhsInput());
		assertNull(evaluatorOperator21.getRhsInput());
		assertEquals(3, ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().size());
		assertEquals("g.countrye, g.namee, g.percentagee", ((ReduceOperatorImpl) evaluatorOperator21).getReconcilingExpression());

		assertNotNull(evaluatorOperator21.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator21.getDataSource());

		Collection<SuperLexical> superLexicalsEx21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().values();
		String[] superLexicalNamesArrayEx21 = new String[3];
		String[] superAbstractNamesArrayEx21 = new String[3];
		i = 0;
		for (SuperLexical superLexical : superLexicalsEx21) {
			superLexicalNamesArrayEx21[i] = superLexical.getName();
			superAbstractNamesArrayEx21[i] = superLexical.getParentSuperAbstract().getName();
			i++;
		}
		String[] expectedSuperLexicalNamesArrayEx21 = { "countrye", "namee", "percentagee" };
		assertArrayEquals(expectedSuperLexicalNamesArrayEx21, superLexicalNamesArrayEx21);
		String[] expectedSuperAbstractNamesArrayEx21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };
		assertArrayEquals(expectedSuperAbstractNamesArrayEx21, superAbstractNamesArrayEx21);
		Set<String> superLexicalVariableNamesEx21 = ((ReduceOperatorImpl) evaluatorOperator21).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArrayEx21 = new String[3];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNamesEx21) {
			superLexicalVariableNamesArrayEx21[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArrayEx21 = { "g.countrye", "g.namee", "g.percentagee" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArrayEx21, superLexicalVariableNamesArrayEx21);

		assertNotNull(evaluatorOperator21.getResultType());
		ResultType resultTypeEx21 = evaluatorOperator21.getResultType();
		assertEquals(3, resultTypeEx21.getResultFields().size());
		String[] expectedResultFieldNamesArrayEx21 = { "g.countrye", "g.namee", "g.percentagee" };
		DataType[] expectedResultFieldTypesArrayEx21 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArrayEx21 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArrayEx21 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArrayEx21 = new String[3];
		DataType[] actualResultFieldTypesArrayEx21 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArrayEx21 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArrayEx21 = new String[3];

		i = 0;
		Map<String, ResultField> resultFieldsEx21 = resultTypeEx21.getResultFields();
		Set<String> resultFieldNamesEx21 = resultFieldsEx21.keySet();
		for (String resultFieldName : resultFieldNamesEx21) {
			actualResultFieldNamesArrayEx21[i] = resultFieldsEx21.get(resultFieldName).getFieldName();
			actualResultFieldTypesArrayEx21[i] = resultFieldsEx21.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArrayEx21[i] = resultFieldsEx21.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFieldsEx21.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArrayEx21[i] = ((SuperLexical) resultFieldsEx21.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArrayEx21, actualResultFieldNamesArrayEx21);
		assertArrayEquals(expectedResultFieldTypesArrayEx21, actualResultFieldTypesArrayEx21);
		assertArrayEquals(expectedCanonicalModelConstructNamesArrayEx21, actualCanonicalModelConstructNamesArrayEx21);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArrayEx21, actualParentCanonicalModelConstructNamesArrayEx21);

		EvaluatorOperator evaluatorOperator211 = evaluatorOperator21.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, evaluatorOperator211);
		assertNotNull(evaluatorOperator211.getVariableName());
		assertEquals("g", evaluatorOperator211.getVariableName());
		assertNull(evaluatorOperator211.getLhsInput());
		assertNull(evaluatorOperator211.getRhsInput());
		assertEquals("ethnicgroupe", ((ScanOperatorImpl) evaluatorOperator211).getSuperAbstract().getName());
		assertNull(((ScanOperatorImpl) evaluatorOperator211).getReconcilingExpression());
		assertEquals(0, ((ScanOperatorImpl) evaluatorOperator211).getPredicates().size());

		assertNotNull(evaluatorOperator211.getDataSource());
		assertEquals(mondialLanguageEconomyReligionOfCountriesEuropeWithRenameDS, evaluatorOperator211.getDataSource());

		assertNotNull(evaluatorOperator211.getResultType());
		ResultType resultType4 = evaluatorOperator211.getResultType();
		assertEquals(3, resultType4.getResultFields().size());
		String[] expectedResultFieldNamesArray4 = { "g.countrye", "g.namee", "g.percentagee" };
		DataType[] expectedResultFieldTypesArray4 = { DataType.STRING, DataType.STRING, DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray4 = { "countrye", "namee", "percentagee" };
		String[] exptectedParentCanonicalModelConstructNamesArray4 = { "ethnicgroupe", "ethnicgroupe", "ethnicgroupe" };

		String[] actualResultFieldNamesArray4 = new String[3];
		DataType[] actualResultFieldTypesArray4 = new DataType[3];
		String[] actualCanonicalModelConstructNamesArray4 = new String[3];
		String[] actualParentCanonicalModelConstructNamesArray4 = new String[3];

		i = 0;
		Map<String, ResultField> resultFields4 = resultType4.getResultFields();
		Set<String> resultFieldNames4 = resultFields4.keySet();
		for (String resultFieldName : resultFieldNames4) {
			actualResultFieldNamesArray4[i] = resultFields4.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray4[i] = resultFields4.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray4[i] = resultFields4.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields4.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray4[i] = ((SuperLexical) resultFields4.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray4, actualResultFieldNamesArray4);
		assertArrayEquals(expectedResultFieldTypesArray4, actualResultFieldTypesArray4);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray4, actualCanonicalModelConstructNamesArray4);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray4, actualParentCanonicalModelConstructNamesArray4);

		EvaluatorOperator input11Operator = joinCityCountryOperator.getLhsInput();

		isInstanceOf(ScanOperatorImpl.class, input11Operator);
		assertNotNull(input11Operator.getVariableName());
		assertEquals("c", input11Operator.getVariableName());
		assertNull(input11Operator.getLhsInput());
		assertNull(input11Operator.getRhsInput());
		assertEquals("citye", ((ScanOperatorImpl) input11Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("c.namee = 'Manchester'", ((ScanOperatorImpl) input11Operator).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) input11Operator).getAndOr());

		assertNotNull(input11Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input11Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input11Operator).getPredicates().size());
		Predicate predicate2 = ((ScanOperatorImpl) input11Operator).getPredicates().iterator().next();
		assertEquals("namee", predicate2.getSuperLexical1().getName());
		assertEquals("citye", predicate2.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate2.getOperator());
		assertEquals("'Manchester'", predicate2.getLiteral2());
		assertEquals("and", predicate2.getAndOr());
		assertNull(predicate2.getLiteral1());
		assertNull(predicate2.getSuperLexical2());

		assertNotNull(input11Operator.getResultType());
		ResultType resultType5 = input11Operator.getResultType();
		assertEquals(6, resultType5.getResultFields().size());
		String[] expectedResultFieldNamesArray5 = { "c.namee", "c.countrye", "c.provincee", "c.populatione", "c.longitudee", "c.latitudee" };
		DataType[] expectedResultFieldTypesArray5 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray5 = { "namee", "countrye", "provincee", "populatione", "longitudee", "latitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray5 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray5 = new String[6];
		DataType[] actualResultFieldTypesArray5 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray5 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray5 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields5 = resultType5.getResultFields();
		Set<String> resultFieldNames5 = resultFields5.keySet();
		for (String resultFieldName : resultFieldNames5) {
			actualResultFieldNamesArray5[i] = resultFields5.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray5[i] = resultFields5.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray5[i] = resultFields5.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields5.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray5[i] = ((SuperLexical) resultFields5.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray5, actualResultFieldNamesArray5);
		assertArrayEquals(expectedResultFieldTypesArray5, actualResultFieldTypesArray5);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray5, actualCanonicalModelConstructNamesArray5);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray5, actualParentCanonicalModelConstructNamesArray5);

		EvaluatorOperator input12Operator = joinCityCountryOperator.getRhsInput();

		isInstanceOf(ScanOperatorImpl.class, input12Operator);
		assertNotNull(input12Operator.getVariableName());
		assertEquals("o", input12Operator.getVariableName());
		assertNull(input12Operator.getLhsInput());
		assertNull(input12Operator.getRhsInput());
		assertEquals("countrye", ((ScanOperatorImpl) input12Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("o.codee = 'GB'", ((ScanOperatorImpl) input12Operator).getReconcilingExpression());
		assertEquals("and", ((ScanOperatorImpl) input12Operator).getAndOr());

		assertNotNull(input12Operator.getDataSource());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input12Operator.getDataSource());

		assertEquals(1, ((ScanOperatorImpl) input12Operator).getPredicates().size());
		Predicate predicate3 = ((ScanOperatorImpl) input12Operator).getPredicates().iterator().next();
		assertEquals("codee", predicate3.getSuperLexical1().getName());
		assertEquals("countrye", predicate3.getSuperLexical1().getParentSuperAbstract().getName());
		assertEquals("=", predicate3.getOperator());
		assertEquals("'GB'", predicate3.getLiteral2());
		assertEquals("and", predicate3.getAndOr());
		assertNull(predicate3.getLiteral1());
		assertNull(predicate3.getSuperLexical2());

		assertNotNull(input12Operator.getResultType());
		ResultType resultType6 = input12Operator.getResultType();
		assertEquals(6, resultType6.getResultFields().size());
		String[] expectedResultFieldNamesArray6 = { "o.namee", "o.codee", "o.capitale", "o.provincee", "o.areae", "o.populatione" };
		DataType[] expectedResultFieldTypesArray6 = { DataType.STRING, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.INTEGER };
		String[] expectedCanonicalModelConstructNamesArray6 = { "namee", "codee", "capitale", "provincee", "areae", "populatione" };
		String[] exptectedParentCanonicalModelConstructNamesArray6 = { "countrye", "countrye", "countrye", "countrye", "countrye", "countrye" };

		String[] actualResultFieldNamesArray6 = new String[6];
		DataType[] actualResultFieldTypesArray6 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray6 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray6 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields6 = resultType6.getResultFields();
		Set<String> resultFieldNames6 = resultFields6.keySet();
		for (String resultFieldName : resultFieldNames6) {
			actualResultFieldNamesArray6[i] = resultFields6.get(resultFieldName).getFieldName();
			actualResultFieldTypesArray6[i] = resultFields6.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray6[i] = resultFields6.get(resultFieldName).getCanonicalModelConstruct().getName();
			isInstanceOf(SuperLexical.class, resultFields6.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray6[i] = ((SuperLexical) resultFields6.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		assertArrayEquals(expectedResultFieldNamesArray6, actualResultFieldNamesArray6);
		assertArrayEquals(expectedResultFieldTypesArray6, actualResultFieldTypesArray6);
		assertArrayEquals(expectedCanonicalModelConstructNamesArray6, actualCanonicalModelConstructNamesArray6);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray6, actualParentCanonicalModelConstructNamesArray6);

	}

}
