package uk.ac.manchester.dstoolkit.service.util;

import java.beans.PropertyVetoException;
import java.util.Set;

import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;

import com.hp.hpl.jena.rdf.model.Model;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add addNewExternalJenaRDFSource()  
 *  2. Add getExternalJenaRDFDataSource(String) 
 *  3. getExternalJenaRDFDataSource(String, String)
 *  3. Add getAllJenaRDFDataSourceNames() 
 *  4. Revise methods for SDB jena store models
 */

public interface ExternalDataSourcePoolUtilService {

	/*
	public abstract ComboPooledDataSource addNewExternalRelationalDataSource(String dataSourceName, String url, String driverClass, String userName,
			String password) throws PropertyVetoException;
	*/

	public abstract ComboPooledDataSource addNewExternalRelationalDataSource(String dataSourceName, String schemaName, String url,
			String driverClass, String userName, String password) throws PropertyVetoException;

	/*
	public abstract XMLResource addNewExternalXmlDataSource(String dataSourceName, String url, String driverClass, String userName, String password)
			throws XMLDBException;
	*/

	public abstract XMLResource addNewExternalXmlDataSource(String dataSourceName, String schemaName, String url, String driverClass,
			String userName, String password) throws XMLDBException;
	
	/*RDF in Jena SDB Store*/
	public SDBStoreServiceImpl addNewExternalJenaRDFSource(String dataSourceName, String schemaName, String connectionURL, String driverClass, String userName,
			String passWord, String DB_ENGINE_NAME);
	
	/*RDF in Jena TDB Store*/
	public Model addNewExternalTDBSource(TDBStoreServiceImpl graphsTDBStore, String dataSourceName, String sourceURI, ModelType mType);

	public abstract ComboPooledDataSource getExternalRelationalDataSource(String dataSourceName, String schemaName);

	public abstract ComboPooledDataSource getExternalRelationalDataSource(String dataSourceName);

	public abstract XMLResource getExternalXmlDataSource(String dataSourceName, String schemaName);

	public abstract XMLResource getExternalXmlDataSource(String dataSourceName);
	
	/*RDF*/
	public abstract SDBStoreServiceImpl getExternalJenaRDFDataSource(String dataSourceName);
	
	public abstract SDBStoreServiceImpl getExternalJenaRDFDataSource(String dataSourceName, String schemaName);

	public abstract Set<String> getAllDataSourceNames();

	public abstract Set<String> getAllRelationalDataSourceNames();

	public abstract Set<String> getAllXmlDataSourceNames();
	
	/*RDF*/
	public abstract Set<String>	getAllJenaRDFDataSourceNames();

	public abstract void removeExternalDataSource(String dataSourceName, String schemaName);

	public abstract void removeExternalDataSource(String dataSourceName);
	
	public Model getExternalJenaModelFromTDBStore(String rdfSourceName, String inferredSchema);

}