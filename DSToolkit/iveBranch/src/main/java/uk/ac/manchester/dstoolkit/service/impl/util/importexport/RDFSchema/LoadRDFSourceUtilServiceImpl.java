package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.sampling.SamplingUtilService;

import com.hp.hpl.jena.sdb.Store;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

/***
 * This class is responsible for laying the foundation of persistent storage of RDF Graphs.
 * Persistent storage is made possible either by an SDB or TDB. 
 *  - SDB Store: stores the model in a relational database 
 *  - TDB: stores the model on a location on disk  
 * 
 *                                                                                             
 * @author Klitos Christodoulou                                                     
 * 
 * Revision (klitos): 
 *  1. Replace the old ModelRDB that uses Jena 2.6.4 with the new Jena-SDB persistent storage
 *  2. For each RDF graph provided load it into a different Database that holds the SDBStore.
 *  3. Create the SDBStore to be used for storing all imported RDF graphs either from dumps or SPARQL end-points. 
 *  4. Create the SDBStore to be used for storing meta-data from namespaces.  
 *  //NOTE: Finalise 3
 */
@Service(value = "loadRDFSourceUtilService")
public class LoadRDFSourceUtilServiceImpl implements LoadRDFSourceUtilService {
	private static Logger logger = Logger.getLogger(LoadRDFSourceUtilServiceImpl.class);
	
	@Autowired
	private SamplingUtilService samplingUtilService;
	
	/*Create a ComboPooledDataSource to handle multiple connections*/
	private Map<String, SDBStoreServiceImpl> dataSourcePool = new HashMap<String, SDBStoreServiceImpl>();
	
	/*Variables from property file*/
	private String DB_NAME = null;
	private String TDB_DIR;
	private String DB_ENGINE_TYPE;
	private String DRIVER_CLASS;
	private String DB_HOST;
	private String DB_USER;  
	private String DB_PASSWORD; 
	private String DESCRIPTION;
	private final String SDB_HOST = "SDB_HOST";
	
	/*SPARQL endpoint conf files*/
	private String sparqlName;
	private String sparqlURL;
	private String sparqlDumpPath;
	private String training;
	
	/*Constructor*/
	public LoadRDFSourceUtilServiceImpl() {
	}//end constructor	

	/**
	 * This method is responsible for creating a TDB Store Service object on a directory specified.
	 * 
	 * @return - TDBStoreServiceImpl object is responsible for creating the TDB store and allow various
	 * operations over the Store, such as connecting to the store or loading, removing a graph.
	 */
	public TDBStoreServiceImpl createTDBStore(String propertyFilePath) {
	  	logger.debug("in createTDBStore");
		TDBStoreServiceImpl tdbStore = null;		
		try {
			/*Load configuration from file*/
			loadConfiguration(propertyFilePath);
		  	logger.debug("Creating TDB Store: " + DB_NAME);
		  	logger.debug("TDB Store location: " + TDB_DIR);			
			tdbStore = new TDBStoreServiceImpl(TDB_DIR);			
		} catch (Exception exe) {
			logger.error("Exception creating TDB:", exe);  
		}		
		return tdbStore;		
	}//end getTDBStore()
	
	/**
	 * Load an RDF dump to a named graph as specified in its property file
	 */
	 public void loadTDBStore(TDBStoreServiceImpl tdb_store, String sparqlPropFilePath, String targetGraph) {
		 logger.debug("in loadTDBStore");
		 /*Load configuration from file*/
		 loadConfigSPARQL(sparqlPropFilePath);
		 logger.debug("SPARQLserviceName: " + sparqlName);
		 logger.debug("SPARQLserviceURL: " + sparqlURL);
		 logger.debug("SPARQLDumpPath: " + sparqlDumpPath);		 
		 
		 if (training != null) {
			 training = training.trim();
			 if (training.equals("true")){
				 sparqlURL = targetGraph;
			 }
		 }
		 	 
		 logger.debug("Loading data to named graph: " + sparqlURL);		 
		 //tdb_store.loadDataToModelFromRDFDump(sparqlURL, sparqlDumpPath);	
		 
		 /**
		  * This will load the data in a named graph if the model is empty
		  */
		 tdb_store.loadDataToModelIfEmpty(sparqlURL, sparqlDumpPath);
	 }//end loadTDBStore()	
	
	/**
	 * This method opens a connection to the DB that will hold the SDBStores and creates an SQL statement. Then it could
	 * be used to create new DB that will hold Jena's SDBStore, for example:
	 *  - sdb_graphs DB: a generic repository of RDF-Graphs stored as Named Graphs in a single triple store.
	 *  - sdb_store DB: holds the RDF graphs that hold meta-data that are dereferenced by URIs, during the schema enrichment phase.
	 */
	public void createSDBStore(String propertyFilePath) {
	  	/*Output for debug*/
	  	logger.debug("in setupMetadataSDB");
		java.sql.Connection conn = null;

		/*Load configuration from file*/
		loadConfiguration(propertyFilePath);
	  	logger.debug("Creating SDB Store for: " + DB_NAME);
	  	
		try {
		 	/*Obtain a connection to the SQL database*/
			ComboPooledDataSource localhostDS = createComboPoolDS(DRIVER_CLASS,DB_HOST, DB_USER, DB_PASSWORD);

			/*Get the connection for this DataSource*/
			conn = localhostDS.getConnection();
		 
  			/*Check whether the connection has been made successfully*/
			if (conn != null) {
				 logger.debug("RDF_load - Connected to database server, " + DB_NAME);
			 	} else {
				 logger.debug("RDF_load - Cannot connect to database server, " + DB_NAME);
				 /*Release all connections*/
				 DataSources.destroy(localhostDS);
				 throw new SQLException();
			 }//end if			 
			 
			 /*Create the SQL database that will be used by the SDB for this graph*/
			 java.sql.Statement s = (java.sql.Statement) conn.createStatement();		  
			 s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
			 s.close();			 

			 /*Dispose connection after use*/
			 conn.close();			 
		 } catch (SQLException exe) {
			 logger.error("Exception while creating Database RDF_DB : " + DB_NAME +"\n", exe);
		 } catch (Exception exe) {
		 	logger.error("Exception while creating Database for the Model:", exe);  
		 }//end catch			 
	}//end setupMetadataSDB()
	
	/**
	 * Create a new DataSource - connection to an external DB. This object holds the driver for this DB,
	 * hostname, and login details such as username and password in a single object that can be used 
	 * later to get access to all the details of the connection. 
	 * 
	 * @param driverClass
	 * @param url
	 * @param userName
	 * @param pass
	 * @return ComboPooledDataSource - that holds the connection
	 * @throws PropertyVetoException
	 */
	private ComboPooledDataSource createComboPoolDS(String driverClass, String db_host, String userName, String pass) 
			                                                                       throws PropertyVetoException {
		logger.debug("in createComboPoolDS()");
		ComboPooledDataSource newDataSource = new ComboPooledDataSource();
		newDataSource.setDriverClass(driverClass);
		newDataSource.setJdbcUrl(db_host);
		newDataSource.setUser(userName);
		newDataSource.setPassword(pass);
		return newDataSource;
	}//end createComboPoolDS()

	/**
	* Create a new SDBStoreServiceImpl object for sdb_graphs and db_metadata. Run this method after a call
	* to createSDBStore(String propertyFilePath) that creates the relational DB to store the SDBModel.
	*  
	* @return SDBStoreServiceImpl - A class that wraps all the SDBStore functions
	*/
	public SDBStoreServiceImpl getSDBStoreForDB(String propertyFilePath) {
	  	logger.debug("in getSDBStoreForDB()");
	  	Store store = null;
	  	SDBStoreServiceImpl metaDataSDBStore = null;
	  	
		if ((propertyFilePath != null) && !propertyFilePath.equals("")) {
		  	logger.debug("Reading property file...");
			loadConfiguration(propertyFilePath);
		} 
		
		if ((DB_NAME == null) || (DB_NAME == "")) {
			logger.error("Please specify path location for creating the SDBStore");
			return null;
		}
  
		try {
			if (dataSourcePool.containsKey(DB_NAME)) {
				logger.debug("SDBStoreServiceImpl already exists...");
				return dataSourcePool.get(DB_NAME); 
			}
			
			/*Create connection URI*/
			if (!DB_HOST.endsWith("/")) DB_HOST = DB_HOST+"/";
			String JDBC_URL = DB_HOST + DB_NAME;
			logger.debug("jdbc_URL : " + JDBC_URL);	
			
			/*Create a new SDBStoreService object*/
			metaDataSDBStore = new SDBStoreServiceImpl(DRIVER_CLASS, JDBC_URL, DB_USER, DB_PASSWORD);
			
			/*Add it to the Map of connections, if not exists*/
			dataSourcePool.put(DB_NAME, metaDataSDBStore);
			
			/*Create a new SDBStore*/
			int status = metaDataSDBStore.createSDBStore();
			logger.debug("Creating SDBStore status is: " + status);			
		
		} catch (Exception exe) {
			logger.error("Exception metadata SDB:", exe);  
		}//end catch

		return metaDataSDBStore;		
		
	}//end getMetadataSDBStore()	

	/**
	 * Loading RDF: Method used to load an RDF-document into an SDBStore. This method loads each RDF dump into
	 * a separate store. Firstly it creates the relational DB using the name of the source and then it calls
	 * the method to create the necessary schema that holds the SDBModel. An alternative way of DSToolkit to store
	 * RDF Graphs is to have them all stored in a single relational DB as Named Graphs. 
	 * 
	 * This method allows the user to decide how to store the RDF graphs.
	 */
	public void loadRDFtoSDB(String rdfSourceName, String connectionURL, String driverClass,
			String rdfSourceURL, String userName, String passWord) {
	  	
		logger.debug("in loadRDFtoSDB()");
	  	logger.debug("Creating DB for: " + rdfSourceName);
		java.sql.Connection conn = null;
		ComboPooledDataSource localhostDS = null;
	  	SDBStoreServiceImpl sdbStore = null;
		try {
			/*Obtain a connection to the SQL database*/
			localhostDS = createComboPoolDS(driverClass, connectionURL, userName, passWord);

			/*Get the connection for this DataSource*/
			conn = localhostDS.getConnection();
	 
			/*Check whether the connection has been made successfully*/
			if (conn != null) {
				logger.debug("RDF_load - Connected to database server");
		 	} else {
		 		logger.debug("RDF_load - Cannot connect to database server");
		 		/*Release all connections*/
		 		DataSources.destroy(localhostDS);
		 		throw new SQLException();
		 	}//end if	
			
			/*Create the SQL database that will be used by the SDB for this graph*/
			java.sql.Statement s = (java.sql.Statement) conn.createStatement();		  
			s.executeUpdate("CREATE DATABASE IF NOT EXISTS " + rdfSourceName);
			s.close();			 

			/*Dispose connection after use*/
			conn.close();
			
			/*Create connection URI for the creation of SDBStoreServiceImpl*/
			if (!connectionURL.endsWith("/")) connectionURL = connectionURL+"/";
			String JDBC_URL = connectionURL + rdfSourceName;
			logger.debug("Jdbc_URL : " + JDBC_URL);	
			
			/*Create an SDBStoreService object*/
			sdbStore = new SDBStoreServiceImpl(driverClass, JDBC_URL, userName, passWord);
			
			/*Create the new SDBStore*/
			int status = sdbStore.createSDBStore();
			logger.debug("Creating SDBStore for DB: " + rdfSourceName + " and status is: " + status);	

			/*Load data from RDF document to the new SDBStore*/
			//Note: If exception consider using loadDataToModelFile() instead
			sdbStore.loadDataToModel(null, rdfSourceURL);			
		
		} catch (SQLException exe) {
			logger.error("Exception while creating Database RDF_DB : " + rdfSourceName +"\n", exe);
		} catch (Exception exe) {
			logger.error("Exception while creating Database or SDBStore Model:", exe);  
		}//end catch	
	}//end loadRDFtoSDB()

	/***
	 * Loading RDF: This method loads RDF triples from a SPARQL service to a local triple store used by
	 * DSToolkit. The sdb_graphs is the store that holds all the imported sources as Named Graphs in a
	 * single place. 
	 */
	public void loadRDFfromSPARQLtoSDB(String rdfSourceName) {
		logger.debug("in loadRDFfromSPARQLtoSDB()");
		
		//Firstly, check if the sdb_graph database exists and if yes then connect to it
		//Check if the SPARQL service is online
		//then check if the sample parameter is there otherwise load all the triples from the service in the 
		//local triple store sdb_graph in chunks 
		
		try {
			/*Check whether sdb_graphs database has been initialised. This should be done as the first step to initialise DSToolkit*/
			if (!this.databaseExists("sdb_graphs")) {
				throw new Exception("sdb_graphs does not exist");
			}//end if
			
			
			
		} catch (SQLException exe) {
			logger.error("Exception: " + exe);
		} catch (Exception exe) {
	 		logger.warn("Exception while loading triples: " + exe);
		}
	}//end loadRDFfromSPARQLtoSDB()	
	
	/**
	 * Drop the entire SDB database created for that RDF graph.
	 * 
	 * @param rdfSourceName
	 * @param rdfSourceURL
	 */
	public void dropSDBdatabase(String rdfSourceName, String connectionURL, String driverClass,
			 					String userName, String passWord) {
	  	logger.debug("in dropSDBdatabase");
	  	logger.debug("Attempt to delete SDB Store for: " + rdfSourceName);
		ComboPooledDataSource localhostDS = null;
		java.sql.Connection conn = null;
		java.sql.Statement stmt  = null;	
		
		try {
		 	/*Obtain a connection to the SQL database*/
			localhostDS = createComboPoolDS(driverClass,connectionURL, userName, passWord);

			/*Get the connection for this DataSource*/
			conn = localhostDS.getConnection();
		 	
			/*Check whether the connection has been made successfully*/
			if (conn != null) {
				logger.debug("RDF_load - Connected to database server");
		 	} else {
		 		logger.debug("RDF_load - Cannot connect to database server");
		 		/*Release all connections*/
		 		DataSources.destroy(localhostDS);
		 		throw new SQLException();
		 	}//end if	
			
		 	/*Execute the query*/
			java.sql.Statement s = (java.sql.Statement) conn.createStatement();		  
			s.executeUpdate ("DROP DATABASE " + rdfSourceName);
			logger.error("Database : " + rdfSourceName + " deleted successfully.");	
			s.close();			 
			/*Dispose connection after use*/
			conn.close();	 	
		 } catch (SQLException exe) {
			logger.error("Exception while deleting Database RDF_DB : " + rdfSourceName +"\n", exe);
		 } catch (Exception exe) {
		 	logger.error("Exception while deleting SDB Model:", exe);		 	
		 } 
	}//end dropSDBdatabase()
	
	/***
	 * This is used to check whether the database to be used exists. 
	 * Note: Works only with MySQL.  
	 * @return false - if does not exists and true otherwise
	 */
	private boolean databaseExists(String name_db) {
	  	logger.debug("in databaseExists");
	  	
		java.sql.Connection conn = null;
		java.sql.Statement stmt  = null;	
		boolean result = false;
		
		try {
		 	/*Obtain a connection to the SQL database*/
			ComboPooledDataSource localhostDS = createComboPoolDS(DRIVER_CLASS, DB_HOST, DB_USER, DB_PASSWORD);

			/*Get the connection for this DataSource*/
			conn = localhostDS.getConnection();
			
			if (name_db == null || name_db.equals("")) {
				name_db = this.DB_NAME;
			}
			
			/*Check whether the connection has been made successfully*/
			if (conn != null) {
				 logger.debug("RDF_load - Connected to database server");
			 	} else {
				 logger.debug("RDF_load - Cannot connect to database server");
				 /*Release all connections*/
				 DataSources.destroy(localhostDS);
				 throw new SQLException();
			 }//end if	
		
		 	stmt = conn.createStatement();
		 	java.sql.ResultSet rs = stmt.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA" +
		 											  " WHERE SCHEMA_NAME = '" + name_db +"'");

		 	if (rs.next())
		 		result = true;
		 	
		 	stmt.close();
		 	conn.close();
	 	
		 } catch (SQLException exe) {
			logger.error("Exception while searching for DB" + exe);
		 } catch (Exception exe) {
			logger.error("Exception while searching for DB" + exe);		 	
		 }
		
		return result;
	}//end databaseExists()
	
	/***
	 * Load properties file ./src/main/resources/datasources/jenaSDBmeta.properties
	 * @param fileName
	 */
	protected void loadConfiguration(String filePath) {
	 try {
		 logger.debug("in loadRDFSourceInDBImpl:" + filePath);
		 InputStream propertyStream = new FileInputStream(filePath);
		 Properties connectionProperties = new java.util.Properties();
		 connectionProperties.load(propertyStream);

		 DB_NAME = connectionProperties.getProperty("DB_NAME");  
		 TDB_DIR = connectionProperties.getProperty("TDB_DIR"); 
		 DB_ENGINE_TYPE = connectionProperties.getProperty("DB_ENGINE_TYPE"); 
		 DRIVER_CLASS = connectionProperties.getProperty("DRIVER_CLASS");
		 DB_HOST = connectionProperties.getProperty("DB_HOST");
		 DB_USER = connectionProperties.getProperty("DB_USER");		 
		 if (DB_USER != null) { DB_USER = DB_USER.trim(); }		 
		 DB_PASSWORD = connectionProperties.getProperty("DB_PASSWORD");
		 if (DB_PASSWORD != null) { DB_PASSWORD = DB_PASSWORD.trim(); }		 
		 DESCRIPTION = connectionProperties.getProperty("DESCRIPTION");
		 logger.debug("RDF_DB_URL: " + DB_HOST);
		 logger.debug("RDF_DRIVER_CLASS: " + DRIVER_CLASS);
		 logger.debug("RDF_DB_USER: " + DB_USER);
		 logger.debug("RDF_DB_PASSWORD: " + DB_PASSWORD);
		 } catch (FileNotFoundException exc) {
		 	logger.error("RDF_exception raised while loading RDF: " + exc);
		 	exc.printStackTrace();
		 } catch (IOException ioexc) {
		  	logger.error("RDF_properties file not found", ioexc);
		  	ioexc.printStackTrace();
		 }//end catch
	 }//end loadConfiguration()
	
	/**
	 * This method will load .sparql files
	 * Location: ./src/test/resources/sparql_endpoints/
	 */
	protected void loadConfigSPARQL(String filePath) {
		 try {
			  logger.debug("in loadConfigSAPRQL():" + filePath);
			  InputStream propertyStream = new FileInputStream(filePath);
			  Properties connectionProperties = new java.util.Properties();
			  connectionProperties.load(propertyStream);
			  //load
			  sparqlName = connectionProperties.getProperty("SPARQLserviceName");
			  sparqlURL = connectionProperties.getProperty("SPARQLserviceURL");
			  sparqlDumpPath = connectionProperties.getProperty("SPARQLDumpPath");		  	  		  
			  training = connectionProperties.getProperty("training");
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
	 }//end loadConfigSPARQL()	
}//end class