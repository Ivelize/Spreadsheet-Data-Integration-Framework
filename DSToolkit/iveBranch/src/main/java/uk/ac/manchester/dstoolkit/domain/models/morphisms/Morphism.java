package uk.ac.manchester.dstoolkit.domain.models.morphisms;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Morphism extends ModelManagementConstruct {

	//TODO not sure whether we're going to need a single container handle for a set of Morphisms ...

	/**
	 * This class is an Abstract class meaning that it cannot be instantiate and therefore a persistent
	 * table for this class is not created.
	 */
	private static final long serialVersionUID = 325745236292903440L;

	static Logger logger = Logger.getLogger(Morphism.class);

	@ManyToMany
	@JoinTable(name = "MORPHISM_CONSTRUCTS1", joinColumns = { @JoinColumn(name = "MORPHISM_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONSTRUCT1_ID") })
	private Set<CanonicalModelConstruct> constructs1 = new LinkedHashSet<CanonicalModelConstruct>();

	@ManyToMany
	@JoinTable(name = "MORPHISM_CONSTRUCTS2", joinColumns = { @JoinColumn(name = "MORPHISM_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONSTRUCT2_ID") })
	private Set<CanonicalModelConstruct> constructs2 = new LinkedHashSet<CanonicalModelConstruct>();

	@Enumerated(EnumType.STRING)
	@Column(name = "CARDINALITY_TYPE")
	private CardinalityType cardinalityType;

	/**
	 * @return the constructs1
	 */
	public Set<CanonicalModelConstruct> getConstructs1() {
		//logger.debug("in getConstructs1");
		//logger.debug("construcst1: " + constructs1);
		//return Collections.unmodifiableSet(constructs1);
		return constructs1;
	}

	/**
	 * @param constructs1 the constructs1 to set
	 */
	public void setConstructs1(Collection<CanonicalModelConstruct> constructs1) {
		this.constructs1 = (Set<CanonicalModelConstruct>) constructs1;
	}

	public void addConstruct1(CanonicalModelConstruct construct1) {
		logger.debug("in addConstruct1: " + construct1);
		this.constructs1.add(construct1);
	}

	public void addAllConstructs1(Collection<CanonicalModelConstruct> constructs1) {
		this.constructs1.addAll(constructs1);
	}

	public void removeConstruct1(CanonicalModelConstruct construct1) {
		this.constructs1.remove(construct1);
	}

	public void removeAllConstructs1(Collection<CanonicalModelConstruct> constructs1) {
		this.constructs1.removeAll(constructs1);
	}

	public void clearConstructs1() {
		this.constructs1.clear();
	}

	public boolean constructs1Contains(CanonicalModelConstruct construct1) {
		return this.constructs1.contains(construct1);
	}

	public boolean constructs1ContainsAll(Collection<CanonicalModelConstruct> constructs1) {
		return this.constructs1.containsAll(constructs1);
	}

	/**
	 * @return the constructs2
	 */
	public Set<CanonicalModelConstruct> getConstructs2() {
		//logger.debug("in getConstructs2");
		//logger.debug("constructs2: " + constructs2);
		//return Collections.unmodifiableSet(constructs2);
		return constructs2;
	}

	/**
	 * @param constructs2 the constructs2 to set
	 */
	public void setConstructs2(Set<CanonicalModelConstruct> constructs2) {
		this.constructs2 = constructs2;
	}

	public void addConstruct2(CanonicalModelConstruct construct2) {
		this.constructs2.add(construct2);
	}

	public void addAllConstructs2(Collection<CanonicalModelConstruct> constructs2) {
		this.constructs2.addAll(constructs2);
	}

	public void removeConstruct2(CanonicalModelConstruct construct2) {
		this.constructs2.remove(construct2);
	}

	public void removeAllConstructs2(Collection<CanonicalModelConstruct> constructs2) {
		this.constructs2.removeAll(constructs2);
	}

	public void clearConstructs2() {
		this.constructs2.clear();
	}

	public boolean constructs2Contains(CanonicalModelConstruct construct2) {
		return this.constructs2.contains(construct2);
	}

	public boolean constructs2ContainsAll(Collection<CanonicalModelConstruct> constructs2) {
		return this.constructs2.containsAll(constructs2);
	}

	//TODO cardinalityType could be inferred
	/**
	 * @param cardinalityType the cardinalityType to set
	 */
	public void setCardinalityType(CardinalityType cardinalityType) {
		this.cardinalityType = cardinalityType;
	}

	/**
	 * @return the cardinalityType
	 */
	public CardinalityType getCardinalityType() {
		return cardinalityType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cardinalityType == null) ? 0 : cardinalityType.hashCode());
		result = prime * result + ((constructs1 == null) ? 0 : constructs1.hashCode());
		result = prime * result + ((constructs2 == null) ? 0 : constructs2.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Morphism))
			return false;
		Morphism other = (Morphism) obj;
		if (cardinalityType == null) {
			if (other.cardinalityType != null)
				return false;
		} else if (cardinalityType != other.cardinalityType)
			return false;
		if (constructs1 == null) {
			if (other.constructs1 != null)
				return false;
		} else if (!constructs1.equals(other.constructs1))
			return false;
		if (constructs2 == null) {
			if (other.constructs2 != null)
				return false;
		} else if (!constructs2.equals(other.constructs2))
			return false;
		return true;
	}

}
