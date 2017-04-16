/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.Matching;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.matching.OneToOneMatching;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.QueryResult;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameter;
import uk.ac.manchester.dstoolkit.domain.provenance.ControlParameterType;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.InstancesPerTwoResultFieldsMatchDurationDetails;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.InstancesPerTwoSuperAbstractsMatchDurationDetails;
import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler.MatchingProfilerServiceImpl;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.InstanceBasedMatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatcherService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingProducerService;
import uk.ac.manchester.dstoolkit.service.morphisms.matching.MatchingService;

/**
 * @author chedeler
 * 
 */
@Scope("prototype")
@Service
public class ConcurrentInstanceMatcherForSuperAbstractsServiceImpl {

	// TODO test this properly

	private final static Logger logger = Logger.getLogger(ConcurrentInstanceMatcherForSuperAbstractsServiceImpl.class);

	private final MatchingService matchingService;
	private final MatchingProducerService matchingProducerService;
	private final MatchingProfilerServiceImpl matchingProfilerServiceImpl;
	private final List<MatcherService> childMatchers;
	private final InstanceBasedMatcherService instanceBasedMatcherService;

	// final private CountDownLatch latch = new CountDownLatch(1);

	public ConcurrentInstanceMatcherForSuperAbstractsServiceImpl(final MatchingProducerService matchingProducerService, final MatchingProfilerServiceImpl matchingProfilerServiceImpl,
			final List<MatcherService> childMatchers, final InstanceBasedMatcherService instanceBasedMatcherService, final MatchingService matchingService) {
		logger.debug("in ConcurrentInstanceMatcherForSuperAbstractsServiceImpl");
		this.matchingProducerService = matchingProducerService;
		this.matchingProfilerServiceImpl = matchingProfilerServiceImpl;
		this.childMatchers = Collections.unmodifiableList(childMatchers);
		this.instanceBasedMatcherService = instanceBasedMatcherService;
		this.matchingService = matchingService;
	}

	public ConcurrentLinkedQueue<Matching> match(final List<SuperAbstract> superAbstracts1, final List<SuperAbstract> superAbstracts2,
			final Map<SuperAbstract, QueryResult> superAbstractQueryResultsMap, final Map<ControlParameterType, ControlParameter> controlParameters) throws ExecutionException {
		logger.info("in public match, thread: " + Thread.currentThread());
		final List<Callable<ListOfMatchings>> listOfListOfMatchings = new ArrayList<Callable<ListOfMatchings>>();
		int noOfSuperAbstracts1Processed = 0;
		for (final SuperAbstract superAbstract1 : superAbstracts1) {
			int noOfSuperAbstracts2Processed = 0;
			for (final SuperAbstract superAbstract2 : superAbstracts2) {
				logger.info("superAbstract1: " + superAbstract1 + ", thread: " + Thread.currentThread());
				logger.info("superAbstract2: " + superAbstract2 + ", thread: " + Thread.currentThread());
				final List<CanonicalModelConstruct> constructs1 = new ArrayList<CanonicalModelConstruct>(Collections.unmodifiableSet(superAbstract1.getSuperLexicals()));
				final List<CanonicalModelConstruct> constructs2 = new ArrayList<CanonicalModelConstruct>(Collections.unmodifiableSet(superAbstract2.getSuperLexicals()));
				logger.info("constructs1.size(): " + constructs1.size() + ", thread: " + Thread.currentThread());
				logger.info("constructs2.size(): " + constructs2.size() + ", thread: " + Thread.currentThread());
				final QueryResult queryResult1 = superAbstractQueryResultsMap.get(superAbstract1);
				final QueryResult queryResult2 = superAbstractQueryResultsMap.get(superAbstract2);
				listOfListOfMatchings.add(new Callable<ListOfMatchings>() {

					public ListOfMatchings call() {
						return match(constructs1, constructs2, queryResult1, queryResult2, controlParameters);
					}
				});
				noOfSuperAbstracts2Processed++;
				logger.info("noOfSuperAbstracts1Processed: " + noOfSuperAbstracts1Processed + ", thread: " + Thread.currentThread());
				logger.info("noOfSuperAbstracts2Processed: " + noOfSuperAbstracts2Processed + ", thread: " + Thread.currentThread());
				logger.info("listOfListOfMatchings.size(): " + listOfListOfMatchings.size() + ", thread: " + Thread.currentThread());
			}
			noOfSuperAbstracts1Processed++;
		}

		final ConcurrentLinkedQueue<Matching> matchings = new ConcurrentLinkedQueue<Matching>();
		logger.info("Runtime.getRuntime().availableProcessors(): " + Runtime.getRuntime().availableProcessors());
		final ExecutorService executorPool = Executors.newFixedThreadPool(14);
		int numberOfListOfMatchingsReturned = 0;
		try {
			final List<Future<ListOfMatchings>> listOfMatchingsBetweenPairsOfSuperAbstracts = executorPool.invokeAll(listOfListOfMatchings); // , 500, TimeUnit.MINUTES);
			executorPool.shutdown();
			for (final Future<ListOfMatchings> listOfMatchings : listOfMatchingsBetweenPairsOfSuperAbstracts) {
				logger.info("listOfMatchings: " + listOfMatchings + ", thread: " + Thread.currentThread());
				logger.info("got matchings, adding them to list" + ", thread: " + Thread.currentThread());
				try {
					logger.info("listOfMatchings.get(): " + listOfMatchings.get() + ", thread: " + Thread.currentThread());
					// matchings.addAll(listOfMatchings.get().matchings);
					final List<Matching> matchingsToSave = listOfMatchings.get().matchings;
					
					/*Klitos: shows how to same matchings*/
					final boolean addedMatchings = this.instanceBasedMatcherService.addMatchings(matchingsToSave);
					final boolean savedMatches = this.saveMatches(matchingsToSave);
					matchings.addAll(matchingsToSave);
					numberOfListOfMatchingsReturned++;
					logger.info("numberOfListOfMatchingsReturned: " + numberOfListOfMatchingsReturned + ", thread: " + Thread.currentThread());
					logger.info("listOfMatchings.get().matchings.size(): " + listOfMatchings.get().matchings.size() + ", thread: " + Thread.currentThread());
				} catch (ExecutionException e) { 
					logger.error("ExecutionException: " + e); 
					logger.error("ExecutionException cause: " + e.getCause()); 
					e.printStackTrace(); 
				} catch (CancellationException e) {
					logger.error("CancellationException: " + e);
					logger.error("CancellationException cause: " + e.getCause());
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			logger.error("InterruptedException: " + e);
			e.printStackTrace();
		}
		// if (noOfSuperAbstracts1Processed.intValue() == numberOfListOfMatchingsReturned.intValue())
		// latch.countDown();
		logger.info("matchings.size(): " + matchings.size() + ", thread: " + Thread.currentThread());
		return matchings;
	}

	// TODO this is a hack to try and stop a race condition that made a 4 day job fail ...
	private boolean saveMatches(List<Matching> matches) {
		logger.info("in saveMatches" + ", thread: " + Thread.currentThread());
		logger.info("matches.size(): " + matches.size() + ", thread: " + Thread.currentThread());
		for (final Matching matching : matches) {
			if (matching instanceof OneToOneMatching) {
				final OneToOneMatching oneToOne = (OneToOneMatching) matching;
				logger.debug("oneToOne.getConstruct1(): " + oneToOne.getConstruct1());
				logger.debug("oneToOne.getConstruct2(): " + oneToOne.getConstruct2());
				logger.debug("oneToOne.getConstruct1().getName(): " + oneToOne.getConstruct1().getName());
				logger.debug("oneToOne.getConstruct2().getName(): " + oneToOne.getConstruct2().getName());
				logger.debug("oneToOne.getScore(): " + oneToOne.getScore());
				logger.debug("oneToOne.getMatcherName(): " + oneToOne.getMatcherName());
			}
			matchingService.addMatching(matching);
		}
		return true;
	}

	private ListOfMatchings match(final List<CanonicalModelConstruct> constructs1, final List<CanonicalModelConstruct> constructs2, final QueryResult queryResult1, final QueryResult queryResult2,
			final Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.info("in match" + ", thread: " + Thread.currentThread());
		logger.debug("constructs1: " + constructs1);
		logger.debug("constructs2: " + constructs2);
		logger.debug("queryResult1: " + queryResult1);
		logger.debug("queryResult2: " + queryResult2);

		final ResultType resultType1 = queryResult1.getResultType();
		logger.debug("resultType1: " + resultType1);
		final List<ResultInstance> resultInstances1 = Collections.unmodifiableList(queryResult1.getResultInstances());
		logger.debug("resultInstances1: " + resultInstances1);
		final LinkedHashSet<String> resultFields1 = new LinkedHashSet<String>(Collections.unmodifiableSet(resultType1.getResultFields().keySet()));
		logger.debug("resultFields1: " + resultFields1);

		final ResultType resultType2 = queryResult2.getResultType();
		logger.debug("resultType2: " + resultType2);
		final List<ResultInstance> resultInstances2 = Collections.unmodifiableList(queryResult2.getResultInstances());
		logger.debug("resultInstances2: " + resultInstances2);
		final LinkedHashSet<String> resultFields2 = new LinkedHashSet<String>(Collections.unmodifiableSet(resultType2.getResultFields().keySet()));
		logger.debug("resultFields2: " + resultFields2);

		logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
		final long startTime = System.nanoTime();
		logger.info("start matching instances: " + startTime + ", thread: " + Thread.currentThread());
		final float[][] instanceBasedSimMatrix = matchInstances(resultFields1, resultFields2, resultInstances1, resultInstances2, controlParameters);
		final long endTime = System.nanoTime();
		logger.info("instanceBasedSimMatrix: " + instanceBasedSimMatrix + ", thread: " + Thread.currentThread());
		logger.info("finished matching instances: " + endTime + ", thread: " + Thread.currentThread());
		logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
		logger.info("duration for matching instances: " + (endTime - startTime) / 1.0e9 + ", thread: " + Thread.currentThread());

		final int sumOfLengthOfValuesOfInstancesForSuperAbstract1 = this.calculateSumOfLengthOfResultValues(resultInstances1);
		final int sumOfLengthOfValuesOfInstancesForSuperAbstract2 = this.calculateSumOfLengthOfResultValues(resultInstances2);

		logger.info("before adding instancesPerTwoSuperAbstractsMatchDurationDetails" + ", thread: " + Thread.currentThread());
		final InstancesPerTwoSuperAbstractsMatchDurationDetails instancesPerTwoSuperAbstractsMatchDurationDetails = new InstancesPerTwoSuperAbstractsMatchDurationDetails(resultInstances1.size(),
				resultInstances2.size(), resultFields1.size(), resultFields2.size(), sumOfLengthOfValuesOfInstancesForSuperAbstract1, sumOfLengthOfValuesOfInstancesForSuperAbstract2,
				(float) ((endTime - startTime) / 1.0e9));
		this.matchingProfilerServiceImpl.addInstancesPerTwoSuperAbstractsMatchDurationDetails(instancesPerTwoSuperAbstractsMatchDurationDetails);
		logger.info("after adding instancesPerTwoSuperAbstractsMatchDurationDetails" + ", thread: " + Thread.currentThread());
		logger.info("sumOfLengthOfValuesOfInstancesForSuperAbstract1: " + sumOfLengthOfValuesOfInstancesForSuperAbstract1 + ", thread: " + Thread.currentThread());
		logger.info("sumOfLengthOfValuesOfInstancesForSuperAbstract2: " + sumOfLengthOfValuesOfInstancesForSuperAbstract2 + ", thread: " + Thread.currentThread());

		// TODO the similarities in the matrix are only between superLexicals, not between superAbstracts
		// TODO not doing any propagation of similarities up to superAbstracts for now ... think about this

		final float[][] simMatrix = this.constructSimilaritiesMatrix(constructs1, constructs2, resultType1, resultType2, resultFields1, resultFields2, instanceBasedSimMatrix);
		logger.info("simMatrix: " + simMatrix + ", thread: " + Thread.currentThread());
		logger.info("matchingProducerService: " + matchingProducerService + ", thread: " + Thread.currentThread());
		final List<Matching> matchingList = matchingProducerService.produceMatches(simMatrix, constructs1, constructs2, controlParameters, instanceBasedMatcherService);
		logger.info("matchingList: " + matchingList + ", thread: " + Thread.currentThread());
		logger.info("matchingList.size(): " + matchingList.size() + ", thread: " + Thread.currentThread());
		final ListOfMatchings listOfMatchings = new ListOfMatchings(matchingList);
		logger.info("listOfMatchings: " + listOfMatchings + ", thread: " + Thread.currentThread());
		logger.info("returning listOfMatchings with matchingList: " + listOfMatchings.matchings.size() + ", thread: " + Thread.currentThread());
		return listOfMatchings;

	}

	private float[][] constructSimilaritiesMatrix(final List<CanonicalModelConstruct> constructs1, final List<CanonicalModelConstruct> constructs2, final ResultType resultType1,
			final ResultType resultType2, final LinkedHashSet<String> resultFields1, final LinkedHashSet<String> resultFields2, final float[][] instanceBasedSimMatrix) {
		logger.info("in constructSimilaritiesMatrix, thread: " + Thread.currentThread());
		logger.info("instanceBasedSimMatrix: " + instanceBasedSimMatrix + ", thread: " + Thread.currentThread());
		final float[][] simMatrix = new float[constructs1.size()][constructs2.size()];
		int resultField1Index = 0;

		for (final String resultFieldName1 : resultFields1) {

			int resultField2Index = 0;

			for (final String resultFieldName2 : resultFields2) {

				logger.debug("resultFieldName1: " + resultFieldName1);
				logger.debug("resultFieldName2: " + resultFieldName2);

				logger.debug("resultField1Index: " + resultField1Index);
				logger.debug("resultField2Index: " + resultField2Index);

				final ResultField field1 = resultType1.getResultFieldWithName(resultFieldName1);
				final ResultField field2 = resultType2.getResultFieldWithName(resultFieldName2);
				logger.debug("field1: " + field1);
				logger.debug("field2: " + field2);

				if (field1 != null) {
					final CanonicalModelConstruct construct1 = field1.getCanonicalModelConstruct();
					logger.debug("construct1: " + construct1);

					if (field2 != null) {
						final CanonicalModelConstruct construct2 = field2.getCanonicalModelConstruct();
						logger.debug("construct2: " + construct2);

						if (constructs1.contains(construct1)) {
							logger.debug("found construct1 in constructs1");

							if (constructs2.contains(construct2)) {
								logger.debug("found construct2 in constructs2");

								logger.info("resultField1Index: " + resultField1Index + ", thread: " + Thread.currentThread());
								logger.info("resultField2Index: " + resultField2Index + ", thread: " + Thread.currentThread());

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

		logger.info("simMatrix: " + simMatrix + ", thread: " + Thread.currentThread());
		return simMatrix;
	}

	// TODO not sure this is going to work when run concurrently as sumOfLengthOfValuesOfInstancesForSuperAbstract is altered here ... use lock on method???

	private int calculateSumOfLengthOfResultValues(final List<ResultInstance> resultInstances) {
		logger.info("in calculateSumOfLengthOfResultValues, thread: " + Thread.currentThread());
		int sumOfLengthOfValuesOfInstancesForSuperAbstract = 0;
		for (final ResultInstance instance : resultInstances) {
			for (final String fieldName : Collections.unmodifiableSet(instance.getResultFieldNameResultValueMap().keySet())) {
				if (instance.getResultValue(fieldName).getValue() != null)
					sumOfLengthOfValuesOfInstancesForSuperAbstract += instance.getResultValue(fieldName).getValue().length();
			}
		}
		logger.info("sumOfLengthOfValuesOfInstancesForSuperAbstract: " + sumOfLengthOfValuesOfInstancesForSuperAbstract + ", thread: " + Thread.currentThread());
		return sumOfLengthOfValuesOfInstancesForSuperAbstract;
	}

	// TODO not sure this is going to work when run concurrently as sumMax1, sumMax2, max are altered here ... use lock on method???
	private float calculateSimilarity(final List<ResultInstance> resultInstances1, final List<ResultInstance> resultInstances2, final float[][] instanceSimMatrix) {
		logger.info("in calculateSimilarity, thread: " + Thread.currentThread());
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

		final float similarity = (sumMax1 + sumMax2) / (resultInstances1.size() + resultInstances2.size());
		logger.info("similarity: " + similarity + ", thread: " + Thread.currentThread());
		return similarity;
	}

	// TODO not sure this is going to work when run concurrently as resultFieldsSimMatrix is filled here ... use lock on method????
	private float[][] matchInstances(final LinkedHashSet<String> resultFields1, final LinkedHashSet<String> resultFields2, final List<ResultInstance> resultInstances1,
			final List<ResultInstance> resultInstances2, final Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.info("in matchInstances, thread: " + Thread.currentThread());
		logger.debug("resultFields1: " + resultFields1);
		logger.debug("resultFields2: " + resultFields2);
		logger.debug("resultFields1.size(): " + resultFields1.size());
		logger.debug("resultFields2.size(): " + resultFields2.size());
		logger.debug("resultInstances1: " + resultInstances1);
		logger.debug("resultInstances2: " + resultInstances2);
		logger.debug("resultInstances1.size(): " + resultInstances1.size());
		logger.debug("resultInstances2.size(): " + resultInstances2.size());

		final float[][] resultFieldsSimMatrix = new float[resultFields1.size()][resultFields2.size()];
		logger.info("resultFieldsSimMatrix: " + resultFieldsSimMatrix + ", thread: " + Thread.currentThread());
		int resultField1Index = 0;

		logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
		final long startTime = System.nanoTime();
		logger.info("start matching all instances for all resultFields: " + startTime + ", thread: " + Thread.currentThread());

		for (final String resultField1 : resultFields1) {

			int resultField2Index = 0;

			for (final String resultField2 : resultFields2) {

				logger.info("resultField1Index: " + resultField1Index + ", thread: " + Thread.currentThread());
				logger.info("resultField2Index: " + resultField2Index + ", thread: " + Thread.currentThread());

				logger.info("resultField1: " + resultField1 + ", thread: " + Thread.currentThread());
				logger.info("resultField2: " + resultField2 + ", thread: " + Thread.currentThread());

				final float[][] instanceSimMatrix = new float[resultInstances1.size()][resultInstances2.size()];

				logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
				final long startTimeInstancesOfTwoResultFields = System.nanoTime();
				logger.info("start matching all instances of two resultFields: " + startTimeInstancesOfTwoResultFields + ", thread: " + Thread.currentThread());
				for (final ResultInstance resultInstance1 : resultInstances1) {
					final String value1 = resultInstance1.getResultValue(resultField1).getValue();
					for (ResultInstance resultInstance2 : resultInstances2) {
						logger.debug("resultInstance1: " + resultInstance1);
						logger.debug("resultInstance2: " + resultInstance2);

						final String value2 = resultInstance2.getResultValue(resultField2).getValue();
						logger.debug("value1: " + value1 + ", thread: " + Thread.currentThread());
						logger.debug("value2: " + value2 + ", thread: " + Thread.currentThread());

						final long startTimeTwoInstanceValues = System.nanoTime();
						logger.debug("start matching two instance values: " + startTimeTwoInstanceValues + ", thread: " + Thread.currentThread());
						if (value1 != null && value2 != null && value1.length() > 0 && value2.length() > 0) {
							logger.debug("value1.length(): " + value1.length() + ", thread: " + Thread.currentThread());
							logger.debug("value2.length(): " + value2.length() + ", thread: " + Thread.currentThread());
							final float[] simArray = this.runChildMatchers(value1, value2);
							logger.debug("simArray: " + simArray);
							final float instanceSim = this.aggregate(simArray, controlParameters);
							logger.debug("instanceSim: " + instanceSim);
							instanceSimMatrix[resultInstances1.indexOf(resultInstance1)][resultInstances2.indexOf(resultInstance2)] = instanceSim;
						} else
							instanceSimMatrix[resultInstances1.indexOf(resultInstance1)][resultInstances2.indexOf(resultInstance2)] = 0f;

						final long endTimeTwoInstanceValues = System.nanoTime();
						logger.debug("finished matching two instanceValues: " + endTimeTwoInstanceValues + ", thread: " + Thread.currentThread());
						if (value1 != null)
							logger.debug("value1.length(): " + value1.length() + ", thread: " + Thread.currentThread());
						if (value2 != null)
							logger.debug("value2.length(): " + value2.length() + ", thread: " + Thread.currentThread());
						logger.debug("duration for matching two instanceValues: " + (endTimeTwoInstanceValues - startTimeTwoInstanceValues) / 1.0e9 + ", thread: " + Thread.currentThread());
						// logger.info("before adding nGramMatchDurationDetails" + ", thread: " + Thread.currentThread());
						// final NGramMatchDurationDetails nGramMatchDurationDetails = new NGramMatchDurationDetails(value1.length(), value2.length(),
						// (float) ((endTimeTwoInstanceValues - startTimeTwoInstanceValues) / 1.0e9));
						// this.matchingProfilerServiceImpl.addNGramMatchDurationDetails(nGramMatchDurationDetails);
						// logger.info("after adding nGramMatchDurationDetails" + ", thread: " + Thread.currentThread());
					}
				}
				logger.debug("calculated instanceSimMatrix");
				logger.info("instanceSimMatrix: " + instanceSimMatrix + ", thread: " + Thread.currentThread());
				logger.info("resultFieldsSimMatrix: " + resultFieldsSimMatrix + ", thread: " + Thread.currentThread());
				resultFieldsSimMatrix[resultField1Index][resultField2Index] = this.calculateSimilarity(resultInstances1, resultInstances2, instanceSimMatrix);

				final long endTimeInstancesOfTwoResultFields = System.nanoTime();
				logger.info("finished matching all instances of two resultFields: " + endTimeInstancesOfTwoResultFields + ", thread: " + Thread.currentThread());
				logger.info("resultField1: " + resultField1 + ", thread: " + Thread.currentThread());
				logger.info("resultField2: " + resultField2 + ", thread: " + Thread.currentThread());
				logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
				logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
				logger.info("duration for matching all instances of two resultFields: " + (endTimeInstancesOfTwoResultFields - startTimeInstancesOfTwoResultFields) / 1.0e9 + ", thread: "
						+ Thread.currentThread());

				final int sumOfLengthOfValuesOfInstancesForResultField1 = this.calculateSumOfLengthOfResultValues(resultInstances1);
				final int sumOfLengthOfValuesOfInstancesForResultField2 = this.calculateSumOfLengthOfResultValues(resultInstances2);

				logger.info("before adding instancesPerTwoResultFieldsMatchDurationDetails" + ", thread: " + Thread.currentThread());
				final InstancesPerTwoResultFieldsMatchDurationDetails instancesPerTwoResultFieldsMatchDurationDetails = new InstancesPerTwoResultFieldsMatchDurationDetails(resultInstances1.size(),
						resultInstances2.size(), sumOfLengthOfValuesOfInstancesForResultField1, sumOfLengthOfValuesOfInstancesForResultField2,
						(float) ((endTimeInstancesOfTwoResultFields - startTimeInstancesOfTwoResultFields) / 1.0e9));
				this.matchingProfilerServiceImpl.addInstancesPerTwoResultFieldsMatchDurationDetails(instancesPerTwoResultFieldsMatchDurationDetails);
				logger.info("after adding instancesPerTwoResultFieldsMatchDurationDetails" + ", thread: " + Thread.currentThread());
				logger.info("sumOfLengthOfValuesOfInstancesForResultField1: " + sumOfLengthOfValuesOfInstancesForResultField1 + ", thread: " + Thread.currentThread());
				logger.info("sumOfLengthOfValuesOfInstancesForResultField2: " + sumOfLengthOfValuesOfInstancesForResultField2 + ", thread: " + Thread.currentThread());

				logger.debug("resultField1Index: " + resultField1Index);
				logger.debug("resultField2Index: " + resultField2Index);

				resultField2Index++;

				logger.info("resultField1Index: " + resultField1Index + ", thread: " + Thread.currentThread());
				logger.info("resultField2Index: " + resultField2Index + ", thread: " + Thread.currentThread());
			}

			logger.debug("resultField1Index: " + resultField1Index);
			logger.debug("resultField2Index: " + resultField2Index);

			resultField1Index++;

			logger.debug("resultField1Index: " + resultField1Index);
			logger.debug("resultField2Index: " + resultField2Index);
		}
		final long endTime = System.nanoTime();
		logger.info("finished matching instances for all resultFields: " + endTime + ", thread: " + Thread.currentThread());
		logger.info("resultFields1.size(): " + resultFields1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size(): " + resultFields2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances1.size(): " + resultInstances1.size() + ", thread: " + Thread.currentThread());
		logger.info("resultInstances2.size(): " + resultInstances2.size() + ", thread: " + Thread.currentThread());
		logger.info("resultFields1.size() * resultInstances1.size(): " + (resultFields1.size() * resultInstances1.size()) + ", thread: " + Thread.currentThread());
		logger.info("resultFields2.size() * resultInstances2.size(): " + (resultFields2.size() * resultInstances2.size()) + ", thread: " + Thread.currentThread());

		logger.info("duration for matching constructs: " + (endTime - startTime) / 1.0e9 + ", thread: " + Thread.currentThread());
		logger.info("resultFieldsSimMatrix: " + resultFieldsSimMatrix + ", thread: " + Thread.currentThread());

		return resultFieldsSimMatrix;
	}

	private String getAggregationType(final Map<ControlParameterType, ControlParameter> controlParameters) {
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE)) {
			final String aggregationType = controlParameters.get(ControlParameterType.MATCH_AGGREGATE_AGGREGATION_TYPE).getValue();
			return aggregationType;
		}
		final String aggregationType = AggregationType.SIMAVERAGE.toString(); // set avg as default
		return aggregationType;
	}

	// code duplication to keep this as isolated as possible as it's running concurrently ... TODO not sure this is going to work though as weights are filled here and noWeights altered
	private float[] getWeights(final float[] sim, final Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in getWeights, thread: " + Thread.currentThread());
		final float[] weights = new float[sim.length];
		int noWeights = 0;
		// TODO not the best method, as if more weights required will have to add them to ControlParameterType and add more code here ... think about this
		// assume weights are in the correct order with respect to the childmatchers - issue if not
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1)) {
			weights[0] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT1).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2)) {
			weights[1] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT2).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3)) {
			weights[2] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT3).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4)) {
			weights[3] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT4).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5)) {
			weights[4] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT5).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6)) {
			weights[5] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT6).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7)) {
			weights[6] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT7).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8)) {
			weights[7] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT8).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9)) {
			weights[8] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT9).getValue()).floatValue();
			noWeights++;
		}
		if (controlParameters.containsKey(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10)) {
			weights[9] = new Float(controlParameters.get(ControlParameterType.MATCH_AGGREGATE_SIMWEIGHT_WEIGHT10).getValue()).floatValue();
			noWeights++;
		}

		if (noWeights < sim.length)
			logger.debug("some weights seem to be missing");

		return weights;
	}

	// code duplication to keep this as isolated as possible as it's running concurrently ... TODO not sure this is going to work though
	private float aggregate(final float[] sim, final Map<ControlParameterType, ControlParameter> controlParameters) {
		logger.debug("in aggregate, thread: " + Thread.currentThread());
		// [childMatchers]

		if (sim == null)
			return -1F;

		// TODO: same code as in float[][] aggregate(float[][][])

		final String aggregationType = this.getAggregationType(controlParameters);
		final float[] weights = this.getWeights(sim, controlParameters);

		// TODO not final ... could cause issues ... do I need to have a lock on these or the whole method????
		float finalSim = -1F;
		float maxSim = 0.0F;
		float minSim = 100000.0F;
		float sumSim = 0.0F;
		float weightedSim = 0.0F;

		for (int l = 0; l < sim.length; ++l) {
			final float similarity = sim[l];
			if (aggregationType.equals(AggregationType.SIMMAX.toString())) {
				if (maxSim < similarity)
					maxSim = similarity;
			} else if (aggregationType.equals(AggregationType.SIMMIN.toString())) {
				if (minSim > similarity)
					minSim = similarity;
			} else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
				sumSim += similarity;
			else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
				weightedSim += similarity * weights[l];
		}

		if (aggregationType.equals(AggregationType.SIMMAX.toString()))
			finalSim = maxSim;
		else if (aggregationType.equals(AggregationType.SIMMIN.toString()))
			finalSim = minSim;
		else if (aggregationType.equals(AggregationType.SIMAVERAGE.toString()))
			finalSim = (sumSim / sim.length);
		else if (aggregationType.equals(AggregationType.SIMWEIGHTED.toString()))
			finalSim = weightedSim;
		logger.debug("finalSim: " + finalSim);
		return finalSim;
	}

	// code duplication to keep this as isolated as possible as it's running concurrently ... TODO not sure this is going to work though as simArray is getting filled here ... use lock????
	private float[] runChildMatchers(final String string1, final String string2) {
		// [childMatchers]
		logger.debug("in runChildMatchers, thread: " + Thread.currentThread());
		logger.debug("string1: " + string1);
		logger.debug("string2: " + string2);
		if (this.childMatchers == null || this.childMatchers.size() == 0)
			logger.error("matcher should have child matchers - sort this");
		else {
			final float[] simArray = new float[this.childMatchers.size()];
			for (final MatcherService matcher : childMatchers) {
				logger.debug("matcher.getName: " + matcher.getName());
				if (matcher instanceof StringBasedMatcherServiceImpl) {
					final float sim = ((StringBasedMatcherServiceImpl) matcher).match(string1, string2);
					simArray[childMatchers.indexOf(matcher)] = sim;
					logger.debug("sim: " + sim);
				} else
					logger.error("childMatchers should only be StringBasedMatchers, matcher: " + matcher);
			}
			return simArray;
		}
		return null;
	}

	/***
	 * Inner Class
	 */
	class ListOfMatchings {

		final public List<Matching> matchings;

		public ListOfMatchings(final List<Matching> matchings) {
			logger.info("in ListOfMatchings, thread: " + Thread.currentThread());
			this.matchings = Collections.unmodifiableList(matchings);
			logger.info("done setting matchings with size: " + matchings.size() + ", thread: " + Thread.currentThread());
		}

	}//end class ListOfMatchings
}//end class ConcurrentInstanceMatcherForSuperAbstractsServiceImpl
