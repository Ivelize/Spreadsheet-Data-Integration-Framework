/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoexclude;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoexclude.ImportSchemaElementsToExcludeFromXMLService;

/**
 * @author chedeler
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class ImportSchemaElementsToExcludeFromXMLServiceImplTest {

	//TODO split up tests and document what case is being tested by giving test method appropriate name

	private static Logger logger = Logger.getLogger(ImportSchemaElementsToExcludeFromXMLServiceImplTest.class);
	private final String fileLocation = "./src/main/xml/schemaElementsToExclude/SchemaElementsToExclude.xml";

	@Autowired
	private ImportSchemaElementsToExcludeFromXMLService importSchemaElementsToExclude;

	@Test
	public void testReadSchemaElementsToExcludeFromXml() throws FileNotFoundException, JAXBException {
		importSchemaElementsToExclude.readSchemaElementsToExcludeFromXml(fileLocation);
		/*
		Map<String, ElementsToExclude> schemaNameElementsToExcludeMap = importSchemaElementsToExclude.getSchemaElementsToInclude();
		assertEquals(1, schemaNameElementsToExcludeMap.size());
		assertEquals(true, schemaNameElementsToExcludeMap.containsKey("MondialCityCountryContinentEurope"));
		ElementsToExclude elementsToExclude = schemaNameElementsToExcludeMap.get("MondialCityCountryContinentEurope");
		assertEquals(0, elementsToExclude.getComplexElement().size());
		assertEquals(2, elementsToExclude.getTable().size());
		List<Table> tables = elementsToExclude.getTable();
		Table table0 = tables.get(0);
		assertEquals("City", table0.getName());
		assertEquals(true, table0.isExclude());
		assertEquals(0, table0.getColumn().size());
		Table table1 = tables.get(1);
		assertEquals("encompasses", table1.getName());
		assertEquals(true, table1.isExclude());
		assertEquals(0, table1.getColumn().size());
		*/

		assertEquals(true, importSchemaElementsToExclude.excludeTableInSchema("MondialCityCountryContinentEuropeExcl", "Borders"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Borders", "Country1"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Borders", "Country1"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Borders", "Length"));

		assertEquals(true, importSchemaElementsToExclude.excludeTableInSchema("MondialCityCountryContinentEuropeExcl", "City"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "City", "Name"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "City", "Country"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "City", "Population"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "City", "Longitude"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "City", "Latitude"));

		assertEquals(false, importSchemaElementsToExclude.excludeTableInSchema("MondialCityCountryContinentEuropeExcl", "Continent"));
		assertEquals(false, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Continent", "Name"));
		assertEquals(false, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Continent", "Area"));

		assertEquals(false, importSchemaElementsToExclude.excludeTableInSchema("MondialCityCountryContinentEuropeExcl", "Country"));
		assertEquals(true, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Country", "Name"));
		assertEquals(false, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Country", "Code"));
		assertEquals(false, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Country", "Capital"));
		assertEquals(false, importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Country", "Area"));
		assertEquals(false,
				importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Country", "Population"));

		assertEquals(false, importSchemaElementsToExclude.excludeTableInSchema("MondialCityCountryContinentEuropeExcl", "Encompasses")); //TODO think about this one ... doesn't make any sense to include the table when all its attributes are excluded, but won't know here that all attributes are excluded, so this will have to stay as is at this stage
		assertEquals(true,
				importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Encompasses", "Country"));
		assertEquals(true,
				importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Encompasses", "Continent"));
		assertEquals(true,
				importSchemaElementsToExclude.excludeColumnInTableInSchema("MondialCityCountryContinentEuropeExcl", "Encompasses", "Percentage"));

	}

}
