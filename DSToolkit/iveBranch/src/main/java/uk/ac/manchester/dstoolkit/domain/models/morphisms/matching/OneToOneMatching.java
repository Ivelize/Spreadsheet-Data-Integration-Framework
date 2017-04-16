package uk.ac.manchester.dstoolkit.domain.models.morphisms.matching;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

/**
 * @author chedeler
 *
 */

/*
@NamedQueries({
    @NamedQuery(
        name = "OneToOneMatching-getMatchingsBetweenSuperAbstractsInTwoSchemasWithName",
        query = "select m from Matching m where m.constructs.typeOfConstruct = SUPER_ABSTRACT and m.construct.schema "
    )
})
*/

@Entity
@Table(name = "ONE_TO_ONE_MATCHINGS")
public class OneToOneMatching extends Matching {

	/**
	 * This Table is created to make matchings persistent
	 */
	private static final long serialVersionUID = -1688043869130844460L;

	@OneToOne(fetch = FetchType.EAGER)
	// , cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL }) IVE: comentei essa linha e começou a gravar no banco
	@JoinColumn(name = "MATCHING_CONSTRUCT1_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MATCHING_CONSTRUCT1_ID")
	private CanonicalModelConstruct construct1;

	@OneToOne(fetch = FetchType.EAGER)
	// , cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL }) IVE: comentei essa linha e começou a gravar no banco
	@JoinColumn(name = "MATCHING_CONSTRUCT2_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_MATCHING_CONSTRUCT2_ID")
	private CanonicalModelConstruct construct2;

	static Logger logger = Logger.getLogger(OneToOneMatching.class);

	/*
	@ManyToMany
	@JoinTable(name = "ONE_TO_ONE_MATCHING_CONSTRUCT", joinColumns = { @JoinColumn(name = "MATCHING_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONSTRUCT_ID") })
	private List<CanonicalModelConstruct> canonicalModelConstructs = new ArrayList<CanonicalModelConstruct>();
	*/

	/**
	 * 
	 */
	public OneToOneMatching() {
		super();
	}

	/**
	 * @param construct1
	 * @param construct2
	 * @param score
	 * @param matcher
	 */
	public OneToOneMatching(CanonicalModelConstruct construct1, CanonicalModelConstruct construct2, double score, String matcherName) {
		super(score);
		logger.debug("construct1: " + construct1);
		logger.debug("construct2: " + construct2);
		logger.debug("score: " + score);
		logger.debug("matcherName: " + matcherName);
		/*
		if (this.getConstructs1().size() > 0 || this.getConstructs2().size() > 0) {
			logger.error("there shouldn't be any constructs associated with matching yet - replace them with the two constructs");
			for (CanonicalModelConstruct construct : this.getConstructs1())
				construct.removeMorphism(this);
			for (CanonicalModelConstruct construct : this.getConstructs2())
				construct.removeMorphism(this);
			this.clearConstructs1();
			this.clearConstructs2();
		}
		*/
		logger.debug("before calling setConstruct1: " + construct1);
		this.setConstruct1(construct1);
		logger.debug("before calling setConstruct2: " + construct2);
		this.setConstruct2(construct2);
		construct1.addMorphism(this);
		construct2.addMorphism(this);
		logger.debug("getScore: " + this.getScore());
		this.setMatcherName(matcherName);
	}

	//-----------------------construct1-----------------

	public void setConstruct1(CanonicalModelConstruct construct1) {
		logger.debug("in setConstruct1: " + construct1);
		logger.debug("this.getConstructs1().size(): " + this.getConstructs1().size());
		if (this.getConstructs1().size() > 0) {
			logger.debug("matching already has constructs1.size(): " + this.getConstructs1().size() + " constructs, remove them");
			for (CanonicalModelConstruct construct : this.getConstructs1())
				construct.removeMorphism(this);
			this.clearConstructs1();
		}
		this.addConstruct1(construct1);
		this.construct1 = construct1;
		/*
		if (construct1 != null && this.getConstruct2() != null) {
			logger.debug("before calling construct1.addMorphism(this)");
			construct1.addMorphism(this);
		}
		*/
		logger.debug("this.getConstructs1(): " + this.getConstructs1());
	}

	/**
	 * @return constructOne
	 */
	public CanonicalModelConstruct getConstruct1() {
		//logger.debug("in getConstruct1");
		//logger.debug("this.getConstructs1(): " + this.getConstructs1());
		if (this.getConstructs1() != null && this.getConstructs1().size() == 1) {
			//logger.debug("before returning construct1");
			return this.getConstructs1().iterator().next();
		} else {
			logger.debug("getConstructs1().size(): " + getConstructs1().size());
			logger.debug("getConstructs1(): " + getConstructs1());
			logger.error("either no construct1 or more than one in oneToOne match");
		}
		return null;
	}

	/*
	public void tempSetConstructs1and2() {
		this.construct1 = this.getConstruct1();
		this.construct2 = this.getConstruct2();
	}
	*/

	public void setConstruct2(CanonicalModelConstruct construct2) {
		logger.debug("in setConstruct2: " + construct2);
		if (this.getConstructs2().size() > 0) {
			logger.debug("matching already had constructs2.size(): " + this.getConstructs2().size() + " constructs, remove them");
			for (CanonicalModelConstruct construct : this.getConstructs2())
				construct.removeMorphism(this);
			this.clearConstructs2();
		}
		this.addConstruct2(construct2);
		this.construct2 = construct2;
		/*
		if (construct2 != null && this.getConstruct1() != null)
			construct2.addMorphism(this);
		*/
		logger.debug("this.getConstructs2(): " + this.getConstructs2());
	}

	public CanonicalModelConstruct getConstruct2() {
		//logger.debug("this.getConstructs2(): " + this.getConstructs2());
		if (this.getConstructs2() != null && this.getConstructs2().size() == 1)
			return this.getConstructs2().iterator().next();
		else {
			logger.debug("getConstructs2().size(): " + getConstructs2().size());
			logger.debug("getConstructs2(): " + getConstructs2());
			logger.error("either no construct2 or more than one in oneToOne match");
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OneToOneMatching [");
		if (getChildMatches() != null)
			builder.append("getChildMatches()=").append(getChildMatches()).append(", ");
		if (getId() != null)
			builder.append("getId()=").append(getId()).append(", ");
		if (getParentMatch() != null)
			builder.append("getParentMatch()=").append(getParentMatch()).append(", ");
		builder.append("getScore()=").append(getScore()).append(", ");
		builder.append("getVersion()=").append(getVersion()).append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.getConstruct1() == null) ? 0 : this.getConstruct2().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		OneToOneMatching other = (OneToOneMatching) obj;
		if (this.getConstruct1() == null) {
			if (other.getConstruct1() != null)
				return false;
		} else if (!this.getConstruct1().equals(other.getConstruct1()))
			return false;
		if (this.getConstruct2() == null) {
			if (other.getConstruct2() != null)
				return false;
		} else if (!this.getConstruct2().equals(other.getConstruct2()))
			return false;
		return true;
	}

}
