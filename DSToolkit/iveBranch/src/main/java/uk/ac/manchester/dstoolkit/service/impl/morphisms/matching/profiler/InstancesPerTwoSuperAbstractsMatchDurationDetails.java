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
public class InstancesPerTwoSuperAbstractsMatchDurationDetails {

	private int numberOfInstancesForSuperAbstract1;
	private int numberOfInstancesForSuperAbstract2;
	private int numberOfResultFieldsForSuperAbstract1;
	private int numberOfResultFieldsForSuperAbstract2;
	private int sumOfLengthOfValuesOfInstancesForSuperAbstract1;
	private int sumOfLengthOfValuesOfInstancesForSuperAbstract2;
	private float minMatchDuration;
	private float maxMatchDuration;
	private float observedMatchDuration;
	private Map<Float, Integer> matchDurationNumberOfTimesObserved = new HashMap<Float, Integer>();

	public InstancesPerTwoSuperAbstractsMatchDurationDetails(int numberOfInstancesForSuperAbstract1, int numberOfInstancesForSuperAbstract2,
			int numberOfResultFieldsForSuperAbstract1, int numberOfResultFieldsForSuperAbstract2,
			int sumOfLengthOfValuesOfInstancesForSuperAbstract1, int sumOfLengthOfValuesOfInstancesForSuperAbstract2, float observedMatchDuration) {
		this.numberOfInstancesForSuperAbstract1 = numberOfInstancesForSuperAbstract1;
		this.numberOfInstancesForSuperAbstract2 = numberOfInstancesForSuperAbstract2;
		this.numberOfResultFieldsForSuperAbstract1 = numberOfResultFieldsForSuperAbstract1;
		this.numberOfResultFieldsForSuperAbstract2 = numberOfResultFieldsForSuperAbstract2;
		this.sumOfLengthOfValuesOfInstancesForSuperAbstract1 = sumOfLengthOfValuesOfInstancesForSuperAbstract1;
		this.sumOfLengthOfValuesOfInstancesForSuperAbstract2 = sumOfLengthOfValuesOfInstancesForSuperAbstract2;
		this.observedMatchDuration = observedMatchDuration;
		this.minMatchDuration = observedMatchDuration;
		this.maxMatchDuration = observedMatchDuration;
		this.addObservationForTime(observedMatchDuration);
	}

	/**
	 * @return the numberOfInstancesForSuperAbstract1
	 */
	public int getNumberOfInstancesForSuperAbstract1() {
		return numberOfInstancesForSuperAbstract1;
	}

	/**
	 * @param numberOfInstancesForSuperAbstract1 the numberOfInstancesForSuperAbstract1 to set
	 */
	public void setNumberOfInstancesForSuperAbstract1(int numberOfInstancesForSuperAbstract1) {
		this.numberOfInstancesForSuperAbstract1 = numberOfInstancesForSuperAbstract1;
	}

	/**
	 * @return the numberOfInstancesForSuperAbstract2
	 */
	public int getNumberOfInstancesForSuperAbstract2() {
		return numberOfInstancesForSuperAbstract2;
	}

	/**
	 * @param numberOfInstancesForSuperAbstract2 the numberOfInstancesForSuperAbstract2 to set
	 */
	public void setNumberOfInstancesForSuperAbstract2(int numberOfInstancesForSuperAbstract2) {
		this.numberOfInstancesForSuperAbstract2 = numberOfInstancesForSuperAbstract2;
	}

	/**
	 * @return the numberOfResultFieldsForSuperAbstract1
	 */
	public int getNumberOfResultFieldsForSuperAbstract1() {
		return numberOfResultFieldsForSuperAbstract1;
	}

	/**
	 * @param numberOfResultFieldsForSuperAbstract1 the numberOfResultFieldsForSuperAbstract1 to set
	 */
	public void setNumberOfResultFieldsForSuperAbstract1(int numberOfResultFieldsForSuperAbstract1) {
		this.numberOfResultFieldsForSuperAbstract1 = numberOfResultFieldsForSuperAbstract1;
	}

	/**
	 * @return the numberOfResultFieldsForSuperAbstract2
	 */
	public int getNumberOfResultFieldsForSuperAbstract2() {
		return numberOfResultFieldsForSuperAbstract2;
	}

	/**
	 * @param numberOfResultFieldsForSuperAbstract2 the numberOfResultFieldsForSuperAbstract2 to set
	 */
	public void setNumberOfResultFieldsForSuperAbstract2(int numberOfResultFieldsForSuperAbstract2) {
		this.numberOfResultFieldsForSuperAbstract2 = numberOfResultFieldsForSuperAbstract2;
	}

	/**
	 * @return the sumOfLengthOfValuesOfInstancesForSuperAbstract1
	 */
	public int getSumOfLengthOfValuesOfInstancesForSuperAbstract1() {
		return sumOfLengthOfValuesOfInstancesForSuperAbstract1;
	}

	/**
	 * @param sumOfLengthOfValuesOfInstancesForSuperAbstract1 the sumOfLengthOfValuesOfInstancesForSuperAbstract1 to set
	 */
	public void setSumOfLengthOfValuesOfInstancesForSuperAbstract1(int sumOfLengthOfValuesOfInstancesForSuperAbstract1) {
		this.sumOfLengthOfValuesOfInstancesForSuperAbstract1 = sumOfLengthOfValuesOfInstancesForSuperAbstract1;
	}

	/**
	 * @return the sumOfLengthOfValuesOfInstancesForSuperAbstract2
	 */
	public int getSumOfLengthOfValuesOfInstancesForSuperAbstract2() {
		return sumOfLengthOfValuesOfInstancesForSuperAbstract2;
	}

	/**
	 * @param sumOfLengthOfValuesOfInstancesForSuperAbstract2 the sumOfLengthOfValuesOfInstancesForSuperAbstract2 to set
	 */
	public void setSumOfLengthOfValuesOfInstancesForSuperAbstract2(int sumOfLengthOfValuesOfInstancesForSuperAbstract2) {
		this.sumOfLengthOfValuesOfInstancesForSuperAbstract2 = sumOfLengthOfValuesOfInstancesForSuperAbstract2;
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
