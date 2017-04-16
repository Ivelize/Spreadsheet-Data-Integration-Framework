/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.morphisms.schematiccorrespondence.representation;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

/**
 * @author chedeler
 *
 */
public class DerivedOneToOneMatching {

	private CanonicalModelConstruct construct1;
	private CanonicalModelConstruct construct2;
	private int numberOfMatchings;
	private double sumOfMatchingScores;

	public DerivedOneToOneMatching(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2) {
		this.construct1 = construct1;
		this.construct2 = construct2;
	}

	public void addMatchingScore(double score) {
		sumOfMatchingScores += score;
		numberOfMatchings++;
	}

	public boolean hasGreaterSumOfMatchingScore(DerivedOneToOneMatching otherMatching) {
		return this.getSumOfMatchingScores() > otherMatching.getSumOfMatchingScores();
	}

	/**
	 * @return the construct1
	 */
	public CanonicalModelConstruct getConstruct1() {
		return construct1;
	}

	/**
	 * @param construct1 the construct1 to set
	 */
	public void setConstruct1(CanonicalModelConstruct construct1) {
		this.construct1 = construct1;
	}

	/**
	 * @return the construct2
	 */
	public CanonicalModelConstruct getConstruct2() {
		return construct2;
	}

	/**
	 * @param construct2 the construct2 to set
	 */
	public void setConstruct2(CanonicalModelConstruct construct2) {
		this.construct2 = construct2;
	}

	/**
	 * @return the numberOfMatchings
	 */
	public int getNumberOfMatchings() {
		return numberOfMatchings;
	}

	/**
	 * @param numberOfMatchings the numberOfMatchings to set
	 */
	public void setNumberOfMatchings(int numberOfMatchings) {
		this.numberOfMatchings = numberOfMatchings;
	}

	/**
	 * @return the sumOfMatchingScores
	 */
	public double getSumOfMatchingScores() {
		return sumOfMatchingScores;
	}

	/**
	 * @param sumOfMatchingScores the sumOfMatchingScores to set
	 */
	public void setSumOfMatchingScores(double sumOfMatchingScores) {
		this.sumOfMatchingScores = sumOfMatchingScores;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DerivedOneToOneMatching [");
		if (construct1 != null)
			builder.append("construct1=").append(construct1).append(", ");
		if (construct2 != null)
			builder.append("construct2=").append(construct2).append(", ");
		builder.append("numberOfMatchings=").append(numberOfMatchings).append(", sumOfMatchingScores=").append(sumOfMatchingScores).append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((construct1 == null) ? 0 : construct1.hashCode());
		result = prime * result + ((construct2 == null) ? 0 : construct2.hashCode());
		result = prime * result + numberOfMatchings;
		long temp;
		temp = Double.doubleToLongBits(sumOfMatchingScores);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		//TODO move tolerance somewhere else where it can be shared
		double tolerance = 0.0001;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DerivedOneToOneMatching))
			return false;
		DerivedOneToOneMatching other = (DerivedOneToOneMatching) obj;
		if (construct1 == null) {
			if (other.construct1 != null)
				return false;
		} else if (!construct1.equals(other.construct1))
			return false;
		if (construct2 == null) {
			if (other.construct2 != null)
				return false;
		} else if (!construct2.equals(other.construct2))
			return false;
		if (numberOfMatchings != other.numberOfMatchings)
			return false;
		if (Math.abs(sumOfMatchingScores - other.sumOfMatchingScores) > tolerance)
			return false;
		return true;
	}

}
