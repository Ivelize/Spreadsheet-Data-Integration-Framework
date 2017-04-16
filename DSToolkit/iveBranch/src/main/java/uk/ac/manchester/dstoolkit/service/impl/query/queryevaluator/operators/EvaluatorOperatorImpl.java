package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.Mapping;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.JoinOperator;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.OperatorException;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

public abstract class EvaluatorOperatorImpl implements EvaluatorOperator {

	private static Logger logger = Logger.getLogger(EvaluatorOperatorImpl.class);

	//TODO possibly change input1 and input2 in MappingOperator into lhsInput and rhsInput

	/** flag denoting whether this operator is the root operator or not */
	private boolean isRoot;
	/** denotes whether the eof is reached **/
	private boolean ateof = true;

	private int maxNumberOfResults = -1;
	private int fetchSize = -1;

	/**
	 * @return the maxNumberOfResults
	 */
	public int getMaxNumberOfResults() {
		return maxNumberOfResults;
	}

	/**
	 * @param maxNumberOfResults the maxNumberOfResults to set
	 */
	public void setMaxNumberOfResults(int maxNumberOfResults) {
		this.maxNumberOfResults = maxNumberOfResults;
	}

	/**
	 * @return the fetchSize
	 */
	public int getFetchSize() {
		return fetchSize;
	}

	/**
	 * @param fetchSize the fetchSize to set
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	private String andOr;
	private String variableName;
	private String reconcilingExpression;
	//private List<Predicate> predicates;

	private EvaluatorOperator lhsInput; //mStoredInput /** Stored input operator.  This input is read and stored. */
	private EvaluatorOperator rhsInput; //mStreamedInput /** Streamed input operator.  This input is streamed through this operator  */

	private ResultType resultType;
	private Set<Predicate> joinPredicatesCarried = new LinkedHashSet<Predicate>();
	private Set<JoinOperator> joinOperatorsCarried = new LinkedHashSet<JoinOperator>();
	private DataSource dataSource = null;
	protected DataSource[] dataSources = new DataSource[2];
	private Set<Mapping> mappingsUsedForExpansion = new LinkedHashSet<Mapping>();
	//TODO think about whether these could be multiple mappings - might only be the case for the evaluateExternallyOp though

	/*
	 * Cardinality estimate the output of the operator.
	 */
	long cardinality;

	public EvaluatorOperatorImpl() {
	}

	public EvaluatorOperatorImpl(ResultType resultType, long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		this.resultType = resultType;
		this.cardinality = cardinality;
		this.joinPredicatesCarried = joinPredicatesCarried;
		this.dataSource = dataSource;
		if (this.joinPredicatesCarried == null)
			joinPredicatesCarried = new LinkedHashSet<Predicate>();
		logger.debug("evaluatorOperatorImpl.resultType: " + this.resultType);
	}

	public EvaluatorOperatorImpl(EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, ResultType resultType, long cardinality,
			Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		this.lhsInput = lhsInput;
		this.rhsInput = rhsInput;
		//this.addAllMappingsUsedForExpansion(lhsInput.getMappingsUsedForExpansion());
		//this.addAllMappingsUsedForExpansion(rhsInput.getMappingsUsedForExpansion());
		this.resultType = resultType;
		this.cardinality = cardinality;
		this.joinPredicatesCarried = joinPredicatesCarried;
		this.dataSource = dataSource;
		if (this.joinPredicatesCarried == null)
			joinPredicatesCarried = new LinkedHashSet<Predicate>();
		logger.debug("evaluatorOperatorImpl.resultType: " + this.resultType);
	}

	public void replaceInput(EvaluatorOperator oldOperator, EvaluatorOperator newOperator) {
		if (lhsInput == oldOperator)
			lhsInput = newOperator;
		else if (rhsInput == oldOperator)
			rhsInput = newOperator;
	}

	/**
	* Opens an input operator.
	* 
	* @param operator  Operator to open.
	* @param inputText Text describing the operator input.  Used to annotate
	*                  error messages.
	* @param logger    Logger to log any error messages.
	* 
	* @throws OperatorException if operator input cannot be opened.
	* @throws InterruptedException if the thread is interrupted.
	*/
	protected void openInputOperator(EvaluatorOperator evaluatorOperator, String inputText, Logger logger) { //throws OperatorException, InterruptedException {
		boolean isSuccesfullyOpened = false;
		//try {
		isSuccesfullyOpened = evaluatorOperator.open();
		//} catch (InterruptedException ex) {
		//	throw ex;
		//} catch (Exception ex) {
		//	logger.error(toString() + ": Error in opening " + inputText + ": " + ex);
		//	throw new OperatorException(0, ex, logger, toString());
		//}

		if (!isSuccesfullyOpened) {
			logger.error(toString() + ": Unable to open " + inputText + " input");
			//throw new OperatorException(0, logger, toString());
		}
	}

	protected void cleanup() {
		//contextId = null;
		//inputOp = null;
		//tupleType = null;
	}

	public DataSource getDataSourceOfLeftInput() {
		return dataSources[0];
	}

	public DataSource getDataSourceOfRightInput() {
		return dataSources[1];
	}

	protected void setDataSourceOfLeftInput(DataSource dataSource) {
		dataSources[0] = dataSource;
	}

	protected void setDataSourceOfRightInput(DataSource dataSource) {
		dataSources[1] = dataSource;
	}

	/**
	 * @return the input
	 */
	public EvaluatorOperator getInput() {
		return lhsInput;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(EvaluatorOperator input) {
		this.lhsInput = input;
		//this.addAllMappingsUsedForExpansion(input.getMappingsUsedForExpansion());
	}

	/**
	 * @return the lhsInput
	 */
	public EvaluatorOperator getLhsInput() {
		return lhsInput;
	}

	/**
	 * @param lhsInput the lhsInput to set
	 */
	public void setLhsInput(EvaluatorOperator lhsInput) {
		this.lhsInput = lhsInput;
		//this.addAllMappingsUsedForExpansion(lhsInput.getMappingsUsedForExpansion());
	}

	/**
	 * @return the rhsInput
	 */
	public EvaluatorOperator getRhsInput() {
		return rhsInput;
	}

	/**
	 * @param rhsInput the rhsInput to set
	 */
	public void setRhsInput(EvaluatorOperator rhsInput) {
		this.rhsInput = rhsInput;
		//this.addAllMappingsUsedForExpansion(rhsInput.getMappingsUsedForExpansion());
	}

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

	/**
	 * @return the joinPredicatesCarried
	 */
	public Set<Predicate> getJoinPredicatesCarried() {
		return joinPredicatesCarried;
	}

	/**
	 * @param joinPredicatesCarried the joinPredicatesCarried to set
	 */
	public void setJoinPredicatesCarried(Set<Predicate> joinPredicatesCarried) {
		this.joinPredicatesCarried = joinPredicatesCarried;
	}

	/**
	 * @return the joinOperatorsCarried
	 */
	public Set<JoinOperator> getJoinOperatorsCarried() {
		return joinOperatorsCarried;
	}

	/**
	 * @param joinOperatorsCarried the joinOperatorsCarried to set
	 */
	public void setJoinOperatorsCarried(Set<JoinOperator> joinOperatorsCarried) {
		this.joinOperatorsCarried = joinOperatorsCarried;
	}

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

	/**
	 * @return the dataSources
	 */
	public DataSource[] getDataSources() {
		return dataSources;
	}

	/**
	 * @param dataSources the dataSources to set
	 */
	public void setDataSources(DataSource[] dataSources) {
		this.dataSources = dataSources;
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

	/**
	 * @return the mappingsUsedForExpansion
	 */
	public Set<Mapping> getMappingsUsedForExpansion() {
		return mappingsUsedForExpansion;
	}

	/**
	 * @return the cardinality
	 */
	public long getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(long cardinality) {
		this.cardinality = cardinality;
	}

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

	/**
	 * @return the reconcilingExpression
	 */
	public String getReconcilingExpression() {
		return reconcilingExpression;
	}

	/**
	 * @param reconcilingExpression the reconcilingExpression to set
	 */
	public void setReconcilingExpression(String reconcilingExpression) {
		this.reconcilingExpression = reconcilingExpression;
	}

	/**
	 * @param isRoot the isRoot to set
	 */
	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	/**
	 * @return the isRoot
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * @param ateof the ateof to set
	 */
	public void setAteof(boolean ateof) {
		this.ateof = ateof;
	}

	/**
	 * @return the ateof
	 */
	public boolean isAteof() {
		return ateof;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EvaluatorOperatorImpl [");
		builder.append("cardinality=").append(cardinality).append(", ");
		if (variableName != null)
			builder.append("variableName=").append(variableName).append(", ");
		if (reconcilingExpression != null)
			builder.append("reconcilingExpression=").append(reconcilingExpression).append(", ");
		if (andOr != null)
			builder.append("andOr=").append(andOr).append(", ");
		if (joinPredicatesCarried != null)
			builder.append("joinPredicatesCarried=").append(joinPredicatesCarried).append(", ");
		if (lhsInput != null)
			builder.append("lhsInput=").append(lhsInput).append(", ");
		if (rhsInput != null)
			builder.append("rhsInput=").append(rhsInput);
		builder.append("]");
		return builder.toString();
	}

}
