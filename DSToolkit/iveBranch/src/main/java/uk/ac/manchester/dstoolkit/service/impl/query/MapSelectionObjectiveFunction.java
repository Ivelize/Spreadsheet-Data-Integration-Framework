package uk.ac.manchester.dstoolkit.service.impl.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.coinor.opents.Move;
import org.coinor.opents.ObjectiveFunction;
import org.coinor.opents.Solution;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.service.annotation.AnnotationService;

//@Transactional(readOnly = true)
//@Scope("prototype")
//@Service
public class MapSelectionObjectiveFunction implements ObjectiveFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5633538503592993176L;

	private static Logger logger = Logger.getLogger(MapSelectionObjectiveFunction.class);

	//@Autowired
	//@Qualifier("annotationService")
	private AnnotationService annotationService;

	//@Autowired
	//@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	//@Autowired
	//@Qualifier("mappingOperatorRepository")
	//private MappingOperatorRepository mappingOperatorRepository;

	private Query query;
	private List<Mapping> candidateMappings;
	private List<Mapping> subsetCandidateMappings;
	private ControlParameterType thresholdType;
	private double thresholdValue;

	public MapSelectionObjectiveFunction(Query query, List<Mapping> candidateMappings, ControlParameterType thresholdType, double thresholdValue) {
		logger.debug("in MapSelectionObjectiveFunction");
		logger.debug("candidateMappings: " + candidateMappings);
		logger.debug("query: " + query);
		logger.debug("thresholdType: " + thresholdType);
		logger.debug("thresholdValue: " + thresholdValue);
		this.candidateMappings = candidateMappings;
		this.query = query;
		this.thresholdType = thresholdType;
		this.thresholdValue = thresholdValue;
	}

	public double[] evaluate(Solution solution, Move move) {
		logger.debug("in evaluate");
		logger.debug("solution: " + solution);
		logger.debug("move: " + move);

		// Prior objective value
		logger.debug("solution.getObjectiveValue: " + solution.getObjectiveValue());
		if (solution.getObjectiveValue() != null) {
			double dist = solution.getObjectiveValue()[0];
			logger.debug("prior objective value - dist: " + dist);
		}

		if (move != null) {
			logger.debug("move != null, call operateOn");
			move.operateOn(solution);
		}

		boolean[] selectedMappings = ((MapSelectionSolution) solution).getSelectedMappings();
		logger.debug("selectedMappings: " + selectedMappings);
		//int len = selectedMappings.length;
		//int scale = 1;

		logger.debug("solution: ");
		for (int i = 0; i < selectedMappings.length; i++)
			logger.debug(" " + i + ": " + selectedMappings[i]);

		subsetCandidateMappings = new ArrayList<Mapping>();

		for (int i = 0; i < selectedMappings.length; i++)
			if (selectedMappings[i])
				subsetCandidateMappings.add(candidateMappings.get(i));

		logger.debug("Selected mappings in the currect solution: ");
		for (int i = 0; i < subsetCandidateMappings.size(); i++)
			logger.debug(" " + subsetCandidateMappings.get(i) + "  ");

		Map<String, Double> precisionRecallFMeasure = calculatePrecisionRecallAndFMeasureOfQueryForSelectedMappings(query, subsetCandidateMappings);

		logger.debug("precisionRecallFMeasure: " + precisionRecallFMeasure);

		if (precisionRecallFMeasure != null && precisionRecallFMeasure.size() > 0) {

			double precision = precisionRecallFMeasure.get("precision").doubleValue();
			logger.debug("precision: " + precision);
			double recall = precisionRecallFMeasure.get("recall").doubleValue();
			logger.debug("recall: " + recall);
			double K;
			double fitness = 0.0;

			logger.debug("thresholdValue: " + thresholdValue);
			if (thresholdType.equals(ControlParameterType.PRECISION_THRESHOLD)) {
				logger.debug("thresholdType is precision");

				K = (1 + Math.exp(5)) / Math.exp(5);
				logger.debug("K: " + K);
				fitness = recall * K * ((1 / (1 + Math.exp((-10 * precision / thresholdValue) + 5))) - (1 / (1 + Math.exp(5))));
				logger.debug("fitness: " + fitness);

			} else if (thresholdType.equals(ControlParameterType.RECALL_THRESHOLD)) {
				logger.debug("thresholdType is recall");
				K = (1 + Math.exp(5)) / Math.exp(5);
				logger.debug("K: " + K);
				fitness = precision * K * ((1 / (1 + Math.exp((-10 * recall / thresholdValue) + 5))) - (1 / (1 + Math.exp(5))));
				logger.debug("fitness: " + fitness);
			} else
				logger.error("unexpected thresholdType: " + thresholdType);

			if (move != null) {
				logger.debug("move != null, call undoOperation");
				((MapSelectionSwapMove) move).undoOperation(solution);
			}
			if (fitness == Double.NaN) {
				logger.debug("fitness == NaN - set it to 0.0");
				fitness = 0.0d;
				logger.debug("fitness: " + fitness);
			}
			logger.debug("before returning fitness: " + fitness);
			return new double[] { fitness };
		}

		if (move != null) {
			logger.debug("move != null, call undoOperation");
			((MapSelectionSwapMove) move).undoOperation(solution);
		}

		logger.debug("before returning 0.0 as fitness");
		return new double[] { 0.0d };

	}

	//partly same code as in AnnotationServiceImpl (propagateAnnotationsFromMappingsToQueryResult(Set<Annotation> annotations, QueryResult queryResult,
	//Set<ModelManagementConstruct> constrainingModelManagementConstructs, boolean duplicateAnnotationAllowed), here without making annotation persistent
	private Map<String, Double> calculatePrecisionRecallAndFMeasureOfQueryForSelectedMappings(Query query, List<Mapping> selectedMappings) {
		logger.debug("in calculatePrecisionRecallAndFMeasureOfQueryForSelectedMappings");
		logger.debug("query: " + query);
		logger.debug("query.getRootOperator(): " + query.getRootOperator());
		logger.debug("selectedMappings.size(): " + selectedMappings.size());

		Set<Mapping> mappingSet = new LinkedHashSet<Mapping>();
		mappingSet.addAll(selectedMappings);
		logger.debug("mappingSet.size(): " + mappingSet.size());

		double beta = 1;

		//logger.debug("mappingOperatorRepository: " + mappingOperatorRepository);
		if (query.isJustScanQuery()) {
			logger.debug("query is just scan");

			long numberOfResultsReturnedByMappingsUsed = queryRepository.getNumberOfResultInstancesOfQueryRetrievedByGivenMappings(query, mappingSet);
			logger.debug("numberOfResultsReturnedByMappingsUsed: " + numberOfResultsReturnedByMappingsUsed);

			long numberOfTpsForQueryReturnedByMappingsUsed = queryRepository
					.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query, "statisticalError",
							"tp", mappingSet);
			logger.debug("numberOfTpsForQueryReturnedByMappingsUsed: " + numberOfTpsForQueryReturnedByMappingsUsed);

			long numberOfFpsForQueryReturnedByMappingsUsed = queryRepository
					.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query, "statisticalError",
							"fp", mappingSet);
			logger.debug("numberOfFpsForQueryReturnedByMappingsUsed: " + numberOfFpsForQueryReturnedByMappingsUsed);

			long numberOfFnsForQueryReturnedByMappingsUsed = queryRepository
					.getNumberOfResultInstancesOfQueryWithGivenAnnotationValueForGivenOntologyTermRetrievedByGivenMappings(query, "statisticalError",
							"fn", mappingSet);
			logger.debug("numberOfFnsForQueryReturnedByMappingsUsed: " + numberOfFpsForQueryReturnedByMappingsUsed);

			Map<String, Double> precisionRecallFMeasure = annotationService.calculatePrecisionRecallAndFMeasure(
					numberOfTpsForQueryReturnedByMappingsUsed, numberOfFpsForQueryReturnedByMappingsUsed, numberOfFnsForQueryReturnedByMappingsUsed,
					beta);
			return precisionRecallFMeasure;
		} else {
			logger.error("query isn't just scan - TODO add propagation");
			//TODO add propagation
			return null;
		}

	}

	/**
	 * @return the candidateMappings
	 */
	public List<Mapping> getCandidateMappings() {
		return candidateMappings;
	}

	/**
	 * @param candidateMappings the candidateMappings to set
	 */
	public void setCandidateMappings(List<Mapping> candidateMappings) {
		this.candidateMappings = candidateMappings;
	}

	/**
	 * @return the query
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	/**
	 * @return the subsetCandidateMappings
	 */
	public List<Mapping> getSubsetCandidateMappings() {
		return subsetCandidateMappings;
	}

	/**
	 * @param subsetCandidateMappings the subsetCandidateMappings to set
	 */
	public void setSubsetCandidateMappings(List<Mapping> subsetCandidateMappings) {
		this.subsetCandidateMappings = subsetCandidateMappings;
	}

	/**
	 * @return the thresholdType
	 */
	public ControlParameterType getThresholdType() {
		return thresholdType;
	}

	/**
	 * @param thresholdType the thresholdType to set
	 */
	public void setThresholdType(ControlParameterType thresholdType) {
		this.thresholdType = thresholdType;
	}

	/**
	 * @return the thresholdValue
	 */
	public double getThresholdValue() {
		return thresholdValue;
	}

	/**
	 * @param thresholdValue the thresholdValue to set
	 */
	public void setThresholdValue(double thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	/**
	 * @return the annotationService
	 */
	public AnnotationService getAnnotationService() {
		return annotationService;
	}

	/**
	 * @param annotationService the annotationService to set
	 */
	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	/**
	 * @return the queryRepository
	 */
	public QueryRepository getQueryRepository() {
		return queryRepository;
	}

	/**
	 * @param queryRepository the queryRepository to set
	 */
	public void setQueryRepository(QueryRepository queryRepository) {
		this.queryRepository = queryRepository;
	}

}
