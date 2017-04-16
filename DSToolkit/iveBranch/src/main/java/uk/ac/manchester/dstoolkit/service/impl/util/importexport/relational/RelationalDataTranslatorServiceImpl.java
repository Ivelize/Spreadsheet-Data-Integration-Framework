package uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultValue;

/**
 * @author chedeler
 *
 */

//@Transactional(readOnly = true)
@Service(value = "relationalDataTranslatorServiceImpl")
public class RelationalDataTranslatorServiceImpl {

	private static Logger logger = Logger.getLogger(RelationalDataTranslatorServiceImpl.class);

	private boolean reachedEof = false;

	//TODO refactor to separate resultType stuff etc
	//TODO add interface
	//TODO check what needs resetting before re-use

	public List<ResultInstance> translateResultSetIntoListOfResultInstances(SqlRowSet resultSet, ResultType resultType) {
		//boolean isFirstElementInResultSet = true;
		logger.debug("in translateResultSetIntoListOfResultInstances");
		logger.debug("resultType: " + resultType);
		logger.debug("resultType.resultFields: " + resultType.getResultFields());
		List<ResultInstance> resultInstances = new LinkedList<ResultInstance>();

		while (resultSet.next()) {
			logger.debug("next element from resultSet");
			int columnCount = resultSet.getMetaData().getColumnCount();
			ResultInstance resultInstance = new ResultInstance();
			//ResultType resultType = null;
			//if (isFirstElementInResultSet) {
			//	resultType = new ResultType();
			//}
			for (int i = 1; i <= columnCount; i++) {
				logger.debug("i: " + i);
				logger.debug("columnCount: " + columnCount);
				String columnLabel = resultSet.getMetaData().getColumnLabel(i);
				logger.debug("columnLabel: " + columnLabel);
				String columnName = resultSet.getMetaData().getColumnName(i);
				logger.debug("columnName: " + columnName);
				String columnClassName = resultSet.getMetaData().getColumnClassName(i);
				logger.debug("columnClassName: " + columnClassName);
				String catalogName = resultSet.getMetaData().getCatalogName(i);
				logger.debug("catalogName: " + catalogName);
				String tableName = resultSet.getMetaData().getTableName(i);
				logger.debug("tableName: " + tableName);
				String schemaName = resultSet.getMetaData().getSchemaName(i);
				logger.debug("schemaName: " + schemaName);

				String fullColumnLabel = tableName + "." + columnLabel; //TODO this assumes it's always the fully qualified name ... not sure what happens when variableName are used - check this
				logger.debug("fullColumnLabel: " + fullColumnLabel);

				/*
				if (isFirstElementInResultSet) {
					String columnName = resultSet.getMetaData().getColumnName(i);
					String columnType = resultSet.getMetaData().getColumnTypeName(i);
					logger.debug("columnName: " + columnName);
					logger.debug("columnType: " + columnType);
					DataType dataType = DataTypeMapper.mapSQLTypeToDataType(columnType);
					ResultField resultField = new ResultField(columnLabel, dataType);
					logger.debug("added new resultField: " + resultField);
					resultType.getResultFields().add(resultField);
				}
				*/
				String resultValue = resultSet.getString(i);
				logger.debug("resultValue: " + resultValue);
				if (resultValue != null) {
					if (resultValue.contains("\n")) {
						logger.debug("resultValue contains new line");
						StringBuilder resultValueWithoutNewLines = new StringBuilder();
						String[] lines = resultValue.split("\n");
						for (String line : lines) {
							logger.debug("line: " + line);
							resultValueWithoutNewLines.append(line.trim());
							resultValueWithoutNewLines.append(" ");
						}
						logger.debug("resultValueWithoutNewLines: " + resultValueWithoutNewLines);
						resultValue = resultValueWithoutNewLines.toString().trim();
					}
					logger.debug("resultValue: " + resultValue);
				}
				/*
				if (resultValue != null) {
					if (resultValue.length() > 250) {
						logger.debug("resultValue too long");
						resultValue = resultValue.substring(0, 250);
						logger.debug("concatenated resultValue: " + resultValue);
					}
				}
				*/
				//ResultValue value = new ResultValue(resultType.getResultFields().get(i - 1), resultValue);
				//resultInstance.getResultFieldNameResultValueMap().put(resultType.getResultFields().get(i - 1).getFieldName(), value);

				//logger.debug("i: " + i);

				int position = resultType.getPosition(fullColumnLabel);
				logger.debug("position: " + position);

				String columnNameWithoutDot = "";
				if (fullColumnLabel.contains("."))
					columnNameWithoutDot = fullColumnLabel.substring(fullColumnLabel.indexOf(".") + 1);
				else
					columnNameWithoutDot = fullColumnLabel;

				logger.debug("columnNameWithoutDot: " + columnNameWithoutDot);

				//TODO decide what to do if it can't find it, add proper error handling, shouldn't happen though

				logger.debug("resultType.getPosition(fullColumnName), position: " + position);
				if (position > -1) {
					ResultField fieldAtPosition = resultType.getResultFieldAtPosition(position);
					if (fieldAtPosition != null) {
						String fieldNameAtPosition = fieldAtPosition.getFieldName();
						logger.debug("fieldNameAtPosition: " + fieldNameAtPosition);
					}
				} else
					logger.error("didn't find fullColumnLabel in resultType");

				//TODO check the stuff below, comment and tidy up

				String fieldNameAtIMinus1 = "";
				String fieldNameAtIndexOfIMinus1 = "";

				logger.debug("i - 1: " + (i - 1));
				ResultField fieldAtIMinus1 = resultType.getResultFieldAtPosition(i - 1);
				if (fieldAtIMinus1 != null) {
					logger.debug("resultType.getResultFieldAtPosition(i - 1): " + fieldAtIMinus1.getFieldName());
					fieldNameAtIMinus1 = resultType.getResultFieldAtPosition(i - 1).getFieldName();
					logger.debug("fieldNameAtIMinus1: " + fieldNameAtIMinus1);
				}

				ResultField fieldAtIndexOfIMinus1 = resultType.getResultFieldWithIndex(i - 1);
				if (fieldAtIndexOfIMinus1 != null) {
					fieldNameAtIndexOfIMinus1 = fieldAtIndexOfIMinus1.getFieldName();
					logger.debug("fieldNameAtIndexOfIMinus1: " + fieldNameAtIndexOfIMinus1);
				} else {
					logger.error("didn't find resultField at index of i-1 "); //TODO proper error handling here
				}
				//ResultValue value = new ResultValue(resultType.getResultFieldAtPosition(i - 1).getFieldName(), resultValue);
				//resultInstance.addResultValue(resultType.getResultFieldAtPosition(i - 1).getFieldName(), value);				

				logger.debug("fieldNameAtIMinus1: " + fieldNameAtIMinus1);
				logger.debug("fieldNameAtIndexOfIMinus1: " + fieldNameAtIndexOfIMinus1);
				logger.debug("resultValue: " + resultValue);

				if (fieldNameAtIMinus1.contains(columnNameWithoutDot) && !fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)) {
					logger.debug("fieldNameAtIMinus1.contains(columnNameWithoutDot) && !fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)");
					ResultValue value = new ResultValue(fieldNameAtIMinus1, resultValue);
					resultInstance.addResultValue(fieldNameAtIMinus1, value);
					//value.setResultInstance(resultInstance);
				} else if (!fieldNameAtIMinus1.contains(columnNameWithoutDot) && fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)) {
					logger.debug("!fieldNameAtIMinus1.contains(columnNameWithoutDot) && fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)");
					ResultValue value = new ResultValue(fieldNameAtIndexOfIMinus1, resultValue);
					resultInstance.addResultValue(fieldNameAtIndexOfIMinus1, value);
					//value.setResultInstance(resultInstance);
				} else if (fieldNameAtIMinus1.contains(columnNameWithoutDot) && fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)) {
					logger.error("fieldNameAtIMinus1.contains(columnNameWithoutDot) && fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)");
					logger.error("both could be potentially correct columns ... check whether they're the same and if yes pick fieldNameAtIndexOfIMinus1  ...");
					if (fieldNameAtIMinus1.equals(fieldNameAtIndexOfIMinus1)) {
						logger.debug("fieldNameAtIMinus1.equals(fieldNameAtIndexOfIMinus1)");
						ResultValue value = new ResultValue(fieldNameAtIndexOfIMinus1, resultValue);
						resultInstance.addResultValue(fieldNameAtIndexOfIMinus1, value);
						logger.debug("fieldNameAtIndexOfIMinus1: " + fieldNameAtIndexOfIMinus1);
						logger.debug("added resultValue: " + value);
					}
				} else if (!fieldNameAtIMinus1.contains(columnNameWithoutDot) && !fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)) {
					logger.error("!fieldNameAtIMinus1.contains(columnNameWithoutDot) && !fieldNameAtIndexOfIMinus1.contains(columnNameWithoutDot)");
					logger.error("TODO neither could be potentially correct columns ... sort this ...");
				}

			}
			resultInstance.setResultType(resultType);
			//isFirstElementInResultSet = false;
			resultInstances.add(resultInstance);
		}

		if (resultSet.isAfterLast()) {
			logger.debug("after last entry in resultSet");
			setReachedEof(true);
			//ResultInstance finalResultInstance = new ResultInstance();
			//finalResultInstance.setEof(true);
		}

		return resultInstances;
	}

	/*
	public List<ResultInstance> translateIntoResultInstances(ResultSet resultSet) {
		List<ResultInstance> result = new ArrayList<ResultInstance>();

		return result;
	}
	*/

	/*
	public QueryResult translateResultSetIntoQueryResult(ResultSet resultSet, ResultType resultType) {
		boolean isFirstElementInResultSet = true;
		QueryResult queryResult = null;
		try {
			String columnLabel = "";
			while (resultSet.next()) {
				logger.debug("next element from resultSet");
				int columnCount = resultSet.getMetaData().getColumnCount();
				ResultInstance resultInstance = new ResultInstance();
				//ResultType resultType = null;
				if (isFirstElementInResultSet) {
					//resultType = new ResultType();
					queryResult = new QueryResult();
					queryResult.setResultType(resultType);
				}
				for (int i = 1; i <= columnCount; i++) {
					logger.debug("i: " + i);
					logger.debug("columnCount: " + columnCount);
					columnLabel = resultSet.getMetaData().getColumnLabel(i);
					logger.debug("columnLabel: " + columnLabel);
					if (isFirstElementInResultSet) {
						String columnName = resultSet.getMetaData().getColumnName(i);
						String columnType = resultSet.getMetaData().getColumnTypeName(i);
						logger.debug("columnName: " + columnName);
						logger.debug("columnType: " + columnType);
						DataType dataType = DataTypeMapper.mapSQLTypeToDataType(columnType);
						//ResultField resultField = new ResultField(columnLabel, dataType);
						//logger.debug("added new resultField: " + resultField);
						//resultType.getResultFields().add(resultField);
					}
					String resultValue = resultSet.getString(i);
					logger.debug("resultValue: " + resultValue);
				
					//if (resultValue != null) {
					//	if (resultValue.length() > 250) {
					//		logger.debug("resultValue too long");
					//		resultValue = resultValue.substring(0, 250);
					//		logger.debug("concatenated resultValue: " + resultValue);
					//	}
					//}
				
					ResultField resultField = resultType.getResultFields().get(columnLabel);
					ResultValue newResultValue = new ResultValue(resultField.getFieldName(), resultValue);
					//resultInstance.getResultFieldNameResultValueMap().put(columnLabel, resultValue);
					resultInstance.addResultValue(columnLabel, newResultValue);
					//newResultValue.setResultInstance(resultInstance);
				}
				resultInstance.setResultType(resultType);
				isFirstElementInResultSet = false;
				queryResult.getResultInstances().add(resultInstance);
			}

			if (resultSet.isAfterLast()) {
				logger.debug("after last entry in resultSet");
				setReachedEof(true);
				//ResultInstance finalResultInstance = new ResultInstance();
				//finalResultInstance.setEof(true);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return queryResult;
	}
	*/

	public void setReachedEof(boolean reachedEof) {
		this.reachedEof = reachedEof;
	}

	public boolean isReachedEof() {
		return reachedEof;
	}

	/*
	public void setQueryResult(QueryResult queryResult) {
		this.queryResult = queryResult;
	}

	public QueryResult getQueryResult() {
		return queryResult;
	}
	*/
}
