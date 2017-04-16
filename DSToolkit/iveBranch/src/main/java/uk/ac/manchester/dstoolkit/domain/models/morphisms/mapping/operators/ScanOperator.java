package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;

/**
 * @author chedeler
 *
 */

@Entity
@DiscriminatorValue(value = "SCAN")
public class ScanOperator extends MappingOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8689684595926723261L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCAN_SUPER_ABSTRACT_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_SCAN_SUPER_ABSTRACT_ID")
	private SuperAbstract superAbstract;

	@OneToMany(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "SCAN_PREDICATE_ID")
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	private Set<Predicate> predicates = new LinkedHashSet<Predicate>();

	/**
	 * 
	 */
	public ScanOperator() {
		super();
	}

	/**
	 * @param superAbstract
	 */
	public ScanOperator(SuperAbstract superAbstract) {
		super();
		this.setSuperAbstract(superAbstract);
	}

	/**
	 * @param superAbstract
	 * @param reconcilingExpression
	 */
	public ScanOperator(SuperAbstract superAbstract, ReconcilingExpression reconcilingExpression, Set<Predicate> predicates) {
		this(superAbstract);
		this.setReconcilingExpression(reconcilingExpression);
		this.setPredicates(predicates);
	}

	public ScanOperator(SuperAbstract superAbstract, ReconcilingExpression reconcilingExpression) {
		this(superAbstract);
		this.setReconcilingExpression(reconcilingExpression);
	}

	/**
	 * @param superAbstract
	 * @param reconcilingExpressionString
	 */
	public ScanOperator(SuperAbstract superAbstract, String reconcilingExpressionString, Set<Predicate> predicates) {
		this(superAbstract);
		this.setReconcilingExpression(reconcilingExpressionString);
		this.setPredicates(predicates);
	}

	public ScanOperator(SuperAbstract superAbstract, String reconcilingExpressionString) {
		this(superAbstract);
		this.setReconcilingExpression(reconcilingExpressionString);
	}

	public ScanOperator(MappingOperator mappingOperator, String reconcilingExpressionString) {
		// TODO Auto-generated constructor stub, Lu needs this, check this
		this.setLhsInput(mappingOperator);
		this.setReconcilingExpression(reconcilingExpressionString);
	}

	public ScanOperator(MappingOperator mappingOperator, ReconcilingExpression reconcilingExpression) {
		// TODO Auto-generated constructor stub, Lu needs this, check this
		this.setLhsInput(mappingOperator);
		this.setReconcilingExpression(reconcilingExpression);
	}

	@Override
	public boolean isJustScanOperator() {
		return true;
	}

	//-----------------------superAbstract-----------------

	/**
	 * @return the superAbstract
	 */
	public SuperAbstract getSuperAbstract() {
		return superAbstract;
	}

	/**
	 * @param superAbstract the superAbstract to set
	 */
	public void setSuperAbstract(SuperAbstract superAbstract) {
		this.superAbstract = superAbstract;
	}

	//-----------------------predicates-----------------

	/**
	 * @return the predicates
	 */
	public Set<Predicate> getPredicates() {
		//return Collections.unmodifiableSet(predicates);
		return predicates;
	}

	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(Set<Predicate> predicates) {
		this.predicates = predicates;
	}

	public void addPredicate(Predicate predicate) {
		this.predicates.add(predicate);
	}

	public void removePredicate(Predicate predicate) {
		this.predicates.remove(predicate);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString("SCAN");
	}

	protected String toString(String scanOperator) {
		StringBuilder builder = new StringBuilder();
		builder.append(scanOperator);
		builder.append(": ScanOperator [");
		if (superAbstract != null)
			builder.append("superAbstract=").append(superAbstract.getName()).append(", ");
		if (reconcilingExpression != null)
			builder.append("reconcilingExpression=").append(reconcilingExpression.getExpression());
		builder.append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((superAbstract == null) ? 0 : superAbstract.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScanOperator other = (ScanOperator) obj;
		if (superAbstract == null) {
			if (other.superAbstract != null)
				return false;
		} else if (!superAbstract.equals(other.superAbstract))
			return false;
		return true;
	}

	/*
	public String toString() {
		if (this.superAbstract == null) {
			String s = "SCAN " + input1 + "(";
			if (reconcilingExpression != null) {
				s = s + reconcilingExpression.getExpression();
			}
			return s + ")";
		} else {
			String s = "SCAN " + superAbstract.getName() + "(";
			if (reconcilingExpression != null) {
				s = s + reconcilingExpression.getExpression();
			}
			return s + ")";
		}
	}
	*/

}
