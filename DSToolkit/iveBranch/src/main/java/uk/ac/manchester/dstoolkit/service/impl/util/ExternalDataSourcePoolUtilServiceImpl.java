/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;

import com.hp.hpl.jena.rdf.model.Model;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * This class holds all connections to all the data models that accepted as Datasources by DSToolkit.
 * 
 * @author chedeler
 * @author klitos
 * 
 * Comment:
 *  1. An RDF datasource need to be added first using LoadRDFSourceUtilService class 
 *   
 * Revision (klitos):
 *  1. Add addNewExternalJenaRDFSource()
 *  2. Edit getAllDataSourceNames()
 *  3. Add getAllJenaRDFDataSourceNames()
 *  4. Add getExternalJenaRDFDataSource()
 *  5. Edit removeExternalDataSource()
 */

@Service(value = "externalDataSourcePoolUtilService")
public class ExternalDataSourcePoolUtilServiceImpl implements ExternalDataSourcePoolUtilService {

	//TODO change this so that it stored the id of the datasource, not the name of the schema
	private final Map<String, ComboPooledDataSource> externalRelationalDataSources = new HashMap<String, ComboPooledDataSource>();
	private final Map<String, XMLResource> externalXmlDataSources = new HashMap<String, XMLResource>();

	/*Hold a Map of Jena SDB stores -  it holds different SDBStoreService objects because each one opens a different connection
	 * to the SDB and each one is stored in a different database */
	private final Map<String, SDBStoreServiceImpl> externalSDBStoreSources = new HashMap<String, SDBStoreServiceImpl>();
	
	/*Hod a Model for that particular RDF graph that is actually connected to a Jena TDB*/
	private final Map<String, Model> externalTDBModels = new HashMap<String, Model>();	

	static Logger logger = Logger.getLogger(ExternalDataSourcePoolUtilServiceImpl.class);

	public ExternalDataSourcePoolUtilServiceImpl() {
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.impl.util.ExternalDataSourcePoolUtilService#addNewExternalDataSource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	/*
	public ComboPooledDataSource addNewExternalRelationalDataSource(String dataSourceName, String url, String driverClass, String userName,
			String password) throws PropertyVetoException {
		//TODO check what kind the dataSource is - relational or XML?
		//assume for now that when no additional schemaURL is provided, it's a relational source
		logger.debug("in addNewExternalRelationalDataSource without schemaName");
		if (externalRelationalDataSources.containsKey(dataSourceName))
			return externalRelationalDataSources.get(dataSourceName);
		ComboPooledDataSource newDataSource = this.addNewExternalRelationalDataSource(dataSourceName, null, url, driverClass, userName, password);
		logger.debug("newDataSource: " + newDataSource);
		return newDataSource;
	}
	*/

	public ComboPooledDataSource addNewExternalRelationalDataSource(String dataSourceName, String schemaName, String url, String driverClass,
			String userName, String password) throws PropertyVetoException {
		//TODO check what kind the dataSource is - relational or XML?
		//assume for now that when no additional schemaURL is provided, it's a relational source
		//TODO check whether I'm storing the schemaName somewhere with the information on the datasource
		logger.debug("in addNewExternalRelationalDataSource with schemaName");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		String dataSourceIdentifier;
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		logger.debug("dataSourceIdentifier: " + dataSourceIdentifier);
		if (externalRelationalDataSources.containsKey(dataSourceIdentifier))
			return externalRelationalDataSources.get(dataSourceIdentifier);
		ComboPooledDataSource newDataSource = new ComboPooledDataSource();
		newDataSource.setDriverClass(driverClass);
		newDataSource.setJdbcUrl(url);
		newDataSource.setUser(userName);
		newDataSource.setPassword(password);
		externalRelationalDataSources.put(dataSourceIdentifier, newDataSource);
		logger.debug("dataSourceIdentifier: " + dataSourceIdentifier);
		logger.debug("newDataSource: " + newDataSource);
		return newDataSource;
	}

	/*
	public XMLResource addNewExternalXmlDataSource(String dataSourceName, String url, String driverClass, String userName, String password)
			throws XMLDBException {
		//TODO check what kind the dataSource is - relational or XML?
		//assume for now that when no additional schemaURL is provided, it's a relational source
		logger.debug("in addNewExternalXmlDataSource without schemaName");
		logger.debug("dataSourceName: " + dataSourceName);
		if (externalXmlDataSources.containsKey(dataSourceName))
			return externalXmlDataSources.get(dataSourceName);

		XMLResource newDocument = this.addNewExternalXmlDataSource(dataSourceName, null, url, driverClass, userName, password);
		return newDocument;
	}
	*/

	public XMLResource addNewExternalXmlDataSource(String dataSourceName, String schemaName, String url, String driverClass, String userName,
			String password) throws XMLDBException {
		//TODO check what kind the dataSource is - relational or XML?
		//assume for now that when no additional schemaURL is provided, it's a relational source
		logger.debug("in addNewExternalXmlDataSource with schemaName");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		String dataSourceIdentifier;
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		logger.debug("dataSourceIdentifier: " + dataSourceIdentifier);

		if (externalXmlDataSources.containsKey(dataSourceIdentifier))
			return externalXmlDataSources.get(dataSourceIdentifier);

		Class cl;
		XMLResource newDocument = null;
		try {
			cl = Class.forName(driverClass);
			logger.debug("cl: " + cl);
			Database newDatabase = (Database) cl.newInstance();
			logger.debug("newDatabase: " + newDatabase);
			DatabaseManager.registerDatabase(newDatabase);
			String collectionUrl = null;
			String documentName = null;
			if (url.endsWith(".xml")) {
				logger.debug("url ends with xml - remove document name to get collection url");
				logger.debug("url: " + url);
				collectionUrl = url.substring(0, url.lastIndexOf("/"));
				logger.debug("collectionUrl: " + collectionUrl);
				documentName = url.substring(url.lastIndexOf("/") + 1);
				logger.debug("documentName: " + documentName);
			} else {
				collectionUrl = url;
				documentName = url;
			}
			logger.debug("collectionUrl: " + collectionUrl);
			Collection newCollection = DatabaseManager.getCollection(collectionUrl, userName, password);
			logger.debug("newCollection: " + newCollection);
			newDocument = (XMLResource) newCollection.getResource(documentName);
			externalXmlDataSources.put(dataSourceIdentifier, newDocument);
			logger.debug("newDocument: " + newDocument);
			logger.debug("added new xml dataSource: " + dataSourceIdentifier);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException");
			e.printStackTrace();
		} catch (InstantiationException e) {
			logger.error("InstantiationException");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error("IllegalAccessException");
			e.printStackTrace();
		}
		logger.debug("newDocument: " + newDocument);
		return newDocument;
	}

	
	/***
	 * Method to import RDF source or Ontologies that hold individuals into a triple store. This method uses Jena's TDB triple store
	 * that uses files to store an RDF graph, than Jena SDB libraries.
	 *
	 * 
	 * @param graphsTDBStore - an object that encapsulates the functionality of Jena TDB store
	 * @param dataSourceName - the name of the source
	 * @param dataSourceURI - a URI or a file path that points to the location of the RDF graph or Ontology
	 * @param isOntology - declaring whether the source is an ontology or not
	 * @return Model, this can be used as a base Model for an ontology.
	 */
	public Model addNewExternalTDBSource(TDBStoreServiceImpl graphsTDBStore, String dataSourceName, String sourceURI, ModelType mType) {
		//This is an RDFSource that has instance level data
		String namedGraphForRDFsource = "x-ns://source.data/rdf/graph/";
		
		//This is an ontology that contains some instance-level data
		String namedGraphForOntology = "x-ns://source.data/onto/";				
		
		//Look for the Model in the TDB Store. The functionality of the TDB Store is encapsulated inside a TDBStoreServiceImpl object
		Model model = null;
		StringBuilder namedGraphURI = null;		

		try {
			
			if (mType.equals(mType.ONTOLOGY)) {			
				logger.debug("this is an ontology");
				namedGraphURI = new StringBuilder(namedGraphForOntology).append(dataSourceName);
				
				logger.debug("namedGraphURI: " + namedGraphURI);
				
				model = graphsTDBStore.getModel(namedGraphURI.toString());
				
				externalTDBModels.put(namedGraphURI.toString(), model);				
				
				return model;
			} else if (mType.equals(mType.RDF)) {
				logger.debug("this is an RDF source");
				namedGraphURI = new StringBuilder(namedGraphForRDFsource).append(dataSourceName);
				
				model = graphsTDBStore.getModel(namedGraphURI.toString());
				
				//If the source is an RDF Source then read the data into a Named Graph in the TDB Store
				if (sourceURI != null && !sourceURI.equals("")) {    
					graphsTDBStore.loadDataToModelIfEmptyNoTransaction(model, sourceURI);
				}		
				
				externalTDBModels.put(namedGraphURI.toString(), model);
				
				return model;
			}//end else
			
		} catch (Exception exe) {
			logger.error("Error - Exception while retreiving Model from Jena TDB.");
			exe.printStackTrace();
		}
		
		return null;
	}
	
	
	/***
	 * Method : Import an external RDF source from RDF_DB 
	 * 
	 * Obtain a new SDBStoreServiceImpl object that holds:
	 * 	- connection to the DB.
	 *  - creates the SDBStore tables and indexes.
	 *  - connects to the SDBStore
	 *  - can provide a Model over the SDBStore
	 *  - load RDF documents in the SDBStore etc.
	 * 
	 * @throws SQLException 
	 */
	public SDBStoreServiceImpl addNewExternalJenaRDFSource(String dataSourceName, String schemaName, String connectionURL, String driverClass, String userName,
			String passWord, String DB_ENGINE_NAME) {

		/*Output for debug*/
		logger.debug("in addNewExternalJenaRDFSource with schemaName");
		logger.debug("RDF_dataSourceName: " + dataSourceName);
		logger.debug("RDF_schemaName: " + schemaName);
		logger.debug("RDF_Database URL: " + connectionURL);
		logger.debug("RDF_engineType: " + DB_ENGINE_NAME);
		
		String dataSourceIdentifier;
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		logger.debug("RDF_dataSourceIdentifier: " + dataSourceIdentifier);
	
		/*Check whether dataSourceName exists in Map<String, Store> and return it*/
		if (externalSDBStoreSources.containsKey(dataSourceIdentifier))
			return externalSDBStoreSources.get(dataSourceIdentifier);

		SDBStoreServiceImpl sdbStoreService = null;

		try {
			/*Create the DB URL to open connection to*/
			if (!connectionURL.endsWith("/")) connectionURL = connectionURL+"/";
			String JDBC_URL = connectionURL + dataSourceName;
			logger.debug("New Jdbc_URL : " + JDBC_URL);	
			
			/*Create an SDBStoreService object*/			
			sdbStoreService = new SDBStoreServiceImpl(driverClass, JDBC_URL, userName, passWord);	
			
			/*Create the new SDBStore, force it to connect to the SDBStore in DB*/
			int status = sdbStoreService.createSDBStore();
			logger.debug("Creating SDBStore for DB: " + dataSourceName + " and status is: " + status);

			/*Save the name of this datasource in its SDBStoreServiceImpl object*/
			sdbStoreService.setSDBstoreName(dataSourceIdentifier);
			
  			/*Add the source to the Map*/
			externalSDBStoreSources.put(dataSourceIdentifier, sdbStoreService);

			//logger.debug("SDBStoreServiceImpl object is : " + sdbStoreService);
			logger.debug("added new RDF dataSource: " + dataSourceName);
			logger.debug("RDF_dataSourceIdentifier: " + dataSourceIdentifier);
		} catch (Exception exe) {
			logger.error("RDF_Exception");
			exe.printStackTrace();
		}//catch	  

		return sdbStoreService;
	}//end method

	/***
	 * Get the names of all Data Sources as a Set
	 * 
	 * @return Set<String>
	 */
	public Set<String> getAllDataSourceNames() {
		Set<String> dataSourceNames = new HashSet<String>();
		dataSourceNames.addAll(externalRelationalDataSources.keySet());
		dataSourceNames.addAll(externalXmlDataSources.keySet());
		dataSourceNames.addAll(externalSDBStoreSources.keySet());
		return dataSourceNames;
	}

	public Set<String> getAllRelationalDataSourceNames() {
		return externalRelationalDataSources.keySet();
	}

	public Set<String> getAllXmlDataSourceNames() {
		return externalXmlDataSources.keySet();
	}

	/***
	 * Get the names of all RDF Data Sources as a Set
	 * 
	 * @return Set<String>
	 */
	public Set<String> getAllJenaRDFDataSourceNames() {
		return externalSDBStoreSources.keySet();
	}

	public ComboPooledDataSource getExternalRelationalDataSource(String dataSourceName, String schemaName) {
		String dataSourceIdentifier;
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		//TODO might be better to throw an exception instead of return null
		if (externalRelationalDataSources.containsKey(dataSourceIdentifier))
			return externalRelationalDataSources.get(dataSourceIdentifier);
		return null;
	}

	public ComboPooledDataSource getExternalRelationalDataSource(String dataSourceName) {
		//TODO might be better to throw an exception instead of return null
		logger.debug("dataSourceName: " + dataSourceName);
		if (externalRelationalDataSources.containsKey(dataSourceName))
			return externalRelationalDataSources.get(dataSourceName);
		return null;
	}

	public XMLResource getExternalXmlDataSource(String dataSourceName, String schemaName) {
		//TODO might be better to throw an exception instead of return null
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		String dataSourceIdentifier;
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		if (externalXmlDataSources.containsKey(dataSourceIdentifier)) {
			logger.debug("contains xml dataSource: " + dataSourceIdentifier);
			logger.debug("externalXmlDataSources.get(dataSourceName): " + externalXmlDataSources.get(dataSourceIdentifier));
			return externalXmlDataSources.get(dataSourceIdentifier);
		}
		return null;
	}

	public XMLResource getExternalXmlDataSource(String dataSourceName) {
		//TODO might be better to throw an exception instead of return null
		logger.debug("dataSourceName: " + dataSourceName);
		if (externalXmlDataSources.containsKey(dataSourceName)) {
			logger.debug("contains xml dataSource: " + dataSourceName);
			logger.debug("externalXmlDataSources.get(dataSourceName): " + externalXmlDataSources.get(dataSourceName));
			return externalXmlDataSources.get(dataSourceName);
		}
		return null;
	}	
	
	/***
	 * This method returns a Jena Model which can also be used as the base model for an OntoModel 
	 * representing an Ontology that we can make inference above it.
	 * 
	 *
	 * @param rdfSourceName - can either be an RDF source or an ontology
	 * @param inferredSchema - is the name to the inferredSchema if known
	 * @return Model
	 */
	public Model getExternalJenaModelFromTDBStore(String rdfSourceName, String inferredSchema) {
		logger.debug("in RDF_getExternalJenaModelFromTDBStore()");
		logger.debug("RDFSourceName: " + rdfSourceName);
		logger.debug("inferredSchema: " + inferredSchema);
		
		String dataSourceIdentifier;
		if (inferredSchema != null)
			dataSourceIdentifier = rdfSourceName + "_" + inferredSchema;
		else
			dataSourceIdentifier = rdfSourceName;
		
		if (externalTDBModels.containsKey(dataSourceIdentifier)) {
			logger.debug("contains Jena TDB Model: " + dataSourceIdentifier);
			logger.debug("externalTDBModels.get(dataSourceName): " + externalTDBModels.get(dataSourceIdentifier));
			return externalTDBModels.get(dataSourceIdentifier);
		}
		
		return null;		
	}	
	
	/***
	 * Return a Jena RDF model for the specific dataSourceName.
	 * 
	 * @param dataSourceName - The name of the RDF source to return (part of data source name identifier)
	 * @param schemaName - Name of schema (part of data source name identifier)
	 * @return an SDB Store - This method returns an entire SDBStore object because we assume that each 
	 * RDF source has its own database in Jena SDB, whereas in the case of our TDB store we assume a single
	 * TDB store that stores RDF sources into different Named Graphs.
	 */
	public SDBStoreServiceImpl getExternalJenaRDFDataSource(String dataSourceName, String schemaName) {
		//TODO might be better to throw an exception instead of return null
		logger.debug("in RDF_getExternalJenaRDFDataSource()");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		String dataSourceIdentifier;
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		
		logger.debug("RDF_dataSourceIdentifier: " + dataSourceIdentifier);
		
		if (externalSDBStoreSources.containsKey(dataSourceIdentifier)) {
			logger.debug("contains Jena RDF dataSource: " + dataSourceIdentifier);
			logger.debug("externalJenaRDFSources.get(dataSourceName): " + externalSDBStoreSources.get(dataSourceIdentifier));
			return externalSDBStoreSources.get(dataSourceIdentifier);
		}
	
		return null;
	}

	/***
	 * Return a Jena RDF model for the specific dataSourceName
	 * 
	 * @param dataSourceName - The name of the RDF source to return
	 * @return ModelRDB
	 */
	public SDBStoreServiceImpl getExternalJenaRDFDataSource(String dataSourceName) {
		//TODO: might be better to throw an exception instead of return null
		logger.debug("dataSourceName: " + dataSourceName);
		if (externalSDBStoreSources.containsKey(dataSourceName)) {
			logger.debug("contains Jena RDF dataSource: " + dataSourceName);
			logger.debug("externalJenaRDFSources.get(dataSourceName): " + externalSDBStoreSources.get(dataSourceName));
			return externalSDBStoreSources.get(dataSourceName);
		}
		return null;
	}

	public void removeExternalDataSource(String dataSourceName, String schemaName) {
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		String dataSourceIdentifier;
		if (schemaName != null)
			dataSourceIdentifier = dataSourceName + "_" + schemaName;
		else
			dataSourceIdentifier = dataSourceName;
		logger.debug("dataSourceIdentifier: " + dataSourceIdentifier);
		this.removeExternalDataSource(dataSourceName);
	}

	public void removeExternalDataSource(String dataSourceName) {
		logger.debug("dataSourceName: " + dataSourceName);
		if (externalRelationalDataSources.containsKey(dataSourceName)) {
			ComboPooledDataSource dataSource = externalRelationalDataSources.get(dataSourceName);
			dataSource.close();
			externalRelationalDataSources.remove(dataSourceName);
		} else if (externalXmlDataSources.containsKey(dataSourceName)) {
			XMLResource dataSource = externalXmlDataSources.get(dataSourceName);
			try {
				if (dataSource.getParentCollection().isOpen())
					dataSource.getParentCollection().close();
				externalXmlDataSources.remove(dataSourceName);
			} catch (XMLDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (externalSDBStoreSources.containsKey(dataSourceName)) {
			SDBStoreServiceImpl dataSource = externalSDBStoreSources.get(dataSourceName);
			dataSource.closeJDBCConnection();
			dataSource.closePooledDataSource();
			externalSDBStoreSources.remove(dataSourceName);
		}
	}

}//end class
