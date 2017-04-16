package uk.ac.manchester.dstoolkit;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.Statement;

import uk.ac.manchester.dstoolkit.domain.Dataspace;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.user.User;
import uk.ac.manchester.dstoolkit.repository.DataspaceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.user.UserRepository;
import uk.ac.manchester.dstoolkit.service.DataspaceService;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;
import uk.ac.manchester.dstoolkit.service.util.importexport.mappings.PredefinedMappingsLoaderService;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.Constant;
import uk.ac.manchester.dstoolkit.service.util.spreadsheet.SpreadsheetService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
@Transactional
public abstract class AbstractIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	static Logger logger = Logger.getLogger(AbstractIntegrationTest.class);

	@PersistenceContext
	protected EntityManager entityManager;

	@Autowired
	protected static SessionFactory sessionFactory;
	
	private static String databaseName;
	private static String schemaName;
	private static String url;
	private static String schemaUrl;
	private static String driverClass;
	private static String userName;
	private static String password;
	private static String description;

	protected static User currentUser;
	protected static Dataspace currentDataspace;

	private static boolean loadedMappings = false;
	
	//Biology Spreadsheet

	private static String spreadsheetSourceSchema1 = "./src/test/resources/datasources/spreadsheetSourceSchema1.properties";

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
	@Qualifier("predefinedMappingsLoaderService")
	protected PredefinedMappingsLoaderService predefinedMappingsLoaderService;
	
		
	@Before
	public void setUp() {
		logger.debug("in AbstractIntegrationTest.setup()");

		//TODO this isn't quite the right way to set things up, but I seem to remember that it didn't work in setUpOnce that easily either for some reason

		if (userRepository.getUserWithUserName("teste") == null) {
			//add user		
			User newUser = new User();
			newUser.setAcceptTerms(true);
			Date date = new Date();
			newUser.setDateCreated(date);
			newUser.setUserName("teste");
			newUser.setPassword("teste");
			newUser.setFirstName("teste");
			newUser.setEmail("anonymous.guest@guest.co.uk");
			userService.addUser(newUser);
			currentUser = newUser;
		}else{
			currentUser = userRepository.getUserWithUserName("teste");
		}

		if (dataspaceRepository.getDataspaceWithName("spreadsheetDS1") == null) {
			Dataspace dataspace = new Dataspace("spreadsheetDS1");
			dataspace.addUser(currentUser);
			currentUser.addDataspace(dataspace);
			dataspaceService.addDataspace(dataspace);
			userRepository.update(currentUser);
			currentDataspace = dataspace;
		}else{
			currentDataspace = dataspaceRepository.getDataspaceWithName("spreadsheetDS1");
		}
		
		SpreadsheetService spreadsheetService = new SpreadsheetService();
		
		File spreadsheetSourceSpreadsheets[];
		File spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_SOURCE);
		spreadsheetSourceSpreadsheets = spreadsheetsDirectory.listFiles();
		spreadsheetService.uploadSpreadsheet (spreadsheetSourceSpreadsheets, "source");
		
		File spreadsheetTargetSpreadsheets[];
		spreadsheetsDirectory = new File(Constant.DIRECTORY_PATH_SPREADSHEETS_TARGET);
		spreadsheetTargetSpreadsheets = spreadsheetsDirectory.listFiles();
		spreadsheetService.uploadSpreadsheet (spreadsheetTargetSpreadsheets, "target");
		
 		int j = 0;
		int jMax = spreadsheetTargetSpreadsheets.length;
		for (int i = 0; i < spreadsheetSourceSpreadsheets.length; i++) {
			
			if (spreadsheetSourceSpreadsheets[i].getName().endsWith("csv") || spreadsheetSourceSpreadsheets[i].getName().endsWith("xls")){
				
				if(!spreadsheetTargetSpreadsheets[j].getName().endsWith("csv") && !spreadsheetTargetSpreadsheets[j].getName().endsWith("xls")){
					if(j < jMax){
						j = j+1;
					}
				}
			
				String[] sourceName = spreadsheetSourceSpreadsheets[i].getName().split("[.]");			
				String[] targetName = spreadsheetTargetSpreadsheets[j].getName().split("[.]");	
				
				//BiologySpreadsheets
				if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("dryad_source_"+sourceName[0].replaceAll("[^a-zA-Z0-9]", "")) == null) {
					loadConfiguration(spreadsheetSourceSchema1, "dryad_source_"+sourceName[0].replaceAll("[^a-zA-Z0-9]", ""));
					DataSource dataSourcePostgress = dataSourceService.findDataSourceByName("dryad_source_"+sourceName[0].replaceAll("[^a-zA-Z0-9]", ""));
					if( dataSourcePostgress == null){
						try{
							DataSource spreadsheetSource1DS = dataSourceService.addDataSource(databaseName, null,
									description, driverClass, url, userName, password);
							currentDataspace.addDataSource(spreadsheetSource1DS);
							currentDataspace.addSchema(spreadsheetSource1DS.getSchema());
						}catch(Exception e){
							 e.printStackTrace();
						}
					}else{
						currentDataspace.addDataSource(dataSourcePostgress);
						currentDataspace.addSchema(dataSourcePostgress.getSchema());
					}
				}
				
				if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("schema_target_"+targetName[0].replaceAll("[^a-zA-Z0-9]", "")) == null) {
					loadConfiguration(spreadsheetSourceSchema1, "schema_target_"+targetName[0].replaceAll("[^a-zA-Z0-9]", ""));
					DataSource dataSourcePostgress = dataSourceService.findDataSourceByName("schema_target_"+targetName[0].replaceAll("[^a-zA-Z0-9]", ""));
					if( dataSourcePostgress == null){
						try{
							DataSource spreadsheetSource2DS = dataSourceService.addDataSource(databaseName, null,
									description, driverClass, url, userName, password);
							currentDataspace.addDataSource(spreadsheetSource2DS);
							currentDataspace.addSchema(spreadsheetSource2DS.getSchema());
						}catch(Exception e){
							 e.printStackTrace();
						}
					}else{
						currentDataspace.addDataSource(dataSourcePostgress);
						currentDataspace.addSchema(dataSourcePostgress.getSchema());
					}
				}
				
				if(j < jMax){
					j = j+1;
				}
				
				//spreadsheetService.dropSpreadsheetSchema("schema_source_"+sourceName[0]);
				//spreadsheetService.dropSpreadsheetSchema("schema_target_"+targetName[0]);
			}
		
		}
		
		logger.debug("externalDataSourcePoolUtilService.getAllDataSourceNames(): " + externalDataSourcePoolUtilService.getAllDataSourceNames());
		logger.debug("externalDataSourcePoolUtilService.getAllDataSourceNames().size(): "
				+ externalDataSourcePoolUtilService.getAllDataSourceNames().size());
		
		

	}

	

	protected void loadConfiguration(String fileName, String spreadsheetDatabaseName) {
		try {
			InputStream propertyStream = new FileInputStream(fileName);
			Properties connectionProperties = new java.util.Properties();
			connectionProperties.load(propertyStream);
			databaseName = spreadsheetDatabaseName;
			schemaName = spreadsheetDatabaseName;
			url = connectionProperties.getProperty("connectionURL")+spreadsheetDatabaseName;
			schemaUrl = connectionProperties.getProperty("schemaURL");
			driverClass = connectionProperties.getProperty("driverClass");
			userName = connectionProperties.getProperty("username");
			password = connectionProperties.getProperty("password");
			description = connectionProperties.getProperty("description");
			//TODO will need to add schemaName, as same datasource can have multiple schemas ... think about this
			logger.debug("databaseName: " + databaseName);
			logger.debug("url: " + url);
			logger.debug("schemaUrl: " + schemaUrl);
			logger.debug("driverClass: " + driverClass);
			logger.debug("userName: " + userName);
			logger.debug("password: " + password);
		} catch (FileNotFoundException exc) {
			logger.error("exception raised in DataspacesRepository: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("properties file not found", ioexc);
			ioexc.printStackTrace();
		}
	}

}