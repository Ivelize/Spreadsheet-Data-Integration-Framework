package uk.ac.manchester.dstoolkit.service.impl.util.RDF;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;

/** 
 * @author klitos
 *
 * Comment: RDF sources needed to be loaded first. 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class LoadRDFSourceUtilServiceTest {

	private static String rdfSourceName;
	private static String schemaNameRDF;
	private static String connectionURL;
	private static String driverClass;
	private static String rdfSourceURL;
	private static String schemaURL;
	private static String userName;
	private static String passWord;
	private static String isRDFSource;
	private static String description;	
	
	static Logger logger = Logger.getLogger(LoadRDFSourceUtilServiceTest.class);
	
	/*Configuration files for SDBStores, sdb_graphs and sdb_metadata*/
	private static String jenaSDBMetaDataPropLoc = "./src/main/resources/datasources/jenaSDBmeta.properties";
	private static String jenaSDBGraphsPropLoc   = "./src/main/resources/datasources/jenaSDBgraphs.properties";
	
	//Geographical Data RDF Sources
	private static String mondialRDFEuropePropsLoc = "./src/test/resources/datasources/MondialRDFEurope.properties";
	private static String telegraphisRDFPropsLoc = "./src/test/resources/datasources/TelegraphisRDFCapitalsContinentsCountriesCurrencies.properties";

	//DBTune RDF Sources
	private static String magnatuneRDFPropsLoc = "./src/test/resources/datasources/MagnatuneRDFSource.properties";
	private static String jamendoRDFPropsLoc = "./src/test/resources/datasources/JamendoRDFSource.properties";	
	
	//test RDF source
	private static String studentsRDFPropsLoc = "./src/test/resources/datasources/StudentsRDFSource.properties";
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Before
	public void setUp() {	
	}//end setUp()
	
	/**
	 * Load Students RDF Source
	 */
	@Test
	public void testLoadExternalStdRDFsource() throws SQLException {
		
		/*load configuration from mondialRDFEurope.properties*/
		loadConfiguration(studentsRDFPropsLoc);
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);

		
		assertNotNull(loadRDFSourceUtilService);
		
	}//end testLoadExternalRDFsource()	
	
	/**
	 * Load Mondial Europe RDF Source
	 */
	@Test
	public void testLoadExternalMondialRDFsource() throws SQLException {
		
		/*load configuration from mondialRDFEurope.properties*/
		loadConfiguration(mondialRDFEuropePropsLoc);
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
		
		assertNotNull(loadRDFSourceUtilService);
		
	}//end testLoadExternalMondialRDFsource()		
	
	
	/**
	 * Load Telegraphis RDF Source
	 */
	@Test
	public void testLoadExternalTelegraphisRDFsource() throws SQLException {
		
		/*load configuration from mondialRDFEurope.properties*/
		loadConfiguration(telegraphisRDFPropsLoc);
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
		
		assertNotNull(loadRDFSourceUtilService);
		
	}//end testLoadExternalMondialRDFsource()		
	
	
	/**
	 * Load both Mondial Europe and Telegraphis RDF Sources
	 */	
	@Test
	public void testLoadExternalRDFsources() throws SQLException {
		
		/*load configuration from mondialRDFEurope.properties*/
		loadConfiguration(mondialRDFEuropePropsLoc);
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
		
		/*load configuration from mondialRDFEurope.properties*/
		loadConfiguration(telegraphisRDFPropsLoc);
		
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
	}//end testLoadExternalRDFsource()	
		
	
	/**
	 * Load Magnatune RDF Source
	 */
	@Test
	public void testLoadExternalMagnatuneRDFsource() throws SQLException {
		
		/*load configuration from magnatuneRDFPropsLoc.properties*/
		loadConfiguration(magnatuneRDFPropsLoc);
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
		
		assertNotNull(loadRDFSourceUtilService);
		
	}//end testLoadExternalMondialRDFsource()	
	
	/**
	 * Load Jamendo RDF Source
	 */
	@Test
	public void testLoadExternalJamendoRDFsource() throws SQLException {
		
		/*load configuration from jamendoRDFPropsLoc.properties*/
		loadConfiguration(jamendoRDFPropsLoc);
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
		
		assertNotNull(loadRDFSourceUtilService);
		
	}//end testLoadExternalMondialRDFsource()	
	
	/**
	 * Load Both RDF Sources from DBtune (Magnatune & Jamendo)
	 */	
	@Test
	public void testLoadExternalRDFsourcesDBtune() throws SQLException {
		
		/*Magnatune RDF source*/
		loadConfiguration(magnatuneRDFPropsLoc);

		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);
		
		/*Jamendo RDF source*/
		loadConfiguration(jamendoRDFPropsLoc);		
		
		/*import RDF to a Jena model and make it persistent*/
		loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass, rdfSourceURL, userName, passWord);	
	}//end testLoadExternalRDFsource()	
		
	/**
	 * Test method for:
	 *  - the creation of the metadata DB
	 *  - the creation of SDBStore for metadata
	 *  - load an rdf document from the web to test it	
	 * @throws SQLException
	 */
	@Test
	public void initMetadataSDB() throws SQLException {
		/*Create the new DB that will hold the SDBStore*/
		
		//Firstly call this to create the DB for the SDBStore
		loadRDFSourceUtilService.createSDBStore(jenaSDBGraphsPropLoc);
		
		SDBStoreServiceImpl graphsSDBStore = loadRDFSourceUtilService.getSDBStoreForDB(jenaSDBGraphsPropLoc);
		logger.debug("SDBStoreServiceImpl:" + graphsSDBStore);
		
		//test querying 
		//TODO: A class that will have all SPARQL queries inside
		//Note some vocabularies may output a RDFDefaultErrorHandler error
		
        /*String queryString2 = "SELECT ?s ?p ?o WHERE {GRAPH <http://purl.org/dc/elements/1.1/>  { ?s "+
        												"?p" + " ?o}}" ;
        
        String queryString = SPARQLQueriesLibrary.LIST_ALL_NAMED_GRAPHS;
        
        Query query = QueryFactory.create(queryString) ;
		
        Store store = metaDataSDBStore.getSDBStore();
		
        Dataset ds = DatasetStore.create(store) ;
        QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
        try {
            ResultSet rs = qe.execSelect() ;
            ResultSetFormatter.out(rs) ;
        } finally { qe.close() ; }
				
		
		
		
		/*Model m = metaDataSDBStore.getModel("foaf_project");
		if (m.isEmpty())
		 logger.debug("Model is empty: " + m);		
		else 
	      logger.debug("Model is NOT empty: " + m);*/			
		//metaDataSDBStore.loadDataToModel("http://www.example.org/foo/bar", "http://xmlns.com/foaf/0.1/Person");
		
		//Check whether the named Model exists in the SDBStore
		//boolean exists = metaDataSDBStore.nameModelExists("http://www.example.org/foo/bar");
		//logger.debug("--> Name Model Exists: " + exists);
		
	}//end initMetadataSDB()
	
	
	/***
	 * Import: RDF from a SPARQL end-point.
	 */
	@Test
	public void loadRDFfromSPARQLtoSDB() throws SQLException {
		
	}
	
	
	/**
	 * Load configuration
	 * @param filePath
	 */
	protected void loadConfiguration(String filePath) {
	    try {
		  logger.debug("in LoadRDFSourceUtilServiceTest:" + filePath);
		  InputStream propertyStream = new FileInputStream(filePath);
		  Properties connectionProperties = new java.util.Properties();
		  connectionProperties.load(propertyStream);
		  //load
		  rdfSourceName = connectionProperties.getProperty("rdfSourceName");
		  schemaNameRDF = connectionProperties.getProperty("schemaNameRDF");
		  connectionURL = connectionProperties.getProperty("connectionURL");
		  driverClass = connectionProperties.getProperty("driverClass");	
		  rdfSourceURL = connectionProperties.getProperty("rdfSourceURL");	
		  schemaURL = connectionProperties.getProperty("schemaURL");
		  userName = connectionProperties.getProperty("username");
		  passWord = connectionProperties.getProperty("password");
		  isRDFSource = connectionProperties.getProperty("isRDFSource");
		  description = connectionProperties.getProperty("description");		  	  		  
		  //print
		  logger.debug("RDF_sourceName: "   + rdfSourceName);
		  logger.debug("RDF_schemaName: "   + schemaNameRDF);
		  logger.debug("RDF_connURL: "      + connectionURL);
		  logger.debug("RDF_driverClass: "  + driverClass);
		  logger.debug("RDF_rdfSourceURL: " + rdfSourceURL);		  
		  logger.debug("RDF_schemaURL: "    + schemaURL);
		  logger.debug("RDF_userName: "     + userName);
		  logger.debug("RDF_passWord: "     + passWord);
		  logger.debug("RDF_isRDF: "        + isRDFSource);
		  logger.debug("RDF_description: "  + description);
		} catch (FileNotFoundException exc) {
			logger.error("RDF_exception raised while loading RDF: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("RDF_properties file not found", ioexc);
			ioexc.printStackTrace();
		}//end catch
	 }//end loadConfiguration()		
	
}//end class
