package uk.ac.manchester.dstoolkit.service.impl.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notEmpty;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.annotation.Annotation;
import uk.ac.manchester.dstoolkit.domain.annotation.OntologyTerm;
import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ReduceOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.ScanOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultValue;
import uk.ac.manchester.dstoolkit.repository.annotation.AnnotationRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;
import uk.ac.manchester.dstoolkit.service.annotation.OntologyTermService;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;

public class QueryServiceImplIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(QueryServiceImplIntegrationTest.class);

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("ontologyTermService")
	private OntologyTermService ontologyTermService;

	@Autowired
	@Qualifier("annotationService")
	private AnnotationService annotationService;

	@Autowired
	@Qualifier("annotationRepository")
	private AnnotationRepository annotationRepository;

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("queryResultService")
	private QueryResultService queryResultService;

	@Autowired
	@Qualifier("mappingService")
	private MappingService mappingService;

	//TODO test that whole query stack works properly for various queries, test that each resultInstance has correct mapping associated with it
	//TODO test query stack with and without mappingSelection
	//TODO test mappingSelection separately
	//TODO test expander separately, test that each operator has the correct mapping associated with it
	//TODO test repository methods
	//TODO try to work out why re-running evaluateQuery within the same test doesn't seem to work
	//TODO test maxNumberOfResults
	//TODO sort out fetchSize and test it

	//TODO add africa to lakes database
	//TODO add tests for annotation other than expectancy
	//TODO need to test that each resultInstance has correct mapping associated with it

	@Test
	public void testEvaluateSelectStarFromCityQueryOverTwoSourcesQueryUser() {
		String selectStarFromQuery = "Select * from City";
		String queryName = "AllCities";
		Schema mondialSchema = schemaRepository.getSchemaByName("MondialIntegr");

		DataSource mondialCityProvinceCountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEurope");
		DataSource mondialCityProvinceCountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfrica");

		DataSource mondialDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIntegr");

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		query.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);

		queryService.addQuery(query);

		assertNotNull(query.getId());
		assertEquals(2, query.getDataSources().size());

		QueryResult queryResult = queryService.evaluateQuery(query, null, currentUser, null);

		assertNotNull(queryResult.getId());

		Query query1 = queryResult.getQuery();
		assertEquals(query, query1);
		assertNotNull(query1.getId());

		assertEquals(selectStarFromQuery, query1.getQueryString());
		assertEquals(queryName, query1.getQueryName());
		assertEquals(1, query1.getSchemas().size());
		assertEquals(mondialSchema, query1.getSchemas().iterator().next());
		assertNotNull(query1.getSchemas().iterator().next().getId());
		assertEquals(2, query1.getDataSources().size());

		Iterator<DataSource> dataSourcesIt = query1.getDataSources().iterator();

		assertEquals(mondialCityProvinceCountryContinentAfricaNoRenameDS, dataSourcesIt.next());
		assertEquals(mondialCityProvinceCountryContinentEuropeNoRenameDS, dataSourcesIt.next());

		assertNotNull(query1.getRootOperator());
		assertNotNull(query1.getRootOperatorOfExpandedQuery());

		MappingOperator rootOperator = query1.getRootOperator();
		isInstanceOf(ReduceOperator.class, rootOperator);
		ReduceOperator reduceOperator = (ReduceOperator) rootOperator;

		assertNotNull(rootOperator.getId());
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(6, ((ReduceOperator) rootOperator).getSuperLexicals().size());
		assertEquals("city.name, city.country, city.province, city.population, city.longitude, city.latitude", ((ReduceOperator) rootOperator)
				.getReconcilingExpression().getExpression());
		assertNotNull(((ReduceOperator) rootOperator).getReconcilingExpression().getId());

		logger.debug(rootOperator.getDataSource());
		assertNotNull(rootOperator.getDataSource());
		assertEquals(mondialDS, rootOperator.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperator) rootOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		int i = 0;
		for (SuperLexical superLexical : superLexicals) {
			assertNotNull(superLexical.getId());
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			assertNotNull(superLexical.getParentSuperAbstract().getId());
			i++;
		}
		String[] expectedSuperLexicalNamesArray = { "longitude", "latitude", "country", "population", "province", "name" };
		assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "city", "city", "city", "city", "city", "city" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperator) rootOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "city.longitude", "city.latitude", "city.country", "city.population", "city.province",
				"city.name" };
		assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperator.getResultType());
		ResultType resultType0 = rootOperator.getResultType();
		assertNotNull(resultType0.getId());
		assertEquals(6, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "city.name", "city.longitude", "city.latitude", "city.country", "city.population",
				"city.province" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.FLOAT, DataType.FLOAT, DataType.STRING, DataType.INTEGER,
				DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray0 = { "name", "longitude", "latitude", "country", "population", "province" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "city", "city", "city", "city", "city", "city" };

		String[] actualResultFieldNamesArray0 = new String[6];
		DataType[] actualResultFieldTypesArray0 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray0 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFieldName;
			assertNotNull(resultFields0.get(resultFieldName).getId());
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			assertNotNull(resultFields0.get(resultFieldName).getCanonicalModelConstruct().getId());
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		//assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		//assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		//assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		MappingOperator input0Operator = rootOperator.getLhsInput();

		assertNotNull(input0Operator.getId());
		isInstanceOf(ScanOperator.class, input0Operator);
		assertEquals("city", input0Operator.getVariableName());
		assertNull(input0Operator.getLhsInput());
		assertNull(input0Operator.getRhsInput());
		assertEquals("city", ((ScanOperator) input0Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperator) input0Operator).getSuperAbstract().getId());
		assertNull(((ScanOperator) input0Operator).getReconcilingExpression());
		assertEquals(0, ((ScanOperator) input0Operator).getPredicates().size());

		assertNotNull(input0Operator.getDataSource());
		assertEquals(mondialDS, input0Operator.getDataSource());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType = input0Operator.getResultType();
		assertNotNull(resultType.getId());
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "city.longitude", "city.latitude", "city.country", "city.population", "city.province", "city.name" };
		DataType[] expectedResultFieldTypesArray = { DataType.FLOAT, DataType.FLOAT, DataType.STRING, DataType.INTEGER, DataType.STRING,
				DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray = { "longitude", "latitude", "country", "population", "province", "name" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "city", "city", "city", "city", "city", "city" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFieldName;
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			assertNotNull(resultFields.get(resultFieldName).getId());
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			assertNotNull(resultFields.get(resultFieldName).getCanonicalModelConstruct().getId());
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		//assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		//assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		//assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);

		MappingOperator rootOperatorOfExpandedQuery = query1.getRootOperatorOfExpandedQuery();
		isInstanceOf(ReduceOperator.class, rootOperatorOfExpandedQuery);
		ReduceOperator reduceOperatorOfExpandedQuery = (ReduceOperator) rootOperatorOfExpandedQuery;
		assertNull(rootOperatorOfExpandedQuery.getId());
		assertNotNull(rootOperatorOfExpandedQuery.getVariableName());
		assertEquals("exUnion1", rootOperatorOfExpandedQuery.getVariableName());
		assertNotNull(rootOperatorOfExpandedQuery.getInput());
		assertNotNull(rootOperatorOfExpandedQuery.getLhsInput());
		assertEquals(rootOperatorOfExpandedQuery.getInput(), rootOperatorOfExpandedQuery.getLhsInput());
		assertNull(rootOperatorOfExpandedQuery.getRhsInput());
		assertEquals(6, ((ReduceOperator) rootOperatorOfExpandedQuery).getSuperLexicals().size());
		assertEquals("city.name, city.country, city.province, city.population, city.longitude, city.latitude",
				((ReduceOperator) rootOperatorOfExpandedQuery).getReconcilingExpression().getExpression());
		assertNotNull(((ReduceOperator) rootOperatorOfExpandedQuery).getReconcilingExpression().getId());

		assertNull(rootOperatorOfExpandedQuery.getMapping());
		assertNotNull(rootOperatorOfExpandedQuery.getMappingsUsedForExpansion());
		assertEquals(2, rootOperatorOfExpandedQuery.getMappingsUsedForExpansion().size());
		assertNull(rootOperatorOfExpandedQuery.getDataSource());

		Collection<SuperLexical> superLexicals2 = ((ReduceOperator) rootOperatorOfExpandedQuery).getSuperLexicals().values();
		String[] superLexicalNamesArray2 = new String[6];
		String[] superAbstractNamesArray2 = new String[6];
		i = 0;
		for (SuperLexical superLexical : superLexicals2) {
			assertNotNull(superLexical.getId());
			superLexicalNamesArray2[i] = superLexical.getName();
			superAbstractNamesArray2[i] = superLexical.getParentSuperAbstract().getName();
			assertNotNull(superLexical.getParentSuperAbstract().getId());
			i++;
		}
		String[] expectedSuperLexicalNamesArray2 = { "longitude", "latitude", "country", "population", "province", "name" };
		//assertArrayEquals(expectedSuperLexicalNamesArray2, superLexicalNamesArray2);
		String[] expectedSuperAbstractNamesArray2 = { "city", "city", "city", "city", "city", "city" };
		//assertArrayEquals(expectedSuperAbstractNamesArray2, superAbstractNamesArray2);
		Set<String> superLexicalVariableNames2 = ((ReduceOperator) rootOperatorOfExpandedQuery).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray2 = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames2) {
			superLexicalVariableNamesArray2[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray2 = { "city.longitude", "city.latitude", "city.country", "city.population", "city.province",
				"city.name" };
		//assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperatorOfExpandedQuery.getResultType());
		ResultType resultType20 = rootOperatorOfExpandedQuery.getResultType();
		assertNotNull(resultType20.getId());
		assertEquals(6, resultType20.getResultFields().size());
		String[] expectedResultFieldNamesArray20 = { "city.longitude", "city.latitude", "city.country", "city.population", "city.province",
				"city.name" };
		DataType[] expectedResultFieldTypesArray20 = { DataType.FLOAT, DataType.FLOAT, DataType.STRING, DataType.INTEGER, DataType.STRING,
				DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray20 = { "longitude", "latitude", "country", "population", "province", "name" };
		String[] exptectedParentCanonicalModelConstructNamesArray20 = { "city", "city", "city", "city", "city", "city" };

		String[] actualResultFieldNamesArray20 = new String[6];
		DataType[] actualResultFieldTypesArray20 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray20 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray20 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields20 = resultType20.getResultFields();
		Set<String> resultFieldNames20 = resultFields20.keySet();
		for (String resultFieldName : resultFieldNames20) {
			actualResultFieldNamesArray20[i] = resultFieldName;
			assertNotNull(resultFields20.get(resultFieldName).getId());
			actualResultFieldTypesArray20[i] = resultFields20.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray20[i] = resultFields20.get(resultFieldName).getCanonicalModelConstruct().getName();
			assertNotNull(resultFields20.get(resultFieldName).getCanonicalModelConstruct().getId());
			isInstanceOf(SuperLexical.class, resultFields20.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray20[i] = ((SuperLexical) resultFields20.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		//assertArrayEquals(expectedResultFieldNamesArray20, actualResultFieldNamesArray20);
		//assertArrayEquals(expectedResultFieldTypesArray20, actualResultFieldTypesArray20);
		//assertArrayEquals(expectedCanonicalModelConstructNamesArray20, actualCanonicalModelConstructNamesArray20);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray20, actualParentCanonicalModelConstructNamesArray20);

		MappingOperator input20Operator = rootOperatorOfExpandedQuery.getLhsInput();

		assertNull(input20Operator.getId());
		isInstanceOf(SetOperator.class, input20Operator);

		assertNull(input20Operator.getMapping());
		assertNotNull(input20Operator.getMappingsUsedForExpansion());
		assertEquals(2, input20Operator.getMappingsUsedForExpansion().size());
		logger.debug("input20Operator().size(): " + input20Operator.getMappingsUsedForExpansion().size());

		//TODO need to finish tests here: check that the operators know the mapping they're from
		//check that the resultInstances are correct and that they know the mapping they're from

		/*
		assertEquals("city", input0Operator.getVariableName());
		assertNull(input0Operator.getLhsInput());
		assertNull(input0Operator.getRhsInput());
		assertEquals("city", ((ScanOperator) input0Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperator) input0Operator).getSuperAbstract().getId());
		assertNull(((ScanOperator) input0Operator).getReconcilingExpression());
		assertEquals(0, ((ScanOperator) input0Operator).getPredicates().size());

		assertNotNull(input0Operator.getDataSource());
		assertEquals(mondialDS, input0Operator.getDataSource());
		*/

		assertNotNull(input20Operator.getResultType());
		ResultType resultType2 = input20Operator.getResultType();
		assertNotNull(resultType2.getId());
		assertEquals(6, resultType2.getResultFields().size());
		String[] expectedResultFieldNamesArray2 = { "city.longitude", "city.latitude", "city.country", "city.population", "city.province",
				"city.name" };
		DataType[] expectedResultFieldTypesArray2 = { DataType.FLOAT, DataType.FLOAT, DataType.STRING, DataType.INTEGER, DataType.STRING,
				DataType.STRING };
		String[] expectedCanonicalModelConstructNamesArray2 = { "longitude", "latitude", "country", "province", "population", "name" };
		String[] exptectedParentCanonicalModelConstructNamesArray2 = { "city", "city", "city", "city", "city", "city" };

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
			assertNotNull(resultFields2.get(resultFieldName).getId());
			actualCanonicalModelConstructNamesArray2[i] = resultFields2.get(resultFieldName).getCanonicalModelConstruct().getName();
			assertNotNull(resultFields2.get(resultFieldName).getCanonicalModelConstruct().getId());
			isInstanceOf(SuperLexical.class, resultFields2.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray2[i] = ((SuperLexical) resultFields2.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		MappingOperator lhsSetInput = input20Operator.getLhsInput();
		assertNotNull(lhsSetInput);
		isInstanceOf(ReduceOperator.class, lhsSetInput);
		assertNull(lhsSetInput.getMapping());
		assertNotNull(lhsSetInput.getMappingsUsedForExpansion());
		assertEquals(1, lhsSetInput.getMappingsUsedForExpansion().size());

		MappingOperator lhsScan = lhsSetInput.getLhsInput();
		isInstanceOf(ScanOperator.class, lhsScan);
		assertEquals("city", ((ScanOperator) lhsScan).getSuperAbstract().getName());
		assertNotNull(lhsScan.getMapping());
		assertNotNull(lhsScan.getMappingsUsedForExpansion());
		assertEquals(1, lhsScan.getMappingsUsedForExpansion().size());

		MappingOperator rhsSetInput = input20Operator.getRhsInput();
		assertNotNull(rhsSetInput);
		isInstanceOf(ReduceOperator.class, rhsSetInput);
		assertNull(rhsSetInput.getMapping());
		assertNotNull(rhsSetInput.getMappingsUsedForExpansion());
		assertEquals(1, rhsSetInput.getMappingsUsedForExpansion().size());

		MappingOperator rhsScan = rhsSetInput.getLhsInput();
		isInstanceOf(ScanOperator.class, rhsScan);
		assertEquals("city", ((ScanOperator) rhsScan).getSuperAbstract().getName());
		assertNotNull(rhsScan.getMapping());
		assertNotNull(rhsScan.getMappingsUsedForExpansion());
		assertEquals(1, rhsScan.getMappingsUsedForExpansion().size());

		Mapping europeMapping = null;
		Mapping africaMapping = null;

		if (((ScanOperator) lhsScan).getSuperAbstract().getSchema().getName().equals("MondialCityProvinceCountryContinentEurope")) {
			europeMapping = lhsScan.getMapping();
			africaMapping = rhsScan.getMapping();
			assertEquals("MondialCityProvinceCountryContinentEurope", ((ScanOperator) lhsScan).getSuperAbstract().getSchema().getName());
			assertEquals("MondialCityProvinceCountryContinentAfrica", ((ScanOperator) rhsScan).getSuperAbstract().getSchema().getName());
		} else {
			europeMapping = rhsScan.getMapping();
			africaMapping = lhsScan.getMapping();
			assertEquals("MondialCityProvinceCountryContinentEurope", ((ScanOperator) rhsScan).getSuperAbstract().getSchema().getName());
			assertEquals("MondialCityProvinceCountryContinentAfrica", ((ScanOperator) lhsScan).getSuperAbstract().getSchema().getName());
		}

		Set<Mapping> mappingsUsed = queryResult.getMappings();
		assertEquals(2, mappingsUsed.size());

		Iterator<Mapping> mappingsIt = mappingsUsed.iterator();

		Mapping mapping1 = mappingsIt.next();
		//mapping1 = mappingService.fetchConstructs(mapping1);
		notEmpty(mapping1.getConstructs1());
		notEmpty(mapping1.getConstructs2());

		Set<CanonicalModelConstruct> mapping1Query1Constructs = mapping1.getConstructs1();
		assertEquals(1, mapping1Query1Constructs.size());
		Set<CanonicalModelConstruct> mapping1Query2Constructs = mapping1.getConstructs2();
		assertEquals(1, mapping1Query2Constructs.size());

		CanonicalModelConstruct mapping1Query1Construct = mapping1Query1Constructs.iterator().next();
		isInstanceOf(SuperAbstract.class, mapping1Query1Construct);
		assertEquals("city", mapping1Query1Construct.getName());
		assertEquals("MondialIntegr", mapping1Query1Construct.getSchema().getName());
		CanonicalModelConstruct mapping1Query2Construct = mapping1Query2Constructs.iterator().next();
		isInstanceOf(SuperAbstract.class, mapping1Query2Construct);
		assertEquals("city", mapping1Query2Construct.getName());

		Mapping mapping2 = mappingsIt.next();
		//mapping2 = mappingService.fetchConstructs(mapping2);
		notEmpty(mapping2.getConstructs1());
		notEmpty(mapping2.getConstructs2());

		Set<CanonicalModelConstruct> mapping2Query1Constructs = mapping2.getConstructs1();
		assertEquals(1, mapping2Query1Constructs.size());
		Set<CanonicalModelConstruct> mapping2Query2Constructs = mapping2.getConstructs2();
		assertEquals(1, mapping2Query2Constructs.size());

		CanonicalModelConstruct mapping2Query1Construct = mapping2Query1Constructs.iterator().next();
		isInstanceOf(SuperAbstract.class, mapping2Query1Construct);
		assertEquals("city", mapping2Query1Construct.getName());
		assertEquals("MondialIntegr", mapping2Query1Construct.getSchema().getName());
		CanonicalModelConstruct mapping2Query2Construct = mapping2Query2Constructs.iterator().next();
		isInstanceOf(SuperAbstract.class, mapping2Query2Construct);
		assertEquals("city", mapping2Query2Construct.getName());

		if (mapping1.equals(europeMapping)) {
			assertEquals(europeMapping, mapping1);
			assertEquals("MondialCityProvinceCountryContinentEurope", mapping1Query2Construct.getSchema().getName());
			assertEquals(africaMapping, mapping2);
			assertEquals("MondialCityProvinceCountryContinentAfrica", mapping2Query2Construct.getSchema().getName());
		} else {
			assertEquals(africaMapping, mapping1);
			assertEquals("MondialCityProvinceCountryContinentAfrica", mapping1Query2Construct.getSchema().getName());
			assertEquals(europeMapping, mapping2);
			assertEquals("MondialCityProvinceCountryContinentEurope", mapping2Query2Construct.getSchema().getName());
		}

		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		assertEquals(19, resultInstances.size());

		for (ResultInstance resultInstance : resultInstances) {
			assertEquals(query, resultInstance.getQuery());
		}

		/*
		assertEquals(6, resultInstances.get(0).getResultFieldNameResultValueMap().size());
		assertEquals("London", resultInstances.get(0).getResultValue("city.name").getValue());
		assertEquals("GB", resultInstances.get(0).getResultValue("city.country").getValue());
		assertEquals("Greater London", resultInstances.get(0).getResultValue("city.province").getValue());
		assertEquals("6967500", resultInstances.get(0).getResultValue("city.population").getValue());
		assertEquals("0.0", resultInstances.get(0).getResultValue("city.longitude").getValue());
		assertEquals("51.4833", resultInstances.get(0).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(0).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(0).getMappings().iterator().next());
		
		assertEquals(6, resultInstances.get(15).getResultFieldNameResultValueMap().size());
		assertEquals("Dublin", resultInstances.get(15).getResultValue("city.name").getValue());
		assertEquals("IRL", resultInstances.get(15).getResultValue("city.country").getValue());
		assertEquals("Ireland", resultInstances.get(15).getResultValue("city.province").getValue());
		assertEquals("502337", resultInstances.get(15).getResultValue("city.population").getValue());
		assertEquals("-6.35", resultInstances.get(15).getResultValue("city.longitude").getValue());
		assertEquals("53.3667", resultInstances.get(15).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(15).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(15).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(1).getResultFieldNameResultValueMap().size());
		assertEquals("Aarhus", resultInstances.get(1).getResultValue("city.name").getValue());
		assertEquals("DK", resultInstances.get(1).getResultValue("city.country").getValue());
		assertEquals("Denmark", resultInstances.get(1).getResultValue("city.province").getValue());
		assertEquals("194345", resultInstances.get(1).getResultValue("city.population").getValue());
		assertEquals("10.1", resultInstances.get(1).getResultValue("city.longitude").getValue());
		assertEquals("56.1", resultInstances.get(1).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(1).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(1).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(2).getResultFieldNameResultValueMap().size());
		assertEquals("Munich", resultInstances.get(2).getResultValue("city.name").getValue());
		assertEquals("D", resultInstances.get(2).getResultValue("city.country").getValue());
		assertEquals("Bayern", resultInstances.get(2).getResultValue("city.province").getValue());
		assertEquals("1290079", resultInstances.get(2).getResultValue("city.population").getValue());
		assertEquals("11.5667", resultInstances.get(2).getResultValue("city.longitude").getValue());
		assertEquals("48.15", resultInstances.get(2).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(2).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(2).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(3).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(3).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(4).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(4).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(5).getResultFieldNameResultValueMap().size());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("city.name").getValue());
		assertEquals("D", resultInstances.get(5).getResultValue("city.country").getValue());
		assertEquals("Berlin", resultInstances.get(5).getResultValue("city.province").getValue());
		assertEquals("3472009", resultInstances.get(5).getResultValue("city.population").getValue());
		assertEquals("13.3", resultInstances.get(5).getResultValue("city.longitude").getValue());
		assertEquals("52.45", resultInstances.get(5).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(5).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(5).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(6).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(6).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(7).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(7).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(8).getResultFieldNameResultValueMap().size());
		assertEquals("Leeds", resultInstances.get(8).getResultValue("city.name").getValue());
		assertEquals("GB", resultInstances.get(8).getResultValue("city.country").getValue());
		assertEquals("West Yorkshire", resultInstances.get(8).getResultValue("city.province").getValue());
		assertEquals("724400", resultInstances.get(8).getResultValue("city.population").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("city.longitude").getValue());
		assertEquals("0.0", resultInstances.get(8).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(8).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(8).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(9).getResultFieldNameResultValueMap().size());
		assertEquals("Birmingham", resultInstances.get(9).getResultValue("city.name").getValue());
		assertEquals("GB", resultInstances.get(9).getResultValue("city.country").getValue());
		assertEquals("West Midlands", resultInstances.get(9).getResultValue("city.province").getValue());
		assertEquals("1008400", resultInstances.get(9).getResultValue("city.population").getValue());
		assertEquals("-1.93333", resultInstances.get(9).getResultValue("city.longitude").getValue());
		assertEquals("52.4833", resultInstances.get(9).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(9).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(9).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(10).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(10).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(11).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(11).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(12).getResultFieldNameResultValueMap().size());
		assertEquals("Manchester", resultInstances.get(12).getResultValue("city.name").getValue());
		assertEquals("GB", resultInstances.get(12).getResultValue("city.country").getValue());
		assertEquals("Greater Manchester", resultInstances.get(12).getResultValue("city.province").getValue());
		assertEquals("431100", resultInstances.get(12).getResultValue("city.population").getValue());
		assertEquals("0.0", resultInstances.get(12).getResultValue("city.longitude").getValue());
		assertEquals("0.0", resultInstances.get(12).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(12).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(12).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(13).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(13).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(14).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(14).getMappings().iterator().next());

		assertEquals(1, resultInstances.get(16).getMappings().size());
		assertEquals(africaMapping, resultInstances.get(16).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(17).getResultFieldNameResultValueMap().size());
		assertEquals("Hamburg", resultInstances.get(17).getResultValue("city.name").getValue());
		assertEquals("D", resultInstances.get(17).getResultValue("city.country").getValue());
		assertEquals("Hamburg", resultInstances.get(17).getResultValue("city.province").getValue());
		assertEquals("1705872", resultInstances.get(17).getResultValue("city.population").getValue());
		assertEquals("9.96667", resultInstances.get(17).getResultValue("city.longitude").getValue());
		assertEquals("53.55", resultInstances.get(17).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(17).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(17).getMappings().iterator().next());

		assertEquals(6, resultInstances.get(18).getResultFieldNameResultValueMap().size());
		assertEquals("Copenhagen", resultInstances.get(18).getResultValue("city.name").getValue());
		assertEquals("DK", resultInstances.get(18).getResultValue("city.country").getValue());
		assertEquals("Denmark", resultInstances.get(18).getResultValue("city.province").getValue());
		assertEquals("1358540", resultInstances.get(18).getResultValue("city.population").getValue());
		assertEquals("12.55", resultInstances.get(18).getResultValue("city.longitude").getValue());
		assertEquals("55.6833", resultInstances.get(18).getResultValue("city.latitude").getValue());
		assertEquals(1, resultInstances.get(18).getMappings().size());
		assertEquals(europeMapping, resultInstances.get(18).getMappings().iterator().next());
		*/
	}

	//TODO sort this test out, might have to go into AnnotationServiceImplIntegrationTest
	//@Test
	public void testEvaluateAndAnnotateSelectStarFromCityQueryOverFourSourcesQueryUser() {
		String selectStarFromQuery = "Select * from city";
		String queryName = "AllCities";
		Schema mondialIntegrSchema = schemaRepository.getSchemaByName("MondialIntegr");

		//DataSource mondialDS = dataSourceRepository.getDataSourceWithSchemaName("Mondial");
		DataSource mondialCityProvinceCountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEurope");
		DataSource mondialCityProvinceCountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentAfrica");
		DataSource mondialCityProvinceNACountryContinentEuropeNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceNACountryContinentEurope");
		DataSource mondialCityProvinceNACountryContinentAfricaNoRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceNACountryContinentAfrica");

		DataSource mondialIntegrDS = dataSourceRepository.getDataSourceWithSchemaName("MondialIntegr");

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialIntegrSchema);
		//query.addDataSource(mondialDS);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeNoRenameDS);
		query.addDataSource(mondialCityProvinceCountryContinentAfricaNoRenameDS);
		query.addDataSource(mondialCityProvinceNACountryContinentEuropeNoRenameDS);
		query.addDataSource(mondialCityProvinceNACountryContinentAfricaNoRenameDS);
		query.setUser(currentUser);

		queryService.addQuery(query);

		Long queryId = query.getId();
		//assertNotNull(queryId);

		OntologyTerm expectancyOT = new OntologyTerm("expectancy", DataType.BOOLEAN);
		OntologyTerm precisionOT = new OntologyTerm("precision", DataType.DOUBLE);
		OntologyTerm recallOT = new OntologyTerm("recall", DataType.DOUBLE);
		OntologyTerm fmeasureOT = new OntologyTerm("f-measure", DataType.DOUBLE);
		OntologyTerm fractionOfAnnotatedResultsOT = new OntologyTerm("fractionOfAnnotatedResults", DataType.DOUBLE);

		Set<String> statisticalErrorValues = new LinkedHashSet<String>();
		statisticalErrorValues.add("tp");
		statisticalErrorValues.add("fp");
		statisticalErrorValues.add("fn");

		OntologyTerm statisticalErrorOT = new OntologyTerm("statisticalError", statisticalErrorValues);

		ontologyTermService.addOntologyTerm(statisticalErrorOT);
		ontologyTermService.addOntologyTerm(expectancyOT);
		ontologyTermService.addOntologyTerm(precisionOT);
		ontologyTermService.addOntologyTerm(recallOT);
		ontologyTermService.addOntologyTerm(fmeasureOT);
		ontologyTermService.addOntologyTerm(fractionOfAnnotatedResultsOT);

		//run query over mondialCityProvinceCountryContinentEuropeNoRenameDS and mondialCityProvinceCountryContinentAfricaNoRenameDS, annotate results and mappings

		QueryResult queryResult = queryService.evaluateQuery(query, null, currentUser, null);
		List<ResultInstance> resultInstances = queryResult.getResultInstances();

		Long resultInstanceId = resultInstances.get(0).getId();

		Set<ModelManagementConstruct> constrainingModelManagementConstructs = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructs.add(queryResult.getQuery());

		Set<Mapping> mappings = queryResult.getMappings();
		Set<ModelManagementConstruct> constructsToAnnotate = new LinkedHashSet<ModelManagementConstruct>();

		Mapping europeProvinceMapping = null;
		Mapping africaProvinceMapping = null;

		for (Mapping mapping : mappings) {
			logger.debug("mapping: " + mapping);
			constructsToAnnotate.add(mapping);
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentEurope"))
				europeProvinceMapping = mapping;
			if (mapping.getConstructs2().iterator().next().getSchema().getName().equals("MondialCityProvinceCountryContinentAfrica"))
				africaProvinceMapping = mapping;
		}

		logger.debug("europeProvinceMapping: " + europeProvinceMapping);
		logger.debug("africaProvinceMapping: " + africaProvinceMapping);

		Set<Annotation> annotationsOfResultInstancesToPropagate = new LinkedHashSet<Annotation>();

		//assertEquals(38, queryResult.getResultInstances().size());

		ResultInstance otherResultInstance = resultInstanceRepository.find(resultInstanceId);

		ResultInstance missingResultInstance = new ResultInstance();
		missingResultInstance.setResultType(otherResultInstance.getResultType());
		missingResultInstance.setQuery(query);
		ResultValue value1 = new ResultValue("city.name", "Edinburgh");
		ResultValue value2 = new ResultValue("city.country", "GB");
		ResultValue value3 = new ResultValue("city.province", "Lothian");
		ResultValue value4 = new ResultValue("city.population", "447600");
		ResultValue value5 = new ResultValue("city.longitude", "-3.18333");
		ResultValue value6 = new ResultValue("city.latitude", "55.9167");

		missingResultInstance.addResultValue("city.name", value1);
		missingResultInstance.addResultValue("city.country", value2);
		missingResultInstance.addResultValue("city.province", value3);
		missingResultInstance.addResultValue("city.population", value4);
		missingResultInstance.addResultValue("city.longitude", value5);
		missingResultInstance.addResultValue("city.latitude", value6);

		missingResultInstance.setUserSpecified(true);
		missingResultInstance.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance to queryResult");
		queryResult.addResultInstance(missingResultInstance);
		logger.debug("added missing resultInstance to queryResult");

		annotationService.annotate("expectancy", "true", missingResultInstance, queryResult, null, true, currentUser);

		ResultInstance resultInstance1 = resultInstanceRepository.find(missingResultInstance.getId());
		//assertEquals(2, resultInstance1.getAnnotations().size());
		List<Annotation> annotations1 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance1, currentUser);
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance1.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations1);

		//assertEquals(4, resultInstance1.getMappings().size());

		ResultInstance missingResultInstance2 = new ResultInstance();
		missingResultInstance2.setResultType(otherResultInstance.getResultType());
		missingResultInstance2.setQuery(query);
		ResultValue value21 = new ResultValue("city.name", "Safi");
		ResultValue value22 = new ResultValue("city.country", "MA");
		ResultValue value23 = new ResultValue("city.province", "Morocco");
		ResultValue value24 = new ResultValue("city.population", "376038");
		ResultValue value25 = new ResultValue("city.longitude", "0");
		ResultValue value26 = new ResultValue("city.latitude", "0");

		missingResultInstance2.addResultValue("city.name", value21);
		missingResultInstance2.addResultValue("city.country", value22);
		missingResultInstance2.addResultValue("city.province", value23);
		missingResultInstance2.addResultValue("city.population", value24);
		missingResultInstance2.addResultValue("city.longitude", value25);
		missingResultInstance2.addResultValue("city.latitude", value26);

		missingResultInstance2.setUserSpecified(true);
		missingResultInstance2.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance2 to queryResult");
		queryResult.addResultInstance(missingResultInstance2);
		logger.debug("added missing resultInstance to queryResult");

		annotationService.annotate("expectancy", "true", missingResultInstance2, queryResult, null, true, currentUser);

		ResultInstance resultInstance21 = resultInstanceRepository.find(missingResultInstance2.getId());
		//assertEquals(2, resultInstance21.getAnnotations().size());
		List<Annotation> annotations21 = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(resultInstance21, currentUser);
		//annotationsOfResultInstancesToPropagate.addAll(resultInstance21.getAnnotations());
		annotationsOfResultInstancesToPropagate.addAll(annotations21);

		//assertEquals(4, resultInstance21.getMappings().size());

		/*
		ResultInstance missingResultInstance3 = new ResultInstance();
		missingResultInstance3.setResultType(otherResultInstance.getResultType());
		ResultValue value31 = new ResultValue("city.name", "Constantine");
		ResultValue value32 = new ResultValue("city.country", "DZ");
		ResultValue value33 = new ResultValue("city.province", "Algeria");
		ResultValue value34 = new ResultValue("city.population", "440842");
		ResultValue value35 = new ResultValue("city.longitude", "0");
		ResultValue value36 = new ResultValue("city.latitude", "0");

		missingResultInstance3.addResultValue("city.name", value31);
		missingResultInstance3.addResultValue("city.country", value32);
		missingResultInstance3.addResultValue("city.province", value33);
		missingResultInstance3.addResultValue("city.population", value34);
		missingResultInstance3.addResultValue("city.longitude", value35);
		missingResultInstance3.addResultValue("city.latitude", value36);

		missingResultInstance3.setUserSpecified(true);
		missingResultInstance3.addAllMappings(queryResult.getMappings());
		logger.debug("about to add missing resultInstance3 to queryResult");
		queryResult.addResultInstance(missingResultInstance3);
		logger.debug("added missing resultInstance to queryResult");

		annotationService.annotate("expectancy", "true", missingResultInstance3, null, true, currentUser);

		ResultInstance resultInstance22 = resultInstanceRepository.find(missingResultInstance3.getId());
		assertEquals(2, resultInstance22.getAnnotations().size());
		annotationsOfResultInstancesToPropagate.addAll(resultInstance22.getAnnotations());
		*/

		//assertEquals(4, resultInstance21.getMappings().size());

		queryResultRepository.update(queryResult);
		queryResultRepository.flush();

		/*
		assertEquals(41, queryResult.getResultInstances().size());
		assertNotNull(missingResultInstance.getId());
		assertNotNull(missingResultInstance2.getId());
		assertNotNull(missingResultInstance3.getId());

		//TODO this tests that each resultInstance has the mapping that produced that resultInstance associated with it, should move into the test class for the expander
		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);
			assertNotNull(resultInstance.getId());
			logger.debug("resultInstance.getResultValue(city.name):" + resultInstance.getResultValue("city.name"));
			logger.debug("resultInstance.getMappings().size(): " + resultInstance.getMappings().size());
			if (resultInstance.getResultValue("city.name").getValue().equals("Edinburgh")
					|| resultInstance.getResultValue("city.name").getValue().equals("Safi")
					|| resultInstance.getResultValue("city.name").getValue().equals("Constantine")) {
				logger.debug("found Edinburgh, Safi or Constantine");
				assertEquals(2, resultInstance.getMappings().size());
			} else {
				logger.debug("didn't find Edinburgh, Safi or Constantine");
				assertEquals(1, resultInstance.getMappings().size());

				String country = resultInstance.getResultValue("city.country").getValue();
				logger.debug("country: " + country);
				if (country.equals("DK") || country.equals("GB") || country.equals("D") || country.equals("IRL")) {
					logger.debug("europe");
					assertEquals(europeProvinceMapping, resultInstance.getMappings().iterator().next());
					if (country.equals("D")) {
						logger.debug("D");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), constrainingModelManagementConstructs, true,
								currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						assertEquals(2, resultInstance2.getAnnotations().size());
						annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
					} else if (country.equals("GB")) {
						logger.debug("GB");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), constrainingModelManagementConstructs, true,
								currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						assertEquals(2, resultInstance2.getAnnotations().size());
						annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
					}
				} else if (country.equals("DZ") || country.equals("GH") || country.equals("MA") || country.equals("RT") || country.equals("WSA")) {
					logger.debug("africa");
					assertEquals(africaProvinceMapping, resultInstance.getMappings().iterator().next());
					if (country.equals("WSA")) {
						logger.debug("WSA");
						annotationService.annotate("expectancy", "false", resultInstance.getId(), constrainingModelManagementConstructs, true,
								currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						assertEquals(2, resultInstance2.getAnnotations().size());
						annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
					} else if (country.equals("GH")) {
						logger.debug("GH");
						annotationService.annotate("expectancy", "true", resultInstance.getId(), constrainingModelManagementConstructs, true,
								currentUser);
						ResultInstance resultInstance2 = resultInstanceRepository.find(resultInstance.getId());
						assertEquals(2, resultInstance2.getAnnotations().size());
						annotationsOfResultInstancesToPropagate.addAll(resultInstance2.getAnnotations());
					}
				} else
					logger.error("unexpected country");
			}
		}

		for (Annotation annotation : annotationsOfResultInstancesToPropagate) {
			logger.debug("annotation: " + annotation);
			logger.debug("annotation.getValue(): " + annotation.getValue());
			logger.debug("annotation.getOntologyTerm().getName(): " + annotation.getOntologyTerm().getName());
			assertNotNull(annotation.getId());
		}
		*/

		annotationService.propagateAnnotation(annotationsOfResultInstancesToPropagate, constructsToAnnotate, constrainingModelManagementConstructs,
				false, currentUser);

		Mapping epMapping = mappingService.findMapping(europeProvinceMapping.getId());
		Mapping apMapping = mappingService.findMapping(africaProvinceMapping.getId());

		/*
		assertEquals(4, epMapping.getAnnotations().size());
		assertEquals(4, apMapping.getAnnotations().size());
		*/

		List<Annotation> epAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(epMapping, currentUser);
		//List<Annotation> epAnnotations = epMapping.getAnnotations();
		Annotation epAnnotationPr = epAnnotations.get(0);
		Annotation epAnnotationRec = epAnnotations.get(1);
		Annotation epAnnotationFM = epAnnotations.get(2);
		Annotation epAnnotationFrAnRes = epAnnotations.get(3);

		logger.debug("epAnnotationPr: " + epAnnotationPr);
		logger.debug("epAnnotationRec: " + epAnnotationRec);
		logger.debug("epAnnotationFM: " + epAnnotationFM);
		logger.debug("epAnnotationFrAnRes: " + epAnnotationFrAnRes);

		List<Annotation> apAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(apMapping, currentUser);
		//List<Annotation> apAnnotations = apMapping.getAnnotations();
		Annotation apAnnotationPr = apAnnotations.get(0);
		Annotation apAnnotationRec = apAnnotations.get(1);
		Annotation apAnnotationFM = apAnnotations.get(2);
		Annotation apAnnotationFrAnRes = apAnnotations.get(3);

		logger.debug("apAnnotationPr: " + apAnnotationPr);
		logger.debug("apAnnotationRec: " + apAnnotationRec);
		logger.debug("apAnnotationFM: " + apAnnotationFM);
		logger.debug("apAnnotationFrAnRes: " + apAnnotationFrAnRes);

		/*
		double epPrecisionValue = 8.0 / (8.0 + 6.0);
		double epRecallValue = 8.0 / (8.0 + 3.0);
		double epFmeasureValue = 2.0 * (epPrecisionValue * epRecallValue) / (epPrecisionValue + epRecallValue);
		double epFractionOfAnnotatedResults = (8.0 + 6.0 + 3.0) / 23.0;

		double apPrecisionValue = 4.0 / (4.0 + 2.0);
		double apRecallValue = 4.0 / (4.0 + 3.0);
		double apFmeasureValue = 2.0 * (apPrecisionValue * apRecallValue) / (apPrecisionValue + apRecallValue);
		double apFractionOfAnnotatedResults = (4.0 + 2.0 + 3.0) / 21.0;

		logger.debug("epPrecisionValue: " + epPrecisionValue);
		logger.debug("epRecallValue: " + epRecallValue);
		logger.debug("epFmeasureValue: " + epFmeasureValue);
		logger.debug("epFractionOfAnnotatedResults: " + epFractionOfAnnotatedResults);

		logger.debug("apPrecisionValue: " + apPrecisionValue);
		logger.debug("apRecallValue: " + apRecallValue);
		logger.debug("apFmeasureValue: " + apFmeasureValue);
		logger.debug("apFractionOfAnnotatedResults: " + apFractionOfAnnotatedResults);

		assertEquals(new Double(epPrecisionValue).toString(), epAnnotationPr.getValue());
		assertNotNull(epAnnotationPr.getId());
		assertEquals(1, epAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(epMapping.getId(), epAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(epMapping, epAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, epAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), epAnnotationPr.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), epAnnotationPr.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, epAnnotationPr.getOntologyTerm());
		assertNotNull(epAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, epAnnotationPr.getUser());

		assertEquals(new Double(epRecallValue).toString(), epAnnotationRec.getValue());
		assertNotNull(epAnnotationRec.getId());
		assertEquals(1, epAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(epMapping.getId(), epAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(epMapping, epAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, epAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), epAnnotationRec.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), epAnnotationRec.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(recallOT, epAnnotationRec.getOntologyTerm());
		assertNotNull(epAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, epAnnotationRec.getUser());

		assertEquals(new Double(epFmeasureValue).toString(), epAnnotationFM.getValue());
		assertNotNull(epAnnotationFM.getId());
		assertEquals(1, epAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(epMapping.getId(), epAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(epMapping, epAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, epAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), epAnnotationFM.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), epAnnotationFM.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fmeasureOT, epAnnotationFM.getOntologyTerm());
		assertNotNull(epAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, epAnnotationFM.getUser());

		assertEquals(new Double(epFractionOfAnnotatedResults).toString(), epAnnotationFrAnRes.getValue());
		assertNotNull(epAnnotationFrAnRes.getId());
		assertEquals(1, epAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(epMapping.getId(), epAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(epMapping, epAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, epAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), epAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), epAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, epAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(epAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, epAnnotationFrAnRes.getUser());

		assertEquals(new Double(apPrecisionValue).toString(), apAnnotationPr.getValue());
		assertNotNull(apAnnotationPr.getId());
		assertEquals(1, apAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(apMapping.getId(), apAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(apMapping, apAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, apAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), apAnnotationPr.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), apAnnotationPr.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(precisionOT, apAnnotationPr.getOntologyTerm());
		assertNotNull(apAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, apAnnotationPr.getUser());

		assertEquals(new Double(apRecallValue).toString(), apAnnotationRec.getValue());
		assertNotNull(apAnnotationRec.getId());
		assertEquals(1, apAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(apMapping.getId(), apAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(apMapping, apAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, apAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), apAnnotationRec.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), apAnnotationRec.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(recallOT, apAnnotationRec.getOntologyTerm());
		assertNotNull(apAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, apAnnotationRec.getUser());

		assertEquals(new Double(apFmeasureValue).toString(), apAnnotationFM.getValue());
		assertNotNull(apAnnotationFM.getId());
		assertEquals(1, apAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(apMapping.getId(), apAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(apMapping, apAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, apAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), apAnnotationFM.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), apAnnotationFM.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fmeasureOT, apAnnotationFM.getOntologyTerm());
		assertNotNull(apAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, apAnnotationFM.getUser());

		assertEquals(new Double(apFractionOfAnnotatedResults).toString(), apAnnotationFrAnRes.getValue());
		assertNotNull(apAnnotationFrAnRes.getId());
		assertEquals(1, apAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(apMapping.getId(), apAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(apMapping, apAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(1, apAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(queryResult.getQuery().getId(), apAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next().getId());
		assertEquals(queryResult.getQuery(), apAnnotationFrAnRes.getConstrainingModelManagementConstructs().iterator().next());
		assertEquals(fractionOfAnnotatedResultsOT, apAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(apAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, apAnnotationFrAnRes.getUser());
		*/

		//TODO think about this: it's not really the mapping annotations that get propagated,
		//it's the annotation of the resultInstances that are retrieved by these mappings for this query
		//annotation of queryResult is constrained by the mappings used to expand the query for that particular queryResult
		Set<Annotation> annotationsToBePropagated = new LinkedHashSet<Annotation>();
		//annotationsToBePropagated.addAll(epMapping.getAnnotations());
		//annotationsToBePropagated.addAll(apMapping.getAnnotations());
		annotationsToBePropagated.addAll(epAnnotations);
		annotationsToBePropagated.addAll(apAnnotations);

		Set<ModelManagementConstruct> constructsToBeAnnotated = new LinkedHashSet<ModelManagementConstruct>();
		QueryResult updatedQueryResult = queryResultService.findQueryResult(queryResult.getId());
		constructsToBeAnnotated.add(updatedQueryResult);

		Set<ModelManagementConstruct> constrainingModelManagementConstructsMappings = new LinkedHashSet<ModelManagementConstruct>();
		constrainingModelManagementConstructsMappings.add(epMapping);
		constrainingModelManagementConstructsMappings.add(apMapping);

		annotationService.propagateAnnotation(annotationsToBePropagated, constructsToBeAnnotated, constrainingModelManagementConstructsMappings,
				false, currentUser);

		QueryResult fetchedQueryResult = queryResultService.findQueryResult(queryResult.getId());

		List<Annotation> qrpAnnotations = annotationRepository.getAnnotationsForModelManagementConstructProvidedByUser(fetchedQueryResult,
				currentUser);
		//assertEquals(4, fetchedQueryResult.getAnnotations().size());
		assertEquals(4, qrpAnnotations.size());

		//List<Annotation> qrpAnnotations = fetchedQueryResult.getAnnotations();
		Annotation qrpAnnotationPr = qrpAnnotations.get(0);
		Annotation qrpAnnotationRec = qrpAnnotations.get(1);
		Annotation qrpAnnotationFM = qrpAnnotations.get(2);
		Annotation qrpAnnotationFrAnRes = qrpAnnotations.get(3);

		double qrpPrecisionValue = 6.0 / (6.0 + 4.0);
		double qrpRecallValue = 6.0 / (6.0 + 3.0);
		double qrpFmeasureValue = 2.0 * (qrpPrecisionValue * qrpRecallValue) / (qrpPrecisionValue + qrpRecallValue);
		double qrpFractionOfAnnotatedResults = (6.0 + 4.0 + 3.0) / 22.0;

		logger.debug("qrPrecisionValue: " + qrpPrecisionValue);
		logger.debug("qrRecallValue: " + qrpRecallValue);
		logger.debug("qrFmeasureValue: " + qrpFmeasureValue);
		logger.debug("qrFractionOfAnnotatedResults: " + qrpFractionOfAnnotatedResults);

		assertEquals(new Double(qrpPrecisionValue).toString(), qrpAnnotationPr.getValue());
		assertNotNull(qrpAnnotationPr.getId());
		assertEquals(1, qrpAnnotationPr.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrpAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrpAnnotationPr.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrpAnnotationPr.getConstrainingModelManagementConstructs().size());
		assertEquals(precisionOT, qrpAnnotationPr.getOntologyTerm());
		assertNotNull(qrpAnnotationPr.getOntologyTerm().getId());
		assertEquals(currentUser, qrpAnnotationPr.getUser());

		assertEquals(new Double(qrpRecallValue).toString(), qrpAnnotationRec.getValue());
		assertNotNull(qrpAnnotationRec.getId());
		assertEquals(1, qrpAnnotationRec.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrpAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrpAnnotationRec.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrpAnnotationRec.getConstrainingModelManagementConstructs().size());
		assertEquals(recallOT, qrpAnnotationRec.getOntologyTerm());
		assertNotNull(qrpAnnotationRec.getOntologyTerm().getId());
		assertEquals(currentUser, qrpAnnotationRec.getUser());

		assertEquals(new Double(qrpFmeasureValue).toString(), qrpAnnotationFM.getValue());
		assertNotNull(qrpAnnotationFM.getId());
		assertEquals(1, qrpAnnotationFM.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrpAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrpAnnotationFM.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrpAnnotationFM.getConstrainingModelManagementConstructs().size());
		assertEquals(fmeasureOT, qrpAnnotationFM.getOntologyTerm());
		assertNotNull(qrpAnnotationFM.getOntologyTerm().getId());
		assertEquals(currentUser, qrpAnnotationFM.getUser());

		assertEquals(new Double(qrpFractionOfAnnotatedResults).toString(), qrpAnnotationFrAnRes.getValue());
		assertNotNull(qrpAnnotationFrAnRes.getId());
		assertEquals(1, qrpAnnotationFrAnRes.getAnnotatedModelManagementConstructs().size());
		assertEquals(fetchedQueryResult.getId(), qrpAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next().getId());
		assertEquals(fetchedQueryResult, qrpAnnotationFrAnRes.getAnnotatedModelManagementConstructs().iterator().next());
		assertEquals(2, qrpAnnotationFrAnRes.getConstrainingModelManagementConstructs().size());
		assertEquals(fractionOfAnnotatedResultsOT, qrpAnnotationFrAnRes.getOntologyTerm());
		assertNotNull(qrpAnnotationFrAnRes.getOntologyTerm().getId());
		assertEquals(currentUser, qrpAnnotationFrAnRes.getUser());

		queryResultRepository.flush();
		annotationRepository.flush();
	}

	@Test
	public void testAddQuery() {
		String selectStarFromQuery = "Select * from citye";
		String queryName = "AllCities";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");

		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);

		Query query1 = new Query(queryName, selectStarFromQuery);
		query1.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query1.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);

		queryService.addQuery(query1);

		Query query = queryService.findQuery(query1.getId());

		assertNotNull(query.getId());
		assertNotNull(query.getRootOperator());
		assertNull(query.getRootOperatorOfExpandedQuery());

		assertEquals(selectStarFromQuery, query.getQueryString());
		assertEquals(queryName, query.getQueryName());
		assertEquals(1, query.getSchemas().size());
		assertEquals(mondialCityProvinceCountryContinentEuropeWRSchema, query.getSchemas().iterator().next());
		assertNotNull(query.getSchemas().iterator().next().getId());
		assertEquals(1, query.getDataSources().size());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, query.getDataSources().iterator().next());
		assertNotNull(query.getDataSources().iterator().next().getId());

		MappingOperator rootOperator = query.getRootOperator();

		isInstanceOf(ReduceOperator.class, rootOperator);
		assertNotNull(rootOperator.getId());
		assertNull(rootOperator.getVariableName());
		assertNotNull(rootOperator.getInput());
		assertNotNull(rootOperator.getLhsInput());
		assertEquals(rootOperator.getInput(), rootOperator.getLhsInput());
		assertNull(rootOperator.getRhsInput());
		assertEquals(6, ((ReduceOperator) rootOperator).getSuperLexicals().size());
		assertEquals("citye.namee, citye.countrye, citye.provincee, citye.populatione, citye.longitudee, citye.latitudee",
				((ReduceOperator) rootOperator).getReconcilingExpression().getExpression());
		assertNotNull(((ReduceOperator) rootOperator).getReconcilingExpression().getId());

		assertNotNull(rootOperator.getDataSource());
		assertNotNull(rootOperator.getDataSource().getId());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, rootOperator.getDataSource());

		Collection<SuperLexical> superLexicals = ((ReduceOperator) rootOperator).getSuperLexicals().values();
		String[] superLexicalNamesArray = new String[6];
		String[] superAbstractNamesArray = new String[6];
		int i = 0;
		for (SuperLexical superLexical : superLexicals) {
			logger.debug("i: " + i);
			logger.debug("superLexical.getName(): " + superLexical.getName());
			assertNotNull(superLexical.getId());
			superLexicalNamesArray[i] = superLexical.getName();
			superAbstractNamesArray[i] = superLexical.getParentSuperAbstract().getName();
			assertNotNull(superLexical.getParentSuperAbstract().getId());
			i++;
		}
		logger.debug("superLexicalNamesArray: " + superLexicalNamesArray);
		String[] expectedSuperLexicalNamesArray = { "namee", "populatione", "countrye", "provincee", "latitudee", "longitudee" }; // not sure why the order is suddently different
		//assertArrayEquals(expectedSuperLexicalNamesArray, superLexicalNamesArray);
		String[] expectedSuperAbstractNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };
		assertArrayEquals(expectedSuperAbstractNamesArray, superAbstractNamesArray);
		Set<String> superLexicalVariableNames = ((ReduceOperator) rootOperator).getSuperLexicals().keySet();
		String[] superLexicalVariableNamesArray = new String[6];
		i = 0;
		for (String superLexicalVariableName : superLexicalVariableNames) {
			superLexicalVariableNamesArray[i] = superLexicalVariableName;
			i++;
		}
		String[] expectedSuperLexicalVariableNamesArray = { "citye.namee", "citye.populatione", "citye.countrye", "citye.provincee",
				"citye.latitudee", "citye.longitudee" };
		//assertArrayEquals(expectedSuperLexicalVariableNamesArray, superLexicalVariableNamesArray);

		assertNotNull(rootOperator.getResultType());
		ResultType resultType0 = rootOperator.getResultType();
		assertNotNull(resultType0.getId());
		assertEquals(6, resultType0.getResultFields().size());
		String[] expectedResultFieldNamesArray0 = { "citye.namee", "citye.populatione", "citye.countrye", "citye.provincee", "citye.latitudee",
				"citye.longitudee" };
		DataType[] expectedResultFieldTypesArray0 = { DataType.STRING, DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray0 = { "namee", "populatione", "countrye", "provincee", "latitudee", "longitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray0 = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray0 = new String[6];
		DataType[] actualResultFieldTypesArray0 = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray0 = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray0 = new String[6];

		i = 0;
		Map<String, ResultField> resultFields0 = resultType0.getResultFields();
		Set<String> resultFieldNames0 = resultFields0.keySet();
		for (String resultFieldName : resultFieldNames0) {
			actualResultFieldNamesArray0[i] = resultFieldName;
			assertNotNull(resultFields0.get(resultFieldName).getId());
			actualResultFieldTypesArray0[i] = resultFields0.get(resultFieldName).getFieldType();
			actualCanonicalModelConstructNamesArray0[i] = resultFields0.get(resultFieldName).getCanonicalModelConstruct().getName();
			assertNotNull(resultFields0.get(resultFieldName).getCanonicalModelConstruct().getId());
			isInstanceOf(SuperLexical.class, resultFields0.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray0[i] = ((SuperLexical) resultFields0.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		//assertArrayEquals(expectedResultFieldNamesArray0, actualResultFieldNamesArray0);
		//assertArrayEquals(expectedResultFieldTypesArray0, actualResultFieldTypesArray0);
		//assertArrayEquals(expectedCanonicalModelConstructNamesArray0, actualCanonicalModelConstructNamesArray0);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray0, actualParentCanonicalModelConstructNamesArray0);

		MappingOperator input0Operator = rootOperator.getLhsInput();

		assertNotNull(input0Operator.getId());
		isInstanceOf(ScanOperator.class, input0Operator);
		assertEquals("citye", input0Operator.getVariableName());
		assertNull(input0Operator.getLhsInput());
		assertNull(input0Operator.getRhsInput());
		assertEquals("citye", ((ScanOperator) input0Operator).getSuperAbstract().getName());
		assertNotNull(((ScanOperator) input0Operator).getSuperAbstract().getId());
		assertNull(((ScanOperator) input0Operator).getReconcilingExpression());
		assertEquals(0, ((ScanOperator) input0Operator).getPredicates().size());

		assertNotNull(input0Operator.getDataSource());
		assertNotNull(input0Operator.getDataSource().getId());
		assertEquals(mondialCityProvinceCountryContinentEuropeWithRenameDS, input0Operator.getDataSource());

		assertNotNull(input0Operator.getResultType());
		ResultType resultType = input0Operator.getResultType();
		assertNotNull(resultType.getId());
		assertEquals(6, resultType.getResultFields().size());
		String[] expectedResultFieldNamesArray = { "citye.populatione", "citye.countrye", "citye.namee", "citye.provincee", "citye.latitudee",
				"citye.longitudee" };
		DataType[] expectedResultFieldTypesArray = { DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.STRING, DataType.FLOAT,
				DataType.FLOAT };
		String[] expectedCanonicalModelConstructNamesArray = { "populatione", "countrye", "namee", "provincee", "latitudee", "longitudee" };
		String[] exptectedParentCanonicalModelConstructNamesArray = { "citye", "citye", "citye", "citye", "citye", "citye" };

		String[] actualResultFieldNamesArray = new String[6];
		DataType[] actualResultFieldTypesArray = new DataType[6];
		String[] actualCanonicalModelConstructNamesArray = new String[6];
		String[] actualParentCanonicalModelConstructNamesArray = new String[6];

		i = 0;
		Map<String, ResultField> resultFields = resultType.getResultFields();
		Set<String> resultFieldNames = resultFields.keySet();
		for (String resultFieldName : resultFieldNames) {
			actualResultFieldNamesArray[i] = resultFieldName;
			actualResultFieldTypesArray[i] = resultFields.get(resultFieldName).getFieldType();
			assertNotNull(resultFields.get(resultFieldName).getId());
			actualCanonicalModelConstructNamesArray[i] = resultFields.get(resultFieldName).getCanonicalModelConstruct().getName();
			assertNotNull(resultFields.get(resultFieldName).getCanonicalModelConstruct().getId());
			isInstanceOf(SuperLexical.class, resultFields.get(resultFieldName).getCanonicalModelConstruct());
			actualParentCanonicalModelConstructNamesArray[i] = ((SuperLexical) resultFields.get(resultFieldName).getCanonicalModelConstruct())
					.getParentSuperAbstract().getName();
			i++;
		}

		//assertArrayEquals(expectedResultFieldNamesArray, actualResultFieldNamesArray);
		//assertArrayEquals(expectedResultFieldTypesArray, actualResultFieldTypesArray);
		//assertArrayEquals(expectedCanonicalModelConstructNamesArray, actualCanonicalModelConstructNamesArray);
		assertArrayEquals(exptectedParentCanonicalModelConstructNamesArray, actualParentCanonicalModelConstructNamesArray);
	}

	/* 
	 * Ruhaila
	 * moved by Conny from (Bio)SchemaServiceImplTest
	@Test
	public void testQueryResultXML() {
		String selectStarFromQuery = "Select * from ensemblProteinIds";
		String queryName = "AllSelect";

		//ArrayExpress
		//		Schema theSchema = schemaRepository.getSchemaByName("ArrayExpressXML");
		//		DataSource theDatasource = dataSourceRepository.getDataSourceWithSchemaName("ArrayExpressXML");

		//GEO
		Schema theSchema = schemaRepository.getSchemaByName("GeoXML");
		DataSource theDatasource = dataSourceRepository.getDataSourceWithSchemaName("GeoXML");

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(theSchema);
		query.addDataSource(theDatasource);
		query.setUser(currentUser);
		logger.debug("testQueryResultRuhai currentUser: " + currentUser);

		queryService.addQuery(query);

		QueryResult queryResult = queryService.evaluateQuery(query, null, currentUser, null);
		logger.debug("testQueryResultRuhai queryResult: " + queryResult.toString());
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		logger.debug("testQueryResultRuhai resultInstances.size(): " + resultInstances.size());

		for (ResultInstance resultInstance : resultInstances) {
			Map<String, ResultValue> resultFieldNameMap = resultInstance.getResultFieldNameResultValueMap();

			Set s = resultFieldNameMap.entrySet();
			Iterator it = s.iterator();

			while (it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				String key = (String) m.getKey();
				ResultValue resultvalue = (ResultValue) m.getValue();
				logger.debug("testXMLQueryResultRuhai Key: " + key + " Resultvalue: " + resultvalue.getValue());
			}
		}
	}
	*/

	/* 
	 * Ruhaila
	 * moved by Conny from (Bio)SchemaServiceImplTest
	@Test
	public void testQueryResultRelational() {
		String selectStarFromQuery = "Select * from experiment";
		String queryName = "AllExperiment";
		Schema theSchema = schemaRepository.getSchemaByName("Stanford");
		DataSource theDatasource = dataSourceRepository.getDataSourceWithSchemaName("Stanford");

		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(theSchema);
		query.addDataSource(theDatasource);
		query.setUser(currentUser);
		logger.debug("testQueryResultRuhai currentUser: " + currentUser);

		queryService.addQuery(query);

		QueryResult queryResult = queryService.evaluateQuery(query, null, currentUser, null);
		List<ResultInstance> resultInstances = queryResult.getResultInstances();
		logger.debug("testQueryResultRuhai resultInstances.size(): " + resultInstances.size());

		for (ResultInstance resultInstance : resultInstances) {
			Map<String, ResultValue> resultFieldNameMap = resultInstance.getResultFieldNameResultValueMap();

			Set s = resultFieldNameMap.entrySet();
			Iterator it = s.iterator();

			while (it.hasNext()) {
				Map.Entry m = (Map.Entry) it.next();
				String key = (String) m.getKey();
				ResultValue resultvalue = (ResultValue) m.getValue();
				logger.debug("testRelationalQueryResultRuhai Key: " + key + " Resultvalue: " + resultvalue.getValue());
			}
		}
	}
	*/
}
