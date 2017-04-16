package uk.ac.manchester.dstoolkit.service.impl.util.sampling;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.util.sampling.SamplingUtilService;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;


/***
 * This class is responsible of sampling a Linked Data source by the use of SPARQL queries. The class
 * reads from a configuration file the location of the LD source to read. The LD source could be 
 * represented as a SPARQL end-point where the configuration file points to a SPARQL interface.
 * 
 * 
 * @author Klitos Chistodoulou
 */
@Service(value = "samplingUtilService")
public class SamplingUtilServiceImpl implements SamplingUtilService {
	
	/*Logging*/
	private static Logger logger = Logger.getLogger(SamplingUtilServiceImpl.class);
	
	/*Constructor*/
	public SamplingUtilServiceImpl() {		
	}
	
	/***
	 * This method checks whether the SPARQL end-point service is UP or Down
	 */
	public boolean checkSPARQLservice(String sparqlServiceURL) {
		String queryString = "ASK { }";
		QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlServiceURL, queryString);
		boolean status = false;
		
		try {
			if (qe.execAsk()) {
				/*SPARQL service is alive*/
			  	logger.debug("in setupMetadataSDB");
			  	status = true;
			}//end if
		} catch (QueryExceptionHTTP e) {
			/*SPARQL service is not alive and exception is thrown*/
		  	logger.warn("SPARQL Service for: " + sparqlServiceURL + " is down.");
		} catch (Exception exe) {
		 	logger.warn("Checking SPARQL service exception: ", exe);  
		} finally {
		  	qe.close();
		}		
		return status;
	}//end checkSPARQLservice()
	
	

}//end class
