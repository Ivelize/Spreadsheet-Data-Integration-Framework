package uk.ac.manchester.dstoolkit.domain.models.canonical;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.exceptions.TypeMappingException;

public class DataTypeMapper {

	protected static Logger logger = Logger.getLogger(DataTypeMapper.class);

	//TODO move this class somewhere else - still need to decide where though, which layer it goes - service ---
	//TODO refactor

	/**
	 * Type checked abstract syntax tree node category
	 */
	/*
	public static final int QUERY = 5;
	public static final int SELECT_LIST = 8;
	public static final int FROM_LIST = 9;
	public static final int CONDITION = 4; //i.e. WHERE_CLAUSE or AND
	public static final int AND = 5;
	public static final int PREDICATE = 6;
	public static final int IN = 7;
	public static final int AGGREGATE = 8;
	public static final int COUNT = 9;
	public static final int UNION = 25;
	public static final int ORDER_BY = 11;
	public static final int ASC = 32;
	public static final int DESC = 33;
	public static final int LITERAL = 1001;
	public static final int SUPER_LEXICAL = 14;
	public static final int FUNCTION = 16; 
	public static final int SUPER_ABSTRACT = 13;   
	*/
	/*
	RIGHT_OUTER_JOIN=20
	SUPER_LEXICAL_OF_SUPER_ABSTRACT=19
	INTERSECT_ALL=30
	ORDER=7     
	NUMERIC=37
	INTERSECT=27   
	STRING=38
	BOUND=31
	SQL_STATEMENT=4
	SETOP=6
	UNION_ALL=28
	HAVING=12
	SET=18
	WHERE=10
	SUPER_RELATIONSHIP=15
	GROUP_BY=11
	FLOAT=36
	EXCEPT=26
	LEFT_OUTER_JOIN=21  
	IS_NULL=24
	INT=35
	JOIN=23   
	ID=34
	EXCEPT_ALL=29
	FULL_OUTER_JOIN=22   
	NOT=17
	*/

	//TODO SQL data type text is completely separate now and only taken into account in mapSQLTypeToDataType reason: had to add this as can't get numberOfDistinctValues for it, same for image

	/**
	 * Java number type category
	 */
	public static final Class[] numberTypes = { Long.class, Float.class, Double.class, BigDecimal.class, Byte.class, Short.class, Integer.class,
			BigInteger.class };

	public static final Class[] integerTypes = { Byte.class, Short.class, Integer.class, Long.class, BigInteger.class };

	public static final Class[] realTypes = { Float.class, Double.class, BigDecimal.class };

	/**
	 * Java time/date category
	 */
	public static final Class[] timeDateTypes = { Date.class, Time.class, Timestamp.class };

	/**
	 * Java string category
	 */
	public static final Class[] stringTypes = { Character.class, String.class };

	/**
	 * Returns true if the first type is castable as the second type
	 */
	public static boolean castableAs(Class arg1, Class arg2) {
		if (arg1 == arg2)
			return true;
		if (isNumberType(arg1) && isNumberType(arg2))
			return true;
		if (isNumberType(arg1) && (arg2 == String.class))
			return true;
		if (isTimeDateType(arg1) && isTimeDateType(arg2))
			return true;
		if (isTimeDateType(arg1) && (arg2 == String.class))
			return true;
		if (isStringType(arg1) && (isStringType(arg2)))
			return true;
		return false;
	}

	public static DataType getDataTypeToUse(DataType dataType1, DataType dataType2) {
		if (dataType1 == null) {
			logger.debug("dataType1 == null");
			if (dataType2 == null)
				logger.error("both dataTypes are null - TODO check this");
			else
				return dataType2;
		} else if (dataType2 == null) {
			logger.debug("dataType2 == null");
			return dataType1;
		} else if (dataType1.equals(dataType2)) {
			logger.debug("dataTypes are the same");
			return dataType1;
		} else {
			logger.debug("dataTypes are different, decide which one to use");
			logger.debug("dataType1: " + dataType1);
			logger.debug("dataType2: " + dataType2);

			Class dataType1Class = DataTypeMapper.mapDataTypeToClassOfType(dataType1);
			Class dataType2Class = DataTypeMapper.mapDataTypeToClassOfType(dataType2);

			boolean isCastable = DataTypeMapper.castableAs(dataType1Class, dataType2Class);
			if (!isCastable) {
				logger.error("dataTypes aren't castable - TODO check this");
				logger.debug("dataType1: " + dataType1);
				logger.debug("dataType2: " + dataType2);
			} else {
				//TODO check this here, it shouldn't get here and something seems wrong anyway; this shouldn't be the same datatypes for both
				if (dataType1.equals(DataType.STRING) || dataType2.equals(DataType.STRING))
					return DataType.STRING;

				if (dataType1.equals(DataType.BIGDECIMAL) || dataType2.equals(DataType.BIGDECIMAL))
					return DataType.BIGDECIMAL;
				if (dataType1.equals(DataType.DOUBLE) || dataType2.equals(DataType.DOUBLE))
					return DataType.DOUBLE;
				if (dataType1.equals(DataType.FLOAT) || dataType2.equals(DataType.FLOAT))
					return DataType.FLOAT;

				if (dataType1.equals(DataType.BIGINTEGER) || dataType2.equals(DataType.BIGINTEGER))
					return DataType.BIGINTEGER;
				if (dataType1.equals(DataType.LONG) || dataType2.equals(DataType.LONG))
					return DataType.LONG;
				if (dataType1.equals(DataType.INTEGER) || dataType2.equals(DataType.INTEGER))
					return DataType.INTEGER;
				if (dataType1.equals(DataType.SHORT) || dataType2.equals(DataType.SHORT))
					return DataType.SHORT;
				if (dataType1.equals(DataType.BYTE) || dataType2.equals(DataType.BYTE))
					return DataType.BYTE;

				if (dataType1.equals(DataType.DATE) || dataType2.equals(DataType.DATE))
					return DataType.DATE;
				if (dataType1.equals(DataType.TIME) || dataType2.equals(DataType.TIME))
					return DataType.TIME;
				if (dataType1.equals(DataType.TIMESTAMP) || dataType2.equals(DataType.TIMESTAMP))
					return DataType.TIMESTAMP;
			}
		}
		return null;
	}

	/*
	 * Returns true if the type is a number type
	 */
	private static boolean isStringType(Class c) {
		for (Class stringType : numberTypes)
			if (stringType == c)
				return true;
		return false;
	}

	public static boolean isStringType(DataType dataType) {
		Class c = DataTypeMapper.mapDataTypeToClassOfType(dataType);
		return DataTypeMapper.isStringType(c);
	}

	/*
	 * Returns true if the type is a number type
	 */
	private static boolean isNumberType(Class c) {
		for (Class numberType : numberTypes)
			if (numberType == c)
				return true;
		return false;
	}

	public static boolean isNumberType(DataType dataType) {
		Class c = DataTypeMapper.mapDataTypeToClassOfType(dataType);
		return DataTypeMapper.isNumberType(c);
	}

	private static boolean isIntegerType(Class c) {
		for (Class numberType : integerTypes)
			if (numberType == c)
				return true;
		return false;
	}

	public static boolean isIntegerType(DataType dataType) {
		Class c = DataTypeMapper.mapDataTypeToClassOfType(dataType);
		return DataTypeMapper.isIntegerType(c);
	}

	private static boolean isRealType(Class c) {
		for (Class numberType : realTypes)
			if (numberType == c)
				return true;
		return false;
	}

	public static boolean isRealType(DataType dataType) {
		Class c = DataTypeMapper.mapDataTypeToClassOfType(dataType);
		return DataTypeMapper.isRealType(c);
	}

	/*
	 * Returns true if the type is a time/date/timestamp type
	 */
	private static boolean isTimeDateType(Class c) {
		for (Class timeDateType : timeDateTypes)
			if (timeDateType == c)
				return true;
		return false;
	}

	public static boolean isTimeDateType(DataType dataType) {
		Class c = DataTypeMapper.mapDataTypeToClassOfType(dataType);
		return DataTypeMapper.isTimeDateType(c);
	}

	public static DataType mapClassOfTypeToDataType(Class type) throws TypeMappingException {
		if (type.equals(String.class))
			return DataType.STRING;
		if (type.equals(Character.class))
			return DataType.CHAR;
		if (type.equals(Boolean.class))
			return DataType.BOOLEAN;
		if (type.equals(Double.class))
			return DataType.DOUBLE;
		if (type.equals(Float.class))
			return DataType.FLOAT;
		if (type.equals(Integer.class))
			return DataType.INTEGER;
		if (type.equals(Byte.class))
			return DataType.BYTE;
		if (type.equals(Long.class))
			return DataType.LONG;
		if (type.equals(Short.class))
			return DataType.SHORT;
		if (type.equals(BigDecimal.class))
			return DataType.BIGDECIMAL;
		if (type.equals(Date.class))
			return DataType.DATE;
		if (type.equals(Time.class))
			return DataType.TIME;
		if (type.equals(Timestamp.class))
			return DataType.TIMESTAMP;
		throw new TypeMappingException("No type found for class type: " + type);
	}

	public static Class mapDataTypeToClassOfType(DataType type) throws TypeMappingException {
		if (type.equals(DataType.STRING))
			return String.class;
		if (type.equals(DataType.CHAR))
			return Character.class;
		if (type.equals(DataType.BOOLEAN))
			return Boolean.class;
		if (type.equals(DataType.DOUBLE))
			return Double.class;
		if (type.equals(DataType.FLOAT))
			return Float.class;
		if (type.equals(DataType.INTEGER))
			return Integer.class;
		if (type.equals(DataType.BYTE))
			return Byte.class;
		if (type.equals(DataType.LONG))
			return Long.class;
		if (type.equals(DataType.SHORT))
			return Short.class;
		if (type.equals(DataType.BIGDECIMAL))
			return BigDecimal.class;
		if (type.equals(DataType.DATE))
			return Date.class;
		if (type.equals(DataType.TIME))
			return Time.class;
		if (type.equals(DataType.TIMESTAMP))
			return Timestamp.class;
		if (type.equals(DataType.SERIAL))
			return Integer.class;
		throw new TypeMappingException("No type found for class type: " + type);
	}

	public static DataType mapXSDTypeToDataType(String xsdType) throws TypeMappingException {
		if (xsdType.toLowerCase().contains("decimal"))
			return DataType.BIGDECIMAL;
		if (xsdType.toLowerCase().contains("integer"))
			return DataType.INTEGER;
		if (xsdType.toLowerCase().contains("int"))
			return DataType.INTEGER;
		if (xsdType.toLowerCase().contains("byte"))
			return DataType.BYTE;
		if (xsdType.toLowerCase().contains("short"))
			return DataType.SHORT;
		if (xsdType.toLowerCase().contains("long"))
			return DataType.LONG;
		if (xsdType.toLowerCase().contains("string"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("token"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("idrefs"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("idref"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("id"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("language"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("name"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("base64binary"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("hexbinary"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("anyuri"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("entity"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("entities"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("notation"))
			return DataType.STRING;
		if (xsdType.toLowerCase().contains("date"))
			return DataType.DATE;
		if (xsdType.toLowerCase().contains("time"))
			return DataType.TIME;
		if (xsdType.toLowerCase().contains("double"))
			return DataType.DOUBLE;
		if (xsdType.toLowerCase().contains("float"))
			return DataType.FLOAT;
		if (xsdType.toLowerCase().contains("boolean"))
			return DataType.BOOLEAN;
		throw new TypeMappingException("No type found for XSD type: " + xsdType);
	}

	public static DataType mapSQLTypeToDataType(String sqlType) throws TypeMappingException {
		if (sqlType.toLowerCase().contains("varchar"))
			return DataType.STRING;
		if (sqlType.toLowerCase().contains("text"))
			return DataType.TEXT;
		if (sqlType.toLowerCase().contains("longvarchar"))
			return DataType.STRING;
		if (sqlType.toLowerCase().contains("raw"))
			return DataType.STRING;
		if (sqlType.toLowerCase().contains("char"))
			return DataType.CHAR;
		if (sqlType.toLowerCase().contains("real"))
			return DataType.DOUBLE;
		if (sqlType.toLowerCase().contains("double"))
			return DataType.DOUBLE;
		if (sqlType.toLowerCase().contains("float"))
			return DataType.FLOAT;
		if (sqlType.toLowerCase().contains("money"))
			return DataType.FLOAT;
		if (sqlType.toLowerCase().contains("numeric"))
			return DataType.BIGDECIMAL;
		if (sqlType.toLowerCase().contains("number"))
			return DataType.BIGDECIMAL;
		if (sqlType.toLowerCase().contains("decimal"))
			return DataType.BIGDECIMAL;
		if (sqlType.toLowerCase().contains("long"))
			return DataType.LONG;
		if (sqlType.toLowerCase().contains("integer"))
			return DataType.INTEGER;
		if (sqlType.toLowerCase().contains("bigint"))
			return DataType.INTEGER;
		if (sqlType.toLowerCase().contains("tinyint"))
			return DataType.INTEGER;
		if (sqlType.toLowerCase().contains("smallint"))
			return DataType.INTEGER;
		if (sqlType.toLowerCase().contains("int"))
			return DataType.INTEGER;
		if (sqlType.toLowerCase().contains("bit"))
			return DataType.BOOLEAN;
		if (sqlType.toLowerCase().contains("boolean"))
			return DataType.BOOLEAN;
		if (sqlType.toLowerCase().contains("bool"))
			return DataType.BOOLEAN;
		if (sqlType.toLowerCase().contains("date"))
			return DataType.DATE;
		/* Both time and timestamp map to DateTime */
		if (sqlType.toLowerCase().contains("timestamp"))
			return DataType.TIMESTAMP;
		if (sqlType.toLowerCase().contains("time"))
			return DataType.TIME;
		if (sqlType.toLowerCase().contains("geocoord"))
			return DataType.GEOCOORD;
		if (sqlType.toLowerCase().contains("serial"))
			return DataType.SERIAL;
		//TODOsort the following types out properly
		if (sqlType.toLowerCase().contains("enum"))
			return DataType.STRING;
		if (sqlType.toLowerCase().contains("set"))
			return DataType.SET;
		if (sqlType.toLowerCase().contains("blob"))
			return DataType.BLOB;
		if (sqlType.toLowerCase().contains("clob"))
			return DataType.CLOB;
		if (sqlType.toLowerCase().contains("sysname"))
			return DataType.SYSNAME;
		if (sqlType.toLowerCase().contains("varbinary"))
			return DataType.BLOB;
		if (sqlType.toLowerCase().contains("image"))
			return DataType.IMAGE;
		if (sqlType.toLowerCase().contains("rowid"))
			return DataType.ROWID;
		if (sqlType.toLowerCase().contains("sdo_geometry"))
			return DataType.SDO_GEOMETRY;
		if (sqlType.toLowerCase().contains("xmltype"))
			return DataType.XMLTYPE;
		throw new TypeMappingException("No type found for SQL type: " + sqlType);
	}

	public static Class mapXSDTypeToType(String xsdType) throws TypeMappingException {
		if (xsdType.toLowerCase().contains("decimal"))
			return BigDecimal.class;
		if (xsdType.toLowerCase().contains("integer"))
			return Integer.class;
		if (xsdType.toLowerCase().contains("int"))
			return Integer.class;
		if (xsdType.toLowerCase().contains("byte"))
			return Byte.class;
		if (xsdType.toLowerCase().contains("short"))
			return Short.class;
		if (xsdType.toLowerCase().contains("long"))
			return Long.class;
		if (xsdType.toLowerCase().contains("string"))
			return String.class;
		if (xsdType.toLowerCase().contains("token"))
			return String.class;
		if (xsdType.toLowerCase().contains("idrefs"))
			return String.class;
		if (xsdType.toLowerCase().contains("idref"))
			return String.class;
		if (xsdType.toLowerCase().contains("id"))
			return String.class;
		if (xsdType.toLowerCase().contains("language"))
			return String.class;
		if (xsdType.toLowerCase().contains("name"))
			return String.class;
		if (xsdType.toLowerCase().contains("base64binary"))
			return String.class;
		if (xsdType.toLowerCase().contains("hexbinary"))
			return String.class;
		if (xsdType.toLowerCase().contains("anyURI"))
			return String.class;
		if (xsdType.toLowerCase().contains("entity"))
			return String.class;
		if (xsdType.toLowerCase().contains("entities"))
			return String.class;
		if (xsdType.toLowerCase().contains("notation"))
			return String.class;
		if (xsdType.toLowerCase().contains("date"))
			return Date.class;
		if (xsdType.toLowerCase().contains("time"))
			return Time.class;
		if (xsdType.toLowerCase().contains("double"))
			return Double.class;
		if (xsdType.toLowerCase().contains("float"))
			return Float.class;
		if (xsdType.toLowerCase().contains("boolean"))
			return Boolean.class;
		throw new TypeMappingException("No type found for XSD type: " + xsdType);
	}

	public static Class mapSQLTypeToType(String sqlType) throws TypeMappingException {
		if (sqlType.equalsIgnoreCase("varchar"))
			return String.class;
		if (sqlType.equalsIgnoreCase("text"))
			return String.class;
		if (sqlType.equalsIgnoreCase("char"))
			return String.class;
		if (sqlType.equalsIgnoreCase("longvarchar"))
			return String.class;
		if (sqlType.equalsIgnoreCase("real"))
			return Double.class;
		if (sqlType.equalsIgnoreCase("double"))
			return Double.class;
		if (sqlType.equalsIgnoreCase("float"))
			return Double.class;
		if (sqlType.equalsIgnoreCase("numeric"))
			return BigDecimal.class;
		if (sqlType.equalsIgnoreCase("decimal"))
			return BigDecimal.class;
		if (sqlType.equalsIgnoreCase("integer"))
			return Integer.class;
		if (sqlType.equalsIgnoreCase("bigint"))
			return Integer.class;
		if (sqlType.equalsIgnoreCase("tinyint"))
			return Integer.class;
		if (sqlType.equalsIgnoreCase("int"))
			return Integer.class;
		if (sqlType.equalsIgnoreCase("smallint"))
			return Integer.class;
		if (sqlType.equalsIgnoreCase("bit"))
			return Boolean.class;
		if (sqlType.equalsIgnoreCase("boolean"))
			return Boolean.class;
		if (sqlType.equalsIgnoreCase("date"))
			return Date.class;
		/* Both time and timestamp map to DateTime */
		if (sqlType.equalsIgnoreCase("time"))
			return Timestamp.class;
		if (sqlType.equalsIgnoreCase("timestamp"))
			return Timestamp.class;
		throw new TypeMappingException("No type found for SQL type: " + sqlType);
	}

	public static Class userDefinedFunctionTypeToJavaType(String type) throws TypeMappingException {
		if (type.equalsIgnoreCase("string"))
			return String.class;
		if (type.equalsIgnoreCase("java.lang.string"))
			return String.class;
		if (type.equalsIgnoreCase("float"))
			return Float.class;
		if (type.equalsIgnoreCase("java.lang.float"))
			return Float.class;
		if (type.equalsIgnoreCase("double"))
			return Double.class;
		if (type.equalsIgnoreCase("java.lang.double"))
			return Double.class;
		if (type.equalsIgnoreCase("long"))
			return Long.class;
		if (type.equalsIgnoreCase("java.lang.long"))
			return Long.class;
		if (type.equalsIgnoreCase("int"))
			return Integer.class;
		if (type.equalsIgnoreCase("integer"))
			return Integer.class;
		if (type.equalsIgnoreCase("java.lang.integer"))
			return Integer.class;
		if (type.equalsIgnoreCase("date"))
			return Date.class;
		if (type.equalsIgnoreCase("java.sql.date"))
			return Date.class;
		if (type.equalsIgnoreCase("time"))
			return Time.class;
		if (type.equalsIgnoreCase("java.sql.time"))
			return Time.class;
		if (type.equalsIgnoreCase("timestamp"))
			return Timestamp.class;
		if (type.equalsIgnoreCase("java.sql.timestamp"))
			return Timestamp.class;
		throw new TypeMappingException("Type " + type + " not supported as a user defined function type");
	}

}
