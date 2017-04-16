package uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.antlr.runtime.tree.CommonTree;

import uk.ac.manchester.dstoolkit.domain.DomainEntity;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;

/**
 * @author chedeler
 *
 */

@Entity
@Table(name = "RECONCILING_EXPRESSION")
public class ReconcilingExpression extends DomainEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2109247293618935021L;

	//TODO add here what to do when another reconcilingExpression needs to be added/set

	@Enumerated(EnumType.STRING)
	@Column(name = "RECONCILING_EXPRESSION_TYPE")
	private ReconcilingExpressionType typeOfReconcilingExpression;

	@Column(name = "RECONCILING_EXPRESSION_EXPRESSION", length = 10000)
	private String expression;

	//TODO could this be applied to multiple canonical model constructs? m:n correspondences? joins
	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "RECONCILING_EXPRESSION_APPLIED_TO_CANONICAL_MODEL_CONSTRUCT", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.ForeignKey(name = "FK_RECONCILING_EXPRESSION_APPLIED_TO_CANONICAL_MODEL_CONSTRUCT_ID")
	private CanonicalModelConstruct appliedToCanonicalModelConstruct;

	// Begin added by Lu, hibernate annotation added by Conny

	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "RECONCILING_EXPRESSION_SELECTION_TARGET_SUPER_ABSTRACT", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.ForeignKey(name = "FK_RECONCILING_EXPRESSION_TARGET_SUPER_ABSTRACT_ID")
	private SuperAbstract selectionTargetSuperAbstract;

	//TODO check these two, they look weird, might just need to be given a different name or added to a list of constructs that the expression is applied to
	//they may actually be ok
	@OneToOne(fetch = FetchType.EAGER)
	//,cascade = { CascadeType.MERGE })
	@JoinColumn(name = "RECONCILING_EXPRESSION_JOIN_PRED_1", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.ForeignKey(name = "FK_RECONCILING_EXPRESSION_JOIN_PRED1_ID")
	private SuperAbstract joinPred1;

	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "RECONCILING_EXPRESSION_JOIN_PRED_2", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@org.hibernate.annotations.ForeignKey(name = "FK_RECONCILING_EXPRESSION_JOIN_PRED2_ID")
	private SuperAbstract joinPred2;
	// End added by Lu, hibernate annotation added by Conny

	//@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@JoinTable(name = "RECONCILING_EXPRESSION_OPERATOR", joinColumns = { @JoinColumn(name = "RECONCILING_EXPRESSION_ID") }, inverseJoinColumns = { @JoinColumn(name = "OPERATOR_ID") })
	//@JoinColumn(name = "RECONCILING_EXPRESSION_MAPPING_OPERATOR_ID", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.ForeignKey(name = "FK_RECONCILING_EXPRESSION_MAPPING_OPERATOR_ID")
	//private MappingOperator mappingOperator;

	//@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//@JoinColumn(name = "RECONCILING_EXPRESSION_SCHEMATIC_CORRESPONDENCE_ID", nullable = true)
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	//@org.hibernate.annotations.ForeignKey(name = "FK_RECONCILING_EXPRESSION_SCHEMATIC_CORRESPONDENCE_ID")
	//private SchematicCorrespondence schematicCorrespondence;

	@Transient
	private CommonTree expressionTree;

	/**
	 * 
	 */
	public ReconcilingExpression() {
		super();
	}

	/**
	 * @param expression
	 */
	public ReconcilingExpression(String expression) {
		this.setExpression(expression);
	}

	/**
	 * @param expression
	 * @param schematicCorrespondence
	 */
	/*
	public ReconcilingExpression(String expression, SchematicCorrespondence schematicCorrespondence) {
		this.setExpression(expression);
		//this.setSchematicCorrespondence(schematicCorrespondence);
	}
	*/

	// Begin added by Lu

	/**
	 * @param expression
	 * @param selectionTargetSuperAbstract
	 */
	public ReconcilingExpression(String expression, SuperAbstract selectionTargetSuperAbstract) {
		this.setExpression(expression);
		this.setSelectionTargetSuperAbstract(selectionTargetSuperAbstract);
	}

	/**
	 * @param expression
	 * @param joinPred1
	 * @param joinPred2
	 */
	public ReconcilingExpression(String expression, SuperAbstract joinPred1, SuperAbstract joinPred2) {
		this.setExpression(expression);
		this.setJoinPred1(joinPred1);
		this.setJoinPred2(joinPred2);
	}

	// End added by Lu

	//-----------------------typeOfReconcilingExpression-----------------

	/**
	 * @return the typeOfReconcilingExpression
	 */
	public ReconcilingExpressionType getTypeOfReconcilingExpression() {
		return typeOfReconcilingExpression;
	}

	/**
	 * @param typeOfReconcilingExpression the typeOfReconcilingExpression to set
	 */
	public void setTypeOfReconcilingExpression(ReconcilingExpressionType typeOfReconcilingExpression) {
		this.typeOfReconcilingExpression = typeOfReconcilingExpression;
	}

	//-----------------------expression-----------------

	/**
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	//-----------------------appliedToCanonicalModelConstruct-----------------

	/**
	 * @return the appliedToCanonicalModelConstruct
	 */
	public CanonicalModelConstruct getAppliedToCanonicalModelConstruct() {
		return appliedToCanonicalModelConstruct;
	}

	/**
	 * @param appliedToCanonicalModelConstruct the appliedToCanonicalModelConstruct to set
	 */
	public void setAppliedToCanonicalModelConstruct(CanonicalModelConstruct appliedToCanonicalModelConstruct) {
		this.appliedToCanonicalModelConstruct = appliedToCanonicalModelConstruct;
	}

	//-----------------------selectionTargetSuperAbstract-----------------

	/**
	 * @return the selectionTargetSuperAbstract
	 */
	public SuperAbstract getSelectionTargetSuperAbstract() {
		return selectionTargetSuperAbstract;
	}

	/**
	 * @param selectionTargetSuperAbstract the selectionTargetSuperAbstract to set
	 */
	public void setSelectionTargetSuperAbstract(SuperAbstract selectionTargetSuperAbstract) {
		this.selectionTargetSuperAbstract = selectionTargetSuperAbstract;
	}

	//-----------------------joinPred1-----------------

	/**
	 * @return the joinPred1
	 */
	public SuperAbstract getJoinPred1() {
		return joinPred1;
	}

	/**
	 * @param joinPred1 the joinPred1 to set
	 */
	public void setJoinPred1(SuperAbstract joinPred1) {
		this.joinPred1 = joinPred1;
	}

	//-----------------------joinPred2-----------------

	/**
	 * @return the joinPred2
	 */
	public SuperAbstract getJoinPred2() {
		return joinPred2;
	}

	/**
	 * @param joinPred2 the joinPred2 to set
	 */
	public void setJoinPred2(SuperAbstract joinPred2) {
		this.joinPred2 = joinPred2;
	}

	//-----------------------expressionTree-----------------

	/**
	 * @return the expressionTree
	 */
	public CommonTree getExpressionTree() {
		return expressionTree;
	}

	/**
	 * @param expressionTree the expressionTree to set
	 */
	public void setExpressionTree(CommonTree expressionTree) {
		this.expressionTree = expressionTree;
	}

	//-----------------------mappingOperator-----------------

	/**
	 * @return the mappingOperator
	 */
	/*
	public MappingOperator getMappingOperator() {
		return mappingOperator;
	}

	public void setMappingOperator(MappingOperator mappingOperator) {
		if (this.mappingOperator != null) {
			this.mappingOperator.internalSetReconcilingExpression(null);
		}
		this.mappingOperator = mappingOperator;
		if (mappingOperator != null) {
			mappingOperator.internalSetReconcilingExpression(this);
		}
	}
	*/

	/**
	 * @param mappingOperator the mappingOperator to set
	 */
	/*
	public void internalSetMappingOperator(MappingOperator mappingOperator) {
		this.mappingOperator = mappingOperator;

	}
	*/

	//-----------------------schematicCorrespondence-----------------

	/**
	 * @return the schematicCorrespondence
	 */
	/*
	public SchematicCorrespondence getSchematicCorrespondence() {
		return schematicCorrespondence;
	}
	*/

	/**
	 * @param schematicCorrespondence the schematicCorrespondence to set
	 */
	/*
	public void setSchematicCorrespondence(SchematicCorrespondence schematicCorrespondence) {
		this.schematicCorrespondence = schematicCorrespondence;
	}
	*/

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReconcilingExpression [");
		if (expression != null)
			builder.append("expression=").append(expression).append(", ");
		if (expressionTree != null)
			builder.append("expressionTree=").append(expressionTree).append(", ");
		if (typeOfReconcilingExpression != null)
			builder.append("typeOfReconcilingExpression=").append(typeOfReconcilingExpression);
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
		result = prime * result + ((expression == null) ? 0 : expression.hashCode());
		result = prime * result + ((typeOfReconcilingExpression == null) ? 0 : typeOfReconcilingExpression.hashCode());
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
		ReconcilingExpression other = (ReconcilingExpression) obj;
		if (expression == null) {
			if (other.expression != null)
				return false;
		} else if (!expression.equals(other.expression))
			return false;
		if (typeOfReconcilingExpression == null) {
			if (other.typeOfReconcilingExpression != null)
				return false;
		} else if (!typeOfReconcilingExpression.equals(other.typeOfReconcilingExpression))
			return false;
		return true;
	}

	/*
	public String toString(int a) {
		return "Reconciling expression: " + expression + "\n\t Type: " + this.typeOfReconcilingExpression + "\n\t source superabstract"
				+ "\n\t targeting superabstract:" + selectionTargetSuperAbstract.getName();
	}
	*/

}
