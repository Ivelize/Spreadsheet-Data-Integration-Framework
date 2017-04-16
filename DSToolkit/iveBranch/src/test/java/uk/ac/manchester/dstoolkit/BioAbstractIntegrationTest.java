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
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.user.UserService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public abstract class BioAbstractIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

	static Logger logger = Logger.getLogger(BioAbstractIntegrationTest.class);

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

	//Bioinformatics property files

	private static String stanfordPropsLoc = "./src/test/resources/datasources/StanfordMySQL.properties";
	private static String arrayExpressXMLPropsLoc = "./src/test/resources/datasources/ArrayExpressXML.properties";
	private static String geoXMLPropsLoc = "./src/test/resources/datasources/GeoXML.properties";

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

	//Bioinformatics Datasources
	protected static DriverManagerDataSource stanfordDataSource;
	protected static DriverManagerDataSource arrayExpressDataSource;
	protected static DriverManagerDataSource geoSource;

	@Before
	//adds the databases to the dataspace (R)
	public void setUp() {
		logger.debug("in BioAbstractIntegrationTest.setup()");

		//TODO this isn't quite the right way to set things up, but I seem to remember that it didn't work in setUpOnce that easily either for some reason

		if (userRepository.getUserWithUserName("connie") == null) {
			//add user		
			User connieUser = new User();
			connieUser.setUserName("connie");
			connieUser.setPassword("connie");
			connieUser.setFirstName("connie");
			connieUser.setEmail("anonymous.guest@guest.co.uk");
			userService.addUser(connieUser);
			currentUser = connieUser;
		}

		if (dataspaceRepository.getDataspaceWithName("BioDS1") == null) {
			Dataspace dataspace = new Dataspace("BioDS1");
			dataspace.addUser(currentUser);
			currentUser.addDataspace(dataspace);
			dataspaceService.addDataspace(dataspace);
			userRepository.update(currentUser);
			currentDataspace = dataspace;
		}

		//Bioinformatics dataspaces (R)

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("Stanford") == null) {
			loadConfiguration(stanfordPropsLoc);
//<<<<<<< .mine
			uk.ac.manchester.dstoolkit.domain.models.meta.DataSource stanfordDS = dataSourceService.addDataSource(databaseName, databaseName,
					description, driverClass, url, userName, password);
//=======
			uk.ac.manchester.dstoolkit.domain.models.meta.DataSource stanfordDS2 = dataSourceService.addDataSource(databaseName, null, description,
					driverClass, url, userName, password);
//>>>>>>> .r730
			currentDataspace.addDataSource(stanfordDS);
			currentDataspace.addSchema(stanfordDS.getSchema());
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("ArrayExpressXML") == null) {
			loadConfiguration(arrayExpressXMLPropsLoc);
			uk.ac.manchester.dstoolkit.domain.models.meta.DataSource arrayExpressXMLDS = dataSourceService.addDataSource(databaseName, null,
					description, driverClass, url, schemaUrl, userName, password);
			currentDataspace.addDataSource(arrayExpressXMLDS);
			currentDataspace.addSchema(arrayExpressXMLDS.getSchema());
		}

		if (externalDataSourcePoolUtilService.getExternalRelationalDataSource("GeoXML") == null) {
			loadConfiguration(geoXMLPropsLoc);
			uk.ac.manchester.dstoolkit.domain.models.meta.DataSource geoXMLDS = dataSourceService.addDataSource(databaseName, null, description,
					driverClass, url, schemaUrl, userName, password);
			currentDataspace.addDataSource(geoXMLDS);
			currentDataspace.addSchema(geoXMLDS.getSchema());
		}

		logger.debug("externalDataSourcePoolUtilService.getAllDataSourceNames(): " + externalDataSourcePoolUtilService.getAllDataSourceNames());
		logger.debug("externalDataSourcePoolUtilService.getAllDataSourceNames().size(): "
				+ externalDataSourcePoolUtilService.getAllDataSourceNames().size());

		for (String dataSourceName : externalDataSourcePoolUtilService.getAllDataSourceNames()) {
			DataSource dataSource = dataSourceRepository.getDataSourceWithSchemaName(dataSourceName);
			logger.debug("dataSource.getSchema().getName(): " + dataSource.getSchema().getName());
			logger.debug("dataSource.getConnectionURL(): " + dataSource.getConnectionURL());
		}

	}

	protected void loadConfiguration(String fileName) {
		try {
			InputStream propertyStream = new FileInputStream(fileName);
			Properties connectionProperties = new java.util.Properties();
			connectionProperties.load(propertyStream);
			databaseName = connectionProperties.getProperty("databaseName");
			schemaName = connectionProperties.getProperty("schemaName");
			url = connectionProperties.getProperty("connectionURL");
			schemaUrl = connectionProperties.getProperty("schemaURL");
			driverClass = connectionProperties.getProperty("driverClass");
			userName = connectionProperties.getProperty("username");
			password = connectionProperties.getProperty("password");
			description = connectionProperties.getProperty("description");
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
