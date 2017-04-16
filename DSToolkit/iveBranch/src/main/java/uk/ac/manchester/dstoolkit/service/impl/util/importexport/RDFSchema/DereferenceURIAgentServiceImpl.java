package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ActionStatus;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.DereferenceURIAgentService;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sdb.store.DatasetStore;

/**
 * Implementing:
 * 	- Low level HTTP client for dereferencing LD.
 * 
 * @author Klitos Christodoulou
 */

@Service(value = "dereferenceURIAgentService")
public class DereferenceURIAgentServiceImpl implements DereferenceURIAgentService {
	
	/*Initialised Logger*/
	static Logger logger = Logger.getLogger(DereferenceURIAgentServiceImpl.class);
	
	/*Hold a reference to the SDBStore that holds meta-data*/
	private SDBStoreServiceImpl metaDataSDBStore = null;

	/*Constructor*/
	public DereferenceURIAgentServiceImpl() {
		logger.debug("in DereferenceURIAgentServiceImpl");
	}	
	
	/*Constructor*/
	public DereferenceURIAgentServiceImpl(SDBStoreServiceImpl obj) {
		metaDataSDBStore = obj;
		logger.debug("in DereferenceURIAgentServiceImpl");
	}	
	
	/**
	 * This method reads a model directly into the metadata SDBStore.
	 * 
	 * @param modelName - the name of the Graph to connect to in the SDBStore
	 * @param uri - the URI to dereference
	 * 
	 * @return ActionStatus - 0 if fails / 1 otherwise
	 */
	public ActionStatus dereferenceURI(String modelName, String sourceURL) {
		logger.debug("in dereferenceURI()");
		ActionStatus status = null;
		
		if (metaDataSDBStore == null) {
		 return new ActionStatus(0, "Meta-data SDBStore is NULL");
		}
		 
		try {
			status = metaDataSDBStore.loadDataToModel(modelName, sourceURL);
			return status;
		} catch (Exception exe) {
			logger.debug("Exception while loading ");
			return new ActionStatus(0, exe.getClass().getSimpleName());
		}		
	}//end dereferenceURI() 
	
	
	public ActionStatus dereferenceFromSPARQLdump(String modelName, String sourceURL) {
		logger.debug("in dereferenceURI()");
		ActionStatus status = null;
		
		if (metaDataSDBStore == null) {
		 return new ActionStatus(0, "Meta-data SDBStore is NULL");
		}
		 
		try {
			status = metaDataSDBStore.loadDataToModelFileWithStatus(modelName, sourceURL);
			return status;
		} catch (Exception exe) {
			logger.debug("Exception while loading ");
			return new ActionStatus(0, exe.getClass().getSimpleName());
		}		
	}//end dereferenceURI() 
	
	
	
	/**
	 * This method invokes an HttpClient that sends a GET request to the server to dereference a URI.
	 * The default serialisation format for RDF documents is RDF/XML therefore the client send an
	 * application/rdf+xml header request to the server to retieve such documents. 
	 * 
	 * Note: the method will call itself until it find a uri to dereference
	 * 
	 * @param uri - the URI to dereference
	 * @param modelName - the name of the Graph to connect to in the SDBStore
	 * @param header - the header to be used by the HttpClient when send a GET message
	 * 
	 * @return ActionStatus - 0 if fails / 1 otherwise
	 */
	public ActionStatus dereferenceURIHttpClient(String modelName, String uri, String header) {
		logger.debug("in dereferenceURIHttpClient()");
		ActionStatus status = null;
	
		/*Create a new HTTP Client object, so as to be able to execute HTTP methods*/
		HttpClient httpClient = new DefaultHttpClient();
		
		/*The client will execute an HTTP GET method*/
		HttpGet httpGetRequest = new HttpGet(uri);
		
		if ((header == null) || (header.endsWith(""))) {
			header = "application/rdf+xml, application/turtle;q=0.6, application/rdf+turtle;q=0.6, application/xml;q=0.6, text/rdf+n3;q=0.5" ;
		}
		
		//Add the header of this request
		httpGetRequest.addHeader("Accept", header);
	
		/*Now that we have the client and the header that we want to execute we need
		 * to ask the client to execute it*/
		HttpResponse response = null;
		HttpEntity entity = null;
		BufferedReader buffer = null;
		InputStream instream = null;
		try {
			response = httpClient.execute(httpGetRequest);
			logger.debug("HTTP Status line: " + response.getStatusLine());
			logger.debug("HTTP Status code: " + response.getStatusLine().getStatusCode());
			
			/*If the server response is OK*/
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
				try {
					entity = response.getEntity();
					if (entity != null) {
						instream = entity.getContent();
						buffer = new BufferedReader(new InputStreamReader(instream));
						
						metaDataSDBStore.loadStreamToModel(modelName, buffer, uri);					
						status = new ActionStatus(1, "Dereferencing URI OK");
						//String output;
						//logger.debug("Output from Server .... \n");
						//while ((output = buffer.readLine()) != null) {
							//logger.debug(output);
						//}						
					}//end if					
				} catch (Exception exe) {
					status = new ActionStatus(0, exe.getClass().getSimpleName());
				} finally {
			        instream.close();
			    }
				logger.debug("HTTP Client return");
				return status;
			} else if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				logger.debug("HTTP Client: doing recursion");
				
				if (uri.contains("#")) {
				   	uri = uri.substring(0, uri.lastIndexOf("#"));	
				}
				
				uri = uri.substring(0, uri.lastIndexOf("/"));
				logger.debug("Dereference URI: " + uri);
				
				status = dereferenceURIHttpClient(modelName, uri, null);
			} else {
				logger.debug("HTTP Client return, else");
				status = new ActionStatus(0, "Something is wrong");
				return status;
			}		
		} catch (Exception exe) {
			logger.debug("HTTP Client Exception :" + exe);
			status = new ActionStatus(0, exe.getClass().getSimpleName());
		}
		return status;			
	}//end dereferenceURIHttpClient()	
	
	/**
	 * If a Resource does not exists in a particular Named Graph it might exists into the Dataset anyway. This method sends a
	 * SPARQL Select query to get the Resource from any Named Graph and returns the actual Named Graph that contains the resource, if any.
	 * @param model
	 * @param constructURI
	 * @return Model - The named graph model that contains this Resource as a Subject.
	 *               - if Model == null means that it does not exists in any Named Graph in the Dataset
	 */
	public Model subjectURIexistsDatasetSELECT(String constructURI) {
		logger.debug("in subjectURIexistsDatasetSELECT()");
		Model model = null;
		if ((constructURI == null) || constructURI.equals("")) {
			return null;
		}
		
		String queryStringAllGraphs =
				"SELECT DISTINCT ?g " +
				"WHERE {" +
				"  GRAPH ?g { <" + constructURI + "> " + "?p ?o .}" +
				" }";
	
			//Create Query over the whole Dataset
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryStringAllGraphs);
			Dataset ds = DatasetStore.create(metaDataSDBStore.getSDBStore());
			QueryExecution qe = QueryExecutionFactory.create(query, ds);
			try {
				ResultSet results = qe.execSelect();
				if (results.hasNext()) {
					logger.debug("Subject exists in another Graph retrieve only one Graph");
					QuerySolution soln = results.nextSolution();
					RDFNode node = soln.get("g");
					String graphName = node.toString().trim();					
					logger.debug("Graph name found : " + graphName);					
					if ((node != null) && !graphName.equals("") ) {
						model = metaDataSDBStore.getModel(graphName);
						//logger.debug("Model found : " + model);
					}//end inner				
				}//end if				
			} finally {
				qe.close() ; //--> an epistre4ei null na to kamw comment afto
			}		
		return model;
	}//end subjectURIexistsDatasetASK()
	
	/**
	 * Method that uses a SPARQL ASK query to check whether a subject exists in a particular Named Graph
	 * 
	 * @param model - the Named Graph to search for this URI as a subject
	 * @param constructURI - the URI of the subject to search
	 * @return - true if the URI exists as a subject in this named graph
	 * 				   - false otherwise
	 */
	public boolean subjectURIexistsASK(Model model, String constructURI) {
		logger.debug("in subjectURIexistsASK()");
		boolean result = false;
		
		String queryString =
				"ASK " +
				"WHERE {" +
				" <" + constructURI + "> " + "?p ?o . " +
				" }";
			
			//Create the SPARQL ASK query
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			result = qe.execAsk();
			qe.close();
						
			if (result) { 
				return true;
			} else {
				//Retry with lowercase namespace URI
				//URIs in RDF are case of sensitive so this might not be a good idea
				/*logger.debug("Re-try with lower-case URI");
				String queryString2 =
						"ASK " +
						"WHERE {" +
						" <" + constructURI.toLowerCase() + "> " + "?p ?o . " +
						" }";		
			
				com.hp.hpl.jena.query.Query query2 = QueryFactory.create(queryString2);
				QueryExecution qe2 = QueryExecutionFactory.create(query2, model);
				result = qe2.execAsk();
				qe2.close();*/
				return result;
			}//end else			
	}//end executeSPARQLSelectQuery()
	
	
	public ResultSet getResultSetForSubjectURIandPredicate(Model model, String constructURI, com.hp.hpl.jena.rdf.model.Property pred) {
		logger.debug("in getResultSetForSubjectURI()");
		ResultSet results = null;
		/*Firstly check whether this construct URI exists in the model*/
		boolean exists = subjectURIexistsASK(model, constructURI);
		
		if (exists) {			
			String queryString =
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#> " +	
					"SELECT DISTINCT ?o " +
					"WHERE {" +
					"  <" + constructURI + "> <" + pred + "> ?o ." +
					" }";
			
			com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, model);
			results = qe.execSelect();	
			/*Do not close the set here because any operations on the ResultSet afterwards are not allowed*/
			//qe.close();
		}//end if		
		return results;
	}//end getResultSetForSubjectURI()
	

	/**
	 * Method that sends a SPARQL ASK query that checks whether a graph exists or not
	 * @return boolean - true if the graph exists 
	 * 				   - false otherwise
	 */
	public boolean isGraphExistsASK(String graphName) {
		logger.debug("in isGraphExistsASK()");
		boolean result = false;

		if ((graphName == null) || graphName.endsWith("")) {
			return false;
		}
		
		String queryString =
				"ASK { " +
				"GRAPH " +
				" <" + graphName + "> " + "{ ?s ?p ?o . }" +
				" }";
		
		//Create the SPARQL ASK query
		com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
        Dataset ds = DatasetStore.create(metaDataSDBStore.getSDBStore()) ;
		QueryExecution qe = QueryExecutionFactory.create(query, ds);
		result = qe.execAsk();
		qe.close();
		
		if (result) { 
			return true;
		} else {
			//Retry with lowercase namespace URI
			logger.debug("Re-try with lower-case URI");
			String queryString2 =
					"ASK { " +
					"GRAPH " +
					" <" + graphName.toLowerCase() + "> " + "{ ?s ?p ?o . }" +
					" }";		
		
			com.hp.hpl.jena.query.Query query2 = QueryFactory.create(queryString2);
			Dataset ds2 = DatasetStore.create(metaDataSDBStore.getSDBStore()) ;
			QueryExecution qe2 = QueryExecutionFactory.create(query2, ds2);
			result = qe2.execAsk();
			qe2.close();
			return result;
		}
	}//end executeSPARQLSelectQuery()
	
	/**
	 * Method that checks whether a graph exists or not in the SDBStore. Similar to
	 * isGraphExistsASK() but without sending a SPARQL query.
	 * @return boolean - true if the graph exists 
	 * 				   - false otherwise
	 */
	public boolean isGraphExists(String graphName) {
		logger.debug("in isGraphExists()");
		boolean exists = this.metaDataSDBStore.nameModelExists(graphName);
		
		if (!exists) {
			//try again with graphName in lowercase
			logger.debug("Re-try with lower-case URI");
			exists = this.metaDataSDBStore.nameModelExists(graphName.toLowerCase());
		}	
		return exists;
	}//end executeSPARQLSelectQuery()

	/**
	 * Pass to this Object a reference to the SDBStore that will hold metadata from
	 * namespaces as RDF Named Graphs.
	 * @param obj
	 */
	public void initiliaseURIagent(SDBStoreServiceImpl obj) {
		logger.debug("in initiliaseURIagent()");
		this.metaDataSDBStore = obj;
		logger.debug("metaDataSDBStore : " + metaDataSDBStore);
	}//setSDBStoreServiceObject()		
	
	/**
	 * Return a reference to the SDBStoreServiceImpl that holds SDBStore for metadata.   
	 * @return
	 */
	public SDBStoreServiceImpl getSDBStoreService() {
		if (metaDataSDBStore != null) {
			return this.metaDataSDBStore;
		} else {
			return null;
		}
	}//end getSDBStoreService()	
}//end Class