package uk.ac.manchester.dstoolkit.repository.impl.hibernate.queryresults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.AbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators.EvaluateExternallyOperatorImpl;
import uk.ac.manchester.dstoolkit.service.query.QueryService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.QueryEvaluationEngineService;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;
import uk.ac.manchester.dstoolkit.service.query.queryoptimisier.LogicalQueryOptimiserService;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;
import uk.ac.manchester.dstoolkit.service.query.querytranslator.LocalQueryTranslator2SQLService;

public class HibernateResultInstanceRepositoryIntegrationTest extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(HibernateResultInstanceRepositoryIntegrationTest.class);

	@Autowired
	@Qualifier("queryEvaluationEngineService")
	private QueryEvaluationEngineService queryEvaluationEngine;

	@Autowired
	@Qualifier("localQueryTranslator2SQLService")
	private LocalQueryTranslator2SQLService localQueryTranslator2SQL;

	@Autowired
	@Qualifier("logicalQueryOptimiserService")
	private LogicalQueryOptimiserService logicalQueryOptimiser;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("queryResultService")
	private QueryResultService queryResultService;

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	@Test
	public void testGetResultInstanceForQueryWithSameResultValuesAsGivenResultInstance() {
		//TODO this fails at the moment
		String selectStarFromQuery = "Select * from CityE";
		String queryName = "queryName";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		Query query = new Query(queryName, selectStarFromQuery);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		queryService.addQuery(query);

		assertNotNull(query.getId());
		assertNotNull(query.getRootOperator());

		Long queryId = query.getId();

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;
		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		for (ResultInstance inst : resultInstances) {
			logger.debug("inst: " + inst);
		}

		QueryResult queryResult = new QueryResult(query);
		queryResult.setResultInstances(resultInstances);
		queryResultService.addQueryResult(queryResult);

		List<ResultInstance> persistentInstances = queryResult.getResultInstances();
		for (ResultInstance inst : persistentInstances) {
			logger.debug("persistentInst: " + inst);
		}

		Query query2 = queryService.findQuery(queryId);
		EvaluatorOperator rootOperator2 = logicalQueryOptimiser.optimise(query2, null);
		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) rootOperator2;
		String queryString2 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp2, null);
		rootOperator2.setQueryString(queryString2);

		List<ResultInstance> resultInstances2 = queryEvaluationEngine.evaluateQuery(rootOperator2);
		for (ResultInstance inst2 : resultInstances2) {
			logger.debug("inst2: " + inst2);
		}

		for (ResultInstance resultInstance2 : resultInstances2) {
			logger.debug("resultInstance2: " + resultInstance2);
			ResultInstance instance = resultInstanceRepository.getResultInstanceForQueryWithSameResultValuesAsGivenResultInstance(query2,
					resultInstance2, false);

			assertNotNull(instance);
			assertNotNull(instance.getId());

			assertEquals(6, instance.getResultFieldNameResultValueMap().size());
			assertEquals(resultInstance2.getResultValue("CityE.NameE").getValue(), instance.getResultValue("CityE.NameE").getValue());
			assertEquals(resultInstance2.getResultValue("CityE.CountryE").getValue(), instance.getResultValue("CityE.CountryE").getValue());
			assertEquals(resultInstance2.getResultValue("CityE.ProvinceE").getValue(), instance.getResultValue("CityE.ProvinceE").getValue());
			assertEquals(resultInstance2.getResultValue("CityE.PopulationE").getValue(), instance.getResultValue("CityE.PopulationE").getValue());
			assertEquals(resultInstance2.getResultValue("CityE.LongitudeE").getValue(), instance.getResultValue("CityE.LongitudeE").getValue());
			assertEquals(resultInstance2.getResultValue("CityE.LatitudeE").getValue(), instance.getResultValue("CityE.LatitudeE").getValue());

		}
	}

	@Test
	public void testGetResultInstanceForQueryWithSameResultValuesAsGivenResultInstanceNoSameResultInstancesAvailable() {
		String selectStarFromQuery1 = "Select * from CityE";
		String queryName1 = "queryName1";
		Schema mondialCityProvinceCountryContinentEuropeWRSchema = schemaRepository.getSchemaByName("MondialCityProvinceCountryContinentEuropeWR");
		DataSource mondialCityProvinceCountryContinentEuropeWithRenameDS = dataSourceRepository
				.getDataSourceWithSchemaName("MondialCityProvinceCountryContinentEuropeWR");
		logger.debug("mondialCityProvinceCountryContinentEuropeWithRenameDS: " + mondialCityProvinceCountryContinentEuropeWithRenameDS);
		Query query = new Query(queryName1, selectStarFromQuery1);
		query.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		queryService.addQuery(query);

		assertNotNull(query.getId());
		assertNotNull(query.getRootOperator());

		EvaluatorOperator rootOperator = logicalQueryOptimiser.optimise(query, null);
		EvaluateExternallyOperatorImpl evaluateExternallyOp = (EvaluateExternallyOperatorImpl) rootOperator;
		String queryString = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp, null);
		rootOperator.setQueryString(queryString);

		List<ResultInstance> resultInstances = queryEvaluationEngine.evaluateQuery(rootOperator);

		for (ResultInstance inst : resultInstances) {
			logger.debug("inst: " + inst);
		}

		QueryResult queryResult = new QueryResult(query);
		queryResult.setResultInstances(resultInstances);
		queryResultService.addQueryResult(queryResult);

		List<ResultInstance> persistentInstances = queryResult.getResultInstances();
		for (ResultInstance inst : persistentInstances) {
			logger.debug("persistentInst: " + inst);
		}

		String selectStarFromQuery2 = "Select * from CountryE";
		String queryName2 = "queryName2";

		Query query2 = new Query(queryName2, selectStarFromQuery2);
		query2.addSchema(mondialCityProvinceCountryContinentEuropeWRSchema);
		query2.addDataSource(mondialCityProvinceCountryContinentEuropeWithRenameDS);
		queryService.addQuery(query2);

		assertNotNull(query2.getId());
		assertNotNull(query2.getRootOperator());

		EvaluatorOperator rootOperator2 = logicalQueryOptimiser.optimise(query2, null);
		EvaluateExternallyOperatorImpl evaluateExternallyOp2 = (EvaluateExternallyOperatorImpl) rootOperator2;
		String queryString2 = localQueryTranslator2SQL.translate2SQL(evaluateExternallyOp2, null);
		rootOperator2.setQueryString(queryString2);

		List<ResultInstance> resultInstances2 = queryEvaluationEngine.evaluateQuery(rootOperator2);
		for (ResultInstance inst2 : resultInstances2) {
			logger.debug("inst2: " + inst2);
		}

		for (ResultInstance resultInstance2 : resultInstances2) {
			ResultInstance instance = resultInstanceRepository.getResultInstanceForQueryWithSameResultValuesAsGivenResultInstance(query2,
					resultInstance2, false);

			assertNull(instance);
		}
	}

}
