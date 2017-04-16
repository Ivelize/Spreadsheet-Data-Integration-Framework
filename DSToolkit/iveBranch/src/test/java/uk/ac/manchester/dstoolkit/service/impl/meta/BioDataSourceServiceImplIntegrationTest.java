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
import uk.ac.manchester.dstoolkit.domain.models.canonical.ConstructType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.ParticipationOfCMCInSuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstract;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstractMIDSTSuperModelType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperAbstractModelSpecificType;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperLexical;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationship;
import uk.ac.manchester.dstoolkit.domain.models.canonical.SuperRelationshipRoleType;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.domain.models.meta.Schema;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.repository.meta.SchemaRepository;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;

/**
 * @author chedeler
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class BioDataSourceServiceImplIntegrationTest { //extends BioAbstractIntegrationTest {

	static Logger logger = Logger.getLogger(BioDataSourceServiceImplIntegrationTest.class);

	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;

	@Autowired
	@Qualifier("schemaRepository")
	private SchemaRepository schemaRepository;

	private String relDataSourceName;
	private String relUrl;
	private String relDriverClass;
	private String relUserName;
	private String relPassword;
	private String relDescription;

	private String xmlDataSourceName;
	private String xmlUrl;
	private String xmlSchemaUrl;
	private String xmlDriverClass;
	private String xmlUserName;
	private String xmlPassword;
	private String xmlDescription;

	private String xmlDataSourceName2;
	private String xmlUrl2;
	private String xmlSchemaUrl2;
	private String xmlDriverClass2;
	private String xmlUserName2;
	private String xmlPassword2;
	private String xmlDescription2;

	//@Override
	@Before
	public void setUp() {
		relDataSourceName = "Stanford";
		relUrl = "jdbc:mysql://localhost/spreatest";
		relDriverClass = "com.mysql.jdbc.Driver";
		relUserName = "root";
		relPassword = "root";
		relDescription = "Stanford";

		xmlDataSourceName = "ArrayExpressXML";
		xmlUrl = "xmldb:exist://localhost:8680/exist/xmlrpc/db/arrayexpress/ArrayExpress.xml";
		xmlSchemaUrl = "./src/test/resources/schemas/arrayexschema.xsd";
		xmlDriverClass = "org.exist.xmldb.DatabaseImpl";
		xmlUserName = "admin";
		xmlPassword = "Sh*l)M";
		xmlDescription = "Array Express, XML";

		xmlDataSourceName2 = "GeoXML";
		xmlUrl2 = "xmldb:exist://localhost:8680/exist/xmlrpc/db/geo/GEO.xml";
		xmlSchemaUrl2 = "./src/test/resources/schemas/geoschema.xsd";
		xmlDriverClass2 = "org.exist.xmldb.DatabaseImpl";
		xmlUserName2 = "admin";
		xmlPassword2 = "Sh*l)M";
		xmlDescription2 = "GEO, XML";
	}

	@Test
	public void testAddStanfordDataSourceRelational() {
		DataSource dataSource = dataSourceService.addDataSource(relDataSourceName, null, relDescription, relDriverClass, relUrl, relUserName,
				relPassword, null);
		assertNotNull(dataSource.getId());
		assertEquals(relUrl, dataSource.getConnectionURL());
		assertEquals(relDriverClass, dataSource.getDriverClass());
		assertEquals(relUserName, dataSource.getUserName());
		assertEquals(relPassword, dataSource.getPassword());

		//TODO need to check that schema information is loaded correctly
	}

	@Test
	public void testAddArrayExDataSourceXML() {
		DataSource dataSource = dataSourceService.addDataSource(xmlDataSourceName, xmlDescription, xmlDriverClass, xmlUrl, xmlSchemaUrl, xmlUserName,
				xmlPassword, null);
		assertNotNull(dataSource.getId());
		assertEquals(xmlUrl, dataSource.getConnectionURL());
		assertEquals(xmlDriverClass, dataSource.getDriverClass());
		assertEquals(xmlUserName, dataSource.getUserName());
		assertEquals(xmlPassword, dataSource.getPassword());

		//TODO need to check that schema information is loaded correctly

		Schema schema = schemaRepository.getSchemaByName("ArrayExpressXML");
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

		assertEquals(9, superAbstracts.size());
		assertEquals(15, superLexicals.size());
		assertEquals(22, superRelationships.size());

		for (SuperAbstract superAbstract : superAbstracts) {
			if (superAbstract.getName().equals("arrayexpress")) {
				assertEquals(SuperAbstractModelSpecificType.XSD_COMPLEX_ELEMENT, superAbstract.getModelSpecificType()); //TODO this should actually be the root element, but it's not the first element in the schema ...
				assertEquals(SuperAbstractMIDSTSuperModelType.STRUCT_OF_ATTRIBUTES, superAbstract.getMidstSuperModelType());
				assertEquals(1, superAbstract.getParticipationInSuperRelationships().size());
				Set<ParticipationOfCMCInSuperRelationship> arrayExpressParts = superAbstract.getParticipationInSuperRelationships();
				ParticipationOfCMCInSuperRelationship arrayExpressParentPart = arrayExpressParts.iterator().next();
				assertEquals(SuperRelationshipRoleType.PARENT, arrayExpressParentPart.getRole());
				SuperRelationship arrayExpressSR = arrayExpressParentPart.getSuperRelationship();
				Set<ParticipationOfCMCInSuperRelationship> arrayExpressPartsOfCMCs = arrayExpressSR.getParticipationsOfConstructs();
				assertEquals(2, arrayExpressPartsOfCMCs.size());
				for (ParticipationOfCMCInSuperRelationship arrayExpressPartOfCMC : arrayExpressPartsOfCMCs) {
					if (arrayExpressPartOfCMC.getRole().equals(SuperRelationshipRoleType.CHILD)) {
						CanonicalModelConstruct arrayExpressChild = arrayExpressPartOfCMC.getCanonicalModelConstruct();
						assertEquals(ConstructType.SUPER_ABSTRACT, arrayExpressChild.getTypeOfConstruct());
						assertEquals("results", arrayExpressChild.getName());
						assertEquals(2, arrayExpressChild.getParticipationInSuperRelationships().size());
						Set<ParticipationOfCMCInSuperRelationship> resultsParts = arrayExpressChild.getParticipationInSuperRelationships();
						for (ParticipationOfCMCInSuperRelationship resultsParentPart : resultsParts) {
							if (resultsParentPart.getRole().equals(SuperRelationshipRoleType.PARENT)) {
								SuperRelationship resultsSR = resultsParentPart.getSuperRelationship();
								Set<ParticipationOfCMCInSuperRelationship> resultsPartsOfCMCs = resultsSR.getParticipationsOfConstructs();
								assertEquals(2, resultsPartsOfCMCs.size());
								for (ParticipationOfCMCInSuperRelationship resultsPartOfCMC : resultsPartsOfCMCs) {
									if (resultsPartOfCMC.getRole().equals(SuperRelationshipRoleType.CHILD)) {
										CanonicalModelConstruct resultsChild = resultsPartOfCMC.getCanonicalModelConstruct();
										assertEquals(ConstructType.SUPER_ABSTRACT, resultsChild.getTypeOfConstruct());
										assertEquals("result", resultsChild.getName());
										assertEquals(3, resultsChild.getParticipationInSuperRelationships().size());

										//TODO test for rest of schema
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testAddGeoDataSourceXML() {
		DataSource dataSource = dataSourceService.addDataSource(xmlDataSourceName2, xmlDescription2, xmlDriverClass2, xmlUrl2, xmlSchemaUrl2,
				xmlUserName2, xmlPassword2, null);
		assertNotNull(dataSource.getId());
		assertEquals(xmlUrl2, dataSource.getConnectionURL());
		assertEquals(xmlDriverClass2, dataSource.getDriverClass());
		assertEquals(xmlUserName2, dataSource.getUserName());
		assertEquals(xmlPassword2, dataSource.getPassword());

		//TODO need to check that schema information is loaded correctly

		Schema schema = schemaRepository.getSchemaByName("GeoXML");
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
		assertEquals(24, superLexicals.size());
		//assertEquals(29, superRelationships.size()); //TODO is 22 
	}

}
