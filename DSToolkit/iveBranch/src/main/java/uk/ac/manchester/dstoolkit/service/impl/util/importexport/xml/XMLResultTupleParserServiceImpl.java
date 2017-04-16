/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.xml;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultField;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultValue;

/**
 * @author chedeler
 *
 */
@Scope("prototype")
@Service
public class XMLResultTupleParserServiceImpl extends DefaultHandler {

	private static Logger logger = Logger.getLogger(XMLResultTupleParserServiceImpl.class);

	private ResultInstance currentResultInstance;
	private final ResultType resultType;
	private int tupleCount;
	private List<ResultInstance> resultInstances;
	private StringBuffer elementTextAccumulator;

	public XMLResultTupleParserServiceImpl(ResultType resultType) {
		logger.debug("in XMLResultTupleParserServiceImpl");
		logger.debug("resultType: " + resultType);
		logger.debug("resultFields: " + resultType.getResultFields());
		this.resultType = resultType;
	}

	@Override
	public void startDocument() {
		logger.debug("in startDocument");
		resultInstances = new LinkedList<ResultInstance>();
		tupleCount = 0;
		currentResultInstance = null;
		elementTextAccumulator = new StringBuffer();
	}

	@Override
	public void characters(char[] buffer, int start, int length) {
		logger.debug("in characters");
		logger.debug("buffer: " + buffer.toString());
		logger.debug("start:" + start);
		logger.debug("length: " + length);
		logger.debug("elementTextAccumulator: " + elementTextAccumulator);
		elementTextAccumulator.append(buffer, start, length);
		logger.debug("elementTextAccumulator: " + elementTextAccumulator);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		logger.debug("in startElement");
		logger.debug("uri: " + uri);
		logger.debug("localName: " + localName);
		logger.debug("qName: " + qName);
		logger.debug("attributes: " + attributes);

		logger.debug("elementTextAccumulator: " + elementTextAccumulator);
		elementTextAccumulator.setLength(0);
		logger.debug("elementTextAccumulator: " + elementTextAccumulator);

		if (qName.equalsIgnoreCase("result")) {
			logger.debug("result");
		} else if (qName.equalsIgnoreCase("tuple")) {
			logger.debug("new tuple");
			currentResultInstance = new ResultInstance();
			tupleCount++;
		} else {
			logger.debug("qName: " + qName);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		logger.debug("in endElement");
		logger.debug("uri: " + uri);
		logger.debug("localName: " + localName);
		logger.debug("qName: " + qName);

		if (qName.equalsIgnoreCase("result")) {
			logger.debug("result");
			logger.debug("tupleCount: " + tupleCount);
		} else if (qName.equalsIgnoreCase("tuple")) {
			logger.debug("end of new tuple");
			logger.debug("currentResultInstance: " + currentResultInstance);
			currentResultInstance.setResultType(resultType);
			resultInstances.add(currentResultInstance);
		} else {
			logger.debug("qName: " + qName);

			logger.debug("elementTextAccumulator: " + elementTextAccumulator.toString().trim());
			String fullLabel = qName;
			logger.debug("fullLabel: " + fullLabel);
			String resultValue = elementTextAccumulator.toString().trim();
			logger.debug("resultValue: " + resultValue);

			int position = resultType.getPosition(fullLabel);
			logger.debug("position: " + position);

			String columnNameWithoutDot = "";
			if (fullLabel.contains("."))
				columnNameWithoutDot = fullLabel.substring(fullLabel.indexOf(".") + 1);
			else
				columnNameWithoutDot = fullLabel;

			logger.debug("columnNameWithoutDot: " + columnNameWithoutDot);

			//TODO decide what to do if it can't find it, add proper error handling, shouldn't happen though

			logger.debug("resultType.getPosition(fullColumnName), position: " + position);
			if (position > -1) {
				ResultField fieldAtPosition = resultType.getResultFieldAtPosition(position);
				if (fieldAtPosition != null) {
					String fieldNameAtPosition = fieldAtPosition.getFieldName();
					logger.debug("fieldNameAtPosition: " + fieldNameAtPosition);

					ResultValue value = new ResultValue(fieldNameAtPosition, resultValue);
					logger.debug("value: " + value);
					currentResultInstance.addResultValue(fieldNameAtPosition, value);

				}
			} else
				logger.error("didn't find fullLabel in resultType ... TODO sort this out");

		}

	}

	public List<ResultInstance> getResultInstances() {
		return this.resultInstances;
	}
}
