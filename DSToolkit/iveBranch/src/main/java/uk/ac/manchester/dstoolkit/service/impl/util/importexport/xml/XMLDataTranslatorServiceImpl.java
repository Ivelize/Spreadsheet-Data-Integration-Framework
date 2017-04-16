package uk.ac.manchester.dstoolkit.service.impl.util.importexport.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;

/**
 * @author ruhaila
 * @author chedeler
 *
 */

//@Transactional(readOnly = true)
@Service(value = "xmlDataTranslatorServiceImpl")
public class XMLDataTranslatorServiceImpl {

	private static Logger logger = Logger.getLogger(XMLDataTranslatorServiceImpl.class);

	private boolean reachedEof = false;

	public List<ResultInstance> translateResultSetIntoListOfResultInstances(ResourceSet resourceSet, ResultType resultType) {
		//boolean isFirstElementInResultSet = true;
		logger.debug("in translateResultSetIntoListOfResultInstances");
		logger.debug("resultType: " + resultType);
		logger.debug("resultType.resultFields: " + resultType.getResultFields());
		logger.debug("resourceSet: " + resourceSet.toString());
		List<ResultInstance> resultInstances = null;

		try {
			ResourceIterator resourceIterator = resourceSet.getIterator();
			while (resourceIterator.hasMoreResources()) {
				Resource res = resourceIterator.nextResource();
				String content = (String) res.getContent();
				logger.debug("xml tuples, content: " + content);

				XMLReader saxParser = new SAXParser();
				XMLResultTupleParserServiceImpl xmlResultTupleHandler = new XMLResultTupleParserServiceImpl(resultType);
				saxParser.setContentHandler(xmlResultTupleHandler);
				saxParser.setErrorHandler(xmlResultTupleHandler);
				InputSource input = new InputSource(new StringReader(content));
				saxParser.parse(input);

				logger.debug("finished parsing resultTuples");
				reachedEof = true;
				resultInstances = xmlResultTupleHandler.getResultInstances();

			}
		} catch (XMLDBException e) {
			logger.error("XMLDBException e: " + e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException e: " + e);
			e.printStackTrace();
		} catch (SAXException e) {
			logger.error("SAXException e: " + e);
			e.printStackTrace();
		}

		return resultInstances;
	}

	public void setReachedEof(boolean reachedEof) {
		this.reachedEof = reachedEof;
	}

	public boolean isReachedEof() {
		return reachedEof;
	}
}
