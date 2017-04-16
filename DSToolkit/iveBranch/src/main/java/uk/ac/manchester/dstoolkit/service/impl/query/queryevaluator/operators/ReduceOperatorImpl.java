package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.OperatorException;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

/**
 * class representing the REDUCE operator. This operator performs the PROJECT
 * operation on the input resultInstance. It builds a new resultInstance (for output) containing the fields specified in the
 * parameters of the REDUCE operator.
 */
@Scope("prototype")
@Service
public class ReduceOperatorImpl extends EvaluatorOperatorImpl {

	private static Logger logger = Logger.getLogger(ReduceOperatorImpl.class);

	private Map<String, SuperLexical> superLexicals;

	/** fields used for the operation */
	//private String[] parameters;

	/** type of operation - PROJECT/AGGREGATE */
	private final String applyOpType = "PROJECT";

	/** stack of resultInstances to be output at eof */
	private Stack<ResultInstance> tail = new Stack<ResultInstance>();

	public ReduceOperatorImpl(EvaluatorOperator input, String reconcilingExpression, Map<String, SuperLexical> superLexicals, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(resultType, cardinality, joinPredicatesCarried, dataSource);
		this.setLhsInput(input);
		this.setReconcilingExpression(reconcilingExpression);
		this.superLexicals = superLexicals;
	}

	/**
	 * Opens the input
	 * @throws OperatorException
	 *             thrown if there is an error
	 * @return true/false
	 */
	public boolean open() { //throws InterruptedException, OperatorException {
		logger.debug("Entering ReduceOperatorImpl:open: " + this.toString());
		logger.debug("ReduceOperatorImpl - this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		boolean inputRet = false;
		//try {
		inputRet = this.getLhsInput().open();
		//} catch (InterruptedException ix) {
		//	throw ix;
		//} catch (Exception ex) {
		//	logger.error("ReduceOperatorImpl:open, Error opening input: " + ex.toString() + "this.toString(): " + this.toString());
		//	throw new OperatorException(0, logger, this.toString());
		//}
		if (!inputRet) {
			logger.error("!inputRet: " + !inputRet);
			logger.error("ReduceOperatorImpl:open, Error opening input, this.toString(): " + this.toString());
			//throw new OperatorException(0, logger, this.toString());
		}
		/*
		if (applyOpType.equals("SUM") || applyOpType.equals("MIN") || applyOpType.equals("MAX") || applyOpType.equals("COUNT")
				|| applyOpType.equals("STDDEV") || applyOpType.equals("AVG")) {
			mLog.debug(this.operatorId + ": Aggregation attribute name: " + this.aggregationOps.attributeName + " type: "
					+ this.aggregationOps.aggregationType);
			this.aggregateObj = Aggregate.Create(this.aggregationOps.aggregationType);
		}
		*/
		this.setAteof(false);
		logger.debug("Exiting ReduceOp:open: " + this.toString());
		return true;
	}

	/**
	 * Calls next on the input operator; if the operation type is PROJECT, it
	 * filters out the unnecessary fields and build s a new output tuple;
	 * 
	 * @throws OperatorException
	 *             thrown in case of any error
	 * @return output tuple
	 */
	public ResultInstance next() { //throws InterruptedException, OperatorException {
		logger.debug("Entering ReduceOperatorImpl:next: " + this.toString());
		logger.debug("this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		ResultInstance inResultInstance = null;
		ResultInstance outResultInstance = null;
		if (!this.isAteof()) {
			//try {
			if (applyOpType.equals("PROJECT")) {
				logger.debug("ReduceOperatorImpl:next: Applying PROJECT, this.toString(): " + this.toString());
				inResultInstance = getLhsInput().next();
				logger.debug("ReduceOperatorImpl:next: resultInstance: " + inResultInstance);
				if (inResultInstance != null) {
					logger.debug("ReduceOperatorImpl:next: result type " + inResultInstance.getResultType() + ", value = "
							+ inResultInstance.toString() + "this.toString(): " + this.toString());
				}
				if (!inResultInstance.isEof()) {
					logger.debug("inResultInstance isn't eof");
					outResultInstance = applyProject(inResultInstance);
					logger.debug("outResultInstance: " + outResultInstance);
				} else {
					outResultInstance = inResultInstance;
					setAteof(true);
				}
			} else if (applyOpType.equals("SUM") || applyOpType.equals("MIN") || applyOpType.equals("MAX") || applyOpType.equals("COUNT")
					|| applyOpType.equals("STDDEV") || applyOpType.equals("AVG")) {
				logger.debug("TODO ReduceOperatorImpl: Applying AGGREGATE: " + this.toString());
				/*
				inResultInstance = this.getLhsInput().next();
				while (!inResultInstance.isEOF()) {
					applyAggregate(inResultInstance);
					inResultInstance = this.getLhsInput().next();
				}
				logger.debug(this.toString() + ": Received EOF from input");
				Vector outVector = new Vector();
				outVector.addElement(this.aggregateObj.Result());
				mLog.debug(this.operatorId + ": Aggregate results: " + outVector.toString());
				outResultInstance = new DataTuple(outVector);
				this.tail.push(outResultInstance);
				this.ateof = true;
				*/
			} else {
				logger.debug("ReduceOperatorImpl : Unsupported operation..." + this.toString());
				//throw new OperatorException(0, logger, this.toString());
			}
			//} catch (InterruptedException ix) {
			//	throw ix;
			//} catch (Exception ex) {
			//	logger
			//			.error("ReduceOperatorImpl : Exception in ReduceOperatorImpl::next: " + ex.getMessage() + "this.toString(): "
			//					+ this.toString());
			//	throw new OperatorException(0, logger, this.toString());
			//}
		}

		/*
		if (!this.tail.isEmpty()) {
			logger.debug("!this.tail.isEmpty()");
			ResultInstance outRI = this.tail.pop();
			logger.debug("outRI: " + outRI);
			return outRI;
		} else 
		*/
		if (!isAteof()) {
			logger.debug("!isAteof()" + !isAteof());
			logger.debug("outResultInstance: " + outResultInstance);
			//outResultInstance.addAllMappings(this.getMappingsUsedForExpansion());
			outResultInstance.addAllMappings(inResultInstance.getMappings());
			logger.debug("outResultInstance.getMappings(): " + outResultInstance.getMappings());
			logger.debug("inResultInstance.getMappings(): " + inResultInstance.getMappings());
			return outResultInstance;
		} else {
			logger.debug("isAteof: " + isAteof());
			logger.debug("outResultInstance: " + outResultInstance);
			return outResultInstance; //should be EOF - TODO check this
		}
	}

	/**
	* closes the input operator
	* 
	* @return true/false
	*/
	public boolean close() {
		logger.debug("Entering ReduceOperatorImpl: close: " + this.toString());
		boolean retVal = this.getLhsInput().close();
		tail = null;
		/*
		parameters = null;
		aggregationOps = null;
		aggregateObj = null;
		*/
		cleanup();
		logger.debug("Exiting ReduceOperatorImpl: close: " + this.toString());
		return retVal;
	}

	/**
	* Applies the PROJECT operation (filters fields) as specified in resultType
	*
	* @return new output resultInstance with the unnecessary fields filtered out
	*/
	private ResultInstance applyProject(ResultInstance inResultInstance) {
		logger.debug("Entering ReduceOperatorImpl: applyProject: " + this.toString());
		logger.debug("ReduceOperatorImpl: Incoming resultInstance: " + inResultInstance); // + " this.toString(): " + this.toString());
		logger.debug("inResultInstance.getMappings(): " + inResultInstance.getMappings());
		logger.debug("this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());
		ResultInstance outResultInstance = new ResultInstance();
		outResultInstance.setResultType(this.getResultType());

		Map<String, ResultField> outFields = this.getResultType().getResultFields();
		//Object[] tmpArray = new Object[outFields.length];
		Set<String> outFieldNames = outFields.keySet();
		logger.debug("outFieldNames: " + outFieldNames);

		//for (ResultField resultField : outFields) {
		for (String resultFieldName : outFieldNames) {
			//String resultFieldName = resultField.getFieldName();
			logger.debug("ReduceOperatorImpl: resultFieldName: " + resultFieldName); // + " this.toString(): " + this.toString());
			logger.debug("getResultValue(resultFieldName): " + inResultInstance.getResultValue(resultFieldName));
			if (inResultInstance.getResultValue(resultFieldName) != null) {
				String resultValue = inResultInstance.getResultValue(resultFieldName).getValue();
				logger.debug("resultValue: " + resultValue);
				if (resultValue != null) {
					//outResultInstance.getResultFieldNameResultValueMap().put(resultFieldName, inResultInstance.getResultValue(resultFieldName));
					outResultInstance.addResultValue(resultFieldName, inResultInstance.getResultValue(resultFieldName));
				} //else {
					//	outResultInstance.addResultValue(resultFieldName, null);
					//}
			} else {
				logger.debug("resultValue is null - check whether variableName is causing an issue - this is a hack to avoid having to sort out the expander properly");
				logger.debug("resultFieldName: " + resultFieldName);
				if (resultFieldName.contains(".")) {
					logger.debug("resultFieldName contains .");
					Set<String> inFieldNames = inResultInstance.getResultFieldNameResultValueMap().keySet();
					String inFieldName = inFieldNames.iterator().next();
					logger.debug("inFieldName: " + inFieldName);
					if (inFieldName.contains(".")) {
						logger.debug("inFieldName contains .");
						String variableNameOfInFieldName = inFieldName.substring(0, inFieldName.indexOf("."));
						logger.debug("variableNameOfInFieldName: " + variableNameOfInFieldName);
						String resultFieldNameWithoutVariableName = resultFieldName.substring(resultFieldName.indexOf(".") + 1);
						logger.debug("resultFieldNameWithoutVariableName: " + resultFieldNameWithoutVariableName);
						String newFullColumnName = variableNameOfInFieldName + "." + resultFieldNameWithoutVariableName;
						logger.debug("newFullColumnName: " + newFullColumnName);
						logger.debug("getResultValue(newFullColumnName): " + inResultInstance.getResultValue(newFullColumnName));
						if (inResultInstance.getResultValue(newFullColumnName) != null) {
							String resultValue = inResultInstance.getResultValue(newFullColumnName).getValue();
							logger.debug("resultValue: " + resultValue);
							if (resultValue != null) {
								//outResultInstance.getResultFieldNameResultValueMap().put(resultFieldName, inResultInstance.getResultValue(resultFieldName));
								outResultInstance.addResultValue(resultFieldName, inResultInstance.getResultValue(newFullColumnName));
							}
						} else {
							logger.debug("still haven't found resultValue");
							for (String inResultFieldName : inResultInstance.getResultFieldNameResultValueMap().keySet()) {
								if (inResultFieldName.contains(".") && resultFieldName.contains(".")) {
									String inResultFieldNameWithoutDot = inResultFieldName.substring(inResultFieldName.indexOf(".") + 1);
									String resultFieldNameWithoutDot = resultFieldName.substring(resultFieldName.indexOf(".") + 1);
									logger.debug("inResultFieldNameWithoutDot: " + inResultFieldNameWithoutDot);
									logger.debug("resultFieldNameWithoutDot: " + resultFieldNameWithoutDot);
									if (inResultFieldNameWithoutDot.equals(resultFieldNameWithoutDot)) {
										logger.debug("inResultFieldNameWithoutDot.equals(resultFieldNameWithoutDot)");
										if (inResultInstance.getResultValue(inResultFieldName) != null) {
											String resultValue = inResultInstance.getResultValue(inResultFieldName).getValue();
											logger.debug("resultValue: " + resultValue);
											if (resultValue != null) {
												//outResultInstance.getResultFieldNameResultValueMap().put(resultFieldName, inResultInstance.getResultValue(resultFieldName));
												outResultInstance.addResultValue(resultFieldName, inResultInstance.getResultValue(newFullColumnName));
											}
										}
									}
								}
							}
						} //else {
							//	outResultInstance.addResultValue(resultFieldName, null);
							//}
					}
				}
			}
		}
		logger.debug("ReduceOperatorImpl: Projected resultInstance: " + outResultInstance); // + "this.toString(): " + this.toString());
		logger.debug("Exiting ReduceOperatorImpl: applyProject: " + this.toString());
		return outResultInstance;
	}

	/**
	 * applies the required aggregate operation by calling the correct aggregate
	 * operator object.
	 * 
	 * @throws OperatorException
	 *             thrown in case of any error
	 * @return output tuple containing the result of the aggregate operation.
	 */
	/*
	private void applyAggregate(DataTuple inTuple) throws OperatorException {
	    mLog.debug("Entering ReduceOp:" + this.operatorId + ":applyAggregate");
	    try {
	        Vector tmpVector = inTuple.getTuple();
	        String attrName = this.aggregationOps.attributeName;
	        int posn = this.inputType.returnPosition(attrName);
	        mLog.debug(this.operatorId + ": attribute name: " + attrName + ", position: " + posn);
	        this.aggregateObj.Next(tmpVector.elementAt(posn));
	        mLog.debug("Exiting ReduceOp:" + this.operatorId
	                + ":applyAggregate");
	    } catch (Exception ex) {
	        mLog.error(this.operatorId + ": Exception in Reduce::Next:applyAggregate "
	                + ex.getMessage());
	        throw new OperatorException(TypeDefs.OPERATOR_EXCEPTION, mLog,
	                this.operatorId);
	    }
	}
	*/

	/*
	public List<ResultInstance> getResultInstances() {
		logger.error("in getResultInstances");
		// TODO Auto-generated method stub
		return null;
	}
	*/

	/**
	 * @return the superLexicals
	 */
	public Map<String, SuperLexical> getSuperLexicals() {
		return superLexicals;
	}

	/**
	 * @param superLexicals the superLexicals to set
	 */
	public void setSuperLexicals(Map<String, SuperLexical> superLexicals) {
		this.superLexicals = superLexicals;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReduceOperatorImpl [");
		if (superLexicals != null)
			builder.append("superLexicals=").append(superLexicals).append(", ");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	public void setQueryString(String queryString) {
		// TODO Auto-generated method stub

	}

}
