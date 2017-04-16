package uk.ac.manchester.dstoolkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
@Transactional
public abstract class MergeAbstractIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	static Logger logger = Logger.getLogger(MergeAbstractIntegrationTest.class);

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

	private static String sourceSchema = "./src/test/resources/datasources/mergeSourceSchema.properties";
	private static String targetSchema = "./src/test/resources/datasources/mergeTargetSchema.properties";

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

		if (userRepository.getUserWithUserName("mergeTest") == null) {
			//add user		
			User newUser = new User();
			newUser.setAcceptTerms(true);
			Date date = new Date();
			newUser.setDateCreated(date);
			newUser.setUserName("userMergeTest");
			newUser.setPassword("test");
			newUser.setFirstName("firstNameMergeTest");
			newUser.setEmail("anonymous.guest@guest.co.uk");
			userService.addUser(newUser);
			currentUser = newUser;
		}else{
			currentUser = userRepository.getUserWithUserName("userMergeTest");
		}

		if (dataspaceRepository.getDataspaceWithName("mergeTestDS") == null) {
			Dataspace dataspace = new Dataspace("mergeTestDS");
			dataspace.addUser(currentUser);
			currentUser.addDataspace(dataspace);
			dataspaceService.addDataspace(dataspace);
			userRepository.update(currentUser);
			currentDataspace = dataspace;
		}else{
			currentDataspace = dataspaceRepository.getDataspaceWithName("mergeTestDS");
		}
	
		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(sourceSchema) == null) {
			loadConfiguration(sourceSchema);
			DataSource dataSourcePostgress = dataSourceService.findDataSourceByName(sourceSchema);
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
		
		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource(targetSchema) == null) {
			loadConfiguration(targetSchema);
			DataSource dataSourcePostgress = dataSourceService.findDataSourceByName(targetSchema);
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

	}

	

	protected void loadConfiguration(String fileName) {
		try {
			InputStream propertyStream = new FileInputStream(fileName);
			Properties connectionProperties = new java.util.Properties();
			connectionProperties.load(propertyStream);
			databaseName = connectionProperties.getProperty("databaseName");
			schemaName = connectionProperties.getProperty("databaseName");
			url = connectionProperties.getProperty("connectionURL");
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