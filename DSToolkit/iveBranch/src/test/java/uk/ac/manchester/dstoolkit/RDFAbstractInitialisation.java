package uk.ac.manchester.dstoolkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;

/**
 * RDFAbstractInitialisation: Top hierarchy class for running JUnit tests. This class is actually an 
 * Agent for testing various functionalities of the system. Its purpose is to setup DSToolkit 
 * properly so it can function over RDF graphs.
 * 
 * This class will be extended when a class needs to initialise the system for use with LD.
 * 
 * @author Klitos Christodoulou
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public abstract class RDFAbstractInitialisation extends AbstractTransactionalJUnit4SpringContextTests {
	static Logger logger = Logger.getLogger(RDFAbstractInitialisation.class);
	
	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;	
	
	@Autowired
	@Qualifier("userService")
	private UserService userService;
	
	@Autowired
	@Qualifier("dataspaceRepository")
	private DataspaceRepository dataspaceRepository;
	
	@Autowired
	@Qualifier("dataspaceService")
	private DataspaceService dataspaceService;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	/*Configuration files for the SDBStores:
	 * sdb_graphs: a single space that holds all the imported RDF graphs or RDFAlign graphs for
	 * generating GT.
	 * sdb_metadata: holds metadata for enriching the schemata*/
	private static String jenaSDBMetaDataPropLoc = "./src/main/resources/datasources/jenaSDBmeta.properties";
	private static String jenaSDBGraphsPropLoc   = "./src/main/resources/datasources/jenaSDBgraphs.properties";
	private static String jenaTDBGraphsPropLoc 	 = "./src/main/resources/datasources/jenaTDB.properties";
	
	/**
	 * Configuration file for training 
	 */
	private static String trainModelsPropLoc = "./src/main/resources/datasources/train.properties";
	
	/*Properties from src/test/resources/name.properties*/
	protected static String   rdfSourceName;
	protected static String   schemaNameRDF;
	protected static String   connectionURL;
	protected static String   driverClass;
	protected static String   rdfSourceURL;
	protected static String   schemaURL;
	protected static String   userName;
	protected static String   passWord;
	protected static String   isRDFSource;
	protected static String   description;	
	
	/*Settings for this Dataspace User*/
	protected static User currentUser;
	protected static Dataspace currentDataspace;
	
	/*Hold the SDBStores for sdb_graphs and sdb_metadata*/
	private SDBStoreServiceImpl metaDataSDBStore = null;
	private SDBStoreServiceImpl graphsSDBStore = null;
	private TDBStoreServiceImpl graphsTDBStore = null;
	
	@Before
	public void setUp() { 
		logger.info("Initialising DSToolking for Linked Data");
		logger.debug("in RDFAbstractInitialisation.setup()");
		this.createDataspace();
		
		//NOTE: Comment/Uncomment the conf needed for quicker startup
		
		/*Create SDBGraphs Store: common store for all RDF graphs*/
		//graphsSDBStore = this.createSDBStoreForDatasources(jenaSDBGraphsPropLoc);
		//logger.debug("SDBStore for sdb_graphs is: " + graphsSDBStore);
		
		/*Create SDBMetaData Store: store all metadata graphs*/		
		metaDataSDBStore = this.createSDBStoreForMetadata(jenaSDBMetaDataPropLoc);
		logger.debug("SDBStore for sdb_metadata is: " + metaDataSDBStore);
				
		/*Create TDBStore: use to store various RDF graphs*/
		graphsTDBStore = this.createTDBStore(jenaTDBGraphsPropLoc);
		logger.debug("TDBStore is: " + graphsTDBStore);		
	}//end setUp()
	
    /***
	 * Initialisation: Create Dataspace and owner of this Dataspace
	 */
	public void createDataspace() {
		logger.debug("Creating new Dataspace and Users.");
		/*If Dataspace user do not exist, create a new dataspace user*/
		if (userRepository.getUserWithUserName("klitos") == null) {
			//add user		
			User klitosUser = new User();
			klitosUser.setUserName("klitos");
			klitosUser.setPassword("klitos");
			klitosUser.setFirstName("klitos");
			klitosUser.setEmail("christodoulou@cs.man.ac.uk");
			userService.addUser(klitosUser);
			currentUser = klitosUser;
		}

		/*If Dataspace do not exist, create a new Dataspace*/
		if (dataspaceRepository.getDataspaceWithName("RDF_DS1") == null) {
			Dataspace dataspace = new Dataspace("RDF_DS1");
			dataspace.addUser(currentUser);
			currentUser.addDataspace(dataspace);
			dataspaceService.addDataspace(dataspace);
			userRepository.update(currentUser);
			currentDataspace = dataspace;
		}
	}//end createDataspace()
			
	/*Initialisation: Create SDBstore that holds all RDF-graphs as Named Graphs*/
	public SDBStoreServiceImpl createSDBStoreForDatasources(String path) {
		/*Create the SDBStore that holds the graphs (sdb_graphs)*/
		SDBStoreServiceImpl graphsSDBStore = null;
		logger.debug("Creating SDBStore for holding metadata from namespaces");
		loadRDFSourceUtilService.createSDBStore(path);
		//immediate call to getSDBStroreForDB therefore does not need to read the file-path again.
		graphsSDBStore = loadRDFSourceUtilService.getSDBStoreForDB("");
		return graphsSDBStore;		
	}//end createSDBStoreForDatasources()
	
	/*Initialisation: Create SDBstore that holds meta-data as Named Graphs*/
	public SDBStoreServiceImpl createSDBStoreForMetadata(String path) {
		/*Create the SDBStore that holds meta-data (sdb_metadata)*/
		SDBStoreServiceImpl metaSDBStore = null;
		logger.debug("Creating SDBStore for holding metadata from namespaces");
		loadRDFSourceUtilService.createSDBStore(path);
		//immediate call to getSDBStroreForDB therefore does not need the filepath again.
		metaSDBStore = loadRDFSourceUtilService.getSDBStoreForDB("");
		return metaSDBStore;
	}//end createSDBStoreForMetadata()	
	
	/*Initialisation: Create TDBStore*/
	public TDBStoreServiceImpl createTDBStore(String path) {
		TDBStoreServiceImpl tdbStore = null;
		logger.debug("Creating TDBStore...");
		tdbStore = loadRDFSourceUtilService.createTDBStore(path);
		return tdbStore;
	}//end createTDBStore()	
	
	/***
	* @return SDBStoreServiceImpl - object that holds the SDBModel, and allows 
	* operations over a Model backed by a relational database. This particular
	* model is used if user wants to store all Graphs as NamedGraphs in Model
	* backed by a relational database.
	*/
	public SDBStoreServiceImpl getSDBGraphsStore() {
		return this.graphsSDBStore;
	}//end getSDBGraphsStore()	
		
	/***
	* @return SDBStoreServiceImpl - object that holds the SDBModel, and allows 
	* operations over a Model backed by a relational database. This particular
	* model is used for storing meta-data graphs
	*/
	public SDBStoreServiceImpl getMetadataSDBStore() {
		return this.metaDataSDBStore;
	}//end getMetadataSDBStore()	
	
	/***
	 * @return TDBStoreServiceImpl - object that allows operations over a 
	 * TDB store
	 */
	public TDBStoreServiceImpl getTDBStore() {
		return this.graphsTDBStore;
	}//end getMetadataSDBStore()	
	
	/**
	 * Load a configuration file to read the sources and their schemas
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
	
	/**
	 * Load a configuration file to a HashMap
	 * @param filePath
	 */
	protected Map<String, String> loadConfigFile(String filePath) {
		Map<String, String> alignMap = null;		
		try {
			alignMap = new HashMap<String, String>();
			InputStream propertyStream = new FileInputStream(filePath);
			Properties alignProps = new java.util.Properties();
			alignProps.load(propertyStream);
	    	  
			//Load alignment file location
			for(String key : alignProps.stringPropertyNames()) {
				  String value = alignProps.getProperty(key);
				  alignMap.put(key, value);
			}//end for 	
			
	    } catch (FileNotFoundException exc) {
			logger.error("RDF_exception raised while loading RDF: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("RDF_properties file not found", ioexc);
			ioexc.printStackTrace();
		}//end catch
		
		return alignMap;
	 }//end loadConfiguration()	
	
}//end class
