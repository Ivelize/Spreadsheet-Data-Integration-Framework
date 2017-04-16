/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.query.Query;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.repository.query.QueryRepository;
import uk.ac.manchester.dstoolkit.repository.query.queryresults.QueryResultRepository;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.InstancesPerTwoResultFieldsMatchDurationDetails;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.InstancesPerTwoSuperAbstractsMatchDurationDetails;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.MatchingProfilerServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.NGramMatchDurationDetails;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.InstanceBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingService;
import uk.ac.manchester.dstoolkit.service.query.QueryService;

/**
 * @author chedeler
 * 
 */
@Scope("prototype")
@Service
@Configurable(autowire = Autowire.BY_NAME)
public class InstanceBasedMatcherServiceImpl extends MatcherServiceImpl implements InstanceBasedMatcherService {

	// TODO test this properly

	private static Logger logger = Logger.getLogger(InstanceBasedMatcherServiceImpl.class);

	@Autowired
	private MatchingProducerService matchingProducerService;

	@Autowired
	@Qualifier("matchingProfilerServiceImpl")
	private MatchingProfilerServiceImpl matchingProfilerServiceImpl;

	@Autowired
	@Qualifier("queryService")
	private QueryService queryService;

	@Autowired
	@Qualifier("queryRepository")
	private QueryRepository queryRepository;

	@Autowired
	@Qualifier("queryResultRepository")
	private QueryResultRepository queryResultRepository;

	@Autowired
	@Qualifier("matchingService")
	private MatchingService matchingService;

	public InstanceBasedMatcherServiceImpl() {
		logger.debug("in InstanceBasedMatcherServiceImpl");
		this.setName("instanceMatcher");
		this.setMatcherType(MatcherType.INSTANCES);
	}

	// TODO if construct1 and construct2 in single match aren't superAbstract it doesn't make sense to run it ... decide what to do ... commented out for now

	// TODO go through all constructs to find superAbstracts, run query to get instances for each superAbstract
	// TODO decide whether to use queryEvaluationEngine to avoid persistence or use QueryServiceImpl to have persistence ...
	// TODO using QueryServiceImpl at the moment to avoid having to retrieve instances again as only getting subset of instances ...
	// TODO could use this for incremental matching ... re-matching by retrieving instances previously retrieved ... but could be out of date so may not be ideal ...

	// TODO think about bloomfilter

	public InstanceBasedMatcherServiceImpl(MatcherType matcherType) {
		super(matcherType);
		logger.debug("in InstanceBasedMatcherServiceImpl");
		logger.debug("matcherType: " + matcherType);
	}

	public List<Matching> match(Schema schema1, Schema schema2, DataSource dataSource1, DataSource dataSource2,
			Map<ControlParameterType, ControlParameter> controlParameters) throws ExecutionException {
		Set<CanonicalModelConstruct> constructs1Set = schema1.getSuperAbstractsAndSuperLexicals();
		Set<CanonicalModelConstruct> constructs2Set = schema2.getSuperAbstractsAndSuperLexicals();
		// TOOD this is a hack ...
		List<CanonicalModelConstruct> constructs1 = new ArrayList<CanonicalModelConstruct>();
		for (CanonicalModelConstruct construct1 : constructs1Set)
			constructs1.add(construct1);
		List<CanonicalModelConstruct> constructs2 = new ArrayList<CanonicalModelConstruct>();
		for (CanonicalModelConstruct construct2 : constructs2Set)
			constructs1.add(construct2);

		return this.match(constructs1, constructs2, dataSource1, dataSource2, controlParameters);
	}

	/**
	 * Klitos note: This seems to use the new concurrent InstanceMatcherService
	 */
	public List<Matching> match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2, DataSource dataSource1,
			DataSource dataSource2, Map<ControlParameterType, ControlParameter> controlParameters) throws ExecutionException {

		ConcurrentInstanceMatcherForSuperAbstractsServiceImpl concurrentInstanceMatcherForSuperAbstractsService = new ConcurrentInstanceMatcherForSuperAbstractsServiceImpl(
				matchingProducerService, matchingProfilerServiceImpl, this.getChildMatchers(), this, this.matchingService);
		List<SuperAbstract> superAbstracts1 = this.getSuperAbstracts(constructs1);
		List<SuperAbstract> superAbstracts2 = this.getSuperAbstracts(constructs2);
		logger.info("superAbstracts1.size(): " + superAbstracts1.size() + ", thread: " + Thread.currentThread());
		logger.info("superAbstracts2.size(): " + superAbstracts2.size() + ", thread: " + Thread.currentThread());
		Map<SuperAbstract, QueryResult> superAbstractQueryResultsMap = this.getQueryResultsForSuperAbstractsInSchemas(superAbstracts1, superAbstracts2, dataSource1, dataSource2);
		logger.info("concurrentInstanceMatcherForSuperAbstractsService: " + concurrentInstanceMatcherForSuperAbstractsService);

		ConcurrentLinkedQueue<Matching> concurrentQueueOfMatches = concurrentInstanceMatcherForSuperAbstractsService.match(superAbstracts1, superAbstracts2, superAbstractQueryResultsMap,
				controlParameters);
		logger.info("concurrentQueueOfMatches: " + concurrentQueueOfMatches);
		logger.info("concurrentQueueOfMatches.size(): " + concurrentQueueOfMatches.size());
		List<Matching> listOfMatchings = new ArrayList<Matching>(concurrentQueueOfMatches);
		return listOfMatchings;

		/*
		float[][] simMatrix = this.match(constructs1, constructs2, dataSource1, dataSource2);
		return matchingProducerService.produceMatches(simMatrix, constructs1, constructs2, controlParameters, this);
		*/
	}

	//@Transactional
	protected float[][] match(List<CanonicalModelConstruct> constructs1, List<CanonicalModelConstruct> constructs2, DataSource dataSource1,
			DataSource dataSource2) {
		//[constructs1][constructs2]

		// TODO refactor ... pull parts out for better testing

		logger.debug("in match");
		logger.debug("constructs1: " + constructs1);
		logger.debug("constructs2: " + constructs2);
		logger.debug("dataSource1: " + dataSource1);
		logger.debug("dataSource2: " + dataSource2);

		float[][] simMatrix = new float[constructs1.size()][constructs2.size()];

		List<SuperAbstract> superAbstracts1 = this.getSuperAbstracts(constructs1);
		logger.debug("superAbstracts1: " + superAbstracts1);
		List<SuperAbstract> superAbstracts2 = this.getSuperAbstracts(constructs2);
		logger.debug("superAbstracts2: " + superAbstracts2);

		Map<SuperAbstract, QueryResult> superAbstractQueryResultsMap = this.getQueryResultsForSuperAbstractsInSchemas(superAbstracts1,
				superAbstracts2, dataSource1, dataSource2);

		logger.info("superAbstracts1.size(): " + superAbstracts1.size() + ", thread: " + Thread.currentThread());
		logger.info("superAbstracts2.size(): " + superAbstracts2.size() + ", thread: " + Thread.currentThread());
		for (SuperAbstract superAbstract1 : superAbstracts1) {
			logger.debug("superAbstract1.getName: " + superAbstract1.getName());
			logger.debug("superAbstract1: " + superAbstract1);
			for (SuperAbstract superAbstract2 : superAbstracts2) {
				logger.debug("superAbstract2: " + superAbstract2);
				logger.debug("superAbstract2.getName: " + superAbstract2.getName());

				QueryResult queryResult1 = superAbstractQueryResultsMap.get(superAbstract1);
				QueryResult queryResult2 = superAbstractQueryResultsMap.get(superAbstract2);

				ResultType resultType1 = queryResult1.getResultType();
				logger.debug("resultType1: " + resultType1);
				List<ResultInstance> resultInstances1 = queryResult1.getResultInstances();
				logger.debug("resultInstances1: " + resultInstances1);
				LinkedHashSet<String> resultFields1 = new LinkedHashSet<String>(resultType1.getResultFields().keySet());
				logger.debug("resultFields1: " + resultFields1);

				ResultType resultType2 = queryResult2.getResultType();
				logger.debug("resultType2: " + resultType2);
				List<ResultInstance> resultInstances2 = queryResult2.getResultInstances();
				logger.debug("resultInstances2: " + resultInstances2);
				LinkedHashSet<String> resultFields2 = new LinkedHashSet<String>(resultType2.getResultFields().keySet());
				logger.debug("resultFields2: " + resultFields2);

				logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
				long startTime = System.nanoTime();
				logger.info("start matching instances: " + startTime + ", thread: " + Thread.currentThread());
				float[][] instanceBasedSimMatrix = matchInstances(resultFields1, resultFields2, resultInstances1, resultInstances2);
				long endTime = System.nanoTime();
				logger.info("finished matching instances: " + endTime + ", thread: " + Thread.currentThread());
				logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
				logger.info("duration for matching instances: " + (endTime - startTime) / 1.0e9 + ", thread: " + Thread.currentThread());

				int sumOfLengthOfValuesOfInstancesForSuperAbstract1 = 0;
				int sumOfLengthOfValuesOfInstancesForSuperAbstract2 = 0;

				for (ResultInstance instance : resultInstances1) {
					for (String fieldName : instance.getResultFieldNameResultValueMap().keySet()) {
						sumOfLengthOfValuesOfInstancesForSuperAbstract1 += instance.getResultValue(fieldName).getValue().length();
					}
				}
				for (ResultInstance instance : resultInstances2) {
					for (String fieldName : instance.getResultFieldNameResultValueMap().keySet()) {
						sumOfLengthOfValuesOfInstancesForSuperAbstract2 += instance.getResultValue(fieldName).getValue().length();
					}
				}

				InstancesPerTwoSuperAbstractsMatchDurationDetails instancesPerTwoSuperAbstractsMatchDurationDetails = new InstancesPerTwoSuperAbstractsMatchDurationDetails(
						resultInstances1.size(), resultInstances2.size(), resultFields1.size(), resultFields2.size(),
						sumOfLengthOfValuesOfInstancesForSuperAbstract1, sumOfLengthOfValuesOfInstancesForSuperAbstract2,
						(float) ((endTime - startTime) / 1.0e9));

				this.matchingProfilerServiceImpl
						.addInstancesPerTwoSuperAbstractsMatchDurationDetails(instancesPerTwoSuperAbstractsMatchDurationDetails);

				logger.info("sumOfLengthOfValuesOfInstancesForSuperAbstract1: " + sumOfLengthOfValuesOfInstancesForSuperAbstract1 + ", thread: "
						+ Thread.currentThread());
				logger.info("sumOfLengthOfValuesOfInstancesForSuperAbstract2: " + sumOfLengthOfValuesOfInstancesForSuperAbstract2 + ", thread: "
						+ Thread.currentThread());

				//TODO the similarities in the matrix are only between superLexicals, not between superAbstracts
				//TODO not doing any propagation of similarities up to superAbstracts for now ... think about this

				int resultField1Index = 0;
				int resultField2Index = 0;

				for (String resultFieldName1 : resultFields1) {

					resultField2Index = 0;

					for (String resultFieldName2 : resultFields2) {

						logger.debug("resultFieldName1: " + resultFieldName1);
						logger.debug("resultFieldName2: " + resultFieldName2);

						logger.debug("resultField1Index: " + resultField1Index);
						logger.debug("resultField2Index: " + resultField2Index);

						ResultField field1 = resultType1.getResultFieldWithName(resultFieldName1);
						ResultField field2 = resultType2.getResultFieldWithName(resultFieldName2);
						logger.debug("field1: " + field1);
						logger.debug("field2: " + field2);

						if (field1 != null) {
							CanonicalModelConstruct construct1 = field1.getCanonicalModelConstruct();
							logger.debug("construct1: " + construct1);

							if (field2 != null) {
								CanonicalModelConstruct construct2 = field2.getCanonicalModelConstruct();
								logger.debug("construct2: " + construct2);

								if (constructs1.contains(construct1)) {
									logger.debug("found construct1 in constructs1");

									if (constructs2.contains(construct2)) {
										logger.debug("found construct2 in constructs2");

										logger.debug("resultField1Index: " + resultField1Index);
										logger.debug("resultField2Index: " + resultField2Index);

										simMatrix[constructs1.indexOf(construct1)][constructs2.indexOf(construct2)] = instanceBasedSimMatrix[resultField1Index][resultField2Index];

									} else
										logger.error("didn't find construct2 in constructs2, construct2: " + construct2);
								} else
									logger.error("didn't find construct1 in constructs1, construct1: " + construct1);
							} else
								logger.error("didn't find resultField2 with name: " + resultFieldName2);
						} else
							logger.error("didn't find resultField1 with name: " + resultFieldName1);

						resultField2Index++;
					}

					resultField1Index++;
				}
			}
		}

		logger.debug("simMatrix: " + simMatrix);
		return simMatrix;
	}

	private float[][] matchInstances(LinkedHashSet<String> resultFields1, LinkedHashSet<String> resultFields2, List<ResultInstance> resultInstances1,
			List<ResultInstance> resultInstances2) {
		logger.debug("in matchInstances");
		logger.debug("resultFields1: " + resultFields1);
		logger.debug("resultFields2: " + resultFields2);
		logger.debug("resultFields1.size(): " + resultFields1.size());
		logger.debug("resultFields2.size(): " + resultFields2.size());
		logger.debug("resultInstances1: " + resultInstances1);
		logger.debug("resultInstances2: " + resultInstances2);
		logger.debug("resultInstances1.size(): " + resultInstances1.size());
		logger.debug("resultInstances2.size(): " + resultInstances2.size());

		float[][] resultFieldsSimMatrix = new float[resultFields1.size()][resultFields2.size()];

		int resultField1Index = 0;
		int resultField2Index = 0;

		logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
		long startTime = System.nanoTime();
		logger.info("start matching all instances for all resultFields: " + startTime + ", thread: " + Thread.currentThread());

		for (String resultField1 : resultFields1) {

			resultField2Index = 0;

			for (String resultField2 : resultFields2) {

				logger.debug("resultField1Index: " + resultField1Index);
				logger.debug("resultField2Index: " + resultField2Index);

				logger.debug("resultField1: " + resultField1);
				logger.debug("resultField2: " + resultField2);

				float[][] instanceSimMatrix = new float[resultInstances1.size()][resultInstances2.size()];

				logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
				long startTimeInstancesOfTwoResultFields = System.nanoTime();
				logger.info("start matching all instances of two resultFields: " + startTimeInstancesOfTwoResultFields + ", thread: "
						+ Thread.currentThread());
				for (ResultInstance resultInstance1 : resultInstances1) {
					String value1 = resultInstance1.getResultValue(resultField1).getValue();
					for (ResultInstance resultInstance2 : resultInstances2) {
						logger.debug("resultInstance1: " + resultInstance1);
						logger.debug("resultInstance2: " + resultInstance2);

						String value2 = resultInstance2.getResultValue(resultField2).getValue();
						logger.debug("value1: " + value1);
						logger.debug("value2: " + value2);

						float instanceSim = 0f;

						long startTimeTwoInstanceValues = System.nanoTime();
						logger.debug("start matching two instance values: " + startTimeTwoInstanceValues);
						logger.debug("value1.length(): " + value1.length());
						logger.debug("value2.length(): " + value2.length());
						if (value1 != null && value2 != null) {
							float[] simArray = this.runChildMatchers(value1, value2);
							logger.debug("simArray: " + simArray);
							instanceSim = this.aggregate(simArray);
							logger.debug("instanceSim: " + instanceSim);
						}

						instanceSimMatrix[resultInstances1.indexOf(resultInstance1)][resultInstances2.indexOf(resultInstance2)] = instanceSim;
						long endTimeTwoInstanceValues = System.nanoTime();
						logger.debug("finished matching two instanceValues: " + endTimeTwoInstanceValues);
						logger.debug("value1.length(): " + value1.length());
						logger.debug("value2.length(): " + value2.length());
						logger.debug("duration for matching two instanceValues: " + (endTimeTwoInstanceValues - startTimeTwoInstanceValues) / 1.0e9);

						NGramMatchDurationDetails nGramMatchDurationDetails = new NGramMatchDurationDetails(value1.length(), value2.length(),
								(float) ((endTimeTwoInstanceValues - startTimeTwoInstanceValues) / 1.0e9));
						this.matchingProfilerServiceImpl.addNGramMatchDurationDetails(nGramMatchDurationDetails);
					}
				}
				logger.debug("calculated instanceSimMatrix");
				logger.debug("instanceSimMatrix: " + instanceSimMatrix);

				float sumMax1 = 0F;
				for (int i = 0; i < resultInstances1.size(); i++) {
					float max = 0F;
					for (int j = 0; j < resultInstances2.size(); j++) {
						if (instanceSimMatrix[i][j] > max)
							max = instanceSimMatrix[i][j];
					}
					sumMax1 += max;
				}

				float sumMax2 = 0F;
				for (int i = 0; i < resultInstances2.size(); i++) {
					float max = 0;
					for (int j = 0; j < resultInstances1.size(); j++) {
						if (instanceSimMatrix[j][i] > max)
							max = instanceSimMatrix[j][i];
					}
					sumMax2 += max;
				}

				logger.debug("sumMax1: " + sumMax1);
				logger.debug("sumMax2: " + sumMax2);

				float similarity = (sumMax1 + sumMax2) / (resultInstances1.size() + resultInstances2.size());
				logger.debug("similarity: " + similarity);

				resultFieldsSimMatrix[resultField1Index][resultField2Index] = similarity;

				long endTimeInstancesOfTwoResultFields = System.nanoTime();
				logger.info("finished matching all instances of two resultFields: " + endTimeInstancesOfTwoResultFields + ", thread: "
						+ Thread.currentThread());
				logger.info("resultField1: " + resultField1 + ", thread: " + Thread.currentThread());
				logger.info("resultField2: " + resultField2 + ", thread: " + Thread.currentThread());
				logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
				logger.info("duration for matching all instances of two resultFields: "
						+ (endTimeInstancesOfTwoResultFields - startTimeInstancesOfTwoResultFields) / 1.0e9 + ", thread: " + Thread.currentThread());

				int sumOfLengthOfValuesOfInstancesForResultField1 = 0;
				int sumOfLengthOfValuesOfInstancesForResultField2 = 0;

				for (ResultInstance resultInstance : resultInstances1) {
					String value = resultInstance.getResultValue(resultField1).getValue();
					sumOfLengthOfValuesOfInstancesForResultField1 += value.length();
				}
				for (ResultInstance resultInstance : resultInstances2) {
					String value = resultInstance.getResultValue(resultField2).getValue();
					sumOfLengthOfValuesOfInstancesForResultField2 += value.length();
				}

				InstancesPerTwoResultFieldsMatchDurationDetails instancesPerTwoResultFieldsMatchDurationDetails = new InstancesPerTwoResultFieldsMatchDurationDetails(
						resultInstances1.size(), resultInstances2.size(), sumOfLengthOfValuesOfInstancesForResultField1,
						sumOfLengthOfValuesOfInstancesForResultField2,
						(float) ((endTimeInstancesOfTwoResultFields - startTimeInstancesOfTwoResultFields) / 1.0e9));
				this.matchingProfilerServiceImpl.addInstancesPerTwoResultFieldsMatchDurationDetails(instancesPerTwoResultFieldsMatchDurationDetails);
				logger.info("sumOfLengthOfValuesOfInstancesForResultField1: " + sumOfLengthOfValuesOfInstancesForResultField1 + ", thread: "
						+ Thread.currentThread());
				logger.info("sumOfLengthOfValuesOfInstancesForResultField2: " + sumOfLengthOfValuesOfInstancesForResultField2 + ", thread: "
						+ Thread.currentThread());

				logger.debug("resultField1Index: " + resultField1Index);
				logger.debug("resultField2Index: " + resultField2Index);

				resultField2Index++;

				logger.debug("resultField1Index: " + resultField1Index);
				logger.debug("resultField2Index: " + resultField2Index);
			}

			logger.debug("resultField1Index: " + resultField1Index);
			logger.debug("resultField2Index: " + resultField2Index);

			resultField1Index++;

			logger.debug("resultField1Index: " + resultField1Index);
			logger.debug("resultField2Index: " + resultField2Index);
		}
		long endTime = System.nanoTime();
		logger.info("finished matching instances for all resultFields: " + endTime + ", thread: " + Thread.currentThread());
		logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields1.size() * resultInstances1.size(): " + (resultFields1.size() * resultInstances1.size()) + ", thread: "
				+ Thread.currentThread());
		logger.info("resultFields2.size() * resultInstances2.size(): " + (resultFields2.size() * resultInstances2.size()) + ", thread: "
				+ Thread.currentThread());

		logger.info("duration for matching constructs: " + (endTime - startTime) / 1.0e9 + ", thread: " + Thread.currentThread());
		logger.debug("resultFieldsSimMatrix: " + resultFieldsSimMatrix);

		return resultFieldsSimMatrix;
	}

	protected QueryResult getQueryResultForSingleSuperAbstract(SuperAbstract superAbstract, DataSource dataSource,
			Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.info("in getQueryResultForSingleSuperAbstract" + ", thread: " + Thread.currentThread());
		logger.info("superAbstract: " + superAbstract + ", thread: " + Thread.currentThread());
		String superAbstractName = superAbstract.getName();
		logger.debug("superAbstractName: " + superAbstractName);
		String queryString = "Select * from " + superAbstractName;
		String queryName = "Get" + superAbstractName + "Instances_" + dataSource.getName();

		logger.debug("queryString: " + queryString);
		logger.debug("queryName: " + queryName);

		Query query = queryRepository.getQueryWithName(queryName);
		QueryResult queryResult = null;

		if (query == null) {
			query = new Query(queryName, queryString);
			query.addSchema(dataSource.getSchema());
			query.addDataSource(dataSource);

			logger.debug("queryService: " + queryService);
			queryService.addQuery(query);
			logger.debug("query.getId(): " + query.getId());
			queryResult = queryService.evaluateQuery(query, null, null, controlParameters);
			logger.debug("queryResult: " + queryResult);
			logger.debug("queryResult.getId: " + queryResult.getId());
		} else {
			logger.debug("found query with queryName: " + queryName);
			queryResult = queryResultRepository.getQueryResultForQuery(query);
		}
		logger.info("superAbstract: " + superAbstract + ", thread: " + Thread.currentThread());
		logger.info("queryResult.getResultInstances().size(): " + queryResult.getResultInstances().size() + ", thread: " + Thread.currentThread());
		return queryResult;
	}

	protected Map<SuperAbstract, QueryResult> getQueryResultsForSuperAbstractsInSchemas(List<SuperAbstract> superAbstracts1,
			List<SuperAbstract> superAbstracts2, DataSource dataSource1, DataSource dataSource2) {
		Map<SuperAbstract, QueryResult> superAbstractQueryResultsMap = new HashMap<SuperAbstract, QueryResult>();

		ControlParameter controlParameter = new ControlParameter(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS, "50");
		Map<ControlParameterType, ControlParameter> controlParameters = new HashMap<ControlParameterType, ControlParameter>();
		controlParameters.put(ControlParameterType.MAX_NUMBER_OF_QUERY_RESULTS, controlParameter);
		logger.debug("controlParameters: " + controlParameters);

		for (SuperAbstract superAbstract1 : superAbstracts1)
			superAbstractQueryResultsMap.put(superAbstract1,
					this.getQueryResultForSingleSuperAbstract(superAbstract1, dataSource1, controlParameters));

		for (SuperAbstract superAbstract2 : superAbstracts2)
			superAbstractQueryResultsMap.put(superAbstract2,
					this.getQueryResultForSingleSuperAbstract(superAbstract2, dataSource2, controlParameters));

		return superAbstractQueryResultsMap;
	}

	private List<SuperAbstract> getSuperAbstracts(List<CanonicalModelConstruct> constructs) {
		logger.debug("in getSuperAbstracts");
		List<SuperAbstract> superAbstracts = new ArrayList<SuperAbstract>();
		for (CanonicalModelConstruct construct : constructs) {
			if (construct.getTypeOfConstruct() == ConstructType.SUPER_ABSTRACT) {
				superAbstracts.add((SuperAbstract) construct);
				logger.debug("got superAbstract: " + construct.getName());
			}
		}
		return superAbstracts;
	}

	protected float[] runChildMatchers(String string1, String string2) {
		// [childMatchers]
		logger.debug("in runChildMatchers");
		logger.debug("string1: " + string1);
		logger.debug("string2: " + string2);
		if (this.getChildMatchers() == null || this.getChildMatchers().size() == 0)
			logger.error("matcher should have child matchers - sort this");
		else {
			float[] simArray = new float[this.getChildMatchers().size()];
			List<MatcherService> childMatchers = this.getChildMatchers();
			for (MatcherService matcher : childMatchers) {
				logger.debug("matcher.getName: " + matcher.getName());
				float sim = 0;
				if (matcher instanceof StringBasedMatcherServiceImpl) {
					sim = ((StringBasedMatcherServiceImpl) matcher).match(string1, string2);
					simArray[childMatchers.indexOf(matcher)] = sim;
					logger.debug("sim: " + sim);
				} else
					logger.error("childMatchers should only be StringBasedMatchers, matcher: " + matcher);
			}
			return simArray;
		}
		return null;
	}

	/**
	 * @param queryService the queryService to set
	 */
	public void setQueryService(QueryService queryService) {
		this.queryService = queryService;
	}

	/**
	 * @param queryRepository the queryRepository to set
	 */
	public void setQueryRepository(QueryRepository queryRepository) {
		this.queryRepository = queryRepository;
	}

	/**
	 * @param queryResultRepository the queryResultRepository to set
	 */
	public void setQueryResultRepository(QueryResultRepository queryResultRepository) {
		this.queryResultRepository = queryResultRepository;
	}

	/**
	 * @param matchingProfilerServiceImpl the matchingProfilerServiceImpl to set
	 */
	public void setMatchingProfilerServiceImpl(MatchingProfilerServiceImpl matchingProfilerServiceImpl) {
		this.matchingProfilerServiceImpl = matchingProfilerServiceImpl;
	}

}
