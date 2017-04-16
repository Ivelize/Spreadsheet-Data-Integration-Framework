/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.query.queryresults;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultValue;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultInstanceRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.query.queryresults.QueryResultService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "queryResultService")
public class QueryResultServiceImpl extends GenericEntityServiceImpl<QueryResult, Long> implements QueryResultService {

	static Logger logger = Logger.getLogger(QueryResultServiceImpl.class);

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	//TODO possibly change the following two to services and add corresponding methods to services
	//@Autowired
	//@Qualifier("queryRepository")
	//private QueryRepository queryRepository;

	//@Autowired
	//@Qualifier("queryService")
	//private QueryService queryService;

	//@Autowired
	//@Qualifier("resultTypeRepository")
	//private ResultTypeRepository resultTypeRepository;

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("resultInstanceRepository")
	private ResultInstanceRepository resultInstanceRepository;

	//@Autowired
	//@Qualifier("ontologyTermRepository")
	//private OntologyTermRepository ontologyTermRepository;

	//@Autowired
	//@Qualifier("annotationRepository")
	//private AnnotationRepository annotationRepository;

	//@Autowired
	//@Qualifier("annotationService")
	//private AnnotationService annotationService;

	//TODO add method for adding FN
	//TODO remove this here, annotate in AnnotationServiceImpl should be used for this	
	/*
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addExpectancyAnnotationToQueryResultInstance(Long queryResultInstanceId, Boolean expected, Boolean notExpected, User user) {

		//TODO think about whether to call annotateMappings from here or put it in the homeController 

		logger.debug("in addAnnotationToQueryResult");
		ResultInstance resultInstance = resultInstanceRepository.find(queryResultInstanceId);
		logger.debug("resultInstance: " + resultInstance);
		OntologyTerm expectancyOT = ontologyTermRepository.getOntologyTermWithName("expectancy");
		logger.debug("expectancyOT: " + expectancyOT);
		OntologyTerm statisticalErrorOT = ontologyTermRepository.getOntologyTermWithName("statisticalError");
		logger.debug("statisticalErrorOT: " + statisticalErrorOT);
		String statisticalError;
		String value;
		if (expected) {
			value = "true";
			statisticalError = "tp";
		} else {
			value = "false";
			statisticalError = "fp";
		}

		List<Annotation> expectancyAnnotationsForResultInstanceProvidedByUser = annotationRepository
				.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(resultInstance, expectancyOT.getName(), user);
		if (expectancyAnnotationsForResultInstanceProvidedByUser == null || expectancyAnnotationsForResultInstanceProvidedByUser.isEmpty()) {
			logger.debug("didn't find expectancy for resultInstance provided by User");
			Annotation annotation = new Annotation(value, expectancyOT);
			annotation.setUser(user);
			annotation.addAnnotatedModelManagementConstruct(resultInstance);
			resultInstance.addAnnotation(annotation);
			annotationService.addAnnotation(annotation);
			resultInstanceRepository.update(resultInstance);
			ontologyTermRepository.update(expectancyOT);
		} else {
			logger.debug("found expectancy annotation for resultInstance provided by user");
			if (expectancyAnnotationsForResultInstanceProvidedByUser.size() > 2)
				logger.error("found more than two annotations - TODO sort this out, as should only be either yes or no");
			else {
				for (Annotation annotation : expectancyAnnotationsForResultInstanceProvidedByUser) {
					logger.debug("annotationValue: " + annotation.getValue());
					if (annotation.getValue().equals(value)) {
						annotation.updateTimestamp();
						annotationRepository.update(annotation);
					}
				}
			}
		}

		List<Annotation> statisticalErrorAnnotationsForResultInstanceProvidedByUser = annotationRepository
				.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(resultInstance, statisticalErrorOT.getName(), user);
		if (statisticalErrorAnnotationsForResultInstanceProvidedByUser == null
				|| statisticalErrorAnnotationsForResultInstanceProvidedByUser.isEmpty()) {
			logger.debug("didn't find statisticalError for resultInstance provided by User");
			Annotation statErrorAnnot = new Annotation(statisticalError, statisticalErrorOT);
			statErrorAnnot.setUser(user);
			statErrorAnnot.addAnnotatedModelManagementConstruct(resultInstance);
			resultInstance.addAnnotation(statErrorAnnot);
			annotationService.addAnnotation(statErrorAnnot);
			resultInstanceRepository.update(resultInstance);
			ontologyTermRepository.update(expectancyOT);
		} else {
			logger.debug("found expectancy annotation for resultInstance provided by user");
			if (statisticalErrorAnnotationsForResultInstanceProvidedByUser.size() > 2)
				logger.error("found more than two annotations - TODO sort this out, as should only be either yes or no");
			else {
				for (Annotation annotation : statisticalErrorAnnotationsForResultInstanceProvidedByUser) {
					logger.debug("annotationValue: " + annotation.getValue());
					if (annotation.getValue().equals(value)) {
						annotation.updateTimestamp();
						annotationRepository.update(annotation);
					}
				}
			}
		}
	}
	*/

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.QueryResultService#addQueryResult(uk.ac.manchester.dataspaces.domain.models.query.queryresults.QueryResult)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	public void addQueryResult(QueryResult queryResult) {
		logger.debug("in addQueryResult");

		Query query = queryResult.getQuery();

		if (query.getId() == null)
			//queryService.addQuery(query);
			logger.error("query doesn't have id - TODO sort this");
		//else
		//	queryRepository.merge(query);

		List<ResultInstance> resultInstances = queryResult.getResultInstances();

		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);

			ResultInstance instance = resultInstanceRepository.getResultInstanceForQueryWithSameResultValuesAsGivenResultInstance(query,
					resultInstance, false);
			if (instance != null) {
				logger.debug("found instance with same values (and same resultType) as resultInstance");
				Set<Mapping> mappings = resultInstance.getMappings();
				instance.addAllMappings(mappings);
				//TODO think about this
				logger.debug("added mappings from resultInstance to instance");

				int index = resultInstances.indexOf(resultInstance);
				//resultInstances.set(index, instance);
				queryResult.setResultInstanceAtIndex(index, instance);
				logger.debug("replaced new resultInstance with instance in queryResult");
			}
		}

		ResultType resultType = queryResult.getResultType();
		//resultTypeRepository.update(resultType);
		//logger.debug("updated resultType: " + resultType);
		queryResultRepository.save(queryResult);
		logger.debug("saved queryResult: " + queryResult);

		//List<ResultInstance> resultInstances = queryResult.getResultInstances();
		for (ResultInstance resultInstance : resultInstances) {
			logger.debug("resultInstance: " + resultInstance);
			//ResultType resultType = resultInstance.getResultType();
			//resultTypeRepository.update(resultType);

			if (resultInstance.getId() == null) {
				for (String resultFieldName : resultInstance.getResultFieldNameResultValueMap().keySet()) {
					ResultValue resultValue = resultInstance.getResultValue(resultFieldName);
					if (resultValue != null && resultValue.getValue() != null && resultValue.getValue().contains("\u0000")) {
						logger.info("found \u0000, which can't be handled by postgres - this is a hack - removing \u0000");
						String[] valueWithoutOffendingCharacter = resultValue.getValue().split("\u0000");
						StringBuilder newValue = new StringBuilder();
						for (String value : valueWithoutOffendingCharacter)
							newValue.append(value);
						resultValue.setValue(newValue.toString());
					}
				}
				resultInstanceRepository.save(resultInstance);
				logger.debug("saved resultInstance");
			} else {
				resultInstanceRepository.update(resultInstance);
				logger.debug("updated resultInstance");
			}
			//resultInstanceRepository.flush();

			/*
			Set<Mapping> mappings = resultInstance.getMappings();
			for (Mapping mapping : mappings) {
				logger.debug("mapping: " + mapping);
				mappingRepository.update(mapping);
				logger.debug("updated mapping");
			}
			*/

		}
		logger.debug("getting mappings of queryResult");
		//assume the queryResult has all the mappings of all the instances anyway - TODO check this
		Set<Mapping> mappings = queryResult.getMappings();
		logger.debug("mappings: " + mappings);
		for (Mapping mapping : mappings) {
			logger.debug("mapping: " + mapping);
			//mappingRepository.merge(mapping);
			//logger.debug("updated mapping");
		}
		//queryRepository.update(queryResult.getQuery());
		//logger.debug("updated query of queryResult");
		//if (queryResult.getResultType().getId() != null)
		//	resultTypeRepository.update(queryResult.getResultType());
		//queryResultRepository.update(queryResult);
		queryResultRepository.update(queryResult);
		logger.debug("updated queryResult");
		//queryResultRepository.flush();
		logger.debug("leaving addQueryResult");
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.QueryResultService#deleteQueryResult(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteQueryResult(Long queryResultId) {
		// TODO 
		queryResultRepository.delete(queryResultRepository.find(queryResultId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.query.queryresults.QueryResultService#findQueryResult(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public QueryResult findQueryResult(Long queryResultId) {
		return queryResultRepository.find(queryResultId);
	}

	/**
	 * @param queryResultRepository the queryResultRepository to set
	 */
	public void setQueryResultRepository(QueryResultRepository queryResultRepository) {
		this.queryResultRepository = queryResultRepository;
	}

	/**
	 * @return the queryResultRepository
	 */
	public QueryResultRepository getQueryResultRepository() {
		return queryResultRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<QueryResult, Long> getRepository() {
		return queryResultRepository;
	}

}
