package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.schematiccorrespondence.ReconcilingExpression;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;

/**
 * @author chedeler
 *
 */

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//TODO this is overruled by modelManagementConstruct anyway - change it
//@DiscriminatorColumn(name = "OPERATOR_TYPE", discriminatorType = DiscriminatorType.STRING)
//@Table(name = "MAPPING_OPERATORS")
//@SecondaryTable(name = "RECONCILING_EXPRESSION_MAPPING_OPERATOR")
public abstract class MappingOperator extends ModelManagementConstruct {

	static Logger logger = Logger.getLogger(MappingOperator.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4686671876529281130L;

	@Column(name = "MAPPING_OPERATOR_AND_OR")
	protected String andOr;

	@Column(name = "MAPPING_OPERATOR_VARIABLE_NAME")
	protected String variableName;

	@OneToOne(fetch = FetchType.EAGER)
	//cascade = { CascadeType.PERSIST, CascadeType.MERGE },
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "MAPPING_OPERATOR_RESULT_TYPE_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_MAPPING_OPERATOR_RESULT_TYPE_ID")
	protected ResultType resultType;

	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinColumn(name = "MAPPING_OPERATOR_LHS_INPUT_OPERATOR_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_OPERATOR_LHS_INPUT_OPERATOR_ID")
	protected MappingOperator lhsInput;

	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@JoinColumn(name = "MAPPING_OPERATOR_RHS_INPUT_OPERATOR_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_OPERATOR_RHS_INPUT_OPERATOR_ID")
	protected MappingOperator rhsInput;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	//mappedBy = "mappingOperator",
	//@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.ALL })
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	protected ReconcilingExpression reconcilingExpression;

	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "MAPPING_OPERATOR_DATA_SOURCE_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_OPERATOR_DATA_SOURCE_ID")
	protected DataSource dataSource;

	//to keep track of mappings during queryExpansion and in querystack
	//the mapping to which this mappingOperator belongs
	@OneToOne(fetch = FetchType.EAGER)
	//, cascade = { CascadeType.MERGE })
	//@org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	@JoinColumn(name = "MAPPING_OPERATOR_MAPPING_ID", nullable = true)
	@org.hibernate.annotations.ForeignKey(name = "FK_OPERATOR_MAPPING_ID")
	protected Mapping mapping;

	@Transient
	protected Set<Mapping> mappingsUsedForExpansion = new LinkedHashSet<Mapping>();

	/**
	 * 
	 */
	public MappingOperator() {
		super();
		if (this.getMapping() != null)
			this.addMappingUsedForExpansion(this.getMapping());
	}

	/**
	* @param input
	*/
	public MappingOperator(MappingOperator input) {
		this();
		this.setLhsInput(input);
	}

	/**
	 * @param input1
	 * @param input2
	 */
	public MappingOperator(MappingOperator lhsInput, MappingOperator rhsInput) {
		this();
		this.setLhsInput(lhsInput);
		this.setRhsInput(rhsInput);
	}

	public abstract boolean isJustScanOperator();

	/*
	public boolean queryIsJustScan() {
		logger.debug("in queryIsJustScan");
		logger.debug("this: " + this);
		if (this.getLhsInput() != null) {
			boolean lhsIsJustScan = this.getLhsInput().queryIsJustScan();
			logger.debug("lhsIsJustScan: " + lhsIsJustScan);
			if (!lhsIsJustScan)
				return false;
		}
		if (this.getRhsInput() != null) {
			boolean rhsIsJustScan = this.getRhsInput().queryIsJustScan();
			logger.debug("rhsIsJustScan: " + rhsIsJustScan);
			if (!rhsIsJustScan)
				return false;
		}

		if (this instanceof JoinOperator) {
			logger.debug("join - false");
			return false;
		} else if (this instanceof SetOperator) {
			logger.debug("set - false");
			return false;
		} else if (this instanceof ReduceOperator) {
			logger.debug("reduce - check whether its lhsInput is scan");
			ReduceOperator reduceOperator = (ReduceOperator) this;
			if (reduceOperator.getLhsInput() instanceof ScanOperator) {
				logger.debug("lhsInput is scan - check whether reduce has same number of superLexicals as scan - might have to check content though rather than just size");
				ScanOperator scanOperator = (ScanOperator) reduceOperator.getLhsInput();
				if (reduceOperator.getSuperLexicals().size() == scanOperator.getSuperAbstract().getSuperLexicals().size()) {
					//TODO only checking size here, not content, might have to change that
					logger.debug("same number of superLexicals - true");
					return true;
				} else {
					logger.debug("different number of superLexicals - false");
					return false;
				}

			} else {
				logger.debug("lhsInput of reduce isn't scan - false");
				return false;
			}
		} else if (this instanceof ScanOperator) {
			logger.debug("scan - true");
			return true;
		} else {
			logger.error("unknown operator: " + this);
			return false;
		}
	}
	*/

	//-----------------------lhsInput/input-----------------

	/**
	 * @return the input
	 */
	public MappingOperator getInput() {
		return lhsInput;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(MappingOperator input) {
		this.lhsInput = input;
	}

	/**
	 * @return the lhsInput
	 */
	public MappingOperator getLhsInput() {
		return lhsInput;
	}

	/**
	 * @param lhsInput the lhsInput to set
	 */
	public void setLhsInput(MappingOperator lhsInput) {
		this.lhsInput = lhsInput;
	}

	//-----------------------rhsInput-----------------

	/**
	 * @return the rhsInput
	 */
	public MappingOperator getRhsInput() {
		return rhsInput;
	}

	/**
	 * @param rhsInput the rhsInput to set
	 */
	public void setRhsInput(MappingOperator rhsInput) {
		this.rhsInput = rhsInput;
	}

	//-----------------------reconcilingExpression-----------------

	/**
	 * @param reconcilingExpressionString the reconcilingExpressionString to set
	 */
	public void setReconcilingExpression(String reconcilingExpressionString) {
		this.reconcilingExpression = new ReconcilingExpression(reconcilingExpressionString.trim());
		//this.reconcilingExpression.internalSetMappingOperator(this);
	}

	/**
	 * @return the reconcilingExpression
	 */
	public ReconcilingExpression getReconcilingExpression() {
		return reconcilingExpression;
	}

	/**
	 * @param reconcilingExpression the reconcilingExpression to set
	 */
	public void setReconcilingExpression(ReconcilingExpression reconcilingExpression) {
		/*
		if (this.reconcilingExpression != null) {
			this.reconcilingExpression.internalSetMappingOperator(null);
		}
		*/
		this.reconcilingExpression = reconcilingExpression;
		/*
		if (reconcilingExpression != null) {
			reconcilingExpression.internalSetMappingOperator(this);
		}
		*/
	}

	/*
	public void internalSetReconcilingExpression(ReconcilingExpression reconcilingExpression) {
		this.reconcilingExpression = reconcilingExpression;
	}
	*/

	//-----------------------andOr-----------------

	/**
	 * @return the andOr
	 */
	public String getAndOr() {
		return andOr;
	}

	/**
	 * @param andOr the andOr to set
	 */
	public void setAndOr(String andOr) {
		this.andOr = andOr;
	}

	//-----------------------resultType-----------------

	/**
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}

	/**
	 * @param resultType the resultType to set
	 */
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	//-----------------------dataSource-----------------

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	//-----------------------variableName-----------------

	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * @param variableName the variableName to set
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	//-----------------------mapping-----------------

	/**
	 * @return the mapping
	 */
	public Mapping getMapping() {
		return mapping;
	}

	/**
	 * @param mapping the mapping to set
	 */
	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	//-----------------------mappingsUsedForExpansion-----------------

	/**
	 * @return the mappingsUsedForExpansion
	 */
	public Set<Mapping> getMappingsUsedForExpansion() {
		//return Collections.unmodifiableSet(mappingsUsedForExpansion);
		return mappingsUsedForExpansion;
	}

	/**
	 * @param mappingsUsedForExpansion the mappingsUsedForExpansion to set
	 */
	public void setMappingsUsedForExpansion(Set<Mapping> mappingsUsedForExpansion) {
		this.mappingsUsedForExpansion = mappingsUsedForExpansion;
	}

	/**
	 * @param mappingUsedForExpansion
	 */
	public void addMappingUsedForExpansion(Mapping mappingUsedForExpansion) {
		this.mappingsUsedForExpansion.add(mappingUsedForExpansion);
	}

	/**
	 * @param mappingsUsedForExpansion
	 */
	public void addAllMappingsUsedForExpansion(Set<Mapping> mappingsUsedForExpansion) {
		this.mappingsUsedForExpansion.addAll(mappingsUsedForExpansion);
	}

	public void removeMappingUsedForExpansion(Mapping mappingUsedForExpansion) {
		this.mappingsUsedForExpansion.remove(mappingUsedForExpansion);
	}

	public void removeAllMappingsUsedforExpansion(Set<Mapping> mappingsUsedForExpansion) {
		this.mappingsUsedForExpansion.removeAll(mappingsUsedForExpansion);
	}

	//-----------------------replaceInput-----------------

	/**
	 * @param oldOperator
	 * @param newOperator
	 */
	public void replaceInput(MappingOperator oldOperator, MappingOperator newOperator) {
		logger.debug("in replaceInput");
		boolean foundOldOperator = false;
		if (lhsInput == oldOperator) {
			logger.debug("lhsInput == oldOperator");
			foundOldOperator = true;
			lhsInput = newOperator;
		}
		if (rhsInput == oldOperator) {
			logger.debug("rhsInput == oldOperator");
			foundOldOperator = true;
			rhsInput = newOperator;
		}
		if (!foundOldOperator)
			logger.error("didn't find old operator: " + oldOperator);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingOperator [");
		if (id != null)
			builder.append("id=").append(id).append(", ");
		if (reconcilingExpression != null)
			builder.append("reconcilingExpression=").append(reconcilingExpression.getExpression()).append(", ");
		builder.append("version=").append(version).append("]");
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lhsInput == null) ? 0 : lhsInput.hashCode());
		result = prime * result + ((rhsInput == null) ? 0 : rhsInput.hashCode());
		result = prime * result + ((reconcilingExpression == null) ? 0 : reconcilingExpression.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MappingOperator other = (MappingOperator) obj;
		if (lhsInput == null) {
			if (other.lhsInput != null)
				return false;
		} else if (!lhsInput.equals(other.lhsInput))
			return false;
		if (rhsInput == null) {
			if (other.rhsInput != null)
				return false;
		} else if (!rhsInput.equals(other.rhsInput))
			return false;
		if (reconcilingExpression == null) {
			if (other.reconcilingExpression != null)
				return false;
		} else if (!reconcilingExpression.equals(other.reconcilingExpression))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
