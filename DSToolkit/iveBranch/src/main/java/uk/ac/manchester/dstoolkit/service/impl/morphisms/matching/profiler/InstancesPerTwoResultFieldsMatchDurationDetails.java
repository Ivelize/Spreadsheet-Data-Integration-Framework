/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.profiler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chedeler
 *
 */
public class InstancesPerTwoResultFieldsMatchDurationDetails {

	private int numberOfInstancesForResultField1;
	private int numberOfInstancesForResultField2;
	private int sumOfLengthOfValuesOfInstancesForResultField1;
	private int sumOfLengthOfValuesOfInstancesForResultField2;
	private float minMatchDuration;
	private float maxMatchDuration;
	private float observedMatchDuration;
	private Map<Float, Integer> matchDurationNumberOfTimesObserved = new HashMap<Float, Integer>();

	public InstancesPerTwoResultFieldsMatchDurationDetails(int numberOfInstancesForResultField1, int numberOfInstancesForResultField2,
			int sumOfLengthOfValuesOfInstancesForResultField1, int sumOfLengthOfValuesOfInstancesForResultField2, float observedMatchDuration) {
		this.numberOfInstancesForResultField1 = numberOfInstancesForResultField1;
		this.numberOfInstancesForResultField2 = numberOfInstancesForResultField2;
		this.sumOfLengthOfValuesOfInstancesForResultField1 = sumOfLengthOfValuesOfInstancesForResultField1;
		this.sumOfLengthOfValuesOfInstancesForResultField2 = sumOfLengthOfValuesOfInstancesForResultField2;
		this.observedMatchDuration = observedMatchDuration;
		this.minMatchDuration = observedMatchDuration;
		this.maxMatchDuration = observedMatchDuration;
		this.addObservationForTime(observedMatchDuration);
	}

	/**
	 * @return the numberOfInstancesForResultField1
	 */
	public int getNumberOfInstancesForResultField1() {
		return numberOfInstancesForResultField1;
	}

	/**
	 * @param numberOfInstancesForResultField1 the numberOfInstancesForResultField1 to set
	 */
	public void setNumberOfInstancesForResultField1(int numberOfInstancesForResultField1) {
		this.numberOfInstancesForResultField1 = numberOfInstancesForResultField1;
	}

	/**
	 * @return the numberOfInstancesForResultField2
	 */
	public int getNumberOfInstancesForResultField2() {
		return numberOfInstancesForResultField2;
	}

	/**
	 * @param numberOfInstancesForResultField2 the numberOfInstancesForResultField2 to set
	 */
	public void setNumberOfInstancesForResultField2(int numberOfInstancesForResultField2) {
		this.numberOfInstancesForResultField2 = numberOfInstancesForResultField2;
	}

	/**
	 * @return the sumOfLengthOfValuesOfInstancesForResultField1
	 */
	public int getSumOfLengthOfValuesOfInstancesForResultField1() {
		return sumOfLengthOfValuesOfInstancesForResultField1;
	}

	/**
	 * @param sumOfLengthOfValuesOfInstancesForResultField1 the sumOfLengthOfValuesOfInstancesForResultField1 to set
	 */
	public void setSumOfLengthOfValuesOfInstancesForResultField1(int sumOfLengthOfValuesOfInstancesForResultField1) {
		this.sumOfLengthOfValuesOfInstancesForResultField1 = sumOfLengthOfValuesOfInstancesForResultField1;
	}

	/**
	 * @return the sumOfLengthOfValuesOfInstancesForResultField2
	 */
	public int getSumOfLengthOfValuesOfInstancesForResultField2() {
		return sumOfLengthOfValuesOfInstancesForResultField2;
	}

	/**
	 * @param sumOfLengthOfValuesOfInstancesForResultField2 the sumOfLengthOfValuesOfInstancesForResultField2 to set
	 */
	public void setSumOfLengthOfValuesOfInstancesForResultField2(int sumOfLengthOfValuesOfInstancesForResultField2) {
		this.sumOfLengthOfValuesOfInstancesForResultField2 = sumOfLengthOfValuesOfInstancesForResultField2;
	}

	/**
	 * @return the minMatchDuration
	 */
	public float getMinMatchDuration() {
		return minMatchDuration;
	}

	/**
	 * @param minMatchDuration the minMatchDuration to set
	 */
	public void setMinMatchDuration(float minMatchDuration) {
		this.minMatchDuration = minMatchDuration;
	}

	/**
	 * @return the maxMatchDuration
	 */
	public float getMaxMatchDuration() {
		return maxMatchDuration;
	}

	/**
	 * @param maxMatchDuration the maxMatchDuration to set
	 */
	public void setMaxMatchDuration(float maxMatchDuration) {
		this.maxMatchDuration = maxMatchDuration;
	}

	/**
	 * @return the matchDurationNumberOfTimesObserved
	 */
	public Map<Float, Integer> getMatchDurationNumberOfTimesObserved() {
		return matchDurationNumberOfTimesObserved;
	}

	public void addObservationForTime(Float time) {
		if (matchDurationNumberOfTimesObserved.containsKey(time)) {
			int numberOfObservations = matchDurationNumberOfTimesObserved.get(time);
			matchDurationNumberOfTimesObserved.put(time, numberOfObservations++);
		} else {
			matchDurationNumberOfTimesObserved.put(time, 1);
		}
	}

	/**
	 * @param matchDurationNumberOfTimesObserved the matchDurationNumberOfTimesObserved to set
	 */
	public void setMatchDurationNumberOfTimesObserved(Map<Float, Integer> matchDurationNumberOfTimesObserved) {
		this.matchDurationNumberOfTimesObserved = matchDurationNumberOfTimesObserved;
	}

	/**
	 * @return the observedMatchDuration
	 */
	public float getObservedMatchDuration() {
		return observedMatchDuration;
	}

	/**
	 * @param observedMatchDuration the observedMatchDuration to set
	 */
	public void setObservedMatchDuration(float observedMatchDuration) {
		this.observedMatchDuration = observedMatchDuration;
	}

}
