package uk.ac.manchester.dstoolkit.domain.models.morphisms.matching;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.Morphism;

/**
 * @author chedeler
 *
 */

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Matching extends Morphism {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7925240932934501931L;

	/*
	@ManyToOne
	@JoinColumn(name = "MATCHING_DATASPACE_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MATCHING_DATASPACE_ID")
	private Dataspace dataspace;
	*/

	@Column(name = "MATCHING_SCORE")
	private double score;

	@ManyToOne
	@JoinColumn(name = "PARENT_MATCH_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_MATCHING_PARENT_MATCH_ID")
	private Matching parentMatch;

	@OneToMany(mappedBy = "parentMatch", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//, fetch = FetchType.EAGER
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private Set<Matching> childMatches = new LinkedHashSet<Matching>();

	/*
	@ManyToOne
	@JoinColumn(name = "MATCHER_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MATCHING_MATCHER_ID")
	private Matcher matcher;
	*/

	@Column(name = "MATCHER_NAME")
	private String matcherName;
	
	//Used for the benchmark
	@Column(name = "CLASS_LABEL")
	private String classLabel;

	@Column(name = "ABS_ERROR")
	private double absError;
	
	@Column(name = "SQUARED_ERROR")
	private double squaredError;
	
	/**
	 * Constructor
	 */
	public Matching() {
		super();
	}

	/**
	 * @param score
	 * @param typeOfMatching
	 * @param matcher
	 */
	public Matching(double score) { //, Matcher matcher) {
		super();
		this.setScore(score);
		//this.setMatcher(matcher);
	}

	//-----------------------score-----------------

	/**
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	//-----------------------dataspace-----------------

	/**
	 * @return the dataspace
	 */
	/*
	public Dataspace getDataspace() {
		return dataspace;
	}
	*/

	/**
	 * @param dataspace the dataspace to set
	 */
	/*
	public void setDataspace(Dataspace dataspace) {
		if (this.dataspace != null) {
			this.dataspace.internalRemoveMatching(this);
		}
		this.dataspace = dataspace;
		if (dataspace != null) {
			dataspace.internalAddMatching(this);
		}
	}

	public void internalSetDataspace(Dataspace dataspace) {
		this.dataspace = dataspace;
	}
	*/

	//-----------------------typeOfMatching-----------------

	/**
	 * @return the typeOfMatching
	 */
	/*
	public MatchingType getTypeOfMatching() {
		return typeOfMatching;
	}
	*/

	/**
	 * @param typeOfMatching the typeOfMatching to set
	 */
	/*
	public void setTypeOfMatching(MatchingType typeOfMatching) {
		this.typeOfMatching = typeOfMatching;
	}
	*/

	//-----------------------parentMatch-----------------

	/**
	 * @return the parentMatch
	 */
	public Matching getParentMatch() {
		return parentMatch;
	}

	/**
	 * @param parentMatch the parentMatch to set
	 */
	public void setParentMatch(Matching parentMatch) {
		if (this.parentMatch != null) {
			this.parentMatch.internalRemoveChildMatch(this);
		}
		this.parentMatch = parentMatch;
		if (parentMatch != null) {
			parentMatch.internalAddChildMatch(this);
		}
	}

	public void internalSetParentMatch(Matching parentMatch) {
		this.parentMatch = parentMatch;
	}

	//-----------------------childMatches-----------------

	/**
	 * @return the childMatches
	 */
	public Set<Matching> getChildMatches() {
		//return Collections.unmodifiableSet(childMatches);
		return childMatches;
	}

	/**
	 * @param childMatches the childMatches to set
	 */
	public void setChildMatches(Set<Matching> childMatches) {
		this.childMatches = childMatches;
		for (Matching childMatch : childMatches) {
			childMatch.internalSetParentMatch(this);
		}
	}

	public void addChildMatch(Matching childMatch) {
		this.childMatches.add(childMatch);
		childMatch.internalSetParentMatch(this);
	}

	public void internalAddChildMatch(Matching childMatch) {
		this.childMatches.add(childMatch);
	}

	public void removeChildMatch(Matching childMatch) {
		this.childMatches.remove(childMatch);
		childMatch.internalSetParentMatch(this);
	}

	public void internalRemoveChildMatch(Matching childMatch) {
		this.childMatches.remove(childMatch);
	}

	
	
	//-----------------------matcher-----------------

	
	/**
	 * @return the matcher
	 */
	/*
	public Matcher getMatcher() {
		return matcher;
	}
	*/

	/**
	 * @param matcher the matcher to set
	 */
	/*
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Matching [");
		if (childMatches != null)
			builder.append("childMatches=").append(childMatches).append(", ");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		//if (matcher != null)
		//	builder.append("matcher=").append(matcher).append(", ");
		if (parentMatch != null)
			builder.append("parentMatch=").append(parentMatch).append(", ");
		builder.append("score=").append(score).append(", ");
		builder.append("version=").append(version).append("]");
		return builder.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((childMatches == null) ? 0 : childMatches.hashCode());
		//result = prime * result + ((matcher == null) ? 0 : matcher.hashCode());
		result = prime * result + ((parentMatch == null) ? 0 : parentMatch.hashCode());
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		double tolerance = 0.0001;
		if (!super.equals(obj))
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Matching other = (Matching) obj;
		if (childMatches == null) {
			if (other.childMatches != null)
				return false;
		} else if (!childMatches.equals(other.childMatches))
			return false;
		//if (matcher == null) {
		//	if (other.matcher != null)
		//		return false;
		//} else if (!matcher.equals(other.matcher))
		//	return false;
		if (parentMatch == null) {
			if (other.parentMatch != null)
				return false;
		} else if (!parentMatch.equals(other.parentMatch))
			return false;
		if (matcherName == null) {
			if (other.matcherName != null)
				return false;
		} else if (!matcherName.equals(other.matcherName))
			return false;
		if (Math.abs(score - other.score) > tolerance)
			return false;
		return true;
	}

	/**
	 * @param matcherName the matcherName to set
	 */
	public void setMatcherName(String matcherName) {
		this.matcherName = matcherName;
	}

	/**
	 * @return the matcherName
	 */
	public String getMatcherName() {
		return matcherName;
	}

	/**
	 * @param classLabel of this match object 
	 */
	public String getClassLabel() {
		return classLabel;
	}

	public void setClassLabel(String classLabel) {
		this.classLabel = classLabel;
	}

	/**
	 * Individual errors used during benchmark
	 * @return
	 */
	public double getAbsError() {
		return absError;
	}

	public void setAbsError(double absError) {
		this.absError = absError;
	}

	public double getSquaredError() {
		return squaredError;
	}

	public void setSquaredError(double squaredError) {
		this.squaredError = squaredError;
	}	
}//end Class
