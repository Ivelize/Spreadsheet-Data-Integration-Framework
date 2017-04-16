package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;

/**
 * @author chedeler
 *
 */

@Entity
@DiscriminatorValue(value = "JOIN")
public class JoinOperator extends MappingOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5973107841526813601L;

	@OneToMany(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "JOIN_PREDICATE_ID")
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	private Set<Predicate> predicates = new LinkedHashSet<Predicate>();

	/**
	 * 
	 */
	public JoinOperator() {
	}

	/**
	 * @param leftInput
	 * @param rightInput
	 */
	public JoinOperator(MappingOperator leftInput, MappingOperator rightInput) {
		super(leftInput, rightInput);
	}

	/**
	 * @param leftInput
	 * @param rightInput
	 * @param reconcilingExpression
	 */
	public JoinOperator(MappingOperator leftInput, MappingOperator rightInput, ReconcilingExpression reconcilingExpression, Set<Predicate> predicates) {
		this(leftInput, rightInput);
		this.setReconcilingExpression(reconcilingExpression);
		this.setPredicates(predicates);
	}

	public JoinOperator(MappingOperator leftInput, MappingOperator rightInput, ReconcilingExpression reconcilingExpression) {
		this(leftInput, rightInput);
		this.setReconcilingExpression(reconcilingExpression);
	}

	/**
	 * @param leftInput
	 * @param rightInput
	 * @param reconcilingExpressionString
	 */
	public JoinOperator(MappingOperator leftInput, MappingOperator rightInput, String reconcilingExpressionString, Set<Predicate> predicates) {
		this(leftInput, rightInput);
		this.setReconcilingExpression(reconcilingExpressionString);
		this.setPredicates(predicates);
	}

	public JoinOperator(MappingOperator leftInput, MappingOperator rightInput, String reconcilingExpressionString) {
		this(leftInput, rightInput);
		this.setReconcilingExpression(reconcilingExpressionString);
	}

	@Override
	public boolean isJustScanOperator() {
		return false;
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
		return toString("JOIN");
	}

	protected String toString(String joinOperator) {
		StringBuilder builder = new StringBuilder();
		builder.append(joinOperator);
		builder.append(": JoinOperator [");
		if (reconcilingExpression != null)
			builder.append("reconcilingExpression=").append(reconcilingExpression.getExpression());
		builder.append("]");
		return builder.toString();
	}

	/*
	protected String toString(String joinOperator) {
	     if ( reconcilingExpression != null ) { 
	        joinOperator = joinOperator + " (";
	        joinOperator = joinOperator + reconcilingExpression.getExpression();
	        joinOperator = joinOperator + ")";
	    }
	    return joinOperator + " [ " + input1.toString() + " ] [ " +
	           input2.toString() + " ]";
	}
	*/

}
