package uk.ac.manchester.dstoolkit.service.meta;

import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.service.GenericEntityService;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.reasoner.Reasoner;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add addDataSource() for RDF
 */
public interface DataSourceService extends GenericEntityService<DataSource, Long> {

	/**
	 * @param dataSourceName
	 * @param driverClass
	 * @param url
	 * @param userName
	 * @param password
	 * @return dataSource
	 */
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String userName,
			String password);

	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String userName,
			String password, String schemaElementsToIncludeOrExcludeFileLocation, boolean isInclude);

	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String schemaUrl,
			String userName, String password);

	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String schemaUrl,
			String userName, String password, String schemaElementsToIncludeOrExcludeFileLocation, boolean isInclude);

	/*RDF/Ontology Source - Jena SDB Store*/
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String schemaUrl,
			String userName, String password, String isRDFSource);
	
	/*RDF/Ontology - Jena TDB Store*/
	public DataSource addDataSource(TDBStoreServiceImpl tdbStore, String dataSourceName, String schemaName, String sourceURI,
			String schemaURI, ModelType mType, OntDocumentManager docMgr, Reasoner reasoner);
	
	public DataSource addDataSource(String tdb_dir_path, String dataSourceName, String schemaName, String sourceURI,
			String schemaURI, ModelType mType, OntDocumentManager docMgr, Reasoner reasoner);
	
	//public DataSource addDataSourceVirtual(String dataSourceName, Schema schemaMerged, String description, String driverClass, String url, String userName,
	//		String password);
	
	/**
	 * @param dataSourceId
	 * @return dataSource
	 */
	public DataSource findDataSource(Long dataSourceId);
	
	public DataSource findDataSourceByName(String dataSourceName);

	/**
	 * @param dataSourceId
	 */
	public void deleteDataSource(Long dataSourceId);

	/**
	 * @param dataSource
	 */
	//public void saveDataSource(DataSource dataSource);

	/**
	 * @param url
	 * @param driverClass
	 * @param userName
	 * @param password
	 * @return
	 */
	//public DataSource createDataSource(String url, String driverClass, String userName, String password);
}
