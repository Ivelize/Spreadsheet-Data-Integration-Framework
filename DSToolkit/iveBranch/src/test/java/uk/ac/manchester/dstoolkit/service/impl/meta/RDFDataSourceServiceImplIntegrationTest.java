/**
 * 
 */
package uk.ac.manchester.dstoolkit.service.impl.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.manchester.dstoolkit.RDFAbstractIntegrationTest;
import uk.ac.manchester.dstoolkit.domain.models.meta.DataSource;
import uk.ac.manchester.dstoolkit.repository.meta.DataSourceRepository;
import uk.ac.manchester.dstoolkit.service.impl.util.SDBStoreServiceImpl;
import uk.ac.manchester.dstoolkit.service.meta.DataSourceService;
import uk.ac.manchester.dstoolkit.service.util.ExternalDataSourcePoolUtilService;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Test - Import RDF Data Sources to Dataspace. This is for making a data source available to DSToolkit
 * 
 * @author klitos
 */

//@RunWith(SpringJUnit4ClassRunner.class)
public class RDFDataSourceServiceImplIntegrationTest extends RDFAbstractIntegrationTest {

	static Logger logger = Logger.getLogger(RDFDataSourceServiceImplIntegrationTest.class);

	/*Location to RDF-Sources property files*/	
	private static String studentsRDFSourcePropsLoc = "./src/test/resources/datasources/StudentsRDFSource.properties";
	private static String mondialRDFEuropePropsLoc = "./src/test/resources/datasources/MondialRDFEurope.properties";
	private static String TelegraphisRDFCapitalsContinentsCountriesCurrenciesPropsLoc = "./src/test/resources/datasources/TelegraphisRDFCapitalsContinentsCountriesCurrencies.properties";
	
	/*Properties from src/test/resources/name.properties*/
	private static String   rdfSourceName;
	private static String   schemaNameRDF;
	private static String   connectionURL;
	private static String   driverClass;
	private static String   rdfSourceURL;
	private static String   schemaURL;
	private static String   userName;
	private static String   passWord;
	private static String   isRDFSource;
	private static String   description;
	
	@Autowired
	@Qualifier("dataSourceService")
	private DataSourceService dataSourceService;

	@Autowired
	@Qualifier("dataSourceRepository")
	private DataSourceRepository dataSourceRepository;
	
	@Autowired
	@Qualifier("externalDataSourcePoolUtilService")
	private ExternalDataSourcePoolUtilService externalDataSourcePoolUtilService;

	/*This override the setUP() from RDFAbstractIntegrationTest*/
	//@Override
	@Before
	public void setUp() {
	}//setUp()

	@Test
	public void testAddDataSourceRDFStudnets() {
	   logger.debug("RDF_in testAddDataSourceRDFStudnets()");		
	   loadConfiguration(studentsRDFSourcePropsLoc);
	   //Add RDF sources to Dataspace
	   DataSource studentsRDFSource 
		 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
				 passWord, isRDFSource);
	   
		//Get a list of the RDF sources names
		//Set<String> listOfRDFsource = externalDataSourcePoolUtilService.getAllJenaRDFDataSourceNames();
		
		//logger.debug("RDF_Number of sources: " + listOfRDFsource);
		//assertEquals(1, listOfRDFsource.size());
		
	}//end testAddDataSourceRDFStudnets()
	
	@Test
	public void testAddDataSourceRDFMondial() {
		
		//Load mondial Europe
		loadConfiguration(mondialRDFEuropePropsLoc);
		
	    //Add RDF sources to Dataspace
		DataSource mondialEuropeRDFSource 
		 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
				 passWord, isRDFSource);
		
		loadConfiguration(TelegraphisRDFCapitalsContinentsCountriesCurrenciesPropsLoc);
		
		DataSource telegraphisRDFSource 
		 = dataSourceService.addDataSource(rdfSourceName, schemaNameRDF, description, driverClass, connectionURL, schemaURL, userName,
				 passWord, isRDFSource);	
	
		//Get a list of the RDF sources names
		Set<String> listOfRDFsource = externalDataSourcePoolUtilService.getAllJenaRDFDataSourceNames();
		
		logger.debug("RDF_Number of sources: " + listOfRDFsource);
		assertEquals(2, listOfRDFsource.size());

		//Make sure that the models are not null
		SDBStoreServiceImpl mondialEuropeStore = externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("MondialRDFEurope");
		SDBStoreServiceImpl telegraphisStore = externalDataSourcePoolUtilService.getExternalJenaRDFDataSource("TelegraphisRDFCapitalsContinentsCountriesCurrencies");
		
		assertNotNull(mondialEuropeStore);
		assertNotNull(telegraphisStore);		
		
		//Retrieve a model from the SDBstore object
		//getModel(null) - to connect to the default graph
		Model telegraphisModel = telegraphisStore.getModel(null);
		
		logger.debug("Name spaces: " + telegraphisModel.listNameSpaces());
		
		/*Test if the DataSource object for this Data source*/
		//assertNotNull(mondialEuropeRDFSource.getId());
		//assertEquals(urlRDF1, mondialEuropeRDFSource.getConnectionURL());
		//assertEquals(driverClassRDF1, mondialEuropeRDFSource.getDriverClass());
		//assertEquals(userNameRDF1, mondialEuropeRDFSource.getUserName());
		//assertEquals(passwordRDF1, mondialEuropeRDFSource.getPassword());
		//assertEquals(isRDFSourceRDF1, mondialEuropeRDFSource.getIsRDFSource());		
		
		//TODO: need to check that schema information is loaded correctly
		
	}//testAddDataSourceRDF()
		
	/***
	 * Load properties file ./src/test/resources/datasources/JenaSDB.properties
	 * @param fileName
	 */
	protected void loadConfiguration(String filePath) {
	    try {
		  logger.debug("in LoadRDFSourceUtilServiceTest:" + filePath);
		  InputStream propertyStream = new FileInputStream(filePath);
		  Properties connectionProperties = new java.util.Properties();
		  connectionProperties.load(propertyStream);
		  //load
		  rdfSourceName = connectionProperties.getProperty("rdfSourceName");
		  schemaNameRDF = connectionProperties.getProperty("schemaNameRDF");
		  connectionURL = connectionProperties.getProperty("connectionURL");
		  driverClass = connectionProperties.getProperty("driverClass");
		  rdfSourceURL = connectionProperties.getProperty("rdfSourceURL");	
		  schemaURL = connectionProperties.getProperty("schemaURL");
		  userName = connectionProperties.getProperty("username");
		  passWord = connectionProperties.getProperty("password");
		  isRDFSource = connectionProperties.getProperty("isRDFSource");
		  description = connectionProperties.getProperty("description");		  	  		  
		  //print
		  logger.debug("RDF_sourceName: "   + rdfSourceName);
		  logger.debug("RDF_schemaName: "   + schemaNameRDF);
		  logger.debug("RDF_connURL: "      + connectionURL);
		  logger.debug("RDF_driverClass: "  + driverClass);
		  logger.debug("RDF_rdfSourceURL: " + rdfSourceURL);		  
		  logger.debug("RDF_schemaURL: "    + schemaURL);
		  logger.debug("RDF_userName: "     + userName);
		  logger.debug("RDF_passWord: "     + passWord);
		  logger.debug("RDF_isRDF: "        + isRDFSource);
		  logger.debug("RDF_description: "  + description);
		} catch (FileNotFoundException exc) {
			logger.error("RDF_exception raised while loading RDF: " + exc);
			exc.printStackTrace();
		} catch (IOException ioexc) {
			logger.error("RDF_properties file not found", ioexc);
			ioexc.printStackTrace();
		}//end catch
	 }//end loadConfiguration()	
}//end class
