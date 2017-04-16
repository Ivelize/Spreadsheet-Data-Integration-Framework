/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.domain.models.canonical.CanonicalModelConstruct;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;

/**
 * @author chedeler
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class DataSourceServiceImplIntegrationTest { //extends AbstractIntegrationTest {

	static Logger logger = Logger.getLogger(DataSourceServiceImplIntegrationTest.class);

	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	private String relDataSourceName;
	private String relUrl;
	private String relDriverClass;
	private String relUserName;
	private String relPassword;
	private String relDescription;

	private String fileLocationSchemaElementsToInclude;
	private String fileLocationSchemaElementsToExclude;

	private String xmlDataSourceName;
	private String xmlUrl;
	private String xmlSchemaUrl;
	private String xmlDriverClass;
	private String xmlUserName;
	private String xmlPassword;
	private String xmlDescription;

	//@Override
	@Before
	public void setUp() {
		fileLocationSchemaElementsToInclude = "./src/main/xml/schemaElementsToInclude/SchemaElementsToInclude.xml";
		fileLocationSchemaElementsToExclude = "./src/main/xml/schemaElementsToExclude/SchemaElementsToExclude.xml";

		relDataSourceName = "MondialCityCountryContinentEurope";
		relUrl = "jdbc:mysql://localhost/MondialCityCountryContinentEuropeNoRename";
		relDriverClass = "com.mysql.jdbc.Driver";
		relUserName = "root";
		relPassword = "p4nkl1t0s";
		relDescription = "Mondial - City, Country, Continent etc., Europe";

		xmlDataSourceName = "MondialCityCountryContinentEuropeXMLTest";
		xmlUrl = "xmldb:exist://localhost:8680/exist/xmlrpc/db/mondial/mondialEurope/mondial-europe.xml";
		xmlSchemaUrl = "./src/test/resources/schemas/mondial_1.xsd";
		xmlDriverClass = "org.exist.xmldb.DatabaseImpl";
		xmlUserName = "admin";
		xmlPassword = "p4nkl1t0s";
		xmlDescription = "Mondial - City, Country, Continent etc., Europe; XML";
	}

	@Test
	public void testAddDataSourceRelationalWithSchemaElementsToIncludeFile() {

		//TODO test datasources don't have foreign keys

		String dataSourceName = relDataSourceName + "Incl";

		DataSource dataSource = dataSourceService.addDataSource(dataSourceName, null, relDescription, relDriverClass, relUrl, relUserName,
				relPassword, fileLocationSchemaElementsToInclude, true);

		assertNotNull(dataSource.getId());
		assertEquals(relUrl, dataSource.getConnectionURL());
		assertEquals(relDriverClass, dataSource.getDriverClass());
		assertEquals(relUserName, dataSource.getUserName());
		assertEquals(relPassword, dataSource.getPassword());

		Schema schema = schemaRepository.getSchemaByName(dataSourceName);
		Set<CanonicalModelConstruct> constructs = schema.getCanonicalModelConstructs();

		Set<SuperAbstract> superAbstracts = new HashSet<SuperAbstract>();
		Set<SuperLexical> superLexicals = new HashSet<SuperLexical>();
		Set<SuperRelationship> superRelationships = new HashSet<SuperRelationship>();

		for (CanonicalModelConstruct construct : constructs) {
			if (construct instanceof SuperAbstract)
				superAbstracts.add((SuperAbstract) construct);
			else if (construct instanceof SuperLexical)
				superLexicals.add((SuperLexical) construct);
			else if (construct instanceof SuperRelationship)
				superRelationships.add((SuperRelationship) construct);
			else
				logger.error("unexpected canonicalModelConstruct");
		}

		assertEquals(2, superAbstracts.size());
		assertEquals(6, superLexicals.size());
		assertEquals(0, superRelationships.size());

		//TODO need to check that schema information is loaded correctly

	}

	@Test
	public void testAddDataSourceRelationalWithSchemaElementsToExcludeFile() {

		//TODO test datasources don't have foreign keys

		String dataSourceName = relDataSourceName + "Excl";

		DataSource dataSource = dataSourceService.addDataSource(dataSourceName, null, relDescription, relDriverClass, relUrl, relUserName,
				relPassword, fileLocationSchemaElementsToExclude, false);

		assertNotNull(dataSource.getId());
		assertEquals(relUrl, dataSource.getConnectionURL());
		assertEquals(relDriverClass, dataSource.getDriverClass());
		assertEquals(relUserName, dataSource.getUserName());
		assertEquals(relPassword, dataSource.getPassword());

		Schema schema = schemaRepository.getSchemaByName(dataSourceName);
		Set<CanonicalModelConstruct> constructs = schema.getCanonicalModelConstructs();

		Set<SuperAbstract> superAbstracts = new HashSet<SuperAbstract>();
		Set<SuperLexical> superLexicals = new HashSet<SuperLexical>();
		Set<SuperRelationship> superRelationships = new HashSet<SuperRelationship>();

		for (CanonicalModelConstruct construct : constructs) {
			if (construct instanceof SuperAbstract)
				superAbstracts.add((SuperAbstract) construct);
			else if (construct instanceof SuperLexical)
				superLexicals.add((SuperLexical) construct);
			else if (construct instanceof SuperRelationship)
				superRelationships.add((SuperRelationship) construct);
			else
				logger.error("unexpected canonicalModelConstruct");
		}

		assertEquals(3, superAbstracts.size());
		assertEquals(6, superLexicals.size());
		assertEquals(0, superRelationships.size());

		//TODO need to check that schema information is loaded correctly
	}

	@Test
	public void testAddDataSourceRelational() {

		//TODO test datasources don't have foreign keys

		String dataSourceName = relDataSourceName + "2";

		DataSource dataSource = dataSourceService.addDataSource(dataSourceName, null, relDescription, relDriverClass, relUrl, relUserName,
				relPassword);
		assertNotNull(dataSource.getId());
		assertEquals(relUrl, dataSource.getConnectionURL());
		assertEquals(relDriverClass, dataSource.getDriverClass());
		assertEquals(relUserName, dataSource.getUserName());
		assertEquals(relPassword, dataSource.getPassword());

		Schema schema = schemaRepository.getSchemaByName(dataSourceName);
		Set<CanonicalModelConstruct> constructs = schema.getCanonicalModelConstructs();

		Set<SuperAbstract> superAbstracts = new HashSet<SuperAbstract>();
		Set<SuperLexical> superLexicals = new HashSet<SuperLexical>();
		Set<SuperRelationship> superRelationships = new HashSet<SuperRelationship>();

		for (CanonicalModelConstruct construct : constructs) {
			if (construct instanceof SuperAbstract)
				superAbstracts.add((SuperAbstract) construct);
			else if (construct instanceof SuperLexical)
				superLexicals.add((SuperLexical) construct);
			else if (construct instanceof SuperRelationship)
				superRelationships.add((SuperRelationship) construct);
			else
				logger.error("unexpected canonicalModelConstruct");
		}

		assertEquals(5, superAbstracts.size());
		assertEquals(18, superLexicals.size());
		assertEquals(0, superRelationships.size());

		//TODO need to check that schema information is loaded correctly
	}

	@Test
	public void testAddDataSourceXML() {
		DataSource dataSource = dataSourceService.addDataSource(xmlDataSourceName, null, xmlDescription, xmlDriverClass, xmlUrl, xmlSchemaUrl,
				xmlUserName, xmlPassword);
		assertNotNull(dataSource.getId());
		assertEquals(xmlUrl, dataSource.getConnectionURL());
		assertEquals(xmlDriverClass, dataSource.getDriverClass());
		assertEquals(xmlUserName, dataSource.getUserName());
		assertEquals(xmlPassword, dataSource.getPassword());

		//TODO need to check that schema information is loaded correctly
	}

}
