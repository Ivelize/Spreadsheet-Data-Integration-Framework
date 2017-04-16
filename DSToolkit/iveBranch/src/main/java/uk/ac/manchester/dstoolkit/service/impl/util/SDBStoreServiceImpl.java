package uk.ac.manchester.dstoolkit.service.impl.util;

import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import uk.ac.manchester.dstoolkit.service.impl.morphisms.matching.agent.ActionStatus;
import uk.ac.manchester.dstoolkit.service.util.SDBStoreService;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.hp.hpl.jena.util.FileManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;


/***
 * This class holds:
 *   - ComboPooledDataSource connection to a DB.
 *   - 
 * 
 * @author Klitos Christodoulou
 *
 */
public class SDBStoreServiceImpl implements SDBStoreService {

	static Logger logger = Logger.getLogger(SDBStoreServiceImpl.class);

	private LayoutType DEFAULT_LAYOUT = LayoutType.LayoutTripleNodesIndex;
	private boolean storeIsClosed = false;
	private boolean isInitialised = false;
	/*The SDB Store already exists in the database but it is BLANK*/
	private static final int SDB_STORE_BLANK = -1;
	/*The SDB Store already exists in the database and has RDF statements*/
	private static final int SDB_STORE_NOT_EMPTY = -2;
	/*SDB Store has been created from scratch in the Database*/
	private static final int SDB_STORE_CREATED = 1;
	
	private String DRIVER_CLASS = null;
	private String DB_HOST = null;
	private String DB_USER = null;
	private String DB_PASS = null;
	private DatabaseType DB_TYPE = null;
	private String sdbStoreName = null;
	
	private ComboPooledDataSource dataSource;
	private java.sql.Connection jdbcConnection = null;
	private Dataset dataset = null;
	private Store sdbStore = null;
	private SDBConnection sdbConnection = null;
	
	/**
	 * Constructor 1: Provide the details for the ComboPooledDataSource object to be created.
	 * 		DRIVER_CLASS
	 * 		DB_HOST
	 * 		DB_USER
	 * 		DB_PASSWORD
	 */
	public SDBStoreServiceImpl(String driverClass, String db_host, String db_user, String db_pass) {
		this.DRIVER_CLASS = driverClass;
		this.DB_HOST = db_host;
		this.DB_USER = db_user;
		this.DB_PASS = db_pass;
		
		initConnection();
	}
	
	/**
	 * Constructor 2: Construct an SDB store by providing connection details through a 
	 * ComboPooledDataSource object.
	 * 
	 * @param dataSource
	 */
	public SDBStoreServiceImpl(ComboPooledDataSource dataSource) {
		this.dataSource = dataSource;
	}
		
	/**
	 * 
	 * @return true  - if the connection has been initialised successfully
	 *  	   false - if the connection has not been initialised successfully
	 */
	private boolean initConnection() {
		logger.debug("in initConnection()");
		boolean result = false;
		
		if (isInitialised) {
			result = true;
		}

		try {			
			dataSource = new ComboPooledDataSource();
			dataSource.setDriverClass(this.DRIVER_CLASS);
			dataSource.setJdbcUrl(this.DB_HOST);
			dataSource.setUser(this.DB_USER);
			dataSource.setPassword(this.DB_PASS);			
			
			/*Initialise the connection*/
			if (dataSource.getConnection() != null) {
				this.isInitialised = true;
			}
		} catch(Exception exe) {
			logger.error("Exception while ", exe);		
			result = false;
		}
		
		return result;			
	}//end initConnection() 
	
	
	/**
	 * Check if the store is created for the first time and if so create the store by formatting 
	 * the DB to get the necessary tables and indexes required for the SDB Store
	 *
	 * @return 
     */
	public int createSDBStore() {
		logger.debug("in createSDBStore()");
		int result = 0;
		
		/*Once a store is obtained it needs to create tables and indexes in the backed DB*/
		Store store = this.getSDBStore();
		try {
			/*When the tables in the DB for the store has not been created then the store
			 *returns an exception on getSize()*/
			long sdbStoreSize = store.getSize();
			logger.debug("SDBStore size is: " + sdbStoreSize);
			if (sdbStoreSize > 0) {
				result = this.SDB_STORE_NOT_EMPTY;
			} else {
				result = this.SDB_STORE_BLANK;
			}			
		} catch (Exception exe) {			
			logger.error("Create DB tables for SDBStore");
			emptyCreateSDBStore();
			result = this.SDB_STORE_CREATED;			
		}
		return result;		
	}//end createSDBStore()
		
	/**
	 * 
	 * Note: This method will empty the tables from the database and then create the SDBStore again to
	 * store the Graph.
	 */
	public void emptyCreateSDBStore() {
		logger.debug("in emptyCreateSDBStore()");        
		try {
			getSDBStore().getTableFormatter().create();
		} catch (Exception exe) {
            logger.error("Unable to createSDBStore()" + exe);
		}			
	}//end deleteAndCreateSDBStore()
	
	
	public void removeSDBStore() {
		logger.debug("in removeSDBStore()");
        
		try {
			getSDBStore().getTableFormatter().truncate();
		} catch (Exception exe) {
            logger.error("Unable to createSDBStore()" + exe);
		}			
	}//end deleteAndCreateSDBStore()	
			
	/**
	 * Retrieves a com.hp.hpl.jena.query.Dataset object, representing 1 or more
     * Jena models. Dataset includes both the default and named graphs.
	 * @return
	 */
	public Store getSDBStore() {
		logger.debug("in getSDBStore()");
		if (storeIsClosed) {
			sdbStore = this.reconnect();
			return this.sdbStore;
		}
		
		if (sdbStore != null) {
			return this.sdbStore;
		}	
		
		if (dataSource == null) {
			return null;
		}		
		
		/*SDBStore is null/does not exists so connect to it*/
		StoreDesc storeDesc = new StoreDesc(DEFAULT_LAYOUT, getDBType());
		/*Now that we have the description obtain the Store object on the connection*/
		sdbStore = SDBFactory.connectStore(getSDBConnection(), storeDesc);	
		logger.debug("sdbStore : " + sdbStore);

		return sdbStore;		
	}//end getSDBStore()
	
	/**
	 * @return SDBConnection
	 */
	private SDBConnection getSDBConnection() {
		logger.debug("in getSDBConnection()");
		if (sdbConnection != null) {
			return this.sdbConnection;
		}		
		
		try {
			logger.debug("Creating a new SDBConnection");
			sdbConnection = new SDBConnection(dataSource.getConnection());			
		} catch (SQLException exe) {
			logger.error("Error while getting DataSource connection ", exe);		
			return null;
		}				
		return sdbConnection;		
	}//end getSDBConnection()
		
	/**
	 * A Jena model can be connected to one Graph in the store and used with all the Jena API operations.
	 * 
	 * Note: Ensure that the SDBStore connection has been made and the SDBStore has created in the database.
	 * 
	 * @param modelName - The name of the Graph to retrieve. If null or empty then retrieve the default graph.
	 * 		  
	 * @return a Jena Model object
	 */
    public Model getModel(String modelName) {
		logger.debug("in getModel()");
    	Model model = null;    	
    	Store store = getSDBStore();
        if (modelName == null || modelName.equals("")) {
        	model = SDBFactory.connectDefaultModel(store);
        } else {
        	model = SDBFactory.connectNamedModel(store, modelName);  
        }
        return model;
    }//end getModel()
    
    /**
     * Using Jena OntoModel 
     * 
     * //TODO: Change this to use a proper reasoner if needed, like Pellet. Or see whether Jena reasoners could do the trick.
     * 
     * @param modelName
     * @return OntModel
     */
    public OntModel getOntModel(String modelName) {
		logger.debug("in getOntModel()");
		OntModel ontModel = null;
		//Get the base Jena model
		Model baseModel = this.getModel(modelName);
		if (baseModel != null) {
			//OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
			OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
			ontModel = ModelFactory.createOntologyModel(modelSpec, baseModel);
			return ontModel;
		}    	
    	return null;
    }//end getOntModel()    
    
    /**
     * @param baseModel - provide the model
     * @return OntModel
     */
    public OntModel getOntModel(Model baseModel) {
		logger.debug("in getOntModel(Model)");
		OntModel ontModel = null;
		if (baseModel != null) {
			//OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
			OntModelSpec modelSpec = new OntModelSpec(OntModelSpec.OWL_MEM);
			//TODO - a model maker here this does not work, because it access all ontologies from the web
			ontModel = ModelFactory.createOntologyModel(modelSpec, baseModel);
			
			return ontModel;
		}    	
    	return null;
    }//end getOntModel()  
    
    /**
     * Check whether this named model exists in the SDBStore
     * @param modelName
     * @return a Jena Model object if exists otherwise null
     */
    public boolean nameModelExists(String modelName) {
		logger.debug("in modelExists()");
		boolean exists = false;
    	Model model = null;    	
    	Store store = getSDBStore();
    	
    	model = SDBFactory.connectNamedModel(store, modelName);
    	
    	if (model != null) {
    		if (model.isEmpty()) {
    			exists = false;
    		} else {
    			exists = true;
    		}
    	}//end if   	
    	
    	return exists;
    }//end nameModelExists()

    /***
     * Load RDF-triples into a Graph, either the default Graph or a NamedGraph
     * @param modelName - name of the Graph to load the data to 
     * @param sourceURL - path or URI
     */
    public void loadDataToModelFile(String modelName, String sourceURL) {
		logger.debug("in loadDataToModelFile()");
    	Model model = this.getModel(modelName);
		FileManager.get().readModel(model, sourceURL);		
	
		//Close the store after read to force it to commit.
		closeSDBStore();
    }//end loadDataToModel()    
    
    /***
     * Load RDF-triples into a Graph, either the default Graph or a NamedGraph
     * @param modelName - name of the Graph to load the data to 
     * @param sourceURL - path or URI
     */
    public ActionStatus loadDataToModelFileWithStatus(String modelName, String sourceURL) {
		logger.debug("in loadDataToModelFile()");
    	Model model = this.getModel(modelName);
    	
    	try {
    		FileManager.get().readModel(model, sourceURL);
    	} catch (Exception exe) {
   			logger.error("Exception while reading URI: " + exe);
   			closeSDBStore();
   			return new ActionStatus(0, exe.getClass().getSimpleName());
    	}
		
		//Close the store after read to force it to commit.
		closeSDBStore();
		return new ActionStatus(1, "Loading Dump: OK");
    }//end loadDataToModel()    
    
    /***
     * Load RDF-triples into a Graph, either the default Graph or a NamedGraph
     * 
     * @param modelName - name of the Graph to load the data to 
     * @param sourceURL - path or URI
     */
    public ActionStatus loadDataToModel(String modelName, String sourceURL) {
		logger.debug("in loadDataToModel()");
    	Model model = this.getModel(modelName);

    	try {
    	 model.read(sourceURL);	
    	 } catch (Exception exe) {   		 
    		 try {
        		 logger.error("URI does not exists, retry using namespace URI: " + modelName); 
    			 model.read(modelName);	
    		 } catch (Exception e) {
        		 logger.error("Exception while reading URI: " + exe);
                 return new ActionStatus(0, exe.getClass().getSimpleName());
    		 }
    	 }
		//Close the store after read to force it to commit.
		//Note: if closeSDBStore() is disabled I do not get the SQLException warning, it seems to work smoothly. 
    	
    	//closeSDBStore();
		return new ActionStatus(1, "Dereferencing URI OK");
    }//end loadDataToModel()     
    
    /***
     * Load RDF-triples into a Graph, either the default Graph or a NamedGraph
     * 
     * Note: We assume that the RDF document retrieved has no xml:base uri.
     * 
     * //TODO: find the xml:base uri by looking the XML document with a parser and
     *  then provide it to the method, instead of null.  
     *
     * @param modelName - name of the Graph to load the data to 
     * @param inputeStream - pass the input stream to be read as a Graph
     */
    public void loadStreamToModel(String modelName, BufferedReader inputStream, String baseURI) {
		logger.debug("in loadStreamToModel()");
    	Model model = this.getModel(modelName);
    	try {
    		if ((baseURI == null) || (baseURI.endsWith(""))) {
        		model.read(inputStream, null);
    		} else {
        		model.read(inputStream, baseURI);    			
    		}    	
    	} catch (Exception exe) {
    		logger.debug("Error reading model, propably because it has baseURI.");
    		try {
    			baseURI = modelName;
    			model.read(inputStream, baseURI);
    		} catch (Exception e) {
    			logger.debug("Error reading model with uri as base uri.");
    		}
    	}//end catch
		//Close the store after read to force it to commit.
		//closeSDBStore();
    }//end loadDataToModel()   
    
    /**
     * SDB handle queries made on an RDF dataset which is of the SDB class DatasetStore
     * 
     * @return - Dataset representing 1 or more Jena Models
     *         - Dataset: includes both the default and named Graphs
     */
    public Dataset getDataset() {
		logger.debug("in getDataset()");
    	if (dataset == null) {
    		dataset = SDBFactory.connectDataset(getSDBStore());    		
    	}    	
    	return this.dataset;
    }//end getDataset()
       
    /**
     * Return an ArrayList<String> that has the URIs of all the NamedGraphs in the
     * Dataset which are stored in this SDB Store.
     */
    public ArrayList<String> getNamedGraphs() {
		logger.debug("in getNamedGraphs()");
    	ArrayList<String> array = new ArrayList<String>();
    	
		Dataset dataset = this.getDataset();

		Iterator<String> graphNames = dataset.listNames();
		while (graphNames.hasNext()) {
		    String graphName = graphNames.next();
		    array.add(graphName);
		}
		return array;
    }//end getNamedGraphs()    
    
    /**
     * Connect to the SDBStore that exists in the sdb_metadata table, 
     * and then remove the specified NamedGraph from the store 
     * 
     * @param modelName - the URI of the NamedGraph that exists in the Dataset
     */
    public void removeNamedGraph(String modelName) {
		logger.debug("in removeNamedGraph()");
    	Model model = this.getModel(modelName);

		model.begin();
		model.removeAll();
		model.commit();   	
    }//end removeNamedGraph()  
        
	/**
	 * Method that closes the SDBStore but not the JDBC connection.
	 */
	public void closeSDBStore() {
		logger.debug("in closeSDBStore()");
		if (!storeIsClosed) {
			try {
				logger.debug("Closing SDBStore");
				this.sdbStore.close();
				this.storeIsClosed = true;				
			} catch (Exception exe) {
				logger.error("Error while closing SDBStore ", exe);
			}
		}//end if
	}//end closeSDBStore()
	
	/**
	 * Close the underlying JDBC connection of this SDBStore
	 */
	public void closeJDBCConnection() {
		logger.debug("in closeJDBCConnection()");
        try {
            sdbStore.getConnection().close();
            sdbStore.close();
            this.storeIsClosed = true;
        } catch (Exception exe) {
            logger.error("Error closing JDBC connection: " + exe);
        }		
	}//end closeJDBCConnection()	
	
	/**
	 * Reconnect to SDBStore
	 * @return
	 */
    public Store reconnect() {
		logger.debug("in reconnect()");
    	if(storeIsClosed){
    			logger.debug("SDBStore is closed trying to reconnect to store");
        		StoreDesc storeDesc = new StoreDesc(DEFAULT_LAYOUT, getDBType());
        		sdbStore = SDBFactory.connectStore(getSDBConnection(), storeDesc);	
                storeIsClosed = false;                  
        }        
        return this.sdbStore;
    }//end reconnect()
    	
	/**
	 * This method will return the ComboPooledDataSource object that holds the connection.
	 * @return ComboPooledDataSource
	 */
	public ComboPooledDataSource returnPooledDataSource() {
		return this.dataSource;
	}//end returnPooledDataSource()
	
	/**
	 * @return java.sql.Connection object
	 * @throws SQLException
	 */
	public java.sql.Connection getJdbcConnection() throws SQLException {
		if (!isInitialised) {
			boolean hasInitialised = initConnection();
			if (!hasInitialised) {
				return null;
			}
		}
		if (jdbcConnection == null) {
			jdbcConnection = dataSource.getConnection();
		}		
		return jdbcConnection;		
	}//end getJdbcConnection() 
	
	/**
	 * @return DatabaseType
	 */
	private DatabaseType getDBType() {
		if (this.DB_TYPE != null) {
			return DB_TYPE;
		}
		
		String connectionURL = DB_HOST.toLowerCase();
		String driverClass	 = DRIVER_CLASS.toLowerCase();
		 
		if (connectionURL.contains("mysql") && driverClass.contains("mysql")) {
			DB_TYPE = DatabaseType.MySQL;
			return DB_TYPE;
		} else if (connectionURL.contains("postgresql") && driverClass.contains("postgresql")) {
			DB_TYPE = DatabaseType.PostgreSQL;	
			return DB_TYPE;
		} else if (connectionURL.contains("derby") && driverClass.contains("derby")) {
			DB_TYPE = DatabaseType.Derby;
			return DB_TYPE;
		} else {
			logger.debug("Invalid DB engine type");
			return null;
		}		
	}//end getDBType()
	
	/**
	 * Set LayoutType
	 * 	- LayoutTripleNodesIndex
	 *  - layout2/index
	 *  etc.
	 * @param type
	 */
	public void setDBLayout(LayoutType type) {
		this.DEFAULT_LAYOUT = type;
	}//end setDBLayout()
	
	/**
	 * Give a name to this SDBStore
	 * @param name - The name of this SDBStore
	 */
	public void setSDBstoreName(String name) {
		this.sdbStoreName = name;
	}//end setSDBstoreName()

	/**
	 * Return the name of this SDBStore
	 * @return
	 */
	public String getSDBstoreName() {
		return this.sdbStoreName;
	}//end setSDBstoreName()
	
	/**
	 * Return DataSource that holds the connection for this ComboPooledDataSource
	 * @return
	 */
	public ComboPooledDataSource getDataSource() {
		return this.dataSource;
	}//end getDataSource()
	
	/**
	 * Close the connection to the Datasource and release resources.
	 */
	public void closePooledDataSource() {		
		logger.debug("in closePooledDataSource()");
        try {
    		if (this.dataset != null) {
    			dataset.close();
    		}//end if
        } catch (Exception exe) {
            logger.error("Error closing ComboPooledDataSource: " + exe);
        }		
	}//end closePooledDataSource()
	
}//end class