/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

/**
 * @author chedeler
 *
 */
@Service(value = "matchingProfilerServiceImpl")
public class MatchingProfilerServiceImpl {

	//TODO change to AOP

	final private ConcurrentMap<Integer, ConcurrentMap<Integer, NGramMatchDurationDetails>> ngramMatchDurationDetailsForCombinationOfLengthsOFValues = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, NGramMatchDurationDetails>>();
	final private ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>>> matchDurationDetailsForCombinationOfNumberOfInstances = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>>>();
	final private ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>>> matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>>>();

	public void printMatchingProfileToFile() {
		final String fileName = "matchingProfile.log";
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(fileName);
			final BufferedWriter out = new BufferedWriter(fileWriter);
			out.write("lengthOfValue1,lengthOfValue2,minMatchDuration,maxMatchDuration,avgMatchDuration");
			out.write("\n");
			for (final Integer lengthOfValue1 : ngramMatchDurationDetailsForCombinationOfLengthsOFValues.keySet()) {
				for (final Integer lengthOfValue2 : ngramMatchDurationDetailsForCombinationOfLengthsOFValues.get(lengthOfValue1).keySet()) {
					final NGramMatchDurationDetails nGramMatchDurationDetails = ngramMatchDurationDetailsForCombinationOfLengthsOFValues.get(
							lengthOfValue1).get(lengthOfValue2);
					out.write("" + lengthOfValue1);
					out.write(",");
					out.write("" + lengthOfValue2);
					out.write(",");
					out.write("" + nGramMatchDurationDetails.getMinMatchDuration());
					out.write(",");
					out.write("" + nGramMatchDurationDetails.getMaxMatchDuration());
					out.write(",");
					float sumTime = 0f;
					int numberOfOccurences = 0;
					for (final Float time : nGramMatchDurationDetails.getMatchDurationNumberOfTimesObserved().keySet()) {
						sumTime += (time * nGramMatchDurationDetails.getMatchDurationNumberOfTimesObserved().get(time));
						numberOfOccurences += nGramMatchDurationDetails.getMatchDurationNumberOfTimesObserved().get(time);
					}
					out.write("" + (sumTime / numberOfOccurences));
					out.write("\n");
				}
			}
			out.write("\n");

			out.write("numberOfInstances1,numberOfInstances2,avgLengthOfValues1,avgLengthOfValues2,minMatchDuration,maxMatchDuration,avgMatchDuration");
			out.write("\n");
			for (final Integer numberOfInstances1 : matchDurationDetailsForCombinationOfNumberOfInstances.keySet()) {
				for (final Integer numberOfInstances2 : matchDurationDetailsForCombinationOfNumberOfInstances.get(numberOfInstances1).keySet()) {
					for (final InstancesPerTwoResultFieldsMatchDurationDetails matchDetails : matchDurationDetailsForCombinationOfNumberOfInstances
							.get(numberOfInstances1).get(numberOfInstances2)) {
						out.write("" + numberOfInstances1);
						out.write(",");
						out.write("" + numberOfInstances2);
						out.write(",");
						out.write("" + new Float(matchDetails.getSumOfLengthOfValuesOfInstancesForResultField1()) / new Float(numberOfInstances1));
						out.write(",");
						out.write("" + new Float(matchDetails.getSumOfLengthOfValuesOfInstancesForResultField2()) / new Float(numberOfInstances2));
						out.write(",");
						out.write("" + matchDetails.getMinMatchDuration());
						out.write(",");
						out.write("" + matchDetails.getMaxMatchDuration());
						out.write(",");
						float sumTime = 0f;
						int numberOfOccurences = 0;
						for (final Float time : matchDetails.getMatchDurationNumberOfTimesObserved().keySet()) {
							sumTime += (time * matchDetails.getMatchDurationNumberOfTimesObserved().get(time));
							numberOfOccurences += matchDetails.getMatchDurationNumberOfTimesObserved().get(time);
						}
						out.write("" + (sumTime / numberOfOccurences));
						out.write("\n");
					}
				}
			}
			out.write("\n");

			out.write("numberOfInstances1,numberOfInstances2,numberOfResultFields1,numberOfResultFields2,avgLengthOfValues1,avgLengthOfValues2,minMatchDuration,maxMatchDuration,avgMatchDuration");
			out.write("\n");
			for (final Integer numberOfInstances1 : matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields.keySet()) {
				for (final Integer numberOfInstances2 : matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields.get(
						numberOfInstances1).keySet()) {
					for (final Integer numberOfResultFields1 : matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields
							.get(numberOfInstances1).get(numberOfInstances2).keySet()) {
						for (final Integer numberOfResultFields2 : matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields
								.get(numberOfInstances1).get(numberOfInstances2).get(numberOfResultFields1).keySet()) {
							for (final InstancesPerTwoSuperAbstractsMatchDurationDetails matchDetails : matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields
									.get(numberOfInstances1).get(numberOfInstances2).get(numberOfResultFields1).get(numberOfResultFields2)) {
								out.write("" + numberOfInstances1);
								out.write(",");
								out.write("" + numberOfInstances2);
								out.write(",");
								out.write("" + numberOfResultFields1);
								out.write(",");
								out.write("" + numberOfResultFields2);
								out.write(",");
								out.write("" + new Float(matchDetails.getSumOfLengthOfValuesOfInstancesForSuperAbstract1())
										/ new Float((numberOfInstances1 * numberOfResultFields1)));
								out.write(",");
								out.write("" + new Float(matchDetails.getSumOfLengthOfValuesOfInstancesForSuperAbstract2())
										/ new Float(numberOfInstances2 * numberOfResultFields2));
								out.write(",");
								out.write("" + matchDetails.getMinMatchDuration());
								out.write(",");
								out.write("" + matchDetails.getMaxMatchDuration());
								out.write(",");
								float sumTime = 0f;
								int numberOfOccurences = 0;
								for (final Float time : matchDetails.getMatchDurationNumberOfTimesObserved().keySet()) {
									sumTime += (time * matchDetails.getMatchDurationNumberOfTimesObserved().get(time));
									numberOfOccurences += matchDetails.getMatchDurationNumberOfTimesObserved().get(time);
								}
								out.write("" + (sumTime / numberOfOccurences));
								out.write("\n");
							}
						}
					}
				}
			}
			out.write("\n");
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @return the ngramMatchDurationDetailsForCombinationOfLengthsOFValues
	 */
	public ConcurrentMap<Integer, ConcurrentMap<Integer, NGramMatchDurationDetails>> getNgramMatchDurationDetailsForCombinationOfLengthsOFValues() {
		return ngramMatchDurationDetailsForCombinationOfLengthsOFValues;
	}

	public void addNGramMatchDurationDetails(final NGramMatchDurationDetails ngramMatchDurationDetails) {
		final float observedMatchDuration = ngramMatchDurationDetails.getObservedMatchDuration();
		if (ngramMatchDurationDetailsForCombinationOfLengthsOFValues.containsKey(ngramMatchDurationDetails.getLengthOfValue1())) {
			final ConcurrentMap<Integer, NGramMatchDurationDetails> ngramMatchDurationDetailsForLengthsOfValue2 = ngramMatchDurationDetailsForCombinationOfLengthsOFValues
					.get(ngramMatchDurationDetails.getLengthOfValue1());
			if (ngramMatchDurationDetailsForLengthsOfValue2.containsKey(ngramMatchDurationDetails.getLengthOfValue2())) {
				final NGramMatchDurationDetails existingNGramMatchDurationDetails = ngramMatchDurationDetailsForLengthsOfValue2
						.get(ngramMatchDurationDetails.getLengthOfValue2());
				if (observedMatchDuration < existingNGramMatchDurationDetails.getMinMatchDuration())
					existingNGramMatchDurationDetails.setMinMatchDuration(observedMatchDuration);
				if (observedMatchDuration > existingNGramMatchDurationDetails.getMaxMatchDuration())
					existingNGramMatchDurationDetails.setMaxMatchDuration(observedMatchDuration);
				existingNGramMatchDurationDetails.addObservationForTime(observedMatchDuration);
			} else {
				ngramMatchDurationDetailsForLengthsOfValue2.put(ngramMatchDurationDetails.getLengthOfValue2(), ngramMatchDurationDetails);
			}
		} else {
			final ConcurrentMap<Integer, NGramMatchDurationDetails> ngramMatchDurationDetailsForLengthsOfValue2 = new ConcurrentHashMap<Integer, NGramMatchDurationDetails>();
			ngramMatchDurationDetailsForLengthsOfValue2.put(ngramMatchDurationDetails.getLengthOfValue2(), ngramMatchDurationDetails);
			ngramMatchDurationDetailsForCombinationOfLengthsOFValues.put(ngramMatchDurationDetails.getLengthOfValue1(),
					ngramMatchDurationDetailsForLengthsOfValue2);
		}
	}

	/**
	 * @param ngramMatchDurationDetailsForCombinationOfLengthsOFValues
	 *            the ngramMatchDurationDetailsForCombinationOfLengthsOFValues to set
	 */
	/*
	 * public void setNgramMatchDurationDetailsForCombinationOfLengthsOFValues( final ConcurrentMap<Integer, ConcurrentMap<Integer, NGramMatchDurationDetails>>
	 * ngramMatchDurationDetailsForCombinationOfLengthsOFValues) { this.ngramMatchDurationDetailsForCombinationOfLengthsOFValues = ngramMatchDurationDetailsForCombinationOfLengthsOFValues; }
	 */

	/**
	 * @return the matchDurationDetailsForCombinationOfNumberOfInstances
	 */
	public ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>>> getMatchDurationDetailsForCombinationOfNumberOfInstances() {
		return matchDurationDetailsForCombinationOfNumberOfInstances;
	}

	public void addInstancesPerTwoResultFieldsMatchDurationDetails(
			final InstancesPerTwoResultFieldsMatchDurationDetails instancesPerTwoResultFieldsMatchDurationDetails) {
		if (matchDurationDetailsForCombinationOfNumberOfInstances.containsKey(instancesPerTwoResultFieldsMatchDurationDetails
				.getNumberOfInstancesForResultField1())) {
			final ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>> instancePerTwoResultFieldsForNumbersOfInstances2 = matchDurationDetailsForCombinationOfNumberOfInstances
					.get(instancesPerTwoResultFieldsMatchDurationDetails.getNumberOfInstancesForResultField1());
			if (instancePerTwoResultFieldsForNumbersOfInstances2.containsKey(instancesPerTwoResultFieldsMatchDurationDetails
					.getNumberOfInstancesForResultField2())) {
				final ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails> listOfMatchDetails = instancePerTwoResultFieldsForNumbersOfInstances2
						.get(instancesPerTwoResultFieldsMatchDurationDetails.getNumberOfInstancesForResultField2());
				listOfMatchDetails.add(instancesPerTwoResultFieldsMatchDurationDetails);
			} else {
				final ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails> listOfMatchDetails = new ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>();
				listOfMatchDetails.add(instancesPerTwoResultFieldsMatchDurationDetails);
				instancePerTwoResultFieldsForNumbersOfInstances2.put(
						instancesPerTwoResultFieldsMatchDurationDetails.getNumberOfInstancesForResultField2(), listOfMatchDetails);
			}
		} else {
			final ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>> instancePerTwoResultFieldsForNumbersOfInstances2 = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>>();
			final ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails> listOfMatchDetails = new ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>();
			listOfMatchDetails.add(instancesPerTwoResultFieldsMatchDurationDetails);
			instancePerTwoResultFieldsForNumbersOfInstances2.put(
					instancesPerTwoResultFieldsMatchDurationDetails.getNumberOfInstancesForResultField2(), listOfMatchDetails);
			matchDurationDetailsForCombinationOfNumberOfInstances.put(
					instancesPerTwoResultFieldsMatchDurationDetails.getNumberOfInstancesForResultField1(),
					instancePerTwoResultFieldsForNumbersOfInstances2);
		}
	}

	/**
	 * @param matchDurationDetailsForCombinationOfNumberOfInstances
	 *            the matchDurationDetailsForCombinationOfNumberOfInstances to set
	 */
	/*
	 * public void setMatchDurationDetailsForCombinationOfNumberOfInstances( ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoResultFieldsMatchDurationDetails>>>
	 * matchDurationDetailsForCombinationOfNumberOfInstances) { this.matchDurationDetailsForCombinationOfNumberOfInstances = matchDurationDetailsForCombinationOfNumberOfInstances; }
	 */

	/**
	 * @return the matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields
	 */
	public ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>>> getMatchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields() {
		return matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields;
	}

	public void addInstancesPerTwoSuperAbstractsMatchDurationDetails(
			final InstancesPerTwoSuperAbstractsMatchDurationDetails instancesPerTwoSuperAbstractsMatchDurationDetails) {
		// ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>>
		// matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields;
		// ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>> matchDetailsForCombinationOfNumberOfResultFields;
		// ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>> matchDetailsForNumberOfResultFieldsInSuperAbstract2;
		// ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails> listOfMatchDetails;
		if (matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields
				.containsKey(instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract1())) {
			final ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>> matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields = matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields
					.get(instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract1());
			if (matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields
					.containsKey(instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract2())) {
				final ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>> matchDetailsForCombinationOfNumberOfResultFields = matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields
						.get(instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract2());
				if (matchDetailsForCombinationOfNumberOfResultFields.containsKey(instancesPerTwoSuperAbstractsMatchDurationDetails
						.getNumberOfResultFieldsForSuperAbstract1())) {
					final ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>> matchDetailsForNumberOfResultFieldsInSuperAbstract2 = matchDetailsForCombinationOfNumberOfResultFields
							.get(instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract1());
					if (matchDetailsForNumberOfResultFieldsInSuperAbstract2.containsKey(instancesPerTwoSuperAbstractsMatchDurationDetails
							.getNumberOfResultFieldsForSuperAbstract2())) {
						final ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails> listOfMatchDetails = matchDetailsForNumberOfResultFieldsInSuperAbstract2
								.get(instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract2());
						listOfMatchDetails.add(instancesPerTwoSuperAbstractsMatchDurationDetails);
					} else {
						final ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails> listOfMatchDetails = new ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>();
						listOfMatchDetails.add(instancesPerTwoSuperAbstractsMatchDurationDetails);
						matchDetailsForNumberOfResultFieldsInSuperAbstract2.put(
								instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract2(), listOfMatchDetails);
					}
				} else {
					final ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>> matchDetailsForNumberOfResultFieldsInSuperAbstract2 = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>();
					final ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails> listOfMatchDetails = new ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>();
					listOfMatchDetails.add(instancesPerTwoSuperAbstractsMatchDurationDetails);
					matchDetailsForNumberOfResultFieldsInSuperAbstract2.put(
							instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract2(), listOfMatchDetails);
					matchDetailsForCombinationOfNumberOfResultFields.put(
							instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract1(),
							matchDetailsForNumberOfResultFieldsInSuperAbstract2);
				}
			} else {
				final ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>> matchDetailsForCombinationOfNumberOfResultFields = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>();
				final ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>> matchDetailsForNumberOfResultFieldsInSuperAbstract2 = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>();
				final ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails> listOfMatchDetails = new ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>();
				listOfMatchDetails.add(instancesPerTwoSuperAbstractsMatchDurationDetails);
				matchDetailsForNumberOfResultFieldsInSuperAbstract2.put(
						instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract2(), listOfMatchDetails);
				matchDetailsForCombinationOfNumberOfResultFields.put(
						instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract1(),
						matchDetailsForNumberOfResultFieldsInSuperAbstract2);
				matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields.put(
						instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract2(),
						matchDetailsForCombinationOfNumberOfResultFields);
			}

		} else {
			final ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>> matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>>();
			final ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>> matchDetailsForCombinationOfNumberOfResultFields = new ConcurrentHashMap<Integer, ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>();
			final ConcurrentMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>> matchDetailsForNumberOfResultFieldsInSuperAbstract2 = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>();
			final ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails> listOfMatchDetails = new ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>();
			listOfMatchDetails.add(instancesPerTwoSuperAbstractsMatchDurationDetails);
			matchDetailsForNumberOfResultFieldsInSuperAbstract2.put(
					instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract2(), listOfMatchDetails);
			matchDetailsForCombinationOfNumberOfResultFields.put(
					instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfResultFieldsForSuperAbstract1(),
					matchDetailsForNumberOfResultFieldsInSuperAbstract2);
			matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields.put(
					instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract2(),
					matchDetailsForCombinationOfNumberOfResultFields);
			matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields.put(
					instancesPerTwoSuperAbstractsMatchDurationDetails.getNumberOfInstancesForSuperAbstract1(),
					matchDetailsForNumbersOfInstancesOfSuperAbstract2AndCombinationOfNumberOfResultFields);
		}
	}

	/**
	 * @param matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfAttributes
	 *            the matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields to set
	 */
	/*
	 * public void setMatchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields( ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer,
	 * ConcurrentLinkedQueue<InstancesPerTwoSuperAbstractsMatchDurationDetails>>>>> matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields) {
	 * this.matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields = matchDurationDetailsForCombinationOfNumberOfInstancesAndNumberOfResultFields; }
	 */

}
