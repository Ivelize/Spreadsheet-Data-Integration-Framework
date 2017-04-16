/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.util.importexport.schemaelementstoinclude;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.service.util.importexport.schemaelementstoinclude.ImportSchemaElementsToIncludeFromXMLService;

/**
 * @author chedeler
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class ImportSchemaElementsToIncludeFromXMLServiceImplTest {

	//TODO split up tests and document what case is being tested by giving test method appropriate name

	private static Logger logger = Logger.getLogger(ImportSchemaElementsToIncludeFromXMLServiceImplTest.class);
	private final String fileLocation = "./src/main/xml/schemaElementsToInclude/SchemaElementsToInclude.xml";

	@Autowired
	private ImportSchemaElementsToIncludeFromXMLService importSchemaElementsToInclude;

	@Test
	public void testReadSchemaElementsToIncludeFromXml() throws FileNotFoundException, JAXBException {
		importSchemaElementsToInclude.readSchemaElementsToIncludeFromXml(fileLocation);
		/*
		Map<String, ElementsToInclude> schemaNameElementsToIncludeMap = importSchemaElementsToInclude.getSchemaElementsToInclude();
		assertEquals(1, schemaNameElementsToIncludeMap.size());
		assertEquals(true, schemaNameElementsToIncludeMap.containsKey("MondialCityCountryContinentEurope"));
		ElementsToInclude elementsToInclude = schemaNameElementsToIncludeMap.get("MondialCityCountryContinentEurope");
		assertEquals(0, elementsToInclude.getComplexElement().size());
		assertEquals(2, elementsToInclude.getTable().size());
		List<Table> tables = elementsToInclude.getTable();
		Table table0 = tables.get(0);
		assertEquals("City", table0.getName());
		assertEquals(true, table0.isInclude());
		assertEquals(0, table0.getColumn().size());
		Table table1 = tables.get(1);
		assertEquals("encompasses", table1.getName());
		assertEquals(true, table1.isInclude());
		assertEquals(0, table1.getColumn().size());
		*/
		assertEquals(false, importSchemaElementsToInclude.includeTableInSchema("MondialCityCountryContinentEuropeIncl", "Borders"));
		assertEquals(false,
				importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Borders", "Country1"));
		assertEquals(false,
				importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Borders", "Country2"));
		assertEquals(false, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Borders", "Length"));

		assertEquals(true, importSchemaElementsToInclude.includeTableInSchema("MondialCityCountryContinentEuropeIncl", "City"));
		assertEquals(true, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "City", "Name"));
		assertEquals(true, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "City", "Country"));
		assertEquals(true, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "City", "Population"));
		assertEquals(true, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "City", "Longitude"));
		assertEquals(true, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "City", "Latitude"));

		assertEquals(false, importSchemaElementsToInclude.includeTableInSchema("MondialCityCountryContinentEuropeIncl", "Continent"));
		assertEquals(false, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Continent", "Name"));
		assertEquals(false, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Continent", "Area"));

		assertEquals(true, importSchemaElementsToInclude.includeTableInSchema("MondialCityCountryContinentEuropeIncl", "Country"));
		assertEquals(false, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Country", "Name"));
		assertEquals(false, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Country", "Code"));
		assertEquals(true, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Country", "Capital"));
		assertEquals(false, importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Country", "Area"));
		assertEquals(false,
				importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Country", "Population"));

		assertEquals(false, importSchemaElementsToInclude.includeTableInSchema("MondialCityCountryContinentEuropeIncl", "Encompasses"));
		assertEquals(false,
				importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Encompasses", "Continent"));
		assertEquals(false,
				importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Encompasses", "Country"));
		assertEquals(false,
				importSchemaElementsToInclude.includeColumnInTableInSchema("MondialCityCountryContinentEuropeIncl", "Encompasses", "Percentage"));

	}

}
