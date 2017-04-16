package uk.ac.manchester.dstoolkit.domain.models.morphisms.mapping.operators;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.domain.models.ModelManagementConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataTypeMapper;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;

@Entity
@Table(name = "PREDICATES")
public class Predicate extends ModelManagementConstruct {

	private static Logger logger = Logger.getLogger(Predicate.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5259412677094130470L;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PREDICATE_SUPER_LEXICAL1_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_PREDICATE_SUPER_LEXICAL1_ID")
	private SuperLexical superLexical1 = null;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PREDICATE_SUPER_LEXICAL2_ID")
	@org.hibernate.annotations.ForeignKey(name = "FK_PREDICATE_SUPER_LEXICAL2_ID")
	private SuperLexical superLexical2 = null;

	@Column(name = "literal1")
	private String literal1 = null;

	@Column(name = "literal2")
	private String literal2 = null;

	//TODO sort out functions

	/*
	 * The predicate operator. May take the values:
	 * =, !=, <, >, <=, >=, IN 
	 */
	@Column(name = "operator")
	private String operator;

	@Column(name = "andOr")
	private String andOr;

	public Predicate() {
		super();
	}

	public Predicate(SuperLexical superLexical1, SuperLexical superLexical2, String operator) {
		this.setSuperLexical1(superLexical1);
		this.setSuperLexical2(superLexical2);
		this.setOperator(operator);
	}

	public Predicate(SuperLexical superLexical1, String literal2, String operator) {
		this.setSuperLexical1(superLexical1);
		this.setLiteral2(literal2);
		this.setOperator(operator);
	}

	public Predicate(String literal1, SuperLexical superLexical2, String operator) {
		this.setLiteral1(literal1);
		this.setSuperLexical2(superLexical2);
		this.setOperator(operator);
	}

	//-----------------------reverseOperands-----------------

	/**
	 * Reverses the operands of the predicate.
	 * E.g. x < y becomes y > x etc.
	 * Necessary because of the evaluator's sensitivity
	 * to predicate operand order.
	 * The compiler should never attempt to reverse a predicate containing
	 * IN. This operator should have been replaced with "=" before reverseOperands
	 * is invoked.
	 * @throws Exception 
	 */
	public void reverseOperands() throws Exception {
		if (operator.equalsIgnoreCase(("IN"))) {
			throw new Exception("Attempt to reverse predicate containing IN");
		}
		if (superLexical1 != null && superLexical2 != null) {
			SuperLexical temp = superLexical1;
			superLexical1 = superLexical2;
			superLexical2 = temp;
		} else if (superLexical1 != null && literal2 != null) {
			literal1 = literal2;
			literal2 = null;
			superLexical2 = superLexical1;
			superLexical1 = null;
		} else if (literal1 != null && superLexical2 != null) {
			literal2 = literal1;
			literal1 = null;
			superLexical1 = superLexical2;
			superLexical2 = null;
		}
		if (operator.equals("<"))
			operator = ">";
		else if (operator.equals(">"))
			operator = "<";
		else if (operator.equals("<="))
			operator = ">=";
		else if (operator.equals(">="))
			operator = "<=";
		//(= and != stay the same!).    
	}

	//-----------------------isInequality-----------------

	/**
	 * Returns true if the predicate involves an inequality
	 * operator.
	 */
	public boolean isInequality() {
		if (operator.equalsIgnoreCase("<"))
			return true;
		if (operator.equalsIgnoreCase("!="))
			return true;
		if (operator.equalsIgnoreCase(">"))
			return true;
		if (operator.equalsIgnoreCase(">="))
			return true;
		if (operator.equalsIgnoreCase("<="))
			return true;
		return false;
	}

	//-----------------------hasSuperLexicalArgument-----------------

	/*
	 * Returns true if one of the predicate's arguments
	 * is an attribute.
	 */
	private boolean hasSuperLexicalArgument() {
		if (superLexical1 != null)
			return true;
		if (superLexical2 != null)
			return true;
		return false;
	}

	//-----------------------hasLiteralArgument-----------------

	/*
	 * Returns true is one of the predicate's arguments
	 * is a literal.
	 */
	private boolean hasLiteralArgument() {
		if (literal1 != null)
			return true;
		if (literal2 != null)
			return true;
		return false;
	}

	/*
	 * Returns true if one of the predicate's arguments is
	 * a function call.
	 */
	//TODO sort out functions
	/*
	private boolean hasFunctionArgument() {
		if (argument1 instanceof Function)
			return true;
		if (argument2 instanceof Function)
			return true;
		return false;
	}
	*/

	//-----------------------isSuperLexicalEquatedToConstant-----------------

	/**
	 * Returns true if the predicate is of the from superLexicalReference=constant
	 */
	public boolean isSuperLexicalEquatedToConstant() {
		if (!(operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("like")))
			return false;
		if (hasLiteralArgument() && hasSuperLexicalArgument())
			return true;
		return false;
	}

	/**
	 * Returns true if the predicate is of the from function=constant
	 */
	//TODO sort out functions
	/*
	public boolean isFunctionEquatedToConstant() {
		if (!(operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("like")))
			return false;
		if (hasLiteralArgument() && hasFunctionArgument())
			return true;
		return false;
	}
	*/

	/**
	 * Returns true if the predicate is of the from attribute=function
	 */
	/*
	public boolean isSuperLexicalEquatedToFunction() {
		if (!(operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("like")))
			return false;
		if (hasSuperLexicalArgument() && hasFunctionArgument())
			return true;
		return false;
	}
	*/

	//-----------------------isSuperLexicalEquatedToSuperLexical-----------------

	/**
	 * Returns true if the predicate is of the from attribute=attribute
	 */
	public boolean isSuperLexicalEquatedToSuperLexical() {
		if (!(operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("like")))
			return false;
		if (!(superLexical1 instanceof SuperLexical))
			return false;
		if (!(superLexical2 instanceof SuperLexical))
			return false;
		return true;
	}

	/**
	 * Returns true if the predicate is of the from function=function
	 */
	/*
	public boolean isFunctionEquatedToFunction() {
		if (!(operator.equalsIgnoreCase("=") || operator.equalsIgnoreCase("like")))
			return false;
		if (!(argument1 instanceof Function))
			return false;
		if (!(argument2 instanceof Function))
			return false;
		return true;
	}
	*/

	//-----------------------sameArguments-----------------

	/**
	 * Returns true if the predicate is known to be true
	 * (This might be overkill! Just in case the query involves something
	 * daft...)
	 */
	//can occur if subquery refers to same element though
	public boolean sameArguments(SuperLexical arg1, SuperLexical arg2) {
		if ((arg1 == superLexical1) && (arg2 == superLexical2))
			return true;
		if ((arg2 == superLexical1) && (arg1 == superLexical2))
			return true;
		return false;
	}

	public boolean sameArguments(SuperLexical arg1, String arg2) {
		if ((arg1 == superLexical1) && (arg2 == literal2))
			return true;
		if ((arg2 == literal1) && (arg1 == superLexical2))
			return true;
		return false;
	}

	public boolean sameArguments(String arg1, SuperLexical arg2) {
		if ((arg1 == literal1) && (arg2 == superLexical2))
			return true;
		if ((arg2 == superLexical1) && (arg1 == literal2))
			return true;
		return false;
	}

	//-----------------------evaluate-----------------

	/**
	 * Evaluates the predicate on the given resultInstance.
	 * 
	 * @param resultInstance
	 * @return The result of evaluating the predicate on the resultInstance.
	 */
	public boolean evaluate(ResultInstance resultInstance) {
		logger.debug("in evaluate");
		logger.debug("resultInstance: " + resultInstance);
		ResultType resultType = resultInstance.getResultType();
		logger.debug("resultType: " + resultType);
		Map<String, ResultField> resultFields = resultType.getResultFields();
		logger.debug("superLexical1: " + superLexical1);
		logger.debug("literal1: " + literal1);
		logger.debug("operator: " + operator);
		logger.debug("superLexical2: " + superLexical2);
		logger.debug("literal2: " + literal2);

		String value1 = null;
		String value2 = null;
		DataType dataType1 = null;
		DataType dataType2 = null;

		Set<String> resultFieldNames = resultFields.keySet();
		for (String fieldName : resultFieldNames) {
			ResultField resultField = resultFields.get(fieldName);
			logger.debug("resultField: " + resultField);
			CanonicalModelConstruct cmc = resultField.getCanonicalModelConstruct();
			logger.debug("cmc: " + cmc);

			if (superLexical1 == null) {
				logger.debug("superLexical1 == null");
				value1 = literal1;
				logger.debug("value1: " + value1);
			} else if (cmc != null && cmc == superLexical1) {
				logger.debug("cmc == superLexical1");
				value1 = resultInstance.getResultValue(resultField.getFieldName()).getValue();
				dataType1 = resultField.getFieldType();
				logger.debug("value1: " + value1);
				logger.debug("dataType1: " + dataType1);
			}
			if (superLexical2 == null) {
				logger.debug("superLexical2 == null");
				value2 = literal2;
				logger.debug("value2: " + value2);
			} else if (cmc != null && cmc == superLexical2) {
				logger.debug("cmc == superLexical2");
				value2 = resultInstance.getResultValue(resultField.getFieldName()).getValue();
				dataType2 = resultField.getFieldType();
				logger.debug("value2: " + value2);
				logger.debug("dataType2: " + dataType2);
			}
		}

		DataType dataType = DataTypeMapper.getDataTypeToUse(dataType1, dataType2);
		logger.debug("dataType to use: " + dataType);

		boolean evaluateExpressionResult = evaluateExpression(dataType, value1, operator, value2);
		logger.debug("evaluateExpressionResult: " + evaluateExpressionResult);

		return evaluateExpressionResult;
	}

	//-----------------------evaluateExpression-----------------

	private boolean evaluateExpression(DataType dataType, String value1, String operator, String value2) {
		//TODO datatypes not handled here: BLOB, CHAR, ENUM, GEOCOORD, SET
		//TODO decide what to do with comparisons that aren't defined
		logger.debug("in evaluateExpression");
		logger.debug("dataType: " + dataType);
		logger.debug("value1: " + value1);
		logger.debug("operator: " + operator);
		logger.debug("value2: " + value2);
		if (operator.equals("=")) {
			logger.debug("operator: =");
			if (dataType.equals(DataType.BIGDECIMAL))
				return BigDecimal.valueOf(Double.valueOf(value1)).equals(BigDecimal.valueOf(Double.valueOf(value2)));
			if (dataType.equals(DataType.BIGINTEGER))
				return BigInteger.valueOf(Long.valueOf(value1)).equals(BigInteger.valueOf(Long.valueOf(value2)));
			if (dataType.equals(DataType.BOOLEAN))
				return Boolean.valueOf(value1).booleanValue() == Boolean.valueOf(value2).booleanValue();
			if (dataType.equals(DataType.BYTE))
				return Byte.valueOf(value1).byteValue() == Byte.valueOf(value2).byteValue();
			if (dataType.equals(DataType.DATE))
				return Date.valueOf(value1).equals(Date.valueOf(value2));
			if (dataType.equals(DataType.DOUBLE))
				return Double.valueOf(value1).doubleValue() == Double.valueOf(value2).doubleValue();
			if (dataType.equals(DataType.FLOAT))
				return Float.valueOf(value1).floatValue() == Float.valueOf(value2).doubleValue();
			if (dataType.equals(DataType.INTEGER))
				return Integer.valueOf(value1).intValue() == Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.LONG))
				return Long.valueOf(value1).longValue() == Long.valueOf(value2).longValue();
			if (dataType.equals(DataType.SERIAL))
				return Integer.valueOf(value1).intValue() == Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.SHORT))
				return Short.valueOf(value1).shortValue() == Short.valueOf(value2).shortValue();
			if (dataType.equals(DataType.STRING))
				return value1.equals(value2);
			if (dataType.equals(DataType.TIME))
				return Time.valueOf(value1).equals(Time.valueOf(value2));
			if (dataType.equals(DataType.TIMESTAMP))
				return Timestamp.valueOf(value1).equals(Timestamp.valueOf(value2));
		} else if (operator.equals("<")) {
			logger.debug("operator: <");
			if (dataType.equals(DataType.BIGDECIMAL))
				return BigDecimal.valueOf(Double.valueOf(value1)).doubleValue() < BigDecimal.valueOf(Double.valueOf(value2)).doubleValue();
			if (dataType.equals(DataType.BIGINTEGER))
				return BigInteger.valueOf(Long.valueOf(value1)).longValue() < BigInteger.valueOf(Long.valueOf(value2)).longValue();
			if (dataType.equals(DataType.BOOLEAN))
				return false;
			if (dataType.equals(DataType.BYTE))
				return Byte.valueOf(value1).byteValue() < Byte.valueOf(value2).byteValue();
			if (dataType.equals(DataType.DATE))
				return Date.valueOf(value1).before(Date.valueOf(value2));
			if (dataType.equals(DataType.DOUBLE))
				return Double.valueOf(value1).doubleValue() < Double.valueOf(value2).doubleValue();
			if (dataType.equals(DataType.FLOAT))
				return Float.valueOf(value1).floatValue() < Float.valueOf(value2).floatValue();
			if (dataType.equals(DataType.INTEGER))
				return Integer.valueOf(value1).intValue() < Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.LONG))
				return Long.valueOf(value1).longValue() < Long.valueOf(value2).longValue();
			if (dataType.equals(DataType.SERIAL))
				return Integer.valueOf(value1).intValue() < Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.SHORT))
				return Short.valueOf(value1).shortValue() < Short.valueOf(value2).shortValue();
			if (dataType.equals(DataType.STRING))
				return false;
			if (dataType.equals(DataType.TIME))
				return Time.valueOf(value1).before(Time.valueOf(value2));
			if (dataType.equals(DataType.TIMESTAMP))
				return Timestamp.valueOf(value1).before(Timestamp.valueOf(value2));
		} else if (operator.equals("<=")) {
			logger.debug("operator: <=");
			if (dataType.equals(DataType.BIGDECIMAL))
				return BigDecimal.valueOf(Double.valueOf(value1)).doubleValue() <= BigDecimal.valueOf(Double.valueOf(value2)).doubleValue();
			if (dataType.equals(DataType.BIGINTEGER))
				return BigInteger.valueOf(Long.valueOf(value1)).longValue() <= BigInteger.valueOf(Long.valueOf(value2)).longValue();
			if (dataType.equals(DataType.BOOLEAN))
				return false;
			if (dataType.equals(DataType.BYTE))
				return Byte.valueOf(value1).byteValue() <= Byte.valueOf(value2).byteValue();
			if (dataType.equals(DataType.DATE))
				return (Date.valueOf(value1).before(Date.valueOf(value2)) || Date.valueOf(value1).equals(Date.valueOf(value2)));
			if (dataType.equals(DataType.DOUBLE))
				return Double.valueOf(value1).doubleValue() <= Double.valueOf(value2).doubleValue();
			if (dataType.equals(DataType.FLOAT))
				return Float.valueOf(value1).floatValue() <= Float.valueOf(value2).floatValue();
			if (dataType.equals(DataType.INTEGER))
				return Integer.valueOf(value1).intValue() <= Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.LONG))
				return Long.valueOf(value1).longValue() <= Long.valueOf(value2).longValue();
			if (dataType.equals(DataType.SERIAL))
				return Integer.valueOf(value1).intValue() <= Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.SHORT))
				return Short.valueOf(value1).shortValue() <= Short.valueOf(value2).shortValue();
			if (dataType.equals(DataType.STRING))
				return false;
			if (dataType.equals(DataType.TIME))
				return (Time.valueOf(value1).before(Time.valueOf(value2)) || Time.valueOf(value1).equals(Time.valueOf(value2)));
			if (dataType.equals(DataType.TIMESTAMP))
				return (Timestamp.valueOf(value1).before(Timestamp.valueOf(value2)) || Timestamp.valueOf(value1).equals(Timestamp.valueOf(value2)));
		} else if (operator.equals(">")) {
			logger.debug("operator: >");
			if (dataType.equals(DataType.BIGDECIMAL))
				return BigDecimal.valueOf(Double.valueOf(value1)).doubleValue() > BigDecimal.valueOf(Double.valueOf(value2)).doubleValue();
			if (dataType.equals(DataType.BIGINTEGER))
				return BigInteger.valueOf(Long.valueOf(value1)).longValue() > BigInteger.valueOf(Long.valueOf(value2)).longValue();
			if (dataType.equals(DataType.BOOLEAN))
				return false;
			if (dataType.equals(DataType.BYTE))
				return Byte.valueOf(value1).byteValue() > Byte.valueOf(value2).byteValue();
			if (dataType.equals(DataType.DATE))
				return Date.valueOf(value1).after(Date.valueOf(value2));
			if (dataType.equals(DataType.DOUBLE))
				return Double.valueOf(value1).doubleValue() > Double.valueOf(value2).doubleValue();
			if (dataType.equals(DataType.FLOAT))
				return Float.valueOf(value1).floatValue() > Float.valueOf(value2).floatValue();
			if (dataType.equals(DataType.INTEGER))
				return Integer.valueOf(value1).intValue() > Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.LONG))
				return Long.valueOf(value1).longValue() > Long.valueOf(value2).longValue();
			if (dataType.equals(DataType.SERIAL))
				return Integer.valueOf(value1).intValue() > Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.SHORT))
				return Short.valueOf(value1).shortValue() > Short.valueOf(value2).shortValue();
			if (dataType.equals(DataType.STRING))
				return false;
			if (dataType.equals(DataType.TIME))
				return Time.valueOf(value1).after(Time.valueOf(value2));
			if (dataType.equals(DataType.TIMESTAMP))
				return Timestamp.valueOf(value1).after(Timestamp.valueOf(value2));
		} else if (operator.equals(">=")) {
			logger.debug("operator: >=");
			if (dataType.equals(DataType.BIGDECIMAL))
				return BigDecimal.valueOf(Double.valueOf(value1)).doubleValue() >= BigDecimal.valueOf(Double.valueOf(value2)).doubleValue();
			if (dataType.equals(DataType.BIGINTEGER))
				return BigInteger.valueOf(Long.valueOf(value1)).longValue() >= BigInteger.valueOf(Long.valueOf(value2)).longValue();
			if (dataType.equals(DataType.BOOLEAN))
				return false;
			if (dataType.equals(DataType.BYTE))
				return Byte.valueOf(value1).byteValue() >= Byte.valueOf(value2).byteValue();
			if (dataType.equals(DataType.DATE))
				return (Date.valueOf(value1).after(Date.valueOf(value2)) || Date.valueOf(value1).equals(Date.valueOf(value2)));
			if (dataType.equals(DataType.DOUBLE))
				return Double.parseDouble(value1) >= Double.parseDouble(value2);
			if (dataType.equals(DataType.FLOAT))
				return Float.parseFloat(value1) >= Float.parseFloat(value2);
			if (dataType.equals(DataType.INTEGER))
				return Integer.parseInt(value1) >= Integer.parseInt(value2);
			if (dataType.equals(DataType.LONG))
				return Long.parseLong(value1) >= Long.parseLong(value2);
			if (dataType.equals(DataType.SERIAL))
				return Integer.parseInt(value1) >= Integer.parseInt(value2);
			if (dataType.equals(DataType.SHORT))
				return Short.parseShort(value1) >= Short.parseShort(value2);
			if (dataType.equals(DataType.STRING))
				return false;
			if (dataType.equals(DataType.TIME))
				return (Time.valueOf(value1).after(Time.valueOf(value2)) || Time.valueOf(value1).equals(Time.valueOf(value2)));
			if (dataType.equals(DataType.TIMESTAMP))
				return (Timestamp.valueOf(value1).after(Timestamp.valueOf(value2)) || Timestamp.valueOf(value1).equals(Timestamp.valueOf(value2)));
		} else if (operator.equals("<>") || (operator.equals("!="))) {
			logger.debug("operator: <>, !=");
			if (dataType.equals(DataType.BIGDECIMAL))
				return BigDecimal.valueOf(Double.valueOf(value1)).doubleValue() != BigDecimal.valueOf(Double.valueOf(value2)).doubleValue();
			if (dataType.equals(DataType.BIGINTEGER))
				return BigInteger.valueOf(Long.valueOf(value1)).longValue() != BigInteger.valueOf(Long.valueOf(value2)).longValue();
			if (dataType.equals(DataType.BOOLEAN))
				return Boolean.valueOf(value1).booleanValue() != Boolean.valueOf(value2).booleanValue();
			if (dataType.equals(DataType.BYTE))
				return Byte.valueOf(value1).byteValue() != Byte.valueOf(value2).byteValue();
			if (dataType.equals(DataType.DATE))
				return (Date.valueOf(value1).before(Date.valueOf(value2)) || Date.valueOf(value1).after(Date.valueOf(value2)));
			if (dataType.equals(DataType.DOUBLE))
				return Double.valueOf(value1).doubleValue() != Double.valueOf(value2).doubleValue();
			if (dataType.equals(DataType.FLOAT))
				return Float.valueOf(value1).floatValue() != Float.valueOf(value2).floatValue();
			if (dataType.equals(DataType.INTEGER))
				return Integer.valueOf(value1).intValue() != Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.LONG))
				return Long.valueOf(value1).longValue() != Long.valueOf(value2).longValue();
			if (dataType.equals(DataType.SERIAL))
				return Integer.valueOf(value1).intValue() != Integer.valueOf(value2).intValue();
			if (dataType.equals(DataType.SHORT))
				return Short.valueOf(value1).shortValue() != Short.valueOf(value2).shortValue();
			if (dataType.equals(DataType.STRING))
				return value1 != value2;
			if (dataType.equals(DataType.TIME))
				return (Time.valueOf(value1).before(Time.valueOf(value2)) || Time.valueOf(value1).after(Time.valueOf(value2)));
			if (dataType.equals(DataType.TIMESTAMP))
				return (Timestamp.valueOf(value1).before(Timestamp.valueOf(value2)) || Timestamp.valueOf(value1).after(Timestamp.valueOf(value2)));
		} else if (operator.equals("like")) {
			logger.debug("operator: like");
			if (dataType.equals(DataType.STRING))
				return (value1.contains(value2) || value2.contains(value2) || value1.equalsIgnoreCase(value2));
			else
				return false;
		}
		logger.error("missed something in evaluate - TODO check this");
		logger.debug("value1: " + value1);
		logger.debug("value2: " + value2);
		logger.debug("operator: " + operator);
		logger.debug("dataType1: " + dataType);

		return false;
	}

	//-----------------------superLexical1-----------------

	/**
	 * @return the superLexical1
	 */
	public SuperLexical getSuperLexical1() {
		return superLexical1;
	}

	/**
	 * @param superLexical1 the superLexical1 to set
	 */
	public void setSuperLexical1(SuperLexical superLexical1) {
		this.superLexical1 = superLexical1;
	}

	//-----------------------superLexical2-----------------

	/**
	 * @return the superLexical2
	 */
	public SuperLexical getSuperLexical2() {
		return superLexical2;
	}

	/**
	 * @param superLexical2 the superLexical2 to set
	 */
	public void setSuperLexical2(SuperLexical superLexical2) {
		this.superLexical2 = superLexical2;
	}

	//-----------------------literal1-----------------

	/**
	 * @return the literal1
	 */
	public String getLiteral1() {
		return literal1;
	}

	/**
	 * @param literal1 the literal1 to set
	 */
	public void setLiteral1(String literal1) {
		this.literal1 = literal1;
	}

	//-----------------------literal2-----------------

	/**
	 * @return the literal2
	 */
	public String getLiteral2() {
		return literal2;
	}

	/**
	 * @param literal2 the literal2 to set
	 */
	public void setLiteral2(String literal2) {
		this.literal2 = literal2;
	}

	//-----------------------operator-----------------

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((andOr == null) ? 0 : andOr.hashCode());
		result = prime * result + ((literal1 == null) ? 0 : literal1.hashCode());
		result = prime * result + ((literal2 == null) ? 0 : literal2.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((superLexical1 == null) ? 0 : superLexical1.hashCode());
		result = prime * result + ((superLexical2 == null) ? 0 : superLexical2.hashCode());
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
		Predicate other = (Predicate) obj;
		if (andOr == null) {
			if (other.andOr != null)
				return false;
		} else if (!andOr.equals(other.andOr))
			return false;
		if (literal1 == null) {
			if (other.literal1 != null)
				return false;
		} else if (!literal1.equals(other.literal1))
			return false;
		if (literal2 == null) {
			if (other.literal2 != null)
				return false;
		} else if (!literal2.equals(other.literal2))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (superLexical1 == null) {
			if (other.superLexical1 != null)
				return false;
		} else if (!superLexical1.equals(other.superLexical1))
			return false;
		if (superLexical2 == null) {
			if (other.superLexical2 != null)
				return false;
		} else if (!superLexical2.equals(other.superLexical2))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Predicate [");
		if (andOr != null)
			builder.append("andOr=").append(andOr).append(", ");
		if (literal1 != null)
			builder.append("literal1=").append(literal1).append(", ");
		if (superLexical1 != null)
			builder.append("superLexical1=").append(superLexical1.getName()).append(", ");
		if (operator != null)
			builder.append("operator=").append(operator).append(", ");
		if (literal2 != null)
			builder.append("literal2=").append(literal2).append(", ");
		if (superLexical2 != null)
			builder.append("superLexical2=").append(superLexical2.getName());
		builder.append("]");
		return builder.toString();
	}
}
