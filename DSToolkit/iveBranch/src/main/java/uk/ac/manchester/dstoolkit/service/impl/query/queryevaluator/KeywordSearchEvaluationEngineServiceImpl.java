package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperAbstractRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperLexicalRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.RelationalDataTranslatorServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.KeywordSearchEvaluationEngineService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add RDF in evaluateKeywordQuery()
 */


@Transactional(readOnly = true)
@Service(value = "keywordSearchEvaluationEngineService")
public class KeywordSearchEvaluationEngineServiceImpl implements KeywordSearchEvaluationEngineService {

	private static Logger logger = Logger.getLogger(QueryEvaluationEngineServiceImpl.class);

	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("superAbstractRepository")
	private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("superLexicalRepository")
	private SuperLexicalRepository superLexicalRepository;

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.query.queryevaluator.KeywordSearchEvaluationEngineService#evaluateKeywordQuery(java.lang.String)
	 */
	public List<ResultInstance> evaluateKeywordQuery(String keyword) {
		logger.debug("in evaluateKeywordQuery");
		logger.debug("keyword: " + keyword);

		List<ResultInstance> allResultInstances = new ArrayList<ResultInstance>();

		long initialStartTime = System.currentTimeMillis();

		Set<String> dataSourceNames = externalDataSourcePoolUtilService.getAllDataSourceNames();
		for (String dataSourceName : dataSourceNames) {
			logger.debug("dataSourceName: " + dataSourceName);

			DataSource dataSource = dataSourceRepository.getDataSourceWithSchemaName(dataSourceName);
			Schema schema = dataSource.getSchema();
			ModelType modelType = schema.getModelType();
			//ComboPooledDataSource externalDataSource = externalDataSourcePoolUtilService.getExternalDataSource(dataSourceName);

			if (modelType.equals(ModelType.RELATIONAL)) {
				logger.debug("modelType is RELATIONAL");
				logger.debug("externalDataSourcePoolUtilService: " + externalDataSourcePoolUtilService);
				ComboPooledDataSource externalDataSource = externalDataSourcePoolUtilService.getExternalRelationalDataSource(dataSourceName);
				logger.debug("externalDataSource: " + externalDataSource);
				JdbcTemplate jdbcTemplate = new JdbcTemplate(externalDataSource);
				logger.debug("jdbcTemplate: " + jdbcTemplate);

				List<SuperAbstract> superAbstracts = superAbstractRepository.getAllSuperAbstractsInSchemaWithName(dataSourceName);
				for (SuperAbstract superAbstract : superAbstracts) {
					logger.debug("superAbstract: " + superAbstract);

					if (superAbstract.getCardinality() > 0) {

						List<SuperLexical> superLexicals = superLexicalRepository
								.getAllSuperLexicalsOfSuperAbstractOrderedById(superAbstract.getId());

						ResultType resultType = new ResultType();
						for (SuperLexical superLexical : superLexicals) {
							ResultField resultField = new ResultField(superLexical.getName(), superLexical.getDataType());
							resultField.setCanonicalModelConstruct(superLexical);
							resultType.addResultField(superLexical.getName(), resultField);
							logger.debug("added resultField to resultType, resultField: " + resultField);
						}

						StringBuilder queryStringSB = new StringBuilder();
						queryStringSB.append("SELECT * FROM ");
						queryStringSB.append(superAbstract.getName());
						queryStringSB.append(" WHERE ");

						int i = 0;
						for (SuperLexical superLexical : superLexicals) {
							logger.debug("superLexical: " + superLexical);
							if (i > 0)
								queryStringSB.append(" OR ");
							queryStringSB.append(superLexical.getName());
							queryStringSB.append(" = ");
							queryStringSB.append("'");
							queryStringSB.append(keyword);
							queryStringSB.append("'");
							i++;
						}

						String queryString = queryStringSB.toString();

						logger.debug("queryString: " + queryString);

						long startTime = System.currentTimeMillis();
						SqlRowSet rowSet = jdbcTemplate.queryForRowSet(queryString);
						logger.debug("rowSet: " + rowSet);
						RelationalDataTranslatorServiceImpl dataTranslator = new RelationalDataTranslatorServiceImpl();
						List<ResultInstance> resultInstances = dataTranslator.translateResultSetIntoListOfResultInstances(rowSet, resultType);
						long endTime = System.currentTimeMillis();
						long elapsedTime = endTime - startTime;
						logger.debug("Total time for delivery " + "(including conversion to resultInstances) " + elapsedTime + " ms.");

						logger.debug("resultInstances: " + resultInstances);
						logger.debug("resultInstances.size: " + resultInstances.size());

						if (resultInstances != null && !resultInstances.isEmpty())
							allResultInstances.addAll(resultInstances);

					}
				}

				long finalEndTime = System.currentTimeMillis();
				long overallElapsedTime = finalEndTime - initialStartTime;
				logger.debug("Overall time for delivery " + "(including conversion to resultInstances) " + overallElapsedTime + " ms.");

				logger.debug("allResultInstances: " + allResultInstances);
				logger.debug("allResultInstances.size: " + allResultInstances.size());
			} else if (modelType.equals(ModelType.XSD)) {
				logger.debug("modelType is XSD");
			} else if (modelType.equals(ModelType.RDF)) {
				logger.debug("modelType is RDF");
			}
		}
		return allResultInstances;
	}
}
