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
public class NGramMatchDurationDetails {

	private int lengthOfValue1;
	private int lengthOfValue2;
	private float minMatchDuration;
	private float maxMatchDuration;
	private float observedMatchDuration;
	private Map<Float, Integer> matchDurationNumberOfTimesObserved = new HashMap<Float, Integer>();

	public NGramMatchDurationDetails(int lengthOfValue1, int lengthOfValue2, float observedMatchDuration) {
		this.lengthOfValue1 = lengthOfValue1;
		this.lengthOfValue2 = lengthOfValue2;
		this.observedMatchDuration = observedMatchDuration;
		this.maxMatchDuration = observedMatchDuration;
		this.minMatchDuration = observedMatchDuration;
		this.addObservationForTime(observedMatchDuration);
	}

	/**
	 * @return the lengthOfValue1
	 */
	public int getLengthOfValue1() {
		return lengthOfValue1;
	}

	/**
	 * @param lengthOfValue1 the lengthOfValue1 to set
	 */
	public void setLengthOfValue1(int lengthOfValue1) {
		this.lengthOfValue1 = lengthOfValue1;
	}

	/**
	 * @return the lengthOfValue2
	 */
	public int getLengthOfValue2() {
		return lengthOfValue2;
	}

	/**
	 * @param lengthOfValue2 the lengthOfValue2 to set
	 */
	public void setLengthOfValue2(int lengthOfValue2) {
		this.lengthOfValue2 = lengthOfValue2;
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
