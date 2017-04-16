package uk.ac.manchester.dstoolkit.service.impl.util.training;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractInitialisation;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.meta.SchemaService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.GraphvizDotGeneratorService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.training.SemEvidenceTrainingUtil;

import com.hp.hpl.jena.query.Dataset;


/***
 * This JUnit class will be responsible for calling the appropriate methods to create probability distributions
 * (likelihoods) for all the Semantic Evidences. In doing so the LOV aggregator SPARQL endpoint will be used. 
 * This endpoint try to aggregate all the vocabularies that exist under a single endpoint. I managed to get a 
 * stable version of it and I will use that to do the training instead to accessing the SPARQL endpoint directly
 * through SPARQL federated queries using the SERVICE keyword. Having access to the data this class proceeds as follows:
 *  1. import the RDF-graph that holds the LOV endpoint into sdb_metadata as a named graph. (the uri of the SPARQL endpoint is
 *  used as the name of the NamedGraph)
 * 
 * The agent uses SDB Store which might be a bit slow, for large datasets, consider using the TDB model
 * 
 * @author klitos
 */
public class SemanticEvidenceTrainingSDBTest extends RDFAbstractInitialisation {
	
	private static Logger logger = Logger.getLogger(SemanticEvidenceTrainingSDBTest.class);
	
	protected static String sparqlName;
	protected static String sparqlURL;
	protected static String sparqlDumpPath;
		
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;
		
	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;
	
	@Autowired
	@Qualifier("schemaService")
	private SchemaService schemaService;
	
	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	@Autowired
	@Qualifier("trainingDataService")
	private SemEvidenceTrainingUtil trainingDataService;	
	
	@Autowired
	@Qualifier("graphvizDotGeneratorService")
	private GraphvizDotGeneratorService graphvizDotGeneratorService;
	
	
	/*Hold a reference to the SDBStore that holds meta-data*/
	private SDBStoreServiceImpl metaDataSDBStore = null;
	
	/**
	 * Location of the local LOV RDF-graph.
	 * Date downloaded: 28/05/13
	 */
	private static String local_LOV_graph = "./src/test/resources/sparql_endpoints/LOV.sparql";	
		
	@Override
	@Before
	public void setUp() {
		super.setUp();	
	}//end setUp()	
	
	@Test
	public void testGenerateSemanticEvidence() {
		logger.debug("testGenerateSemanticEvidence()");
		/*Get the reference to the store*/		
		metaDataSDBStore = this.getMetadataSDBStore();
		/*Load the .sparql configuration file*/
		loadConfiguration(local_LOV_graph);
		
		logger.debug("SPARQLserviceName: " + sparqlName);
		logger.debug("SPARQLserviceURL: " + sparqlURL);
		logger.debug("SPARQLDumpPath: " + sparqlDumpPath);
		
						
		/*Load the SPARQL endpoint in sdb_metadata*/
		
		Dataset dataset = metaDataSDBStore.getDataset();

		Iterator<String> graphNames = dataset.listNames();
		while (graphNames.hasNext()) {
		    String graphName = graphNames.next();
		    logger.debug("graph: " + graphName);
		}
		
		
		metaDataSDBStore.loadDataToModelFile(sparqlURL, sparqlDumpPath);
		
		
		//ActionStatus status = trainingDataService.loadSPARQLendpoint(sparqlURL, sparqlDumpPath);
		
		Iterator<String> graphNames2 = dataset.listNames();
		while (graphNames2.hasNext()) {
		    String graphName = graphNames2.next();
		    logger.debug("graph2: " + graphName);
		}
		
		
		//ActionStatus status = trainingDataService.loadSPARQLendpoint(sparqlURL, sparqlDumpPath);
		
		//if (status.getStatus() == 1) {
			//Model model = metaDataSDBStore.getModel(modelName);
			//logger.debug("status: model has been loaded");	
		//}
		
		
		//Model model = trainingDataService.loadSPARQLendpoint(rdfSourceName, connectionURL);
		//logger.debug("status: " + model);		
		
		
		//trainingDataService.createSetOfEquivalentClassConstructs(sparqlName, sparqlURL, sparqlDumpPath, false);
	}//end method
	
	
	
	
	
	
	
	
	
	
	/**
	 * This method will load .sparql files
	 * Location: ./src/test/resources/sparql_endpoints/
	 */
	@Override
	protected void loadConfiguration(String filePath) {
		 try {
			  logger.debug("in SemanticEvidenceLoad():" + filePath);
			  InputStream propertyStream = new FileInputStream(filePath);
			  Properties connectionProperties = new java.util.Properties();
			  connectionProperties.load(propertyStream);
			  //load
			  sparqlName = connectionProperties.getProperty("SPARQLserviceName");
			  sparqlURL = connectionProperties.getProperty("SPARQLserviceURL");
			  sparqlDumpPath = connectionProperties.getProperty("SPARQLDumpPath");		  	  		  
			  //print
			  //logger.debug("SPARQLserviceName: " + sparqlName);
			  //logger.debug("SPARQLserviceURL: " + sparqlURL);
			  //logger.debug("SPARQLDumpPath: " + sparqlDumpPath);
			} catch (FileNotFoundException exc) {
				logger.error("RDF_exception raised while loading .sparql: " + exc);
				exc.printStackTrace();
			} catch (IOException ioexc) {
				logger.error("SPARQL_conf file not found", ioexc);
				ioexc.printStackTrace();
			}//end catch
	 }//end loadConfiguration()		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


}//end class
