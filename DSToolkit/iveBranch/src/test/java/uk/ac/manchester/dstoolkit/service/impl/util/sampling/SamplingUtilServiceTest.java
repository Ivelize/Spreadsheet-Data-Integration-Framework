package uk.ac.manchester.dstoolkit.service.impl.util.sampling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.manchester.dstoolkit.service.util.sampling.SamplingUtilService;


/** 
 * @author Klitos Christodoulou
 *
 * Sampling: This method is responsible of sampling the sources.
 * If DSToolkit is presented with a SPARQL endpoint this method will get a sample of the source and
 * from there try to generate a schema that will use later one to integrate the sources.  
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml" })
public class SamplingUtilServiceTest {
	
	/*Enable Logging*/
	static Logger logger = Logger.getLogger(SamplingUtilServiceTest.class);
	
	/*Variables read from the property files*/
	private static String rdfSourceName;
	private static String schemaNameRDF;
	private static String connectionURL;
	private static String driverClass;
	private static String rdfSourceURL;
	private static String schemaURL;
	private static String userName;
	private static String passWord;
	private static String isRDFSource;
	private static String description;	
	
	@Autowired
	@Qualifier("samplingUtilService")
	private SamplingUtilService samplingUtilService;
	
	@Before
	public void setUp() {	
	}//end setUp()	
	
	/***
	 * Class to test whether SPARQL end-point is alive or not. 
	 * @throws SQLException
	 */
	@Test
	public void testCheckSPARQLservice() throws SQLException {
		String sparqlServiceURL = "http://dbpediasdf.org/sparql2";
		boolean status = samplingUtilService.checkSPARQLservice(sparqlServiceURL);
		
		if (status) {
			logger.warn("SPARQL endpoint is: UP");	
		} else {
			logger.warn("SPARQL endpoint is: DOWN");
		}
	}	
	
	/**
	 * Load configuration
	 * @param filePath
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