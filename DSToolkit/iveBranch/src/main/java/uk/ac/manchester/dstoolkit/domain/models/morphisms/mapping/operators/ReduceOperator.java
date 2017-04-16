package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;

/**
 * @author chedeler
 *
 */

@Entity
@DiscriminatorValue(value = "REDUCE")
public class ReduceOperator extends MappingOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2176156001884386614L;

	//TODO add aggregates

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "SUPER_LEXICAL_ID")
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private Map<String, SuperLexical> superLexicals = new LinkedHashMap<String, SuperLexical>();

	/**
	 * 
	 */
	public ReduceOperator() {
	}

	/**
	 * @param input
	 * @param reconcilingExpression
	 * @param superLexicals
	 */
	public ReduceOperator(MappingOperator input, ReconcilingExpression reconcilingExpression, Map<String, SuperLexical> superLexicals) {
		super(input);
		this.setReconcilingExpression(reconcilingExpression);
		this.setSuperLexicals(superLexicals);
	}

	public ReduceOperator(MappingOperator input, ReconcilingExpression reconcilingExpression) {
		super(input);
		this.setReconcilingExpression(reconcilingExpression);
	}

	/**
	 * @param input
	 * @param reconcilingExpressionString
	 */
	public ReduceOperator(MappingOperator input, String reconcilingExpressionString, Map<String, SuperLexical> superLexicals) {
		super(input);
		this.setReconcilingExpression(reconcilingExpressionString);
		this.setSuperLexicals(superLexicals);
	}

	public ReduceOperator(MappingOperator input, String reconcilingExpressionString) {
		super(input);
		this.setReconcilingExpression(reconcilingExpressionString);
	}

	//TODO add test
	@Override
	public boolean isJustScanOperator() {
		logger.debug("in isJustScanOperator");
		logger.debug("reduce - check whether its lhsInput is scan");
		if (this.getLhsInput() instanceof ScanOperator) {
			logger.debug("lhsInput is scan - check whether reduce has same superLexicals as scan");
			ScanOperator scanOperator = (ScanOperator) this.getLhsInput();
			if (this.getSuperLexicals().size() == scanOperator.getSuperAbstract().getSuperLexicals().size()) {
				logger.debug("same number of superLexicals - return true for now; TODO: check that superLexicals are same");
				return true;
				//return this.getSuperLexicals().keySet().containsAll(scanOperator.getSuperAbstract().getSuperLexicals());
			} else {
				logger.debug("different number of superLexicals - false");
				return false;
			}
		} else {
			logger.debug("lhsInput of reduce isn't scan - false");
			return false;
		}
	}

	//-----------------------superLexicals-----------------

	/**
	 * @return the superLexicals
	 */
	public Map<String, SuperLexical> getSuperLexicals() {
		//return Collections.unmodifiableMap(superLexicals);
		return superLexicals;
	}

	/**
	 * @param superLexicals the superLexicals to set
	 */
	public void setSuperLexicals(Map<String, SuperLexical> superLexicals) {
		this.superLexicals = superLexicals;
	}

	public void addSuperLexical(String superLexicalName, SuperLexical superLexical) {
		this.superLexicals.put(superLexicalName, superLexical);
	}

	public void removeSuperLexical(String superLexicalName) {
		this.superLexicals.remove(superLexicalName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString("REDUCE");
	}

	protected String toString(String reduceOperator) {
		StringBuilder builder = new StringBuilder();
		builder.append(reduceOperator);
		builder.append(": ReduceOperator [");
		if (lhsInput != null)
			builder.append("lhsInput=").append(lhsInput).append(", ");
		if (rhsInput != null)
			builder.append("rhsInput=").append(rhsInput).append(", ");
		if (reconcilingExpression != null)
			builder.append("reconcilingExpression=").append(reconcilingExpression.getExpression());
		builder.append("]");
		return builder.toString();
	}

	/*
	public String toString() {
	    String s = "REDUCE ";
	    if ( reconcilingExpression != null ) if ( reconcilingExpression != null ) { 
	        s = s + " (";
	        s = s + reconcilingExpression.getExpression();
	        s = s + ")";
	    }
	    return s + " [ " + input1.toString() + " ]";
	}
	*/
}
