package uk.ac.manchester.dstoolkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.mappings.RDFPredefinedMappingsLoaderService;

/**
 * @author klitos
 * 
 * RDFAbstractIntegrationTest
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public abstract class RDFAbstractIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {
	static Logger logger = Logger.getLogger(RDFAbstractIntegrationTest.class);

	/*Configuration files for the SDBStores:
	 * sdb_graphs: a single space that holds all the imported RDF graphs or RDFAlign graphs for
	 * generating GT.
	 * sdb_metadata: holds metadata for enriching the schemata*/
	private static String jenaSDBMetaDataPropLoc = "./src/main/resources/datasources/jenaSDBmeta.properties";
	private static String jenaSDBGraphsPropLoc   = "./src/main/resources/datasources/jenaSDBgraphs.properties";
	
	/*Properties from src/test/resources/name.properties*/
	private static String   rdfSourceName = "";
	private static String   schemaNameRDF;
	private static String   connectionURL;
	private static String   driverClass;
	private static String   rdfSourceURL;
	private static String   schemaURL;
	private static String   userName;
	private static String   passWord;
	private static String   isRDFSource;
	private static String   description;
	
	/*Settings for Dataspace User*/
	protected static User currentUser;
	protected static Dataspace currentDataspace;
	
	private static boolean loadedMappings = false;
	
	/*Hold the SDBStores for sdb_graphs and sdb_metadata*/
	private SDBStoreServiceImpl metaDataSDBStore = null;
	private SDBStoreServiceImpl graphsSDBStore = null;
	
	/*Location to RDF-Sources property files*/	
	private static String studentsRDFPropsLoc = "./src/test/resources/datasources/StudentsRDFSource.properties";
	
	//Geographical Data RDF Sources
	//private static String mondialRDFEuropePropsLoc = "./src/test/resources/datasources/MondialRDFEurope.properties";
	//private static String telegraphisRDFPropsLoc = "./src/test/resources/datasources/TelegraphisRDFCapitalsContinentsCountriesCurrencies.properties";

	//DBTune RDF Sources
	private static String magnatuneRDFmutationPropLoc = "./src/test/resources/datasources/MagnatuneRDFmutation.properties";
	private static String magnatuneRDFPropsLoc = "./src/test/resources/datasources/MagnatuneRDFSource.properties";
	private static String jamendoRDFPropsLoc = "./src/test/resources/datasources/JamendoRDFSource.properties";	
	private static String jamendoRDFmutationPropsLoc = "./src/test/resources/datasources/JamendoRDFmutation.properties";	
	private static String dbTuneIntegrRDFPropsLoc = "./src/test/resources/datasources/DBTuneIntegrMySQL.properties";
	
	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("userRepository")
	private UserRepository userRepository;

	@Autowired
	@Qualifier("dataspaceService")
	private DataspaceService dataspaceService;

	@Autowired
	@Qualifier("dataspaceRepository")
	private DataspaceRepository dataspaceRepository;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	@Autowired
	@Qualifier("rdfPredefinedMappingsLoaderService")
	protected RDFPredefinedMappingsLoaderService rdfPredefinedMappingsLoaderService;

	/*RDF Data Sources*/
	protected static DriverManagerDataSource mondialRDFEuropeDataSource;
	protected static DriverManagerDataSource telegraphisRDFDataSource;


	@Before
	//adds the databases to the dataspace (R)
	public void setUp() { 
		logger.debug("in RDF_AbstractIntegrationTest.setup()");

		/*Initialisation: Create Dataspace and owner of this Dataspace*/
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
		
		/*Initialisation: Create SDBStore that holds datasources as Named Graphs in a single SDBStore*/
		//change this to true when I need to initialise the sdbstores sdb_graphs, sdb_metadata
		if (false) {
			logger.debug("Creating SDBStore for holding metadata from namespaces");
		 	loadRDFSourceUtilService.createSDBStore(jenaSDBGraphsPropLoc);
		 	//immediate call to getSDBStroreForDB therefore does not need the filepath again.
		 	graphsSDBStore = loadRDFSourceUtilService.getSDBStoreForDB("");
		 	logger.debug("SDBStore for sdb_graphs is: " + graphsSDBStore);
		}
		
		if (false) {
			/*Initialisation: Create SDBStore that holds meta-data as Named Graphs in a single SDBStore*/
			logger.debug("Creating SDBStore for holding metadata from namespaces");
			loadRDFSourceUtilService.createSDBStore(jenaSDBMetaDataPropLoc);
			//immediate call to getSDBStroreForDB therefore does not need the filepath again.
			metaDataSDBStore = loadRDFSourceUtilService.getSDBStoreForDB("");
			logger.debug("SDBStore for sdb_metadata is: " + metaDataSDBStore);
		}
		//metaDataSDBStore.emptyCreateSDBStore(); /*Note: The Dataspace should clear the SDBStore before use*/
				
		/************************************
		 * ADD THE DATA SOURCES TO INTEGRATE *
		 ************************************/
				
		/***
		 * Add new Data source: StudentsRDFSource
		 * 
		 * DataSourceServiceImpl.addDataSource
		 */
		/*if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("StudentsRDFSource","studentsRDFSchema") == null) {
			//Load configuration
			loadConfiguration(studentsRDFPropsLoc);
						
			DataSource studentsRDFSource 
				 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
						 passWord, isRDFSource);
				
			currentDataspace.addDataSource(studentsRDFSource);
			currentDataspace.addSchema(studentsRDFSource.getSchema());
			
			logger.debug("TEST_RDF_Source : " + studentsRDFSource);
			logger.debug("TEST_RDF_Schema : " + studentsRDFSource.getSchema());
		}//end if   
		
		/***
		 * Add new Data source: MagnatuneRDFSmpl RDF Data Source
		 * 
		 * DataSourceServiceImpl.addDataSource
		 */
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("MagnatuneRDFSmpl","mangatuneRDFSchema") == null) {
			//Load configuration
			//loadConfiguration(magnatuneRDFPropsLoc);
			//loadConfiguration(magnatuneRDFmutationPropLoc);
			//For bayes I have changed this to JamendoRDFmutationProps
			loadConfiguration(jamendoRDFmutationPropsLoc);		
						
			/*IMPORTANT: Firstly import RDF to a Jena model and make it persistent
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
						
			DataSource magnatuneRDFSource 
				 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
						 passWord, isRDFSource);
				
			currentDataspace.addDataSource(magnatuneRDFSource);
			currentDataspace.addSchema(magnatuneRDFSource.getSchema());
			
			logger.debug("TEST_RDF_Source : " + magnatuneRDFSource);
			logger.debug("TEST_RDF_Schema : " + magnatuneRDFSource.getSchema());
		}//end if		
		
		
		/***
		 * Add new Data source: JamendoRDFSmpl RDF Data Source
		 * 
		 * DataSourceServiceImpl.addDataSource
		 */
		if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("JamendoRDFSmpl","jamendoRDFSchema") == null) {
			//Load configuration
			loadConfiguration(jamendoRDFPropsLoc);
	
									
			/*IMPORTANT: Firstly import RDF to a Jena model and make it persistent
			loadRDFSourceUtilService.loadRDFtoSDB(rdfSourceName, connectionURL, driverClass,
					                                        rdfSourceURL, userName, passWord);*/
					
			logger.debug("IS_EMPTY? : " + rdfSourceName);
			
			DataSource jamendoRDFSource 
				 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
						 passWord, isRDFSource);
				
			currentDataspace.addDataSource(jamendoRDFSource);
			currentDataspace.addSchema(jamendoRDFSource.getSchema());
			
			logger.debug("TEST_RDF_Source : " + jamendoRDFSource);
			logger.debug("TEST_RDF_Schema : " + jamendoRDFSource.getSchema());
		}//end if			
		 
		
		/***
		 * Add the Global Schema 
		 */
		/*if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("DBTuneIntegrRDF") == null) {
			loadConfiguration(dbTuneIntegrRDFPropsLoc);
			uk.ac.manchester.dstoolkit.domain.models.meta.DataSource dbTuneIntegrDS = dataSourceService.addDataSource(rdfSourceName, null,
					description, driverClass, connectionURL, userName, passWord);
			
			currentDataspace.addDataSource(dbTuneIntegrDS);
			currentDataspace.addSchema(dbTuneIntegrDS.getSchema());
			
			logger.debug("DBTuneIntegrRDF_Source : " + dbTuneIntegrDS);
			logger.debug("DBTuneIntegrRDF_Schema : " + dbTuneIntegrDS.getSchema());
		}*/
		
		
		
		/***
		 * Add new Data source: MondialRDFEurope RDF Data Source
		 * 
		 * DataSourceServiceImpl.addDataSource
		 */
		/*if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("MondialRDFEurope","mondialRDFSchema") == null) {
			//Load configuration
			loadConfiguration(mondialRDFEuropePropsLoc);
						
			/*IMPORTANT: Firstly import RDF to a Jena model and make it persistent
			loadRDFSourceUtilService.loadExternalRDFsource(rdfSourceName, connectionURL, driverClass,
					                                       //rdfSourceURL, userName, passWord)*/
						
			/*DataSource mondialRDFSource 
				 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
						 passWord, isRDFSource);
				
			currentDataspace.addDataSource(mondialRDFSource);
			currentDataspace.addSchema(mondialRDFSource.getSchema());
			
			logger.debug("TEST_RDF_Source : " + mondialRDFSource);
			logger.debug("TEST_RDF_Schema : " + mondialRDFSource.getSchema());
		}//end if 		

		
		
		/***
		 * Add new Data source: Telegraphis RDF Data Source
		 * 
		 * DataSourceServiceImpl.addDataSource
		 */		
		 /*if (externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("TelegraphisRDFCapitalsContinentsCountriesCurrencies","telegraphisRDFSchema") == null) {
			//Load configuration
			loadConfiguration(telegraphisRDFPropsLoc);
			
			/*IMPORTANT: Firstly import RDF to a Jena model and make it persistent
			loadRDFSourceUtilService.loadExternalRDFsource(rdfSourceName, connectionURL, driverClass,
					                                       //rdfSourceURL, userName, passWord)*/
						
			/*DataSource telegraphisRDFSource 
				 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
						 passWord, isRDFSource);
				
			currentDataspace.addDataSource(telegraphisRDFSource);
			currentDataspace.addSchema(telegraphisRDFSource.getSchema());
			
			logger.debug("TEST_RDF_Source : " + telegraphisRDFSource);
			logger.debug("TEST_RDF_Schema : " + telegraphisRDFSource.getSchema());
		}*/ 
		
		
		
		logger.debug("RDF_externalDataSourcePoolUtilService.getAllDataSourceNames(): " + externalDataSourcePoolUtilService.getAllDataSourceNames());
		logger.debug("RDF_externalDataSourcePoolUtilService.getAllDataSourceNames().size(): "
				+ externalDataSourcePoolUtilService.getAllDataSourceNames().size());

		
		//TODO - This is causing an error 
		/*The code below just lists the data sources that have been imported into the Dataspace the reason that I was getting an
		exception might was because the getAllDataSourceName was returing null */
		
		/*for (String dataSourceName : externalDataSourcePoolUtilService.getAllDataSourceNames()) {
			DataSource dataSource = dataSourceRepository.getDataSourceWithSchemaName(dataSourceName);
			logger.debug("RDF_dataSource.getSchema().getName(): " + dataSource.getSchema().getName());
			logger.debug("RDF_dataSource.getConnectionURL(): " + dataSource.getConnectionURL());
		}*/
		
	   /**
	    * Load mappings 
	    */
		//This should load the mappings, this is not working so comment for now.
	   //if (!loadedMappings) {
		 //  logger.debug("predefinedMappingsLoaderService: " + rdfPredefinedMappingsLoaderService);
		 // logger.debug("currentDataspace: " + currentDataspace);
		 //  rdfPredefinedMappingsLoaderService.loadMappingsForSWIM(currentDataspace);
		 //  loadedMappings = true;
	   //}	
		
	}//end setUp()
	
	
	/***
	* @return SDBStoreServiceImpl - object that holds the SDBModel that organises 
	* meta-data information
	*/
	public SDBStoreServiceImpl getSDBGraphsStore() {
		return this.graphsSDBStore;
	}	
		
	/***
	* @return SDBStoreServiceImpl - object that holds the SDBModel that organises 
	* meta-data information
	*/
	public SDBStoreServiceImpl getMetadataSDBStore() {
		return this.metaDataSDBStore;
	}	
	
   /***
   * Method : Load configuration for RDF data sources from .properties files 
   * in src/test/resources.
   * 
   * Comments: 
   *   1. For the RDF case an extra property isRDFSource is added to indicate
   *      that this data source is an RDF Graph.
   *       
   * Issues: 
   *   1. If the Class that extends this Class has a loadConfiguration() then the 
   *   loadConfiguration() of the subClass is called.
   * 	
   * @param fileName
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
