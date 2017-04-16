/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.mapping;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.MappingOperator;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.morphisms.mapping.MappingRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.ResultTypeRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.mapping.MappingService;

/**
 * @author chedeler
 *
 */
//@Transactional(readOnly = true)
@Service(value = "mappingService")
public class MappingServiceImpl extends GenericEntityServiceImpl<Mapping, Long> implements MappingService {

	@Autowired
	@Qualifier("mappingRepository")
	private MappingRepository mappingRepository;

	@Autowired
	@Qualifier("resultTypeRepository")
	private ResultTypeRepository resultTypeRepository;

	static Logger logger = Logger.getLogger(MappingServiceImpl.class);

	//TODO remove these two methods as AnnotationService can be used to do that now
	/*
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void annotateMappingsUsedToProduceQueryResult(QueryResult queryResult, User user) {
		logger.debug("in annotatedMappingsUsedToEvaluateQuery");
		logger.debug("queryResult: " + queryResult);
		Query query = queryResult.getQuery();
		double beta = 1;
		Set<Mapping> mappings = queryResult.getMappings();
		logger.debug("mappings: " + mappings);
		logger.debug("mappings.size():" + mappings.size());
		long numberOfKnownTpsForQuery = queryRepository.getNumberOfResultInstancesOfQueryWithGivenAnnotationForGivenOntologyTerm(query,
				"statisticalError", "tp");
		logger.debug("numberOfKnownTpsForQuery: " + numberOfKnownTpsForQuery);
		if (numberOfKnownTpsForQuery > 0) {
			for (Mapping mapping : mappings) {
				logger.debug("mapping: " + mapping);

				Set<Mapping> mappingSet = new LinkedHashSet<Mapping>();
				mappingSet.add(mapping);
				logger.debug("mappingSet: " + mappingSet);

				long numberOfResultsReturnedByMappingUsed = queryRepository.getNumberOfResultInstancesOfQueryRetrievedByGivenMappings(query,
						mappingSet);
				logger.debug("numberOfResultsReturnedByMappingUsed: " + numberOfResultsReturnedByMappingUsed);

				long numberOfTpsForQueryReturnedByMappingUsed = queryRepository
						.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query,
								"statisticalError", "tp", mappingSet);
				logger.debug("numberOfTpsForQueryReturnedByMappingUsed: " + numberOfTpsForQueryReturnedByMappingUsed);

				long numberOfFpsForQueryReturnedByMappingUsed = queryRepository
						.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query,
								"statisticalError", "fp", mappingSet);
				logger.debug("numberOfFpsForQueryReturnedByMappingUsed: " + numberOfFpsForQueryReturnedByMappingUsed);

				long numberOfFnsForQueryReturnedByMappingUsed = queryRepository
						.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query,
								"statisticalError", "fn", mappingSet);
				logger.debug("numberOfFnsForQueryReturnedByMappingUsed: " + numberOfFnsForQueryReturnedByMappingUsed);

				if ((numberOfTpsForQueryReturnedByMappingUsed != 0) || (numberOfFpsForQueryReturnedByMappingUsed != 0)) {

					double estimatedPrecision = (double) numberOfTpsForQueryReturnedByMappingUsed
							/ (double) (numberOfTpsForQueryReturnedByMappingUsed + numberOfFpsForQueryReturnedByMappingUsed);
					logger.debug("estimatedPrecision: " + estimatedPrecision);

					double estimatedRecall = (double) numberOfTpsForQueryReturnedByMappingUsed
							/ (double) (numberOfTpsForQueryReturnedByMappingUsed + numberOfFnsForQueryReturnedByMappingUsed);
					logger.debug("estimatedRecall: " + estimatedRecall);

					double estimatedFMeasure = ((1 + (beta * beta)) * estimatedPrecision * estimatedRecall)
							/ (((beta * beta) * estimatedPrecision) + estimatedRecall);
					if (((Object) estimatedFMeasure).toString().equals("NaN"))
						estimatedFMeasure = 0.0;
					logger.debug("estimatedFMeasure: " + estimatedFMeasure);

					double fractionOfAnnotatedResults = (numberOfTpsForQueryReturnedByMappingUsed + numberOfFpsForQueryReturnedByMappingUsed)
							/ (double) numberOfResultsReturnedByMappingUsed;
					logger.debug("fractionOfAnnotatedResults: " + fractionOfAnnotatedResults);

					this.addAnnotationToMapping(mapping, "precision", new Double(estimatedPrecision).toString(), user);
					this.addAnnotationToMapping(mapping, "recall", new Double(estimatedRecall).toString(), user);
					this.addAnnotationToMapping(mapping, "f-measure", new Double(estimatedFMeasure).toString(), user);
				}
			}
		}
	}
	*/

	/*
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void addAnnotationToMapping(Mapping mapping, String nameOfOntologyTerm, String annotationValue, User user) {
		logger.debug("in addAnnotationToMapping");
		logger.debug("mapping: " + mapping);
		logger.debug("nameOfOntologyTerm: " + nameOfOntologyTerm);
		logger.debug("annotationValue: " + annotationValue);
		OntologyTerm ontologyTerm = ontologyTermRepository.getOntologyTermWithName(nameOfOntologyTerm);
		logger.debug("ontologyTerm: " + ontologyTerm);
		List<Annotation> annotationsForMappingAndOntologyTermNameProvidedByUser = annotationRepository
				.getAnnotationsForModelManagementConstructAndOntologyTermNameProvidedByUser(mapping, nameOfOntologyTerm, user);
		if (annotationsForMappingAndOntologyTermNameProvidedByUser == null || annotationsForMappingAndOntologyTermNameProvidedByUser.isEmpty()) {
			logger.debug("didn't find annotation for mapping, ontologyTermName and provided by User");
			Annotation annotation = new Annotation(annotationValue, ontologyTerm);
			annotation.setUser(user);
			//annotation.addModelManagementConstruct(mapping);
			//logger.debug("mapping: " + mapping);
			//mapping = mappingRepository.fetchAnnotations(mapping);
			mapping.addAnnotation(annotation);
			annotationService.addAnnotation(annotation);
			//mappingRepository.update(mapping);
			ontologyTermRepository.update(ontologyTerm);
		} else {
			logger.debug("found annotation for mapping, ontologyTermName and provided by user");
			if (annotationsForMappingAndOntologyTermNameProvidedByUser.size() > 1)
				logger.error("found more than one annotation - TODO sort this out");
			else {
				Annotation annotation = annotationsForMappingAndOntologyTermNameProvidedByUser.iterator().next();
				logger.debug("annotationValue: " + annotation.getValue());
				annotation.setValue(annotationValue);
				annotationRepository.update(annotation);
			}
		}
	}
	*/

	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Transactional(readOnly = true)
	public Mapping fetchConstructs(Mapping mapping) {
		return mappingRepository.fetchConstructs(mapping);
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.mapping.MappingService#addMapping(uk.ac.manchester.dataspaces.domain.models.mapping.Mapping)
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addMapping(Mapping mapping) {
		if (mapping.getQuery1() != null) {
			MappingOperator query1RootOperator = mapping.getQuery1().getRootOperator();
			if (query1RootOperator != null)
				this.saveResultTypeForMappingOperator(query1RootOperator);
		}
		if (mapping.getQuery2() != null) {
			MappingOperator query2RootOperator = mapping.getQuery2().getRootOperator();
			if (query2RootOperator != null)
				this.saveResultTypeForMappingOperator(query2RootOperator);
		}
		//setConstructsForBothQueries(mapping);
		mappingRepository.save(mapping);
	}

	/*
	private void setConstructsForBothQueries(Mapping mapping) {
		MappingOperator rootOperatorQuery1 = mapping.getQuery1().getRootOperator();
		Set<ScanOperator> scanOperatorsQuery1 = this.getScanOperatorsOfMapping(rootOperatorQuery1);
		Set<CanonicalModelConstruct> query1Constructs = new LinkedHashSet<CanonicalModelConstruct>();
		for (ScanOperator scanOp : scanOperatorsQuery1) {
			CanonicalModelConstruct construct = scanOp.getSuperAbstract();
			query1Constructs.add(construct);
		}
		mapping.setQuery1Constructs(query1Constructs);

		MappingOperator rootOperatorQuery2 = mapping.getQuery2().getRootOperator();
		Set<ScanOperator> scanOperatorsQuery2 = this.getScanOperatorsOfMapping(rootOperatorQuery2);
		Set<CanonicalModelConstruct> query2Constructs = new LinkedHashSet<CanonicalModelConstruct>();
		for (ScanOperator scanOp : scanOperatorsQuery2) {
			CanonicalModelConstruct construct = scanOp.getSuperAbstract();
			query2Constructs.add(construct);
		}
		mapping.setQuery2Constructs(query2Constructs);
	}
	*/

	//same code as in queryServiceImpl
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveResultTypeForMappingOperator(MappingOperator mappingOperator) {
		if (mappingOperator.getResultType() != null){
			ResultType resultType = mappingOperator.getResultType();
			resultTypeRepository.save(resultType);
		}
		if (mappingOperator.getLhsInput() != null)
			saveResultTypeForMappingOperator(mappingOperator.getLhsInput());
		if (mappingOperator.getRhsInput() != null)
			saveResultTypeForMappingOperator(mappingOperator.getRhsInput());
	}

	//same code as in predefinedMappingsLoaderServiceImpl
	/*
	private Set<ScanOperator> getScanOperatorsOfMapping(MappingOperator mappingOperator) {
		//logger.debug("in getScanOperatorsOfMapping");
		Set<ScanOperator> scanOperators = new HashSet<ScanOperator>();
		//logger.debug("mappingOperator: " + mappingOperator);
		mappingOperator.addMappingUsedForExpansion(mappingOperator.getMapping());
		if (mappingOperator instanceof JoinOperator || mappingOperator instanceof SetOperator) {
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getLhsInput()));
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getRhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ReduceOperator) {
			scanOperators.addAll(getScanOperatorsOfMapping(mappingOperator.getLhsInput()));
			return scanOperators;
		} else if (mappingOperator instanceof ScanOperator) {
			ScanOperator scanOperator = (ScanOperator) mappingOperator;
			scanOperators.add(scanOperator);
			return scanOperators;
		} else
			logger.error("unexpected operator");
		return scanOperators;
	}
	*/

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.mapping.MappingService#deleteMapping(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteMapping(Long mappingId) {
		//TODO
		mappingRepository.delete(mappingRepository.find(mappingId));
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.mapping.MappingService#findMapping(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public Mapping findMapping(Long mappingId) {
		return mappingRepository.find(mappingId);
	}

	/**
	 * @param mappingRepository the mappingRepository to set
	 */
	public void setMappingRepository(MappingRepository mappingRepository) {
		this.mappingRepository = mappingRepository;
	}

	/**
	 * @return the mappingRepository
	 */
	public MappingRepository getMappingRepository() {
		return mappingRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<Mapping, Long> getRepository() {
		return mappingRepository;
	}

}
