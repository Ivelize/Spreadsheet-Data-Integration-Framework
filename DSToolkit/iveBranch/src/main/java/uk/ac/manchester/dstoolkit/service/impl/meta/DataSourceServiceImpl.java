package uk.ac.manchester.dstoolkit.service.impl.meta;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.DataTypeMapper;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstractMIDSTSuperModelType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstractModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexicalMIDSTSuperModelType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexicalModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipMIDSTSuperModelType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.ModelType;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.GenericRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.ParticipationOfCMCInSuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.canonical.SuperRelationshipRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.TDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema.ImportRDFSchemaFromXMLServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.ontology.ImportOntologyServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.GetColumnInformationOfTableServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.GetColumnInformationOfTableServiceImpl.ColumnInformation;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.GetForeignKeyInformationOfTableServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.GetForeignKeyInformationOfTableServiceImpl.ForeignKeyInformation;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.GetPrimaryKeysOfTableServiceImpl;
import uk.ac.manchester.dstoolkit.service.impl.util.importexport.relational.GetTableNamesServiceImpl;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFModel.ImportRDFModelService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.ImportRDFSchemaFromXMLService;
import uk.ac.manchester.dstoolkit.service.util.importexport.RDFSchema.LoadRDFSourceUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.ontology.ImportOntologyService;
import uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoexclude.ImportSchemaElementsToExcludeFromXMLService;
import uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoinclude.ImportSchemaElementsToIncludeFromXMLService;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sun.xml.xsom.XSAttContainer;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.XmlString;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * @author chedeler
 *
 * Revision (klitos):
 *  1. Add new method addDataSource() for RDF
 *  2. Edit determineModelType(), add RDF
 *  3. Revise addDataSource() to be used with the new SDB persistent storage
 *  4. Add some functionality for storing RDF graphs in a Jena TDB store
 */

//@Transactional(readOnly = true)
@Service(value = "dataSourceService")
public class DataSourceServiceImpl extends GenericEntityServiceImpl<DataSource, Long> implements DataSourceService {

	//TODO check that for foreign keys I check that they're both in the same schema
	//TODO ignore foreign keys across schemas for now
	//TODO the loading of the schema should probably be moved elsewhere so that it can be reused for loading manually provided integration schemas ...
	//TODO won't quite work for relational though, as a manually provided relational schema would be provided in DDL, rather than obtained by accessing corresponding database
	//TODO will work for XML though ... think about this
	//TODO might be better to move XML processing bit in the corresponding importexport package, similar to relational ...
	//TODO finish off inclusion/exclusion for XML, doesn't work at the moment as haven't solved issue with multiple local complex elements with the same name ...
	//TODO currently need to list each complex element separately in inclusion/exclusion file, which isn't going to work when there are multiple with the same name
	//TODO as I'm currently assuming that there is only one per name ...
	//TODO test inclusion/exclusion

	@Autowired
	@Qualifier("importSchemaElementsToIncludeFromXMLService")
	private ImportSchemaElementsToIncludeFromXMLService importSchemaElementsToIncludeFromXmlService;

	@Autowired
	@Qualifier("importSchemaElementsToExcludeFromXMLService")
	private ImportSchemaElementsToExcludeFromXMLService importSchemaElementsToExcludeFromXmlService;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	static Logger logger = Logger.getLogger(DataSourceServiceImpl.class);

	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	//@Autowired
	//@Qualifier("superAbstractRepository")
	//private SuperAbstractRepository superAbstractRepository;

	@Autowired
	@Qualifier("superRelationshipRepository")
	private SuperRelationshipRepository superRelationshipRepository;

	@Autowired
	@Qualifier("participationOfCMCInSuperRelationshipRepository")
	private ParticipationOfCMCInSuperRelationshipRepository participationRepository;

	@Autowired
	@Qualifier("importRDFModelService")
	private ImportRDFModelService importRDFModelService;
	
	@Autowired
	@Qualifier("loadRDFSourceUtilService")
	private LoadRDFSourceUtilService loadRDFSourceUtilService;
	
	//private final boolean gotSchemaElementsToIncludeForSchemaName = false;
	//private final boolean gotSchemaElementsToExcludeForSchemaName = false;

	private DataSource dataSource;
	private Schema schema;
	//private SuperAbstract xmlRootSuperAbstract;

	private ComboPooledDataSource externalRelationalDataSource;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private XMLResource externalXmlResource;
	
	/*Hold reference to a SDBStoreServiceImpl - in this case each RDF source has its own connection to its own database*/
	private SDBStoreServiceImpl externalRDFSDBSource;
		
	/*Hold a reference to the TDBStoreServiceImpl - in this case a sinlge TDB location is used to store a TDB Dataset, that 
	 * maintain a different Named Graph for each RDF source stored using the TDB Store*/
	private TDBStoreServiceImpl graphsTDBStore;	

	private final List<XSIdentityConstraint> allConstraintsWithoutParentSA = new ArrayList<XSIdentityConstraint>();
	private final Map<SuperAbstract, List<XSIdentityConstraint>> allConstraints = new HashMap<SuperAbstract, List<XSIdentityConstraint>>();
	private final Map<String, SuperAbstract> globalSuperAbstractElements = new HashMap<String, SuperAbstract>();
	private final Map<String, SuperLexical> globalSuperLexicalElements = new HashMap<String, SuperLexical>();

	//private final Map<String, ElementsToInclude> schemaNameElementsToIncludeMap = null;

	/*
	private String dataSourceName;
	private String driverClass;
	private String url;
	private String userName;
	private String password;
	*/

	public DataSourceServiceImpl() {
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.operators.OperatorService#execute()
	 */

	//TODO sort out minOccurs and maxOccurs in XSD, will have to be attached to the superRelationship nest

	//TODO add check what type the datasource is and move relational stuff in private method
	//TODO assume for now that if schemaUrl isn't provided it's a relational dataSource

	//@Transactional
	//(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional (readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String userName,
			String password) {
		logger.debug("in addDataSource_Relational");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		logger.debug("description: " + description);
		logger.debug("driverClass: " + driverClass);
		logger.debug("url: " + url);
		logger.debug("userName: " + userName);
		logger.debug("password: " + password);

		dataSource = createDataSource(url, driverClass, userName, password);
		dataSource.setName(dataSourceName);
		dataSource.setDescription(description);

		if (url.contains("jdbc")) {
			try {
				externalRelationalDataSource = externalDataSourcePoolUtilService.addNewExternalRelationalDataSource(dataSourceName, schemaName, url,
						driverClass, userName, password);
				logger.debug("externalRelationalDataSource: " + externalRelationalDataSource);
				simpleJdbcTemplate = new SimpleJdbcTemplate(externalRelationalDataSource);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			logger.debug("dataSourceName: " + dataSourceName);
			logger.debug("schemaName: " + schemaName);
			if (schemaName == null)
				schemaName = dataSourceName;
			schema = createSchema(schemaName);
			logger.debug("schema: " + schema);
			schema.setDataSource(dataSource);
			dataSource.setSchema(schema);
			saveDataSource(dataSource);
			List<String> tableNames = getTableNames(schemaName, externalRelationalDataSource);
			logger.debug("tableNames: " + tableNames);
			Map<String, SuperAbstract> nameSuperAbstractsMap = createSuperAbstractsForTablesAndAddToSchema(schemaName, tableNames, null);
			logger.debug("nameSuperAbstractsMap: " + nameSuperAbstractsMap);
			createAndAddSuperLexicalsForSuperAbstracts(schemaName, nameSuperAbstractsMap, null);
			logger.debug("schema.name: " + schema.getName());
			saveSchemaInformation(schema);
			createAndAddSuperRelationshipsForSuperAbstracts(schemaName, nameSuperAbstractsMap, null);
			return dataSource;
		} else
			logger.error("not relational source as expected");
		return null;
	}
	
	@Transactional (readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public DataSource addDataSourceFromSpreadsheets(String dataSourceName, String schemaName, String description, String driverClass, String url, String userName,
			String password) {
		logger.debug("in addDataSource_Relational");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		logger.debug("description: " + description);
		logger.debug("driverClass: " + driverClass);
		logger.debug("url: " + url);
		logger.debug("userName: " + userName);
		logger.debug("password: " + password);

		dataSource = createDataSource(url, driverClass, userName, password);
		dataSource.setName(dataSourceName);
		dataSource.setDescription(description);

		if (url.contains("jdbc")) {
			try {
				externalRelationalDataSource = externalDataSourcePoolUtilService.addNewExternalRelationalDataSource(dataSourceName, schemaName, url,
						driverClass, userName, password);
				logger.debug("externalRelationalDataSource: " + externalRelationalDataSource);
				simpleJdbcTemplate = new SimpleJdbcTemplate(externalRelationalDataSource);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
			logger.debug("dataSourceName: " + dataSourceName);
			logger.debug("schemaName: " + schemaName);
			if (schemaName == null)
				schemaName = dataSourceName;
			schema = createSchema(schemaName);
			logger.debug("schema: " + schema);
			schema.setDataSource(dataSource);
			dataSource.setSchema(schema);
			saveDataSource(dataSource);
			List<String> tableNames = getTableNames(schemaName, externalRelationalDataSource);
			logger.debug("tableNames: " + tableNames);
			Map<String, SuperAbstract> nameSuperAbstractsMap = createSuperAbstractsForTablesAndAddToSchema(schemaName, tableNames, null);
			logger.debug("nameSuperAbstractsMap: " + nameSuperAbstractsMap);
			createAndAddSuperLexicalsForSuperAbstracts(schemaName, nameSuperAbstractsMap, null);
			logger.debug("schema.name: " + schema.getName());
			saveSchemaInformation(schema);
			createAndAddSuperRelationshipsForSuperAbstracts(schemaName, nameSuperAbstractsMap, null);
			return dataSource;
		} else
			logger.error("not relational source as expected");
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.meta.DataSourceService#addDataSourceVirtual(java.lang.String, uk.ac.manchester.dstoolkit.domain.models.meta.Schema, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
//	@Transactional (readOnly = false, propagation = Propagation.REQUIRES_NEW)
//	public DataSource addDataSourceVirtual(String dataSourceName, Schema schemaMerged, String description, String driverClass, String url, String userName,
//			String password) {
//		dataSource = createDataSource(url, driverClass, userName, password);
//		dataSource.setName(dataSourceName);
//		dataSource.setDescription(description);
//
//		if (url.contains("jdbc")) {
//			try {
//
//				logger.debug("externalRelationalDataSource: " + externalRelationalDataSource);
//				simpleJdbcTemplate = new SimpleJdbcTemplate(externalRelationalDataSource);
//			} catch (PropertyVetoException e) {
//				e.printStackTrace();
//			}
//			logger.debug("dataSourceName: " + dataSourceName);
//			logger.debug("schemaName: " + schemaName);
//			
//			if (schemaName == null)
//				schemaName = dataSourceName;
//			schema = createSchema(schemaName);
//			
//			logger.debug("schema: " + schema);
//			schema.setDataSource(dataSource);
//			dataSource.setSchema(schema);
//			
//			saveDataSource(dataSource);
//			
//			List<String> tableNames = schemaMerged.getSuperAbstracts()
//			logger.debug("tableNames: " + tableNames);
//			
//			Map<String, SuperAbstract> nameSuperAbstractsMap = createSuperAbstractsForTablesAndAddToSchema(schemaName, tableNames, null);
//			logger.debug("nameSuperAbstractsMap: " + nameSuperAbstractsMap);
//			
//			createAndAddSuperLexicalsForSuperAbstracts(schemaName, nameSuperAbstractsMap, null);
//			
//			logger.debug("schema.name: " + schema.getName());
//			saveSchemaInformation(schema);
//			
//			createAndAddSuperRelationshipsForSuperAbstracts(schemaName, nameSuperAbstractsMap, null);
//			
//			return dataSource;
//		} else
//			logger.error("not relational source as expected");
//		return null;
//	}
	
	@Transactional
	//(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String userName,
			String password, String schemaElementsToIncludeOrExcludeFileLocation, boolean isInclude) {
		logger.debug("in addDataSource");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		logger.debug("description: " + description);
		logger.debug("driverClass: " + driverClass);
		logger.debug("url: " + url);
		logger.debug("userName: " + userName);
		logger.debug("password: " + password);
		logger.debug("schemaElementsToIncludeOrExcludeFileLocation: " + schemaElementsToIncludeOrExcludeFileLocation);
		logger.debug("isInclude: " + isInclude);

		dataSource = createDataSource(url, driverClass, userName, password);
		dataSource.setName(dataSourceName);
		dataSource.setDescription(description);
		logger.debug("dataSource: " + dataSource);
		if (schemaElementsToIncludeOrExcludeFileLocation != null) {
			if (isInclude)
				importSchemaElementsToIncludeFromXmlService.readSchemaElementsToIncludeFromXml(schemaElementsToIncludeOrExcludeFileLocation);
			else
				importSchemaElementsToExcludeFromXmlService.readSchemaElementsToExcludeFromXml(schemaElementsToIncludeOrExcludeFileLocation);
		}
		//TODO can check in driverClass and url what kind of database it is
		//TODO this could go wrong if it's not a relational datasource - sort this
		if (url.contains("jdbc")) {
			try {
				externalRelationalDataSource = externalDataSourcePoolUtilService.addNewExternalRelationalDataSource(dataSourceName, schemaName, url,
						driverClass, userName, password);
				logger.debug("externalRelationalDataSource: " + externalRelationalDataSource);
				simpleJdbcTemplate = new SimpleJdbcTemplate(externalRelationalDataSource);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}

			logger.debug("dataSourceName: " + dataSourceName);
			logger.debug("schemaName: " + schemaName);
			if (schemaName == null)
				schemaName = dataSourceName;
			schema = createSchema(schemaName);
			logger.debug("schema: " + schema);
			schema.setDataSource(dataSource);
			dataSource.setSchema(schema);
			saveDataSource(dataSource);
			List<String> tableNames = getTableNames(schemaName, externalRelationalDataSource);
			logger.debug("tableNames: " + tableNames);
			Map<String, SuperAbstract> nameSuperAbstractsMap = createSuperAbstractsForTablesAndAddToSchema(schemaName, tableNames, isInclude);
			logger.debug("nameSuperAbstractsMap: " + nameSuperAbstractsMap);
			createAndAddSuperLexicalsForSuperAbstracts(schemaName, nameSuperAbstractsMap, isInclude);
			logger.debug("schema.name: " + schema.getName());
			saveSchemaInformation(schema);
			createAndAddSuperRelationshipsForSuperAbstracts(schemaName, nameSuperAbstractsMap, isInclude);
			return dataSource;

		} else
			logger.error("not relational source as expected");

		return null;
	}

	@Transactional
	//(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String schemaUrl,
			String userName, String password) {
		logger.debug("in addDataSource");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		logger.debug("description: " + description);
		logger.debug("driverClass: " + driverClass);
		logger.debug("url: " + url);
		logger.debug("schemaUrl: " + schemaUrl);
		logger.debug("userName: " + userName);
		logger.debug("password: " + password);

		dataSource = createDataSource(url, schemaUrl, driverClass, userName, password);
		dataSource.setDescription(description);
		logger.debug("dataSource: " + dataSource);

		//TODO this could go wrong if it's not a XML datasource - sort this
		if (url.contains("xmldb")) {
			try {
				externalXmlResource = externalDataSourcePoolUtilService.addNewExternalXmlDataSource(dataSourceName, schemaName, url, driverClass,
						userName, password);
				logger.debug("externalXmlResource: " + externalXmlResource);

			} catch (XMLDBException e) {
				e.printStackTrace();
			}
			logger.debug("dataSourceName: " + dataSourceName);
			logger.debug("schemaName: " + schemaName);
			if (schemaName == null)
				schemaName = dataSourceName;
			schema = createSchema(schemaName);
			logger.debug("schema: " + schema);
			schema.setDataSource(dataSource);
			dataSource.setSchema(schema);
			saveDataSource(dataSource);
			saveSchemaInformation(schema);
			parseSchemaInformationFromSchemaFile(schema, schemaUrl, null);
			return dataSource;
		} else
			logger.error("not xml source as expected");
		return null;
	}

	//TODO add check what type the datasource is and move xml stuff in private method
	//TODO assume for now that if schemaUrl is provided it's an XML dataSource
	@Transactional
	//(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String url, String schemaUrl,
			String userName, String password, String schemaElementsToIncludeOrExcludeFileLocation, boolean isInclude) {
		logger.debug("in addDataSource");
		logger.debug("dataSourceName: " + dataSourceName);
		logger.debug("schemaName: " + schemaName);
		logger.debug("description: " + description);
		logger.debug("driverClass: " + driverClass);
		logger.debug("url: " + url);
		logger.debug("schemaUrl: " + schemaUrl);
		logger.debug("userName: " + userName);
		logger.debug("password: " + password);
		logger.debug("schemaElementsToIncludeOrExcludeFileLocation: " + schemaElementsToIncludeOrExcludeFileLocation);
		logger.debug("isInclude: " + isInclude);
		dataSource = createDataSource(url, schemaUrl, driverClass, userName, password);
		dataSource.setDescription(description);
		logger.debug("dataSource: " + dataSource);
		if (schemaElementsToIncludeOrExcludeFileLocation != null) {
			importSchemaElementsToIncludeFromXmlService.readSchemaElementsToIncludeFromXml(schemaElementsToIncludeOrExcludeFileLocation);
		}
		//TODO this could go wrong if it's not a XML datasource - sort this
		if (url.contains("xmldb")) {
			try {
				externalXmlResource = externalDataSourcePoolUtilService.addNewExternalXmlDataSource(dataSourceName, schemaName, url, driverClass,
						userName, password);
				logger.debug("externalXmlResource: " + externalXmlResource);

			} catch (XMLDBException e) {
				e.printStackTrace();
			}

			logger.debug("dataSourceName: " + dataSourceName);
			logger.debug("schemaName: " + schemaName);
			if (schemaName == null)
				schemaName = dataSourceName;
			schema = createSchema(schemaName);
			logger.debug("schema: " + schema);
			schema.setDataSource(dataSource);
			dataSource.setSchema(schema);
			saveDataSource(dataSource);
			saveSchemaInformation(schema);
			parseSchemaInformationFromSchemaFile(schema, schemaUrl, isInclude);

			return dataSource;
		} else
			logger.error("not xml source as expected");
		return null;
	}
	
	
	/***
	 * Method that creates a DSToolkit DataSource. The data for this kind of a DataSource are backed up 
	 * by a Jena TDB Store. The RDF source in this case is stores as a Jena Model. A Datasource here 
	 * can by an Ontology as well 
	 * 
	 * @param tdb_dir_path - the file path to the directory to create the TDB store needs to be given
	 * @param docMgr - control how to process an ontology document e.g., whether to load the imports etc.
	 * @param reasoner - this is an optional argument and is only set when dealing with ontologies 
	 * @return DataSource
	 */
	@Transactional
	public DataSource addDataSource(String tdb_dir_path, String dataSourceName, String schemaName, String sourceURI,
											String schemaURI, ModelType mType, OntDocumentManager docMgr, Reasoner reasoner) {		
		logger.debug("in TDB_addDataSource()");
		logger.debug("Backed by Jena TDB Store");	
		
		TDBStoreServiceImpl tdbStore = null;
		ImportRDFSchemaFromXMLService importRDFSchemaFromXMLService = null;
		ImportOntologyService importOntologyService = null;
		Model model = null;
		
		try {
			//This will create the TDB Store in the particular location if does not exists.
			tdbStore = loadRDFSourceUtilService.createTDBStore(tdb_dir_path);
			graphsTDBStore = tdbStore;
						
			dataSource = new DataSource(dataSourceName, sourceURI, schemaURI); 
			dataSource.setDescription("Datasource as a Jena Model");
			
			//Add a new external RDF source or ontology into a TDB Dataset, each is stored in a different Named Graph
			if (graphsTDBStore != null) {
				model = externalDataSourcePoolUtilService.addNewExternalTDBSource(tdbStore, dataSourceName, sourceURI, mType);
			}
			
			//Check is source is an ontology - If ontology then extract a schema from the TBox of an ontology
			if (mType.equals(ModelType.ONTOLOGY)) {
				logger.info("Model Type: " + ModelType.ONTOLOGY);
				
				//load the base model with the ontology and then used that model to create an OntModel
				if (model != null) {
					tdbStore.loadDataToModelIfEmptyNoTransaction(model, sourceURI);
				}
				
				/**
				 * We assume that an ontology contains ABox and TBox, we are interested here to get a schema 
				 * representation considering the TBox of the ontology.
				 */				
				OntModel ontoModel = tdbStore.getOntModel(model, OntModelSpec.OWL_MEM, docMgr, reasoner);
				
				/*Create a new Schema object to add the super model constructs from the ontology. 
				 The schema is then added to a DSToolkit Datasource object*/
				if (schemaName == null)
					schemaName = dataSourceName;
				
				schema = createSchema(schemaName);
				schema.setDataSource(dataSource);
				dataSource.setSchema(schema);
				
				//Create an internal representation of an Ontology so that it maps the super model.
				logger.info("Infer schema from ontology (TBox)");
				importOntologyService = new ImportOntologyServiceImpl();
				importOntologyService.init(ontoModel, schema, superRelationshipRepository, participationRepository);
				importOntologyService.mapOntologyToSuperModel();
				
				dataSource.setSchema(schema);
				
				/*Save data source to DataSourceRepository*/
				saveDataSource(dataSource);
				
				//add information to schema here
				/*Save Schema to the repository of Schemas*/
				saveSchemaInformation(schema);				
				
				return dataSource;
			} else {
				logger.info("Model Type: " + ModelType.RDF);
				
				importRDFSchemaFromXMLService = new ImportRDFSchemaFromXMLServiceImpl();
				importRDFSchemaFromXMLService.readRDFSchemaElementsFromXml(schemaURI);	
				
				/*Create a schema to attach this DataSource*/
				if (schemaName == null)
					schemaName = dataSourceName;
				
				schema = createSchema(schemaName);
				logger.debug("RDF_schema: " + schema);
				schema.setDataSource(dataSource);

				dataSource.setSchema(schema);
				
				/*Save data source to DataSourceRepository*/
				saveDataSource(dataSource);
				
				//add information to schema here
				/*Save Schema to the repository of Schemas*/
				saveSchemaInformation(schema);
				
				/*Parse schema data structure and populate the SuperModel*/
				importRDFModelService.init(superRelationshipRepository, participationRepository);
				importRDFModelService.populateRDFModelToCanonicalModel(importRDFSchemaFromXMLService, schema);

				return dataSource;			
			}//end else
			
		} catch  (Exception e) {
			logger.error("Error - creating Datasource using Jena TDB.");
			e.printStackTrace();
		}	
		
		return null;		
	}	
	
	
	/***
	 * Method: Create a DSToolkit Datasource that is backed by a Jena TDB store
	 * 
	 * @param TDBStoreServiceImpl graphsTDBStore
	 * @return DataSource
	 * 
	 */
	@Transactional
	public DataSource addDataSource(TDBStoreServiceImpl tdbStore, String dataSourceName, String schemaName, String sourceURI,
														String schemaURI, ModelType mType, OntDocumentManager docMgr, Reasoner reasoner) {
		
		logger.debug("in TDB_addDataSource()");
		logger.debug("Backed by Jena TDB Store");	
		
		ImportRDFSchemaFromXMLService importRDFSchemaFromXMLService = null;
		ImportOntologyService importOntologyService = null;
		Model model = null;
		Dataset dataset = null;
		
		try {
			graphsTDBStore = tdbStore;
			
			//Open a WRITE transaction to the triple store
	 		dataset = tdbStore.getDataset();
	 		dataset.begin(ReadWrite.WRITE);
						
			dataSource = new DataSource(dataSourceName, sourceURI, schemaURI); 
			dataSource.setDescription("Datasource as a Jena Model");
			
			//Add a new external RDF source or ontology into a TDB Dataset, each is stored in a different Named Graph
			if (graphsTDBStore != null) {
				model = externalDataSourcePoolUtilService.addNewExternalTDBSource(tdbStore, dataSourceName, sourceURI, mType);
			}
			
			//Check is source is an ontology - If ontology then extract a schema from the TBox of an ontology
			if (mType.equals(ModelType.ONTOLOGY)) {
				logger.info("Model Type: " + ModelType.ONTOLOGY);
								
				//load the base model with the ontology and then used that model to create an OntModel
				if (model != null) {
					tdbStore.loadDataToModelIfEmptyNoTransaction(model, sourceURI);
				}
				
				/**
				 * We assume that an ontology contains ABox and TBox, we are interested here to get a schema 
				 * representation considering the TBox of the ontology.
				 */				
				OntModel ontoModel = tdbStore.getOntModel(model, OntModelSpec.OWL_MEM, docMgr, reasoner);
							
				/*Create a new Schema object to add the super model constructs from the ontology. 
				 The schema is then added to a DSToolkit Datasource object*/
				if (schemaName == null)
					schemaName = dataSourceName;
				
				schema = createSchema(schemaName);
				schema.setDataSource(dataSource);
				dataSource.setSchema(schema);
				
				//Create an internal representation of an Ontology so that it maps the super model.
				logger.info("Infer schema from ontology (TBox)");
				importOntologyService = new ImportOntologyServiceImpl();
				importOntologyService.init(ontoModel, schema, superRelationshipRepository, participationRepository);
				importOntologyService.mapOntologyToSuperModel();			
				
				dataSource.setSchema(schema);
				
				/*Save data source to DataSourceRepository*/
				saveDataSource(dataSource);
				
				//add information to schema here
				/*Save Schema to the repository of Schemas*/
				saveSchemaInformation(schema);	
				
				logger.debug("dataSource: " + dataSource);				

			} else {
				logger.info("Model Type: " + ModelType.RDF);
				
				importRDFSchemaFromXMLService = new ImportRDFSchemaFromXMLServiceImpl();
				importRDFSchemaFromXMLService.readRDFSchemaElementsFromXml(schemaURI);	
				
				/*Create a schema to attach this DataSource*/
				if (schemaName == null)
					schemaName = dataSourceName;
				
				schema = createSchema(schemaName);
				logger.debug("RDF_schema: " + schema);
				schema.setDataSource(dataSource);

				dataSource.setSchema(schema);
				
				/*Save data source to DataSourceRepository*/
				saveDataSource(dataSource);
				
				//add information to schema here
				/*Save Schema to the repository of Schemas*/
				saveSchemaInformation(schema);
				
				/*Parse schema data structure and populate the SuperModel*/
				importRDFModelService.init(superRelationshipRepository, participationRepository);
				importRDFModelService.populateRDFModelToCanonicalModel(importRDFSchemaFromXMLService, schema);
	
			}//end else
					
			//commit the transaction
		    dataset.commit();			
			
		} finally { 
 			try {
 				dataset.end();
 			} catch (Exception exe) {
 				logger.error("Error - creating Datasource using Jena TDB." + exe.getLocalizedMessage());  		   
 	 		} 	 		
 		} 	
		
		return dataSource;	
		
	}//end addDataSource	
	
	/***
	 * Method: Add new RDFDataSource
	 * 
	 * @return DataSource
	 */
	@Transactional (readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public DataSource addDataSource(String dataSourceName, String schemaName, String description, String driverClass, String connectionURL, String schemaUrl,
			String userName, String password, String isRDFSource) {		

		logger.debug("in RDF_addDataSource()");
		logger.debug("RDF_dataSourceName: " + dataSourceName);
		logger.debug("RDF_schemaName: " + schemaName);
		logger.debug("RDF_connectionUrl: " + connectionURL);
		logger.debug("RDF_schemaUrl: " + schemaUrl);
		logger.debug("RDF_driverClass: " + driverClass);
		logger.debug("RDF_userName: " + userName);
		logger.debug("RDF_password: " + password);
		logger.debug("RDF_isRDFSource: " + isRDFSource);		
		logger.debug("RDF_description: " + description);
		
		String DB_ENGINE_NAME = "";
		
		dataSource = createDataSource(dataSourceName, connectionURL, schemaUrl, driverClass, userName, password, isRDFSource);
		dataSource.setDescription(description);
		/*Get the engine type from driverClass*/
		DB_ENGINE_NAME = dataSource.getEngineType();
		
		logger.debug("RDF_dataSource: " + dataSource);
		
		/*Call method to parse RDF-schema information from RDF Schema to extra Layer*/
		//TODO: The layer is not persistent	
		
		ImportRDFSchemaFromXMLService importRDFSchemaFromXMLService = new ImportRDFSchemaFromXMLServiceImpl();
		importRDFSchemaFromXMLService.readRDFSchemaElementsFromXml(schemaUrl);	
		
		/*Check whether this source is RDF*/
		if (isRDFSource.equals("true")) {
			try {
			 externalRDFSDBSource = externalDataSourcePoolUtilService.addNewExternalJenaRDFSource(dataSourceName, schemaName, connectionURL, driverClass,
					   userName, password, DB_ENGINE_NAME);
			 
		 
			 logger.debug("RDF_externalRDFSource: " + externalRDFSDBSource);

			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug("RDF_dataSourceName: " + dataSourceName);
			logger.debug("RDF_schemaName: " + schemaName);
			logger.debug("RDF_Is RDF: " + isRDFSource);
			
			/*Create a schema to attach this DataSource*/
			if (schemaName == null)
				schemaName = dataSourceName;
			
			schema = createSchema(schemaName);
			logger.debug("RDF_schema: " + schema);
			schema.setDataSource(dataSource);

			dataSource.setSchema(schema);
			
			/*Save data source to DataSourceRepository*/
			saveDataSource(dataSource);
			
			//add information to schema here
			/*Save Schema to the repository of Schemas*/
			saveSchemaInformation(schema);
			
			/*Parse schema data structure and populate the SuperModel*/
			importRDFModelService.init(superRelationshipRepository, participationRepository);
			importRDFModelService.populateRDFModelToCanonicalModel(importRDFSchemaFromXMLService, schema);

			return dataSource;
		} else
			//logger.error("not RDF source as expected");
		return null;
	}//end addDataSource()
		
	/*
	private SuperAbstract findSuperAbstractWithNameAndCheckParent(Schema schema, String superAbstractName, SuperAbstract parentSuperAbstract) {
		//TODO decide what to do with attributes and childElements
		SuperAbstract superAbstract = superAbstractRepository.getSuperAbstractByNameInSchemaWithName(superAbstractName, schema.getName());
		if (superAbstract != null) {
			logger.debug("found superAbstract with same name in same schema");
			logger.debug("superAbstract: " + superAbstract);
			if (parentSuperAbstract != superAbstract.getParentSuperAbstract()) {
				logger.error("different parents ... something wrong - TODO sort this");
				logger.debug("parentSuperAbstract: " + parentSuperAbstract);
				logger.debug("superAbstract.getParentSuperAbstract(): " + superAbstract.getParentSuperAbstract());
			} else {
				//TODO check child elements, superAbstracts and superLexicals
				logger.debug("same parents ... might still need to check childElements though - TODO");
				return superAbstract;
			}
		}
		return null;
	}
	*/

	private void processElement(Schema schema, SuperAbstract parentSuperAbstract, XSElementDecl element, int minOccurs, int maxOccurs,
			Boolean isInclude) {
		logger.debug("in processElement");
		logger.debug("element: " + element);
		logger.debug("parentSuperAbstract: " + parentSuperAbstract);
		logger.debug("minOccurs: " + minOccurs);
		logger.debug("maxOccurs: " + maxOccurs);
		String name = element.getName();
		logger.debug("element.getName(): " + name);
		logger.debug("element.getType(): " + element.getType());
		logger.debug("element.getTargetNamespace(): " + element.getTargetNamespace());

		XSType elementType = element.getType();
		logger.debug("element.isElementDecl(): " + element.isElementDecl());
		logger.debug("element.isLocal(): " + element.isLocal());
		logger.debug("element.isGlobal(): " + element.isGlobal());
		logger.debug("element.isAbstract(): " + element.isAbstract());
		logger.debug("element.isModelGroup(): " + element.isModelGroup());
		logger.debug("element.isModelGroupDecl(): " + element.isModelGroupDecl());
		logger.debug("element.isNillable(): " + element.isNillable());
		logger.debug("element.isWildcard(): " + element.isWildcard());

		if (elementType.isComplexType()) {
			logger.debug("is complex type");
			XSComplexType complexType = (XSComplexType) elementType;
			logger.debug("complexType: " + complexType);
			java.util.Collection<? extends XSAttributeUse> attributeUses = complexType.getAttributeUses();
			logger.debug("attributeUses.size(): " + attributeUses.size());
			logger.debug("isMixed: " + complexType.isMixed());
			//TODO decide what to do with mixed types
			logger.debug("complexType.getContentType(): " + complexType.getContentType());
			XSContentType contentType = complexType.getContentType();

			if (contentType.asParticle() != null) {
				logger.debug("is complexType with child elements");
				if (minOccurs > -1 || maxOccurs > -1) {
					logger.error("already got min- or maxOccurs that I'm ignoring now");
					logger.debug("minOccurs: " + minOccurs);
					logger.debug("maxOccurs: " + maxOccurs);
				}
				processComplexType(schema, parentSuperAbstract, element, isInclude);
			} else if (contentType.asEmpty() != null) {
				logger.debug("empty - check for attributes");
				if (attributeUses.size() > 0) {
					logger.debug("found attributes, process complexType");
					processComplexType(schema, parentSuperAbstract, element, isInclude);
				} else {
					//TODO empty complex element without attributes - decide what to do next
					logger.error("no attributes, TODO decide what to do next");
				}
			} else if (contentType.asSimpleType() != null) {
				logger.debug("simple type");
				processComplexTypeOfSimpleContentOrSimpleType(schema, parentSuperAbstract, element, isInclude);
			}
		} else if (elementType.isSimpleType()) {
			logger.debug("is simple type");
			XSSimpleType simpleType = (XSSimpleType) elementType;
			logger.debug("simpleType: " + simpleType);
			processComplexTypeOfSimpleContentOrSimpleType(schema, parentSuperAbstract, element, isInclude);
		} else
			logger.error("unexpected type: " + elementType);
	}

	/*
	public boolean isOptional(final String elementName) {
		for (final Entry<String, XSComplexType> entry : getComplexTypes().entrySet()) {
			final XSContentType content = entry.getValue().getContentType();
			final XSParticle particle = content.asParticle();
			if (null != particle) {
				final XSTerm term = particle.getTerm();
				if (term.isModelGroup()) {
					final XSParticle[] particles = term.asModelGroup().getChildren();
					for (final XSParticle p : particles) {
						final XSTerm pterm = p.getTerm();
						if (pterm.isElementDecl()) {
							final XSElementDecl e = pterm.asElementDecl();
							if (0 == e.getName().compareToIgnoreCase(elementName)) {
								return p.getMinOccurs() == 0;
							}
						}
					}
				}
				You can go from XSComplexType to XSContentType which can be a 
				XSParticle, then to XSTerm, which can be XSModelGroup.
			}
		}
		return true;
	}
	*/

	private void processModelGroup(Schema schema, SuperAbstract parentSuperAbstract, XSModelGroup modelGroup, int minOccurs, int maxOccurs,
			Boolean isInclude) {
		logger.debug("in processModelGroup");
		logger.debug("schema: " + schema);
		logger.debug("parentSuperAbstract: " + parentSuperAbstract);
		logger.debug("modelGroup: " + modelGroup);
		logger.debug("minOccurs: " + minOccurs);
		logger.debug("maxOccurs: " + maxOccurs);

		XSParticle[] particles = modelGroup.getChildren();
		logger.debug("particles.length: " + particles.length);
		for (XSParticle p : particles) {
			logger.debug("p: " + p);
			logger.debug("p.getMinOccurs(): " + p.getMinOccurs());
			logger.debug("p.getMaxOccurs(): " + p.getMaxOccurs());
			if (minOccurs > -1 || maxOccurs > -1) {
				logger.error("already got minOccurs or maxOccurs that I might be loosing here");
			} else {
				logger.debug("not got minOccurs or maxOccurs, assign those from p");
				minOccurs = p.getMinOccurs();
				maxOccurs = p.getMaxOccurs();
			}
			XSTerm pterm = p.getTerm();
			logger.debug("pterm: " + pterm);
			if (pterm.isModelGroupDecl()) {
				logger.debug("pterm is modelGroupDecl");
				XSModelGroupDecl modelGroupDecl = pterm.asModelGroupDecl();
				logger.debug("modelGroupDecl: " + modelGroupDecl);
				logger.debug("modelGroupDecl.getModelGroup(): " + modelGroupDecl.getModelGroup());
				processModelGroup(schema, parentSuperAbstract, modelGroupDecl.getModelGroup(), minOccurs, maxOccurs, isInclude);

				//XSModelGroup modelGroup = modelGroupDecl.getModelGroup();
				//logger.debug("modelGroup: " + modelGroup);
				//XSParticle[] parts = modelGroup.getChildren();
				//logger.debug("parts.length: " + parts.length);
				//TODO should place this into recursive method for deeper hierarchies
				//for (XSParticle part : parts) {
				//logger.debug("part: " + part);
				//XSTerm partTerm = part.getTerm();
				//if (partTerm.isElementDecl()) {
				//logger.debug("partTerm isElementDecl");
				//XSElementDecl e = partTerm.asElementDecl();
				//logger.debug("e: " + e);
				//logger.debug("before calling processElement");
				//logger.debug("parentSuperAbstract: " + parentSuperAbstract);
				//processElement(schema, parentSuperAbstract, e);
				//logger.debug("returned from processElement");
				//logger.debug("parentSuperAbstract: " + parentSuperAbstract);
				//}
				//}
			} else if (pterm.isElementDecl()) {
				logger.debug("pterm isElementDecl");
				XSElementDecl e = pterm.asElementDecl();
				logger.debug("e: " + e);
				logger.debug("before calling processElement");
				logger.debug("parentSuperAbstract: " + parentSuperAbstract);
				processElement(schema, parentSuperAbstract, e, minOccurs, maxOccurs, isInclude);
				logger.debug("returned from processElement");
				logger.debug("parentSuperAbstract: " + parentSuperAbstract);
			} else {
				logger.error("unexpected type of pterm: " + pterm);
			}
		}
	}

	private void processComplexType(Schema schema, SuperAbstract parentSuperAbstract, XSElementDecl element, Boolean isInclude) {
		logger.debug("in processComplexType");
		logger.debug("schema: " + schema);
		logger.debug("parentSuperAbstract: " + parentSuperAbstract);
		logger.debug("element: " + element);

		String schemaName = schema.getName();
		XSComplexType complexType = (XSComplexType) element.getType();

		String superAbstractName = element.getName();
		logger.debug("superAbstractName: " + superAbstractName);

		logger.debug("complexType.getContentType(): " + complexType.getContentType());
		XSContentType contentType = complexType.getContentType();

		int minOccurs = -1;
		int maxOccurs = -1;

		if (contentType.asParticle() != null) {
			logger.debug("contentType is particle");

			XSParticle particle = contentType.asParticle();
			minOccurs = particle.getMinOccurs();
			maxOccurs = particle.getMaxOccurs();
			boolean isRepeated = particle.isRepeated();

			logger.debug("minOccurs: " + minOccurs);
			logger.debug("maxOccurs: " + maxOccurs);
			logger.debug("isRepeated: " + isRepeated);
		}

		SuperAbstract superAbstract = null;
		boolean alreadyProcessed = false;

		if (isInclude == null
				|| (isInclude && importSchemaElementsToIncludeFromXmlService.includeComplexElementInSchema(schemaName, superAbstractName))
				|| (!isInclude && !importSchemaElementsToExcludeFromXmlService.excludeTableInSchema(schemaName, superAbstractName))) {
			logger.debug("complexElement/superAbstract to be included in schema, superAbstractName: " + superAbstractName);
			//check whether it's global or local
			if (element.isGlobal()) {
				logger.debug("global - should only be one with that name in schema");

				//check whether it's already been processed
				if (globalSuperAbstractElements.containsKey(superAbstractName)) {
					logger.debug("found superAbstract with name: " + superAbstractName);
					alreadyProcessed = true;
					superAbstract = globalSuperAbstractElements.get(superAbstractName);
				}
			} else
				logger.debug("complexElement/superAbstract not to be included in schema, superAbstractName: " + superAbstractName);

			//TODO for this to work with include/exclude, each complexElement needs to be listed separately in inclusion/exclusion file even if it is a local element ...
			//TODO which isn't going to work if there are multiple local elements with the same name ... work on this
			if (!alreadyProcessed) {
				logger.debug("not already processed");
				logger.debug("either localElement, i.e., could be multiple elements with same name or didn't find superAbstract with name: "
						+ superAbstractName);
				if (element.isLocal() && parentSuperAbstract == null) {
					logger.error("something wrong - local element should have parentSuperAbstract - TODO sort this");
					//TODO sort this - something wrong - local element without parentSuperAbstract
				} else {
					logger.debug("add new superAbstract");
					logger.debug("for local element - might have to check whether it already exists though, not doing that for now though");

					superAbstract = new SuperAbstract(superAbstractName, schema, SuperAbstractMIDSTSuperModelType.STRUCT_OF_ATTRIBUTES,
							SuperAbstractModelSpecificType.XSD_COMPLEX_ELEMENT);
					schema.addCanonicalModelConstruct(superAbstract);
					if (element.isGlobal())
						globalSuperAbstractElements.put(superAbstractName, superAbstract);
					else
						superAbstract.setGlobal(false);
				}
			}

			if (parentSuperAbstract != null) {
				logger.debug("got parentSuperAbstract: " + parentSuperAbstract);

				if (!element.isGlobal()) {
					logger.debug("local element");
					logger.debug("add parentSuperAbstract");
					if (superAbstract.getParentSuperAbstract() != null && superAbstract.getParentSuperAbstract() != parentSuperAbstract) {
						logger.error("superAbstract already got parentSuperAbstract and it's different from current one - TODO sort this");
					} else {
						superAbstract.setParentSuperAbstract(parentSuperAbstract);
					}
				} else {
					logger.debug("global element");
					logger.debug("add nesting - check whether particular nesting is already there");
					Set<ParticipationOfCMCInSuperRelationship> participations = superAbstract.getParticipationInSuperRelationships();
					logger.debug("participations.size(): " + participations.size());
					boolean foundNesting = false;
					for (ParticipationOfCMCInSuperRelationship participation : participations) {
						logger.debug("participation: " + participation);
						logger.debug("participation.getCanonicalModelConstruct(): " + participation.getCanonicalModelConstruct());
						SuperRelationship superRelationship = participation.getSuperRelationship();
						logger.debug("superRelationship: " + superRelationship);
						if (superRelationship.getMidstSuperModelType().equals(SuperRelationshipMIDSTSuperModelType.NEST)
								&& participation.getRole().equals(SuperRelationshipRoleType.CHILD)) {
							logger.debug("found nesting with current superAbstract as CHILD, check whether it's got the right parent, i.e., is this nesting");
							Set<ParticipationOfCMCInSuperRelationship> partsOfConstrs = superRelationship.getParticipationsOfConstructs();
							logger.debug("partsOfConstrs.size(): " + partsOfConstrs.size());
							for (ParticipationOfCMCInSuperRelationship part : partsOfConstrs) {
								logger.debug("part: " + part);
								logger.debug("part.getCanonicalModelConstruct(): " + part.getCanonicalModelConstruct());
								if (part != participation && part.getCanonicalModelConstruct().equals(parentSuperAbstract)) {
									logger.debug("found parentSuperAbstract - check whether it's the parent");
									if (part.getRole().equals(SuperRelationshipRoleType.PARENT)) {
										logger.debug("is parent - found this nesting");
										foundNesting = true;
									}
								}
							}
						}
					}

					logger.debug("foundNesting: " + foundNesting);

					if (!foundNesting) {
						logger.debug("didn't find nesting, add it");
						String superRelationshipName = parentSuperAbstract.getName() + "_" + superAbstract.getName();
						SuperRelationship superRelationship = new SuperRelationship(superRelationshipName, schema,
								SuperRelationshipMIDSTSuperModelType.NEST, SuperRelationshipModelSpecificType.XSD_NEST);
						schema.addCanonicalModelConstruct(superRelationship);
						saveSuperRelationship(superRelationship);
						ParticipationOfCMCInSuperRelationship participationOfParentSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
								SuperRelationshipRoleType.PARENT, superRelationship, parentSuperAbstract);
						ParticipationOfCMCInSuperRelationship participationOfChildSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
								SuperRelationshipRoleType.CHILD, superRelationship, superAbstract);
						saveParticipationOfCMCInSuperRelationship(participationOfParentSuperAbstractInSuperRelationship);
						saveParticipationOfCMCInSuperRelationship(participationOfChildSuperAbstractInSuperRelationship);
					} else {
						logger.debug("found nesting for parentSuperAbstract: " + parentSuperAbstract);
						logger.debug("and superAbstract: " + superAbstract);
						logger.debug("nothing else to do");
					}
				}
			}

			if (!alreadyProcessed) {
				logger.debug("not already processed - check for attributes and child elements");
				logger.debug("checking for attributes");
				java.util.Collection<? extends XSAttributeUse> attributeUses = complexType.getAttributeUses();
				logger.debug("attributeUses.size(): " + attributeUses.size());
				for (XSAttributeUse attrUse : attributeUses) {
					logger.debug("attrUse: " + attrUse);
					XSAttributeDecl attr = attrUse.getDecl();
					logger.debug("attr.getName(): " + attr.getName());
					logger.debug("attr.getType(): " + attr.getType());

					processAttribute(schema, superAbstract, null, attr, isInclude);
				}

				logger.debug("checking for childElements");
				logger.debug("contentType.asParticle(): " + contentType.asParticle());
				if (contentType.asParticle() != null) {
					logger.debug("contentType is particle");
					XSParticle particle = contentType.asParticle();
					XSTerm term = particle.getTerm();
					logger.debug("term: " + term);
					if (term.isModelGroup()) {
						logger.debug("term is modelGroup");
						processModelGroup(schema, superAbstract, term.asModelGroup(), minOccurs, maxOccurs, isInclude);
					}
				}

				/*
				List<XSElementDecl> elements = complexType.getElementDecls();
				logger.debug("elements.size(): " + elements.size());
				for (XSElementDecl elm : elements) {
					logger.debug("elm: " + elm);
					logger.debug("before calling processElement");
					logger.debug("superAbstract: " + superAbstract);
					processElement(schema, superAbstract, elm);
					logger.debug("returned from processElement");
					logger.debug("superAbstract: " + superAbstract);
				}
				*/

				logger.debug("adding constraints");
				logger.debug("parentSuperAbstract: " + parentSuperAbstract);
				List<XSIdentityConstraint> constraints = element.getIdentityConstraints();
				if (constraints != null && constraints.size() > 0) {
					if (parentSuperAbstract != null) {
						if (allConstraints.containsKey(parentSuperAbstract)) {
							logger.debug("found parentSuperAbstract: " + parentSuperAbstract);
							logger.debug("add constraints");
							List<XSIdentityConstraint> constrs = new ArrayList<XSIdentityConstraint>();
							constrs.addAll(allConstraints.get(parentSuperAbstract));
							constrs.addAll(constraints);
							allConstraints.put(parentSuperAbstract, constrs);
						} else {
							logger.debug("didn't find parentSuperAbstract: " + parentSuperAbstract);
							logger.debug("add constraints");
							allConstraints.put(parentSuperAbstract, constraints);
						}
					} else {
						logger.debug("no parentSuperAbstract for constraints");
						allConstraintsWithoutParentSA.addAll(constraints);
					}
				}
			}
		} else
			logger.debug("complexElement/superAbstract not to be included in schema, superAbstractName: " + superAbstractName);
	}

	private void processComplexTypeOfSimpleContentOrSimpleType(Schema schema, SuperAbstract parentSuperAbstract, XSElementDecl element,
			Boolean isInclude) {
		logger.debug("in processComplexTypeOfSimpleContentOrSimpleType");
		logger.debug("schema: " + schema);
		logger.debug("parentSuperAbstract: " + parentSuperAbstract);
		logger.debug("element: " + element);

		String superLexicalName = element.getName();
		logger.debug("superLexicalName: " + superLexicalName);

		XSSimpleType simpleType = null;

		int minOccurs = -1;
		int maxOccurs = -1;

		if (element.getType().isComplexType()) {
			logger.debug("complex type - should be of simple content");
			XSComplexType complexType = (XSComplexType) element.getType();

			XSContentType contentType = complexType.getContentType();
			simpleType = contentType.asSimpleType();

			XSParticle particle = contentType.asParticle();
			logger.debug("particle: " + particle);
			if (particle != null) {
				minOccurs = particle.getMinOccurs();
				maxOccurs = particle.getMaxOccurs();
				boolean isRepeated = particle.isRepeated();

				logger.debug("minOccurs: " + minOccurs);
				logger.debug("maxOccurs: " + maxOccurs);
				logger.debug("isRepeated: " + isRepeated);
			}
		} else {
			logger.debug("simple type");
			simpleType = (XSSimpleType) element.getType();
		}

		if (simpleType != null) {
			logger.debug("simpleType.getBaseType(): " + simpleType.getBaseType());
			logger.debug("simpleType.getName(): " + simpleType.getName());
			logger.debug("simpleType.getLocator(): " + simpleType.getLocator().toString());
			logger.debug("simpleType.getPrimitiveType(): " + simpleType.getPrimitiveType());

			XSParticle particle = simpleType.asParticle();
			logger.debug("particle: " + particle);
			if (particle != null) {
				minOccurs = particle.getMinOccurs();
				maxOccurs = particle.getMaxOccurs();
				boolean isRepeated = particle.isRepeated();

				logger.debug("minOccurs: " + minOccurs);
				logger.debug("maxOccurs: " + maxOccurs);
				logger.debug("isRepeated: " + isRepeated);
			}
		} else {
			logger.error("not simpleType - something wrong here - shouldn't get here though");
		}

		SuperLexical superLexical = null;
		boolean alreadyProcessed = false;

		//check whether it's global or local
		if (element.isGlobal()) {
			logger.debug("global - should only be one with that name in schema");
			//check whether it's already been processed
			if (globalSuperLexicalElements.containsKey(superLexicalName)) {
				logger.debug("found superLexical with name: " + superLexicalName);
				alreadyProcessed = true;
				superLexical = globalSuperLexicalElements.get(superLexicalName);
			}
		}

		if (!alreadyProcessed) {
			logger.debug("either localElement, i.e., could be multiple elements with same name or didn't find superLexical with name: "
					+ superLexicalName);
			if (element.isLocal() && parentSuperAbstract == null) {
				logger.error("something wrong - local element should have parentSuperAbstract - TODO sort this");
				//TODO sort this - something wrong - local element without parentSuperAbstract
			} else {
				logger.debug("add new superLexical");
				logger.debug("for local element - might have to check whether it already exists though, not doing that for now though");

				DataType dataType = DataTypeMapper.mapXSDTypeToDataType(simpleType.getPrimitiveType().getName());
				SuperLexicalModelSpecificType superLexicalModelSpecificType = null;
				if (element.getType().isComplexType())
					superLexicalModelSpecificType = SuperLexicalModelSpecificType.XSD_COMPLEX_ELEMENT_WITH_SIMPLE_CONTENT;
				else if (element.getType().isSimpleType())
					superLexicalModelSpecificType = SuperLexicalModelSpecificType.XSD_SIMPLE_ELEMENT;
				superLexical = new SuperLexical(superLexicalName, schema, dataType, element.isNillable(), SuperLexicalMIDSTSuperModelType.LEXICAL,
						superLexicalModelSpecificType);
				String type = simpleType.getPrimitiveType().getName();
				if (type.contains("ID") && !type.contains("IDREF") && !type.contains("IDREFS")) {
					logger.debug("found id");
					superLexical.setIsIdentifier(true);
				}

				schema.addCanonicalModelConstruct(superLexical);
				if (element.isGlobal())
					globalSuperLexicalElements.put(superLexicalName, superLexical);
				else
					superLexical.setGlobal(false);
			}
		}

		if (parentSuperAbstract != null) {
			logger.debug("got parentSuperAbstract: " + parentSuperAbstract);
			if (!element.isGlobal()) {
				logger.debug("local element");
				logger.debug("add parentSuperAbstract");
				if (superLexical.getParentSuperAbstract() != null && superLexical.getParentSuperAbstract() != parentSuperAbstract) {
					logger.error("superLexical already got parentSuperAbstract and it's different from current one - TODO sort this");
				} else {
					superLexical.setParentSuperAbstract(parentSuperAbstract);
				}
			} else {
				logger.debug("add nesting - check whether particular nesting is already there");
				Set<ParticipationOfCMCInSuperRelationship> participations = superLexical.getParticipationInSuperRelationships();
				logger.debug("participations.size(): " + participations.size());
				boolean foundNesting = false;
				for (ParticipationOfCMCInSuperRelationship participation : participations) {
					logger.debug("participation: " + participation);
					logger.debug("participation.getCanonicalModelConstruct(): " + participation.getCanonicalModelConstruct());
					SuperRelationship superRelationship = participation.getSuperRelationship();
					logger.debug("superRelationship: " + superRelationship);
					if (superRelationship.getMidstSuperModelType().equals(SuperRelationshipMIDSTSuperModelType.NEST)
							&& participation.getRole().equals(SuperRelationshipRoleType.CHILD)) {
						logger.debug("found nesting with current superAbstract as CHILD, check whether it's got the right parent, i.e., is this nesting");
						Set<ParticipationOfCMCInSuperRelationship> partsOfConstrs = superRelationship.getParticipationsOfConstructs();
						logger.debug("partsOfConstrs.size(): " + partsOfConstrs.size());
						for (ParticipationOfCMCInSuperRelationship part : partsOfConstrs) {
							logger.debug("part: " + part);
							logger.debug("part.getCanonicalModelConstruct(): " + part.getCanonicalModelConstruct());
							if (part != participation && part.getCanonicalModelConstruct().equals(parentSuperAbstract)) {
								logger.debug("found parentSuperAbstract - check whether it's the parent");
								if (part.getRole().equals(SuperRelationshipRoleType.PARENT)) {
									logger.debug("is parent - found this nesting");
									foundNesting = true;
								}
							}
						}
					}
				}

				logger.debug("foundNesting: " + foundNesting);

				if (!foundNesting) {
					logger.debug("didn't find nesting, add it");
					String superRelationshipName = parentSuperAbstract.getName() + "_" + superLexical.getName();
					SuperRelationship superRelationship = new SuperRelationship(superRelationshipName, schema,
							SuperRelationshipMIDSTSuperModelType.NEST, SuperRelationshipModelSpecificType.XSD_NEST);
					schema.addCanonicalModelConstruct(superRelationship);
					saveSuperRelationship(superRelationship);
					ParticipationOfCMCInSuperRelationship participationOfParentSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
							SuperRelationshipRoleType.PARENT, superRelationship, parentSuperAbstract);
					ParticipationOfCMCInSuperRelationship participationOfChildSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
							SuperRelationshipRoleType.CHILD, superRelationship, superLexical);
					saveParticipationOfCMCInSuperRelationship(participationOfParentSuperAbstractInSuperRelationship);
					saveParticipationOfCMCInSuperRelationship(participationOfChildSuperAbstractInSuperRelationship);
				} else {
					logger.debug("found nesting for parentSuperAbstract: " + parentSuperAbstract);
					logger.debug("and superLexical: " + superLexical);
					logger.debug("nothing else to do");
				}
			}

			if (!alreadyProcessed) {
				if (element.getType().isComplexType()) {
					java.util.Collection<? extends XSAttributeUse> attributeUses = ((XSAttContainer) element.getType()).getAttributeUses();
					logger.debug("attributeUses.size(): " + attributeUses.size());
					for (XSAttributeUse attrUse : attributeUses) {
						logger.debug("attrUse: " + attrUse);
						XSAttributeDecl attr = attrUse.getDecl();
						logger.debug("attr.getName(): " + attr.getName());
						logger.debug("attr.getType(): " + attr.getType());

						processAttribute(schema, null, superLexical, attr, isInclude);
					}
				}

				logger.debug("adding constraints");
				logger.debug("parentSuperAbstract: " + parentSuperAbstract);
				List<XSIdentityConstraint> constraints = element.getIdentityConstraints();
				if (constraints != null && constraints.size() > 0) {
					if (parentSuperAbstract != null) {
						if (allConstraints.containsKey(parentSuperAbstract)) {
							logger.debug("found parentSuperAbstract: " + parentSuperAbstract);
							logger.debug("add constraints");
							List<XSIdentityConstraint> constrs = new ArrayList<XSIdentityConstraint>();
							constrs.addAll(allConstraints.get(parentSuperAbstract));
							constrs.addAll(constraints);
							allConstraints.put(parentSuperAbstract, constrs);
						} else {
							logger.debug("didn't find parentSuperAbstract: " + parentSuperAbstract);
							logger.debug("add constraints");
							allConstraints.put(parentSuperAbstract, constraints);
						}
					} else {
						logger.debug("no parentSuperAbstract for constraints");
						allConstraintsWithoutParentSA.addAll(constraints);
					}
				}
			}
		}
	}

	private void processAttribute(Schema schema, SuperAbstract parentSuperAbstract, SuperLexical parentSuperLexical, XSAttributeDecl attribute,
			Boolean isInclude) {
		logger.debug("in processAttribute");
		logger.debug("schema: " + schema);
		logger.debug("parentSuperAbstract: " + parentSuperAbstract);
		logger.debug("parentSuperLexical: " + parentSuperLexical);
		logger.debug("attribute: " + attribute);

		logger.debug("attribute.getName(): " + attribute.getName());
		logger.debug("attribute.getDefaultValue(): " + attribute.getDefaultValue());
		logger.debug("attribute.getFixedValue(): " + attribute.getFixedValue());
		logger.debug("attribute.getType(): " + attribute.getType());
		logger.debug("attribute.getType().getName(): " + attribute.getType().getName());
		logger.debug("attribute.isGlobal(): " + attribute.isGlobal());
		logger.debug("attribute.isLocal(): " + attribute.isLocal());
		logger.debug("attribute.getTargetNamespace(): " + attribute.getTargetNamespace());

		//TODO decide what to do with global attributes: store only once and link it to parent element by nest relationship or 
		//store multiple and link it to parent element by parent
		//querying will most likely result in having the name of the parent of the attribute, might be ok to store multiple -
		//do that for now and ignore whether attribute is global or local

		String superLexicalName = attribute.getName();
		logger.debug("superLexicalName: " + superLexicalName);
		String dataTypeName = null;
		if (attribute.getType().getName() == null && attribute.getType().asRestriction() != null) {
			logger.debug("null name, could be anonymous and is restriction");
			logger.debug("attribute.getType().asRestriction(): " + attribute.getType().asRestriction());
			XSRestrictionSimpleType restriction = attribute.getType().asRestriction();
			logger.debug("restriction.toString(): " + restriction.toString());
			logger.debug("restriction.getSimpleBaseType(): " + restriction.getSimpleBaseType());
			logger.debug("restriction.getPrimitiveType(): " + restriction.getPrimitiveType());
			logger.debug("restriction.getPrimitiveType().getName(): " + restriction.getPrimitiveType().getName());
			dataTypeName = restriction.getPrimitiveType().getName();
		} else if (!attribute.getType().getName().contains("NMTOKENS") && !attribute.getType().getName().contains("string")
				&& !attribute.getType().getName().contains("ID") && attribute.getType().asRestriction() != null) {
			logger.debug("is restriction and isn't ID... or string");
			logger.debug("attribute.getType().asRestriction(): " + attribute.getType().asRestriction());
			XSRestrictionSimpleType restriction = attribute.getType().asRestriction();
			logger.debug("restriction.toString(): " + restriction.toString());
			logger.debug("restriction.getSimpleBaseType(): " + restriction.getSimpleBaseType());
			logger.debug("restriction.getPrimitiveType(): " + restriction.getPrimitiveType());
			logger.debug("restriction.getPrimitiveType().getName(): " + restriction.getPrimitiveType().getName());
			dataTypeName = restriction.getPrimitiveType().getName();
		} else
			dataTypeName = attribute.getType().getName();

		XSParticle particle = attribute.getType().asParticle();
		logger.debug("particle: " + particle);
		if (particle != null) {
			int minOccurs = particle.getMinOccurs();
			int maxOccurs = particle.getMaxOccurs();
			boolean isRepeated = particle.isRepeated();

			logger.debug("minOccurs: " + minOccurs);
			logger.debug("maxOccurs: " + maxOccurs);
			logger.debug("isRepeated: " + isRepeated);
		}

		logger.debug("dataTypeName: " + dataTypeName);

		DataType dataType = DataTypeMapper.mapXSDTypeToDataType(dataTypeName);
		logger.debug("dataType: " + dataType);
		boolean isNullable = false; //TODO used as default setting for now, sort out minOccurs, check isOptional too

		SuperLexical superLexical = new SuperLexical(superLexicalName, schema, dataType, isNullable, SuperLexicalMIDSTSuperModelType.LEXICAL,
				SuperLexicalModelSpecificType.XSD_ATTRIBUTE);

		if (attribute.getType().getName() != null) {
			String type = attribute.getType().getName();
			if (type.contains("ID") && !type.contains("IDREF") && !type.contains("IDREFS")) {
				logger.debug("found id");
				superLexical.setIsIdentifier(true);
			}
		}

		if (parentSuperAbstract != null)
			superLexical.setParentSuperAbstract(parentSuperAbstract);
		else if (parentSuperLexical != null)
			superLexical.setParentSuperLexical(parentSuperLexical);
		else
			logger.error("no parent for superLexical - something wrong - TODO sort this");
	}

	private void processIdentityConstraints(Schema schema, Map<SuperAbstract, List<XSIdentityConstraint>> allConstraints, Boolean isInclude) {
		//TODO might have to add IDREF and IDREFS too and might need the parent here for the identityConstraints key and keyref
		logger.debug("constraints with superAbstract parent");
		Set<SuperAbstract> superAbstracts = allConstraints.keySet();
		for (SuperAbstract superAbstract : superAbstracts) {
			logger.debug("superAbstract: " + superAbstract);
			List<XSIdentityConstraint> constraints = allConstraints.get(superAbstract);
			for (XSIdentityConstraint constraint : constraints) {
				logger.debug("constraint: " + constraint);
				logger.debug("constraint.getName(): " + constraint.getName());
				logger.debug("constraint.getCategory(): " + constraint.getCategory());
				logger.debug("constraint is KEY: " + (constraint.getCategory() == XSIdentityConstraint.KEY));
				logger.debug("constraint is KEYREF: " + (constraint.getCategory() == XSIdentityConstraint.KEYREF));
				logger.debug("constraint is UNIQUE: " + (constraint.getCategory() == XSIdentityConstraint.UNIQUE));
				logger.debug("constraint.getLocator(): " + constraint.getLocator());
				logger.debug("constraint.getParent(): " + constraint.getParent());
				logger.debug("constraint.getSelector().getXPath(): " + constraint.getSelector().getXPath());
				logger.debug("constraint.getReferencedKey(): " + constraint.getReferencedKey());
				List<XSXPath> fields = constraint.getFields();
				for (XSXPath field : fields) {
					logger.debug("field.getXPath(): " + field.getXPath());
				}
			}
		}

		logger.debug("constraints without superAbstract parent:");
		for (XSIdentityConstraint constraint : allConstraintsWithoutParentSA) {
			logger.debug("constraint: " + constraint);
			logger.debug("constraint.getName(): " + constraint.getName());
			logger.debug("constraint.getCategory(): " + constraint.getCategory());
			logger.debug("constraint is KEY: " + (constraint.getCategory() == XSIdentityConstraint.KEY));
			logger.debug("constraint is KEYREF: " + (constraint.getCategory() == XSIdentityConstraint.KEYREF));
			logger.debug("constraint is UNIQUE: " + (constraint.getCategory() == XSIdentityConstraint.UNIQUE));
			logger.debug("constraint.getLocator(): " + constraint.getLocator());
			logger.debug("constraint.getParent(): " + constraint.getParent());
			logger.debug("constraint.getSelector().getXPath(): " + constraint.getSelector().getXPath());
			List<XSXPath> fields = constraint.getFields();
			for (XSXPath field : fields) {
				logger.debug("field.getXPath(): " + field.getXPath());
			}

			if (constraint.getCategory() == XSIdentityConstraint.KEY) {
				logger.debug("constraint is KEY");
			} else if (constraint.getCategory() == XSIdentityConstraint.KEYREF) {
				logger.debug("constraint is KEYREF");
			} else if (constraint.getCategory() == XSIdentityConstraint.UNIQUE) {
				logger.debug("constraint is UNIQUE");
			}
		}
	}

	private void setKey(XSIdentityConstraint keyConstraint, SuperAbstract parentSuperAbstract) {
		logger.debug("in setKey");
		logger.debug("keyConstraint: " + keyConstraint);
		logger.debug("parentSuperAbstract: " + parentSuperAbstract);
		logger.debug("find key element");
		logger.debug("constraint.getParent(): " + keyConstraint.getParent());
		CanonicalModelConstruct parentConstruct = this.findParentConstruct(keyConstraint.getParent());
		logger.debug("parentConstruct: " + parentConstruct);
		List<String> elementsToSelect = getElementsToSelect(keyConstraint.getSelector().getXPath());
		for (String elementToSelect : elementsToSelect) {
			logger.debug("elementToSelect: " + elementToSelect);
			if (elementToSelect.startsWith(".//")) {
				logger.debug("element starts with .//");
			} else {
				logger.debug("element doesn't start with .//, might start with ./ though");
				Set<ParticipationOfCMCInSuperRelationship> participations = parentSuperAbstract.getParticipationInSuperRelationships();
				for (ParticipationOfCMCInSuperRelationship participation : participations) {
					logger.debug("participation: " + participation);
					boolean foundParent = false;
					boolean foundChild = false;
					if (participation.getSuperRelationship().getModelSpecificType().equals(SuperRelationshipModelSpecificType.XSD_NEST)) {
						CanonicalModelConstruct construct = participation.getCanonicalModelConstruct();
						if (construct.equals(parentSuperAbstract)) {
							logger.debug("found parent");
							if (participation.getRole().equals(SuperRelationshipRoleType.PARENT)) {
								logger.debug("is parent");
								foundParent = true;
							}
						}
						if (construct.getName().equals(elementToSelect)) {
							logger.debug("found elementToSelect");
							if (participation.getRole().equals(SuperRelationshipRoleType.CHILD)) {
								logger.debug("is child");
								foundChild = true;

								List<XSXPath> fields = keyConstraint.getFields();
								for (XSXPath field : fields) {
									logger.debug("field.getXPath(): " + field.getXPath());
									if (field.getXPath().value.contains("@")) {
										logger.debug("field is attribute");
										String fieldName = field.getXPath().value.substring(1);
										logger.debug("fieldName: " + fieldName);
										Set<SuperLexical> superLexicals = null;
										if (construct instanceof SuperAbstract)
											superLexicals = ((SuperAbstract) construct).getSuperLexicals();
										else if (construct instanceof SuperLexical)
											superLexicals = ((SuperLexical) construct).getChildSuperLexicals();
										else
											logger.error("unexpected type of construct: " + construct);
										for (SuperLexical superLexical : superLexicals) {
											logger.debug("superLexical: " + superLexical);
											if (superLexical.getName().equals(fieldName)) {
												logger.debug("found key superLexical");
												logger.debug("superLexical.getIsIdentifier(): " + superLexical.getIsIdentifier());
												superLexical.setIsIdentifier(true);
												logger.debug("participation.getSuperRelationship(): " + participation.getSuperRelationship());
												logger.debug("participation.getSuperRelationship().getName(): "
														+ participation.getSuperRelationship().getName());
												logger.debug("set name of superRelationship to: " + keyConstraint.getName());
												participation.getSuperRelationship().setName(keyConstraint.getName());
												logger.debug("participation.getSuperRelationship().getName(): "
														+ participation.getSuperRelationship().getName());
											}
										}
									} else {
										logger.debug("field is element");
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private CanonicalModelConstruct findParentConstruct(XSElementDecl parentElement) {
		if (this.globalSuperAbstractElements.containsKey(parentElement)) {
			logger.debug("found parent element in globalSuperAbstractElements");
			SuperAbstract parentSuperAbstract = this.globalSuperAbstractElements.get(parentElement);
			logger.debug("parentSuperAbstract: " + parentSuperAbstract);
			return parentSuperAbstract;
		} else if (this.globalSuperLexicalElements.containsKey(parentElement)) {
			logger.debug("found parent element in globalSuperLexicalElements");
			SuperLexical parentSuperLexical = this.globalSuperLexicalElements.get(parentElement);
			logger.debug("parentSuperLexical: " + parentSuperLexical);
			return parentSuperLexical;
		} else {
			logger.error("didn't find parent - TODO check this");
			return null;
		}
	}

	private List<String> getElementsToSelect(XmlString selectorXPath) {
		List<String> elementsToSelect = new ArrayList<String>();
		logger.debug("selectorXPath.value: " + selectorXPath.value);
		StringTokenizer st = new StringTokenizer(selectorXPath.value, "|");
		while (st.hasMoreElements()) {
			String nextElement = (String) st.nextElement();
			logger.debug("nextElement: " + nextElement);
			elementsToSelect.add(nextElement);
		}
		return elementsToSelect;
	}

	private void parseSchemaInformationFromSchemaFile(Schema schema, String schemaUrl, Boolean isInclude) {
		XSOMParser parser = new XSOMParser();
		//parser.setErrorHandler(...);
		//parser.setEntityResolver(...);

		try {
			parser.parse(new File(schemaUrl));

			XSSchemaSet result = parser.getResult();

			Iterator<XSElementDecl> elementIt = result.iterateElementDecls();
			while (elementIt.hasNext()) {
				XSElementDecl element = elementIt.next();
				this.processElement(schema, null, element, -1, -1, isInclude);
			}
			processIdentityConstraints(schema, allConstraints, isInclude);
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveSchemaInformation(Schema schema) {
		logger.debug("in saveSchemaInformation");
		schemaRepository.save(schema);
		schemaRepository.flush();
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveSuperRelationship(SuperRelationship superRelationship) {
		logger.debug("in saveSuperRelationship");
		superRelationshipRepository.save(superRelationship);
		superRelationshipRepository.flush();
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveParticipationOfCMCInSuperRelationship(ParticipationOfCMCInSuperRelationship participation) {
		logger.debug("in saveParticipationOfCMCInSuperRelationship");
		participationRepository.save(participation);
		participationRepository.flush();
	}

	private void createAndAddSuperRelationshipsForSuperAbstracts(String schemaName, Map<String, SuperAbstract> nameSuperAbstractsMap,
			Boolean isInclude) {
		logger.debug("in createAndAddSuperRelationshipsForSuperAbstracts");
		logger.debug("schemaName: " + schemaName);
		Set<String> superAbstractNames = nameSuperAbstractsMap.keySet();
		
		for (String superAbstractName : superAbstractNames) {
			logger.debug("superAbstractName: " + superAbstractName);
			logger.debug("isInclude: " + isInclude);
			
			if (isInclude == null || (isInclude && importSchemaElementsToIncludeFromXmlService.includeTableInSchema(schemaName, superAbstractName))
					|| (!isInclude && !importSchemaElementsToExcludeFromXmlService.excludeTableInSchema(schemaName, superAbstractName))) {
				logger.debug("superAbstract included in schema, superAbstractName: " + superAbstractName);
				SuperAbstract referencingSuperAbstract = nameSuperAbstractsMap.get(superAbstractName);
				logger.debug("referencingSuperAbstract: " + referencingSuperAbstract);
				logger.debug("referencingSuperAbstract.id: " + referencingSuperAbstract.getId());
				List<ForeignKeyInformation> foreignKeyInformation = getForeignKeyInformationOfTable(schemaName, externalRelationalDataSource,
						superAbstractName);
				
				for (ForeignKeyInformation foreignKey : foreignKeyInformation) {
					//TODO I'm not checking whether the complete foreign key is included if some of its columns are included, might result in an incomplete
					//TODO foreign key being stored ... think about this
					
					if ((foreignKey.getPkTableSchema() == null && foreignKey.getFkTableSchema() == null)
							|| (foreignKey.getPkTableSchema() != null && foreignKey.getPkTableSchema().equals(schemaName)
									&& foreignKey.getFkTableSchema() != null && foreignKey.getFkTableSchema().equals(schemaName))) {
						String nameOfReferencedSuperAbstract = foreignKey.getPkTableName();
						
						if (isInclude == null
								|| (isInclude && importSchemaElementsToIncludeFromXmlService.includeTableInSchema(schemaName,
										nameOfReferencedSuperAbstract))
								|| (!isInclude && !importSchemaElementsToExcludeFromXmlService.excludeTableInSchema(schemaName,
										nameOfReferencedSuperAbstract))) {
							logger.debug("referenced superAbstract included in schema, nameOfReferencedSuperAbstract: "
									+ nameOfReferencedSuperAbstract);

							SuperAbstract referencedSuperAbstract = nameSuperAbstractsMap.get(nameOfReferencedSuperAbstract);
							logger.debug("referenced superAbstract: " + referencedSuperAbstract);
							logger.debug("referenced superAbstract.id: " + referencedSuperAbstract.getId());

							String referencingSuperLexicalName = null;
							SuperLexical referencingSuperLexical = null;
							String referencedSuperLexicalName = null;
							SuperLexical referencedSuperLexical = null;

							Set<SuperLexical> superLexicalsOfReferencingSuperAbstract = referencingSuperAbstract.getSuperLexicals();
							boolean foundReferencingSuperLexical = false;

							for (SuperLexical referencingSL : superLexicalsOfReferencingSuperAbstract) {
								if (referencingSL.getName().equals(foreignKey.getFkColumnName())) {
									logger.debug("found referencing superLexical");
									logger.debug("referencingSL: " + referencingSL);
									logger.debug("referencingSL.id: " + referencingSL.getId());
									foundReferencingSuperLexical = true;
									referencingSuperLexicalName = referencingSL.getName();
									referencingSuperLexical = referencingSL;
								}
							}
							if (!foundReferencingSuperLexical)
								logger.error("didn't find referencing superLexical ************** check this: " + foreignKey.getFkColumnName());

							Set<SuperLexical> superLexicalsOfReferencedSuperAbstract = referencedSuperAbstract.getSuperLexicals();
							boolean foundReferencedSuperLexical = false;
							for (SuperLexical referencedSL : superLexicalsOfReferencedSuperAbstract) {
								if (referencedSL.getName().equals(foreignKey.getPkColumnName())) {
									logger.debug("found referenced superLexical");
									logger.debug("referencedSL: " + referencedSL);
									logger.debug("referencedSL.id: " + referencedSL.getId());
									foundReferencedSuperLexical = true;
									referencedSuperLexicalName = referencedSL.getName();
									referencedSuperLexical = referencedSL;
								}
							}
							if (!foundReferencedSuperLexical)
								logger.error("didn't find referenced superLexical ************** check this: " + foreignKey.getPkColumnName());

							if (foundReferencingSuperLexical && foundReferencedSuperLexical) {
								logger.debug("found both referencing and referenced SuperLexical");

								if (isInclude == null
										|| (isInclude
												&& importSchemaElementsToIncludeFromXmlService.includeColumnInTableInSchema(schemaName,
														referencingSuperAbstract.getName(), referencingSuperLexicalName) && importSchemaElementsToIncludeFromXmlService
													.includeColumnInTableInSchema(schemaName, referencedSuperAbstract.getName(),
															referencedSuperLexicalName))
										|| (!isInclude
												&& !importSchemaElementsToExcludeFromXmlService.excludeColumnInTableInSchema(schemaName,
														referencingSuperAbstract.getName(), referencingSuperLexicalName) && !importSchemaElementsToExcludeFromXmlService
													.excludeColumnInTableInSchema(schemaName, referencedSuperAbstract.getName(),
															referencedSuperLexicalName))) {

									logger.debug("referencing and referenced superLexical included in Schema");
									logger.debug("referencedSuperLexicalName: " + referencedSuperLexicalName);
									logger.debug("referencingSuperLexicalName: " + referencingSuperLexicalName);
									SuperRelationship superRelationship = new SuperRelationship(foreignKey.getFkName(), schema,
											SuperRelationshipMIDSTSuperModelType.FOREIGN_KEY, SuperRelationshipModelSpecificType.REL_FOREIGN_KEY);
									schema.addCanonicalModelConstruct(superRelationship);

									saveSuperRelationship(superRelationship);

									ParticipationOfCMCInSuperRelationship participationOfReferencingSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
											SuperRelationshipRoleType.REFERENCING, superRelationship, referencingSuperAbstract);

									ParticipationOfCMCInSuperRelationship participationOfReferencedSuperAbstractInSuperRelationship = new ParticipationOfCMCInSuperRelationship(
											SuperRelationshipRoleType.REFERENCED, superRelationship, referencedSuperAbstract);
									participationOfReferencingSuperAbstractInSuperRelationship.addSpecifyingSuperLexical(referencingSuperLexical);
									participationOfReferencedSuperAbstractInSuperRelationship.addSpecifyingSuperLexical(referencedSuperLexical);
									//saveSuperRelationship(superRelationship);

									saveParticipationOfCMCInSuperRelationship(participationOfReferencingSuperAbstractInSuperRelationship);
									saveParticipationOfCMCInSuperRelationship(participationOfReferencedSuperAbstractInSuperRelationship);
								} else {
									logger.debug("referencing or referenced SuperLexical not included in schema");
									logger.debug("referencingSuperLexicalName: " + referencingSuperLexicalName);
									logger.debug("referencedSuperLexicalName: " + referencedSuperLexicalName);
								}
							} else {
								logger.debug("didn't find referencing or referenced SuperLexical");
								logger.debug("referencingSuperLexicalName: " + referencingSuperLexicalName);
								logger.debug("referencedSuperLexicalName: " + referencedSuperLexicalName);
							}
						} else
							logger.debug("referenced superAbstract included from schema, nameOfReferencedSuperAbstract: "
									+ nameOfReferencedSuperAbstract);
					} else
						logger.debug("referenced SuperAbstract in different schema");
				}
			} else
				logger.debug("referencing superAbstract included from schema, superAbstractName: " + superAbstractName);
		}
	}

	@SuppressWarnings("unchecked")
	private List<ForeignKeyInformation> getForeignKeyInformationOfTable(String schemaName, ComboPooledDataSource externalDataSource, String tableName) {
		logger.debug("in getForeignKeyInformationOfTable");
		logger.debug("tableName: " + tableName);
		GetForeignKeyInformationOfTableServiceImpl getForeignKeyInformation = new GetForeignKeyInformationOfTableServiceImpl();
		getForeignKeyInformation.setTableName(tableName);
		getForeignKeyInformation.setSchemaName(schemaName);
		List<ForeignKeyInformation> foreignKeyInformation = null;
		try {
			foreignKeyInformation = (List<ForeignKeyInformation>) JdbcUtils.extractDatabaseMetaData(externalDataSource, getForeignKeyInformation);
		} catch (MetaDataAccessException e) {
			//TODO
		}
		return foreignKeyInformation;
	}

	private void createAndAddSuperLexicalsForSuperAbstracts(String schemaName, Map<String, SuperAbstract> nameSuperAbstractsMap, Boolean isInclude) {
		logger.debug("in createSuperLexicalsForSuperAbstracts");
		logger.debug("schemaName: " + schemaName);
		Set<String> superAbstractNames = nameSuperAbstractsMap.keySet();
		
		for (String superAbstractName : superAbstractNames) {
			logger.debug("superAbstractName: " + superAbstractName);
			logger.debug("isInclude: " + isInclude);
			
			if (isInclude == null || (isInclude && importSchemaElementsToIncludeFromXmlService.includeTableInSchema(schemaName, superAbstractName))
					|| (!isInclude && !importSchemaElementsToExcludeFromXmlService.excludeTableInSchema(schemaName, superAbstractName))) {
				
				logger.debug("superAbstract included in schema, superAbstractNameL " + superAbstractName);
				SuperAbstract superAbstract = nameSuperAbstractsMap.get(superAbstractName);
				
				logger.debug("superAbstract: " + superAbstract);
				List<String> primaryKeyNames = getPrimaryKeysOfTable(externalRelationalDataSource, schemaName, superAbstractName);
				
				logger.debug("primaryKeyNames: " + primaryKeyNames);
				List<ColumnInformation> columnInformation = getColumnInformationOfTable(externalRelationalDataSource, schemaName, superAbstractName);
				
				logger.debug("columnInformation: " + columnInformation);
				logger.debug("columnInformation.size(): " + columnInformation.size());
				for (ColumnInformation column : columnInformation) {
					String columnName = column.getColumnName();
					logger.debug("columnName: " + columnName);
					
					if (isInclude == null
							|| (isInclude && importSchemaElementsToIncludeFromXmlService.includeColumnInTableInSchema(schemaName, superAbstractName,
									columnName))
							|| (!isInclude && !importSchemaElementsToExcludeFromXmlService.excludeColumnInTableInSchema(schemaName,
									superAbstractName, columnName))) {
						logger.debug("superLexical/column included in schema, superLexicalName/columnName: " + columnName);
						
						SuperLexical superLexical = new SuperLexical(column.getColumnName(), schema, column.getColumnType(), column.getIsNullable(),
								SuperLexicalMIDSTSuperModelType.LEXICAL, SuperLexicalModelSpecificType.REL_COLUMN);
						
						if (primaryKeyNames.contains(column.getColumnName()))
							superLexical.setIsIdentifier(true);
						else
							superLexical.setIsIdentifier(false);
						logger.debug("column.getColumnType(): " + column.getColumnType());
						
						if (column.getColumnType() != DataType.TEXT && column.getColumnType() != DataType.IMAGE) {
							int distinctValueCount = getNumberOfDistinctValuesInColumnInTable(superAbstractName, column.getColumnName());
							superLexical.setNumberOfDistinctValues(distinctValueCount);
						}
						superAbstract.addSuperLexical(superLexical);
					} else
						logger.debug("superLexical/column not to be included in schema, superLexicalName/columnName: " + columnName);
				}
			} else
				logger.debug("superAbstract not to be included in schema, superAbstractName: " + superAbstractName);
		}
	}

	private int getNumberOfDistinctValuesInColumnInTable(String tableName, String columnName) {
		logger.debug("in getNumberOfDistinctValuesInColumnInTable");
		logger.debug("tableName: " + tableName);
		logger.debug("columnName: " + columnName);
		String sql = "select count(distinct " + columnName + ") from " + tableName;
		int distinctValueCountResult = this.simpleJdbcTemplate.queryForInt(sql);
		logger.debug("distinctValueCountResult: " + distinctValueCountResult);
		return distinctValueCountResult;
	}

	private int getCardinalityOfTable(String tableName) {
		logger.debug("in getCardinalityOfTable");
		logger.debug("tableName: " + tableName);
		String sql = "select count(*) from " + tableName;
		int cardinality = this.simpleJdbcTemplate.queryForInt(sql);
		logger.debug("cardinality: " + cardinality);
		return cardinality;

	}

	private Map<String, SuperAbstract> createSuperAbstractsForTablesAndAddToSchema(String schemaName, List<String> tableNames, Boolean isInclude) {
		logger.debug("in createSuperAbstractsForTablesAndAddToSchema");
		logger.debug("schemaName: " + schemaName);
		//TODO might make more sense to split this up and put the adding into the SuperAbstractServiceImpl
		Map<String, SuperAbstract> nameSuperAbstractsMap = new HashMap<String, SuperAbstract>();
		
		for (String tableName : tableNames) {
			logger.debug("tableName: " + tableName);
			logger.debug("isInclude: " + isInclude);
			System.out.println("tableName: " + tableName);
			System.out.println("isInclude: " + isInclude);
			
			/*if (isInclude == null || (isInclude && importSchemaElementsToIncludeFromXmlService.includeTableInSchema(schemaName, tableName))
					|| (!isInclude && !importSchemaElementsToExcludeFromXmlService.excludeTableInSchema(schemaName, tableName))) {
				logger.debug("superAbstract/table to include in schema, tableName: " + tableName);*/
				
				SuperAbstract superAbstract = new SuperAbstract(tableName, schema, SuperAbstractMIDSTSuperModelType.AGGREGATION,
						SuperAbstractModelSpecificType.REL_TABLE);
				logger.debug("SuperAbstract: " + superAbstract);
				int cardinality = getCardinalityOfTable(tableName);
				logger.debug("cardinality: " + cardinality);
				superAbstract.setCardinality(cardinality);
				nameSuperAbstractsMap.put(tableName, superAbstract);
				//schema.addCanonicalModelConstruct(superAbstract);
			/*} else
				logger.debug("superAbstract/table not to include in schema, tableName: " + tableName); */
		}
		return nameSuperAbstractsMap;
	}

	@SuppressWarnings("unchecked")
	private List<ColumnInformation> getColumnInformationOfTable(ComboPooledDataSource externalDataSource, String schemaName, String tableName) {
		logger.debug("in getColumnInformationOfTable");
		logger.debug("tableName: " + tableName);
		GetColumnInformationOfTableServiceImpl getColumnInformation = new GetColumnInformationOfTableServiceImpl();
		getColumnInformation.setSchemaName(schemaName);
		getColumnInformation.setTableName(tableName);
		List<ColumnInformation> columnInformation = null;
		try {
			columnInformation = (List<ColumnInformation>) JdbcUtils.extractDatabaseMetaData(externalDataSource, getColumnInformation);
		} catch (MetaDataAccessException e) {
			//TODO
		}
		return columnInformation;
	}

	@SuppressWarnings("unchecked")
	private List<String> getPrimaryKeysOfTable(ComboPooledDataSource externalDataSource, String schemaName, String tableName) {
		logger.debug("in getPrimaryKeysOfTable");
		logger.debug("tableName: " + tableName);
		GetPrimaryKeysOfTableServiceImpl getPrimaryKeys = new GetPrimaryKeysOfTableServiceImpl();
		getPrimaryKeys.setSchemaName(schemaName);
		getPrimaryKeys.setTableName(tableName);
		List<String> primaryKeys = null;
		try {
			primaryKeys = (List<String>) JdbcUtils.extractDatabaseMetaData(externalDataSource, getPrimaryKeys);
		} catch (MetaDataAccessException e) {
			//TODO
		}
		logger.debug("primaryKeys: " + primaryKeys);
		return primaryKeys;
	}

	@SuppressWarnings("unchecked")
	private List<String> getTableNames(String schemaName, ComboPooledDataSource externalDataSource) {
		logger.debug("in getTableNames");
		GetTableNamesServiceImpl getTableNames = new GetTableNamesServiceImpl();
		getTableNames.setSchemaName(schemaName);
		List<String> tableNames = null;
		try {
			tableNames = (List<String>) JdbcUtils.extractDatabaseMetaData(externalDataSource, getTableNames);
		} catch (MetaDataAccessException e) {
			//TODO
		}
		return tableNames;
	}

	/**
	 * Method: For creating the Schema
	 * 
	 * @param schemaName
	 * @return Schema
	 */
	private Schema createSchema(String schemaName) {
		logger.debug("in createSchema");
		logger.debug("schemaName: " + schemaName);
		logger.debug("dataSource: " + dataSource);
		ModelType modelType = determineModelType(dataSource);
		schema = new Schema(schemaName, modelType);
		logger.debug("schema.name: " + schema.getName());
		return schema;
	}

	/***
	 * Method: Determine the type of this dataSource
	 *  
     * Revision:
     *  1. Check if model is RDF first because the RDF connection URL includes substring "jdbc"
	 */
	private ModelType determineModelType(DataSource dataSource) {
		logger.debug("in determineModelType");
		logger.debug("dataSource: " + dataSource);
		logger.debug("dataSource.getConnectionURL(): " + dataSource.getConnectionURL());

		if ((dataSource.getIsRDFSource()!= null) && (dataSource.getIsRDFSource().equals("true")))
			return ModelType.RDF;
		else if (dataSource.getConnectionURL().contains("jdbc"))
			return ModelType.RELATIONAL;
		else if (dataSource.getConnectionURL().contains("xmldb"))
			return ModelType.XSD;
		else {
			logger.error("unexpected modelType");
			logger.debug("dataSource.connectionUrl: " + dataSource.getConnectionURL());
			return null;
		}
	}	
	
	private DataSource createDataSource(String url, String driverClass, String userName, String password) {
		logger.debug("in createDataSource");
		logger.debug("url:" + url + " ,driverClass: " + driverClass + " ,userName: " + userName + " ,pass:" + password);
		//TODO need to check that I've got all the information and otherwise throw an exception
		//TODO think about whether schemaName should go in there too
		DataSource dataSource = new DataSource(url, driverClass, userName, password);
		logger.debug("dataSource>>: " + dataSource);
		return dataSource;
	}

	private DataSource createDataSource(String url, String schemaUrl, String driverClass, String userName, String password) {
		logger.debug("in createDataSource");
		//TODO need to check that I've got all the information and otherwise throw an exception
		//TODO think about whether schemaName should go in there too
		DataSource dataSource = new DataSource(url, schemaUrl, driverClass, userName, password);
		return dataSource;
	}
	
	/**
	 * Method: Create a persistent representation of an RDF source in the Model
	 */
	private DataSource createDataSource(String dataSourceName, String connectionURL, String schemaUrl, String driverClass,
			                            String userName, String password, String isRDFSource) {
		logger.debug("in RDF_createDataSource");
		//TODO need to check that I've got all the information and otherwise throw an exception
		//TODO think about whether schemaName should go in there too
		DataSource dataSource = new DataSource(dataSourceName, connectionURL, schemaUrl, driverClass, userName, password, isRDFSource);
		return dataSource;
	}

	//@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Transactional
	private void saveDataSource(DataSource dataSource) {
		logger.debug("in saveDataSource"); //TODO use aspect for these
		logger.debug("dataSource: " + dataSource);
		logger.debug("dataSourceRepository: " + dataSourceRepository);
		dataSourceRepository.save(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.meta.DataSourceService#deleteDataSource(java.lang.Long)
	 */
	//@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	@Transactional
	public void deleteDataSource(Long dataSourceId) {
		// TODO
		dataSourceRepository.delete(dataSourceRepository.find(dataSourceId));
	}

	/*
	 * (non-Javadoc)
	 * @see uk.ac.manchester.dataspaces.service.meta.DataSourceService#findDataSource(java.lang.Long)
	 */
	@Transactional(readOnly = true)
	public DataSource findDataSource(Long dataSourceId) {
		return dataSourceRepository.find(dataSourceId);
	}

	/**
	 * @param dataSourceRepository the dataSourceRepository to set
	 */
	public void setDataSourceRepository(DataSourceRepository dataSourceRepository) {
		this.dataSourceRepository = dataSourceRepository;
	}

	/**
	 * @return the dataSourceRepository
	 */
	public DataSourceRepository getDataSourceRepository() {
		return dataSourceRepository;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.dstoolkit.service.impl.GenericEntityServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<DataSource, Long> getRepository() {
		return dataSourceRepository;
	}

	@Transactional(readOnly = true)
	public DataSource findDataSourceByName(String dataSourceName) {
		return dataSourceRepository.getDataSourceWithName(dataSourceName);
	}

}