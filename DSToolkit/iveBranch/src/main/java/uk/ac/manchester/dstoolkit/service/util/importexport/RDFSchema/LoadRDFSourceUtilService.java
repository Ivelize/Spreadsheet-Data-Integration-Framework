package uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema;

import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;

/***
 * Interface for LoadRDFSourceUtilServiceImpl
 * 
 * @author Klitos Christodoulou 
 */

public interface LoadRDFSourceUtilService {
 
	/**
	 * Create DB that will hold the SDBStores, sdb_graphs and  sdb_metadata
	 */
	public void createSDBStore(String propertyFilePath);
 
	/** 	
	 * Load RDF Source to a RDB
	 * 
	 * @param rdfSourceName
	 * @param connectionURL
	 * @param driverClass
	 * @param engineType
	 * @param rdfSourceURL
	 * @param userName
	 */	
	public void loadRDFtoSDB(String rdfSourceName, String connectionURL, String driverClass,
							 String rdfSourceURL, String userName, String passWord);
	/**
	 * Remove existing RDF Source from a RDB
	 * 
	 * @param rdfSourceName
	 */
	public void dropSDBdatabase(String rdfSourceName, String connectionURL, String driverClass,
								String userName, String passWord);
	
	public SDBStoreServiceImpl getSDBStoreForDB(String propertyFilePath);
	
	public TDBStoreServiceImpl createTDBStore(String propertyFilePath);
		
	public void loadTDBStore(TDBStoreServiceImpl tdb_store, String sparqlPropFilePath, String targetGraph);
}//end class