package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultInstance;
import uk.ac.manchester.dstoolkit.domain.models.query.queryresults.ResultType;

/**
 * 
 * @author Klitos Christodoulou
 * 
 */



@Service(value = "sparqlDataTranslatorServiceImpl")
public class SPARQLDataTranslatorServiceImpl  {

	private static Logger logger = Logger.getLogger(SPARQLDataTranslatorServiceImpl.class);
	
	
	public List<ResultInstance> translateResultSetIntoListOfResultInstances(com.hp.hpl.jena.query.ResultSet resultSet, ResultType resultType) {
		logger.debug("in SPARQLDataTranslatorServiceImpl");		
		logger.debug("in translateResultSetIntoListOfResultInstances");
		logger.debug("resultType: " + resultType);

		List<ResultInstance> resultInstances = new LinkedList<ResultInstance>();
		
		logger.debug("list of variables " + resultSet.getResultVars()); 

		//TODO: finish with the output of the query
		while (resultSet.hasNext()) {
			logger.debug("next element from resultSet");
			ResultInstance resultInstance = new ResultInstance();
			
			logger.debug("Variable Binding: " + resultSet.nextBinding());
			logger.debug("Solution: " + resultSet.nextSolution());			
		}
			
		
		
		
		return resultInstances;
	}//end translateResultSetIntoListOfResultInstances()
}//end class 
