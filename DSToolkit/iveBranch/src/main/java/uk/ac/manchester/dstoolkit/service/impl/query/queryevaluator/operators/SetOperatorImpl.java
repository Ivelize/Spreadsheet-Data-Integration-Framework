package uk.ac.manchester.dstoolkit.service.impl.query.queryevaluator.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.Predicate;
import uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators.SetOperationType;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.exceptions.OperatorException;
import uk.ac.manchester.dstoolkit.service.query.queryevaluator.operators.EvaluatorOperator;

@Scope("prototype")
@Service
public class SetOperatorImpl extends EvaluatorOperatorImpl {

	private SetOperationType setOpType;

	//TODO set resultType to resultType of rhs
	//TODO resultType doesn't really work for rhsInput, as the resultType from lhsInput is used
	//TODO still need to check union compatibility in globalTranslator

	private static Logger logger = Logger.getLogger(SetOperatorImpl.class);
	/** ResultType object representing the input ResultType for the lhs input. */
	private final ResultType lhsType;
	/** ResultType object representing the input ResultType for the rhs input. */
	private final ResultType rhsType;
	/** List holding the fieldNames of the left join attributes. */
	private List<String> resultFieldNames;
	/** Size of the hash table */
	private final long hashTableSize;
	/** Size of the hash table as a power of 2 */
	private final int hashTableSizePW2;
	/**
	 * Linked list implementation of the hash table which will store the hashed
	 * resultInstances from the left input.
	 */
	private LinkedList<ResultInstance>[] hashedList;
	private int currentPosition = 0;
	private int countInCurrentPosition;

	/**
	 * Object of type ResultInstance which holds the current resultInstance from the right
	 * input.
	 */
	private ResultInstance currentResultInstance;
	/** List storing the result values from the current resultInstance. */
	//private List<String> currentResult;

	private int switchEOF = 0; //1 left finish, 2 right finish, 3 both finished

	/** List iterator for matching objects */
	private Iterator<ResultInstance> matchingIter = null;

	public SetOperatorImpl(EvaluatorOperator lhsInput, EvaluatorOperator rhsInput, SetOperationType setOpType, ResultType resultType,
			long cardinality, Set<Predicate> joinPredicatesCarried, DataSource dataSource) {
		super(lhsInput, rhsInput, resultType, cardinality, joinPredicatesCarried, dataSource);
		logger.debug("Entering SetOperatorImpl::SetOperatorImpl");
		this.setOpType = setOpType;
		logger.debug("setOpType=" + setOpType);
		setDataSourceOfLeftInput(lhsInput.getDataSource());
		setDataSourceOfRightInput(rhsInput.getDataSource());
		this.lhsType = this.getLhsInput().getResultType();
		this.rhsType = this.getRhsInput().getResultType();

		//TODO check if hashTablesize have to be bigger for UNIONS
		this.hashTableSize = 4096;
		this.hashTableSizePW2 = (int) (Math.log(hashTableSize) / Math.log(2));
		this.hashedList = new LinkedList[(int) this.hashTableSize];
		for (int listCount = 0; listCount < this.hashTableSize; listCount++) {
			this.hashedList[listCount] = new LinkedList<ResultInstance>();
		}
		logger.debug("Exiting SetOperatorImpl::SetOperatorImpl");
		/*
		if (lhsInput.getDataSource() != null && rhsInput.getDataSource() != null && dataSource != null) {
			logger.debug("all sourceIds are known");
			if (lhsInput.getDataSource().equals(rhsInput.getDataSource()) && !lhsInput.getDataSource().equals(dataSource)) {
				logger.error("sourceIds of inputs are same, but sourceId of union op is different - TODO proper error handling");
				setDataSource(lhsInput.getDataSource());
				//TODO sourceIds different, but shouldn't be - proper error handling - currently: sourceId gets set accordingly
			}
		}
		*/
	}

	public String makeResultValuesString(ResultInstance resultInstance) {
		logger.debug("in makeResultValueString");
		logger.debug("resultInstance: " + resultInstance);
		StringBuilder tmp = new StringBuilder();
		Map<String, ResultField> resultFieldsOfResultInstance = resultInstance.getResultType().getResultFields();
		//for (String resultFieldName : resultFieldNames) {
		Set<String> resultFieldNames = resultFieldsOfResultInstance.keySet();
		//for (ResultField resultField : resultFieldsOfResultInstance) {
		for (String resultFieldName : resultFieldNames) {
			//String resultFieldName = resultField.getFieldName();
			logger.debug("resultFieldName: " + resultFieldName);
			logger.debug("resultInstance.getResultValue(resultFieldName): " + resultInstance.getResultValue(resultFieldName));
			String resultValue = "";
			if (resultInstance.getResultValue(resultFieldName) != null)
				resultValue = resultInstance.getResultValue(resultFieldName).getValue();
			else
				resultValue = "null";
			logger.debug("resultValue: " + resultValue);
			tmp.append(resultValue);
		}
		logger.debug("exiting make resultValuesString");
		logger.debug("tmp.toString(): " + tmp.toString());
		return tmp.toString();
	}

	public boolean open() { //throws Exception {
		logger.debug("Entering SetOperatorImpl:" + this.toString() + ":open");
		logger.debug("this.getMappingsUsedForExpansion(): " + this.getMappingsUsedForExpansion());

		/* open the left input and hash all the tuples into a hashtable */
		boolean leftRet = false;
		//try {
		leftRet = getLhsInput().open();
		//} catch (InterruptedException ex) {
		//	throw ex;
		//} catch (Exception ex) {
		//	logger.error(this.toString() + ": Error in opening left input: " + ex.toString());
		//	throw new OperatorException(0, ex, logger, this.toString());
		//}
		if (lhsType == null || rhsType == null) {
			logger.error(this.toString() + ": Null result types");
			//throw new OperatorException(0, logger, this.toString());
		}

		/* Build the List of resultFieldNames for the union attributes */
		this.resultFieldNames = getResultFieldNames(this.lhsType);

		/* if the open on left input return true - i.e. it is correct */
		ResultInstance tmpResultInstance = null;
		//Vector result = null;
		String resultValuesString = new String();

		if (leftRet) {
			if (setOpType != SetOperationType.UNION_ALL) {
				//union all doesn't need to have the first data set hashed
				tmpResultInstance = getLhsInput().next();
				/*
				 * while left input is not exhausted, get next resultInstance
				 * from left input and build a hashKey using all resultValues
				 * Insert each resultInstance into the hash table
				 */
				while (!tmpResultInstance.isEof()) {
					//tmpResultInstance.addAllMappings(getLhsInput().getMappingsUsedForExpansion());
					//tmpResultInstance.addAllMappings(this.getMappingsUsedForExpansion());
					logger.debug("tmpResultInstance.getMappings(): " + tmpResultInstance.getMappings());
					resultValuesString = makeResultValuesString(tmpResultInstance);
					logger.debug(this.toString() + ": resultValuesString: " + resultValuesString);
					int key = BKDRHash(resultValuesString);
					logger.debug(this.toString() + ": Hash key: " + key);

					if (setOpType != SetOperationType.UNION) {
						//intersect and except don't care about same rows in first dataset
						logger.debug(this.toString() + ": Added resultInstance from left to hashtable: " + tmpResultInstance.toString());
						this.hashedList[key].addLast(tmpResultInstance);
					} else {
						if (!this.hashedList[key].listIterator().hasNext()) {
							//union cares about same rows in  first dataset
							logger.debug(this.toString() + ": Added resultInstance from left to hashtable: " + tmpResultInstance.toString());
							this.hashedList[key].addLast(tmpResultInstance);
						} else
							logger.debug("ResultInstance with key " + key + " already found - not added:" + tmpResultInstance.toString());
					}
					tmpResultInstance = getLhsInput().next();
				}
				getLhsInput().close();
			}

			boolean rightRet = false;
			//try {
			rightRet = getRhsInput().open();
			//} catch (InterruptedException ex) {
			//	throw ex;
			//} catch (Exception ex) {
			//	logger.error(this.toString() + ": Error in opening right input: " + ex.toString());
			//	throw new OperatorException(0, ex, logger, this.toString());
			//}
			if (!rightRet) {
				logger.error(this.toString() + ": Unable to open right input");
				//throw new OperatorException(0, logger, this.toString());
			} else {
				/* This is the starting point for the next iteration */
				if (setOpType == SetOperationType.UNION) {
					logger.debug("Union, setOpType: " + setOpType);
					tmpResultInstance = getRhsInput().next();
					logger.debug("getRhsInput().next(), next tmpResultInstance: " + tmpResultInstance);
					/*
					 * while right input is not exhausted, get next
					 * resultInstances from right input and build a hashKey
					 * using all resultValues. Insert each resultInstance into the
					 * hash table
					 */
					while (!tmpResultInstance.isEof()) {
						logger.debug("!tmpResultInstance.isEOF()");
						//tmpResultInstance.addAllMappings(getRhsInput().getMappingsUsedForExpansion());
						logger.debug("tmpResultInstance.getMappings(): " + tmpResultInstance.getMappings());
						//tmpResultInstance.addAllMappings(this.getMappingsUsedForExpansion());
						//TODO currently assumed it'll have the correct resultType, might not work though
						//logger.debug("set resultType to resultType of rhs");
						//tmpResultInstance.setResultType(rhsType);
						resultValuesString = makeResultValuesString(tmpResultInstance);
						logger.debug(this.toString() + ": resultValuesString: " + resultValuesString);
						int key = BKDRHash(resultValuesString);
						logger.debug(this.toString() + ": Hash key: " + key);
						this.hashedList[key].addLast(tmpResultInstance);
						logger.debug(this.toString() + ": Added resultInstance from right input to " + "hashtable: " + tmpResultInstance.toString());
						tmpResultInstance = getRhsInput().next();
						logger.debug("next tmpResultInstance: " + tmpResultInstance);
					}
					getRhsInput().close();
					//break;
				} else if (setOpType.equals(SetOperationType.EXCEPT)) {
					logger.debug("Except, setOpType: " + setOpType);
					tmpResultInstance = getRhsInput().next();
					logger.debug("getRhsInput().next(), next tmpResultInstance: " + tmpResultInstance);
					/*
					 * while right input is not exhausted, get next
					 * tuples from right input and build a hashKey
					 * using all positions delete match tuple from
					 * the hash table
					 */
					while (!tmpResultInstance.isEof()) {
						logger.debug("!tmpResultInstance.isEOF()");
						//tmpResultInstance.addAllMappings(getRhsInput().getMappingsUsedForExpansion());
						logger.debug("tmpResultInstance.getMappings(): " + tmpResultInstance.getMappings());
						//TODO currently assumed it'll have the correct resultType, might not work though
						//logger.debug("set resultType to resultType of rhs");
						//tmpResultInstance.setResultType(rhsType);
						resultValuesString = makeResultValuesString(tmpResultInstance);
						logger.debug(this.toString() + ": resultValuesString: " + resultValuesString);
						int key = BKDRHash(resultValuesString);
						logger.debug(this.toString() + ": Hash key: " + key);
						this.hashedList[key].clear();
						logger.debug(this.toString() + ": removed resultInstance from hashtable with:" + tmpResultInstance.toString());
						tmpResultInstance = getRhsInput().next();
						logger.debug("next tmpResultInstance: " + tmpResultInstance);
					}
					getRhsInput().close();
					//break;
				}
			}
		} else {
			logger.error(this.toString() + ": Unable to open left input");
			//throw new OperatorException(0, logger, this.toString());
		}
		this.switchEOF = 0;
		this.setAteof(false);

		logger.debug("Exiting SetOperatorImpl:" + this.toString() + ":open");
		return true;
	}

	/**
	 * The next method fetches the next resultInstance from the right input, applies
	 * the same hash function and probes the hash table for matches.
	 * According to UNION method .... otherwise the next resultInstance is fetched
	 * from the right input - this process is repeated till the EOF is found.
	 * 
	 * @throws OperatorException
	 *             thrown in case of any error
	 * @return the next unioned resultInstance
	 */
	public ResultInstance next() { //throws InterruptedException, OperatorException {
		logger.debug("Entering SetOperatorImpl:" + this.toString() + ":next");
		//TODO check what happens with last resultInstance from both inputs, should be just eof without data

		String resultValuesString = new String();
		int key;
		if (this.isAteof() != true) {
			if (setOpType.equals(SetOperationType.UNION)) {
				logger.debug("Union, setOpType: " + setOpType);
				for (; currentPosition < hashedList.length;) {
					if (matchingIter == null) {
						matchingIter = hashedList[currentPosition].iterator();
						this.countInCurrentPosition = 1;
					}
					if (matchingIter.hasNext() && this.countInCurrentPosition == 1) {
						this.countInCurrentPosition++;
						ResultInstance currentResultInstance = matchingIter.next();
						while (matchingIter.hasNext()) {
							ResultInstance resultInstance = matchingIter.next();
							currentResultInstance.addAllMappings(resultInstance.getMappings());
						}
						//currentResultInstance.addAllMappings(this.getMappingsUsedForExpansion());
						logger.debug("currentResultInstance.getMappings(): " + currentResultInstance.getMappings());
						return currentResultInstance;
					} else {
						matchingIter = null;
						currentPosition++;
					}
				}
				setAteof(true);
				//break;
			} else if (setOpType.equals(SetOperationType.EXCEPT)) {
				//just return remaining tuples in hashedList!
				logger.debug("Except, setOpType: " + setOpType);
				for (; currentPosition < hashedList.length;) {
					if (matchingIter == null) {
						matchingIter = hashedList[currentPosition].iterator();
					}
					if (matchingIter.hasNext()) {
						ResultInstance currentResultInstance = matchingIter.next();
						while (matchingIter.hasNext()) {
							ResultInstance resultInstance = matchingIter.next();
							currentResultInstance.addAllMappings(resultInstance.getMappings());
						}
						//currentResultInstance.addAllMappings(this.getMappingsUsedForExpansion());
						logger.debug("currentResultInstance.getMappings(): " + currentResultInstance.getMappings());
						return currentResultInstance;
					} else {
						matchingIter = null;
						currentPosition++;
					}
				}
				setAteof(true);
			} else if (setOpType.equals(SetOperationType.INTERSECT)) {
				logger.debug("intersect, setOpType: " + setOpType);
				this.currentResultInstance = getRhsInput().next();
				while (matchingIter.hasNext()) {
					ResultInstance resultInstance = matchingIter.next();
					currentResultInstance.addAllMappings(resultInstance.getMappings());
				}
				//currentResultInstance.addAllMappings(this.getMappingsUsedForExpansion());
				logger.debug("currentResultInstance:" + currentResultInstance);
				logger.debug("currentResultInstance.getMappings(): " + currentResultInstance.getMappings());
				while (!this.currentResultInstance.isEof()) {
					logger.debug("getRhsInput().next(): Received current resultInstance: " + this.currentResultInstance.toString()
							+ "this.toString(): " + this.toString());
					//TODO currently assumed it'll have the correct resultType, might not work though
					//logger.debug("set resultType to resultType of rhs");
					//currentResultInstance.setResultType(rhsType);
					/* current resultInstance has to be hashed */
					resultValuesString = makeResultValuesString(currentResultInstance);
					logger.debug(this.toString() + ": resultValuesString: " + resultValuesString);
					key = BKDRHash(resultValuesString);
					logger.debug(this.toString() + ": Hash key: " + key);
					this.matchingIter = this.hashedList[key].listIterator();
					if (this.matchingIter.hasNext()) {
						//we find a match so this row is in both and can be returned
						return currentResultInstance;
					} else {
						this.currentResultInstance = getRhsInput().next();
					}
				}
				setAteof(true);
				getRhsInput().close();
			} else if (setOpType.equals(SetOperationType.UNION_ALL)) {
				logger.debug("unionAll, setOpType: " + setOpType);
				ResultInstance tmpResultInstance = null;
				//Vector result = null;
				//first put out all rows from the left input, then from the right
				switch (switchEOF) {
				case 0://put out left resultInstances first
					logger.debug("put out left resultInstances first");
					tmpResultInstance = getLhsInput().next();
					logger.debug("tmpResultInstance: " + tmpResultInstance);
					if (!tmpResultInstance.isEof()) {
						logger.debug("Received a resultInstance from left:" + tmpResultInstance);
						//tmpResultInstance.addAllMappings(getLhsInput().getMappingsUsedForExpansion());
						logger.debug("tmpResultInstance.getMappings(): " + tmpResultInstance.getMappings());
						return tmpResultInstance;
					}
					switchEOF = 1;
					getLhsInput().close();
					// Fall through expected

				case 1://put out right tuples
					logger.debug("put out right tuples");
					tmpResultInstance = getRhsInput().next();
					logger.debug("getRhsInput().next(), tmpResultInstance: " + tmpResultInstance);
					if (!tmpResultInstance.isEof()) {
						logger.debug("Recieved a resultInstance from right:" + tmpResultInstance);
						//TODO currently assumed it'll have the correct resultType, might not work though
						//logger.debug("set resultType to resultType of rhs");
						//currentResultInstance.setResultType(rhsType);
						//tmpResultInstance.addAllMappings(getRhsInput().getMappingsUsedForExpansion());
						logger.debug("tmpResultInstance.getMappings(): " + tmpResultInstance.getMappings());
						return tmpResultInstance;
					}
					switchEOF = 2;
					getRhsInput().close();
					// Fall through expected

				case 2://both inputs eof => finished
					logger.debug("both inputs eof => finished");
					switchEOF = 3;
					this.setAteof(true);
					break;
				}
			}
		}
		logger.debug(this.toString() + ": returning EOF");
		ResultInstance eofResultInstance = new ResultInstance();
		eofResultInstance.setEof(true);
		return eofResultInstance;
	}

	/**
	    * Closes the right input and returns boolean result.
	    * 
	    * @return boolean
	    */
	public boolean close() {
		logger.debug("Entering SetOperatorImpl:" + this.toString() + ":close");
		hashedList = null;
		cleanup();
		logger.debug("Exiting SetOperatorImpl:" + this.toString() + ":close");
		return true;
	}

	/**
	 * This hash function comes from Brian Kernighan and Dennis Ritchie's
	 * book "The C Programming Language". It is a simple hash function using
	 * a set of possible seeds which all constitute a pattern of
	 * 31....31...31 etc, it seems to have a good data distribution feature,
	 * and is reasonably efficient. 
	 * 
	 * @return integer hashKey
	 * @param str
	 *            String input
	 */
	public int BKDRHash(String str) {
		logger.debug("in BKDFHash");
		logger.debug("str: " + str);
		long seed = 13131; // 31 131 1313 13131 131313 etc..
		long hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}
		hash = hash & 0x7FFFFFFF;
		hash = hash & ((1 << hashTableSizePW2) - 1);
		return (int) hash;
	}

	/**
	 * This method builds the List holding names for all attributes.
	 * 
	 * @return list containing the names of all attributes
	 * @param resultType
	 *            resultType for the left or right input
	 */
	private List<String> getResultFieldNames(ResultType resultType) {
		logger.debug("Entering SetOperatorImpl:" + this.toString() + ":getResultFieldNames");
		logger.debug("resultType: " + resultType);
		List<String> resultList = new ArrayList<String>();

		Map<String, ResultField> inField = resultType.getResultFields();
		logger.debug("inField: " + inField);
		Set<String> inFieldNames = inField.keySet();
		//for (int posCount = 0; posCount < inField.size(); posCount++) {
		for (String inFieldName : inFieldNames) {
			//logger.debug("posCount: " + posCount);
			//CanonicalModelConstruct cmc = inField.get(posCount).getCanonicalModelConstruct();
			CanonicalModelConstruct cmc = inField.get(inFieldName).getCanonicalModelConstruct();
			logger.debug("cmc: " + cmc);
			//resultList.add(inField.get(posCount).getFieldName());
			resultList.add(inFieldName);
			//logger.debug("add FieldName to resultList: " + inField.get(posCount).getFieldName());
			logger.debug("add FieldName to resultList: " + inFieldName);
		}

		logger.debug("Exiting SetOperatorImpl:" + this.toString() + ":getResultFieldNames");
		return resultList;
	}

	/*
	public List<ResultInstance> getResultInstances() {
		// TODO Auto-generated method stub
		return null;
	}
	*/

	/**
	 * @return the setOpType
	 */
	public SetOperationType getSetOpType() {
		return setOpType;
	}

	/**
	 * @param setOpType the setOpType to set
	 */
	public void setSetOpType(SetOperationType setOpType) {
		this.setOpType = setOpType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SetOperatorImpl [");
		if (setOpType != null)
			builder.append("setOpType=").append(setOpType).append(", ");
		if (super.toString() != null)
			builder.append("toString()=").append(super.toString());
		builder.append("]");
		return builder.toString();
	}

	public void setQueryString(String queryString) {
		// TODO Auto-generated method stub

	}

}
